export function deepClone(obj) {
    let _obj = JSON.stringify(obj)
    let objClone = JSON.parse(_obj)
    return objClone
}
