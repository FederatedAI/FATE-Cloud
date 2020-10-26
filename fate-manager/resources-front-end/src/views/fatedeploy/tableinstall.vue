<template>
    <div>
        <div class="row-content">
            <div  class="row-type">
                <el-tabs v-model="activeName" @tab-click="handleClick">
                    <el-tab-pane label="Install image" name="installimage"></el-tab-pane>
                    <el-tab-pane label="Log" name="log"></el-tab-pane>
                </el-tabs>
                <div class="empty"></div>
                <div class="row-activa">
                    <el-button v-if='startStatus===1' :disabled="disabledStart"  @click="toStart" type="primary" >
                        <img src="@/assets/start.png" alt="">
                        <span>Start</span>
                    </el-button>
                    <el-button v-if='startStatus===2' :disabled="disabledStart" @click="toStart" type="primary" >
                        <img src="@/assets/restart.png" alt="">
                        <span>Retry</span>
                    </el-button>
                    <el-button v-if='startStatus===3' :disabled="disabledStart" @click="toStart" type="primary" >
                        <img src="@/assets/restart.png" alt="">
                        <span>Restart</span>
                    </el-button>
                </div>
            </div>
        </div>
        <div v-show="activeName==='installimage'" class="deploying-body">
            <div class="table" >
                <el-table
                :data="tableData"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="100%"
                tooltip-effect="light"
                >
                    <el-table-column type="index" label="Index" width="70"></el-table-column>
                    <el-table-column prop="componentName" label="Installation item">
                        <template slot-scope="scope">
                            <span v-if="scope.row.componentName==='fateboard'" @click="tofateboard" style="color:#217AD9;cursor:pointer;" >
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
                    <el-table-column prop="address" label="IP"  width="350">
                        <template slot-scope="scope">
                            <span v-if="!scope.row.edit" type="text">
                                <span class="ip-text">{{scope.row.address}}</span>
                                <!-- 0和6 安装成功 7 安装失败 8 测试中，安装成功 9 第三步测试成功 ，10第三步测试失败 -->
                                <i @click="toEdit(scope)" v-if="scope.row.deployStatus.code===0||scope.row.deployStatus.code===2 ||scope.row.deployStatus.code===6||scope.row.deployStatus.code===7 ||scope.row.deployStatus.code===8 ||scope.row.deployStatus.code===9 ||scope.row.deployStatus.code===10" class="el-icon-edit edit"></i>
                            </span>
                            <span  v-if="scope.row.edit" >
                                <el-input v-model="input" id="close" placeholder="请输入内容"></el-input>
                                <i @click="toEdited(scope.row)" class="el-icon-check close"></i>
                                <i @click="toClose(scope)" class="el-icon-close close-a"></i>
                            </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="duration" label="Installation duration">
                        <template slot-scope="scope">
                            <span>{{scope.row.duration | durationFormat}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="deployStatus.desc" align="center" label="Status">
                        <template slot-scope="scope">
                            <!-- 6 安装成功 9 第三步测试成功 ，10第三步测试失败 -->
                            <span v-if="scope.row.deployStatus.code===0||scope.row.deployStatus.code===6||scope.row.deployStatus.code===8||scope.row.deployStatus.code===9 ||scope.row.deployStatus.code===10" type="text">
                                <img src="@/assets/success.png" alt="">
                            </span>
                            <span v-if="scope.row.deployStatus.code===7 || scope.row.deployStatus.code===3"  type="text">
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
                </el-table>
            </div>
            <div  class="btn">
                <el-button v-if="btntype==='primary'" type="primary" @click="toNext" >Next</el-button>
                <el-button v-else type="info" disabled >Next</el-button>
            </div>
        </div>
        <div  v-if="activeName==='log'" class="deploying-body">
            <log  ref="log" />
        </div>
    </div>

</template>

<script>
import { getInstallList, updateIp, installStart, toClick, tofateboard } from '@/api/fatedeploy'
import checkip from '@/utils/checkip'
import { toLog } from '@/api/deploy'
import log from './log'
import event from '@/utils/event'
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
            activeName: 'installimage',
            paramsData: {},
            btntype: 'info',
            startStatus: 1,
            disabledStart: true,
            time: null, // 定时器
            updataipstart: false // 是否已经点击更新
        }
    },
    components: { log },

    beforeDestroy() {
        window.clearTimeout(this.time)
    },
    methods: {
        async initiInstallList() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            this.paramsData = data
            let res = await getInstallList(data)
            this.tableData = res.data || []
            this.tableData.forEach(item => {
                item.edit = false
            })
            return this.tableData
        },
        // 激活编辑
        toEdit(row) {
            let tempArr = []
            this.tableData.forEach((item, index) => {
                if (index === row.$index) {
                    this.input = item.address
                    item.edit = true
                } else {
                    item.edit = false
                }
                tempArr.push(item)
            })
            this.tableData = [...tempArr]
            // 锁定焦点
            this.$nextTick(() => {
                let onFocus = document.querySelector('#close')
                onFocus.focus()
            })
        },
        // 完成编辑
        toEdited(row) {
            if (!checkip(this.input)) {
                this.$message.error('The address is invalid. Please enter again!')
                return
            }
            if (row.address === this.input) {
                this.tableData = this.tableData.map((item, index) => {
                    item.edit = false
                    return item
                })
                return
            }
            let data = {
                address: this.input,
                componentName: row.componentName,
                federatedId: this.paramsData.federatedId,
                partyId: this.paramsData.partyId,
                productType: 1
            }
            updateIp(data).then(res => {
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
                this.initiInstallList()
                this.$parent.currentpage = 3 // 将当前页转为当前步骤
                this.currentSteps.autoPrepare = false
                this.currentSteps.autoFinish = false
            })
        },
        // 关闭
        toClose(row) {
            let tempArr = []
            this.tableData.forEach((item, index) => {
                if (index === row.$index) {
                    item.edit = false
                    this.input = ''
                }
                tempArr.push(item)
            })
            this.tableData = [...tempArr]
        },
        // 点击开始
        toStart() {
            this.updataipstart = false // 已经点击开始
            window.clearTimeout(this.time)
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            installStart(data).then(res => {
                this.disabledStart = true
                this.lessTime()
            })
        },
        toNext() {
            if (this.formInline.partyId) {
                let data = {
                    federatedId: this.formInline.federatedId,
                    partyId: this.formInline.partyId,
                    productType: 1,
                    clickType: 4
                }
                toClick(data).then(res => {
                    this.$parent.togetpages()
                })
            } else {
                this.$parent.showwarn = true
            }
        },
        handleClick() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            if (this.activeName === 'log') {
                this.disabledStart = true
                window.clearTimeout(this.time)
                toLog(data).then(res => {
                    this.$refs['log'].data = res.data
                    for (const key in res.data) {
                        res.data[key] && res.data[key].forEach(item => {
                            this.$refs['log'].dataList.push(item)
                        })
                    }
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
            this.initiInstallList().then(res => {
                if (res.length > 0 && res.every(item => item.deployStatus.code === 2)) { // 已经拉取，等待安装
                    event.$emit('myFun', false)
                    this.btntype = 'info'
                    this.updataipstart = true
                    this.disabledStart = false
                    this.startStatus = 1
                } else if (res.length > 0 && res.every(item => item.deployStatus.code === 6 || item.deployStatus.code === 8)) { // 已经安装、成功
                    this.updataipstart = false
                    this.btntype = 'primary'
                    this.disabledStart = true
                    this.currentSteps.instllFinish = true
                } else if (res.length > 0 && res.every(item => item.deployStatus.code === 7)) { // 安装失败
                    this.btntype = 'info'
                    this.updataipstart = true
                    this.startStatus = 2
                    this.disabledStart = false
                } else if (res.length > 0 && res.every(item => item.deployStatus.code === 6 || item.deployStatus.code === 7)) { // 安装成功或者失败
                    this.btntype = 'info'
                    this.updataipstart = true
                    this.startStatus = 2
                    this.disabledStart = false
                } else if (res.length === 0) {
                    this.btntype = 'info'
                } else {
                    event.$emit('myFun', true)
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
@import 'src/styles/deploying.scss';

</style>
