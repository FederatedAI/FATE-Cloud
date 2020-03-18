import axios from 'axios'
import { Message } from 'element-ui'
// import msgCode from './msgCode'

import store from '@/store'
import { getToken } from '@/utils/auth'

// axios.defaults.headers.common['Authorization'] = getToken()
// create an axios instance
const service = axios.create({
  baseURL: process.env.NODE_ENV === 'mock' ? process.env.VUE_APP_BASE_API : process.env.BASE_API,
  withCredentials: true, // 跨域请求时发送 cookies
  timeout: 15000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // Do something before request is sent
    if (store.getters.token) {
      // config.headers['Authorization'] = getToken()
      config.headers['WB-FDN-JWT'] = getToken()
    }
    return config
  },
  error => {
    // Do something with request error
    console.log('==', error) // for debug
    Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get information such as headers or status
   * Please return  response => response
  */
  response => {
    const res = response.data
    if (res.code !== '00000000' && res.code !== 0) {
      Message({
        message: res.message || res.msg,
        type: 'error',
        duration: 10 * 1000
      })
      return Promise.reject('error')
    } else {
      return res
    }
  },
  error => {
    if (error.response && error.response.status === 401) {
      store.dispatch('LogOut').then(() => {
        location.reload()
      })
    }
    console.log('err==', error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 10 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
