<template>

  <div class="switch-box">

        <!-- <div class="card"  >
                <div class="card-title">{{$t('Auto-deploy')}}</div>
                <div class="card-switch" @click="toswitch('auto')">
                    <el-switch v-model="autostatus" ></el-switch>
                </div>
                <div class="card-text">
                    {{$t('Manage the automated deployment and upgrade of the site')}}
                </div>
        </div> -->
        <div class="card">
            <div class="card-title">{{$t('Site-Authorization')}}</div>
            <div class="card-switch"  @click="toswitch('site')">
                <el-switch v-model="sitestatus"></el-switch>
            </div>
            <div class="card-text">{{$t("Control whether a FATE Manager can view other FATE Manager's sites")}}</div>
            <div class="desc">
                <!-- 丛向 -->
                <span v-if="descriptions==='3'">{{$t('Hetero federation only')}}</span>
                <!-- 横向 -->
                <span v-if="descriptions==='2'">{{$t('Homo federation only')}}</span>
                <!-- 混合 -->
                <span v-if="descriptions==='1'">{{$t('Hetero federation and homo federation')}}</span>
            </div>
        </div>
        <popupdialog
            dialogTitle=" "
            :visible.sync="switchVisible"
            @updateVisible="updateSwitchVisible"
            @resetPopupData="cancelAction"
            @submitPopupData="okAction"
            @handleClose="cancelAction"
            :dialogWidth="'500px'"
            :buttons="{
                sure: {
                    text:$t('m.common.sure'),
                },
                cancel: $t('m.common.cancel'),
                reject: null,
            }"
        >
            <div class="line-text-one">
                <span v-if="$t('zh')==='en'">
                    Are you sure you want to turn {{ status }} the function of
                    <div>
                        “<span style="color: #217AD9;">{{itmetext}}</span>”?
                    </div>
                </span>
                <span v-if="$t('zh')==='中'">
                    <span v-if="status==='off'">
                        确认关闭
                        <span v-if="itmetext==='Auto-deploy'">
                            “自动部署”
                        </span>
                        <span v-if="itmetext==='Site-Authorization'">
                            “站点授权”
                        </span>
                        功能吗?
                    </span>
                    <span v-if="status==='on'">
                        确认开启
                        <span v-if="itmetext==='Auto-deploy'">
                            “自动部署”
                        </span>
                        <span v-if="itmetext==='Site-Authorization'">
                            “站点授权”
                        </span>
                        功能吗
                    </span>
                </span>
            </div>
            <div class="line-text-two">
                <span v-if="$t('zh')==='en'">
                    After {{ statusdeploy }}, all related functions of
                    {{itmeline}}
                    be available.
                </span>
                <span v-if="$t('zh')==='中'">
                    <span v-if="statusdeploy==='opening'">
                        <span v-if="itmetext==='Auto-deploy'">
                            功能开启后，自动化部署及站点升级相关功能可以使用
                        </span>
                        <span v-if="itmetext==='Site-Authorization'">
                            功能开启后，站点授权相关功能可以使用
                        </span>
                    </span>
                    <span v-if="statusdeploy==='shutdown'">
                        <span v-if="itmetext==='Auto-deploy'">
                            功能关闭后，自动化部署及站点升级相关功能将无法使用
                        </span>
                        <span v-if="itmetext==='Site-Authorization'">
                            功能关闭后，站点授权相关功能将无法使用
                        </span>
                    </span>
                </span>
            </div>
        </popupdialog>
        <popupdialog
            dialogTitle=" "
            :visible.sync="selectVisible"
            @updateVisible="updateRadioVisible"
            @submitPopupData="sureVisible"
            @resetPopupData="cancelAction"
            @handleClose="cancelAction"
            :dialogWidth="'500px'"
            :buttons="{
                sure: {
                    text:$t('m.common.sure'),
                    disabled:!selectRadio
                },
                cancel: $t('m.common.cancel'),
                reject: null,
            }"
        >
            <div class="dialog-title">
                {{$t('Please select the supported federated scenario')}}
            </div>
            <el-radio-group v-model="selectRadio">
                <div class="select-item">
                    <el-radio :label="'3'">{{$t('Hetero federation only')}}</el-radio>
                </div>
                    <div class="select-item">
                    <el-radio :label="'2'">{{$t('Homo federation only')}}</el-radio>
                </div>
                    <div class="select-item">
                    <el-radio :label="'1'">{{$t('Hetero federation and homo federation')}}</el-radio>
                </div>
            </el-radio-group>
        </popupdialog>
        <popupdialog
            dialogTitle=" "
            :visible.sync="sureVisible"
            @updateVisible="updateSureVisible"
            @submitPopupData="selectSure"
            @resetPopupData="cancelAction"
            @handleClose="cancelAction"
            :dialogWidth="'400px'"
            :buttons="{
                sure: {
                    text:$t('m.common.sure'),
                    disabled: false
                },
                cancel: $t('m.common.cancel'),
                reject: null,
            }"
        >
            <div class="main-tips">
                {{$t('Please confirm the option again.')}}
            </div>
            <div class="gray-tips">
                {{$t('Once selected, it cannot be modified.')}}
            </div>
        </popupdialog>
  </div>

</template>

<script>
import { switchState, updateSwitch, siteUpdateStatus } from '@/api/setting'
import popupdialog from '@/components/dialog'
// import { mapGetters } from 'vuex'
// 国际化
const local = {
    zh: {
        'Auto-deploy': '自动部署',
        'Site-Authorization': '站点授权',
        'Manage the automated deployment and upgrade of the site': '管理自动化部署及站点升级相关功能',
        "Control whether a FATE Manager can view other FATE Manager's sites": '管理FATE Manager是否可以查看其它联邦成员的站点信息',
        'zh': '中',
        'Sure': '确定',
        'Cancel': '取消',
        'Please select the supported federated scenario': '请选择支持的联邦场景',
        'Hetero federation only': '仅支持纵向联邦',
        'Homo federation only': '仅支持横向联邦',
        'Hetero federation and homo federation': '同时支持纵向及横向联邦',
        'Please confirm the option again.': '请确认所选场景是否有误。',
        'Once selected, it cannot be modified.': '联邦场景选择后，无法再做更改。'
    },
    en: {
        'Auto-deploy': 'Auto-deploy',
        'Site-Authorization': 'Site-Authorization',
        'Manage the automated deployment and upgrade of the site': 'Manage the automated deployment and upgrade of the site',
        "Control whether a FATE Manager can view other FATE Manager's sites": "Control whether a FATE Manager can view other FATE Manager's sites",
        'zh': 'en',
        'Sure': 'Sure',
        'Cancel': 'Cancel',
        'Please select the supported federated scenario': 'Please select the supported federated scenario',
        'Hetero federation only': 'Hetero federation only',
        'Homo federation only': 'Homo federation only',
        'Hetero federation and homo federation': 'Hetero federation and homo federation',
        'Please confirm the option again.': 'Please confirm the option again. ',
        'Once selected, it cannot be modified.': 'Once selected, it cannot be modified.'

    }
}
export default {
    name: 'switchsys',
    components: { popupdialog },
    // computed: {
    //     ...mapGetters(['autostatus','sitestatus'])
    // },
    data() {
        return {

            status: '',
            statusdeploy: '',
            switchVisible: false,
            selectVisible: false,
            sureVisible: false,
            selectRadio: '',
            autostatus: false, // 是否打开，默认关闭
            sitestatus: false, // 是否打开，默认关闭
            itmetext: '',
            itmeline: '',
            descriptions: '',
            typedialog: '', // 弹框类型
            paramsData: {
                functionId: '',
                status: ''
            },
            functionIdObj: {}
        }
    },
    created() {
        this.init()
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    mounted() {

    },
    methods: {
        updateSwitchVisible(val) {
            this.switchVisible = !val
        },
        updateRadioVisible(val) {
            this.selectVisible = !val
        },
        updateSureVisible(val) {
            this.sureVisible = !val
        },
        init() {
            switchState().then(res => {
                res.data.forEach(item => {
                    if (item.functionName === 'Auto-Deploy') {
                        this.autostatus = item.status === 1
                        this.$store.dispatch('Getautostatus', this.autostatus)
                        this.functionIdObj['auto'] = item.functionId
                    }
                    if (item.functionName === 'Site-Authorization') {
                        this.sitestatus = item.status === 1
                        this.$store.dispatch('Getsitestatus', this.sitestatus)
                        this.functionIdObj['site'] = item.functionId
                        if (item.descriptions) {
                            this.descriptions = item.descriptions
                        } else {
                            this.descriptions = ''
                        }
                    }
                })
            })
        },
        // 确定改变状态
        okAction() {
            if (this.typedialog === 'site' && this.sitestatus && !this.descriptions) {
                this.selectRadio = ''
                this.switchVisible = false
                this.selectVisible = true
            } else {
                this.updated()
            }
        },
        updated() {
            updateSwitch(this.paramsData).then(res => {
                this.switchVisible = false
                this.init()
            }).catch(res => {
                this.cancelAction()
            })
        },
        // 取消状态
        cancelAction() {
            this.switchVisible = false
            this.selectVisible = false
            this.sureVisible = false
            this.init()
        },
        // 确定选中
        selectSure() {
            let data = {
                functionId: this.paramsData.functionId,
                scenarioType: this.selectRadio
            }
            siteUpdateStatus(data).then(res => {
                this.sureVisible = false
                this.selectVisible = false
                this.updated()
            })
        },
        // 点击开关
        toswitch(type) {
            this.typedialog = type
            this.status = ''
            this.switchVisible = true // 弹框
            this.paramsData.functionId = this.functionIdObj[type]
            if (type === 'auto') {
                if (this.autostatus) {
                    this.paramsData.status = 1
                    this.status = 'on'
                    this.statusdeploy = 'opening'
                    this.itmetext = 'Auto-deploy'
                    this.itmeline = 'automatic deployment and upgrade will'
                } else {
                    this.paramsData.status = 2
                    this.status = 'off'
                    this.itmetext = 'Auto-deploy'
                    this.statusdeploy = 'shutdown'
                    this.itmeline = 'automatic deployment and upgrade will not'
                }
            } else if (type === 'site') {
                if (this.sitestatus) {
                    this.paramsData.status = 1
                    this.status = 'on'
                    this.statusdeploy = 'opening'
                    this.itmetext = 'Site-Authorization'
                    this.itmeline = 'Site-Authorization will'
                } else {
                    this.paramsData.status = 2
                    this.status = 'off'
                    this.statusdeploy = 'shutdown'
                    this.itmetext = 'Site-Authorization'
                    this.itmeline = 'Site-Authorization will not'
                }
            }
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/switch.scss';
</style>
