<template>
  <div class="auto">
    <div class="auto-box">
      <div class="serve-title">
        <div class="Service-inline">Site Service Management</div>
        <div class="inline">
            <el-form :inline="true" :model="formInline" class="demo-form-inline" size="mini">
                <!-- <el-form-item label="Federated organization">
                    <el-select v-model="formInline.federatedId" placeholder="" @change="changeFederatedId">
                        <el-option
                            v-for="item in organization"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item> -->
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
      <div class="serve-content">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="FATE" name="FATE"></el-tab-pane>
          <!-- <el-tab-pane disabled label="FATE Serving" name="FATE Serving"></el-tab-pane> -->
        </el-tabs>
        <div class="empty"></div>
        <div class="table">
          <el-table
            :data="tableData"
            header-row-class-name="tableHead"
            header-cell-class-name="tableHeadCell"
            cell-class-name="tableCell"
            height="100%"
            tooltip-effect="light" >
            <el-table-column type="index" label="Index" width="90"></el-table-column>
            <el-table-column prop="componentName" label="FATE component"></el-table-column>
            <el-table-column prop="componentVersion" label="Version" ></el-table-column>
            <el-table-column prop="address" label="Node"  ></el-table-column>
            <el-table-column prop="finishTime" label="Deployment time"  ></el-table-column>
            <el-table-column prop="deployStatus.desc" label="Deploy status" ></el-table-column>
            <el-table-column prop="status.desc" label="Status" ></el-table-column>
            <el-table-column prop=""   label="Action"></el-table-column>
          </el-table>
        </div>
        <div class="content">
          <div  class="text">I haven’t deployed any FATE components yet,
            <span class="text-click" @click="toStart">start deploying</span>
          </div>
          <div class="text">I have deployed FATE by KubeFATE,
            <span @click="toserviceip" class="text-click">connect</span>
          </div>
        </div>
      </div>
    </div>
    <el-dialog :visible.sync="autodialog" class="auto-dialog" width="700px" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="dialog-box">
        <div class="dialog-title">
          {{textTitle}}
        </div>
        <div :class="{input:true,'input-warn':warn}">
          <el-input v-model="address" @focus="warn=false" placeholder="Type the address like: 192.168.0.1 or 192.168.0.1:8080"></el-input>
          <div v-if="warn" class="text-warn" >Please enter the address.</div>
        </div>
        <div class="dialog-foot">
          <el-button type="primary" @click="toService">{{textbtn}}</el-button>
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
                <el-button type="primary" @click="toService">Retry</el-button>
                <el-button type="info" @click="connecdialog=false">Cancel</el-button>
            </div>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { toconnect, pagesStep, deployversion } from '@/api/deploy'
// import checkip from '@/utils/checkip'

export default {
    name: 'auto',
    data() {
        return {
            tableData: [],
            connecdialog: false,
            showwarn: false,
            warn: false,
            autodialog: false,
            formInline: {
                federatedId: parseInt(this.$route.query.federatedId) || this.$route.query.federatedId,
                siteName: this.$route.query.siteName,
                partyId: parseInt(this.$route.query.partyId)
            },
            activeName: 'FATE',
            address: '',
            tostatus: '',
            connectiontext: '',
            textTitle: '',
            textbtn: ''
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
        toStart() {
            if (this.formInline.partyId) {
                this.textTitle = 'KubeFATE Service URL'
                this.textbtn = 'Start'

                this.autodialog = true
                this.warn = false
                this.address = ''
            } else {
                this.showwarn = true
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
        toService() {
            this.connecdialog = false
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1,
                kubenetesUrl: this.address
            }
            if (this.address) {
                toconnect(data).then(rest => {
                    // 获取步骤
                    this.topagesStep()
                }).catch(res => {
                    this.connecdialog = true
                })
            } else {
                this.warn = true
            }
        },
        // changeFederatedId(data) {
        //     this.$store.dispatch('selectEnum', data)
        //     this.formInline.partyId = ''
        //     this.formInline.siteName = ''
        // },
        tochangepartyId() {
            this.showwarn = false
            this.partyId.forEach(element => {
                if (element.value === this.formInline.partyId) {
                    this.formInline.siteName = element.text
                }
            })
            let data = {
                federatedId: this.formInline.federatedId,
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
                federatedId: this.formInline.federatedId,
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
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                } else if (res.data.pageStatus.code === 1) {
                    this.$router.push({
                        name: 'prepare',
                        query: {
                            siteName: this.formInline.siteName,
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                } else if (res.data.pageStatus.code === 2) {
                    this.$router.push({
                        name: 'deploying',
                        query: {
                            siteName: this.formInline.siteName,
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                } else {
                    this.$router.push({
                        name: 'service',
                        query: {
                            siteName: this.formInline.siteName,
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/auto.scss';
</style>
