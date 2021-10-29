import request from '@/utils/request'

let urlType = 'fate-manager'
// 获取getSiteInfo数据
export function getSiteInfo(data) {
    return request({
        url: `/${urlType}/api/site/info`,
        method: 'post',
        data
    })
}

// 测试Telent
export function telnet(data) {
    return request({
        url: `/${urlType}/api/site/telnet`,
        method: 'post',
        data
    })
}

// 修改
export function update(data) {
    return request({
        url: `/${urlType}/api/site/update`,
        method: 'post',
        data
    })
}

// 已读
export function readmsg(data) {
    return request({
        url: `/${urlType}/api/site/readmsg`,
        method: 'post',
        data
    })
}

// 获取
export function getmsg(data) {
    return request({
        url: `/${urlType}/api/site/getmsg`,
        method: 'post',
        data
    })
}

// 获取 accessList
export function accessList(data) {
    return request({
        url: `/${urlType}/api/user/accesslist`,
        method: 'post',
        data
    })
}

// 获取userList远程搜索
export function userListDown(data) {
    return request({
        url: `/${urlType}/api/user/list`,
        method: 'post',
        data
    })
}

// 获取sitList下拉
export function siteListDown(data) {
    return request({
        url: `/${urlType}/api/user/sitelist`,
        method: 'post',
        data
    })
}

// 获取siteInfoList信息
export function siteInfoList(data) {
    return request({
        url: `/${urlType}/api/user/siteinfouserlist`,
        method: 'post',
        data
    })
}

// 添加用户信息
export function addUser(data) {
    return request({
        url: `/${urlType}/api/user/add`,
        method: 'post',
        data
    })
}

// 编辑用户
export function editUser(data) {
    return request({
        url: `/${urlType}/api/user/edit`,
        method: 'post',
        data
    })
}
// 删除用户
export function deleteUser(data) {
    return request({
        url: `/${urlType}/api/user/delete`,
        method: 'post',
        data
    })
}

// 获取弹框信息
export function getInstitutions(params) {
    return request({
        url: `/${urlType}/api/site/institutions`,
        method: 'get',
        params
    })
}

// 确认添加站点
export function applysite(data) {
    return request({
        url: `/${urlType}/api/site/applysite`,
        method: 'post',
        data
    })
}

// 获取我的站点
export function mySiteList(data) {
    return request({
        url: `/${urlType}/api/site/getsite`,
        method: 'post',
        data
    })
}

// 获取其他站点
export function otherSitList(data) {
    return request({
        url: `/${urlType}/api/site/other`,
        method: 'post',
        data
    })
}

// 获取审批状态列表
export function applyState(params) {
    return request({
        url: `/${urlType}/api/site/queryapply`,
        method: 'get',
        params
    })
}

// 获取审批状态
export function readState(data) {
    return request({
        url: `/${urlType}/api/site/readapplysite`,
        method: 'post',
        data
    })
}

// 获取已经审批列表
export function fatemanagerList(params) {
    return request({
        url: `/${urlType}/api/site/fatemanager`,
        method: 'get',
        params
    })
}

// 获取Site-Authorization 开关
export function getAuthorState(data) {
    return request({
        url: `/${urlType}/api/site/function`,
        method: 'post',
        data
    })
}

// 获取Site-Authorization 已读状态
export function readAuthorState(params) {
    return request({
        url: `/${urlType}/api/site/functionread`,
        method: 'get',
        params
    })
}

// 获取Cluster列表
export function getClusterList(params) {
    return request({
        url: `/${urlType}/api/dropdown/cluster`,
        method: 'get',
        params
    })
}

// 获取fateboard列表
export function getFateboardList(params) {
    return request({
        url: `/${urlType}/api/dropdown/fateboard`,
        method: 'get',
        params
    })
}

// 获取fateflow列表
export function getFateflowList(params) {
    return request({
        url: `/${urlType}/api/dropdown/fateflow`,
        method: 'get',
        params
    })
}

// 获取fateserving列表
export function getFateservingList(params) {
    return request({
        url: `/${urlType}/api/dropdown/fateserving`,
        method: 'get',
        params
    })
}

// 获取mysql列表
export function getMysqlList(params) {
    return request({
        url: `/${urlType}/api/dropdown/mysql`,
        method: 'get',
        params
    })
}

// 获取node列表
export function getNodeList(params) {
    return request({
        url: `/${urlType}/api/dropdown/node`,
        method: 'get',
        params
    })
}

// 获取rollsite列表
export function getrollsiteList(params) {
    return request({
        url: `/${urlType}/api/dropdown/rollsite`,
        method: 'get',
        params
    })
}

// 更新版本号
export function updateVersion(data) {
    return request({
        url: `/${urlType}/api/site/updateVersion`,
        method: 'post',
        data
    })
}

// 获取历史记录
export function applyHistory(data) {
    return request({
        url: `/${urlType}/api/site/applylog`,
        method: 'post',
        data
    })
}

// 获取子版本
export function getcomponentversion(data) {
    return request({
        url: `/${urlType}/api/dropdown/componentversion`,
        method: 'post',
        data
    })
}

// 获取Exchange列表
export function getexchangeList(data) {
    return request({
        url: `/${urlType}/api/site/exchange`,
        method: 'post',
        data
    })
}

// 查询机构审批状态
export function getNoticeapplysite(data) {
    return request({
        url: `/${urlType}/api/site/noticeapplysite`,
        method: 'post',
        data
    })
}

// 测试rollsite
export function testrollsite(data) {
    return request({
        url: `/${urlType}/api/site/testrollsite`,
        method: 'post',
        data
    })
}
