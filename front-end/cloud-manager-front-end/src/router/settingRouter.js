
export default {
    path: '/setting',
    component: () => import('@/views/layout/Layout'),
    name: 'setting',
    children: [
        {
            name: 'Party ID', //
            path: 'partyid',
            hidden: true,
            component: () => import('@/views/setting/partyid')
        },
        {
            name: 'Certificate', //
            path: 'certificate',
            hidden: true,
            component: () => import('@/views/setting/certificate')
        },
        {
            name: 'Repository', //
            path: 'repository',
            component: () => import('@/views/setting/repository')
        },
        {
            name: 'partyuser', //
            path: 'partyuser',
            component: () => import('@/views/setting/partyuser')
        }, {
            name: 'Admin Access', //
            path: 'access',
            hidden: true,
            component: () => import('@/views/setting/access')
        }, {
            name: 'System Function Switch', //
            path: 'switch',
            hidden: true,
            component: () => import('@/views/setting/switch')
        }
    ]
}
