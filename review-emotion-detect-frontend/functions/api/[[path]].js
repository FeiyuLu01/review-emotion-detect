export async function onRequest(context) {
  const { request, env } = context
  const url = new URL(request.url)
  const pathname = url.pathname

  // serve static / iteration paths
  if (pathname === '/iteration1' || pathname.startsWith('/iteration1/')) {
    return fetch(new URL('/iteration1/index.html', url.origin), context.env.ASSETS)
  }

  // only proxy paths starting with /api/ or /i3/api/
  if (!pathname.startsWith('/api/') && !pathname.startsWith('/i3/api/')) {
    return context.next()
  }

  // Determine which origin to proxy to
  // here we treat /api/moderate and other business paths as core backend
  const hitAPI =
    pathname.startsWith('/api/gemini-classify') ||
    pathname.startsWith('/api/gemini-rewrite')

  const hitCore =
    pathname.startsWith('/api/emotion') ||
    pathname.startsWith('/api/emotion_analysis') ||
    pathname.startsWith('/api/posts') ||
    pathname.startsWith('/api/moderate') ||
    pathname.startsWith('/i3/api/moderate') ||
    pathname.startsWith('/api/v3') ||
    pathname.startsWith('/api/swagger') ||
    pathname.startsWith('/api/docs')

  const ORIGIN = hitAPI
    ? env.API_ORIGIN
    : hitCore
      ? env.CORE_ORIGIN
      : env.API_ORIGIN

  // Compute upstream path
  let upstreamPath = ''
  if (pathname.startsWith('/api/moderate')) {
    // Only rewrite the moderation API
    upstreamPath = pathname.replace(/^\/api/, '/i3/api')
  } else {
    // if front-end already calls /i3/api/..., keep it
    upstreamPath = pathname
  }
  upstreamPath += url.search

  const target = ORIGIN.replace(/\/+$/, '') + upstreamPath
  const targetUrl = new URL(target)

  // Prepare headers
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

  return new Response(resp.body, { status: resp.status, headers: outHeaders })
}