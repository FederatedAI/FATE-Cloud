<template>
    <div class="table">
        <div class="table-head">
            <el-button class="route-add" type="text" @click="toAdd">
                <img src="@/assets/add_ip.png">
                <span>add</span>
            </el-button>
        </div>
        <el-table
            :data="tableData"
            ref="table"
            header-row-class-name="tableHead"
            header-cell-class-name="tableHeadCell"
            cell-class-name="tableCell"
            height="100%">
            <el-table-column prop="" type="index" width="70"  label="Index"  ></el-table-column>
            <el-table-column prop="exchangeName" label="Exchange"  show-overflow-tooltip></el-table-column>
            <el-table-column prop="networkAccessEntrances" label="Network Acess Entrance"  show-overflow-tooltip></el-table-column>
            <el-table-column prop="networkAccessExits" label="Network Acess Exit"  show-overflow-tooltip></el-table-column>
            <el-table-column prop="updateTime" label="Update Time"  show-overflow-tooltip>
                <template slot-scope="scope">
                    <span>{{scope.row.time | dateFormat}}</span>
                </template>
            </el-table-column>
            <el-table-column prop=""  width="70" label="Action"  show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-button type="text">
                        <i class="el-icon-edit edit" @click="handleEdit(scope.row)"></i>
                    </el-button>
                    <el-button type="text" >
                        <i class="el-icon-delete-solid delete" @click="handleDelete(scope.row)"></i>
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="pagination">
            <el-pagination
                background
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage1"
                :page-size="data.pageSize"
                layout="total, prev, pager, next, jumper"
                :total="total"
            ></el-pagination>
        </div>
        <!-- 删除 -->
        <el-dialog :visible.sync="deletedialog" class="access-delete-dialog" width="700px">
            <div class="line-text-one">Are you sure you want to delete this exchange?</div>
            <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="toDelet">Sure</el-button>
            <el-button class="ok-btn" type="info" @click="deletedialog = false">Cancel</el-button>
            </div>
        </el-dialog>
        <!-- 添加或编辑 -->
        <el-dialog :visible.sync="editdialog" class="access-edit-dialog" width="700px">
            <div class="dialog-title">
                {{type}}
                 Exchange
            </div>
            <div class="dialog-body">
                <el-form ref="editform" class="edit-form" :rules="editRules" :model="tempData">
                    <div class="edit-text">Exchange</div>
                    <el-form-item label="" prop="exchangeName" :class="{'exchange-warn':exchangewarnshow}">
                        <el-input @blur="cancelValid('exchange')" @focus="cancelValid('exchange')" v-model="tempData.exchangeName" ></el-input>
                    </el-form-item>
                    <div class="edit-text">Network Acess Entrance</div>
                    <el-form-item label="" prop="networkAccessEntrances" :class="{'entrance-warn':entrancewarnshow}">
                        <el-input @blur="cancelValid('entrance')" @focus="cancelValid('entrance')" v-model="tempData.networkAccessEntrances"></el-input>
                    </el-form-item>
                    <div class="edit-text">Network Acess Exit</div>
                    <el-form-item label="" prop="networkAccessExits" :class="{'exit-warn':exitwarnshow}">
                        <el-input @blur="cancelValid('exit')" @focus="cancelValid('exit')" v-model="tempData.networkAccessExits"></el-input>
                    </el-form-item>
                </el-form>
            </div>
            <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="toaction">Sure</el-button>
            <el-button class="ok-btn" type="info" @click="tocancel">Cancel</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getIpchangeList, addIpchange, editIpchange, deleteIpchange } from '@/api/federated'

import moment from 'moment'

export default {
    name: 'Ip',
    components: {},
    filters: {
        dateFormat(value) {
            return moment(value).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            exchangewarnshow: false,
            entrancewarnshow: false,
            exitwarnshow: false,
            deletedialog: false, // 删除框
            editdialog: false,
            type: '',
            currentPage1: 1, // 当前页
            tableData: [
                // { exchangeName: '456', networkAccessEntrances: '127.1.1.1', networkAccessExits: '127.1.1.2', updateTime: 1602576243473 },
                // { exchangeName: '789', networkAccessEntrances: '127.1.1.3', networkAccessExits: '127.1.1.4', updateTime: 1602576243473 }
            ],
            data: {
                pageNum: 1,
                pageSize: 20
            },
            total: 0,
            deleteExchangeId: '',
            tempData: {},
            editRules: {
                exchangeName: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.exchangewarnshow = true
                                callback(new Error('Please enter the Exchange'))
                            } else {
                                this.exchangewarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                networkAccessEntrances: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.entrancewarnshow = true
                                callback(new Error('Please enter the Network Acess Entrance'))
                            } else {
                                this.entrancewarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                networkAccessExits: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.exitwarnshow = true
                                callback(new Error('Please enter the Network Acess Exit'))
                            } else {
                                this.exitwarnshow = false
                                callback()
                            }
                        }
                    }
                ]
            }
        }
    },
    created() {
        this.initi()
    },
    mounted() {

    },
    methods: {
        initi() {
            let data = {
                pageNum: 1,
                pageSize: 20
            }
            getIpchangeList(data).then(res => {
                this.tableData = res.data.list
                this.total = res.data.totalRecord
            })
        },
        handleDelete(row) {
            this.deleteExchangeId = row.exchangeId
            this.deletedialog = true
        },
        handleSizeChange(val) {
            console.log(`每页 ${val} 条`)
        },
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        // 确认删除
        toDelet() {
            let data = {
                exchangeId: this.deleteExchangeId
            }
            deleteIpchange(data).then(res => {
                this.deletedialog = false
                this.initi()
            })
        },
        // 编辑
        handleEdit(row) {
            this.type = 'Edit'
            this.tempData = { ...row }
            this.editdialog = true
        },
        // 添加
        toAdd() {
            this.type = 'Add'
            this.tempData = {}
            this.editdialog = true
        },
        // 确认
        toaction() {
            this.$refs['editform'].validate((valid) => {
                if (valid) {
                    let data = { ...this.tempData }
                    if (this.type === 'Add') {
                        addIpchange(data).then(res => {
                            this.tocancel()
                            this.initi()
                        })
                    } else if (this.type === 'Edit') {
                        editIpchange(data).then(res => {
                            this.tocancel()
                            this.initi()
                        })
                    }
                }
            })
        },
        // 缺认取消
        tocancel() {
            this.tempData = {}
            this.$refs['editform'].resetFields()
            this.editdialog = false
            this.exchangewarnshow = false
            this.entrancewarnshow = false
            this.exitwarnshow = false
        },
        cancelValid(validtype) {
            this.$refs['editform'].clearValidate(validtype)
            this[`${validtype}warnshow`] = false
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ip.scss';

</style>
