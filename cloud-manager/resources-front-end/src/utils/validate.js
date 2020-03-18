/**
 * Created by jiachenpan on 16/11/18.
 */

export function isvalidUsername(str) {
  const valid_map = ['admin', 'editor']
  return valid_map.indexOf(str.trim()) >= 0
}

export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

export function validMobilePhone(rule, value, callback) {
  const reg = /^[1][3,4,5,7,8][0-9]{9}$/
  if (!reg.test(value)) {
    callback(new Error('请输入合法的手机号'))
  } else {
    callback()
  }
}

export function validEmail(rule, value, callback) {
  const reg = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/
  if (!reg.test(value)) {
    callback(new Error('请输入合法的邮箱'))
  } else {
    callback()
  }
}
