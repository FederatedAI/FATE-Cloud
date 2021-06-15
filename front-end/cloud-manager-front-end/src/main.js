import Vue from 'vue'
import ECharts from 'vue-echarts'
import 'echarts'
import ElementUI from 'element-ui'
import singleMessageCell from './message'
import '@/styles/loading.scss'
import i18n from './lang'
import '@/styles/theme/index.css'
import '@/styles/index.scss'
import App from './App'
import store from './store'
import router from './router'
import * as _filter from './filters/filter'
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
Vue.component('v-chart', ECharts)
Vue.component('v-bar', ECharts)
Vue.use(ElementUI, {
    i18n: (key, value) => i18n.t(key, value)
})

console.log(_filter, '_filter')
Object.keys(_filter).forEach(item => {
    Vue.filter(item, _filter[item])
})

Vue.prototype.$message = singleMessageCell
console.log(Vue.prototype, 'vue')
Vue.config.productionTip = false
/* eslint-disable no-new */
new Vue({
    el: '#app',
    router,
    store,
    i18n,
    render: h => h(App)
})
