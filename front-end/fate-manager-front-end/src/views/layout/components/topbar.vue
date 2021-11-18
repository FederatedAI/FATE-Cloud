<template>
  <div class="top-bar">
    <el-menu
        :default-active="activeIndex"
        class="top-menu"
        background-color="#217ad9"
        text-color="#ccc"
        active-text-color="#fff"
        mode="horizontal"
        @select="handleSelect">
        <el-menu-item  index="Manage">
            <el-popover placement="bottom" v-model="visible" popper-class="bar-pop" :visible-arrow="false" trigger="hover">
                <div :class="{item:true,itemactive:$route.name === 'sitemanage'}" @click="clickSite">{{$t('Site Manage')}}</div>
                <div :class="{item:true,itemactive:$route.name === 'access'}" v-if="role.roleName==='Admin'" @click="clickAccess">{{$t('User Access')}}</div>
                <div slot="reference" >
                    <span :class="{active}">{{$t('Manage')}}</span>
                </div>
            </el-popover>
        </el-menu-item>
        <!-- <el-menu-item v-if='autoState' index="Auto-deploy" :class="{notactive:active || monitoractive}" >{{$t('Auto-deploy')}}</el-menu-item> -->
        <el-menu-item  v-if="role.roleName==='Admin'" index="Monitor">
            <el-popover placement="bottom" v-model="monitorvisible" popper-class="bar-pop" :visible-arrow="false" trigger="hover">
                <div :class="{item:true,itemactive:$route.name === 'cooperation'}" @click="clickCooperation" >{{$t('Site Cooperation')}}</div>
                <div :class="{item:true,itemactive:$route.name === 'jobmonitor'}" @click="clickJobmonitor" >{{$t('Job Monitor')}}</div>
                <div slot="reference" >
                    <span :class="{active:monitoractive}">{{$t('Monitor')}}</span>
                </div>
            </el-popover>
        </el-menu-item>
    </el-menu>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
// 国际化
const local = {
    zh: {
        'Manage': '管理',
        'Auto-deploy': '自动部署',
        'Monitor': '监控',
        'Site Manage': '站点管理',
        'User Access': '用户授权',
        'Site Cooperation': '站点合作监控',
        'Job Monitor': '离线任务监控'
    },
    en: {
        'Manage': 'Manage',
        'Auto-deploy': 'Auto-deploy',
        'Monitor': 'Monitor',
        'Site Manage': 'Site Manage',
        'User Access': 'User Access',
        'Site Cooperation': 'Site Cooperation',
        'Job Monitor': 'Job Monitor'
    }
}
export default {
    data() {
        return {
            visible: false,
            monitorvisible: false,
            active: false,
            monitoractive: false,
            activeIndex: 'Auto-deploy'
        }
    },

    watch: {
        $route: {
            handler(value) {
                if (value.name === 'sitemanage' || value.name === 'access') {
                    this.activeIndex = 'Manage'
                    this.active = true
                    this.monitoractive = false
                } else if (value.name === 'cooperation' || value.name === 'jobmonitor') {
                    this.activeIndex = 'Monitor'
                    this.monitoractive = true
                    this.active = false
                } else {
                    this.active = false
                    this.monitoractive = false
                }
            }
        }
    },
    computed: {
        ...mapGetters(['role', 'autoState'])
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    mounted() {
        if (this.$route.name === 'sitemanage' || this.$route.name === 'access') {
            this.activeIndex = 'Manage'
        } else if (this.$route.name === 'cooperation' || this.$route.name === 'jobmonitor') {
            this.activeIndex = 'Monitor'
        }
    },
    methods: {
        handleSelect(key, keyPath) {
            if (key === 'Auto-deploy') {
                this.$router.push({ name: 'overview' }).catch(() => {})
            } else if (key === 'Manage') {
                this.$router.push({ name: 'sitemanage' }).catch(() => {})
            } else if (key === 'Monitor') {
                this.$router.push({ name: 'cooperation' }).catch(() => {})
            }
        },
        clickSite() {
            this.$router.push({
                name: 'sitemanage'
            }).catch(() => {})
        },
        clickAccess() {
            this.$router.push({
                name: 'access'
            }).catch(() => {})
        },
        clickCooperation() {
            this.$router.push({
                name: 'cooperation'
            }).catch(() => {})
        },
        clickJobmonitor() {
            this.$router.push({
                name: 'jobmonitor'
            }).catch(() => {})
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.top-bar {
    height: 65px;
    line-height: 65px;
    .top-menu{
        position: absolute;
        display: inline-block;
        left: 240px;
        .focusing:focus {
            outline:none;
        }
        .el-menu-item:hover{
            border-bottom:0px;
            background-color: #4AA2FF !important;
            border-bottom-color:#4AA2FF !important;
        }
        .el-menu-item{
            height: 65px;
            line-height: 65px;
            font-size: 18px;
            border-bottom-color:#217AD9 !important;
        }
    }
    .active{
        color: #fff
    }
    .notactive{
        color: #ccc !important
    }
}
.bar-pop{
    text-align: center;
    line-height: 30px;
    margin-top:0 !important;
    min-width: 110px !important;
    padding: 5px;
    .item{
        cursor: pointer;
        font-size: 14px;
        color: #4E5766;
    }
    .itemactive{
        color: #217AD9
    }
    .item:hover{
        background-color: #ecf5ff;
    }

}

</style>
