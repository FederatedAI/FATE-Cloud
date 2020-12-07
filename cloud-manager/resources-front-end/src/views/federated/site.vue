<template>
  <div class="site-box">
    <div class="site" >
        <div class="site-header">
            <el-button class="add" type="text" @click="addsite">
                <img src="@/assets/add_site.png">
                <span>add</span>
            </el-button>
            <el-input class="input input-placeholder" v-model.trim="data.condition" placeholder="Search for Site Name or Party ID" clearable>
            <!-- <i slot="suffix" @click="toSearch" class="el-icon-search search el-input__icon" /> -->
            </el-input>
            <el-select class="sel-role input-placeholder" v-model="data.institutionsArray" filterable multiple collapse-tags placeholder="institution">
                <el-option
                    v-for="(item,index) in institutionsSelectList"
                    :key="index"
                    :label="item.label"
                    :value="item.value">
                </el-option>
            </el-select>
            <el-button class="go" type="text" @click="toFold" >
                <span v-if='activeName.length>0'>unfold all</span>
                <span v-else>fold all</span>
            </el-button>
            <el-button class="go" @click="toSearch" type="primary">GO</el-button>
        </div>
        <div class="collapse" >
            <el-collapse v-model="activeName" v-for="(itm, index) in institutionsItemList" :key="index" >
                <el-collapse-item  :name="itm.institutions">
                    <template slot="title">
                        <span class="ins" >{{itm.institutions}}</span>
                        <img v-if="itm.tooltip && siteState" src="@/assets/msg.png" @click.stop="toshowins(itm.institutions)" alt="" >
                        <span v-if="itm.historyList.length>0">
                            <el-popover
                                v-if="siteState"
                                v-model="itm.visible"
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
                                        <div v-for="(item, index) in itm.historyList" :key="index" >
                                            <div class="title-time">{{item.updateTime | dateFormat}}</div>
                                            <div class="title-history">
                                                <span v-if="item.cancel.length>0">
                                                    Canceled the authorization of {{item.institutions}} to
                                                    <span v-for="(elm, ind) in item.cancel" :key="ind">
                                                        <span v-if="ind===item.cancel.length-1">{{elm}}</span>
                                                        <span v-else>{{elm}},</span>
                                                    </span>
                                                </span>
                                                <span v-if="item.agree.length>0">
                                                    Agreed to authorze {{item.institutions}} to view sites of
                                                    <span v-for="(elm, ind) in item.agree" :key="ind">
                                                        <span v-if="ind===item.agree.length-1">{{elm}}</span>
                                                        <span v-else>{{elm}},</span>
                                                    </span>
                                                </span>
                                                <span v-if="item.reject.length>0">
                                                    Rejected to authorize  {{item.institutions}}  to view the sites of
                                                    <span v-for="(elm, ind) in item.reject" :key="ind">
                                                        <span v-if="ind===item.reject.length-1">{{elm}}</span>
                                                        <span v-else>{{elm}},</span>
                                                    </span>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <img slot="reference"  class="tickets" src="@/assets/historyclick.png" @click.stop="gethistory(index)" alt="" >
                            </el-popover>
                        </span>
                        <span v-else class="ins">
                            <img class="disable" src="@/assets/history.png" alt="" >
                        </span>
                        <div class="sitenum">
                            <span>{{itm.number}}</span>
                            <span>
                                <span v-if="itm.number === 1">site</span>
                                <span v-else>sites</span>
                                joined
                            </span>
                        </div>
                    </template>
                    <div  class="msg-warn" v-if=" siteState && itm.authoritylist && itm.authoritylist.length>0">
                        <span>
                            {{itm.institutions}}
                        </span>
                            has permission to view the sites of
                        <span v-for="(itr, ind) in itm.authoritylist" :key="ind">
                            <span v-if="ind===itm.authoritylist.length-1">{{itr}}</span>
                            <span v-else>{{itr}},</span>
                        </span>
                        <el-button  class="btn" @click="toshowcancelAuthor(itm.authoritylist,itm.institutions)" type="text">
                            Cancal authorization
                        </el-button>
                    </div>
                    <sitetable ref="sitelist" :institutions="itm.institutions" :condition="data.condition"/>

                </el-collapse-item>
            </el-collapse>
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
        <!-- 审批弹框 -->
         <el-dialog :visible.sync="tipsVisible" class="add-author-dialog" width="700px">
            <div class="line-text-one"> FATE Maneger
                " <span style="color:#217AD9">{{tipstempData.institutions}}</span> "?
            </div>
            <div class="line-text-one">applied to view the other sites of </div>
            <div class="line-text-one" style="color:#217AD9">
                <span v-for="(item, index) in tipstempData.insList" :key="index">
                    <span v-if="index===tipstempData.insList.length-1">{{item.authorityInstitutions}}</span>
                    <span v-else> {{item.authorityInstitutions}},</span>
                </span>
            </div>
            <div class="line-text-one">please select the institution you want to approve </div>
            <div class="line-text-one">the authorization, and the others will be reject:</div>
             <div class="dialog-main">
                <el-checkbox-group v-model="tipstempData.institucheckList">
                    <div v-for="(item, index) in tipstempData.institucheckboxList" :key="index"> <el-checkbox :label="item"></el-checkbox></div>
                </el-checkbox-group>
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="totipsAction">Sure</el-button>
                <el-button class="ok-btn" type="info" @click="tipsVisible = false">Cancel</el-button>
            </div>
        </el-dialog>
              <!-- 取消授权申请 -->
         <el-dialog :visible.sync="cancelAuthorVisible" class="cancel-author-dialog" width="700px">
            <div class="line-text-one">Please select the institution to cancel</div>
            <div class="line-text-one">the authorization to {{canceltempData.institutions}}</div>
             <div class="dialog-main">
                <el-checkbox style="margin-bottom:10px" :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">all</el-checkbox>
                <el-checkbox-group v-model="canceltempData.cancelAuthorList"  @change="handleCheckedCitiesChange">
                    <div v-for="(item, index) in canceltempData.cancelcheckboxList" :key="index"> <el-checkbox :label="item"></el-checkbox></div>
                </el-checkbox-group>
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" :disabled="canceltempData.cancelAuthorList.length===0" @click="tocancelAuthor">Sure</el-button>
                <el-button class="ok-btn" type="info" @click="cancelAuthorVisible = false">Cancel</el-button>
            </div>
        </el-dialog>
    </div>
  </div>
</template>

<script>

import {
    getinstitutionsHistory,
    institutionsList,
    institutionsStatus,
    institutionsDetails,
    institutionsReview,
    cancelAuthority,
    authorityPermiss } from '@/api/federated'
import { switchState } from '@/api/setting'
import moment from 'moment'
import { mapGetters } from 'vuex'
import sitetable from './siteaatable'
import { setTimeout } from 'timers'

export default {
    name: 'Site',
    components: {
        sitetable
    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            activeName: [], // 折叠版激活
            loading: true,
            currentPage1: 1, // 当前页
            total: 0,
            tipsVisible: false, // 提示弹框
            tipstempData: {
                institucheckList: [], // 已经同意的institutions
                institucheckboxList: [], // 带审批的institutions
                institutions: '',
                insList: []
            },
            siteState: '', // 是否显示Site-Authorization
            historyList: [],
            cancelAuthorVisible: false, // 取消弹框授权
            canceltempData: {
                institutions: '',
                cancelAuthorList: [], // 已经选择
                cancelcheckboxList: [] // 待选中
            },
            institutionsItemList: [], // 站点列表
            institutionsSelectList: [], // 站点下拉
            data: {
                condition: '',
                // institutionsArray: [],
                pageNum: 1,
                pageSize: 20
            },
            isIndeterminate: false,
            checkAll: false

        }
    },
    computed: {
        ...mapGetters(['getInfo'])
    },
    created() {
        this.$nextTick(res => {
            this.getinsSelectList()
            this.getinitinstitutions()
            this.info()
        })
    },

    methods: {

        // 初始化信息和权限
        info() {
            // 获取sit-auto 状态
            switchState().then(res => {
                this.siteState = ''
                res.data.forEach(item => {
                    if (item.functionName === 'Site-Authorization') {
                        this.siteState = item.status === 1
                    }
                })
            })
        },
        // 站点下拉接口
        async getinsSelectList() {
            let data = {
                pageNum: 1,
                pageSize: 100
            }
            let res = await institutionsList(data)
            res.data.list.forEach((item, index) => {
                let obj = {}
                obj.value = item.institutions
                obj.label = item.institutions
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
            await institutionsList(this.data).then(resl => {
                this.total = resl.data.totalRecord
                this.institutionsItemList = [] // 清空记录
                let Arr = []
                resl.data.list.forEach(async (item, index) => {
                    item.historyList = []
                    item.visible = false // 历史记录弹框
                    Arr.push(item)
                    this.institutionsItemList = Arr
                    this.activeName.push(item.institutions)
                    // 获取历史记录
                    let data = {
                        institutions: item.institutions,
                        pageNum: 1,
                        pageSize: 100
                    }
                    let res = await getinstitutionsHistory(data)
                    res.data.list.forEach((itr) => {
                        let obj = {}
                        obj.agree = []
                        obj.reject = []
                        obj.cancel = []
                        obj.updateTime = itr.updateTime
                        obj.institutions = itr.institutions
                        itr.authorityApplyReceivers.forEach((elm) => {
                            if (elm.status === 2) {
                                obj.agree.push(elm.authorityInstitutions)
                            } else if (elm.status === 3) {
                                obj.reject.push(elm.authorityInstitutions)
                            } else if (elm.status === 4) {
                                obj.cancel.push(elm.authorityInstitutions)
                            }
                        })
                        item.historyList.push(obj)
                    })
                    // 获取institutions中cancel授权信息
                    let dataparams = {
                        institutions: item.institutions
                    }
                    let re = await authorityPermiss(dataparams)
                    item.authoritylist = re.data
                    // 获取小黄点显示状态
                    let params = {
                        institutions: [item.institutions]
                    }
                    let r = await institutionsStatus(params)
                    // this.institutionsItemList.push(item)
                    if (r.data.length > 0) {
                        item.tooltip = true
                    } else {
                        item.tooltip = false
                    }
                })
                setTimeout(() => {
                    this.institutionsItemList = [...Arr]
                }, 1000)
            })

            return this.institutionsItemList
        },
        // 点击显示机构历史记录
        gethistory(index) {
            // 其他弹框隐藏
            this.institutionsItemList.forEach(item => {
                item.visible = false
            })
            // 显示本次点击弹框
            setTimeout(() => {
                this.institutionsItemList[index].visible = true
            }, 500)
        },
        // 搜索
        toSearch() {
            this.data.pageNum = 1
            this.getinitinstitutions().then(res => {
                res.forEach((item, index) => {
                    console.log('index==>>', index)
                    this.$refs['sitelist'][index].initList()
                })
            })
        },
        handleSizeChange(val) {
            console.log(`每页 ${val} 条`)
        },
        handleCurrentChange(val) {
            this.data.pageNum = val
        },
        // 添加站点
        addsite() {
            this.$store.dispatch('SiteName', 'Add a site')
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
            this.tipsVisible = true
            let data = {
                institutions
            }
            this.tipstempData.institutions = institutions
            institutionsDetails(data).then(res => {
                this.tipstempData.insList = res.data
                this.tipstempData.institucheckboxList = []
                res.data.forEach(item => {
                    this.tipstempData.institucheckboxList.push(item.authorityInstitutions)
                })
            })
        },
        // 同意或者拒绝
        totipsAction() {
            let data = {
                approvedInstitutionsList: this.tipstempData.institucheckList,
                institutions: this.tipstempData.institutions
            }
            institutionsReview(data).then(res => {
                this.$message.success('success')
                this.getinitinstitutions()
                this.info()
                this.tipsVisible = false
            })
        },
        // 取消授权
        toshowcancelAuthor(val, item) {
            this.canceltempData.institutions = item
            this.canceltempData.cancelcheckboxList = []
            val.forEach(item => {
                this.canceltempData.cancelcheckboxList.push(item)
            })
            this.cancelAuthorVisible = true
        },
        // 确定取消授权
        tocancelAuthor() {
            let data = {
                canceledInstitutionsList: this.canceltempData.cancelAuthorList,
                institutions: this.canceltempData.institutions
            }
            cancelAuthority(data).then(res => {
                this.cancelAuthorVisible = false
                this.getinitinstitutions()
                this.info()
            })
        },
        // 全选
        handleCheckAllChange(val) {
            this.canceltempData.cancelAuthorList = val ? this.canceltempData.cancelcheckboxList : []
            this.isIndeterminate = false
        },
        handleCheckedCitiesChange(value) {
            let checkedCount = value.length
            this.checkAll = checkedCount === this.canceltempData.cancelcheckboxList.length
            this.isIndeterminate = checkedCount > 0 && checkedCount < this.canceltempData.cancelcheckboxList.length
        },
        // 展开或者折叠
        toFold() {
            if (this.activeName.length > 0) {
                this.activeName = []
            } else {
                this.activeName = this.institutionsItemList.map(item => item.institutions)
            }
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
