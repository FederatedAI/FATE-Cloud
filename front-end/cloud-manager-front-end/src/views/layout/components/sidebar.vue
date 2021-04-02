<template>
  <div class="sidebarbox">
    <div class="sidebar">
      <el-menu
        :default-active="active"
        class="el-menu-vertical-demo"
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
              <!-- <i class="el-icon-location"></i> -->
              <img src="@/assets/federated.png">
              <span>Federated Site</span>
            </div>
          </template>
          <el-menu-item-group>
            <el-menu-item index="Site Manage">Site Manage</el-menu-item>
            <el-menu-item index="IP Manage">IP Manage</el-menu-item>
            <el-menu-item v-if='autostatus' index="Service Manage">Service Manage</el-menu-item>
            <el-menu-item index="Site Monitor">Site Monitor</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
        <el-submenu index="Setting">
          <template slot="title">
            <div class="title">
              <!-- <i class="el-icon-s-tools"></i> -->
              <img src="@/assets/setting.png">
              <span>Setting</span>
            </div>
          </template>
          <el-menu-item-group>
            <el-menu-item index="Party ID">Party ID</el-menu-item>
            <el-menu-item index="Admin Access">Admin Access</el-menu-item>
            <el-menu-item index="System Function Switch">System Function Switch</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { switchState } from '@/api/setting'
export default {
    name: 'sidebar',
    data() {
        return {
            active: 'Site Manage'
        }
    },
    computed: {
        ...mapGetters(['autostatus'])
    },
    watch: {
        $route: {
            handler: function(val) {
                if (val.name === 'siteadd' || val.name === 'detail') {
                    this.active = 'Site Manage'
                } else if (val.name === 'partyuser') {
                    this.active = 'Party ID'
                } else {
                    this.active = val.name
                }
            },
            immediate: true
        }
    },
    created() {
        this.init()
    },
    methods: {
        init() {
            switchState().then(res => {
                res.data.forEach(item => {
                    if (item.functionName === 'Auto-Deploy') {
                        this.$store.dispatch('Getautostatus', item.status === 1)
                    }
                })
            })
        },
        handleOpen(key, keyPath) {

        },
        handleClose(key, keyPath) {

        },
        handleSelect(key, keyPath) {
            this.$store.dispatch('ToggleSideBar', keyPath)
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
    width: 300px;
    height: calc(100% - 65px);
    background-color: #e6ebf0;
    overflow: auto;
    .sidebar {
        .el-menu-item:hover {
            background: #e6ebf0 !important;
            color: #005aba !important;
        }
        .el-submenu__title:hover {
            background: #e6ebf0 !important;
            color: #005aba !important;
        }
        .el-icon-arrow-down:before {
            content: '\E790';
            width: 10px;
            height: 6px;
            color: rgba(78, 87, 102, 1);
            font-size: 14px
        }
        .el-submenu {
            padding-left: 16px;
            .title {
                color: #4e5766;
                font-weight: bold;
                border-bottom: 2px solid #dce1e6;
                height: 54px;
                font-size: 16px;
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
                font-size: 16px;
                color: #e6ebf0;
            }
        }
        .el-menu-item-group__title {
          padding: 0;
        }
    }
}
</style>
