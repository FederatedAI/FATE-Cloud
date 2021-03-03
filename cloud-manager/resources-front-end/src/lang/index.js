import Vue from 'vue'
import VueI18n from 'vue-i18n'
import en from './en'
import zh from './zh'
import { getCookie } from '@/utils/auth'
import elementEnLocale from 'element-ui/lib/locale/lang/en' // element-ui lang
import elementZhLocale from 'element-ui/lib/locale/lang/zh-CN'// element-ui lang

Vue.use(VueI18n)
const messages = {
    en: {
        ...elementEnLocale,
        ...en
    },
    zh: {
        ...elementZhLocale,
        ...zh
    }
}

const i18n = new VueI18n({
    locale: getCookie('language') || 'zh', // set locale
    messages // set locale messages
})

export default i18n
