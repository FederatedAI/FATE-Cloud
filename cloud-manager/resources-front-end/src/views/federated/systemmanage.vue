<template>
    <div  class="table">
        <div class="table-head">
            <el-button class="route-add" type="text" @click="toAdd">
                <img src="@/assets/add_ip.png">
                <span>add</span>
            </el-button>
            <el-select class="product input-placeholder" v-model="data.productName" placeholder="product">
                <el-option
                    v-for="item in productSelect"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                ></el-option>
            </el-select>
            <el-select class="version input-placeholder" v-model="data.productVersion" placeholder="FATE_Version">
                <el-option
                    v-for="item in versionSelect"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                ></el-option>
            </el-select>
            <el-button class="go" type="primary" @click="toSearch">GO</el-button>
        </div>
        <el-table
            :data="tableData"
            header-row-class-name="tableHead"
            cell-class-name="tableCell"
            ref="table"
            height="100%"
            tooltip-effect="light">
            <el-table-column prop="productName" label="product"  class-name="cell-td-td"  show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="productVersion" label="version" class-name="cell-td-td"  show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="kubernetesChart" label="Kubernetes Chart Version" >
            </el-table-column>
            <el-table-column prop="" label="Component_name" >
                <template slot-scope="scope">
                   <div v-for="(item, index) in scope.row.federatedComponentVersionDos" :key="index">
                        <span>{{item.componentName}}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="federatedGroupSetDo"  min-width="100" label="Component_version" >
                <template slot-scope="scope">
                   <div v-for="(item, index) in scope.row.federatedComponentVersionDos" :key="index">
                        <span>{{item.componentVersion}}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="" label="Image Info."  align="center" >
                <el-table-column prop="federatedGroupSetDo" label="reposity" >
                    <template slot-scope="scope">
                        <div v-for="(item, index) in scope.row.federatedComponentVersionDos" :key="index">
                            <span>{{item.imageRepository}}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="" label="tag" >
                    <template slot-scope="scope">
                        <div v-for="(item, index) in scope.row.federatedComponentVersionDos" :key="index">
                            <span>{{item.imageTag}}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="imageDownloadUrl" label="Download_URL" class-name="cell-td-td"  show-overflow-tooltip >
                </el-table-column>
            </el-table-column>
            <el-table-column prop="" label="Package Info."  >
                <el-table-column prop="packageDownloadUrl" label="Download_URL" class-name="cell-td-td" show-overflow-tooltip>
                </el-table-column>
            </el-table-column>
            <el-table-column prop="publicStatus" align="center"  label="Publish_status" >
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.status" @change="changeSwitch(scope.row) ">
                    </el-switch>
                </template>
            </el-table-column>
            <el-table-column prop="federatedSiteModelDos" align="center" label="Action" >
                <template slot-scope="scope">
                    <el-button type="text" @click="toEditRow(scope.row)">
                        <i class="el-icon-edit"></i>
                    </el-button>
                    <el-button type="text" @click="toDeleteRow(scope.row)" >
                        <i class="el-icon-delete-solid"></i>
                    </el-button>
                </template>
            </el-table-column>

        </el-table>
        <div class="pagination">
            <el-pagination
                background
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage"
                :page-size="data.pageSize"
                layout="total, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
        </div>
        <!-- 添加或编辑 -->
        <el-dialog :visible.sync="editdialog" class="sys-edit-dialog" width="700px">
            <div class="dialog-title">
                {{type}}
            </div>
            <div class="dialog-body">
                <el-form label-position="left" ref="editform" class="edit-form" :rules="editRules"   label-width="190px"  :model="editFormData">
                    <el-form-item label="productName" prop="productName" >
                        <el-select class="version input-placeholder" v-model="editFormData.productName" placeholder=" ">
                            <el-option
                                v-for="item in productName"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="Version" prop="productVersion" >
                        <el-input @focus="cancelValid('productVersion')" v-model="editFormData.productVersion"></el-input>
                    </el-form-item>
                    <el-form-item label="Kubernetes Chart Version" prop="kubernetesChart" >
                        <span slot="label">
                            <span>Kubernetes Chart Version</span>
                            <el-tooltip effect="dark" placement="top">
                                <div style="font-size:12px" slot="content">
                                    <div>Required when using k8s deployment</div>
                                </div>
                                <i style="margin-left: 5px;" class="el-icon-info icon-info"></i>
                            </el-tooltip>
                        </span>
                        <el-input  v-model="editFormData.kubernetesChart"></el-input>
                    </el-form-item>
                    <div class="title-body">
                        <span>Component</span>
                        <el-button type="text" @click="toAddComponent" icon="el-icon-circle-plus"></el-button>
                    </div>
                    <div class="content-body">
                        <el-table
                            :data="editFormData.federatedComponentVersionDos"
                            header-row-class-name="tableHead"
                            header-cell-class-name="tableHeadCell"
                            cell-class-name="tableCell"
                            height="100%">
                            <el-table-column prop="componentName"  label="Name" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="componentVersion" align="center" label="Version"  show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="imageRepository" align="center" label="Repository" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="imageTag" align="center" label="Tag" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="" align="center" label="Action">
                                <template slot-scope="scope">
                                    <el-button type="text" @click="toeEditComponent(scope.row)">
                                        <i class="el-icon-edit"></i>
                                    </el-button>
                                    <el-button type="text" @click="toeDelteComponent(scope.row)">
                                        <i class="el-icon-close"></i>
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>

                    <div class="componentWarn" v-if='componentWarn'>The field is required !</div>
                    <div class="componentWarn" v-else></div>
                    <div class="checked-box">
                        <el-checkbox v-model="checkedImage">Image Download</el-checkbox>
                    </div>
                    <el-form-item label="Download_name" prop="imageName" >
                        <el-input @focus="cancelValid('imageName')" :disabled="!checkedImage" v-model="editFormData.imageName"></el-input>
                    </el-form-item>
                    <el-form-item label="Download_URL" prop="imageDownloadUrl" >
                        <el-input @focus="cancelValid('imageDownloadUrl')" :disabled="!checkedImage" v-model="editFormData.imageDownloadUrl"></el-input>
                    </el-form-item>
                    <div class="checked-box">
                        <el-checkbox v-model="checkedPackage">Package Download</el-checkbox>
                    </div>
                    <el-form-item label="Download_name" prop="packageName" >
                        <el-input @focus="cancelValid('packageName')" :disabled="!checkedPackage" v-model="editFormData.packageName"></el-input>
                    </el-form-item>
                    <el-form-item label="Download_URL" prop="packageDownloadUrl" >
                        <el-input @focus="cancelValid('packageDownloadUrl')" :disabled="!checkedPackage" v-model="editFormData.packageDownloadUrl"></el-input>
                    </el-form-item>
                    <div class="title-body">
                        <span>Publish_status</span>
                        <el-switch style="margin-left: 15px;" v-model="editFormData.status"> </el-switch>
                    </div>
                </el-form>
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toSave">Save</el-button>
                <el-button class="ok-btn" type="info" @click="tocancel">Cancel</el-button>
            </div>
            <el-dialog class="sys-edit-inner" width="600px" :visible.sync="componentVisible" append-to-body>
                <el-form label-position="left" ref="editcomponentform" class="inner-form" :rules="editRules"   label-width="140px"  :model="tempEditComponent">
                    <div class="title-body">
                        <span>Component</span>
                    </div>
                    <el-form-item label="Component_Name" prop="componentName" >
                        <el-input @focus="cancelValid('componentName')"  v-model="tempEditComponent.componentName"></el-input>
                    </el-form-item>
                    <el-form-item label="Component_Version" prop="componentVersion" >
                        <el-input @focus="cancelValid('componentVersion')"  v-model="tempEditComponent.componentVersion"></el-input>
                    </el-form-item>
                    <el-form-item label="Repository" prop="imageRepository" >
                        <el-input @focus="cancelValid('imageRepository')"  v-model="tempEditComponent.imageRepository"></el-input>
                    </el-form-item>
                    <el-form-item label="Tag" prop="imageTag" >
                        <el-input @focus="cancelValid('imageTag')"  v-model="tempEditComponent.imageTag"></el-input>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button class="ok-btn" type="primary" @click="toInnerOk">OK</el-button>
                    <el-button class="ok-btn" type="info" @click="toInnerCancel">Cancel</el-button>
                </div>
            </el-dialog>
        </el-dialog>
        <el-dialog class="sys-delete" width="700px" :visible.sync="deleteVisible">
            <div class="title">
                Are you sure you want to delete it ?
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="todelete">OK</el-button>
                <el-button class="ok-btn" type="info" @click="deleteVisible = false">Cancel</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { serviceManageList, addManageList, updateManageList, deleteManageList, getSelect } from '@/api/federated'
import { deepClone } from '@/utils/deepClone'
import moment from 'moment'
import { setTimeout } from 'timers'

export default {
    name: 'PartyId',
    components: {

    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            radio: 'Site service info.',
            editdialog: false,
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            type: 'Add',
            editFormData: {
                federatedComponentVersionDos: []
            },
            tempEditComponent: {},
            tableData: [],
            versionSelect: [],
            productSelect: [],
            productName: [],
            data: {
                pageNum: 1,
                pageSize: 20
            },
            checkedImage: false,
            checkedPackage: false,
            tempData: {},
            componentVisible: false,
            deleteVisible: false, // 删除弹框
            productId: '', // 删除id
            componentWarn: false,
            componentType: 'add',
            editRules: {
                productName: [{ required: true, message: 'The field is required !', trigger: 'change' }],
                productVersion: [{
                    required: true,
                    trigger: 'change',
                    validator: (rule, value, callback) => {
                        let versionReg = new RegExp(/^\d(\.\d+)+$/)
                        if (!value) {
                            callback(new Error('The field is required !'))
                        } else if (!versionReg.test(value)) {
                            callback(new Error('Please enter the correct version!'))
                        } else {
                            callback()
                        }
                    }
                }],
                componentName: [{ required: true, message: 'The field is required !', trigger: 'change' }],
                componentVersion: [{
                    required: true,
                    trigger: 'change',
                    validator: (rule, value, callback) => {
                        let versionReg = new RegExp(/^\d(\.\d+)+$/)
                        if (!value) {
                            callback(new Error('The field is required !'))
                        } else if (!versionReg.test(value)) {
                            callback(new Error('Please enter the correct version!'))
                        } else {
                            callback()
                        }
                    }
                }],
                imageRepository: [{ required: true, message: 'The field is required !', trigger: 'change' }],
                imageTag: [{ required: true, message: 'The field is required !', trigger: 'change' }]
            }
        }
    },
    watch: {
        checkedImage: {
            handler(newVal) {
                if (newVal) {
                    this.editRules.imageName = [{ required: true, message: 'The field is required !', trigger: 'change' }]
                    this.editRules.imageDownloadUrl = [{ required: true, message: 'The field is required !', trigger: 'change' }]
                } else {
                    delete this.editRules.imageName
                    delete this.editRules.imageDownloadUrl
                }
            }
        },
        checkedPackage: {
            handler(newVal) {
                if (newVal) {
                    this.editRules.packageName = [{ required: true, message: 'The field is required !', trigger: 'change' }]
                    this.editRules.packageDownloadUrl = [{ required: true, message: 'The field is required !', trigger: 'change' }]
                } else {
                    delete this.editRules.packageName
                    delete this.editRules.packageDownloadUrl
                }
            }
        }

    },
    created() {
        this.initList()
    },
    mounted() {

    },
    methods: {
        // 初始化表格
        initList() {
            this.tableData = []
            // 去除空参数
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            serviceManageList(this.data).then(res => {
                this.total = res.data.totalRecord || 0
                res.data.list.forEach(item => {
                    item.status = item.publicStatus === 1
                    this.tableData.push(item)
                })
            })
            this.togetSelect()
        },
        togetSelect() {
            this.productSelect = [{
                value: '',
                label: 'product'
            }]
            this.productName = []
            this.versionSelect = [{
                value: '',
                label: 'FATE_Version'
            }]
            getSelect().then(res => {
                res.data.productNameList.forEach(item => {
                    let obj = {}
                    obj.value = item
                    obj.label = item
                    this.productSelect.push(obj)
                    this.productName.push(obj)
                })
                res.data.productVersionList.forEach(item => {
                    let obj = {}
                    obj.value = item
                    obj.label = item
                    this.versionSelect.push(obj)
                })
            })
        },
        // 搜索
        toSearch() {
            this.data.pageNum = 1
            this.initList()
        },
        // 翻页
        handleCurrentChange(val) {
            this.data.pageNum = val
        },
        // 添加行
        toAdd() {
            if (this.$refs['editform']) {
                this.$refs['editform'].resetFields()
            }
            this.type = 'Add'
            this.checkedImage = false
            this.checkedPackage = false
            this.componentWarn = false
            this.editdialog = true
            this.editFormData = {
                federatedComponentVersionDos: []
            }
        },
        // 编辑行
        toEditRow(row) {
            this.type = 'Edit'
            console.log('row.imageDownloadUrl==>>', row.imageDownloadUrl)

            if (row.imageDownloadUrl) {
                this.checkedImage = true
            } else {
                this.checkedImage = false
            }
            if (row.packageDownloadUrl) {
                this.checkedPackage = true
            } else {
                this.checkedPackage = false
            }
            this.editdialog = true
            this.editFormData = deepClone(row)
        },
        // 取消验证
        cancelValid(validtype) {
            if (this.$refs['editform']) {
                this.$refs['editform'].clearValidate(validtype)
            }
            if (this.$refs['editcomponentform']) {
                this.$refs['editcomponentform'].clearValidate(validtype)
            }
        },
        // 确定添加
        toSave() {
            let data = {
                ...this.editFormData,
                componentVersionAddQos: this.editFormData.federatedComponentVersionDos,
                publicStatus: this.editFormData.status ? 1 : 2
            }
            for (const key in data) {
                if (key === 'federatedComponentVersionDos' || key === 'status') {
                    delete data[key]
                }
            }
            this.componentWarn = !(data.componentVersionAddQos.length > 0)
            this.$refs['editform'].validate((valid) => {
                if (valid && !this.componentWarn) {
                    if (this.type === 'Add') {
                        addManageList(data).then(res => {
                            this.initList()
                            this.editdialog = false
                        })
                    } else {
                        updateManageList(data).then(res => {
                            this.initList()
                            this.editdialog = false
                        })
                    }
                }
            })
        },
        // 取消
        tocancel() {
            this.componentWarn = false
            this.editdialog = false
        },
        // 删除弹框内容
        toeDelteComponent(row) {
            this.editFormData.federatedComponentVersionDos.forEach((item, index) => {
                if (item.componentName === row.componentName) {
                    this.editFormData.federatedComponentVersionDos.splice(index, 1)
                }
            })
            setTimeout(() => {
                this.editFormData = deepClone(this.editFormData)
            }, 500)
        },
        // 添加内部弹框
        toAddComponent() {
            if (this.$refs['editcomponentform']) {
                this.$refs['editcomponentform'].resetFields()
            }
            this.componentWarn = false
            this.componentVisible = true
            this.componentType = 'add'
            this.tempEditComponent = { componentId: new Date().getTime() }
        },
        // 编辑弹框
        toeEditComponent(row) {
            this.tempEditComponent = deepClone(row)
            this.componentVisible = true
            this.componentType = 'edit'
        },
        // 内部弹框确定
        toInnerOk() {
            this.$refs['editcomponentform'].validate((valid) => {
                if (valid) {
                    if (this.componentType === 'add') {
                        this.editFormData.federatedComponentVersionDos.push({ ...this.tempEditComponent })
                        this.componentVisible = false
                    } else {
                        this.editFormData.federatedComponentVersionDos.forEach((item, index) => {
                            if (item.componentId === this.tempEditComponent.componentId) {
                                this.editFormData.federatedComponentVersionDos[index] = deepClone(this.tempEditComponent)
                            }
                        })
                        setTimeout(() => {
                            this.editFormData = deepClone(this.editFormData)
                        }, 500)
                        this.componentVisible = false
                    }
                }
            })
        },
        // 内容弹框取消
        toInnerCancel() {
            this.componentVisible = false
        },

        changeSwitch(row) {
            let data = {
                ...row,
                componentVersionAddQos: row.federatedComponentVersionDos,
                publicStatus: row.status ? 1 : 2
            }
            for (const key in data) {
                if (key === 'federatedComponentVersionDos' || key === 'status') {
                    delete data[key]
                }
            }
            updateManageList(data).then(res => {
                this.initList()
                this.editdialog = false
            })
        },
        // 删除行
        toDeleteRow(row) {
            console.log('productId==>>', row.productId)
            this.productId = row.productId
            this.deleteVisible = true
        },
        // 删除行
        todelete() {
            let data = {
                productId: this.productId
            }
            deleteManageList(data).then(res => {
                this.deleteVisible = false
                this.initList()
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/system.scss';

</style>
