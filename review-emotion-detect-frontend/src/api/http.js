import axios from 'axios'
import { message } from 'ant-design-vue'
import { API_BASE, Test_API_BASE, SHARE_REVIEW_API_BASE } from '@/utils/apiBase'  

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
    baseURL: API_BASE,   
    timeout: 15000,
  })
)

const testHttp = setupInterceptors(
  axios.create({
    baseURL: Test_API_BASE,   
    timeout: 15000,
  })
)

const sharehttp = setupInterceptors(
  axios.create({
    baseURL: SHARE_REVIEW_API_BASE,   
    timeout: 15000,
  })
)

export { http, testHttp, sharehttp }
export default http