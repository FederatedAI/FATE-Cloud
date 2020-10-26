<template>
  <div class="top-bar">
    <div class="avatar-container">
        <el-popover placement="bottom" popper-class="top-pop" :visible-arrow="arrow" trigger="click">
            <div class="content">
                <div class="title">Organization Info</div>
                <div class="name-title">Federated Organization Name</div>
                <div class="name">{{getInfo.name}}</div>
                <div class="name-title">Institution</div>
                <div class="name">{{getInfo.institution}}</div>
                <div class="name-title">Creation</div>
                <div class="name">{{getInfo.createTime | dateFormat}}</div>
            </div>
            <div slot="reference" >
                <span>{{getInfo.name}}</span>
                <i class="el-icon-caret-bottom" />
        </div>
        </el-popover>
    </div>
    <!-- <img src="@/assets/notification.png" alt=""> -->
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import moment from 'moment'

export default {
    filters: {
        dateFormat(vaule) {
            return moment(vaule).format('YYYY-MM-DD HH:mm:ss')
        }
    },
    data() {
        return {
            arrow: false,
            activeIndex: '2',
            size: 'small',
            boxWidth: false,
            show: false,
            parentBool: false
        }
    },
    computed: {
        ...mapGetters(['getInfo'])
    },
    watch: {

    },

    mounted() {},
    methods: {

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
.top-bar {
    height: 65px;
    line-height: 65px;
    .avatar-container {
        position: absolute;
        display: inline-block;
        cursor: pointer;
        right: 240px;
        top: 0px;
        font-size: 16px;
        color: #fff;
        height: 65px;
    }
    img{
        position: absolute;
        right: 35px;
        top: 20px;
    }
}
.top-pop {
    width: 215px;
    // height: 250px;
    padding: 30px 35px 35px 35px;
    background-color: #fff;
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
</style>
