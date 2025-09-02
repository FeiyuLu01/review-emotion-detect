export async function onRequest(context) {
  const { request, env } = context;

  //  http://3.104.35.168:8000
  const ORIGIN = env.BACKEND_URL;

  const inUrl = new URL(request.url);
  const upstreamPath = inUrl.pathname.replace(/^\/api/, "") + inUrl.search;
  const target = ORIGIN.replace(/\/+$/, "") + upstreamPath;

  const init = {
    method: request.method,
    headers: request.headers,
  };

  if (!["GET", "HEAD"].includes(request.method)) {
    init.body = request.body;
  }

  const resp = await fetch(target, init);

  return new Response(resp.body, {
    status: resp.status,
    headers: {
      "content-type": resp.headers.get("content-type") || "application/json",
    },
  });
}