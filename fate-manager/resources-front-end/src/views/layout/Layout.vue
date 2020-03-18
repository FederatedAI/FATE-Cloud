<template>
  <div class="app-wrapper">
    <el-container style="height:100%">
      <el-header>
        <div class="logo" @click="toHome">
          <!-- <img src="@/assets/fate_logo.png" style="height:30px;"> -->
          <span>FATECloud</span>
          <!-- <span></span> -->
        </div>
        <navbar v-if="name"/>
      </el-header>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import Navbar from './components/Navbar'
import Breadcrumb from '@/components/Breadcrumb'
import { mapGetters } from 'vuex'

export default {
  name: 'Layout',
  components: {
    Navbar,
    Breadcrumb
  },
  data() {
    return {
      // imgCode: '@/assets/service.png'
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  methods: {
    toHome() {
      this.$router.push({ path: '/' })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
  // @import "src/styles/mixin.scss";
  .app-wrapper {
    // @include clearfix;
    position: relative;
    height: 100%;
    // width: 100%;
    // min-width: 1440px;
    &.mobile.openSidebar {
      position: fixed;
      top: 0;
    }
    .fix{
      position: fixed;
      background-color: #2C2C34;
      right: 0px;
      top:calc(50% - 70px );
      color: #fff;
      text-align: center;
      line-height: 70px;
      .wechat{
        border-bottom: 1px solid #fff;
      }
      .wechat,.phone{
        cursor: pointer;
        width: 70px;
        height: 70px;
      }

    }
  }
   .wechat-code-box{
      text-align: center;
      .wechat-code{
        width: 200px;
        height: 200px;
        margin-top: 10px;
        line-height: 200px;
        color: #393939;
        // border: 1px solid #666;
      }
    }
  .phone-box{
    text-align: center;
    line-height: 45px;
    height: 45px;
    font-size: 18px;
    color: #393939
  }
  .el-header {
    padding: 0;
    background-color: #2C2C34;
    color: #fff;
    text-align: center;
    height: 75px !important;
    line-height: 75px;
    border-bottom:1px  solid #2C2C34;
    .logo {
      cursor: pointer;
      position: absolute;
      left: 40px;
      font-size: 20px;
    }
  }
  .el-main{
    height: 100%;
    padding: 0px;
    background-color: #EEF0F2;
  }
</style>
