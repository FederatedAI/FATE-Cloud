import axios from 'axios'
import store from '@/store'
import router from '@/router'
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import { getCookie } from '@/utils/auth'

Vue.use(VueI18n)
const messages = {
    en: { ...require('../lang/en.js') },
    zh: { ...require('../lang/zh.js') }
}
const i18n = new VueI18n({
    locale: getCookie('language') || store.getters.language,
    messages
})
const tipI18n = new Vue({ i18n })

// axios.defaults.headers.common['Authorization'] = getToken()
// create an axios instance
const service = axios.create({
    baseURL: process.env.NODE_ENV === 'mock' ? process.env.VUE_APP_BASE_API : process.env.BASE_API,
    withCredentials: true, // 跨域请求时发送 cookies
    timeout: 15000 // request timeout
})

const setErrorMsgToI18n = (msg) => {
    i18n.locale = getCookie('language') || store.getters.language
    let tipKey = msg.replace("'", '')
    let tipText = tipI18n.$t(`m.errorTips.${tipKey}`).indexOf('m.errorTips') > -1 ? msg : tipI18n.$t(`m.errorTips.${tipKey}`)
    Vue.prototype.$message.error({
        message: `${msg ? tipText : tipI18n.$t('m.errorTips.reqestFailed')}`,
        duration: 5 * 1000
    })
}

const loading = document.getElementById('ajaxLoading')

// 不开启loading的接口
let URL = [
    '/cloud-manager/api/authority/history',
    '/cloud-manager/api/authority/currentAuthority',
    '/cloud-manager/api/authority/status',
    '/cloud-manager/api/site/institutions/status/dropdown',
    '/cloud-manager/api/site/institutions',
    '/cloud-manager/api/site/page/cloudManager',
    '/cloud-manager/api/dropdown/version',
    '/cloud-manager/api/function/find/all'

]

// request interceptor
// 请求拦截
service.interceptors.request.use(
    config => {
        // 开启全局loading
        if (!URL.includes(config.url)) {
            loading.style.display = 'block'
        }
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
        const res = response.data
        let url = response.config.url
        // 关闭全局loading
        if (!URL.includes(url)) {
            loading.style.display = 'none'
        };

        if (res.code === 0) {
            return res
        } else if (response.config.responseType === 'blob') { // 流下载设置
            return response
        } else if (res.code === 130) {
            // code=110 系统错误 130 请先登录
            // 退出登录
            store.dispatch('setloginname', '').then(r => {
                localStorage.setItem('name', r)
                router.push({
                    path: '/home/welcome'
                })
            })
            return Promise.reject(res)
        } else if (msgCode(res.code)) {
            setErrorMsgToI18n(res.msg)
            return Promise.reject(res)
        } else {
            setErrorMsgToI18n(res.msg)
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
        loading.style.display = 'none'
        setErrorMsgToI18n(error)
        // Vue.prototype.$message.error({
        //     message: `${error}`,
        //     duration: 5 * 1000
        // })
        // 服务端发生错误退出
        store.dispatch('setloginname', '').then(r => {
            localStorage.setItem('name', r)
            router.push({
                path: '/home/welcome'
            })
            location.reload() // 为了重新实例化vue-router对象 避免bug
        })
        return Promise.reject(error)
    }
)

export default service
