<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="logo">
            <!-- <img src="@/assets/logo.png"> -->
            <span>FATE Cloud</span>

        </div>
        <div class="right-bar">
            <el-popover v-if="userName" placement="bottom" popper-class="usrname-pop" :visible-arrow="false" trigger="click">
                <div class="mane" @click="tologout">{{$t('Sign out')}}</div>
                <div slot="reference" >
                    <span>{{userName}}</span>
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
                <el-dropdown-menu slot="dropdown"  class="dropdown-lang">
                    <el-dropdown-item command='zh' :disabled="language==='zh'">中文</el-dropdown-item>
                    <el-dropdown-item command='en' :disabled="language==='en'">English</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
        <topbar ref="topbar" />
      </el-header>
      <el-main>
        <!-- <sidebar /> -->
        <contentbox  />
      </el-main>
    </el-container>
    <!-- Site-Authorization开关状态-->
    <el-dialog :visible.sync="siteAuth" class="siteAuth-dialog" width="600px" :close-on-click-modal="false" :close-on-press-escape="false">
        <div class="line-text-two">
            “{{title}}”has been turned off,
        </div>
        <div class="line-text-two">
            and all related functions of
            <span v-if="title==='Site-Authorization'">Site-Authorization</span>
            <span v-else>automatic deployment and upgrade</span>
        </div>
        <div class="line-text-two">
            are no longer available
        </div>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="toSiteAuth">Sure</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import topbar from './components/topbar'
// import sidebar from './components/sidebar'
import contentbox from './components/contentbox'
import { mapGetters } from 'vuex'
import { readAuthorState } from '@/api/home'

export default {
    name: 'Layout',
    components: {
        topbar,
        // sidebar,
        contentbox
    },
    data() {
        return {
            siteAuth: false,
            autositetimeless: null,
            title: 'Site-Authorization'
        }
    },
    computed: {
        ...mapGetters(['language', 'userId', 'userName'])
    },
    created() {
        this.$store.dispatch('TogetAuthorSite')
        // this.togetAuthorSite()// 发版之前记得开启定时器
    },
    beforeDestroy() {
        window.clearTimeout(this.autositetimeless)
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
                // location.reload()
                this.$router.push({ path: '/welcome/login' })
            })
        },
        tologin() {
            this.$router.push({
                path: '/welcome/login'
            })
        },
        togetAuthorSite() {
            this.$store.dispatch('TogetAuthorSite').then(res => {
                for (const iter of res.data) {
                    if (iter.functionName === 'Site-Authorization') {
                        if (iter.status === 2 && iter.readStatus === 0) {
                            this.siteAuth = true
                            this.title = 'Site-Authorization'
                            return
                        }
                    } else if (iter.functionName === 'Auto-Deploy') {
                        if (iter.status === 2 && iter.readStatus === 0) {
                            this.siteAuth = true
                            this.title = 'Auto-deploy'
                            return
                        }
                    }
                }
                this.autositetimeless = setTimeout(() => {
                    this.togetAuthorSite()
                }, 2000)
            })
        },
        toSiteAuth() {
            readAuthorState().then(res => {
                this.$router.push({ path: '/home/sitemanage' })
                this.siteAuth = false
                this.togetAuthorSite()
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
.usrname-pop{
    text-align: center;
    height: 35px !important;
    line-height: 35px;
    margin-top:0 !important;
    min-width: 95px !important;
    left: calc(100% - 195px) !important;
    padding: 0px;
    .mane{
        cursor: pointer;
        font-size: 14px;
        color: #217AD9;
    }
    .mane:hover{
        background-color: #ecf5ff;
    }
}
@import 'src/styles/home.scss';
.dropdown-lang{
    text-align: center;

    margin-top:0 !important;

    left: calc(100% - 110px) !important;
    .popper__arrow{
        display: none
    }
}
</style>
