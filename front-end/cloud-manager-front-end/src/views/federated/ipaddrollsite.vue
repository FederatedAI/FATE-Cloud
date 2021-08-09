<template>
    <div >
        <!-- 添加或编辑 -->
        <el-dialog :visible.sync="editdialog" class="access-edit-dialog" width="872px" :show-close="true" :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="dialog-title">
                {{exchangeData.networkAccess ? `${$t('m.ip.editRollsite')}` : `${$t('m.ip.addRollsite')}`}}
            </div>
            <div class="dialog-body">
                <el-form ref="editform" class="edit-form" :rules="editRules"  label-width="260px" label-position="left" :model="exchangeData">
                    <el-form-item label="" prop="networkAccess" >
                        <span slot="label">
                            <i v-if="rollsiteType==='add'" style="margin-right: 3px;" class="el-icon-star-on"></i>
                            <span>{{$t('m.ip.rollsiteNetworkAccess')}}</span>
                        </span>
                        <span v-if="rollsiteType==='edit'">{{exchangeData.networkAccess}}</span>
                        <el-input  v-else
                            @blur="cancelValid('networkAccess')"
                            @focus="cancelValid('networkAccess')"
                            @change="exchangeData.partyAddBeanList=[]"
                            v-model.trim="exchangeData.networkAccess"></el-input>
                    </el-form-item>
                    <el-form-item label="" prop="networkAccessExit" >
                        <span slot="label">
                            <i v-if="rollsiteType==='add'" style="margin-right: 3px;" class="el-icon-star-on"></i>
                            <span>{{$t('m.site.networkExits')}}</span>
                        </span>
                        <span v-if="rollsiteType==='edit'">{{exchangeData.networkAccessExit}}</span>
                        <el-input  v-else
                            @blur="cancelValid('networkExits')"
                            @focus="cancelValid('networkExits')"
                            v-model.trim="exchangeData.networkAccessExit"></el-input>
                    </el-form-item>

                    <div class="edit-table">
                        <el-table :data="exchangeData.partyAddBeanList" max-height="250" >
                            <el-table-column>
                                <template slot="header" slot-scope="scope">
                                    <div class="edit-text">
                                        <span >{{$t('m.ip.routerInfo')}}</span>
                                        <el-button type="text" :disabled="!exchangeData.networkAccess" @click="toAcquire" icon="el-icon-refresh-right"></el-button>
                                        <el-button type="text" :disabled="!exchangeData.networkAccess" @click="showAddSiteNet" icon="el-icon-circle-plus"></el-button>
                                        <div class="search-input">
                                            <i slot="prefix" @click="toSearch" class="el-icon-search search" />
                                            <input type="text"  v-model="searchData.partyId" :placeholder="$t('m.common.serchForPlaceholder',{type:$t('m.common.partyID')})">
                                        </div>
                                        <el-button class="go" type="primary" @click="toSearch">{{$t('m.common.go')}}</el-button>
                                    </div>
                                </template>
                                <el-table-column type="index"  :label="$t('m.common.index')" width="70">
                                </el-table-column>
                                <el-table-column prop="partyId"  :label="$t('m.common.partyID')"  show-overflow-tooltip>
                                </el-table-column>
                                <el-table-column prop="networkAccess"  :label="$t('m.ip.routerNetworkAccess')" width="180" show-overflow-tooltip>
                                </el-table-column>
                                <el-table-column prop="secureStatus" :label="$t('m.ip.isSecure')"  show-overflow-tooltip>
                                    <template slot-scope="scope">
                                        <span>{{scope.row.secureStatus===1? $t('m.common.true') : $t('m.common.false') }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="pollingStatus" :label="$t('m.ip.isPolling')"  show-overflow-tooltip>
                                    <template slot-scope="scope">
                                        <span>{{scope.row.pollingStatus===1? $t('m.common.true') : $t('m.common.false') }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="updateTime"  :label="$t('m.common.updateTime')" width="130" show-overflow-tooltip>
                                    <template slot-scope="scope">
                                        <span>{{scope.row.updateTime | dateFormat}}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="Status" :label="$t('m.ip.status')" width="80" show-overflow-tooltip>
                                    <template slot-scope="scope">
                                        <span v-if="scope.row.status===0">{{$t('m.common.unactivated')}}</span>
                                        <span v-if="scope.row.status===1">{{$t('m.common.published')}}</span>
                                        <span v-if="scope.row.status===2">{{$t('m.common.unpublished')}}</span>
                                        <span v-if="scope.row.status===3">{{$t('m.common.toDeleted')}}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="" align="center" :label="$t('m.common.action')" >
                                    <template slot-scope="scope">
                                        <span v-if="scope.row.partyId==='default'">
                                            <el-button type="text" :disabled="scope.row.using">
                                                <i @click="toEditSiteNet(scope)" class="el-icon-edit"></i>
                                            </el-button>
                                            <el-button disabled type="text" >
                                                <i  class="el-icon-close"></i>
                                            </el-button>
                                        </span>
                                        <span v-else>
                                            <span v-if="scope.row.status===1 || scope.row.status===2">
                                                <el-button type="text" :disabled="scope.row.using">
                                                    <i @click="toEditSiteNet(scope)" class="el-icon-edit"></i>
                                                </el-button>
                                                <el-button type="text" :disabled="scope.row.using">
                                                    <i @click="siteNetIndex = scope.$index;scope.row.status=3" class="el-icon-close"></i>
                                                </el-button>
                                            </span>
                                            <el-button v-if="scope.row.status===3" @click="toRecover(scope)" type="text">
                                                {{$t('m.common.recover')}}
                                            </el-button>
                                        </span>
                                    </template>
                                </el-table-column>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-form>
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toaction">{{$t('m.common.OK')}}</el-button>
                <el-button class="ok-btn" type="info" @click="tocancel">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <!-- 添加site NetWork -->
        <el-dialog :visible.sync="addSiteNet" class="add-site-dialog" width="500px" :show-close="true" :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="site-net-title">
                {{$t('m.ip.routerNetworkAccess')}}
            </div>
            <div class="site-net-table">
                <el-form ref="siteNetform" class="edit-form" :rules="siteEditRules"  label-width="200px" label-position="top" :model="tempSiteNet">
                    <el-form-item class="inline" :class="{'inline':tempSiteNet.partyId === 'default'}" label="" prop="partyId">
                        <span slot="label">
                            <i v-if="siteNetType === 'add'" style="margin-right: 3px;" class="el-icon-star-on"></i>
                            <span>{{$t('m.common.partyID')}} :</span>
                        </span>
                         <span v-if="(siteNetType === 'edit' && tempSiteNet.status===1) || tempSiteNet.partyId === 'default'" >
                            {{tempSiteNet.partyId}}
                        </span>
                        <el-input v-else
                            @blur="checkParty"
                            @focus="$refs['siteNetform'].clearValidate('partyId')"
                            v-model.trim="tempSiteNet.partyId" ></el-input>
                    </el-form-item>
                    <el-form-item label="" style="height:100%;" prop="networkAccess" >
                        <span slot="label">
                            <i style="margin-right: 3px;" class="el-icon-star-on"></i>
                            <span>{{$t('m.ip.routerNetworkAccess')}}：</span>
                        </span>
                        <el-input
                            @focus="addShow('router')"
                            :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn':routerNetworkAccesswarnshow }"
                            v-model="tempSiteNet.networkAccess"
                            placeholder >
                            <i slot="suffix" @click="addShow('router')" class="el-icon-plus plus" />
                        </el-input>
                    </el-form-item>

                     <el-form-item class="inline" :label="$t('m.ip.isSecure')" prop="isSecure" >
                         <span slot="label">
                            <i style="margin-right: 3px;" class="el-icon-star-on"></i>
                            <span>isSecure:</span>
                         </span>
                        <el-switch v-model="isSecure">
                        </el-switch>
                    </el-form-item>
                    <el-form-item class="inline" :label="$t('m.ip.isPolling')" prop="isPolling" >
                        <span slot="label">
                            <i style="margin-right: 3px;" class="el-icon-star-on"></i>
                            <span>isPolling:</span>
                        </span>
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
                    <el-button class="ok-btn" type="primary" @click="toAddSiteNet">{{$t('m.common.submit')}}</el-button>
                    <el-button class="ok-btn" type="info" @click="cancelAddSiteNet">{{$t('m.common.cancel')}}</el-button>
                </div>
            </div>
        </el-dialog>
        <!-- 是否保存信息弹框 -->
        <el-dialog :visible.sync="sureexchange" class="sure-exchange-dialog" width="700px">
            <div class="line-text-one">{{$t('m.ip.sureWantSaveExchange')}}</div>
            <div class="line-text-two">{{$t('m.ip.updateToServer')}}</div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toSureEexchange">{{$t('m.common.OK')}}</el-button>
                <el-button class="ok-btn" type="info" @click="sureexchange = false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <ipeditadd ref="ipeditadd" @updateIp="updateIp" :formName="formName" />

    </div>
</template>

<script>
import { getNetworkAccessList, addRollsite, rollsiteUpdate, searchByPartyId, checkParty } from '@/api/federated'
import moment from 'moment'
import ipeditadd from './ipeditadd'
// import checkip from '@/utils/checkip'

export default {
    name: 'ipaddrollsite',
    filters: {
        dateFormat(value) {
            return moment(value).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    components: { ipeditadd },
    data() {
        return {
            sureexchange: false,
            editdialog: false,
            addSiteNet: false,
            siteNetIndex: 0,
            tempExchangeDataList: [], // 临时保存数据做对比用
            exchangeId: '',
            rollsiteType: 'add',
            siteNetType: 'add',
            formName: 'siteNetform',
            routerNetworkAccesswarnshow: false,
            exchangeData: {
                networkAccess: '',
                networkAccessExit: '',
                partyAddBeanList: []
            }, // 添加数据
            tempSiteNet: {
                partyId: 'default',
                networkAccess: '',
                isSecure: true,
                isPolling: true
            }, // sitenet数据
            partyIdList: [], // 临时的partyIdList列表
            isSecure: true,
            isPolling: true,
            tempPartyId: '', // 临时PartyId比较
            searchData: {
                partyId: '',
                rollSiteId: ''
            },
            checkResult: '',
            siteEditRules: {
                partyId: [{
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(this.$t('m.common.requiredfieldWithType', { type: this.$t('m.common.partyID') })))
                        // } else if (this.partyIdList.includes(val) && val !== this.tempPartyId) {
                        } else if (this.checkResult) {
                            callback(new Error(this.$t('m.ip.partyIDAssigned')))
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
                                callback(new Error(this.$t('m.common.requiredfieldWithType', { type: this.$t('m.ip.routerNetworkAccess') })))
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
        addShow(type) {
            this.$refs['ipeditadd'].networkacesstype = type
            this.$refs['ipeditadd'].adddialog = true

            let editType = {
                'router': 'networkAccess'
            }
            let parameterName = editType[type]
            if (this.tempSiteNet[parameterName]) {
                let tempArr = []
                this.tempSiteNet[parameterName].split(';').forEach(item => {
                    if (item) {
                        let obj = {}
                        obj.ip = item
                        obj.show = false
                        obj.checked = false
                        tempArr.push(obj)
                    }
                })
                this.$refs['ipeditadd'].entrancesSelect = [...new Set(tempArr)]
            } else {
                this.$refs['ipeditadd'].entrancesSelect = []
            }
        },
        updateIp(data) {
            console.log(data, 'data')
            this.$set(this.tempSiteNet, `${data.name}`, data.data)
        },
        // 确认添加rollsite
        toaction() {
            if (this.exchangeData.partyAddBeanList.length === 0) {
                this.$message.error(this.$t('m.ip.routerInfoRequired'))
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
                        this.toSureEexchange()
                        // 不要变化后的弹框
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
                networkAccessExit: '',
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
            // 此时需刷新列表重置缓存
            this.$set(this.searchData, 'partyId', '')
            this.toAcquire()
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
            let tempArr = this.tempExchangeDataList[this.siteNetIndex] // 获取点击编辑行临时数据
            this.partyIdList = this.tempExchangeDataList.map(item => {
                return item.partyId
            })
            this.$refs['siteNetform'].validate((valid) => {
                if (valid) {
                    if (this.siteNetType === 'edit') {
                        if (tempArr.networkAccess !== this.tempSiteNet.networkAccess ||
                        tempArr.secureStatus !== this.tempSiteNet.secureStatus ||
                        tempArr.pollingStatus !== this.tempSiteNet.pollingStatus) {
                            this.tempSiteNet.status = 2
                        } else {
                            this.tempSiteNet.status = tempArr.status
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
        toSearch() {
            searchByPartyId(this.searchData).then(res => {
                this.exchangeData.partyAddBeanList = [ ...res.data ]
                this.tempExchangeDataList = JSON.parse(JSON.stringify(res.data)) // 临时数据
            })
        },
        // 编辑siteNet
        toEditSiteNet(scope) {
            this.siteNetType = 'edit'
            this.addSiteNet = true
            this.tempSiteNet = { ...scope.row } // 点击编辑行临时数据
            this.isSecure = this.tempSiteNet.secureStatus === 1
            this.isPolling = this.tempSiteNet.pollingStatus === 1
            this.siteNetIndex = scope.$index //
            this.tempPartyId = scope.row.partyId
        },
        // 确认变更
        toSureEexchange() {
            let data = {
                partyAddBeanList: this.exchangeData.partyAddBeanList,
                rollSiteId: this.searchData.rollSiteId
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
        },
        checkParty() {
            let self = this
            let partyId = self.tempSiteNet.partyId
            checkParty({ partyId }).then(res => {
                if (res.data === true) {
                    this.checkResult = res.data
                    this.$refs['siteNetform'].validateField('partyId', valid => {})
                }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ip.scss';

</style>
