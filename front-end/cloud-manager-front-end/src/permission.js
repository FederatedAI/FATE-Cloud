import router from './router'
import store from '@/store'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { find } from '@/api/welcomepage'
NProgress.configure({ showSpinner: false })// NProgress configuration

const whiteList =
  [
      '/home/login',
      '/home/welcome',
      '/home/register'
  ]
router.beforeEach((to, from, next) => {
    NProgress.start()
    if (localStorage.getItem('name')) {
        if (!store.getters.siteStatus) {
            getStatus(to, from, next)
        } else {
            routeHandler(to, from, next)
        }
    } else {
        if (whiteList.indexOf(to.path) !== -1) {
            next()
        } else {
            next({ path: '/home/welcome' })
            NProgress.done()
        }
    }
})

const getStatus = (to, from, next) => {
    find().then(res => {
        if (res.data && res.data.name) {
            store.dispatch('getInfo', res.data)
            store.dispatch('setSiteStatus', 'registered')
        } else {
            store.dispatch('setSiteStatus', 'unregistered')
        }
        routeHandler(to, from, next)
    }).catch(() => {
        routeHandler(to, from, next)
    })
}

const routeHandler = (to, from, next) => {
    if (store.getters.siteStatus === 'registered') {
        if (to.path === '/home/welcome' || to.path === '/home/register' || to.path === '/home/login') {
            next({ path: '/federated/site' })
            NProgress.done()
        } else {
            next()
        }
    } else {
        // clear cache
        store.dispatch('setloginname', '').then(r => {
            localStorage.setItem('name', r)
        })
        if (whiteList.indexOf(to.path) !== -1) {
            next()
        } else {
            next({ path: '/home/welcome' })
            NProgress.done()
        }
    }
}

router.afterEach(() => {
    NProgress.done()
})
