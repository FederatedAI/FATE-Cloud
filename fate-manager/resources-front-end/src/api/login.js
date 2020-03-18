import request from '@/utils/request'

// login
export function login(emailOrTel, password) {
  return request({
    url: '/authentication/login',
    method: 'post',
    data: {
      emailOrTel,
      password
    }
  })
}

// getInfo
export function getInfo(data) {
  return request({
    url: '/user/getUserBaseInfoVO',
    method: 'post',
    data
  })
}

// logout
export function logout() {
  return request({
    url: '/authentication/logout',
    method: 'post'
  })
}
