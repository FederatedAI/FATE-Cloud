<template>
  <div class="partyid-add">
    <el-dialog
      class="add-groud"
      :title="title!=='Edit'?$t('Add a new ID Group'):$t('Edit')"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="true"
      :before-close="cancelAction"
    >
      <el-form
        ref="groudform"
        :model="partidform"
        :rules="rules"
        label-position="top"
        label-width="128px"
      >
        <el-form-item :label="$t('Name')+'：'" prop="groupName">
          <el-input :class="{ 'edit-text': true, 'groupNamewarn': groupNamewarn }"
          @blur="toCheckGroupName"
          @focus="cancelValid('groupName')"
          v-model.trim="partidform.groupName">
          </el-input>
        </el-form-item>
        <el-form-item  :label="$t('Type')+'：'"  prop="role">
          <el-select
            v-if="title!=='Edit'"
            class="edit-text select"
            :popper-append-to-body="false"
            v-model="partidform.role"
            :placeholder="$t('Type')"
          >
            <el-option
              v-for="item in roleSelect"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
          <span v-if="title ==='Edit'" class="info-text" style="color:#B8BFCC">{{partidform.role===1? $t('m.common.guest') : $t('m.common.host')}}</span>
        </el-form-item>
        <el-form-item label="ID range" prop="rangeInfo">
          <span slot="label">
            <span>
              <span>{{$t('ID range')}}</span>
              <el-tooltip effect="dark" placement="bottom" >
                <div style="font-size:14px;" slot="content">
                    <span v-if="title !=='Edit'" >
                        <div>{{$t("PartyID's coding rules are limited to natural Numbers only,")}}</div>
                        <div>{{$t("and a natural number uniquely represents a party.")}}</div>
                    </span>
                    <span v-if="title ==='Edit'" >
                        <div>{{$t("Support for adding new rules or modifying unused rules")}}</div>
                        <div>{{$t("additionToUsedCodingRules")}}</div>
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
            :placeholder="$t('m.partyId.enterIDRangeLike')+':10000~19999;11111'"
            v-model.trim="partidform.rangeInfo"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" style="margin-right:14px" :disabled="submitbtn" @click="submitAction">{{$t('m.common.submit')}}</el-button>
        <el-button class="ok-btn" type="info" @click="cancelAction">{{$t('m.common.cancel')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { partyidUpdate, partyidAdd, checkRange, checkUpdateRange, checkGroupName } from '@/api/setting'
import { requestRange } from '@/utils/idRangeRule'
// 国际化
const local = {
    zh: {
        'Add a new ID Group': '添加新ID组',
        'Edit': '编辑ID组',
        'Name': '分组名',
        'Type': 'ID类型',
        'ID range': 'ID范围',
        "PartyID's coding rules are limited to natural Numbers only,": '站点ID的编码规则仅限于自然数，',
        'and a natural number uniquely represents a party.': '且每个ID唯一对应一个站点。',
        'ID range field is required': 'ID范围字段为必填项',
        'Support for adding new rules or modifying unused rules': '支持添加新规则或修改未使用的规则',
        'additionToUsedCodingRules': '除了使用的编码规则。'

    },
    en: {
        'Add a new ID Group': 'Add a new ID Group',
        'Edit': 'Edit',
        'Name': 'Name',
        'Type': 'Type',
        'ID range': 'ID range',
        "PartyID's coding rules are limited to natural Numbers only,": "PartyID's coding rules are limited to natural Numbers only,",
        'and a natural number uniquely represents a party.': 'and a natural number uniquely represents a party.',
        'ID range field is required': 'ID range field is required',
        'Support for adding new rules or modifying unused rules': 'Support for adding new rules or modifying unused rules',
        'additionToUsedCodingRules': 'in addition to the used coding rules.'
    }
}

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
            cannotEdit: '',
            partidform: {},
            roleSelect: [
                {
                    value: 1,
                    label: this.$t('m.common.guest')
                },
                {
                    value: 2,
                    label: this.$t('m.common.host')
                }
            ],
            rules: {
                groupName: [{
                    required: true,
                    trigger: 'blur',
                    validator: (rule, value, callback) => {
                        if (!value) {
                            this.groupNamewarn = true
                            callback(new Error(this.$t('m.common.requiredfieldWithType', { type: this.$t('Name') })))
                        } else if (this.groupNamewarn) {
                            this.groupNamewarn = true
                            callback(new Error(this.$t('m.partyId.groupNameExist')))
                        } else {
                            callback()
                        }
                    } }],
                role: [{ required: true, trigger: 'change', message: this.$t('m.common.requiredfieldWithType', { type: this.$t('Type') }) }],
                rangeInfo: [
                    {
                        required: true,
                        trigger: 'blur',
                        validator: (rule, value, callback) => {
                            if (value) {
                                // 去空格
                                value = value.replace(/\s+/g, '')
                            } else {
                                callback(new Error(this.$t('ID range field is required')))
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
                                callback(new Error(''))
                            } else if (pattern.test(value) || letter.test(value) || chinese.test(value) || !result) {
                                callback(new Error(this.$t('m.common.invalidInput')))
                            // } else if (this.cannotEdit.intervalWithPartyIds.length > 0 || this.cannotEdit.sets.length > 0) {
                            } else if (this.cannotEdit.length > 0 && this.cannotEdit[0].region && this.cannotEdit[0].usedPartyIds) {
                                let str = ''
                                console.log(this.cannotEdit, 'this.cannotEdit')
                                this.cannotEdit.forEach(item => {
                                    if (item.region.leftRegion === item.region.rightRegion) {
                                        str += `"${item.region.leftRegion}" ${this.$t('m.partyId.cannotBeEdited')} ${item.usedPartyIds}. ${this.$t('m.partyId.itBeenAssigned')}`
                                    } else {
                                        str += `"${item.region.leftRegion}-${item.region.rightRegion}" ${this.$t('m.partyId.cannotBeEdited')} ${item.usedPartyIds}. ${this.$t('m.partyId.etcBeenAssigned')}`
                                    }
                                })
                                callback(new Error(str))
                            } else if (this.beenUsed) {
                                callback(new Error(`${this.beenUsed.leftRegion}-${this.beenUsed.rightRegion} ${this.$t('m.partyId.beenUsed')}`))
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
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
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
                                this.$message.success(this.$t('m.common.success'))
                                this.$refs['groudform'].resetFields()// 取消表单验证
                            }).catch(res => {
                                console.log(res, 'edit-res')
                                // {leftRegion: 40, rightRegion: 45}
                                this.beenUsed = res.data
                                this.$refs['groudform'].validateField('rangeInfo', valid => {})
                            })
                        }).catch(res => {
                            console.log(res, 'res')
                            this.cannotEdit = [...res.data]
                            console.log(this.cannotEdit, 'cannotEdit')
                            this.$refs['groudform'].validateField('rangeInfo', valid => {})
                        })
                    } else {
                        // 添加检查partyid范围checkRange
                        checkRange(data).then(res => {
                            partyidAdd(data).then(res => {
                                this.dialogVisible = false
                                this.$parent.initList()// 刷新列表
                                this.$message.success(this.$t('m.common.success'))
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
            this.cannotEdit.intervalWithPartyIds = ''
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
