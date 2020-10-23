import axios from 'axios'
import { Message } from 'element-ui'

import store from '@/store'
import { getToken } from '@/utils/auth'
// axios.defaults.headers.common['Authorization'] = getToken()
// create an axios instance
const service = axios.create({
    baseURL: process.env.NODE_ENV === 'mock' ? process.env.VUE_APP_BASE_API : process.env.BASE_API,
    withCredentials: true, // 跨域请求时发送 cookies
    timeout: 15000 // request timeout
})
// 不开启loading的接口
let URL = [
    '/fate-manager/api/service/toy',
    '/fate-manager/api/site/getmsg',
    '/fate-manager/api/deploy/installlist',
    '/fate-manager/api/deploy/pulllist',
    '/fate-manager/api/deploy/testlist',
    '/fate-manager/api/service/test',
    '/fate-manager/api/deploy/testresult',
    '/fate-manager/api/site/function'
]
// request interceptor
// 请求拦截
service.interceptors.request.use(
    config => {
        // 开启全局loading
        if (!URL.includes(config.url)) {
            let loading = document.getElementById('ajaxLoading')
            loading.style.display = 'block'
        }
        // if (store.getters.token) {
        //     config.headers['token'] = getToken()
        // }
        // 让每个请求携带token-- ['X-Token']为自定义key 请根据实际情况自行修改
        // config.headers['Authorization'] = getToken()
        config.headers['token'] = getToken()
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

        let res = response.data
        let url = response.config.url
        // 关闭全局loading
        if (!URL.includes(url)) {
            let loading = document.getElementById('ajaxLoading')
            loading.style.display = 'none'
        };
        if (res.code === 0) {
            return res
        } else if (res.code === 10066) {
            store.dispatch('LogOut').then(() => {
                location.reload() // 为了重新实例化vue-router对象 避免bug
            })
        } else if (msgCode(res.code)) {
            return Promise.reject(res)
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
            let arr = [400, 10056, 10064]
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
        // 服务端发生错误退出
        // setTimeout(() => {
        //     store.dispatch('LogOut').then(res => {
        //         location.reload() // 为了重新实例化vue-router对象 避免bug
        //     })
        // }, 1500)

        return Promise.reject(error)
    }
)

export default service
