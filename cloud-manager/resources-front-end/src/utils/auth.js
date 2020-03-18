import Cookies from 'js-cookie'

const TokenKey = 'WB-FDN-JWT'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getAuth(key) {
  return Cookies.get(key)
}

export function setAuth(key, value) {
  return Cookies.set(key, value)
}
