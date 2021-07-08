<template>
  <div class="cooperation">
    <div class="cooperation-box">
        <div class="monitor-header">
            <el-radio-group class="radio" v-model="radio" @change="tohandle">
                <el-radio-button label="Today’s active data">{{$t('Today’s active data')}}</el-radio-button>
                <el-radio-button label="Cumulative active data">{{$t('Cumulative active data')}}</el-radio-button>
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

import moment from 'moment'
// 国际化
const local = {
    zh: {
        'Today’s active data': '今日活跃数据',
        'Cumulative active data': '累加活跃数据'
    },
    en: {
        'Today’s active data': 'Today’s active data',
        'Cumulative active data': 'Cumulative active data'

    }
}
export default {
    name: 'cooperation',
    components: {
        cooperationdata
    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
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
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
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
