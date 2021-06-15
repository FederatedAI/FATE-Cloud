import Vue from 'vue'
import ECharts from 'vue-echarts'
import 'echarts'
import i18n from './lang'
import ElementUI from 'element-ui'
import singleMessageCell from './message'
import '@/styles/theme/index.css'
import '@/styles/index.scss'
import '@/styles/loading.scss'

import App from './App'
import store from './store'
import router from './router'
import waterfall from 'vue-waterfall2'
import * as _filter from './filters/filter'
import map from '@/utils/map'
import '@/icons' // icon

import '@/permission' // permission control

/**
 * This project originally used easy-mock to simulate data,
 * but its official service is very unstable,
 * and you can build your own service if you need it.
 * So here I use Mock.js for local emulation,
 * it will intercept your request, so you won't see the request in the network.
 * If you remove `../mock` it will automatically request easy-mock data.
 */
if (process.env.NODE_ENV === 'mock') {
    require('../mock') // simulation data
}
Vue.use(waterfall)

Vue.component('v-chart', ECharts)// 全局使用
Vue.component('v-bar', ECharts)
console.log(_filter, '_filter')
Object.keys(_filter).forEach(item => {
    Vue.filter(item, _filter[item])
})

Vue.use(ElementUI, {
    i18n: (key, value) => i18n.t(key, value)
})
// Vue.use(VueClipboard)
// Vue.use(mavonEditor)
// Vue.component('my-tooltip', Tooltip)
// Vue.component('v-chart', ECharts)// 全局使用
Vue.config.productionTip = false
Vue.prototype.$message = singleMessageCell
Vue.prototype.$Map = map

/* eslint-disable no-new */
new Vue({
    el: '#app',
    router,
    store,
    i18n,
    render: h => h(App)
})
