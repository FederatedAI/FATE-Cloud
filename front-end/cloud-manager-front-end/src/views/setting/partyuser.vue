<template>
  <div class="partyuser-box">
    <div class="partyuser">
        <div class="partyuser-header">
            <el-radio-group class="radio" v-model="radio">
                <el-radio-button label="In Use">{{$t('In Use')}}</el-radio-button>
                <el-radio-button label="Historic Uses">{{$t('Historic Uses')}}</el-radio-button>
            </el-radio-group>
        </div>
        <div class="row-add">
            <el-select class="sel-institutions input-placeholder" filterable clearable collapse-tags v-model="data.institutions" @change="toSearch" :placeholder="$t('Institutions')">
                <el-option
                    v-for="(item,index) in groupSetStatusSelect"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                ></el-option>
            </el-select>
            <el-input class="input input-placeholder" v-model.trim="data.condition" clearable :placeholder="$t('Search for Site Name or Party ID')">
            </el-input>
            <el-button class="go" type="primary" @click="toSearch">{{$t('m.common.go')}}</el-button>
        </div>
        <div class="partyuser-body">
            <div class="table">
            <el-table :data="tableData" height="100%" @sort-change='sortChange' header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell">
                <el-table-column prop="" type="index" :label="$t('Index')" width="180"></el-table-column>
                <el-table-column prop="partyId" sortable='custom' :label="$t('Party ID')" ></el-table-column>
                <el-table-column prop="siteName" sortable='custom' :label="$t('Site Name')" ></el-table-column>
                <el-table-column prop="institutions" sortable='custom'  :label="$t('Institutions')"  show-overflow-tooltip></el-table-column>
                <el-table-column prop="createTime" sortable='custom' :label="$t('CreationTime')" >
                    <template slot-scope="scope">
                        <span>{{scope.row.createTime | dateFormat}}</span>
                    </template>
                </el-table-column>
            </el-table>
            </div>
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
        </div>
    </div>
  </div>
</template>

<script>
import { getUeserList, getInstitutionsDomn } from '@/api/setting'
// import { selectAll } from '@/api/federated'
import moment from 'moment'
// 国际化
const local = {
    zh: {
        'In Use': '使用中',
        'Historic Uses': '历史使用',
        'Index': '序号',
        'Party ID': '站点ID',
        'Site Name': '站点名称',
        'Institutions': '机构',
        'CreationTime': '创建时间',
        'Search for Site Name or Party ID': '搜索站点名称或站点ID'

    },
    en: {
        'In Use': 'In Use',
        'Historic Uses': 'Historic Uses',
        'Index': 'Index',
        'Party ID': 'Party ID',
        'Site Name': 'Site Name',
        'Institutions': 'Institutions',
        'CreationTime': 'CreationTime',
        'Search for Site Name or Party ID': 'Search for Site Name or Party ID'

    }
}

export default {
    name: 'partyuser',
    components: {
    },
    filters: {
        dateFormat(vaule) {
            return moment(vaule).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            radio: 'In Use',
            dialogVisible: false,
            currentPage1: 1, // 当前页
            total: 0,
            tableData: [],
            groupSetStatusSelect: [
                {
                    value: '',
                    label: 'institutions'
                }
            ],
            data: {
                pageNum: 1,
                pageSize: 20,
                groupId: parseInt(this.$route.query.groupId),
                statusList: [],
                orderField: 'create_time',
                orderRule: 'asc', // 升序
                institutions: ''
            },
            params: {
                statusList: [],
                groupId: parseInt(this.$route.query.groupId)
            }

        }
    },
    created() {
        // desc降序 asc升序
        // this.initList()
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },
    watch: {
        radio: {
            handler(val) {
                if (val === 'In Use') {
                    this.data.statusList = [1, 2]
                    this.params.statusList = [1, 2]
                } else if (val === 'Historic Uses') {
                    this.data.statusList = [3]
                    this.params.statusList = [3]
                }
                this.initList()
                this.togetinstitution()
            },
            deep: true,
            immediate: true
        }
    },
    methods: {
        initList() {
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    const element = this.data[key]
                    if (!element) {
                        delete this.data[key]
                    }
                }
            }
            getUeserList(this.data).then(res => {
                this.tableData = res.data.list
                this.total = res.data.totalRecord
            })
        },
        togetinstitution() {
            this.groupSetStatusSelect = [
                // {
                //     value: '',
                //     label: 'institutions'
                // }
            ]
            getInstitutionsDomn(this.params).then(res => {
                res.data.forEach(item => {
                    let obj = {}
                    obj.label = item
                    obj.value = item
                    this.groupSetStatusSelect.push(obj)
                })
            })
        },
        toSearch() {
            this.data.pageNum = 1
            this.initList()
        },
        handleSizeChange(val) {
            // console.log(`每页 ${val} 条`)
        },
        handleCurrentChange(val) {
            this.data.pageNum = val
            this.initList()
        },
        sortChange(val) {
            // 项目
            if (val.prop === 'siteName') {
                this.data.orderField = 'site_name'
            } else if (val.prop === 'partyId') {
                this.data.orderField = 'party_id'
            } else if (val.prop === 'institutions') {
                this.data.orderField = 'institutions'
            } else if (val.prop === 'createTime') {
                this.data.orderField = 'create_time'
            }
            // 排序
            if (val.order === 'ascending') { // 升序
                this.data.orderRule = 'asc'
            } else if (val.order === 'descending') { // 降序
                this.data.orderRule = 'desc'
            }
            this.initList()
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/partyuser.scss';
</style>
