<template>
<div>
   <div class="row-content">
        <el-radio-group class="radio" v-model="activeName" @change="handleClick">
            <el-radio-button label="Auto-test"></el-radio-button>
            <el-radio-button label="Log"></el-radio-button>
        </el-radio-group>
    </div>
    <div v-if="activeName==='Auto-test'" class="deploying-body">
        <div class="row-activa">
            <el-button v-if='startStatus===1' :disabled="disabledStart"  type="text"  @click="toStart"  >
                <img class="disable" v-if='disabledStart'  src="@/assets/start.png">
                <img class="activa" v-else src="@/assets/start.png">
                <span>Start</span>
            </el-button>
            <el-button v-if='startStatus===2' :disabled="disabledStart" @click="toStart" type="text" >
                <img class="disable" v-if='disabledStart'  src="@/assets/retry.png">
                <img class="activa" v-else src="@/assets/retry.png">
                <span>Retry</span>
            </el-button>
            <el-button v-if='startStatus===3' :disabled="disabledStart" @click="toStart" type="text" >
                <img class="disable" v-if='disabledStart' src="@/assets/restart.png">
                <img class="activa" v-else src="@/assets/restart.png">
                <span>Restart</span>
            </el-button>
        </div>
        <div class="table">
            <el-table
                :data="tableData"
                :span-method="objectSpanMethod"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="100%"
                tooltip-effect="light"
                >
                <el-table-column prop="index" class-name="index" label="Index" width="100"></el-table-column>
                <el-table-column prop="testItem" label="Test item"></el-table-column>
                <el-table-column prop="duration" label="Test duration">
                     <template slot-scope="scope">
                            <span>{{scope.row.duration | durationFormat}}</span>
                        </template>
                </el-table-column>
                <el-table-column prop="status.desc" align="center" label="Status">
                    <template slot-scope="scope">
                        <span v-if="scope.row.testItem!=='Pod/SVC Status Of Each Component'">
                            <span v-if="scope.row.status&&scope.row.status.code===0" type="text">
                                <img src="@/assets/waiting.png" alt="" >
                            </span>
                            <span v-if="scope.row.status&&scope.row.status.code===1" disabled type="text">
                                <i style="color:#217AD9;font-size:18px" class="el-icon-loading"></i>
                            </span>
                            <span v-if="scope.row.status&&scope.row.status.code===2" disabled type="text">
                                <img src="@/assets/success.png" alt="">
                            </span>
                            <span v-if="scope.row.status&&scope.row.status.code===3" disabled type="text">
                                <img src="@/assets/failed.png" alt="" >
                            </span>
                        </span>
                    </template>
                </el-table-column>
                <!-- 并行测试，0.3版改为串行 -->
                <!-- <el-table-column prop="" align="center" label="Action">
                    <template slot-scope="scope">
                        <span v-if="itemkey.some(item=>scope.row.testItem===item)">
                            <el-button v-if="scope.row.status.code===3" @click="toRery(scope.row)" type="text">
                                Retry
                            </el-button>
                            <el-button v-else disabled type="text">
                                Retry
                            </el-button>
                        </span>
                    </template>
                </el-table-column> -->
            </el-table>
        </div>
        <div  class="btn">
            <el-button v-if="btntype==='primary'" type="primary" @click="toFinish" >Finish</el-button>
            <el-button v-else type="info" disabled >Finish</el-button>
        </div>
    </div>
    <div v-if="activeName==='Log'" class="deploying-body">
        <log  ref="log" />
    </div>
</div>

</template>

<script>
import { getTestList, autotestStart, toClick } from '@/api/fatedeploy'
import { toyTest, getTestLog } from '@/api/deploy'
import log from './log'

export default {
    name: 'pulltable',
    props: {
        formInline: {
            type: Object,
            default: function () {
                return {}
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
            startStatus: 1,
            activeName: 'Auto-test',
            disabledStart: false,
            //
            length: '',
            tableData: [],
            testItemList: {}, // 0.2并行模式 retry(重试)项临时入参数据
            podList: [], // 子项列表
            time: null,
            btntype: 'info',
            itemkey: ['Pod/SVC Status Of Each Component']
        }
    },
    components: { log },
    watch: {
        // $route: {
        // handler: function(val) {
        //     this.toPath()
        // },
        // immediate: true
        // }
    },
    computed: {
        // ...mapGetters(['siteName'])
    },

    beforeDestroy() {
        window.clearTimeout(this.time)
    },
    methods: {
        async initiTextList() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            let res = await getTestList(data)
            // 判断返回值是否为空
            if (!res.data || JSON.stringify(res.data.AutoTest) === '{}') {
                return [] // 如果为空,返回[]
            }
            // Pod/SVC Status Of Each Component 首项赋值
            this.length = res.data.AutoTest['Pod/SVC Status Of Each Component'].length + 1
            if ([...res.data.AutoTest['Pod/SVC Status Of Each Component']].every(item => item.status.code === 2)) {
                this.tableData = [{
                    testItem: 'Pod/SVC Status Of Each Component',
                    status: { code: 2, desc: '成功' },
                    index: 1
                }]
            } else if ([...res.data.AutoTest['Pod/SVC Status Of Each Component']].some(item => item.status.code === 3)) {
                this.tableData = [{
                    testItem: 'Pod/SVC Status Of Each Component',
                    status: { code: 3, desc: '失败' },
                    index: 1
                }]
            } else {
                this.tableData = [{
                    testItem: 'Pod/SVC Status Of Each Component',
                    status: { code: 0, desc: '等待' },
                    index: 1
                }]
            }
            // 0.2并行模式 Pod/SVC Status Of Each Component 子项以及其他项retry(重试)临时数据赋值
            for (const key in res.data.AutoTest) {
                if (key === 'Pod/SVC Status Of Each Component') {
                    this.tableData.push(...res.data.AutoTest[key])
                    let arr = []
                    res.data.AutoTest[key].map(item => {
                        arr.push(item.testItem)
                    })
                    this.podList[key] = [...arr]
                    this.testItemList[key] = [...arr]
                } else {
                    let arr = []
                    res.data.AutoTest[key].map(item => {
                        arr.push(item.testItem)
                    })
                    this.testItemList[key] = [...arr]
                }
            }
            // 其他项排序
            let ind = 2
            let ArrList = [] // 临时赋值
            let itemstrArr = ['Single Test', 'Toy Test', 'Minimize Fast Test', 'Minimize Normal Test']
            itemstrArr.forEach(elm => {
                for (const key in res.data.AutoTest) {
                    if (key === elm) {
                        [...res.data.AutoTest[key]].forEach(item => {
                            item.index = ind
                        })
                        ArrList.push(...res.data.AutoTest[key])
                        this.itemkey.push(key)
                        ind++
                        return
                    }
                }
            })

            // 重新赋值
            ArrList.forEach(item => {
                this.tableData.push(item)
            })

            return this.tableData
        },
        // 并行重试
        toRery(row) {
            let testItemList
            if (row.testItem === 'Pod/SVC Status Of Each Component') {
                testItemList = { 'Pod/SVC Status Of Each Component': [...this.podList['Pod/SVC Status Of Each Component']] }
            } else {
                testItemList = JSON.parse(`{"${row.testItem}":["${row.testItem}"]}`)
            }
            let data = {
                testItemList,
                federatedId: this.$parent.formInline.federatedId,
                partyId: this.$parent.formInline.partyId,
                productType: 1
            }
            toyTest(data).then(res => {
                this.initiTextList().then(res => {
                    if (res.some(item => item.testItem === row.testItem && item.status.code === 2)) {
                        return res
                    } else {
                        this.lessTime()
                    }
                })
            })
        },
        objectSpanMethod({ row, column, rowIndex, columnIndex }) {
            if (columnIndex === 0) {
                if (rowIndex === 0) {
                    return {
                        rowspan: this.length,
                        colspan: 1
                    }
                } else if (rowIndex < this.length) {
                    return {
                        rowspan: 0,
                        colspan: 0
                    }
                }
            }
        },
        // 切换log
        handleClick() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            if (this.activeName === 'Log') {
                this.disabledStart = true
                window.clearTimeout(this.time)
                getTestLog(data).then(res => {
                    this.$refs['log'].data = res.data
                    for (const key in res.data) {
                        res.data[key].forEach(item => {
                            this.$refs['log'].dataList.push(item)
                        })
                    }
                })
            } else {
                this.lessTime()
            }
        },
        toStart() {
            window.clearTimeout(this.time)
            let dataParams = {
                testItemList: this.testItemList,
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            autotestStart(dataParams).then(res => {
                this.lessTime()
            })
        },
        // //并行测试，0.3版改为串行
        // lessTime() {
        //     this.initiTextList().then(res => {
        //         if (res.every(item => item.status.code === 0)) { // 等待安装
        //             this.disabledStart = false
        //             this.startStatus = 1
        //         } else if (res.every(item => item.status.code === 2)) { // 安装成功
        //             // this.finish = true
        //             this.currentSteps.autoFinish = true
        //             this.startStatus = 1
        //             this.btntype = 'primary'
        //             this.disabledStart = true
        //         } else if (res.every(item => item.status.code === 3)) { // 安装失败
        //             this.startStatus = 3
        //             this.disabledStart = false
        //         } else if (res.every(item => item.status.code === 2 || item.status.code === 3)) { // 安装失败或者成功
        //             window.clearTimeout(this.time)
        //             this.startStatus = 2
        //             this.disabledStart = false
        //         } else {
        //             this.disabledStart = true
        //             this.time = setTimeout(() => {
        //                 this.lessTime()
        //             }, 1500)
        //         }
        //     })
        // },
        /// / 0.3串行模式
        lessTime() {
            this.initiTextList().then(res => {
                if (res.length > 0 && res.every(item => item.status.code === 0)) { // 等待测试
                    this.btntype = 'info'
                    this.disabledStart = false
                    this.startStatus = 1
                } else if (res.length > 0 && res.every(item => item.status.code === 2)) { // 测试成功
                    this.startStatus = 3
                    this.btntype = 'primary'
                    this.disabledStart = true
                    this.currentSteps.autoFinish = true
                } else if (res.length > 0 && res.some(item => item.status.code === 3)) { // 其中一个测试失败
                    this.btntype = 'info'
                    this.startStatus = 2
                    this.disabledStart = false
                } else if (res.length === 0) {
                    this.btntype = 'info'
                } else {
                    this.btntype = 'info'
                    this.disabledStart = true
                    this.time = setTimeout(() => {
                        this.lessTime()
                    }, 1500)
                }
            })
        },
        toFinish() {
            if (this.formInline.partyId) {
                let data = {
                    federatedId: this.formInline.federatedId,
                    partyId: this.formInline.partyId,
                    productType: 1,
                    clickType: 5
                }
                toClick(data).then(res => {
                    this.$router.push({
                        name: 'service',
                        query: {
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            siteName: this.formInline.siteName,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                })
            } else {
                this.$parent.showwarn = true
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/deploying.scss';
</style>
