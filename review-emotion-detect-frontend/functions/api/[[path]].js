export async function onRequest(context) {
  const { request, env } = context
  const url = new URL(request.url)
  const pathname = url.pathname

  console.log("Worker received request for:", pathname)  // 调试日志

  // Static / archive route
  if (pathname === '/iteration1' || pathname.startsWith('/iteration1/')) {
    return fetch(new URL('/iteration1/index.html', url.origin), context.env.ASSETS)
  }

  // Only proxy paths under /api/ 
  if (!pathname.startsWith('/api/')) {
    console.log("Not /api/, next()", pathname)
    return context.next()
  }

  // 分流：模型 vs 后端
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

  const ORIGIN = hitAPI
    ? env.API_ORIGIN
    : hitCore
      ? env.CORE_ORIGIN
      : env.API_ORIGIN

  console.log("Proxying to origin:", ORIGIN)

  // 构造 upstreamPath：只有 moderate 要重写 i3，posts 等保持原样
  let upstreamPath = ''
  if (pathname.startsWith('/api/moderate')) {
    upstreamPath = pathname.replace(/^\/api/, '/i3/api')
    console.log("Moderate rewrite to upstream:", upstreamPath)
  } else {
    upstreamPath = pathname
    console.log("Normal path upstream:", upstreamPath)
  }
  upstreamPath += url.search

  const target = ORIGIN.replace(/\/+$/, '') + upstreamPath
  console.log("Full target:", target)

  const targetUrl = new URL(target)
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

  if (request.method === 'OPTIONS') {
    return new Response(null, { status: 204 })
  }

  const resp = await fetch(target, init)
  const outHeaders = new Headers(resp.headers)
  if (!outHeaders.get('content-type')) {
    outHeaders.set('content-type', 'application/json')
  }

  console.log("Response status:", resp.status)

  return new Response(resp.body, {
    status: resp.status,
    headers: outHeaders
  })
}