<template>
  <div class="home">
    <el-header>
        <div class="begin" @click="toHome">
            <!-- <img src="@/assets/logo.png"> -->
            <span>FATE Cloud</span>
        </div>
        <div class="name-bar">
            <el-popover v-if="loginName" placement="bottom" popper-class="bar-pop" :visible-arrow="false" trigger="click">
                <div class="mane" @click="tologout">{{$t('Sign out')}}</div>
                <div slot="reference" >
                    <span>{{loginName}}</span>
                    <i class="el-icon-caret-bottom" />
                </div>
            </el-popover>
            <span v-else @click="tologin">{{$t('Sign in')}}</span>
        </div>
        <div class="lang-bar">
            <el-dropdown  trigger="click"  @command="handleCommand">
                <span class="text-link">
                    <span>{{language==='zh'?'中文':'English'}}</span>
                    <i class="el-icon-caret-bottom"></i>
                </span>
                <el-dropdown-menu slot="dropdown"  class="dropdown">
                    <el-dropdown-item command='zh' :disabled="language==='zh'">中文</el-dropdown-item>
                    <el-dropdown-item command='en' :disabled="language==='en'">English</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </el-header>
    <el-main>
      <img src="@/assets/welcomepage.svg" />
      <router-view />
    </el-main>
  </div>
</template>

<script>
import { logout } from '@/api/welcomepage'
import { mapGetters } from 'vuex'
// 国际化
const local = {
    zh: {
        'Sign in': '登录',
        'Sign out': '退出'
    },
    en: {
        'Sign in': 'Sign in',
        'Sign out': 'Sign out'
    }
}
export default {
    name: 'home',
    components: {},
    data() {
        return {
            name: ''
        }
    },
    watch: {

    },
    created() {
        this.$store.dispatch('setloginname', localStorage.getItem('name')).then(r => {
            localStorage.setItem('name', r)
        })
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    computed: {
        ...mapGetters(['loginName', 'language'])
    },
    methods: {
        toHome() {
            // this.$router.push({
            //     name: 'welcome',
            //     path: '/home/welcome',
            //     query: {}
            // })
        },
        tologin() {
            this.$router.push({
                path: '/home/login'
            })
        },
        // 点击退出
        tologout(e) {
            logout().then(res => {
                this.$store.dispatch('setloginname', '').then(r => {
                    localStorage.setItem('name', r)
                })
                this.$router.push({
                    path: '/home/welcome'
                })
            })
        },
        handleCommand(val) {
            this.$i18n.locale = val
            this.$store.dispatch('setLanguage', val)
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

@import 'src/styles/home.scss';
</style>
