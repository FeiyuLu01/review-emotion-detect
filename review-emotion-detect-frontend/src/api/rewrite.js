export async function rewriteReview(text, tone = 'neutral', signal) {
    const base = (import.meta.env.VITE_REWRITE_API_URL || 'http://localhost:8000').replace(/\/+$/, '');
    const resp = await fetch(`${base}/gemini-rewrite `, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text, tone }),
      signal,
    });
    if (!resp.ok) {
      let msg = '';
      try { msg = await resp.text(); } catch (_) {}
      throw new Error(`Rewrite API ${resp.status}: ${msg || resp.statusText}`);
    }
    const json = await resp.json();
    return (json && json.text ? json.text : '').trim();
  }