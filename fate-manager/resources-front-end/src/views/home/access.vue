<template>
  <div class="access">
    <div class="access-box">
        <div class="partyid-header">
            <el-button class="add" type="primary" @click="addRoleUser">Add</el-button>
            <el-input class="input input-placeholder" clearable v-model.trim="data.userName" placeholder="Name">
                <i slot="prefix" @click="getList" class="el-icon-search search el-input__icon" />
            </el-input>
            <el-select class="sel-institutions input-placeholder" v-model="data.partyId" placeholder="Site">
                <el-option
                    v-for="item in partyIdSiteList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                ></el-option>
            </el-select>
            <el-select class="sel-institutions input-placeholder" v-model="data.roleId" placeholder="Role">
                <el-option
                    v-for="item in typeSelect"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                ></el-option>
            </el-select>
            <el-button class="go" type="primary" @click="getList">GO</el-button>
        </div>
        <div class="table">
            <el-table
                :data="tableData"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                tooltip-effect="light">
                <el-table-column prop="userName"  label="Name" show-overflow-tooltip></el-table-column>
                <el-table-column prop="site.siteName" label="Site"></el-table-column>
                <el-table-column prop="role.roleName" label="Role" show-overflow-tooltip></el-table-column>
                <el-table-column prop="permissionList" label="Permission" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <span v-for="(item, index) in scope.row.permissionList" :key="index">{{item.permissionName}};</span>
                    </template>
                </el-table-column>
                <el-table-column prop="creator" label="Creator" show-overflow-tooltip></el-table-column>
                <el-table-column prop="createTime" label="Create Time">
                    <template slot-scope="scope">
                        <span >{{scope.row.createTime | dateFormat}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="Action" label="Action" align="center">
                    <template slot-scope="scope" >
                        <!-- 由could生成不能编辑与删除 -->
                        <span v-if='scope.row.cloudUser'>
                            <el-button type="text" disabled>
                                <i class="el-icon-edit" style="margin-right: 10px;"></i>
                            </el-button>
                            <el-button type="text" disabled>
                                <i class="el-icon-delete-solid"  ></i>
                            </el-button>
                        </span>
                         <!-- 不能删除自己 -->
                        <span v-else-if="userName===scope.row.userName">
                            <el-button type="text" >
                                <i class="el-icon-edit edit" @click="handleEdit(scope.row)"></i>
                            </el-button>
                             <el-button type="text" disabled>
                                <i class="el-icon-delete-solid" ></i>
                            </el-button>
                        </span>
                        <span v-else>
                            <el-button type="text">
                                <i class="el-icon-edit edit" @click="handleEdit(scope.row)"></i>
                            </el-button>
                            <el-button type="text" >
                                <i class="el-icon-delete-solid delete" @click="handleDelete(scope.row)"></i>
                            </el-button>
                        </span>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
    <!-- 删除 -->
    <el-dialog :visible.sync="deletedialog" class="access-delete-dialog" width="700px">
        <div class="line-text-one">Are you sure you want to delete this user?</div>
        <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" @click="toDelet">Sure</el-button>
        <el-button class="ok-btn" type="info" @click="deletedialog = false">Cancel</el-button>
        </div>
    </el-dialog>
    <!-- 添加弹框 -->
    <el-dialog :visible.sync="adddialog" class="access-add-dialog" width="700px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
          <el-form :model="siteTemp" :rules="rules" ref="ruleForm">
            <div class="dialog-title">
                <span>{{typetitle}}</span>
            user
            </div>
            <el-form-item class="add-input" prop="userName">
                <span class="input-title">
                    User
                </span>
                <el-select
                    v-if="typetitle!=='Edit'"
                    v-model="siteTemp.userName"
                    filterable
                    remote
                    reserve-keyword
                    placeholder="Please enter a name"
                    :remote-method="remoteMethod"
                    @change="userchange"
                    @focus="toclearValid('userName')"
                    :loading="loading">
                    <el-option
                        v-for="item in userList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
                <span v-else style="font-size: 16px;">{{siteTemp.userName}}</span>
                <!-- <div v-if='userWarn' class="text-warn" >The User Name field is required.</div> -->
            </el-form-item>
             <el-form-item class="add-input"  prop="roleId">
                <span class="input-title">Role</span>
                <el-radio-group v-model="siteTemp.roleId">
                    <el-radio label="1">admin</el-radio>
                    <el-radio label="2">Developer or OP</el-radio>
                    <el-radio label="3">Business or Data Analyst</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item v-if="!sitedisable" class="add-input" prop="siteName">
                 <span class="input-title">Site</span>
                <el-select v-model="siteTemp.siteName"
                    :disabled="sitedisable"
                    @change="sitechange"
                     @focus="toclearValid('siteName')"
                    filterable remote
                    placeholder="">
                    <el-option
                        v-for="item in siteList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    ></el-option>
                </el-select>
                 <!-- <div  v-if='siteWarn' class="text-warn" >The Site field is required.</div> -->
            </el-form-item>
         </el-form>
        <div  class="permission">
            <span class="input-title">Permission</span>
            <span class="item" >
                <span v-for="(item, index) in permissionList" :key="index">
                    <div v-if="item.show" class="show-item">
                        <i class="el-icon-check"></i>
                        {{item.item}}
                    </div>
                    <div v-else class="hide-item">
                        <span>{{item.item}}</span>
                    </div>
                </span>
            </span>
        </div>
        <div class="dialog-foot">
          <el-button type="primary" @click="toSure">Sure</el-button>
          <el-button type="info" @click="adddialog=false">Cancel</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { accessList, userListDown, siteListDown, addUser, editUser, deleteUser } from '@/api/home'

import moment from 'moment'
import { mapGetters } from 'vuex'

export default {
    name: 'access',
    filters: {
        dateFormat(value) {
            return value ? moment(value).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            typetitle: 'Edit',
            adddialog: false,
            deletedialog: false,
            userWarn: false, // 显示警告
            siteWarn: false, // 显示警告
            userList: [], // 用户下拉
            siteList: [], // 站点下拉
            partyIdSiteList: [
                {
                    value: 0,
                    label: 'Site'
                }
            ], // 头部站点下拉
            loading: true, // list加载
            sitedisable: true, // 是否可选site
            deletesite: {}, // 将要删除site
            siteTemp: {
                userId: '',
                userName: '',
                partyId: '',
                siteName: '',
                roleId: '1'
            }, // site临时数据
            tableData: [],
            permissionList: [
                { item: 'FATE Cloud: Basic management', show: true },
                { item: 'FATE Cloud: Auto-deploy', show: true },
                { item: 'FATE Studio', show: true },
                { item: 'FATEBoard', show: true }
                // { item: 'FDN', show: true }
            ],
            typeSelect: [
                {
                    value: 0,
                    label: 'Role'
                },
                {
                    value: 1,
                    label: 'Admin'
                },
                {
                    value: 2,
                    label: 'Developer or OP'
                },
                {
                    value: 3,
                    label: 'Business or Data Analyst'
                }
            ],
            rules: {
                userName: [
                    { required: true, message: 'The User Name field is required !', trigger: 'blur' }
                ],
                siteName: [
                    { required: true, message: 'The Site field is required !', trigger: 'blur' }
                ]
            },
            // 入参
            data: {
                partyId: '',
                roleId: '',
                userName: ''
            }
        }
    },
    watch: {
        'siteTemp.roleId': {
            handler: function(newval) {
                if (newval === '1') {
                    this.sitedisable = true
                    this.siteTemp.siteName = ''
                    this.permissionList.forEach(item => {
                        item.show = true
                    })
                } else if (newval === '2') {
                    this.sitedisable = true
                    this.siteTemp.siteName = ''
                    this.permissionList.forEach((item, index) => {
                        if (index === 0 || index === 1) {
                            item.show = true
                        } else {
                            item.show = false
                        }
                    })
                } else if (newval === '3') {
                    this.sitedisable = false
                    this.permissionList.forEach((item, index) => {
                        if (index === 0 || index === 1) {
                            item.show = false
                        } else {
                            item.show = true
                        }
                    })
                }
            },
            immediate: true
        }
    },
    computed: {
        ...mapGetters(['userName'])
    },
    created() {
        this.getList()
        this.tositeListDown()
    },

    mounted() {

    },
    methods: {
        getList() {
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            accessList(this.data).then(res => {
                this.tableData = res.data
            })
        },
        // 获取site下拉
        tositeListDown() {
            this.siteList = []
            siteListDown().then(res => {
                res.data && res.data.forEach(item => {
                    let obj = { ...item }
                    obj.value = item.partyId
                    obj.label = item.siteName
                    this.siteList.push(obj)
                    this.partyIdSiteList.push(obj)
                })
            })
        },
        // 编辑用户
        handleEdit(row) {
            this.typetitle = 'Edit'
            this.adddialog = true
            this.siteListObj = {
                value: row.site.partyId,
                label: row.site.siteName
            }
            this.siteTemp = {
                roleId: `${row.role.roleId}`,
                partyId: row.site.partyId,
                siteName: row.site.siteName,
                userId: row.userId,
                userName: row.userName
            }
        },
        // 添加用户
        addRoleUser() {
            this.typetitle = 'Add'
            this.adddialog = true
            this.siteTemp = {
                userId: '',
                userName: '',
                partyId: '',
                siteName: '',
                roleId: '1'
            }
        },
        // 确定编辑、添加用户
        toSure() {
            let permissionList
            if (this.siteTemp.roleId === '1') {
                permissionList = [1, 2, 3, 4, 5]
            } else if (this.siteTemp.roleId === '2') {
                permissionList = [1, 2]
            } else if (this.siteTemp.roleId === '3') {
                permissionList = [3, 4, 5]
            }
            let data = {
                creator: this.userName,
                permissionList,
                roleId: parseInt(this.siteTemp.roleId),
                partyId: this.siteTemp.partyId,
                siteName: this.siteTemp.siteName,
                userId: this.siteTemp.userId,
                userName: this.siteTemp.userName
            }
            // 删除空值
            for (const key in data) {
                if (data.hasOwnProperty(key)) {
                    const element = data[key]
                    if (element === '' && element !== 0) {
                        delete data[key]
                    }
                }
            }
            this.$refs['ruleForm'].validate((valid) => {
                if (valid) {
                    if (this.typetitle === 'Add') {
                        addUser(data).then(res => {
                            this.adddialog = false
                            this.getList()
                        })
                    } else if (this.typetitle === 'Edit') {
                        editUser(data).then(res => {
                            this.adddialog = false
                            this.getList()
                        })
                    }
                }
            })
        },
        // 删除用户
        handleDelete(row) {
            this.deletedialog = true
            this.deletesite = row
        },
        // 确定删除
        toDelet() {
            let data = {
                partyId: this.deletesite.site.partyId,
                siteName: this.deletesite.site.siteName,
                userId: this.deletesite.userId,
                userName: this.deletesite.userName
            }
            // 删除空值
            for (const key in data) {
                if (data.hasOwnProperty(key)) {
                    const element = data[key]
                    if (element === '' && element !== 0) {
                        delete data[key]
                    }
                }
            }
            deleteUser(data).then(res => {
                this.deletedialog = false
                this.getList()
            })
        },
        remoteMethod(query) {
            this.loading = true
            this.userList = []
            if (query !== '') {
                let data = {
                    context: query
                }
                userListDown(data).then(res => {
                    res.data && res.data.forEach(item => {
                        let obj = { ...item }
                        obj.value = item.userId
                        obj.label = item.userName
                        this.userList.push(obj)
                    })
                })
                this.loading = false
            }
        },
        userchange(val) {
            for (const iter of this.userList) {
                if (val === iter.value) {
                    this.siteTemp.userId = iter.userId
                    this.siteTemp.userName = iter.userName
                    return
                }
            }
        },
        sitechange(val) {
            for (const iter of this.siteList) {
                if (val === iter.value) {
                    this.siteTemp.partyId = iter.partyId
                    this.siteTemp.siteName = iter.siteName
                    return
                }
            }
        },
        // 清楚验证
        toclearValid(type) {
            this.$refs['ruleForm'].clearValidate(type)
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/access.scss';
</style>
