import request from '@/utils/request'

let urlType = 'fate-manager'
// prepra 页面
// export function ansibleIp(data) {
//     return request({
//         url: `/${urlType}/api/ansible/prepare`,
//         method: 'post',
//         data
//     })
// }

// 第一步check 点击开始
// export function checkStart(data) {
//     return request({
//         url: `/${urlType}/api/ansible/check`,
//         method: 'post',
//         data
//     })
// }
// 第一步获取nodeList下拉
export function getNodeList(params) {
    return request({
        url: `/${urlType}/api/dropdown/managernode`,
        method: 'get',
        params
    })
}

// 第一步check system 列表
// export function checkList(data) {
//     return request({
//         url: `/${urlType}/api/ansible/getcheck`,
//         method: 'post',
//         data
//     })
// }

// 第二步步ansible 开始键
// export function ansibleStart(data) {
//     return request({
//         url: `/${urlType}/api/ansible/deployansible`,
//         method: 'post',
//         data
//     })
// }

// 第二步ansible 列表
// export function ansibleList(params) {
//     return request({
//         url: `/${urlType}/api/ansible/list`,
//         method: 'get',
//         params
//     })
// }
// 第二步步acquire列表
export function toacquire(data) {
    return request({
        url: `/${urlType}/api/ansible/autoacquire`,
        method: 'post',
        data
    })
}

// 第三步upload获取列表
export function toupload(data) {
    return request({
        url: `/${urlType}/api/ansible/upload`,
        method: 'post',
        data
    })
}

// 第三步点击next
export function fatepackagelist(data) {
    return request({
        url: `/${urlType}/api/ansible/componentlist`,
        method: 'post',
        data
    })
}

// 第三步点击next
export function commitNext(data) {
    return request({
        url: `/${urlType}/api/ansible/commit`,
        method: 'post',
        data
    })
}
// 第四步install安装获取列表
export function ansibleInstall(data) {
    return request({
        url: `/${urlType}/api/ansible/install`,
        method: 'post',
        data
    })
}

// 第四步获取Install
export function getAnsibleInstallList(data) {
    return request({
        url: `/${urlType}/api/ansible/installlist`,
        method: 'post',
        data
    })
}
// 第四步单独修改ip单独弹框下拉
export function getIpList(params) {
    return request({
        url: `/${urlType}/api/dropdown/manager`,
        method: 'get',
        params
    })
}
// 更新ip
export function ansibleUpdateIp(data) {
    return request({
        url: `/${urlType}/api/ansible/update`,
        method: 'post',
        data
    })
}

// 获取test-start按钮
export function ansibleAutoTest(data) {
    return request({
        url: `/${urlType}/api/ansible/autotest`,
        method: 'post',
        data
    })
}

// 获取test列表
export function ansibleTestList(data) {
    return request({
        url: `/${urlType}/api/ansible/testlist`,
        method: 'post',
        data
    })
}

// 步骤点击
export function ansibleClick(data) {
    return request({
        url: `/${urlType}/api/ansible/click`,
        method: 'post',
        data
    })
}

// 获取Toy test按钮
export function ansibletoyTest(data) {
    return request({
        url: `/${urlType}/api/ansible/autotest`,
        method: 'post',
        data
    })
}
// 确认升级
export function ansibleUpgrade(data) {
    return request({
        url: `/${urlType}/api/ansible/upgrade`,
        method: 'post',
        data
    })
}

// 日志弹框
export function ansibleLoge(data) {
    return request({
        url: `/${urlType}/api/ansible/log`,
        method: 'post',
        data
    })
}
