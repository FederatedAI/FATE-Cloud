
const app = {
    state: {
        sidebar: [],
        sitestatus: true, // 是否打开，默认打开
        autostatus: true // 是否打开，默认打开
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
        }
    }
}

export default app
