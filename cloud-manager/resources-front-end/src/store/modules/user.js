// import { login, logout, getInfo } from '@/api/login'
// import { getToken, setToken, removeToken } from '@/utils/auth'

const user = {
    state: {
        siteName: '',
        getInfo: '',
        siteStatus: '',
        loginName: ''
    },
    mutations: {
        SITE_NAME: (state, data) => {
            state.siteName = data
        },
        SET_INFO: (state, data) => {
            state.getInfo = { ...data }
        },
        SET_STATUS: (state, data) => {
            state.siteStatus = data
        },
        SET_LOGINNAME: (state, data) => {
            state.loginName = data
        }
    },

    actions: {
        // siteName
        setSiteName: ({ commit }, data) => {
            commit('SITE_NAME', data)
        },
        getInfo: ({ commit }, data) => {
            commit('SET_INFO', data)
        },
        setSiteStatus: ({ commit }, data) => {
            commit('SET_STATUS', data)
        },
        setloginname: async ({ commit }, data) => {
            await commit('SET_LOGINNAME', data)
            return data
        }
    }
}

export default user
