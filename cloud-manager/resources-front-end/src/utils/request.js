import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

// axios.defaults.headers.common['Authorization'] = getToken()
// create an axios instance
const service = axios.create({
    baseURL: process.env.NODE_ENV === 'mock' ? process.env.VUE_APP_BASE_API : process.env.BASE_API,
    withCredentials: true, // 跨域请求时发送 cookies
    timeout: 15000 // request timeout
})

// request interceptor
// 请求拦截
service.interceptors.request.use(
    config => {
        // 开启全局loading
        let loading = document.getElementById('ajaxLoading')
        loading.style.display = 'block'
        // Do something before request is sent
        return config
    },
    error => {
        // Do something with request error
        Promise.reject(error)
    }
)

// response interceptor
// 响应拦截
service.interceptors.response.use(
    /**
   * If you want to get information such as headers or status
   * Please return  response => response
  */
    /**
   * 下面的注释为通过在response里，自定义code来标示请求状态
   * 当code返回如下情况则说明权限有问题，登出并返回到登录页
   * 如想通过 XMLHttpRequest 来状态码标识 逻辑可写在下面error中
   * 以下代码均为样例，请结合自生需求加以修改，若不需要，则可删除
   */
    response => {
        // 关闭全局loading
        let loading = document.getElementById('ajaxLoading')
        loading.style.display = 'none'
        const res = response.data
        if (res.code === 0) {
            return res
        } else if (msgCode(res.code)) {
            return Promise.reject(res)
        } else if (res.code === 110 || res.code === 130) {
            // code=110 系统错误 130 请先登录
            // 退出登录
            store.dispatch('setloginname', '').then(r => {
                localStorage.setItem('name', r)
                router.push({
                    path: '/home/welcome'
                })
            })
        } else {
            Message({
                message: `${res.msg ? res.msg : 'http reqest failed!'}`,
                type: 'error',
                duration: 5 * 1000
            })
            return Promise.reject(res)
        }

        function msgCode(code) {
            // code 解析
            let arr = [101, 102, 103, 108, 109, 122, 123, 124, 125]
            return arr.includes(code)
        }
    },
    error => {
        // 关闭全局loading
        let loading = document.getElementById('ajaxLoading')
        loading.style.display = 'none'
        Message({
            message: `${error}`,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
