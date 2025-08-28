// Axios instance with interceptors and unified response handling
import axios from 'axios'
import { message } from 'ant-design-vue'

const http = axios.create({
  baseURL: '', // Let Vite dev server proxy /api in development if needed
  timeout: 15000
})

// Attach token or metadata here if needed
http.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error)
)

// Accept common backend shapes: { code, data, message } or raw data
http.interceptors.response.use(
  (resp) => {
    const res = resp.data
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code === 0) return res.data
      message.error(res.message || 'Request failed')
      return Promise.reject(new Error(res.message || 'Request failed'))
    }
    return res
  },
  (error) => {
    const msg = error?.response?.data?.message || error.message || 'Network error'
    message.error(msg)
    return Promise.reject(error)
  }
)

export default http