<template>
  <div class="contentbox">
    <div class="breadcrumb">
      <el-breadcrumb separator="/" >
        <el-breadcrumb-item v-for="(item,index) in path" :key="index">
          <span class="item" v-if="index===0" @click="toroute(item)">{{$t(`${item}`)}}</span>
          <span v-else :class="{item:true,active:!$route.query.groupName}" @click="toroute(item)">{{$t(`${item}`)}}</span>
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="$route.query.groupName">
          <span class="active">{{$route.query.groupName}}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <router-view />
  </div>
</template>

<script>

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
                let routerList = ['partyuser', 'siteadd', 'detail', 'Add an Exchange']
                if (!routerList.some(item => item === val.name)) {
                    this.$store.dispatch('setSiteName', '')
                }
                this.toPath()
            },
            immediate: true
        }
    },
    created() {
        this.toPath()
    },
    methods: {
        toPath() {
            let name = this.$route.name
            if (name === 'Site Manage' || name === 'siteadd' || name === 'detail') {
                this.path = ['Federated Site', 'Site Manage']
            } else if (name === 'IP Manage' || name === 'Add an Exchange') {
                this.path = ['Federated Site', 'IP Manage']
            } else if (name === 'Service Manage') {
                this.path = ['Federated Site', 'Service Manage']
            } else if (name === 'Site Monitor') {
                this.path = ['Federated Site', 'Site Monitor']
            } else if (name === 'Job Monitor') {
                this.path = ['Federated Site', 'Job Monitor']
            } else if (name === 'Party ID' || name === 'partyuser') {
                this.path = ['Setting', 'Party ID']
            } else if (name === 'Certificate') {
                this.path = ['Setting', 'Certificate']
            } else if (name === 'Repository') {
                this.path = ['Setting', 'Repository']
            } else if (name === 'Admin Access') {
                this.path = ['Setting', 'Admin Access']
            } else if (name === 'System Function Switch') {
                this.path = ['Setting', 'System Function Switch']
            }
        },
        toroute(item) {
            if (this.$route.name === item) {
                return
            }
            if (item === 'Federated Site' || item === 'Site Manage') {
                this.$router.push({ name: 'Site Manage' })
            }
            if (item === 'Setting' || item === 'Party ID') {
                this.$router.push({ name: 'Party ID' })
            }
            if (item === 'IP Manage') {
                this.$router.push({ name: 'IP Manage' })
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.contentbox {
    position: absolute;
    top: 65px;
    left: 245px;
    background: #f5f8fa;
    height: calc(100% - 65px);
    width: calc(100% - 245px);
    .breadcrumb {
        font-size: 14px;
        width: calc(100% - 24px);
        // background-color: #e6ebf0;
        .el-breadcrumb {
            height: 40px;
            line-height: 40px;
            margin-left: 12px;
            .item {
                font-size: 14px;
                cursor: pointer;
                color: #848c99;
            }
            .item:hover {
                color: #217ad9;
            }
            .active {
                color: #4E5766;
                font-weight: 600;
            }
        }
    }
}
</style>
