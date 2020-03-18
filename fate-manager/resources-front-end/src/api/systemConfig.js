import request from '@/utils/request'

// get site info
export function getSiteInfo(params) {
  return request({
    url: '/node/management/query',
    method: 'get',
    params
  })
}

// save site info
export function saveSiteInfo(data) {
  return request({
    url: '/node/management/update',
    method: 'post',
    data
  })
}
