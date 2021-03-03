<template>
    <div class="home">
        <img src="@/assets/welcomepage.svg" />
        <div class="welcomepage">
            <div class="title">
            <span>{{$t('Welcome to FATE Cloud!')}}</span>
            </div>
            <div class="text">
            <span>
                {{$t('It is an Infrastructure for Building and Managing Federated Data Collaboration Network.')}}
            </span>
            </div>
            <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" label-position="left">
                <div class="from-text">{{$t('Username/Email/Phone')}}</div>
                <el-form-item prop="username">
                    <el-input
                        style="width:450px"
                        v-model.trim="loginForm.username"
                        name="username"
                        type="text"
                        :placeholder="placeholderUsername"
                        @blur="getplaceholder('username')"
                        auto-complete="on"
                        @focus="toclearValid('username')"
                        @keyup.enter.native="handleLogin">
                    </el-input>
                </el-form-item>
                <div class="from-text">{{$t('Password')}}</div>
                <el-form-item prop="password">
                    <el-input
                        style="width:450px"
                        v-model.trim="loginForm.password"
                        :type='pwdType'
                        name="password"
                        :placeholder="placeholderPassword"
                        @blur="getplaceholder('password')"
                        auto-complete="on"
                        @focus="toclearValid('password')"
                        @keyup.enter.native="handleLogin">
                    </el-input>
                    <span  @click="toshowPwd" v-if='showPwd' class="view"> <img src="@/assets/view_hide.png" /></span>
                    <span  @click="toshowPwd" v-else class="view"> <img src="@/assets/view_show.png" /></span>
                </el-form-item>
                <div class="Remember-text">
                    <el-checkbox v-model="checked">{{$t('Remember me')}}</el-checkbox>
                </div>
                <el-form-item>
                    <el-button  class="btn-login" type="primary" @click.native.prevent="handleLogin">
                        {{$t('Sign in')}}
                    </el-button>
                </el-form-item>
                <div class="activate-it">
                    <span style="color:#848C99">
                        {{$t('Administrator account not activated yet?')}}
                    </span>
                    <span class="it" @click="toAct">{{$t('Activate it.')}}</span>
                </div>
            </el-form>
        </div>
        <activte-dialog ref="activtedialog"/>
        <el-dialog :visible.sync="contactdialog" :close-on-click-modal="false" :close-on-press-escape="false" class="contact-dialog">
            <div class="line-text-two">
                {{$t('Please contact the administrator to add permission for you.')}}
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="contactdialog=false"> {{$t('OK')}}</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
import activteDialog from './loginDialog'
import { decode64, encode64 } from '@/utils/base64'
// 国际化
const local = {
    zh: {
        'Welcome to FATE Cloud!': '欢迎来到FATE Cloud!',
        'It is an Infrastructure for Building and Managing Federated Data Collaboration Network.': '作为构建和管理联邦数据合作网络的基础设施，提供一站式联邦数据合作服务。',
        'Username/Email/Phone': '用户名/邮箱/手机号',
        'Password': '密码',
        'Remember me': '记住密码',
        'Sign in': '登录',
        'Administrator account not activated yet?': '管理员账号尚未激活？',
        'Activate it.': '点此激活',
        'Please contact the administrator to add permission for you.': '请联系管理员为你添加权限'
    },
    en: {
        'Welcome to FATE Cloud!': 'Welcome to FATE Cloud!',
        'It is an Infrastructure for Building and Managing Federated Data Collaboration Network.': 'It is an Infrastructure for Building and Managing Federated Data Collaboration Network.',
        'Username/Email/Phone': 'Username/Email/Phone',
        'Password': 'Password',
        'Remember me': 'Remember me',
        'Sign in': 'Sign in',
        'Administrator account not activated yet?': 'Administrator account not activated yet?',
        'Activate it.': 'Activate it.',
        'Please contact the administrator to add permission for you.': 'Please contact the administrator to add permission for you.'
    }
}

export default {
    name: 'login',
    components: {
        activteDialog
    },
    data() {
        return {
            contactdialog: false, // 连接失败
            placeholderUsername: 'Please enter the Username/Email/Phone', // 兼容edge浏览器 光标不在中间
            placeholderPassword: 'Please enter the Password', // 兼容edge浏览器 光标不在中间
            loginForm: {
                username: '',
                password: ''
            },
            value: '',
            pwdType: '',
            checked: false,
            showPwd: false,
            loginRules: {
                username: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            const name = value.trim()
                            if (name.length === 0) {
                                callback(
                                    new Error(
                                        'Please enter the Username/Email/Phone'
                                    )
                                )
                            } else if (name.length >= 50) {
                                callback(
                                    new Error(
                                        'Please enter the correct account!'
                                    )
                                )
                            } else {
                                callback()
                            }
                        }
                    }
                ],
                password: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            const password = value.trim()
                            if (password.length === 0) {
                                callback(new Error('Please enter the password'))
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
        checked: {
            handler(val) {
                if (val) {
                    this.tosave()
                } else {
                    this.toclear()
                }
            }
        }
    },
    computed: {},
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    mounted() {
        this.checked = localStorage.getItem('fatechecked') === 'true'
        if (this.checked) {
            this.pwdType = 'password'
            this.showPwd = true
            this.loginForm.username = decode64(localStorage.getItem('fateusername'))
            this.loginForm.password = decode64(localStorage.getItem('fatepassword'))
        }
    },
    methods: {
        toshowPwd() {
            this.showPwd = !this.showPwd
            if (this.pwdType === 'password') {
                this.pwdType = ''
            } else {
                this.pwdType = 'password'
            }
        },
        handleLogin() {
            if (this.checked) {
                this.tosave()
            } else {
                this.toclear()
            }
            let data = {
                userName: this.loginForm.username,
                passWord: this.loginForm.password
            }
            this.$refs['loginForm'].validate((valid) => {
                if (valid) {
                    this.$store.dispatch('Login', data).then(res => {
                        this.$router.push({ path: '/home/sitemanage' })
                    }).catch(res => {
                        if (res.code === 10064) {
                            this.contactdialog = true
                        }
                    })
                }
            })
        },
        // 保存密码
        tosave() {
            localStorage.setItem('fatechecked', true)
            localStorage.setItem('fateusername', encode64(this.loginForm.username))
            localStorage.setItem('fatepassword', encode64(this.loginForm.password))
        },
        // 清除密码
        toclear() {
            localStorage.setItem('fatechecked', false)
            localStorage.setItem('fateusername', '')
            localStorage.setItem('fatepassword', '')
        },
        // 单独验证
        toclearValid(type) {
            if (type === 'username') {
                this.placeholderUsername = ''
            } else if (type === 'password') {
                this.placeholderPassword = ''
            }
            this.$refs['loginForm'].clearValidate(type)
        },
        // 兼容edge浏览器 光标不在中间
        getplaceholder(type) {
            if (type === 'username') {
                this.placeholderUsername = 'Please enter the Username/Email/Phone'
            } else if (type === 'password') {
                this.placeholderPassword = 'Please enter the Password'
            }
        },
        //
        toAct() {
            this.$refs['activtedialog'].activDialog = true
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >

@import 'src/styles/home.scss';
</style>
