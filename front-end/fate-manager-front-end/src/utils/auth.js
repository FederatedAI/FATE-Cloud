import Cookies from 'js-cookie'

const TokenKey = 'WB-FDN-JWT'

export function getCookie(key) {
    return Cookies.get(key)
}

export function setCookie(key, value) {
    return Cookies.set(key, value)
}

export function removeCookie(key) {
    return Cookies.remove(key)
}

export function getToken() {
    return Cookies.get(TokenKey)
}

export function setToken(token) {
    return Cookies.set(TokenKey, token)
}

export function removeToken() {
    return Cookies.remove(TokenKey)
}
