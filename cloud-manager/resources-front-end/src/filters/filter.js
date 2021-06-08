import moment from 'moment'

let dateFormat = (value, formats) => {
    return value ? moment(value).format(formats || 'YYYY-MM-DD HH:mm:ss') : '--'
}

let timeFormat = (timeStamp) => {
    // console.log(timeStamp, 'timeStamp')
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

export {
    dateFormat,
    timeFormat
}
