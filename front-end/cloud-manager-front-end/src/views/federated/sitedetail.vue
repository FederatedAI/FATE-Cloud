
<template>
  <div class="site-detail-box">
    <div class="site-detail">
      <div class="info-box">
        <div class="info">{{$t('Basic Info')}}</div>
        <el-form ref="form" :model="form" label-position="left" label-width="180px">
          <el-row :gutter="10">
            <el-col :span="12">
                <el-form-item  :label="$t('Site Name')" >
                    <span class="info-text">{{form.siteName}}</span>
                </el-form-item>
                <el-form-item :label="$t('Institution')" >
                    <span class="link-text">{{form.institutions}}</span>
                </el-form-item>
                <el-form-item :label="$t('Role')">
                    <span class="info-text">{{form.role===1? $t('m.common.guest') : $t('m.common.host') }}</span>
                </el-form-item>
                <el-form-item label="Federation key">
                    <span class="info-text">{{form.secretInfo?form.secretInfo.key:form.secretInfo}}</span>
                </el-form-item>
                <el-form-item label="Secret Key">
                    <el-popover
                        v-if="secretkeyshow"
                        placement="top"
                        width="300"
                        trigger="hover"
                        :content="form.secretInfo?form.secretInfo.secret:form.secretInfo">
                        <span slot="reference" class="link-text secretkey">{{form.secretInfo?form.secretInfo.secret:form.secretInfo}}</span>
                    </el-popover>
                    <span v-else class="link-text secretkey">{{form.secretInfo?form.secretInfo.secret:form.secretInfo}}</span>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item :label="$t('Status')">
                  <span v-if="form.status===2" class="info-text">{{$t('Joined')}}</span>
                  <span v-if="form.status===3" class="info-text">{{$t('Delete')}}</span>
                </el-form-item>
                <el-form-item :label="$t('Party ID')">
                  <span class="info-text">{{form.partyId}}</span>
                </el-form-item>
                <el-form-item :label="$t('Creation Time')">
                      <el-popover
                        v-show="showTime"
                        placement="top"
                        trigger="hover"
                        :content="form.createTime | dateFormat">
                        <span slot="reference" class="link-text time">{{form.createTime | dateFormat}}</span>
                    </el-popover>
                  <span v-show="!showTime"  class="link-text time">{{form.createTime | dateFormat}}</span>
                </el-form-item>
                <el-form-item :label="$t('Activation Time')" >
                     <el-popover
                        v-if="showTime"
                        placement="top"
                        trigger="hover"
                        :content="form.activationTime | dateFormat">
                        <span slot="reference" class="link-text time">{{form.activationTime | dateFormat}}</span>
                    </el-popover>
                  <span v-if="!showTime" class="link-text time">{{form.activationTime | dateFormat}}</span>
                </el-form-item>
                <el-form-item :label="$t('Registration Link')">
                    <el-popover
                        placement="top"
                        width="300"
                        trigger="hover"
                        :content="form.registrationLink">
                        <span slot="reference" class="link-text">{{form.registrationLink}}</span>
                    </el-popover>
                </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="info-box">
        <div class="info">{{$t('Network configuration')}}</div>
        <el-form ref="form" :model="form" label-position="left" label-width="280px">
            <el-row :gutter="140">
                <el-col :span="12">
                    <el-form-item v-if="form.networkAccessEntrances" style="height:100%;" :label="$t('Network Acess Entrances')" >
                        <span v-for="(item,index) in form.networkAccessEntrances.split(';')" :key='index'>
                            <div style="width:100%;"  v-if="item" class="info-text ">
                            {{item}}
                            </div>
                        </span>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item v-if="form.networkAccessExits" style="height:100%;" :label="$t('Network Acess Exits')" >
                        <span v-for="(item,index) in form.networkAccessExits.split(';')" :key='index'>
                            <div style="width:100%;"  v-if="item" class="info-text ">
                            {{item}}
                            </div>
                        </span>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
      </div>
      <div class="info-box">
        <div class="info">{{$t('System version')}}</div>
            <el-radio-group class="radio" v-model="radio">
                <el-radio-button label="FATE"></el-radio-button>
                <el-radio-button disabled label="FATE Serving"></el-radio-button>
            </el-radio-group>
            <el-tooltip effect="dark" placement="top">
                <div style="font-size:14px" slot="content">
                    <div>including FATE-Board, FATE-Flow</div>
                </div>
                <i class="el-icon-info icon-info"></i>
            </el-tooltip>
            <div class="fate-version">
                <span class="fate-inline">{{$t('FATE version')}}</span>
                <span class="fate-text">{{form.fateVersion}}</span>
            </div>
            <div class="table">
                <div class="title">
                    <div class="title-text">{{$t('FATE Component')}}</div>
                    <div class="title-text">{{$t('Version')}}</div>
                    <div class="title-text">IP</div>
                </div>
                <div class="body" v-for="(item, index) in form.componentVersion" :key="index">
                    <div class="body-text">{{item.label}}</div>
                    <div class="body-text">{{item.version.version}}</div>
                    <div class="body-text">{{item.version.address}}</div>
                </div>
            </div>
      </div>
    </div>
  </div>
</template>

<script>

import { getSiteInfo } from '@/api/federated'
import moment from 'moment'
import elementResizeDetectorMaker from 'element-resize-detector'
// 国际化
const local = {
    zh: {
        'Basic Info': '基本信息',
        'Status': '状态',
        'Joined': '已加入',
        'Delete': '已删除',
        'Site Name': '站点名称',
        'Institution': '站点机构',
        'Role': '站点角色',
        'Creation Time': '创建时间',
        'Activation Time': '激活时间',
        'Registration Link': '注册链接',
        'Network configuration': '网关设置',
        'Network Acess Entrances': '网关入口',
        'Network Acess Exits': '网关出口',
        'System version': '系统版本',
        'FATE version': 'FATE版本',
        'FATE Component': 'FATE服务组件',
        'Version': '版本'
    },
    en: {
        'Basic Info': 'Basic Info',
        'Status': 'Status',
        'Joined': 'Joined',
        'Delete': 'Delete',
        'Site Name': 'Site Name',
        'Institution': 'Institution',
        'Role': 'Role',
        'Creation Time': 'Creation Time',
        'Activation Time': 'Activation Time',
        'Registration Link': 'Registration Link',
        'Network configuration': 'Network configuration',
        'Network Acess Entrances': 'Network Acess Entrances',
        'Network Acess Exits': 'Network Acess Exits',
        'System version': 'System version',
        'FATE version': 'FATE version',
        'FATE Component': 'FATE Component',
        'Version': 'Version'

    }
}

export default {
    name: 'home',
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '- -'
        }
    },
    data() {
        return {
            showTime: false, // 是否显示弹框
            timeWidth: '', // 监听宽度
            secretkeyshow: false,
            secretkeyWidth: '',
            form: {},
            versionList: [], // 版本
            radio: 'FATE'

        }
    },
    watch: {
        timeWidth: {
            handler: function(val) {
                if (val < 170) {
                    this.showTime = true
                } else {
                    this.showTime = false
                }
            },
            deep: true,
            immediate: true
        },
        secretkeyWidth: {
            handler: function(val) {
                if (val < 375) {
                    this.secretkeyshow = true
                } else {
                    this.secretkeyshow = false
                }
            },
            deep: true,
            immediate: true
        }
    },
    computed: {},
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        this.$nextTick(() => {
            this.getKeySansLink()
        })
    },
    mounted() {
        let erd = elementResizeDetectorMaker()
        let that = this
        // 监听元素width变化
        erd.listenTo(document.querySelectorAll('.time'), function(element) {
            that.timeWidth = element.offsetWidth
        })
        erd.listenTo(document.querySelectorAll('.secretkey'), function(element) {
            that.secretkeyWidth = element.offsetWidth
        })
    },
    methods: {
        getKeySansLink() {
            let data = {
                id: parseInt(this.$route.query.id)
            }
            getSiteInfo(data).then(res => {
                res.data.registrationLink = JSON.stringify(res.data.registrationLink).replaceAll('"', '')
                this.form = { ...res.data }
                this.form.componentVersion = []
                if (res.data.componentVersion) {
                    let object = JSON.parse(res.data.componentVersion)
                    for (const key in object) {
                        let obj = {}
                        obj.label = key
                        obj.version = object[key]
                        this.form.componentVersion.push(obj)
                    }
                }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/sitedetail.scss';
</style>
