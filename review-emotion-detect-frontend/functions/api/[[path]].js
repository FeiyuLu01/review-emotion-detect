// export async function onRequest(context) {
//   const { request, env } = context;

//   const ORIGIN = env.BACKEND_URL; // "http://3.104.35.168:8000"

//   const inUrl = new URL(request.url);
//   const upstreamPath = inUrl.pathname.replace(/^\/api/, "") + inUrl.search;
//   const target = ORIGIN.replace(/\/+$/, "") + upstreamPath;

//   const targetUrl = new URL(target);
//   const headers = new Headers(request.headers);

//   const url = new URL(context.request.url);

  
//   if (url.pathname === '/iteration1' || url.pathname.startsWith('/iteration1/')) {
//     // return context.next(); 
//     return fetch(new URL('/iteration1/index.html', url.origin), context.env.ASSETS);
//   }


//   headers.set("Host", targetUrl.host);
//   headers.set("Accept", "application/json, */*");
//   headers.delete("Origin");
//   headers.delete("Referer");
//   headers.delete("CF-Connecting-IP");
//   headers.delete("CF-IPCountry");
//   headers.delete("X-Forwarded-Host");
//   headers.delete("X-Forwarded-Proto");

//   const init = { method: request.method, headers };

//   if (!["GET", "HEAD"].includes(request.method)) {
//     init.body = request.body;
//   }

//   const resp = await fetch(target, init);

//   const outHeaders = new Headers(resp.headers);
//   if (!outHeaders.get("content-type")) {
//     outHeaders.set("content-type", "application/json");
//   }

//   return new Response(resp.body, {
//     status: resp.status,
//     headers: outHeaders,
//   });
// }


export async function onRequest(context) {
  const { request, env } = context;
  const url = new URL(request.url);

  // 让 /iteration1 和 /iteration1/* 返回归档页面
  if (url.pathname === '/iteration1' || url.pathname.startsWith('/iteration1/')) {
    return fetch(new URL('/iteration1/index.html', url.origin), context.env.ASSETS);
  }

  // 只处理 /api/* 走反代，其它交给静态资源
  if (!url.pathname.startsWith('/api/')) {
    return context.next();
  }

  // ---- 路由分流规则：根据路径选择上游域名 ----
  const path = url.pathname;

  // 1) 模型 API → api.luosong.wang
  const hitAPI =
    path.startsWith('/api/gemini-classify') ||
    path.startsWith('/api/gemini-rewrite');

  // 2) 业务后端 → backend.luosong.wang
  const hitCore =
    path.startsWith('/api/emotion') ||                 // /emotion/...（含 /emotion/questionnaire 等）
    path.startsWith('/api/emotion_analysis') ||        // 旧写法兜底
    path.startsWith('/api/posts') ||
    path.startsWith('/api/v3') ||                      // /v3/api-docs 等
    path.startsWith('/api/swagger') ||                 // 如有
    path.startsWith('/api/docs');                      // 如有

  const ORIGIN = hitAPI ? env.API_ORIGIN
                : hitCore ? env.CORE_ORIGIN
                : env.API_ORIGIN; // 默认给到模型域，避免 404

  // 去掉 /api 前缀拼到目标
  const upstreamPath = url.pathname.replace(/^\/api/, '') + url.search;
  const target = ORIGIN.replace(/\/+$/, '') + upstreamPath;
  const targetUrl = new URL(target);

  // 透传/清洗请求头
  const inHeaders = new Headers(request.headers);
  inHeaders.set('Host', targetUrl.host);
  inHeaders.set('Accept', 'application/json, */*');
  inHeaders.delete('Origin');
  inHeaders.delete('Referer');
  inHeaders.delete('CF-Connecting-IP');
  inHeaders.delete('CF-IPCountry');
  inHeaders.delete('X-Forwarded-Host');
  inHeaders.delete('X-Forwarded-Proto');

  const init = { method: request.method, headers: inHeaders };
  if (!['GET', 'HEAD'].includes(request.method)) {
    init.body = request.body;
  }

  // （可选）快速处理预检，提升速度；否则也可以直接 proxy 过去
  if (request.method === 'OPTIONS') {
    return new Response(null, { status: 204 });
  }

  const resp = await fetch(target, init);

  // 统一 content-type（仅当上游未设置时）
  const outHeaders = new Headers(resp.headers);
  if (!outHeaders.get('content-type')) {
    outHeaders.set('content-type', 'application/json');
  }

  // 同源反代：不需要设置 CORS 头
  return new Response(resp.body, { status: resp.status, headers: outHeaders });
}