<template>
    <div class="activate">
        <div class="site-add">
            <div class="add-info">
            <div class="title">
                <span>{{$t('m.welcome.activateSite')}}</span>
            </div>
            <el-form ref="infoform" :rules="formRules" :model="form" label-position="left" label-width="250px" >
                <!-- <el-form-item label="Federated Organization" prop="stiename">
                    <span class="info-text">{{form.federatedOrganization}}</span>
                </el-form-item> -->
                <div class="module-title">{{$t('m.sitemanage.basicInfo')}}</div>
                <el-form-item :label="$t('m.common.siteName')" prop="stiename">
                    <span class="info-text">{{form.siteName}}</span>
                </el-form-item>
                <el-form-item :label="$t('m.common.institution',{type:'I'})" prop="institution">
                    <span class="info-text">{{form.institutions}}</span>
                </el-form-item>
                <el-form-item :label="$t('m.common.role')" prop="role">
                    <span class="info-text">{{form.role | getSiteType}}</span>
                </el-form-item>
                <el-form-item :label="$t('m.common.partyID')">
                    <span class="info-text">{{form.partyId}}</span>
                </el-form-item>

                <el-form-item label="Federation Key">
                    <span v-if="keyViewDefault" class="info-text">{{form.appKey}} <img src="@/assets/view_show.png" @click="keyViewDefault = !keyViewDefault" class="view" ></span>
                    <span  v-if="!keyViewDefault" class="info-text">***********************<img src="@/assets/view_hide.png" @click="keyViewDefault = !keyViewDefault" class="view" ></span>
                </el-form-item>
                <el-form-item label="Federation Secret">
                    <span v-if="secretViewDefault" class="info-text">{{form.appSecret}} <img src="@/assets/view_show.png" @click="secretViewDefault = !secretViewDefault" class="view" ></span>
                    <span  v-if="!secretViewDefault" class="info-text">***********************<img src="@/assets/view_hide.png" @click="secretViewDefault = !secretViewDefault" class="view" ></span>
                </el-form-item>
                <el-form-item :label="$t('m.sitemanage.registrationLink')">
                    <el-popover
                        placement="top"
                        width="400"
                        trigger="hover"
                        :content="form.registrationLink">
                        <span slot="reference" class="link-text">{{form.registrationLink}}</span>
                    </el-popover>
                </el-form-item>
                <div class="module-title">{{$t('m.sitemanage.siteNetworkConf')}}</div>
                <el-form-item class="ip-input is-required" :label="$t('m.sitemanage.networkEntrances')" style="margin-bottom:22px" prop="networkAccessEntrances">
                    <el-input
                    class="plus-text"
                    @focus="addShow('entrances')"
                    @blur="cancelValid('networkAccessEntrances')"
                    v-model="form.networkAccessEntrances"
                    placeholder  >
                    <i slot="suffix" @click="addShow('entrances')" class="el-icon-plus plus" />
                </el-input>
                </el-form-item>
                <div class="module-title">{{$t('m.sitemanage.rollsiteNetworkConf')}}</div>
                <el-form-item class="ip-input is-required" :label="$t('m.sitemanage.rollsiteEntrances')" prop="fmRollSiteNetworkEntrances">
                    <el-input
                        @focus="addShow('rollsite')"
                        @blur="cancelValid('fmRollSiteNetworkEntrances')"
                        class="plus-text"
                        v-model="form.fmRollSiteNetworkEntrances"
                        placeholder>
                        <i slot="suffix" @click="addShow('rollsite')" class="el-icon-plus plus" />
                    </el-input>
                </el-form-item>
                <el-form-item class="ip-input is-required" :label="$t('m.sitemanage.rollsiteExits')"  prop="fmRollSiteNetworkAccessExitsList">
                    <el-input
                        @focus="addShow('exit')"
                        @blur="cancelValid('fmRollSiteNetworkAccessExitsList')"
                        class="plus-text"
                        v-model="form.fmRollSiteNetworkAccessExitsList"
                        placeholder>
                        <i slot="suffix" @click="addShow('exit')" class="el-icon-plus plus" />
                    </el-input>
                </el-form-item>
                <el-form-item class="inline" :label="$t('m.siteAdd.isSecure')" prop="secureStatus" >
                    <el-switch v-model="form.secureStatus"></el-switch>
                </el-form-item>
                <el-form-item class="inline" :label="$t('m.siteAdd.isPolling')" prop="pollingStatus" >
                    <el-switch v-model="form.pollingStatus"></el-switch>
                </el-form-item>
            </el-form>
            <div class="Submit">
                <el-button type="primary" @click="modifyAction">{{$t('m.welcome.confirmAndActivate')}}</el-button>
            </div>
            </div>
            <el-dialog :visible.sync="confirmdialog" class="site-toleave-dialog" width="550px" :close-on-click-modal="false" :close-on-press-escape="false">
                <i class="el-icon-success"></i>
                <div class="line-text-success">{{$t('m.welcome.activateSuccessfully')}}</div>
                <div class="line-text-one">{{$t('m.welcome.activateSuccessfully')}}</div>
                <div class="dialog-footer">
                    <el-button class="sure-btn" type="primary" @click="confirm">{{$t('m.common.OK')}}</el-button>
                </div>
            </el-dialog>
            <siteaddip ref="siteaddip"/>
        </div>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { register } from '@/api/welcomepage'
// import { decode64, utf8to16 } from '@/utils/base64'
import siteaddip from './siteaddip'

export default {
    name: 'home',
    components: {
        siteaddip
    },
    data() {
        return {
            keyViewDefault: false,
            secretViewDefault: false,
            confirmdialog: false, //
            form: {
                networkAccessEntrances: '',
                fmRollSiteNetworkAccessExitsList: '',
                fmRollSiteNetworkEntrances: ''
            },
            rollSiteNetworkAccesswarnshow: false,
            formRules: {
                networkAccessEntrances: [{
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(this.$t('m.siteAdd.networkAccessEntrancesRequired')))
                        } else {
                            callback()
                        }
                    }
                }],
                fmRollSiteNetworkAccessExitsList: [{
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(this.$t('m.siteAdd.networkAccessExitRequired')))
                        } else {
                            callback()
                        }
                    }
                }],
                fmRollSiteNetworkEntrances: [{
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val) {
                            callback(new Error(this.$t('m.siteAdd.rollSiteNetworkAccessRequired')))
                        } else {
                            callback()
                        }
                    }
                }]
            }

        }
    },
    watch: {},
    computed: {
        ...mapGetters(['organization'])
    },

    beforeDestroy() {},
    created() {
        // this.$store.dispatch('selectEnum')
    },
    mounted() {
        let obj = {}
        // 此处需要添加缓存，避免刷新导致数据丢失
        if (this.$route.query.data !== '[object Object]') {
            obj = this.$route.query.data
            localStorage.setItem('activateData', JSON.stringify(obj))
        } else {
            obj = JSON.parse(localStorage.getItem('activateData'))
        }
        // console.log(obj, 'obj')
        let fromObj = {}
        fromObj.appKey = obj.secretInfo.key
        fromObj.appSecret = obj.secretInfo.secret
        fromObj.registrationLink = obj.registrationLink // 回传加密
        fromObj.federatedOrganization = obj.federatedOrganization
        fromObj.id = obj.federatedOrganizationId
        fromObj.institutions = obj.institutions
        fromObj.networkAccessEntrances = ''
        fromObj.fmRollSiteNetworkAccessExitsList = ''
        fromObj.fmRollSiteNetworkEntrances = ''
        fromObj.partyId = obj.partyId
        fromObj.role = obj.role
        fromObj.siteName = obj.siteName
        fromObj.cmRollSiteNetworkAccessExitsList = obj.rollSiteNetworkAccessExits === ';' ? '' : obj.rollSiteNetworkAccessExits
        fromObj.pollingStatus = false
        fromObj.secureStatus = false
        fromObj.exchangeName = obj.exchangeName
        fromObj.vipEntrance = obj.vipEntrance
        fromObj.siteId = obj.id
        // fromObj.exchangeNetworkAccess = ''
        // fromObj.exchangeNetworkAccessExits = ''
        // fromObj.network = obj.network
        if (obj.rollSiteDoList && obj.rollSiteDoList.length > 0) {
            fromObj.exchangeNetworkAccess = obj.rollSiteDoList.map(item => item.networkAccess).join(';')
            fromObj.exchangeNetworkAccessExits = obj.rollSiteDoList.map(item => item.networkAccessExit).join(';')
        }
        this.form = { ...fromObj }
    },
    methods: {
        // 添加/编辑出入口
        addShow(type) {
            this.$refs['siteaddip'].networkacesstype = type
            this.$refs['siteaddip'].adddialog = true
            let editType = {
                'entrances': 'networkAccessEntrances',
                'exit': 'fmRollSiteNetworkAccessExitsList',
                'rollsite': 'fmRollSiteNetworkEntrances'
            }
            let parameterName = editType[type]
            if (this.form[parameterName]) {
                let tempArr = []
                this.form[parameterName].split(';').forEach(item => {
                    if (item) {
                        let obj = {}
                        obj.ip = item
                        obj.show = false
                        obj.checked = false
                        tempArr.push(obj)
                    }
                })
                this.$refs['siteaddip'].entrancesSelect = [...new Set(tempArr)]
            } else {
                this.$refs['siteaddip'].entrancesSelect = []
            }
        },
        // 取消表单验证
        cancelValid(validtype) {
            this.$refs['infoform'].clearValidate(validtype)
            this[`${validtype}warnshow`] = false
        },
        modifyAction() {
            let data = { ...this.form }
            let secureStatus = this.getStatus(data.secureStatus)
            let pollingStatus = this.getStatus(data.pollingStatus)
            data.secureStatus = secureStatus
            data.pollingStatus = pollingStatus
            // console.log(data, 'sub-data')
            this.$refs['infoform'].validate((valid) => {
                if (valid) {
                    register(data).then((res) => {
                        this.confirmdialog = true
                        this.$store.dispatch('setSiteStatus', 'registered')
                        this.$router.push({
                            name: 'sitemanage',
                            path: 'sitemanage'
                        })
                    })
                }
            })
        },
        // OK
        confirm() {
            this.$router.push({
                name: 'auto',
                query: {
                    siteName: this.form.siteName,
                    federatedId: this.form.federatedId || this.form.federatedOrganization,
                    partyId: this.form.partyId
                }
            })
        },
        getStatus(stauts) {
            if (typeof stauts === 'number') {
                return stauts === 1
            } else {
                return stauts === true ? 1 : 2
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/activate.scss';

</style>
