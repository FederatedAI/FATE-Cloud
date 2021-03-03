<template>
    <div class="failed-rate-echart">
        <v-chart :options="options" />
    </div>
</template>

<script>
import echarts from 'echarts'
// 国际化
// const local = {
//     zh: {
//         'start': '开始日期',
//         'end': '结束日期'
//     },
//     en: {
//         'start': 'start',
//         'end': 'end'
//     }
// }

export default {
    name: 'durationDistribution',
    components: {

    },
    filters: {
    },
    data() {
        return {
            options: {
                tooltip: {
                    trigger: 'axis',
                    show: true,
                    formatter: (params) => {
                        return `<div style='min-width:60px;'>${params[0].name}</div>
                                <div style='min-width:60px;'>${params[0].value}</div>`
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
                        boundaryGap: false,
                        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        splitLine: {
                            lineStyle: {
                                type: 'dashed' // y轴分割线类型-虚线
                            }
                        }
                    }
                ],
                series: [

                    {
                        type: 'line',
                        stack: '总量',
                        itemStyle: {
                            normal: { // 颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
                                color: '#7CBCFF', // 折线点的颜色
                                lineStyle: { // 系列级个性化折线样式
                                    width: 3,
                                    type: 'solid',
                                    color: '#7CBCFF'
                                }
                            }
                        }, // 线条样式
                        symbol: 'circle', // 折线点设置为实心点
                        symbolSize: 5, // 折线点的大小
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
                            } },
                        data: [320, 332, 601, 134, 120, 230, 210]
                    }]
            }
        }
    },
    created() {
        // this.$i18n.mergeLocaleMessage('en', local.en)
        // this.$i18n.mergeLocaleMessage('zh', local.zh)
    },

    mounted() {

    },
    methods: {
        initi() {}
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/jobmonitor.scss';
</style>
