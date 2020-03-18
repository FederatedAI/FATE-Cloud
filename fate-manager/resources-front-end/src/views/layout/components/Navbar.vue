<template>
  <div class="navbar">
    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      background-color="#2C2C34"
      text-color="#fff"
      active-text-color="#fff"
      @select="handleSelect"
    >
      <el-menu-item index="1">System Configuration</el-menu-item>
    </el-menu>
    <el-dropdown class="avatar-container" trigger="click">
      <div class="accout-box">
        <img src="@/assets/favicon.png" class="icon" >
        <!-- <el-avatar :size="size" :src="circleUrl" class="icon" /> -->
        <el-tooltip v-if="boxWidth" :content="name" class="item" effect="light" placement="bottom">
          <span class="box" style="margin: 0 -5px;">{{ name }}</span>
        </el-tooltip>
        <div v-if="!boxWidth" class="box">
          <span ref="box"> {{ name }} </span>
        </div>
        <i class="el-icon-caret-bottom " />
      </div>
      <el-dropdown-menu slot="dropdown" class="user-dropdown">
        <el-dropdown-item>
          <span style="display:block;" @click="toAccountinfo">User Info</span>
        </el-dropdown-item>
        <el-dropdown-item divided>
          <span style="display:block;" @click="logout">Log Out</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      // activeName: '',
      activeIndex: '1',
      // circleUrl: '../../.././assets/favicon.png',
      size: 'small',
      boxWidth: false
    }
  },
  computed: {
    ...mapGetters([
      'roles', // 权限(是否已经认证)
      'avatar', // 用户头像
      'name' // 用户名字
    ])
  },
  watch: {
    '$route.name': {
      handler(newval, oldval) {
        if (newval === 'cloud') {
          this.activeIndex = '1'
        } else if (newval === 'clist' || newval === 'sdetail') {
          this.activeIndex = '2'
        }
      },
      immediate: true
    }
  },
  mounted() {
    const width = this.$refs.box.offsetWidth
    if (width > 100) {
      this.boxWidth = true
    }
  },
  methods: {
    handleSelect(key) {
      this.activeIndex = key
      if (key === '1') {
        if (this.$route.name !== 'gather') {
          this.$router.push({ path: '/fate/system/cfg' })
        }
      }
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload() // 为了重新实例化vue-router对象 避免bug
      })
    },
    toAccountinfo() {
      this.$router.push({ path: '/fate/accountinfo' })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.navbar {
  // position: relative;
  height: 75px;
  line-height: 75px;
  .el-menu-demo {
    position: absolute;
    left: 227px;
    .el-menu-item {
      height: 75px;
      line-height: 75px;
      font-size: 16px;
    }
  }
  .is-active {
    border-bottom-color: #fff !important;
    background: #444 !important;
  }
  .avatar-container {
    cursor: pointer;
    height: 75px;
    display: inline-block;
    position: absolute;
    right: 20px;
    top: 0px;
    font-size: 16px;
    color: #fff;
    .accout-box{
      .icon{
        background-color: #ccc;
        width: 30px;
        height: 30px;
        border-radius: 15px
      }
      .box{
        max-width: 100px;
        color: #fff;
        vertical-align: middle;
        overflow: hidden;
        text-overflow:ellipsis;
        white-space: nowrap;
        display: inline-block;
      }
      .attestation{
        vertical-align: middle;
        border-radius: 6px;
        padding: 3px;
      }
    }
  }
}
</style>

