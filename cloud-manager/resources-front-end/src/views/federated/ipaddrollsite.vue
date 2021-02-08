<template>
    <div class="exchange-info">
        <!-- 添加或编辑 -->
        <el-dialog :visible.sync="editdialog" class="access-edit-dialog" width="720px" :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="dialog-title">
                Add Rollsite
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
                        <el-button type="text" @click="showAddSiteNet" icon="el-icon-circle-plus"></el-button>
                        <el-button type="text" :disabled="!exchangeData.networkAccess" @click="toAcquire" icon="el-icon-refresh-right"></el-button>
                    </div>
                    <div class="edit-table">
                        <el-table :data="exchangeData.partyAddBeanList" max-height="250" >
                            <el-table-column type="index"  label="Index" width="65">
                            </el-table-column>
                            <el-table-column prop="partyId"  label="Party ID" width="80" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="networkAccess"  label="Site Network Access" width="160" show-overflow-tooltip>
                            </el-table-column>
                             <el-table-column prop="updateTime"  label="Update Time" width="160" show-overflow-tooltip>
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
                                        <el-button disabled type="text" >
                                            <i  class="el-icon-edit"></i>
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
                                                <i @click="scope.row.status=3" class="el-icon-close"></i>
                                            </el-button>
                                        </span>
                                        <el-button @click="scope.row.status=2" v-if="scope.row.status===3" type="text" >
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
                         <span v-if="siteNetType === 'edit'">
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
                        <el-input
                            @blur="$refs['siteNetform'].clearValidate('networkAccess')"
                            @focus="$refs['siteNetform'].clearValidate('networkAccess')"
                            v-model.trim="tempSiteNet.networkAccess"></el-input>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button class="ok-btn" type="primary" @click="toAddSiteNet">OK</el-button>
                    <el-button class="ok-btn" type="info" @click="cancelAddSiteNet">Cancel</el-button>
                </div>
            </div>
        </el-dialog>
          <!-- 不同信息 -->
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

import checkip from '@/utils/checkip'

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
            tempStatusStr: '{}',
            exchangeId: '',
            rollsiteType: 'add',
            siteNetType: 'add',
            exchangeData: {
                networkAccess: '',
                partyAddBeanList: []
            }, // 添加数据
            tempSiteNet: {}, // sitenet数据
            partyIdList: [], // 临时的partyIdList列表
            siteEditRules: {
                partyId: [{
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(' '))
                        } else if (!(/(^[1-9]\d*$)/).test(val)) {
                            callback(new Error('The party ID invalid input'))
                        } else if (this.siteNetType === 'add' && this.partyIdList.includes(val)) {
                            callback(new Error('The party ID has been assigned router'))
                        } else {
                            callback()
                        }
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
                            } else if (!checkip(val)) {
                                callback(new Error('The router network access invalid input '))
                            } else {
                                callback()
                            }
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
                            if (!val || !checkip(val)) {
                                callback(new Error(' '))
                            } else {
                                callback()
                            }
                        }
                    }
                ]
            }
        }
    },
    // watch: {
    //     exchangeId: {
    //         handler(val) {
    //             console.log('exchangeId++>>', val)
    //         }
    //     },
    //     rollsiteType: {
    //         handler(val) {
    //             console.log('rollsiteType++>>', val)
    //         }
    //     }
    // },
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
            let arr = JSON.parse(this.tempStatusStr)[this.siteNetIndex]
            this.partyIdList = this.exchangeData.partyAddBeanList.map(item => {
                return item.partyId
            })
            this.$refs['siteNetform'].validate((valid) => {
                if (valid) {
                    if (this.siteNetType === 'edit') {
                        if (arr.networkAccess !== this.tempSiteNet.networkAccess) {
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
                        this.tempStatusStr = JSON.stringify(res.data)
                    })
                }
            })
        },
        // 编辑siteNet
        toEditSiteNet(scope) {
            this.siteNetType = 'edit'
            this.addSiteNet = true
            this.tempSiteNet = { ...scope.row }
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
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ip.scss';

</style>
