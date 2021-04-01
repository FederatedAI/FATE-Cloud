<template>
    <div class="exchange-info">
        <!-- 添加或编辑 -->
        <el-dialog :visible.sync="editdialog" class="access-edit-dialog" width="850px" :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="dialog-title">
                {{rollsiteType==='Edit'?'Edit':'Add'}} Rollsite
            </div>
            <div class="dialog-body">
                <el-form ref="editform" class="edit-form" :rules="editRules"  label-width="260px" label-position="left" :model="exchangeData">
                    <el-form-item label="Rollsite Network Access" prop="networkAccess" >
                        <span slot="label">
                            <span>Rollsite Network Access</span>
                            <i v-if="rollsiteType==='add'" style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                        <span v-if="rollsiteType==='edit'">{{exchangeData.networkAccess}}</span>
                        <el-input  v-else
                            @blur="cancelValid('networkAccess')"
                            @focus="cancelValid('networkAccess')"
                            @change="exchangeData.partyAddBeanList=[]"
                            v-model.trim="exchangeData.networkAccess"></el-input>
                    </el-form-item>
                    <div class="edit-text">
                        <span >Router Info
                            <i  class="el-icon-star-on"></i>
                        </span>
                        <el-button type="text" :disabled="!exchangeData.networkAccess" @click="showAddSiteNet" icon="el-icon-circle-plus"></el-button>
                        <el-button type="text" :disabled="!exchangeData.networkAccess" @click="toAcquire" icon="el-icon-refresh-right"></el-button>
                    </div>
                    <div class="edit-table">
                        <el-table :data="exchangeData.partyAddBeanList" max-height="250" >
                            <el-table-column type="index"  label="Index" width="65">
                            </el-table-column>
                            <el-table-column prop="partyId"  label="Party ID" width="80" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="networkAccess"  label="Site Network Access" width="150" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="secureStatus"  label="Is Secure" width="75" show-overflow-tooltip>
                                <template slot-scope="scope">
                                    <span>{{scope.row.secureStatus===1?'true':"false"}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="pollingStatus"  label="Is Polling" width="75" show-overflow-tooltip>
                                <template slot-scope="scope">
                                    <span>{{scope.row.pollingStatus===1?'true':"false"}}</span>
                                </template>
                            </el-table-column>
                             <el-table-column prop="updateTime"  label="Update Time" width="150" show-overflow-tooltip>
                                <template slot-scope="scope">
                                    <span>{{scope.row.updateTime | dateFormat}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="Status"  label="Status" show-overflow-tooltip>
                                <template slot-scope="scope">
                                    <span v-if="scope.row.status===1">Published</span>
                                    <span v-if="scope.row.status===2">Unpublished</span>
                                    <span v-if="scope.row.status===3">To be deleted</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="" align="right" label="Action" width="70">
                                 <template slot-scope="scope">
                                    <span v-if="scope.row.partyId==='default'">
                                        <el-button type="text" >
                                            <i @click="toEditSiteNet(scope)" class="el-icon-edit"></i>
                                        </el-button>
                                        <el-button disabled type="text" >
                                            <i  class="el-icon-close"></i>
                                        </el-button>
                                    </span>
                                    <span v-else>
                                        <span v-if="scope.row.status===1 || scope.row.status===2">
                                            <el-button type="text" >
                                                <i @click="toEditSiteNet(scope)" class="el-icon-edit"></i>
                                            </el-button>
                                            <el-button type="text" >
                                                <i @click="siteNetIndex = scope.$index;scope.row.status=3" class="el-icon-close"></i>
                                            </el-button>
                                        </span>
                                        <el-button v-if="scope.row.status===3" @click="toRecover(scope)" type="text">
                                            recover
                                        </el-button>
                                    </span>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-form>
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toaction">OK</el-button>
                <el-button class="ok-btn" type="info" @click="tocancel">Cancel</el-button>
            </div>
        </el-dialog>
        <!-- 添加site NetWork -->
        <el-dialog :visible.sync="addSiteNet" class="add-site-dialog" width="550px" :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="site-net-title">
                Site Network Access
            </div>
            <div class="site-net-table">
                <el-form ref="siteNetform" class="edit-form" :rules="siteEditRules"  label-width="195px" label-position="left" :model="tempSiteNet">
                    <el-form-item label="" prop="partyId">
                        <span slot="label">
                            <span>Party ID :</span>
                            <i v-if="siteNetType === 'add'" style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                         <span v-if="siteNetType === 'edit' " >
                            {{tempSiteNet.partyId}}
                        </span>
                        <el-input v-else
                            @blur="$refs['siteNetform'].clearValidate('partyId')"
                            @focus="$refs['siteNetform'].clearValidate('partyId')"
                            v-model.trim="tempSiteNet.partyId" ></el-input>
                    </el-form-item>
                    <el-form-item label="" prop="networkAccess" >
                        <span slot="label">
                            <span>Router Network Access:</span>
                            <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                        <span v-if="tempSiteNet.partyId==='default'">
                            {{tempSiteNet.networkAccess}}
                        </span>
                        <el-input v-else
                            @blur="$refs['siteNetform'].clearValidate('networkAccess')"
                            @focus="$refs['siteNetform'].clearValidate('networkAccess')"
                            v-model.trim="tempSiteNet.networkAccess"></el-input>
                    </el-form-item>
                    <el-form-item label="Is Secure:" prop="isSecure" >
                        <el-switch v-model="isSecure">
                        </el-switch>
                    </el-form-item>
                    <el-form-item label="Is Polling:" prop="isSecure" >
                        <span v-if="tempSiteNet.partyId==='exchange'">
                            <el-switch disabled v-model="isPolling">
                            </el-switch>
                        </span>
                        <span v-else>
                            <el-switch  v-model="isPolling">
                            </el-switch>
                        </span>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button class="ok-btn" type="primary" @click="toAddSiteNet">OK</el-button>
                    <el-button class="ok-btn" type="info" @click="cancelAddSiteNet">Cancel</el-button>
                </div>
            </div>
        </el-dialog>
          <!-- 是否保存信息弹框 -->
        <el-dialog :visible.sync="sureexchange" class="sure-exchange-dialog" width="700px">
            <div class="line-text-one">Are you sure you want to save this exchange? </div>
            <div class="line-text-two">Site network access info will update to server as well.</div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toSureEexchange">OK</el-button>
                <el-button class="ok-btn" type="info" @click="sureexchange = false">Cancel</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getNetworkAccessList, addRollsite, rollsiteUpdate } from '@/api/federated'

import moment from 'moment'

// import checkip from '@/utils/checkip'

export default {
    name: 'ipaddrollsite',
    filters: {
        dateFormat(value) {
            return moment(value).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            sureexchange: false,
            editdialog: false,
            addSiteNet: false,
            siteNetIndex: 0,
            tempExchangeDataList: [], // 临时保存数据
            exchangeId: '',
            rollsiteType: 'add',
            siteNetType: 'add',
            exchangeData: {
                networkAccess: '',
                partyAddBeanList: []
            }, // 添加数据
            tempSiteNet: { }, // sitenet数据
            partyIdList: [], // 临时的partyIdList列表
            isSecure: true,
            isPolling: true,
            siteEditRules: {
                partyId: [{
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(' '))
                        } else if (this.siteNetType === 'add' && this.partyIdList.includes(val)) {
                            callback(new Error('The party ID has been assigned router'))
                        } else {
                            callback()
                        }
                        // 取消只能输入数字校验
                        // else if (val !== 'default' && !(/(^[1-9]\d*$)/).test(val)) {
                        //     callback(new Error('The party ID invalid input'))
                        // }
                    }
                }],
                networkAccess: [
                    {
                        required: true,
                        trigger: 'bulr',
                        validator: (rule, value, callback) => {
                            value = value || ''
                            let val = value.trim()
                            if (!val) {
                                callback(new Error(' '))
                            } else {
                                callback()
                            }
                            // 取消检验ip
                            // else if (!checkip(val)) {
                            //     callback(new Error('The router network access invalid input '))
                            // }
                        }
                    }
                ]
            },
            editRules: {
                networkAccess: [
                    {
                        required: true,
                        trigger: 'bulr',
                        validator: (rule, value, callback) => {
                            value = value || ''
                            let val = value.trim()
                            if (!val) {
                                callback(new Error(' '))
                            } else {
                                callback()
                            }
                            // if (!val || !checkip(val)) {
                            //     callback(new Error(' '))
                            // }
                        }
                    }
                ]
            }
        }
    },

    created() {

    },
    mounted() {

    },
    methods: {
        // 确认添加rollsite
        toaction() {
            if (this.exchangeData.partyAddBeanList.length === 0) {
                this.$message.error('The router info is required')
                return
            }
            this.$refs['editform'].validate((valid) => {
                if (valid) {
                    if (this.rollsiteType === 'add') {
                        let data = {
                            exchangeId: this.exchangeId,
                            networkAccess: this.exchangeData.networkAccess.trim(),
                            partyAddBeanList: this.exchangeData.partyAddBeanList
                        }
                        addRollsite(data).then(res => {
                            this.editdialog = false
                            this.$parent.initList()
                        })
                    } else if (this.rollsiteType === 'edit') {
                        // 不要变化后的弹框
                        this.toSureEexchange()
                        // if (this.tempStatusStr !== JSON.stringify(this.exchangeData.partyAddBeanList)) {
                        //     this.sureexchange = true
                        // } else {
                        //     this.$parent.initList()
                        //     this.tocancel()
                        // }
                    }
                }
            })
        },
        // 缺认取消
        tocancel() {
            this.exchangeData = {
                networkAccess: '',
                partyAddBeanList: []
            }
            this.$refs['editform'].resetFields()
            this.editdialog = false
        },
        // 取消验证
        cancelValid(validtype) {
            this.$refs['editform'].clearValidate(validtype)
        },
        // 显示添加siteNet弹框
        showAddSiteNet() {
            this.siteNetType = 'add'
            this.isSecure = true
            this.tempSiteNet = { status: 2 }
            this.addSiteNet = true
            if (this.$refs['siteNetform']) {
                this.$refs['siteNetform'].resetFields()
            }
        },
        // 取消添加
        cancelAddSiteNet() {
            this.tempSiteNet = {}
            this.addSiteNet = false
            this.$refs['siteNetform'].resetFields()
        },
        // 确定添加siteNet
        toAddSiteNet() {
            this.tempSiteNet.secureStatus = this.isSecure === true ? 1 : 2
            this.tempSiteNet.pollingStatus = this.isPolling === true ? 1 : 2
            let arr = this.tempExchangeDataList[this.siteNetIndex] // 获取点击行临时数据
            this.partyIdList = this.tempExchangeDataList.map(item => {
                return item.partyId
            })
            this.$refs['siteNetform'].validate((valid) => {
                if (valid) {
                    if (this.siteNetType === 'edit') {
                        if (arr.networkAccess !== this.tempSiteNet.networkAccess ||
                        arr.secureStatus !== this.tempSiteNet.secureStatus ||
                        arr.pollingStatus !== this.tempSiteNet.pollingStatus) {
                            this.tempSiteNet.status = 2
                        } else {
                            this.tempSiteNet.status = arr.status
                        }
                        this.exchangeData.partyAddBeanList[this.siteNetIndex] = { ...this.tempSiteNet }
                        this.exchangeData.partyAddBeanList = [...this.exchangeData.partyAddBeanList]
                        this.addSiteNet = false
                    } else if (this.siteNetType === 'add') {
                        this.exchangeData.partyAddBeanList.push({ ...this.tempSiteNet })
                        this.exchangeData.partyAddBeanList = [...this.exchangeData.partyAddBeanList]
                        this.tempExchangeDataList = JSON.parse(JSON.stringify(this.exchangeData.partyAddBeanList)) // 临时数据
                        this.addSiteNet = false
                    }
                }
            })
        },
        // 获取
        toAcquire() {
            let data = {
                networkAccess: this.exchangeData.networkAccess
            }
            this.$refs['editform'].validate((valid) => {
                if (valid) {
                    getNetworkAccessList(data).then(res => {
                        this.exchangeData.partyAddBeanList = [ ...res.data ]
                        this.tempExchangeDataList = JSON.parse(JSON.stringify(res.data)) // 临时数据
                    })
                }
            })
        },
        // 编辑siteNet
        toEditSiteNet(scope) {
            this.siteNetType = 'edit'
            this.addSiteNet = true
            this.tempSiteNet = { ...scope.row }
            this.isSecure = this.tempSiteNet.secureStatus === 1
            this.isPolling = this.tempSiteNet.pollingStatus === 1
            this.siteNetIndex = scope.$index
        },
        // 缺认变更
        toSureEexchange() {
            let data = {
                partyAddBeanList: this.exchangeData.partyAddBeanList,
                rollSiteId: this.exchangeData.partyAddBeanList[0].rollSiteId
            }
            rollsiteUpdate(data).then(res => {
                this.tocancel()
                this.$parent.initList()
                this.sureexchange = false
            })
        },
        // 点击恢复
        toRecover(scope) {
            let arr = this.tempExchangeDataList[this.siteNetIndex] // 获取点击行临时数据
            let Arr = scope.row // 获取点击行临时数据
            if (arr.networkAccess !== Arr.networkAccess ||
                        arr.secureStatus !== Arr.secureStatus ||
                        arr.pollingStatus !== Arr.pollingStatus) {
                Arr.status = 2
            } else {
                Arr.status = arr.status
            }
            this.exchangeData.partyAddBeanList = [...this.exchangeData.partyAddBeanList]
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ip.scss';

</style>
