import { login, logout, getInfo } from '@/api/welcomepage'
import { getToken, setToken, removeToken, setCookie, getCookie, removeCookie } from '@/utils/auth'
const user = {
    state: {
        // 站点激活状态
        siteStatus: '',
        token: getToken(),
        role: {
            roleId: '',
            roleName: ''
        },
        // 登录用户名称
        userId: '',
        userName: '',
        // 权限
        permissionList: []
    },
    mutations: {
        SET_STATUS: (state, data) => {
            state.siteStatus = data
        },
        SET_TOKEN: (state, token) => {
            state.token = token
        },
        ROLE: (state, role) => {
            state.role = role
        },
        USER_Id: (state, userId) => {
            state.userId = userId
        },
        USER_NAME: (state, userName) => {
            state.userName = userName
        },
        PIL: (state, permissionList) => {
            state.permissionList = permissionList
        }
    },
    actions: {
        // siteName
        setSiteStatus: ({ commit }, data) => {
            commit('SET_STATUS', data)
        },
        // 账号登录
        Login({ commit }, params) {
            return new Promise((resolve, reject) => {
                login(params).then(response => {
                    const data = response.data
                    const token = data.token
                    setToken(token)
                    commit('SET_TOKEN', token)
                    resolve(response)
                }).catch(error => {
                    reject(error)
                })
            })
        },
        // 登出
        LogOut({ commit, state }) {
            return new Promise((resolve, reject) => {
                if (!state.userId) {
                    commit('USER_Id', getCookie('USER_Id'))
                    commit('USER_NAME', getCookie('USER_NAME'))
                }
                logout({ userId: state.userId, userName: state.userName }).then(response => {
                    commit('ROLE', {})
                    commit('USER_Id', '')
                    commit('USER_NAME', '')
                    commit('PIL', '')
                    removeCookie('USER_Id')
                    removeCookie('USER_NAME')
                    removeToken()
                    resolve(response)
                }).catch((error) => {
                    commit('ROLE', {})
                    commit('USER_Id', '')
                    commit('USER_NAME', '')
                    commit('PIL', [])
                    removeToken()
                    reject(error)
                })
            })
        },
        // 获取用户信息
        GetInfo({ commit }) {
            return new Promise((resolve, reject) => {
                getInfo().then(response => {
                    const data = response.data
                    const role = data.role
                    const userId = data.userId
                    const userName = data.userName
                    const permissionList = data.permissionList
                    commit('ROLE', role)
                    commit('USER_Id', userId)
                    commit('USER_NAME', userName)
                    setCookie('USER_Id', userId)
                    setCookie('USER_NAME', userName)
                    commit('PIL', permissionList)
                    resolve(response)
                }).catch(error => {
                    reject(error)
                })
            })
        }

    }

}

export default user
