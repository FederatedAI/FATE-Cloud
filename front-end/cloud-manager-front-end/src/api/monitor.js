import request from '@/utils/request'

let urlType = 'cloud-manager'

export function institutionsListPeriod(data) {
    return request({
        url: `/${urlType}/api/job/v3/summary/institutions/each/period`,
        method: 'post',
        data
    })
}

export function siteListPeriod(data) {
    return request({
        url: `/${urlType}/api/job/v3/summary/site/each/period`,
        method: 'post',
        data
    })
}

export function institutionsSataPeriod(data) {
    return request({
        url: `/${urlType}/api/job/v3/institutions/period`,
        method: 'post',
        data
    })
}

export function institutionsAllPeriod(data) {
    return request({
        url: `/${urlType}/api/job/v3/summary/institutions/all/period`,
        method: 'post',
        data
    })
}

export function siteAllPeriod(data) {
    return request({
        url: `/${urlType}/api/job/v3/summary/site/all/period`,
        method: 'post',
        data
    })
}
// 新获取site接口
export function getSummarySite(data) {
    return request({
        url: `/${urlType}/api/job/v3/site/period`,
        method: 'post',
        data
    })
}

// job监控板块
export function getFinished(data) {
    return request({
        url: `/${urlType}/api/job/v3/find/summary/finished`,
        method: 'post',
        data
    })
}

// 任务时长
export function getDuration(data) {
    return request({
        url: `/${urlType}/api/job/v3/find/typed/duration`,
        method: 'post',
        data
    })
}

// 任务数据列表
export function getTypeTable(data) {
    return request({
        url: `/${urlType}/api/job/v3/find/typed/table`,
        method: 'post',
        data
    })
}

// 机构下的站点
export function getSiteAll(data) {
    return request({
        url: `/${urlType}/api/job/v3/site/all`,
        method: 'post',
        data
    })
}
