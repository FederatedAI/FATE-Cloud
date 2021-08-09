<template>

  <div class="access-box">
    <div class="system-header">
      <el-radio-group class="radio" v-model="radio">
        <el-radio-button label="Cloud Manager">{{$t('m.access.Cloud Manager')}}</el-radio-button>
        <el-radio-button label="FATE Manager">{{$t('m.access.FATE Manager')}}</el-radio-button>
      </el-radio-group>
    </div>
     <div class="row-add">
        <div class="btn-add">
            <el-button type="text" class="access-add" @click="toAdd"  >
                <img src="@/assets/add_admin.png">
                <span>{{$t('m.access.add')}}</span>
            </el-button>
            <el-button class="go" type="primary" @click="toSearch">{{$t('m.common.go')}}</el-button>
            <el-input v-if="radio === 'Cloud Manager'" class="input" clearable v-model.trim="data.name" :placeholder="$t('m.access.Search for Name')">
                <i slot="prefix" @click="toSearch" class="el-icon-search search" />
            </el-input>
            <el-input v-else class="input" clearable v-model.trim="data.institutions" :placeholder="$t('m.access.Search for Institution')">
                <i slot="prefix" class="el-icon-search search" />
            </el-input>

        </div>

    </div>
    <div class="system-body">
        <div v-if="radio === 'Cloud Manager'" class="table">
            <el-table :data="cloudtableData" ref="table" header-row-class-name="tableHead" header-cell-class-name="tableHeadCell" cell-class-name="tableCell" height="100%" tooltip-effect="light">
                <el-table-column prop="name" :label="$t('m.access.Name')" ></el-table-column>
                <el-table-column prop="adminLevel" :label="$t('m.access.Admin-level')"    align="center" show-overflow-tooltip></el-table-column>
                <el-table-column prop="creator" :label="$t('m.access.Creator')"   align="center"></el-table-column>
                <el-table-column prop="createTime"  :label="$t('m.access.Create Time')"  align="center">
                    <template slot-scope="scope">
                    <span>{{scope.row.createTime | dateFormat}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" :label="$t('m.access.Action')"  align="right" >
                    <template slot-scope="scope">
                        <span >
                            <el-button v-if="scope.row.name===loginName" type="text" disabled="" icon="el-icon-delete-solid"></el-button>
                            <el-button v-else type="text" @click="todDelete(scope.row)" icon="el-icon-delete-solid"></el-button>
                        </span>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div v-else class="table">
            <el-table :data="managertableData" ref="table" header-row-class-name="tableHead" header-cell-class-name="tableHeadCell" cell-class-name="tableCell" height="100%" tooltip-effect="light">
                <el-table-column prop="institutions"  :label="$t('m.access.Institution')" show-overflow-tooltip></el-table-column>
                <el-table-column prop="fateManagerId" :label="$t('m.access.Admin ID')"  show-overflow-tooltip></el-table-column>
                <el-table-column prop="creator" :label="$t('m.access.Creator')" ></el-table-column>
                <el-table-column prop="createTime" :label="$t('m.access.Create Time')" >
                    <template slot-scope="scope">
                    <span>{{scope.row.createTime | dateFormat}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status"  :label="$t('m.access.Status')"  >
                    <template slot-scope="scope">
                        <span v-if="scope.row.status===1" type="text">{{$t('m.common.unactivated')}}</span>
                        <span v-if="scope.row.status===2" type="text">{{$t('m.common.activated')}}</span>
                        <span v-if="scope.row.status===20" type="text">{{$t('m.common.activated')}}</span>
                    </template>
                </el-table-column>
                 <el-table-column prop=""  :label="$t('m.access.Action')"  align="center" >
                    <template slot-scope="scope">
                        <span>
                            <el-button @click="toactivat(scope.row)" type="text">{{$t('m.access.detail')}}</el-button>
                        </span>
                        <span style="margin-left:25px">
                            <el-button v-if="scope.row.status===1" disabled type="text">{{$t('m.access.reactivate')}}</el-button>
                            <el-button v-if="scope.row.status===2" @click="toReactivate(scope.row)" type="text">{{$t('m.access.reactivate')}}</el-button>
                            <el-button v-if="scope.row.status===20" disabled type="text">{{$t('m.access.reactivate')}}</el-button>
                        </span>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="pagination">
            <el-pagination background @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="data.pageSize" layout="total, prev, pager, next, jumper" :total="total"></el-pagination>
        </div>
    </div>
    <!-- 添加弹框 -->
    <el-dialog :visible.sync="adddialog" class="auto-dialog" width="690px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
        <div class="dialog-title">
            {{$t('m.access.Add admin')}}
        </div>
        <div  v-if='radio === "FATE Manager"' class="dialog-line">
            {{$t('m.access.To add')}}
        </div>
        <el-form ref="managerForm" :model="managerForm" label-position="left" class="add-input" :rules="editRules" label-width="260px">
            <el-form-item prop="institutionName">
                <span slot="label" class="input-title">
                    <span  v-if='radio === "Cloud Manager"'>{{$t('m.access.Name')}}</span>
                    <span  v-if='radio === "FATE Manager"'>{{$t('m.access.Institution')}}</span>
                </span>
                <el-input v-model="managerForm.institutionName" @focus="$refs['managerForm'].clearValidate('institutionName')" :placeholder="$t('m.access.Maximum of 20 chatacters')"></el-input>
            </el-form-item>
            <el-form-item v-if='radio === "Cloud Manager"'>
                <span slot="label" class="input-title">{{$t('m.access.Admin-level')}}</span>
                <el-checkbox v-model="managerForm.levelChecked" disabled>{{$t('m.access.senior admin')}}</el-checkbox>
            </el-form-item>
            <span  v-if='radio === "FATE Manager"'>
                <el-form-item  >
                    <span slot="label" style="color:#4E5766" class="input-title">{{$t('m.access.Invitation Link Setting')}}</span>
                </el-form-item>
                <el-form-item >
                    <span slot="label" class="input-title">{{$t('m.access.Link Type')}}</span>
                    <el-radio-group v-model="managerForm.protocol">
                        <el-radio label="https://">HTTPS</el-radio>
                        <el-radio label="http://">HTTP</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item prop="network">
                    <span slot="label" class="input-title">{{$t('m.access.Proxy Network Access')}}</span>
                    <el-input class="input"  v-model="managerForm.network" placeholder=""></el-input>
                    <span class="reset" @click="toResetNetwork">{{$t('m.access.reset to default')}}</span>
                </el-form-item>
            </span>
        </el-form>
        <div class="dialog-foot">
          <el-button type="primary" @click="toOK">{{$t('m.common.OK')}}</el-button>
          <el-button type="info" @click="adddialog=false;$refs['managerForm'].clearValidate()">{{$t('m.common.cancel')}}</el-button>
        </div>
      </div>
    </el-dialog>
    <!-- 删除弹框 -->
    <el-dialog :visible.sync="deletedialog" class="auto-dialog" width="700px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
        <div class="line-text-one">
            {{$t('m.access.Are you sure you want to delete this administrator?')}}
        </div>
        <div class="dialog-foot">
          <el-button type="primary" @click="toSure">{{$t('m.common.sure')}}</el-button>
          <el-button type="info" @click="deletedialog=false">{{$t('m.common.cancel')}}</el-button>
        </div>
      </div>
    </el-dialog>
    <!-- 修改弹框 -->
    <el-dialog :visible.sync="addSuccessdialog"  class="add-success-dialog" width="700px">
        <div class="line-text-two">{{$t('m.access.the administrator')}}</div>
          <el-form ref="showForm" :model="managerForm" label-position="left"  class="add-input" :rules="editRules" label-width="235px">
                <span class="registration-box">
                    <el-form-item  label="" prop="">
                        <span slot="label">
                            <span style="color:#848C99;margin-right:20px">{{$t('m.access.Invitation Link Setting')}}</span>
                        </span>
                    </el-form-item>
                    <el-form-item :label="$t('m.access.Link Type')"  prop="protocol">
                        <span>{{managerForm.protocol==='http://'?'HTTP':'HTTPS'}}</span>
                    </el-form-item>
                    <el-form-item :label="$t('m.access.Proxy Network Access')"  prop="network">
                        <span >{{managerForm.network}}</span>

                    </el-form-item>
                </span>
                <el-form-item>
                    <span  slot="label" class="input-title" style="margin-left:30px">{{$t('m.access.Invitation Link')}}</span>
                    <span class="line-text-three">
                        <el-popover
                            placement="top"
                            width="660"
                            trigger="hover"
                            offset="-30"
                            :content="managerForm.addSuccessText">
                            <span class="copy-link" slot="reference">{{managerForm.addSuccessText}}</span>
                        </el-popover>
                        <span class="copy dialogcopy"  @click="toCopy"  :data-clipboard-text="managerForm.addSuccessText">{{$t('m.common.copy')}}</span>
                    </span>
                </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="addSuccessdialog=false">{{$t('m.common.OK')}}</el-button>
            <el-button class="ok-btn" type="info" v-if="managerForm.status===1" @click="modifyDialog=true" >{{$t('m.common.modify')}}</el-button>
        </div>
    </el-dialog>
    <!-- 进入弹框 -->
    <el-dialog :visible.sync="modifyDialog"  class="add-success-dialog"  width="700px">
        <div class="line-text-two">{{$t('m.access.Invitation Link Setting')}}</div>
          <el-form ref="showForm" :model="managerForm" label-position="left"  class="add-input" :rules="editRules" label-width="240px">
            <el-form-item :label="$t('m.access.Link Type')"  prop="protocol">
                <el-radio-group  v-model="managerForm.protocol">
                    <el-radio label="https://">HTTPS</el-radio>
                    <el-radio label="http://">HTTP</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item :label="$t('m.access.Proxy Network Access')"  prop="proxyNetworkAccess">
                <el-input class="input" v-model="managerForm.network" placeholder=""></el-input>
                <span class="reset" @click="toResetNetwork">{{$t('m.access.reset to default')}}</span>
            </el-form-item>
        </el-form>
        <div class="dialog-footer" style="margin-top: 25px;">
            <el-button class="ok-btn" type="primary" @click="toUpdata">{{$t('m.common.submit')}}</el-button>
            <el-button class="ok-btn" type="info" @click="modifyDialog=false" >{{$t('m.common.cancel')}}</el-button>
        </div>
    </el-dialog>
    <!-- 恢复弹框 -->
    <el-dialog :visible.sync="reactivateDialog" class="res-institutions-dialog" width="700px">
        <div class="line-text-one">
            {{$t('m.access.Are you sure you want to reactivate this institution')}}?
        </div>
        <div class="line-text-two">
            {{$t('m.access.Status of the institution will be reset to unactivated')}}.
        </div>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="reactivateUser">{{$t('m.common.sure')}}</el-button>
            <el-button class="ok-btn" type="info" @click="reactivateDialog=false">{{$t('m.common.cancel')}}</el-button>
        </div>
    </el-dialog>
  </div>

</template>

<script>

import moment from 'moment'
import Clipboard from 'clipboard'
import { mapGetters } from 'vuex'
import { accessCloudList, accessManagerList, addManager, addCloud, deleteCloud, updataManager, reactivateUser } from '@/api/setting'
import { resetNetwork } from '@/api/federated'
// import checkip from '@/utils/checkip'

export default {
    name: 'access',
    components: {},
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            fateManagerId: '',
            adddialog: false,
            deletedialog: false,
            modifyDialog: false,
            addSuccessdialog: false,
            reactivateDialog: false,
            radio: 'Cloud Manager',
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            managertableData: [],
            cloudtableData: [],
            historydata: [], // 历史记录

            managerForm: {
                addSuccessText: '', // 连接
                institutionName: '',
                levelChecked: true,
                protocol: 'https://',
                network: '' // 代理网关

            },
            data: {
                pageNum: 1,
                pageSize: 20
            },
            editRules: {
                institutionName: [{ required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        if (!value) {
                            callback(new Error(this.$t('m.siteAdd.institutionRrequired')))
                        } else if (value.length > 20) {
                            callback(new Error(this.$t('m.siteAdd.maximum20chatacters')))
                        } else {
                            callback()
                        }
                    } }],
                network: [
                    {
                        required: true,
                        trigger: 'bulr',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                callback(new Error(this.$t('m.siteAdd.proxyNetworkAccessRequired')))
                            } else {
                                callback()
                            }
                        }

                    }
                ]

            }
        }
    },
    watch: {
        radio: {
            handler(val) {
                // 全部入参条件初始化
                this.data = {
                    name: '',
                    institutions: '',
                    pageNum: 1,
                    pageSize: 20
                }
                this.initList()
            }
        }
    },
    computed: {
        ...mapGetters(['getInfo', 'loginName'])
    },
    created() {
        this.initList()
        this.toResetNetwork()
        if (this.$route.query.type === 'FATEManager') {
            this.radio = 'FATE Manager'
        }
    },
    mounted() {

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
            if (this.radio === 'Cloud Manager') {
                accessCloudList(this.data).then(res => {
                    this.cloudtableData = res.data.list
                    this.total = res.data.totalRecord
                })
            } else if (this.radio === 'FATE Manager') {
                accessManagerList(this.data).then(res => {
                    this.managertableData = res.data.list
                    this.total = res.data.totalRecord
                })
            }
        },
        // 确定添加
        toOK() {
            this.$refs['managerForm'].validate((valid) => {
                if (valid) {
                    if (this.radio === 'Cloud Manager') {
                        let data = {
                            creator: this.loginName, // 当前登录用户
                            adminLevel: 1, // 暂定
                            name: this.managerForm.institutionName.trim()
                        }
                        addCloud(data).then(res => {
                            this.adddialog = false
                            this.initList()
                        })
                    } else {
                        let data = {

                            creator: this.loginName, // 当前登录用户
                            institutions: this.managerForm.institutionName.trim(),
                            protocol: this.managerForm.protocol,
                            network: this.managerForm.network,
                            mode: 'short' // 1.4.0 版本修改
                        }
                        addManager(data).then(res => {
                            this.managerForm.addSuccessText = JSON.stringify(res.data.registrationLink).replace(new RegExp('"', 'g'), '')
                            this.managerForm.fateManagerId = res.data.fateManagerId
                            this.managerForm.protocol = res.data.protocol
                            this.adddialog = false
                            this.initList()
                            this.addSuccessdialog = true
                        })
                    }
                }
            })
        },
        // 删除行
        todDelete(row) {
            this.deletedialog = true
            this.deleteRow = row.cloudManagerId
        },
        // 确实删除
        toSure() {
            let data = {
                cloudManagerId: this.deleteRow
            }
            deleteCloud(data).then(res => {
                this.deletedialog = false
                this.initList()
            })
        },
        // 添加
        toAdd() {
            this.managerForm.institutionName = ''
            this.adddialog = true
            this.managerForm.protocol = 'https://'
        },
        // 激活
        toactivat(row) {
            this.managerForm.addSuccessText = JSON.stringify(row.registrationLink).replace(new RegExp('"', 'g'), '')
            this.managerForm.fateManagerId = row.fateManagerId
            this.managerForm.institutionName = row.institutions
            this.managerForm.network = row.network
            this.managerForm.protocol = row.protocol
            this.managerForm.status = row.status
            this.addSuccessdialog = true
        },
        toUpdata() {
            let data = {
                creator: this.loginName,
                institution: this.managerForm.institutionName,
                fateManagerId: this.managerForm.fateManagerId,
                network: this.managerForm.network,
                protocol: this.managerForm.protocol,
                mode: 'short' // 1.4.0 版本修改
            }

            updataManager(data).then(res => {
                this.modifyDialog = false
                this.addSuccessdialog = false
                this.managerForm.addSuccessText = JSON.stringify(res.data.registrationLink).replace(new RegExp('"', 'g'), '')
                this.managerForm.protocol = res.data.protocol
                this.managerForm.network = res.data.network
                this.initList()
                setTimeout(() => {
                    this.addSuccessdialog = true
                }, 300)
            })
        },
        // 翻页
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        toCopy() {
            let dialogClipboard = new Clipboard('.dialogcopy')
            dialogClipboard.on('success', e => {
                this.$message.success(this.$t('m.common.copySuccess'))
                // 释放内存
                dialogClipboard.destroy()
            })
        },
        // 点击搜索
        toSearch() {
            this.currentPage = 1
            this.initList()
        },
        // 重置默认值
        toResetNetwork() {
            resetNetwork().then(res => {
                this.managerForm.network = res.data.network
            })
        },
        // 重新激活
        toReactivate(row) {
            this.reactivateDialog = true
            this.fateManagerId = row.fateManagerId
        },
        reactivateUser() {
            let data = {
                fateManagerId: this.fateManagerId
            }
            reactivateUser(data).then(res => {
                this.initList()
                this.reactivateDialog = false
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/access.scss';
.el-popover{
    background: rgba(0,0,0,.8);
    color: #fff;
    border:transparent;
}
.el-popper[x-placement^=top]{
        .popper__arrow::after {
        border-top-color: rgba(0,0,0,.8);
    }
}
</style>
