<template>
  <div class="system-box">
    <div class="system">
      <div class="system-header">
        <el-radio-group class="radio" v-model="radio">
            <!-- <el-radio-button label="FATE"></el-radio-button> -->
            <!-- <el-radio-button disabled label="FATE Serving"></el-radio-button> -->
        </el-radio-group>
        <el-input class="input input-placeholder" clearable v-model.trim="data.condition" placeholder="Search for Site Name or Party ID"> </el-input>
        <el-select class="sel-role input-placeholder" v-model="data.role" placeholder="Role">
          <el-option
            v-for="item in typeSelect"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-button class="go" type="primary" @click="toSearch">GO</el-button>
      </div>
      <div class="system-body">
        <div class="table">
          <el-table
            :data="tableData"
            ref="table"
            header-row-class-name="tableHead"
            header-cell-class-name="tableHeadCell"
            cell-class-name="tableCell"
            height="100%"
            tooltip-effect="light"
          >
            <el-table-column type="index" label="Index" class-name="cell-td-td" width="70"></el-table-column>
            <el-table-column prop="siteName" label="Site Name" min-width="90" class-name="cell-td-td">
                <template slot-scope="scope">
                    <!-- <el-button type="text">{{scope.row.siteName}}</el-button> -->
                    <span>{{scope.row.siteName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="institutions" label="Institution" class-name="cell-td-td" min-width="90" show-overflow-tooltip></el-table-column>
            <el-table-column prop="partyId" label="Party ID" class-name="cell-td-td"></el-table-column>
            <el-table-column prop="federatedGroupSetDo" label="Role" class-name="cell-td-td" >
                <template slot-scope="scope">
                    <span>{{scope.row.federatedGroupSetDo.role===1?'Guest':'Host'}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="federatedSiteModelDos" label="Installed items" min-width="110" class-name="cell-td-td">
                <template slot-scope="scope">
                    <span v-if="scope.row.federatedSiteModelDos.length>0">
                        <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                            <!-- <span v-if="item.updateStatus===1">{{item.installItems}}</span> -->
                            <tooltip :width="'106px'" :content="item.installItems" :placement="'top'"/>
                            <!-- <span >{{item.installItems}}</span> -->
                        </div>
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="federatedSiteModelDos" label="Version" min-width="90" class-name="cell-td-td" >
                <template slot-scope="scope">
                    <span v-if="scope.row.federatedSiteModelDos.length>0">
                        <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                            <!-- <span v-if="item.updateStatus===1">{{item.version}}</span> -->
                             <tooltip :width="'80px'" :content="item.version" :placement="'top'"/>
                            <!-- <span >{{item.version}}</span> -->
                        </div>
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="federatedSiteModelDos" label="Installed time" min-width="160" class-name="cell-td-td">
                 <template slot-scope="scope">
                    <span v-if="scope.row.federatedSiteModelDos.length>0">
                        <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                            <span >{{item.installTime | dateFormat}}</span>
                        </div>
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="federatedSiteModelDos" label="Upgrade time" min-width="160" class-name="cell-td-td">
                 <template slot-scope="scope">
                    <span v-if="scope.row.federatedSiteModelDos.length>0">
                        <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                            <span v-if="item.updateTime ===item.installTime">
                                <span style="opacity: 0">.</span>
                            </span>
                            <span v-else>
                                <span >{{item.updateTime | dateFormat}}</span>
                            </span>
                        </div>
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="" label="History"  width="70" align="center" >
                <template slot-scope="scope">
                    <span v-if="scope.row.federatedSiteModelDos.length>0">
                        <div v-for="(elm, ind) in scope.row.federatedSiteModelDos" :key="ind" >
                            <el-popover
                                placement="left"
                                :visible-arrow="false"
                                v-model="elm.visible"
                                popper-class="system-history"
                                :offset="-300"
                                width="450"
                                trigger="click">
                                <div class="content">
                                    <div class="title">
                                        <div class="title-time">Time</div>
                                        <div class="title-history">History</div>
                                    </div>
                                    <div class="content-box">
                                        <div v-for="(item, index) in elm.historylist" :key="index">
                                            <div class="title-time">{{item.updateTime | dateFormat}}</div>
                                            <div v-if="index===0 " class="title-history">
                                                <span >Installed </span>
                                                <span class="version">{{item.version}}</span>
                                                <span v-if="item.updateStatus===1"> successfully</span>
                                                <span v-if="item.updateStatus===2"> failed</span>
                                            </div>
                                            <div v-if="index > 0 " class="title-history">
                                                <span v-if="elm.historylist[index-1].updateStatus===1">
                                                    upgraded to
                                                    <span class="version">{{item.version}}</span>
                                                    <span v-if="item.updateStatus===1"> successfully</span>
                                                    <span v-if="item.updateStatus===2"> failed</span>
                                                </span>
                                                <span v-if="elm.historylist[index-1].updateStatus===2">
                                                    Installed
                                                    <span class="version">{{item.version}}</span>
                                                    <span v-if="item.updateStatus===1"> successfully</span>
                                                    <span v-if="item.updateStatus===2"> failed</span>
                                                </span>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                                <el-button  slot="reference" type="text">
                                    <i class="el-icon-tickets" ></i>
                                </el-button>
                            </el-popover>
                        </div>
                    </span>
                    <span v-else>
                        <el-button disabled slot="reference" type="text">
                            <i class="el-icon-tickets" ></i>
                        </el-button>
                    </span>
                </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="pagination">
          <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :page-size="data.pageSize"
            layout="total, prev, pager, next, jumper"
            :total="total"
          ></el-pagination>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { getSystemManage, systemhistory } from '@/api/federated'
import moment from 'moment'
import tooltip from '@/components/Tooltip'
export default {
    name: 'PartyId',
    components: {
        tooltip
    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            radio: 'FATE',
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            tableData: [],
            historydata: [], // 历史记录
            typeSelect: [
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
                if (item.federatedSiteModelDos.length > 0) {
                    item.federatedSiteModelDos.forEach(elm => {
                        elm.visible = false// 关闭历史记录表格弹框
                    })
                }
            })
        })
    },
    methods: {
        // 初始化表格
        initList() {
            // 去除空参数
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            getSystemManage(this.data).then(res => {
                res.data.list.forEach(item => {
                    if (item.federatedSiteModelDos.length > 0) {
                        item.federatedSiteModelDos = item.federatedSiteModelDos.map(elm => {
                            elm.visible = false// 历史记录表格弹框
                            systemhistory({ id: elm.id, installItems: elm.installItems }).then(res => {
                                elm.historylist = [...res.data]
                            })
                            return elm
                        })
                    }
                })
                this.tableData = res.data.list
                this.total = res.data.totalRecord
            })
        },
        getHistory(item) {
            this.historydata = []
            let data = {
                id: item.id,
                installItems: item.installItems
            }
            systemhistory(data).then(res => {
                this.historydata = [...res.data]
            })
        },
        // 搜索
        toSearch() {
            this.data.pageNum = 1
            this.initList()
        },
        // 翻页
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/system.scss';
.system-history{
    margin-top: -18px !important;
    padding: 0;
    line-height:inherit;
    .content{
        padding: 12px 0px;
    }
     .title{
        padding: 0px 24px;
        color:#217AD9;
        font-size: 18px;
        font-weight:bold;
    }
    .content-box{
        padding: 0px 24px;
        .title-time{
            color: #848C99
        }
        .title-history{
            vertical-align: top;
            color: #4E5766;
            .version{
                color: #217AD9;
            }
        }
    }
    .title-time{
        width: 50%;
        display:inline-block;
        margin: 12px 0;
    }
    .title-history{
        width: 50%;
        display:inline-block;
        margin: 12px 0;
    }

}
</style>
