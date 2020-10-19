<template>
  <div class="home">
    <el-header>
        <div class="begin" @click="toHome">
            <img src="@/assets/logo.png">
            <span>FATE Cloud</span>
        </div>
        <div class="right-bar">
            <el-popover v-if="loginName" placement="bottom" popper-class="bar-pop" :visible-arrow="false" trigger="click">
                <div class="mane" @click="tologout">Sign out</div>
                <div slot="reference" >
                    <span>{{loginName}}</span>
                    <i class="el-icon-caret-bottom" />
                </div>
            </el-popover>
            <span v-else @click="tologin">Sign in</span>
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
    },
    computed: {
        ...mapGetters(['loginName'])
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
