<template>
    <div>
        <el-dialog :visible.sync="activDialog" :close-on-click-modal="false" :close-on-press-escape="false" class="activ-dialog" width="1000px">
            <div class="dialog-header">{{$t('m.welcome.adminActivation')}}</div>
            <div class="dialog-body">
                <el-form ref="activteForm" :model="activteForm" :rules="activteRules" class="activte-form" label-position="left">
                    <div class="text-one">{{$t('m.welcome.enterLinkTip')}}</div>
                    <el-form-item prop="link">
                        <el-input v-model="activteForm.link" @blur="decodeLink" type="text" clearable @focus="toclearValid('link')">
                        </el-input>
                    </el-form-item>
                    <div class="text-one">{{$t('m.welcome.bindAccount')}}</div>
                    <el-form-item prop="userName">
                        <el-input v-model="activteForm.userName" type='text' clearable @focus="toclearValid('userName')">
                        </el-input>
                    </el-form-item>
                    <div class="text-one">{{$t('m.welcome.enterPasswordTip')}}</div>
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
                <div class="line-text-one" >{{$t('m.welcome.activatedSuccessfully')}}</div>
                <div class="line-text-two">{{$t('m.welcome.canSignInWithAccount')}}</div>
            </span>
            <span v-else>
                <div class="line-text-one" >{{$t('m.welcome.activatedFailed')}}</div>
                <div class="line-text-two">{{$t('m.welcome.pleaseRe-enter')}}</div>
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
            warnActive: false, // 链接格式校验
            activteRules: {
                link: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            try {
                                if (value.length > 0) {
                                    let urlStr = this.activteForm.link.split('\\n').join('')
                                    let Url = utf8to16(decode64(urlStr))
                                    let newStr = Url.split('st=')[1].replace(new RegExp('\\\\', 'g'), '')
                                    callback()
                                }
                            } catch {
                                callback(new Error(this.$t('m.sitemanage.registrationInvalid')))
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
                                // callback(new Error(this.$t('m.welcome.usernameRequired')))
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
                                // callback(new Error(this.$t('m.welcome.passwordRequired')))
                            } else {
                                callback()
                            }
                        }
                    }
                ]
            }
        }
    },
    watch: {
        activDialog: {
            handler: function(val) {
                if (val) {
                    this.initForm()
                }
            }
        }
    },
    computed: {},
    created() {
    },
    methods: {
        initForm() {
            this.$nextTick(() => {
                this.activteForm = {
                    link: '',
                    userName: '',
                    passWord: ''
                }
                this.$refs['activteForm'].clearValidate()
            })
        },
        toActivte() {
            this.$refs['activteForm'].validate((valid) => {
                if (valid) {
                    let urlStr = this.activteForm.link.split('\\n').join('')
                    let Url = utf8to16(decode64(urlStr))
                    let newStr = Url.split('st=')[1].replace(new RegExp('\\\\', 'g'), '')
                    this.activteForm.link = this.activteForm.link.replace(/\\n/g, '\n')
                    let data = {}
                    var obj = { ...JSON.parse(newStr) }
                    console.log(obj, 'obj')
                    data.activateUrl = `${Url.split('//')[0]}//${Url.split('//')[1].split('/')[0]}`
                    data.appKey = obj.fateManagerUser.secretInfo.key
                    data.appSecret = obj.fateManagerUser.secretInfo.secret
                    data.fateManagerId = obj.fateManagerUser.fateManagerId
                    data.federatedId = obj.federatedOrganization.id
                    data.federatedOrganization = obj.federatedOrganization.name
                    data.link = this.activteForm.link
                    data.federatedUrl = `${Url.split('//')[0]}//${Url.split('//')[1].split('/')[0]}`
                    data.institution = obj.federatedOrganization.institution
                    data.institutions = obj.fateManagerUser.institutions
                    data.userName = this.activteForm.userName
                    data.passWord = this.activteForm.passWord
                    data.createTime = obj.federatedOrganization.createTime
                    console.log(data, 'data')
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
                this.warnActive = false
                // decode64 编码提取json对象
                // let Url = utf8to16(decode64(this.activteForm.link))
                // let obj = Url.split('st=')[1].replace(new RegExp('\\\\', 'g'), '')
                let urlStr = this.activteForm.link.split('\\n').join('')
                let Url = utf8to16(decode64(urlStr))
                console.log(Url, 'Url')
                let obj = { ...JSON.parse(Url.split('st=')[1].replace(new RegExp('\\\\', 'g'), '')) }
                console.log(obj, 'obj')
                this.activteForm.link = this.activteForm.link.replace(/\\n/g, '\n')
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
.warn-text{
    position: absolute;
    top:30px;
    width: 100%;
    color: #FF9D00;
    text-align: center;
    font-size: 1em;
}
</style>
