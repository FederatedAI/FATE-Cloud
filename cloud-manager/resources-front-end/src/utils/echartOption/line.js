export const lineOption = {
  title: {
    text: ''
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  legend: {
    show: false
  },
  grid: {
    left: '5%',
    right: '5%',
    containLabel: true
  },
  yAxis: {
    type: 'value',
    name: '调用量/次',
    nameLocation: 'end',
    nameTextStyle: {
      color: '#aaaaaa',
      align: 'right'
    },
    nameGap: 30
  },
  xAxis: {
    type: 'category',
    data: ['2019-10-30', '2019-10-31', '2019-11-01', '2019-11-02', '2019-11-03', '2019-11-04']
  },
  series: [
    {
      type: 'line',
      smooth: true,
      name: 'label1',
      data: [10, 20, 30, 40, 50, 40],
      itemStyle: {
        color: ''
      }
    },
    {
      type: 'line',
      smooth: true,
      name: 'label2',
      data: [10, 30, 30, 50, 40, 40]
    }
  ]
}

// texts 为Array 数据项类型是String
function setLegendData(options, texts) {
  options.legend.data = texts
  return options
}

export function setXAxis(options, from, to) {
  const dates = []
  if (!(from instanceof Date)) {
    from = new Date(from)
  }
  if (!(to instanceof Date)) {
    to = new Date(to)
  }
  const dataExchange = function(date) {
    if (date instanceof Date) {
      const y = date.getFullYear()
      let m = date.getMonth() + 1
      m = m < 10 ? ('0' + m) : m
      let d = date.getDate()
      d = d < 10 ? ('0' + d) : d
      return y + '-' + m + '-' + d
    } else {
      return date
    }
  }
  let middle = to
  do {
    dates.unshift(dataExchange(middle))
    middle = new Date(middle - 24 * 60 * 60 * 1000)
  } while (middle.getTime() >= from.getTime())
  options.xAxis.data = dates
  return options
}

// lineData 设置折现的数据内容 形式为： [{name, value}] value是数组
export function addLine(options, lineData, colorList) {
  options.series = []
  const legendData = []
  for (let i = 0; i < lineData.length; i++) {
    const val = lineData[i]
    const middle = {}
    middle.name = val.name
    middle.type = 'line'
    middle.smooth = false
    middle.data = val.value
    middle.itemStyle = {}
    middle.itemStyle.color = colorList instanceof Array ? colorList[i] : colorList(i)
    middle.lineStyle = {}
    middle.lineStyle.color = colorList instanceof Array ? colorList[i] : colorList(i)
    options.series.push(middle)
    legendData.push(val.name)
  }
  setLegendData(options, legendData)
  return options
}
