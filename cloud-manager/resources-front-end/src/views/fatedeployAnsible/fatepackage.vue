<template>
    <div>
        <div class="row-content">
            <el-radio-group class="radio" v-model="activeName" @change="handleClick">
                <el-radio-button label="FATE package acquisition"></el-radio-button>
                <el-radio-button label="Log"></el-radio-button>
            </el-radio-group>
        </div>
        <div v-show="activeName==='FATE package acquisition'" class="deploying-body">
            <span  v-if='tableData.length>0 && reacquireShow'>
                <div class="row-activa">
                    <div class="modify">
                        <!-- <span v-if='radio===1' class="action-control">From Local upload</span>
                        <span v-else class="action-control">From Automatic acquisition</span> -->
                        <span v-if="disReacquire" @click="reacquire"  class="action-modify">Reacquire</span>
                        <span  v-else style="opacity:0.75;cursor:not-allowed;" class="action-modify">Reacquire</span>
                    </div>
                </div>
                <div class="table" >
                    <el-table
                    :data="tableData"
                    header-row-class-name="tableHead"
                    header-cell-class-name="tableHeadCell"
                    cell-class-name="tableCell"
                    height="100%">
                        <el-table-column type="index"  :label="$t('Index')" width="70"></el-table-column>
                        <el-table-column prop="module" :label="$t('Item')" >
                            <template slot-scope="scope">
                                <span> {{scope.row.module}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="description" :label="$t('Description')">
                            <template slot-scope="scope">
                                <span>{{scope.row.description}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="version" :label="$t('Version')" >
                            <template slot-scope="scope">
                                <span>{{scope.row.version }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="size" :label="$t('Size')">
                            <template slot-scope="scope">
                                <span>{{scope.row.size }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="time" :label="$t('Time')" >
                            <template slot-scope="scope">
                                <span>{{scope.row.time |dateFormat }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="deployStatus.desc" align="center" :label="$t('Status')">
                            <template slot-scope="scope">
                                <span v-if="scope.row.status.code===0"  type="text">
                                    <img src="@/assets/waiting.png" alt="" >
                                </span>
                                <span v-if="scope.row.status.code===1" type="text">
                                    <img src="@/assets/success.png" alt="">
                                </span>
                                <span v-if="scope.row.status.code===2"  type="text">
                                    <img src="@/assets/failed.png" alt="" >
                                </span>
                                <span v-if="scope.row.status.code===3"  type="text">
                                    <i style="color:#217AD9;font-size:18px" class="el-icon-loading"></i>
                                </span>
                            </template>
                        </el-table-column>
                        <!-- <el-table-column prop="duration" label="Action"  align="right" >
                            <template slot-scope="scope">
                                <el-button v-if="scope.row.status.code ===2" @click="toRetry(scope.row)"  type="text">
                                    Retry
                                </el-button>
                                <el-button v-else disabled  type="text">
                                    Retry
                                </el-button>
                            </template>
                        </el-table-column> -->
                    </el-table>
                </div>
                <div  class="btn">
                    <el-button v-if="btntype==='primary'" type="primary" @click="toNext" >Next</el-button>
                    <el-button v-else type="info" disabled >Next</el-button>
                </div>
            </span>
            <span v-else>
                <div class="row-activa">
                </div>
                <div class="table">
                    <div class="acquire-fate">
                        <el-button type="text" @click="toShowacquire" >Acquire FATE installation package</el-button>
                    </div>
                </div>
            </span>

        </div>
        <div  v-if="activeName==='Log'" class="deploying-body">
            <log  ref="log" />
        </div>
        <el-dialog
            :visible.sync="acquireVisible"
            class="package-dialog"
            width="700px">
            <div class="dialog-body">
                <div class="body-title">Acquire FATE installation package</div>
                <div class="body-radio">
                    <el-radio-group v-model="radio">
                        <el-radio :label="1">Local upload</el-radio>
                        <el-radio :label="2">Auto-acquire</el-radio>
                    </el-radio-group>
                </div>
                <div class="body-select" v-if='radio===1'>
                    <!-- <div class="plus">
                        <i class="el-icon-plus"></i>
                    </div> -->
                     <el-form :model="uploadForm" :rules="rules"  ref="uploadForm"  >
                        <div class="text">Please select a machine</div>
                        <el-form-item label=""  prop="ip">
                            <el-select v-model="uploadForm.ip" placeholder="">
                                <el-option
                                v-for="item in nodeData"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <div class="text">Please enter the installation package path</div>
                        <el-form-item  label="" prop="path" >
                            <el-input  v-model="uploadForm.path" ></el-input>
                        </el-form-item>
                    </el-form>
                </div>
                <div class="body-select" v-else>
                    <div class="text">
                        <span>Please select FATE version</span>
                    </div>
                    <el-form :model="versionForm" :rules="rules"  ref="versionForm"  >
                        <el-form-item label=""  prop="fateVersion">
                            <el-select v-model="versionForm.fateVersion" placeholder="">
                                <el-option
                                    v-for="item in version"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-form>
                    <div class="info" >
                        Make sure the network is available.
                    </div>
                </div>
            </div>
            <div  class="dialog-footer" v-if='radio===1'>
                <el-button type="primary" @click="toUpload">Start</el-button>
                <el-button type="info" @click="tocancel">Cancel</el-button>
            </div>
            <div  class="dialog-footer" v-else>
                <el-button type="primary" @click="toAcquire">Start</el-button>
                <el-button type="info" @click="tocancel">Cancel</el-button>
            </div>
        </el-dialog>
        <el-dialog
            :visible.sync="failedVisible"
            class="failed-dialog"
            width="700px">
            <div class="body-title">Failed to acquire, please try again.</div>
            <div  class="dialog-footer">
                <el-button type="primary" @click="failedVisible = false">OK</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
import { mapGetters } from 'vuex'
import moment from 'moment'
import event from '@/utils/event'
import { toacquire, getNodeList, toupload, ansibleClick, ansibleLoge, commitNext, fatepackagelist } from '@/api/fatedeployAnsible'

import log from './log'
// 国际化
const local = {
    zh: {
        'Item': '项目',
        'Description': '描述',
        'Version': '版本',
        'Size': '文件大小',
        'Time': '上传时间',
        'Status': '状态'
    },
    en: {
        'Item': 'Item',
        'Description': 'Description',
        'Version': 'Version',
        'Size': 'Size',
        'Time': 'Time',
        'Status': 'Status'

    }
}
export default {
    name: 'pulltable',
    props: {
        formInline: {
            type: Object,
            default: function () {
                return { }
            }
        },
        currentSteps: {
            type: Object,
            default: function () {
                return { }
            }
        },
        page: {
            type: Number,
            default: function () {
                return 4
            }
        },
        currentpage: {
            type: Number,
            default: function () {
                return 0
            }
        }
    },
    filters: {
        dateFormat(vaule) {
            return vaule > 0 ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '- -'
        }
    },
    data() {
        return {
            reacquireShow: true,
            uploadForm: {}, // 选中上传路径
            versionForm: {},
            tableData: [],
            disReacquire: true,
            radio: 1, // Local upload
            acquireVisible: false,
            failedVisible: false,
            activeName: 'FATE package acquisition',
            btntype: 'info',
            startStatus: 1,
            disabledStart: true,
            time: null, // 定时器
            fateVersion: '',
            commitVersion: '',
            nodeData: [],
            rules: {
                ip: [{ required: true, message: 'The field is required !', trigger: 'change' }],
                path: [{ required: true, message: 'The field is required !', trigger: 'change' }],
                fateVersion: [{ required: true, message: 'The field is required !', trigger: 'change' }]
            }
        }
    },
    watch: {
        page: {
            handler: function(val) {
                if (val === 4) {
                    this.lessTime()
                } else {
                    window.clearTimeout(this.time)
                }
            },
            immediate: true
        }
    },
    components: { log },
    computed: {
        ...mapGetters(['organization', 'partyId', 'version'])
    },
    beforeDestroy() {
        window.clearTimeout(this.time)
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    mounted() {
        event.$on('myFunAnsible', (msg) => {
            this.disReacquire = msg
        })
    },
    methods: {
        async initiInstallList() {
            let data = {
                partyId: this.formInline.partyId,
                productType: 1
            }
            let res = await fatepackagelist(data)
            this.tableData = res.data ? res.data.list : []
            this.commitVersion = res.data ? res.data.fateVersion : ''
            if (res.data && res.data.list.length > 0) {
                this.reacquireShow = true
            }
            return this.tableData
        },
        // 重来
        reacquire() {
            this.reacquireShow = false
            this.uploadForm = {}
            this.versionForm = {}
            if (this.radio === 1) {
                this.$refs['uploadForm'].resetFields()
            } else {
                this.$refs['versionForm'].resetFields()
            }
        },
        // 显示弹框
        toShowacquire() {
            this.acquireVisible = true
            this.nodeList()
        },
        // 选择版本
        toAcquire() {
            this.$refs['versionForm'].validate((valid) => {
                if (valid) {
                    let data = {
                        fateVersion: this.versionForm.fateVersion,
                        partyId: this.formInline.partyId,
                        siteName: this.formInline.siteName
                    }
                    toacquire(data).then(res => {
                        this.btntype = 'primary'
                        this.$parent.currentpage = 4
                        this.$parent.currentSteps = {
                            acquisiPrepare: true,
                            acquisiFinish: false,
                            instllPrepare: false,
                            instllFinish: false,
                            autoPrepare: false,
                            autoFinish: false
                        }
                        this.acquireVisible = false
                        this.reacquireShow = false
                        this.lessTime()
                    })
                }
            })
        },

        // 地址上传
        toUpload() {
            this.$refs['uploadForm'].validate((valid) => {
                if (valid) {
                    let data = {
                        ip: this.uploadForm.ip,
                        path: this.uploadForm.path,
                        partyId: this.formInline.partyId,
                        siteName: this.formInline.siteName
                    }
                    toupload(data).then(res => {
                        this.btntype = 'primary'
                        this.currentpage = 4
                        this.acquireVisible = false
                        this.reacquireShow = false
                        this.currentSteps.systemFinish = true
                        this.currentSteps.ansibleFinish = true
                        this.currentSteps.acquisiPrepare = true
                        this.lessTime()
                    })
                }
            })
        },
        // 取消
        tocancel() {
            this.lessTime()
            this.acquireVisible = false
            if (this.radio === 1) {
                this.$refs['uploadForm'].resetFields()
            } else {
                this.$refs['versionForm'].resetFields()
            }
        },
        // 下一步
        toNext() {
            let data = {
                fateVersion: this.commitVersion,
                partyId: this.formInline.partyId,
                productType: 1
            }
            commitNext(data).then(res => {
                let dataClick = {
                    partyId: this.formInline.partyId,
                    productType: 1,
                    clickType: 5
                }
                ansibleClick(dataClick).then(res => {
                    this.$parent.togetpages()
                })
            })
        },
        handleClick() {
            if (this.activeName === 'Log') {
                this.$refs['log'].data.all = []
                this.$refs['log'].dataList = []
                this.disabledStart = true
                window.clearTimeout(this.time)
                let dataparams = ['debug', 'error', 'info', 'waring']
                dataparams.forEach(item => {
                    let data = {
                        level: item
                    }
                    ansibleLoge(data).then(res => {
                        this.$refs['log'].data[item] = res.data.content || []
                        res.data.content && res.data.content.forEach((item) => {
                            this.$refs['log'].dataList.push(item)
                        })
                    })
                    this.$refs['log'].data.all = this.$refs['log'].dataList
                })
            } else {
                if (this.page !== this.currentpage) {
                    this.btntype = 'info'
                }
            }
        },
        lessTime() {
            this.initiInstallList().then(res => {
                if (res.length > 0 && res.every(item => item.status.code === 0)) { // 已经拉取，等待安装
                    this.btntype = 'info'
                    this.disabledStart = false
                    this.startStatus = 1
                } else if (res.length > 0 && res.every(item => item.status.code === 1)) { // 已经安装、成功
                    if (this.page !== this.currentpage) {
                        this.btntype = 'info'
                    } else {
                        this.btntype = 'primary'
                    }
                    this.disabledStart = true
                    this.currentSteps.acquisiFinish = true
                } else if (res.length > 0 && res.every(item => item.status.code === 2)) { // 安装失败
                    this.btntype = 'info'
                    this.startStatus = 2
                    this.disabledStart = false
                } else if (res.length === 0) {
                    this.btntype = 'info'
                } else {
                    this.btntype = 'info'
                    this.disabledStart = true
                    this.time = setTimeout(() => {
                        this.lessTime()
                    }, 1500)
                }
            })
        },
        // node下拉
        nodeList() {
            this.nodeData = []
            getNodeList().then(res => {
                res.data.forEach(item => {
                    let obj = {}
                    obj.value = item.ip
                    obj.label = item.ip
                    obj.status = item.status
                    this.nodeData.push(obj)
                })
            })
        },
        toRetry() {

        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/deployansible.scss';
</style>
