import router from './router'
import store from '@/store'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from '@/utils/auth' // getToken from cookie
import { Message } from 'element-ui'
NProgress.configure({ showSpinner: false })// NProgress configuration

// 不重定向白名单
const whiteList =
  [
      '/welcome/login'
  ]
router.beforeEach((to, from, next) => {
    NProgress.start()
    // 有Token值
    if (getToken()) {
        if (!store.getters.userName) {
            // 拉登录用户信息取用户信息
            store.dispatch('GetInfo').then(res => {
                next()
            }).catch((err) => {
                store.dispatch('LogOut').then(() => {
                    Message.error(err || 'Verification failed, please login again')
                    next({ path: '/welcome/login' })
                    location.reload() // 为了重新实例化vue-router对象 避免bug
                })
                NProgress.done() // 结束Progress
            })
        } else {
            // 有登录用户信息，执行下一步
            if (to.path === '/welcome/login') {
                next({ path: '/home/sitemanage' })
                NProgress.done()
            } else {
                next()
            }
        }
        // 没有Token值 回到登录
    } else {
        if (whiteList.indexOf(to.path) !== -1) {
            next()
        } else {
            next({ path: '/welcome/login' })
            NProgress.done()
        }
    }
})

router.afterEach(() => {
    NProgress.done() // 结束Progress
})
