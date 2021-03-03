<template>
  <div class="contentbox">
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="(item,index) in path" :key="index">
                <span style="margin-left:10px" class="item" v-if="index===0" @click="toroute(item)">{{$t(`${item}`)}}</span>
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
// import { mapGetters } from 'vuex'
export default {
    name: 'contentbox',
    data() {
        return {
            path: []
        }
    },
    watch: {
        $route: {
            handler() {
                this.toPath()
            }
        }
    },

    created() {
        this.toPath()
    },
    methods: {
        toPath() {
            let name = this.$route.name
            if (name === 'sitemanage' || name === 'siteinfo') {
                this.path = ['Manage', 'Site Manage']
            } else if (name === 'access') {
                this.path = ['Manage', 'User Access']
            } else if (name === 'overview') {
                this.path = ['Auto-deploy', 'Overview']
            } else if (name === 'service') {
                this.path = ['Auto-deploy', 'Site Service Management']
            } else if (name === 'cooperation') {
                this.path = ['Monitor', 'Site Cooperation']
            } else if (name === 'jobmonitor') {
                this.path = ['Monitor', 'job Monitor']
            } else if (name === 'Admin Access') {
                this.path = ['Setting', 'Admin Access']
            } else if (name === 'System Function Switch') {
                this.path = ['Setting', 'System Function Switch']
            }
        },
        toroute(item) {
            if (item === 'Manage') {
                this.$router.push({ name: 'sitemanage' })
            }
            if (item === 'Auto Deploy') {
                this.$router.push({ name: 'overview' })
            }
            if (item === 'Monitor') {
                this.$router.push({ name: 'cooperation' })
            }
        }
    }

}
</script>

<style rel="stylesheet/scss" lang="scss" >
.breadcrumb{
    background-color: #F5F8FA;

    .el-breadcrumb{
        background-color: #E6EBF0;
        font-size: 14px;
        height: 28px;
        line-height: 28px;
        margin: 0 auto;
        width: 1400px;
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
        }
    }

}
</style>
