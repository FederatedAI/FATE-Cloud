<template>
  <div class="site-detail-box">
    <div class="site-detail">
        <div class="Basic">
            <div class="info">
            <span>{{$t('Basic Info')}}</span>
            <div class="info-del">
                <span class="info-sta">{{$t('Status')}}</span>
                <span class="sta-action">{{form.status ? form.status.desc:''}}</span>
            </div>
            </div>
            <el-form ref="form" :model="form" label-position="left" label-width="180px">
            <el-row :gutter="140">
                <el-col :span="12">
                <el-form-item :label="$t('Site Name')">
                    <span class="info-text">{{form.siteName}}</span>
                </el-form-item>
                <el-form-item :label="$t('Institution')">
                    <span class="info-text">{{form.institutions}}</span>
                </el-form-item>
                <el-form-item  :label="$t('Role')" >
                    <span class="info-text">{{form.role?form.role.desc:form.role}}</span>
                </el-form-item>
                <el-form-item label="Federation key">
                    <span class="info-text">{{form.appKey}}</span>
                </el-form-item>
                <el-form-item label="Secret Key">
                    <el-popover placement="top" width="400" trigger="hover" :content="form.appSecret">
                    <span slot="reference" class="link-text">{{form.appSecret}}</span>
                    </el-popover>
                </el-form-item>
                </el-col>
                <el-col :span="12">
                <el-form-item :label="$t('Status')" >
                    <span class="info-text">{{form.status?form.status.desc:form.status}}</span>
                </el-form-item>
                <el-form-item :label="$t('Party ID')" >
                    <span class="info-text">{{form.partyId}}</span>
                </el-form-item>
                <el-form-item  :label="$t('Creation Time')">
                    <span class="info-text">{{form.createTime | dateFormat}}</span>
                </el-form-item>
                <el-form-item  :label="$t('Activation Time')" >
                    <span class="info-text">{{form.acativationTime | dateFormat}}</span>
                </el-form-item>
                <el-form-item  :label="$t('Registration Link')">
                    <el-popover
                    placement="top"
                    width="400"
                    trigger="hover"
                    :content="form.registrationLink"
                    >
                    <span slot="reference" class="link-text">{{form.registrationLink}}</span>
                    </el-popover>
                </el-form-item>
                </el-col>
            </el-row>
            </el-form>
        </div>
        <div class="Basic">
            <div class="info">
                <span>{{$t('Network configuration')}}</span>
                <span  v-if="role.roleName==='Admin' || role.roleName==='Developer or OP'">
                    <div class="info-del" v-if="form.status && form.status.code !== 3">
                        <img src="@/assets/edit_click.png" v-if="editSubmitted === 1" @click="toEdit" class="edit" alt />
                        <el-button v-if="editSubmitted === 2" @click="submit" :disabled="tosubmit" type="primary">Submit</el-button>
                        <el-button v-if="editSubmitted === 2" @click="cancel" type="info">Cancel</el-button>
                        <span v-if="editSubmitted === 3" class="under">
                            <img src="@/assets/under.png" style="margin-right:5px" alt />
                            <span>Under Review</span>
                        </span>
                    </div>
                </span>
            </div>
            <el-form ref="form" :model="form" label-position="left" :rules="rules" label-width="230px">
            <el-row :gutter="140">
                <el-col :span="12">
                    <el-form-item :label="$t('Network Acess Entrances')" style="height:100%;" prop="networkAccessEntrances" >
                        <span class="info-text" v-if="editSubmitted!==2 &&  form.networkAccessEntrances">
                            <div v-for="(item,index) in form.networkAccessEntrances.split(';')" :key="index" >{{item}}</div>
                        </span>
                        <el-input
                            v-if="editSubmitted===2"
                            @focus="addShow('entrances')"
                            @blur="cancelValid('networkAccessEntrances')"
                            :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'entranceswarn': networkAccessEntranceswarnshow }"
                            v-model="form.networkAccessEntrances"
                            placeholder >
                            <i slot="suffix" @click="addShow('entrances')" class="el-icon-edit plus" />
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item :label="$t('Network Acess Exits')" style="height:100%;" prop="networkAccessExits" >
                        <span class="info-text" v-if="editSubmitted!==2 && form.networkAccessExits">
                            <div v-for="(item,index) in form.networkAccessExits.split(';')" :key="index" >{{item}}</div>
                        </span>
                        <el-input
                            v-if="editSubmitted===2"
                            @focus="addShow('exit')"
                            @blur="cancelValid('networkAccessExits')"
                            :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': networkAccessExitswarnshow }"
                            v-model="form.networkAccessExits"
                            placeholder >
                            <i slot="suffix" @click="addShow('exit')" class="el-icon-edit plus" />
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            </el-form>
        </div>
        <div class="Basic">
            <div class="info">
                <span>{{$t('System version')}}</span>
                <span v-if="role.roleName==='Admin' || role.roleName==='Developer or OP'">
                    <div class="info-del" v-if="form.status && form.status.code !== 3">
                        <span v-if="form.versionEditStatus.code===2">
                            <img src="@/assets/edit_click.png" v-if="editVersion === 1" @click="toEditVersion" class="edit" alt />
                            <el-button v-if="editVersion === 2" @click="versiondialog=true"  type="primary">Submit</el-button>
                            <el-button v-if="editVersion === 2" @click="toCanceleditVersion" type="info">Cancel</el-button>
                        </span>
                        <span v-else>
                             <img src="@/assets/edit_disable.png" style="cursor:not-allowed" class="edit" alt />
                        </span>

                    </div>
                </span>
            </div>
            <el-form ref="form" :model="form" label-position="left" :rules="rules" >
                 <!-- fateVersion版本下拉 -->
                <div class="system-label" >
                    <span class="system-title">{{$t('FATE version')}}</span>
                    <el-tooltip effect="dark" placement="bottom">
                        <div style="font-size:14px" slot="content">
                            <div>including FATE-Board, FATE-Flow</div>
                        </div>
                        <i class="el-icon-info icon-info"></i>
                    </el-tooltip>
                    <span v-if="editVersion === 1" class="info-text">{{form.fateVersion}}</span>
                    <span  v-if="editVersion === 2" class="info-text-select" >
                        <el-select v-model="fateVersion" @change="togetcomponentVersion" placeholder="">
                            <el-option
                                v-for="item in version"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            ></el-option>
                        </el-select>
                    </span>
                </div>
                <div class="label" >
                    <span class="label-title" style="color:#4E5766;font-weight:bold">{{$t('FATE Component')}}</span>
                    <span class="label-version" style="color:#4E5766;font-weight:bold" >{{$t('Version')}}</span>
                    <span class="label-ip" style="color:#4E5766;font-weight:bold">IP </span>
                </div>
                <el-form-item label="" style="height:100%;">
                    <div class="label" v-for="(item, index) in form.componentVersion" :key="index">
                        <span class="label-title">{{item.label}} </span>
                        <span class="label-version" v-if="editVersion === 1" >{{item.version}}</span>
                        <span class="label-version" v-else>
                                <el-select v-model="formVersion[item.label]" >
                                <el-option
                                    v-for="item in item.setl"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                ></el-option>
                            </el-select>
                        </span>
                        <span class="label-ip">{{item.ip}} </span>
                    </div>
                </el-form-item>
            </el-form>
        </div>
        <div  class="Basic"  v-if="role.roleName==='Admin'">
            <div class="info">
                <span>{{$t('User List')}}</span>
            </div>
            <el-table
                :data="siteList"
                class="site-table"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                tooltip-effect="light">
                <el-table-column prop="userName" :label="$t('Name')" width="200" show-overflow-tooltip></el-table-column>
                <el-table-column prop="role" :label="$t('Role')"  width="200"></el-table-column>
                <el-table-column prop="permission" :label="$t('Permission')" ></el-table-column>
            </el-table>
        </div>

    </div>
    <!-- 审批完成弹框 -->
    <el-dialog :visible.sync="noticedialog" :close-on-click-modal="false" :close-on-press-escape="false" class="site-delete-dialog" width="774px">
      <div class="line-text-one">{{noticedesc}}</div>
      <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" @click="notice">OK</el-button>
      </div>
    </el-dialog>
    <!-- 确定更改version版本弹框 -->
    <el-dialog :visible.sync="versiondialog" :close-on-click-modal="false" :close-on-press-escape="false" class="site-delete-dialog" width="774px">
        <div class="line-one">Are you sure you want to change system version?</div>
        <div class="line-text-two">The results will be synchronized to cloud manager.</div>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="sureVersion">Sure</el-button>
            <el-button class="ok-btn" type="info" @click="versiondialog=false">cancel</el-button>
        </div>
    </el-dialog>
    <!-- 确定更改ip弹框 -->
    <el-dialog
        :visible.sync="changedialog"
        v-if="networkAccessEntrancesOld && networkAccessExitsOld"
        class="ip-delete-dialog"
        width="700px">
        <div class="line-text-one">Are you sure you want to</div>
        <div class="line-text-one" v-if="form.networkAccessEntrances!==networkAccessEntrancesOld">change the Network Access Entrances</div>
        <div class="line-text" v-if="form.networkAccessEntrances!==networkAccessEntrancesOld">
            <div class="entrances">
            <div class="rigth-box" style="margin-right: 70px">
                <div class="from">from</div>
                <div class="text">
                <span
                    v-for="(item, index) in networkAccessEntrancesOld.split(';')"
                    :key="index"
                >{{item}}</span>
                </div>
            </div>
            <div class="rigth-box" style="margin-left: 70px">
                <div class="from">to</div>
                <div class="text">
                <span
                    v-for="(item, index) in form.networkAccessEntrances.split(';')"
                    :key="index"
                >{{item}}</span>
                </div>
            </div>
            </div>
        </div>
        <div style="margin:36px 0;font-size:24px;color:#2D3642" v-if="form.networkAccessExits!==networkAccessExitsOld" >and change the Network Access Exits</div>
        <div class="line-text" v-if="form.networkAccessExits!==networkAccessExitsOld">
            <div class="entrances">
            <div class="rigth-box" style="margin-right: 70px">
                <div class="from">from</div>
                <div class="text">
                <span v-for="(item, index) in networkAccessExitsOld.split(';')" :key="index">{{item}}</span>
                </div>
            </div>
            <div class="rigth-box" style="margin-left: 70px">
                <div class="from">to</div>
                <div class="text">
                <span
                    v-for="(item, index) in form.networkAccessExits.split(';')"
                    :key="index"
                >{{item}}</span>
                </div>
            </div>
            </div>
        </div>
        <div class="line-text-two">You will get the change results after cloud manager reviews.</div>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="okAction">Sure</el-button>
            <el-button class="ok-btn" style="margin-left:20px" type="info" @click="changedialog=false" >Cancel</el-button>
        </div>
    </el-dialog>

    <sitedetailip ref="sitedetailip" />
  </div>
</template>

<script>
import {
    getSiteInfo,
    update,
    readmsg,
    getmsg,
    siteInfoList,
    getClusterList,
    getFateboardList,
    getFateflowList,
    getFateservingList,
    getMysqlList,
    getNodeList,
    getrollsiteList,
    getcomponentversion,
    updateVersion
} from '@/api/home'
import moment from 'moment'
import sitedetailip from './sitedetailip'
import { mapGetters } from 'vuex'
// 国际化
const local = {
    zh: {
        'Basic Info': '基本信息',
        'Status': '状态',
        'Joined': '已加入',
        'Delete': '已删除',
        'Site Name': '站点名称',
        'Institution': '站点机构',
        'Party ID': '站点ID',
        'Role': '站点角色',
        'Creation Time': '创建时间',
        'Activation Time': '激活时间',
        'Registration Link': '注册链接',
        'Network configuration': '网关设置',
        'Network Acess Entrances': '网关入口',
        'Network Acess Exits': '网关出口',
        'System version': '系统版本',
        'FATE version': 'FATE版本',
        'FATE Component': 'FATE服务组件',
        'Version': '版本',
        'User List': '站点用户列表',
        'Name': '用户名称',
        'Permission': '权限'
    },
    en: {
        'Basic Info': 'Basic Info',
        'Status': 'Status',
        'Joined': 'Joined',
        'Delete': 'Delete',
        'Site Name': 'Site Name',
        'Institution': 'Institution',
        'Party ID': 'Party ID',
        'Role': 'Role',
        'Creation Time': 'Creation Time',
        'Activation Time': 'Activation Time',
        'Registration Link': 'Registration Link',
        'Network configuration': 'Network configuration',
        'Network Acess Entrances': 'Network Acess Entrances',
        'Network Acess Exits': 'Network Acess Exits',
        'System version': 'System version',
        'FATE version': 'FATE version',
        'FATE Component': 'FATE Component',
        'Version': 'Version',
        'User List': 'User List',
        'Name': 'Name',
        'Permission': 'Permission'

    } }
export default {
    name: 'sitedetail',
    components: {
        sitedetailip
    },
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '- -'
        }
    },
    data() {
        return {
            siteList: [], // 站点信息
            changedialog: false,
            noticedialog: false,
            noticedesc: '', // 已经读信息
            submitted: false, // 是否已经提交
            tosubmit: true, // 是否可提价
            edit: false,
            editSubmitted: 1, // 1 显示可编辑按钮，2 显示可提交按钮 3 显示等待审批按钮
            editVersion: 1, // 是否可编辑版本
            getmsgtimeless: null,
            networkAccessEntranceswarnshow: false, // 是否显示警告样式
            networkAccessExitswarnshow: false, // 是否显示警告样式
            networkAccessEntrancesOld: '', // 旧ip
            networkAccessExitsOld: '', // 新ip
            formVersion: {}, // 更新版本号集合
            tempobject: {}, // 编辑版本临时数据
            fateServingVersion: '', // 待更新
            fateVersion: '', // 待更新
            versiondialog: false, // 是否确定更新版本弹框
            fateServingVersionList: [],
            form: {
                networkAccessEntrances: '',
                networkAccessExits: ''
            },
            rules: {
                networkAccessEntrances: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.networkAccessEntranceswarnshow = true
                                callback(
                                    new Error(
                                        'Network Acess Entrances field is required.'
                                    )
                                )
                            } else {
                                this.networkAccessEntranceswarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                networkAccessExits: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.networkAccessExitswarnshow = true
                                callback(
                                    new Error(
                                        'Network Acess Exits field is required.'
                                    )
                                )
                            } else {
                                this.networkAccessExitswarnshow = false
                                callback()
                            }
                        }
                    }
                ]
            }
        }
    },
    computed: {
        ...mapGetters(['role', 'version'])
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        this.initInfo()
        // this.togetMsg()
        this.$store.dispatch('selectEnum')
    },
    beforeDestroy() {
        window.clearTimeout(this.getmsgtimeless)
    },
    methods: {
        initInfo() {
            let data = {
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId)
            }
            getSiteInfo(data).then(res => {
                this.form = { ...res.data }
                this.networkAccessEntrancesOld = this.form.networkAccessEntrances // 旧ip入口
                this.networkAccessExitsOld = this.form.networkAccessExits // 旧ip出口
                if (res.data.editStatus.code === 1) {
                    this.editSubmitted = 3
                } else if (res.data.editStatus.code === 2) {
                    this.editSubmitted = 1
                }

                this.form.componentVersion = []
                console.log('JSON==>>', JSON.parse(res.data.componentVersion))

                // 版本号赋值
                if (res.data.componentVersion) {
                    let object = JSON.parse(res.data.componentVersion)
                    for (const key in object) {
                        let obj = {}
                        obj.label = key
                        obj.version = object[key].version
                        obj.ip = object[key].address
                        this.form.componentVersion.push(obj)
                    }
                }

                this.fateVersion = res.data.fateVersion
                this.fateServingVersion = res.data.fateServingVersion
                let OBJ = {}
                this.form.componentVersion.forEach(item => {
                    OBJ[item.label] = item['version']
                })
                this.formVersion = { ...OBJ }
            })
            // 获取uesrid列表
            let params = {
                partyId: parseInt(this.$route.query.partyId)
            }
            siteInfoList(params).then(res => {
                this.siteList = res.data
            })
        },
        // 点击编辑
        toEdit() {
            this.editSubmitted = 2
        },
        // 取消编辑
        cancel() {
            this.form.networkAccessEntrances = this.networkAccessEntrancesOld // 旧ip入口
            this.form.networkAccessExits = this.networkAccessExitsOld // 旧ip出口
            this.editSubmitted = 1
            this.$refs['form'].validateField(
                'networkAccessEntrances',
                valid => {}
            )
            this.$refs['form'].validateField('networkAccessExits', valid => {})
            this.shouldtosubmit()
        },
        // 提交更新
        submit() {
            this.$refs['form'].validate(valid => {
                if (valid) {
                    this.changedialog = true
                }
            })
        },
        // 确定修改
        okAction() {
            let data = { ...this.form }
            data.role = this.form.role.code
            update(data).then(res => {
                this.changedialog = false
                this.initInfo()
            })
        },
        // 显示增加行
        addShow(type) {
            this.$refs['sitedetailip'].networkacesstype = type
            this.$refs['sitedetailip'].adddialog = true
            if (type === 'entrances') {
                if (this.form.networkAccessEntrances) {
                    this.form.networkAccessEntrances
                        .split(';')
                        .forEach(item => {
                            if (item) {
                                let obj = {}
                                obj.ip = item
                                obj.show = false
                                obj.checked = false
                                this.$refs['sitedetailip'].entrancesSelect.push(
                                    obj
                                )
                            }
                        })
                } else {
                    this.$refs['sitedetailip'].entrancesSelect = []
                }
            } else if (type === 'exit') {
                if (this.form.networkAccessExits) {
                    this.form.networkAccessExits.split(';').forEach(item => {
                        if (item) {
                            let obj = {}
                            obj.ip = item
                            obj.show = false
                            obj.checked = false
                            this.$refs['sitedetailip'].entrancesSelect.push(obj)
                        }
                    })
                } else {
                    this.$refs['sitedetailip'].entrancesSelect = []
                }
            }
        },
        // 提交按钮是否可点击
        shouldtosubmit() {
            if (
                this.networkAccessEntrancesOld !==
                    this.form.networkAccessEntrances ||
                this.networkAccessExitsOld !== this.form.networkAccessExits
            ) {
                this.tosubmit = false // 提交可点击
            } else {
                this.tosubmit = true // 提交按钮可点击
            }
        },
        // 取消表单验证
        cancelValid(validtype) {
            this.$refs['form'].clearValidate(validtype)
            this[`${validtype}warnshow`] = false
        },
        // 已读通知
        notice() {
            let data = {
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId),
                readStatus: 3
            }
            readmsg(data).then(res => {
                window.clearTimeout(this.getmsgtimeless) // 清除定时器
                this.noticedialog = false
                this.initInfo()
            })
            this.noticedialog = false
        },
        async togetMsg() {
            let data = {
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId)
            }
            let res = await getmsg(data)
            if (res.data.editStatus.code === 2) {
                if (res.data.readStatus.code > 0 && res.data.readStatus.code !== 3) {
                    this.noticedialog = true
                    this.noticedesc = res.data.readStatus.desc
                } else if (res.data.readStatus.code === 3) {
                    this.noticedialog = false
                }
            } else {
                this.getmsgtimeless = setTimeout(() => {
                    this.togetMsg()
                }, 1500)
            }
        },
        // 编辑版本
        toEditVersion() {
            let object = JSON.parse(JSON.stringify(this.form.componentVersion))
            this.tempobject = object
            function getres (res, setl) {
                return res.data && res.data.forEach(item => {
                    let o = {}
                    o.label = item
                    o.value = item
                    setl.push(o)
                })
            }
            this.form.componentVersion = object.map(item => {
                let setl = []
                if (item.label === 'clustermanager') {
                    getClusterList().then(res => {
                        getres(res, setl)
                    })
                } else if (item.label === 'fateboard') {
                    getFateboardList().then(res => {
                        getres(res, setl)
                    })
                } else if (item.label === 'mysql') {
                    getMysqlList().then(res => {
                        getres(res, setl)
                    })
                } else if (item.label === 'nodemanager') {
                    getNodeList().then(res => {
                        getres(res, setl)
                    })
                } else if (item.label === 'rollsite') {
                    getrollsiteList().then(res => {
                        getres(res, setl)
                    })
                } else if (item.label === 'fateflow') {
                    getFateflowList().then(res => {
                        getres(res, setl)
                    })
                }

                item.setl = setl
                return item
            })
            this.fateServingVersionList = []
            getFateservingList().then(res => {
                res.data && res.data.forEach(item => {
                    let obj = {}
                    obj.label = item
                    obj.value = item
                    this.fateServingVersionList.push(obj)
                })
            })

            this.editVersion = 2
        },
        // 取消编辑版本
        toCanceleditVersion() {
            this.form.componentVersion = this.tempobject
            this.editVersion = 1
        },
        // 确定更新
        sureVersion() {
            let data = {
                fateServingVersion: this.fateServingVersion,
                fateVersion: this.fateVersion,
                componentVersion: JSON.stringify(this.formVersion),
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId)
            }
            updateVersion(data).then(res => {
                this.versiondialog = false
                this.editVersion = 1
                this.initInfo()
            })
        },
        // 自动更新版本
        toRefresh() {

        },
        // 获取子版本
        togetcomponentVersion(val) {
            let data = {
                fateVersion: val
            }
            getcomponentversion(data).then(res => {
                let object = JSON.parse(res.data)
                let arr = []
                for (const key in object) {
                    let obj = {}
                    obj.label = key
                    obj.version = object[key]
                    arr.push(obj)
                }
                this.form.componentVersion = arr
                let OBJ = {}
                this.form.componentVersion.forEach(item => {
                    OBJ[item.label] = item['version']
                })
                this.formVersion = { ...OBJ }
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/sitedetail.scss';
</style>
