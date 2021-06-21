<template>
    <div >
        <div class="type-time" v-if="radio==='Today’s active data'">
            <span>
                {{$t('Active sites today')}}:
            </span>
            <span style="color:#217AD9">{{total.activeData}}</span>
        </div>
        <div class="time-picker" v-else>
            <span class="date">{{$t('Date')}}</span>
            <el-date-picker
                v-model="timevalue"
                @change="handeldeDate"
                type="daterange"
                :clearable="false"
                range-separator="~"
                :start-placeholder="$t('start')"
                :end-placeholder="$t('end')"
                format="yyyy-MM-dd"
                value-format="yyyyMMdd"
                :picker-options="pickerOptions">
            </el-date-picker>
        </div>
        <div class="content-item">
            <div class="institutions-jobs">
                <div class="job-alone-complete">
                    <div class="job-alone" >
                        <span>{{$t('Total jobs')}}：</span><span class="span">{{total.total}}</span>
                        <span >{{$t('Waiting')}}：</span> <span class="span">{{total.waiting}} ({{total.waiting_percent | toGetFixed}}%)</span>
                        <span >{{$t('Running')}}：</span> <span class="span">{{total.running}} ({{total.running_percent | toGetFixed}}%)  </span>
                        <span>{{$t('Success')}}：</span> <span class="span">{{total.success}} ({{total.success_percent | toGetFixed}}%)   </span>
                        <span>{{$t('Failed')}}：</span> <span class="span">{{total.failed}} ({{total.failed_percent | toGetFixed}}%)</span>
                    </div>
                    <div  class="complete-failed">
                        <span class="waiting"></span>
                        <span >{{$t('Waiting')}}</span>
                        <span class="running"></span>
                        <span>{{$t('Running')}}</span>
                        <span class="success"></span>
                        <span>{{$t('Success')}}</span>
                        <span class="failed"></span>
                        <span >{{$t('Failed')}}</span>

                    </div>
                </div>
                <div class="jobs">
                    <div v-if="totalList.length>0" class="jobs-box">
                        <span v-for="(item, index) in totalList" :key="index">
                            <div class="jobs-institution" ref='jobs' >
                                <!-- <overflowtooltip class="jobs-text" :width="'80px'" :content="item.siteName" :placement="'top'"/> -->
                                <overflowtooltip class="jobs-text" :width="'60px'" :content="`${item.partyId}`" :placement="'top'"/>
                                <el-tooltip placement="top">
                                    <div slot="content">
                                        <div>{{$t('Waiting')}}:
                                            {{(item.waiting)}}
                                            ({{item.waiting_percent | toGetFixed}}%)
                                        </div>
                                        <div>{{$t('Running')}}:
                                            {{(item.running)}}
                                            ({{item.running_percent | toGetFixed}}%)
                                        </div>
                                        <div>{{$t('Success')}}:
                                            {{(item.success)}}
                                            ({{item.success_percent | toGetFixed}}%)
                                        </div>
                                        <div>{{$t('Failed')}}:
                                            {{(item.failed)}}
                                            ({{item.failed_percent | toGetFixed}}%)
                                        </div>

                                    </div>
                                    <span style="height:24px;">
                                        <div class="jobs-bar" :style="`width:${item.total*jobsitemWidth/maxtoal}px`">
                                            <div class="jobs-bar-waiting" :style="`width:${item.waiting / item.total*100}%`"></div>
                                            <div class="jobs-bar-running" :style="`width:${item.running /item.total*100}%`"> </div>
                                            <div class="jobs-bar-success" :style="`width:${item.success /item.total*100}%`"> </div>
                                            <div class="jobs-bar-faile" :style="`width:${item.failed / item.total*100}%`"></div>
                                        </div>
                                    </span>

                                </el-tooltip>
                                <span class="text" ref='text'>{{item.total}}</span>
                            </div>
                        </span>
                    </div>
                    <div v-else class="jobs-box no-date-box">
                        {{$t('No Data')}}
                    </div>
                    <!-- <div class="institutions-pagination">
                        <el-pagination
                            small
                            background
                            @current-change="handleCurrentChange"
                            layout="prev, pager, next"
                            :page-size="10"
                            :total="40">
                        </el-pagination>
                    </div> -->
                </div>
            </div>
        </div>
        <div class="content-item">
            <div class="institution-based">
                <div class="statistics">{{$t('Institution based statistics')}}</div>
                <div class="between">
                    {{$t('Statistics of cooperation between my institution and other institutions')}}

                </div>
                <div class="institution-table">
                    <el-table
                        header-row-class-name="tableHead"
                        header-cell-class-name="tableHeadCell"
                        cell-class-name="tableCell"
                        :data="institutionList"
                        :header-cell-style="{background:'#FAFBFC'}"
                        :cell-style="{background:'#FAFBFC'}"

                        border
                        height="100%">
                        <el-table-column prop="name" fixed label="" align="center" :resizable="false">
                            <template slot-scope="scope">
                                <span style="color:#4E5766;font-weight:bold">{{scope.row.institution}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="total" fixed :label="$t('Jobs')" align="center" :resizable="false">
                        </el-table-column>
                        <el-table-column prop="waiting" :label="$t('Waiting')"  align="center" :resizable="false">
                            <template slot-scope="scope">
                                <span >{{scope.row.waiting}}  ({{scope.row.waiting_percent | toGetFixed}}%)</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="running" :label="$t('Running')"  align="center" :resizable="false">
                            <template slot-scope="scope">
                                <span >{{scope.row.running}}  ({{scope.row.running_percent | toGetFixed}}%)</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="success" :label="$t('Success')"  align="center" :resizable="false">
                            <template slot-scope="scope">
                                <span >{{scope.row.success}}  ({{scope.row.success_percent | toGetFixed}}%)</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="failed" :label="$t('Failed')"  align="center" :resizable="false">
                            <template slot-scope="scope">
                                <span >{{scope.row.failed}}  ({{scope.row.failed_percent | toGetFixed}}%)</span>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="paginationInstitutionSize">
                        <el-pagination
                            background
                            @current-change="handleInstitutionCurrentChange"
                            :current-page.sync="currentInstitutionPage"
                            :page-size="pageInstitutionSize"
                            layout="total, prev, pager, next, jumper"
                            :total="totalInstitution">
                        </el-pagination>
                    </div>
                </div>
            </div>
        </div>
        <div class="content-item">
            <div class="institution-based">
                <div class="statistics">
                    {{$t('Site based statistics')}}
                </div>
                <div class="between">
                    {{$t('Statistics of cooperation between my site and other institutions sites')}}
                </div>

                <div class="institution-table">
                    <el-table
                        header-row-class-name="tableHead"
                        header-cell-class-name="tableHeadCell"
                        cell-class-name="tableCell"
                        :data="siteTableList"
                        :span-method="objectSpanMethod"
                        :header-cell-style="{background:'#FAFBFC'}"
                        :cell-style="{background:'#FAFBFC'}"
                        border
                        height="100%">
                       <el-table-column prop="institution" label="" align="center" :resizable="false">
                            <template slot-scope="scope">
                                <span style="color:#4E5766;font-weight:bold">{{scope.row.institution}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="institutionSiteName"  align="center"  label="site" :resizable="false">
                            <template slot-scope="scope">
                                <span >{{scope.row.institutionSiteName}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column v-for="(item, index) in siteNameList" :key="index" align="center"  :prop="item" :label="item" :resizable="false">
                            <template slot-scope="scope">
                                <el-tooltip v-if='scope.row[item]' placement="top">
                                    <div slot="content">
                                        <div>
                                            <span style="margin-right:5px;">{{$t('Waiting')}}：{{scope.row[item].waiting}}  </span>
                                            <span>({{scope.row[item].waiting_percent | toGetFixed}}%)</span>
                                        </div>
                                        <div>
                                            <span style="margin-right:5px;">{{$t('Running')}}：{{scope.row[item].running}}  </span>
                                            <span >({{scope.row[item].running_percent | toGetFixed}}%)</span>
                                        </div>
                                        <div>
                                            <span style="margin-right:5px;">{{$t('Success')}}：{{scope.row[item].success}}  </span>
                                            <span >({{scope.row[item].success_percent | toGetFixed}}%)</span>
                                        </div>
                                        <div>
                                            <span style="margin-right:5px;">{{$t('Failed')}}：{{scope.row[item].failed}}  </span>
                                            <span>({{scope.row[item].failed_percent | toGetFixed}}%)</span>
                                        </div>

                                    </div>
                                    <span >{{scope.row[item].total}}</span>
                                </el-tooltip>
                                <span v-else>
                                    <el-tooltip  placement="top">
                                        <div slot="content">
                                            <div>
                                                <span style="margin-right:5px;">{{$t('m.monitor.waiting',{type:'w'})}}：0  </span>
                                                <span>(0%)</span>
                                            </div>
                                            <div>
                                                <span style="margin-right:5px;">{{$t('m.monitor.running',{type:'r'})}}：0  </span>
                                                <span >(0%)</span>
                                            </div>
                                            <div>
                                                <span style="margin-right:5px;">{{$t('m.common.success')}}：0  </span>
                                                <span >(0%)</span>
                                            </div>
                                            <div>
                                                <span style="margin-right:5px;">{{$t('m.common.failed')}}：0  </span>
                                                <span>(0%)</span>
                                            </div>
                                        </div>
                                        <span >0</span>
                                    </el-tooltip>
                                </span>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="paginationInstitutionSize">
                        <el-pagination
                            background
                            @current-change="handleSiteCurrentChange"
                            :current-page.sync="currentSitePage"
                            :page-size="pageInstitutionSize"
                            layout="total, prev, pager, next, jumper"
                            :total="totalSitetitution">
                        </el-pagination>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import overflowtooltip from '@/components/Tooltip'
import { getTotal, getInstitution, getSite } from '@/api/monitor'
import moment from 'moment'
// 国际化
const local = {
    zh: {
        'Today’s active data': '今日活跃数据',
        'Cumulative active data': '累加活跃数据',
        'Date': '日期',
        'Active sites today': '今日活跃站点数',
        'start': '开始',
        'end': 'end',
        'Total jobs': '总任务数',
        'Waiting': '等待中',
        'Running': '运行中',
        'Success': '成功',
        'Failed': '失败',
        'No Data': '暂无数据',
        'Institution based statistics': '基于机构数据统计',
        'Statistics of cooperation between my institution and other institutions': '各机构间的建模任务统计',
        'Jobs': '任务数',
        'Site based statistics': '基于站点数据统计',
        'Statistics of cooperation between my site and other institutions sites': '各站点间的建模任务统计'
    },
    en: {
        'Today’s active data': 'Today’s active data',
        'Cumulative active data': 'Cumulative active data',
        'Date': 'Date',
        'Active sites today': 'Active sites today',
        'start': 'start',
        'end': 'end',
        'Total jobs': 'Total jobs',
        'Waiting': 'Waiting',
        'Running': 'Running',
        'Success': 'Success',
        'Failed': 'Failed',
        'No Data': 'No Data',
        'Institution based statistics': 'Institution based statistics',
        'Statistics of cooperation between my institution and other institutions': 'Statistics of cooperation between my institution and other institutions',
        'Jobs': 'Jobs',
        'Site based statistics': 'Site based statistics',
        'Statistics of cooperation between my site and other institutions sites': 'Statistics of cooperation between my site and other institutions sites'
    }
}

export default {
    name: 'cooperation',
    components: {
        overflowtooltip
    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        },
        datevaule(vaule) {
            return moment(vaule).format('YYYYMMDD')
        },
        toGetFixed(val) {
            let vaule = val * 100
            return vaule ? vaule.toFixed(1) : '0.0'
        }
    },
    data() {
        return {
            timevalue: [moment(new Date()).format('YYYYMMDD'), moment(new Date()).format('YYYYMMDD')],
            radio: 'Today’s active data',
            maxtoal: 10,
            totalList: [],
            total: { activeData: 0 },
            itemWidth: '',
            stylebar: '',
            jobsitemWidth: '',
            institutionList: [],
            siteTableList: [],
            siteNameList: [],
            sitedata: [],
            lengthList: [],
            pageInstitutionSize: 10, // 机构维度一页共有几行
            totalInstitution: 0, // 机构维度总行
            totalSitetitution: 0, // 站点维度总行,
            currentInstitutionPage: 1, // 机构维度当前页
            currentSitePage: 1, // 站点维度当前页
            firstInstPageNum: 1, // 机构维度第一页
            secondSitePageNum: 1, // 机构维度第一页
            sortArr: {},
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now()
                }
            }

        }
    },
    watch: {
        totalList: {
            handler: function(val) {
                if (val.length > 0) {
                    this.$nextTick(() => {
                        this.jobsitemWidth = this.$refs.jobs[0].clientWidth - 180
                    })
                }
            },
            immediate: true
        },
        radio: {
            handler(val) {
                if (val === 'Today’s active data') {
                    this.timevalue = [moment(new Date()).format('YYYYMMDD'), moment(new Date()).format('YYYYMMDD')]
                } else {
                    this.timevalue = [moment(new Date() - 30 * 24 * 60 * 60 * 1000).format('YYYYMMDD'), moment(new Date()).format('YYYYMMDD')]
                }
                setTimeout(() => {
                    this.initi()
                }, 300)
            }
        }
    },
    computed: {
        ...mapGetters(['organization'])

    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        this.initi()
        // let res = {
        //     data: {
        //         totalJobs: 0,
        //         waitingJobs: 0,
        //         runningJobs: 0,
        //         successJobs: 0,
        //         failedJobs: 0,
        //         data: [
        //             {
        //                 waitingJobs: 0,
        //                 waitingPercent: 0,
        //                 runningJobs: 0,
        //                 runningPercent: 0,
        //                 successJobs: 0,
        //                 successPercent: 0,
        //                 failedJobs: 0,
        //                 failedPercent: 0,
        //                 totalJobs: 0
        //             }
        //         ]
        //     }
        // }
    },
    mounted() {

    },
    methods: {
        initi() {
            this.toGetTotal()
            this.toGetInstitution()
            this.toGetSite()
            // this.toGetSite2()
        },
        handleCurrentChange(val) {
            console.log('val==>>', val)
        },
        // 改变时间
        handeldeDate(val) {
            this.initi()
        },
        // 空格部分
        objectSpanMethod({ row, column, rowIndex, columnIndex }) {
            if (columnIndex === 0) {
                let row = this.lengthList[rowIndex]
                let col = row > 0 ? 1 : 0
                return {
                    rowspan: row,
                    colspan: col
                }
            }
        },
        // 获取总共
        toGetTotal() {
            this.totalList = []
            this.total = {}
            let data = {
                startDate: this.timevalue[0],
                endDate: this.timevalue[1]
            }
            getTotal(data).then(res => {
                let listData = res.data || []
                this.total = listData ? listData['all'].data : { activeData: 0 }
                delete listData['all']
                this.totalList = Object.keys(listData).map(item => {
                    let items = listData[item].data
                    items.siteName = listData[item].site_name
                    items.partyId = item
                    return items
                })
                let maxlist = []
                this.totalList.forEach(element => {
                    maxlist.push(element.total)
                })
                this.maxtoal = Math.max(...maxlist)
            })
        },
        // 获取Institution表格(第一个表格)
        toGetInstitution() {
            this.institutionList = []
            let data = {
                startDate: this.timevalue[0],
                endDate: this.timevalue[1],
                pageNum: this.firstInstPageNum,
                pageSize: 10
            }
            getInstitution(data).then(res => {
                let listData = res.data || []
                this.institutionList = Object.keys(listData).map(item => {
                    let items = listData[item].data
                    items.institution = item
                    return items
                })
                this.totalInstitution = (res.data && res.data.total) || 0
            })
        },
        // 获取site表格(第二个表格)
        toGetSite() {
            this.siteTableList = []
            let data = {
                startDate: this.timevalue[0],
                endDate: this.timevalue[1],
                pageNum: this.secondSitePageNum,
                pageSize: 10
            }
            getSite(data).then(res => {
                let tableData = res.data || []
                this.siteTableList = []
                // mock
                // tableData = JSON.parse(localStorage.getItem('siteData'))
                console.log(tableData, 'tableData')

                this.siteNameList = Object.values(tableData).map(item => item.site_name) || []
                console.log(this.siteNameList, 'siteNameList')

                this.totalSitetitution = (tableData.total) || 0
                let arr = []
                // 数据转成数组
                Object.values(tableData).map(item => {
                    Object.keys(item.institutions).map(key => {
                        Object.values(item.institutions[key]).map(val => {
                            arr.push({
                                institution: key,
                                institutionSiteName: val.site_name,
                                [item.site_name]: { ...val.data }
                            })
                        })
                    })
                })
                // 按站点维度排序
                let siteSortArr = []
                this.sortBySame(arr, 'institutionSiteName')
                // 按站点名合并
                for (let k = arr.length - 1; k >= 0; k--) {
                    for (let j = k - 1; j >= 0; j--) {
                        if (arr[k].institutionSiteName === arr[j].institutionSiteName) {
                            siteSortArr.push({
                                ...arr[k],
                                [Object.keys(arr[j])[2]]: Object.values(arr[j])[2]
                            })
                        }
                    }
                }
                siteSortArr.map(item => {
                    arr.map((val, index) => {
                        if (item.institutionSiteName === val.institutionSiteName) {
                            delete arr[index]
                        }
                    })
                })
                arr = arr.filter(el => el != null).concat(siteSortArr)
                // 按机构维度排序
                this.sortBySame(arr, 'institution')
                // 获取机构数组
                let insNameArr = []
                arr.map(item => !insNameArr.includes(item.institution) && insNameArr.push(item.institution))
                this.lengthList = []
                insNameArr.map((item, index) => {
                    let len = this.getLength(arr, item)
                    this.lengthList.push(len)
                    for (let i = 1; i < len; i++) {
                        this.lengthList.push(0)
                    }
                })

                console.log(arr, 'arr-after')
                this.siteTableList = arr
            })
        },
        getLength(arr, name) {
            let lengthArr = []
            arr.map(item => {
                if (item.institution === name) {
                    lengthArr.push(item)
                }
            })
            return lengthArr.length
        },
        sortBySame(arr, name) {
            return arr.sort((a, b) => {
                var x = a[name].toLowerCase()
                var y = b[name].toLowerCase()
                if (x < y) { return -1 }
                if (x > y) { return 1 }
                return 0
            })
        },
        // 机构维度(第一个表格翻页)翻页
        handleInstitutionCurrentChange(val) {
            this.firstInstPageNum = val
            this.toGetInstitution()
        },
        // 站点维度(第二个表格翻页)翻页
        handleSiteCurrentChange(val) {
            this.secondSitePageNum = val
            this.toGetSite()
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/cooperation.scss';
    .time-picker{

        .el-input__inner {
            width:320px;
            height: 35px;
            line-height: 35px;

            .el-range__icon {
                line-height: 30px;
            }

            .el-range__close-icon{
                width: 0;
            }
        }
    }
    .institution-table{
        .el-table{
            border: 1px solid #e6e6e6;
            .tableHeadCell:not(:nth-of-type(1)):not(:nth-of-type(2)){
                font-weight: normal;
            }
        }
    }
</style>
