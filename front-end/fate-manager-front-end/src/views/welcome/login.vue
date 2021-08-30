<template>
    <div class="home">
        <img src="@/assets/welcomepage.svg" />
        <div class="welcomepage">
            <div class="title">
            <span>{{$t('m.welcome.welcome')}}</span>
            </div>
            <div class="text">
            <span>
                {{$t('m.welcome.introductions')}}
            </span>
            </div>
            <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" label-position="left">
                <!-- <div class="from-text">{{$t('Username/Email/Phone')}}</div> -->
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
                <!-- <div class="from-text">{{$t('Password')}}</div> -->
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
                    <el-checkbox v-model="checked">{{$t('m.welcome.RememberMe')}}</el-checkbox>
                </div>
                <el-form-item>
                    <el-button  class="btn-login" type="primary" @click.native.prevent="handleLogin">
                        {{$t('m.welcome.SignIn')}}
                    </el-button>
                </el-form-item>
                <div class="activate-it">
                    <span style="color:#848C99">
                        {{$t('m.welcome.administratorNotActivated')}}
                    </span>
                    <span class="it" @click="toAct">{{$t('m.welcome.activateIt')}}</span>
                </div>
            </el-form>
        </div>
        <activte-dialog ref="activtedialog"/>
        <el-dialog :visible.sync="contactdialog" :close-on-click-modal="false" :close-on-press-escape="false" class="contact-dialog">
            <div class="line-text-two">
                {{$t('m.welcome.contactAdministratorTips')}}
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="contactdialog=false"> {{$t('m.common.OK')}}</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
import activteDialog from './loginDialog'
import { decode64, encode64 } from '@/utils/base64'

export default {
    name: 'login',
    components: {
        activteDialog
    },
    data() {
        return {
            contactdialog: false, // 连接失败
            // placeholderUsername: this.$t('m.welcome.userNameTips'), // 兼容edge浏览器 光标不在中间
            // placeholderPassword: this.$t('m.welcome.passWordTips'), // 兼容edge浏览器 光标不在中间
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
                                        this.$t('m.welcome.userNameTips')
                                    )
                                )
                            } else if (name.length >= 50) {
                                callback(
                                    new Error(
                                        this.$t('m.welcome.correctAccountTips')
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
                                callback(new Error(this.$t('m.welcome.passWordTips')))
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
    computed: {
        placeholderUsername() {
            return this.$t('m.welcome.userNameTips')
        },
        placeholderPassword() {
            return this.$t('m.welcome.passWordTips')
        }
    },
    created() {
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
                this.placeholderUsername = this.$t('m.welcome.userNameTips')
            } else if (type === 'password') {
                this.placeholderPassword = this.$t('m.welcome.passWordTips')
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
