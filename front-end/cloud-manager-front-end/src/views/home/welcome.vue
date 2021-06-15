<template>
  <div class="welcomepage">
    <div class="title">
      <span>{{$t('Welcome to FATE Cloud!')}}</span>
    </div>
    <div class="line"></div>
    <div class="text">
      <span>{{$t('Welcome_text_one')}}</span>
    </div>
    <div class="text-two">
      <span>{{$t('Welcome_text_two')}}</span>
    </div>
    <div class="text-two">
      <span>{{$t('Welcome_text_three')}}</span>
    </div>
    <el-button class="begin-btn" type="primary" @click="begin">{{$t('Begin')}}</el-button>
    <!-- <div class="round">
      <span class="circul"></span>
      <span class="circul"></span>
      <span class="circul"></span>
    </div> -->
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
// 国际化
const local = {
    zh: {
        'Welcome to FATE Cloud!': '欢迎来到FATE Cloud!',
        'Welcome_text_one': '作为构建和管理联邦数据合作网络的基础设施，提供一站式联邦数据合作服务',
        'Welcome_text_two': '开始搭建你的联邦数据合作网络前，请先注册联邦组织。',
        'Welcome_text_three': '名称等联邦组织信息将会同步展示到旗下各联邦站点处。',
        'Begin': '开始'

    },
    en: {
        'Welcome to FATE Cloud!': 'Welcome to FATE Cloud!',
        'Welcome_text_one': 'It is an Infrastructure for Building and Managing Federated Data Collaboration Network',
        'Welcome_text_two': 'Before you start building your federated data collaboration network, register your federated organization.',
        'Welcome_text_three': 'The name and information of the organization will be synchronized to the federated sites.',
        'Begin': 'Begin'
    }
}

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
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
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
