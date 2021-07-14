<template>
<div>
    <img src="@/assets/welcomepage.svg" />
    <div class="welcomepage">
        <div class="title">
        <span>{{$t('m.welcome.welcome')}}</span>
        </div>
        <div class="line"></div>
        <div class="text">
        <span>{{$t('m.welcome.introductions')}}</span>
        </div>
        <div class="text-two">
        <span>{{$t('m.welcome.startRegistering')}}</span>
        </div>
        <div class="text-two">
        <span>{{$t('m.welcome.afterJoining')}}</span>
        </div>
        <el-button class="begin-btn" type="primary" @click="begin">{{$t('m.welcome.register')}}</el-button>
        <div class="round">
        <span class="circul"></span>
        <span class="circul"></span>
        <span class="circul"></span>
        </div>
    </div>
</div>
</template>

<script>

export default {
    name: 'home',
    components: {},
    data() {
        return {}
    },
    watch: {},
    computed: {},
    created() {},
    methods: {
        begin() {
            this.$router.push({
                name: 'register'
                // path: '/home/register',
                // query: {}

            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
</style>
