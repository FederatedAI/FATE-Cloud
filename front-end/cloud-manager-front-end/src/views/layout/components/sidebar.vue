<template>
  <div class="sidebarbox">
    <div class="sidebar">
      <el-menu
        :default-active="active"
        @open="handleOpen"
        @close="handleClose"
        @select="handleSelect"
        background-color="#e6ebf0"
        text-color="#4E5766"
        active-text-color="#005ABA"
      >
        <el-submenu index="Federated Site">
          <template slot="title">
            <div class="title">
              <img src="@/assets/federated.png">
              <span>{{$t('Federated Site')}}</span>
            </div>
          </template>
          <el-menu-item-group>
              <template v-for="(item, index) in menuFederatedList.children" >
                    <el-menu-item v-if='item.hidden' :key="index" :index="item.name">{{$t(`${item.name}`)}}</el-menu-item>
              </template>
            <!-- <el-menu-item index="Site Manage">{{$t('Site Manage')}}</el-menu-item>
            <el-menu-item index="IP Manage">{{$t('IP Manage')}}</el-menu-item>
            <el-menu-item v-if='autostatus' index="Service Manage">{{$t('Service Manage')}}</el-menu-item>
            <el-menu-item index="Site Monitor">{{$t('Site Monitor')}}</el-menu-item>
            <el-menu-item index="Job Monitor">{{$t('Job Monitor')}}</el-menu-item> -->
          </el-menu-item-group>
        </el-submenu>
        <el-submenu index="Setting">
          <template slot="title">
            <div class="title">
              <img src="@/assets/setting.png">
              <span>{{$t('Setting')}}</span>
            </div>
          </template>
          <el-menu-item-group>
               <template v-for="(item, index) in menusettingList.children">
                   <el-menu-item v-if='item.hidden' :key="index" :index="item.name">{{$t(`${item.name}`)}}</el-menu-item>
               </template>
            <!-- <el-menu-item index="Party ID">{{$t('Party ID')}}</el-menu-item>
            <el-menu-item index="Certificate">{{$t('Certificate')}}</el-menu-item>
            <el-menu-item index="Admin Access">{{$t('Admin Access')}}</el-menu-item>
            <el-menu-item index="System Function Switch">{{$t('System Function Switch')}}</el-menu-item> -->
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { switchState } from '@/api/setting'
import menuFederatedList from '@/router/federatedRouter'
import menusettingList from '@/router/settingRouter'
// 国际化
const local = {
    zh: {
        'Federated Site': '联邦站点',
        'Site Manage': '站点管理',
        'IP Manage': 'IP管理',
        'Service Manage': '服务管理',
        'Site Monitor': '站点监控',
        'Job Monitor': '任务监控',
        'Setting': '设置',
        'Party ID': '站点ID',
        'Certificate': '证书',
        'Admin Access': '管理权限',
        'System Function Switch': '系统功能开关'
    },
    en: {
        'Site Manage': 'Site Manage',
        'Federated Site': 'Federated Site',
        'IP Manage': 'IP Manage',
        'Service Manage': 'Service Manage',
        'Site Monitor': 'Site Monitor',
        'Job Monitor': 'Job Monitor',
        'Setting': 'Setting',
        'Party ID': 'Party ID',
        'Certificate': 'Certificate',
        'Admin Access': 'Admin Access',
        'System Function Switch': 'System Function Switch'
    }
}
export default {
    name: 'sidebar',
    data() {
        return {
            menuFederatedList,
            menusettingList
            // active: 'Site Manage'
        }
    },
    computed: {
        ...mapGetters(['autostatus', 'active'])
    },
    watch: {
        $route: {
            handler: function(val) {
                if (val.name === 'siteadd' || val.name === 'detail') {
                    this.$store.dispatch('SetMune', 'Site Manage')
                } else if (val.name === 'Add an Exchange') {
                    this.$store.dispatch('SetMune', 'IP Manage')
                } else if (val.name === 'partyuser') {
                    this.$store.dispatch('SetMune', 'Party ID')
                } else {
                    this.$store.dispatch('SetMune', val.name)
                }
            },
            immediate: true
        }
    },
    created() {
        this.init()
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    methods: {
        init() {
            if (this.active !== 'System Function Switch') {
                switchState().then(res => {
                    res.data.forEach(item => {
                        if (item.functionName === 'Auto-Deploy') {
                            this.$store.dispatch('Getautostatus', item.status === 1)
                        }
                        if (item.functionName === 'Site-Authorization') {
                            this.$store.dispatch('Getsitestatus', item.status === 1)
                        }
                    })
                })
            }
        },
        handleOpen(key, keyPath) {

        },
        handleClose(key, keyPath) {

        },
        handleSelect(key, keyPath) {
            if (this.$route.name === key) {
                return
            }
            this.$store.dispatch('ToggleSideBar', keyPath)
            this.$store.dispatch('SetMune', key)
            this.$router.push({
                name: key
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.sidebarbox {
    position: absolute;
    top: 65px;
    width: 245px;
    height: calc(100% - 65px);
    background-color: #e6ebf0;
    overflow: auto;
    .sidebar {
        //  width: 260px !important;
        .el-menu-item:hover {
            background: #e6ebf0 !important;
            color: #005aba !important;
        }
        .is-active{
            font-weight: 600 !important;
        }
        .el-submenu__title:hover {
            background: #e6ebf0 !important;
            color: #005aba !important;
        }
        .el-submenu__title{
            padding:0px !important;
            margin-left: 20px
        }
        .el-icon-arrow-down:before {
            content: '\E790';
            width: 10px;
            height: 6px;
            color: rgba(78, 87, 102, 1);
            font-size: 14px
        }
        .el-submenu {
            .title {
                color: #4e5766;
                font-weight: bold;
                border-bottom: 2px solid #dce1e6;
                height: 54px;
                font-size: 16px;
                // padding: 0;
                img {
                    color: #005aba;
                    width: 18px;
                    margin-right: 12px;
                }
            }
            .title:hover {
                color: #005aba;
            }
            .el-menu-item {
                background: rgb(230, 235, 240) !important;
                padding: 0px 0 !important;
                width: 75%;
                margin-left: 45px;
                font-weight: 400;
                border-bottom: 2px solid #dce1e6;
                font-size: 14px;
                color: #e6ebf0;
            }
        }
        .el-menu-item-group__title {
          padding: 0;
        }
    }
}
</style>
