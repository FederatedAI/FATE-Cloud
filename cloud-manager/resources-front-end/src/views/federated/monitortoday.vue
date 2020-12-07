<template>
 <div>
    <div v-if="type==='Today’s active institution'" class="type-time">
        <span>Active institutions today: </span>
        <span style="color:#217AD9">{{activejobs.active}}</span>
    </div>
    <div v-else class="time-picker">
        <span class="date">Date</span>
        <el-date-picker
            v-model="timevalue"
            type="daterange"
            range-separator="~"
            start-placeholder="start"
            end-placeholder="end"
            :picker-options="pickerOptions">
        </el-date-picker>
    </div>
    <div class="content-item">
        <div class="jobs-complete">
            <span>Federated modeling jobs：</span> <span class="span">{{activejobs.total}}</span>
            <span>running：</span> <span class="span">{{activejobs.running}} ({{activejobs.running/activejobs.total | toGetFixed}}%)   </span>
            <span>success：</span> <span class="span">{{activejobs.success}} ({{activejobs.success/activejobs.total | toGetFixed}}%)   </span>
            <span>failed：</span> <span class="span">{{activejobs.failed}} ({{activejobs.failed/activejobs.total | toGetFixed}}%)</span>
        </div>
        <div class="institutions-jobs">
            <div class="institutions" >
                <div v-if='institutionsList.length>0' class="item-box">
                    <span v-for="(item, index) in institutionsList" :key="index">
                        <div :class="{'item-institution':true,'institution-activa':item.show}" @click="getsite(item,index)">
                            <overflowtooltip class="item-text" :width="'100px'" :content="item.institutions" :placement="'top'"/>
                            <span class="bar" ref='bar' :style='stylebar'>
                                <div class="item-bar" :style="`width:${(item.total)*institutionsitemWidth/item.max}px`"> </div>
                                <span class="text" ref="text">{{item.total}}</span>
                            </span>
                            <i v-if="item.show" class="el-icon-caret-right icon-right"></i>
                        </div>
                    </span>
                </div>
                <div v-else class="item-box no-date-box">
                    No Date
                </div>
                <div  class="institutions-pagination">
                    <el-pagination
                        small
                        background
                        @current-change="handleCurrentChangeInst"
                        layout="total, prev, pager, next"
                        :page-size="pageSizeInst"
                        :total="instTotal">
                    </el-pagination>
                </div>
            </div>
            <div class="jobs">
                <div v-if="type==='Today’s active institution'" class="jobs-site-title">Active sites today:{{activeSite.active}}</div>
                <div class="job-alone-complete">
                    <span>Federated modeling jobs：</span> <span class="span">{{activeSite.total}}</span>
                    <span >running：</span> <span class="span">{{activeSite.running}} ({{activeSite.running/activeSite.total | toGetFixed}}%)</span>
                    <span>success：</span> <span class="span">{{activeSite.success}}  ({{activeSite.success/activeSite.total | toGetFixed}}%)</span>
                    <span>failed：</span> <span class="span">{{activeSite.failed}}  ({{activeSite.failed/activeSite.total | toGetFixed}}%)</span>
                </div>
                <div class="jobs-title">
                    <span class="running"></span>
                    <span>running</span>
                    <span class="complete"></span>
                    <span>success</span>
                    <span class="failed"></span>
                    <span >failed</span>
                </div>
                <div v-if='siteList.length>0' class="jobs-box" >
                    <span v-for="(item, index) in siteList" :key="index">
                        <div class="jobs-institution" ref='jobs' >
                            <overflowtooltip class="jobs-text" :width="'50px'" :content="item.site" :placement="'top'"/>
                            <overflowtooltip class="jobs-text" :width="'50px'" :content="item.partyId" :placement="'top'"/>
                            <el-tooltip placement="top">
                                <div slot="content">
                                    <div>running:
                                        {{(item.running )}}
                                        ({{item.running/item.total | toGetFixed}}%)
                                    </div>
                                    <div>success:
                                        {{(item.success)}}
                                        ({{item.success/item.total | toGetFixed}}%)
                                    </div>
                                    <div>failed:
                                        {{(item.failed)}}
                                        ({{item.failed/item.total | toGetFixed}}%)
                                    </div>
                                </div>
                                <div class="jobs-bar" :style="`width:${item.total*sitemWidth/item.max}px`">
                                    <div class="jobs-bar-complete" :style="`width:${(item.success + item.failed)/item.total*100}%`">
                                        <div class="jobs-bar-running" :style="`width:${(item.failed/(item.success + item.failed))*100}%`"></div>
                                    </div>
                                </div>
                            </el-tooltip>
                            <span class="text" >{{item.total}}</span>
                        </div>
                    </span>
                </div>
                <div v-else class="jobs-box no-date-box">
                    No Date
                </div>
                <div  class="institutions-pagination">
                    <!-- <el-pagination
                        small
                        background
                        @current-change="handleCurrentChange"
                        layout="prev, pager, next"
                        :page-size="80"
                        :total="siteTotal">
                    </el-pagination> -->
                </div>
            </div>
        </div>
    </div>
    <!-- 机构维度 -->
    <div class="content-item">
        <div class="institution-based">
            <div class="statistics">Institution based statistics</div>
            <div class="cooperation">
                Statistics of cooperation between institutions
            </div>
            <div class="select">
                <span class="select-text">Institution</span>
                <el-select v-model="institutionStat" @change="toGetIntSate" placeholder="请选择">
                    <el-option
                    v-for="item in institutionsdownList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div class="institution-table">
                <el-table
                    :data="tableIntSateData"
                    header-row-class-name="tableHead"
                    header-cell-class-name="tableHeadCell"
                    cell-class-name="tableCell"
                    :header-cell-style="{background:'#FAFBFC'}"
                    :cell-style="{background:'#FAFBFC'}"
                    border
                    height="250">
                    <el-table-column prop="institutions" fixed align="center" label="" >
                        <template slot-scope="scope">
                            <span style="color:#4E5766;font-weight:bold">{{scope.row.institutions}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="name" label="Jobs" >
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.total}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Running">
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.failed}} ({{scope.row.failed/scope.row.total | toGetFixed}}%)</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Success">
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.success}} ({{scope.row.success/scope.row.total | toGetFixed}}%)</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Failed">
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.failed}} ({{scope.row.failed/scope.row.total | toGetFixed}}%)</span>
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
    <!-- 站点维度 -->
    <div class="content-item">
        <div class="institution-based">
            <div class="statistics">Site based statistics</div>
            <div class="cooperation">
                Statistics of cooperation between institutions
            </div>
            <div class="select">
                <span class="select-text">Institution</span>
                <el-select v-model="institutionSite" @change="toGetIntSite" placeholder="请选择">
                    <el-option
                    v-for="item in institutionsdownList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div class="institution-table">
                <el-table
                    header-row-class-name="tableHead"
                    header-cell-class-name="tableHeadCell"
                    cell-class-name="tableCell"
                    :data="tableIntSiteData"
                    :span-method="objectSpanMethod"
                    :header-cell-style="{background:'#FAFBFC'}"
                    :cell-style="{background:'#FAFBFC'}"
                    border
                    height="250">
                    <el-table-column prop="" align="center" fixed label="" >
                        <template slot-scope="scope">
                            <span style="color:#4E5766;font-weight:bold">{{scope.row.institutionsName}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="siteName" align="center" fixed label="site" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column v-for="(item, index) in tableIntLabeList" :key="index" align="center" :prop="item"  :label="item">
                        <template slot-scope="scope">
                            <span v-if="scope.row[item]==='none'" style="opacity:0">{{scope.row[item]}}</span>
                            <el-tooltip v-else placement="top">
                                <div slot="content">
                                    <div>
                                        <span style="margin-right:5px;">running：{{scope.row[item].running}}  </span>
                                        <span >({{scope.row[item].running/scope.row[item].total | toGetFixed}}%)</span>
                                    </div>
                                    <div>
                                        <span style="margin-right:5px;">success：{{scope.row[item].success}}  </span>
                                        <span >({{scope.row[item].success/scope.row[item].total | toGetFixed}}%)</span>
                                    </div>
                                    <div>
                                        <span style="margin-right:5px;">failed：{{scope.row[item].failed}}  </span>
                                        <span>({{scope.row[item].failed/scope.row[item].total | toGetFixed}}%)</span>
                                    </div>
                                </div>
                                 <span >{{scope.row[item].total}}</span>
                            </el-tooltip>
                        </template>
                        <!-- <template slot="header" slot-scope="scope">
                            <span>{{item}}</span>
                        </template> -->
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
import overflowtooltip from '@/components/Tooltip'
import {
    institutionsListToday,
    siteListToday,
    institutionsSataToday,
    siteSataToday,
    institutionsAllToday,
    siteAllToday,
    institutionsListPeriod,
    siteListPeriod,
    institutionsSataPeriod,
    siteSataPeriod,
    institutionsAllPeriod,
    siteAllPeriod } from '@/api/monitor'

import { addInstitutionsList } from '@/api/federated'

export default {
    name: 'monitor',
    components: {
        overflowtooltip
    },
    filters: {
        toGetFixed(val) {
            let value = val * 100
            return value ? value.toFixed(1) : 0
        }
    },
    data() {
        return {
            type: 'Today’s active institution',
            // dateToday: new Date(),
            dateToday: 1603987212345,
            timevalue: [new Date() - 24 * 60 * 60 * 1000, new Date().getTime()],
            instTotal: 0,
            pageSizeInst: 80,
            instpageNum: 1,
            siteTotal: 0,
            activejobs: {},
            activeSite: {},
            institutionsList: [],
            siteList: [],
            institutionsdownList: [],
            tableIntSateData: [],
            tableIntSiteData: [],
            tableIntLabeList: [],
            institutionsitemWidth: '',
            stylebar: '',
            sitemWidth: '',
            institutionStat: '', // 机构维度选择
            institutionSite: '', // 站点维度选择
            value: '',
            lengthList: [],
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now()
                }
            },
            currentInstitutionPage: 1, // 机构维度当前页
            currentSitePage: 1, // 站点维度当前页
            pageInstitutionSize: 10, // 机构维度第几页
            totalInstitution: 0, // 机构维度页
            totalSitetitution: 0, // 站点维度页,
            firstPageNum: 1, // 机构维度当前页
            secondPageNum: 1, // 站点维度当前页
            firstTempVal: '', // 机构维度当临时数据
            secondTempVal: '' // 站点维度当临时数据

        }
    },
    watch: {
        institutionsList: {
            handler(val) {
                if (val.length > 0) {
                    this.$nextTick(() => {
                        this.calcinstitution().then(res => {
                            this.institutionsitemWidth = this.$refs.bar[0].clientWidth - res - 10
                        })
                    })
                }
            },
            immediate: true,
            deep: true
        },
        siteList: {
            handler(val) {
                if (val.length > 0) {
                    this.$nextTick(() => {
                        this.sitemWidth = this.$refs.jobs[0].clientWidth - 125
                    })
                }
            }
        }
    },
    created() {

    },

    mounted() {
        this.initi()
    },
    methods: {
        initi() {
            this.getInstitutionsListToday()
            this.getinstitutionsdownList()
            // 机构和站点
            this.institutionsAll()
        },
        institutionsAll() {
            if (this.type === 'Today’s active institution') {
                let data = {
                    dateToday: this.dateToday,
                    pageNum: this.instpageNum,
                    pageSize: 80
                }
                institutionsAllToday(data).then(res => {
                    getData(res)
                })
            } else {
                let data = {
                    beginDate: this.timevalue[0],
                    endDate: this.timevalue[1],
                    pageNum: this.instpageNum,
                    pageSize: 80
                }
                institutionsAllPeriod(data).then(res => {
                    getData(res)
                })
            }
            let that = this
            let getData = function (res) {
                let datares = res.data
                let obj = {}
                obj.active = datares.institutionsCount
                obj.running = datares.runningJobCount
                obj.success = datares.successJobCount
                obj.failed = datares.failedJobCount
                obj.total = datares.failedJobCount + datares.successJobCount + datares.runningJobCount
                that.activejobs = { ...obj }
            }
        },
        // get
        getinstitutionsdownList() {
            this.institutionsdownList = []
            addInstitutionsList().then(res => {
                res.data.forEach((item, index) => {
                    let obj = {}
                    obj.value = item
                    obj.label = item
                    this.institutionsdownList.push(obj)
                })
                this.institutionSite = this.institutionStat = this.institutionsdownList[0].value
                this.toGetIntSate(this.institutionStat)
                this.toGetIntSite(this.institutionSite, 'type')
            })
        },
        // 计算宽度
        async calcinstitution() {
            let arr = this.$refs.text.map(item => {
                return item.clientWidth
            })
            let textWidth = await Math.max(...arr)
            this.stylebar = `width:calc(100% - ${textWidth}px - 110px)`
            return textWidth
        },
        // 机构翻页
        handleCurrentChangeInst(val) {
            this.instpageNum = val
            this.institutionsAll()
        },
        // 机构维度翻页
        handleInstitutionCurrentChange(val) {
            this.firstPageNum = val
            this.toGetIntSate(this.firstTempVal)
        },
        // 站点维度翻页
        handleSiteCurrentChange(val) {
            this.secondPageNum = val
            this.toGetIntSite(this.secondTempVal, 'type')
        },
        // 获取机构维度
        getInstitutionsListToday() {
            if (this.type === 'Today’s active institution') {
                let data = {
                    dateToday: this.dateToday,
                    pageNum: 1,
                    pageSize: 80
                }
                institutionsListToday(data).then(res => {
                    getData(res)
                })
            } else {
                let data = {
                    beginDate: this.timevalue[0],
                    endDate: this.timevalue[1],
                    pageNum: 1,
                    pageSize: 80
                }
                institutionsListPeriod(data).then(res => {
                    getData(res)
                })
            }

            let that = this
            let getData = function (res) {
                that.instTotal = res.data.totalRecord
                let maxlist = []
                res.data.list.forEach(element => {
                    maxlist.push(element.runningJobCount + element.failedJobCount + element.successJobCount)
                })
                let max = Math.max(...maxlist)
                that.institutionsList = res.data.list.map(item => {
                    let obj = {}
                    obj.institutions = item.institutions
                    obj.running = item.runningJobCount
                    obj.success = item.successJobCount
                    obj.failed = item.failedJobCount
                    obj.total = item.failedJobCount + item.successJobCount + item.runningJobCount
                    obj.max = max
                    obj.show = false
                    return obj
                })
                that.getsite(that.institutionsList[0], 0)
            }
        },
        // 获取site
        getsite(row, ind) {
            let tempArr = []
            this.institutionsList.forEach((item, index) => {
                if (index === ind) {
                    item.show = true
                } else {
                    item.show = false
                }
                tempArr.push(item)
            })
            this.institutionsList = [...tempArr]
            if (this.type === 'Today’s active institution') {
                let data = {
                    dateToday: this.dateToday,
                    institutions: row ? row.institutions : ''
                }
                siteListToday(data).then(res => {
                    getData(res)
                })
            } else {
                let data = {
                    beginDate: this.timevalue[0],
                    endDate: this.timevalue[1],
                    institutions: row ? row.institutions : ''
                }
                siteListPeriod(data).then(res => {
                    getData(res)
                })
            }

            let that = this
            let getData = function (res) {
                that.siteTotal = res.data.totalRecord
                that.getsiteAllToday(row ? row.institutions : '')
                let maxlist = []
                res.data.list.forEach(element => {
                    maxlist.push(element.failedJobCount + element.successJobCount + element.runningJobCount)
                })
                let max = Math.max(...maxlist)
                that.siteList = res.data.list.map(item => {
                    let obj = {}
                    obj.partyId = item.partyId
                    obj.site = item.site
                    obj.running = item.runningJobCount
                    obj.success = item.successJobCount
                    obj.failed = item.failedJobCount
                    obj.total = item.failedJobCount + item.successJobCount + item.runningJobCount
                    obj.max = max
                    return obj
                })
            }
        },
        // 获取站点site总数
        getsiteAllToday(val) {
            if (this.type === 'Today’s active institution') {
                let data = {
                    dateToday: this.dateToday,
                    institutions: val,
                    pageNum: 1,
                    pageSize: 100
                }
                siteAllToday(data).then(res => {
                    getData(res)
                })
            } else {
                let data = {
                    beginDate: this.timevalue[0],
                    endDate: this.timevalue[1],
                    institutions: val,
                    pageNum: 1,
                    pageSize: 100
                }
                siteAllPeriod(data).then(res => {
                    getData(res)
                })
            }
            let that = this
            let getData = function (res) {
                let datares = res.data
                let obj = {}
                obj.active = datares.siteCount
                obj.running = datares.runningJobCount
                obj.success = datares.successJobCount
                obj.failed = datares.failedJobCount
                obj.total = datares.failedJobCount + datares.successJobCount + datares.runningJobCount
                that.activeSite = { ...obj }
            }
        },
        // 表格空格部分
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
        // 第一个表格下拉选择
        toGetIntSate(val) {
            this.firstTempVal = val
            if (this.type === 'Today’s active institution') {
                let data = {
                    dateToday: this.dateToday,
                    institutions: this.firstTempVal,
                    pageNum: this.firstPageNum,
                    pageSize: 10
                }
                institutionsSataToday(data).then(res => {
                    getData(res)
                })
            } else {
                let data = {
                    beginDate: this.timevalue[0],
                    endDate: this.timevalue[1],
                    institutions: val
                }
                institutionsSataPeriod(data).then(res => {
                    getData(res)
                })
            }
            let that = this
            let getData = function (res) {
                that.totalInstitution = res.data.totalRecord
                that.tableIntSateData = res.data.list.map((item) => {
                    item.running = parseInt(item.runningJobCountForInstitutions) || 0
                    item.failed = parseInt(item.failedJobCountForInstitutions) || 0
                    item.success = parseInt(item.successJobCountForInstitutions) || 0
                    item.total = item.failed + item.success + item.running
                    return item
                })
            }
        },
        // 第二个表格下拉选择
        toGetIntSite(val, type) {
            this.secondTempVal = val
            if (this.type === 'Today’s active institution') {
                let data = {
                    dateToday: this.dateToday,
                    institutions: this.secondTempVal,
                    pageNum: this.secondPageNum,
                    pageSize: 10
                }
                siteSataToday(data).then(res => {
                    this.getsiteSataData(res, type)
                })
            } else {
                let data = {
                    beginDate: this.timevalue[0],
                    endDate: this.timevalue[1],
                    institutions: val
                }
                siteSataPeriod(data).then(res => {
                    this.getsiteSataData(res, type)
                })
            }
        },
        // 第二个下拉表格返回方法
        getsiteSataData(res, type) {
            this.lengthList = [] // 清空空格数据
            this.tableIntSiteData = [] // 表格总数据
            this.tableIntLabeList = res.data.sites // 横坐标表头
            let jobs = res.data.jobStatisticsOfSiteDimensions.list // 纵坐标表头
            this.totalSitetitution = res.data.jobStatisticsOfSiteDimensions.totalRecord
            let insList = res.data.institutionsWithSites // 纵坐标合并表头
            let ArrList = [] // 横坐标临时数据
            // 纵坐标合并表头数据
            insList.map(item => {
                this.lengthList.push(item.sites.length)
                for (let index = 0; index < item.sites.length - 1; index++) {
                    this.lengthList.push(0)
                }
                item.sites.map(ele => {
                    let obj = {}
                    obj.institutionsName = item.institutions
                    obj.siteName = ele
                    ArrList.push(obj)
                })
            })
            // 横坐标内容(site)临时数据
            let jobsArr = []
            jobs.forEach(item => {
                item.institutionsWithHostSites.forEach(e => {
                    e.jobStatisticsList.forEach(m => {
                        let obj = {}
                        obj.siteHostName = m.siteHostName
                        obj.running = parseInt(m.jobRunningCount)
                        obj.success = parseInt(m.jobSuccessCount)
                        obj.failed = parseInt(m.jobFailedCount)
                        obj.total = parseInt(m.jobSuccessCount) + parseInt(m.jobFailedCount) + parseInt(m.jobRunningCount)
                        obj.siteGuestName = item.siteGuestName
                        jobsArr.push(obj)
                    })
                })
            })
            let table = []
            ArrList.forEach(item => {
                let obj = {}
                this.tableIntLabeList.forEach(e => {
                    obj.institutionsName = item.institutionsName
                    obj.siteName = item.siteName
                    obj[e] = 'none'
                })
                table.push(obj)
            })
            table.forEach((item) => {
                jobsArr.forEach(e => {
                    if (item.siteName === e.siteHostName) {
                        item[e.siteGuestName] = { total: e.total, running: e.running, success: e.success, failed: e.failed }
                    } else {
                        item[e.siteGuestName] = 'none'
                    }
                })
            })
            if (type) {
                this.tableIntSiteData = [...table]
            } else {
                setTimeout(() => {
                    this.tableIntSiteData = [...table]
                }, 500)
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/monitor.scss';

</style>
