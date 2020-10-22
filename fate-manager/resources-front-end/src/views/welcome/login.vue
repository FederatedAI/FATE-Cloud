<template>
    <div class="home">
        <img src="@/assets/welcomepage.svg" />
        <div class="welcomepage">
            <div class="title">
            <span>Welcome to FATE Cloud!</span>
            </div>
            <div class="text">
            <span>It is an Infrastructure for Building and Managing Federated Data Collaboration Network.</span>
            </div>
            <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" label-position="left">
                <div class="from-text">Username/Email/Phone</div>
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
                <div class="from-text"> Password</div>
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
                    <el-checkbox v-model="checked">Remember me</el-checkbox>
                </div>
                <el-form-item>
                    <el-button  class="btn-login" type="primary" @click.native.prevent="handleLogin">
                    Sign in
                    </el-button>
                </el-form-item>
                <div class="activate-it">
                    <span style="color:#848C99">Administrator account not activated yet? </span>
                    <span class="it" @click="toAct">Activate it.</span>
                </div>
            </el-form>
        </div>
        <activte-dialog ref="activtedialog"/>
        <el-dialog :visible.sync="contactdialog" :close-on-click-modal="false" :close-on-press-escape="false" class="contact-dialog">
            <div class="line-text-two">Please contact the administrator to add permissions for you.</div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="contactdialog=false">OK</el-button>
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
                            // const pwdRegex = new RegExp('(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,20}')
                            // if (!pwdRegex.test(value)) {
                            //   callback(new Error('密码长度为 8-20 个字 ，需同时包含数字、字母以及特殊符号'))
                            // } else {
                            //   callback()
                            // }
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
