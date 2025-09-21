// src/utils/apiBase.js
const isLocal = ['localhost', '127.0.0.1'].includes(location.hostname);

const stripSlash = (s) => (s || '').replace(/\/+$/, '');

export const API_BASE =
  isLocal ? stripSlash(import.meta.env.VITE_API_BASE) : '/api';

export const ANALYSIS_BASE =
  isLocal ? stripSlash(import.meta.env.VITE_ANALYSIS_API) : '/api';

export const REWRITE_BASE =
  isLocal ? stripSlash(import.meta.env.VITE_REWRITE_API_URL) : '/api';

export const Test_API_BASE =
  isLocal ? stripSlash(import.meta.env.VITE_Test_API_BASE) : '/api';

export { isLocal };