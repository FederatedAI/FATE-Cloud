import { getOrganizationEnum, getPartyIDEnum, getVersionEnum } from '@/api/deploy'
import { getAuthorState } from '@/api/home'
import { getCookie, setCookie } from '@/utils/auth'
const app = {
    state: {
        language: getCookie('language') || 'zh',
        sidebar: [],
        organization: [],
        partyId: [],
        version: [],
        autoState: false,
        siteState: false // site
    },
    mutations: {
        TOGGLE_SIDEBAR: (state, sidebarArr) => {
            state.sidebar = sidebarArr
        },
        ORGANIZATION: (state, data) => {
            state.organization = []
            data && data.length > 0 && data.forEach(item => {
                let obj = {}
                obj.federatedId = obj.value = item.federatedId
                obj.label = item.federatedOrganization
                state.organization.push(obj)
            })
        },
        PARTYID: (state, data) => {
            state.partyId = []
            data && data.length > 0 && data.forEach(item => {
                let obj = {}
                obj.text = item.siteName
                obj.deployStatus = item.DeployStatus
                obj.value = item.partyId
                obj.label = item.partyId
                state.partyId.push(obj)
            })
        },
        VERSION: (state, data) => {
            state.version = []
            data && data.length > 0 && data.forEach(item => {
                let obj = {}
                obj.value = item
                obj.label = item
                state.version.push(obj)
            })
        },
        AUTO_STATE: (state, data) => {
            state.autoState = data
        },
        SITE_STATE: (state, data) => {
            state.siteState = data
        },
        // 中英文
        SET_LANGUAGE: (state, language) => {
            state.language = language
            setCookie('language', language)
        }
    },
    actions: {
        ToggleSideBar: ({ commit }, keyPath) => {
            commit('TOGGLE_SIDEBAR', keyPath)
        },
        selectEnum: async ({ commit }, federatedId) => {
            let organizationRes = await getOrganizationEnum()
            commit('ORGANIZATION', organizationRes.data)
            let partyIdRes = await getPartyIDEnum({ federatedId })
            commit('PARTYID', partyIdRes.data)
            let versionRes = await getVersionEnum({ productType: 1 })
            commit('VERSION', versionRes.data)
            return organizationRes
        },
        TogetAuthorSite: async ({ commit }) => {
            // Site-Authorization开关状态
            let res = await getAuthorState()
            for (const iter of res.data) {
                if (iter.functionName === 'Site-Authorization') {
                    if (iter.status === 1) {
                        commit('SITE_STATE', true)
                    } else {
                        commit('SITE_STATE', false)
                    }
                } else if (iter.functionName === 'Auto-Deploy') {
                    if (iter.status === 1) {
                        commit('AUTO_STATE', true)
                    } else {
                        commit('AUTO_STATE', false)
                    }
                }
            }
            return res
        },
        // 设中英文
        setLanguage({ commit }, language) {
            commit('SET_LANGUAGE', language)
        }
    }
}

export default app
