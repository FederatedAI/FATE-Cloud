
export default {
    path: '/home',
    component: () => import('@/views/home/home'),
    name: 'home',
    children: [
        {
            name: 'welcome', //
            path: 'welcome',
            component: () => import('@/views/home/welcome')
        }, {
            name: 'register', //
            path: 'register',
            component: () => import('@/views/home/register')
        }, {
            name: 'login', //
            path: 'login',
            component: () => import('@/views/home/login')
        }

    ]
}
