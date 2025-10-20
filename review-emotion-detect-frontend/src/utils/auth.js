const KEY = 'ml_auth_ok'; // localStorage key

export function isAuthed() {
  return localStorage.getItem(KEY) === '1';
}

export function loginWithPassword(pw) {
  const expected = import.meta.env.VITE_SITE_PASSWORD || '';
  const ok = typeof pw === 'string' && pw.length > 0 && pw === expected;
  if (ok) localStorage.setItem(KEY, '1');
  return ok;
}

export function logout() {
  localStorage.removeItem(KEY);
}