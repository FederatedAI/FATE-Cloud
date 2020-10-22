<template>
  <div class="service">
    <div class="service-box">
      <div class="serve-title">
        <div class="Service-inline">Site service management</div>
        <div class="inline">
          <el-form :inline="true" :model="formInline" class="demo-form-inline" size="mini">
                <!-- <el-form-item label="Federated organization:">
                    <el-select v-model="formInline.federatedId" placeholder="" @change="changeFederatedId">
                        <el-option
                            v-for="item in organization"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item> -->
                <el-form-item label="PartyID:">
                    <el-select v-model="formInline.partyId" placeholder="" @change="tochangepartyId">
                        <el-option
                            v-for="item in partyId"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="Site name:" class="form-item">
                    <span style="color:#217AD9">{{formInline.siteName}}</span>
                </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="serve-content">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="FATE" name="FATE"></el-tab-pane>
          <el-tab-pane disabled label="FATE Serving" name="FATE Serving"></el-tab-pane>
        </el-tabs>
        <div class="empty"></div>
        <div v-if="showcontinue" class="continue" @click="toDeployment">FATE in deployment, continue >></div>
        <div v-if="!showcontinue" class="toy-test">
            <span @click="totest"  v-if="!testloading">Toy test</span>
            <span v-else> <i  class="el-icon-loading test-loading"></i>Testing...</span>
        </div>
        <div v-if="!showcontinue"  @click="toShowLog" class="log">Log</div>
        <span v-if="!showcontinue" >
            <div v-if="upgradelist.length===0" class="upfate-default">
                <span> Upgrade FATE </span>
            </div>
            <el-dropdown v-else trigger="click" class="upfate-dropdown" @command="handleCommand">
                <div class="upfate-activa">
                    <el-badge is-dot type="warning">
                        <span type="text"> Upgrade FATE </span>
                    </el-badge>
                </div>
                <el-dropdown-menu slot="dropdown" >
                    <el-dropdown-item v-for="(item, index) in upgradelist" :key="index" :command="item">upgrade to
                        <span style="color:#217AD9">{{item.FateVersion}}</span>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </span>
      </div>
      <div class="partyid-body">
        <div class="table">
          <el-table
            :data="tableData"
            header-row-class-name="tableHead"
            header-cell-class-name="tableHeadCell"
            cell-class-name="tableCell"
            height="100%"
            tooltip-effect="light"
          >
            <el-table-column type="index" label="Index" width="70"></el-table-column>
            <el-table-column prop="componentName" label="FATE component" show-overflow-tooltip width="130">
                <template slot-scope="scope">
                    <span v-if="scope.row.componentName==='fateboard'" @click="tofateboard" style="color:#217AD9;cursor:pointer;" >{{scope.row.componentName}}</span>
                    <span v-else> {{scope.row.componentName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="componentVersion" label="Version" >
                <template slot-scope="scope">
                    <span>v{{scope.row.componentVersion}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="address" label="Node" show-overflow-tooltip width="220">
                <template slot-scope="scope">
                    <div>{{scope.row.address}}</div>
                </template>
            </el-table-column>
            <el-table-column prop="finishTime" label="Deployment time" show-overflow-tooltip width="200">
                <template slot-scope="scope">
                    <span>{{scope.row.finishTime|dateFormat}}</span>
                </template>
            </el-table-column>
            <!-- <el-table-column prop="startTime" label="Start time" show-overflow-tooltip width="200">
                <template slot-scope="scope">
                    <span>{{scope.row.startTime|dateFormat}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="duration" label="Duration">
                <template slot-scope="scope">
                    <span>{{scope.row.duration | durationFormat}}</span>
                </template>
            </el-table-column> -->
            <el-table-column prop="deployStatus.desc" label="Deploy status" width="130"></el-table-column>
            <el-table-column prop="status.desc" label="Status" align="center">
                <template slot-scope="scope">
                    <span  v-if="scope.row.status.code===0">- -</span>
                    <span v-else>{{scope.row.status.desc}}</span>
                </template>
            </el-table-column>
            <el-table-column prop=""  align="center"  label="Operation">
                <template slot-scope="scope">
                    <span v-if="scope.row.status.code===0">- -</span>
                    <el-button @click="toAction(scope.row,'restart')" v-if="scope.row.status.code===1" type="text">
                       restart
                    </el-button>
                    <el-button @click="toAction(scope.row,'stop')" v-if="scope.row.status.code===2" type="text">
                        stop
                    </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <el-dialog
        :visible.sync="upgradedialog"
        class="servicedialog"
        width="800px"
        :close-on-click-modal="false"
        :close-on-press-escape="false">
        <div class="dialog-box" style="padding:20px">
            <div style="padding-bottom:10px">FATE {{upgradeData.FateVersion}} will be upgraded.</div>
            <div> This upgrade will not save data. Are you sure to continue the upgrade?</div>
        </div>
        <div class="dialog-foot">
            <el-button type="primary" @click="toSureUpgrade">Sure</el-button>
            <el-button type="info" @click="upgradedialog=false">Cancel</el-button>
        </div>
    </el-dialog>
    <servicelog ref="log"/>
    <servicedialog ref="service" :formInline="formInline"/>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import servicelog from './servicelog'
import servicedialog from './servicedialog'
import { getserviceList, toyTest, toLog, upGradeList, pagesStep, testresult, deployversion } from '@/api/deploy'
import { toupgrade, tofateboard } from '@/api/fatedeploy'
import moment from 'moment'

export default {
    name: 'service',
    components: { servicelog, servicedialog },
    filters: {
        dateFormat(vaule) {
            return vaule > 0 ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--:--:--'
        },
        durationFormat(mss) {
            function PadZero(str) {
                return new RegExp(/^\d$/g).test(str) ? `0${str}` : str
            }
            let str
            if (mss && mss > 1000) {
                var hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
                var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60))
                var seconds = parseInt((mss % (1000 * 60)) / 1000)
                str = PadZero(hours) + ':' + PadZero(minutes) + ':' + PadZero(seconds)
            } else if (mss === -1) {
                str = '--:--:--'
            } else {
                str = '00:00:00'
            }
            return str
        }
    },
    data() {
        return {
            tableData: [],
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            // servicedialog: false,
            upgradedialog: false, // 确认升级弹框
            upgradeData: {},
            formInline: {
                federatedId: parseInt(this.$route.query.federatedId),
                partyId: parseInt(this.$route.query.partyId),
                siteName: this.$route.query.siteName,
                fateVersion: this.$route.query.fateVersion
            },
            activeName: 'FATE',
            address: '',
            timeToyTimeless: null,
            testloading: false,
            upgradelist: [],
            showcontinue: false,
            timeLess: null// 定时器
        }
    },
    watch: {
        // showcontinue: {
        //     handler: function(val) {
        //         console.log('==>>', val)
        //     },
        //     immediate: true
        // }
    },
    computed: {
        ...mapGetters(['organization', 'partyId', 'version'])
    },
    created() {
        this.getinitiList()
        this.$store.dispatch('selectEnum', this.formInline.federatedId)
        this.topagesStep()
        this.getUpGradeList()
        this.testres()
    },
    beforeDestroy() {
        window.clearTimeout(this.timeToyTimeless)
    },
    methods: {
        getinitiList() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            this.tableData = []
            getserviceList(data).then(res => {
                if (res.data) {
                    this.tableData = [...res.data]
                    if ([...res.data].every(item => item.deployStatus.code === 0)) {
                        this.showcontinue = false
                    } else {
                        this.showcontinue = true
                    }
                }
            })
        },
        async getUpGradeList() {
            this.upgradelist = []
            let data = {
                federatedId: parseInt(this.formInline.federatedId),
                partyId: parseInt(this.formInline.partyId),
                productType: 1
            }
            let res = await upGradeList(data)
            if (res.data) {
                this.upgradelist = [...res.data]
            }
        },
        handleClick(tab, event) {
            // console.log(tab, event)
        },
        tostart() {

        },
        // 编辑
        handleEdit() {},
        // 翻页
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        toShowLog() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            toLog(data).then(res => {
                this.$refs['log'].logdialog = true
                this.$refs['log'].data = res.data
                for (const key in res.data) {
                    res.data[key].forEach(item => {
                        this.$refs['log'].dataList.push(item)
                    })
                }
            })
        },
        testres() {
            let data = {
                testItem: 'Toy Test',
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            this.timeToyTimeless = setTimeout(() => {
                testresult(data).then(res => {
                    if (res.data && res.data.code === 1) {
                        this.testloading = true
                        this.testres()
                    } else if (res.data && res.data.code === 2) {
                        this.testloading = false
                        this.$refs['service'].servicedialog = true
                        this.$refs['service'].type = 'test'
                        this.$refs['service'].title = 'Toy test passed.'
                    } else if (res.data && res.data.code === 3) {
                        this.testloading = false
                        this.$refs['service'].servicedialog = true
                        this.$refs['service'].type = 'test'
                        this.$refs['service'].title = 'Toy test failed.'
                    }
                })
            }, 1500)
        },
        totest() {
            this.testloading = true
            let data = {
                testItemList: { 'Toy Test': ['Toy Test'] },
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1,
                ifonly: true
            }
            toyTest(data).then(res => {
                this.testres()
            })
        },
        toAction(row, type) {
            let data = {
                action: type === 'stop' ? 0 : 1,
                componentName: row.componentName,
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            this.$refs['service'].servicedialog = true
            this.$refs['service'].type = type
            this.$refs['service'].params = data
            this.$refs['service'].title = `Are you sure you want to ${type} the service?`
        },
        // 前往部署页面
        toDeployment() {
            this.$router.push({
                name: 'deploying',
                query: {
                    federatedId: this.formInline.federatedId,
                    partyId: this.formInline.partyId,
                    siteName: this.formInline.siteName,
                    fateVersion: this.formInline.fateVersion
                }
            })
        },
        // 下拉更新
        handleCommand(command) {
            this.upgradeData.FateVersion = command.FateVersion
            this.upgradedialog = true
        },
        // 确认升级弹框
        toSureUpgrade() {
            let data = {
                fateVersion: this.upgradeData.FateVersion,
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            toupgrade(data).then(res => {
                this.$router.push({
                    name: 'deploying',
                    query: {
                        siteName: this.formInline.siteName,
                        federatedId: this.formInline.federatedId,
                        partyId: this.formInline.partyId,
                        fateVersion: this.formInline.fateVersion
                    }
                })
            })
        },
        // 下拉改变partyip
        tochangepartyId() {
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
            this.testloading = false
            window.clearTimeout(this.timeToyTimeless)
        },
        // 当前页按照
        topagesStep() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            pagesStep(data).then(res => {
                // debugger
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
                    this.getinitiList()
                    this.getUpGradeList()
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
        },
        // 下拉关闭组织
        // changeFederatedId(data) {
        //     this.$store.dispatch('selectEnum', data)
        //     this.formInline.partyId = ''
        //     this.formInline.siteName = ''
        // }
        // 跳转fateboard
        tofateboard() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId
            }
            tofateboard(data).then(res => {
                window.open(`http://${res.data}`)
            })
            // window.open
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/service.scss';

.el-dropdown-menu{
    width: 240px;
    margin-top:0 !important;
    .popper__arrow{
        display: none
    }
    .el-dropdown-menu__item{
        margin-left: 20px;
        width: 160px;
        color: #2D3642;
        text-align:center;
        font-size: 18px;
    }

}
.item-to{
    width: 180px;
    .item{
        margin:0 20px;
        width: 140px;
        padding:0
    }
}
</style>
