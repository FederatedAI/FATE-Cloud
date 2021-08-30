export function checkip(value) {
    console.log(value, 'value-reg')
    if (value && value.indexOf(';') > -1) {
        return new RegExp(/^([0-9a-zA-Z.*-]+:[0-9]{1,5};)+$/).test(value)
    } else {
        return new RegExp(/^[0-9a-zA-Z.*-]+:[0-9]{1,5}$/).test(value)
    }
}
