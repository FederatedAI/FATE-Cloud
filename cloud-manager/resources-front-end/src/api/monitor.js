import request from '@/utils/request'

let urlType = 'fate-manager'

// 总柱状图
export function getTotal(data) {
    return request({
        url: `/${urlType}/api/monitor/total`,
        method: 'post',
        data
    })
}

// institution表格
export function getInstitution(data) {
    return request({
        url: `/${urlType}/api/monitor/institution`,
        method: 'post',
        data
    })
}

// site表格
export function getSite(data) {
    return request({
        url: `/${urlType}/api/monitor/site`,
        method: 'post',
        data
    })
}
