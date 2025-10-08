export async function onRequest(context) {
  console.log(">>> Function called, URL:", context.request.url);
  const { request, env } = context;
  const url = new URL(request.url);
  const pathname = url.pathname;

  console.log("Worker received request for:", pathname);

  // === Handle static archive route ===
  if (pathname === "/iteration1" || pathname.startsWith("/iteration1/")) {
    return fetch(new URL("/iteration1/index.html", url.origin), context.env.ASSETS);
  }

  // === Only proxy /api/* or /i3/api/* paths ===
  if (!pathname.startsWith("/api/") && !pathname.startsWith("/i3/api/")) {
    console.log("Not /api/, passing to next handler:", pathname);
    return context.next();
  }

  // === Select target origin ===
  const hitAPI =
    pathname.startsWith("/api/gemini-classify") ||
    pathname.startsWith("/api/gemini-rewrite");

  const hitCore =
    pathname.startsWith("/api/emotion") ||
    pathname.startsWith("/api/emotion_analysis") ||
    pathname.startsWith("/api/posts") ||
    pathname.startsWith("/api/moderate") ||
    pathname.startsWith("/api/v3") ||
    pathname.startsWith("/api/swagger") ||
    pathname.startsWith("/api/docs");

  const ORIGIN = hitAPI
    ? env.API_ORIGIN
    : hitCore
    ? env.CORE_ORIGIN
    : env.API_ORIGIN;

  console.log("Proxying to origin:", ORIGIN);

  // === Build upstream path ===
  let upstreamPath = "";

  if (pathname.startsWith("/api/moderate")) {
    // For /api/moderate → /i3/api/moderate
    upstreamPath = pathname.replace(/^\/api/, "/i3/api");
    console.log("Rewriting /api/moderate to:", upstreamPath);
  } else {
    // For all other /api/* → remove /api prefix
    upstreamPath = pathname.replace(/^\/api/, "");
    console.log("Removing /api prefix:", upstreamPath);
  }

  upstreamPath += url.search;

  const target = ORIGIN.replace(/\/+$/, "") + upstreamPath;
  console.log("Full target URL:", target);

  // === Proxy request ===
  const targetUrl = new URL(target);
  const inHeaders = new Headers(request.headers);
  inHeaders.set("Host", targetUrl.host);
  inHeaders.set("Accept", "application/json, */*");
  inHeaders.delete("Origin");
  inHeaders.delete("Referer");
  inHeaders.delete("CF-Connecting-IP");
  inHeaders.delete("CF-IPCountry");
  inHeaders.delete("X-Forwarded-Host");
  inHeaders.delete("X-Forwarded-Proto");

  const init = { method: request.method, headers: inHeaders };
  if (!["GET", "HEAD"].includes(request.method)) {
    init.body = request.body;
  }

  // === Handle preflight requests ===
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204 });
  }

  // === Fetch target and return response ===
  const resp = await fetch(target, init);
  const outHeaders = new Headers(resp.headers);
  if (!outHeaders.get("content-type")) {
    outHeaders.set("content-type", "application/json");
  }

  console.log("Response status:", resp.status);
  return new Response(resp.body, {
    status: resp.status,
    headers: outHeaders,
  });
}