<template>
    <div>
        <div class="row-content">
            <el-radio-group class="radio" v-model="activeName" @change="handleClick">
                <el-radio-button label="FATE installation"></el-radio-button>
                <el-radio-button label="Log"></el-radio-button>
            </el-radio-group>
        </div>
        <div v-show="activeName==='FATE installation'" class="deploying-body">
            <div class="row-activa">
                <el-button v-if='startStatus===1' :disabled="disabledStart"  type="text"  @click="toStart"  >
                    <img class="disable" v-if='disabledStart'  src="@/assets/start.png">
                    <img class="activa" v-else src="@/assets/start.png">
                    <span>{{$t('Start')}}</span>
                </el-button>
                <el-button v-if='startStatus===2' :disabled="disabledStart" @click="toStart" type="text" >
                    <img class="disable" v-if='disabledStart'  src="@/assets/retry.png">
                    <img class="activa" v-else src="@/assets/retry.png">
                    <span>{{$t('Retry')}}</span>
                </el-button>
                <el-button v-if='startStatus===3' :disabled="disabledStart" @click="toStart" type="text" >
                    <img class="disable" v-if='disabledStart' src="@/assets/restart.png">
                    <img class="activa" v-else src="@/assets/restart.png">
                    <span>{{$t('Restart')}}</span>
                </el-button>
            </div>
            <div class="table" >
                <el-table
                :data="tableData"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="100%"
                tooltip-effect="light"
                >
                    <el-table-column type="index" :label="$t('Index')" width="70"></el-table-column>
                    <el-table-column prop="componentName" :label="$t('Installation item')" >
                        <template slot-scope="scope">
                            <span v-if="scope.row.componentName==='fateboard' && (scope.row.deployStatus.code===6 || scope.row.deployStatus.code===9)" @click="tofateboard" style="color:#217AD9;cursor:pointer;" >
                                {{scope.row.componentName}}
                                <el-tooltip effect="dark" placement="bottom">
                                    <div style="font-size:14px" slot="content">
                                        <div>Before accessing FATEBoard, please configure the host that corresponding to </div>
                                        <div>FATEBoard deployment machine IP, for example: 172.16.0.1 10000.fateboard.kubefate.net</div>
                                    </div>
                                    <i class="el-icon-info icon-info"></i>
                                </el-tooltip>
                            </span>
                            <span v-else> {{scope.row.componentName}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="address" label="Host IP"  width="350" show-overflow-tooltip>
                        <template slot-scope="scope">
                                <span class="ip-text">
                                    <tooltip :width="'200px'" :content="scope.row.address" :placement="'top'"/>
                                </span>
                                <el-popover
                                    placement="right"
                                    v-model="scope.row.edit"
                                    width="350"
                                    popper-class="fateinstall"
                                    trigger="click">
                                    <div class="content" >
                                        <div class="title">{{$t('Select host IP')}}</div>
                                        <div class="checkbox">
                                            <div class="checkbox-title">
                                                <span class="ip">IP</span>
                                                <span class="port">Port</span>
                                            </div>
                                            <!-- <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">all</el-checkbox> -->
                                            <!-- <el-checkbox-group v-model="checkedIpport" @change="handleCheckedCitiesChange"> -->
                                            <el-checkbox-group v-model="checkedIpport" @change="handlecheck">
                                                <div v-for="(ipaddress,index) in ipPortList" :key="ipaddress">
                                                    <el-checkbox :label="ipaddress" :disabled="disabledEditeList[index]">
                                                        <span> {{ipaddress.split(':')[0]}}</span>
                                                        <span v-if="showEditeList[index]" @click.prevent="toPrevent">
                                                            <span class="label-port" >{{ipaddress.split(':')[1]}}</span>
                                                            <div  class="edit-port" >
                                                                <i v-if="checkedIpport.indexOf(ipaddress)>-1" style="cursor:not-allowed;opacity:0.7" class="el-icon-edit"></i>
                                                                <i v-else @click.prevent="toEditPort(ipaddress.split(':')[1],index)" class="el-icon-edit"></i>
                                                            </div>
                                                        </span>
                                                        <span v-else @click.prevent="toPrevent" style="cursor:pointer;">
                                                            <el-input  class="input"  v-model="portList[index]" placeholder=""></el-input>
                                                            <div class="edit-port" >
                                                                <i @click.prevent="toEditCheck(index)" class="el-icon-check check"></i>
                                                                <i @click.prevent="toEditClose(index)" class="el-icon-close close"></i>
                                                            </div>
                                                        </span>
                                                    </el-checkbox>
                                                </div>
                                            </el-checkbox-group>
                                        </div>
                                        <div class="floor">
                                            <el-button type="primary" :disabled="ipSave" @click="toEdited(scope.row)" >Save</el-button>
                                            <el-button type="info"  @click="toClose(scope)">Cancel</el-button>
                                        </div>
                                    </div>
                                    <!-- 2 等待安装 6 安装成功 7安装失败 -->
                                    <i @click="toEdit(scope.row)" slot="reference"
                                    v-if="scope.row.deployStatus.code===2 || scope.row.deployStatus.code===3 ||scope.row.deployStatus.code===6 ||scope.row.deployStatus.code===7 || scope.row.deployStatus.code===9 || scope.row.deployStatus.code===10" class="el-icon-edit edit"></i>
                                </el-popover>
                        </template>
                    </el-table-column>
                    <el-table-column prop="duration"  :label="$t('Installation duration')"  >
                        <template slot-scope="scope">
                            <span>{{scope.row.duration | durationFormat}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="deployStatus.desc" align="center" :label="$t('Status')">
                        <template slot-scope="scope">
                            <!-- 2是waiting 4||5是进行中 6是成功 7是失败-->
                            <span v-if="scope.row.deployStatus.code===6 || scope.row.deployStatus.code===9" type="text">
                                <img src="@/assets/success.png" alt="">
                            </span>
                            <span v-if="scope.row.deployStatus.code===3 || scope.row.deployStatus.code===7|| scope.row.deployStatus.code===10"  type="text">
                                <img src="@/assets/failed.png" alt="" >
                            </span>
                            <span v-if="scope.row.deployStatus.code===4 || scope.row.deployStatus.code===5"  type="text">
                                <i style="color:#217AD9;font-size:18px" class="el-icon-loading"></i>
                            </span>
                            <span v-if="scope.row.deployStatus.code===2"  type="text">
                                <img src="@/assets/waiting.png" alt="" >
                            </span>
                        </template>
                    </el-table-column>
                    <!-- <el-table-column prop="duration" label="Action"  align="right" >
                        <template slot-scope="scope">
                            <span>{{scope.row.duration }}</span>
                        </template>
                    </el-table-column> -->
                </el-table>
            </div>
            <div  class="btn">
                <el-button v-if="btntype==='primary'" type="primary" @click="toNext" >Next</el-button>
                <el-button v-else type="info" disabled >Next</el-button>
            </div>
        </div>
        <div  v-if="activeName==='Log'" class="deploying-body">
            <log  ref="log" />
        </div>
    </div>

</template>

<script>
import { tofateboard } from '@/api/fatedeploy'
import event from '@/utils/event'
import { ansibleInstall, ansibleClick, ansibleLoge, getIpList, getAnsibleInstallList, ansibleUpdateIp } from '@/api/fatedeployAnsible'
import tooltip from '@/components/Tooltip'

import log from './log'
// 国际化
const local = {
    zh: {
        'Start': '开始',
        'Retry': '重试',
        'Restart': '重新开始',
        'Installation item': '安装项',
        'Cumulative active data': '累加活跃数据',
        'Installation duration': '安装时长',
        'Status': '状态',
        'Select host IP': '选择 host IP'

    },
    en: {
        'Start': 'Start',
        'Retry': 'Retry',
        'Restart': 'Restart',
        'Installation item': 'Installation item',
        'Cumulative active data': 'Cumulative active data',
        'Installation duration': 'Installation duration',
        'Status': 'Status',
        'Select host IP': 'Select host IP'

    }
}

export default {
    name: 'pulltable',
    components: {
        log,
        tooltip
    },
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
                return 5
            }
        },
        currentpage: {
            type: Number,
            default: function () {
                return 5
            }
        }
    },
    filters: {
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
            input: '',
            activeName: 'FATE installation',
            paramsData: {},
            btntype: 'info',
            startStatus: 1,
            disabledStart: false,
            time: null, // 定时器
            updataipstart: false, // 是否已经点击更新
            ipPortList: [], // 待选部分
            checkedIpport: [], // 已经选择
            tempcheckeIpPort: [], // 临时选中ip
            checkAll: false,
            isIndeterminate: true,
            portList: [], // 临时端口集合
            disabledEditeList: [],
            showEditeList: [], // 显示编辑
            ipSave: true
        }
    },
    watch: {
        page: {
            handler: function(val) {
                if (val === 5) {
                    this.lessTime()
                } else {
                    window.clearTimeout(this.time)
                }
            },
            immediate: true
        },
        currentpage: {
            handler: function(val) {
                // console.log('currentpage==>>', val)
            },
            immediate: true
        }
    },
    beforeDestroy() {
        window.clearTimeout(this.time)
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    mounted() {
    },
    methods: {
        async initiInstallList() {
            let data = {
                partyId: this.formInline.partyId,
                productType: 1
            }
            this.paramsData = data
            let res = await getAnsibleInstallList(data)
            this.tableData = res.data || []

            return this.tableData
        },
        toGetIplist(componentName) {
            this.ipPortList = []
            let params = {
                componentName
            }
            getIpList(params).then(res => {
                res.data.forEach(item => {
                    item.edit = false
                    this.showEditeList.push(true)
                    this.disabledEditeList.push(false)
                })
                res.data && res.data.forEach(item => {
                    this.ipPortList.push(`${item.ip}:${item.port}`)
                    if (this.tempcheckeIpPort === [`${item.ip}:${item.port}`]) {
                        this.checkedIpport = this.tempcheckeIpPort
                    }
                })
            }).catch(res => {
                this.toClose()
            })
        },
        // // 全选
        // handleCheckAllChange(val) {
        //     this.checkedIpport = val ? this.ipPortList : []
        //     this.isIndeterminate = false
        // },
        // // 全选
        // handleCheckedCitiesChange(value) {
        //     let checkedCount = value.length
        //     this.checkAll = checkedCount === this.ipPortList.length
        //     this.isIndeterminate = checkedCount > 0 && checkedCount < this.ipPortList.length
        // },
        // 激活编辑
        toEdit(row) {
            this.checkedIpport = []
            this.ipSave = true
            this.tempcheckeIpPort = row.address.split(';')
            this.toGetIplist(row.componentName)
        },
        // 关闭
        toClose(row) {
            let tempArr = []
            this.tableData.forEach((item, index) => {
                if (index === row.$index) {
                    item.edit = false
                }
                tempArr.push(item)
            })
            this.portList = this.portList.map(item => {
                item = ''
                return item
            })
            this.showEditeList = this.showEditeList.map(item => {
                item = true
                return item
            })
            this.disabledEditeList = this.disabledEditeList.map(item => {
                item = false
                return item
            })
            this.checkedIpport = []
            this.ipSave = true
            this.ipPortList = [...this.ipPortList]
            this.tableData = [...tempArr]
        },
        // 完成编辑
        toEdited(row) {
            if (!this.checkedIpport.join() || row.address === this.checkedIpport.join()) {
                this.tableData = this.tableData.map((item, index) => {
                    item.edit = false
                    return item
                })
                return
            }
            let data = {
                address: this.checkedIpport.join(),
                componentName: row.componentName,
                partyId: this.paramsData.partyId,
                productType: 1
            }
            ansibleUpdateIp(data).then(res => {
                this.toClose(row)
                this.updataipstart = true // 已经点更新
                this.disabledStart = false // 可以点击重新开始
                this.btntype = 'info'
                if (this.tableData.every(item => item.deployStatus.code === 6)) { // 安装成功
                    this.startStatus = 3
                }
                this.$message.success('success!')
                this.tableData = this.tableData.map((item, index) => {
                    item.edit = false
                    return item
                })
                // this.$parent.togetpages()
                this.initiInstallList()
                this.currentSteps.autoPrepare = false
                this.currentSteps.autoFinish = false
            })
        },

        toEditPort(val, index) {
            this.portList[index] = val
            this.disabledEditeList[index] = true
            this.showEditeList[index] = false
            this.ipPortList = [...this.ipPortList]
        },
        toEditCheck(index) {
            // 规则匹配
            let val = this.portList[index]
            let regNum = new RegExp('^[0-9]*$')
            if (val === '' || !regNum.test(val) || (parseInt(val) < 0 || parseInt(val) > 65535)) {
                this.$message.error('Invalid input!')
                return
            }
            if (this.checkedIpport[index] === this.ipPortList[index]) {
                this.checkedIpport[index] = this.ipPortList[index] = `${this.ipPortList[index].split(':')[0]}:${this.portList[index]}`
            } else {
                this.ipPortList[index] = `${this.ipPortList[index].split(':')[0]}:${this.portList[index]}`
            }
            this.showEditeList[index] = true
            this.disabledEditeList[index] = false
            this.portList[index] = ''
            this.ipPortList = [...this.ipPortList]
        },
        toEditClose(index) {
            this.showEditeList[index] = true
            this.disabledEditeList[index] = false
            this.portList[index] = ''
            this.ipPortList = [...this.ipPortList]
        },
        // 选中
        handlecheck(val) {
            if (val.length > 0) {
                this.ipSave = false
            } else {
                this.ipSave = true
            }
            this.ipPortList = [...this.ipPortList]
            // console.log('val==>>', val)
        },
        // 阻止默认事件
        toPrevent() {},
        // 点击开始
        toStart() {
            this.updataipstart = false // 已经点击开始
            window.clearTimeout(this.time)
            let data = {
                partyId: this.formInline.partyId,
                productType: 1
            }
            ansibleInstall(data).then(res => {
                this.disabledStart = true
                this.lessTime()
            })
        },
        toNext() {
            if (this.formInline.partyId) {
                let data = {
                    partyId: this.formInline.partyId,
                    productType: 1,
                    clickType: 6
                }
                ansibleClick(data).then(res => {
                    this.$parent.getAutoTest()
                    this.$parent.togetpages()
                })
            } else {
                this.$parent.showwarn = true
            }
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
                if (this.$parent.page !== this.$parent.currentpage) {
                    this.btntype = 'info'
                }
                if (this.updataipstart) {
                    this.disabledStart = false
                } else {
                    this.disabledStart = true
                }
            }
        },
        lessTime() {
            // 2是waiting 4||5是进行中 6是成功 7是失败
            this.initiInstallList().then(res => {
                if (res.length > 0 && res.every(item => item.deployStatus.code === 2)) { // 已经拉取，等待安装
                    event.$emit('myFunAnsible', true)
                    this.btntype = 'info'
                    this.updataipstart = true
                    this.disabledStart = false
                    this.startStatus = 1
                } else if (res.length > 0 && res.every(item => item.deployStatus.code === 6 || item.deployStatus.code === 9)) { // 已经安装、成功
                    event.$emit('myFunAnsible', false)
                    this.updataipstart = false
                    if (this.page !== this.currentpage) {
                        this.btntype = 'info'
                    } else {
                        this.btntype = 'primary'
                    }
                    this.disabledStart = true
                    this.startStatus = 3
                    this.currentSteps.instllFinish = true
                } else if (res.length > 0 && res.every(item => item.deployStatus.code === 3 || item.deployStatus.code === 7 || item.deployStatus.code === 10)) { // 安装失败
                    this.btntype = 'info'
                    this.updataipstart = true
                    this.startStatus = 2
                    this.disabledStart = false
                } else if (res.length > 0 && res.every(item => item.deployStatus.code === 6 || item.deployStatus.code === 7)) { // 安装成功或者失败
                    event.$emit('myFunAnsible', false)
                    this.btntype = 'info'
                    this.updataipstart = true
                    this.startStatus = 2
                    this.disabledStart = false
                } else if (res.length > 0 && res.some(item => item.deployStatus.code === 3 || item.deployStatus.code === 7 || item.deployStatus.code === 10)) { // 其中一个安装失败
                    event.$emit('myFunAnsible', false)
                    this.btntype = 'info'
                    this.updataipstart = true
                    this.startStatus = 2
                    this.disabledStart = false
                } else if (res.length === 0) {
                    this.btntype = 'info'
                } else {
                    event.$emit('myFunAnsible', false)
                    this.btntype = 'info'
                    this.disabledStart = true
                    this.time = setTimeout(() => {
                        this.lessTime()
                    }, 1500)
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
@import 'src/styles/deployansible.scss';
.fateinstall{
    padding: 0;

    .content{
        margin: 24px;
        .title{
            margin-bottom: 12px;
            font-size: 18px;
            color: #2D3642;
        }
        .checkbox{
            background-color: #FAFBFC;
            max-height: 250px;
            overflow: auto;
            .checkbox-title{
                margin: 10px 0;
                font-size: 14px;
                color: #4E5766;
                font-weight: bold;
                .ip{
                    margin-left: 35px;
                }
                .port{
                    margin-left: 105px;
                }
            }
            .el-checkbox{
                margin-bottom: 10px;
                margin-left: 10px;

                .label-port{
                    position: absolute;
                    left: 137px;
                }
                .edit-port{
                    color: #217AD9;
                    position: absolute;
                    top:0;
                    left: 195px;
                }
                .input{
                    position: absolute;
                    left: 133px;
                    .el-input__inner{
                        text-align: center;
                        background-color: #FAFBFC;
                        height: 16px;
                        width: 40px;
                        padding: 0;
                        border-bottom: 1px solid #ccc
                    }
                }

            }

        }
        .floor{
            margin-top: 24px;
            text-align: center;
            .el-button{
                width: 96px;
                height: 24px;
                line-height: 24px;
                padding: 0;
                font-size: 14px
            }
}

    }
}
</style>
