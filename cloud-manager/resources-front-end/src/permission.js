// import router from './router'
// // import store from './store'
// import NProgress from 'nprogress' // progress bar
// import 'nprogress/nprogress.css' // progress bar style
// // import { Message } from 'element-ui'
// import { getToken, setToken } from '@/utils/auth' // getToken from cookie

// NProgress.configure({ showSpinner: false })// NProgress configuration

// const whiteList = ['/login'] // white
// router.beforeEach((to, from, next) => {
//   NProgress.start()
//   const indexJwt = window.location.href.indexOf('?jwt=')
//   const indexUmid = window.location.href.indexOf('&umId=')
//   const indexEnd = window.location.href.indexOf('#')

//   if (indexJwt !== -1) {
//     const jwt = window.location.href.substring(indexJwt + 5, indexUmid)
//     const umId = window.location.href.substring(indexUmid + 6, indexEnd)
//     setToken(jwt)
//     localStorage.setItem('umId', umId)
//     window.location.href = window.location.href.replace('?jwt=' + jwt, '').replace('&umId=' + umId, '')
//   } else if (getToken()) {
//     if (to.path === '/login') {
//       next({ path: '/' })
//       NProgress.done() // if current page is dashboard will not trigger	afterEach hook, so manually handle it
//     } else {
//       next()
//     }
//   } else {
//     if (whiteList.indexOf(to.path) !== -1) {
//       next()
//     } else {
//       const originService = window.location.href.split('#')[0]
//       window.location.href = 'http://' + window.location.host + '/account/v1/um/login?originService=' + originService
//     }
//   }
// })

// router.afterEach(() => {
//   NProgress.done()
// })
