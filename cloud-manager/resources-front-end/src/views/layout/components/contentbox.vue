<template>
  <div class="contentbox">
    <div class="breadcrumb">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item v-for="(item,index) in path" :key="index">
          <span class="item" v-if="index===0" @click="toroute(item)">{{item}}</span>
          <span v-else class="item active" @click="toroute(item)">{{item}}</span>
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="siteName">
          <span class="item">{{siteName}}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <router-view />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'contentbox',
    data() {
        return {
            path: []
        }
    },
    watch: {
        $route: {
            handler: function(val) {
                if (val.name !== 'partyuser' && val.name !== 'siteadd' && val.name !== 'detail') {
                    this.$store.dispatch('SiteName', '')
                }
                this.toPath()
            },
            immediate: true
        }
    },
    computed: {
        ...mapGetters(['siteName'])
    },
    // 防止刷新
    created() {
        this.toPath()
    },
    methods: {
        toPath() {
            let name = this.$route.name
            if (name === 'Site Manage' || name === 'siteadd' || name === 'detail') {
                this.path = ['Federated Site', 'Site Manage']
            } else if (name === 'IP Manage') {
                this.path = ['Federated Site', 'IP Manage']
            } else if (name === 'Service Manage') {
                this.path = ['Federated Site', 'Service Manage']
            } else if (name === 'Party ID' || name === 'partyuser') {
                this.path = ['Setting', 'Party ID']
            } else if (name === 'Repository') {
                this.path = ['Setting', 'Repository']
            } else if (name === 'Admin Access') {
                this.path = ['Setting', 'User Access']
            } else if (name === 'System Function Switch') {
                this.path = ['Setting', 'System Function Switch']
            }
        },
        toroute(item) {
            if (item === 'Federated Site' || item === 'Site Manage') {
                this.$router.push({ name: 'Site Manage' })
            }
            if (item === 'Setting' || item === 'Party ID') {
                this.$router.push({ name: 'Party ID' })
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.contentbox {
    position: absolute;
    top: 65px;
    left: 300px;
    background: #f5f8fa;
    height: calc(100% - 65px);
    width: calc(100% - 300px);
    // .el-icon-arrow-right {
    //     margin: 0 !important;
    // }
    .breadcrumb {
        height: 32px;
        margin-left: 36px;
        font-size: 14px;
        width: calc(100% - 72px);
        background-color: #e6ebf0;
        .el-breadcrumb {
            line-height: 32px;
            margin: 0 10px;

            .item {
                font-size: 14px;
                font-weight: 550;
                cursor: pointer;
                color: #848c99;
            }
            .item:hover {
                color: #217ad9;
            }
            .active {
                color: #217ad9;
            }
        }
    }
}
</style>
