<template>
  <div class="federal-site">
    <div class="panel-header">
      <el-button type="primary" size="small" @click="addSite">Add</el-button>
    </div>
    <div class="panel-body">
      <div class="product-main">
        <div class="table-main">
          <el-table
            v-loading="loading"
            :data="tableData"
            height="100%">
            <el-table-column prop="partyId" label="Party ID" align="left" show-overflow-tooltip/>
            <el-table-column prop="portalUrl" label="Network Access Entrances" align="left" width="250" show-overflow-tooltip/>
            <el-table-column prop="exportUrl" label="Network Access Exits" align="left" width="250" show-overflow-tooltip/>
            <el-table-column label="SecretKey" align="left" show-overflow-tooltip>
              <template slot-scope="scope">
                <el-popover
                  placement="bottom"
                  title=""
                  width="450"
                  trigger="click">
                  <p>Federation Key: {{ scope.row.key }}</p>
                  <p>Federation Secret: {{ scope.row.secret }}</p>
                  <el-button slot="reference" type="text">View</el-button>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column label="Role" align="left" show-overflow-tooltip>
              <template slot-scope="scope">
                <el-select :disabled="scope.row.nodeStatusVO.code===2" v-model="scope.row.roleTypeVO.code" placeholder="select" size="small" style="width:100px;" @change="changeRoleType(scope.row)">
                  <el-option :value="1" label="guest"/>
                  <el-option :value="2" label="host"/>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="nodePublicVO.englishDescription" label="Identity Public" align="left" width="150" show-overflow-tooltip/>
            <el-table-column prop="nodeStatusVO.englishDescription" label="Status" align="left" show-overflow-tooltip/>
            <el-table-column label="Operation" align="left" show-overflow-tooltip>
              <template slot-scope="scope">
                <el-button type="text" @click="deleteSite(scope.row)">Delete</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <pagination
        :total="total"
        :page.sync="listQuery.pageNumber"
        :limit.sync="listQuery.pageSize"
        @pagination="getList"/>
    </div>
    <el-dialog
      :visible.sync="onshow"
      :close-on-click-modal="false"
      width="25%"
      center>
      <div style="word-break:break-word">{{ confirmText }}</div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmDelete">Yes</el-button>
        <el-button @click="onshow=false">No</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// import { MessageBox } from 'element-ui'
import Pagination from '@/components/Pagination'
import { getSiteList, addSite, deleteSite, updateSite } from '@/api/manageSite'

export default {
  name: 'FederalSite',
  components: {
    Pagination
  },
  data() {
    return {
      loading: false,
      total: 0,
      listQuery: {
        updateTimeOrder: 1,
        pageNumber: 1,
        pageSize: 20
      },
      tableData: [],
      confirmText: '',
      onshow: false,
      rowInfo: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // get list
    getList() {
      this.loading = true
      getSiteList(this.listQuery).then((res) => {
        this.loading = false
        this.total = res.data.totalRecord
        this.tableData = res.data.list
      }).catch(() => {
        this.loading = false
      })
    },
    // add Site
    addSite() {
      addSite().then((res) => {
        setTimeout(() => {
          this.listQuery.pageNum = 1
          this.getList()
        }, 200)
      }).catch(err => {
        console.log('error：', err)
      })
    },
    // delete Site
    deleteSite(row) {
      // MessageBox.confirm('The member will be removed from federation，are you sure to delete?', 'Tips', {
      //   cancelButtonText: 'No',
      //   confirmButtonText: 'Yes',
      //   type: 'warning'
      // }).then(() => {
      //   this.confirmDelete(row)
      // }).catch(() => {
      // })
      this.rowInfo = row
      this.confirmText = 'The member will be removed from federation，are you sure to delete?'
      this.onshow = true
    },
    // confirm delete
    confirmDelete() {
      this.onshow = false
      deleteSite({ partyId: this.rowInfo.partyId }).then((res) => {
        this.$message.success('The member has been removed successfully！')
        setTimeout(() => {
          this.listQuery.pageNum = 1
          this.getList()
        }, 200)
      }).catch(err => {
        console.log('error：', err)
      })
    },
    // update role
    changeRoleType(row) {
      updateSite({ partyId: row.partyId, roleType: row.roleTypeVO.code }).then((res) => {
        this.$message.success('Update member configuration successfully！')
      }).catch(err => {
        console.log('error：', err)
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
  .federal-site{
    height: calc(100vh - 150px);
    .panel-header{
      padding: 0px 15px;
      display: flex;
      justify-content: space-between;
      // border-bottom: 1px solid #E5E5E5;
    }
    .panel-body{
      padding: 15px;
      height: 100%;
      .product-main{
        height: 100%;
        .table-main{
          height: 100%;
          .el-table{
            .el-table__header-wrapper tr th{
              background-color: #F3F4F5;
            }
            .el-table__body tr,
              .el-table__body td {
              // height: 20px;
              padding: 3px 0px;
            }
          }
        }
      }
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
