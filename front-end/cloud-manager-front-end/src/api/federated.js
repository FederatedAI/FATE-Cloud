import request from '@/utils/request'

// 获取sit manange 表格数据并查询
export function siteList(data) {
    return request({
        url: '/cloud-manager/api/site/page/cloudManager',
        method: 'post',
        data
    })
}

// 获取site下拉
export function siteListAll(data) {
    return request({
        url: '/cloud-manager/api/site/find/all',
        method: 'post',
        data
    })
}

// // 下拉获取getParty范围
// export function getPartyRang(data) {
//     return request({
//         url: '/cloud-manager/api/group/findPagedRangeInfos',
//         method: 'post',
//         data
//     })
// }

// 下拉获取getParty范围
export function getPartyRang(data) {
    return request({
        url: '/cloud-manager/api/group/findPagedRegionInfoNew',
        method: 'post',
        data
    })
}

// 检查PartId是否被占用
export function checkPartId(data) {
    return request({
        url: '/cloud-manager/api/site/check',
        method: 'post',
        data
    })
}

// // site添加
// export function siteAdd(data) {
//     return request({
//         url: '/cloud-manager/api/site/add',
//         method: 'post',
//         data
//     })
// }

// site添加 新接口
export function siteAdd(data) {
    return request({
        url: '/cloud-manager/api/site/addNew',
        method: 'post',
        data
    })
}
// 获取默认值

export function resetNetwork(data) {
    return request({
        url: '/cloud-manager/api/site/cloudManager/network',
        method: 'post',
        data
    })
}

// site更新
export function siteUpdate(data) {
    return request({
        url: '/cloud-manager/api/site/updateNew',
        method: 'post',
        data
    })
}

// site删除
export function siteDelete(data) {
    return request({
        url: '/cloud-manager/api/site/delete',
        method: 'post',
        data
    })
}

// 获取siteInfo信息
export function getSiteInfo(data) {
    return request({
        url: '/cloud-manager/api/site/find',
        method: 'post',
        data
    })
}

// 测试Telnet
export function telnet(data) {
    return request({
        url: '/cloud-manager/api/site/checkWeb',
        method: 'post',
        data
    })
}

// 激活ip
export function activate(data) {
    return request({
        url: '/cloud-manager/api/site/activate',
        method: 'post',
        data
    })
}

// 选择器下拉数据
export function selectAll() {
    return request({
        url: '/cloud-manager/api/dropdown/all',
        method: 'get'
    })
}

// 获取IP Manager表格数据与查询
export function ipList(data) {
    return request({
        url: '/cloud-manager/api/site/ip/list',
        method: 'post',
        data
    })
}

// 获取是否同意覆盖旧ip
export function deal(data) {
    return request({
        url: '/cloud-manager/api/site/ip/deal',
        method: 'post',
        data
    })
}

// 检查SiteName是否重名
export function checkSiteName(data) {
    return request({
        url: '/cloud-manager/api/site/checkSiteName',
        method: 'post',
        data
    })
}

// 获取小版本信息
export function findinfo(data) {
    return request({
        url: '/cloud-manager/api/system/find',
        method: 'post',
        data
    })
}

// 获取Service Manage 表格信息
export function getSystemManage(data) {
    return request({
        url: '/cloud-manager/api/system/page',
        method: 'post',
        data
    })
}

// 获取Service Manage 表格信息
export function systemhistory(data) {
    return request({
        url: '/cloud-manager/api/system/history',
        method: 'post',
        data
    })
}

// 获取iphistory表格信息
export function iphistory(data) {
    return request({
        url: '/cloud-manager/api/site/ip/query/history',
        method: 'post',
        data
    })
}

// 获取site institutions列表
export function institutionsList(data) {
    return request({
        url: '/cloud-manager/api/site/institutions',
        method: 'post',
        data
    })
}

// 获取institutions下拉
export function institutionsListDropdown(data) {
    return request({
        url: '/cloud-manager/api/site/institutions/all/dropdown',
        method: 'post',
        data
    })
}

// 添加站点新增查询全部institutions下拉枚举
export function institutionsAll(data) {
    return request({
        url: '/cloud-manager/api/fate/user/institutions/all',
        method: 'post',
        data
    })
}

// 取消权限
export function cancelAuthority(data) {
    return request({
        url: '/cloud-manager/api/authority/cancel',
        method: 'post',
        data
    })
}

// 获取取消权限列表
export function cancelAuthorityList(data) {
    return request({
        url: '/cloud-manager/api/authority/cancelList',
        method: 'post',
        data
    })
}

// 获取site 获取历史记录(abandoned)
export function institutionsHistory(data) {
    return request({
        url: '/cloud-manager/api/authority/history',
        method: 'post',
        data
    })
}

// 获取site 获取历史记录
export function getinstitutionsHistory(data) {
    return request({
        url: '/cloud-manager/api/authority/history/fateManager',
        method: 'post',
        data
    })
}

// 获取红点状态
export function institutionsStatus(data) {
    return request({
        url: '/cloud-manager/api/authority/status',
        method: 'post',
        data
    })
}

// 点击红点详情
export function institutionsDetails(data) {
    return request({
        url: '/cloud-manager/api/authority/details',
        method: 'post',
        data
    })
}

//  点击同意或者拒绝 2同意，3拒绝
export function institutionsReview(data) {
    return request({
        url: '/cloud-manager/api/authority/review',
        method: 'post',
        data
    })
}

// 添加中下拉列表

export function addInstitutionsList(data) {
    return request({
        url: '/cloud-manager/api/fate/user/institutions',
        method: 'post',
        data
    })
}

// ins一行字
export function authorityPermiss(data) {
    return request({
        url: '/cloud-manager/api/authority/currentAuthority',
        method: 'post',
        data
    })
}

// 获取版本下拉
export function getversion(data) {
    return request({
        url: '/cloud-manager/api/dropdown/version',
        method: 'post',
        data
    })
}

// ip-change列表
export function getIpchangeList(data) {
    return request({
        url: '/cloud-manager/api/exchange/exchange/page',
        method: 'post',
        data
    })
}

// ip-change列表添加
export function addIpchange(data) {
    return request({
        url: '/cloud-manager/api/exchange/add',
        method: 'post',
        data
    })
}

// ip-change 列表 删除
export function deleteIpchange(data) {
    return request({
        url: '/cloud-manager/api/exchange/delete',
        method: 'post',
        data
    })
}
// 添加新的RollStie
export function addRollsite(data) {
    return request({
        url: '/cloud-manager/api/exchange/rollsite/add',
        method: 'post',
        data
    })
}

// 查询Rollsite 表格
export function getRollsiteList(data) {
    return request({
        url: '/cloud-manager/api/exchange/rollsite/page',
        method: 'post',
        data
    })
}
// 查询Rollsite Network Access 内部表格
export function getNetworkAccessList(data) {
    return request({
        url: '/cloud-manager/api/exchange/query',
        method: 'post',
        data
    })
}
// 点击publish
export function toPublish(data) {
    return request({
        url: '/cloud-manager/api/exchange/rollsite/publish',
        method: 'post',
        data
    })
}
// 点击删除rollsite
export function deleteRollsite(data) {
    return request({
        url: '/cloud-manager/api/exchange/rollsite/delete',
        method: 'post',
        data
    })
}

// 查询更新 rollsite update
export function rollsiteUpdate(data) {
    return request({
        url: '/cloud-manager/api/exchange/rollsite/update',
        method: 'post',
        data
    })
}

// Service Manage 列表
export function serviceManageList(data) {
    return request({
        url: '/cloud-manager/api/product/page',
        method: 'post',
        data
    })
}

// 添加列表
export function addManageList(data) {
    return request({
        url: '/cloud-manager/api/product/add',
        method: 'post',
        data
    })
}

// 添加列表
export function updateManageList(data) {
    return request({
        url: '/cloud-manager/api/product/update',
        method: 'post',
        data
    })
}

// 删除列表项
export function deleteManageList(data) {
    return request({
        url: '/cloud-manager/api/product/delete',
        method: 'post',
        data
    })
}

// 获取下拉选框
export function getSelect(data) {
    return request({
        url: '/cloud-manager/api/product/version',
        method: 'post',
        data
    })
}

// 添加或者编辑获取mane下拉
export function getnameSelect(data) {
    return request({
        url: '/cloud-manager/api/product/name',
        method: 'post',
        data
    })
}
