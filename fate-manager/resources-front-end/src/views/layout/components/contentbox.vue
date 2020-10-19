<template>
  <div class="contentbox">
    <router-view />
  </div>
</template>

<script>
// import { mapGetters } from 'vuex'
export default {
    name: 'contentbox',
    data() {
        return {
            // path: [],
            // detailShow: false
        }
    },
    watch: {
        // $route: {
        // handler: function(val) {
        //     this.toPath()
        // },
        // immediate: true
        // }
    },
    computed: {
        // ...mapGetters(['siteName'])
    },
    // 防止刷新
    created() {

    },
    methods: {

    }

}
</script>

<style rel="stylesheet/scss" lang="scss" >
</style>
