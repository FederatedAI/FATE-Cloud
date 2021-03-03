<template>
    <div class="log">
        <div class="log-box">
            <div class="log-title">
                <span  class="text" style=" margin-left: 48px;">
                    all
                    <span @click="toScreen('all')" style="color:#FC6A16;cursor: pointer;">
                        {{ data.all && data.all.length || 0 }}
                    </span>
                </span>
                <span v-if="data.error"   class="text">
                    error
                    <span @click="toScreen('error')" style="color:#FC6A16;cursor: pointer;">{{data.error && data.error.length || 0}}</span>
                </span>
                <span v-if="data.waring" class="text">
                    warning
                    <span  @click="toScreen('waring')" style="color:#FF9D00;cursor: pointer;">{{data.waring && data.waring.length || 0}}</span>
                </span>
                <span v-if="data.info" class="text">
                    info
                    <span @click="toScreen('info')" style="color:#00D269;cursor: pointer;">{{data.info && data.info.length || 0}}</span>
                </span>
                <span v-if="data.debug" class="text">
                    debug
                    <span @click="toScreen('debug')" style="color:#217AD9;cursor: pointer;">{{data.debug && data.debug.length || 0}}</span>
                </span>
            </div>
            <div class="log-content">
                <div v-if="dataList.length>0" class="log-li">
                    <span v-for="(item, index) in dataList" :key="index">
                        <span class="content-text">{{index + 1}}</span>
                        <span class="content-text">{{item}}</span>
                    </span>
                </div>
                <div v-else class="log-li">
                    <div style="text-align: center;">No DATA</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    name: 'log',
    data() {
        return {
            data: {},
            dataList: []
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
    created() {},
    mounted() {
        this.toScreen('error')
    },
    methods: {
        toScreen(type) {
            this.dataList = []
            for (const key in this.data) {
                if (type === key) {
                    this.data[key].forEach((item, index) => {
                        this.dataList.push(`${item}`)
                    })
                }
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.log{
    .log-box{

        .log-title{
            height: 42px;
            line-height: 42px;
            border-bottom: 2px solid #E6EBF0;
            .text{
                margin-right: 36px;
                color: #2D3642;
                font-size: 18px;
                span{
                    text-decoration: underline;
                    font-size: 16px
                }
            }
            .input{
                width: 260px;
                .el-input__inner{
                    height: 24px;
                }
                .el-input__suffix{
                    line-height: 54px;
                    cursor: pointer;
                }
            }
        }
        .log-content{
            position: absolute;
            height: calc(100% - 10px);
            width: 100%;
            overflow: auto;
            border-bottom: 1px solid #E6EBF0;
            // height: 560px;
            overflow: auto;
            padding: 24px 0;
            .log-li{
                font-size: 14px;
                color: #848C99;
                .content-text{
                    vertical-align: top;
                    display: inline-block;
                }
                .content-text:nth-child(1){
                    width: 50px;
                }
                .content-text:nth-child(2){
                    width: calc(100% - 50px);
                }
            }

        }
    }
}
</style>
