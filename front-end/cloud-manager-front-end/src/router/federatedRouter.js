
export default {
    path: '/federated',
    component: () => import('@/views/layout/Layout'),
    name: 'federated',
    children: [
        {
            name: 'Site Manage', // 根据侧边导航栏命名
            path: 'site',
            hidden: true,
            component: () => import('@/views/federated/site')
        }, {
            name: 'siteadd', //
            path: 'siteadd',
            component: () => import('@/views/federated/siteadd')
        }, {
            name: 'detail', //
            path: 'detail',
            component: () => import('@/views/federated/sitedetail')
        }, {
            name: 'IP Manage', //
            path: 'ip',
            hidden: true,
            component: () => import('@/views/federated/ip')
        }, {
            name: 'Add an Exchange', //
            path: 'ipexchange',
            component: () => import('@/views/federated/ipaddexchange')
        },
        // {
        //     name: 'Service Manage', //
        //     path: 'sys',
        //     hidden: true,
        //     component: () => import('@/views/federated/system')
        // },
        {
            name: 'Site Monitor', //
            path: 'sitemonitor',
            hidden: true,
            component: () => import('@/views/federated/monitor')
        }, {
            name: 'Job Monitor', //
            path: 'jobmonitor',
            hidden: true,
            component: () => import('@/views/federated/jobMonitor')
        }

    ]
}
