<template>
  <div class="register">
    <div class="title">{{$t('Federated Organization Register')}}</div>
    <div class="organization">
      <div class="name-tip">
        <span>{{$t('Federated Organization Name')}}</span>
      </div>
      <div class="name">
        <el-input
          v-model="inputName"
          :class="{ active: nameActive }"
          :placeholder="$t('input Federated Organization Name')"
        ></el-input>
      </div>
    </div>
    <div class="organization">
      <div class="name-tip">
        <span>{{$t('Institution')}}</span>
      </div>
      <div class="name">
        <el-input
          v-model="inputInst"
          :class="{ active: instActive }"
          :placeholder="$t('input institution')"
        ></el-input>
      </div>
      <el-button class="OK-btn" :type="type" :disabled="disabledbtn" @click="dialogVisible=true">{{$t('m.common.OK')}}</el-button>
      <el-dialog :visible.sync="dialogVisible" :close-on-click-modal="false" :close-on-press-escape="false" width="775px">
        <div class="line-text-one">{{$t('You are creating your federated organization:')}}</div>
        <div class="line-text-one">{{$t('your name is :')}}
            <span style="color:#217AD9">  {{inputName}} </span>

        </div>
        <div class="line-text-one">{{$t('your institution is :')}}
            <span style="color:#217AD9"> {{inputInst}} </span>
        </div>
        <div class="line-text-two">{{$t('The organization information will be synchronized to your federated sites.')}}</div>
        <div class="line-text-three">{{$t('Are you sure to creat it ?')}}</div>
        <div class="dialog-footer">
          <el-button class="sure-btn" type="primary" @click="sureAction">{{$t('m.common.sure')}}</el-button>
          <el-button class="cancel-btn" type="info" @click="dialogVisible = false">{{$t('m.common.cancel')}}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { register, find } from '@/api/welcomepage'
// 国际化
const local = {
    zh: {
        'Federated Organization Register': '联邦组织注册',
        'Federated Organization Name': '联邦组织名称',
        'Institution': '管理机构',
        'You are creating your federated organization:': '你正在创建以下联邦组织',
        'your name is :': '组织名称',
        'your institution is :': '管理机构',
        'The organization information will be synchronized to your federated sites.': '组织信息会同步到管理的个联邦站点处',
        'Are you sure to creat it ?': '确认以上述信息创建组织吗？',
        'input Federated Organization Name': '输入联合组织名称',
        'input institution': '输入机构名称'
    },
    en: {
        'Federated Organization Register': 'Federated Organization Register',
        'Federated Organization Name': 'Federated Organization Name',
        'Institution': 'Institution',
        'You are creating your federated organization:': 'You are creating your federated organization:',
        'your name is :': 'your name is :',
        'your institution is :': 'your institution is :',
        'The organization information will be synchronized to your federated sites.': 'The organization information will be synchronized to your federated sites.',
        'Are you sure to creat it ?': 'Are you sure to creat it ?',
        'input Federated Organization Name': 'input Federated Organization Name',
        'input institution': 'input institution'
    }
}

export default {
    name: 'home',
    components: {},
    data() {
        return {
            dialogVisible: false,
            nameActive: false,
            instActive: false,
            disabledbtn: true,
            type: 'info',
            inputName: '',
            inputInst: ''
        }
    },
    watch: {
        inputName: {
            handler: function(val) {
                this.showBtn()
                if (val) {
                    this.nameActive = true
                } else {
                    this.nameActive = false
                }
            },
            immediate: true
        },
        inputInst: {
            handler: function(val) {
                this.showBtn()
                if (val) {
                    this.instActive = true
                } else {
                    this.instActive = false
                }
            },
            immediate: true
        }
    },
    computed: {},
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    methods: {
        showBtn() {
            if (this.inputName && this.inputInst) {
                this.disabledbtn = false
                this.type = 'primary'
            } else {
                this.disabledbtn = true
                this.type = 'info'
            }
        },
        sureAction() {
            // this.dialogVisible = true
            let params = {
                institution: this.inputInst,
                name: this.inputName
            }
            register(params).then((res) => {
                this.$store.dispatch('setSiteStatus', 'registered')// 注册
                find().then(res => {
                    if (res.data.name) {
                        this.$store.dispatch('getInfo', res.data)
                        this.dialogVisible = true
                        this.$router.push({
                            name: 'Site Manage',
                            path: '/federated/site',
                            query: {}
                        })
                    } else {
                        this.$router.push({
                            name: 'register'
                        })
                    }
                })
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
</style>
