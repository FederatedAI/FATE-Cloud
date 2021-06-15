<template>
    <div>
        <div  class="deploying-body">
            <div class="row-activa" >
                <div class="version">
                    <el-select v-model="formInline.fateVersion" :disabled="disVersion" placeholder="Version" @change="toVersion">
                        <el-option
                            v-for="item in version"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <el-button @click="toPull" :disabled="distopull" type="text" >
                    <img class="disable" v-if='distopull'  src="@/assets/pull.png">
                    <img class="activa" v-else src="@/assets/pull.png">
                    <span>Pull</span>
                </el-button>
            </div>
            <div class="warn-box">
                <i class="el-icon-warning"></i>
                The current images are stored on Master, please ensure that the docker images of Node are consistent with the Master’s, otherwise the installation will fail.
            </div>
            <div class="table" style="height: calc(100% - 201px)">
                <el-table
                :data="tableData"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                height="100%"
                tooltip-effect="light"
                >
                    <el-table-column type="index" label="Index" width="70"></el-table-column>
                    <el-table-column prop="imageName" label="Image" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="imageId" label="Image ID" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="iamgeTag" label="Tag"></el-table-column>
                    <el-table-column prop="iamgeDescription" label="Description"></el-table-column>
                    <el-table-column prop="imageVersion" label="Version" ></el-table-column>
                    <el-table-column prop="iamgeSize" label="Size"></el-table-column>
                    <el-table-column prop="imageCreateTime" label="Created" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <span>{{scope.row.imageCreateTime | dateFormat}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="pullStatus.desc" align="center" label="Status">
                        <template slot-scope="scope">
                            <span v-if="scope.row.pullStatus.code===0" >
                                <img src="@/assets/waiting.png" alt="" >
                            </span>
                            <span v-if="scope.row.pullStatus.code===1">
                                <img src="@/assets/success.png" alt="">
                            </span>
                            <span v-if="scope.row.pullStatus.code===2">
                                <img src="@/assets/failed.png" alt="" >
                            </span>
                            <span v-if="scope.row.pullStatus.code===3">
                                <i style="color:#217AD9;font-size:18px" class="el-icon-loading"></i>
                            </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="log" align="center" label="Action">
                        <template slot-scope="scope">
                            <el-button v-if="scope.row.pullStatus.code ===1 || scope.row.pullStatus.code ===0" disabled type="text">
                                Retry
                            </el-button>
                            <el-button v-if="scope.row.pullStatus.code===2" @click="toRetry(scope.row)" type="text">
                                Retry
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div  class="btn">
                <el-button v-if="btntype==='primary'" type="primary" @click="toNext" >Next</el-button>
                <el-button v-else type="info" disabled >Next</el-button>
            </div>
        </div>
    </div>

</template>

<script>

import { getPullList, pull, tofistnext } from '@/api/fatedeploy'
import moment from 'moment'
import { mapGetters } from 'vuex'
import { setTimeout } from 'timers'
import event from '@/utils/event'

export default {
    name: 'pulltable',
    filters: {
        dateFormat(vaule) {
            return vaule > 0 ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '- -'
        }
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
        }
    },
    data() {
        return {
            tableData: [],
            pullComponentList: [], // 拉取镜像名称列表
            distopull: true,
            disVersion: false,
            btntype: 'info'
        }
    },
    computed: {
        ...mapGetters(['version'])
    },
    mounted() {
        event.$on('myFun', (msg) => {
            this.disVersion = msg
        })
    },
    methods: {
        async initiPullList() {
            this.tableData = []
            let data = {
                fateVersion: this.formInline.fateVersion,
                productType: 1
            }
            let res = await getPullList(data)
            this.tableData = res.data || []
            this.tableData.forEach(item => {
                this.pullComponentList.push(item.imageName)
            })
            return this.tableData
        },
        toRetry(row) {
            let data = {
                fateVersion: this.formInline.fateVersion,
                productType: 1,
                pullComponentList: [row.imageName]
            }
            pull(data).then(res => {
                this.toPull()
            })
        },
        // 拉版本号
        toVersion() {
            this.currentSteps.pullPrepare = true
            this.currentSteps.instllPrepare = false
            this.btntype = 'info'
            this.lessTime()
            this.distopull = false
        },
        // 点击拉取
        toPull() {
            let data = {
                fateVersion: this.formInline.fateVersion,
                productType: 1,
                pullComponentList: this.pullComponentList
            }
            pull(data).then(res => {
                this.lessTime()
                this.distopull = true
            })
        },
        toNext() {
            if (this.formInline.partyId) {
                let data = {
                    fateVersion: this.formInline.fateVersion,
                    federatedId: this.formInline.federatedId,
                    partyId: this.formInline.partyId,
                    productType: 1
                }
                tofistnext(data).then(res => {
                    this.$parent.togetpages()
                })
            } else {
                this.$parent.showwarn = true
            }
        },
        // 开启循环
        lessTime() {
            this.initiPullList().then(res => {
                if (res.length > 0 && res.every(item => item.pullStatus.code === 0)) { // 等待
                    this.distopull = false
                    this.btntype = 'info'
                } else if (res.length > 0 && res.every(item => item.pullStatus.code === 1)) { // 成功
                    this.currentSteps.pullPrepare = true
                    this.currentSteps.pullFinish = true
                    // 点击拉取pull后，next按钮才能点击
                    if (this.distopull) {
                        this.btntype = 'primary'
                    }
                } else if (res.length > 0 && res.every(item => item.pullStatus.code === 2)) { // 失败
                    this.distopull = false
                } else if (res.length > 0 && res.every(item => item.pullStatus.code === 1 || item.pullStatus.code === 2)) { // 成功或失败
                    this.distopull = false
                    this.btntype = 'info'
                } else if (res.length === 0) {
                    this.btntype = 'info'
                } else {
                    this.distopull = true
                    this.btntype = 'info'
                    setTimeout(() => {
                        this.lessTime()
                    }, 2000)
                }
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/deploying.scss';
</style>
