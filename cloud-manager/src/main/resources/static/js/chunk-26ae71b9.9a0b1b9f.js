(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-26ae71b9"],{"15a1":function(t,a,e){},"28a5":function(t,a,e){"use strict";var n=e("aae3"),i=e("cb7c"),s=e("ebd6"),o=e("0390"),r=e("9def"),c=e("5f1b"),u=e("520a"),l=e("79e5"),d=Math.min,h=[].push,p="split",f="length",m="lastIndex",b=4294967295,g=!l(function(){RegExp(b,"y")});e("214f")("split",2,function(t,a,e,l){var v;return v="c"=="abbc"[p](/(b)*/)[1]||4!="test"[p](/(?:)/,-1)[f]||2!="ab"[p](/(?:ab)*/)[f]||4!="."[p](/(.?)(.?)/)[f]||"."[p](/()()/)[f]>1||""[p](/.?/)[f]?function(t,a){var i=String(this);if(void 0===t&&0===a)return[];if(!n(t))return e.call(i,t,a);var s,o,r,c=[],l=(t.ignoreCase?"i":"")+(t.multiline?"m":"")+(t.unicode?"u":"")+(t.sticky?"y":""),d=0,p=void 0===a?b:a>>>0,g=new RegExp(t.source,l+"g");while(s=u.call(g,i)){if(o=g[m],o>d&&(c.push(i.slice(d,s.index)),s[f]>1&&s.index<i[f]&&h.apply(c,s.slice(1)),r=s[0][f],d=o,c[f]>=p))break;g[m]===s.index&&g[m]++}return d===i[f]?!r&&g.test("")||c.push(""):c.push(i.slice(d)),c[f]>p?c.slice(0,p):c}:"0"[p](void 0,0)[f]?function(t,a){return void 0===t&&0===a?[]:e.call(this,t,a)}:e,[function(e,n){var i=t(this),s=void 0==e?void 0:e[a];return void 0!==s?s.call(e,i,n):v.call(String(i),e,n)},function(t,a){var n=l(v,t,this,a,v!==e);if(n.done)return n.value;var u=i(t),h=String(this),p=s(u,RegExp),f=u.unicode,m=(u.ignoreCase?"i":"")+(u.multiline?"m":"")+(u.unicode?"u":"")+(g?"y":"g"),y=new p(g?u:"^(?:"+u.source+")",m),x=void 0===a?b:a>>>0;if(0===x)return[];if(0===h.length)return null===c(y,h)?[h]:[];var D=0,j=0,_=[];while(j<h.length){y.lastIndex=g?j:0;var O,C=c(y,g?h:h.slice(j));if(null===C||(O=d(r(y.lastIndex+(g?0:j)),h.length))===D)j=o(h,j,f);else{if(_.push(h.slice(D,j)),_.length===x)return _;for(var $=1;$<=C.length-1;$++)if(_.push(C[$]),_.length===x)return _;j=D=O}}return _.push(h.slice(D)),_}]})},"3d9a":function(t,a,e){},"5df3":function(t,a,e){"use strict";var n=e("02f4")(!0);e("01f9")(String,"String",function(t){this._t=String(t),this._i=0},function(){var t,a=this._t,e=this._i;return e>=a.length?{value:void 0,done:!0}:(t=n(a,e),this._i+=t.length,{value:t,done:!1})})},"71be":function(t,a,e){"use strict";var n=e("15a1"),i=e.n(n);i.a},"7cf8":function(t,a,e){"use strict";e.d(a,"g",function(){return s}),e.d(a,"j",function(){return o}),e.d(a,"h",function(){return r}),e.d(a,"f",function(){return c}),e.d(a,"i",function(){return u}),e.d(a,"d",function(){return l}),e.d(a,"b",function(){return d}),e.d(a,"a",function(){return h}),e.d(a,"e",function(){return p}),e.d(a,"c",function(){return f});var n=e("b775"),i="cloud-manager";function s(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/summary/institutions/each/period"),method:"post",data:t})}function o(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/summary/site/each/period"),method:"post",data:t})}function r(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/institutions/period"),method:"post",data:t})}function c(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/summary/institutions/all/period"),method:"post",data:t})}function u(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/summary/site/all/period"),method:"post",data:t})}function l(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/site/period"),method:"post",data:t})}function d(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/find/summary/finished"),method:"post",data:t})}function h(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/find/typed/duration"),method:"post",data:t})}function p(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/find/typed/table"),method:"post",data:t})}function f(t){return Object(n["a"])({url:"/".concat(i,"/api/job/v3/site/all"),method:"post",data:t})}},8695:function(t,a,e){"use strict";var n=e("3d9a"),i=e.n(n);i.a},"8f55":function(t,a,e){},aae3:function(t,a,e){var n=e("d3f4"),i=e("2d95"),s=e("2b4c")("match");t.exports=function(t){var a;return n(t)&&(void 0!==(a=t[s])?!!a:"RegExp"==i(t))}},ba3b:function(t,a,e){"use strict";e.r(a);var n=function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"job-monitor"},[e("div",{staticClass:"job-monitor-date"},[e("div",{staticClass:"select-date"},[e("span",{staticClass:"line-title"},[t._v(t._s(t.$t("Date"))+" ：")]),e("el-date-picker",{staticClass:"picker",attrs:{type:"daterange","range-separator":"~","value-format":"timestamp","start-placeholder":t.$t("start"),"end-placeholder":t.$t("end"),"picker-options":t.pickerOptions},on:{change:t.initi},model:{value:t.timevalue,callback:function(a){t.timevalue=a},expression:"timevalue"}}),e("span",{staticClass:"institution"},[t._v(t._s(t.$t("Institution"))+" : ")]),e("el-select",{staticClass:"select",attrs:{filterable:"",placeholder:"请选择"},on:{change:t.changeInstitution},model:{value:t.searchData.institutions,callback:function(a){t.$set(t.searchData,"institutions",a)},expression:"searchData.institutions"}},[e("el-option",{key:"",attrs:{label:t.$t("m.common.all"),value:""}}),t._l(t.institutionsSelectList,function(t,a){return e("el-option",{key:a,attrs:{label:t.label,value:t.value}})})],2),e("span",{staticClass:"site"},[t._v(t._s(t.$t("Site"))+" : ")]),e("el-select",{staticClass:"select",attrs:{disabled:0==t.searchData.institutions.length,placeholder:"请选择"},on:{change:t.getProgressData},model:{value:t.searchData.site,callback:function(a){t.$set(t.searchData,"site",a)},expression:"searchData.site"}},[e("el-option",{key:"",attrs:{label:t.$t("m.common.all"),value:""}}),t._l(t.sitesSelectList,function(t,a){return e("el-option",{key:a,attrs:{label:t.label,value:t.value}})})],2)],1),e("div",{staticClass:"totals"},[e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Total jobs"))+" : ")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t.totalData.total)+"\n            ")]),e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Success"))+" : ")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t.totalData.successfulJobs)+"("+t._s(t.totalData.successfulRatio)+")\n            ")]),e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Failed"))+" : ")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t.totalData.failedJobs)+"("+t._s(t.totalData.failedRatio)+")\n            ")]),e("div",{staticClass:"jobs-sign"},[e("span",{staticClass:"success-sign"}),e("span",[t._v(" "+t._s(t.$t("Success"))+" ")]),e("span",{staticClass:"failed-sign"}),e("span",[t._v(" "+t._s(t.$t("Failed"))+" ")])])]),e("div",{staticClass:"job-modeling"},[e("div",{staticClass:"echarts-line"},[e("jobMonitorProgress",{attrs:{chartData:t.chartData,lang:t.lang},on:{setProgressIndex:t.changeProgressType}})],1)])]),e("div",{staticClass:"job-details"},[e("div",{staticClass:"job-info"},[e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Jobs"))+" : ")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t.selectData.typeListData.total)+"\n            ")]),e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Success"))+" : ")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t.selectData.typeListData.successfulJobs)+"("+t._s(t.selectData.typeListData.successfulRatio)+")\n            ")]),e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Failed"))+" : ")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t.selectData.typeListData.failedJobs)+"("+t._s(t.selectData.typeListData.failedRatio)+")\n            ")]),e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Average duration"))+" : ")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t._f("timeFormat")(t.selectData.typeListData.avgDuration))+"\n            ")]),e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Min duration"))+" :")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t._f("timeFormat")(t.selectData.typeListData.minDuration))+"\n            ")]),e("span",{staticClass:"total-jobs"},[t._v(t._s(t.$t("Max duration"))+" :")]),e("span",{staticClass:"span"},[t._v("\n                "+t._s(t._f("timeFormat")(t.selectData.typeListData.maxDuration))+"\n            ")])]),e("div",{staticClass:"table"},[e("div",{staticClass:"table-head"}),e("el-table",{attrs:{data:t.dayListData.list,border:"","max-height":"300"}},[e("el-table-column",{attrs:{prop:"time",label:t.$t("Date")}}),e("el-table-column",{attrs:{prop:"total",label:t.$t("Jobs")}}),e("el-table-column",{attrs:{prop:"success",label:t.$t("Success")},scopedSlots:t._u([{key:"default",fn:function(a){return[e("span",[t._v(t._s(a.row.success)+"("+t._s(a.row.successRatio)+")")])]}}])}),e("el-table-column",{attrs:{prop:"failed",label:t.$t("Failed")},scopedSlots:t._u([{key:"default",fn:function(a){return[e("span",[t._v(t._s(a.row.failed)+"("+t._s(a.row.failedRatio)+")")])]}}])}),e("el-table-column",{attrs:{prop:"mean",label:t.$t("Average duration")},scopedSlots:t._u([{key:"default",fn:function(a){return[e("span",[t._v(t._s(t._f("timeFormat")(a.row.mean)))])]}}])}),e("el-table-column",{attrs:{prop:"min",label:t.$t("Min duration")},scopedSlots:t._u([{key:"default",fn:function(a){return[e("span",[t._v(t._s(t._f("timeFormat")(a.row.min)))])]}}])}),e("el-table-column",{attrs:{prop:"max",label:t.$t("Max duration")},scopedSlots:t._u([{key:"default",fn:function(a){return[e("span",[t._v(t._s(t._f("timeFormat")(a.row.max)))])]}}])})],1)],1),e("div",{staticClass:"echarts-line"},[e("div",{staticClass:"line-title"},[t._v("\n                "+t._s(t.$t("Failed rate"))+"\n            ")]),e("jobMonitorfailed",{attrs:{chartData:t.failedChartData}})],1),e("div",{staticClass:"echarts-line"},[e("div",{staticClass:"line-title"},[t._v("\n                "+t._s(t.$t("Duration distribution"))+"\n            ")]),e("jobMonitorDur",{attrs:{chartData:t.durChartData}})],1)])])},i=[],s=(e("7f7f"),e("96cf"),e("3b8d")),o=e("cebc"),r=(e("5df3"),e("ac6a"),e("456d"),e("28a5"),e("c1df")),c=e.n(r),u=function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"failed-rate-echart"},[e("v-bar",{ref:"chart_progress",attrs:{lang:t.lang,data:t.chartData,options:t.chartExtend}})],1)},l=[],d=e("4360"),h=e("5f87"),p={zh:{start:"开始日期",end:"结束日期"},en:{start:"start",end:"end"}},f={name:"durationDistribution",props:{chartData:{type:Object,required:!0},lang:{type:Array,required:!0}},components:{},filters:{},data:function(){var t=this;return{selectedProgressBarIndex:0,emphasisStyle:{itemStyle:{shadowBlur:10,shadowColor:"rgba(0,0,0,0.3)",triggerOn:"click"}},chartExtend:{tooltip:{trigger:"axis",triggerOn:"mousemove|click",axisPointer:{type:"none"},backgroundColor:"rgba(45, 54, 66, .8)",textStyle:{color:"#fff",fontFamily:"OPPOSans",fontSize:12},formatter:function(a,e,n){var i=1*a[1].value+1*a[2].value,s=0===i?0:100*(a[2].value/i).toFixed(1),o=0===i?0:100*(a[1].value/i).toFixed(1);return'<div style="margin-bottom:10px">\n                                    <span style="\n                                    display:inline-block;\n                                    width:6px;\n                                    height:6px;\n                                    border-radius:100%;\n                                    background:#00C99E">\n                                    </span> \n                                    '.concat(t.$t("m.common.success")," : ").concat(a[2].value," (").concat(s,'%)\n                                </div>\n                                <div>\n                                    <span style="\n                                        display:inline-block;\n                                        width:6px;\n                                        height:6px;\n                                        border-radius:100%;\n                                        background:#E6EBF0">\n                                    </span> \n                                    ').concat(t.$t("m.common.failed")," : ").concat(a[1].value," (").concat(o,"%)\n                                </div>")}},xAxis:{data:this.lang,axisLine:{show:!1},splitLine:{show:!1},splitArea:{show:!1},axisTick:{show:!1},axisLabel:{fontSize:14,padding:[0,45,0,0],margin:12,fontFamily:"OPPOSans",fontWeight:600,opacity:.7,color:function(a,e){return e===t.selectedProgressBarIndex?"#4E5766":"#848C99"},formatter:function(a,e){return e===t.selectedProgressBarIndex?a+"\n\n﹀":a}},triggerEvent:!0},yAxis:{axisLine:{show:!1},splitLine:{show:!1},splitArea:{show:!1},axisLabel:{show:!1},axisTick:{show:!1},max:100},grid:{top:0,left:55,right:55,bottom:145},series:[{name:"bar",type:"bar",stack:"one",barWidth:8,data:[2,2,2,2],zlevel:1,itemStyle:{normal:{barBorderRadius:10,color:"#fff"},triggerOn:"click"}},{name:"bar",type:"bar",stack:"one",emphasis:this.emphasisStyle,hoverAnimation:!1,barWidth:8,data:this.chartData.failed,zlevel:1,itemStyle:{normal:{barBorderRadius:10,color:"#E6EBF0"},emphasis:{color:"#E6EBF0"}}},{name:"bar",type:"bar",stack:"one",emphasis:this.emphasisStyle,hoverAnimation:!1,barWidth:8,barCategoryGap:"105%",data:this.chartData.success,zlevel:1,itemStyle:{normal:{barBorderRadius:10,color:"#00C99E"},emphasis:{color:"#00C99E"}}},{type:"bar",barGap:"-675%",barWidth:100,data:[100,100,100,100],itemStyle:{normal:{barBorderRadius:0,color:function(a){return t.selectedProgressBarIndex===a.dataIndex?"#FAFBFC":"#FFFFFF"}}}}]}}},created:function(){var t=this;this.$i18n.mergeLocaleMessage("en",p.en),this.$i18n.mergeLocaleMessage("zh",p.zh),this.$nextTick(function(a){t.$refs.chart_progress.chart.on("click",function(a){t.clickProgress(a)})})},mounted:function(){},methods:{clickProgress:function(t){var a,e,n=Object(h["a"])("language")||d["a"].getters.language;e=0===t.dataIndex?0:t.dataIndex||this.getclickxAxisIndex(t),a="zh"===n?this.getEnglish(t.name||t.value):t.name||t.value,this.selectedProgressBarIndex=e,this.reFreshProgress(),this.$emit("setProgressIndex",a)},getclickxAxisIndex:function(t){return this.$t("xAxis").split(",").indexOf(t.value)},reFreshProgress:function(){var t=this.chartExtend.series;this.chartExtend.series=[],this.chartExtend.series=t},getEnglish:function(t){var a={"交集任务":"intersect","建模任务":"modeling","上传任务":"upload","下载任务":"download"};return a[t]}},watch:{chartData:{handler:function(t,a){t&&(this.chartExtend.series[1].data=t.failed,this.chartExtend.series[2].data=t.success)},deep:!0},lang:{handler:function(t,a){t&&(this.chartExtend.xAxis.data=t)}}}},m=f,b=(e("8695"),e("2877")),g=Object(b["a"])(m,u,l,!1,null,null,null),v=g.exports,y=function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"failed-rate-echart"},[e("v-chart",{attrs:{data:t.chartData,options:t.options}})],1)},x=[],D={zh:{start:"开始日期",end:"结束日期",faild:"失败率"},en:{start:"start",end:"end",faild:"faild"}},j={name:"durationDistribution",props:{chartData:{type:Object,required:!0}},components:{},filters:{},data:function(){var t=this;return{rotate:0,options:{tooltip:{trigger:"axis",show:!0,padding:[5,10,5,10],axisPointer:{lineStyle:{color:"#4AA2FF"}},formatter:function(a){return"<div style='min-width:60px;'>".concat(a[0].name,"</div>\n                                <div style='margin-top:5px;'>\n                                    <span style=\"\n                                        display:inline-block;\n                                        width:6px;\n                                        height:6px;\n                                        border-radius:100%;\n                                        background:#E6EBF0\">\n                                    </span> \n                                    ").concat(t.$t("faild")," : (").concat(a[0].value,"%)\n                                </div>")}},grid:{left:"0%",right:"1%",bottom:"5%",top:"5%",containLabel:!0},xAxis:[{type:"category",axisTick:{show:!1},axisLine:{lineStyle:{color:"#E6EBF0"}},boundaryGap:!0,nameRotate:"45deg",axisLabel:{color:"#848C99",margin:14,interval:0,rotate:0},data:t.chartData.day}],yAxis:[{type:"value",axisLine:{show:!1},axisTick:{show:!1},max:100,axisLabel:{show:!0,interval:"auto",fontFamily:"OPPOSans",fontWeight:400,formatter:"{value} %"},splitLine:{lineStyle:{type:"dashed",color:"#E6EBF0"}}}],series:[{type:"line",stack:"总量",itemStyle:{normal:{color:"#FF9D00",lineStyle:{width:2,type:"solid",color:"#FF9D00"}}},showSymbol:!1,symbol:"emptyCircle",symbolSize:6,data:t.chartData.data}]}}},created:function(){this.$i18n.mergeLocaleMessage("en",D.en),this.$i18n.mergeLocaleMessage("zh",D.zh)},mounted:function(){},methods:{initi:function(){}},watch:{chartData:{handler:function(t,a){t&&(this.options.xAxis[0].data=[],this.options.series[0].data=[],this.options.xAxis[0].axisLabel.rotate="",this.options.xAxis[0].axisLabel.rotate=t.day.length>20?40:0,this.options.series[0].data=t.data,this.options.xAxis[0].data=t.day)},deep:!0}}},_=j,O=(e("71be"),Object(b["a"])(_,y,x,!1,null,null,null)),C=O.exports,$=function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"failed-rate-echart"},[e("v-chart",{attrs:{data:t.chartData,options:t.options}})],1)},w=[],S=e("313e"),F=e.n(S),L={zh:{start:"开始日期",end:"结束日期",jobs:"任务数",duration:"时间区间",min:"分钟",hour:"小时",day:"天"},en:{start:"start",end:"end",jobs:"jobs",duration:"Duration",min:"min",hour:"h",day:"d"}},k={name:"durationDistribution",props:{chartData:{type:Array,required:!0}},components:{},filters:{},data:function(){var t=this;return{options:{tooltip:{trigger:"axis",show:!0,padding:[5,10,5,10],textStyle:{opacity:".1"},axisPointer:{lineStyle:{color:"#4AA2FF"}},formatter:function(a){return"<div>".concat(t.$t("duration")," : ").concat(a[0].name,"</div>\n                                <div style='margin-top:5px;'>").concat(t.$t("jobs")," : ").concat(a[0].value,"</div>")}},grid:{left:"0%",right:"1%",bottom:"5%",top:"5%",containLabel:!0},xAxis:[{type:"category",axisTick:{show:!1},axisLine:{lineStyle:{color:"#E6EBF0"}},boundaryGap:!0,axisLabel:{color:"#848C99",margin:14},data:["<1min","1～30min","30min～2h","2～6h","6～12h","6～12h",">1d"]}],yAxis:[{type:"value",axisLine:{show:!1},axisTick:{show:!1},axisLabel:{color:"#848C99",fontFamily:"OPPOSans",fontWeight:400},splitLine:{lineStyle:{type:"dashed",color:"#E6EBF0"}}}],series:[{type:"line",stack:"总量",itemStyle:{normal:{color:"#7CBCFF",lineStyle:{width:2,type:"solid",color:"#7CBCFF"}}},showSymbol:!1,symbol:"emptyCircle",symbolSize:6,areaStyle:{normal:{color:new F.a.graphic.LinearGradient(0,0,0,1,[{offset:0,color:"#7CBCFF"},{offset:.4,color:"#C0DEFF"},{offset:.8,color:"#EAF4FF"},{offset:1,color:"#fff"}])}},data:t.chartData}]}}},created:function(){this.$i18n.mergeLocaleMessage("en",L.en),this.$i18n.mergeLocaleMessage("zh",L.zh),this.options.xAxis[0].data=["<1".concat(this.$t("min")),"1～30".concat(this.$t("min")),"30".concat(this.$t("min"),"～2").concat(this.$t("hour")),"2～6".concat(this.$t("hour")),"6～12".concat(this.$t("hour")),"6～12".concat(this.$t("hour")),">1".concat(this.$t("day"))]},mounted:function(){},methods:{initi:function(){}},watch:{chartData:{handler:function(t,a){t&&(this.options.series[0].data=t)},deep:!0},"$i18n.locale":function(t){this.options.xAxis[0].data=[],this.options.xAxis[0].data=["<1".concat(this.$t("min")),"1～30".concat(this.$t("min")),"30".concat(this.$t("min"),"～2").concat(this.$t("hour")),"2～6".concat(this.$t("hour")),"6～12".concat(this.$t("hour")),"6～12".concat(this.$t("hour")),">1".concat(this.$t("day"))]}}},E=k,A=(e("ca05"),Object(b["a"])(E,$,w,!1,null,null,null)),M=A.exports,P=e("7cf8"),I=e("c6a8"),R={zh:{Date:"日期",start:"开始日期",end:"结束日期",Institution:"机构",Site:"站点","Total jobs":"总任务数",Success:"成功",Failed:"失败",Intersect:"交集任务",Modeling:"建模任务",Upload:"上传任务",Download:"下载任务",Jobs:"任务数","Average duration":"平均时长","Min duration":"最小时长","Max duration":"最大时长","Failed rate":"失败率统计","Duration distribution":"任务时长分布",xAxis:"交集任务,建模任务,上传任务,下载任务"},en:{Date:"Date",start:"start",end:"end",Institution:"Institution",Site:"Site","Total jobs":"Total jobs",Success:"Success",Failed:"Failed",Intersect:"Intersect",Modeling:"Modeling",Upload:"Upload",Download:"Download",Jobs:"Jobs","Average duration":"Average duration","Min duration":"Min duration","Max duration":"Max duration","Failed rate":"Failed rate","Duration distribution":"Duration distribution",xAxis:"Intersect,Modeling,Upload,Download"}},B=document.getElementById("ajaxLoading"),T={name:"monitor",components:{jobMonitorProgress:v,jobMonitorfailed:C,jobMonitorDur:M},filters:{},data:function(){return{lang:"",totalData:{},selectData:{typeListData:{}},institutionsSelectList:[],sitesSelectList:[],allChartData:{intersect:{},modeling:{},upload:{},download:{}},timevalue:[new Date("2021-03-01"),new Date("2021-03-31")],typeTotalData:{failed:"",failed_percent:"",success:"",success_percent:"",total:""},dayTotal:{failed:"",failed_percent:"",max:"",mean:"",min:"",success:"",success_percent:"",total:""},dayListData:{total:{},list:[]},searchData:{beginDate:new Date,endDate:new Date,institutions:"",site:""},pickerOptions:{disabledDate:function(t){return t.getTime()>Date.now()}},chartData:{success:[],failed:[]},failedChartData:{data:[],day:[]},durChartData:[]}},watch:{timevalue:{handler:function(t){this.$set(this.searchData,"beginDate",t[0]),this.$set(this.searchData,"endDate",t[1])},deep:!0,immediate:!0},"$i18n.locale":function(t){this.lang=this.$t("xAxis").split(",")}},created:function(){this.$i18n.mergeLocaleMessage("en",R.en),this.$i18n.mergeLocaleMessage("zh",R.zh),this.lang=this.$t("xAxis").split(","),this.getProgressData(),this.getinsSelectList()},mounted:function(){},methods:{initi:function(){this.getProgressData(50,10)},getProgressData:function(){var t=this,a=[],e=[];Object(P["b"])(this.searchData).then(function(n){var i=n.data,s=i.failedJobs,r=i.failedRatio,c=i.successfulJobs,u=i.successfulRatio,l=i.total;t.totalData={failedJobs:s,failedRatio:r,successfulJobs:c,successfulRatio:u,total:l};var d=n.data.jobTypeStatisticsBeans,h=t.allChartData,p=t.searchData;Object.keys(h).map(function(t){d.map(function(a){a.type===t&&(h[t].typeListData=a)}),a.push(h[t].typeListData.successfulJobs),e.push(h[t].typeListData.failedJobs),Promise.all([Object(P["a"])(Object(o["a"])({type:t},p)),Object(P["e"])(Object(o["a"])({type:t},p))]).then(function(a){h[t].durationList=a[0].data.map(function(t){return t.count}),h[t].typeTableList=a[1].data})}),t.$set(t.chartData,"success",a),t.$set(t.chartData,"failed",e),t.changeProgressType("intersect")})},getinsSelectList:function(){var t=Object(s["a"])(regeneratorRuntime.mark(function t(){var a;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(I["A"])();case 2:a=t.sent,this.institutionsSelectList=a.data.institutionsSet.map(function(t){return{value:t,label:t}});case 4:case"end":return t.stop()}},t,this)}));function a(){return t.apply(this,arguments)}return a}(),getSiteSelectList:function(){var t=Object(s["a"])(regeneratorRuntime.mark(function t(){var a;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:return this.$set(this.searchData,"site",""),t.next=3,Object(P["c"])({institutions:this.searchData.institutions});case 3:a=t.sent,this.sitesSelectList=a.data.map(function(t){return{value:t,label:t}});case 5:case"end":return t.stop()}},t,this)}));function a(){return t.apply(this,arguments)}return a}(),changeInstitution:function(){this.getSiteSelectList()},changeSite:function(){this.getProgressData()},changeProgressType:function(t){var a=this;this.selectData=this.allChartData[t],B.style.display="block",setTimeout(function(){a.setDayChartData(a.selectData.typeTableList),a.setFailedData(a.selectData.typeTableList),a.setDurData(a.selectData.durationList),B.style.display="none"},300)},setDayChartData:function(t){this.dayListData=[],this.dayListData.list=t&&t.map(function(t){return Object(o["a"])({time:c()(t.date).format("YYYY-MM-DD"),max:t.maxDuration,min:t.minDuration,mean:t.avgDuration,failed:t.failedCount,failedRatio:t.failedRatio,success:t.successCount,successRatio:t.successRatio},t)})},setFailedData:function(t){var a=[],e=[];t.map(function(t){a.push(t.failedCount),e.push(c()(t.date).format("YYYY-MM-DD"))}),this.$set(this.failedChartData,"data",a),this.$set(this.failedChartData,"day",e)},setDurData:function(t){this.durChartData=[],this.durChartData=t}}},z=T,J=(e("f885"),Object(b["a"])(z,n,i,!1,null,null,null));a["default"]=J.exports},c1a1:function(t,a,e){},c6a8:function(t,a,e){"use strict";e.d(a,"K",function(){return i}),e.d(a,"p",function(){return s}),e.d(a,"h",function(){return o}),e.d(a,"I",function(){return r}),e.d(a,"F",function(){return c}),e.d(a,"L",function(){return u}),e.d(a,"J",function(){return l}),e.d(a,"s",function(){return d}),e.d(a,"N",function(){return h}),e.d(a,"D",function(){return p}),e.d(a,"j",function(){return f}),e.d(a,"i",function(){return m}),e.d(a,"t",function(){return b}),e.d(a,"M",function(){return g}),e.d(a,"E",function(){return v}),e.d(a,"z",function(){return y}),e.d(a,"A",function(){return x}),e.d(a,"w",function(){return D}),e.d(a,"f",function(){return j}),e.d(a,"g",function(){return _}),e.d(a,"y",function(){return O}),e.d(a,"C",function(){return C}),e.d(a,"x",function(){return $}),e.d(a,"B",function(){return w}),e.d(a,"a",function(){return S}),e.d(a,"e",function(){return F}),e.d(a,"v",function(){return L}),e.d(a,"n",function(){return k}),e.d(a,"b",function(){return E}),e.d(a,"k",function(){return A}),e.d(a,"d",function(){return M}),e.d(a,"q",function(){return P}),e.d(a,"o",function(){return I}),e.d(a,"O",function(){return R}),e.d(a,"m",function(){return B}),e.d(a,"G",function(){return T}),e.d(a,"H",function(){return z}),e.d(a,"c",function(){return J}),e.d(a,"P",function(){return W}),e.d(a,"l",function(){return Y}),e.d(a,"r",function(){return q}),e.d(a,"u",function(){return G});var n=e("b775");function i(t){return Object(n["a"])({url:"/cloud-manager/api/site/page/cloudManager",method:"post",data:t})}function s(t){return Object(n["a"])({url:"/cloud-manager/api/group/findPagedRegionInfoNew",method:"post",data:t})}function o(t){return Object(n["a"])({url:"/cloud-manager/api/site/check",method:"post",data:t})}function r(t){return Object(n["a"])({url:"/cloud-manager/api/site/addNew",method:"post",data:t})}function c(t){return Object(n["a"])({url:"/cloud-manager/api/site/cloudManager/network",method:"post",data:t})}function u(t){return Object(n["a"])({url:"/cloud-manager/api/site/updateNew",method:"post",data:t})}function l(t){return Object(n["a"])({url:"/cloud-manager/api/site/delete",method:"post",data:t})}function d(t){return Object(n["a"])({url:"/cloud-manager/api/site/find",method:"post",data:t})}function h(t){return Object(n["a"])({url:"/cloud-manager/api/site/checkWeb",method:"post",data:t})}function p(t){return Object(n["a"])({url:"/cloud-manager/api/site/ip/list",method:"post",data:t})}function f(t){return Object(n["a"])({url:"/cloud-manager/api/site/ip/deal",method:"post",data:t})}function m(t){return Object(n["a"])({url:"/cloud-manager/api/site/checkSiteName",method:"post",data:t})}function b(t){return Object(n["a"])({url:"/cloud-manager/api/system/page",method:"post",data:t})}function g(t){return Object(n["a"])({url:"/cloud-manager/api/system/history",method:"post",data:t})}function v(t){return Object(n["a"])({url:"/cloud-manager/api/site/ip/query/history",method:"post",data:t})}function y(t){return Object(n["a"])({url:"/cloud-manager/api/site/institutions",method:"post",data:t})}function x(t){return Object(n["a"])({url:"/cloud-manager/api/site/institutions/all/dropdown",method:"post",data:t})}function D(t){return Object(n["a"])({url:"/cloud-manager/api/fate/user/institutions/all",method:"post",data:t})}function j(t){return Object(n["a"])({url:"/cloud-manager/api/authority/cancel",method:"post",data:t})}function _(t){return Object(n["a"])({url:"/cloud-manager/api/authority/cancelList",method:"post",data:t})}function O(t){return Object(n["a"])({url:"/cloud-manager/api/authority/history",method:"post",data:t})}function C(t){return Object(n["a"])({url:"/cloud-manager/api/authority/status",method:"post",data:t})}function $(t){return Object(n["a"])({url:"/cloud-manager/api/authority/details",method:"post",data:t})}function w(t){return Object(n["a"])({url:"/cloud-manager/api/authority/review",method:"post",data:t})}function S(t){return Object(n["a"])({url:"/cloud-manager/api/fate/user/institutions",method:"post",data:t})}function F(t){return Object(n["a"])({url:"/cloud-manager/api/authority/currentAuthority",method:"post",data:t})}function L(t){return Object(n["a"])({url:"/cloud-manager/api/dropdown/version",method:"post",data:t})}function k(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/exchange/page",method:"post",data:t})}function E(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/add",method:"post",data:t})}function A(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/delete",method:"post",data:t})}function M(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/rollsite/add",method:"post",data:t})}function P(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/rollsite/page",method:"post",data:t})}function I(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/query",method:"post",data:t})}function R(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/rollsite/publish",method:"post",data:t})}function B(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/rollsite/delete",method:"post",data:t})}function T(t){return Object(n["a"])({url:"/cloud-manager/api/exchange/rollsite/update",method:"post",data:t})}function z(t){return Object(n["a"])({url:"/cloud-manager/api/product/page",method:"post",data:t})}function J(t){return Object(n["a"])({url:"/cloud-manager/api/product/add",method:"post",data:t})}function W(t){return Object(n["a"])({url:"/cloud-manager/api/product/update",method:"post",data:t})}function Y(t){return Object(n["a"])({url:"/cloud-manager/api/product/delete",method:"post",data:t})}function q(t){return Object(n["a"])({url:"/cloud-manager/api/product/version",method:"post",data:t})}function G(t){return Object(n["a"])({url:"/cloud-manager/api/product/name",method:"post",data:t})}},ca05:function(t,a,e){"use strict";var n=e("c1a1"),i=e.n(n);i.a},f885:function(t,a,e){"use strict";var n=e("8f55"),i=e.n(n);i.a}}]);