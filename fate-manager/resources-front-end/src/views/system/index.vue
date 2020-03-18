<template>
  <div class="product-service">
    <div class="aside">
      <el-tabs v-model="active" tab-position="left" @tab-click="handleClick">
        <el-tab-pane name="myService">
          <span slot="label"> System Configuration</span>
          <!-- <i class="el-icon-s-tools" style="margin-right:5px;"/> -->
        </el-tab-pane>
      </el-tabs>
      <el-link :href="getUrl()" type="primary" class="link-btn" target="_blank">FATEBoard <i class="el-icon-right"/></el-link>
    </div>
    <div class="main">
      <router-view/>
    </div>
  </div>

</template>

<script>
// import { mapGetters } from 'vuex'

export default {
  name: 'ProductService',
  data() {
    return {
      active: 'myService'
    }
  },
  computed: {
    // ...mapGetters([
    //   'name',
    //   'roles'
    // ])
  },
  mounted() {

  },
  methods: {
    handleClick(tab, event) {
    },
    getUrl() {
      return 'http://' + window.location.host
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.product-service{

  position: absolute;
  top:75px;
  width: 100%;
  background-color: #fff;
  height: calc(100% - 75px);

  .aside{
    width: 180px ;
    background-color: #37373c;
    height: 100%;
    float: left;
    .el-tabs__active-bar{
      width: 0px;
    }
    .is-active{
      color: #fff;
      background: #2772DB;
    }
    .el-tabs--left .el-tabs__item.is-left{
      color: #fff;
      text-align: center;
    }
    .el-tabs--left .el-tabs__nav-wrap.is-left {
      margin-right: -2px;
    }
    .el-tabs .el-tabs__header{
      margin-top: 30px;
      width: 180px;
    }
    .el-icon-tickets{
      margin-right: 5px;
    }
  }
  .main{
      height: calc(100% - 20px);
      position: relative;
      overflow-y: auto;
  }
  .link-btn{
    position:absolute;
    bottom: 30px;
    left: 30px;
    // color: #fff;
    font-size: 16px;
    font-weight: 500;
  }
}

</style>
