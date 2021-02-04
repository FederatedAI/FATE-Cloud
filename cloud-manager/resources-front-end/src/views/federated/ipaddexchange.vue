<template>
  <div class="ipaddexchange-box">
        <div class="add-exchange">
            <div class="exchange-title">Add  an  Exchange</div>
            <el-form ref="editform" class="edit-form" :rules="editRules"  label-width="100px" label-position="left" :model="exchangeData">
                <el-form-item label="Exchange" prop="exchangeName" >
                    <span slot="label">
                        <span>Exchange</span>
                        <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                    </span>
                    <el-input @blur="cancelValid('exchangeName')" @focus="cancelValid('exchangeName')" v-model="exchangeData.exchangeName"></el-input>
                </el-form-item>
                <el-form-item label="VIP" prop="vip" >
                    <span slot="label">
                        <span>VIP</span>
                        <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                    </span>
                    <el-input @blur="cancelValid('vip')" @focus="cancelValid('vip')" v-model.trim="exchangeData.vip"></el-input>
                </el-form-item>
                <div class="edit-text">
                    <span>{{`Rollsite & Router Info`}}</span>
                    <el-button type="text" @click="addRollsite" icon="el-icon-circle-plus"></el-button>
                </div>
                <span class="rollsite-text" v-for="(item, index) in rollsiteList" :key="index">
                    <el-form-item label="Rollsite Network Access" prop="networkAccess" >
                        <span slot="label">
                            <span>Rollsite Network Access</span>
                            <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                        <el-input :class="{inputwarn: item.warnshow }"
                            @focus="item.warnshow = false"
                            @change="item.partyAddBeanList = []"
                            v-model.trim="item.networkAccess">
                        </el-input>
                        <el-button type="text"  :class="{remove:rollsiteList.length!==1}" :disabled="rollsiteList.length===1" @click="removeRollsite(index)"  icon="el-icon-remove"></el-button>
                    </el-form-item>
                    <div class="router-info">
                        <span style="color:#217AD9">Router Info
                            <i  class="el-icon-star-on"></i>
                        </span>
                        <el-button type="text" disabled  @click="showAddSiteNet(index)" icon="el-icon-circle-plus"></el-button>
                        <el-button type="text"  :disabled="!item.networkAccess"  @click="toAcquire(index)" icon="el-icon-refresh-right"></el-button>
                    </div>
                    <div class="edit-table">
                        <el-table :data="rollsiteList[index].partyAddBeanList" max-height="250" >
                            <el-table-column type="index" label="Index" width="70"  >
                            </el-table-column>
                            <el-table-column prop="partyId"  label="Party ID" width="70" show-overflow-tooltip>
                            </el-table-column>
                            <el-table-column prop="networkAccess"  label="Site Network Access" width="150">
                            </el-table-column>
                            <el-table-column prop="Update Time"  label="Update Time" width="150">
                                <template slot-scope="scope">
                                    <span>{{scope.row.updateTime | dateFormat}}</span>
                                </template>
                            </el-table-column>
                             <el-table-column prop="status" align="center"  label="Status" show-overflow-tooltip>
                                 <template slot-scope="scope">
                                    <span v-if="scope.row.status===1">Published</span>
                                    <span v-if="scope.row.status===2">Unpublished</span>
                                    <span v-if="scope.row.status===3">To be deleted</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="" align="right" label="Action">
                                <template slot-scope="scope">
                                    <span v-if="scope.row.partyId==='default'">
                                            <el-button disabled type="text" >
                                                <i  class="el-icon-edit"></i>
                                            </el-button>
                                            <el-button disabled type="text" >
                                                <i  class="el-icon-close"></i>
                                            </el-button>
                                        </span>
                                        <span v-else>
                                            <span v-if="scope.row.status===1 || scope.row.status===2">
                                                <el-button disabled type="text" >
                                                    <i @click="toEditSiteNet(scope)" class="el-icon-edit"></i>
                                                </el-button>
                                                <el-button disabled type="text" >
                                                    <i @click="scope.row.status=3" class="el-icon-close"></i>
                                                </el-button>
                                            </span>
                                            <el-button disabled @click="scope.row.status=2" v-if="scope.row.status===3" type="text" >
                                                recover
                                            </el-button>
                                        </span>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </span>

            </el-form>
            <div class="exchange-footer">
                <el-button class="ok-btn" type="primary" @click="toaction">OK</el-button>
                <el-button class="ok-btn" type="info" @click="tocancel">Cancel</el-button>
            </div>
        </div>
        <!-- 添加site NetWork -->
        <el-dialog :visible.sync="addSiteNet" class="add-site-dialog" width="500px" :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="site-net-title">
                Site Network Access
            </div>
            <div class="site-net-table">
                <el-form ref="siteNetform" class="edit-form" :rules="siteEditRules"  label-width="180px" label-position="left" :model="tempSiteNet">
                    <el-form-item label="" prop="partyId">
                        <span slot="label">
                            <span>Praty ID :</span>
                            <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                        <el-input @blur="$refs['siteNetform'].clearValidate('partyId')" @focus="$refs['siteNetform'].clearValidate('partyId')" v-model="tempSiteNet.partyId" ></el-input>
                    </el-form-item>
                    <el-form-item label="" prop="networkAccess" >
                        <span slot="label">
                            <span>Site Network Access :</span>
                            <i style="margin-left: 3px;" class="el-icon-star-on"></i>
                        </span>
                        <el-input @blur="$refs['siteNetform'].clearValidate('networkAccess')" @focus="$refs['siteNetform'].clearValidate('networkAccess')" v-model="tempSiteNet.networkAccess"></el-input>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button class="ok-btn" type="primary" @click="toAddSiteNet">OK</el-button>
                    <el-button class="ok-btn" type="info" @click="cancelAddSiteNet">Cancel</el-button>
                </div>
            </div>
        </el-dialog>
        <!-- 弹框离开 -->
        <el-dialog :visible.sync="isleavedialog" class="site-toleave-dialog" width="700px">
            <div class="line-text-one">Are you sure you want to leave this page ?</div>
            <div class="line-text-two">Your information will not be saved.</div>
            <div class="dialog-footer">
                <el-button class="sure-btn" type="primary" @click="sureLeave">Sure</el-button>
                <el-button class="sure-btn" type="info" @click="cancelLeave">Cancel</el-button>
            </div>
        </el-dialog>
  </div>
</template>

<script>
import { addIpchange, getNetworkAccessList } from '@/api/federated'
import moment from 'moment'
import checkip from '@/utils/checkip'
export default {
    name: 'ipaddexchange',
    components: {

    },
    filters: {
        dateFormat(value) {
            return moment(value).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            addSiteNet: false, // 弹框
            isleave: false, // 是否可以离开路由
            isleavedialog: false, // 离开弹框
            siteNetIndex: 0,
            rollsiteIndex: 0,
            rollsiteList: [{
                warnshow: false,
                networkAccess: '',
                partyAddBeanList: []
            } ],
            tempSiteNet: {}, // sitenet数据
            exchangeData: {},
            siteEditRules: {
                partyId: [{ required: true, message: ' ', trigger: 'bulr' }],
                networkAccess: [{ required: true, message: ' ', trigger: 'bulr' }]
            },
            editRules: {
                exchangeName: [{ required: true, message: ' ', trigger: 'bulr' }],
                vip: [ {
                    required: true,
                    trigger: 'bulr',
                    validator: (rule, value, callback) => {
                        value = value || ''
                        let val = value.trim()
                        if (!val || !checkip(val)) {
                            callback(new Error(' '))
                        } else {
                            callback()
                        }
                    }
                } ]
            }
        }
    },
    created() {
        // this.getInstitutionsToday()

        // this.getSiteToday()

    },

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
        // 添加循环
        addRollsite() {
            this.rollsiteList.unshift({ warnshow: false, networkAccess: '', partyAddBeanList: [] })
        },
        showAddSiteNet(index) {
            this.rollsiteIndex = index
            this.siteNetType = 'add'
            this.tempSiteNet = { status: 2 }
            this.addSiteNet = true
            if (this.$refs['siteNetform']) {
                this.$refs['siteNetform'].resetFields()
            }
        },
        // 去除列表值
        removeRollsite(index) {
            this.rollsiteList.splice(index, 1)
        },
        // 确定添加
        toaction() {
            let passAdd = true
            let msgshow = true
            let Arr = []
            let arr = []
            this.rollsiteList.forEach((item, index) => {
                if (!item.networkAccess || !checkip(item.networkAccess)) {
                    this.rollsiteList[index].warnshow = true
                }
                if (item.partyAddBeanList.length === 0) {
                    passAdd = false
                }
                Arr.push(item.networkAccess)
                arr = [...new Set(Arr)]
                // 重复提示弹框
                if (Arr.length !== arr.length) {
                    msgshow = false
                    this.rollsiteList[index].warnshow = true
                }
            })

            if (!passAdd) {
                this.$message.error('The Router Info is required')
                return
            }
            if (!msgshow) {
                this.$message.error('The rollsite network already exists')
                return
            }
            this.$refs['editform'].validate((valid) => {
                if (valid && passAdd && msgshow) {
                    let data = {
                        exchangeName: this.exchangeData.exchangeName.trim(),
                        rollSiteAddBeanList: this.rollsiteList,
                        vip: this.exchangeData.vip
                    }
                    addIpchange(data).then(res => {
                        this.isleave = true
                        this.$router.push({
                            name: 'IP Manage',
                            query: { type: 'exchange' }
                        })
                    })
                }
            })
        },
        // 确定取消
        tocancel() {
            this.$router.push({
                name: 'IP Manage',
                query: { type: 'exchange' }
            })
        },
        // 取消验证
        cancelValid(validtype) {
            this.$refs['editform'].clearValidate(validtype)
        },
        // 确定添加siteNet
        toAddSiteNet() {
            this.$refs['siteNetform'].validate((valid) => {
                if (valid) {
                    if (this.siteNetType === 'edit') {
                        this.rollsiteList[this.rollsiteIndex].partyAddBeanList[this.siteNetIndex] = { ...this.tempSiteNet }
                        this.rollsiteList[this.rollsiteIndex].partyAddBeanList = [...this.rollsiteList[this.rollsiteIndex].partyAddBeanList]
                        this.addSiteNet = false
                    } else if (this.siteNetType === 'add') {
                        this.rollsiteList[this.rollsiteIndex].partyAddBeanList.push({ ...this.tempSiteNet })
                        this.rollsiteList[this.rollsiteIndex].partyAddBeanList = [...this.rollsiteList[this.rollsiteIndex].partyAddBeanList]
                        this.addSiteNet = false
                    }
                }
            })
        },
        // 取消添加
        cancelAddSiteNet() {
            this.tempSiteNet = {}
            this.addSiteNet = false
            this.$refs['siteNetform'].resetFields()
        },
        // 编辑siteNet
        toEditSiteNet(index, scope) {
            this.rollsiteIndex = index
            this.siteNetType = 'edit'
            this.addSiteNet = true
            this.tempSiteNet = { ...scope.row }
            this.siteNetIndex = scope.$index
        },
        // 删除siteNet行
        todelSiteNet(index, scope) {
            this.rollsiteList[index].partyAddBeanList.splice(scope.$index, 1)
        },
        // 获取列表
        toAcquire(index) {
            let data = {
                networkAccess: this.rollsiteList[index].networkAccess
            }
            getNetworkAccessList(data).then(res => {
                this.rollsiteList[index].partyAddBeanList = res.data
            })
        },
        // 确定离开
        sureLeave() {
            this.isleave = true
            this.isleavedialog = false
            let query = this.leaveRouteName === 'IP Manage' ? { type: 'exchange' } : ''
            this.$router.push({
                name: this.leaveRouteName,
                query
            })
        },
        // 取消离开
        cancelLeave() {
            this.isleavedialog = false
        }
    }

}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ipaddexchange.scss';

</style>
