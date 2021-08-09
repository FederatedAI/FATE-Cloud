<template>
  <div class="add-ip-box">
    <el-dialog :visible.sync="adddialog" :close-on-click-modal="false" :close-on-press-escape="false" class="add-dialog" width="520px" >
      <div class="add-dialog-head">
        <el-button class="addUrl" :disabled="addDisabled" @click="addentrancesSelect" type="text">
          <i class="el-icon-circle-plus"></i>
          <span>{{$t('m.common.add')}}</span>
        </el-button>
        <el-button class="deleteAll" type="text" @click="deleteAll">
          <i class="el-icon-delete-solid"></i>
          <span>{{$t('m.common.delete')}}</span>
        </el-button>
        <span class="total">{{$t('m.common.total')}}:{{addtotal}}</span>
      </div>
      <div class="add-dialog-body">
        <div class="line-box">
          <div class="line"  @dblclick="getEdit(index)" v-for="(item, index) in entrancesSelect" :key="index" >
            <span v-if="!item.show">
              <el-checkbox v-model="item.checked"></el-checkbox>
              <span class="network-text">{{item.ip}}</span>
              <span v-if="networkacesstype==='entrances'" @click="testTelent(index)" class="telent" >{{$t('m.sitemanage.telnet')}}</span>
              <i @click="deleteEntrances(index)" class="el-icon-close del"></i>
            </span>
            <el-input v-if="item.show" autocomplete="off" class="input-show" id="close" v-model="entrancesInput" @blur="closeEntrances(index)"
            :placeholder="`${$t('m.sitemanage.typeLike',{type:networkacesstype==='entrances' ? $t('m.sitemanage.entrances') : $t('m.sitemanage.exit')})}: 127.0.0.1:8080`" >
              <i slot="suffix" @click="closeEntrances(index)" class="el-icon-check check" />
              <i slot="suffix" @click="deleteEntrances(index)" @mousedown="mouseDown" style="right: 2px;" class="el-icon-close del" />
            </el-input>
          </div>
        </div>
        <div class="message-add" :style="{ opacity: showMes,transition:'all .5s ease' }">
          <span v-if="telnetsuccess" class="tips">
            <img src="@/assets/success.png" />
            <span>
              {{$t('m.sitemanage.telnetSuccess')}}
              <span style="color:#4AA2FF">{{ipPost}}</span>
            </span>
          </span>
          <span v-if="invalidsuccess" class="tips">
            <img src="@/assets/failed.png" />
            <span>{{$t('m.common.invalidInput')}}</span>
          </span>
          <span v-if="telnetfail" class="tips">
            <img src="@/assets/failed.png" />
            <span>
              {{$t('m.sitemanage.unableConnect')}}
              <span style="color:#4AA2FF">{{ipPost}}</span>
            </span>
          </span>
        </div>
      </div>

      <div class="add-dialog-footer">
        <el-button :disabled="saveDisabled" type="primary" @click="saveAction">{{$t('m.common.save')}}</el-button>
        <el-button type="primary" @click="cancelAction">{{$t('m.common.cancel')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { telnet } from '@/api/home'

export default {
    name: 'sitedetailip',

    data() {
        return {
            adddialog: false, // 是否显示弹框
            entrancesInput: '', // 弹框增加ip
            networkacesstype: '', // 添加ip弹框类型
            showMes: 0, // 是否模态弹框
            addtotal: '', // 添加数量
            telnetsuccess: false, // 测试成功后提示
            telnetfail: false, // 测试成功后提示
            invalidsuccess: false, // 无效输入
            saveDisabled: false, // 是否可点击保存
            addDisabled: false, // 是否可点击添加
            canEdit: true, // 是否可双击编辑
            entrancesSelect: [
                // { ip: '192.168.8.1:8081;', show: false },
                // { ip: '192.168.8.1:8082;', show: false },
                // { ip: '192.168.8.1:8083;', show: false }
            ],
            form: {},
            ipPost: ''
        }
    },
    watch: {
        entrancesSelect: {
            handler: function(val) {
                console.log(val, 'val')
                this.addtotal = this.entrancesSelect.length
                if (val.length >= 20) {
                    this.addDisabled = true
                } else if (!this.saveDisabled) {
                    this.addDisabled = false
                }
            },
            deep: true,
            immediate: true
        }
    },
    computed: {},
    created() {},

    methods: {
        // 增加行ip
        addentrancesSelect() {
            this.entrancesSelect.push({ ip: '', show: false, checked: false })
            this.canEdit = true // 可编辑
            this.getEdit(this.entrancesSelect.length - 1)
        },
        // 删除ip
        deleteEntrances(index) {
            this.entrancesInput = ''
            this.entrancesSelect.splice(index, 1)
            this.saveDisabled = false // 可点击保存
            this.addDisabled = false // 可点击添加
            this.canEdit = true // 可编辑
        },
        // 删除所有ip
        deleteAll() {
            this.entrancesSelect = this.entrancesSelect.filter(item => !item.checked)
        },
        // 双击
        getEdit(index) {
            if (this.canEdit) {
                this.entrancesSelect[index].show = true
                this.entrancesInput = this.entrancesSelect[index].ip
                this.entrancesindex = index
                this.canEdit = false // 不可编辑（双击无反应）
                this.addDisabled = true // 不可点击添加
                this.saveDisabled = true // 不可点击保存
                // 锁定焦点
                this.$nextTick(() => {
                    let onFocus = document.querySelector('#close')
                    onFocus.focus()
                })
            }
        },
        // 关闭
        closeEntrances(index) {
            let RegExpVal = this.entrancesInput.split(':')
            // ip正则校验
            let ipReg = new RegExp(
                /^(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])$/
            )
            // 域名正则校验
            // let domainReg = new RegExp(/[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/)
            // 端口正则校验
            let portReg = new RegExp(
                /^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/
            )
            // 校验
            if (!this.entrancesInput) {
                this.deleteEntrances(index)
            } else {
                if (portReg.test(RegExpVal[1]) && ipReg.test(RegExpVal[0])) {
                    this.entrancesSelect[this.entrancesindex].show = false
                    this.entrancesSelect[this.entrancesindex].ip = this.entrancesInput
                    this.saveDisabled = false // 可点击保存
                    this.addDisabled = false // 可点击添加
                    this.canEdit = true // 可双击编辑
                } else {
                    this.saveDisabled = true // 不可点击保存
                    this.addDisabled = true // 不可点击添加
                    this.invalidsuccess = true
                    this.showMes = 0.8
                    setTimeout(() => {
                        this.showMes = 0
                        this.invalidsuccess = false
                    }, 2000)
                }
            }
        },
        // 阻止失去焦点事件触发
        mouseDown(event) {
            event.preventDefault()
        },
        // 确定保存
        saveAction() {
            let arr = [...new Set(this.entrancesSelect.map(item => `${item.ip};`))]
            let editType = {
                'entrances': 'networkAccessEntrances',
                'exit': 'networkAccessExits',
                'rollsite': 'rollsiteNetworkAccess'
            }
            let parameterName = editType[this.networkacesstype]
            if (parameterName) {
                let paramData = {
                    name: parameterName,
                    data: arr.join('')
                }
                this.$emit('updateIp', paramData)
                this.$parent.$refs['form'].validateField(`${parameterName}`, valid => {})
                this.entrancesSelect = []
            }
            this.adddialog = false
            this.$parent.shouldtosubmit(parameterName)
        },
        // 关闭弹框
        cancelAction() {
            let editType = {
                'entrances': 'networkAccessEntrances',
                'exit': 'networkAccessExits',
                'rollsite': 'rollsiteNetworkAccess'
            }
            let parameterName = editType[this.networkacesstype]
            this.entrancesSelect = []
            this.adddialog = false
            this.$parent.shouldtosubmit(parameterName)
            this.saveDisabled = false// 可点击保存
            this.addDisabled = false // 可点击添加
            this.showMes = 0 // 透明度为0
        },
        // 测试telnet
        testTelent(index) {
            let data = {
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId),
                ip: this.entrancesSelect[index].ip.split(':')[0],
                port: this.entrancesSelect[index].ip.split(':')[1]
            }
            this.ipPost = this.entrancesSelect[index].ip
            telnet(data)
                .then(res => {
                    if (res.code === 0) {
                        this.telnetsuccess = true
                        this.showMes = 0.8
                        setTimeout(() => {
                            this.telnetsuccess = false
                            this.showMes = 0
                        }, 2000)
                    }
                })
                .catch(res => {
                    if (res.code === 10015) {
                        this.telnetfail = true
                        this.showMes = 0.8
                        setTimeout(() => {
                            this.telnetfail = false
                            this.showMes = 0
                        }, 2000)
                    }
                })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/sitedetailip.scss';
</style>
