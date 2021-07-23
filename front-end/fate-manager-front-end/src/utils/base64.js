
// 解码
var keyStr = 'ABCDEFGHIJKLMNOP' + 'QRSTUVWXYZabcdef' + 'ghijklmnopqrstuv' + 'wxyz0123456789+/' + '='
export function decode64(input) {
    var output = ''
    var chr1; var chr2; var chr3 = ''
    var enc1; var enc2; var enc3; var enc4 = ''
    var i = 0
    input = input.replace(/[^A-Za-z0-9\+\/\=]/g, '')
    do {
        enc1 = keyStr.indexOf(input.charAt(i++))
        enc2 = keyStr.indexOf(input.charAt(i++))
        enc3 = keyStr.indexOf(input.charAt(i++))
        enc4 = keyStr.indexOf(input.charAt(i++))
        chr1 = (enc1 << 2) | (enc2 >> 4)
        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2)
        chr3 = ((enc3 & 3) << 6) | enc4
        output = output + String.fromCharCode(chr1)
        if (enc3 !== 64) {
            output = output + String.fromCharCode(chr2)
        }
        if (enc4 !== 64) {
            output = output + String.fromCharCode(chr3)
        }
        chr1 = chr2 = chr3 = ''
        enc1 = enc2 = enc3 = enc4 = ''
    } while (i < input.length)
    return unescape(output)
}
// 编码
export function encode64(input) {
    input = escape(input)
    var output = ''
    var chr1; var chr2; var chr3 = ''
    var enc1; var enc2; var enc3; var enc4 = ''
    var i = 0
    do {
        chr1 = input.charCodeAt(i++)
        chr2 = input.charCodeAt(i++)
        chr3 = input.charCodeAt(i++)
        enc1 = chr1 >> 2
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4)
        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6)
        enc4 = chr3 & 63
        if (isNaN(chr2)) {
            enc3 = enc4 = 64
        } else if (isNaN(chr3)) {
            enc4 = 64
        }
        output = output +
            keyStr.charAt(enc1) +
            keyStr.charAt(enc2) +
            keyStr.charAt(enc3) +
            keyStr.charAt(enc4)
        chr1 = chr2 = chr3 = ''
        enc1 = enc2 = enc3 = enc4 = ''
    } while (i < input.length)
    return output
}
// utf8转为utf16
export function utf8to16(str) {
    let out, i, len, c
    let char2, char3
    out = ''
    len = str.length
    i = 0
    while (i < len) {
        c = str.charCodeAt(i++)
        switch (c >> 4) {
        case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
        // 0xxxxxxx
            out += str.charAt(i - 1)
            break
        case 12: case 13:
            // 110x xxxx 10xx xxxx
            char2 = str.charCodeAt(i++)
            out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F))
            break
        case 14:
            // 1110 xxxx 10xx xxxx 10xx xxxx
            char2 = str.charCodeAt(i++)
            char3 = str.charCodeAt(i++)
            out += String.fromCharCode(((c & 0x0F) << 12) |
                    ((char2 & 0x3F) << 6) |
                    ((char3 & 0x3F) << 0))
            break
        }
    }
    return out
}

// utf16转为utf8：
export function utf16to8(str) {
    let out, i, len, c
    out = ''
    len = str.length
    for (i = 0; i < len; i++) {
        c = str.charCodeAt(i)
        if ((c >= 0x0001) && (c <= 0x007F)) {
            out += str.charAt(i)
        } else if (c > 0x07FF) {
            out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F))
            out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F))
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F))
        } else {
            out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F))
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F))
        }
    }
    return out
}
