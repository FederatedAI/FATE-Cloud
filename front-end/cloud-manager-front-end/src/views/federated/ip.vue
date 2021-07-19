<template>
    <div class="ip-box">
        <div class="ip">
            <div class="ip-header">
                <el-radio-group class="radio" v-model="radio">
                    <el-radio-button label="IP manage">{{$t('m.ip.IPmanage')}}</el-radio-button>
                    <el-radio-button label="Exchange info">{{$t('m.ip.exchangeInfo')}}</el-radio-button>
                </el-radio-group>
            </div>
            <div class="ip-body">
                <div class="ip-manage" v-if="radio==='IP manage'">
                    <div class="table-head">
                        <el-select class="sel-role input-placeholder" v-model="data.role" clearable :placeholder="$t('m.common.role')">
                            <el-option
                                v-for="item in typeSelect"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            ></el-option>
                        </el-select>
                        <el-select class="sel-institution input-placeholder" v-model="data.institutionsList" filterable multiple collapse-tags :placeholder="$t('m.common.institutionName')">
                            <el-option
                                v-for="item in institutionsSelectList"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            ></el-option>
                        </el-select>
                        <el-input class="input input-placeholder" clearable v-model.trim="data.condition"  :placeholder="$t('m.site.searchSiteOrParty')"> </el-input>
                        <el-button class="go" type="primary" @click="toSearch">{{$t('m.common.go')}}</el-button>
                    </div>
                        <el-table
                            :data="tableData"
                            ref="table"
                            header-row-class-name="tableHead"
                            header-cell-class-name="tableHeadCell"
                            cell-class-name="tableCell"
                            height="100%">
                            <el-table-column prop="" type="index"  :label="$t('m.common.index')" class-name="cell-td-td" width="70"></el-table-column>
                            <el-table-column prop="siteName"  :label="$t('m.common.siteName')"  class-name="cell-td-td" min-width="90" show-overflow-tooltip></el-table-column>
                            <el-table-column prop="institutions" :label="$t('m.common.institutionName')"  class-name="cell-td-td" min-width="90" show-overflow-tooltip></el-table-column>
                            <el-table-column prop="partyId" :label="$t('m.common.partyID')"  class-name="cell-td-td" ></el-table-column>
                            <el-table-column prop="role" :label="$t('m.common.role')" class-name="cell-td-td">
                                <template slot-scope="scope">
                                    <span>{{scope.row.role===1? $t('m.common.guest') : $t('m.common.host')}}</span>
                                </template>
                            </el-table-column>
                                <el-table-column prop="networkAccessEntrances" :label="$t('m.site.networkEntrances')" min-width="210px">
                                    <template slot-scope="scope"  >
                                        <span v-if="scope.row.networkAccessEntrancesOld"
                                        :class="{ 'cell-td': scope.row.networkAccessEntrancesOld.split(';').length>=scope.row.networkAccessExitsOld.split(';').length?false:true }">
                                            <div  v-for="(item,index) in scope.row.networkAccessEntrancesOld.split(';')" :key="index">
                                                <span class="iptext" v-if="item">{{item}}</span>
                                                <span class="telnet"  @click="testTelent(item)" v-if="item">{{$t('m.ip.telent')}}</span>
                                            </div>
                                        </span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="networkAccessExits" :label="$t('m.site.networkExits')"  min-width="150">
                                    <template slot-scope="scope" class="td">
                                        <span v-if="scope.row.networkAccessExitsOld"
                                        :class="{ 'cell-td': scope.row.networkAccessEntrancesOld.split(';').length<=scope.row.networkAccessExitsOld.split(';').length?false:true }">
                                            <div  v-for="(item,index) in scope.row.networkAccessExitsOld.split(';')" :key="index">
                                                <span class="iptext" v-if="item">{{item}} </span>
                                            </div>
                                        </span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="updateTime" show-overflow-tooltip :label="$t('m.common.updateTime')"  class-name="cell-td-td" min-width="100px">
                                <template slot-scope="scope">
                                    <span>{{scope.row.updateTime | dateFormat}}</span>
                                </template>
                                </el-table-column>
                                <el-table-column prop="" align="left" :label="$t('m.common.action')" class-name="cell-td-td" min-width="70">
                                    <template slot-scope="scope">
                                        <el-badge is-dot v-if="scope.row.status===0" type="warning" style="margin-right:10px;">
                                            <el-button type="text" >
                                                <i class="el-icon-refresh-right" @click="upDate(scope.row)"></i>
                                            </el-button>
                                        </el-badge>
                                        <el-button v-else type="text" :disabled="true" style="margin-right:10px;" class="delete">
                                            <i class="el-icon-refresh-right" ></i>
                                        </el-button>
                                        <el-popover
                                            v-if="scope.row.history"
                                            v-model="scope.row.visible"
                                            effect="light"
                                            placement="left"
                                            popper-class="ip-history"
                                            width="650"
                                            trigger="click"
                                            :visible-arrow="false"
                                            :offset="-300">
                                            <div class="content">
                                                <div class="tiltle">
                                                    <div class="tiltle-time">{{$t('m.common.time')}}</div>
                                                    <div class="tiltle-history">{{$t('m.common.history')}}</div>
                                                </div>
                                                <div class="content-loop">
                                                    <div class="loop" v-for="(item, index) in scope.row.historylist" :key="index">
                                                        <div class="time">
                                                            <div class="time-text">{{item.updateTime | dateFormat}}</div>
                                                        </div>
                                                        <div class="history">
                                                            <!-- status===1同意 -->
                                                            <div v-if="item.status===1 && item.networkAccessEntrances !== item.networkAccessEntrancesOld" class="line">
                                                                {{$t('m.common.agree')}}{{$t('m.ip.toChange')}}{{$t('m.site.networkEntrances')}}
                                                            </div>
                                                            <!-- status===2拒绝 -->
                                                            <div v-if="item.status===2 && item.networkAccessEntrances !== item.networkAccessEntrancesOld"  class="line">
                                                                {{$t('m.common.reject')}}{{$t('m.ip.toChange')}}{{$t('m.site.networkEntrances')}}
                                                            </div>
                                                            <!-- 入口 -->
                                                            <div v-if="item.networkAccessEntrances !==item.networkAccessEntrancesOld" class="content-box">
                                                                <div class="from-tiltle">{{$t('m.ip.from')}}</div>
                                                                <div class="from-text">
                                                                    <div v-for="(item,index) in item.networkAccessEntrancesOld.split(';')" :key="index">
                                                                        {{item}}
                                                                    </div>
                                                                </div>
                                                                <div style="margin-left: 50px;" class="from-tiltle" >{{$t('m.ip.to')}}</div>
                                                                <div class="from-text">
                                                                    <div v-for="(item,index) in item.networkAccessEntrances.split(';')" :key="index">
                                                                        {{item}}
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <!-- status===1同意 -->
                                                            <div v-if="item.status===1 && item.networkAccessExits !== item.networkAccessExitsOld"  class="line">
                                                                <span v-if="item.networkAccessEntrances !== item.networkAccessEntrancesOld"> {{$t('m.common.and')}} </span>
                                                                {{$t('m.common.agree')}}{{$t('m.ip.toChange')}}{{$t('m.site.networkExits')}}
                                                            </div>
                                                            <!-- status===2拒绝 -->
                                                            <div v-if="item.status===2 && item.networkAccessExits !== item.networkAccessExitsOld"  class="line">
                                                                <span v-if="item.networkAccessEntrances !== item.networkAccessEntrancesOld"> {{$t('m.common.and')}} </span>
                                                                {{$t('m.common.reject')}}{{$t('m.ip.toChange')}}{{$t('m.site.networkExits')}}
                                                            </div>
                                                            <!-- 出口 -->
                                                            <div v-if="item.networkAccessExitsOld !==item.networkAccessExits" class="content-box">
                                                                <div class="from-tiltle">{{$t('m.ip.from')}}</div>
                                                                <div class="from-text">
                                                                    <div v-for="(item,index) in item.networkAccessExitsOld.split(';')" :key="index">
                                                                        {{item}}
                                                                    </div>
                                                                </div>
                                                                <div style="margin-left: 50px;" class="from-tiltle" >{{$t('m.ip.to')}}</div>
                                                                <div class="from-text">
                                                                    <div v-for="(item,index) in item.networkAccessExits.split(';')" :key="index">
                                                                        {{item}}
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <el-button slot="reference" type="text" >
                                                <i class="el-icon-tickets"></i>
                                            </el-button>
                                        </el-popover>
                                        <el-button v-if="!scope.row.history" slot="reference" disabled style="margin-left:0" type="text" >
                                            <i class="el-icon-tickets"></i>
                                        </el-button>
                                    </template>
                                </el-table-column>
                        </el-table>
                    <div class="pagination">
                        <el-pagination
                            background
                            @size-change="handleSizeChange"
                            @current-change="handleCurrentChange"
                            :current-page.sync="currentPage1"
                            :page-size="data.pageSize"
                            layout="total, prev, pager, next, jumper"
                            :total="total"
                        ></el-pagination>
                    </div>
                </div>
                <span  v-else>
                    <ipexchange/>
                </span>
            </div>
            <el-dialog :visible.sync="dialogVisible" class="ip-delete-dialog" width="700px">
                <div class="line-text-one">{{$t('m.ip.federatedSite')}}
                    "<span style="color:#217AD9">{{dialogData.siteName}}</span>"
                    {{$t('m.ip.fedSite')}}
                </div>
                <div class="line-text-one" v-if="dialogData.newEntrancesData!==dialogData.oldEntrancesData">
                    {{$t('m.ip.appliesAcessEntrances')}}
                </div>
                    <div class="line-text" v-if="dialogData.newEntrancesData!==dialogData.oldEntrancesData">
                    <div class="entrances">
                    <div class="rigth-box" style="margin-right: 70px">
                        <div class="from">{{$t('m.ip.from')}}</div>
                        <div class="text">
                        <span v-for="(item, index) in dialogData.oldEntrancesData.split(';')" :key="index">{{item}}</span>
                        </div>
                    </div>
                    <div class="rigth-box" style="margin-left: 70px">
                        <div class="from">{{$t('m.ip.to')}}</div>
                        <div class="text">
                        <span v-for="(item, index) in dialogData.newEntrancesData.split(';')" :key="index">{{item}}</span>
                        </div>
                    </div>
                    </div>
                </div>
                    <div class="line-text-one" style="margin-top: 24px;" v-if="dialogData.newExitsData!==dialogData.oldExitsData">
                    {{$t('m.ip.appliesAcessExits')}}
                    </div>
                    <div class="line-text" v-if="dialogData.newExitsData!==dialogData.oldExitsData">
                    <div class="entrances">
                    <div class="rigth-box" style="margin-right: 70px">
                        <div class="from">{{$t('m.ip.from')}}</div>
                        <div class="text">
                        <span v-for="(item, index) in dialogData.oldExitsData.split(';')" :key="index">{{item}}</span>
                        </div>
                    </div>
                    <div class="rigth-box" style="margin-left: 70px">
                        <div class="from">{{$t('m.ip.to')}}</div>
                        <div class="text">
                        <span v-for="(item, index) in dialogData.newExitsData.split(';')" :key="index">{{item}}</span>
                        </div>
                    </div>
                    </div>
                </div>
                <div class="line-text-two">{{$t('m.ip.confirmUpdates')}}</div>
                <div class="dialog-footer">
                    <el-button class="ok-btn" type="primary" @click="sureAction">{{$t('m.common.agree')}}</el-button>
                    <el-button class="ok-btn" type="info" @click="rejectAction">{{$t('m.common.reject')}}</el-button>
                    <el-button class="ok-btn" type="info" @click="cancelAction">{{$t('m.common.cancel')}}</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>

import { ipList, telnet, deal, iphistory, institutionsListDropdown } from '@/api/federated'
import moment from 'moment'
import ipexchange from './ipexchange'

export default {
    name: 'Ip',
    components: { ipexchange },
    filters: {
        dateFormat(vaule) {
            return moment(vaule).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            dialogVisible: false,
            radio: this.$route.query.type ? 'Exchange info' : 'IP manage',
            currentPage1: 1, // 当前页
            total: 0,
            isdot: false,
            historylist: [], // 历史数据
            dialogData: {
                siteName: '',
                type: '',
                newEntrancesData: '',
                oldEntrancesData: '',
                newExitsData: '',
                oldExitsData: ''
            },
            input: '',
            tableData: [],
            value: '',
            institutionsSelectList: [], // 机构名称
            typeSelect: [{
                value: 1,
                label: this.$t('m.common.guest')
            },
            {
                value: 2,
                label: this.$t('m.common.host')
            }],
            data: {
                pageNum: 1,
                pageSize: 20
            }

        }
    },
    created() {
        this.initList()
        this.getinsSelectList()
    },
    mounted() {
        if (this.radio === 'IP manage') {
            // 获取需要绑定的table
            this.dom = this.$refs.table.bodyWrapper
            // 监听表格滚动
            this.dom.addEventListener('scroll', () => {
                this.tableData.forEach(item => {
                    item.visible = false
                })
            })
        }
    },
    methods: {
        initList() {
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            ipList(this.data).then(res => {
                this.tableData = res.data.list.map(item => {
                    if (item.history) {
                        item.visible = false// 历史记录表格弹框
                        iphistory({ partyId: item.partyId }).then(res => {
                            item.historylist = [...res.data]
                        })
                    }
                    return item
                })
                this.total = res.data.totalRecord
            })
        },
        // 机构下拉接口
        async getinsSelectList() {
            let res = await institutionsListDropdown()
            res.data.institutionsSet.forEach((item, index) => {
                let obj = {}
                obj.value = item
                obj.label = item
                this.institutionsSelectList.push(obj)
            })
        },
        // 搜索
        toSearch() {
            this.data.pageNum = 1
            this.initList()
        },
        handleSizeChange(val) {
            console.log(`每页 ${val} 条`)
        },
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        upDate(row, type) {
            this.dialogVisible = true
            this.dialogData = {
                caseId: row.caseId,
                partyId: row.partyId,
                siteName: row.siteName,
                newEntrancesData: row.networkAccessEntrances,
                oldEntrancesData: row.networkAccessEntrancesOld,
                newExitsData: row.networkAccessExits,
                oldExitsData: row.networkAccessExitsOld
            }
        },
        // 确定更改
        sureAction() {
            let data = {
                caseId: this.dialogData.caseId,
                partyId: this.dialogData.partyId,
                status: 1// 同意
            }
            deal(data).then(res => {
                this.dialogVisible = false
                this.$message.success(this.$t('m.ip.updateSuccess'))
                this.initList()
            })
        },
        // 拒绝更改
        rejectAction() {
            let data = {
                caseId: this.dialogData.caseId,
                partyId: this.dialogData.partyId,
                status: 2// 拒绝
            }
            deal(data).then(res => {
                this.dialogVisible = false
                this.$message.success(this.$t('m.ip.handleSuccess'))
                this.initList()
            })
        },
        // 取消更改
        cancelAction() {
            this.dialogVisible = false
        },
        // 测试Telent
        testTelent(ipport) {
            let data = {
                ip: ipport.split(':')[0],
                port: parseInt(ipport.split(':')[1])
            }
            telnet(data).then(res => {
                if (res.code === 0) {
                    this.$message.success(this.$t('m.ip.telnetSuccess'))
                }
            }).catch(res => {
                if (res.code === 109) {
                    this.$message.error(this.$t('m.ip.telnetFailed'))
                }
            })
        }
        // gethistory(row) {
        //     // this.sth = 'height:300px;'
        //     this.historylist = []
        //     let data = {
        //         partyId: row.partyId
        //     }
        //     iphistory(data).then(res => {
        //         this.historylist = [...res.data]
        //     })
        // }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ip.scss';

.ip-history{
    margin-top: -18px !important;
    padding: 0;
    background: #fff !important;
    .content{
        padding: 36px 0;
        // height:500px;
        .tiltle{
            margin:0 36px;
            font-size: 18px;
            color: #217AD9;
            font-weight: bold;
            margin-bottom: 12px;
            .tiltle-time{
                width: 30%;
                display:inline-block;
            }
            .tiltle-history{
                width: 70%;
                display:inline-block;
            }
        }
        .content-loop{
            max-height: 452px;
            overflow: auto;
            .loop{
                margin:0 36px;
                .time{
                    width: 30%;
                    display:inline-block;
                    vertical-align: top;
                    .time-text{
                        color: #848C99;
                        margin: 12px 0;
                    }
                }
                .history{
                    width: 70%;
                    display:inline-block;
                    .line{
                        color: #2D3642;
                        font-size: 14px;
                        margin: 12px 0;
                    }
                    .content-box{
                        padding: 12px;
                        background-color: #F5F8FA;
                        .from-tiltle{
                            color: #848C99;
                            display:inline-block;
                            width: 42px;
                            vertical-align: top;
                            text-align: right;
                            margin-right: 10px;
                        }
                        .from-text{
                            color: #217AD9;
                            display:inline-block;
                            // width: 30%;
                            vertical-align: top;
                        }
                    }
                }
            }
        }

    }
}
</style>
