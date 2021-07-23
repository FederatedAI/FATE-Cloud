import { getCookie } from '@/utils/auth'
import Vue from 'vue'
import VueI18n from 'vue-i18n'

const messages = {
    en: { ...require('../lang/map-en.js') },
    zh: { ...require('../lang/map-zh.js') }
}
const i18n = new VueI18n({
    locale: getCookie('language') || 'zh',
    messages
})
const tipI18n = new Vue({ i18n })

const map = {
    siteType: [
        {
            value: '1',
            name: 'guest',
            label: tipI18n.$t('mp.guest')
        },
        {
            value: '2',
            name: 'host',
            label: tipI18n.$t('mp.host')
        }
    ],
    roleType: [
        {
            value: '1',
            name: 'admin',
            label: tipI18n.$t('mp.admin')
        },
        {
            value: '2',
            name: 'developerOrOp',
            label: tipI18n.$t('mp.developerOrOp')
        },
        {
            value: '3',
            name: 'businessOrDataAnalyst',
            label: tipI18n.$t('mp.businessOrDataAnalyst')
        }
    ],
    siteStatus: [
        {
            value: '-1',
            name: 'unknown',
            label: tipI18n.$t('mp.unknown')
        },
        {
            value: '1',
            name: 'notJoin',
            label: tipI18n.$t('mp.notJoin')
        },
        {
            value: '2',
            name: 'joined',
            label: tipI18n.$t('mp.joined')
        },
        {
            value: '3',
            name: 'deleted',
            label: tipI18n.$t('mp.deleted')
        }
    ],
    serviceStatus: [
        {
            value: '-1',
            name: 'unknown',
            label: tipI18n.$t('mp.unknown')
        },
        {
            value: '1',
            name: 'unavaiable',
            label: tipI18n.$t('mp.unavaiable')
        },
        {
            value: '2',
            name: 'avaiable',
            label: tipI18n.$t('mp.avaiable')
        }
    ],
    setLanguage(val, selectType) {
        console.log(val)
        let _self = this
        i18n.locale = val
        if (selectType) {
            Object.values(_self[selectType]).forEach(i => {
                i.label = tipI18n.$t(`mp.${i.name}`)
            })
        } else {
            Object.keys(_self).forEach(item => {
                Object.values(_self[item]).forEach(k => {
                    k.label = tipI18n.$t(`mp.${k.name}`)
                })
            })
        }
    }
}

export default map
