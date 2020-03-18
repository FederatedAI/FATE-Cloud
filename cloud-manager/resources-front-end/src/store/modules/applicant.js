const applicant = {
  namespaced: true,
  state: {
    appData: {
      'data': {
      }
    }

  },
  mutations: {
    ADD_DATA: (state, payload) => {
      state.appData.data = payload
    },
    // 基本信息
    ADD_APPESSENTIAL: (state, payload) => {
      state.appData.data.appid = payload.appid
      state.appData.data.state = 0
      state.appData.data.type = payload.type
      state.appData.data.dataset_id = payload.dataset_id
      state.appData.data.dataname = payload.dataname
      state.appData.data.description = payload.description
    },
    // 连接样本
    ADD_APPSAMPLEDATA: (state, payload) => {
      state.appData.data.sample_data = payload
      state.appData.data.sample_data.connect_params = {}
    },
    // 连接生产
    ADD_APPPRODUCEDATA: (state, payload) => {
      state.appData.data.produce_data = payload
    }
  },
  actions: {
  }
}

export default applicant
