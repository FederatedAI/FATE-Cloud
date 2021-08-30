<template>
    <el-dialog  class="register" :title="$t('m.sitemanage.register')" :visible.sync="registerVisible" width="500px" >
        <div class="organization">
        <div class="name-tip">
            <span>{{$t('m.sitemanage.pleaseEnterRegistration')}}</span>
        </div>
        <el-form ref="inputform" :model="inputform" :rules="rules" @submit.native.prevent >
            <el-form-item :class="{ name:true,'name-warn': warnActive }" prop="inputUrl">
                <el-input :class="{ 'active': inputClass }" placeholder="" clearable v-model="inputform.input"></el-input>
                <div class="warn-text">
                    <span v-show='warnActive'>{{$t('m.sitemanage.registrationInvalid')}}</span>
                </div>
            </el-form-item>
        </el-form>
        </div>
        <div class="dialog-footer btn">
            <el-button class="ok-btn" :type="type" :disabled="disabledbtn" @click="okAction">{{$t('m.common.OK')}}</el-button>
            <el-button class="ok-btn" type="info"  @click="cancelAction">{{$t('m.common.cancel')}}</el-button>
        </div>
    </el-dialog>
</template>

<script>
import { checkUrl } from '@/api/welcomepage'
import { decode64, utf8to16 } from '@/utils/base64'
export default {
    name: 'home',
    components: {},
    data() {
        return {
            registerVisible: false,
            inputClass: false, // 是否显示输入框样式
            warnActive: false, // 显示警告样式
            disabledbtn: true, // 按钮可点击
            type: 'info',
            inputform: { inputUrl: '' },
            inputUrl: '', // 输入框URL
            rules: {
                inputUrl: [
                    { type: 'url', message: ' ', trigger: 'change' }
                ]
            }
        }
    },
    watch: {
        'inputform.input': {
            handler: function(val) {
                // this.showBtn()
                if (val) {
                    this.inputClass = true
                    let url = this.inputform.input
                    if (url.indexOf('?st') < 0) {
                        url = url.split('\\n').join('')
                        this.inputform.inputUrl = url ? utf8to16(decode64(url)).split('?st')[0] : ''
                    } else {
                        this.inputform.inputUrl = url.split('?st')[0]
                    }
                    console.log(url, 'url-watch')
                    console.log(this.inputform.inputUrl, 'url-inputUrl')
                    this.$refs['inputform'].validateField('inputUrl', valid => {
                        if (valid) {
                            this.warnActive = true
                        } else {
                            this.disabledbtn = false
                            this.type = 'primary'
                        }
                    })
                } else {
                    this.disabledbtn = true
                    this.type = 'info'
                    this.warnActive = false
                    this.inputClass = false
                }
            },
            immediate: true
        }
    },
    computed: {},
    mounted() {

    },
    methods: {

        okAction() {
            let Url = this.inputform.input
            try {
                let data = {}
                data.link = Url
                console.log(data, 'data')
                checkUrl(data).then(res => {
                    if (res.code === 0) {
                        this.$router.push({
                            name: 'activate',
                            query: { data: res.data }
                        })
                    }
                }).catch(res => {
                    console.log(res, 'warn')
                    // this.warnActive = true
                })
            } catch (err) {
                console.log(err, 'erro')
                // this.warnActive = true
            }
        },
        cancelAction() {
            this.registerVisible = false
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/register.scss';
</style>
