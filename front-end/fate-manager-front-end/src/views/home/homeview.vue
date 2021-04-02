<template>
  <div class="homeview">
    <div class="homeview-box">
        <div class="add">
            <div class="add-box">
                <i class="el-icon-plus" @click="toRegister"></i>
            </div>
        </div>
    <el-collapse v-model="activeNames" @change="handleChange">
        <el-collapse-item v-for="(item, index) in listdata" :key="index" :name="index" :class="{activate:true,definition:item.siteList.every(e=>{return e.status.code===3 } )}" >
            <template slot="title" >
                <div class="item">
                    <div class="item-title">Federated Organization</div>
                    <div class="item-content">{{item.federatedOrganization}}</div>
                </div>
                <div class="item">
                    <div class="item-title">Size</div>
                    <div class="item-content">{{item.size}}</div>
                </div>
                <div class="item">
                    <div class="item-title">Institution</div>
                    <div class="item-content">{{item.institutions}}</div>
                </div>
                <div class="item">
                    <div class="item-title">Creation Time</div>
                    <div class="item-content">{{item.createTime | dateFormat}}</div>
                </div>
                <div class="icon">
                </div>
            </template>
            <el-table
                :data="item.siteList"
                :style="`height:${item.siteList.length*49 + 50}px`"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                @row-click="toSietInfo"
                tooltip-effect="light">
                <el-table-column prop="siteName" label="Site Name" show-overflow-tooltip></el-table-column>
                <el-table-column prop="status.desc" label="status">
                    <template slot-scope="scope">
                        <span style="color:#217AD9">{{scope.row.status.desc}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="role.desc" label="Role"></el-table-column>
                <el-table-column prop="partyId" label="Party ID"></el-table-column>
                <el-table-column prop="acativationTime" label="Activation Time">
                    <template slot-scope="scope">
                        <span>{{scope.row.acativationTime | dateFormat}}</span>
                    </template>
                </el-table-column>
            </el-table>
        </el-collapse-item>
    </el-collapse>
    </div>
  </div>
</template>

<script>
import { mySiteList } from '@/api/home'
import moment from 'moment'

export default {
    name: 'homeview',
    filters: {
        dateFormat(vaule) {
            return vaule ? moment(vaule).format('YYYY-MM-DD HH:mm:ss') : '--'
        }
    },
    data() {
        return {
            activeNames: '1',
            style: '',
            listdata: [],
            tableData: []
        }
    },
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
    created() {
        this.getList()
    },

    mounted() {

    },
    methods: {
        getList() {
            mySiteList().then(res => {
                this.listdata = [...res.data]
                if (this.listdata.length) {
                    this.listdata.forEach(item => {
                        item.siteList.forEach(element => {
                            element.federatedId = item.federatedId
                        })
                    })
                }
            })
        },
        handleChange(val) {
            // console.log(val)
        },
        toRegister() {
            this.$router.push({
                name: 'register',
                path: '/home/register'
                // query: {}
            })
        },
        toSietInfo(row) {
            this.$router.push({
                name: 'siteinfo',
                path: '/siteinfo/index',
                query: { federatedId: row.federatedId, partyId: row.partyId }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/homeview.scss';
</style>
