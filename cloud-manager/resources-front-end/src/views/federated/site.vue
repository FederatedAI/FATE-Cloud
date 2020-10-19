<template>
  <div class="site-box">
    <div class="site">
        <div class="site-header">
            <el-button class="add" type="primary" @click="addsite">Add</el-button>
            <el-input class="input input-placeholder" v-model.trim="data.condition" placeholder="Search for Site Name or Party ID" clearable>
            <!-- <i slot="suffix" @click="toSearch" class="el-icon-search search el-input__icon" /> -->
            </el-input>
            <el-select class="sel-role input-placeholder" v-model="data.institutions" placeholder="institution">
                <el-option
                    v-for="item in institutionsItemList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
            </el-select>
            <el-button class="go" @click="toSearch" type="primary">GO</el-button>
        </div>
        <div class="collapse"  >
            <el-collapse  v-for="(item, index) in institutionsItemList" :key="index">
                <el-collapse-item>
                    <template slot="title">
                        <span class="ins" >{{item.institutions}}</span>
                        <img v-if="item.tooltip && siteState" src="@/assets/msg.png" @click.stop="toshowins(item.institutions)" alt="" >
                        <el-popover
                            v-if="siteState"
                            v-model="item.visible"
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
                                    <div v-for="(item, index) in historyList" :key="index" >
                                        <div class="title-time">{{item.updateTime | dateFormat}}</div>
                                        <div class="title-history">
                                            <span>{{item.status===2?'Agree':'Reject'}}</span>
                                            to authorize {{item.institutions}} to view the sites of
                                            <span v-for="(elm, ind) in item.authorityApplyReceivers" :key="ind">
                                                <span v-if="ind===item.authorityApplyReceivers.length-1">{{elm.authorityInstitutions}}</span>
                                                <span v-else>{{elm.authorityInstitutions}},</span>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <img slot="reference" class="tickets" src="@/assets/historyclick.png" @click.stop="gethistory(item.institutions,index)" alt="" >
                        </el-popover>

                        <div class="sitenum">
                            <span>{{item.number}}</span>
                            <span>sites  joined</span>
                        </div>
                    </template>
                    <div  class="msg-warn" v-if=" siteState && item.authoritylist && item.authoritylist.length>0">
                        <span>
                            {{item.institutions}}
                        </span>
                            has permission to view the sites of
                        <span v-for="(itr, ind) in item.authoritylist" :key="ind">
                            <span v-if="ind===item.authoritylist.length-1">{{itr}}</span>
                            <span v-else>{{itr}},</span>
                        </span>
                        <el-button  class="btn" @click="toshowcancelAuthor(item.authoritylist,item.institutions)" type="text">
                            Cancal authorization
                        </el-button>
                    </div>
                    <sitetable ref="getins" :institutions="item.institutions"/>
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
                <el-checkbox-group v-model="canceltempData.cancelAuthorList">
                    <div v-for="(item, index) in canceltempData.cancelcheckboxList" :key="index"> <el-checkbox :label="item"></el-checkbox></div>
                </el-checkbox-group>
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="tocancelAuthor">Sure</el-button>
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
// import elementResizeDetectorMaker from 'element-resize-detector'

export default {
    name: 'Site',
    components: {
        sitetable
        // notJoined
        // siteAdd
    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            currentPage1: 1, // 当前页
            total: 0,
            visible: false, // 历史记录弹框
            clickState: false, // 点击状态,
            activebody: false, // 变化的表格
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
            institutionsItemList: [], // 站点下拉
            data: {
                // condition: '',
                // institutions:'',
                pageNum: 1,
                pageSize: 20
            }
        }
    },
    computed: {
        ...mapGetters(['getInfo'])
    },
    created() {
        this.$nextTick(res => {
            this.getinitinstitutions()
            this.info()
        })
    },
    mounted() {
        // let erd = elementResizeDetectorMaker()
        // // 监听 是否会出现箭头
        // erd.listenTo(this.$refs.siteitem, (element) => { // 这里的this.$refs 指定要监听的元素对象，对应的是<div ref="fan"></div>
        //     this.getstylewidth()
        // })
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
        // 获取机构数
        async getinitinstitutions() {
            let data = {
                pageNum: 1,
                pageSize: 100
            }
            let res = await institutionsList(data)
            this.total = res.data.totalRecord
            this.institutionsItemList = [] // 清空记录
            res.data.list.forEach((item, index) => {
                item.value = item.institutions
                item.label = item.institutions
                item.visible = false
                // 获取ins授权信息
                let dataparams = {
                    institutions: item.institutions
                }
                authorityPermiss(dataparams).then(res => {
                    item.authoritylist = res.data
                })
                // 小黄点显示
                let params = {
                    institutions: [item.institutions]
                }
                institutionsStatus(params).then(r => {
                    if (r.data.length > 0) {
                        item.tooltip = true
                    } else {
                        item.tooltip = false
                    }
                    this.institutionsItemList.push(item)
                })
            })
        },
        // 获取机构列表
        // getinstitutionsItemList(){

        // },
        // 获取机构历史记录
        gethistory(item, index) {
            // 其他弹框隐藏
            this.institutionsItemList.forEach(item => {
                item.visible = false
            })
            let data = {
                institutions: item,
                pageNum: 1,
                pageSize: 100
            }
            getinstitutionsHistory(data).then(res => {
                // 其他弹框显示
                this.institutionsItemList[index].visible = true
                this.visible = true
                this.historyList = res.data.list
            })
        },
        // 搜索
        toSearch() {
            this.data.pageNum = 1
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
