<template>
  <div class="sitemanage">
    <div class="site-item">
        <div class="site-item-text">
            <span class="title">{{$t('m.sitemanage.federatedOrganization')}}</span>
            <tooltip  class="text" :width="'120px'" :content="`${myInstitution.federatedOrganization || ''}`" :placement="'top'"/>
        </div>
        <div class="site-item-text">
            <span class="title">{{$t('m.common.institution',{type:'i'})}}</span>
            <tooltip   class="text" :width="'120px'" :content="myInstitution.institutions" :placement="'top'"/>
        </div>
        <div class="site-item-text">
            <span class="title">{{$t('m.sitemanage.organizationSize')}}</span>
            <div class="text"> {{myInstitution.size}} </div>
        </div>
        <div class="site-item-text">
            <span class="title">{{$t('m.sitemanage.creationTime')}}</span>
            <div style="color:#4E5766;">{{myInstitution.createTime | dateFormat}}</div>
        </div>
        <div class="site-title">
            <span class="site-tiem-view" @click="togetexchangeList">{{$t('m.sitemanage.viewExchange')}}</span>
        </div>
    </div>
    <div class="sitemanage-box">
        <div class="add-site">
            <span v-if="role.roleName==='Admin'">
                <el-tooltip effect="dark" :content="$t('m.sitemanage.addNewSiteJoinOrganization')" placement="top">
                    <div  class="add" @click="toAddSite">
                        <img src="@/assets/add_site.png">
                        <span>{{$t('m.common.add')}}</span>
                    </div>
                </el-tooltip>
                <span v-if='siteState && myInstitution.joinedSites > 0'>
                    <div class="app" v-if="applyStatus === 1">
                        <span>{{$t('m.sitemanage.alreadyApply')}}
                            <span v-for="(item, index) in applyStatusList" :key="index">
                                <span v-if="index===applyStatusList.length-1">{{item}}</span>
                                <span v-else> {{item}},</span>
                            </span>
                        </span>

                        <span style="margin-left: 10px">{{$t('m.sitemanage.waitApproval')}}</span>
                    </div>
                    <div class="apply"  v-if='applyStatus === 3' >
                        <span  class="apply-click" v-if='showapplyBtn' @click="showApply">
                            {{$t('m.sitemanage.applySites')}}
                        </span>
                        <span v-else class="apply-click"  style="color: #848C99;cursor:not-allowed">{{$t('m.sitemanage.applySites')}}</span>
                        <el-popover
                            placement="bottom"
                            :visible-arrow="false"
                            width="500"
                            :offset="-340"
                            popper-class="site-history"
                            trigger="click">
                            <div class="content">
                                <div class="title">
                                    <div class="title-time">{{$t('m.common.time')}}</div>
                                    <div class="title-history">{{$t('m.common.history')}}</div>
                                </div>
                                <div class="content-box">
                                    <div v-for="(item, index) in siteHistoryList" :key="index" >
                                        <div class="title-time">{{item.applyTime | dateFormat}}</div>
                                        <div class="title-history">
                                            <span v-if="item.agree.length>0">
                                                {{$t('m.sitemanage.setStatusApplication',{type:$t('m.common.agreed'),who:item.institutions})}}
                                                <span v-for="(elm, ind) in item.agree" :key="ind">
                                                    <span v-if="ind===item.agree.length-1">{{elm}}</span>
                                                    <span v-else>{{elm}},</span>
                                                </span>
                                                <span>{{$t('m.sitemanage.applications')}}</span>
                                            </span>
                                            <span v-if="item.reject.length>0">
                                                <span v-if="item.agree.length>0"> ,{{$t('m.common.and')}} </span>
                                                {{$t('m.sitemanage.setStatusApplication',{type:$t('m.common.reject'),who:item.institutions})}}
                                                <span v-for="(elm, ind) in item.reject" :key="ind">
                                                    <span v-if="ind===item.reject.length-1">{{elm}}</span>
                                                    <span v-else>{{elm}},</span>
                                                </span>
                                                <span>{{$t('m.sitemanage.applications')}}</span>
                                            </span>
                                            <span v-if="item.cancel.length>0">
                                                <span v-if="item.agree.length>0 || item.reject.length>0"> ,{{$t('m.common.and')}} </span>
                                                {{$t('m.sitemanage.setStatusApplication',{type:$t('m.common.cancel'),who:item.institutions})}}
                                                <span v-for="(elm, ind) in item.cancel" :key="ind">
                                                    <span v-if="ind===item.cancel.length-1">{{elm}}</span>
                                                    <span v-else>{{elm}},</span>
                                                </span>
                                                <span>{{$t('m.sitemanage.applications')}}</span>
                                            </span>
                                            <!-- <span v-if="item.apply.length>0">
                                                <span v-if="item.agree.length>0 || item.reject.length>0 || item.cancel.length>0"> ,{{$t('m.common.and')}} </span>
                                                {{$t('m.sitemanage.applyApplication',{who:item.institutions})}}
                                                <span v-for="(elm, ind) in item.apply" :key="ind">
                                                    <span v-if="ind===item.apply.length-1">{{elm}}</span>
                                                    <span v-else>{{elm}},</span>
                                                </span>
                                                <span>{{$t('m.sitemanage.siteOf')}}</span>
                                            </span> -->
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
                <div class="add" style="cursor:not-allowed">
                    <img src="@/assets/add_site.png">
                    <span>{{$t('m.common.add')}}</span>
                </div>
                <div v-if='siteState' class="apply" style="cursor:not-allowed;color:#c8c9cc" >
                    <span >{{$t('m.sitemanage.applySites')}} </span>
                    <img slot="reference" class="tickets" src="@/assets/history.png" style="cursor:not-allowed" alt="" >
                </div>
            </span>
        </div>
        <!-- 我的站点申请 -->
        <div class="site-name">
            <div class="name-left">
                <span class="institution-title">{{$t('m.sitemanage.myInstitution')}}</span>
                <span class="institution-text">
                    <tooltip :width="'170px'" :content="myInstitution.fateManagerInstitutions" :placement="'top'"/>
                </span>
                <span class="size-title">{{$t('m.sitemanage.size')}}</span>
                <span class="size-num">{{myInstitution.joinedSites }}</span>
            </div>
            <div class="name-right">
                <span class="right-text">{{$t('m.sitemanage.hasBeenApplied')}}</span>
                <el-tooltip v-if="viewContent.totalLength>0" popper-class="view-tip" effect="light"  placement="bottom">
                    <span slot="content" >
                        <div class="viewcontent" v-if="viewContent.scenarioType !== '1'">
                            <p class="content-title">{{$t('m.sitemanage.siteViewed',{type:$t('m.common.host',{type:'H'})})}}</p>
                            <div class="sites-list">{{hostListText}}</div>
                        </div>
                        <div class="viewcontent" style="margin-top:25px" v-if="viewContent.scenarioType !== '1'">
                            <p class="content-title">{{$t('m.sitemanage.siteViewed',{type:$t('m.common.guest',{type:'G'})})}}</p>
                            <div class="sites-list">{{guestListText}}</div>
                        </div>
                        <div class="viewcontent" v-if="viewContent.scenarioType === '1'">
                            <div class="sites-list">{{allListText}}</div>
                        </div>
                        <span v-for="(item, index) in viewContent.institutions" :key="index">
                            <span v-if="index===viewContent.institutions.length-1">{{item}}</span>
                            <span v-else> {{item}},</span>
                        </span>
                    </span>
                    <span class="right-num">{{viewContent.totalLength}}</span>
                </el-tooltip>
                <span v-else class="right-num">{{0}}</span>
                <span class="right-title">{{$t('m.sitemanage.FATEManagers')}}</span>
                <span class="refresh">
                    <i @click="getList" class="el-icon-refresh-right"></i>
                </span>
            </div>
        </div>
        <el-table
            border
            :data="myInstitution.siteList"
            class="site-table"
            header-row-class-name="tableHead"
            header-cell-class-name="tableHeadCell"
            cell-class-name="tableCell"
            tooltip-effect="light">
            <el-table-column prop="siteName" :label="$t('m.sitemanage.siteName')"  show-overflow-tooltip>
                <template slot-scope="scope">
                    <span @click="toSietInfo(scope.row)" style="color:#217AD9;cursor:pointer;">{{scope.row.siteName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="status.desc" :label="$t('m.common.status')" >
                <template slot-scope="scope">
                    <span>{{scope.row.status | getSiteStatus}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="serviceStatus.desc" :label="$t('m.sitemanage.serviceStatus')" >
                <template slot-scope="scope">
                    <span>{{scope.row.serviceStatus | getServiceStatus}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="role.desc" :label="$t('m.common.role')" >
                <template slot-scope="scope">
                    <span>{{scope.row.role | getSiteType}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="partyId" :label="$t('m.common.partyID')" ></el-table-column>
            <el-table-column prop="activationTime" :label="$t('m.sitemanage.activationTime')">
                <template slot-scope="scope">
                    <span>{{scope.row.activationTime | dateFormat}}</span>
                </template>
            </el-table-column>
        </el-table>
        <!-- 申请查看其它站点 -->
        <span v-if='siteState'>
            <span v-for="(item, index) in otherSiteList" :key="index" >
                <div class="site-name" style="margin-top: 12px;">
                    <div class="name-left">
                        <span class="institution-title">{{$t('m.sitemanage.otherInstitution')}}</span>
                        <span class="institution-text">
                            <tooltip :width="'170px'" :content="item.fateManagerInstitutions" :placement="'top'"/>
                        </span>
                        <span class="size-title">{{$t('m.sitemanage.size')}}</span>
                        <span class="size-num">{{item.size}}</span>
                    </div>
                </div>
                <el-table
                    :data="item.siteList"
                    class="site-table"
                    border
                    header-row-class-name="tableHead"
                    header-cell-class-name="tableHeadCell"
                    cell-class-name="tableCell"
                    tooltip-effect="light">
                    <el-table-column prop="siteName" :label="$t('m.sitemanage.siteName')" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="status.desc" :label="$t('m.common.status')">
                        <template slot-scope="scope">
                            <span>{{scope.row.status | getSiteStatus}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="serviceStatus.desc" :label="$t('m.sitemanage.serviceStatus')">
                        <template slot-scope="scope">
                            <span>{{scope.row.serviceStatus | getServiceStatus}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="role.desc" :label="$t('m.common.role')">
                        <template slot-scope="scope">
                            <span>{{scope.row.role | getSiteType}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="partyId" :label="$t('m.common.partyID')"></el-table-column>
                    <el-table-column prop="activationTime" :label="$t('m.sitemanage.activationTime')">
                        <template slot-scope="scope">
                            <span>{{scope.row.activationTime | dateFormat}}</span>
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
                {{$t('m.sitemanage.apply')}}
            </div>
            <div class="line-text-one">
                {{$t('m.sitemanage.selectTips')}}
            </div>
            <div class="line-text-gray">{{$t('m.sitemanage.applyOther')}}</div>
            <div class="dialog-main">
                <el-checkbox-group v-model="checkList">
                    <div v-for="(item, index) in checkboxList" :key="index">
                        <el-checkbox :disabled="item.disable" :label="item.institutions"></el-checkbox>
                    </div>
                </el-checkbox-group>
            </div>
            <div class="dialog-foot">
                <el-button type="primary" @click="toApply">{{$t('m.common.OK')}}</el-button>
                <el-button type="info" @click="applydialog=false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </div>
    </el-dialog>
     <!-- 审批不通过查看弹框 -->
    <el-dialog :visible.sync="applynotpass" class="apply-not-pass" width="700px">
        <span v-if='agreedList.length>0'>
            <div class="line-text-one">{{$t('m.sitemanage.setStatusApplication',{type:$t('m.common.agreed')})}}</div>
            <div class="line-text-one" >
                <span class="span" v-for="(item, index) in agreedList" :key="index">
                    <span v-if="index===agreedList.length-1">{{item}}</span>
                    <span v-else>{{item}},</span>
                </span>
            </div>
            {{$t('m.sitemanage.applications')}}
        </span>

         <span v-if='rejectList.length>0'>
            <div class="line-text-one">
                <span v-if='agreedList.length>0'>{{$t('m.common.and')}}</span>
                {{$t('m.sitemanage.setStatusApplication',{type:$t('m.common.reject')})}}
            </div>
            <div class="line-text-one" >
                <span class="span" v-for="(item, index) in rejectList" :key="index">
                    <span v-if="index===rejectList.length-1">{{item}}</span>
                    <span v-else>{{item}},</span>
                </span>
            </div>
            {{$t('m.sitemanage.applications')}}
        </span>
        <span v-if='cancelList.length>0'>
            <div class="line-text-one">
                <span v-if='rejectList.length>0'>{{$t('m.common.and')}}</span>
                {{$t('m.sitemanage.setStatusApplication',{type:$t('m.common.cancel')})}}
            </div>
            <div class="line-text-one" >
                <span class="span" v-for="(item, index) in cancelList" :key="index">
                    <span v-if="index===cancelList.length-1">{{item}}</span>
                    <span v-else>{{item}},</span>
                </span>
            </div>
            {{$t('m.sitemanage.applications')}}
        </span>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="toapplynotpass">{{$t('m.common.OK')}}</el-button>
        </div>
    </el-dialog>
      <!-- Exchange弹框 -->
    <el-dialog :visible.sync="exchangedialog" class="exchange-dialog" width="800px" >
        <div class="vip-box">
            <div class="dialog-title">{{$t('m.sitemanage.exchangeInfo')}}</div>
            <el-table
                :data="exchangeList.exchangeVip"
                class="site-table"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="250"
                tooltip-effect="light">
                <el-table-column type="index" :label="$t('m.common.index')" width="250"></el-table-column>
                <el-table-column prop="exchangeName" label="Exchange" show-overflow-tooltip></el-table-column>
                <el-table-column prop="vip" label="VIP"></el-table-column>
            </el-table>
        </div>
    </el-dialog>
    <!-- 注册弹框 -->
    <siteregister ref="siteregister"/>
  </div>
</template>

<script>

import { mapGetters } from 'vuex'
import siteregister from './siteregister'
import tooltip from '@/components/Tooltip'
import { getInstitutions, applysite, mySiteList, otherSitList, applyState, readState, fatemanagerList, applyHistory, getexchangeList, getNoticeapplysite } from '@/api/home'

export default {
    name: 'homeview',
    components: {
        tooltip,
        siteregister
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
            viewContent: {
                allInstuList: [],
                guestInstuList: [],
                hostInstuList: []
            }, // 被别人查看的内容
            applyed: [], // 已经申请
            applyStatus: 3, // 默认审批通过
            applyStatusList: [],
            agreedList: [], // 同意列表
            rejectList: [], // 拒绝列表
            cancelList: [], // 取消列表
            siteHistoryList: [], // 历史审批记录
            exchangedialog: false,
            exchangeList: { },
            chartTimer: null // 轮询定时器

        }
    },
    computed: {
        ...mapGetters(['role', 'siteState']),
        hostListText() {
            return this.viewContent.hostInstuList.length > 0 ? this.viewContent.hostInstuList.join(',') : this.$t('m.common.noData')
        },
        guestListText() {
            return this.viewContent.guestInstuList.length > 0 ? this.viewContent.guestInstuList.join(',') : this.$t('m.common.noData')
        },
        allListText() {
            return this.viewContent.allInstuList.length > 0 ? this.viewContent.allInstuList.join(',') : this.$t('m.common.noData')
        }
    },
    created() {
        this.getList()
        this.getNoticeapplysite()
        this.getapplyList()
    },

    mounted() {

    },
    beforeDestroy() {
        this.clearPollingTimer()
        clearInterval(this.clusterTimer)
    },
    methods: {
        getList() {
            // 我的站点
            mySiteList().then(res => {
                if (res.data) {
                    this.myInstitution = res.data[0]
                    this.myInstitution.joinedSites = 0
                    this.myInstitution.siteList = res.data[0].siteList && res.data[0].siteList.map(item => {
                        if (item.status.code === 2) {
                            this.myInstitution.joinedSites += 1
                        }
                        item.federatedId = res.data[0].federatedId
                        return item
                    })
                }
            })
            // 其他人申请列表
            this.otherApplys()
            this.setPollingTimer(() => {
                this.otherApplys()
            })
            // 查看他人站点
            otherSitList().then(res => {
                this.otherSiteList = []
                res.data && res.data.forEach((item, index) => {
                    this.otherSiteList.push(item)
                })
            })
            // 站点状态列表
            applyState().then(res => {
                this.applyStatusList = res.data || []
                // mock
                // this.applyStatusList = ['test_wzh', 'stu1']
                console.log(this.applyStatusList, 'applyStatusList')
                if (this.applyStatusList.length > 0) {
                    this.applyStatus = 1 // 审批中
                } else {
                    this.applyStatus = 3 // 无申请记录
                }
                console.log(this.applyStatus, 'applyStatus')
            })
        },
        otherApplys() {
            fatemanagerList().then(res => {
                // res.data = []
                let data = res.data.institutions || []
                let scenarioType = data.scenarioType
                // mock
                // let scenarioType = '1'
                // data.guestList = ['WZH-HOST', 'manager-1']
                // data.hostList = ['WZH-HOST', 'manager-1']
                this.viewContent.scenarioType = scenarioType
                if (scenarioType === '1') { // 混合
                    this.viewContent.allInstuList = (data.all && data.all.map(item => item)) || []
                } else if (scenarioType === '2' || scenarioType === '3') {
                    this.viewContent.guestInstuList = (data.guestList && data.guestList.map(item => item)) || []
                    this.viewContent.hostInstuList = (data.hostList && data.hostList.map(item => item)) || []
                }
                // this.viewContent.totalLength = this.viewContent.allInstuList.length + this.viewContent.guestInstuList.length + this.viewContent.hostInstuList.length
                this.viewContent.totalLength = data.total

                console.log(this.viewContent)
            })
        },
        // 机构审批状态查询
        getNoticeapplysite() {
            getNoticeapplysite().then(res => {
                // res.data = [{ 'institutions': 'A', 'status': 2 }, { 'institutions': 'B', 'status': 3 }, { 'institutions': 'C', 'status': 4 }]
                if (res.data.length > 0) {
                    res.data.forEach(item => {
                        if (item.status === 2) {
                            this.agreedList.push(item.institutions)
                        } else if (item.status === 3) {
                            this.rejectList.push(item.institutions)
                        } else if (item.status === 4) {
                            this.cancelList.push(item.institutions)
                        }
                    })
                    this.applynotpass = true
                }
            })
        },
        // 获取exchangeList 列表
        togetexchangeList() {
            this.exchangedialog = true
            getexchangeList().then(res => {
                this.exchangeList = res.data || []
            })
        },
        // 添加站点
        toAddSite() {
            // 前往注册
            this.$refs['siteregister'].registerVisible = true
            this.$refs['siteregister'].inputform = { inputUrl: '' }
            // this.$router.push({ path: '/welcome/register' })
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
                this.showapplyBtn = res.data && res.data.length > 0
            })
        },
        // 显示申请弹框
        showApply() {
            this.checkList = []
            // 显示已经加入的Institution
            // this.otherSiteList && this.otherSiteList.forEach((item) => {
            //     this.checkList.push(item.fateManagerInstitutions)
            // })
            getInstitutions().then(res => {
                let applyedArr = []
                this.checkboxList = res.data.map(item => {
                    // item.status.code = 1
                    if (item.status.code === 2) {
                        item.disable = true
                        applyedArr.push(item.institutions)
                    }
                    return item
                })
                this.applyed = this.applyed.concat(applyedArr)
                console.log(this.applyed, 'applyed')
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
                res.data && res.data.list && res.data.list.forEach(item => {
                    console.log(item, 'items')
                    let obj = {}
                    obj.agree = []
                    obj.reject = []
                    obj.cancel = []
                    obj.apply = []
                    obj.applyTime = item.createTime
                    obj.institutions = item.institutions
                    item.authorityApplyReceivers.forEach(elm => {
                        if (elm.status === 2) {
                            obj.agree.push(elm.authorityInstitutions)
                        } else if (elm.status === 3) {
                            obj.reject.push(elm.authorityInstitutions)
                        } else if (elm.status === 4) {
                            obj.cancel.push(elm.authorityInstitutions)
                        }
                        //  else if (elm.status === 1) {
                        //     obj.apply.push(elm.authorityInstitutions)
                        // }
                    })
                    this.siteHistoryList.push(obj)
                })
            })
        },
        // 轮询定时器
        setPollingTimer(cb, interval) {
            clearInterval(this.chartTimer)
            this.chartTimer = setInterval(cb, interval || 5000)
        },
        clearPollingTimer() {
            clearInterval(this.chartTimer)
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
        width: 31%;
        display:inline-block;
        margin: 12px 0;
        margin-right: 10px;
    }
    .title-history{
        vertical-align: top;
        width: 65%;
        display:inline-block;
        margin: 12px 0;
    }
}
.view-tip{
    border: 1px solid #999 !important;
    width: 250px;
    .viewcontent{
        .content-title{
            margin: 0 0 10px 0;
            font-size: 16px;
            color: #999;
        }
        .sites-list{
            font-size: 14px;
            font-weight: 600;
            color: #666;
        }
    }
}

</style>
