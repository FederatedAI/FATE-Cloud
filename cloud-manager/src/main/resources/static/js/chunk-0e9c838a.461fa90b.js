(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-0e9c838a"],{"124f":function(e,t,a){},"1e3a":function(e,t){e.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAGKADAAQAAAABAAAAGAAAAADB/VeXAAACHUlEQVRIDe1UMVbbQBCdEQkpkxsgGkyqmBPYN4DcIJzApgMq09gpzQngBignwL6BqAIV4ga4jHlo8v+u1xKSbZLKjfc9aWd3Z/7Mn5ldkc1Ydwb0vQDi09+x6FZH1NpQblLfRFIxHYm9XmY/v2arMFY6iM/vhyraAeJEVD67mWiUZ8NMh9lg7ySsq/NSB/HZQ6oq30zsEojPAD8Ci5EDMG3DSQIuXxgAGWX9xkEVnOuFDkLkJnos2x8TmU4fJc8PJIp+eBBN1Gxkn7Zjmb4cqdgVA8n6+11/XvyjQvQSc+6josHeNQHE7K6ca+ynAEx5Rh2C08bVqwJYYxCir+j903IRixoDdEcbOf312G8oP+T5yfJ8lzL2L/g5GXs8C3q0QcZb1UhqDlhYKKWFou6U0xP2/Z7uhDVtEI1r49KefCgv/lcms7mNuaAm8/VMqDEwkzuclSKxp0XFQxujbfVq9/zB4rP7W+KZ5SU77wGs3o73ioxcX+BO8I7c0JI1oBPKCO57NmgklMOoMZDchjxkR/jC6rGh570McFEC9KhTH9at7tUYUCGwCBdN/0wzRx8XDTe2x4jpsAq2aL+mFIzmTwXeGjwRLN4heI0dQ42SWbdVUmTjbLDfDhicl3YRctl0TFTmtMEolsi9Sz3YuhpgLoZt9YqFl5YyCIqugyLt8gL6x0+e3bukUROMuqraQo3GqA1e1bcFDhibeb0Z+AsR0/+1TdgjJAAAAABJRU5ErkJggg=="},"2f21":function(e,t,a){"use strict";var r=a("79e5");e.exports=function(e,t){return!!e&&r(function(){t?e.call(null,function(){},1):e.call(null)})}},"3fc2":function(e,t,a){"use strict";var r=a("124f"),n=a.n(r);n.a},"55dd":function(e,t,a){"use strict";var r=a("5ca1"),n=a("d8e8"),i=a("4bf8"),o=a("79e5"),s=[].sort,l=[1,2,3];r(r.P+r.F*(o(function(){l.sort(void 0)})||!o(function(){l.sort(null)})||!a("2f21")(s)),"Array",{sort:function(e){return void 0===e?s.call(i(this)):s.call(i(this),n(e))}})},"9fb8":function(e,t,a){"use strict";a.r(t);var r=function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("div",{staticClass:"partyid-box"},[r("div",{staticClass:"partyid-header"},[r("el-button",{staticClass:"add",attrs:{type:"text"},on:{click:e.addPartyid}},[r("img",{attrs:{src:a("1e3a")}}),r("span",[e._v(e._s(e.$t("add")))])]),r("el-input",{staticClass:"input input-placeholder",attrs:{clearable:"",placeholder:e.$t("Search ID Group")},model:{value:e.data.groupName,callback:function(t){e.$set(e.data,"groupName","string"===typeof t?t.trim():t)},expression:"data.groupName"}}),r("el-select",{staticClass:"sel-institutions input-placeholder",attrs:{placeholder:e.$t("Type")},model:{value:e.data.role,callback:function(t){e.$set(e.data,"role",t)},expression:"data.role"}},e._l(e.typeSelect,function(e){return r("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1),r("el-button",{staticClass:"go",attrs:{type:"primary"},on:{click:e.toSearch}},[e._v(e._s(e.$t("GO")))])],1),r("div",{staticClass:"partyid-body"},[r("div",{staticClass:"table"},[r("el-table",{attrs:{data:e.tableData,"header-row-class-name":"tableHead","header-cell-class-name":"tableHeadCell","cell-class-name":"tableCell",height:"100%","tooltip-effect":"light"}},[r("el-table-column",{attrs:{type:"index",label:e.$t("Index"),width:"170"}}),r("el-table-column",{attrs:{prop:"groupName",label:e.$t("ID Group")}}),r("el-table-column",{attrs:{prop:"role",label:e.$t("Type")},scopedSlots:e._u([{key:"default",fn:function(t){return[r("span",[e._v(e._s(1===t.row.role?"Guest":"Host"))])]}}])}),r("el-table-column",{attrs:{prop:"rangeInfo",label:e.$t("ID range"),"show-overflow-tooltip":""}}),r("el-table-column",{attrs:{prop:"total",label:e.$t("Total")}}),r("el-table-column",{attrs:{prop:"used",label:e.$t("Used")},scopedSlots:e._u([{key:"default",fn:function(t){return[r("span",{staticClass:"delete",on:{click:function(a){return e.toUser(t.row)}}},[e._v(e._s(t.row.used))])]}}])}),r("el-table-column",{attrs:{prop:"name",label:"Action",width:"120",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[r("el-button",{attrs:{type:"text"}},[r("i",{staticClass:"el-icon-edit edit",on:{click:function(a){return e.handleEdit(t.row)}}})]),t.row.used?r("el-button",{attrs:{type:"text",disabled:""}},[r("i",{staticClass:"el-icon-delete-solid "})]):r("el-button",{attrs:{type:"text"}},[r("i",{staticClass:"el-icon-delete-solid delete",on:{click:function(a){return e.handleDelete(t.row)}}})])]}}])})],1)],1),r("div",{staticClass:"pagination"},[r("el-pagination",{attrs:{background:"","current-page":e.currentPage,"page-size":e.data.pageSize,layout:"total, prev, pager, next, jumper",total:e.total},on:{"current-change":e.handleCurrentChange,"update:currentPage":function(t){e.currentPage=t},"update:current-page":function(t){e.currentPage=t}}})],1)]),r("el-dialog",{staticClass:"partyid-delete-dialog",attrs:{visible:e.dialogVisible,width:"700px"},on:{"update:visible":function(t){e.dialogVisible=t}}},[r("div",{staticClass:"line-text-one"},[e._v(e._s(e.$t("Are you sure you want to delete"))+' "'+e._s(e.groupName)+'"?')]),r("div",{staticClass:"line-text-two"},[e._v(e._s(e.$t("You can't undo this action"))+" ")]),r("div",{staticClass:"dialog-footer"},[r("el-button",{staticClass:"ok-btn",attrs:{type:"primary"},on:{click:e.okAction}},[e._v("Ok")])],1)]),r("partyid-add",{ref:"partyidadd",attrs:{title:e.title}})],1)},n=[],i=a("cebc"),o=a("75fc"),s=(a("ac6a"),function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"partyid-add"},[a("el-dialog",{staticClass:"add-groud",attrs:{title:"Edit"!==e.title?e.$t("Add a new ID Group"):e.$t("Edit"),visible:e.dialogVisible,"close-on-click-modal":!1,"close-on-press-escape":!1,"before-close":e.cancelAction},on:{"update:visible":function(t){e.dialogVisible=t}}},[a("el-form",{ref:"groudform",attrs:{model:e.partidform,rules:e.rules,"label-position":"left","label-width":"128px"}},[a("el-form-item",{attrs:{label:e.$t("Name"),prop:"groupName"}},[a("el-input",{class:{"edit-text":!0,groupNamewarn:e.groupNamewarn},on:{blur:e.toCheckGroupName,focus:function(t){return e.cancelValid("groupName")}},model:{value:e.partidform.groupName,callback:function(t){e.$set(e.partidform,"groupName","string"===typeof t?t.trim():t)},expression:"partidform.groupName"}})],1),a("el-form-item",{attrs:{label:e.$t("Type"),prop:"role"}},["Edit"!==e.title?a("el-select",{staticClass:"edit-text select",attrs:{"popper-append-to-body":!1,placeholder:e.$t("Type")},model:{value:e.partidform.role,callback:function(t){e.$set(e.partidform,"role",t)},expression:"partidform.role"}},e._l(e.roleSelect,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1):e._e(),"Edit"===e.title?a("span",{staticClass:"info-text",staticStyle:{color:"#B8BFCC"}},[e._v(e._s(1===e.partidform.role?"Guest":"Host"))]):e._e()],1),a("el-form-item",{attrs:{label:"ID range",prop:"rangeInfo"}},[a("span",{attrs:{slot:"label"},slot:"label"},[a("span",[a("span",[e._v(e._s(e.$t("ID range")))]),a("el-tooltip",{attrs:{effect:"dark",placement:"bottom"}},[a("div",{staticStyle:{"font-size":"14px"},attrs:{slot:"content"},slot:"content"},["Edit"!==e.title?a("span",[a("div",[e._v(e._s(e.$t("PartyID's coding rules are limited to natural Numbers only,")))]),a("div",[e._v(e._s(e.$t("and a natural number uniquely represents a party.")))])]):e._e(),"Edit"===e.title?a("span",[a("div",[e._v("Support for adding new rules or modifying unused rules")]),a("div",[e._v("in addition to the used coding rules.")])]):e._e()]),a("i",{staticClass:"el-icon-info range-info"})])],1)]),a("el-input",{staticClass:"textarea",attrs:{type:"textarea",placeholder:"Enter an ID range like:10000~19999;11111"},on:{blur:e.toReset,focus:function(t){return e.cancelValid("rangeInfo")}},model:{value:e.partidform.rangeInfo,callback:function(t){e.$set(e.partidform,"rangeInfo","string"===typeof t?t.trim():t)},expression:"partidform.rangeInfo"}})],1)],1),a("div",{staticClass:"dialog-footer"},[a("el-button",{staticStyle:{"margin-right":"14px"},attrs:{type:"primary",disabled:e.submitbtn},on:{click:e.submitAction}},[e._v("Submit")]),a("el-button",{attrs:{type:"info"},on:{click:e.cancelAction}},[e._v("Cancel")])],1)],1)],1)}),l=[],d=(a("28a5"),a("55dd"),a("3b2b"),a("a481"),a("90e7")),u=a("34c7"),c={zh:{"Add a new ID Group":"添加新ID组",Edit:"编辑ID组",Name:"分组名",Type:"ID类型","ID range":"ID范围","PartyID's coding rules are limited to natural Numbers only,":"站点ID的编码规则仅限于自然数，","and a natural number uniquely represents a party.":"且每个ID唯一对应一个站点。"},en:{"Add a new ID Group":"Add a new ID Group",Edit:"Edit",Name:"Name",Type:"Type","ID range":"ID range","PartyID's coding rules are limited to natural Numbers only,":"PartyID's coding rules are limited to natural Numbers only,","and a natural number uniquely represents a party.":"and a natural number uniquely represents a party."}},p={name:"partyadd",components:{},props:{title:{type:String,default:function(){return""}}},data:function(){var e=this;return{submitbtn:!0,dialogVisible:!1,checkPass:!1,groupNamewarn:!1,beenUsed:"",cannotEdit:"",partidform:{},roleSelect:[{value:1,label:"Guest"},{value:2,label:"Host"}],rules:{groupName:[{required:!0,trigger:"blur",validator:function(t,a,r){a?e.groupNamewarn?(e.groupNamewarn=!0,r(new Error("Group name exist! "))):r():(e.groupNamewarn=!0,r(new Error("Name field is required.")))}}],role:[{required:!0,trigger:"change",message:"Type field is required."}],rangeInfo:[{required:!0,trigger:"blur",validator:function(t,a,r){if(a){a=a.replace(/\s+/g,"");var n=new RegExp(/[\u4E00-\u9FA5]/g),i=new RegExp("[`!@#$^&*()=|{}':',\\[\\].<>/?！@#￥……&*（）——|{}【】‘：”“'。，、？]"),o=new RegExp(/[a-zA-Z]+/),s=a.split(/;+；+|；+;+|;+|；+/).sort(u).filter(function(e){return e}),l=s.every(function(e,t){var a,r=e.split(/~|-/).map(function(e){return parseInt(e)});return!(t>0&&(a=s[t-1].split(/~|-/).map(function(e){return parseInt(e)}),r[0]<(a[1]?a[1]:a[0])))&&(2!==r.length?!(1!==r.length||!r[0]):!!(r[0]<r[1]&&r[0]&&r[1])||void 0)});if(a)if(i.test(a)||o.test(a)||n.test(a)||!l)r(new Error("Invalid input."));else if(e.cannotEdit.intervalWithPartyIds.length>0||e.cannotEdit.sets.length>0){var d="";e.cannotEdit.forEach(function(e){e.region.leftRegion===e.region.rightRegion?d+='"'.concat(e.region.leftRegion,'" cannot be edited,in which ').concat(e.usedPartyIds,". it have been assigned to the sites. "):d+='"'.concat(e.region.leftRegion,"-").concat(e.region.rightRegion,'" cannot be edited,in which ').concat(e.usedPartyIds,". etc have been assigned to the sites.")}),r(new Error(d))}else e.beenUsed?r(new Error("".concat(e.beenUsed.leftRegion,"-").concat(e.beenUsed.rightRegion," ID range have been used, please edit again!"))):r();else r(new Error("ID range field is required."))}else r(new Error("ID range field is required."));function u(e,t){var a=e.split(/~|-/)[0],r=t.split(/~|-/)[0];return a-r}}}]}}},watch:{partidform:{handler:function(e){e.rangeInfo&&e.groupName&&e.role?this.submitbtn=!1:this.submitbtn=!0},immediate:!0,deep:!0}},computed:{},created:function(){this.$i18n.mergeLocaleMessage("en",c.en),this.$i18n.mergeLocaleMessage("zh",c.zh)},methods:{toCheckGroupName:function(){var e=this,t={groupName:this.partidform.groupName,groupId:this.partidform.groupId};Object(d["g"])(t).then(function(t){e.groupNamewarn=!1,e.$refs["groudform"].validateField("groupName",function(e){})}).catch(function(t){e.groupNamewarn=!0,e.$refs["groudform"].validateField("groupName",function(e){})})},submitAction:function(){var e=this;this.$refs["groudform"].validate(function(t){if(t){var a=Object(i["a"])({},e.partidform);a.regions=Object(u["a"])(e.partidform.rangeInfo),"Edit"===e.title?Object(d["i"])(a).then(function(t){Object(d["s"])(a).then(function(t){e.dialogVisible=!1,e.$parent.initList(),e.$message({type:"success",message:"success!",duration:3e3}),e.$refs["groudform"].resetFields()}).catch(function(t){e.beenUsed=t.data,e.$refs["groudform"].validateField("rangeInfo",function(e){})})}).catch(function(t){e.cannotEdit=Object(o["a"])(t.data),e.$refs["groudform"].validateField("rangeInfo",function(e){})}):Object(d["h"])(a).then(function(t){Object(d["q"])(a).then(function(t){e.dialogVisible=!1,e.$parent.initList(),e.$message({type:"success",message:"success!",duration:3e3}),e.$refs["groudform"].resetFields()})}).catch(function(t){e.beenUsed=t.data,e.$refs["groudform"].validateField("rangeInfo",function(e){})})}})},cancelAction:function(){this.dialogVisible=!1,this.$refs["groudform"].resetFields()},toReset:function(){this.cannotEdit.intervalWithPartyIds="",this.beenUsed=""},cancelValid:function(e){this.$refs["groudform"].clearValidate(e),this["".concat(e,"warn")]=!1}}},f=p,g=(a("3fc2"),a("2877")),m=Object(g["a"])(f,s,l,!1,null,null,null),h=m.exports,b={zh:{add:"添加",Index:"序号","ID Group":"ID组",Type:"ID类型","ID range":"ID范围",Total:"ID总数",Used:"已使用","Are you sure you want to delete":"确认删除","You can't undo this action":"删除操作不可撤回","Search ID Group":"搜索ID组"},en:{add:"add",Index:"Index","ID Group":"ID Group",Type:"Type","ID range":"ID range",Total:"Total",Used:"Used","Are you sure you want to delete":"Are you sure you want to delete","You can't undo this action":"You can't undo this action","Search ID Group":"Search ID Group"}},A={name:"PartyId",components:{partyidAdd:h},data:function(){return{title:"",partidform:{},dialogVisible:!1,currentPage:1,total:0,tableData:[],delTtempGroup:"",groupName:"",typeSelect:[{value:0,label:"Type"},{value:1,label:"Guest"},{value:2,label:"Host"}],data:{pageNum:1,pageSize:20}}},created:function(){this.initList(),this.$i18n.mergeLocaleMessage("en",b.en),this.$i18n.mergeLocaleMessage("zh",b.zh)},methods:{initList:function(){var e=this;for(var t in this.data)if(this.data.hasOwnProperty(t)){var a=this.data[t];a||delete this.data[t]}Object(d["r"])(this.data).then(function(t){e.tableData=[],t.data.list.length&&(Object(o["a"])(t.data.list).forEach(function(t){2!==t.status&&(t.rangeInfo=Object(u["b"])(t.federatedGroupDetailDos),e.tableData.push(t))}),e.total=t.data.totalRecord)})},toSearch:function(){this.data.pageNum=1,this.initList()},handleCurrentChange:function(e){this.data.pageNum=e,this.initList()},handleDelete:function(e){this.delTtempGroup=e.groupId,this.groupName=e.groupName,this.dialogVisible=!0},okAction:function(e){var t=this,a={groupId:this.delTtempGroup};Object(d["k"])(a).then(function(e){t.dialogVisible=!1,t.initList(),t.$message({type:"success",message:"Delete successful!",duration:5e3})})},toUser:function(e){this.$router.push({name:"partyuser",path:"/setting/partyuser",query:{groupId:e.groupId,groupName:e.groupName}})},addPartyid:function(){this.title="Add a new ID Group",this.$refs["partyidadd"].dialogVisible=!0,this.$refs["partyidadd"].partidform={role:1},this.$refs["partyidadd"].groupNamewarn=!1,this.$refs["partyidadd"].cannotEdit={sets:[],intervalWithPartyIds:[]},this.$refs["partyidadd"].beenUsed=!1},handleEdit:function(e){this.title="Edit",this.$refs["partyidadd"].dialogVisible=!0,this.$refs["partyidadd"].partidform=Object(i["a"])({},e),this.$refs["partyidadd"].groupNamewarn=!1,this.$refs["partyidadd"].cannotEdit={sets:[],intervalWithPartyIds:[]},this.$refs["partyidadd"].beenUsed=!1}}},y=A,v=(a("ae8e"),Object(g["a"])(y,r,n,!1,null,null,null));t["default"]=v.exports},ae8e:function(e,t,a){"use strict";var r=a("ece8"),n=a.n(r);n.a},ece8:function(e,t,a){}}]);