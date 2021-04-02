<template>

  <div class="switch-box">
      <div class="card">
            <div class="card-title">Auto-deploy</div>
            <div class="card-switch" @click="toswitch('auto')">
                <el-switch v-model="autostatus" ></el-switch>
            </div>
            <div class="card-text">Manage the automated deployment and upgrade of the site</div>
      </div>
      <div class="card">
            <div class="card-title">Site-Authorization</div>
            <div class="card-switch"  @click="toswitch('site')">
                <el-switch v-model="sitestatus"></el-switch>
            </div>
            <div class="card-text">Control whether a FATE Manager can view other FATE Manager's sites</div>
      </div>
       <el-dialog :visible.sync="switchVisible" class="switch-dialog" width="700px">
        <div class="line-text-one">
            Are you sure you want to turn {{ status }} the function of
            <div>
                “<span style="color: #217AD9;">{{itmetext}}</span>”?
            </div>
        </div>
        <div class="line-text-two">
            After {{ statusdeploy }}, all related functions of
            {{itmeline}}
            be available.
        </div>
        <div class="dialog-footer">
          <el-button class="ok-btn" type="primary" @click="okAction">Sure</el-button>
          <el-button class="cancel-btn" type="info" @click="cancelAction">Cancel</el-button>
        </div>
      </el-dialog>
  </div>

</template>

<script>
import { switchState, updateSwitch } from '@/api/setting'
// import { mapGetters } from 'vuex'
export default {
    name: 'switchsys',
    components: {},

    data() {
        return {
            status: '',
            statusdeploy: '',
            switchVisible: false,
            autostatus: true, // 是否打开，默认打开
            sitestatus: true, // 是否打开，默认打开
            itmetext: '',
            itmeline: '',
            typedialog: '', // 弹框类型
            paramsData: {}
        }
    },
    created() {
        this.init()
    },

    mounted() {
    },
    methods: {
        init() {
            switchState().then(res => {
                res.data.forEach(item => {
                    if (item.functionName === 'Auto-Deploy') {
                        this.autostatus = item.status === 1
                        this.$store.dispatch('Getautostatus', this.autostatus)
                    }
                    if (item.functionName === 'Site-Authorization') {
                        this.sitestatus = item.status === 1
                        this.$store.dispatch('Getsitestatus', this.sitestatus)
                    }
                })
            })
        },
        // 确定改变状态
        okAction() {
            updateSwitch(this.paramsData).then(res => {
                this.init()
            }).catch(res => {
                this.cancelAction()
            })
            this.switchVisible = false
        },
        cancelAction() {
            this.switchVisible = false
            if (this.typedialog === 'auto') {
                this.autostatus = !this.autostatus
            }
            if (this.typedialog === 'site') {
                this.sitestatus = !this.sitestatus
            }
        },
        toswitch(type) {
            this.typedialog = type
            this.status = ''
            this.switchVisible = true // 弹框
            if (type === 'auto') {
                this.paramsData = {
                    functionId: 1
                }
                if (this.autostatus) {
                    this.paramsData.status = 1
                    this.status = 'on'
                    this.statusdeploy = 'opening'
                    this.itmetext = 'Auto-deploy'
                    this.itmeline = 'automatic deployment and upgrade will'
                } else {
                    this.paramsData.status = 2
                    this.status = 'off'
                    this.itmetext = 'Auto-deploy'
                    this.statusdeploy = 'shutdown'
                    this.itmeline = 'automatic deployment and upgrade will not'
                }
            } else if (type === 'site') {
                this.paramsData = {
                    functionId: 2
                }
                if (this.sitestatus) {
                    this.paramsData.status = 1
                    this.status = 'on'
                    this.statusdeploy = 'opening'
                    this.itmetext = 'Site-Authorization'
                    this.itmeline = 'Site-Authorization will'
                } else {
                    this.paramsData.status = 2
                    this.status = 'off'
                    this.statusdeploy = 'shutdown'
                    this.itmetext = 'Site-Authorization'
                    this.itmeline = 'Site-Authorization will not'
                }
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/switch.scss';
</style>
