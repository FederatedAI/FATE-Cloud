<template>
  <div class="home">
    <div class="dashboard-text"></div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Home',
  computed: {
    ...mapGetters([
      'name',
      'roles'
    ])
  },
  methods: {
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.home {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 20px;
    line-height: 46px;
  }
}
</style>
