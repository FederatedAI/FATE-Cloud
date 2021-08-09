<template>
    <div >
        <!-- 添加或编辑 -->
        <el-dialog
            class="ip-edit-dialog"
            :visible.sync="editdialog"
            width="430px"
            :show-close="true"
            :close-on-click-modal="false"
            :close-on-press-escape="false">
            <div class="dialog-title">
                {{$t('m.common.edit')}}
            </div>
            <div class="dialog-body">
                <el-form ref="siteNetform" label-position="top" class="edit-form" :rules="siteEditRules"  label-width="80px" :model="tempSiteNet">
                    <el-form-item class="inline" label="partyId：" prop="partyId">
                        <template>
                           <span class="edit-text">{{tempSiteNet.partyId}}</span>
                        </template>
                    </el-form-item>
                    <!-- <el-form-item label="" prop="networkAccess" label-width="200px">
                        <div slot="label">
                            <span>{{$t('m.ip.routerNetworkAccess')}}:</span>
                        </div>
                        <span v-if="tempSiteNet.partyId==='default'">
                            {{tempSiteNet.networkAccess}}
                        </span>
                        <el-input v-else
                            @blur="$refs['siteNetform'].clearValidate('networkAccess')"
                            @focus="$refs['siteNetform'].clearValidate('networkAccess')"
                            v-model.trim="tempSiteNet.networkAccess"></el-input>
                    </el-form-item> -->
                    <el-form-item :label="$t('m.site.networkEntrances')+'：'" style="height:100%;" prop="networkAccessEntrances" >
                        <el-input
                            @focus="addShow('entrances')"
                            @blur="cancelValid('networkAccessEntrances')"
                            :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': networkAccessEntranceswarnshow }"
                            v-model="tempSiteNet.networkAccessEntrances"
                            placeholder >
                            <i slot="suffix" @click="addShow('entrances')" class="el-icon-plus plus" />
                        </el-input>
                    </el-form-item>
                    <el-form-item :label="$t('m.site.networkExits')+'：'" style="height:100%;" prop="networkAccessExits" >
                        <el-input
                            @focus="addShow('exit')"
                            @blur="cancelValid('networkAccessExits')"
                            :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': networkAccessExitswarnshow }"
                            v-model="tempSiteNet.networkAccessExits"
                            placeholder >
                            <i slot="suffix" @click="addShow('exit')" class="el-icon-plus plus" />
                        </el-input>
                    </el-form-item>
                    <el-form-item label="Exchange :" style="height:100%;" prop="networkAccessExits">
                        <el-select class="sel-role input-placeholder" v-model="tempSiteNet.exchangeId" clearable placeholder="">
                            <el-option
                                v-for="item in exchangeSelect"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item class="inline" :label="$t('m.ip.isSecure')" prop="secureStatus" >
                        <el-switch v-model="tempSiteNet.secureStatus"></el-switch>
                    </el-form-item>
                    <el-form-item class="inline" :label="$t('m.ip.isPolling')" prop="pollingStatus" >
                        <el-switch v-model="tempSiteNet.pollingStatus"></el-switch>
                    </el-form-item>
                </el-form>
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toaction">{{$t('m.common.submit')}}</el-button>
                <el-button class="ok-btn" type="info" @click="tocancel">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <!-- 是否保存信息弹框 -->
        <el-dialog :visible.sync="sureEdit" class="sure-dialog" width="400px">
            <div class="dialog-title">{{$t('m.ip.sureWantSaveChanges')}}</div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toSureEdit">{{$t('m.common.sure')}}</el-button>
                <el-button class="ok-btn" type="info" @click="sureEdit = false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <ipeditadd ref="ipeditadd" @updateIp="updateIp" :formName="formName" />
    </div>
</template>

<script>
import moment from 'moment'
import ipeditadd from './ipeditadd'
import { getExchangeList } from '@/api/federated'

export default {
    name: 'ipedit',
    filters: {
        dateFormat(value) {
            return moment(value).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    components: { ipeditadd },
    props: {
        rowData: {
            type: Object,
            default: () => {
                return {}
            }
        }
    },
    watch: {
        rowData: {
            handler: function(val) {
                if (val) {
                    console.log(val, 'watch-rowData')
                    const {
                        partyId,
                        networkAccessEntrancesOld: networkAccessEntrances,
                        networkAccessExitsOld: networkAccessExits,
                        exchangeId,
                        secureStatus,
                        pollingStatus,
                        id } = val
                    this.tempSiteNet = { partyId, networkAccessEntrances, networkAccessExits, exchangeId, secureStatus, pollingStatus, id }
                    this.tempSiteNet.secureStatus = this.getStuts(secureStatus)
                    this.tempSiteNet.pollingStatus = this.getStuts(pollingStatus)
                    this.cacheTempSiteNet = JSON.parse(JSON.stringify(this.tempSiteNet)) // 缓存数据
                }
            },
            deep: true
        }
    },
    data() {
        return {
            editdialog: false,
            isSecure: true,
            pollingStatus: true,
            tempPartyId: '', // 临时PartyId比较
            formName: 'siteNetform',
            tempSiteNet: {
                partyId: '',
                networkAccessEntrances: '',
                networkAccessExits: '',
                exchangeId: '',
                secureStatus: true,
                pollingStatus: true,
                id: ''
            },
            cacheTempSiteNet: {},
            networkAccessEntranceswarnshow: false,
            networkAccessExitswarnshow: false,
            sureEdit: false,
            exchangeSelect: [],
            siteEditRules: {
                networkAccess: [
                    {
                        required: false,
                        trigger: 'bulr',
                        validator: (rule, value, callback) => {
                            value = value || ''
                            let val = value.trim()
                            if (!val) {
                                callback(new Error(' '))
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
                            if (!val) {
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

    created() {
        this.getExchangeList()
    },
    mounted() {

    },
    methods: {
        getExchangeList() {
            getExchangeList().then(res => {
                res.data.forEach(item => {
                    let obj = {}
                    obj.value = item.exchangeId
                    obj.label = item.exchangeName
                    this.exchangeSelect.push(obj)
                })
            })
        },
        // 取消表单验证
        cancelValid(validtype) {
            this.$refs['siteNetform'].clearValidate(validtype)
            this[`${validtype}warnshow`] = false
        },
        addShow(type) {
            this.$refs['ipeditadd'].networkacesstype = type
            this.$refs['ipeditadd'].adddialog = true

            let editType = {
                'entrances': 'networkAccessEntrances',
                'exit': 'networkAccessExits',
                'rollsite': 'rollsiteNetworkAccess'
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
        toSureEdit() {
            let secureStatus = this.getStuts(this.tempSiteNet.secureStatus)
            let pollingStatus = this.getStuts(this.tempSiteNet.pollingStatus)
            this.$set(this.tempSiteNet, 'secureStatus', secureStatus)
            this.$set(this.tempSiteNet, 'pollingStatus', pollingStatus)
            this.$emit('updateRowData', this.tempSiteNet)
            this.sureEdit = false
        },
        toaction() {
            if (JSON.stringify(this.cacheTempSiteNet) !== JSON.stringify(this.tempSiteNet)) {
                this.sureEdit = true
            } else {
                this.editdialog = false
            }
        },
        tocancel() {
            this.editdialog = false
            this.tempSiteNet = JSON.parse(JSON.stringify(this.cacheTempSiteNet))
        },
        getStuts(stauts) {
            console.log(typeof stauts, 'typeof stauts')
            if (typeof stauts === 'number') {
                return stauts === 1
            } else {
                return stauts === true ? 1 : 2
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ip.scss';

</style>
