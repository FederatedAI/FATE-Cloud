<template>
  <div class="service">
    <div class="service-box">
        <div class="row-content">
            <el-radio-group class="radio" v-model="activeName" @change="handleClick">
                <el-radio-button label="FATE"></el-radio-button>
                <el-radio-button disabled label="FATE Serving"></el-radio-button>
            </el-radio-group>
            <div class="inline">
                <el-form :inline="true" :model="formInline" class="demo-form-inline" size="mini">
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
            <div v-if="showcontinue" class="continue" @click="toDeployment">
                {{$t('FATE in deployment, continue >>')}}
            </div>
            <div v-if="!showcontinue" class="toy-test">
                <span @click="totest"  v-if="!testloading">{{$t('Toy test')}}</span>
                <span v-else> <i  class="el-icon-loading test-loading"></i>{{$t('Testing')}}...</span>
            </div>
            <div v-if="!showcontinue"  @click="toShowLog" class="log">{{$t('Log')}}</div>
            <span v-if="!showcontinue" >
                <div v-if="upgradelist.length===0" class="upfate-default">
                    <span> {{$t('Upgrade FATE')}} </span>
                </div>
                <el-dropdown v-else trigger="click" class="upfate-dropdown" @command="handleCommand">
                    <div class="upfate-activa">
                        <el-badge is-dot type="warning">
                            <span type="text"> {{$t('Upgrade FATE')}} </span>
                        </el-badge>
                    </div>
                    <el-dropdown-menu class="upgrade-menu" slot="dropdown" >
                        <el-dropdown-item v-for="(item, index) in upgradelist" :key="index" :command="item">{{$t('upgrade to')}}
                            <span style="color:#217AD9">{{item.FateVersion}}</span>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </span>
        </div>
        <div v-if='showAnsible ' class="progress">
            <span v-if='ansiblePage===4'>
                FATE package acquisition...
            </span>
        </div>
        <div v-else class="service-body">
            <div  class="table">
            <el-table
                :data="tableData"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="100%"
                tooltip-effect="light"
            >
                <el-table-column type="index" :label="$t('Index')" width="70"></el-table-column>
                <el-table-column prop="componentName" :label="$t('FATE component')"  show-overflow-tooltip >
                    <template slot-scope="scope">
                        <span v-if="scope.row.componentName==='fateboard'" @click="tofateboard" style="color:#217AD9;cursor:pointer;" >{{scope.row.componentName}}</span>
                        <span v-else> {{scope.row.componentName}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="componentVersion" :label="$t('Version')"  >
                    <template slot-scope="scope">
                        <span>v{{scope.row.componentVersion}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="address" :label="$t('Node')" width="220" show-overflow-tooltip >
                    <template slot-scope="scope">
                        <div>{{scope.row.address}}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="finishTime"  :label="$t('Deployment time')"  show-overflow-tooltip width="200">
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
                <el-table-column prop="deployStatus.desc" :label="$t('Deploy status')"  width="130"></el-table-column>
                <el-table-column prop="status.desc" label="Status" align="center">
                    <template slot-scope="scope">
                        <span  v-if="scope.row.status.code===0">-</span>
                        <span v-else>{{scope.row.status.desc}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop=""  align="center" :label="$t('Action')" >
                    <template slot-scope="scope">
                        <span v-if="scope.row.status.code===0">-</span>
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
import { ansibletoyTest, ansibleUpgrade, ansibleLoge } from '@/api/fatedeployAnsible'
import { toupgrade, tofateboard } from '@/api/fatedeploy'
import moment from 'moment'
// 国际化
const local = {
    zh: {
        'Index': '序号',
        'FATE component': 'FATE服务组件',
        'Version': '版本',
        'Node': '节点',
        'Deployment time': '部署时间',
        'Deploy status': '部署状态',
        'Action': '操作',
        'FATE in deployment, continue >>': 'FATE部署中，继续下一步 >>',
        'Upgrade FATE': 'FATE版本升级',
        'upgrade to': '升级到',
        'Toy test': 'Toy 测试',
        'Testing': '测试中',
        'Log': '日志'
    },
    en: {
        'Index': 'Index',
        'FATE component': 'FATE component',
        'Version': 'Version',
        'Node': 'Node',
        'Deployment time': 'Deployment time',
        'Deploy status': 'Deploy status',
        'Action': 'Action',
        'FATE in deployment, continue >>': 'FATE in deployment, continue >>',
        'Upgrade FATE': 'Upgrade FATE',
        'upgrade to': 'upgrade to',
        'Toy test': 'Toy test',
        'Testing': 'Testing',
        'Log': 'Log'

    }
}

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
            showAnsible: false,
            ansiblePage: 0,
            // servicedialog: false,
            upgradedialog: false, // 确认升级弹框
            upgradeData: {},
            formInline: {
                partyId: parseInt(this.$route.query.partyId),
                siteName: this.$route.query.siteName,
                fateVersion: this.$route.query.fateVersion,
                deployType: this.$route.query.deployType// 部署类型
            },
            activeName: 'FATE',
            address: '',
            timeToyTimeless: null,
            testloading: false,
            upgradelist: [],
            showcontinue: false,
            timeLess: null // 定时器

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
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        this.init()
    },
    beforeDestroy() {
        window.clearTimeout(this.timeToyTimeless)
    },
    methods: {
        init() {
            this.getinitiList()
            this.getUpGradeList()
            this.testres()
            this.topagesStep()
            this.$store.dispatch('selectEnum', this.formInline.federatedId)
        },
        getinitiList() {
            let data = {
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
                } else {
                    this.showcontinue = true
                }
            })
        },
        async getUpGradeList() {
            this.upgradelist = []
            let data = {
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
                partyId: this.formInline.partyId,
                productType: 1
            }
            if (this.formInline.deployType === 1) {
                toLog(data).then(res => {
                    this.$refs['log'].logdialog = true
                    this.$refs['log'].data = res.data
                    for (const key in res.data) {
                        res.data[key].forEach(item => {
                            this.$refs['log'].dataList.push(item)
                        })
                    }
                })
            } else if (this.formInline.deployType === 2) {
                this.$refs['log'].data.all = []
                this.$refs['log'].dataList = []
                this.$refs['log'].logdialog = true
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
            }
        },
        testres() {
            let data = {
                testItem: 'Toy Test',
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
            if (this.formInline.deployType === 1) {
                let data = {
                    testItemList: { 'Toy Test': ['Toy Test'] },
                    partyId: this.formInline.partyId,
                    productType: 1,
                    ifonly: true
                }
                toyTest(data).then(res => {
                    this.testres()
                })
            } else if (this.formInline.deployType === 2) {
                let data = {
                    partyId: this.formInline.partyId,
                    productType: 1,
                    ifonly: true
                }
                ansibletoyTest(data).then(res => {
                    this.testres()
                })
            }
        },
        toAction(row, type) {
            let data = {
                action: type === 'stop' ? 0 : 1,
                componentName: row.componentName,
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
                name: this.formInline.deployType === 1 ? 'deploying' : 'ansible',
                query: {
                    partyId: this.formInline.partyId,
                    siteName: this.formInline.siteName,
                    fateVersion: this.formInline.fateVersion,
                    deployType: this.formInline.deployType// 部署类型
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
                partyId: this.formInline.partyId,
                productType: 1
            }
            if (this.formInline.deployType === 1) {
                toupgrade(data).then(res => {
                    this.$router.push({
                        name: 'deploying',
                        query: {
                            siteName: this.formInline.siteName,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion,
                            deployType: this.formInline.deployType, // 部署类型
                            upgrade: true
                        }
                    })
                })
            } else {
                ansibleUpgrade(data).then(res => {
                    this.$router.push({
                        name: 'ansible',
                        query: {
                            siteName: this.formInline.siteName,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion,
                            deployType: this.formInline.deployType, // 部署类型
                            upgrade: true
                        }
                    })
                })
            }
        },
        // 下拉改变partyip
        tochangepartyId() {
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
                this.init()
            })
            this.testloading = false
            window.clearTimeout(this.timeToyTimeless)
        },
        // 当前页按照
        topagesStep() {
            let data = {
                partyId: this.formInline.partyId,
                productType: 1
            }
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
                this.formInline.deployType = res.data.deployType.code
                switch (res.data.deployType.code) {
                case 1 :
                    this.$router.push({
                        name: 'service',
                        query: {
                            siteName: this.formInline.siteName,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion,
                            deployType: this.formInline.deployType
                        }
                    })
                    break
                case 2 :
                    if (res.data.pageStatus.code === 4) {
                        this.ansiblePage = res.data.pageStatus.code
                        this.showcontinue = true
                        this.showAnsible = true
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
                            name: 'service',
                            query: {
                                siteName: this.formInline.siteName,
                                partyId: this.formInline.partyId,
                                fateVersion: this.formInline.fateVersion,
                                deployType: this.formInline.deployType
                            }
                        })
                    }
                    break
                }
            })
        },

        // 跳转fateboard
        tofateboard() {
            let data = {
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

.upgrade-menu{
    width: 240px;
    margin-top:0 !important;

    .popper__arrow{
        display: none
    }
    .el-dropdown-menu__item{

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
