<template>
  <div class="monitor-box">
    <div class="monitor">
      <div class="monitor-header">
        <el-radio-group class="radio" v-model="radio" @change="handleChahe">
            <el-radio-button label="Today’s active institution"></el-radio-button>
            <el-radio-button label="Cumulative active data"></el-radio-button>
        </el-radio-group>
      </div>
      <div class="monitor-body">
        <div class="content" >
            <monitortoday ref="monitortoday"/>
        </div >

      </div>

    </div>
  </div>
</template>

<script>

import monitortoday from './monitortoday'

export default {
    name: 'monitor',
    components: {
        monitortoday
    },
    filters: {
    },
    data() {
        return {
            radio: 'Today’s active institution'

        }
    },
    created() {
        // this.getInstitutionsToday()

        // this.getSiteToday()

    },

    mounted() {

    },
    methods: {
        handleChahe(val) {
            this.$refs['monitortoday'].type = val
            this.$refs['monitortoday'].initi()
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/monitor.scss';

</style>
