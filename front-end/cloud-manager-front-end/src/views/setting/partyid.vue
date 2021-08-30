<template>
  <div class="partyid-box">

    <div class="partyid-header">
        <el-button class="add" type="text" @click="addPartyid">
                <img src="@/assets/add_ip.png">
            <span>{{$t('add')}}</span>
        </el-button>
        <el-input class="input input-placeholder" clearable v-model.trim="data.groupName" :placeholder="$t('Search ID Group')">
            <!-- <i slot="suffix" @click="toSearch" class="el-icon-search search el-input__icon" /> -->
        </el-input>
        <el-select class="sel-institutions input-placeholder" v-model="data.role" clearable :placeholder="$t('Type')">
            <el-option
            v-for="item in typeSelect"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            ></el-option>
        </el-select>
        <el-button class="go" type="primary" @click="toSearch">{{$t('m.common.go')}}</el-button>
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
            <el-table-column type="index" :label="$t('Index')" width="170"></el-table-column>
            <el-table-column prop="groupName" :label="$t('ID Group')" ></el-table-column>
            <el-table-column prop="role" :label="$t('Type')">
                <template slot-scope="scope">
                    <span>{{scope.row.role===1 ? $t('m.common.guest') : $t('m.common.host') }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="rangeInfo" :label="$t('ID range')" show-overflow-tooltip></el-table-column>
            <el-table-column prop="total" :label="$t('Total')" ></el-table-column>
            <el-table-column prop="used" :label="$t('Used')" >
                <template slot-scope="scope">
                <span class="delete" @click="toUser(scope.row)">{{scope.row.used}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" :label="$t('Action')" width="120" align="center">
                <template slot-scope="scope">
                    <el-button type="text">
                        <i class="el-icon-edit edit" @click="handleEdit(scope.row)"></i>
                    </el-button>
                    <el-button type="text" v-if="scope.row.used" disabled >
                        <i class="el-icon-delete-solid "></i>
                    </el-button>
                    <el-button type="text" v-else>
                        <i class="el-icon-delete-solid delete" @click="handleDelete(scope.row)"></i>
                    </el-button>
                </template>
            </el-table-column>
            </el-table>
        </div>
        <div class="pagination">
            <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :page-size="data.pageSize"
            layout="total, prev, pager, next, jumper"
            :total="total"
            ></el-pagination>
        </div>
    </div>
    <el-dialog :visible.sync="dialogVisible" class="partyid-delete-dialog" :show-close="true" width="500px">
        <div class="line-text-one">{{$t('Are you sure you want to delete')}} "{{groupName}}"?</div>
        <div class="line-text-two">{{$t(`You can't undo this action`)}} </div>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="okAction">{{$t('m.common.OK')}}</el-button>
        </div>
    </el-dialog>
    <partyid-add ref="partyidadd" :title='title' />
  </div>
</template>

<script>
import partyidAdd from '../setting/partyidadd'
import { responseRange } from '@/utils/idRangeRule'
import { deleteGroup, partyidList } from '@/api/setting'
// 国际化
const local = {
    zh: {
        'add': '添加',
        'Index': '序号',
        'ID Group': 'ID组',
        'Type': 'ID类型',
        'ID range': 'ID范围',
        'Total': 'ID总数',
        'Used': '已使用',
        'Action': '操作',
        'Are you sure you want to delete': '确认删除',
        "You can't undo this action": '删除操作不可撤回',
        'Search ID Group': '搜索ID组'

    },
    en: {
        'add': 'add',
        'Index': 'Index',
        'ID Group': 'ID Group',
        'Type': 'Type',
        'ID range': 'ID range',
        'Total': 'Total',
        'Used': 'Used',
        'Action': 'Action',
        'Are you sure you want to delete': 'Are you sure you want to delete',
        "You can't undo this action": "You can't undo this action",
        'Search ID Group': 'Search ID Group'

    }
}

export default {
    name: 'PartyId',
    components: {
        partyidAdd
    },
    data() {
        return {
            title: '', // 标题
            partidform: {},
            dialogVisible: false,
            currentPage: 1, // 当前页
            total: 0, // 表格条数
            tableData: [],
            delTtempGroup: '', // 待删除项
            groupName: '', // 待删除项
            typeSelect: [
                {
                    value: 1,
                    label: this.$t('m.common.guest')
                },
                {
                    value: 2,
                    label: this.$t('m.common.host')
                }
            ],
            data: {
                pageNum: 1,
                pageSize: 20
            }
        }
    },
    created() {
        this.initList()
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    methods: {
        // 初始化表格
        initList() {
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            partyidList(this.data).then(res => {
                this.tableData = []
                if (res.data.list.length) {
                    [...res.data.list].forEach(item => {
                        // 状态status===2时,已经删除，不显示。返回的数据还有
                        if (item.status !== 2) {
                            // item.rangeInfo = responseRange(item.rangeInfo)
                            item.rangeInfo = responseRange(item.federatedGroupDetailDos)
                            this.tableData.push(item)
                        }
                    })
                    this.total = res.data.totalRecord
                }
            })
        },
        // 搜索
        toSearch() {
            this.data.pageNum = 1
            this.initList()
        },
        // 翻页
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        // 删除行
        handleDelete(row) {
            this.delTtempGroup = row.groupId
            this.groupName = row.groupName
            this.dialogVisible = true
        },
        // 确定删除行
        okAction(groupId) {
            let data = {
                groupId: this.delTtempGroup
            }
            deleteGroup(data).then(res => {
                this.dialogVisible = false
                this.initList()
                this.$message.success({
                    message: this.$t('m.partyId.deleteSuccessful'),
                    duration: 5000
                })
            })
        },
        // 跳转user
        toUser(row) {
            this.$router.push({
                name: 'partyuser',
                path: '/setting/partyuser',
                query: { groupId: row.groupId, groupName: row.groupName }
            })
        },
        // 添加
        addPartyid() {
            this.title = 'Add a new ID Group'
            this.$refs['partyidadd'].dialogVisible = true
            this.$refs['partyidadd'].partidform = {
                role: 1
            }
            this.$refs['partyidadd'].groupNamewarn = false
            this.$refs['partyidadd'].cannotEdit = {
                sets: [],
                intervalWithPartyIds: []
            }
            this.$refs['partyidadd'].beenUsed = false
        },
        // 编辑
        handleEdit(row) {
            this.title = 'Edit'
            this.$refs['partyidadd'].dialogVisible = true
            this.$refs['partyidadd'].partidform = { ...row }
            this.$refs['partyidadd'].groupNamewarn = false
            this.$refs['partyidadd'].cannotEdit = {
                sets: [],
                intervalWithPartyIds: []
            }
            this.$refs['partyidadd'].beenUsed = false
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/partyid.scss';
</style>
