import request from '@/utils/request'

let urlType = 'cloud-manager'

export function institutionsListToday(data) {
    return request({
        url: `/${urlType}/api/job/summary/institutions/each/today`,
        method: 'post',
        data
    })
}

export function institutionsListPeriod(data) {
    return request({
        url: `/${urlType}/api/job/summary/institutions/each/period`,
        method: 'post',
        data
    })
}

export function siteListToday(data) {
    return request({
        url: `/${urlType}/api/job/summary/site/each/today`,
        method: 'post',
        data
    })
}

export function siteListPeriod(data) {
    return request({
        url: `/${urlType}/api/job/summary/site/each/period`,
        method: 'post',
        data
    })
}

export function institutionsSataToday(data) {
    return request({
        url: `/${urlType}/api/job/institutions/today`,
        method: 'post',
        data
    })
}

export function institutionsSataPeriod(data) {
    return request({
        url: `/${urlType}/api/job/institutions/period`,
        method: 'post',
        data
    })
}

export function siteSataToday(data) {
    return request({
        url: `/${urlType}/api/job/site/today`,
        method: 'post',
        data
    })
}

export function siteSataPeriod(data) {
    return request({
        url: `/${urlType}/api/job/site/period`,
        method: 'post',
        data
    })
}

export function institutionsAllPeriod(data) {
    return request({
        url: `/${urlType}/api/job/summary/institutions/all/period`,
        method: 'post',
        data
    })
}

export function institutionsAllToday(data) {
    return request({
        url: `/${urlType}/api/job/summary/institutions/all/today`,
        method: 'post',
        data
    })
}

export function siteAllToday(data) {
    return request({
        url: `/${urlType}/api/job/summary/site/all/today`,
        method: 'post',
        data
    })
}
export function siteAllPeriod(data) {
    return request({
        url: `/${urlType}/api/job/summary/site/all/period`,
        method: 'post',
        data
    })
}
// 新获取site接口
export function getSummarySite(data) {
    return request({
        url: `/${urlType}/api/job/summary/site`,
        method: 'post',
        data
    })
}
