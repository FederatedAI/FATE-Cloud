<template>
  <div class="welcomepage">
    <div class="title">
      <span>Welcome to FATE Cloud!</span>
    </div>
    <div class="line"></div>
    <div class="text">
      <span>It is an Infrastructure for Building and Managing Federated Data Collaboration Network.</span>
    </div>
    <div class="text-two">
      <span>Before you start building your federated data collaboration network, register your federated organization.</span>
    </div>
    <div class="text-two">
      <span>The name and information of the organization will be synchronized to the federated sites.</span>
    </div>
    <el-button class="begin-btn" type="primary" @click="begin">Begin</el-button>
    <div class="round">
      <span class="circul"></span>
      <span class="circul"></span>
      <span class="circul"></span>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
// import { find } from '@/api/welcomepage'
// import { setCookie } from '@/utils/auth'
export default {
    name: 'home',
    components: {},
    data() {
        return {}
    },
    computed: {
        ...mapGetters([
            'siteStatus'
        ])
    },
    watch: {},
    // computed: {},
    created() {},
    methods: {
        begin() {
            this.$router.push({ name: 'login' })
            // if (this.siteStatus === 'registered') {
            //     this.$router.push({
            //         path: '/federated/site'
            //     })
            // } else {
            //     this.$router.push({
            //         path: '/home/register'
            //     })
            // }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
</style>
