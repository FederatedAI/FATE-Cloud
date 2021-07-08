<template>
    <div class="failed-rate-echart">
        <v-chart
        :data="chartData"
        :options="options" />
    </div>
</template>

<script>
import echarts from 'echarts'
// 国际化
const local = {
    zh: {
        'start': '开始日期',
        'end': '结束日期',
        'jobs': '任务数',
        'duration': '时间区间',
        'min': '分钟',
        'hour': '小时',
        'day': '天'
    },
    en: {
        'start': 'start',
        'end': 'end',
        'jobs': 'jobs',
        'duration': 'Duration',
        'min': 'min',
        'hour': 'h',
        'day': 'd'
    }
}

export default {
    name: 'durationDistribution',
    props: {
        chartData: {
            type: Array,
            required: true
        }
    },
    components: {

    },
    filters: {
    },
    data() {
        let self = this
        return {
            options: {
                tooltip: {
                    trigger: 'axis',
                    show: true,
                    padding: [5, 10, 5, 10],
                    textStyle: { opacity: '.1' },
                    axisPointer: { lineStyle: { color: '#4AA2FF' } },
                    formatter: (params) => {
                        return `<div>${self.$t('duration')} : ${params[0].name}</div>
                                <div style='margin-top:5px;'>${self.$t('jobs')} : ${params[0].value}</div>`
                    }
                },
                grid: {
                    left: '0%',
                    right: '1%',
                    bottom: '5%',
                    top: '5%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        axisTick: { show: false },
                        axisLine: { lineStyle: { color: '#E6EBF0' } },
                        boundaryGap: true,
                        axisLabel: {
                            color: '#848C99',
                            margin: 14
                        },
                        data: []
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        axisLine: { show: false },
                        axisTick: { show: false },
                        axisLabel: {
                            color: '#848C99',
                            fontFamily: 'OPPOSans',
                            fontWeight: 400
                        },
                        splitLine: {
                            lineStyle: {
                                type: 'dashed', // y轴分割线类型-虚线
                                color: '#E6EBF0'
                            }
                        }
                    }
                ],
                series: [
                    {
                        type: 'line',
                        stack: '总量',
                        itemStyle: {
                            normal: {
                                color: '#7CBCFF', // 折线点的颜色
                                lineStyle: { // 系列级个性化折线样式
                                    width: 2,
                                    type: 'solid',
                                    color: '#7CBCFF'
                                }
                            }
                        }, // 线条样式
                        showSymbol: false,
                        symbol: 'emptyCircle', // 折线点设置为空心点
                        symbolSize: 6, // 折线点的大小
                        areaStyle: {
                            normal: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0, color: '#7CBCFF' // 0% 处的颜色
                                }, {
                                    offset: 0.4, color: '#C0DEFF' // 50% 处的颜色
                                }, {
                                    offset: 0.8, color: '#EAF4FF' // 50% 处的颜色
                                }, {
                                    offset: 1, color: '#fff' // 100% 处的颜色
                                }]) // 背景渐变色
                            }
                        },
                        data: self.chartData
                    }]
            }
        }
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        this.options.xAxis[0].data = [`<1${this.$t('min')}`, `1～30${this.$t('min')}`, `30${this.$t('min')}～2${this.$t('hour')}`, `2～6${this.$t('hour')}`, `6～12${this.$t('hour')}`, `6～12${this.$t('hour')}`, `>1${this.$t('day')}`]
    },

    mounted() {

    },
    methods: {
        initi() {}
    },
    watch: {
        chartData: {
            handler(newVal, oldVal) {
                if (newVal) {
                    console.log(newVal, 'newVal-dur')
                    this.options.series[0].data = newVal
                }
            },
            deep: true
        },
        '$i18n.locale'(newValue) {
            this.options.xAxis[0].data = []
            this.options.xAxis[0].data = [`<1${this.$t('min')}`, `1～30${this.$t('min')}`, `30${this.$t('min')}～2${this.$t('hour')}`, `2～6${this.$t('hour')}`, `6～12${this.$t('hour')}`, `6～12${this.$t('hour')}`, `>1${this.$t('day')}`]
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/jobmonitor.scss';
</style>
