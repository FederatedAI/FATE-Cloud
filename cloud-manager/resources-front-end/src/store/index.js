import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import user from './modules/user'
import sourceAccess from './modules/sourceAccess'
import applicant from './modules/applicant'
import getters from './getters'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    user,
    sourceAccess,
    applicant
  },
  getters
})

export default store
