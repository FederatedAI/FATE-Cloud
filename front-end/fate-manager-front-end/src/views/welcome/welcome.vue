<template>
<div>
    <img src="@/assets/welcomepage.svg" />
    <div class="welcomepage">
        <div class="title">
        <span>Welcome to FATE Cloud!</span>
        </div>
        <div class="line"></div>
        <div class="text">
        <span>It is an Infrastructure for Building and Managing Federated Data Collaboration Network.</span>
        </div>
        <div class="text-two">
        <span>Start registering and joining a federated organization.</span>
        </div>
        <div class="text-two">
        <span>After successfully joining the organization, you are ready for federated modeling.</span>
        </div>
        <el-button class="begin-btn" type="primary" @click="begin">Register</el-button>
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
