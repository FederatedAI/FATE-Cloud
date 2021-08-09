import request from '@/utils/request'

// // Party ID 删除组
// export function deleteGroup(data) {
//     return request({
//         url: '/cloud-manager/api/group/delete',
//         method: 'post',
//         data
//     })
// }

// Party ID 删除组 新接口
export function deleteGroup(data) {
    return request({
        url: '/cloud-manager/api/group/deleteNewGroup',
        method: 'post',
        data
    })
}

// 获取 Party ID 表格数据 新接口
export function partyidList(data) {
    return request({
        url: '/cloud-manager/api/group/findNewPage',
        method: 'post',
        data
    })
}

// 更新Party ID 行数据 新接口
export function partyidUpdate(data) {
    return request({
        url: '/cloud-manager/api/group/updateNewGroup',
        method: 'post',
        data
    })
}

// 添加 Party ID 行数据
export function partyidAdd(data) {
    return request({
        url: '/cloud-manager/api/group/addNewGroup',
        method: 'post',
        data
    })
}

// 添加检查range范围 新接口
export function checkRange(data) {
    return request({
        url: '/cloud-manager/api/group/checkNew',
        method: 'post',
        data
    })
}

// 更新查找range范围
export function checkUpdateRange(data) {
    return request({
        url: '/cloud-manager/api/group/checkUpdateRangeNew',
        method: 'post',
        data
    })
}

// 获取ueser列表数据
export function getUeserList(data) {
    return request({
        url: '/cloud-manager/api/site/usedPage',
        method: 'post',
        data
    })
}

// 获取institutions下拉
export function getInstitutionsDomn(data) {
    return request({
        url: '/cloud-manager/api/site/institutionsInGroup',
        method: 'post',
        data
    })
}

// 检查groupname是否重复
export function checkGroupName(data) {
    return request({
        url: '/cloud-manager/api/group/checkGroupName',
        method: 'post',
        data
    })
}

// 查找cloud列表
export function accessCloudList(data) {
    return request({
        url: '/cloud-manager/api/cloud/user/find/page',
        method: 'post',
        data
    })
}

// cloud添加
export function addCloud(data) {
    return request({
        url: '/cloud-manager/api/cloud/user/add',
        method: 'post',
        data
    })
}

// cloud删除
export function deleteCloud(data) {
    return request({
        url: '/cloud-manager/api/cloud/user/delete',
        method: 'post',
        data
    })
}

// 查找manager列表
export function accessManagerList(data) {
    return request({
        url: '/cloud-manager/api/fate/user/find/page',
        method: 'post',
        data
    })
}

// manager添加
export function addManager(data) {
    return request({
        url: '/cloud-manager/api/fate/user/add',
        method: 'post',
        data
    })
}
// 更新manager
export function updataManager(data) {
    return request({
        url: '/cloud-manager/api/fate/user/update',
        method: 'post',
        data
    })
}

// 更新manager
export function reactivateUser(data) {
    return request({
        url: '/cloud-manager/api/fate/user/reactivate',
        method: 'post',
        data
    })
}

// 获取开关状态
export function switchState(data) {
    return request({
        url: '/cloud-manager/api/function/find/all',
        method: 'post',
        data
    })
}

// 获取开关状态
export function updateSwitch(data) {
    return request({
        url: '/cloud-manager/api/function/update/status',
        method: 'post',
        data
    })
}

// 获取site状态
export function siteUpdateStatus(data) {
    return request({
        url: '/cloud-manager/api/function/update/scenario',
        method: 'post',
        data
    })
}

// 获取证书列表页
export function getCertiCate(data) {
    return request({
        url: '/cloud-manager/api/certificate/query',
        method: 'post',
        data
    })
}

// 证书页publish按钮
export function publishCert(data) {
    return request({
        url: '/cloud-manager/api/certificate/publish',
        method: 'post',
        data
    })
}
// 证书页invalidate按钮
export function invalidateCert(data) {
    return request({
        url: '/cloud-manager/api/certificate/revoke',
        method: 'post',
        data
    })
}
// 获取证书类型
export function certiType(data) {
    return request({
        url: '/cloud-manager/api/certificate/type/query',
        method: 'post',
        data
    })
}

// 添加证书列表
export function addCertificate(data) {
    return request({
        url: '/cloud-manager/api/certificate/save',
        method: 'post',
        data
    })
}

// 更新证书列表
export function updateCertificate(data) {
    return request({
        url: '/cloud-manager/api/certificate/update',
        method: 'post',
        data
    })
}

// 更新证书类型
export function updateCertifiType(data) {
    return request({
        url: '/cloud-manager/api/certificate/type/update',
        method: 'post',
        data
    })
}

// 下载证书
export function downloadCertificate(data) {
    return request({
        url: '/cloud-manager/api/certificate/download',
        method: 'post',
        data,
        responseType: 'blob'
    })
}
