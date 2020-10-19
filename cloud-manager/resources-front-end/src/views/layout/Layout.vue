<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="begin" @click="toHome">
          <img src="@/assets/logo.png">
          <span>FATE Cloud</span>
        </div>
        <div class="right-bar">
            <el-popover placement="bottom" popper-class="bar-pop" :visible-arrow="false" trigger="click">
                <div class="mane" @click="tologout">Sign out</div>
                <div slot="reference" >
                    <span  class="name-text" >
                        <el-tooltip effect="light" v-if="tooltip" :content="loginName" placement="bottom-start">
                            <span ref="name">
                                {{loginName}}
                            </span>
                        </el-tooltip>
                         <span v-else ref="name">
                                {{loginName}}
                        </span>
                    </span>
                    <i class="el-icon-caret-bottom" />
                </div>
            </el-popover>
        </div>
        <topbar ref="topbar" />
      </el-header>
      <el-main>
        <sidebar />
        <contentbox  />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { logout } from '@/api/welcomepage'
import topbar from './components/topbar'
import sidebar from './components/sidebar'
import contentbox from './components/contentbox'
import { mapGetters } from 'vuex'

export default {
    name: 'Layout',
    components: {
        topbar,
        sidebar,
        contentbox
    },
    data() {
        return {
            name: '',
            tooltip: false
        }
    },
    computed: {
        ...mapGetters(['loginName'])
    },
    created() {
        this.$store.dispatch('setloginname', localStorage.getItem('name')).then(r => {
            localStorage.setItem('name', r)
        })
    },
    mounted() {
        let width = this.$refs.name.offsetWidth
        if (width > 160) {
            this.tooltip = true
        } else {
            this.tooltip = false
        }
    },
    methods: {
        toHome() {
            // this.$router.push({ path: '/' })
        },
        // 点击退出
        tologout(e) {
            logout().then(res => {
                this.$store.dispatch('setloginname', '').then(r => {
                    localStorage.setItem('name', r)
                    this.$router.push({
                        path: '/home/welcome'
                    })
                })
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/home.scss';
.bar-pop{
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
</style>
