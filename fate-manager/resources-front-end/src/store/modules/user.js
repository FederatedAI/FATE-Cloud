import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

const user = {
  state: {
    token: getToken(),
    name: '',
    avatar: '',
    userid: '',
    roles: [],
    emptyPassword: '',
    wechatNickname: ''
  },
  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_USERID: (state, userid) => {
      state.userid = userid
    },
    SET_EMPTYPWD: (state, emptyPassword) => {
      state.emptyPassword = emptyPassword
    },
    SET_WECHATNICKNAME: (state, wechatNickname) => {
      state.wechatNickname = wechatNickname
    },
    SET_TEL: (state, tel) => {
      state.tel = tel
    },
    SET_EMAIL: (state, email) => {
      state.email = email
    },
    SET_NICKNAME: (state, nickname) => {
      state.nickname = nickname
    }
  },

  actions: {
    // login
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      return new Promise((resolve, reject) => {
        login(username, userInfo.password).then(response => {
          const data = response.data
          const token = data.jwt
          setToken(token)
          commit('SET_TOKEN', token)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // GetInfo
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getInfo(state.token).then(response => {
          const data = response.data

          commit('SET_ROLES', [data.realNameVerified])
          commit('SET_NAME', data.nickname)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // LogOut
    LogOut({ commit }) {
      return new Promise((resolve, reject) => {
        logout().then(res => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          removeToken()
          resolve()
        }).catch(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          removeToken()
          resolve()
          // reject(error)
        })
      })
    }
  }
}

export default user
