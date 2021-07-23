<template>
    <div class="home">
        <el-header>
        <div class="logo">
            <!-- <img src="@/assets/logo.png"> -->
            <span>FATE Cloud</span>
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

        }
    },
    watch: {

    },
    computed: {
        ...mapGetters(['language', 'userName'])
    },
    created() {

    },
    mounted() {

    },
    methods: {
        handleCommand(val) {
            this.$i18n.locale = val
            this.$store.dispatch('setLanguage', val)
        },
        tologout() {
            let data = {
                userId: this.userId,
                userName: this.userName
            }
            this.$store.dispatch('LogOut', data).then(res => {
                // location.reload() // 为了重新实例化vue-router对象 避免bug
                this.$router.push({ path: '/welcome/login' })
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

@import 'src/styles/home.scss';
</style>
