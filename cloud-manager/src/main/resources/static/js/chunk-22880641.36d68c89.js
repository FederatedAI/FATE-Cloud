(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-22880641"],{"0a19":function(t,e,s){},"34c7":function(t,e,s){"use strict";s.d(e,"b",function(){return a}),s.d(e,"a",function(){return n});s("5df3"),s("4f7f");var i=s("75fc");s("c5f6"),s("28a5"),s("a481"),s("ac6a");function a(t){var e="";return t&&t.forEach(function(t){e=t.leftRegion===t.rightRegion?e.concat("".concat(t.leftRegion,";")):e.concat("".concat(t.leftRegion,"~").concat(t.rightRegion,";"))}),e.substr(0,e.length-1)}function n(t){var e=t.replace(/\s+/g,""),s=e.split(/;+；+|；+;+|;+|；+/),a=[];return s.forEach(function(t){var e=t.split(/~|-/).map(function(t){return parseInt(t)});if(2===e.length){if(e[0]<e[1]){var s={leftRegion:Number(e[0]),rightRegion:Number(e[1])};a.push(s)}}else if(e[0]){var i={leftRegion:Number(e[0]),rightRegion:Number(e[0])};a.push(i)}}),a=Object(i["a"])(new Set(a.map(function(t){return JSON.stringify(t)}))).map(function(t){return JSON.parse(t)}),a}},65823:function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAAABHNCSVQICAgIfAhkiAAAAVRJREFUOE+VlEFSwkAQRf/H7PUmcgOTnbCxXFsCnsDyJHoEgsVeN4Yd8QbhBhxB95BvzZAZhxBCmGVn5qX79+8mGs7tu+KeMIbQB9G3V4QCRFES6eKRef0Zw8Bwrr62eCUQN/3AxQTkvMDL1wMLF/MgA8EGSxBXbRD/TfhBhMTBLOhsyH9qHmZBg5lyAjedMqldMmVmIyashF12hUj4BTAl8ezelETCQSoTHHcBGQgjxEaX4UwfAO5sQ4WUw5mM8tcB6FNCTOIyhO9B6o0RCgOS105IszEn1gYb5A7WCqke10FrRri3qVcwc8+X02KRw9ICf1hbAHDgFp+tmsWume2Uz6zYR9tfwawEJxxv299qSAOzIh0fGwHf2YixH5GwS108VfnH+2pvaM+BhZbYJR2cao28nZo7U462mCyeuD5YIyHQNIAlJtwtNef6lYRCPUybFtsfcIXRwbaurX4AAAAASUVORK5CYII="},"7ec0":function(t,e,s){},"901a":function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAAABHNCSVQICAgIfAhkiAAAAXFJREFUOE/Fk0FOwlAURe9rbR2KKxB34BLq1IRYBsShdcAYWIEuwaGBCcwEBlZInKo7wBVQdlCGFvqv+YXCR9DEhMSOmrz3z7v3/vcFe/pkTxz8L8j3g0IYtmPTzU5FF5XqmW2hBqAoEG95IJorlvX/geCVRGPYb7Vz2AZIT0oPnSfj8GooFW9SYKQhECkQfB52W/4WSKvIm/IigYYtVhg+PkRbdTKeE+cv/dZI92eKtBLlOmM9yYRQVNakUom/D8n6yNhKZqc6rwxUuqqGArk0w7M+k+PUdb2UjFYQogPBtdlHIhz2muUfQYNuU0w7BCe22J6iGm+AllkZ1twIgqNM8XawEwoDS1keBLfrG8DUSpLiypouLKbLG8m6eTsgOlaS1JXr1iC4W2fISargb4SdF3XoieMUdwZr+CH5bicz31zKrT1S7sIigQ+ABYGcgJgSHAkkmpP3uYpfN7tUqQYi4g16zeAvD/p/H+0upXtT9AV107QTb0CmQQAAAABJRU5ErkJggg=="},a14e:function(t,e,s){"use strict";var i=s("7ec0"),a=s.n(i);a.a},a8b2:function(t,e,s){"use strict";var i=s("0a19"),a=s.n(i);a.a},aa77:function(t,e,s){var i=s("5ca1"),a=s("be13"),n=s("79e5"),o=s("fdef"),r="["+o+"]",c="​",l=RegExp("^"+r+r+"*"),d=RegExp(r+r+"*$"),u=function(t,e,s){var a={},r=n(function(){return!!o[t]()||c[t]()!=c}),l=a[t]=r?e(p):o[t];s&&(a[s]=l),i(i.P+i.F*r,"String",a)},p=u.trim=function(t,e){return t=String(a(t)),1&e&&(t=t.replace(l,"")),2&e&&(t=t.replace(d,"")),t};t.exports=u},ab54:function(t,e,s){"use strict";s.r(e);var i=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"site-add"},[i("div",{staticClass:"add-info"},[i("div",{staticClass:"title"},["siteadd"===t.type?i("span",[t._v(t._s(t.$t("m.siteAdd.addSite")))]):t._e(),"siteinfo"===t.type?i("span",[t._v(t._s(t.$t("m.siteAdd.waitingActivation")))]):t._e(),"siteUpdate"===t.type?i("span",[t._v(t._s(t.$t("m.siteAdd.siteUpdate")))]):t._e()]),i("el-form",{ref:"infoform",attrs:{model:t.form,"label-position":"left",rules:t.rules,"label-width":"250px"}},[i("div",{staticClass:"basic-info"},[t._v(t._s(t.$t("m.siteAdd.Basic Info")))]),i("el-form-item",{attrs:{label:t.$t("m.common.siteName"),prop:"siteName"}},["siteinfo"===t.type?i("span",{staticClass:"info-text require"},[t._v(t._s(t.form.siteName))]):i("el-input",{attrs:{placeholder:t.$t("m.siteAdd.maximum20chatacters")},on:{blur:t.toCheckSiteName,focus:function(e){return t.cancelValid("siteName")}},model:{value:t.form.siteName,callback:function(e){t.$set(t.form,"siteName",e)},expression:"form.siteName"}})],1),i("el-form-item",{attrs:{label:t.$t("m.common.siteInstitution"),prop:"institutions"}},["siteinfo"===t.type?i("span",{staticClass:"info-text"},[t._v(t._s(t.form.institutions))]):i("el-select",{attrs:{"popper-append-to-body":!1,filterable:"",placeholder:t.$t("m.siteAdd.chooseInstitutions")},on:{focus:function(e){return t.cancelValid("institutions")}},model:{value:t.form.institutions,callback:function(e){t.$set(t.form,"institutions",e)},expression:"form.institutions"}},t._l(t.institutionsdownList,function(t){return i("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1),"siteadd"===t.type||"siteUpdate"===t.type?i("span",{staticClass:"add-institutions",on:{click:t.toAddInstitutions}},[t._v(t._s(t.$t("m.common.add")))]):t._e()],1),i("el-form-item",{attrs:{label:t.$t("m.common.role"),prop:"role"}},["siteinfo"===t.type?i("span",{staticClass:"info-text"},[t._v(t._s(1===t.form.role?t.$t("m.common.guest"):t.$t("m.common.host")))]):i("el-select",{attrs:{"popper-append-to-body":!1,placeholder:t.$t("m.siteAdd.chooseRole")},on:{focus:function(e){return t.cancelValid("role")},change:t.getPartyid},model:{value:t.form.role,callback:function(e){t.$set(t.form,"role",e)},expression:"form.role"}},t._l(t.roleOp,function(t){return i("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1),i("el-form-item",{attrs:{label:t.$t("m.common.partyID")}},[i("span",{attrs:{slot:"label"},slot:"label"},[i("span",[i("span",[t._v(t._s(t.$t("m.common.partyID")))]),i("i",{staticClass:"el-icon-s-tools tools",staticStyle:{"margin-left":"15px",cursor:"pointer"},on:{click:t.toPartyid}})])]),"siteinfo"===t.type?i("span",{staticClass:"info-text"},[t._v(t._s(t.form.partyId))]):i("el-select",{attrs:{disabled:0===t.partyidname.length,filterable:"","popper-append-to-body":!1,placeholder:t.$t("m.siteAdd.chooseGroup")},on:{change:t.selectPartyid},model:{value:t.partyidSelect,callback:function(e){t.partyidSelect=e},expression:"partyidSelect"}},t._l(t.partyidname,function(t){return i("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1),i("el-form-item",{directives:[{name:"show",rawName:"v-show",value:("siteadd"===t.type||"siteUpdate"===t.type)&&t.partyidSelect,expression:"(type==='siteadd' || type==='siteUpdate') && partyidSelect"}]},[i("div",{staticClass:"dropdown"},[i("i",{staticClass:"el-icon-arrow-down down",on:{click:function(e){t.downshow=!t.downshow}}}),t.downshow?i("el-input",{attrs:{disabled:"",autosize:"",type:"textarea"},model:{value:t.groupRange,callback:function(e){t.groupRange=e},expression:"groupRange"}}):t._e(),t.downshow?t._e():i("div",{staticClass:"down-text"},[t._v(t._s(t.groupRange))])],1)]),"siteadd"===t.type||"siteUpdate"===t.type?i("el-form-item",{attrs:{label:"",prop:"partyId"}},[i("el-input",{attrs:{disabled:!t.partyidSelect,placeholder:t.$t("m.siteAdd.typePartyID")},on:{focus:function(e){return t.cancelValid("partyId")},blur:t.tocheckPartyid},model:{value:t.form.partyId,callback:function(e){t.$set(t.form,"partyId","string"===typeof e?e.trim():e)},expression:"form.partyId"}})],1):t._e(),i("el-form-item",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}],attrs:{prop:"exits"}},["siteUpdate"===t.type||"siteadd"===t.type?i("el-input",{model:{value:t.form.exits,callback:function(e){t.$set(t.form,"exits",e)},expression:"form.exits"}}):t._e()],1),"siteinfo"===t.type?i("el-form-item",{attrs:{label:"Federation Key"}},[t.keyViewDefault?i("span",{staticClass:"info-text"},[t._v(t._s(t.form.secretInfo.key)+" "),i("img",{staticClass:"view",attrs:{src:s("901a")},on:{click:function(e){t.keyViewDefault=!t.keyViewDefault}}})]):t._e(),t.keyViewDefault?t._e():i("span",{staticClass:"info-text"},[t._v("***********************"),i("img",{staticClass:"view",attrs:{src:s("c990")},on:{click:function(e){t.keyViewDefault=!t.keyViewDefault}}})])]):t._e(),"siteinfo"===t.type?i("el-form-item",{attrs:{label:"Federation Secret"}},[t.sansViewDefault?i("span",{staticClass:"info-text"},[t._v(t._s(t.form.secretInfo.secret)+" "),i("img",{staticClass:"view",attrs:{src:s("901a")},on:{click:function(e){t.sansViewDefault=!t.sansViewDefault}}})]):t._e(),t.sansViewDefault?t._e():i("span",{staticClass:"info-text"},[t._v("***********************"),i("img",{staticClass:"view",attrs:{src:s("c990")},on:{click:function(e){t.sansViewDefault=!t.sansViewDefault}}})])]):t._e(),i("span",{staticClass:"registration-box"},[i("el-form-item",{attrs:{label:"",prop:""}},[i("span",{attrs:{slot:"label"},slot:"label"},[i("span",{staticStyle:{color:"#4E5766"}},[t._v(t._s(t.$t("m.siteAdd.registrationLinkSetting")))])])]),i("el-form-item",{attrs:{label:t.$t("m.siteAdd.linkType"),prop:"protocol"}},["siteinfo"===t.type&&t.form.protocol?i("span",{staticClass:"info-text"},[t._v(t._s("https://"===t.form.protocol?"HTTPS":"HTTP"))]):i("el-radio-group",{model:{value:t.form.protocol,callback:function(e){t.$set(t.form,"protocol",e)},expression:"form.protocol"}},[i("el-radio",{attrs:{label:"https://"}},[t._v("HTTPS")]),i("el-radio",{attrs:{label:"http://"}},[t._v("HTTP")])],1)],1),i("el-form-item",{attrs:{label:t.$t("m.siteAdd.encryption"),prop:"encryptType"}},["siteinfo"===t.type&&t.form.encryptType?i("span",{staticClass:"info-text"},[t._v(t._s(1===t.form.encryptType?t.$t("m.siteAdd.encryptionType"):t.$t("m.siteAdd.unencrypted")))]):i("el-radio-group",{model:{value:t.form.encryptType,callback:function(e){t.$set(t.form,"encryptType",e)},expression:"form.encryptType"}},[i("el-radio",{attrs:{label:1}},[t._v(t._s(t.$t("m.siteAdd.encryptionType")))]),i("el-radio",{attrs:{label:2}},[t._v(t._s(t.$t("m.siteAdd.unencrypted")))])],1)],1),i("el-form-item",{attrs:{label:t.$t("m.siteAdd.proxyNetworkAccess"),prop:"network"}},["siteinfo"===t.type?i("span",{staticClass:"info-text"},[t._v(t._s(t.form.network))]):i("el-input",{model:{value:t.form.network,callback:function(e){t.$set(t.form,"network",e)},expression:"form.network"}}),"siteadd"===t.type||"siteUpdate"===t.type?i("span",{staticClass:"add-institutions",on:{click:t.toResetNetwork}},[t._v(t._s(t.$t("m.siteAdd.resetDefault")))]):t._e()],1),"siteinfo"===t.type?i("el-form-item",{attrs:{label:t.$t("m.siteAdd.registrationLink")}},[i("el-popover",{attrs:{placement:"top",width:"300",trigger:"hover",content:t.form.registrationLink}},[i("span",{staticClass:"link-text",staticStyle:{color:"#217AD9"},attrs:{slot:"reference"},slot:"reference"},[t._v(t._s(t.form.registrationLink))])]),i("span",{staticClass:"copy formcopy",attrs:{"data-clipboard-text":t.form.registrationLink},on:{click:function(e){return t.toCopy("from")}}},[t._v(t._s(t.$t("m.common.copy")))])],1):t._e()],1),i("div",{staticClass:"basic-info"},[t._v(t._s(t.$t("m.siteAdd.Exchange Info")))]),i("el-form-item",{attrs:{label:"Exchange",prop:"exchangeId"}},["siteinfo"===t.type?i("span",{staticClass:"info-text"},[t._v(t._s(t.form.exchangeName))]):i("el-select",{attrs:{placeholder:t.$t("m.siteAdd.exchange")},on:{focus:function(e){return t.cancelValid("exchangeId")}},model:{value:t.form.exchangeId,callback:function(e){t.$set(t.form,"exchangeId",e)},expression:"form.exchangeId"}},t._l(t.exchangeList,function(t){return i("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1)],1),i("div",{staticClass:"Submit"},["siteadd"===t.type?i("el-button",{attrs:{type:"primary"},on:{click:t.submitAction}},[t._v(t._s(t.$t("m.common.submit")))]):t._e(),"siteUpdate"===t.type?i("el-button",{attrs:{type:"primary"},on:{click:t.submitAction}},[t._v(t._s(t.$t("m.common.resubmit")))]):t._e(),"siteinfo"===t.type?i("el-button",{attrs:{type:"primary"},on:{click:t.modifyAction}},[t._v(t._s(t.$t("m.common.modify")))]):t._e()],1)],1),i("el-dialog",{staticClass:"ok-dialog",attrs:{visible:t.okdialog,"close-on-click-modal":!1,"close-on-press-escape":!1},on:{"update:visible":function(e){t.okdialog=e}}},[i("div",{staticClass:"icon"},[i("i",{staticClass:"el-icon-success"})]),"siteadd"===t.type?i("div",{staticClass:"line-text-one"},[t._v(t._s(t.$t("m.siteAdd.addSuccessfully")))]):t._e(),"siteUpdate"===t.type?i("div",{staticClass:"line-text-one"},[t._v(t._s(t.$t("m.siteAdd.modifySuccessfully")))]):t._e(),i("div",{staticClass:"line-text-two"},[t._v(t._s(t.$t("m.siteAdd.registrationLinkGenerated")))]),i("div",{staticClass:"line-text-three"},[i("el-popover",{attrs:{placement:"top",width:"660",trigger:"hover",offset:"-30",content:t.form.registrationLink}},[i("span",{staticClass:"copy-link",attrs:{slot:"reference"},slot:"reference"},[t._v(t._s(t.form.registrationLink))])]),i("span",{staticClass:"copy dialogcopy",attrs:{"data-clipboard-text":t.form.registrationLink},on:{click:function(e){return t.toCopy("tooltip")}}},[t._v(t._s(t.$t("m.common.copy")))])],1),i("div",{staticClass:"dialog-footer"},[i("el-button",{staticClass:"ok-btn",attrs:{type:"primary"},on:{click:t.okAction}},[t._v(t._s(t.$t("m.common.OK")))])],1)]),i("el-dialog",{staticClass:"site-toleave-dialog",attrs:{visible:t.isleavedialog,width:"700px"},on:{"update:visible":function(e){t.isleavedialog=e}}},[i("div",{staticClass:"line-text-one"},[t._v(t._s(t.$t("m.siteAdd.sureLeavePage")))]),i("div",{staticClass:"line-text-two"},[t._v(t._s(t.$t("m.siteAdd.notSavedTips")))]),i("div",{staticClass:"dialog-footer"},[i("el-button",{staticClass:"sure-btn",attrs:{type:"primary"},on:{click:t.sureLeave}},[t._v(t._s(t.$t("m.common.sure")))]),i("el-button",{staticClass:"sure-btn",attrs:{type:"info"},on:{click:t.cancelLeave}},[t._v(t._s(t.$t("m.common.cancel")))])],1)]),i("siteaddip",{ref:"siteaddip"})],1)},a=[],n=(s("a481"),s("cebc")),o=(s("96cf"),s("3b8d")),r=(s("7f7f"),s("28a5"),s("ac6a"),s("3b2b"),s("c6a8")),c=s("34c7"),l=s("b311"),d=s.n(l),u=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("el-dialog",{staticClass:"add-dialog",attrs:{visible:t.adddialog,"close-on-click-modal":!1,"close-on-press-escape":!1,width:"520px"},on:{"update:visible":function(e){t.adddialog=e}}},[i("div",{staticClass:"add-dialog-head"},[i("el-button",{staticClass:"addUrl",attrs:{disabled:t.addDisabled,type:"text"},on:{click:t.addentrancesSelect}},[i("i",{staticClass:"el-icon-circle-plus"}),i("span",[t._v(t._s(t.$t("m.common.add")))])]),i("span",{staticClass:"total"},[t._v(t._s(t.$t("m.common.total"))+":"+t._s(t.addtotal))])],1),i("div",{staticClass:"add-dialog-body"},[i("div",{staticClass:"line-box"},t._l(t.entrancesSelect,function(e,s){return i("div",{key:s,staticClass:"line",on:{dblclick:function(e){return t.getEdit(s)}}},[e.show?t._e():i("span",[i("span",{staticClass:"network-text"},[t._v(t._s(e.ip))]),"entrances"===t.networkacesstype?i("span",{staticClass:"telent",on:{click:function(e){return t.testTelent(s)}}},[t._v(t._s(t.$t("m.siteAdd.telnet")))]):t._e(),i("i",{staticClass:"el-icon-close del",on:{click:function(e){return t.deleteEntrances(s)}}})]),e.show?i("el-input",{staticClass:"input-show",attrs:{autocomplete:"off",id:"close",placeholder:t.$t("m.siteAdd.typeLike")+": 127.0.0.1:8080"},on:{blur:function(e){return t.closeEntrances(s)}},model:{value:t.entrancesInput,callback:function(e){t.entrancesInput=e},expression:"entrancesInput"}},[i("i",{staticClass:"el-icon-check check",attrs:{slot:"suffix"},on:{blur:function(e){return t.closeEntrances(s)}},slot:"suffix"}),i("i",{staticClass:"el-icon-close del",staticStyle:{right:"2px"},attrs:{slot:"suffix"},on:{click:function(e){return t.deleteEntrances(s)},mousedown:t.mouseDown},slot:"suffix"})]):t._e()],1)}),0),i("div",{staticClass:"message-add",style:{opacity:t.showMes,transition:"all 0.2s ease"}},[t.telnetsuccess?i("span",{staticClass:"tips"},[i("img",{attrs:{src:s("65823")}}),i("span",[t._v(t._s(t.$t("m.siteAdd.telnetSuccess"))+"!\n                        "),i("span",{staticStyle:{color:"#4AA2FF"}},[t._v(t._s(t.ipPost))])])]):t._e(),t.invalidsuccess?i("span",{staticClass:"tips"},[i("img",{attrs:{src:s("98a8")}}),i("span",[t._v(t._s(t.$t("m.siteAdd.invalidInput"))+"!")])]):t._e(),t.telnetfail?i("span",{staticClass:"tips"},[i("img",{attrs:{src:s("98a8")}}),i("span",[t._v(t._s(t.$t("m.siteAdd.unableConnect"))+"!\n                        "),i("span",{staticStyle:{color:"#4AA2FF"}},[t._v(t._s(t.ipPost))])])]):t._e()])]),i("div",{staticClass:"add-dialog-footer"},[i("el-button",{staticClass:"save-btn",attrs:{disabled:t.saveDisabled,type:"primary"},on:{click:t.saveAction}},[t._v(t._s(t.$t("m.common.save")))]),i("el-button",{staticClass:"cancel-btn",attrs:{type:"info"},on:{click:t.cancelAction}},[t._v(t._s(t.$t("m.common.cancel")))])],1)])],1)},p=[],f=(s("5df3"),s("4f7f"),s("75fc")),m={name:"siteAddIp",components:{},data:function(){return{adddialog:!1,entrancesInput:"",networkacesstype:"",showMes:0,timeLess:null,entrancesSelect:[],addtotal:"",telnetsuccess:!1,telnetfail:!1,invalidsuccess:!1,saveDisabled:!1,addDisabled:!1,canEdit:!0,ipPost:"",verifyPromise:null}},watch:{entrancesSelect:{handler:function(t){this.addtotal=t.length,t.length>=20?this.addDisabled=!0:this.saveDisabled||(this.addDisabled=!1),t.length>=20?this.saveDisabled=!0:this.saveDisabled=!1},deep:!0,immediate:!0}},created:function(){},methods:{addentrancesSelect:function(){this.entrancesSelect.push({ip:"",show:!1,checked:!1}),this.canEdit=!0,this.getEdit(this.entrancesSelect.length-1)},deleteEntrances:function(t){this.entrancesInput="",this.entrancesSelect.splice(t,1),this.saveDisabled=!1,this.addDisabled=!1,this.canEdit=!0},deleteAll:function(){this.entrancesSelect=this.entrancesSelect.filter(function(t){return!t.checked})},getEdit:function(t){this.canEdit&&(this.entrancesSelect[t].show=!0,this.entrancesInput=this.entrancesSelect[t].ip,this.entrancesindex=t,this.canEdit=!1,this.addDisabled=!0,this.saveDisabled=!0,this.$nextTick(function(){var t=document.querySelector("#close");t.focus()}))},closeEntrances:function(t){var e=this,s=this.entrancesInput.split(":"),i=new RegExp(/^(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])$/),a=new RegExp(/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/);""===this.entrancesInput?this.deleteEntrances(t):a.test(s[1])&&i.test(s[0])?(this.entrancesSelect[this.entrancesindex].show=!1,this.entrancesSelect[this.entrancesindex].ip=this.entrancesInput,this.saveDisabled=!1,this.addDisabled=!1,this.canEdit=!0):(this.saveDisabled=!0,this.addDisabled=!0,this.invalidsuccess=!0,this.showMes=.8,setTimeout(function(){e.showMes=0,e.invalidsuccess=!1},2e3))},saveAction:function(){var t=Object(f["a"])(new Set(this.entrancesSelect.map(function(t){return"".concat(t.ip,";")})));"entrances"===this.networkacesstype?(this.$parent.form.networkAccessEntrances=t.join(""),this.$parent.$refs["infoform"].validateField("networkAccessEntrances",function(t){}),this.entrancesSelect=[]):"exit"===this.networkacesstype&&(this.$parent.form.networkAccessExits=t.join(""),this.$parent.$refs["infoform"].validateField("networkAccessExits",function(t){}),this.entrancesSelect=[]),this.adddialog=!1},mouseDown:function(t){t.preventDefault()},cancelAction:function(){this.showMes=0,this.entrancesSelect=[],this.saveDisabled=!1,this.addDisabled=!1,this.adddialog=!1},testTelent:function(t){var e=this,s={ip:this.entrancesSelect[t].ip.split(":")[0],port:parseInt(this.entrancesSelect[t].ip.split(":")[1])};this.ipPost=this.entrancesSelect[t].ip,Object(r["O"])(s).then(function(t){0===t.code&&(e.telnetsuccess=!0,e.showMes=.8,setTimeout(function(){e.telnetsuccess=!1,e.showMes=0},2e3))}).catch(function(t){109===t.code&&(e.telnetfail=!0,e.showMes=.8,setTimeout(function(){e.telnetfail=!1,e.showMes=0},2e3))})}}},h=m,v=(s("a8b2"),s("2877")),g=Object(v["a"])(h,u,p,!1,null,null,null),y=g.exports,A={name:"siteadd",components:{siteaddip:y},data:function(){var t=this;return{type:this.$route.query.type,id:"",isleave:!1,isleavedialog:!1,leaveRouteName:"",sansViewDefault:!1,keyViewDefault:!1,partyidname:[],partyidSelect:"",partyIdPostPass:!0,groupRange:"",okdialog:!1,downshow:!1,siteNameExists:!1,institutionsdownList:[],exchangeList:[],form:{groupId:"",siteName:"",institutions:"",role:"",partyId:"",network:"",secretInfo:{key:"",secret:""},exits:"",protocol:"https://",encryptType:1,registrationLink:"",mode:"short"},roleOp:[{value:1,label:this.$t("m.common.guest")},{value:2,label:this.$t("m.common.host")}],rules:{siteName:[{required:!0,trigger:"change",validator:function(e,s,i){var a=s.trim();a?a.length>20?i(new Error(t.$t("m.siteAdd.maxCharacters"))):t.siteNameExists?i(new Error(t.$t("m.siteAdd.alreadyExists"))):i():i(new Error(t.$t("m.siteAdd.siteNameRequired")))}}],institutions:[{required:!0,message:this.$t("m.siteAdd.institutionRrequired"),trigger:"bulr"}],role:[{required:!0,message:this.$t("m.siteAdd.roleRrequired"),trigger:"bulr"}],partyId:[{required:!0,trigger:"blur",validator:function(e,s,i){var a=new RegExp(/^-?[1-9]\d*$/),n=!0;t.groupRange.split(";").forEach(function(t){var e=t.split(/~|-/).map(function(t){return parseInt(t)}),i=parseInt(s);e&&e.length>1?e[0]<=i&&e[1]>=i&&(n=!1):e&&1===e.length&&e[0]===i&&(n=!1)}),s?n||!a.test(s)?i(new Error(t.$t("m.siteAdd.invalidPartyID"))):t.partyIdPostPass?i():i(new Error(t.$t("m.siteAdd.partyIDUsed"))):i(new Error(t.$t("m.siteAdd.partyIDRequired")))}}],network:[{required:!0,trigger:"change",validator:function(e,s,i){s?i():i(new Error(t.$t("m.siteAdd.proxyNetworkAccessRequired")))}}],exchangeId:[{required:!0,message:this.$t("m.siteAdd.ExchangRequired"),trigger:"bulr"}]}}},watch:{},computed:{},created:function(){var t=this;"siteinfo"===this.type&&(this.isleave=!0),this.$route.query.id&&this.getKeySansLink(),Object(r["a"])().then(function(e){e.data.forEach(function(e,s){var i={};i.value=e,i.label=e,t.institutionsdownList.push(i)})}),this.toResetNetwork(),this.getExchangeList()},beforeDestroy:function(){},mounted:function(){var t=this;this.$router.beforeEach(function(e,s,i){t.leaveRouteName=e.name,t.isleave?i():(i(!1),t.isleavedialog=!0)})},methods:{getExchangeList:function(){var t=this;Object(r["n"])().then(function(e){e.data.forEach(function(e){var s={};s.value=e.exchangeId,s.label=e.exchangeName,t.exchangeList.push(s)})})},toCheckSiteName:function(){var t=this;if(this.form.siteName){var e={siteName:this.form.siteName.trim(),id:this.$route.query.id};Object(r["i"])(e).then(function(e){0===e.code&&(t.siteNameExists=!1,t.$refs["infoform"].validateField("siteName"))}).catch(function(e){122===e.code&&(t.siteNameExists=!0,t.$refs["infoform"].validateField("siteName"))})}},tocheckPartyid:function(){var t=this;this.$refs["infoform"].validateField("partyId",function(e){if(e!==t.$t("m.siteAdd.partyIDRequired")&&e!==t.$t("m.siteAdd.invalidPartyID")){var s={id:t.$route.query.id,partyId:t.form.partyId,rangeInfo:Object(c["a"])(t.groupRange)};Object(r["g"])(s).then(function(e){0===e.code&&(t.partyIdPostPass=!0,t.$refs["infoform"].validateField("partyId",function(t){}))}).catch(function(e){103===e.code&&(t.partyIdPostPass=!1,t.$refs["infoform"].validateField("partyId",function(t){}))})}})},submitAction:function(){var t=this;this.form.siteName=this.form.siteName.trim(),"siteadd"===this.type?this.$refs["infoform"].validate(function(){var e=Object(o["a"])(regeneratorRuntime.mark(function e(s){var i,a;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:if(!s){e.next=6;break}return i=Object(n["a"])({},t.form),e.next=4,Object(r["J"])(i);case 4:a=e.sent,0===a.code&&(t.isleave=!0,t.id=a.data,t.okdialog=!0,t.getKeySansLink());case 6:case"end":return e.stop()}},e)}));return function(t){return e.apply(this,arguments)}}()):"siteUpdate"===this.type&&this.$refs["infoform"].validate(function(){var e=Object(o["a"])(regeneratorRuntime.mark(function e(s){var i,a;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:if(!s){e.next=6;break}return i=Object(n["a"])({},t.form),e.next=4,Object(r["N"])(i);case 4:a=e.sent,0===a.code&&(t.isleave=!0,t.id=t.$route.query.id,t.okdialog=!0,t.getKeySansLink());case 6:case"end":return e.stop()}},e)}));return function(t){return e.apply(this,arguments)}}())},modifyAction:function(){this.type="siteUpdate",this.isleave=!1},okAction:function(){this.okdialog=!1,this.$router.push({name:"Site Manage"})},sureLeave:function(){this.isleave=!0,this.isleavedialog=!1;var t="Admin Access"===this.leaveRouteName?{type:"FATEManager"}:"";this.$router.push({name:this.leaveRouteName,query:t})},cancelLeave:function(){this.$store.dispatch("SetMune","Site Manage"),this.isleavedialog=!1},toPartyid:function(){this.$router.push({name:"Party ID"})},getPartyid:function(){var t=Object(o["a"])(regeneratorRuntime.mark(function t(e){var s,i,a=this;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:return s={pageNum:1,pageSize:100,role:e},this.partyidname=[],"siteadd"!==this.type&&"siteUpdate"!==this.type||(this.partyidSelect="",this.form.partyId=""),t.next=5,Object(r["q"])(s);case 5:i=t.sent,i.data.list.forEach(function(t){var e={};e.value=t.groupName,e.label=t.groupName,e.rangeInfo=Object(c["b"])(t.federatedGroupDetailDos),e.groupId=t.groupId,a.partyidname.push(e)});case 7:case"end":return t.stop()}},t,this)}));function e(e){return t.apply(this,arguments)}return e}(),selectPartyid:function(t){var e=this;this.partyidname.forEach(function(s){t===s.value&&(e.groupRange=s.rangeInfo,e.form.groupId=s.groupId)})},getKeySansLink:function(){var t=Object(o["a"])(regeneratorRuntime.mark(function t(){var e,s,i,a;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:return e={id:this.id?parseInt(this.id):parseInt(this.$route.query.id)},t.next=3,Object(r["s"])(e);case 3:return s=t.sent,i=s.data.registrationLink,i=i.indexOf("?st")<0?JSON.stringify(i).replace(new RegExp('"',"g"),""):JSON.stringify(i).replace(new RegExp("\\\\","g"),""),"siteinfo"===this.type?(a=Object(n["a"])({},s.data),s.data.protocol||s.data.encryptType||(a.protocol="https://",a.encryptType=1),this.form=a,this.form.mode="short"):s.data.registrationLink.indexOf("?st")<0?this.form.registrationLink=i:this.form.registrationLink=s.data.registrationLink,this.partyidSelect=s.data.groupName,t.next=10,this.getPartyid(s.data.role);case 10:return t.next=12,this.selectPartyid(s.data.groupName);case 12:case"end":return t.stop()}},t,this)}));function e(){return t.apply(this,arguments)}return e}(),toCopy:function(t){var e=this;if("tooltip"===t){var s=new d.a(".dialogcopy");s.on("success",function(t){e.$message.success(e.$t("m.common.copySuccess")),s.destroy()})}if("from"===t){var i=new d.a(".formcopy");i.on("success",function(t){e.$message.success(e.$t("m.common.copySuccess")),i.destroy()})}},cancelValid:function(t){this.$refs["infoform"].clearValidate(t),this["".concat(t,"warnshow")]=!1},toAddInstitutions:function(){this.$router.push({name:"Admin Access",query:{type:"FATE Manager"}})},toResetNetwork:function(){var t=this;Object(r["G"])().then(function(e){t.form.network=e.data.network})}}},b=A,w=(s("a14e"),Object(v["a"])(b,i,a,!1,null,null,null));e["default"]=w.exports},c5f6:function(t,e,s){"use strict";var i=s("7726"),a=s("69a8"),n=s("2d95"),o=s("5dbc"),r=s("6a99"),c=s("79e5"),l=s("9093").f,d=s("11e9").f,u=s("86cc").f,p=s("aa77").trim,f="Number",m=i[f],h=m,v=m.prototype,g=n(s("2aeb")(v))==f,y="trim"in String.prototype,A=function(t){var e=r(t,!1);if("string"==typeof e&&e.length>2){e=y?e.trim():p(e,3);var s,i,a,n=e.charCodeAt(0);if(43===n||45===n){if(s=e.charCodeAt(2),88===s||120===s)return NaN}else if(48===n){switch(e.charCodeAt(1)){case 66:case 98:i=2,a=49;break;case 79:case 111:i=8,a=55;break;default:return+e}for(var o,c=e.slice(2),l=0,d=c.length;l<d;l++)if(o=c.charCodeAt(l),o<48||o>a)return NaN;return parseInt(c,i)}}return+e};if(!m(" 0o1")||!m("0b1")||m("+0x1")){m=function(t){var e=arguments.length<1?0:t,s=this;return s instanceof m&&(g?c(function(){v.valueOf.call(s)}):n(s)!=f)?o(new h(A(e)),s,m):A(e)};for(var b,w=s("9e1e")?l(h):"MAX_VALUE,MIN_VALUE,NaN,NEGATIVE_INFINITY,POSITIVE_INFINITY,EPSILON,isFinite,isInteger,isNaN,isSafeInteger,MAX_SAFE_INTEGER,MIN_SAFE_INTEGER,parseFloat,parseInt,isInteger".split(","),k=0;w.length>k;k++)a(h,b=w[k])&&!a(m,b)&&u(m,b,d(h,b));m.prototype=v,v.constructor=m,s("2aba")(i,f,m)}},c990:function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAAABHNCSVQICAgIfAhkiAAAAVNJREFUOE/VkzFSwlAURe/9kVDKDoQduAOxdcYxFI6lWFCrKxBXoLZSiKVaGIcZW+MO2AFhB1gSzHvOj3wMoDijNKRL/nvn33tfHrGkh0viYEVAO/uNTc/gGECZYNXaV2gEIE4FV88Pre5sJFPWgqBeSouFR9f8U34W6g1HtTBsD1zNBGRVrBEvIEvuUIFTb5i07Xta9OsELnLw+F205tRlIKtE/EJvFqKUrlFzY2uEcmTEVEGcTWCqA5OMKlZZBto9aIQE9/JWDE0l1TQiuDH+HhuabVHp5etUEXbur2sLQSLSBbE+Drvv0avOgaBPnbtWkLPmx64pu1FxLkYiKrOMlFqft4Y3kyTliTVb+Bk2oxlY0xhzm2Ukcgii+TUI7aeCYCpsd5iN3y+EJLcWrY6qvnrJKPh2/PnGsboThZYd1DYTjN9VL3/9If+zwCuytH+x+AEzK5sT5xPjdwAAAABJRU5ErkJggg=="},fdef:function(t,e){t.exports="\t\n\v\f\r   ᠎             　\u2028\u2029\ufeff"}}]);