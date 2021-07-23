<template>
  <div class="servicelog">
      <el-dialog
        :visible.sync="logdialog"
        class="log-dialog" width="1200px" >
        <div class="log-box">
            <div class="log-title">
                    <span class="title-text">Log</span>
                    <span v-if="data.all" class="text">
                        all
                        <span @click="toScreen('all')" style="color:#FC6A16;cursor: pointer;">
                            {{ data.all.length}}
                        </span>
                    </span>
                    <span v-if="data.error" class="text">
                        error
                        <span @click="toScreen('error')" style="color:#FC6A16;cursor: pointer;">{{ data.error.length}}</span>
                    </span>
                    <span v-if="data.warn" class="text">
                        warning
                        <span @click="toScreen('warn')" style="color:#FF9D00;cursor: pointer;">{{data.warn.length}}</span>
                    </span>
                    <span v-if="data.info" class="text">
                        info
                        <span @click="toScreen('info')" style="color:#00D269;cursor: pointer;">{{data.info.length}}</span>
                    </span>
                    <span v-if="data.debug" class="text">
                        debug
                        <span @click="toScreen('debug')" style="color:#217AD9;cursor: pointer;">{{data.debug.length}}</span>
                    </span>
                    <span v-if="data.other" class="text">
                        other
                        <span @click="toScreen('other')" style="color:#217AD9;cursor: pointer;">{{data.other.length}}</span>
                    </span>
                    <!-- <el-input class="input" v-model="name"   placeholder="search">
                        <i slot="suffix" class="el-icon-search"/>
                    </el-input> -->
            </div>
            <div class="log-content">
                <div v-if="dataList.length>0" class="log-li">
                    <span v-for="(item, index) in dataList" :key="index">
                        <span class="content-text">{{index + 1}}</span>
                        <span class="content-text">{{item}}</span>
                    </span>
                </div>
                <div  v-else class="log-li">
                        <div style="text-align: center;">No DATA</div>
                </div>
            </div>
        </div>
    </el-dialog>
  </div>
</template>

<script>

export default {
    name: 'servicelog',
    components: {

    },
    data() {
        return {
            name: '',
            logdialog: false,
            data: {},
            dataList: []
        }
    },

    methods: {
        toScreen(type) {
            this.dataList = []
            for (const key in this.data) {
                if (type === key) {
                    this.data[key].forEach((item, index) => {
                        this.dataList.push(`${item}`)
                    })
                } else if (type === 'all') {
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
    .servicelog{

        .el-dialog__header{
            padding: 0;
            .el-dialog__headerbtn{
                top:15px;
                right:15px;
                font-size: 20px;
            }
        }
        .el-dialog__body{
            padding: 30px;
        }
        .log-box{

            .log-title{
                height: 40px;
                margin-bottom: 30px;
                .title-text{
                    color: #217AD9;
                    font-weight: bold;
                    font-size: 32px;
                    margin-right: 50px
                }
                .text{
                    // display:inline-block;
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
                        line-height: 27px;
                        cursor: pointer;
                    }
                }
            }
            .log-content{
                border-top: 2px solid #E6EBF0;
                height: 560px;
                overflow: auto;
                padding: 24px 0;
                .log-li{
                    // margin-bottom: 5px;
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
