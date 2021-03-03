<template>
    <div class="job-monitor">
        <div class="job-monitor-box">
            <div class="job-monitor-date">
                <div class="select-date">
                    <span >{{$t('Date')}} ：</span>
                    <el-date-picker
                        v-model="timevalue"
                        @change="initi"
                        class="picker"
                        type="daterange"
                        range-separator="~"
                        value-format="timestamp"
                        :start-placeholder="'start'"
                        :end-placeholder="'end'"
                        :picker-options="pickerOptions">
                    </el-date-picker>
                    <span class="institution">{{$t('Institution')}} : </span>
                    <el-select v-model="value" class="select" placeholder="请选择">
                        <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>
                    <span class="institution">{{$t('Site')}} : </span>
                    <el-select v-model="value" class="select" placeholder="请选择">
                        <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <div class="totals">
                    <span class="total-jobs">{{$t('Total jobs')}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <span class="total-jobs">{{$t('Success')}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <span class="total-jobs">{{$t('Failed')}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <div class="jobs-sign">
                        <span class="success-sign"> </span>
                        <span> {{$t('Success')}} </span>
                        <span class="failed-sign"> </span>
                        <span > {{$t('Failed')}} </span>
                    </div>
                </div>
                <div class="job-modeling">
                    <el-row :gutter="20">
                        <el-col :span="6" v-for="(item, index) in modelList" :key="index">
                            <div :class="{modeling:true,'active-color':item.active}" @click="showType(index)">
                                <div class="failed"></div>
                                <div class="success"></div>
                            </div>
                            <div class="name">
                                {{$t(`${item.modelname}`)}}
                            </div>
                            <div v-if="item.active">
                                <i class="el-icon-arrow-down"></i>
                            </div>
                        </el-col>

                    </el-row>
                </div>
            </div>
            <div class="job-details">
                <div class="job-info">
                    <span class="total-jobs">{{$t("Jobs")}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <span class="total-jobs">{{$t("Success")}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <span class="total-jobs">{{$t("Failed")}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <span class="total-jobs">{{$t("Average duration")}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <span class="total-jobs">{{$t("Min duration")}} : </span>
                    <span class="span">
                        100(100%)
                    </span>
                    <span class="total-jobs">{{$t("Max duration")}} :</span>
                    <span class="span">
                        100(100%)
                    </span>
                </div>
                <div class="table">
                    <div class="table-head"></div>
                    <el-table :data="tableData" border max-height="300">
                        <el-table-column prop="date" :label="$t(`Date`)" > </el-table-column>
                        <el-table-column prop="name" :label="$t(`Jobs`)"   > </el-table-column>
                        <el-table-column prop="name" :label="$t(`Success`)"   > </el-table-column>
                        <el-table-column prop="name" :label="$t(`Failed`)"   > </el-table-column>
                        <el-table-column prop="name" :label="$t(`Average duration`)" > </el-table-column>
                        <el-table-column prop="name" :label="$t(`Min duration`)" > </el-table-column>
                        <el-table-column prop="address" :label="$t(`Max duration`)" > </el-table-column>
                    </el-table>
                </div>
                <div class="echarts-line">
                    <div class="line-title">
                        {{$t(`Failed rate`)}}
                    </div>
                    <jobMonitorfailed/>
                </div>
                <div class="echarts-line">
                    <div class="line-title">
                        {{$t(`Duration distribution`)}}
                    </div>
                    <jobMonitorDur/>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
import jobMonitorfailed from './jobMonitorfailed'
import jobMonitorDur from './jobMonitorDur'
// 国际化
const local = {
    zh: {
        'Date': '日期',
        'start': '开始日期',
        'end': '结束日期',
        'Institution': '机构',
        'Site': '站点',
        'Total jobs': '总任务数',
        'Success': '成功',
        'Failed': '失败',
        'Intersect': '交集任务',
        'Modeling': '建模任务',
        'Upload': '上传任务',
        'Download': '下载任务',
        'Jobs': '任务数',
        'Average duration': '平均时长',
        'Min duration': '最小时长',
        'Max duration': '最大时长',
        'Failed rate': '失败率统计',
        'Duration distribution': '任务时长分布'

    },
    en: {
        'Date': 'Date',
        'start': 'start',
        'end': 'end',
        'Institution': 'Institution',
        'Site': 'Site',
        'Total jobs': 'Total jobs',
        'Success': 'Success',
        'Failed': 'Failed',
        'Intersect': 'Intersect',
        'Modeling': 'Modeling',
        'Upload': 'Upload',
        'Download': 'Download',
        'Jobs': 'Jobs',
        'Average duration': 'Average duration',
        'Min duration': 'Min duration',
        'Max duration': 'Max duration',
        'Failed rate': 'Failed rate',
        'Duration distribution': 'Duration distribution'

    }
}

export default {
    name: 'monitor',
    components: {
        jobMonitorfailed,
        jobMonitorDur
    },
    filters: {
    },
    data() {
        return {
            tableData: [{
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }, {
                date: '2016-05-04',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1517 弄'
            }],
            timevalue: '',
            options: [{
                value: '选项1',
                label: '黄金糕'
            }, {
                value: '选项2',
                label: '双皮奶'
            }],
            value: '',
            modelList: [
                { active: false, modelname: 'Intersect' },
                { active: false, modelname: 'Modeling' },
                { active: false, modelname: 'Upload' },
                { active: false, modelname: 'Download' }
            ],
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now()
                }
            }
        }
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
    },

    mounted() {
        this.showType(0)
    },
    methods: {
        initi() {},
        showType(index) {
            this.modelList.forEach((item, ind) => {
                if (index === ind) {
                    item.active = true
                } else {
                    item.active = false
                }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/jobmonitor.scss';
</style>
