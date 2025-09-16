export async function onRequest(context) {
  const { request, env } = context;

  const ORIGIN = env.BACKEND_URL; // "http://3.104.35.168:8000"

  const inUrl = new URL(request.url);
  const upstreamPath = inUrl.pathname.replace(/^\/api/, "") + inUrl.search;
  const target = ORIGIN.replace(/\/+$/, "") + upstreamPath;

  const targetUrl = new URL(target);
  const headers = new Headers(request.headers);

  const url = new URL(context.request.url);

  
  if (url.pathname === '/iteration1' || url.pathname.startsWith('/iteration1/')) {
    // return context.next(); 
    return fetch(new URL('/iteration1/index.html', url.origin), context.env.ASSETS);
  }


  headers.set("Host", targetUrl.host);
  headers.set("Accept", "application/json, */*");
  headers.delete("Origin");
  headers.delete("Referer");
  headers.delete("CF-Connecting-IP");
  headers.delete("CF-IPCountry");
  headers.delete("X-Forwarded-Host");
  headers.delete("X-Forwarded-Proto");

  const init = { method: request.method, headers };

  if (!["GET", "HEAD"].includes(request.method)) {
    init.body = request.body;
  }

  const resp = await fetch(target, init);

  const outHeaders = new Headers(resp.headers);
  if (!outHeaders.get("content-type")) {
    outHeaders.set("content-type", "application/json");
  }

  return new Response(resp.body, {
    status: resp.status,
    headers: outHeaders,
  });
}