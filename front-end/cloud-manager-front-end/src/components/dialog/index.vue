<template>
  <!-- 封装弹框 -->
  <div class="popup-dialog">
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :width="dialogWidth"
      :buttons="buttons"
      :before-close="handleClose"
      :show-close="showClose"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <slot>
        <div></div>
      </slot>
      <span slot="footer" class="dialog-footer">
        <el-button class="contral-btn sure" type="primary" :disabled="buttons.sure.disabled" @click="Save">{{buttons.sure.text}}</el-button>
        <el-button class="contral-btn reject" v-if="buttons.reject" type="primary" @click="Reject">{{buttons.reject}}</el-button>
        <el-button class="contral-btn cancel" type="info" @click="Cancel">{{buttons.cancel}}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
    name: 'popup-dialog',
    props: {
        dialogTitle: {
            type: String,
            default: '标题'
        },
        visible: {
            type: Boolean,
            default: false
        },
        dialogWidth: {
            type: String,
            default: '550px'
        },
        buttons: {
            type: Object,
            default: () => {
                return {
                    sure: {
                        text: this.$t('m.common.sure'),
                        disabled: false
                    },
                    cancel: this.$t('m.common.cancel'),
                    reject: this.$t('m.common.reject')
                }
            }
        },
        showClose: {
            type: Boolean,
            default: true
        }
    },
    computed: {
        dialogVisible: {
            get () {
                return this.visible
            },
            set (val) {
                // 当visible改变的时候，触发父组件的 updateVisible方法，在该方法中更改传入子组件的 centerDialogVisible的值
                this.$emit('updateVisible', val)
            }
        }
    },
    methods: {
        Cancel () {
            this.$emit('resetPopupData')
        },
        Save () {
            this.$emit('submitPopupData')
        },
        handleClose () {
            this.$emit('handleClose')
        },
        Reject () {
            this.$emit('Reject')
        }
    }
}
</script>

<style lang="scss">
.popup-dialog {
    .el-dialog__header {
        padding: 24px 24px 0;
    }
    .el-dialog__body {
        padding: 24px 24px 0;
        text-align: center;
        .line-text-one {
            font-size: 18px;
            color: #2D3642;
            line-height: 36px;
            margin-bottom: 16px
        }
        .line-text-two {
            font-size: 18px;
            color: #217AD9;
            line-height: 34px;
            margin-bottom: 16px
        }
        .main-tips{
            font-size: 16px;
            font-weight: bold;
            line-height: 24px;
            color: #4E5766;
            margin-bottom: 12px;
        }
        .gray-tips{
            font-size: 16px;
            font-weight: 500;
            line-height: 24px;
            color: #848C99;
            margin-bottom: 12px;
        }
        .el-dialog__title,.dialog-title {
            line-height: 24px;
            font-size: 18px;
            color: #505765;
            font-weight: bold;
        }
        .select-item{
            height: 36px;
            line-height: 36px;
            .el-radio__label{
                font-size: 16px;
            }
        }
        .el-radio-group{
            margin: 24px 12px;
        }
    }

    .el-dialog__footer {
        text-align: center;
        padding: 10px 24px 24px;
        .contral-btn {
            width: 130px;
            font-size: 16px;
            height: 24px;
            line-height: 24px;
        }
    }
}
</style>
