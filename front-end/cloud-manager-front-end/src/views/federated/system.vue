<template>
  <div class="system-box">
    <div class="system">
        <div class="system-header">
            <el-radio-group class="radio" v-model="radio">
                <el-radio-button label="Site service info">{{$t('m.system.siteServiceInfo')}}</el-radio-button>
                <el-radio-button label="Service version manage">{{$t('m.system.serviceVersionManage')}}</el-radio-button>
            </el-radio-group>
        </div>
        <div class="system-body">
            <div v-if="radio==='Site service info'" class="table" >
                <div class="table-head">
                    <el-select class="sel-role input-placeholder" v-model="data.role" clearable :placeholder="$t('m.common.role')">
                        <el-option
                            v-for="item in typeSelect"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                    <el-select class="sel-institution input-placeholder" v-model="data.institutionsList" filterable multiple collapse-tags :placeholder="$t('m.common.institution')">
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
                    height="100%"
                    tooltip-effect="light">
                    <el-table-column type="index" :label="$t('m.common.index')" class-name="cell-td-td" width="70"></el-table-column>
                    <el-table-column prop="siteName" :label="$t('m.common.siteName')"  min-width="90" class-name="cell-td-td" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <!-- <el-button type="text">{{scope.row.siteName}}</el-button> -->
                            <span>{{scope.row.siteName}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="institutions" :label="$t('m.common.institution')"  class-name="cell-td-td" min-width="90" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="partyId" :label="$t('m.common.partyID')"  class-name="cell-td-td"></el-table-column>
                    <el-table-column prop="federatedGroupSetDo" :label="$t('m.common.role')" class-name="cell-td-td" >
                        <template slot-scope="scope">
                            <span>{{scope.row.federatedGroupSetDo.role===1 ? $t('m.common.guest') : $t('m.common.host') }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="federatedSiteModelDos"  :label="$t('m.system.installedItems')" min-width="110" class-name="cell-td-td">
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
                    <el-table-column prop="federatedSiteModelDos" :label="$t('m.system.version')" min-width="90" class-name="cell-td-td" >
                        <template slot-scope="scope">
                            <span v-if="scope.row.federatedSiteModelDos.length>0">
                                <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                                    <!-- <span v-if="item.updateStatus===1">{{item.version}}</span> -->
                                    <tooltip :width="'90px'" :content="item.version" :placement="'top'"/>
                                    <!-- <span >{{item.version}}</span> -->
                                </div>
                            </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" :label="$t('m.system.serviceStatus')" min-width="110" show-overflow-tooltip>
                            <template slot-scope="scope">
                                <span v-if="scope.row.federatedSiteModelDos.length>0">
                                <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                                    <span>{{item.detectiveStatus===1 ? $t('m.common.unavailable') : $t('m.common.Available') }}</span>
                                </div>
                            </span>
                            </template>
                        </el-table-column>
                    <el-table-column prop="federatedSiteModelDos" :label="$t('m.system.installedTime')" min-width="160" class-name="cell-td-td">
                        <template slot-scope="scope">
                            <span v-if="scope.row.federatedSiteModelDos.length>0">
                                <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                                    <span >{{item.installTime | dateFormat}}</span>
                                </div>
                            </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="federatedSiteModelDos"  :label="$t('m.system.upgradeTime')" min-width="160" class-name="cell-td-td">
                        <template slot-scope="scope">
                            <span v-if="scope.row.federatedSiteModelDos.length>0">
                            <div v-for="(item, index) in scope.row.federatedSiteModelDos" :key="index">
                                <span>{{item.detectiveStatus===1 ? $t('m.common.unavailable') : $t('m.common.Available') }}</span>
                            </div>
                        </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" :label="$t('m.common.history')"  width="100" align="center" >
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
                                                <div class="title-time">{{$t('m.common.time')}}</div>
                                                <div class="title-history">{{$t('m.common.history')}}</div>
                                            </div>
                                            <div class="content-box">
                                                <div v-for="(item, index) in historydata" :key="index">
                                                    <div class="title-time">{{item.updateTime | dateFormat}}</div>
                                                    <div v-if="index===0 " class="title-history">
                                                        <span >{{$t('m.system.installed')}}  </span>
                                                        <span class="version">{{item.version}}</span>
                                                        <span v-if="item.updateStatus===1"> {{$t('m.system.successfully')}}  </span>
                                                        <span v-if="item.updateStatus===2"> {{$t('m.system.failed')}} </span>
                                                    </div>
                                                    <div v-if="index > 0 " class="title-history">
                                                        <span v-if="historydata[index-1].updateStatus===1">
                                                            {{$t('m.system.upgradedTo')}}
                                                            <span class="version">{{item.version}}</span>
                                                            <span v-if="item.updateStatus===1"> {{$t('m.system.successfully')}}</span>
                                                            <span v-if="item.updateStatus===2"> {{$t('m.system.failed')}}</span>
                                                        </span>
                                                        <span v-if="historydata[index-1].updateStatus===2">
                                                            {{$t('m.system.installed')}}
                                                            <span class="version">{{item.version}}</span>
                                                            <span v-if="item.updateStatus===1"> {{$t('m.system.successfully')}}</span>
                                                            <span v-if="item.updateStatus===2"> {{$t('m.system.failed')}}</span>
                                                        </span>
                                                    </div>

                                                </div>

                                            </div>
                                        </div>
                                        <el-button @click="getHistory(elm)"  slot="reference" type="text">
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
             <span v-else>
            <systemmanage/>
        </span>
        </div>
    </div>

  </div>
</template>

<script>
import { getSystemManage, systemhistory, institutionsListDropdown } from '@/api/federated'
import moment from 'moment'
import tooltip from '@/components/Tooltip'
import systemmanage from './systemmanage'

export default {
    name: 'PartyId',
    components: {
        tooltip,
        systemmanage
    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            radio: 'Site service info',
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            tableData: [],
            historydata: [], // 历史记录
            institutionsSelectList: [], // 机构名称
            typeSelect: [{
                value: 1,
                label: this.$t('m.common.guest')
            },
            {
                value: 2,
                label: this.$t('m.common.host')
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
        this.getinsSelectList()
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
                            // systemhistory({ id: elm.id, installItems: elm.installItems }).then(res => {
                            //     elm.historylist = [...res.data]
                            // })
                            return elm
                        })
                    }
                })
                this.tableData = res.data.list
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
        height: 380px;
        overflow: auto;
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
