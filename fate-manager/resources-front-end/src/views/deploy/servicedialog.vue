<template>
  <div class="servicedialog">
    <el-dialog
    :visible.sync="servicedialog"
    width="700px"
    :close-on-click-modal="false"
    :close-on-press-escape="false">
        <div class="dialog-box">
            <span>{{title}}</span>
        </div>
        <div class="dialog-foot">
            <el-button v-if="type==='test'" type="primary" @click="toOK">OK</el-button>
            <el-button v-if="type!=='test'" type="primary" @click="toSure">Sure</el-button>
            <el-button v-if="type!=='test'" type="info" @click="servicedialog=false">Cancel</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import { toAction, testresultread } from '@/api/deploy'

export default {
    name: 'servicedialog',
    props: {
        formInline: {
            type: Object,
            default: function () {
                return {}
            }
        }
    },
    components: {

    },
    data() {
        return {
            servicedialog: false,
            title: '',
            type: '',
            params: {}// 父集传过来的参数
        }
    },
    watch: {
        // type: {
        //     handler: function(val) {
        //         console.log('==>>', val)
        //     },
        //     immediate: true
        // }
    },

    computed: {
        // ...mapGetters([
        //     'name' // 用户名字
        // ])
    },
    methods: {
        toSure() {
            if (this.type === 'restart' || this.type === 'stop') {
                let data = { ...this.params }
                toAction(data).then(res => {
                    this.servicedialog = false
                    this.$parent.getinitiList()
                })
            }
        },
        toOK() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1,
                toyTestOnlyRead: 0
            }
            testresultread(data).then(res => {
                this.servicedialog = false
                this.$parent.getinitiList()
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
    .servicedialog{
        .el-dialog__header{
            display: none;
        }
        .el-dialog__body{
            padding: 0;
        }
        .dialog-box{
            margin-top: 48px;
            color: #4E5766;
            font-size: 24px;
            text-align: center;
        }
        .dialog-foot{
            text-align: center;
            margin: 48px 0;
            .el-button{
                width: 200px;
                height: 36px;
                padding: 0;
                font-size: 18px;
            }
        }
    }
</style>
