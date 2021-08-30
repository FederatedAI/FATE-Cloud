<template>
  <div class="site-add">
    <div class="add-info">
      <div class="title">
        <span v-if="type==='siteadd'">{{$t('m.siteAdd.addSite')}}</span>
        <span v-if="type==='siteinfo'">{{$t('m.siteAdd.waitingActivation')}}</span>
        <span v-if="type==='siteUpdate'">{{$t('m.siteAdd.siteUpdate')}}</span>
      </div>
      <el-form
        ref="infoform"
        :model="form"
        label-position="left"
        :rules="rules"
        label-width="250px">
        <div class="basic-info">{{$t('m.siteAdd.Basic Info')}}</div>
        <el-form-item :label="$t('m.common.siteName')"  prop="siteName">
          <span v-if="type==='siteinfo'" class="info-text require">{{form.siteName}}</span>
          <el-input
            v-else
            v-model="form.siteName"
            @blur="toCheckSiteName"
            @focus="cancelValid('siteName')"
            :placeholder="$t('m.siteAdd.maximum20chatacters')"
          ></el-input>
        </el-form-item>
        <el-form-item  :label="$t('m.common.siteInstitution')" prop="institutions">
            <span v-if="type==='siteinfo'" class="info-text">{{form.institutions}}</span>
            <el-select
                v-else
                :popper-append-to-body="false"
                v-model="form.institutions"
                filterable
                @focus="cancelValid('institutions')"
                :placeholder="$t('m.siteAdd.chooseInstitutions')">
                <el-option v-for="item in institutionsdownList" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <span  v-if="type==='siteadd' || type==='siteUpdate'" @click="toAddInstitutions" class="add-institutions">{{$t('m.common.add')}}</span>
        </el-form-item>
        <el-form-item :label="$t('m.common.role')"  prop="role">
          <span v-if="type==='siteinfo'" class="info-text">{{form.role===1? $t('m.common.guest') : $t('m.common.host')}}</span>
          <el-select
            v-else
            :popper-append-to-body="false"
            v-model="form.role"
            @focus="cancelValid('role')"
            @change="getPartyid"
            :placeholder="$t('m.siteAdd.chooseRole')">
            <el-option v-for="item in roleOp" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item  :label="$t('m.common.partyID')" >
            <span slot="label">
                <span>
                <span>{{$t('m.common.partyID')}}</span>
                    <i style="margin-left:15px;cursor: pointer;" @click="toPartyid" class="el-icon-s-tools tools"></i>
                </span>
            </span>
            <span v-if="type==='siteinfo'" class="info-text">{{form.partyId}}</span>
            <el-select
                v-else
                :disabled="partyidname.length===0"
                filterable
                v-model="partyidSelect"
                @change="selectPartyid"
                :popper-append-to-body="false"
                :placeholder="$t('m.siteAdd.chooseGroup')">
            <el-option v-for="item in partyidname" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-show="(type==='siteadd' || type==='siteUpdate') && partyidSelect" >
          <div class="dropdown">
            <i @click="downshow=!downshow" class="el-icon-arrow-down down"></i>
            <el-input v-if="downshow" v-model="groupRange" disabled autosize type="textarea" />
            <div v-if="!downshow" class="down-text">{{groupRange}}</div>
          </div>
        </el-form-item>
        <el-form-item label prop="partyId" v-if="type==='siteadd' || type==='siteUpdate'">
          <el-input
            :disabled="!partyidSelect"
            v-model.trim="form.partyId"
            @focus="cancelValid('partyId')"
            @blur="tocheckPartyid"
            :placeholder="$t('m.siteAdd.typePartyID')">
            </el-input>
        </el-form-item>
        <el-form-item v-show="false"  prop="exits">
            <el-input v-if="type==='siteUpdate' || type==='siteadd'" v-model="form.exits">
            </el-input>
        </el-form-item>
        <el-form-item v-if="type==='siteinfo'" label="Federation Key">
           <span v-if="keyViewDefault" class="info-text">{{form.secretInfo.key}} <img src="@/assets/view_hide.png" @click="keyViewDefault=!keyViewDefault" class="view" ></span>
            <span  v-if="!keyViewDefault" class="info-text">***********************<img src="@/assets/view_show.png" @click="keyViewDefault=!keyViewDefault" class="view" ></span>
        </el-form-item>
        <el-form-item v-if="type==='siteinfo'" label="Federation Secret">
            <span v-if="sansViewDefault" class="info-text">{{form.secretInfo.secret}} <img src="@/assets/view_hide.png" @click="sansViewDefault=!sansViewDefault" class="view" ></span>
            <span  v-if="!sansViewDefault" class="info-text">***********************<img src="@/assets/view_show.png" @click="sansViewDefault=!sansViewDefault" class="view" ></span>
        </el-form-item>
        <span class="registration-box">
            <el-form-item  label="" prop="">
                <span slot="label">
                    <span style="color:#4E5766">{{$t('m.siteAdd.registrationLinkSetting')}}</span>
                </span>
            </el-form-item>
            <el-form-item :label="$t('m.siteAdd.linkType')"  prop="protocol">
                <span v-if="type==='siteinfo' && form.protocol" class="info-text">{{form.protocol==='https://'?"HTTPS":'HTTP'}}</span>
                <el-radio-group v-else  v-model="form.protocol">
                    <el-radio label="https://">HTTPS</el-radio>
                    <el-radio label="http://">HTTP</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item :label="$t('m.siteAdd.encryption')" prop="encryptType">
                <span v-if="type==='siteinfo' && form.encryptType" class="info-text">{{form.encryptType===1? $t('m.siteAdd.encryptionType') : $t('m.siteAdd.unencrypted')}}</span>
                <span v-else >
                    <el-switch :width='35'  v-model="form.encryptType">
                    </el-switch>
                </span>

                <!-- <el-radio-group v-else  v-model="form.encryptType">
                    <el-radio :label="1">{{$t('m.siteAdd.encryptionType')}}</el-radio>
                    <el-radio :label="2">{{$t('m.siteAdd.unencrypted')}}</el-radio>
                </el-radio-group> -->
            </el-form-item>
            <el-form-item :label="$t('m.siteAdd.proxyNetworkAccess')"  prop="network">
                <span v-if="type==='siteinfo'" class="info-text">{{form.network}}</span>
                <el-input v-else v-model="form.network"></el-input>
                <span  v-if="type==='siteadd' || type==='siteUpdate'" @click="toResetNetwork" class="add-institutions">{{$t('m.siteAdd.resetDefault')}}</span>
            </el-form-item>
            <el-form-item v-if="type==='siteinfo'" :label="$t('m.siteAdd.registrationLink')" >
                <el-popover
                    placement="top"
                    width="300"
                    trigger="hover"
                    :content="form.registrationLink">
                    <span slot="reference" class="link-text" style="color:#217AD9">{{form.registrationLink}}</span>
                </el-popover>
                <span class="copy formcopy" @click="toCopy('from')" :data-clipboard-text="form.registrationLink">{{$t('m.common.copy')}}</span>
            </el-form-item>
        </span>
        <div class="basic-info">{{$t('m.siteAdd.Exchange Info')}}</div>
        <el-form-item label="Exchange"  prop="exchangeId">
            <span v-if="type==='siteinfo'" class="info-text">{{form.exchangeName}}</span>
            <el-select
                v-else
                @focus="cancelValid('exchangeId')"
                v-model="form.exchangeId"
                filterable
                :placeholder="$t('m.siteAdd.exchange')">
                <el-option v-for="item in exchangeList" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
        </el-form-item>

      </el-form>
      <div class="Submit">
        <el-button type="primary" v-if="type==='siteadd'" @click="submitAction">{{$t('m.common.submit')}}</el-button>
        <el-button type="primary" v-if="type==='siteUpdate'" @click="submitAction">{{$t('m.common.resubmit')}}</el-button>
        <el-button type="primary" v-if="type==='siteinfo'" @click="modifyAction">{{$t('m.common.modify')}}</el-button>
      </div>
    </div>
    <!-- 添加成功 -->
    <el-dialog :visible.sync="okdialog" :close-on-click-modal="false" :show-close="true" :close-on-press-escape="false" class="ok-dialog">
      <div class="icon">
        <i class="el-icon-success"></i>
      </div>
      <div v-if="type==='siteadd'" class="line-text-one">{{$t('m.siteAdd.addSuccessfully')}}</div>
      <div v-if="type==='siteUpdate'" class="line-text-one" >{{$t('m.siteAdd.modifySuccessfully')}}</div>
      <div class="line-text-two">{{$t('m.siteAdd.registrationLinkGenerated')}}</div>
      <div class="line-text-three">
        <el-popover
            placement="top"
            width="660"
            trigger="hover"
            offset="-30"
            :content="form.registrationLink">
            <span class="copy-link" slot="reference">{{form.registrationLink}}</span>
        </el-popover>
        <span class="copy dialogcopy"  @click="toCopy('tooltip')"  :data-clipboard-text="form.registrationLink">{{$t('m.common.copy')}}</span>
      </div>
      <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" @click="okAction">{{$t('m.common.OK')}}</el-button>
      </div>
    </el-dialog>
    <!-- 是否离开 -->
    <el-dialog :visible.sync="isleavedialog" class="site-toleave-dialog" width="500px">
      <div class="line-text-one">{{$t('m.siteAdd.sureLeavePage')}}</div>
      <div class="line-text-two">{{$t('m.siteAdd.notSavedTips')}}</div>
      <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" @click="sureLeave">{{$t('m.common.sure')}}</el-button>
        <el-button class="ok-btn" type="info" @click="cancelLeave">{{$t('m.common.cancel')}}</el-button>
      </div>
    </el-dialog>
    <siteaddip ref="siteaddip"/>
  </div>
</template>

<script>
import { getPartyRang,
    siteAdd,
    getSiteInfo,
    siteUpdate,
    checkPartId,
    checkSiteName,
    addInstitutionsList,
    resetNetwork,
    getExchangeList } from '@/api/federated'
import { responseRange, requestRange } from '@/utils/idRangeRule'

import Clipboard from 'clipboard'
import siteaddip from './siteaddip'
import { checkip } from '@/utils/checkip'

export default {
    name: 'siteadd',
    components: {
        siteaddip
    },
    data() {
        return {
            type: this.$route.query.type, // 编辑或者添加
            id: '', // 数据库对应的id
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
            exchangeList: [],
            form: {
                groupId: '', // 每项role下拉，都有相应的groupId项。
                siteName: '',
                institutions: '',
                role: '',
                partyId: '',
                network: '',
                secretInfo: {
                    key: '',
                    secret: ''
                },
                exits: '',
                protocol: 'https://',
                encryptType: true,
                registrationLink: '',
                mode: 'short' // 1.4.0 版本修改
            },
            roleOp: [
                {
                    value: 1,
                    label: this.$t('m.common.guest')
                },
                {
                    value: 2,
                    label: this.$t('m.common.host')
                }
            ],
            rules: {
                siteName: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, val, callback) => {
                            let value = val.trim()
                            if (!value) {
                                callback(new Error(this.$t('m.siteAdd.siteNameRequired')))
                            } else if (value.length > 20) {
                                callback(new Error(this.$t('m.siteAdd.maxCharacters')))
                            } else if (this.siteNameExists) {
                                callback(new Error(this.$t('m.siteAdd.alreadyExists')))
                            } else {
                                callback()
                            }
                        }
                    }
                ],
                institutions: [{ required: true, message: this.$t('m.siteAdd.institutionRrequired'), trigger: 'bulr' }],
                role: [{ required: true, message: this.$t('m.siteAdd.roleRrequired'), trigger: 'bulr' }],
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
                                callback(new Error(this.$t('m.siteAdd.partyIDRequired')))
                            } else if (result || !number.test(value)) {
                                callback(new Error(this.$t('m.siteAdd.invalidPartyID')))
                            } else if (!this.partyIdPostPass) {
                                callback(new Error(this.$t('m.siteAdd.partyIDUsed')))
                            } else {
                                callback()
                            }
                        }
                    }
                ],
                network: [{
                    required: true,
                    trigger: 'change',
                    validator: (rule, value, callback) => {
                        console.log(value, 'value')
                        if (!value) {
                            callback(new Error(this.$t('m.siteAdd.proxyNetworkAccessRequired')))
                        } else if (!checkip(value)) {
                            callback(new Error(this.$t('m.siteAdd.proxyNetworkAccessInvalid')))
                        } else {
                            callback()
                        }
                    }
                }],
                exchangeId: [{ required: true, message: this.$t('m.siteAdd.ExchangRequired'), trigger: 'bulr' }]
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
        this.toResetNetwork()
        this.getExchangeList()
    },
    beforeDestroy() {},
    mounted() {
        this.$router.beforeEach((to, from, next) => {
            this.leaveRouteName = to.name
            // console.log(this.leaveRouteName, 'leaveRouteName')
            if (this.isleave) {
                next()
            } else {
                // 中断路由
                next(false)
                this.isleavedialog = true
            }
        })
    },
    methods: {
        getExchangeList() {
            getExchangeList().then(res => {
                res.data.forEach(item => {
                    let obj = {}
                    obj.value = item.exchangeId
                    obj.label = item.exchangeName
                    this.exchangeList.push(obj)
                })
            })
        },
        // 检查SiteName是否重名
        toCheckSiteName() {
            // SiteName不为空的是校验
            if (this.form.siteName) {
                let data = {
                    siteName: this.form.siteName.trim(),
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
                console.log(valid, 'valid')
                if (valid !== this.$t('m.siteAdd.partyIDRequired') && valid !== this.$t('m.siteAdd.invalidPartyID')) {
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
            // 去除前后空格
            this.form.siteName = this.form.siteName.trim()
            if (typeof this.form.encryptType !== 'number') {
                this.$set(this.form, 'encryptType', this.getStatus(this.form.encryptType))
            }
            if (this.type === 'siteadd') {
                this.$refs['infoform'].validate(async (valid) => {
                    if (valid) {
                        let data = { ...this.form }
                        let res = await siteAdd(data)
                        if (res.code === 0) {
                            this.isleave = true
                            this.id = res.data// 获取id赋值
                            this.okdialog = true
                            this.getKeySansLink()
                        }
                    }
                })
            } else if (this.type === 'siteUpdate') {
                this.$refs['infoform'].validate(async (valid) => {
                    if (valid) {
                        let data = { ...this.form }
                        let res = await siteUpdate(data)
                        if (res.code === 0) {
                            this.isleave = true
                            this.id = this.$route.query.id
                            this.okdialog = true
                            this.getKeySansLink()
                        }
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
            this.$store.dispatch('SetMune', 'Site Manage')
            this.isleavedialog = false
        },

        // 跳转Party ID路由
        toPartyid() {
            this.$router.push({
                name: 'Party ID'
            })
        },

        // 下拉显示groupname
        async getPartyid(role) {
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

            let res = await getPartyRang(data)
            res.data.list.forEach(item => {
                let obj = {}
                obj.value = item.groupName
                obj.label = item.groupName
                obj.rangeInfo = responseRange(item.federatedGroupDetailDos)
                obj.groupId = item.groupId
                this.partyidname.push(obj)
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
        async getKeySansLink() {
            let data = {
                id: this.id ? parseInt(this.id) : parseInt(this.$route.query.id)
            }
            let res = await getSiteInfo(data)
            // 内部包含\n，此处一定得做处理，不然前端把\n解析成空格或者换行
            let link = res.data.registrationLink
            if (link.indexOf('?st') < 0) {
                console.log('加密链接')
                link = JSON.stringify(link).replace(new RegExp('"', 'g'), '')
            } else {
                console.log('未加密链接')
                link = JSON.stringify(link).replace(new RegExp('\\\\', 'g'), '')
                console.log(link, 'link')
            }
            if (this.type === 'siteinfo') {
                let resData = { ...res.data }
                if (!res.data.protocol && !res.data.encryptType) {
                    resData.protocol = 'https://'
                    resData.encryptType = true
                }
                this.form = resData
                this.form.mode = 'short' // 1.4.0 版本修改 短连接
            } else {
                if (res.data.registrationLink.indexOf('?st') < 0) {
                    this.form.registrationLink = link
                } else {
                    this.form.registrationLink = res.data.registrationLink
                }
            }
            this.partyidSelect = res.data.groupName // groupName范围下拉
            await this.getPartyid(res.data.role)// role下拉获取数据
            await this.selectPartyid(res.data.groupName)
        },
        // 复制
        toCopy(type) {
            if (type === 'tooltip') {
                let dialogClipboard = new Clipboard('.dialogcopy')
                dialogClipboard.on('success', e => {
                    console.log(e, 'copy-e')
                    this.$message.success(this.$t('m.common.copySuccess'))
                    // 释放内存
                    dialogClipboard.destroy()
                })
            }
            if (type === 'from') {
                let formClipboard = new Clipboard('.formcopy')
                formClipboard.on('success', e => {
                    this.$message.success(this.$t('m.common.copySuccess'))
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
        },
        // 重置默认值
        toResetNetwork() {
            resetNetwork().then(res => {
                this.form.network = res.data.network
            })
        },
        getStatus(stauts) {
            console.log(typeof stauts, 'typeof stauts')
            if (typeof stauts === 'number') {
                return stauts === 1
            } else {
                return stauts === true ? 1 : 2
            }
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/siteadd.scss';
    /* Solve Firefox compatibility issues  */
    .el-form-item__content{
        margin-bottom: 5px;
    }
</style>
