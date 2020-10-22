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
                <div class="item" @click="clickSite">Site Manage</div>
                <div class="item" v-if="role.roleName==='Admin'" @click="clickAccess">User Access</div>
                <div slot="reference" >
                    <span :class="{active:active}">Manage</span>
                </div>
            </el-popover>
        </el-menu-item>
        <el-menu-item v-if='autoState' index="Auto-deploy" :class="{notactive:active}" >Auto-deploy</el-menu-item>
    </el-menu>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    data() {
        return {
            visible: false,
            active: false,
            activeName: 'first',
            activeIndex: 'Auto-deploy'
        }
    },

    watch: {
        $route: {
            handler(value) {
                if (value.name === 'sitemanage' || value.name === 'access') {
                    this.active = true
                } else {
                    this.active = false
                }
            }
        }

    },
    computed: {
        ...mapGetters(['role', 'autoState'])
    },
    mounted() {
        if (this.$route.name === 'sitemanage' || this.$route.name === 'access') {
            this.activeIndex = 'Manage'
        }
    },
    methods: {
        handleSelect(key, keyPath) {
            if (key === 'Auto-deploy') {
                this.$router.push({ name: 'overview' })
            } else {
                keyPath = key = 'Manage'
            }
        },
        clickSite() {
            // this.visible = false
            this.$router.push({
                name: 'sitemanage'
            })
        },
        clickAccess() {
            // this.visible = false
            this.$router.push({
                name: 'access'
            })
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
        left: 265px;
        .el-menu-item:hover{
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
    line-height: 35px;
    margin-top:0 !important;
    min-width: 124px !important;
    padding: 5px;
    .item{
        cursor: pointer;
        font-size: 16px;
        color: #217AD9;
    }
    .item:hover{
        background-color: #ecf5ff;
    }

}

</style>
