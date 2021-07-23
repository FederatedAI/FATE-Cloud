import request from '@/utils/request'
let urlType = 'fate-manager'

// 登录
export function login(data) {
    return request({
        url: `/${urlType}/api/login/login`,
        method: 'post',
        data
    })
}

// 登出
export function logout(data) {
    return request({
        url: `/${urlType}/api/login/logout`,
        method: 'post',
        data
    })
}

// 获取登录用户信息
export function getInfo(params) {
    return request({
        url: `/${urlType}/api/user/info`,
        method: 'get',
        params
    })
}

// 注册
export function register(data) {
    return request({
        url: `/${urlType}/api/site/register`,
        method: 'post',
        data
    })
}

// 检测URL
export function checkUrl(data) {
    return request({
        url: `/${urlType}/api/site/checkUrl`,
        method: 'post',
        data
    })
}

// 第一次激活账号
export function activateAct(data) {
    return request({
        url: `/${urlType}/api/login/activate`,
        method: 'post',
        data
    })
}

// 测试Telnet
export function telnet(data) {
    return request({
        url: `/${urlType}/api/site/checkWeb`,
        method: 'post',
        data
    })
}
