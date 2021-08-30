<template>
  <div class="site-box">
    <div class="site" >
        <div class="site-radio">
            <el-radio-group class="radio" v-model="radioInstitutions" @change="changeRadio">
                <el-radio-button label="In Use">{{$t('m.site.In Use')}}</el-radio-button>
                <el-radio-button label="Historic Uses">{{$t('m.site.Historic Uses')}}</el-radio-button>
            </el-radio-group>
        </div>
        <div class="site-header">
            <el-button v-if="radioInstitutions==='In Use'" class="add" type="text" @click="addsite">
                <img src="@/assets/add_site.png">
                <span>{{$t('m.common.add')}}</span>
            </el-button>
            <el-input class="input input-placeholder" v-model.trim="data.condition" :placeholder="$t('m.site.searchSiteOrParty')" clearable>
            </el-input>
            <el-select class="sel-role input-placeholder" v-model="data.institutionsArray" filterable multiple collapse-tags :placeholder="$t('m.common.institution')">
                <el-option
                    v-for="(item,index) in institutionsSelectList"
                    :key="index"
                    :label="item.label"
                    :value="item.value">
                </el-option>
            </el-select>
            <el-button class="go" type="text" @click="toFold" v-if="institutionsItemList.length > 0">
                <span v-if='activeName.length>0'>{{$t('m.common.foldAll')}}</span>
                <span v-else >{{$t('m.common.unfoldAll')}}</span>
            </el-button>
            <el-button class="go" @click="toSearch" type="primary">{{$t('m.common.go')}}</el-button>
        </div>
        <div class="collapse" >
            <el-collapse v-model="activeName" v-for="(itm, index) in institutionsItemList" :key="index"  @change="changeCollapse">
                <el-collapse-item  :name="itm.institutions">
                    <template slot="title">
                        <span class="ins">{{itm.institutions}}</span>
                        <img :key="index" v-if="itm.tooltip && siteState" src="@/assets/msg.png" @click.stop="toshowins(itm.institutions)" alt="" >
                        <!-- <img v-if="true" src="@/assets/msg.png" @click.stop="toshowins(itm.institutions)" alt="" > -->
                        <span v-if="itm.historyList.length>0">
                            <el-popover
                                v-if="siteState"
                                v-model="itm.visible"
                                placement="bottom"
                                :visible-arrow="false"
                                width="540"
                                :offset="-260"
                                popper-class="site-history"
                                trigger="hover">
                                <div class="content">
                                    <div class="title">
                                        <div class="title-time">{{$t('m.common.time')}}</div>
                                        <div class="title-history">{{$t('m.common.history')}}</div>
                                    </div>
                                    <div class="content-box">
                                        <div style="display:flex" v-for="(item, index) in itm.historyList" :key="index" >
                                            <div class="title-time">{{item.createTime | dateFormat}}</div>
                                            <div class="title-history">
                                                <span v-if="item.cancel.length>0">
                                                    {{$t('m.site.canceledAuthorization')}}
                                                     {{item.institutions}}
                                                     {{$t('m.site.to')}}
                                                    <span v-for="(elm, ind) in item.cancel" :key="ind">
                                                        <span v-if="ind===item.cancel.length-1">{{elm}}</span>
                                                        <span v-else>{{elm}},</span>
                                                    </span>
                                                    {{$t('m.site.authorization')}}
                                                </span>
                                                <span v-if="item.agree.length>0">
                                                    {{$t('m.site.agreedAuthorze')}}
                                                     {{item.institutions}}
                                                     {{$t('m.site.viewAites')}}
                                                    <span v-for="(elm, ind) in item.agree" :key="ind">
                                                        <span v-if="ind===item.agree.length-1">{{elm}}</span>
                                                        <span v-else>{{elm}},</span>
                                                    </span>
                                                    {{$t('m.site.sitesOfPlaceHodler')}}
                                                </span>
                                                <span v-if="item.reject.length>0">
                                                    {{$t('m.site.rejectedAuthorize')}}
                                                    {{item.institutions}}
                                                    {{$t('m.site.viewAites')}}
                                                    <span v-for="(elm, ind) in item.reject" :key="ind">
                                                        <span v-if="ind===item.reject.length-1">{{elm}}</span>
                                                        <span v-else>{{elm}},</span>
                                                    </span>
                                                    {{$t('m.site.sitesOfPlaceHodler')}}
                                                </span>
                                                <!-- <span v-if="item.apply.length>0">
                                                    {{$t('m.site.applyAuthorize')}}
                                                    {{item.institutions}}
                                                    {{$t('m.site.viewAites')}}
                                                    <span v-for="(elm, ind) in item.apply" :key="ind">
                                                        <span v-if="ind===item.apply.length-1">{{elm}}</span>
                                                        <span v-else>{{elm}},</span>
                                                    </span>
                                                    {{$t('m.site.sitesOfPlaceHodler')}}
                                                </span> -->
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <img slot="reference"  class="tickets" src="@/assets/historyclick.png" alt="" >
                                <!-- <img slot="reference"  class="tickets" src="@/assets/historyclick.png" @click.stop="gethistory(index)" alt="" > -->
                            </el-popover>
                        </span>
                        <span v-else class="ins">
                            <img class="disable" src="@/assets/history.png" alt="" >
                        </span>
                        <div class="sitenum">
                            <span>{{itm.number}}</span>
                            <span>
                                <span v-if="itm.number === 1 || itm.number === 0">{{$t('m.site.siteJoined')}}</span>
                                <span v-else>{{$t('m.site.sitesJoined')}}</span>
                            </span>
                        </div>
                        <span v-if="radioInstitutions==='In Use'" class="site-delete" @click.stop="deleteSite(itm.institutions)">
                            <i class="el-icon-delete-solid"></i>
                        </span>
                    </template>
                    <div  class="msg-warn" v-if=" siteState && itm.authoritylist && itm.authoritylist.length>0">
                        <!-- <div  class="msg-warn" v-if="true"> -->
                        <span>
                            {{itm.institutions}}
                        </span>
                        {{$t('m.site.canView')}}
                        <span v-for="(itr, ind) in itm.authoritylist" :key="ind">
                            <span v-if="ind===itm.authoritylist.length-1">{{itr}}</span>
                            <span v-else>{{itr}},</span>
                        </span>
                        {{$t('m.site.sitesOfPlaceHodler')}}
                        <el-button  class="btn" @click="toshowcancelAuthor(itm.authoritylist,itm.institutions)" type="text">
                            {{$t('m.site.cancalAuthorization')}}
                        </el-button>
                    </div>
                    <sitetable ref="sitelist"
                    :institutions="itm.institutions"
                    :radioInstitutions="radioInstitutions"
                    :fateVersionSelect="fateVersionSelect"
                    :condition="data.condition"/>
                </el-collapse-item>
            </el-collapse>
            <div class="no-data" v-if="institutionsItemList.length===0">{{$t('m.common.noData')}}</div>
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
        <!-- 审批弹框 -->
         <el-dialog :visible.sync="tipsVisible" class="add-author-dialog" :show-close="true" width="550px">
            <div class="line-text-one"> {{$t('m.site.fateManeger')}}
                "{{tipstempData.institutions}}"
                {{$t('m.site.appliedView',
                {type:tipstempData.scenarioType==='1' ? '' :
                tipstempData.scenarioType==='2' ? $t('m.common.guest') : $t('m.common.host')})}}{{$t('m.site.sitesOfPlaceHodler')}}
                <!-- <span v-if="tipstempData.scenarioType==='1'"></span>
                <span v-if="tipstempData.scenarioType==='2'">{{$t('m.common.guest')}}</span>
                <span v-if="tipstempData.scenarioType==='3'">{{$t('m.common.host')}}</span> -->

                <span v-for="(item, index) in tipstempData.insList" :key="index">
                    <span v-if="index===tipstempData.insList.length-1">{{item}}</span>
                    <span v-else> {{item}},</span>
                </span>
            </div>
            <div class="line-text-two">
                {{$t('m.site.selectApprove')}}
                {{$t('m.site.authorizationReject')}}
            </div>

            <div class="dialog-main" v-if="tipstempData.institucheckboxList.length > 0">
                <el-checkbox style="margin-bottom:10px" :indeterminate="tipstempData.instituisnate" v-model="tipstempData.instituAll" @change="instituAllChange">{{$t('m.common.all')}}</el-checkbox>
                <el-checkbox-group v-model="tipstempData.institucheckList" @change="instituChange">
                    <div v-for="(item, index) in tipstempData.institucheckboxList" :key="index">
                        <el-checkbox :label="item">
                            <tooltip :width="'255px'" style="vertical-align: top;" :content="item" :placement="'top'"/>
                        </el-checkbox>
                     </div>
                </el-checkbox-group>
            </div>
            <div class="dialog-footer" style="margin-top:20px">
                <el-button class="ok-btn" type="primary" @click="totipsAction">{{$t('m.common.agree')}}</el-button>
                <el-button class="ok-btn" type="info" @click="tipsVisible = false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <!-- 取消授权申请 -->
        <el-dialog :visible.sync="cancelAuthorVisible" class="cancel-author-dialog" :show-close="true" width="550px">
            <div class="line-text-one">{{$t('m.site.selectCancel')}}
                {{$t('m.site.theAuthorization')}} {{canceltempData.institutions}} {{$t('m.site.authorizationTo')}}
            </div>
            <!-- 1.all | 2.横向(guest) | 3.纵向(guest,host) -->
            <span v-if="canceltempData.scenarioType==='3' && canceltempData.hostboxList.length > 0">
                <div class="siteType">{{$t('m.site.hostSites')}} :</div>
                <div class="dialog-main">
                    <el-checkbox style="margin-bottom:10px" :indeterminate="canceltempData.hostisnate" v-model="canceltempData.hostAll" @change="hostChange">{{$t('m.common.all')}}</el-checkbox>
                    <el-checkbox-group v-model="canceltempData.hostList"  @change="hostStieChange">
                        <div v-for="(item, index) in canceltempData.hostboxList" :key="index">
                            <el-checkbox :label="item" >
                                <tooltip :width="'255px'" style="vertical-align: top;" :content="item" :placement="'top'"/>
                            </el-checkbox>
                        </div>
                    </el-checkbox-group>
                </div>
                <!-- <div class="no-data-line" v-else>
                    <span class="no-data">{{$t('m.common.noData')}}</span>
                </div> -->
            </span>
            <span v-if="(canceltempData.scenarioType==='2' || canceltempData.scenarioType==='3') && canceltempData.guestboxList.length > 0">
                <div class="siteType">{{$t('m.site.guestSites')}} :</div>
                <div class="dialog-main" v-if="canceltempData.guestboxList.length > 0">
                    <el-checkbox style="margin-bottom:10px" :indeterminate="canceltempData.guestisnate" v-model="canceltempData.guestAll" @change="guestChange">{{$t('m.common.all')}}</el-checkbox>
                    <el-checkbox-group v-model="canceltempData.guestList"  @change="guestStieChange">
                        <div v-for="(item, index) in canceltempData.guestboxList" :key="index">
                            <el-checkbox :label="item" >
                                <tooltip :width="'255px'" style="vertical-align: top;" :content="item" :placement="'top'"/>
                            </el-checkbox>
                        </div>
                    </el-checkbox-group>
                </div>
                <!-- <div class="no-data-line" v-else>
                    <span class="no-data">{{$t('m.common.noData')}}</span>
                </div> -->
            </span>
            <span v-if="canceltempData.scenarioType==='1'">
                <div class="dialog-main">
                    <el-checkbox style="margin-bottom:10px" :indeterminate="canceltempData.allisnate" v-model="canceltempData.allAll" @change="allChange">{{$t('m.common.all')}}</el-checkbox>
                    <el-checkbox-group v-model="canceltempData.allList"  @change="allStieChange">
                        <div v-for="(item, index) in canceltempData.allboxList" :key="index">
                            <el-checkbox :label="item" >
                                <tooltip :width="'255px'" style="vertical-align: top;" :content="item" :placement="'top'"/>
                            </el-checkbox>
                        </div>
                    </el-checkbox-group>
                </div>
            </span>
            <div class="dialog-footer">
                <el-button class="ok-btn" v-if="canceltempData.scenarioType==='1'" type="primary" :disabled="canceltempData.allList.length===0 " @click="tocancelAuthor">{{$t('m.common.sure')}}</el-button>
                <el-button class="ok-btn" v-else type="primary" :disabled="canceltempData.guestList.length===0 && canceltempData.hostList.length===0" @click="tocancelAuthor">{{$t('m.common.sure')}}</el-button>
                <el-button class="ok-btn" type="info" @click="cancelAuthorVisible = false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <!-- 删除机构 -->
        <el-dialog :visible.sync="delInstitutions" class="del-institutions-dialog" width="500px">
            <div class="line-text-one">
                {{$t('m.site.Are you sure you want to delete this institution')}}?
            </div>
            <div class="line-text-two">
                {{$t('m.site.All sites of the institution will be deleted')}}.
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="deleteInstitutions" >{{$t('m.common.sure')}}</el-button>
                <el-button class="ok-btn" type="info" @click="delInstitutions=false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
    </div>
  </div>
</template>

<script>
import {
    // getinstitutionsHistory,
    institutionsHistory,
    institutionsList,
    institutionsListValid,
    institutionsStatus,
    institutionsDetails,
    institutionsReview,
    cancelAuthorityList,
    cancelAuthority,
    authorityPermiss,
    deleteInstitutions,
    getversion } from '@/api/federated'
import { switchState } from '@/api/setting'
import moment from 'moment'
import { mapGetters } from 'vuex'
import sitetable from './sitetable'
import tooltip from '@/components/Tooltip'

const loading = document.getElementById('ajaxLoading')

export default {
    name: 'Site',
    components: {
        tooltip,
        sitetable
    },
    filters: {
        dateFormat(value) {
            return value ? moment(value).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            radioInstitutions: 'In Use',
            activeName: [], // 折叠版激活
            currentPage: 1, // 当前页
            total: 0,
            tipsVisible: false, // 提示弹框
            delInstitutions: false,
            tipstempData: {
                instituAll: false,
                instituisnate: false,
                institucheckList: [], // 已经勾选的institutions
                institucheckboxList: [], // 待审批的institutions
                institutions: '',
                insList: [], //
                scenarioType: ''// host或者gest类型或者混合型
            },
            siteState: false, // 是否显示Site-Authorization 1 为开启 2为关闭
            historyList: [],
            cancelAuthorVisible: false, // 取消弹框授权
            canceltempData: {
                scenarioType: '', // 混合或者纵向或横线类型
                institutions: '', // 机构名称
                // 混合型

                allList: [], // 已经选中
                allboxList: [], // 待选中,
                allisnate: false, // 勾选
                allAll: false, //
                //
                hostList: [], // 已经选中
                hostboxList: [], // 待选中,
                hostisnate: false, // 勾选
                hostAll: false, //
                //
                guestList: [], // 已经选中
                guestboxList: [], // 待选中,
                guestisnate: false, // 勾选
                guestAll: false

            },
            institutionsItemList: [], // 站点列表
            institutionsSelectList: [], // 站点下拉
            fateVersionSelect: [], // fate版本下拉
            data: {
                condition: '',
                status: 2,
                institutionsArray: [],
                pageNum: 1,
                pageSize: 20
            }

        }
    },
    computed: {
        ...mapGetters(['getInfo'])
    },
    watch: {
        activeName: {
            handler(newvalue) {
                localStorage.setItem('activeName', newvalue) // 保存折叠记录缓存
                // this.$refs['sitelist'].getTable()
            }
        }
    },

    created() {
        this.$nextTick(() => {
            this.info().then(res => {
                this.getinitinstitutions()
            })
            this.getinsSelectList()
            if (localStorage.getItem('activeName').length > 0) {
                this.activeName = localStorage.getItem('activeName').split(',').filter(item => item) // 缓存记录取折叠记录
            }
            this.togetversion('fate_version')
        })
    },

    methods: {

        // 初始化信息和权限
        async info() {
            // 获取sit-auto 状态
            let res = await switchState()
            this.siteState = false // 清空状态
            res.data && res.data.forEach(item => {
                if (item.functionName === 'Site-Authorization') {
                    this.siteState = item.status === 1
                }
            })
            return true
        },
        // 站点下拉接口
        async getinsSelectList() {
            this.institutionsSelectList = []
            let data = {
                status: this.data.status
            }
            let res = await institutionsListValid(data)
            res.data.institutionsSet.forEach((item, index) => {
                let obj = {}
                obj.value = item
                obj.label = item
                this.institutionsSelectList.push(obj)
            })
        },
        // 获取机构数
        async getinitinstitutions() {
            // 清空筛选条件
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element || JSON.stringify(element) === '[]') {
                        delete this.data[key]
                    }
                }
            }
            loading.style.display = 'block' // 开启loading
            await institutionsList(this.data).then(resl => {
                this.total = resl.data && resl.data.totalRecord
                this.institutionsItemList = [] // 清空记录
                let arrTemp = []
                resl.data.list.length > 0 ? resl.data.list.forEach(async (item, index) => {
                    item.historyList = []
                    item.visible = false // 历史记录弹框 (点击方式)
                    arrTemp.push(item)
                    // this.activeName.push(item.institutions) //默认全部展开
                    if (this.siteState) {
                        // 获取institutions中cancel授权信息
                        let paramsAuthority = {
                            institutions: item.institutions
                        }
                        let resAuthor = await authorityPermiss(paramsAuthority)
                        item.authoritylist = resAuthor.data
                        // 获取历史记录
                        let paramsHistory = {
                            institutions: item.institutions,
                            pageNum: 1,
                            pageSize: 100
                        }
                        item.historyList = await this.institutionsHistory(paramsHistory)
                    }

                    if (resl.data.list.length - 1 === index) {
                        this.institutionsItemList = [...arrTemp]
                        if (this.siteState) {
                            this.setMsg()
                        }
                        this.loadingFalse()
                    }
                }) : this.loadingFalse()
            }).catch(res => {
                this.loadingFalse()
            })

            return this.institutionsItemList
        },
        // 关闭loading
        loadingFalse() {
            loading.style.display = 'none' // 关闭loading
        },
        // 获取历史记录
        async institutionsHistory(paramsHistory) {
            // let res = await getinstitutionsHistory(data)
            let res = await institutionsHistory(paramsHistory)
            let historyList = []
            res.data.list.length > 0 && res.data.list.forEach((itr) => {
                let obj = {}
                obj.agree = []
                obj.reject = []
                obj.cancel = []
                obj.apply = []
                obj.createTime = itr.createTime
                obj.institutions = itr.institutions
                itr.authorityApplyReceivers.forEach((elm) => {
                    if (elm.status === 2) {
                        obj.agree.push(elm.authorityInstitutions)
                    } else if (elm.status === 3) {
                        obj.reject.push(elm.authorityInstitutions)
                    } else if (elm.status === 4) {
                        obj.cancel.push(elm.authorityInstitutions)
                    }
                    // else if (elm.status === 1) {
                    //     obj.apply.push(elm.authorityInstitutions)
                    // }
                })
                historyList.push(obj)
            })
            return historyList
        },
        // 获取小黄点显示状态
        setMsg() {
            this.institutionsItemList.map((item, index) => {
                let i = index
                // 获取小黄点显示状态
                let params = {
                    institutions: [item.institutions]
                }
                institutionsStatus(params).then(r => {
                    this.$set(this.institutionsItemList[i], 'tooltip', r.data.length > 0)
                })
            })
        },
        // 点击展开与关闭
        changeCollapse(value) {
            // console.log('value', value[value.length - 1])
            // console.log('value', value.length)
            // console.log('localStorage.length==>>', localStorage.getItem('activeName').split(',').length)

            if (value.length >= localStorage.getItem('activeName').split(',').length) {
                this.institutionsItemList.forEach((item, index) => {
                    if (value[value.length - 1] === item.institutions) {
                        this.$refs['sitelist'][index].initList()
                    }
                })
            }
        },
        // 点击显示机构历史记录 (历史操作记录变为hover)
        // gethistory(index) {
        //     console.log(index, 'history-index')
        //     // 其他弹框隐藏
        //     this.institutionsItemList.forEach((item, idx) => {
        //         if (idx !== index) {
        //             item.visible = false
        //         }
        //     })
        //     let bol = !this.institutionsItemList[index].visible
        //     this.$set('this.institutionsItemList', this.institutionsItemList[index].visible, bol)
        // },
        // 搜索
        toSearch() {
            this.data.pageNum = 1
            this.getinitinstitutions().then(res => {
                res.length > 0 && res.forEach((item, index) => {
                    this.$refs['sitelist'][index].initList()
                })
            })
        },
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.data.condition = ''
            this.data.institutionsArray = []
            this.getinitinstitutions().then(res => {
                res.length > 0 && res.forEach((item, index) => {
                    this.$refs['sitelist'][index].initList()
                })
            })
        },
        // 添加站点
        addsite() {
            this.$store.dispatch('setSiteName', 'Add a site')
            this.$router.push({
                name: 'siteadd',
                path: '/federated/siteadd',
                query: {
                    type: 'siteadd'
                }
            })
        },
        // 显示审批过程
        toshowins(institutions) {
            this.tipstempData.instituAll = false
            this.tipstempData.instituisnate = false
            this.tipstempData.institucheckList = []
            this.tipstempData.institucheckboxList = []
            this.tipstempData.insList = []
            this.tipstempData.scenarioType = ''
            let data = {
                institutions
            }
            this.tipstempData.institutions = institutions
            institutionsDetails(data).then(res => {
                // res.data.institutionsList = ['t-FATE-186-9090', 'FATE-186-8080']
                this.tipstempData.insList = res.data.institutionsList
                this.tipstempData.scenarioType = res.data.scenarioType
                res.data.institutionsList.forEach(item => {
                    this.tipstempData.institucheckboxList.push(item)
                })
                this.tipsVisible = true
            })
        },
        // 同意或者拒绝
        totipsAction() {
            let data = {
                approvedInstitutionsList: this.tipstempData.institucheckList,
                institutions: this.tipstempData.institutions
            }
            institutionsReview(data).then(res => {
                this.$message.success(this.$t('m.common.success'))
                this.getinitinstitutions()
                this.info()
                this.tipsVisible = false
            })
        },
        // 取消授权
        toshowcancelAuthor(val, institutions) {
            this.canceltempData.institutions = institutions
            this.canceltempData.allAll = false
            this.canceltempData.allisnate = false
            this.canceltempData.allboxList = []
            this.canceltempData.allList = []
            //
            this.canceltempData.hostAll = false
            this.canceltempData.hostisnate = false
            this.canceltempData.hostboxList = []
            this.canceltempData.hostList = []
            //
            this.canceltempData.guestAll = false
            this.canceltempData.guestisnate = false
            this.canceltempData.guestboxList = []
            this.canceltempData.guestList = []
            let data = { institutions }
            cancelAuthorityList(data).then(res => {
                // let data = {
                //     all: ['guestListstring', 'hostListstring'],
                //     guestList: ['guestListstring', 'guest'],
                //     hostList: ['hostListstring', 'host'],
                //     scenarioType: '2'
                // }
                if (!res || !res.data) return
                let scenarioType = res.data.scenarioType
                this.canceltempData.scenarioType = scenarioType
                // console.log('data==>.', res)
                if (scenarioType === '1') { // 混合
                    res.data.all.forEach(item => {
                        this.canceltempData.allboxList.push(item)
                    })
                } else if (scenarioType === '2' || scenarioType === '3') {
                    res.data.guestList && res.data.guestList.forEach(item => {
                        this.canceltempData.guestboxList.push(item)
                    })
                    res.data.hostList && scenarioType !== '2' && res.data.hostList.forEach(item => {
                        this.canceltempData.hostboxList.push(item)
                    })
                }
                this.cancelAuthorVisible = true
            })
        },
        // 确定取消授权
        tocancelAuthor() {
            let data = {
                all: this.canceltempData.allList,
                guestList: this.canceltempData.guestList,
                hostList: this.canceltempData.hostList,
                institutions: this.canceltempData.institutions
            }
            cancelAuthority(data).then(res => {
                this.cancelAuthorVisible = false
                this.getinitinstitutions()
                this.info()
            })
        },
        // 审批全选
        instituAllChange(val) {
            this.tipstempData.institucheckList = val ? this.tipstempData.institucheckboxList : []
            this.tipstempData.instituisnate = false
        },
        // 审批部分选中
        instituChange(value) {
            let checkedCount = value.length
            this.tipstempData.instituAll = checkedCount === this.tipstempData.institucheckboxList.length
            this.tipstempData.instituisnate = checkedCount > 0 && checkedCount < this.tipstempData.institucheckboxList.length
        },
        // 混合全选
        allChange(val) {
            this.canceltempData.allList = val ? this.canceltempData.allboxList : []
            this.canceltempData.allisnate = false
        },
        // 混合部分选中
        allStieChange(value) {
            let checkedCount = value.length
            this.canceltempData.allAll = checkedCount === this.canceltempData.allboxList.length
            this.canceltempData.allisnate = checkedCount > 0 && checkedCount < this.canceltempData.allboxList.length
        },
        // 纵全选
        hostChange(val) {
            this.canceltempData.hostList = val ? this.canceltempData.hostboxList : []
            this.canceltempData.hostisnate = false
        },
        // 纵部分选中
        hostStieChange(value) {
            let checkedCount = value.length
            this.canceltempData.hostAll = checkedCount === this.canceltempData.hostboxList.length
            this.canceltempData.hostisnate = checkedCount > 0 && checkedCount < this.canceltempData.hostboxList.length
        },
        // 横全选
        guestChange(val) {
            this.canceltempData.guestList = val ? this.canceltempData.guestboxList : []
            this.canceltempData.guestisnate = false
        },
        // 横部分选中
        guestStieChange(value) {
            let checkedCount = value.length
            this.canceltempData.guestAll = checkedCount === this.canceltempData.guestboxList.length
            this.canceltempData.guestisnate = checkedCount > 0 && checkedCount < this.canceltempData.guestboxList.length
        },
        // 展开或者折叠
        toFold() {
            if (this.activeName.length > 0) {
                this.activeName = []
            } else {
                this.institutionsItemList.forEach((item, index) => {
                    this.$refs['sitelist'][index].initList()
                })
                this.activeName = this.institutionsItemList.map(item => item.institutions)
            }
        },
        // 删除机构
        deleteSite(institution) {
            this.delInstitutions = true
            this.deleteInstitution = institution
        },
        deleteInstitutions() {
            let data = {
                institution: this.deleteInstitution
            }
            deleteInstitutions(data).then(res => {
                this.delInstitutions = false
                this.getinitinstitutions()
            })
        },
        // 切换历史、使用中
        changeRadio(value) {
            // 重置搜索条件
            this.data.condition = ''
            this.data.pageNum = 1
            this.data.pageSize = 20
            this.data.institutionsArray = []
            if (this.radioInstitutions === 'In Use') {
                this.data.status = 2
            } else {
                this.data.status = 3
            }
            this.getinitinstitutions()
            this.getinsSelectList()
        },
        // 获取版本号
        togetversion(version) {
            let data = {
                institutions: this.institutions,
                versionName: version
            }
            getversion(data).then((res) => {
                res.data.forEach(item => {
                    let obj = {}
                    obj.value = item
                    obj.text = item
                    this.fateVersionSelect.push(obj)
                })
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/site.scss';
.site-history{
    margin-top: 0px !important;
    padding: 0;
    line-height:inherit;
    background: #fff !important;
    .content{
        padding: 12px 10px;
    }
    .title{
        padding-left: 14px;
        color:#4E5766;
        font-size: 18px;
        font-weight:bold;
        border-bottom: 1px solid #E6EBF0;

    }
    .content-box{
        padding-left: 14px;
        max-height: 500px;
        overflow: auto;
        line-height: 1.5;
        .title-time{
            color: #848C99;
            margin-right: 10px;
        }
        .title-history{
            width: 70%;
            color: #4E5766;
            word-break: break-all;
            .version{
                color: #217AD9;
            }
        }
    }
    .title-time{
        width: 30%;
        display:inline-block;
        margin: 12px 10px 12px 0;
    }
    .title-history{
        vertical-align: top;
        width: 60%;
        display:inline-block;
        margin: 12px 0;
    }

}
</style>
