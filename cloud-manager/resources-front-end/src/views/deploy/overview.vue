<template>

<div class="overview">
    <div class="row-content">
        <el-radio-group class="radio" v-model="activeName" @change="handleClick">
            <el-radio-button label="FATE"></el-radio-button>
            <el-radio-button disabled label="FATE Serving"></el-radio-button>
        </el-radio-group>
    </div>
    <div class="overview-header">
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
        <el-button class="go" @click="initiList" type="primary">GO</el-button>
    </div>
    <div class="overview-body">
        <div class="table">
            <el-table
                :data="tableData"
                ref="table"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="100%"
                tooltip-effect="light" >
                <el-table-column type="index" :label="$t('Index')" class-name="cell-td-td" width="70"></el-table-column>
                <el-table-column prop="siteName" :label="$t('Site Name')"  class-name="cell-td-td" >
                    <template slot-scope="scope">
                        <span style="cursor:pointer;color: #217AD9" v-if="scope.row.installInfo || !scope.row.deployTag" @click="toServe(scope.row)">
                            <tooltip :width="'80px'" :content="scope.row.siteName" :placement="'top'"/>
                        </span>
                        <span v-else >
                            <tooltip :width="'80px'" :content="scope.row.siteName" :placement="'top'"/>
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="partyId" :label="$t('Party ID')"   class-name="cell-td-td"></el-table-column>
                <el-table-column prop="role.desc" :label="$t('Role')"  class-name="cell-td-td"></el-table-column>
                <el-table-column prop="installInfo.componentName"  :label="$t('Installed items')"   min-width="110"  >
                    <template slot-scope="scope">
                        <span v-if="scope.row.installInfo">
                            <div v-for="(item, index) in scope.row.installInfo" :key="index">
                                <tooltip  :width="'106px'" :content="item.componentName" :placement="'top'"/>
                            </div>
                        </span>
                        <span v-else style="opacity:0" >''</span>
                    </template>
                </el-table-column>
                <el-table-column prop="installInfo.ComponentVersion" align="center" :label="$t('Version')"  min-width="100" >
                    <template slot-scope="scope">
                        <div style="display: inline-block;">
                            <div v-for="(item, index) in scope.row.installInfo" :key="index" style="text-align: right;">
                                <span class="dot">
                                    <span :class="{'dot-c':item.upgradeStatus}"></span>
                                    <tooltip style="margin-right:48px" :width="'80px'" :content="`${item.ComponentVersion}`" :placement="'top'"/>
                                </span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="Deployment" :label="$t('Deployment')" class-name="cell-td-td" min-width="100">
                    <template slot-scope="scope">
                        <span v-if='scope.row.installInfo'>
                            <div v-for="(item, index) in scope.row.installInfo" :key="index">{{item.deployType.code===1?'By KubeFATE':'By Ansible'}}</div>
                        </span>
                        <span v-else>
                            <span v-if='scope.row.deployType.code===1'>
                                {{'By KubeFATE'}}
                            </span>
                                <span v-if='scope.row.deployType.code===2'>
                                {{'By Ansible'}}
                            </span>

                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="installInfo" :label="$t('Installed Time')"  width="160"  align="left" >
                    <template slot-scope="scope">
                        <div v-for="(item, index) in scope.row.installInfo" :key="index">{{item.installTime | dateFormat}}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="installInfo" :label="$t('Upgrade Time')" width="160" class-name="cell-td-td">
                    <template slot-scope="scope">
                        <div v-for="(item, index) in scope.row.installInfo" :key="index">{{item.upgradeTime | dateFormat}}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="" :label="$t('Service Status')"  width="110" class-name="cell-td-td">
                    <template slot-scope="scope">
                        <span v-if="scope.row.installInfo">
                            <div v-for="(item, index) in scope.row.installInfo" :key="index">{{item.serviceStatus.desc }}</div>
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="" :label="$t('Action')"   align="center" width="100" class-name="cell-td-td" >
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
                        <el-button @click="todeploy(scope.row)" v-if='scope.row.deployTag' type="text">
                                Go to deploy
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</div>

</template>

<script>
import { mapGetters } from 'vuex'
import { getOverViewList } from '@/api/deploy'
import moment from 'moment'
import tooltip from '@/components/Tooltip'
// 国际化
const local = {
    zh: {
        'Index': '序号',
        'Site Name': '站点名称',
        'Party ID': '站点ID',
        'Role': '站点角色',
        'Installed items': '安装项',
        'Version': '版本',
        'Deployment': '部署方式',
        'Installed Time': '安装时间',
        'Upgrade Time': '更新时间',
        'Service Status': '服务状态',
        'Action': '操作'
    },
    en: {
        'Index': 'Index',
        'Site Name': 'Site Name',
        'Party ID': 'Party ID',
        'Role': 'Role',
        'Installed items': 'Installed items',
        'Version': 'Version',
        'Deployment': 'Deployment',
        'Installed Time': 'Installed time',
        'Upgrade Time': 'Upgrade Time',
        'Service Status': 'Service Status',
        'Action': 'Action'

    }
}
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
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
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
                    partyId: row.partyId,
                    siteName: row.siteName,
                    deployType: row.installInfo[0].deployType.code
                }
            })
        },
        todeploy(row) {
            this.$router.push({
                name: 'auto',
                query: {
                    partyId: row.partyId,
                    siteName: row.siteName
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
