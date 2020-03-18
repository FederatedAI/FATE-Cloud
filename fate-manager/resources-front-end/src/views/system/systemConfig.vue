<template>
  <div class="user-edit">
    <el-row>
      <el-col :span="24">
        <div class="user-info-box">
          <div class="header-info">
            <span class="header-left">Current Status：
              <span v-if="status === 1">Not Joined</span>
              <span v-else-if="status === 2">Joined</span>
              <span v-else-if="status === 3">Removed</span>
            </span>
            <span v-if="status === 2" class="header-right">
              <el-button type="text" class="edit-btn" @click="editInfo">Edit</el-button>
            </span>
          </div>
          <div class="form-box">
            <el-form ref="updateForm" :model="updateForm" :rules="rules" label-position="left" label-width="250px" style="min-width:750px;">
              <el-form-item label="Party ID:" prop="partyId" maxlength="20">
                <el-input v-if="status !== 2" v-model.trim="updateForm.partyId" />
                <span v-else>{{ updateForm.partyId }}</span>
              </el-form-item>
              <el-form-item label="Federation Key:" prop="appKey" maxlength="255">
                <el-input v-if="status !== 2" v-model.trim="updateForm.appKey" />
                <span v-else>{{ updateForm.appKey }}</span>
              </el-form-item>
              <el-form-item label="Federation Secret:" prop="appSecret" maxlength="255">
                <el-input v-if="status !== 2" v-model.trim="updateForm.appSecret" />
                <span v-else>{{ updateForm.appSecret }}</span>
              </el-form-item>
              <el-form-item label="Network Access Entrances:" prop="portalUrl">
                <el-input v-if="isEdit || status !== 2" v-model.trim="updateForm.portalUrl" maxlength="255"/>
                <span v-else>{{ updateForm.portalUrl }}</span>
              </el-form-item>
              <el-form-item label="Network Access Exits:" prop="exportUrl">
                <el-input v-if="isEdit || status !== 2" v-model.trim="updateForm.exportUrl" maxlength="255" />
                <span v-else>{{ updateForm.exportUrl }}</span>
              </el-form-item>
              <el-form-item label="Role:" prop="role">
                <el-radio-group v-if="status !== 2" v-model.trim="updateForm.roleTypePair.code">
                  <el-radio :label="1" style="width:100px;">guest</el-radio>
                  <el-radio :label="2">host</el-radio>
                </el-radio-group>
                <span v-else>{{ updateForm.roleTypePair.code === 1? 'guest': 'host' }}</span>
              </el-form-item>
              <el-form-item label="Identity Public:" prop="nodePublic">
                <el-radio-group v-if="isEdit || status !== 2" v-model.trim="updateForm.nodePublicPair.code">
                  <el-radio :label="1" style="width:100px;">Yes</el-radio>
                  <el-radio :label="2">No</el-radio>
                </el-radio-group>
                <span v-else>{{ updateForm.nodePublicPair.code === 1? 'Yes': 'No' }}</span>
              </el-form-item>
              <el-form-item label="Federated Organization to Join：" prop="fedratrionUrl">
                <el-input v-if="isEdit || status !== 2" v-model.trim="updateForm.fedratrionUrl" maxlength="255" />
                <span v-else>{{ updateForm.fedratrionUrl }}</span>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-col>
    </el-row>
    <div class="btn_row">
      <el-button v-if="status!==2" type="primary" @click="register">Apply to Join</el-button>
      <el-button v-if="status===2 && isEdit" type="primary" @click="save">Save</el-button>
      <el-button v-if="status===2 && isEdit" @click="cancelUpdate()">Cancel</el-button>
    </div>
    <el-dialog
      :visible.sync="onshow"
      :close-on-click-modal="false"
      width="25%"
      center>
      <div style="word-break:break-word">{{ confirmText }}</div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmRequest">Yes</el-button>
        <el-button @click="onshow=false">No</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import moment from 'moment'
import { getSiteInfo, saveSiteInfo } from '@/api/systemConfig'

export default {
  filters: {
    datefrom(value) {
      return moment(value).format('YYYY-MM-DD HH:mm:ss')
    }
  },
  data() {
    return {
      updateForm: {
        partyId: '',
        portalUrl: '',
        exportUrl: '',
        appKey: '',
        appSecret: '',
        nodePublicPair: { code: 1 },
        nodeStatusPair: {},
        roleTypePair: { code: 1 },
        fedratrionUrl: ''
      },
      status: 1, // 1 Not Joined， 2 Joined
      isEdit: false,
      stieInfoTemp: {},
      onshow: false,
      confirmText: '',
      addReq: true,
      rules: {
        partyId: [
          { required: true, message: 'Please input the party id', trigger: 'blur' }
        ],
        appKey: [
          { required: true, message: 'Please input the Federation Key', trigger: 'blur' }
        ],
        appSecret: [
          { required: true, message: 'Please input the Federation Secret', trigger: 'blur' }
        ],
        portalUrl: [
          { required: true, message: 'Please input the Network Access Entrances', trigger: 'blur' }
        ],
        exportUrl: [
          { required: true, message: 'Please input the Network Access Exits', trigger: 'blur' }
        ],
        fedratrionUrl: [
          { required: true, message: 'Please input the Federated Organization', trigger: 'blur' },
          { type: 'url', message: 'Incorrect url format', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
    ])
  },
  created() {
    this.getSiteInfo()
  },
  methods: {
    getSiteInfo() {
      this.loading = true
      getSiteInfo().then((res) => {
        this.loading = false
        if (res.data) {
          this.updateForm = res.data
          this.stieInfoTemp = JSON.parse(JSON.stringify(this.updateForm))
          this.status = res.data.nodeStatusPair.code
        }
      }).catch(() => {
        this.loading = false
      })
    },
    register() {
      this.$refs.updateForm.validate((valid) => {
        if (valid) {
          this.confirmText = 'Are you sure to join the federation?'
          this.onshow = true
          this.addReq = true
        }
      })
    },
    editInfo() {
      this.isEdit = true
    },
    cancelUpdate() {
      this.updateForm = JSON.parse(JSON.stringify(this.stieInfoTemp))
      this.isEdit = false
      this.$refs.updateForm.clearValidate()
    },
    save() {
      this.$refs.updateForm.validate((valid) => {
        if (valid) {
          this.confirmText = 'The configuration will be updated to your federation, are you sure to update？'
          this.onshow = true
          this.addReq = false
        }
      })
    },
    confirmRequest() {
      if (this.addReq) {
        this.addRequest()
      } else {
        this.updateRequest()
      }
      this.onshow = false
    },
    addRequest() {
      const params = {
        partyId: this.updateForm.partyId,
        portalUrl: this.updateForm.portalUrl,
        exportUrl: this.updateForm.exportUrl,
        nodePublic: this.updateForm.nodePublicPair.code,
        roleType: this.updateForm.roleTypePair.code,
        fedratrionUrl: this.updateForm.fedratrionUrl,
        appKey: this.updateForm.appKey,
        appSecret: this.updateForm.appSecret
      }
      saveSiteInfo(params).then((res) => {
        this.$message.success('Congratulations，you have joined your federation successfully！')
        this.stieInfoTemp = JSON.parse(JSON.stringify(this.updateForm))
        this.status = 2
      }).catch(err => {
        console.log('error：', err)
      })
    },
    updateRequest() {
      const params = {
        partyId: this.updateForm.partyId,
        portalUrl: this.updateForm.portalUrl,
        exportUrl: this.updateForm.exportUrl,
        nodePublic: this.updateForm.nodePublicPair.code,
        roleType: this.updateForm.roleTypePair.code,
        fedratrionUrl: this.updateForm.fedratrionUrl,
        appKey: this.updateForm.appKey,
        appSecret: this.updateForm.appSecret
      }
      saveSiteInfo(params).then((res) => {
        this.isEdit = false
        this.$message.success('The configuration have updated successfully！')
        this.stieInfoTemp = JSON.parse(JSON.stringify(this.updateForm))
      }).catch(err => {
        console.log('error：', err)
      })
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" >
  .user-edit{
    .user-info-box{
      width: calc(100% - 20px);
      margin: 20px;
      .header-info{
        padding: 5px;
        // background:rgba(243,244,245,1);
        display: flex;
        border-bottom: 1px solid #E5E5E5;
        margin-bottom: 30px;
        &:first-child {
            padding-left: 20px;
        }
        .header-left{
          flex: 1;
          height: 32px;
          line-height: 32px;
        }
        .header-right {
          position: absolute;
          right: 20px;
          top: 20px;
        }
      }
      .form-box{
        display: flex;
        justify-content: center;
      }
      .el-form{
        .el-form-item{
          // margin-bottom: 20px;
          .el-form-item__content{
            .el-input{
              width: 500px;
            }
          }
        }
      }
    }
    .btn_row{
      padding: 14px 50px;
      text-align: center;
    }
    .edit-btn{
      font-size: 18px;
    }
    .el-dialog__wrapper{
        .el-dialog{
          .el-dialog__header{
            .el-dialog__title{
              border-left: 0;
            }
          }
          .el-dialog__body{
            span{
              width: 100%;
              display: inline-block;
              text-align: center;
            }
          }
        }
      }
  }
</style>
