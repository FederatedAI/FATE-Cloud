<template>
  <div class="sitemanage">
    <div class="site-item">
        <div class="site-title">
            <div class="site-item-title">Federated Organization</div>
            <div class="site-item-title">Institution</div>
            <div class="site-item-title">Organization Size</div>
            <div class="site-item-title">Creation Time</div>
        </div>
        <div class="site-title">
            <div class="site-item-text">
                <tooltip :width="'282px'" :content="myInstitution.federatedOrganization" :placement="'top'"/>
            </div>
            <div class="site-item-text">
                <tooltip :width="'282px'" :content="myInstitution.institutions" :placement="'top'"/>
            </div>
            <div class="site-item-text">{{myInstitution.size}}</div>
            <div class="site-item-text">{{myInstitution.createTime | dateFormat}}</div>
        </div>
    </div>
    <div class="sitemanage-box">
        <div class="add-site">
            <span v-if="role.roleName==='Admin'">
                <el-tooltip effect="dark" content="Add a new site to join a federated organization" placement="right">
                    <div  class="add" @click="toAddSite">
                        <span>Add</span>
                    </div>
                </el-tooltip>
                <span v-if='siteState'>
                    <div class="app" v-if="applyStatus === 1">
                        <span>You have applied to view the fate manager sites of
                            <span v-for="(item, index) in applyStatusList" :key="index">
                                <span v-if="index===applyStatusList.length-1">{{item.desc}}</span>
                                <span v-else> {{item.desc}},</span>
                            </span>
                        </span>

                        <span style="margin-left: 10px">Please wait for the approval of Cloud Manager…</span>
                    </div>
                    <div class="apply"  v-if='applyStatus === 3' >
                        <span  class="apply-click" v-if='showapplyBtn' @click="showApply">
                            Apply to view other FATE Manager sites.
                        </span>
                        <span v-else class="apply-click"  style="color: #848C99;cursor:not-allowed">Apply to view other FATE Manager sites.</span>
                        <el-popover
                            placement="bottom"
                            :visible-arrow="false"
                            width="700"
                            :offset="-340"
                            popper-class="site-history"
                            trigger="click">
                            <div class="content">
                                <div class="title">
                                    <div class="title-time">Time</div>
                                    <div class="title-history">History</div>
                                </div>
                                <div class="content-box">
                                    <div v-for="(item, index) in siteHistoryList" :key="index" >
                                        <div class="title-time">{{item.applyTime | dateFormat}}</div>
                                        <div class="title-history">
                                            <span v-if="item.agree.length>0">
                                                Cloud Manager agreed your application to view the fate manager sites of
                                                <span v-for="(elm, ind) in item.agree" :key="ind">
                                                    <span v-if="ind===item.agree.length-1">{{elm}}</span>
                                                    <span v-else>{{elm}},</span>
                                                </span>
                                            </span>
                                            <span v-if="item.reject.length>0">
                                                <span v-if="item.agree.length>0"> ,and cloud</span>
                                                <span v-else> Cloud</span>
                                                Manager reject your application to view the fate manager sites of
                                                <span v-for="(elm, ind) in item.reject" :key="ind">
                                                    <span v-if="ind===item.reject.length-1">{{elm}}</span>
                                                    <span v-else>{{elm}},</span>
                                                </span>
                                            </span>
                                            <span v-if="item.cancel.length>0">
                                                <span v-if="item.agree.length>0 || item.reject.length>0"> ,and cloud</span>
                                                <span v-else> Cloud</span>
                                                Manager cancel your application to view the fate manager sites of
                                                <span v-for="(elm, ind) in item.cancel" :key="ind">
                                                    <span v-if="ind===item.cancel.length-1">{{elm}}</span>
                                                    <span v-else>{{elm}},</span>
                                                </span>
                                            </span>

                                        </div>
                                    </div>
                                </div>
                            </div>
                            <img slot="reference" class="tickets" src="@/assets/historyclick.png" @click="gethistory" alt="" >
                        </el-popover>
                    </div>
                </span>
            </span>
            <span v-else>
                <div class="add" style="cursor:not-allowed;background-color:#c8c9cc">
                    <span>Add</span>
                </div>
                <div v-if='siteState' class="apply" style="cursor:not-allowed;color:#c8c9cc" >
                    <span >Apply to view other FATE Manager sites. </span>
                    <img slot="reference" class="tickets" src="@/assets/history.png" style="cursor:not-allowed" alt="" >
                </div>
            </span>
        </div>
        <!-- 我的站点申请 -->
        <div class="site-name">
            <div class="name-left">
                <span class="institution-title">My Institution</span>
                <span class="institution-text">
                    <tooltip :width="'170px'" :content="myInstitution.fateManagerInstitutions" :placement="'top'"/>
                </span>
                <span class="size-title">Size</span>
                <span class="size-num">{{myInstitution.joinedSites }}</span>
            </div>
            <div class="name-right">
                <span class="right-text">My site has been applied to view by</span>
                <el-tooltip v-if="viewContent.institutions&&viewContent.institutions.length>0" effect="dark" placement="bottom">
                    <span slot="content">
                        <span v-for="(item, index) in viewContent.institutions" :key="index">
                            <span v-if="index===viewContent.institutions.length-1">{{item}}</span>
                            <span v-else> {{item}},</span>
                        </span>
                    </span>
                    <span class="right-num">{{viewContent.institutions.length}}</span>
                </el-tooltip>
                <span v-else class="right-num">{{0}}</span>
                <span class="right-title">FATE Managers</span>
                <span class="refresh">
                    <i @click="getList" class="el-icon-refresh-right"></i>
                </span>
            </div>
        </div>
        <el-table
            :data="myInstitution.siteList"
            class="site-table"
            header-row-class-name="tableHead"
            header-cell-class-name="tableHeadCell"
            cell-class-name="tableCell"
            tooltip-effect="light">
            <el-table-column prop="siteName" label="Site Name" show-overflow-tooltip>
                <template slot-scope="scope">
                    <span @click="toSietInfo(scope.row)" style="color:#217AD9;cursor:pointer;">{{scope.row.siteName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="status.desc" label="Status">
            </el-table-column>
            <el-table-column prop="role.desc" label="Role"></el-table-column>
            <el-table-column prop="partyId" label="Party ID"></el-table-column>
            <el-table-column prop="acativationTime" label="Activation Time">
                <template slot-scope="scope">
                    <span>{{scope.row.acativationTime | dateFormat}}</span>
                </template>
            </el-table-column>
        </el-table>
        <!-- 申请查看其它站点 -->
        <span v-if='siteState'>
            <span v-for="(item, index) in otherSiteList" :key="index">
                <div class="site-name">
                    <div class="name-left">
                        <span class="institution-title">Institution</span>
                        <span class="institution-text">
                            <tooltip :width="'170px'" :content="item.fateManagerInstitutions" :placement="'top'"/>
                        </span>
                        <span class="size-title">Size</span>
                        <span class="size-num">{{item.size}}</span>
                    </div>
                </div>
                <el-table
                    :data="item.siteList"
                    class="site-table"
                    header-row-class-name="tableHead"
                    header-cell-class-name="tableHeadCell"
                    cell-class-name="tableCell"
                    tooltip-effect="light">
                    <el-table-column prop="siteName" label="Site Name" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="status.desc" label="Status"></el-table-column>
                    <el-table-column prop="role.desc" label="Role"></el-table-column>
                    <el-table-column prop="partyId" label="Party ID"></el-table-column>
                    <el-table-column prop="acativationTime" label="Activation Time">
                        <template slot-scope="scope">
                            <span>{{scope.row.acativationTime | dateFormat}}</span>
                        </template>
                    </el-table-column>
                </el-table>
            </span>
        </span>
    </div>
      <!-- 添加弹框 -->
    <el-dialog :visible.sync="applydialog" class="apply-dialog" width="700px" :close-on-click-modal="false" :close-on-press-escape="false">
        <div class="dialog-box">
            <div class="dialog-title">
                Apply
            </div>
            <div class="line-text-one">
                Please select the FATE Manager you want to view.
            </div>
            <div class="line-text-one">
                After submitting the application, please wait for the Cloud Manager’s approval.
            </div>
            <div class="dialog-main">
                <el-checkbox-group v-model="checkList">
                    <div v-for="(item, index) in checkboxList" :key="index">
                        <el-checkbox :disabled="item.disable" :label="item.institutions"></el-checkbox>
                    </div>
                </el-checkbox-group>
            </div>
            <div class="dialog-foot">
                <el-button type="primary" :disabled="(checkList.length-applyed.length)===0" @click="toApply">OK</el-button>
                <el-button type="info" @click="applydialog=false">Cancel</el-button>
            </div>
        </div>
    </el-dialog>
     <!-- 审批不通过查看弹框 -->
    <el-dialog :visible.sync="applynotpass" class="apply-not-pass" width="700px">
        <span v-if='agreedList.length>0'>
            <div class="line-text-one">Cloud Manager agreed  your application</div>
            <div class="line-text-one">to view the fate manager sites of </div>
            <div class="line-text-one" >
                <span class="span" v-for="(item, index) in agreedList" :key="index">
                    <span v-if="index===agreedList.length-1">{{item}}</span>
                    <span v-else>{{item}},</span>
                </span>
            </div>
        </span>

         <span v-if='rejectList.length>0'>
            <div class="line-text-one">
                <span v-if='agreedList.length>0'>and cloud</span>
                <span v-else >Cloud</span>
                Manager reject  your application
            </div>
            <div class="line-text-one">to view the fate manager sites of </div>
            <div class="line-text-one" >
                <span class="span" v-for="(item, index) in rejectList" :key="index">
                    <span v-if="index===rejectList.length-1">{{item}}</span>
                    <span v-else>{{item}},</span>
                </span>
            </div>
        </span>
        <span v-if='cancelList.length>0'>
            <div class="line-text-one">
                <span v-if='rejectList.length>0'>and cloud</span>
                <span v-else >Cloud</span>
                Manager cancel  your application
            </div>
            <div class="line-text-one">to view the fate manager sites of </div>
            <div class="line-text-one" >
                <span class="span" v-for="(item, index) in rejectList" :key="index">
                    <span v-if="index===rejectList.length-1">{{item}}</span>
                    <span v-else>{{item}},</span>
                </span>
            </div>
        </span>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="toapplynotpass">OK</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>

import moment from 'moment'
import { mapGetters } from 'vuex'
import tooltip from '@/components/Tooltip'
import { getInstitutions, applysite, mySiteList, otherSitList, applyState, readState, fatemanagerList, applyHistory } from '@/api/home'
export default {
    name: 'homeview',
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    components: {
        tooltip
    },
    data() {
        return {
            checkList: [],
            checkboxList: [],
            showapplyBtn: false,
            applydialog: false, // 添加弹框
            applynotpass: false, // 审核不通过弹框
            applynotList: [], // 审核不通过弹框列表
            myInstitution: [],
            otherSiteList: [],
            viewContent: {}, // 被别人查看的内容
            applyed: [], // 已经申请
            applyStatus: 3, // 默认审批通过
            applyStatusList: [],
            agreedList: [], // 同意列表
            rejectList: [], // 拒绝列表
            cancelList: [], // 取消列表
            siteHistoryList: []// 历史审批记录
        }
    },
    computed: {
        ...mapGetters(['role', 'siteState'])

    },
    created() {
        this.$nextTick(() => {
            this.getList()
            this.getapplyList()
        })
    },

    mounted() {

    },
    methods: {
        getList() {
            // 我的站点
            mySiteList().then(res => {
                if (res.data) {
                    this.myInstitution = res.data[0]
                    this.myInstitution.joinedSites = 0
                    this.myInstitution.siteList = res.data[0].siteList.map(item => {
                        if (item.status.code === 2) {
                            this.myInstitution.joinedSites += 1
                        }
                        item.federatedId = res.data[0].federatedId
                        return item
                    })
                }
            })
            // 其他人申请列表
            fatemanagerList().then(res => {
                this.viewContent = res.data
            })
            // 查看他人站点
            otherSitList().then(res => {
                this.otherSiteList = []
                res.data && res.data.forEach((item, index) => {
                    this.otherSiteList.push(item)
                })
            })
            // 审批状态列表
            applyState().then(res => {
                this.applyStatusList = res.data || []
                if (res.data && [...res.data].every(item => item.code === 1)) {
                    this.applyStatus = 1 // 审批中
                } else if (res.data && [...res.data].some(item => item.code === 2 || item.code === 3 || item.code === 4)) {
                    this.applyStatus = 2 // code === 2 审批通过、code === 3拒绝或code === 4被取消
                    this.applynotpass = true
                } else {
                    this.applyStatus = 3 // 无申请记录
                }
                res.data && res.data.forEach(item => {
                    if (item.code === 2) {
                        this.agreedList.push(item.desc)
                    } else if (item.code === 3) {
                        this.rejectList.push(item.desc)
                    } else if (item.code === 4) {
                        this.cancelList.push(item.desc)
                    }
                })
            })
        },
        // 添加站点
        toAddSite() {
            // 前往注册
            this.$router.push({ path: '/welcome/register' })
        },
        toSietInfo(row) {
            this.$router.push({
                name: 'siteinfo',
                path: '/siteinfo/index',
                query: { federatedId: row.federatedId, partyId: row.partyId }
            })
        },
        // 获取申请弹框列表
        getapplyList() {
            getInstitutions().then(res => {
                this.showapplyBtn = res.data.length > 0
            })
        },
        // 显示申请弹框
        showApply() {
            this.checkList = []
            // 显示已经加入的Institution
            this.otherSiteList && this.otherSiteList.forEach((item) => {
                this.checkList.push(item.fateManagerInstitutions)
                this.applyed = this.checkList
            })
            getInstitutions().then(res => {
                this.checkboxList = res.data.map(item => {
                    this.checkList.forEach(ele => {
                        if (ele === item.institutions) {
                            item.disable = true
                        }
                    })
                    return item
                })
                this.applydialog = true
            })
        },

        // 确认添加
        toApply() {
            let authorityInstitutions = this.checkList.filter((v) => { return this.applyed.indexOf(v) === -1 })
            let data = {
                authorityInstitutions
            }
            applysite(data).then(res => {
                this.applydialog = false
                this.getList()
            })
        },
        // 查看申请不通过
        toapplynotpass() {
            // 审批状态
            readState().then(res => {
                this.applyStatus = 2
                this.applynotpass = false
                this.getList()
            })
        },
        // 获取历史记录
        gethistory() {
            this.siteHistoryList = []
            applyHistory().then(res => {
                res.data && res.data.forEach(item => {
                    let obj = {}
                    obj.agree = []
                    obj.reject = []
                    obj.cancel = []
                    obj.applyTime = item.applyTime
                    JSON.parse(item.content).forEach(elm => {
                        if (elm.code === 2) {
                            obj.agree.push(elm.desc)
                        } else if (elm.code === 3) {
                            obj.reject.push(elm.desc)
                        } else if (elm.code === 4) {
                            obj.cancel.push(elm.desc)
                        }
                    })
                    this.siteHistoryList.push(obj)
                })
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/sitemanage.scss';
.site-history{
    // margin-top: 0px !important;
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
        max-height: 500px;
        overflow: auto;
        .title-time{
            color: #848C99
        }
        .title-history{
            color: #4E5766;
            .version{
                color: #217AD9;
            }
        }
    }
    .title-time{
        width: 28%;
        display:inline-block;
        margin: 12px 0;
    }
    .title-history{
        vertical-align: top;
        width: 70%;
        display:inline-block;
        margin: 12px 0;
    }

}
</style>
