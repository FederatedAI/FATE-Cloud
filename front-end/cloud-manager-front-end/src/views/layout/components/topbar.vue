<template>
  <div class="top-bar">
    <div class="avatar-container">
        <el-popover placement="bottom" popper-class="top-pop" :visible-arrow="arrow" trigger="click">
            <div class="content">
                <div class="title">{{$t('Organization Info')}}</div>
                <div class="name-title">{{$t('Federated Organization Name')}}</div>
                <div class="name">{{getInfo.name}}</div>
                <div class="name-title">{{$t('Institution')}}</div>
                <div class="name">{{getInfo.institution}}</div>
                <div class="name-title">{{$t('Creation')}}</div>
                <div class="name">{{getInfo.createTime | dateFormat}}</div>
            </div>
            <div slot="reference" >
                <span>{{getInfo.name}}</span>
                <i class="el-icon-caret-bottom" />
            </div>
        </el-popover>
    </div>
    <div class="name-bar">
        <el-popover placement="bottom" popper-class="bar-pop" :visible-arrow="false" trigger="click">
            <div class="mane" @click="tologout">{{$t('Sign out')}}</div>
            <div slot="reference" >
                <span  class="name-text" >
                    <tooltip :width="'80px'" :content="`${loginName}`" :placement="'top'"/>
                </span>
                <i class="el-icon-caret-bottom" />
            </div>
        </el-popover>
    </div>
    <div class="lang-bar">
        <el-dropdown  trigger="click"  @command="handleCommand">
            <span class="text-link">
                <span>{{language==='zh'?'中文':'English'}}</span>
                <i class="el-icon-caret-bottom"></i>
            </span>
            <el-dropdown-menu slot="dropdown"  class="dropdown">
                <el-dropdown-item command='zh' :disabled="language==='zh'">中文</el-dropdown-item>
                <el-dropdown-item command='en' :disabled="language==='en'">English</el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
    </div>
    <!-- <img src="@/assets/notification.png" alt=""> -->
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import moment from 'moment'
import { logout } from '@/api/welcomepage'
import tooltip from '@/components/Tooltip'
// 国际化
const local = {
    zh: {
        'Sign out': '退出',
        'Organization Info': '组织信息',
        'Federated Organization Name': '联邦组织名称',
        'Institution': '管理机构',
        'Creation': '创建时间'
    },
    en: {
        'Sign out': 'Sign out',
        'Organization Info': 'Organization Info',
        'Federated Organization Name': 'Federated Organization Name',
        'Institution': 'Institution',
        'Creation': 'Creation'
    }
}

export default {
    filters: {
        dateFormat(vaule) {
            return moment(vaule).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    components: {
        tooltip
    },
    data() {
        return {
            arrow: false,
            activeIndex: '2',
            size: 'small',
            boxWidth: false,
            show: false,
            parentBool: false,
            name: '',
            tooltip: false
        }
    },
    computed: {
        ...mapGetters(['getInfo', 'loginName', 'language'])
    },
    watch: {

    },
    created() {
        this.$store.dispatch('setloginname', localStorage.getItem('name')).then(r => {
            localStorage.setItem('name', r)
        })
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },

    methods: {
        // 点击退出
        tologout(e) {
            logout().then(res => {
                this.$store.dispatch('setloginname', '').then(r => {
                    localStorage.setItem('name', r)
                    this.$router.push({
                        path: '/home/welcome'
                    })
                })
            })
        },
        handleCommand(val) {
            this.$i18n.locale = val
            this.$store.dispatch('setLanguage', val)
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >

.top-pop {
    width: 215px;
    // height: 250px;
    padding: 30px 35px 35px 35px;
    background-color: #fff !important;
    top: 53px !important;
    left: calc(100% - 322px) !important;
    .content {
        .title {
            line-height: 20px;
            color: #217ad9;
            font-weight: 600;
            font-size: 18px;
            margin-bottom: 10px;
        }
        .name-title {
            line-height: 50px;
            font-size: 14px;
            color: #848c99;
        }
        .name {
            line-height: 25px;
            color: #2d3642;
            border-bottom: 2px solid #e6ebf0;
        }
    }
}
.bar-pop{
    text-align: center;
    height: 35px !important;
    line-height: 35px;
    margin-top:0 !important;
    min-width: 95px !important;
    left: calc(100% - 235px) !important;
    padding: 5px;
    .mane{
        cursor: pointer;
        font-size: 16px;
        color: #217AD9;
    }
    .mane:hover{
        background-color: #ecf5ff;
    }
}
.dropdown{
    text-align: center;

    margin-top:0 !important;

    left: calc(100% - 118px) !important;
    .popper__arrow{
        display: none
    }
}
</style>
