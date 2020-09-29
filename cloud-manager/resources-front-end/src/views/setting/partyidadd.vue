<template>
  <div class="partyid-add">
    <el-dialog
      class="add-groud"
      :title="title"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :before-close="cancelAction"
    >
      <el-form
        ref="groudform"
        :model="partidform"
        :rules="rules"
        label-position="left"
        label-width="128px"
      >
        <el-form-item label="Name" prop="groupName">
          <el-input :class="{ 'edit-text': true, 'groupNamewarn': groupNamewarn }"
          @blur="toCheckGroupName"
          @focus="cancelValid('groupName')"
          v-model.trim="partidform.groupName">
          </el-input>
        </el-form-item>
        <el-form-item label="Type" prop="role">
          <el-select
            v-if="title!=='Edit'"
            class="edit-text select"
            :popper-append-to-body="false"
            v-model="partidform.role"
            placeholder="Type"
          >
            <el-option
              v-for="item in roleSelect"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
          <span v-if="title ==='Edit'" class="info-text" style="color:#B8BFCC">{{partidform.role===1?'Guest':'Host'}}</span>
        </el-form-item>
        <el-form-item label="ID range" prop="rangeInfo">
          <span slot="label">
            <span>
              <span>ID range</span>
              <el-tooltip effect="dark" placement="bottom" >
                <div style="font-size:14px;" slot="content">
                    <span v-if="title !=='Edit'" >
                        <div>PartyID's coding rules are limited to natural Numbers only,</div>
                        <div>and a natural number uniquely represents a party.</div>
                    </span>
                    <span v-if="title ==='Edit'" >
                        <div>Support for adding new rules or modifying unused rules</div>
                        <div>in addition to the used coding rules.</div>
                    </span>
                </div>
                <i class="el-icon-info range-info"></i>
              </el-tooltip>
            </span>
          </span>
          <!-- <span class="info-text text" v-if="title==='Edit'" style="color:#B8BFCC">{{form.rangeInfo}}</span> -->
          <el-input
            type="textarea"
            class="textarea"
            @blur="toReset"
            @focus="cancelValid('rangeInfo')"
            placeholder="Enter an ID range like:10000~19999;11111"
            v-model.trim="partidform.rangeInfo"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button type="primary" style="margin-right:14px" :disabled="submitbtn" @click="submitAction">Submit</el-button>
        <el-button type="info" @click="cancelAction">Cancel</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { partyidUpdate, partyidAdd, checkRange, checkUpdateRange, checkGroupName } from '@/api/setting'
import { requestRange } from '@/utils/idRangeRule'

export default {
    name: 'partyadd',
    components: {},
    props: {
        title: {
            type: String,
            default: function() {
                return ''
            }
        }
        // partidform: {
        //     type: Object,
        //     default: function() {
        //         return {}
        //     }
        // }
    },

    data() {
        return {
            submitbtn: true, // 按钮置灰
            dialogVisible: false,
            checkPass: false, // 检查通过
            groupNamewarn: false,
            beenUsed: '', // 已经有使用范围
            cannotEdit: null,
            partidform: {},
            roleSelect: [
                {
                    value: 1,
                    label: 'Guest'
                },
                {
                    value: 2,
                    label: 'Host'
                }
            ],
            rules: {
                groupName: [{
                    required: true,
                    trigger: 'blur',
                    validator: (rule, value, callback) => {
                        if (!value) {
                            this.groupNamewarn = true
                            callback(new Error('Name field is required.'))
                        } else if (this.groupNamewarn) {
                            this.groupNamewarn = true
                            callback(new Error('Group name exist! '))
                        } else {
                            callback()
                        }
                    } }],
                role: [{ required: true, trigger: 'change', message: 'Type field is required.' }],
                rangeInfo: [
                    {
                        required: true,
                        trigger: 'blur',
                        validator: (rule, value, callback) => {
                            if (value) {
                                // 去空格
                                value = value.replace(/\s+/g, '')
                            } else {
                                callback(new Error('ID range field is required.'))
                                return
                            }
                            // 匹配中文
                            let chinese = new RegExp(/[\u4E00-\u9FA5]/g)
                            // 特殊符号
                            let pattern = new RegExp("[`!@#$^&*()=|{}':',\\[\\].<>/?！@#￥……&*（）——|{}【】‘：”“'。，、？]")
                            // 字母
                            let letter = new RegExp(/[a-zA-Z]+/)
                            // 其他不合法规则3999~3000;1000-；1000~； ~2000
                            let rangeArr = value.split(/;+；+|；+;+|;+|；+/).sort(sortRange).filter(item => item)
                            let result = rangeArr.every((r, index) => {
                                let curItem = r.split(/~|-/).map(item => parseInt(item))
                                let lastItem
                                if (index > 0) {
                                    lastItem = rangeArr[index - 1].split(/~|-/).map(item => parseInt(item))
                                    if (curItem[0] < (lastItem[1] ? lastItem[1] : lastItem[0])) {
                                        return false
                                    }
                                }
                                if (curItem.length === 2) {
                                    if (curItem[0] < curItem[1] && curItem[0] && curItem[1]) {
                                        return true
                                    }
                                } else if (curItem.length === 1 && curItem[0]) {
                                    return true
                                } else {
                                    return false
                                }
                            })
                            function sortRange(a, b) {
                                let aprev = a.split(/~|-/)[0]
                                let bprev = b.split(/~|-/)[0]
                                return aprev - bprev
                            }
                            if (!value) {
                                callback(new Error('ID range field is required.'))
                            } else if (pattern.test(value) || letter.test(value) || chinese.test(value) || !result) {
                                callback(new Error('Invalid input.'))
                            } else if (this.cannotEdit.intervalWithPartyIds.length > 0 || this.cannotEdit.sets.length > 0) {
                                let str = ''
                                this.cannotEdit.forEach(item => {
                                    if (item.region.leftRegion === item.region.rightRegion) {
                                        str += `"${item.region.leftRegion}" cannot be edited,in which ${item.usedPartyIds}. it have been assigned to the sites. `
                                    } else {
                                        str += `"${item.region.leftRegion}-${item.region.rightRegion}" cannot be edited,in which ${item.usedPartyIds}. etc have been assigned to the sites.`
                                    }
                                })
                                callback(new Error(str))
                            } else if (this.beenUsed) {
                                callback(new Error(`${this.beenUsed.leftRegion}-${this.beenUsed.rightRegion} ID range have been used, please edit again!`))
                            } else {
                                callback()
                            }
                        }
                    }
                ]
            }
        }
    },
    watch: {
        partidform: {
            handler(val) {
                if (val.rangeInfo && val.groupName && val.role) {
                    this.submitbtn = false
                } else {
                    this.submitbtn = true
                }
            },
            immediate: true,
            deep: true
        }
    },
    computed: {},
    methods: {
        // 检验GroupName是否重复
        toCheckGroupName() {
            let data = {
                groupName: this.partidform.groupName,
                groupId: this.partidform.groupId
            }
            checkGroupName(data).then(res => {
                this.groupNamewarn = false
                this.$refs['groudform'].validateField('groupName', valid => {})
            }).catch(res => {
                this.groupNamewarn = true
                this.$refs['groudform'].validateField('groupName', valid => {})
            })
        },
        // 提交
        submitAction() {
            this.$refs['groudform'].validate(valid => {
                if (valid) {
                    let data = { ...this.partidform }
                    // data.rangeInfo = requestRange(this.partidform.rangeInfo)
                    data.regions = requestRange(this.partidform.rangeInfo)
                    if (this.title === 'Edit') {
                        // 编辑检查partyid范围checkUpdateRange
                        checkUpdateRange(data).then(res => {
                            partyidUpdate(data).then(res => {
                                this.dialogVisible = false
                                this.$parent.initList()// 刷新列表
                                this.$message({
                                    type: 'success',
                                    message: 'success!',
                                    duration: 3000
                                })
                                this.$refs['groudform'].resetFields()// 取消表单验证
                            }).catch(res => {
                                // {leftRegion: 40, rightRegion: 45}
                                this.beenUsed = res.data
                                this.$refs['groudform'].validateField('rangeInfo', valid => {})
                            })
                        }).catch(res => {
                            this.cannotEdit = [...res.data]
                            this.$refs['groudform'].validateField('rangeInfo', valid => {})
                        })
                    } else {
                        // 添加检查partyid范围checkRange
                        checkRange(data).then(res => {
                            partyidAdd(data).then(res => {
                                this.dialogVisible = false
                                this.$parent.initList()// 刷新列表
                                this.$message({
                                    type: 'success',
                                    message: 'success!',
                                    duration: 3000
                                })
                                this.$refs['groudform'].resetFields()// 取消表单验证
                            })
                        }).catch(res => {
                            this.beenUsed = res.data
                            this.$refs['groudform'].validateField('rangeInfo', valid => {})
                            // console.log('Addres==>>', res)
                        })
                    }
                }
            })
        },
        // 取消
        cancelAction() {
            this.dialogVisible = false
            this.$refs['groudform'].resetFields()
        },
        // 重置
        toReset() {
            this.cannotEdit = null
            this.beenUsed = ''
        },
        // 取消表单验证
        cancelValid(validtype) {
            this.$refs['groudform'].clearValidate(validtype)
            this[`${validtype}warn`] = false
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/partyidadd.scss';
</style>
