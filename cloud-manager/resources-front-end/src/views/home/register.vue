<template>
  <div class="register">
    <div class="title">Federated Organization Register</div>
    <div class="organization">
      <div class="name-tip">
        <span>Federated Organization Name</span>
      </div>
      <div class="name">
        <el-input
          v-model="inputName"
          :class="{ active: nameActive }"
          placeholder="input Federated Organization Name"
        ></el-input>
      </div>
    </div>
    <div class="organization">
      <div class="name-tip">
        <span>Institution</span>
      </div>
      <div class="name">
        <el-input
          v-model="inputInst"
          :class="{ active: instActive }"
          placeholder="input institution"
        ></el-input>
      </div>
      <el-button class="OK-btn" :type="type" :disabled="disabledbtn" @click="dialogVisible=true">OK</el-button>
      <el-dialog :visible.sync="dialogVisible" :close-on-click-modal="false" :close-on-press-escape="false" width="775px">
        <div class="line-text-one">You are creating your federated organization:</div>
        <div class="line-text-one">your name is :
            <span style="color:#217AD9">  {{inputName}} </span>

        </div>
        <div class="line-text-one">your institution is :
            <span style="color:#217AD9"> {{inputInst}} </span>
        </div>
        <div class="line-text-two">The organization information will be synchronized to your federated sites.</div>
        <div class="line-text-three">Are you sure to creat it ?</div>
        <div class="dialog-footer">
          <el-button class="sure-btn" type="primary" @click="sureAction">Sure</el-button>
          <el-button class="cancel-btn" type="info" @click="dialogVisible = false">Cancel</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { register, find } from '@/api/welcomepage'
// import { setCookie } from '@/utils/auth'
export default {
    name: 'home',
    components: {},
    data() {
        return {
            dialogVisible: false,
            nameActive: false,
            instActive: false,
            disabledbtn: true,
            type: 'info',
            inputName: '',
            inputInst: ''
        }
    },
    watch: {
        inputName: {
            handler: function(val) {
                this.showBtn()
                if (val) {
                    this.nameActive = true
                } else {
                    this.nameActive = false
                }
            },
            immediate: true
        },
        inputInst: {
            handler: function(val) {
                this.showBtn()
                if (val) {
                    this.instActive = true
                } else {
                    this.instActive = false
                }
            },
            immediate: true
        }
    },
    computed: {},
    methods: {
        showBtn() {
            if (this.inputName && this.inputInst) {
                this.disabledbtn = false
                this.type = 'primary'
            } else {
                this.disabledbtn = true
                this.type = 'info'
            }
        },
        sureAction() {
            // this.dialogVisible = true
            let params = {
                institution: this.inputInst,
                name: this.inputName
            }
            register(params).then((res) => {
                this.$store.dispatch('setSiteStatus', 'registered')// 注册
                find().then(res => {
                    if (res.data.name) {
                        this.$store.dispatch('getInfo', res.data)
                        this.dialogVisible = true
                        this.$router.push({
                            name: 'Site Manage',
                            path: '/federated/site',
                            query: {}
                        })
                    } else {
                        this.$router.push({
                            name: 'register'
                        })
                    }
                })
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
</style>
