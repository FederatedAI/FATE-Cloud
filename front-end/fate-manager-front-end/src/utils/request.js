import axios from 'axios'
// import router from '@/router'
import store from '@/store'
import { getToken, getCookie } from '@/utils/auth'
import Vue from 'vue'
import VueI18n from 'vue-i18n'

Vue.use(VueI18n)
const messages = {
    en: { ...require('../lang/en.js') },
    zh: { ...require('../lang/zh.js') }
}
const i18n = new VueI18n({
    locale: getCookie('language') || 'zh',
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
    let tipKey = ''
    if (msg.indexOf('msg') > -1) {
        tipKey = msg.split('msg')[1].trim().replace("'", '')
    } else {
        tipKey = msg.replace("'", '')
    }
    console.log(tipKey, 'tipKey')
    let tipText = tipI18n.$t(`m.errorTips.${tipKey}`).indexOf('m.errorTips') > -1 ? tipKey : tipI18n.$t(`m.errorTips.${tipKey}`)
    let message = msg
    if (msg) {
        message = tipText
        if (tipText.indexOf('already exists') > -1) {
            let name = tipText.split(': user')[1].split('already exists')[0]
            message = tipI18n.$t('m.errorTips.checkUserFailedExists', { name: name })
        } else if (tipText.indexOf('required parameters are missing') > -1) {
            let parameters = tipText.split('required parameters are missing:')[1]
            message = tipI18n.$t('m.errorTips.missingParameters', { parameters: parameters })
        } else if (tipText.indexOf('no found account by username:') > -1) {
            let username = tipText.split('no found account by username:')[1]
            message = tipI18n.$t('m.errorTips.noAccountByUsername', { username: username })
        }
    } else {
        message = tipI18n.$t('m.errorTips.reqestFailed')
    }
    if (msg.indexOf('code') > -1) {
        message += `错误码：${msg.split('code')[1].split('msg')[0].trim()}`
    }
    if (message === 'success') {
        Vue.prototype.$message.success({
            message: message,
            duration: 5 * 1000
        })
        return
    }
    Vue.prototype.$message.error({
        message: message,
        duration: 5 * 1000
    })
}

const loading = document.getElementById('ajaxLoading')

// 不开启loading的接口
let URL = [
    '/fate-manager/api/service/toy',
    '/fate-manager/api/site/getmsg',
    '/fate-manager/api/deploy/installlist',
    '/fate-manager/api/deploy/pulllist',
    '/fate-manager/api/deploy/testlist',
    '/fate-manager/api/service/test',
    '/fate-manager/api/deploy/testresult',
    '/fate-manager/api/site/function',
    '/fate-manager/api/ansible/testlist',
    '/fate-manager/api/ansible/getcheck',
    '/fate-manager/api/ansible/installlist',
    '/fate-manager/api/site/fatemanager'
]
// request interceptor
// 请求拦截
service.interceptors.request.use(
    config => {
        // 开启全局loading
        if (!URL.includes(config.url)) {
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
            loading.style.display = 'none'
        };
        if (res.code === 0) {
            return res
        } else if (res.code === 10066) {
            store.dispatch('LogOut').then(() => {
                this.$router.push({ path: '/welcome/login' })
                location.reload() // 为了重新实例化vue-router对象 避免bug
            }).catch(err => {
                console.log(err)
            })
        } else if (msgCode(res.code)) {
            return Promise.reject(res)
        } else {
            setErrorMsgToI18n(res.msg)
            // Message({
            //     message: `${res.msg ? res.msg : 'http reqest failed!'}`,
            //     type: 'error',
            //     duration: 5 * 1000
            // })
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
        loading.style.display = 'none'
        console.log(111)
        setErrorMsgToI18n(error)
        // Message({
        //     message: `${error}`,
        //     type: 'error',
        //     duration: 5 * 1000
        // })

        // 服务端发生错误退出
        // setTimeout(() => {
        //     router.push({
        //         path: '/welcome/login'
        //     })
        //     location.reload() // 为了重新实例化vue-router对象 避免bug
        // }, 1500)

        return Promise.reject(error)
    }
)

export default service
