<template>
    <div class="job-monitor">
        <div class="job-monitor-date">
            <div class="select-date">
                <span class="line-title">{{$t('Date')}} ：</span>
                <el-date-picker
                    v-model="timevalue"
                    class="picker"
                    type="daterange"
                    range-separator="~"
                    value-format="timestamp"
                    :start-placeholder="$t('start')"
                    :end-placeholder="$t('end')"
                    :picker-options="pickerOptions">
                </el-date-picker>
                <span class="institution">{{$t('Institution')}} : </span>
                <el-select v-model="searchData.institutions" filterable @change="changeInstitution" class="select" placeholder="请选择">
                    <el-option key="" :label="$t('m.common.all')" value=""></el-option>
                    <el-option
                    v-for="(item,index) in institutionsSelectList"
                    :key="index"
                    :label="item.label"
                    :value="item.value">
                    </el-option>
                </el-select>
                <span class="site">{{$t('Site')}} : </span>
                <el-select v-model="searchData.site" filterable :disabled="searchData.institutions.length == 0" @change="getProgressData" class="select" placeholder="请选择">
                    <el-option key="" :label="$t('m.common.all')" value=""></el-option>
                    <el-option
                    v-for="(item,index) in sitesSelectList"
                    :key="index"
                    :label="item.label"
                    :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div class="totals">
                <span class="total-jobs">{{$t('Total jobs')}} : </span>
                <span class="span">
                    {{totalData.total}}
                </span>
                <span class="total-jobs">{{$t('Success')}} : </span>
                <span class="span">
                    {{totalData.successfulJobs}}({{totalData.successfulRatio}})
                </span>
                <span class="total-jobs">{{$t('Failed')}} : </span>
                <span class="span">
                    {{totalData.failedJobs}}({{totalData.failedRatio}})
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
                    {{selectData.typeListData.total}}
                </span>
                <span class="total-jobs">{{$t("Success")}} : </span>
                <span class="span">
                    {{selectData.typeListData.successfulJobs}}({{selectData.typeListData.successfulRatio}})
                </span>
                <span class="total-jobs">{{$t("Failed")}} : </span>
                <span class="span">
                    {{selectData.typeListData.failedJobs}}({{selectData.typeListData.failedRatio}})
                </span>
                <span class="total-jobs">{{$t("Average duration")}} : </span>
                <span class="span">
                    {{selectData.typeListData.avgDuration | timeFormat}}
                </span>
                <span class="total-jobs">{{$t("Min duration")}} :</span>
                <span class="span">
                    {{selectData.typeListData.minDuration | timeFormat}}
                </span>
                <span class="total-jobs">{{$t("Max duration")}} :</span>
                <span class="span">
                    {{selectData.typeListData.maxDuration | timeFormat}}
                </span>
            </div>
            <div class="table">
                <div class="table-head"></div>
                <el-table :data="dayListData.list" border max-height="300">
                    <el-table-column prop="time" :label="$t(`Date`)"> </el-table-column>
                    <el-table-column prop="total" :label="$t(`Jobs`)"> </el-table-column>
                    <el-table-column prop="success" :label="$t(`Success`)">
                        <template slot-scope="scope">
                            <span>{{scope.row.success}}({{scope.row.successRatio}})</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="failed" :label="$t(`Failed`)">
                        <template slot-scope="scope">
                            <span>{{scope.row.failed}}({{scope.row.failedRatio}})</span>
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
</template>

<script>
import moment from 'moment'
import jobMonitorProgress from './jobMonitorProgress'
import jobMonitorfailed from './jobMonitorfailed'
import jobMonitorDur from './jobMonitorDur'
import {
    getFinished,
    getDuration,
    getTypeTable,
    getSiteAll
} from '@/api/monitor'
import { institutionsListDropdown } from '@/api/federated'
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
            totalData: {},
            selectData: {
                typeListData: {}
            },
            institutionsSelectList: [],
            sitesSelectList: [],
            allChartData: {
                'intersect': {},
                'modeling': {},
                'upload': {},
                'download': {}
            },
            timevalue: [new Date() - 7 * 24 * 60 * 60 * 1000, new Date().getTime()],
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
            searchData: {
                beginDate: new Date(),
                endDate: new Date(),
                institutions: '',
                site: ''
            },
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now()
                }
            },
            chartData: {
                success: [],
                failed: []
            },
            failedChartData: {
                data: [],
                day: []
            },
            durChartData: []
        }
    },
    watch: {
        timevalue: {
            handler: function(val) {
                this.$set(this.searchData, 'beginDate', val[0])
                this.$set(this.searchData, 'endDate', val[1])
                this.getProgressData()
            },
            deep: true,
            immediate: true
        },
        '$i18n.locale'(newValue) {
            this.lang = this.$t(`xAxis`).split(',')
        }
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        this.lang = this.$t(`xAxis`).split(',')
        this.getProgressData()
        this.getinsSelectList()
    },

    mounted() {

    },
    methods: {
        getProgressData() {
            let success = []
            let failed = []
            getFinished(this.searchData).then(res => {
                // 总计
                let { failedJobs, failedRatio, successfulJobs, successfulRatio, total } = res.data
                this.totalData = { failedJobs, failedRatio, successfulJobs, successfulRatio, total }

                // 类型数据
                let typeList = res.data.jobTypeStatisticsBeans
                let allChartData = this.allChartData
                let searchData = this.searchData
                Object.keys(allChartData).map(item => {
                    // 包装出类型柱状图数据
                    typeList.map(k => {
                        if (k.type === item) {
                            allChartData[item].typeListData = k
                        }
                    })
                    success.push(allChartData[item].typeListData.successfulJobs)
                    failed.push(allChartData[item].typeListData.failedJobs)

                    // 按类型获取图表数据
                    Promise.all([
                        getDuration({ type: item, ...searchData }),
                        getTypeTable({ type: item, ...searchData })
                    ]).then(value => {
                        allChartData[item].durationList = value[0].data.map(item => item.count)
                        allChartData[item].typeTableList = value[1].data
                    })
                })
                this.$set(this.chartData, 'success', success)
                this.$set(this.chartData, 'failed', failed)
                this.changeProgressType('intersect')

                this.$refs['progress'].selectedProgressBarIndex = 0
            })
        },
        // 机构下拉接口
        async getinsSelectList() {
            let res = await institutionsListDropdown()
            this.institutionsSelectList = res.data.institutionsSet.map(item => {
                return {
                    value: item,
                    label: item
                }
            })
        },
        // 站点枚举
        async getSiteSelectList() {
            this.$set(this.searchData, 'site', '')
            let res = await getSiteAll({ institutions: this.searchData.institutions })
            this.sitesSelectList = res.data.map(item => {
                return {
                    value: item,
                    label: item
                }
            })
        },
        changeInstitution() {
            this.getSiteSelectList()
            this.getProgressData()
        },
        changeSite() {
            this.getProgressData()
        },
        changeProgressType(name) {
            this.selectData = this.allChartData[name]
            loading.style.display = 'block'
            setTimeout(() => {
                try {
                    this.setDayChartData(this.selectData.typeTableList)
                    this.setFailedData(this.selectData.typeTableList)
                    this.setDurData(this.selectData.durationList)
                    loading.style.display = 'none'
                } catch {
                    loading.style.display = 'none'
                }
            }, 300)
        },
        setDayChartData(data) {
            this.dayListData = []
            this.dayListData.list = data && data.map(item => {
                // this.dayListData.total = data[item].total
                return {
                    time: moment(item.date).format('YYYY-MM-DD'),
                    max: item.maxDuration,
                    min: item.minDuration,
                    mean: item.avgDuration,
                    failed: item.failedCount,
                    failedRatio: item.failedRatio,
                    success: item.successCount,
                    successRatio: item.successRatio,
                    ...item
                }
            })
        },
        setFailedData(data) {
            let chartdata = []
            let day = []
            data && data.map(item => {
                chartdata.push(item.failedCount)
                day.push(moment(item.date).format('YYYY-MM-DD'))
            })
            this.$set(this.failedChartData, 'data', chartdata)
            this.$set(this.failedChartData, 'day', day)
        },
        setDurData(data) {
            this.durChartData = []
            data && (this.durChartData = data)
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/jobmonitor.scss';
</style>
