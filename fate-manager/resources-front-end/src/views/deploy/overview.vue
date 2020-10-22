<template>
  <div class="overview">
    <div class="overview-box">
        <div class="serve-title">
            <div class="overview-inline">Service Overview</div>
        </div>
        <div class="serve-content">
            <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane label="FATE" name="FATE"></el-tab-pane>
                <el-tab-pane disabled label="FATE Serving" name="FATE Serving"></el-tab-pane>
            </el-tabs>
            <div class="empty"></div>
        </div>
        <div class="ip-header">
            <el-input class="input input-placeholder" clearable v-model.trim="data.condition" placeholder="Search for Site Name or Party ID">
            </el-input>
            <el-select class="sel-role input-placeholder" v-model="data.role" placeholder="Role">
                <el-option
                    v-for="item in roleTypeSelect"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                ></el-option>
            </el-select>
            <!-- <el-select class="sel-organization input-placeholder" v-model="data.federatedId" placeholder="Organization">
                <el-option
                    v-for="item in organizationList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
            </el-select> -->
            <el-button class="go" @click="initiList" type="primary">GO</el-button>
        </div>
        <div class="partyid-body">
            <div class="table">
                <el-table
                    :data="tableData"
                    ref="table"
                    header-row-class-name="tableHead"
                    header-cell-class-name="tableHeadCell"
                    cell-class-name="tableCell"
                    height="100%"
                    tooltip-effect="light" >
                    <el-table-column type="index" label="Index" class-name="cell-td-td" width="70"></el-table-column>
                    <!-- <el-table-column prop="federatedOrganization" label="Organization" class-name="cell-td-td" width="100"></el-table-column> -->
                    <el-table-column prop="siteName" label="Site Name" class-name="cell-td-td" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <span style="cursor:pointer;color: #217AD9" v-if="scope.row.installInfo" @click="toServe(scope.row)">{{scope.row.siteName}}</span>
                            <span v-else >{{scope.row.siteName}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="partyId" label="Party ID" class-name="cell-td-td"></el-table-column>
                    <el-table-column prop="role.desc" label="Role" class-name="cell-td-td"></el-table-column>
                    <el-table-column prop="installInfo.componentName" label="Installed items" min-width="160"  >
                        <template slot-scope="scope">
                            <span v-if="scope.row.installInfo">
                                <div v-for="(item, index) in scope.row.installInfo" :key="index">
                                    <tooltip :width="'106px'" :content="item.componentName" :placement="'top'"/>
                                    <!-- {{item.componentName}} -->
                                </div>
                            </span>
                            <span v-else style="opacity:0" >''</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="installInfo.ComponentVersion" label="Version" min-width="100" >
                        <template slot-scope="scope">
                            <div style="display: inline-block;">
                                <div v-for="(item, index) in scope.row.installInfo" :key="index" style="text-align: right;">
                                    <span class="dot">
                                        <span :class="{'dot-c':item.upgradeStatus}"></span>
                                        <!-- {{item.ComponentVersion}} -->
                                        <tooltip :width="'80px'" :content="item.ComponentVersion" :placement="'top'"/>
                                    </span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="installInfo" label="Installed time" width="160"  align="left" >
                        <template slot-scope="scope">
                            <div v-for="(item, index) in scope.row.installInfo" :key="index">{{item.installTime | dateFormat}}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="installInfo" label="Upgrade time"  width="160" class-name="cell-td-td">
                        <template slot-scope="scope">
                            <div v-for="(item, index) in scope.row.installInfo" :key="index">{{item.upgradeTime | dateFormat}}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="" label="Operation"  align="center" width="100" class-name="cell-td-td" >
                        <template slot-scope="scope">
                            <span v-if="scope.row.installInfo">
                                <div v-for="(item, index) in scope.row.installInfo" :key="index" style="height:23px" >
                                    <span v-if="item.operation">
                                        <el-popover
                                            placement="left"
                                            v-model="item.visible"
                                            :visible-arrow="false"
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
                                                    <span v-for="(elem, ind) in item.operation" :key="ind">
                                                        <div class="title-time">{{elem.operateTime | dateFormat}}</div>
                                                        <div class="title-history">
                                                            <span>{{elem.operateAction.desc}}</span>
                                                        </div>
                                                    </span>
                                                </div>
                                            </div>
                                            <img slot="reference" src="@/assets/history.png" alt="" >
                                        </el-popover>
                                    </span>
                                    <span v-else >
                                        <img style="opacity: 0.7;cursor:not-allowed" src="@/assets/history.png" alt="" >
                                    </span>
                                </div>
                            </span>
                            <el-button @click="todeploy(scope.row)"  v-else type="text">
                                 Go to deploy
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </div>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getOverViewList } from '@/api/deploy'
import moment from 'moment'
import tooltip from '@/components/Tooltip'
export default {
    name: 'overview',
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
            data: {
                // pageNum: 1,
                // pageSize: 20
            },
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
            tableData: [],
            organizationList: [
                {
                    value: '',
                    label: 'Organization'
                }],
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            overviewdialog: false,
            activeName: 'FATE',
            address: ''
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
        this.initiList()
        this.$store.dispatch('selectEnum').then(res => {
            this.organization.forEach(item => {
                this.organizationList.push(item)
            })
        })
    },
    mounted() {
        // 获取需要绑定的table
        this.dom = this.$refs.table.bodyWrapper
        // 监听表格滚动
        this.dom.addEventListener('scroll', () => {
            this.tableData.forEach(item => {
                item.installInfo && item.installInfo.forEach(elm => {
                    elm.visible = false// 历史记录表格弹框
                })
            })
        })
    },
    methods: {
        initiList() {
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            getOverViewList(this.data).then(res => {
                if (res.data) {
                    this.tableData = res.data.map(item => {
                        item.installInfo && item.installInfo.forEach(elm => {
                            elm.visible = false// 历史记录表格弹框
                        })
                        return item
                    })
                } else {
                    this.tableData = []
                }
                // console.log('==>>', this.tableData)
            })
        },
        handleClick(tab, event) {
            // console.log(tab, event)
        },
        tostart() {

        },
        // 翻页
        handleCurrentChange(val) {
            // this.data.pageNum = val
            // this.initiList()
        },
        toShowLog() {
            // this.$refs['log'].logdialog = true
        },
        toServe(row) {
            this.$router.push({
                name: 'service',
                query: {
                    fateVersion: row.fateVersion,
                    partyId: row.partyId,
                    siteName: row.siteName,
                    federatedId: row.federatedId
                    // version: row.installInfo[0].ComponentVersion,
                }
            })
        },
        todeploy(row) {
            this.$router.push({
                name: 'auto',
                query: {
                    partyId: row.partyId,
                    siteName: row.siteName,
                    federatedId: row.federatedId
                }
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/overview.scss';
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
            color: #4E5766;
            span{
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
