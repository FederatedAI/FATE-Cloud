const sourceAccess = {
  namespaced: true,
  state: {
    sourceData: {
      data: {
      }
    }

  },
  mutations: {
    // 所有信息
    ADD_DATA: (state, payload) => {
      state.sourceData.data = payload
    },
    // 基本信息
    ADD_ESSENTIAL: (state, payload) => {
      state.sourceData.data.appid = payload.appid
      state.sourceData.data.dataname = payload.dataname
      state.sourceData.data.description = payload.description
      state.sourceData.data.type = payload.type
      state.sourceData.data.dataset_id = payload.dataset_id
    },
    // 连接id库
    ADD_MATCHINFO: (state, payload) => {
      state.sourceData.data.match_info = payload
    },
    // 连接样本
    ADD_SAMPLEDATA: (state, payload) => {
      state.sourceData.data.sample_data = payload
    },
    // 连接生产
    ADD_PRODUCEDATA: (state, payload) => {
      state.sourceData.data.produce_data = payload.produce_data
      state.sourceData.data.max_call_count = payload.max_call_count
    }
  },

  actions: {
  }
}

export default sourceAccess
