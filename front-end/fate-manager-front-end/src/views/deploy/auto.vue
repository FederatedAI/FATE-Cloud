<template>
  <div class="auto">
    <div class="auto-box">
        <div class="serve-title">
            <div class="Service-inline">Site Service Management</div>
            <div class="inline">
                <el-form :inline="true" :model="formInline" class="demo-form-inline" size="mini">
                    <el-form-item label="PartyID">
                        <el-select v-model="formInline.partyId" placeholder="" :class="{'option-select':showwarn}" @change="tochangepartyId">
                            <el-option
                                v-for="item in partyId"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                        <div v-if="showwarn" class="option-partyId">The PartyID is required.</div>
                    </el-form-item>
                    <el-form-item label="Site name :" class="form-item">
                        <span style="color:#217AD9">{{formInline.siteName }}</span>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <div class="row-content">
            <el-radio-group class="radio" v-model="activeName" @change="handleClick">
                <el-radio-button label="FATE"></el-radio-button>
                <el-radio-button disabled label="FATE Serving"></el-radio-button>
            </el-radio-group>
        </div>
        <div class="serve-content">
            <div class="table">
            <el-table
                :data="tableData"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="100%"
                tooltip-effect="light" >
                <el-table-column type="index" label="Index" width="90"></el-table-column>
                <el-table-column prop="componentName"  align="center" label="FATE component"></el-table-column>
                <el-table-column prop="componentVersion"  align="center" label="Version" ></el-table-column>
                <el-table-column prop="address"  align="center" label="Node"  ></el-table-column>
                <el-table-column prop="finishTime"  align="center" label="Deployment time"  ></el-table-column>
                <el-table-column prop="deployStatus.desc"  align="center" label="Deploy status" ></el-table-column>
                <el-table-column prop="status.desc" align="center" label="Status" ></el-table-column>
                <el-table-column prop="" align="right"  label="Action"></el-table-column>
            </el-table>
            </div>
            <div class="content">
            <div  class="text">I haven’t deployed any FATE components yet,
                <span class="text-click" @click="selectdialog = true">start deploying</span>
            </div>
            <div class="text">I have deployed FATE,
                <span @click="methoddialog = true" class="text-click">connect</span>
            </div>
            </div>
        </div>
    </div>
    <el-dialog :visible.sync="autodialog" class="auto-dialog" width="700px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
        <div class="dialog-title">
            {{typeUrl}}
            Service URL
        </div>
        <div :class="{input:true,'input-warn':warn}">
          <el-input v-model="address" @focus="warn=false" placeholder="Type the address like: 192.168.0.1 or 192.168.0.1:8080"></el-input>
          <div v-if="warn" class="text-warn" >Please enter the address.</div>
        </div>
        <div class="dialog-foot">
          <el-button type="primary" @click="toService">Start</el-button>
          <el-button type="info" @click="autodialog=false">Cancel</el-button>
        </div>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="connecdialog" class="auto-dialog" width="500px" :close-on-click-modal="false" :close-on-press-escape="false">
        <div class="dialog-box">
            <div style="text-align: center;font-size:24px">
                Connection failed !
            </div>
            <div class="dialog-foot">
                <el-button type="primary" @click="toRetry">Retry</el-button>
                <el-button type="info" @click="connecdialog=false">Cancel</el-button>
            </div>
        </div>
    </el-dialog>
    <el-dialog :visible.sync="selectdialog" class="select-dialog" width="700px" >
        <div class="dialog-title">
            Please select deployment method
        </div>
        <div class="dialog-box">
            <div class="dialog-foot">
                <el-button type="primary" @click="toAnsible">Deploy by Ansible</el-button>
            </div>
             <div class="dialog-foot">
                <el-button type="primary" @click="toKubeFATE">Deploy by KubeFATE</el-button>
            </div>
        </div>
    </el-dialog>
    <el-dialog :visible.sync="methoddialog" class="method-dialog" width="700px" >
        <div class="dialog-title">
            Please select deployment method
        </div>
        <div class="dialog-box">
            <el-radio-group v-model="radio" @change="changeMethod">
                <div :class="{input:true,'input-warn':warnKubeFATE}">
                    <el-radio :label="'KubeFATE'">By KubeFATE</el-radio>
                    <el-input v-model="addressKubeFATE" @focus="warnKubeFATE=false" :disabled="radio!=='KubeFATE'" placeholder="Type the KubeFATE address like: 192.168.0.1:8080"></el-input>
                    <div v-if="warnKubeFATE" class="text-warn" >The address is invalid. Please enter again.</div>
                </div>
                 <div :class="{input:true,'input-warn':warnAnsible}">
                    <el-radio :label="'Ansible'">By Ansible</el-radio>
                    <el-input v-model="addressAnsible" @focus="warnAnsible=false" :disabled="radio!=='Ansible'" placeholder="Type the deploy server address like: 192.168.0.1:8080"></el-input>
                    <div v-if="warnAnsible" class="text-warn" >The address is invalid. Please enter again.</div>
                </div>
            </el-radio-group>
        </div>
        <div class="dialog-foot">
            <el-button type="primary" @click="toConnection">OK</el-button>
            <el-button type="info" @click="toCancel">Cancel</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { toconnect, pagesStep, deployversion, connectansible } from '@/api/deploy'
import { toClick } from '@/api/fatedeploy'
import { ansibleClick } from '@/api/fatedeployAnsible'
// import {checkip} from '@/utils/checkip'

export default {
    name: 'auto',
    data() {
        return {
            tableData: [],
            connecdialog: false,
            showwarn: false,
            autodialog: false,
            selectdialog: false,
            methoddialog: false,
            warnKubeFATE: false,
            warnAnsible: false,
            warn: false,
            radio: 'KubeFATE',
            formInline: {
                federatedId: parseInt(this.$route.query.federatedId) || this.$route.query.federatedId,
                siteName: this.$route.query.siteName,
                partyId: parseInt(this.$route.query.partyId),
                fateVersion: this.$route.query.fateVersion,
                deployType: this.$route.query.deployType// 部署类型
            },
            activeName: 'FATE',
            address: '',
            addressKubeFATE: '',
            addressAnsible: '',
            tostatus: '',
            connectiontext: '',
            typeUrl: ''

        }
    },
    watch: {
        // $route: {
        // handler: function(val) {
        //     this.toPath()
        // },
        // immediate: true
        // }
    },
    computed: {
        ...mapGetters(['organization', 'partyId'])
    },
    // 防止刷新
    created() {
        this.$store.dispatch('selectEnum').then(res => {
            this.organization.forEach(item => {
                if (item.label === this.formInline.federatedId) {
                    this.formInline.federatedId = parseInt(item.value)
                }
            })
            this.$store.dispatch('selectEnum', this.formInline.federatedId).then(res => {
                this.topagesStep()
            })
        })
    },

    methods: {
        // tap切换
        handleClick(tab, event) {
            // console.log(tab, event)
        },
        // toStart() {
        //     this.selectdialog = true
        // },
        ansibleClick () {
            let data = {
                partyId: this.formInline.partyId,
                productType: 1,
                clickType: 1
            }
            ansibleClick(data).then(res => {
                this.topagesStep()
            })
        },
        kubefateClick() {
            let data = {
                partyId: this.formInline.partyId,
                productType: 1,
                clickType: 1
            }
            toClick(data).then(res => {
                this.topagesStep()
            })
        },
        toAnsible() {
            this.selectdialog = false
            this.autodialog = true
            this.typeUrl = 'Ansible'
            this.autodialog = true
            this.warn = false
            this.address = ''
        },
        toKubeFATE() {
            this.selectdialog = false
            this.typeUrl = 'KubeFATE'
            this.autodialog = true
            this.warn = false
            this.address = ''
        },
        toService() {
            this.connecdialog = false
            if (this.address) {
                if (this.typeUrl === 'Ansible') {
                    let data = {
                        ansbileUrl: this.address,
                        partyId: this.formInline.partyId,
                        productType: 1
                    }
                    connectansible(data).then(res => {
                        this.ansibleClick()
                    }).catch(res => {
                        this.connecdialog = true
                    })
                } else {
                    let data = {
                        partyId: this.formInline.partyId,
                        productType: 1,
                        kubenetesUrl: this.address
                    }
                    toconnect(data).then(rest => {
                        this.kubefateClick()
                    }).catch(res => {
                        this.connecdialog = true
                    })
                }
            } else {
                this.warn = true
            }
        },
        toserviceip() {
            if (this.formInline.partyId) {
                this.textTitle = 'KubeFATE address'
                this.textbtn = 'Ok'
                this.autodialog = true
                this.warn = false
                this.address = ''
            } else {
                this.showwarn = true
            }
        },
        // 切换
        changeMethod() {
            this.addressKubeFATE = ''
            this.addressAnsible = ''
            this.warnKubeFATE = false
            this.warnAnsible = false
        },
        toConnection() {
            if (this.radio === 'KubeFATE' && this.addressKubeFATE === '') {
                this.warnKubeFATE = true
            } else if (this.radio === 'Ansible' && this.addressAnsible === '') {
                this.warnAnsible = true
            } else if (this.radio === 'Ansible' && this.addressAnsible) {
                this.warnAnsible = false
                let data = {
                    ansbileUrl: this.addressAnsible,
                    partyId: this.formInline.partyId,
                    productType: 1
                }
                connectansible(data).then(res => {
                    this.toAnsible()
                }).catch(res => {
                    this.connecdialog = true
                })
            } else if (this.radio === 'KubeFATE' && this.addressKubeFATE) {
                let data = {
                    partyId: this.formInline.partyId,
                    productType: 1,
                    kubenetesUrl: this.addressKubeFATE || this.address
                }
                toconnect(data).then(rest => {
                    this.kubefateClick()
                }).catch(res => {
                    this.connecdialog = true
                })
            }
        },
        // 重试
        toRetry() {
            if (this.address) {
                this.toService()
            } else {
                this.toConnection()
            }
        },
        // 取消连接
        toCancel() {
            this.methoddialog = false
            this.addressKubeFATE = ''
            this.addressAnsible = ''
            this.warnKubeFATE = false
            this.warnAnsible = false
        },

        tochangepartyId() {
            this.showwarn = false
            this.partyId.forEach(element => {
                if (element.value === this.formInline.partyId) {
                    this.formInline.siteName = element.text
                }
            })
            let data = {

                partyId: this.formInline.partyId
            }
            deployversion(data).then(res => {
                this.formInline.fateVersion = res.data
                this.topagesStep()
            })
            // 获取步骤
        },
        topagesStep() {
            let data = {
                partyId: this.formInline.partyId,
                productType: 1
            }
            // 获取步骤
            pagesStep(data).then(res => {
                if (res.data.pageStatus.code === 0) {
                    this.$router.push({
                        name: 'auto',
                        query: {
                            siteName: this.formInline.siteName,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion,
                            deployType: this.formInline.deployType
                        }
                    })
                }
                switch (res.data.deployType.code) {
                case 1 :
                    if (res.data.pageStatus.code === 5) {
                        this.$router.push({
                            name: 'service',
                            query: {
                                siteName: this.formInline.siteName,
                                partyId: this.formInline.partyId,
                                fateVersion: this.formInline.fateVersion,
                                deployType: this.formInline.deployType
                            }
                        })
                    } else {
                        this.$router.push({
                            name: 'deploying',
                            query: {
                                siteName: this.formInline.siteName,
                                partyId: this.formInline.partyId,
                                fateVersion: this.formInline.fateVersion,
                                deployType: this.formInline.deployType
                            }
                        })
                    }
                    break
                case 2:
                    if (res.data.pageStatus.code === 7) {
                        this.$router.push({
                            name: 'service',
                            query: {
                                siteName: this.formInline.siteName,
                                partyId: this.formInline.partyId,
                                fateVersion: this.formInline.fateVersion,
                                deployType: this.formInline.deployType
                            }
                        })
                    } else {
                        this.$router.push({
                            name: 'ansible',
                            query: {
                                siteName: this.formInline.siteName,
                                partyId: this.formInline.partyId,
                                fateVersion: this.formInline.fateVersion,
                                deployType: this.formInline.deployType
                            }
                        })
                    }
                }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/auto.scss';
</style>
