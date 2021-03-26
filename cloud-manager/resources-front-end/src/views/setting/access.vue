<template>

  <div class="access-box">
    <div class="system-header">
      <el-radio-group class="radio" v-model="radio">
        <el-radio-button label="Cloud Manager"></el-radio-button>
        <el-radio-button label="FATE Manager"></el-radio-button>
      </el-radio-group>

    </div>
     <div class="row-add">
        <div class="btn-add">
            <el-button type="text" class="access-add" @click="toAdd"  >
                <img src="@/assets/add_admin.png">
                <span>add</span>
            </el-button>
            <el-input v-if="radio==='Cloud Manager'" class="input" clearable v-model.trim="data.name" placeholder="Search for Name">
                <i slot="prefix" @click="toSearch" class="el-icon-search search" />
            </el-input>
            <el-input v-else class="input" clearable v-model.trim="data.institutions" placeholder="Search for Institution">
                <i slot="prefix" @click="toSearch" class="el-icon-search search" />
            </el-input>
        </div>

    </div>
    <div class="system-body">
        <div v-if="radio==='Cloud Manager'" class="table">
            <el-table :data="cloudtableData" ref="table" header-row-class-name="tableHead" header-cell-class-name="tableHeadCell" cell-class-name="tableCell" height="100%" tooltip-effect="light">
                <el-table-column prop="name" label="Name" ></el-table-column>
                <el-table-column prop="adminLevel" label="Admin-level"   align="center" show-overflow-tooltip></el-table-column>
                <el-table-column prop="creator" label="Creator"  align="center"></el-table-column>
                <el-table-column prop="createTime" label="Create Time"  align="center">
                    <template slot-scope="scope">
                    <span>{{scope.row.createTime | dateFormat}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="Action" align="right" >
                    <template slot-scope="scope">
                        <span >
                            <el-button v-if="scope.row.name===loginName" type="text" disabled="" icon="el-icon-delete-solid"></el-button>
                            <el-button v-else type="text" @click="todDelete(scope.row)" icon="el-icon-delete-solid"></el-button>
                        </span>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div v-else class="table">
            <el-table :data="managertableData" ref="table" header-row-class-name="tableHead" header-cell-class-name="tableHeadCell" cell-class-name="tableCell" height="100%" tooltip-effect="light">
            <el-table-column prop="institutions" label="Institution" show-overflow-tooltip></el-table-column>
            <el-table-column prop="fateManagerId" label="Admin ID" show-overflow-tooltip></el-table-column>
            <el-table-column prop="creator" label="Creator"></el-table-column>
            <el-table-column prop="createTime" label="Create Time" >
                <template slot-scope="scope">
                <span>{{scope.row.createTime | dateFormat}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="status" align="right" >
                <template slot-scope="scope">
                    <span >
                        <el-button v-if="scope.row.status===1" @click="toactivat(scope.row)" type="text">unactivated</el-button>
                        <el-button v-if="scope.row.status===2" disabled type="text">activated</el-button>
                    </span>
                </template>
            </el-table-column>
            </el-table>
        </div>
        <div class="pagination">
            <el-pagination background @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="data.pageSize" layout="total, prev, pager, next, jumper" :total="total"></el-pagination>
        </div>
    </div>
    <!-- 添加弹框 -->
    <el-dialog :visible.sync="adddialog" class="auto-dialog" width="690px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
        <div class="dialog-title">
          Add admin
        </div>
        <div class="dialog-line" v-if='radio === "FATE Manager"'>
            To add administrtor right to FATE Manager,please fill in the institution name to
            which the administrator belongs. Once filled in,it cannot be modified.
        </div>
        <div class="add-input">
            <span class="input-title">
                <span  v-if='radio === "Cloud Manager"'>admin</span>
                <span  v-if='radio === "FATE Manager"'>Institution</span>
            </span>
            <el-input v-model="institutionName" @focus="warn=false" :class="{'input-warn':warn}" placeholder="Please enter a name"></el-input>
            <div v-if="warn" class="text-warn" >The address is invalid. Please enter again.</div>
        </div>
        <div class="add-input" v-if='radio === "Cloud Manager"'>
            <span class="input-title">admin-level</span>
            <el-checkbox v-model="levelChecked" disabled>senior admin</el-checkbox>
        </div>
         <div class="add-input" v-if='radio === "FATE Manager"'>
            <span class="input-title">Invitation Link Type</span>
            <el-radio-group v-model="protocol">
                <el-radio label="https://">HTTPS</el-radio>
                <el-radio label="http://">HTTP</el-radio>
            </el-radio-group>
        </div>
        <div class="dialog-foot">
          <el-button type="primary" @click="toOK">OK</el-button>
          <el-button type="info" @click="adddialog=false">Cancel</el-button>
        </div>
      </div>
    </el-dialog>
    <!-- 删除弹框 -->
    <el-dialog :visible.sync="deletedialog" class="auto-dialog" width="700px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
        <div class="line-text-one">
          Are you sure you want to delete this administrator?
        </div>
        <div class="dialog-foot">
          <el-button type="primary" @click="toSure">Sure</el-button>
          <el-button type="info" @click="deletedialog=false">Cancel</el-button>
        </div>
      </div>
    </el-dialog>
    <!-- 添加成功弹框 -->
    <el-dialog :visible.sync="addSuccessdialog"  class="add-success-dialog">
        <div class="icon">
            <!-- <i  v-if="addType==='FATE-Manager-add' || addType==='FATE-Manager-updata'" class="el-icon-success"></i> -->
        </div>
        <div v-if="addType==='FATE-Manager-add'" class="line-text-one" >Add successfully !</div>
        <div v-if="addType==='FATE-Manager-updata'" class="line-text-one" >Modify successfully !</div>
        <div class="line-text-two">the administrator invitation link has been generated as follows:</div>
        <div class="line-text-three">
            <el-popover
                placement="top"
                width="660"
                trigger="hover"
                offset="-30"
                :content="addSuccessText">
                <span class="copy-link" slot="reference">{{addSuccessText}}</span>
            </el-popover>
            <span class="copy dialogcopy"  @click="toCopy"  :data-clipboard-text="addSuccessText">copy</span>
        </div>
        <div class="line-text-four">
            <span>
                Current invitation link is generated as
                <span v-if="protocol==='http://'">HTTP</span>
                <span v-if="protocol==='https://'">HTTPS</span>
                ，you can click to
            </span>
            <span class="regenerate" @click="toUpdata">
                regenerate link as
                <span v-if="protocol==='http://'">HTTPS</span>
                <span v-if="protocol==='https://'">HTTP</span>
            </span>
        </div>
        <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="initList();addSuccessdialog=false">OK</el-button>
        </div>
    </el-dialog>
  </div>

</template>

<script>

import moment from 'moment'
import Clipboard from 'clipboard'
import { mapGetters } from 'vuex'
import { accessCloudList, accessManagerList, addManager, addCloud, deleteCloud, updataManager } from '@/api/setting'

export default {
    name: 'access',
    components: {},
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            institutionName: '',
            addType: '',
            fateManagerId: '',
            warn: false,
            adddialog: false,
            deletedialog: false,
            addSuccessdialog: false,
            levelChecked: true,
            radio: 'Cloud Manager',
            protocol: 'https://',
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            managertableData: [],
            cloudtableData: [],
            historydata: [], // 历史记录
            addSuccessText: '', // 连接
            data: {
                pageNum: 1,
                pageSize: 20
            }
        }
    },
    watch: {
        radio: {
            handler(val) {
                // 全部入参条件初始化
                this.data = {
                    name: '',
                    institutions: '',
                    pageNum: 1,
                    pageSize: 20
                }
                this.initList()
            }
        }
    },
    computed: {
        ...mapGetters(['getInfo', 'loginName'])
    },
    created() {
        this.initList()
        if (this.$route.query.type === 'FATEManager') {
            this.radio = 'FATE Manager'
        }
    },
    mounted() {

    },
    methods: {
        // 初始化表格
        initList() {
            // 去除空参数
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            if (this.radio === 'Cloud Manager') {
                accessCloudList(this.data).then(res => {
                    this.cloudtableData = res.data.list
                    this.total = res.data.totalRecord
                })
            } else if (this.radio === 'FATE Manager') {
                accessManagerList(this.data).then(res => {
                    this.managertableData = res.data.list
                    this.total = res.data.totalRecord
                })
            }
        },
        // 确定添加
        toOK() {
            if (!this.institutionName) {
                this.warn = true
                return
            }
            if (this.radio === 'Cloud Manager') {
                let data = {
                    creator: this.loginName, // 当前登录用户
                    adminLevel: 1, // 暂定
                    name: this.institutionName.trim()
                }
                addCloud(data).then(res => {
                    this.adddialog = false
                    this.initList()
                })
            } else {
                let data = {
                    creator: this.loginName, // 当前登录用户
                    institutions: this.institutionName.trim(),
                    protocol: this.protocol
                }
                addManager(data).then(res => {
                    this.addSuccessText = res.data.registrationLink
                    this.fateManagerId = res.data.fateManagerId
                    this.protocol = res.data.protocol
                    this.adddialog = false
                    this.addSuccessdialog = true
                })
            }
        },
        // 删除行
        todDelete(row) {
            this.deletedialog = true
            this.deleteRow = row.cloudManagerId
        },
        // 确实删除
        toSure() {
            let data = {
                cloudManagerId: this.deleteRow
            }
            deleteCloud(data).then(res => {
                this.deletedialog = false
                this.initList()
            })
        },
        // 添加
        toAdd() {
            this.institutionName = ''
            this.addType = 'FATE-Manager-add'
            this.adddialog = true
        },
        // 激活
        toactivat(row) {
            this.addSuccessText = row.registrationLink
            this.fateManagerId = row.fateManagerId
            this.institutionName = row.institutions
            this.protocol = row.protocol
            this.addType = ''
            this.addSuccessdialog = true
        },
        toUpdata() {
            let data = {
                creator: this.loginName,
                institution: this.institutionName,
                fateManagerId: this.fateManagerId
            }
            if (this.protocol === 'http://') {
                data.protocol = 'https://'
            } else if (this.protocol === 'https://') {
                data.protocol = 'http://'
            }
            updataManager(data).then(res => {
                this.addSuccessdialog = false
                this.addSuccessText = res.data.registrationLink
                this.protocol = res.data.protocol
                setTimeout(() => {
                    this.addSuccessdialog = true
                    this.addType = 'FATE-Manager-updata'
                }, 300)
            })
        },
        // 翻页
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        toCopy() {
            let dialogClipboard = new Clipboard('.dialogcopy')
            dialogClipboard.on('success', e => {
                this.$message({ type: 'success', message: 'Success!' })
                // 释放内存
                dialogClipboard.destroy()
            })
        },
        // 点击搜索
        toSearch() {
            this.currentPage = 1
            this.initList()
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/access.scss';
</style>
