import request from '@/utils/request'

// 查询
export function find(params) {
    return request({
        url: '/cloud-manager/api/federation/find',
        method: 'get',
        params
    })
}
// 注册
export function register(data) {
    return request({
        url: '/cloud-manager/api/federation/register',
        // url: '/account/v1/authentication/checkQrCodeLoginStatus',
        method: 'post',
        data
    })
}

// 登录
export function login(data) {
    return request({
        url: '/cloud-manager//api/cloud/user/login',
        method: 'post',
        data
    })
}

// 登出
export function logout(data) {
    return request({
        url: '/cloud-manager/api/cloud/user/logout',
        method: 'post',
        data
    })
}
