<template>
    <div class="exchange-info">
        <div class="table-head">
            <el-button class="route-add" type="text" @click="toAddExchange">
                <img src="@/assets/add_ip.png">
                <span>{{$t('m.common.add')}}</span>
            </el-button>
            <el-button v-if='exchangeList.length' class="fold" type="text" @click="toFold" >
                <span v-if='activeName.length>0'>{{$t('m.common.foldAll')}}</span>
                <span v-else>{{$t('m.common.unfoldAll')}}</span>
            </el-button>
        </div>
        <span v-if='exchangeList.length'>
            <el-collapse  class="collapse" v-model="activeName" v-for="(item, index) in exchangeList" :key="index" >
                <el-collapse-item  :name="item.exchangeName">
                    <template slot="title" >
                        <el-tooltip class="item" effect="light" :content="item.exchangeName" placement="top">
                            <span style="color:#4E5766;font-weight:bold">{{item.exchangeName}}</span>
                        </el-tooltip>
                        <span class="collapse-title-vip">{{$t('m.ip.networkEntrances')}}：{{item.vipEntrance}}</span>
                        <span class="collapse-title-time"> {{$t('m.common.updateTime')}} ：{{item.updateTime | dateFormat}}</span>
                        <el-button style="margin-right:10px"  @click.stop="toAddRollsite(item)"  type="text">
                            {{$t('m.common.add')}} rollsite
                        </el-button>
                        <el-button  @click.stop="toDeleteExchangeId(item)"  type="text">
                            {{$t('m.common.delete')}}
                        </el-button>
                    </template>
                    <ipexchangetable ref="ipexchangetable" :exchangeId="item.exchangeId" />
                </el-collapse-item>
            </el-collapse>
        </span>
        <div class='no-data' v-else>{{$t('m.common.noData')}}</div>
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
        <el-dialog :visible.sync="deletedialog" class="access-delete-dialog" width="500px">
            <div class="line-text-one">
                {{$t('m.ip.sureWantDeleteExchange')}}
            </div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="toDelet">{{$t('m.common.sure')}}</el-button>
                <el-button class="ok-btn" type="info" @click="deletedialog = false">{{$t('m.common.cancel')}}</el-button>
            </div>
        </el-dialog>
        <ipaddrollsite ref="ipaddrollsite"/>
    </div>
</template>

<script>
import { getIpchangeList, deleteIpchange } from '@/api/federated'
import moment from 'moment'
import ipexchangetable from './ipexchangetable'
import ipaddrollsite from './ipaddrollsite'

export default {
    name: 'Ip',
    components: { ipexchangetable, ipaddrollsite },
    filters: {
        dateFormat(value) {
            return moment(value).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            activeName: [], // 折叠版激活
            isAllFold: false,
            exchangeList: [],
            deletedialog: false, // 删除框
            editdialog: false,
            addSiteNet: false,
            siteNetIndex: 0,
            currentPage1: 1, // 当前页
            tableData: [1, 2, 3], // 表格数据
            siteNetData: [],
            tempStatusStr: '{}',
            data: {
                pageNum: 1,
                pageSize: 20
            },
            total: 0,
            deleteExchangeId: '',
            exchangeData: {
                networkAccess: '',
                networkAccessExit: '',
                partyAddBeanList: []
            }, // 添加数据
            tempSiteNet: {} // sitenet数据

        }
    },
    created() {
        this.initList()
    },
    mounted() {

    },
    methods: {
        initList() {
            getIpchangeList(this.data).then(res => {
                this.exchangeList = res.data.list
                if (this.isAllFold) {
                    this.activeName = this.activeName.concat(this.exchangeList.map(item => item.exchangeName))
                }
                this.total = res.data.totalRecord
                setTimeout(() => {
                    this.exchangeList.forEach((item, index) => {
                        this.$refs['ipexchangetable'][index].togetRollsiteList()
                    })
                }, 500)
            })
        },
        // 点击准备删除
        toDeleteExchangeId(row) {
            this.deleteExchangeId = row.exchangeId
            this.deletedialog = true
        },
        // 确认删除
        toDelet() {
            let data = {
                exchangeId: this.deleteExchangeId
            }

            deleteIpchange(data).then(res => {
                this.deletedialog = false
                this.initList()
            })
        },
        handleSizeChange(val) {
            // console.log(`每页 ${val} 条`)
        },
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        // 跳转添加Exchange路由
        toAddExchange() {
            this.$store.dispatch('setSiteName', 'Add an Exchange')
            this.$store.dispatch('SetMune', 'IP Manage')
            this.$router.push({
                name: 'Add an Exchange',
                path: '/federated/ipexchange'
            })
        },
        // 点击add rollsite
        toAddRollsite(row) {
            this.$refs['ipaddrollsite'].exchangeId = row.exchangeId
            this.$refs['ipaddrollsite'].editdialog = true
            this.$refs['ipaddrollsite'].rollsiteType = 'add'
            // this.$refs['ipaddrollsite'].resetFields()
        },
        // 展开或者折叠
        toFold() {
            if (this.activeName.length > 0) {
                this.activeName = []
            } else {
                this.activeName = this.exchangeList.map(item => item.exchangeName)
            }
            this.isAllFold = !this.isAllFold
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/ip.scss';

</style>
