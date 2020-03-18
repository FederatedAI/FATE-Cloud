import request from '@/utils/request'

/** 获取站点列表 */
export function getSiteList(data) {
  return request({
    url: '/cloud-manager/station/management/queryPage',
    method: 'post',
    data
  })
}

/** 添加站点 */
export function addSite(data) {
  return request({
    url: '/cloud-manager/station/management/add',
    method: 'post',
    data
  })
}

/** 更新站点 */
export function updateSite(data) {
  return request({
    url: '/cloud-manager/station/management/update',
    method: 'post',
    data
  })
}

/** 删除站点 */
export function deleteSite(data) {
  return request({
    url: '/cloud-manager/station/management/remove',
    method: 'post',
    data
  })
}
