(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-eb7adc18"],{"4f92":function(t,e,n){"use strict";var o=n("52fe"),a=n.n(o);a.a},"52fe":function(t,e,n){},"77b8":function(t,e,n){"use strict";n.r(e);var o=function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("div",{staticClass:"home"},[o("el-header",[o("div",{staticClass:"begin",on:{click:t.toHome}},[o("span",[t._v("FATE Cloud")])]),o("div",{staticClass:"name-bar"},[t.loginName?o("el-popover",{attrs:{placement:"bottom","popper-class":"bar-pop","visible-arrow":!1,trigger:"click"}},[o("div",{staticClass:"mane",on:{click:t.tologout}},[t._v(t._s(t.$t("Sign out")))]),o("div",{attrs:{slot:"reference"},slot:"reference"},[o("span",[t._v(t._s(t.loginName))]),o("i",{staticClass:"el-icon-caret-bottom"})])]):o("span",{on:{click:t.tologin}},[t._v(t._s(t.$t("Sign in")))])],1),o("div",{staticClass:"lang-bar"},[o("el-dropdown",{attrs:{trigger:"click"},on:{command:t.handleCommand}},[o("span",{staticClass:"text-link"},[o("span",[t._v(t._s("zh"===t.language?"中文":"English"))]),o("i",{staticClass:"el-icon-caret-bottom"})]),o("el-dropdown-menu",{staticClass:"dropdown",attrs:{slot:"dropdown"},slot:"dropdown"},[o("el-dropdown-item",{attrs:{command:"zh",disabled:"zh"===t.language}},[t._v("中文")]),o("el-dropdown-item",{attrs:{command:"en",disabled:"en"===t.language}},[t._v("English")])],1)],1)],1)]),o("el-main",[o("img",{attrs:{src:n("e053")}}),o("router-view")],1)],1)},a=[],s=n("cebc"),i=n("879a"),c=n("2f62"),l={zh:{"Sign in":"登录","Sign out":"退出"},en:{"Sign in":"Sign in","Sign out":"Sign out"}},r={name:"home",components:{},data:function(){return{name:""}},watch:{},created:function(){this.$store.dispatch("setloginname",localStorage.getItem("name")).then(function(t){localStorage.setItem("name",t)}),this.$i18n.mergeLocaleMessage("en",l.en),this.$i18n.mergeLocaleMessage("zh",l.zh)},computed:Object(s["a"])({},Object(c["mapGetters"])(["loginName","language"])),methods:{toHome:function(){},tologin:function(){this.$router.push({path:"/home/login"})},tologout:function(t){var e=this;Object(i["c"])().then(function(t){e.$store.dispatch("setloginname","").then(function(t){localStorage.setItem("name",t)}),e.$router.push({path:"/home/welcome"})})},handleCommand:function(t){this.$i18n.locale=t,this.$store.dispatch("setLanguage",t)}}},m=r,g=(n("4f92"),n("2877")),d=Object(g["a"])(m,o,a,!1,null,null,null);e["default"]=d.exports},e053:function(t,e,n){t.exports=n.p+"img/welcomepage.8ceb5537.svg"}}]);