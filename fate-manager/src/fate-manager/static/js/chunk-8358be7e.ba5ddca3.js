(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-8358be7e"],{"5f873":function(e,t,s){},abf7:function(e,t,s){"use strict";var a=s("5f873"),i=s.n(a);i.a},f38d:function(e,t,s){"use strict";s.r(t);var a=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"access"},[s("div",{staticClass:"access-box"},[s("div",{staticClass:"partyid-header"},[s("el-button",{staticClass:"add",attrs:{type:"primary"},on:{click:e.addRoleUser}},[e._v("Add")]),s("el-input",{staticClass:"input input-placeholder",attrs:{clearable:"",placeholder:"Name"},model:{value:e.data.userName,callback:function(t){e.$set(e.data,"userName","string"===typeof t?t.trim():t)},expression:"data.userName"}},[s("i",{staticClass:"el-icon-search search el-input__icon",attrs:{slot:"prefix"},on:{click:e.getList},slot:"prefix"})]),s("el-select",{staticClass:"sel-institutions input-placeholder",attrs:{placeholder:"Site"},model:{value:e.data.partyId,callback:function(t){e.$set(e.data,"partyId",t)},expression:"data.partyId"}},e._l(e.partyIdSiteList,function(e){return s("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1),s("el-select",{staticClass:"sel-institutions input-placeholder",attrs:{placeholder:"Role"},model:{value:e.data.roleId,callback:function(t){e.$set(e.data,"roleId",t)},expression:"data.roleId"}},e._l(e.typeSelect,function(e){return s("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1),s("el-button",{staticClass:"go",attrs:{type:"primary"},on:{click:e.getList}},[e._v("GO")])],1),s("div",{staticClass:"table"},[s("el-table",{attrs:{data:e.tableData,"header-row-class-name":"tableHead","header-cell-class-name":"tableHeadCell","cell-class-name":"tableCell","tooltip-effect":"light"}},[s("el-table-column",{attrs:{prop:"userName",label:"Name","show-overflow-tooltip":""}}),s("el-table-column",{attrs:{prop:"site.siteName",label:"Site"}}),s("el-table-column",{attrs:{prop:"role.roleName",label:"Role","show-overflow-tooltip":""}}),s("el-table-column",{attrs:{prop:"permissionList",label:"Permission","show-overflow-tooltip":""},scopedSlots:e._u([{key:"default",fn:function(t){return e._l(t.row.permissionList,function(t,a){return s("span",{key:a},[e._v(e._s(t.permissionName)+";")])})}}])}),s("el-table-column",{attrs:{prop:"creator",label:"Creator","show-overflow-tooltip":""}}),s("el-table-column",{attrs:{prop:"createTime",label:"Create Time"},scopedSlots:e._u([{key:"default",fn:function(t){return[s("span",[e._v(e._s(e._f("dateFormat")(t.row.createTime)))])]}}])}),s("el-table-column",{attrs:{prop:"Action",label:"Action",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.cloudUser?s("span",[s("el-button",{attrs:{type:"text",disabled:""}},[s("i",{staticClass:"el-icon-edit",staticStyle:{"margin-right":"10px"}})]),s("el-button",{attrs:{type:"text",disabled:""}},[s("i",{staticClass:"el-icon-delete-solid"})])],1):e.userName===t.row.userName?s("span",[s("el-button",{attrs:{type:"text"}},[s("i",{staticClass:"el-icon-edit edit",on:{click:function(s){return e.handleEdit(t.row)}}})]),s("el-button",{attrs:{type:"text",disabled:""}},[s("i",{staticClass:"el-icon-delete-solid"})])],1):s("span",[s("el-button",{attrs:{type:"text"}},[s("i",{staticClass:"el-icon-edit edit",on:{click:function(s){return e.handleEdit(t.row)}}})]),s("el-button",{attrs:{type:"text"}},[s("i",{staticClass:"el-icon-delete-solid delete",on:{click:function(s){return e.handleDelete(t.row)}}})])],1)]}}])})],1)],1)]),s("el-dialog",{staticClass:"access-delete-dialog",attrs:{visible:e.deletedialog,width:"700px"},on:{"update:visible":function(t){e.deletedialog=t}}},[s("div",{staticClass:"line-text-one"},[e._v("Are you sure you want to delete this user?")]),s("div",{staticClass:"dialog-footer"},[s("el-button",{staticClass:"ok-btn",attrs:{type:"primary"},on:{click:e.toDelet}},[e._v("Sure")]),s("el-button",{staticClass:"ok-btn",attrs:{type:"info"},on:{click:function(t){e.deletedialog=!1}}},[e._v("Cancel")])],1)]),s("el-dialog",{staticClass:"access-add-dialog",attrs:{visible:e.adddialog,width:"700px","close-on-click-modal":!1,"close-on-press-escape":!1},on:{"update:visible":function(t){e.adddialog=t}}},[s("div",{staticClass:"dialog-box"},[s("el-form",{ref:"ruleForm",attrs:{model:e.siteTemp,rules:e.rules}},[s("div",{staticClass:"dialog-title"},[s("span",[e._v(e._s(e.typetitle))]),e._v("\n          user\n          ")]),s("el-form-item",{staticClass:"add-input",attrs:{prop:"userName"}},[s("span",{staticClass:"input-title"},[e._v("\n                  User\n              ")]),"Edit"!==e.typetitle?s("el-select",{attrs:{filterable:"",remote:"","reserve-keyword":"",placeholder:"Please enter a name","remote-method":e.remoteMethod,loading:e.loading},on:{change:e.userchange,focus:function(t){return e.toclearValid("userName")}},model:{value:e.siteTemp.userName,callback:function(t){e.$set(e.siteTemp,"userName",t)},expression:"siteTemp.userName"}},e._l(e.userList,function(e){return s("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1):s("span",{staticStyle:{"font-size":"16px"}},[e._v(e._s(e.siteTemp.userName))])],1),s("el-form-item",{staticClass:"add-input",attrs:{prop:"roleId"}},[s("span",{staticClass:"input-title"},[e._v("Role")]),s("el-radio-group",{model:{value:e.siteTemp.roleId,callback:function(t){e.$set(e.siteTemp,"roleId",t)},expression:"siteTemp.roleId"}},[s("el-radio",{attrs:{label:"1"}},[e._v("admin")]),s("el-radio",{attrs:{label:"2"}},[e._v("Developer or OP")]),s("el-radio",{attrs:{label:"3"}},[e._v("Business or Data Analyst")])],1)],1),e.sitedisable?e._e():s("el-form-item",{staticClass:"add-input",attrs:{prop:"siteName"}},[s("span",{staticClass:"input-title"},[e._v("Site")]),s("el-select",{attrs:{disabled:e.sitedisable,filterable:"",remote:"",placeholder:""},on:{change:e.sitechange,focus:function(t){return e.toclearValid("siteName")}},model:{value:e.siteTemp.siteName,callback:function(t){e.$set(e.siteTemp,"siteName",t)},expression:"siteTemp.siteName"}},e._l(e.siteList,function(e){return s("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1)],1)],1),s("div",{staticClass:"permission"},[s("span",{staticClass:"input-title"},[e._v("Permission")]),s("span",{staticClass:"item"},e._l(e.permissionList,function(t,a){return s("span",{key:a},[t.show?s("div",{staticClass:"show-item"},[s("i",{staticClass:"el-icon-check"}),e._v("\n                      "+e._s(t.item)+"\n                  ")]):s("div",{staticClass:"hide-item"},[s("span",[e._v(e._s(t.item))])])])}),0)]),s("div",{staticClass:"dialog-foot"},[s("el-button",{attrs:{type:"primary"},on:{click:e.toSure}},[e._v("Sure")]),s("el-button",{attrs:{type:"info"},on:{click:function(t){e.adddialog=!1}}},[e._v("Cancel")])],1)],1)])],1)},i=[],l=(s("ac4d"),s("8a81"),s("cebc")),r=(s("ac6a"),s("3191")),o=s("c1df"),n=s.n(o),d=s("2f62"),c={name:"access",filters:{dateFormat:function(e){return e?n()(e).format("YYYY-MM-DD HH:mm:ss"):"--"}},data:function(){return{typetitle:"Edit",adddialog:!1,deletedialog:!1,userWarn:!1,siteWarn:!1,userList:[],siteList:[],partyIdSiteList:[{value:0,label:"Site"}],loading:!0,sitedisable:!0,deletesite:{},siteTemp:{userId:"",userName:"",partyId:"",siteName:"",roleId:"1"},tableData:[],permissionList:[{item:"FATE Cloud: Basic management",show:!0},{item:"FATE Cloud: Auto-deploy",show:!0},{item:"FATE Studio",show:!0},{item:"FATEBoard",show:!0}],typeSelect:[{value:0,label:"Role"},{value:1,label:"Admin"},{value:2,label:"Developer or OP"},{value:3,label:"Business or Data Analyst"}],rules:{userName:[{required:!0,message:"The User Name field is required !",trigger:"blur"}],siteName:[{required:!0,message:"The Site field is required !",trigger:"blur"}]},data:{partyId:"",roleId:"",userName:""}}},watch:{"siteTemp.roleId":{handler:function(e){"1"===e?(this.sitedisable=!0,this.siteTemp.siteName="",this.permissionList.forEach(function(e){e.show=!0})):"2"===e?(this.sitedisable=!0,this.siteTemp.siteName="",this.permissionList.forEach(function(e,t){e.show=0===t||1===t})):"3"===e&&(this.sitedisable=!1,this.permissionList.forEach(function(e,t){e.show=0!==t&&1!==t}))},immediate:!0}},computed:Object(l["a"])({},Object(d["mapGetters"])(["userName"])),created:function(){this.getList(),this.tositeListDown()},mounted:function(){},methods:{getList:function(){var e=this;for(var t in this.data)if(this.data.hasOwnProperty(t)){var s=this.data[t];s||delete this.data[t]}Object(r["a"])(this.data).then(function(t){e.tableData=t.data})},tositeListDown:function(){var e=this;this.siteList=[],Object(r["A"])().then(function(t){t.data&&t.data.forEach(function(t){var s=Object(l["a"])({},t);s.value=t.partyId,s.label=t.siteName,e.siteList.push(s),e.partyIdSiteList.push(s)})})},handleEdit:function(e){this.typetitle="Edit",this.adddialog=!0,this.siteListObj={value:e.site.partyId,label:e.site.siteName},this.siteTemp={roleId:"".concat(e.role.roleId),partyId:e.site.partyId,siteName:e.site.siteName,userId:e.userId,userName:e.userName}},addRoleUser:function(){this.typetitle="Add",this.adddialog=!0,this.siteTemp={userId:"",userName:"",partyId:"",siteName:"",roleId:"1"}},toSure:function(){var e,t=this;"1"===this.siteTemp.roleId?e=[1,2,3,4,5]:"2"===this.siteTemp.roleId?e=[1,2]:"3"===this.siteTemp.roleId&&(e=[3,4,5]);var s={creator:this.userName,permissionList:e,roleId:parseInt(this.siteTemp.roleId),partyId:this.siteTemp.partyId,siteName:this.siteTemp.siteName,userId:this.siteTemp.userId,userName:this.siteTemp.userName};for(var a in s)if(s.hasOwnProperty(a)){var i=s[a];""===i&&0!==i&&delete s[a]}this.$refs["ruleForm"].validate(function(e){e&&("Add"===t.typetitle?Object(r["b"])(s).then(function(e){t.adddialog=!1,t.getList()}):"Edit"===t.typetitle&&Object(r["g"])(s).then(function(e){t.adddialog=!1,t.getList()}))})},handleDelete:function(e){this.deletedialog=!0,this.deletesite=e},toDelet:function(){var e=this,t={partyId:this.deletesite.site.partyId,siteName:this.deletesite.site.siteName,userId:this.deletesite.userId,userName:this.deletesite.userName};for(var s in t)if(t.hasOwnProperty(s)){var a=t[s];""===a&&0!==a&&delete t[s]}Object(r["f"])(t).then(function(t){e.deletedialog=!1,e.getList()})},remoteMethod:function(e){var t=this;if(this.loading=!0,this.userList=[],""!==e){var s={context:e};Object(r["E"])(s).then(function(e){e.data&&e.data.forEach(function(e){var s=Object(l["a"])({},e);s.value=e.userId,s.label=e.userName,t.userList.push(s)})}),this.loading=!1}},userchange:function(e){var t=!0,s=!1,a=void 0;try{for(var i,l=this.userList[Symbol.iterator]();!(t=(i=l.next()).done);t=!0){var r=i.value;if(e===r.value)return this.siteTemp.userId=r.userId,void(this.siteTemp.userName=r.userName)}}catch(o){s=!0,a=o}finally{try{t||null==l.return||l.return()}finally{if(s)throw a}}},sitechange:function(e){var t=!0,s=!1,a=void 0;try{for(var i,l=this.siteList[Symbol.iterator]();!(t=(i=l.next()).done);t=!0){var r=i.value;if(e===r.value)return this.siteTemp.partyId=r.partyId,void(this.siteTemp.siteName=r.siteName)}}catch(o){s=!0,a=o}finally{try{t||null==l.return||l.return()}finally{if(s)throw a}}},toclearValid:function(e){this.$refs["ruleForm"].clearValidate(e)}}},u=c,p=(s("abf7"),s("2877")),m=Object(p["a"])(u,a,i,!1,null,null,null);t["default"]=m.exports}}]);