<template>
    <div class="site-body">
        <div class="table">
            <el-table
                :data="tableData"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                @filter-change="tofilter"
                max-height="255">
                <el-table-column prop type="index" label="Index" width="70"></el-table-column>
                <el-table-column prop="siteName" label="Site Name" show-overflow-tooltip min-width="85">
                <template slot-scope="scope">
                    <!-- 1 not joined,2 joined,3 removed -->
                    <span
                    v-if="scope.row.status ===2 || scope.row.status ===3"
                    class="todetail"
                    @click="sitedetail(scope.row)"
                    >{{scope.row.siteName}}</span>
                    <span
                    v-else-if="scope.row.status ===1"
                    class="todetail"
                    @click="siteInfo(scope.row)"
                    >{{scope.row.siteName}}</span>
                    <span v-else class="todetail">{{scope.row.siteName}}</span>
                </template>
                </el-table-column>
                <el-table-column prop="partyId" label="Party ID"></el-table-column>
                <el-table-column prop="networkAccessEntrances" label="Network Acess Entrances" width="220">
                <template slot-scope="scope">
                    <div v-if="scope.row.networkAccessEntrancesArr.length>2">
                        <el-popover
                            placement="bottom"
                            popper-class="scope"
                            :visible-arrow="false"
                            :offset="-40"
                            trigger="hover">
                            <div style="line-height: 25px;" v-for="(item, index) in scope.row.networkAccessEntrancesArr" :key="index" >{{item}}</div>
                            <div slot="reference" class="icon-caret">
                            <span>{{`${scope.row.networkAccessEntrancesArr[0]}...`}}</span>
                            <i class="el-icon-caret-bottom icon-caret" />
                            </div>
                        </el-popover>
                    </div>
                    <div  v-else>{{scope.row.networkAccessEntrances.split(';')[0]}}</div>
                </template>
                </el-table-column>
                <el-table-column prop="networkAccessExits" label="Network Access Exits" width="200">
                <template slot-scope="scope">
                    <div v-if="scope.row.networkAccessExitsArr.length>2">
                        <el-popover
                            placement="bottom"
                            popper-class="scope"
                            :visible-arrow="false"
                            :offset="-30"
                            trigger="hover">
                            <div style="line-height: 25px;" v-for="(item, index) in scope.row.networkAccessExitsArr" :key="index" >{{item}}</div>
                            <div slot="reference" class="icon-caret">
                                <span>{{`${scope.row.networkAccessExitsArr[0]}...`}}</span>
                                <i class="el-icon-caret-bottom" />
                            </div>
                        </el-popover>
                    </div>
                    <div v-else>{{scope.row.networkAccessExits.split(';')[0]}}</div>
                </template>
                </el-table-column>
                <el-table-column
                    :filters="roleTypeSelect"
                    :filter-multiple="false"
                    column-key="role"
                    filter-placement="bottom"
                    prop="role"
                    label="Role"
                    min-width="65" >
                    <template slot-scope="scope">
                        <span>{{scope.row.role===1?'Guest':'Host'}}</span>
                    </template>
                </el-table-column>
                <el-table-column
                    :filters="fateVersionSelect"
                    :filter-multiple="false"
                    column-key="fateVersion"
                    filter-placement="bottom"
                    prop="fateVersion"
                    label="FATE"
                    show-overflow-tooltip
                    min-width="80">
                    <template slot-scope="scope">
                        <span>{{scope.row.fateVersion}}</span>
                    </template>
                </el-table-column>
                <!-- <el-table-column
                    :filters="fateServingVersionSelect"
                    :filter-multiple="false"
                    column-key="fateServingVersion"
                    filter-placement="bottom"
                    prop="fateServingVersion"
                    label="FATE Serving"
                    min-width="110" show-overflow-tooltip></el-table-column> -->
                <el-table-column prop="activationTime" label="Activation Time" min-width="125" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <span>{{scope.row.activationTime | dateFormat}}</span>
                    </template>
                </el-table-column>
                <el-table-column
                    :filters="siteStatusSelect"
                    :filter-multiple="false"
                    column-key="status"
                    filter-placement="bottom"
                    prop="status"
                    label="Status"
                    min-width="90">
                    <template slot-scope="scope">
                        <span v-if="scope.row.status===1">Not Joined</span>
                        <span v-if="scope.row.status===2">Joined</span>
                        <span v-if="scope.row.status===3">Deleted</span>
                    </template>
                </el-table-column>
                <el-table-column prop="" label="Service Status" min-width="110" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <span>{{scope.row.detectiveStatus==='1'?'Unavailable':'Available'}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop label="Action" min-width="70" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" class="btn" disabled v-if="scope.row.status ===3">
                            <i class="el-icon-delete-solid "></i>
                        </el-button>
                        <el-button type="text" v-else>
                            <i class="el-icon-delete-solid delete" @click="handleDelete(scope.row)"></i>
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="pagina">
            <el-pagination
                background
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage"
                :page-size="data.pageSize"
                layout="total, prev, pager, next, jumper"
                :total="total"
            ></el-pagination>
        </div>
         <!-- 删除弹框 -->
        <el-dialog :visible.sync="dialogVisible" class="site-delete-dialog" width="700px">
            <div class="line-text-one">Are you sure you want to delete "{{ delSitename }}"?</div>
            <div class="line-text-two">You can't undo this action.</div>
            <div class="dialog-footer">
                <el-button class="ok-btn" type="primary" @click="okAction">OK</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>

import { siteDelete, siteList, getversion } from '@/api/federated'
import moment from 'moment'

export default {
    name: 'Siteatble',
    components: {},
    props: {
        institutions: {
            type: String,
            default: function() {
                return ''
            }
        },
        condition: {
            type: String,
            default: function() {
                return ''
            }
        }
    },
    filters: {
        dateFormat(value) {
            return value ? moment(value).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            total: 0, // 总共多少页
            currentPage: 1, // 当前页
            delSitename: '', // 删除项
            deleteRowId: '', // 删除id
            dialogVisible: false,
            tableData: [],
            fateVersionSelect: [], // fate下拉
            fateServingVersionSelect: [], // fateServingVersion下拉
            siteStatusSelect: [// status下拉
                //  1 not joined,2 joined,3 removed
                {
                    value: 1,
                    text: 'Not Joined'
                },
                {
                    value: 2,
                    text: 'Joined'
                },
                {
                    value: 3,
                    text: 'Delete'
                }
            ],
            roleTypeSelect: [// role下拉
                {
                    value: 1,
                    text: 'Guest'
                },
                {
                    value: 2,
                    text: 'Host'
                }
            ],
            data: {
                pageNum: 1,
                pageSize: 10
            }
        }
    },
    created() {
        this.$nextTick(res => {
            this.initList()
            this.togetversion('fate_version')
            this.togetversion('fate_serving_version')
        })
    },
    methods: {
        tofilter(value) {
            for (const key in value) {
                this.data[key] = value[key][0]
            }
            this.data.pageNum = 1
            this.currentPage = 1
            this.initList()
        },

        // 初始化表格
        initList() {
            this.tableData = []
            this.data.institutions = this.institutions
            this.data.condition = this.condition
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            siteList(this.data).then(res => {
                this.total = res.data.totalRecord
                res.data.list.forEach(element => {
                    element.networkAccessExitsArr = element.networkAccessExits.split(';')
                    element.networkAccessEntrancesArr = element.networkAccessEntrances.split(';')
                })
                this.tableData = [ ...res.data.list ]
            })
        },
        // 获取版本号
        togetversion(version) {
            let data = {
                institutions: this.institutions,
                versionName: version
            }
            getversion(data).then((res) => {
                res.data.forEach(item => {
                    let obj = {}
                    obj.value = item
                    obj.text = item
                    if (version === 'fate_version') {
                        this.fateVersionSelect.push(obj)
                    } else {
                        this.fateServingVersionSelect.push(obj)
                    }
                })
            })
        },
        handleSizeChange(val) {
            console.log(`每页 ${val} 条`)
        },
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        handleDelete(row) {
            this.dialogVisible = true
            this.deleteRowId = row.id
            this.delSitename = row.siteName
        },
        // 确认删除
        okAction() {
            let data = {
                id: this.deleteRowId
            }
            siteDelete(data).then(res => {
                this.dialogVisible = false
                this.initList()
            })
        },
        // 跳转详情
        sitedetail(row) {
            this.$store.dispatch('SiteName', row.siteName)
            this.$router.push({
                name: 'detail',
                path: '/federated/detail',
                query: {
                    id: row.id
                }
            })
        },
        // 查看详情
        siteInfo(row) {
            this.$store.dispatch('SiteName', row.siteName)
            this.$router.push({
                name: 'siteadd',
                path: '/federated/siteadd',
                query: {
                    type: 'siteinfo',
                    id: row.id
                }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/site.scss';
.site-history{
    margin-top: 0px !important;
    padding: 0;
    line-height:inherit;
    .content{
        padding: 12px 0px;
    }
     .title{
        padding: 0px 24px;
        color:#217AD9;
        font-size: 18px;
        font-weight:bold;
    }
    .content-box{
        padding: 0px 24px;
        max-height: 500px;
        overflow: auto;
        .title-time{
            color: #848C99
        }
        .title-history{
            color: #4E5766;
            .version{
                color: #217AD9;
            }
        }
    }
    .title-time{
        width: 28%;
        display:inline-block;
        margin: 12px 0;
    }
    .title-history{
        vertical-align: top;
        width: 70%;
        display:inline-block;
        margin: 12px 0;
    }

}
</style>
