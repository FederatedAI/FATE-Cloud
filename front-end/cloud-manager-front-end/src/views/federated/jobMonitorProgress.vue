<template>
    <div class="failed-rate-echart">
        <v-bar
            ref="chart_progress"
            :lang="lang"
            :data="chartData"
            :options="chartExtend"
        >
        </v-bar>
    </div>
</template>

<script>

import store from '@/store'
import { getCookie } from '@/utils/auth'

// 国际化
const local = {
    zh: {
        'start': '开始日期',
        'end': '结束日期'
    },
    en: {
        'start': 'start',
        'end': 'end'
    }
}

export default {
    name: 'durationDistribution',
    props: {
        chartData: {
            type: Object,
            required: true
        },
        lang: {
            type: Array,
            required: true
        }
    },
    components: {

    },
    filters: {
    },
    data() {
        const self = this
        return {
            selectedProgressBarIndex: 0,
            emphasisStyle: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(0,0,0,0.3)',
                    triggerOn: 'click'
                }
            },
            chartExtend: {
                tooltip: {
                    trigger: 'item',
                    triggerOn: 'mousemove|click',
                    axisPointer: { type: 'none' },
                    backgroundColor: 'rgba(45, 54, 66, .8)',
                    textStyle: {
                        color: '#fff',
                        fontFamily: 'OPPOSans',
                        fontSize: 12
                    },
                    formatter: function (params, ticket, callback) {
                        // let total = params[1].value * 1 + params[2].value * 1
                        // let success = total === 0 ? 0 : (params[2].value / total).toFixed(1) * 100
                        // let failed = total === 0 ? 0 : (params[1].value / total).toFixed(1) * 100
                        let data = self.chartData
                        let index = params.dataIndex
                        let success = data.success[index] * 1
                        let failed = data.failed[index] * 1
                        let total = success + failed
                        let successPer = total === 0 ? 0 : (success / total).toFixed(1) * 100
                        let failedPer = total === 0 ? 0 : (failed / total).toFixed(1) * 100
                        return `<div style="margin-bottom:10px">
                                    <span style="
                                    display:inline-block;
                                    width:6px;
                                    height:6px;
                                    border-radius:100%;
                                    background:#00C99E">
                                    </span> 
                                    ${self.$t('m.common.success')} : ${success} (${successPer}%)
                                </div>
                                <div style="text-align:left">
                                    <span style="
                                        display:inline-block;
                                        width:6px;
                                        height:6px;
                                        border-radius:100%;
                                        background:#E6EBF0">
                                    </span> 
                                    ${self.$t('m.common.failed')} : ${failed} (${failedPer}%)
                                </div>`
                    }
                },
                xAxis: {
                    data: this.lang,
                    axisLine: { show: false },
                    splitLine: { show: false },
                    splitArea: { show: false },
                    axisTick: { show: false },
                    axisLabel: {
                        fontSize: 14,
                        padding: [0, 45, 0, 0],
                        margin: 12,
                        fontFamily: 'OPPOSans',
                        fontWeight: 600,
                        opacity: 0.7,
                        color: (value, index) => {
                            return index === self.selectedProgressBarIndex ? '#4E5766' : '#848C99'
                        },
                        formatter: (value, index) => {
                            return index === self.selectedProgressBarIndex ? value + '\n\n﹀' : value
                        }
                    },
                    triggerEvent: true
                },
                yAxis: {
                    axisLine: { show: false },
                    splitLine: { show: false },
                    splitArea: { show: false },
                    axisLabel: { show: false },
                    axisTick: { show: false },
                    max: 100
                },
                grid: {
                    top: 0,
                    left: 55,
                    right: 55,
                    bottom: 145
                },
                series: [
                    {
                        name: 'bar',
                        type: 'bar',
                        stack: 'one',
                        barWidth: 8,
                        data: [2, 2, 2, 2],
                        zlevel: 1,
                        itemStyle: {
                            normal: {
                                barBorderRadius: 10,
                                color: '#fff'
                            },
                            triggerOn: 'click'
                        }
                    },
                    {
                        name: 'bar',
                        type: 'bar',
                        stack: 'one',
                        emphasis: this.emphasisStyle,
                        hoverAnimation: false,
                        barWidth: 8,
                        data: this.chartData.failed,
                        zlevel: 1,
                        itemStyle: {
                            normal: {
                                barBorderRadius: 10,
                                color: '#E6EBF0'
                            },
                            emphasis: {
                                color: '#E6EBF0'
                            }
                        }
                    },
                    {
                        name: 'bar',
                        type: 'bar',
                        stack: 'one',
                        emphasis: this.emphasisStyle,
                        hoverAnimation: false,
                        barWidth: 8,
                        barCategoryGap: '105%',
                        data: this.chartData.success,
                        zlevel: 1,
                        itemStyle: {
                            normal: {
                                barBorderRadius: 10,
                                color: '#00C99E'
                            },
                            emphasis: {
                                color: '#00C99E'
                            }
                        }
                    },
                    {
                        type: 'bar',
                        barGap: '-675%',
                        barWidth: 100,
                        data: [100, 100, 100, 100],
                        itemStyle: {
                            normal: {
                                barBorderRadius: 0,
                                color: function(param) {
                                    return self.selectedProgressBarIndex === param.dataIndex ? '#FAFBFC' : '#FFFFFF'
                                }
                            }
                        }
                    }
                ]
            }
        }
    },
    created() {
        this.$i18n.mergeLocaleMessage('en', local.en)
        this.$i18n.mergeLocaleMessage('zh', local.zh)
        console.log(this.$t(`xAxis`).split(','), 'xAxis')
        this.$nextTick((e) => {
            // 解决v-charts 绑定不上事件问题
            this.$refs.chart_progress.chart.on('click', (e) => {
                this.clickProgress(e)
            })
        })
    },

    mounted() {

    },
    methods: {
        clickProgress(e) {
            console.log(e, 'e')
            // 根据鼠标点击事件设置选中的项
            let dataName
            let hightLightIndex
            let language = getCookie('language') || store.getters.language
            if (e.dataIndex === 0) {
                hightLightIndex = 0
            } else {
                hightLightIndex = e.dataIndex || this.getclickxAxisIndex(e)
            }
            dataName = language === 'zh' ? this.getEnglish(e.name || e.value) : (e.name || e.value)
            this.selectedProgressBarIndex = hightLightIndex
            this.reFreshProgress()
            this.$emit('setProgressIndex', dataName)
        },
        getclickxAxisIndex(e) {
            return (this.$t(`xAxis`).split(',')).indexOf(e.value)
        },
        reFreshProgress() {
            var series = this.chartExtend.series
            this.chartExtend.series = []
            this.chartExtend.series = series
        },
        getEnglish(name) {
            let xAxisTranslate = {
                '交集任务': 'intersect',
                '建模任务': 'modeling',
                '上传任务': 'upload',
                '下载任务': 'download'
            }
            return xAxisTranslate[name]
        }
    },
    watch: {
        chartData: {
            handler(newVal, oldVal) {
                if (newVal) {
                    this.chartExtend.series[1].data = newVal.failed
                    this.chartExtend.series[2].data = newVal.success
                }
            },
            deep: true
        },
        lang: {
            handler(newVal, oldVal) {
                if (newVal) {
                    this.chartExtend.xAxis.data = newVal
                }
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import 'src/styles/jobmonitor.scss';
</style>
