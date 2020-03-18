// 用户管理路由

import Layout from '@/views/layout/Layout'

const userManagerRouter = {
  path: '/federal',
  component: Layout,
  name: 'manageFederal',
  redirect: '/federal/site',
  meta: {
    title: 'Federal'
  },
  children: [
    {
      path: 'site',
      name: 'site',
      component: () => import('@/views/manager/federalSite'),
      meta: { title: 'Federated Management' }
    }
  ]
}

export default userManagerRouter
