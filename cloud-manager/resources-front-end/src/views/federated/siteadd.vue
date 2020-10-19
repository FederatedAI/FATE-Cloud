<template>
  <div class="site-add">
    <div class="add-info">
      <div class="title">
        <span v-if="type==='siteadd'">Add a site</span>
        <span v-if="type==='siteinfo'">Waiting for site activation...</span>
        <span v-if="type==='siteUpdate'">Site Update</span>
      </div>
      <el-form
        ref="infoform"
        :model="form"
        label-position="left"
        :rules="rules"
        label-width="250px"
      >
        <el-form-item label="Site Name" prop="siteName">
          <span v-if="type==='siteinfo'" class="info-text">{{form.siteName}}</span>
          <el-input
            v-if="type==='siteadd' || type==='siteUpdate'"
            :class="{ 'edit-text': true, 'stienamewarn': siteNamewarnshow }"
            v-model.trim="form.siteName"
            @blur="toCheckSiteName"
            @focus="cancelValid('siteName')"
            placeholder="Maximum of 20 chatacters"
          ></el-input>
        </el-form-item>
        <el-form-item label="Institution" prop="institutions">
            <span v-if="type==='siteinfo'" class="info-text">{{form.institutions}}</span>
            <el-select
                v-if="type==='siteadd' || type==='siteUpdate'"
                :class="{ 'edit-text': true, 'institutionwarn': institutionswarnshow }"
                :popper-append-to-body="false"
                v-model.trim="form.institutions"
                @focus="cancelValid('institutions')"
                placeholder="Choose Institutions"
            >
                <el-option v-for="item in institutionsdownList" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <span  v-if="type==='siteadd' || type==='siteUpdate'" @click="toAddInstitutions" class="add-institutions">add</span>

          <!-- <el-input
            v-if="type==='siteadd' || type==='siteUpdate'"
            :class="{ 'edit-text': true, 'institutionwarn': institutionswarnshow }"
            v-model.trim="form.institutions"
            @focus="cancelValid('institutions')"
            placeholder
          ></el-input> -->
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <span v-if="type==='siteinfo'" class="info-text">{{form.role===1?"Guest":"Host"}}</span>
          <el-select
            v-if="type==='siteadd' || type==='siteUpdate'"
            :class="{ 'edit-text': true, 'rolewarn': rolewarnshow }"
            :popper-append-to-body="false"
            v-model="form.role"
            @focus="cancelValid('role')"
            @change="getPartyid"
            placeholder="Choose Role"
          >
            <el-option v-for="item in roleOp" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Party ID" >
            <span slot="label">
                <span>
                <span>Party ID</span>
                    <i style="margin-left:15px;cursor: pointer;" @click="toPartyid" class="el-icon-s-tools tools"></i>
                </span>
            </span>
            <span v-if="type==='siteinfo'" class="info-text">{{form.partyId}}</span>
            <el-select
                v-if="type==='siteadd' || type==='siteUpdate'"
                class="edit-text"
                :disabled="partyidname.length===0"
                v-model="partyidSelect"
                @change="selectPartyid"
                :popper-append-to-body="false"
                placeholder="choose an ID Group"
            >
            <el-option v-for="item in partyidname" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-show="(type==='siteadd' || type==='siteUpdate') && partyidSelect" >
          <div class="dropdown">
            <i @click="downtext" class="el-icon-arrow-down down"></i>
            <el-input v-if="downshow" v-model="groupRange" disabled autosize type="textarea" />
            <div v-if="!downshow" class="down-text">{{groupRange}}</div>
          </div>
        </el-form-item>
        <el-form-item label prop="partyId" v-if="type==='siteadd' || type==='siteUpdate'">
          <el-input
            :class="{ 'edit-text': true, 'partyidwarn': partyIdwarnshow }"
            :disabled="!partyidSelect"
            v-model.trim="form.partyId"
            @focus="cancelValid('partyId')"
            @blur="tocheckPartyid"
            placeholder="Type the Party ID"
          ></el-input>
        </el-form-item>

        <el-form-item label="Network Acess Entrances" prop="networkAccessEntrances">
          <span v-if="type==='siteinfo'" class="info-text">
            <div v-for="(item,index) in form.networkAccessEntrances.split(';')" :key='index'>{{item}}</div>
          </span>
          <el-input
            v-if="type==='siteadd' || type==='siteUpdate'"
            @focus="addShow('entrances')"
            @blur="cancelValid('networkAccessEntrances')"
            :class="{ 'edit-text': true, 'plus-text':true,'entranceswarn': networkAccessEntranceswarnshow }"
            v-model="form.networkAccessEntrances"
            placeholder
          >
            <i slot="suffix" @click="addShow('entrances')" class="el-icon-edit plus" />
          </el-input>
        </el-form-item>
        <el-form-item label="Network Acess Exits" prop="networkAccessExits">
          <span class="info-text"  v-if="type==='siteinfo'">
            <div v-for="(item,index) in form.networkAccessExits.split(';')" :key='index'>{{item}}</div>
          </span>
          <el-input
            v-if="type==='siteadd' || type==='siteUpdate'"
            @focus="addShow('exit')"
            @blur="cancelValid('networkAccessExits')"
            :class="{ 'edit-text': true, 'plus-text':true,'exitwarn': networkAccessExitswarnshow }"
            v-model="form.networkAccessExits"
            placeholder>
            <i slot="suffix" @click="addShow('exit')" class="el-icon-edit plus" />
          </el-input>
        </el-form-item>
        <el-form-item v-show="false"  prop="exits">
            <el-input v-if="type==='siteUpdate' || type==='siteadd'" v-model="form.exits">
            </el-input>
        </el-form-item>
        <el-form-item v-if="type==='siteinfo'" label="Federation Key">
           <span v-if="keyViewDefault" class="info-text">{{form.secretInfo.key}} <img src="@/assets/view_show.png" @click="keyShowView" class="view" ></span>
            <span  v-if="!keyViewDefault" class="info-text">***********************<img src="@/assets/view_hide.png" @click="keyShowView" class="view" ></span>
        </el-form-item>
        <el-form-item v-if="type==='siteinfo'" label="Federation Secret">
            <span v-if="sansViewDefault" class="info-text">{{form.secretInfo.secret}} <img src="@/assets/view_hide.png" @click="sansShowView" class="view" ></span>
            <span  v-if="!sansViewDefault" class="info-text">***********************<img src="@/assets/view_show.png" @click="sansShowView" class="view" ></span>
        </el-form-item>
        <el-form-item v-if="type==='siteinfo'" label="Registration Link">
            <el-popover
                placement="top"
                width="300"
                trigger="hover"
                :content="form.registrationLink">
                <span slot="reference" class="link-text" style="color:#217AD9">{{form.registrationLink}}</span>
            </el-popover>
            <span class="copy formcopy" @click="toCopy('from')" :data-clipboard-text="form.registrationLink">copy</span>
        </el-form-item>
      </el-form>
      <div class="Submit">
        <el-button type="primary" v-if="type==='siteadd'" @click="submitAction">Submit</el-button>
        <el-button type="primary" v-if="type==='siteUpdate'" @click="submitAction">Resubmit</el-button>
        <el-button type="primary" v-if="type==='siteinfo'" @click="modifyAction">Modify</el-button>
      </div>
    </div>
    <el-dialog :visible.sync="okdialog" :close-on-click-modal="false" :close-on-press-escape="false" class="ok-dialog">
      <div class="icon">
        <i class="el-icon-success"></i>
      </div>
      <div v-if="type==='siteadd'" class="line-text-one" >Add successfully !</div>
      <div v-if="type==='siteUpdate'" class="line-text-one" >Modify successfully !</div>
      <div class="line-text-two">the registration link has been generated as follows:</div>
      <div class="line-text-three">
        <el-popover
            placement="top"
            width="660"
            trigger="hover"
            offset="-30"
            :content="form.registrationLink">
            <span class="copy-link" slot="reference">{{form.registrationLink}}</span>
        </el-popover>
        <span class="copy dialogcopy"  @click="toCopy('tooltip')"  :data-clipboard-text="form.registrationLink">copy</span>
      </div>
      <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" @click="okAction">OK</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="isleavedialog" class="site-toleave-dialog" width="700px">
      <div class="line-text-one">Are you sure you want to leave this page ?</div>
      <div class="line-text-two">Your information will not be saved.</div>
      <div class="dialog-footer">
        <el-button class="sure-btn" type="primary" @click="sureLeave">Sure</el-button>
        <el-button class="sure-btn" type="primary" @click="cancelLeave">Cancel</el-button>
      </div>
    </el-dialog>
    <siteaddip ref="siteaddip"/>
  </div>
</template>

<script>
import { getPartyRang, siteAdd, getSiteInfo, siteUpdate, checkPartId, checkSiteName, addInstitutionsList } from '@/api/federated'
import { responseRange, requestRange } from '@/utils/idRangeRule'

import Clipboard from 'clipboard'
import siteaddip from './siteaddip'
export default {
    name: 'siteadd',
    components: {
        siteaddip
    },
    data() {
        return {
            type: this.$route.query.type, // 编辑或者添加
            id: '', // 数据库对应的id
            siteNamewarnshow: false, // 是否显示警告样式
            institutionswarnshow: false, // 是否显示警告样式
            rolewarnshow: false, // 是否显示警告样式
            partyIdwarnshow: false, // 是否显示警告样式
            networkAccessEntranceswarnshow: false, // 是否显示警告样式
            networkAccessExitswarnshow: false, // 是否显示警告样式
            isleave: false, // 是否可以离开路由
            isleavedialog: false, // 中途离开弹窗
            leaveRouteName: '', // 中途离开路由名称
            sansViewDefault: false, // 是否显示sans
            keyViewDefault: false, // 是否显示key
            partyidname: [], // 下拉partyid
            partyidSelect: '', // partyid显示
            partyIdPostPass: true, // 验证是否被占用
            groupRange: '', // id group范围
            okdialog: false,
            downshow: false,
            siteNameExists: false,
            institutionsdownList: [],
            form: {
                groupId: '', // 每项role下拉，都有相应的groupId项。
                siteName: '',
                institutions: '',
                role: '',
                partyId: '',
                networkAccessEntrances: '',
                networkAccessExits: '',
                secretInfo: {
                    key: '',
                    secret: ''
                },
                exits: '',
                aa: '',
                registrationLink: ''
            },
            roleOp: [
                {
                    value: 1,
                    label: 'Guest'
                },
                {
                    value: 2,
                    label: 'Host'
                }
            ],
            rules: {
                siteName: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.siteNamewarnshow = true
                                callback(new Error('The Site Name field is required.'))
                            } else if (value.length > 20) {
                                this.siteNamewarnshow = true
                                callback(new Error('The Site Name cannot exceed 20 characters.'))
                            } else if (this.siteNameExists) {
                                this.siteNamewarnshow = true
                                callback(new Error('This site name already exists'))
                            } else {
                                this.siteNamewarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                institutions: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.institutionswarnshow = true
                                callback(new Error('Institution field is required.'))
                            } else {
                                this.institutionswarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                role: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.rolewarnshow = true
                                callback(new Error('Role field is required.'))
                            } else {
                                this.rolewarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                partyId: [
                    {
                        required: true,
                        trigger: 'blur',
                        validator: (rule, value, callback) => {
                            // 验证数字
                            let number = new RegExp(/^-?[1-9]\d*$/)
                            // 验证范围
                            let result = true
                            this.groupRange.split(';').forEach(item => {
                                let itemArr = item.split(/~|-/).map(item => parseInt(item))
                                let val = parseInt(value)
                                if (itemArr && itemArr.length > 1) {
                                    if (itemArr[0] <= val && itemArr[1] >= val) {
                                        result = false
                                    }
                                } else if (itemArr && itemArr.length === 1) {
                                    if (itemArr[0] === val) {
                                        result = false
                                    }
                                }
                            })
                            if (!value) {
                                this.partyIdwarnshow = true
                                callback(new Error('Party ID field is required'))
                            } else if (result || !number.test(value)) {
                                this.partyIdwarnshow = true
                                callback(new Error('Invalid Party ID'))
                            } else if (!this.partyIdPostPass) {
                                this.partyIdwarnshow = true
                                callback(new Error('This Party ID has been used, please reassign'))
                            } else {
                                this.partyIdwarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                networkAccessEntrances: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.networkAccessEntranceswarnshow = true
                                callback(new Error('Network Acess Entrances field is required.'))
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
                                callback(new Error('Network Acess Exit field is required.'))
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
    watch: {

    },
    computed: {},

    created() {
        if (this.type === 'siteinfo') {
            this.isleave = true
        }
        if (this.$route.query.id) {
            this.getKeySansLink()
        }
        addInstitutionsList().then(res => {
            res.data.forEach((item, index) => {
                let obj = {}
                obj.value = item
                obj.label = item
                this.institutionsdownList.push(obj)
            })
        })
    },
    beforeDestroy() {},
    mounted() {
        this.$router.beforeEach((to, from, next) => {
            this.leaveRouteName = to.name
            if (this.isleave) {
                next()
            } else {
                this.isleavedialog = true
                // 中断路由
                next(false)
            }
        })
    },
    methods: {
        // 检查SiteName是否重名
        toCheckSiteName() {
            // SiteName不为空的是校验
            if (this.form.siteName) {
                let data = {
                    siteName: this.form.siteName,
                    id: this.$route.query.id
                }
                checkSiteName(data).then(res => {
                    if (res.code === 0) {
                        this.siteNameExists = false
                        this.$refs['infoform'].validateField('siteName')
                    }
                }).catch(res => {
                    if (res.code === 122) {
                        this.siteNameExists = true
                        this.$refs['infoform'].validateField('siteName')
                    }
                })
            }
        },
        // 检查partyid是否被占用
        tocheckPartyid() {
            this.$refs['infoform'].validateField('partyId', valid => {
                if (valid !== 'Invalid Party ID' && valid !== 'Party ID field is required') {
                    let data = {
                        id: this.$route.query.id,
                        partyId: this.form.partyId,
                        rangeInfo: requestRange(this.groupRange)
                    }
                    checkPartId(data).then(res => {
                        if (res.code === 0) {
                            this.partyIdPostPass = true// partyid没有被占用
                            this.$refs['infoform'].validateField('partyId', valid => {})
                        }
                    }).catch(res => {
                        if (res.code === 103) {
                            this.partyIdPostPass = false// partyid已经被占用
                            this.$refs['infoform'].validateField('partyId', valid => {})
                        }
                    })
                }
            })
        },
        // 提交
        submitAction() {
            if (this.type === 'siteadd') {
                this.$refs['infoform'].validate((valid) => {
                    if (valid) {
                        let data = { ...this.form }
                        siteAdd(data).then(res => {
                            this.isleave = true
                            if (res.data) {
                                this.id = res.data// 获取id赋值
                            }
                            setTimeout(() => {
                                this.getKeySansLink()
                            }, 300)
                        }).catch(err => {
                            console.log(err)
                        })
                    }
                })
            } else if (this.type === 'siteUpdate') {
                this.$refs['infoform'].validate((valid) => {
                    if (valid) {
                        let data = { ...this.form }
                        siteUpdate(data).then(res => {
                            if (res.code === 0) {
                                this.isleave = true
                                this.id = this.$route.query.id
                                setTimeout(() => {
                                    this.getKeySansLink()
                                }, 300)
                            }
                        }).catch(err => {
                            console.log(err)
                        })
                    }
                })
            }
        },
        // 激活编辑
        modifyAction() {
            this.type = 'siteUpdate'
            this.isleave = false
        },
        // 添加成功，确定
        okAction() {
            this.okdialog = false
            this.$router.push({
                name: 'Site Manage'
            })
        },
        // 确定离开
        sureLeave() {
            this.isleave = true
            this.isleavedialog = false
            let query = this.leaveRouteName === 'Admin Access' ? { type: 'FATEManager' } : ''
            this.$router.push({
                name: this.leaveRouteName,
                query
            })
        },
        // 取消离开
        cancelLeave() {
            this.isleavedialog = false
        },
        // 添加/编辑出入口
        addShow(type) {
            this.$refs['siteaddip'].networkacesstype = type
            this.$refs['siteaddip'].adddialog = true
            if (type === 'entrances') {
                if (this.form.networkAccessEntrances) {
                    let tempArr = []
                    this.form.networkAccessEntrances.split(';').forEach(item => {
                        if (item) {
                            let obj = {}
                            obj.ip = item
                            obj.show = false
                            obj.checked = false
                            tempArr.push(obj)
                        }
                    })
                    this.$refs['siteaddip'].entrancesSelect = [...new Set(tempArr)]
                } else {
                    this.$refs['siteaddip'].entrancesSelect = []
                }
            } else if (type === 'exit') {
                if (this.form.networkAccessExits) {
                    let tempArr = []
                    this.form.networkAccessExits.split(';').forEach(item => {
                        if (item) {
                            let obj = {}
                            obj.ip = item
                            obj.show = false
                            obj.checked = false
                            tempArr.push(obj)
                        }
                    })
                    this.$refs['siteaddip'].entrancesSelect = [...new Set(tempArr)]
                } else {
                    this.$refs['siteaddip'].entrancesSelect = []
                }
            }
        },
        // 跳转Party ID路由
        toPartyid() {
            this.$router.push({
                name: 'Party ID'
                // path: '/federated/siteadd',
                // query: {
                //     type: 'siteinfo'
                // }
            })
        },
        // 显示下拉文字
        downtext() {
            this.downshow = !this.downshow
        },

        // 显示sans
        sansShowView() {
            this.sansViewDefault = !this.sansViewDefault
        },
        // 显示key
        keyShowView() {
            this.keyViewDefault = !this.keyViewDefault
        },
        // 下拉显示groupname
        getPartyid(role) {
            let data = {
                pageNum: 1,
                pageSize: 100,
                role
            }
            this.partyidname = [] // 清空原有下拉groupName选项
            if (this.type === 'siteadd' || this.type === 'siteUpdate') {
                this.partyidSelect = ''// 下清空拉选中的groupName
                this.form.partyId = ''// 清空原选中的partyId
            }

            getPartyRang(data).then(res => {
                res.data.list.forEach(item => {
                    let obj = {}
                    obj.value = item.groupName
                    obj.label = item.groupName
                    // obj.rangeInfo = responseRange(item.rangeInfo)
                    obj.rangeInfo = responseRange(item.federatedGroupDetailDos)
                    obj.groupId = item.groupId
                    this.partyidname.push(obj)
                })
            })
        },
        // 获取groupName范围赋值
        selectPartyid(val) {
            this.partyidname.forEach(item => {
                if (val === item.value) {
                    this.groupRange = item.rangeInfo
                    this.form.groupId = item.groupId
                }
            })
        },
        // 获取最新信息
        getKeySansLink() {
            let data = {
                id: this.id ? parseInt(this.id) : parseInt(this.$route.query.id)
            }
            getSiteInfo(data).then(res => {
                this.form = { ...res.data }
                this.partyidSelect = res.data.groupName // groupName范围下拉
                this.getPartyid(res.data.role)// role下拉获取数据
                setTimeout(() => {
                    this.selectPartyid(res.data.groupName)// groupName范围赋值
                }, 500)

                if (this.id) {
                    this.okdialog = true
                }
            })
        },
        // 复制
        toCopy(type) {
            if (type === 'tooltip') {
                let dialogClipboard = new Clipboard('.dialogcopy')
                dialogClipboard.on('success', e => {
                    this.$message({ type: 'success', message: 'Success!' })
                    // 释放内存
                    dialogClipboard.destroy()
                })
            }
            if (type === 'from') {
                let formClipboard = new Clipboard('.formcopy')
                formClipboard.on('success', e => {
                    this.$message({ type: 'success', message: 'Success!' })
                    // 释放内存
                    formClipboard.destroy()
                })
            }
        },
        // 取消表单验证
        cancelValid(validtype) {
            this.$refs['infoform'].clearValidate(validtype)
            this[`${validtype}warnshow`] = false
        },
        // 跳转添加
        toAddInstitutions() {
            this.$router.push({
                name: 'Admin Access',
                query: { type: 'FATE Manager' }
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >

@import 'src/styles/siteadd.scss';
</style>
