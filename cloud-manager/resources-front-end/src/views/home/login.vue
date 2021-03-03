<template>
   <div class="login">
        <div class="title">
          <span>Sign in to FATE Cloud</span>
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
                    auto-complete="off"
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
                    auto-complete="off"
                    @focus="toclearValid('password')"
                    @keyup.enter.native="handleLogin">
                </el-input>
                <span  @click="toshowPwd" v-if='showPwd' class="view"> <img src="@/assets/view_hide.png"/></span>
                <span  @click="toshowPwd" v-else class="view"> <img src="@/assets/view_show.png"/></span>
            </el-form-item>
            <div class="Remember-text">
                <el-checkbox v-model="checked">Remember me</el-checkbox>
            </div>
            <el-form-item>
                <el-button  class="btn-login" type="primary" @click.native.prevent="handleLogin">
                    Sign in
                </el-button>
            </el-form-item>
        </el-form>
    </div>

</template>

<script>
import { login } from '@/api/welcomepage'
import { mapGetters } from 'vuex'
import { decode64, encode64 } from '@/utils/base64'
export default {
    name: 'home',
    components: {},
    data() {
        return {
            placeholderUsername: 'Please enter the Username/Email/Phone', // 兼容edge浏览器 光标不在中间
            placeholderPassword: 'Please enter the Password', // 兼容edge浏览器 光标不在中间
            loginForm: {
                username: '',
                password: ''
            },
            showPwd: false,
            value: '',
            pwdType: '',
            checked: false,
            loginRules: {
                username: [{
                    required: true,
                    trigger: 'change',
                    validator: (rule, value, callback) => {
                        const name = value.trim()
                        if (name.length === 0) {
                            callback(new Error('Please enter the Username/Email/Phone'))
                        } else {
                            callback()
                        }
                    } }],
                password: [{
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
                    } }]
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
        ...mapGetters(['loginName'])
    },
    mounted() {
        this.checked = localStorage.getItem('checked') === 'true'
        if (this.checked) {
            this.pwdType = 'password'
            this.showPwd = true
            this.loginForm.username = decode64(localStorage.getItem('cloudusername'))
            this.loginForm.password = decode64(localStorage.getItem('cloudpassword'))
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
                name: this.loginForm.username,
                password: this.loginForm.password
            }
            this.$refs['loginForm'].validate((valid) => {
                if (valid) {
                    login(data).then(res => {
                        this.$store.dispatch('setloginname', res.data.name).then(r => {
                            localStorage.setItem('name', r)
                            this.$router.push({
                                path: '/home/register'
                            })
                        })
                    })
                }
            })
        },
        // 保存密码
        tosave() {
            localStorage.setItem('checked', true)
            localStorage.setItem('cloudusername', encode64(this.loginForm.username))
            localStorage.setItem('cloudpassword', encode64(this.loginForm.password))
        },
        // 清除密码
        toclear() {
            localStorage.setItem('checked', false)
            localStorage.setItem('cloudusername', '')
            localStorage.setItem('cloudpassword', '')
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
        }
    }
}
</script>
<style rel="stylesheet/scss" lang="scss" >

</style>
