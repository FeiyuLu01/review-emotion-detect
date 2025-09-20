// // src/api/http.js
// import axios from 'axios'
// import { message } from 'ant-design-vue'

// // 工具函数：统一响应处理
// function setupInterceptors(instance) {
//   instance.interceptors.request.use(
//     (config) => config,
//     (error) => Promise.reject(error)
//   )

//   instance.interceptors.response.use(
//     (resp) => {
//       const res = resp.data
//       if (res && typeof res === 'object' && 'code' in res) {
//         if (res.code === 0) return res.data
//         message.error(res.message || 'Request failed')
//         return Promise.reject(new Error(res.message || 'Request failed'))
//       }
//       return res
//     },
//     (error) => {
//       const msg = error?.response?.data?.message || error.message || 'Network error'
//       message.error(msg)
//       return Promise.reject(error)
//     }
//   )
//   return instance
// }

// // ===== 默认接口 (主 API) =====
// const http = setupInterceptors(
//   axios.create({
//     baseURL: (import.meta.env.VITE_API_BASE || '').replace(/\/+$/, ''),
//     timeout: 15000,
//   })
// )

// // ===== Test 专用接口 =====
// const testHttp = setupInterceptors(
//   axios.create({
//     baseURL: (import.meta.env.VITE_API_BASE || '').replace(/\/+$/, ''),
//     timeout: 15000,
//   })
// )

// export { http, testHttp }
// export default http


// src/api/http.js
import axios from 'axios'
import { message } from 'ant-design-vue'
import { API_BASE } from '@/utils/apiBase'   // ★ 新增

function setupInterceptors(instance) {
  instance.interceptors.request.use(
    (config) => config,
    (error) => Promise.reject(error)
  )
  instance.interceptors.response.use(
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
  return instance
}

const http = setupInterceptors(
  axios.create({
    baseURL: API_BASE,   // 本地=绝对地址；线上=/api
    timeout: 15000,
  })
)

const testHttp = setupInterceptors(
  axios.create({
    baseURL: API_BASE,   // 若需要独立 TEST_BASE，可按同样方式扩展
    timeout: 15000,
  })
)

export { http, testHttp }
export default http