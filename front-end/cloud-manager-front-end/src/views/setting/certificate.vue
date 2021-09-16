<template>

  <div class="certificate-box">
    <div class="row-add">
        <el-button type="text" class="access-add" @click="toAdd"  >
            <img src="@/assets/add_cert.png">
            <span>{{$t('m.common.add')}}</span>
        </el-button>
        <el-button class="go" type="primary" @click="toSearch">{{$t('m.common.go')}}</el-button>
        <el-input  class="input" clearable v-model.trim="data.queryString" :placeholder="$t('Search for Certificate ID or Site')">
            <i slot="prefix" class="el-icon-search search" />
        </el-input>
        <el-select class="sel-status" v-model="data.status" clearable  :placeholder="$t('Status')">
            <el-option
                v-for="item in status"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
        </el-select>
        <el-select class="sel-institutions" v-model="data.institution" clearable filterable  :placeholder="$t('Institution')">
            <el-option
                v-for="item in institutionsSelectList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
        </el-select>
            <el-select class="sel-certificate" v-model="data.typeId" clearable filterable :placeholder="$t('Certificate Type')">
            <el-option
                v-for="item in certiTypeSelect"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
        </el-select>
    </div>
    <div class="system-body">
        <div  class="table">
            <el-table :data="certificateData" ref="table" header-row-class-name="tableHead" header-cell-class-name="tableHeadCell" cell-class-name="tableCell" height="100%" tooltip-effect="light">
                <el-table-column type="index" :label="$t('m.common.index')"  width="70"></el-table-column>
                <el-table-column prop="serialNumber" :label="$t('Certificate ID')" show-overflow-tooltip>
                     <template slot-scope="scope">
                        <span>{{scope.row.serialNumber ?scope.row.serialNumber :'-' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="certificateType" :label="$t('Certificate Type')"  width="150px" show-overflow-tooltip></el-table-column>
                <el-table-column prop="validity" sortable :label="$t('Validity')"  width="190px"></el-table-column>
                <el-table-column prop="institution"  :label="$t('Institution')"  show-overflow-tooltip></el-table-column>
                <el-table-column prop="siteAuthority"  :label="$t('Site Authority')"  show-overflow-tooltip >
                    <template slot-scope="scope">
                        <span>{{scope.row.siteAuthority ?scope.row.siteAuthority :'-' }}</span>
                    </template>
                </el-table-column>
                 <el-table-column prop="createDate" sortable :label="$t('Create Time')"  width="160px">
                    <template slot-scope="scope">
                        <span>{{scope.row.createDate | dateFormat}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="updateDate" sortable :label="$t('Update Time')"  width="160px">
                    <template slot-scope="scope">
                        <span>{{scope.row.updateDate | dateFormat}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" :label="$t('Status')" >
                    <template slot-scope="scope">
                        {{$t(`m.common.${scope.row.status.toLowerCase()}`)}}
                    </template>
                </el-table-column>
                <el-table-column prop="notes" :label="$t('Notes')"   show-overflow-tooltip>
                    <template slot-scope="scope">
                        <span>{{scope.row.notes ?scope.row.notes :'-' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" :label="$t('Action')"  align="left" width="160px">
                    <template slot-scope="scope">
                        <span v-if="scope.row.status==='Invalid'">
                            <el-button class="text" type="text" >
                            </el-button>
                            <el-button type="text" disabled>
                                <img class="edit" src="@/assets/edit_disable.png">
                            </el-button>
                            <el-button  type="text" @click="toDownload(scope.row.serialNumber)">
                                <img class="edit" src="@/assets/download.png">
                            </el-button>
                        </span>
                        <span  v-if="scope.row.status==='Unpublished'">
                            <el-button class="text" type="text"  @click="toPublish(scope.row)">
                                {{$t('publish')}}
                            </el-button>
                            <el-button type="text"  @click="toEdit(scope.row)">
                                <img class="edit" src="@/assets/edit_click.png">
                            </el-button>
                        </span>
                         <span  v-if="scope.row.status==='Valid'" >
                             <el-button class="text" type="text" @click="toInvalidate(scope.row)">
                                {{$t('revoke')}}
                            </el-button>
                            <el-button type="text"  @click="toEdit(scope.row)">
                                <img class="edit" src="@/assets/edit_click.png">
                            </el-button>
                            <el-button  type="text" @click="toDownload(scope.row.serialNumber)">
                                <img class="edit" src="@/assets/download.png">
                            </el-button>
                        </span>
                        <span  v-if="scope.row.status==='Revoked'" >
                             <el-button class="text" type="text" >
                            </el-button>
                            <el-button type="text" disabled>
                                <img class="edit" src="@/assets/edit_disable.png">
                            </el-button>
                            <el-button  type="text" disabled="">
                            </el-button>
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
    <el-dialog :visible.sync="adddialog" class="add-dialog" width="600px" :show-close="true" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
            <div class="title">
                <span v-if="type==='add'">{{$t('Add Certificate')}}</span>
                <span v-else> {{$t('Edit Certificate')}} </span>
            </div>
            <el-form label-position="left" ref="addfrom" class="inner-form" :rules="editRules"  label-width="154px"  :model="addfrom">
                <el-form-item v-if="addfrom.status=='Valid'" label="Certificate ID" prop="serialNumber" >
                    <span style="font-size: 16px;color:#4E5766">{{addfrom.serialNumber}}</span>
                </el-form-item>
                <el-form-item label="" prop="typeId" >
                    <span slot="label">
                        <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        <span>{{$t('Certificate Type')}}</span>
                    </span>
                    <span v-if="addfrom.status==='Valid'">{{addfrom.certificateType}}</span>
                    <el-select v-else @focus="$refs['addfrom'].clearValidate('typeId')" v-model="addfrom.typeId" filterable :placeholder="$t('Certificate Type')">
                        <el-option
                            v-for="item in certiTypeSelect"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                    <el-button v-if="addfrom.status==='Unpublished' || type==='add'"  @click="toAddType" type="text">
                        <img class="img" src="@/assets/edit_click.png">
                    </el-button>
                </el-form-item>
                <el-form-item label="Validity" prop="validity" >
                    <span slot="label">
                        <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        <span>{{$t('Validity')}}</span>
                    </span>
                    <span v-if="addfrom.status==='Valid'">{{addfrom.validity}}</span>
                    <el-date-picker v-else
                        v-model="addfrom.validity"
                        @focus="$refs['addfrom'].clearValidate('validity')"
                        type="daterange"
                        range-separator="~"
                        :start-placeholder="$t('start')"
                        value-format="yyyy-MM-dd"
                        :picker-options="pickerOptions"
                        :end-placeholder="$t('end')">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="Institution" prop="institution" >
                    <span slot="label">
                        <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        <span>{{$t('Institution')}}</span>
                    </span>
                    <span v-if="addfrom.status==='Valid'">{{addfrom.institution}}</span>
                    <el-select v-else v-model="addfrom.institution" @change="togetSite" filterable @focus="$refs['addfrom'].clearValidate('institution')"  :placeholder="$t('Institution')">
                        <el-option
                            v-for="item in institutionsSelectListAll"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value" ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item v-if="addfrom.status=='Valid'" :label="$t('institution DNS')" prop="dnsName" >
                    <span style="font-size: 16px;color:#4E5766">{{addfrom.dnsName}}</span>
                </el-form-item>
                <el-form-item :label="$t('Site Authority')" prop="siteAuthority" >
                    <span v-if="addfrom.status==='Valid'">{{addfrom.siteAuthority instanceof Array?addfrom.siteAuthority.join():addfrom.siteAuthority}}</span>
                    <el-select v-else multiple v-model="addfrom.siteAuthority" filterable :disabled="siteSelect.length===0" :placeholder="$t('Site Authority')">
                        <el-option
                            v-for="item in siteSelect"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item :label="$t('Notes')" prop="notes" >
                    <el-input
                        type="textarea"
                        autosize
                        :rows="3"
                        :placeholder="$t('Notes')"
                        show-word-limit
                        v-model="addfrom.notes">
                    </el-input>
                </el-form-item>
            </el-form>
      </div>
       <div class="dialog-foot">
          <el-button class="ok-btn" type="primary" @click="toSave">{{$t('m.common.save')}}</el-button>
          <el-button class="ok-btn" type="info" @click="toCancel">{{$t('m.common.cancel')}}</el-button>
        </div>
    </el-dialog>
    <!-- 删除弹框 -->
    <el-dialog :visible.sync="deletedialog" class="auto-dialog" :show-close="true" width="500px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
        <div class="line-text-one">
          {{$t('m.certificate.sureTodo',{type:$t(`m.common.${dialogType}`)})}}
        </div>
        <div class="line-text-two" v-if="dialogType === 'publish'">
            {{$t('m.certificate.certificateAfterPublished')}}
        </div>
        <div class="line-text-two" v-else>
            {{$t('m.site.cantUndo')}}
        </div>
        <div class="dialog-foot">
          <el-button class="ok-btn" type="primary" @click="toSure">{{$t('m.common.sure')}}</el-button>
          <el-button class="ok-btn" type="info" @click="deletedialog=false">{{$t('m.common.cancel')}}</el-button>
        </div>
      </div>
    </el-dialog>
    <certi-add-type ref="certiAddType"/>
  </div>

</template>

<script>
// 国际化
import { getCertiCate,
    publishCert,
    invalidateCert,
    certiType,
    addCertificate,
    updateCertificate,
    downloadCertificate } from '@/api/setting'
import { institutionsListDropdown, siteListAll, institutionsAll } from '@/api/federated'
import certiAddType from './certiAddType'
import moment from 'moment'
import { mapGetters } from 'vuex'
import FileSaver from 'file-saver'
const local = {
    zh: {
        'Certificate ID': '证书ID',
        'Certificate Type': '证书类型',
        'Validity': '有效期',
        'Institution': '站点机构',
        'Site Authority': '授权站点',
        'Create Time': '创建时间',
        'Update Time': '更新时间',
        'Status': '证书状态',
        'Notes': '备注',
        'Action': '操作',
        'publish': '发布',
        'revoke': '吊销',
        'Search for Certificate ID or Site': '搜索证书ID或站点',
        'Add Certificate': '添加证书',
        'Edit Certificate': '编辑证书',
        'institution DNS': '机构域名',
        'start': '开始时间',
        'end': '结束时间'
    },
    en: {
        'Certificate ID': 'Certificate ID',
        'Certificate Type': 'Certificate Type',
        'Validity': 'Validity',
        'Institution': 'Institution',
        'Site Authority': 'Site Authority',
        'Create Time': 'Create Time',
        'Update Time': 'Update Time',
        'Status': 'Status',
        'Notes': 'Notes',
        'Action': 'Action',
        'publish': 'publish',
        'revoke': 'revoke',
        'Search for Certificate ID or Site': 'Search for Certificate ID or Site',
        'Add Certificate': 'Add Certificate',
        'Edit Certificate': 'Edit Certificate',
        'institution DNS': 'institution DNS',
        'start': 'start',
        'end': 'end'

    }
}

export default {
    name: 'access',
    components: { certiAddType },
    // filters: {
    //     dateFormat(value) {
    //         return value ? moment(value).format('YYYY-MM-DD HH:mm:ss') : '--'
    //     }
    // },
    data() {
        return {
            type: '', // 弹框类型
            adddialog: false,
            deletedialog: false,
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            certificateData: [],
            historydata: [], // 历史记录
            addSuccessText: '', // 连接
            data: {
                pageNum: 1,
                pageSize: 20
            },
            addfrom: { },
            institutionsSelectList: [], // 机构下拉选项
            institutionsSelectListAll: [], // 新增弹窗内全部机构
            certiTypeSelect: [], // 类型下拉选项
            addCertiTypeList: [],
            siteSelect: [], // 站点下拉选项
            dialogType: 'publish', // 弹框类型
            params: '', // 弹框传入id
            editRules: {
                typeId: [{ required: true, message: ' ', trigger: 'bulr' }],
                validity: [{ required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        if (!value) {
                            callback(new Error(''))
                        } else if (value instanceof Array && moment(value[1]).valueOf() < moment(moment(+new Date()).format('YYYY-MM-DD')).valueOf()) {
                            callback(new Error(''))
                        } else {
                            callback()
                        }
                    } }],
                institution: [{ required: true, message: ' ', trigger: 'bulr' }]
            },
            tepmtime: '', // 临时选中时间
            pickerOptions: {
                disabledDate: (time) => {
                    return this.addfrom.validity
                        ? time.getTime() < new Date(this.tepmtime[0]).getTime() - (24 * 60 * 60 * 1000)
                        : time.getTime() < (new Date().getTime()) - (24 * 60 * 60 * 1000)
                }
            }
        }
    },
    computed: {
        ...mapGetters(['getInfo', 'loginName']),
        status() {
            return [
                {
                    value: 'Valid',
                    label: this.$t('m.certificate.valid')
                }, {
                    value: 'Invalid',
                    label: this.$t('m.certificate.invalid')
                }, {
                    value: 'Revoked',
                    label: this.$t('m.certificate.revoked')
                }, {
                    value: 'Unpublished',
                    label: this.$t('m.certificate.unpublished')
                }
            ]
        }
    },
    created() {
        this.initList()
        this.init()
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
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
            getCertiCate(this.data).then(res => {
                this.certificateData = res.data.records
                this.total = res.data.total
            })
        },
        init() {
            this.institutionsSelectList = []
            this.institutionsSelectListAll = []
            this.certiTypeSelect = []
            institutionsListDropdown().then(res => {
                res.data.institutionsSet.forEach(element => {
                    let obj = {}
                    obj.label = obj.value = element
                    this.institutionsSelectList.push(obj)
                })
            })
            institutionsAll().then(res => {
                this.institutionsSelectListAll = res.data.map(element => {
                    return {
                        label: element,
                        value: element
                    }
                })
            })
            certiType().then(res => {
                this.addCertiTypeList = res.data
                res.data.forEach(item => {
                    let obj = {}
                    obj.label = item.typeName
                    obj.value = item.id
                    this.certiTypeSelect.push(obj)
                })
            })
        },
        // 获取机构站点
        togetSite(val) {
            let data = {
                institutions: val
            }
            this.siteSelect = []
            this.addfrom.siteAuthority = []
            siteListAll(data).then(res => {
                this.siteSelect = res.data && res.data.map(item => {
                    return {
                        value: item,
                        label: item
                    }
                })
            })
        },
        // 取搜索
        toSearch() {
            this.currentPage = 1
            this.data.pageNum = 1
            this.initList()
        },
        // 添加弹框
        toAdd() {
            this.type = 'add'
            this.adddialog = true
            this.addfrom = {
                siteAuthority: [],
                validity: [moment(new Date()).format('YYYY-MM-DD'), moment(new Date().getTime() + 5 * 365 * 24 * 60 * 60 * 1000).format('YYYY-MM-DD')]
            }
            this.tepmtime = this.addfrom.validity // 临时选中时间
        },
        // 编辑弹框
        toEdit(row) {
            this.togetSite(row.institution)
            this.type = 'edit'
            this.adddialog = true
            this.addfrom = { ...row }
            this.tepmtime = this.addfrom.validity = row.status === 'Valid' ? this.addfrom.validity : [row.validity.split('~')[0], row.validity.split('~')[1]]
            this.addfrom.siteAuthority = this.addfrom.siteAuthority.length > 0 ? this.addfrom.siteAuthority.split(',') : ''
        },
        // 确定添加
        toSave() {
            this.addfrom.organization = this.getInfo.name
            this.$refs['addfrom'].validate((valid) => {
                if (valid) {
                    if (this.type === 'add') {
                        this.addfrom.siteAuthority = this.addfrom.siteAuthority && this.addfrom.siteAuthority.join()
                        this.addfrom.validity = `${this.addfrom.validity[0]}~${this.addfrom.validity[1]}`
                        addCertificate(this.addfrom).then(res => {
                            this.initList()
                            this.adddialog = false
                        }).catch(res => {
                            this.addfrom.siteAuthority = this.addfrom.siteAuthority.length > 0 ? this.addfrom.siteAuthority.split(',') : ''
                            this.$message.error(res.msg)
                        })
                    } else if (this.type === 'edit') {
                        this.addfrom.siteAuthority = this.addfrom.siteAuthority && this.addfrom.siteAuthority.join()

                        this.addfrom.validity = this.addfrom.status === 'Valid' ? this.addfrom.validity : `${this.addfrom.validity[0]}~${this.addfrom.validity[1]}`
                        updateCertificate(this.addfrom).then(res => {
                            this.initList()
                            this.adddialog = false
                        }).catch(res => {
                            this.addfrom.siteAuthority = this.addfrom.siteAuthority.length > 0 ? this.addfrom.siteAuthority.split(',') : ''
                            this.$message.error(res.msg)
                        })
                    }
                }
            })
        },
        toCancel() {
            this.adddialog = false
            this.$refs['addfrom'].resetFields()
        },
        toPublish(row) {
            this.deletedialog = true
            this.dialogType = 'publish'
            this.params = row.id
        },
        toInvalidate(row) {
            this.deletedialog = true
            this.dialogType = 'revoke'
            this.params = row.serialNumber
        },
        // 确实删除
        toSure() {
            if (this.dialogType === 'publish') {
                let data = { id: this.params }
                publishCert(data).then(res => {
                    this.initList()
                    this.deletedialog = false
                })
            } else {
                let data = { serialNumber: this.params }
                invalidateCert(data).then(res => {
                    this.initList()
                    this.deletedialog = false
                })
            }
        },
        toAddType() {
            this.$refs['certiAddType'].entrancesSelect = []// 清空数据
            this.$refs['certiAddType'].params = [] // 清空入参
            this.$refs['certiAddType'].addTypeDialog = true
            this.addCertiTypeList.forEach(item => {
                item.show = false
                this.$refs['certiAddType'].entrancesSelect.push(item)
            })
        },
        // 翻页
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        // 下载证书
        toDownload(serialNumber) {
            let data = {
                serialNumber
            }
            downloadCertificate(data).then(res => {
                var blob = new Blob([res.data])
                const fileName = res.headers['content-disposition'].split('=')[1]
                FileSaver.saveAs(blob, fileName)
            }).catch(() => {
                this.$message.error(this.$t('m.certificate.cannotConnectToServer'))
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/certificate.scss';
</style>
