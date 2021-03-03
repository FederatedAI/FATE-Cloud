
// idRange响应解析
export function responseRange(params) {
    let resStr = ''
    params && params.forEach(item => {
        if (item.leftRegion === item.rightRegion) {
            resStr = resStr.concat(`${item.leftRegion};`)
        } else {
            resStr = resStr.concat(`${item.leftRegion}~${item.rightRegion};`)
        }
    })
    return resStr.substr(0, resStr.length - 1)
}

// idRange请求拼接
export function requestRange(params) {
    let noSpaceStr = params.replace(/\s+/g, '')
    let rangeArr = noSpaceStr.split(/;+；+|；+;+|;+|；+/)
    let regions = []
    rangeArr.forEach(item => {
        let itemArr = item.split(/~|-/).map(item => parseInt(item))
        if (itemArr.length === 2) {
            if (itemArr[0] < itemArr[1]) {
                let obj = {
                    leftRegion: Number(itemArr[0]),
                    rightRegion: Number(itemArr[1])
                }
                regions.push(obj)
            }
        } else if (itemArr[0]) {
            let obj = {
                leftRegion: Number(itemArr[0]),
                rightRegion: Number(itemArr[0])
            }
            regions.push(obj)
        }
    })
    // 去除重复
    regions = [...new Set(regions.map(item => JSON.stringify(item)))].map(item => JSON.parse(item))
    return regions
}
