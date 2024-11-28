<template>
    <div class="job-monitor">
        <div class="job-monitor-box">
            <div class="job-monitor-date">
                <div class="select-date">
                    <span >{{$t('Date')}} ：</span>
                    <el-date-picker
                        v-model="timevalue"
                        @change="initi"
                        class="picker"
                        type="daterange"
                        range-separator="~"
                        value-format="timestamp"
                        :start-placeholder="'start'"
                        :end-placeholder="'end'"
                        :picker-options="pickerOptions">
                    </el-date-picker>
                    <span class="institution">{{$t('Site')}} : </span>
                    <el-select @change="getJobs" v-model="party_id" class="select" placeholder="请选择">
                        <el-option
                        v-for="item in siteOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <div class="totals">
                    <span class="total-jobs">{{$t('Total jobs')}} : </span>
                    <span class="span">
                        {{typeTotalData.total}}
                    </span>
                    <span class="total-jobs">{{$t('Success')}} : </span>
                    <span class="span">
                        {{typeTotalData.success}}({{(typeTotalData.success_percent*100).toFixed(2)}}%)
                    </span>
                    <span class="total-jobs">{{$t('Failed')}} : </span>
                    <span class="span">
                        {{typeTotalData.failed}}({{(typeTotalData.failed_percent*100).toFixed(2)}}%)
                    </span>
                    <div class="jobs-sign">
                        <span class="success-sign"> </span>
                        <span> {{$t('Success')}} </span>
                        <span class="failed-sign"> </span>
                        <span > {{$t('Failed')}} </span>
                    </div>
                </div>
                <div class="job-modeling">
                    <div class="echarts-line">
                        <jobMonitorProgress ref="progress" :chartData="chartData" :lang="lang" @setProgressIndex="changeProgressType" />
                    </div>
                </div>
            </div>
            <div class="job-details">
                <div class="job-info">
                    <span class="total-jobs">{{$t("Jobs")}} : </span>
                    <span class="span">
                        {{dayTotal.total}}
                    </span>
                    <span class="total-jobs">{{$t("Success")}} : </span>
                    <span class="span">
                        {{dayTotal.success}}({{(dayTotal.success_percent*100).toFixed(2)}}%)
                    </span>
                    <span class="total-jobs">{{$t("Failed")}} : </span>
                    <span class="span">
                        {{dayTotal.failed}}({{(dayTotal.failed_percent*100).toFixed(2)}}%)
                    </span>
                    <span class="total-jobs">{{$t("Average duration")}} : </span>
                    <span class="span">
                        {{dayTotal.mean | timeFormat}}
                    </span>
                    <span class="total-jobs">{{$t("Min duration")}} : </span>
                    <span class="span">
                        {{dayTotal.min | timeFormat}}
                    </span>
                    <span class="total-jobs">{{$t("Max duration")}} :</span>
                    <span class="span">
                        {{dayTotal.max | timeFormat}}
                    </span>
                </div>
                <div class="table">
                    <div class="table-head"></div>
                    <el-table :data="dayListData.list" border max-height="300">
                        <el-table-column prop="time" :label="$t(`Date`)"> </el-table-column>
                        <el-table-column prop="total" :label="$t(`Jobs`)"> </el-table-column>
                        <el-table-column prop="success" :label="$t(`Success`)">
                            <template slot-scope="scope">
                            <span>{{scope.row.success}}({{(scope.row.success_percent*100).toFixed(2)}}%)</span>
                        </template>
                        </el-table-column>
                        <el-table-column prop="failed" :label="$t(`Failed`)">
                            <template slot-scope="scope">
                            <span>{{scope.row.failed}}({{(scope.row.failed_percent*100).toFixed(2)}}%)</span>
                        </template>
                        </el-table-column>
                        <el-table-column prop="mean" :label="$t(`Average duration`)">
                            <template slot-scope="scope">
                                <span >{{scope.row.mean | timeFormat}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="min" :label="$t(`Min duration`)">
                            <template slot-scope="scope">
                                <span >{{scope.row.min | timeFormat}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="max" :label="$t(`Max duration`)">
                            <template slot-scope="scope">
                                <span >{{scope.row.max | timeFormat}}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
                <div class="echarts-line">
                    <div class="line-title">
                        {{$t(`Failed rate`)}}
                    </div>
                    <jobMonitorfailed :chartData="failedChartData" />
                </div>
                <div class="echarts-line">
                    <div class="line-title">
                        {{$t(`Duration distribution`)}}
                    </div>
                    <jobMonitorDur :chartData="durChartData" />
                </div>
            </div>
        </div>

    </div>
</template>

<script>
import moment from 'moment'
import jobMonitorProgress from './jobMonitorProgress'
import jobMonitorfailed from './jobMonitorfailed'
import jobMonitorDur from './jobMonitorDur'
import { getSiteOptions, getJobs } from '@/api/monitor'
// 国际化
const local = {
    zh: {
        'Date': '日期',
        'start': '开始日期',
        'end': '结束日期',
        'Institution': '机构',
        'Site': '站点',
        'Total jobs': '总任务数',
        'Success': '成功',
        'Failed': '失败',
        'Intersect': '交集任务',
        'Modeling': '建模任务',
        'Upload': '上传任务',
        'Download': '下载任务',
        'Jobs': '任务数',
        'Average duration': '平均时长',
        'Min duration': '最小时长',
        'Max duration': '最大时长',
        'Failed rate': '失败率统计',
        'Duration distribution': '任务时长分布',
        'xAxis': '交集任务,建模任务,上传任务,下载任务'
    },
    en: {
        'Date': 'Date',
        'start': 'start',
        'end': 'end',
        'Institution': 'Institution',
        'Site': 'Site',
        'Total jobs': 'Total jobs',
        'Success': 'Success',
        'Failed': 'Failed',
        'Intersect': 'Intersect',
        'Modeling': 'Modeling',
        'Upload': 'Upload',
        'Download': 'Download',
        'Jobs': 'Jobs',
        'Average duration': 'Average duration',
        'Min duration': 'Min duration',
        'Max duration': 'Max duration',
        'Failed rate': 'Failed rate',
        'Duration distribution': 'Duration distribution',
        'xAxis': 'Intersect,Modeling,Upload,Download'

    }
}

const loading = document.getElementById('ajaxLoading')

export default {
    name: 'monitor',
    components: {
        jobMonitorProgress,
        jobMonitorfailed,
        jobMonitorDur
    },
    filters: {
    },
    data() {
        return {
            lang: '',
            progressType: 0,
            timevalue: [new Date() - 7 * 24 * 60 * 60 * 1000, new Date()],
            siteOptions: [],
            party_id: 'ALL',
            modelList: [
                { 'active': false, 'modelname': 'Intersect' },
                { 'active': false, 'modelname': 'Modeling' },
                { 'active': false, 'modelname': 'Upload' },
                { 'active': false, 'modelname': 'Download' }
            ],
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now()
                }
            },
            selectData: { total: {} },
            detail: {},
            typeTotalData: {
                'failed': '',
                'failed_percent': '',
                'success': '',
                'success_percent': '',
                'total': ''
            },
            dayTotal: {
                'failed': '',
                'failed_percent': '',
                'max': '',
                'mean': '',
                'min': '',
                'success': '',
                'success_percent': '',
                'total': ''
            },
            dayListData: {
                total: {},
                list: []
            },
            chartData: {
                success: [],
                failed: [],
                keyArr: []
            },
            failedChartData: {
                data: [],
                day: []
            },
            durChartData: []
        }
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        this.lang = this.$t(`xAxis`).split(',')
        this.initi()
    },

    mounted() {
        // this.showType(0)
    },
    methods: {
        initi() {
            this.getSiteOptions()
            this.getJobs()
        },
        getSiteOptions() {
            getSiteOptions().then(res => {
                let optionsArr = res.data || []
                this.siteOptions = Object.keys(optionsArr).map(key => {
                    return {
                        label: key,
                        value: optionsArr[key]
                    }
                })
                this.siteOptions.unshift({
                    label: 'ALL',
                    value: 'ALL'
                })
            })
        },
        getJobs() {
            loading.style.display = 'block'
            let data = {
                'startDate': moment(this.timevalue[0]).format('YYYYMMDD'),
                'endDate': moment(this.timevalue[1]).format('YYYYMMDD'),
                'party_id': this.party_id
            }
            getJobs(data).then(res => {
                this.detail = res.data || []
                this.typeTotalData = this.detail.total
                delete this.detail.total
                this.setProgressData(this.detail)
                this.changeProgressType('intersect')
                loading.style.display = 'none'
                this.$refs['progress'].selectedProgressBarIndex = 0
            })
        },
        changeSite() {
            this.getJobs()
        },
        changeProgressType(name) {
            this.selectData = this.detail[name]
            this.dayTotal = Object.assign({}, this.selectData.total)
            this.setDayChartData(this.selectData.day)
            this.setFailedData(this.selectData.day)
            this.setDurData(this.selectData.duration)
        },
        setDayChartData(data) {
            this.dayListData = []
            this.dayListData.list = Object.keys(data).map(key => {
                // this.dayListData.total = data[key].total
                return {
                    time: moment(key).format('YYYY-MM-DD'),
                    max: data[key].max,
                    min: data[key].min,
                    mean: data[key].mean,
                    ...data[key].total
                }
            })
        },
        setProgressData(data) {
            let success = []
            let failed = []
            let keyArr = []
            Object.keys(data).map(key => {
                keyArr.push(key)
                this.modelList.map((item, index) => {
                    if (item.modelname.toLocaleLowerCase() === key) {
                        success[index] = data[key].total.success
                        failed[index] = data[key].total.failed
                    }
                })
            })
            this.$set(this.chartData, 'keyArr', keyArr)
            this.$set(this.chartData, 'success', success)
            this.$set(this.chartData, 'failed', failed)
        },
        setFailedData(data) {
            let chartdata = []
            let day = []
            Object.keys(data).map(key => {
                chartdata.push((data[key].total.failed_percent * 100).toFixed(2))
                day.push(moment(key).format('YYYY-MM-DD'))
            })
            this.$set(this.failedChartData, 'data', chartdata)
            this.$set(this.failedChartData, 'day', day)
        },
        setDurData(data) {
            this.durChartData = []
            Object.keys(data).map(key => {
                this.durChartData.push(data[key])
            })
        }
    },
    watch: {
        '$i18n.locale'(newValue) {
            this.lang = this.$t(`xAxis`).split(',')
        },
        timevalue: {
            handler: function(val) {
                if (val) {
                    this.startDate = moment(val[0]).format('YYYYMMDD')
                    this.endDate = moment(val[1]).format('YYYYMMDD')
                }
            },
            immediate: true
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/jobmonitor.scss';
</style>
