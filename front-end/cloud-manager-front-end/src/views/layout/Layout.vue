<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="begin" @click="toHome">
          <!-- <img src="@/assets/logo.png"> -->
          <span>FATE Cloud</span>
        </div>
        <topbar ref="topbar" />
      </el-header>
      <el-main>
        <sidebar />
        <contentbox  />
      </el-main>
    </el-container>
  </div>
</template>

<script>

import topbar from './components/topbar'
import sidebar from './components/sidebar'
import contentbox from './components/contentbox'

export default {
    name: 'Layout',
    components: {
        topbar,
        sidebar,
        contentbox
    },
    data() {
        return {
            name: '',
            tooltip: false
        }
    },

    methods: {
        toHome() {
            this.$router.push({ path: '/' })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/home.scss';

</style>
