
import moment from 'moment'
import map from '@/utils/map'
let timeFormat = (timeStamp) => {
    let secondTime = parseInt(timeStamp / 1000)
    let min = 0
    let h = 0
    let result = ''
    if (secondTime > 60) {
        min = parseInt(secondTime / 60)
        secondTime = parseInt(secondTime % 60)
        if (min > 60) {
            h = parseInt(min / 60)
            min = parseInt(min % 60)
        }
    }
    result = `${h.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')}:${secondTime.toString().padStart(2, '0')}`
    return result
}

let dateFormat = (value, formats) => {
    return value ? moment(value).format(formats || 'YYYY-MM-DD HH:mm:ss') : '--'
}

let getRoleType = (value) => {
    let roleId = `${value.roleId}` || `${value}`
    let maps = map['roleType'].filter(item => `${item.value}` === roleId)[0]
    return maps ? maps.label : value.roleName
}

let getSiteStatus = (value) => {
    let maps = map['siteStatus'].filter(item => item.value === `${value.code}`)[0]
    return maps ? maps.label : value.desc
}

let getServiceStatus = (value) => {
    let maps = map['serviceStatus'].filter(item => item.value === `${value.code}`)[0]
    return maps ? maps.label : value.desc
}

let getSiteType = (value) => {
    let code = value.code ? value.code : value
    let maps = map['siteType'].filter(item => item.value === `${code}`)[0]
    return maps ? maps.label : value.desc
}

export {
    timeFormat,
    dateFormat,
    getRoleType,
    getSiteStatus,
    getServiceStatus,
    getSiteType
}
