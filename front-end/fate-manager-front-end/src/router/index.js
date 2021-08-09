import Vue from 'vue'
import Router from 'vue-router'

/* Layout */
import Layout from '../views/layout/Layout'
// import marketingRouter from './modules/marketing-manage'

// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/**
 * hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
 *                                if not set alwaysShow, only more than one route under the children
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    title: 'title'               the name show in subMenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if false, the item will hidden in breadcrumb(default is true)
  }
 **/

export const constantRouterMap = [
    { path: '/', redirect: '/welcome/login', hidden: true },
    // { path: '/login', component: () => import('@/views/login/index'), hidden: true },
    // { path: '/regist', component: () => import('@/views/regist/index'), hidden: true },
    // { path: '*', redirect: '/home/homepage', hidden: true },
    // { path: '/home', component: () => import('@/views/home/home') },
    { path: '/404', component: () => import('@/views/404'), hidden: true },
    {
        path: '/welcome',
        component: () => import('@/views/welcome/index'),
        name: 'welcome',
        hidden: true,
        children: [
            {
                path: 'login',
                name: 'login', //
                hidden: true,
                component: () => import('@/views/welcome/login')
            },
            {
                path: 'register',
                name: 'register', //
                hidden: true,
                component: () => import('@/views/welcome/register')
            }, {
                name: 'activate', //
                path: 'activate',
                component: () => import('@/views/welcome/activate')
            }
        ]
    },
    {
        path: '/home',
        component: Layout,
        name: 'home',
        hidden: true,
        children: [
            {
                path: 'sitemanage',
                name: 'sitemanage', //
                component: () => import('@/views/home/sitemanage')
            },
            {
                path: 'index',
                name: 'homeview', //
                component: () => import('@/views/home/homeview')
            },
            {
                path: 'siteinfo',
                name: 'siteinfo',
                component: () => import('@/views/home/sitedetail')
            },
            {
                path: 'access',
                name: 'access',
                component: () => import('@/views/home/access')
            }
        ]
    },
    // {
    //     path: '/deploy',
    //     component: Layout,
    //     name: 'deploy',
    //     hidden: true,
    //     children: [
    //         {
    //             path: 'auto',
    //             name: 'auto', //
    //             component: () => import('@/views/deploy/auto')
    //         },
    //         {
    //             path: 'overview',
    //             name: 'overview', //
    //             component: () => import('@/views/deploy/overview')
    //         },
    //         {
    //             path: 'service',
    //             name: 'service', //
    //             component: () => import('@/views/deploy/service')
    //         },
    //         {
    //             path: 'prepare',
    //             name: 'prepare', //
    //             component: () => import('@/views/fatedeploy/prepare')
    //         }, {
    //             path: 'deploying',
    //             name: 'deploying', //
    //             component: () => import('@/views/fatedeploy/deploying')
    //         },
    //         {
    //             path: 'ansible',
    //             name: 'ansible', //
    //             component: () => import('@/views/fatedeployAnsible/deployAnsible')
    //         }
    //     ]
    // },
    {
        path: '/monitor',
        component: Layout,
        name: 'monitor',
        hidden: true,
        children: [
            {
                path: 'cooperation',
                name: 'cooperation', //
                component: () => import('@/views/monitor/cooperation')
            },
            {
                path: 'jobmonitor',
                name: 'jobmonitor', //
                component: () => import('@/views/monitor/jobMonitor')
            }
        ]
    }

]

// 获取原型对象上的push函数
const originalPush = Router.prototype.push
// 修改原型对象中的push方法
Router.prototype.push = function push(location) {
    return originalPush.call(
        this, location
    )
    // .catch(err => err)
}

const router = new Router({
    // mode: 'history', // 后端支持可开
    scrollBehavior: () => ({ x: 0, y: 0 }),
    routes: constantRouterMap
})

export default router
