<template>
    <div class="home">
        <el-header>
        <div class="logo">
            <img src="@/assets/logo.png">
            <span>FATE Cloud</span>
        </div>
        <div class="right-bar">
            <el-popover v-if="userName" placement="bottom" popper-class="usrname-pop" :visible-arrow="false" trigger="click">
                <div class="mane" @click="tologout">Sign out</div>
                <div slot="reference" >
                    <span>{{userName}}</span>
                    <i class="el-icon-caret-bottom" />
                </div>
            </el-popover>
            <span v-else @click="tologin">Sign in</span>
        </div>
        </el-header>
        <el-main>
            <router-view />
        </el-main>

    </div>

</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'home',
    components: {

    },
    data() {
        return {
            loginName: '',
            contactdialog: true,
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
                        trigger: 'blur',
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
                        trigger: 'blur',
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

    },
    computed: {
        ...mapGetters(['userId', 'userName'])
    },
    created() {

    },
    mounted() {

    },
    methods: {
        tologout() {
            let data = {
                userId: this.userId,
                userName: this.userName
            }
            this.$store.dispatch('LogOut', data).then(res => {
                location.reload() // 为了重新实例化vue-router对象 避免bug
                // this.$router.push({ path: '/welcome/login' })
            }).catch(() => {

            })
        },
        tologin() {
            this.$router.push({
                path: '/welcome/login'
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@media screen and (max-width: 1400px) {
    .home {
        font-size: 16px;
    }
}
@media screen and (max-width: 1250px) {
    .home {
        font-size: 14px;
    }
}
@media screen and (max-width: 1100px) {
    .home {
        font-size: 12px;
    }
}
@media screen and (max-width: 950px) {
    .home {
        font-size: 10px;
    }
}
@media screen and (max-width: 800px) {
    .home {
        font-size: 8px;
    }
}
.usrname-pop{
    text-align: center;
    height: 35px !important;
    line-height: 35px;
    margin-top:0 !important;
    min-width: 95px !important;
    left: calc(100% - 143px) !important;
    padding: 5px;
    .mane{
        cursor: pointer;
        font-size: 16px;
        color: #217AD9;
    }
    .mane:hover{
        background-color: #ecf5ff;
    }
}

@import 'src/styles/home.scss';
</style>
