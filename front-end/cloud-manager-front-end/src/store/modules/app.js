import { getCookie, setCookie } from '@/utils/auth'
const app = {
    state: {
        language: getCookie('language') || 'zh',
        sidebar: [],
        active: 'Site Manage',
        sitestatus: false, // 是否打开，默认关闭
        autostatus: false // 是否打开，默认关闭
    },
    mutations: {
        TOGGLE_SIDEBAR: (state, sidebarArr) => {
            state.sidebar = sidebarArr
        },
        SITE_STATUS: (state, data) => {
            state.sitestatus = data
        },
        AUTO_STATUS: (state, data) => {
            state.autostatus = data
        }, // 中英文
        SET_LANGUAGE: (state, language) => {
            state.language = language
            setCookie('language', language)
        },
        SET_ACTIVE: (state, active) => {
            state.active = active
        }
    },
    actions: {
        ToggleSideBar: ({ commit }, keyPath) => {
            commit('TOGGLE_SIDEBAR', keyPath)
        },
        Getsitestatus: ({ commit }, data) => {
            commit('SITE_STATUS', data)
        },
        Getautostatus: ({ commit }, data) => {
            commit('AUTO_STATUS', data)
        },
        // 设中英文
        setLanguage({ commit }, language) {
            commit('SET_LANGUAGE', language)
        },
        SetMune({ commit }, active) {
            commit('SET_ACTIVE', active)
        }
    }
}

export default app
