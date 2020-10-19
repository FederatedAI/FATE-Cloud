import request from '@/utils/request'

let urlType = 'fate-manager'
// 获取Service Overview列表数据
export function getOverViewList(data) {
    return request({
        url: `/${urlType}/api/service/overview`,
        method: 'post',
        data
    })
}
// 获取Organization下拉
export function getOrganizationEnum(params) {
    return request({
        url: `/${urlType}/api/dropdown/federation`,
        method: 'get',
        params
    })
}
// 获取partID下拉
export function getPartyIDEnum(params) {
    return request({
        url: `/${urlType}/api/dropdown/site`,
        method: 'get',
        params
    })
}

// 获取Version下拉
export function getVersionEnum(params) {
    return request({
        url: `/${urlType}/api/dropdown/version`,
        method: 'get',
        params
    })
}

// 获取serviceList列表
export function getserviceList(data) {
    return request({
        url: `/${urlType}/api/service/info`,
        method: 'post',
        data
    })
}
// 获取Toy test按钮
export function toyTest(data) {
    return request({
        url: `/${urlType}/api/deploy/autotest`,
        method: 'post',
        data
    })
}

// 获取test按钮轮询
export function testresult(data) {
    return request({
        url: `/${urlType}/api/deploy/testresult`,
        method: 'post',
        data
    })
}
// 二次确认
export function testresultread(data) {
    return request({
        url: `/${urlType}/api/deploy/testresultread`,
        method: 'post',
        data
    })
}

// 获取log按钮
export function toLog(data) {
    return request({
        url: `/${urlType}/api/service/log`,
        method: 'post',
        data
    })
}

// 获取toAction按钮
export function toAction(data) {
    return request({
        url: `/${urlType}/api/service/action`,
        method: 'post',
        data
    })
}

// 更新版本upgradelist按钮
export function upGradeList(data) {
    return request({
        url: `/${urlType}/api/service/upgradelist`,
        method: 'post',
        data
    })
}

// 获取跳转页面
export function pagesStep(data) {
    return request({
        url: `/${urlType}/api/service/pagestatus`,
        method: 'post',
        data
    })
}

// 点击输入ip链接
export function toconnect(data) {
    return request({
        url: `/${urlType}/api/service/connectkubefate`,
        method: 'post',
        data
    })
}

// 状态
export function autoStatus(data) {
    return request({
        url: `/${urlType}/api/service/installstatus`,
        method: 'post',
        data
    })
}

// 获取中途安装版本号
export function deployversion(data) {
    return request({
        url: `/${urlType}/api/deploy/version`,
        method: 'post',
        data
    })
}

// 获取autotest日记
export function getTestLog(data) {
    return request({
        url: `/${urlType}/api/deploy/testlog`,
        method: 'post',
        data
    })
}
