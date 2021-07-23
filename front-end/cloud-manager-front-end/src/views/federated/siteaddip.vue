<template>
  <div>
    <el-dialog :visible.sync="adddialog" :close-on-click-modal="false" :close-on-press-escape="false" class="add-dialog" width="520px">
      <div class="add-dialog-head">
          <el-button class="addUrl" :disabled="addDisabled" @click="addentrancesSelect" type="text">
              <i class="el-icon-circle-plus"></i>
                <span>{{$t('m.common.add')}}</span>
          </el-button>
          <!-- <el-button class="deleteAll"  type="text" @click="deleteAll" >
              <i class="el-icon-delete-solid" ></i>
                <span>{{$t('m.common.delete')}}</span>
          </el-button> -->
        <span class="total">{{$t('m.common.total')}}:{{addtotal}}</span>
      </div>
      <div class="add-dialog-body">
            <div class="line-box">
                <div class="line" @dblclick="getEdit(index)" v-for="(item, index) in entrancesSelect" :key="index">
                    <span v-if="!item.show">
                        <!-- <el-checkbox v-model="item.checked"></el-checkbox> -->
                        <span class="network-text">{{item.ip}}</span>
                        <span v-if="networkacesstype==='entrances'" @click="testTelent(index)" class="telent">{{$t('m.siteAdd.telnet')}}</span>
                        <i @click="deleteEntrances(index)" class="el-icon-close del"></i>
                    </span>
                    <el-input v-if="item.show"  autocomplete="off" class="input-show" id="close" v-model="entrancesInput" @blur="closeEntrances(index)" :placeholder="`${$t('m.siteAdd.typeLike')}: 127.0.0.1:8080` " >
                        <i slot="suffix" @blur="closeEntrances(index)"  class="el-icon-check check" />
                        <i slot="suffix" @click="deleteEntrances(index)" @mousedown="mouseDown" style="right: 2px;" class="el-icon-close del" />
                    </el-input>
                </div>
            </div>
            <div class="message-add" :style="{ opacity: showMes,transition:'all 0.2s ease' }">
                <span v-if="telnetsuccess" class="tips" >
                    <img src="@/assets/success.png">
                    <span>{{$t('m.siteAdd.telnetSuccess')}}!
                        <span style="color:#4AA2FF">{{ipPost}}</span>
                    </span>
                </span>
                <span v-if="invalidsuccess" class="tips" >
                    <img src="@/assets/failed.png">
                    <span>{{$t('m.siteAdd.invalidInput')}}!</span>
                </span>
                <span v-if="telnetfail" class="tips" >
                    <img src="@/assets/failed.png">
                    <span>{{$t('m.siteAdd.unableConnect')}}!
                        <span style="color:#4AA2FF">{{ipPost}}</span>
                    </span>
                </span>
            </div>
      </div>
      <div class="add-dialog-footer">
        <el-button class="save-btn" :disabled="saveDisabled" type="primary" @click="saveAction">{{$t('m.common.save')}}</el-button>
        <el-button class="cancel-btn" type="info"  @click="cancelAction" >{{$t('m.common.cancel')}}</el-button>
        <!-- <span  @mousedown="mouseDownAA"> Cancel</span> -->
      </div>
    </el-dialog>
</div>
</template>

<script>
import { telnet } from '@/api/federated'

export default {
    name: 'siteAddIp',
    components: {},
    data() {
        return {
            adddialog: false, // 添加新ip弹框
            entrancesInput: '', // 弹框增加ip临时数据
            networkacesstype: '', // 弹框类型
            showMes: 0, // 是否模态弹框
            timeLess: null, // 定时器
            // 新加ip数组
            entrancesSelect: [],
            addtotal: '', //
            telnetsuccess: false, // 测试成功后提示
            telnetfail: false, // 测试成功后提示
            invalidsuccess: false, // 无效输入
            saveDisabled: false, // 是否可点击保存
            addDisabled: false, // 是否可点击添加
            canEdit: true, // 是否可双击编辑
            ipPost: '',
            verifyPromise: null
        }
    },
    watch: {
        entrancesSelect: {
            handler: function(val) {
                this.addtotal = val.length
                if (val.length >= 20) {
                    this.addDisabled = true
                } else if (!this.saveDisabled) {
                    this.addDisabled = false
                }
                if (val.length >= 20) {
                    this.saveDisabled = true
                } else {
                    this.saveDisabled = false
                }
            },
            deep: true,
            immediate: true
        }
    },
    created() {
    },
    methods: {

        // 增加行
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
        // 完成校验
        closeEntrances(index) {
            let RegExpVal = this.entrancesInput.split(':')
            // ip正则校验
            let ipReg = new RegExp(/^(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])$/)
            // 域名正则校验
            // let domainReg = new RegExp(/[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/)
            // 端口正则校验
            let portReg = new RegExp(/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/)
            if (this.entrancesInput === '') {
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
        // 确定保存
        saveAction() {
            let arr = [...new Set(this.entrancesSelect.map(item => `${item.ip};`))]
            if (this.networkacesstype === 'entrances') {
                // networkAccessEntrances赋值
                this.$parent.form.networkAccessEntrances = arr.join('')
                // 单独验证entrances父级表单
                this.$parent.$refs['infoform'].validateField('networkAccessEntrances', valid => {})
                this.entrancesSelect = []
            } else if (this.networkacesstype === 'exit') {
                // networkAccessExits赋值
                this.$parent.form.networkAccessExits = arr.join('')
                // 单独验证Exits父级表单
                this.$parent.$refs['infoform'].validateField('networkAccessExits', valid => {})
                this.entrancesSelect = []
            }
            this.adddialog = false
        },
        // 阻止失去焦点事件触发
        mouseDown(event) {
            event.preventDefault()
        },
        // async mouseDownAA(event) {
        //     let aa = true
        //     aa = await event.preventDefault()
        //     if (!aa) {
        //         console.log('===>>')
        //         this.cancelAction()
        //     }
        // },
        // 关闭弹框
        cancelAction() {
            this.showMes = 0 // 透明度为0
            this.entrancesSelect = []// 清理数据
            this.saveDisabled = false// 可点击保存
            this.addDisabled = false // 可点击添加
            this.adddialog = false // 关闭弹框
        },
        // 测试telnet
        testTelent(index) {
            let data = {
                ip: this.entrancesSelect[index].ip.split(':')[0],
                port: parseInt(this.entrancesSelect[index].ip.split(':')[1])
            }
            this.ipPost = this.entrancesSelect[index].ip
            telnet(data).then(res => {
                if ((res.code === 0)) {
                    this.telnetsuccess = true
                    this.showMes = 0.8
                    setTimeout(() => {
                        this.telnetsuccess = false
                        this.showMes = 0
                    }, 2000)
                }
            }).catch(res => {
                if (res.code === 109) {
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
@import 'src/styles/siteaddip.scss';
</style>
