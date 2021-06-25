<template>
  <div class="cooperation">
    <div class="cooperation-box">
        <div class="monitor-header">
            <el-radio-group class="radio" v-model="radio" @change="tohandle">
                <el-radio-button label="Today’s active data">{{$t('m.monitor.todayActiveData')}}</el-radio-button>
                <el-radio-button label="Cumulative active data">{{$t('m.monitor.cumulativeActiveData')}}</el-radio-button>
            </el-radio-group>
        </div>
        <div class="content">
            <cooperationdata ref="cooperationdata"/>
        </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import cooperationdata from './cooperationdata'

export default {
    name: 'cooperation',
    components: {
        cooperationdata
    },
    data() {
        return {
            radio: 'Today’s active data',
            timevalue: ''
        }
    },
    watch: {
        // $route: {
        // handler: function(val) {
        //     this.toPath()
        // },
        // immediate: true
        // }
    },
    computed: {
        ...mapGetters(['organization'])

    },
    created() {
    },
    mounted() {

    },
    methods: {
        tohandle(val) {
            this.$refs.cooperationdata.radio = val
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/cooperation.scss';

</style>
