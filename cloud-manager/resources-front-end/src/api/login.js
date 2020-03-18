import request from '@/utils/request'

export function login(appid, pwd) {
  return request({
    url: '/login/login',
    method: 'post',
    data: {
      appid,
      pwd
    }
  })
}

export function getInfo(token) {
  return request({
    url: '/login/info',
    method: 'get',
    params: { token }
  })
}
