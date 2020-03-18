import Vue from 'vue'
// import 'normalize.css/normalize.css' // A modern alternative to CSS resets
// import ECharts from 'vue-echarts'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n
import VueClipboard from 'vue-clipboard2'
import Tooltip from '@/components/tooltip'
import '@/styles/index.scss' // global css
import 'echarts'

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
// import '@/permission' // permission control
import 'mavon-editor/dist/css/index.css'
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

Vue.use(ElementUI, { locale })
Vue.use(VueClipboard)
Vue.component('my-tooltip', Tooltip)
// Vue.component('v-chart', ECharts)
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
