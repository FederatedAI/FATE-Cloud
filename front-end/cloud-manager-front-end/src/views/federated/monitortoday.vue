<template>
 <div>
    <div v-if="type==='Today’s active data'" class="type-time">
        <span>Active institutions today: </span>
        <span style="color:#217AD9">{{activejobs.active}}</span>
    </div>
    <div v-else class="time-picker">
        <span class="date">Date</span>
        <el-date-picker
            v-model="timevalue"
            @change="initi"
            :clearable="false"
            type="daterange"
            range-separator="~"
            value-format="timestamp"
            start-placeholder="start"
            end-placeholder="end"
            :picker-options="pickerOptions">
        </el-date-picker>
    </div>
    <div class="content-item">
        <div class="jobs-complete">
            <span>Federated modeling jobs：</span> <span class="span">{{activejobs.total}}</span>
            <span>waiting：</span> <span class="span">{{activejobs.waiting}} ({{(activejobs.waiting/activejobs.total) | toGetFixed}}%)   </span>
            <span>running：</span> <span class="span">{{activejobs.running}} ({{(activejobs.running/activejobs.total) | toGetFixed}}%)   </span>
            <span>success：</span> <span class="span">{{activejobs.success}} ({{(activejobs.success/activejobs.total) | toGetFixed}}%)   </span>
            <span>failed：</span> <span class="span">{{activejobs.failed}} ({{(1 -(activejobs.waiting/activejobs.total) - (activejobs.running/activejobs.total) - (activejobs.success/activejobs.total)) | toGetFixed}}%)</span>
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
                    No Data
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
                <div v-if="type==='Today’s active data'" class="jobs-site-title">Active sites today:{{activeSite.active}}</div>
                <div class="job-alone-complete">
                    <span>Federated modeling jobs：</span> <span class="span">{{activeSite.total}}</span>
                    <span >waiting：</span> <span class="span">{{activeSite.waiting}} ({{(activeSite.waiting/activeSite.total) | toGetFixed}}%)</span>
                    <span >running：</span> <span class="span">{{activeSite.running}} ({{(activeSite.running/activeSite.total) | toGetFixed}}%)</span>
                    <span>success：</span> <span class="span">{{activeSite.success}}  ({{(activeSite.success/activeSite.total) | toGetFixed}}%)</span>
                    <span>failed：</span> <span class="span">{{activeSite.failed}}  ({{(1 - (activeSite.waiting/activeSite.total) - (activeSite.running/activeSite.total) - (activeSite.success/activeSite.total)) | toGetFixed}}%)</span>
                </div>
                <div class="jobs-title">
                    <span class="waiting"></span>
                    <span>waiting</span>
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
                                    <div>waiting:
                                        {{(item.waiting )}}
                                        ({{item.waiting/item.total | toGetFixed}}%)
                                    </div>
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
                                <span style="height:24px;vertical-align: bottom">
                                    <div class="jobs-bar" :style="`width:${item.total*sitemWidth/item.max}px`">
                                        <div class="jobs-bar-waiting" :style="`width:${item.waiting/item.total*100}%`"></div>
                                        <div class="jobs-bar-running" :style="`width:${item.running/item.total*100}%`"></div>
                                        <div class="jobs-bar-success" :style="`width:${item.success/item.total*100}%`"></div>
                                        <div class="jobs-bar-failed" :style="`width:${item.failed/item.total*100}%`"></div>
                                    </div>
                                </span>
                            </el-tooltip>
                            <span class="text" >{{item.total}}</span>
                        </div>
                    </span>
                </div>
                <div v-else class="jobs-box no-date-box">
                    No Data
                </div>
                <div v-if="type!=='Today’s active data'" style="height:34px">
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
                <el-select v-model="institutionStat" @change="toGetinstitutions" placeholder="请选择">
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
                    max-height="250">
                    <el-table-column prop="institutions" fixed align="center" :resizable="false" label="" >
                        <template slot-scope="scope">
                            <span style="color:#4E5766;font-weight:bold">{{scope.row.institutions}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="name" label="Jobs" :resizable="false"  >
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.total}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Waiting" :resizable="false">
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.waiting}} ({{scope.row.waiting/scope.row.total | toGetFixed}}%)</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Running" :resizable="false">
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.running}} ({{scope.row.running/scope.row.total | toGetFixed}}%)</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Success" :resizable="false">
                        <template slot-scope="scope">
                            <span style="color:#848C99">{{scope.row.success}} ({{scope.row.success/scope.row.total | toGetFixed}}%)</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Failed" :resizable="false">
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
                Statistics of cooperation between sites
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
                    max-height="250">
                    <el-table-column prop="" align="center"  :resizable="false"  label="" >
                        <template slot-scope="scope">
                            <span style="color:#4E5766;font-weight:bold">{{scope.row.institution}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="siteName" align="center"  :resizable="false"  label="site" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <span >{{scope.row.institutionSiteName}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column v-for="(item, index) in tableIntLabeList" :key="index" align="center"  :resizable="false" :prop="item"  :label="item">
                        <template slot-scope="scope">
                            <el-tooltip v-if='scope.row[item]' placement="top">
                                <div slot="content">
                                    <div>
                                        <span style="margin-right:5px;">running：{{scope.row[item].runningJobs}}  </span>
                                        <span >({{scope.row[item].runningPercent }})</span>
                                    </div>
                                    <div>
                                        <span style="margin-right:5px;">success：{{scope.row[item].successJobs}}  </span>
                                        <span >({{scope.row[item].successPercent }})</span>
                                    </div>
                                    <div>
                                        <span style="margin-right:5px;">failed：{{scope.row[item].failedJobs}}  </span>
                                        <span>({{scope.row[item].failedPercent }})</span>
                                    </div>
                                    <div>
                                        <span style="margin-right:5px;">waiting：{{scope.row[item].waitingJobs}}  </span>
                                        <span>({{scope.row[item].waitingPercent }})</span>
                                    </div>
                                </div>
                                <span >{{scope.row[item].totalJobs}}</span>
                            </el-tooltip>
                            <span v-else>
                                 <el-tooltip  placement="top">
                                    <div slot="content">
                                        <div>
                                            <span style="margin-right:5px;">running：0  </span>
                                            <span >(0%)</span>
                                        </div>
                                        <div>
                                            <span style="margin-right:5px;">success：0  </span>
                                            <span >(0%)</span>
                                        </div>
                                        <div>
                                            <span style="margin-right:5px;">failed：0  </span>
                                            <span>(0%)</span>
                                        </div>
                                        <div>
                                            <span style="margin-right:5px;">waiting：0  </span>
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
import overflowtooltip from '@/components/Tooltip'
import moment from 'moment'
import {
    institutionsListToday,
    siteListToday,
    institutionsSataToday,
    institutionsAllToday,
    siteAllToday,
    institutionsListPeriod,
    siteListPeriod,
    institutionsSataPeriod,
    institutionsAllPeriod,
    siteAllPeriod,
    getSummarySite } from '@/api/monitor'

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
            type: 'Today’s active data',
            dateToday: new Date().getTime(),
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
                            this.institutionsitemWidth = this.$refs.bar[0].clientWidth - res - 10 // 最大值的宽度
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
                        this.sitemWidth = this.$refs.jobs[0].clientWidth - 130 // 最大值的宽度
                    })
                }
            }
        }
    },
    created() {

    },

    mounted() {
        this.getinstitutionsdownList()
        this.initi()
    },
    methods: {
        initi() {
            this.getInstitutionsListToday()
            // 机构和站点
            this.institutionsAll()
            this.toGetinstitutions(this.institutionStat)
            this.toGetIntSite(this.institutionSite, 'type')
        },
        institutionsAll() {
            if (this.type === 'Today’s active data') {
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
                obj.active = datares.institutionsCount || 0
                obj.waiting = datares.waitingJobCount || 0
                obj.running = datares.runningJobCount || 0
                obj.success = datares.successJobCount || 0
                obj.failed = datares.failedJobCount || 0
                obj.total = datares.failedJobCount + datares.successJobCount + datares.runningJobCount + datares.waitingJobCount
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
                this.toGetinstitutions(this.institutionStat)
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
            this.toGetinstitutions(this.institutionStat)
        },
        // 站点维度翻页
        handleSiteCurrentChange(val) {
            this.secondPageNum = val
            this.toGetIntSite(this.institutionSite, 'type')
        },
        // 获取机构维度
        getInstitutionsListToday() {
            if (this.type === 'Today’s active data') {
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
                    maxlist.push(element.runningJobCount + element.failedJobCount + element.successJobCount + element.waitingJobCount)
                })
                let max = Math.max(...maxlist)
                that.institutionsList = res.data.list.map(item => {
                    let obj = {}
                    obj.institutions = item.institutions
                    obj.waiting = item.waitingJobCount
                    obj.running = item.runningJobCount
                    obj.success = item.successJobCount
                    obj.failed = item.failedJobCount
                    obj.total = item.failedJobCount + item.successJobCount + item.runningJobCount + item.waitingJobCount
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
            if (this.type === 'Today’s active data') {
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
                    maxlist.push(element.failedJobCount + element.successJobCount + element.runningJobCount + element.waitingJobCount)
                })
                let max = Math.max(...maxlist)
                that.siteList = res.data.list.map(item => {
                    let obj = {}
                    obj.partyId = item.partyId
                    obj.site = item.site
                    obj.waiting = item.waitingJobCount
                    obj.running = item.runningJobCount
                    obj.success = item.successJobCount
                    obj.failed = item.failedJobCount
                    obj.total = obj.waiting + obj.running + obj.success + obj.failed
                    obj.max = max
                    return obj
                })
            }
        },
        // 获取站点site总数
        getsiteAllToday(val) {
            if (this.type === 'Today’s active data') {
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
                obj.active = datares.siteCount || 0
                obj.waiting = datares.waitingJobCount || 0
                obj.running = datares.runningJobCount || 0
                obj.success = datares.successJobCount || 0
                obj.failed = datares.failedJobCount || 0
                obj.total = obj.waiting + obj.running + obj.success + obj.failed
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
        toGetinstitutions(val) {
            if (this.type === 'Today’s active data') {
                let data = {
                    dateToday: this.dateToday,
                    institutions: val || this.institutionStat,
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
                    institutions: val || this.institutionStat
                }
                institutionsSataPeriod(data).then(res => {
                    getData(res)
                })
            }
            let that = this
            let getData = function (res) {
                that.totalInstitution = res.data.totalRecord
                that.tableIntSateData = res.data.list.map((item) => {
                    item.waiting = parseInt(item.waitingJobCountForInstitutions) || 0
                    item.running = parseInt(item.runningJobCountForInstitutions) || 0
                    item.failed = parseInt(item.failedJobCountForInstitutions) || 0
                    item.success = parseInt(item.successJobCountForInstitutions) || 0
                    item.total = item.failed + item.success + item.running + item.waiting
                    return item
                })
            }
        },
        // 第二个表格下拉选择
        toGetIntSite(val, type) {
            this.institutionSite = val
            if (this.type === 'Today’s active data') {
                let data = {
                    startDate: moment(this.dateToday).format('YYYY-MM-DD 00:00:00'),
                    endDate: moment(this.dateToday).format('YYYY-MM-DD 23:59:59'),
                    institution: val || this.institutionSite,
                    pageNum: this.secondPageNum,
                    pageSize: 10
                }
                getSummarySite(data).then(res => {
                    this.getsiteSataData(res, type)
                })
            } else {
                let data = {
                    startDate: moment(this.timevalue[0]).format('YYYY-MM-DD 00:00:00'),
                    endDate: moment(this.timevalue[1]).format('YYYY-MM-DD 23:59:59'),
                    institution: val || this.institutionSite,
                    pageNum: this.secondPageNum,
                    pageSize: 10
                }
                // siteSataPeriod(data).then(res => {
                //     this.getsiteSataData(res, type)
                // })
                getSummarySite(data).then(res => {
                    this.getsiteSataData(res, type)
                })
            }
        },
        // 第二个下拉表格返回方法
        getsiteSataData(res, type) {
            this.lengthList = [] // 清空空格数据
            this.totalSitetitution = (res.data && res.data.total) || 0
            this.tableIntLabeList = res.data && res.data.siteList

            let arr = []
            res.data && res.data.data.forEach(item => {
                // 空格位置
                if (item.institutionSiteList.length > 1) {
                    this.lengthList.push(item.institutionSiteList.length)
                    for (let index = 0; index < item.institutionSiteList.length - 1; index++) {
                        this.lengthList.push(0)
                    }
                } else {
                    this.lengthList.push(item.institutionSiteList.length)
                }

                item.institutionSiteList.forEach((elm, index) => {
                    let obj = {}
                    obj.institutionSiteName = elm.institutionSiteName
                    obj.institution = item.institution
                    elm.mixSiteModeling.forEach(i => {
                        obj[i.siteName] = { ...i }
                    })
                    arr.push(obj)
                })
            })
            if (type) {
                this.tableIntSiteData = [...arr]
            } else {
                setTimeout(() => {
                    this.tableIntSiteData = [...arr]
                }, 500)
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/monitor.scss';

</style>
