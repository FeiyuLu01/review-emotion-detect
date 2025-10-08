export async function onRequest(context) {
  const { request, env } = context
  const url = new URL(request.url)
  const pathname = url.pathname

  // 如果是 /iteration1 或其子路径，走静态页面逻辑
  if (pathname === '/iteration1' || pathname.startsWith('/iteration1/')) {
    return fetch(new URL('/iteration1/index.html', url.origin), context.env.ASSETS)
  }

  // 只处理 /api/... 的请求。  
  // **注意：** 不要把 /i3/api/... 写在这里，否则会被跳过
  if (!pathname.startsWith('/api/')) {
    return context.next()
  }

  // 路由分流：决定走哪个后端域名
  const hitAPI =
    pathname.startsWith('/api/gemini-classify') ||
    pathname.startsWith('/api/gemini-rewrite')

  const hitCore =
    pathname.startsWith('/api/emotion') ||
    pathname.startsWith('/api/emotion_analysis') ||
    pathname.startsWith('/api/posts') ||
    pathname.startsWith('/api/moderate') ||
    pathname.startsWith('/api/v3') ||
    pathname.startsWith('/api/swagger') ||
    pathname.startsWith('/api/docs')

  const ORIGIN = hitAPI ? env.API_ORIGIN
                : hitCore ? env.CORE_ORIGIN
                : env.API_ORIGIN

  // 构造上游路径（upstreamPath）
  let upstreamPath = ''
  if (pathname.startsWith('/api/moderate')) {
    // moderation 接口前端请求 /api/moderate，重写为 /i3/api/moderate
    upstreamPath = pathname.replace(/^\/api/, '/i3/api')
  } else {
    // 其他接口（包括 /api/posts/get-all）按原样，不加 /i3
    upstreamPath = pathname
  }
  upstreamPath += url.search

  const target = ORIGIN.replace(/\/+$/, '') + upstreamPath
  const targetUrl = new URL(target)

  // 清洗和透传请求头
  const inHeaders = new Headers(request.headers)
  inHeaders.set('Host', targetUrl.host)
  inHeaders.set('Accept', 'application/json, */*')
  inHeaders.delete('Origin')
  inHeaders.delete('Referer')
  inHeaders.delete('CF-Connecting-IP')
  inHeaders.delete('CF-IPCountry')
  inHeaders.delete('X-Forwarded-Host')
  inHeaders.delete('X-Forwarded-Proto')

  const init = { method: request.method, headers: inHeaders }
  if (!['GET', 'HEAD'].includes(request.method)) {
    init.body = request.body
  }

  // 预检请求处理
  if (request.method === 'OPTIONS') {
    return new Response(null, { status: 204 })
  }

  const resp = await fetch(target, init)
  const outHeaders = new Headers(resp.headers)
  if (!outHeaders.get('content-type')) {
    outHeaders.set('content-type', 'application/json')
  }

  return new Response(resp.body, {
    status: resp.status,
    headers: outHeaders
  })
}