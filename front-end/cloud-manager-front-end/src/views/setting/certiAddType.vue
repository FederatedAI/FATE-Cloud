<template>
  <div>
    <el-dialog :visible.sync="addTypeDialog" :close-on-click-modal="false" :close-on-press-escape="false" class="certi-add-type" width="420px">
      <div class="add-dialog-head">
          <el-button class="addUrl" :disabled="addDisabled" @click="addentrancesSelect" type="text">
              <i class="el-icon-circle-plus"></i>
                <span>{{$t('m.common.add')}}</span>
          </el-button>
      </div>
      <div class="add-dialog-body">
            <div class="line-box">
                <!-- <div class="line" @dblclick="getEdit(index)" v-for="(item, index) in entrancesSelect" :key="index"> -->
                <div class="line"  v-for="(item, index) in entrancesSelect" :key="index">
                    <span v-if="!item.show">
                        <span class="network-text">
                            <tooltip :width="'295px'" style="vertical-align: top;display: inline-block;" :content="item.typeName" :placement="'top'"/>
                            <!-- {{item.typeName}} -->
                        </span>
                        <span v-if="item.canDelete===1" >
                            <i  class="el-icon-edit check disable"></i>
                            <i  class="el-icon-close del disable"></i>
                        </span>
                        <span v-else>
                            <i @click="getEdit(index)" class="el-icon-edit check"></i>
                            <i v-if="item.canDelete===2" @click="toShowMes" class="el-icon-close del"></i>
                            <i v-else  @click="deleteEntrances(index,item.id)"  class="el-icon-close del"></i>
                        </span>

                    </span>
                    <el-input v-if="item.show"  autocomplete="off" class="input-show" id="close"  @keyup.enter.native="closeEntrances" v-model="entrancesInput" @blur="closeEntrances(index)"  :placeholder="$t('m.certificate.enterSelfCertificateType')" >
                        <i slot="suffix" @click="closeEntrances(index)" style="right: 35px;" class="el-icon-check check" />
                        <i slot="suffix" @click="closeInput(index)" @mousedown="mouseDown" style="right: 0px;" class="el-icon-close del" />
                    </el-input>
                </div>
            </div>
            <div class="message-add" v-if="showMesDialog" :style="{ opacity: showMes,transition:'all 1s ease' }">
                <span class="tips" >
                    <img src="@/assets/failed.png">
                    <span>{{$t('m.certificate.usedByCertificate')}}</span>
                </span>
            </div>
            <div class="message-add" v-if="nameMesDialog" :style="{ opacity: showMes,transition:'all 1s ease' }">
                <span class="tips" >
                    <img src="@/assets/failed.png">
                    <span>{{$t('m.certificate.certificateAlreadyExists')}}</span>
                </span>
            </div>
            <div class="message-add" v-if="maximumDialog" :style="{ opacity: showMes,transition:'all 1s ease' }">
                <span class="tips" >
                    <img src="@/assets/failed.png">
                    <span>{{$t('m.siteAdd.maximum20chatacters')}}</span>
                </span>
            </div>
      </div>
      <div class="add-dialog-footer">
        <el-button class="ok-btn" :disabled="saveDisabled" type="primary" @click="saveAction">{{$t('m.common.save')}}</el-button>
        <el-button class="ok-btn" type="info"  @click="cancelAction" >{{$t('m.common.cancel')}}</el-button>
      </div>
    </el-dialog>
</div>
</template>

<script>
import { updateCertifiType } from '@/api/setting'
import tooltip from '@/components/Tooltip'
export default {
    name: 'siteAddIp',
    components: { tooltip },
    data() {
        return {
            addTypeDialog: false, // 添加新typeName弹框
            entrancesInput: '', // 弹框增加typeName临时数据
            showMes: 0.85, // 是否模态弹框
            showMesDialog: false, // 不能删除弹框
            nameMesDialog: false, // 重复弹框
            maximumDialog: false, // 最大不能超过20个字符
            timeLess: null, // 定时器
            // 新加typeName数组
            entrancesSelect: [
                // { typeName: '12', show: false },
                // { typeName: '34', show: false },
            ],
            saveDisabled: false, // 是否可点击保存
            addDisabled: false, // 是否可点击添加
            canEdit: true, // 是否可双击编辑
            params: []
        }
    },
    watch: {
        entrancesSelect: {
            handler: function(val) {
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
    methods: {

        // 增加行
        addentrancesSelect() {
            this.entrancesSelect.push({ typeName: '', show: false, checked: false })
            this.canEdit = true // 可编辑
            this.getEdit(this.entrancesSelect.length - 1)
        },
        // 删除typeName
        deleteEntrances(index, deleteID) {
            this.entrancesInput = ''
            this.entrancesSelect.splice(index, 1)
            this.saveDisabled = false // 可点击保存
            this.addDisabled = false // 可点击添加
            this.canEdit = true // 可编辑
            if (deleteID) {
                this.params.push({ id: deleteID })
            }
        },

        // 点击编辑
        getEdit(index) {
            if (this.canEdit) {
                this.entrancesSelect[index].show = true
                this.tempInputClose = this.entrancesInput = this.entrancesSelect[index].typeName
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
            // 最大不能输入超过20个字符
            if (this.entrancesInput.length > 20) {
                setTimeout(() => {
                    this.maximumDialog = true
                }, 500)

                setTimeout(() => {
                    this.maximumDialog = false
                }, 1500)
                return
            }
            // 输入重复弹框
            for (let i = 0; i < this.entrancesSelect.length; i++) {
                if (this.entrancesSelect[i].typeName === this.entrancesInput && index !== i) {
                    setTimeout(() => {
                        this.nameMesDialog = true
                    }, 500)

                    setTimeout(() => {
                        this.nameMesDialog = false
                    }, 1500)
                    return
                }
            }
            if (this.entrancesInput === '') {
                this.deleteEntrances(index)
            } else {
                this.entrancesSelect[this.entrancesindex].show = false
                this.entrancesSelect[this.entrancesindex].typeName = this.entrancesInput.trim()
                this.saveDisabled = false // 可点击保存
                this.addDisabled = false // 可点击添加
                this.canEdit = true // 可双击编辑
            }
        },
        closeInput(index) {
            this.entrancesInput = this.tempInputClose
            this.closeEntrances(index)
        },
        // 确定保存
        saveAction() {
            [...new Set(this.entrancesSelect)].forEach(item => {
                this.params.push(item)
            })
            updateCertifiType(this.params).then(res => {
                this.$parent.initList()
                this.$parent.init()
                this.addTypeDialog = false
            })
        },
        // 阻止失去焦点事件触发
        mouseDown(event) {
            event.preventDefault()
        },
        // 关闭弹框
        cancelAction() {
            this.$parent.initList()
            this.$parent.init()
            this.saveDisabled = false// 可点击保存
            this.addDisabled = false // 可点击添加
            this.addTypeDialog = false // 关闭弹框
        },
        // 显示不能删除
        toShowMes() {
            setTimeout(() => {
                this.showMesDialog = true
            }, 500)

            setTimeout(() => {
                this.showMesDialog = false
            }, 1500)
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/certiAddType.scss';
</style>
