<template>
    <div>
        <el-dialog :visible.sync="activDialog" :close-on-click-modal="false" :close-on-press-escape="false" class="activ-dialog" width="1000px">
            <div class="dialog-header">{{$t('Admin activation')}}</div>
            <div class="dialog-body">
                <el-form ref="activteForm" :model="activteForm" :rules="activteRules" class="activte-form" label-position="left">
                    <div class="text-one">{{$t('Please enter the administrator invitation link from Cloud Manager')}}</div>
                    <el-form-item prop="link">
                        <el-input v-model="activteForm.link" @blur="decodeLink" type="text" clearable @focus="toclearValid('link')">
                        </el-input>
                    </el-form-item>
                    <div class="text-one">{{$t('Please bind an administrator account')}}</div>
                    <el-form-item prop="userName">
                        <el-input v-model="activteForm.userName" type='text' clearable @focus="toclearValid('userName')">
                        </el-input>
                    </el-form-item>
                    <div class="text-one">{{$t('Please enter your password')}}</div>
                    <el-form-item prop="passWord">
                        <el-input  v-model="activteForm.passWord" type='text' clearable @focus="toclearValid('passWord')">
                        </el-input>
                    </el-form-item>
                </el-form>
            </div>
            <div class="dialog-footer">
                <el-button class="sure-btn" type="primary" @click="toActivte">{{$t('m.common.OK')}}</el-button>
                <el-button class="cancel-btn" type="info" @click="activDialog = false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <el-dialog :visible.sync="okdialog" :close-on-click-modal="false" :close-on-press-escape="false" class="ok-dialog">
            <div class="icon">
                <i v-if="activate" class="el-icon-success"></i>
                <i v-else class="el-icon-warning"></i>
            </div>
            <span v-if="activate">
                <div class="line-text-one" >{{$t('Activated Successfully!')}}</div>
                <div class="line-text-two">{{$t('Now you can sign in FATE Cloud with your administrator account.')}}</div>
            </span>
            <span v-else>
                <div class="line-text-one" >{{$t('Activated Failed!')}}</div>
                <div class="line-text-two">{{$t('Please re-enter')}}</div>
            </span>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toOK">{{$t('m.common.OK')}}</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { decode64, utf8to16 } from '@/utils/base64'
import { activateAct } from '@/api/welcomepage'

// 国际化
const local = {
    zh: {
        'Admin activation': '管理员激活',
        'Please enter the administrator invitation link from Cloud Manager': '请输入Cloud Manager 提供的管理员激活链接',
        'Please bind an administrator account': '请绑定一个管理员账号',
        'Please enter your password': '请输入密码',
        'The invitation link is required': '请输入激活链接',
        'The username is required': '请输入管理员名称',
        'The password is required': '请输入密码',
        'Activated Successfully!': '激活成功!',
        'Now you can sign in FATE Cloud with your administrator account.': '现在你可以使用该管理员账号登录FATE Cloud',
        'Activated Failed!': '激活失败!',
        'Please re-enter': '请重新填写激活信息'

    },
    en: {
        'Admin activation': 'Admin activation',
        'Please enter the administrator invitation link from Cloud Manager': 'Please enter the administrator invitation link from Cloud Manager',
        'Please bind an administrator account': 'Please bind an administrator account',
        'Please enter your password': 'Please enter your password',
        'The invitation link is required': 'The invitation link is required',
        'The username is required': 'The username is required',
        'The password is required': 'The password is required',
        'Activated Successfully !': 'Activated Successfully !',
        'Now you can sign in FATE Cloud with your administrator account.': 'Now you can sign in FATE Cloud with your administrator account.',
        'Activated Failed!': 'Activated Failed!',
        'Please re-enter': 'Please re-enter'
    }
}

export default {
    name: 'home',
    components: {},
    data() {
        return {
            activteForm: {
                link: '',
                userName: '',
                passWord: ''
            },
            activDialog: false, // 激活弹框
            okdialog: false, // 激活是否成功弹框
            activate: false, // 是否激活成功
            activteRules: {
                link: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            const name = value.trim()
                            if (name.length === 0) {
                                callback(new Error(this.$t('The invitation link is required')))
                            } else {
                                callback()
                            }
                        }
                    }
                ],
                userName: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            const name = value.trim()
                            if (name.length === 0) {
                                callback(new Error(this.$t('The username is required')))
                            } else {
                                callback()
                            }
                        }
                    }
                ],
                passWord: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            const password = value.trim()
                            if (password.length === 0) {
                                callback(new Error(this.$t('The password is required')))
                            } else {
                                callback()
                            }
                        }
                    }
                ]
            }
        }
    },
    watch: {},
    computed: {},
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    methods: {
        toActivte() {
            this.$refs['activteForm'].validate((valid) => {
                if (valid) {
                    let urlStr = this.activteForm.link.split('\\n').join('')
                    let Url = utf8to16(decode64(urlStr))
                    let newStr = Url.split('st=')[1].replace(new RegExp('\\\\', 'g'), '')
                    this.activteForm.link = this.activteForm.link.replace(/\\n/g, '\n')
                    console.log(newStr, 'newStr')
                    let data = {}
                    var obj = { ...JSON.parse(newStr) }
                    console.log(obj, 'obj')
                    data.activateUrl = `${Url.split('//')[0]}//${Url.split('//')[1].split('/')[0]}`
                    data.appKey = obj.fateManagerUser.secretInfo.key
                    data.appSecret = obj.fateManagerUser.secretInfo.secret
                    data.fateManagerId = obj.fateManagerUser.fateManagerId
                    data.federatedId = obj.federatedOrganization.id
                    data.federatedOrganization = obj.federatedOrganization.institution
                    data.link = this.activteForm.link
                    data.federatedUrl = `${Url.split('//')[0]}//${Url.split('//')[1].split('/')[0]}`
                    data.institution = obj.federatedOrganization.institution
                    data.institutions = obj.fateManagerUser.institutions
                    data.userName = this.activteForm.userName
                    data.passWord = this.activteForm.passWord
                    activateAct(data).then(res => {
                        this.okdialog = true
                        this.activate = true
                    }).catch(res => {
                        this.okdialog = true
                        this.activate = false
                    })
                }
            })
        },
        toOK() {
            this.activDialog = false
            this.okdialog = false
        },
        // 单独验证
        toclearValid(type) {
            this.$refs['activteForm'].clearValidate(type)
        },
        decodeLink(value) {
            if (this.activteForm.link) {
                // decode64 编码提取json对象
                let Url = utf8to16(decode64(this.activteForm.link))
                let obj = { ...JSON.parse(Url.split('st=')[1].replace(new RegExp('\\\\', 'g'), '')) }
                this.activteForm.activateUrl = Url
                this.activteForm.appKey = obj.fateManagerUser.secretInfo.key
                this.activteForm.appSecret = obj.fateManagerUser.secretInfo.secret
                this.activteForm.fateManagerId = obj.fateManagerUser.fateManagerId
                this.activteForm.institutions = obj.fateManagerUser.institutions
                this.activteForm.institution = obj.federatedOrganization.institution
                this.activteForm.federatedId = obj.federatedOrganization.id
                this.activteForm.federatedOrganization = obj.federatedOrganization.name
                this.activteForm.federatedUrl = `${Url.split('//')[0]}//${Url.split('//')[1].split('/')[0]}`
            }
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
</style>
