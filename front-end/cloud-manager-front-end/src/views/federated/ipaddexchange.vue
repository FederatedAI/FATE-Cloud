<template>
  <div class="ipaddexchange-box">
        <div class="add-exchange">
            <div class="exchange-title">{{$t('m.ip.addExchange')}}</div>
            <el-form ref="editform" class="edit-form" :rules="editRules"  label-width="100px" label-position="left" :model="exchangeData">
                <el-form-item label="Exchange" prop="exchangeName" >
                    <span slot="label">
                        <span>Exchange</span>
                        <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                    </span>
                    <el-input @blur="cancelValid('exchangeName')" @focus="cancelValid('exchangeName')" v-model="exchangeData.exchangeName"></el-input>
                </el-form-item>
                <el-form-item label="VIP" prop="vip" >
                    <span slot="label">
                        <span>VIP</span>
                        <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                    </span>
                    <el-input @blur="cancelValid('vip')" @focus="cancelValid('vip')" v-model.trim="exchangeData.vip"></el-input>
                </el-form-item>
                <div class="edit-text">
                    <span>{{$t('m.ip.rollsiteAndRouterInfo')}}</span>
                    <el-button type="text" @click="addRollsite" icon="el-icon-circle-plus"></el-button>
                </div>
                <span class="rollsite-text" v-for="(item, index) in rollsiteList" :key="index">
                    <el-form-item :label="$t('m.ip.rollsiteNetworkAccess')" prop="networkAccess" >
                        <span slot="label">
                            <span>{{$t('m.ip.rollsiteNetworkAccess')}}</span>
                            <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                        <el-input :class="{inputwarn: item.warnshow }"
                            @focus="item.warnshow = false"
                            @change="item.partyAddBeanList = []"
                            v-model.trim="item.networkAccess">
                        </el-input>
                        <el-button type="text"  :class="{remove:rollsiteList.length!==1}" :disabled="rollsiteList.length===1" @click="removeRollsite(index)"  icon="el-icon-remove"></el-button>
                    </el-form-item>
                    <div class="router-info">
                        <span style="color:#217AD9">
                            {{$t('m.ip.routerInfo')}}
                            <i  class="el-icon-star-on"></i>
                        </span>
                        <el-button type="text"  :disabled="!item.networkAccess" @click="showAddSiteNet(index)" icon="el-icon-circle-plus"></el-button>
                        <el-button type="text"  :disabled="!item.networkAccess"  @click="toAcquire(index)" icon="el-icon-refresh-right"></el-button>
                    </div>
                    <div class="edit-table">
                        <el-table :data="rollsiteList[index].partyAddBeanList" max-height="250" >
                            <el-table-column type="index" :label="$t('m.common.index')" width="65"  >
                            </el-table-column>
                            <el-table-column prop="partyId" :label="$t('m.common.partyID')"  width="75" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="networkAccess"  :label="$t('m.ip.siteNetworkAccess')"  width="160" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="secureStatus" :label="$t('m.ip.isSecure')" width="85">
                                <template slot-scope="scope">
                                    <span>{{scope.row.secureStatus===1 ? $t('m.common.true') : $t('m.common.false') }}</span>
                                </template>
                            </el-table-column>
                             <el-table-column prop="pollingStatus"  :label="$t('m.ip.isPolling')" width="85">
                                <template slot-scope="scope">
                                    <span>{{scope.row.pollingStatus===1 ? $t('m.common.true') : $t('m.common.false') }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="Update Time" :label="$t('m.common.updateTime')"  width="155" show-overflow-tooltip>
                                <template slot-scope="scope">
                                    <span>{{scope.row.updateTime | dateFormat}}</span>
                                </template>
                            </el-table-column>
                             <el-table-column prop="status" align="center" :label="$t('m.ip.status')" show-overflow-tooltip>
                                 <template slot-scope="scope">
                                    <span v-if="scope.row.status===1">{{$t('m.common.published')}}</span>
                                    <span v-if="scope.row.status===2">{{$t('m.common.unpublished')}}</span>
                                    <span v-if="scope.row.status===3">{{$t('m.common.toDeleted')}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="" align="right" :label="$t('m.common.action')" >
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
                                                <i @click="siteNetIndex = scope.$index;scope.row.status=3"  class="el-icon-close"></i>
                                            </el-button>
                                        </span>
                                        <el-button v-if="scope.row.status===3" @click="toRecover(scope)" type="text">
                                            {{$t('m.common.recover')}}
                                        </el-button>
                                    </span>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </span>

            </el-form>
            <div class="exchange-footer">
                <el-button class="ok-btn" type="primary" @click="toaction">{{$t('m.common.OK')}}</el-button>
                <el-button class="ok-btn" type="info" @click="tocancel">{{$t('m.common.cancel')}}</el-button>
            </div>
        </div>

         <!-- 添加site NetWork -->
        <el-dialog :visible.sync="addSiteNet" class="add-site-dialog" width="560px" :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="site-net-title">
                {{$t('m.ip.siteNetworkAccess')}}
            </div>
            <div class="site-net-table">
                <el-form ref="siteNetform" class="edit-form" :rules="siteEditRules"  label-width="210px" label-position="left" :model="tempSiteNet">
                    <el-form-item label="" prop="partyId">
                        <span slot="label">
                            <span>{{$t('m.common.partyID')}} :</span>
                            <i v-if="siteNetType === 'add'" style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                         <span v-if="siteNetType === 'edit'  && tempSiteNet.status===1" >
                            {{tempSiteNet.partyId}}
                        </span>
                        <el-input v-else
                            @blur="$refs['siteNetform'].clearValidate('partyId')"
                            @focus="$refs['siteNetform'].clearValidate('partyId')"
                            v-model.trim="tempSiteNet.partyId" ></el-input>
                    </el-form-item>
                    <el-form-item label="" prop="networkAccess" >
                        <span slot="label">
                            <span>{{$t('m.ip.routerNetworkAccess')}}:</span>
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
                    <el-form-item :label="$t('m.ip.isSecure')" prop="isSecure" >
                        <span slot="label">
                            <span>{{$t('m.ip.isSecure')}}:</span>
                        </span>
                        <el-switch v-model="isSecure">
                        </el-switch>
                    </el-form-item>
                    <el-form-item :label="$t('m.ip.isPolling')" prop="isPolling" >
                        <span slot="label">
                            <span>{{$t('m.ip.isPolling')}}:</span>
                        </span>
                        <el-switch v-model="isPolling">
                        </el-switch>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button class="ok-btn" type="primary" @click="toAddSiteNet">{{$t('m.common.OK')}}</el-button>
                    <el-button class="ok-btn" type="info" @click="cancelAddSiteNet">{{$t('m.common.cancel')}}</el-button>
                </div>
            </div>
        </el-dialog>
        <!-- 弹框离开 -->
        <el-dialog :visible.sync="isleavedialog" class="site-toleave-dialog" width="700px">
            <div class="line-text-one">{{$t('m.siteAdd.sureLeavePage')}}</div>
            <div class="line-text-two">{{$t('m.siteAdd.notSavedTips')}}</div>
            <div class="dialog-footer">
                <el-button class="sure-btn" type="primary" @click="sureLeave">{{$t('m.common.sure')}}</el-button>
                <el-button class="sure-btn" type="info" @click="cancelLeave">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
  </div>
</template>

<script>
import { addIpchange, getNetworkAccessList } from '@/api/federated'
import moment from 'moment'
// import checkip from '@/utils/checkip'

export default {
    name: 'ipaddexchange',
    components: {

    },
    filters: {
        dateFormat(value) {
            return moment(value).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            addSiteNet: false, // 弹框
            isleave: false, // 是否可以离开路由
            isleavedialog: false, // 离开弹框
            siteNetIndex: 0, // 编辑框内第几行
            rollsiteIndex: 0,
            rollsiteList: [{
                warnshow: false,
                networkAccess: '',
                partyAddBeanList: []
            } ],
            tempExchangeDataList: [], // 临时保存数据做对比用
            tempSiteNet: {}, // 弹框SiteNet数据
            exchangeData: {},
            isSecure: false,
            isPolling: true,
            siteNetType: 'add',
            partyIdList: [], // 临时的partyIdList列表
            tempPartyId: '', // 临时PartyId比较
            siteEditRules: {
                partyId: [{
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(this.$t('m.ip.partIDRequired')))
                        } else if (this.partyIdList.includes(val) && val !== this.tempPartyId) {
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
                networkAccess: [{ required: true, message: ' ', trigger: 'bulr' }]
            },
            editRules: {
                exchangeName: [{ required: true, message: ' ', trigger: 'bulr' }],
                vip: [ {
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(this.$t('m.ip.vipRequired')))
                        } else {
                            callback()
                        }
                        // 取消检验ip
                        // if (!val || !checkip(val)) {
                        //     callback(new Error(' '))
                        // }
                    }
                } ]
            }
        }
    },
    created() {
        // this.getInstitutionsToday()
        // this.getSiteToday()
    },

    mounted() {
        this.$router.beforeEach((to, from, next) => {
            this.leaveRouteName = to.name
            if (this.isleave) {
                next()
            } else {
                this.isleavedialog = true
                // 中断路由
                next(false)
            }
        })
    },
    methods: {
        // 添加循环
        addRollsite() {
            this.rollsiteList.unshift({ warnshow: false, networkAccess: '', partyAddBeanList: [] })
        },
        // 显示添加siteNet弹框
        showAddSiteNet(index) {
            this.rollsiteIndex = index
            this.siteNetType = 'add'
            this.isSecure = true
            this.tempSiteNet = { status: 2 }
            this.addSiteNet = true
            if (this.$refs['siteNetform']) {
                this.$refs['siteNetform'].resetFields()
            }
        },
        // 去除列表值
        removeRollsite(index) {
            this.rollsiteList.splice(index, 1)
        },
        // 确定添加
        toaction() {
            let passAdd = true
            let msgshow = true
            let Arr = []
            let arr = []
            this.rollsiteList.forEach((item, index) => {
                // if (!item.networkAccess || !checkip(item.networkAccess)) {
                //     this.rollsiteList[index].warnshow = true
                // }
                if (!item.networkAccess) {
                    this.rollsiteList[index].warnshow = true
                }
                if (item.partyAddBeanList.length === 0) {
                    passAdd = false
                }
                Arr.push(item.networkAccess)
                arr = [...new Set(Arr)]
                // 重复提示弹框
                if (Arr.length !== arr.length) {
                    msgshow = false
                    this.rollsiteList[index].warnshow = true
                }
            })

            if (!passAdd) {
                this.$message.error(this.$t('m.ip.routerInfoRequired'))
                return
            }
            if (!msgshow) {
                this.$message.error(this.$t('m.ip.rollsiteNetworkExists'))
                return
            }
            this.$refs['editform'].validate((valid) => {
                if (valid && passAdd && msgshow) {
                    let data = {
                        exchangeName: this.exchangeData.exchangeName.trim(),
                        rollSiteAddBeanList: this.rollsiteList,
                        vip: this.exchangeData.vip
                    }
                    addIpchange(data).then(res => {
                        this.isleave = true
                        this.$router.push({
                            name: 'IP Manage',
                            query: { type: 'exchange' }
                        })
                    })
                }
            })
        },
        // 确定取消
        tocancel() {
            this.$router.push({
                name: 'IP Manage',
                query: { type: 'exchange' }
            })
        },
        // 取消验证
        cancelValid(validtype) {
            this.$refs['editform'].clearValidate(validtype)
        },
        // 确定添加siteNet
        toAddSiteNet() {
            let index = this.rollsiteIndex
            this.tempSiteNet.secureStatus = this.isSecure === true ? 1 : 2
            this.tempSiteNet.pollingStatus = this.isPolling === true ? 1 : 2
            let tempArr = this.tempExchangeDataList[index][this.siteNetIndex] // 获取点击编辑行临时数据

            this.partyIdList = this.tempExchangeDataList[index].map(item => {
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
                        this.rollsiteList[index].partyAddBeanList[this.siteNetIndex] = { ...this.tempSiteNet }
                        this.rollsiteList[index].partyAddBeanList = [...this.rollsiteList[index].partyAddBeanList]
                        this.addSiteNet = false
                    } else if (this.siteNetType === 'add') {
                        this.rollsiteList[index].partyAddBeanList.push({ ...this.tempSiteNet })
                        this.rollsiteList[index].partyAddBeanList = [...this.rollsiteList[index].partyAddBeanList]
                        this.tempExchangeDataList[index] = JSON.parse(JSON.stringify(this.rollsiteList[index].partyAddBeanList)) // 临时数据
                        this.addSiteNet = false
                    }
                }
            })
        },
        // 取消添加
        cancelAddSiteNet() {
            this.tempSiteNet = {}
            this.addSiteNet = false
            this.$refs['siteNetform'].resetFields()
        },
        // 编辑siteNet
        toEditSiteNet(scope) {
            this.siteNetType = 'edit'
            this.addSiteNet = true
            this.tempSiteNet = { ...scope.row }
            this.isSecure = this.tempSiteNet.secureStatus === 1
            this.isPolling = this.tempSiteNet.pollingStatus === 1
            this.siteNetIndex = scope.$index
            this.tempPartyId = scope.row.partyId
        },
        // 删除siteNet行
        todelSiteNet(index, scope) {
            this.rollsiteList[index].partyAddBeanList.splice(scope.$index, 1)
        },
        // 获取列表
        toAcquire(index) {
            this.rollsiteIndex = index
            let data = {
                networkAccess: this.rollsiteList[index].networkAccess
            }
            getNetworkAccessList(data).then(res => {
                this.rollsiteList[index].partyAddBeanList = res.data
                this.tempExchangeDataList[index] = JSON.parse(JSON.stringify(res.data)) // 临时数据
            }).catch(res => {
                let nowMessage = document.querySelector('.el-message')
                if (!nowMessage) {
                    this.$message.error(res.msg)
                }
            })
        },
        // 确定离开
        sureLeave() {
            this.isleave = true
            this.isleavedialog = false
            let query = this.leaveRouteName === 'IP Manage' ? { type: 'exchange' } : ''
            this.$router.push({
                name: this.leaveRouteName,
                query
            })
        },
        // 取消离开
        cancelLeave() {
            this.$router.push({
                name: 'IP Manage',
                type: 'exchange'
            })
            this.$store.dispatch('SetMune', 'IP Manage')
            this.isleavedialog = false
        },
        // 点击恢复
        toRecover(scope) {
            let index = this.rollsiteIndex
            let arr = this.tempExchangeDataList[this.rollsiteIndex][this.siteNetIndex] // 获取点击行临时数据
            let Arr = scope.row // 获取点击行临时数据
            if (arr.networkAccess !== Arr.networkAccess ||
                        arr.secureStatus !== Arr.secureStatus ||
                        arr.pollingStatus !== Arr.pollingStatus) {
                Arr.status = 2
            } else {
                Arr.status = arr.status
            }
            this.rollsiteList[index].partyAddBeanList = [...this.rollsiteList[index].partyAddBeanList]
        }
    }

}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ipaddexchange.scss';

</style>
