<template>
    <div class="failed-rate-echart">
        <v-chart
        :data="chartData"
        :options="options" />
    </div>
</template>

<script>

// 国际化
const local = {
    zh: {
        'start': '开始日期',
        'end': '结束日期',
        'faild': '失败率'
    },
    en: {
        'start': 'start',
        'end': 'end',
        'faild': 'faild'
    }
}

export default {
    name: 'durationDistribution',
    props: {
        chartData: {
            type: Object,
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
                    axisPointer: { lineStyle: { color: '#4AA2FF' } },
                    formatter: (params) => {
                        return `<div style='min-width:60px;'>${params[0].name}</div>
                                <div style='margin-top:5px;'>
                                    <span style="
                                        display:inline-block;
                                        width:6px;
                                        height:6px;
                                        border-radius:100%;
                                        background:#E6EBF0">
                                    </span> 
                                    ${self.$t('faild')} : (${params[0].value}%)
                                </div>`
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
                            margin: 14,
                            interval: 0,
                            rotate: 0
                        },
                        data: self.chartData.day
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        axisLine: { show: false },
                        axisTick: { show: false },
                        max: 100,
                        axisLabel: {
                            show: true,
                            interval: 'auto',
                            fontFamily: 'OPPOSans',
                            fontWeight: 400,
                            formatter: '{value} %'
                        },
                        splitLine: {
                            lineStyle: {
                                type: 'dashed',
                                color: '#E6EBF0'
                            }
                        }
                    }
                ],
                series: [
                    {
                        type: 'line',
                        stack: '失败率',
                        itemStyle: {
                            normal: {
                                color: '#FF9D00',
                                lineStyle: {
                                    width: 2,
                                    type: 'solid',
                                    color: '#FF9D00'
                                }
                            }
                        }, // 线条样式
                        showSymbol: false,
                        symbol: 'emptyCircle', // 折线点设置为空心点
                        symbolSize: 6, // 折线点的大小
                        data: self.chartData.data
                    }]
            }
        }
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
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
                    this.options.xAxis[0].data = []
                    this.options.series[0].data = []
                    this.options.xAxis[0].axisLabel.rotate = ''
                    this.options.xAxis[0].axisLabel.rotate = newVal.day.length > 20 ? 40 : 0
                    this.options.series[0].data = newVal.data
                    this.options.xAxis[0].data = newVal.day
                }
            },
            deep: true
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/jobmonitor.scss';
</style>
