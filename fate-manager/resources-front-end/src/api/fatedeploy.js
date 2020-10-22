import request from '@/utils/request'

let urlType = 'fate-manager'
// 获取Service Overview列表数据
export function getPrepare(params) {
    return request({
        url: `/${urlType}/api/deploy/prepare`,
        method: 'get',
        params
    })
}

// pull按钮
export function pull(data) {
    return request({
        url: `/${urlType}/api/deploy/pull`,
        method: 'post',
        data
    })
}

// pull列表数据
export function getPullList(data) {
    return request({
        url: `/${urlType}/api/deploy/pulllist`,
        method: 'post',
        data
    })
}

// install列表数据
export function getInstallList(data) {
    return request({
        url: `/${urlType}/api/deploy/installlist`,
        method: 'post',
        data
    })
}

// install列表数据
export function getTestList(data) {
    return request({
        url: `/${urlType}/api/deploy/testlist`,
        method: 'post',
        data
    })
}
// 第一个next按钮
export function tofistnext(data) {
    return request({
        url: `/${urlType}/api/deploy/commit`,
        method: 'post',
        data
    })
}
// 部署2的start按钮
export function installStart(data) {
    return request({
        url: `/${urlType}/api/deploy/install`,
        method: 'post',
        data
    })
}
// 部署3的start按钮
export function autotestStart(data) {
    return request({
        url: `/${urlType}/api/deploy/autotest`,
        method: 'post',
        data
    })
}
// 更新地址
export function updateIp(data) {
    return request({
        url: `/${urlType}/api/deploy/update`,
        method: 'post',
        data
    })
}

// 确定更新版本
export function toupgrade(data) {
    return request({
        url: `/${urlType}/api/deploy/upgrade`,
        method: 'post',
        data
    })
}

// 点击序列
export function toClick(data) {
    return request({
        url: `/${urlType}/api/deploy/click`,
        method: 'post',
        data
    })
}

// 点击跳转fateboard
export function tofateboard(data) {
    return request({
        url: `/${urlType}/api/deploy/fateboard`,
        method: 'post',
        data
    })
}
