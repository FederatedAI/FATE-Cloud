<template>
  <div class="ip-box">
    <div class="ip">
      <div class="ip-header">
        <el-input class="input input-placeholder" clearable v-model.trim="data.condition" placeholder="Search for Site Name or Party ID">
          <!-- <i slot="suffix" @click="toSearch" class="el-icon-search search el-input__icon" /> -->
        </el-input>
        <el-select class="sel-role input-placeholder" v-model="data.role" placeholder="Role">
          <el-option
            v-for="item in roleTypeSelect"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-button class="go" @click="toSearch" type="primary">GO</el-button>
      </div>
      <div class="ip-body">
        <div class="table">
          <el-table
            :data="tableData"
            ref="table"
            header-row-class-name="tableHead"
            header-cell-class-name="tableHeadCell"
            cell-class-name="tableCell"
            height="100%"
          >
            <el-table-column prop="" type="index"  label="Index" class-name="cell-td-td" width="70"></el-table-column>
            <el-table-column prop="siteName" label="Site Name" class-name="cell-td-td" min-width="90" show-overflow-tooltip></el-table-column>
            <el-table-column prop="partyId" label="Party ID" class-name="cell-td-td" ></el-table-column>
            <el-table-column prop="role" label="Role" class-name="cell-td-td">
                <template slot-scope="scope">
                    <span>{{scope.row.role===1?'Guest':'Host'}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="networkAccessEntrances" label="Network Acess Entrances" min-width="210px">
              <template slot-scope="scope"  >
                   <span v-if="scope.row.networkAccessEntrancesOld"
                   :class="{ 'cell-td': scope.row.networkAccessEntrancesOld.split(';').length>=scope.row.networkAccessExitsOld.split(';').length?false:true }">
                        <div  v-for="(item,index) in scope.row.networkAccessEntrancesOld.split(';')" :key="index">
                            <span class="iptext" v-if="item">{{item}}</span>
                            <span class="telnet"  @click="testTelent(item)" v-if="item">telnet</span>
                        </div>
                    </span>
              </template>
            </el-table-column>
            <el-table-column prop="networkAccessExits" label="Network Acess Exits" min-width="150">
              <template slot-scope="scope" class="td">
                    <span v-if="scope.row.networkAccessExitsOld"
                    :class="{ 'cell-td': scope.row.networkAccessEntrancesOld.split(';').length<=scope.row.networkAccessExitsOld.split(';').length?false:true }">
                        <div  v-for="(item,index) in scope.row.networkAccessExitsOld.split(';')" :key="index">
                            <span class="iptext" v-if="item">{{item}} </span>
                        </div>
                    </span>
              </template>
            </el-table-column>
            <el-table-column prop="updateTime" show-overflow-tooltip label="Update Time" class-name="cell-td-td" min-width="100px">
                <template slot-scope="scope">
                    <span>{{scope.row.updateTime | dateFormat}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="" align="left" label="Action" class-name="cell-td-td" min-width="70">
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
                        placement="left"
                        popper-class="ip-history"
                        width="650"
                        trigger="click"
                        :visible-arrow="false"
                        :offset="-300">
                        <div class="content">
                            <div class="tiltle">
                                <div class="tiltle-time">Time</div>
                                <div class="tiltle-history">History</div>
                            </div>
                            <div class="content-loop">
                                <div class="loop" v-for="(item, index) in scope.row.historylist" :key="index">
                                    <div class="time">
                                        <div class="time-text">{{item.updateTime | dateFormat}}</div>
                                    </div>
                                    <div class="history">
                                        <!-- status===1同意 -->
                                        <div v-if="item.status===1 && item.networkAccessEntrances !== item.networkAccessEntrancesOld" class="line">
                                            Agreed to change the Network Access Entrances
                                        </div>
                                        <!-- status===2拒绝 -->
                                        <div v-if="item.status===2 && item.networkAccessEntrances !== item.networkAccessEntrancesOld"  class="line">
                                            Rejected to change the Network Acess Exits
                                        </div>
                                        <!-- 入口 -->
                                        <div v-if="item.networkAccessEntrances !==item.networkAccessEntrancesOld" class="content-box">
                                            <div class="from-tiltle">From</div>
                                            <div class="from-text">
                                                <div v-for="(item,index) in item.networkAccessEntrancesOld.split(';')" :key="index">
                                                    {{item}}
                                                </div>
                                            </div>
                                            <div style="margin-left: 50px;" class="from-tiltle" >to</div>
                                            <div class="from-text">
                                                <div v-for="(item,index) in item.networkAccessEntrances.split(';')" :key="index">
                                                    {{item}}
                                                </div>
                                            </div>
                                        </div>
                                        <!-- status===1同意 -->
                                        <div v-if="item.status===1 && item.networkAccessExits !== item.networkAccessExitsOld"  class="line">
                                            <span v-if="item.networkAccessEntrances !== item.networkAccessEntrancesOld"> and </span>
                                            Agreed to change the Network Access Exits
                                        </div>
                                        <!-- status===2拒绝 -->
                                        <div v-if="item.status===2 && item.networkAccessExits !== item.networkAccessExitsOld"  class="line">
                                            <span v-if="item.networkAccessEntrances !== item.networkAccessEntrancesOld"> and </span>
                                            Rejected to change the Network Access Exits
                                        </div>
                                         <!-- 出口 -->
                                        <div v-if="item.networkAccessExitsOld !==item.networkAccessExits" class="content-box">
                                            <div class="from-tiltle">From</div>
                                            <div class="from-text">
                                                <div v-for="(item,index) in item.networkAccessExitsOld.split(';')" :key="index">
                                                    {{item}}
                                                </div>
                                            </div>
                                            <div style="margin-left: 50px;" class="from-tiltle" >to</div>
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
        </div>
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
      <el-dialog :visible.sync="dialogVisible" class="ip-delete-dialog" width="700px">
        <div class="line-text-one">Federated Site
            "<span style="color:#217AD9">{{dialogData.siteName}}</span>"
        </div>
        <div class="line-text-one" v-if="dialogData.newEntrancesData!==dialogData.oldEntrancesData">applies to change the Network Acess Entrances</div>
         <div class="line-text" v-if="dialogData.newEntrancesData!==dialogData.oldEntrancesData">
            <div class="entrances">
            <div class="rigth-box" style="margin-right: 70px">
                <div class="from">from</div>
                <div class="text">
                <span v-for="(item, index) in dialogData.oldEntrancesData.split(';')" :key="index">{{item}}</span>
                </div>
            </div>
            <div class="rigth-box" style="margin-left: 70px">
                <div class="from">to</div>
                <div class="text">
                <span v-for="(item, index) in dialogData.newEntrancesData.split(';')" :key="index">{{item}}</span>
                </div>
            </div>
            </div>
        </div>
         <div class="line-text-one" style="margin-top: 24px;" v-if="dialogData.newExitsData!==dialogData.oldExitsData">applies to change the Network Acess Exits</div>
         <div class="line-text" v-if="dialogData.newExitsData!==dialogData.oldExitsData">
            <div class="entrances">
            <div class="rigth-box" style="margin-right: 70px">
                <div class="from">from</div>
                <div class="text">
                <span v-for="(item, index) in dialogData.oldExitsData.split(';')" :key="index">{{item}}</span>
                </div>
            </div>
            <div class="rigth-box" style="margin-left: 70px">
                <div class="from">to</div>
                <div class="text">
                <span v-for="(item, index) in dialogData.newExitsData.split(';')" :key="index">{{item}}</span>
                </div>
            </div>
            </div>
        </div>
        <div class="line-text-two">Do you confirm these updates?</div>
        <div class="dialog-footer">
          <el-button class="ok-btn" type="primary" @click="sureAction">Agree</el-button>
          <el-button class="ok-btn" type="info" @click="rejectAction">Reject</el-button>
          <el-button class="ok-btn" type="info" @click="cancelAction">Cancel</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>

import { ipList, telnet, deal, iphistory } from '@/api/federated'
import moment from 'moment'

export default {
    name: 'Ip',
    components: {},
    filters: {
        dateFormat(vaule) {
            return moment(vaule).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            dialogVisible: false,
            currentPage1: 1, // 当前页
            hide: true,
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
            roleTypeSelect: [// role下拉
                {
                    value: 0,
                    label: 'Role'
                },
                {
                    value: 1,
                    label: 'Guest'
                },
                {
                    value: 2,
                    label: 'Host'
                }
            ],
            data: {
                pageNum: 1,
                pageSize: 20
            }

        }
    },
    created() {
        this.initList()
    },
    mounted() {
        // 获取需要绑定的table
        this.dom = this.$refs.table.bodyWrapper
        // 监听表格滚动
        this.dom.addEventListener('scroll', () => {
            this.tableData.forEach(item => {
                item.visible = false
            })
        })
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
                this.$message({ type: 'success', message: 'Update Success !', duration: 3000 })
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
                this.$message({ type: 'success', message: 'Handle Success!', duration: 3000 })
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
                    this.$message({ type: 'success', message: 'Telnet Success !', duration: 5000 })
                }
            }).catch(res => {
                if (res.code === 109) {
                    this.$message({ type: 'error', message: 'Telnet Failed !', duration: 5000 })
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
                            width: 32px;
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
