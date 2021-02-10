const checkip = function (value) {
    // ip正则校验
    let ipReg = new RegExp(/^(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])$/)
    // 端口正则校验
    let portReg = new RegExp(/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/)
    // 域名正则校验
    // let domainReg = new RegExp(/[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/)
    if (
        // (domainReg.test(value) && portReg.test(value.split(':')[2])) ||
        ipReg.test(value.split(':')[0]) && portReg.test(value.split(':')[1])
    ) {
        return true
    } else {
        return false
    }
}

export default checkip
