(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-9fd3ed18"],{"0a19":function(t,e,i){},"1af6":function(t,e,i){var s=i("63b6");s(s.S,"Array",{isArray:i("9003")})},"20fd":function(t,e,i){"use strict";var s=i("d9f6"),n=i("aebd");t.exports=function(t,e,i){e in t?s.f(t,e,n(0,i)):t[e]=i}},"34c7":function(t,e,i){"use strict";i.d(e,"b",function(){return n}),i.d(e,"a",function(){return a});i("5df3"),i("4f7f");var s=i("75fc");i("c5f6"),i("28a5"),i("a481"),i("ac6a");function n(t){var e="";return t&&t.forEach(function(t){e=t.leftRegion===t.rightRegion?e.concat("".concat(t.leftRegion,";")):e.concat("".concat(t.leftRegion,"~").concat(t.rightRegion,";"))}),e.substr(0,e.length-1)}function a(t){var e=t.replace(/\s+/g,""),i=e.split(/;+；+|；+;+|;+|；+/),n=[];return i.forEach(function(t){var e=t.split(/~|-/).map(function(t){return parseInt(t)});if(2===e.length){if(e[0]<e[1]){var i={leftRegion:Number(e[0]),rightRegion:Number(e[1])};n.push(i)}}else if(e[0]){var s={leftRegion:Number(e[0]),rightRegion:Number(e[0])};n.push(s)}}),n=Object(s["a"])(new Set(n.map(function(t){return JSON.stringify(t)}))).map(function(t){return JSON.parse(t)}),n}},"4f7f":function(t,e,i){"use strict";var s=i("c26b"),n=i("b39a"),a="Set";t.exports=i("e0b8")(a,function(t){return function(){return t(this,arguments.length>0?arguments[0]:void 0)}},{add:function(t){return s.def(n(this,a),t=0===t?0:t,t)}},s)},"549b":function(t,e,i){"use strict";var s=i("d864"),n=i("63b6"),a=i("241e"),r=i("b0dc"),o=i("3702"),c=i("b447"),l=i("20fd"),d=i("7cd6");n(n.S+n.F*!i("4ee1")(function(t){Array.from(t)}),"Array",{from:function(t){var e,i,n,u,f=a(t),p="function"==typeof this?this:Array,m=arguments.length,h=m>1?arguments[1]:void 0,v=void 0!==h,A=0,g=d(f);if(v&&(h=s(h,m>2?arguments[2]:void 0,2)),void 0==g||p==Array&&o(g))for(e=c(f.length),i=new p(e);e>A;A++)l(i,A,v?h(f[A],A):f[A]);else for(u=g.call(f),i=new p;!(n=u.next()).done;A++)l(i,A,v?r(u,h,[n.value,A],!0):n.value);return i.length=A,i}})},"54a1":function(t,e,i){i("6c1c"),i("1654"),t.exports=i("95d5")},"5df3":function(t,e,i){"use strict";var s=i("02f4")(!0);i("01f9")(String,"String",function(t){this._t=String(t),this._i=0},function(){var t,e=this._t,i=this._i;return i>=e.length?{value:void 0,done:!0}:(t=s(e,i),this._i+=t.length,{value:t,done:!1})})},65823:function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAAABHNCSVQICAgIfAhkiAAAAVRJREFUOE+VlEFSwkAQRf/H7PUmcgOTnbCxXFsCnsDyJHoEgsVeN4Yd8QbhBhxB95BvzZAZhxBCmGVn5qX79+8mGs7tu+KeMIbQB9G3V4QCRFES6eKRef0Zw8Bwrr62eCUQN/3AxQTkvMDL1wMLF/MgA8EGSxBXbRD/TfhBhMTBLOhsyH9qHmZBg5lyAjedMqldMmVmIyashF12hUj4BTAl8ezelETCQSoTHHcBGQgjxEaX4UwfAO5sQ4WUw5mM8tcB6FNCTOIyhO9B6o0RCgOS105IszEn1gYb5A7WCqke10FrRri3qVcwc8+X02KRw9ICf1hbAHDgFp+tmsWume2Uz6zYR9tfwawEJxxv299qSAOzIh0fGwHf2YixH5GwS108VfnH+2pvaM+BhZbYJR2cao28nZo7U462mCyeuD5YIyHQNIAlJtwtNef6lYRCPUybFtsfcIXRwbaurX4AAAAASUVORK5CYII="},"75fc":function(t,e,i){"use strict";var s=i("a745"),n=i.n(s);function a(t){if(n()(t)){for(var e=0,i=new Array(t.length);e<t.length;e++)i[e]=t[e];return i}}var r=i("774e"),o=i.n(r),c=i("c8bb"),l=i.n(c);function d(t){if(l()(Object(t))||"[object Arguments]"===Object.prototype.toString.call(t))return o()(t)}function u(){throw new TypeError("Invalid attempt to spread non-iterable instance")}function f(t){return a(t)||d(t)||u()}i.d(e,"a",function(){return f})},"774e":function(t,e,i){t.exports=i("d2d5")},"7ec0":function(t,e,i){},9003:function(t,e,i){t.exports=i("0b93")(176)},"901a":function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAAABHNCSVQICAgIfAhkiAAAAXFJREFUOE/Fk0FOwlAURe9rbR2KKxB34BLq1IRYBsShdcAYWIEuwaGBCcwEBlZInKo7wBVQdlCGFvqv+YXCR9DEhMSOmrz3z7v3/vcFe/pkTxz8L8j3g0IYtmPTzU5FF5XqmW2hBqAoEG95IJorlvX/geCVRGPYb7Vz2AZIT0oPnSfj8GooFW9SYKQhECkQfB52W/4WSKvIm/IigYYtVhg+PkRbdTKeE+cv/dZI92eKtBLlOmM9yYRQVNakUom/D8n6yNhKZqc6rwxUuqqGArk0w7M+k+PUdb2UjFYQogPBtdlHIhz2muUfQYNuU0w7BCe22J6iGm+AllkZ1twIgqNM8XawEwoDS1keBLfrG8DUSpLiypouLKbLG8m6eTsgOlaS1JXr1iC4W2fISargb4SdF3XoieMUdwZr+CH5bicz31zKrT1S7sIigQ+ABYGcgJgSHAkkmpP3uYpfN7tUqQYi4g16zeAvD/p/H+0upXtT9AV107QTb0CmQQAAAABJRU5ErkJggg=="},"95d5":function(t,e,i){var s=i("40c3"),n=i("5168")("iterator"),a=i("481b");t.exports=i("584a").isIterable=function(t){var e=Object(t);return void 0!==e[n]||"@@iterator"in e||a.hasOwnProperty(s(e))}},"98a8":function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAEKADAAQAAAABAAAAEAAAAAAXnVPIAAABTUlEQVQ4EZVT21XCQBCdWSgEKxArMJ/ij8cCBK0ArESoQKUCvoifsYOkAqUECyDjvTG7LmHjkTlnMs87mZndVenQ1bONdCBzNclEZdyETUpTKWwvq7cH/YwhGhuTV1uqyjz2dXUTWeZTffT+UOB6bRs4b3zgT4mOtjO9YI7jh3+G6IJ39V7OyIjtmBcIo7UYUc7sBvIRgq2CVt/RakZzsrYCrV62oSBY3KmTRfCcqBDrsLQshcMpnEf+n9OIHFTZlWJ56DZN7fySGtEjhl5JSR2Ge5AKNz4WqMBxu7/JtTw1BnrtocphgKInKNjPiNwXJ9ZZLbwDR4TFfOH63jYM/SgBDmId7zYqrRIJZX6nGzJiZTdODLFhOpwGkw52UZvcE+hUXigjqrZTHdMOBWj86zHhz/lMF8wnHRSgo3nOuGFYXgbTd1RxYZy5+5y/AQmkdWsANTevAAAAAElFTkSuQmCC"},a14e:function(t,e,i){"use strict";var s=i("7ec0"),n=i.n(s);n.a},a745:function(t,e,i){t.exports=i("f410")},a8b2:function(t,e,i){"use strict";var s=i("0a19"),n=i.n(s);n.a},aa77:function(t,e,i){var s=i("5ca1"),n=i("be13"),a=i("79e5"),r=i("fdef"),o="["+r+"]",c="​",l=RegExp("^"+o+o+"*"),d=RegExp(o+o+"*$"),u=function(t,e,i){var n={},o=a(function(){return!!r[t]()||c[t]()!=c}),l=n[t]=o?e(f):r[t];i&&(n[i]=l),s(s.P+s.F*o,"String",n)},f=u.trim=function(t,e){return t=String(n(t)),1&e&&(t=t.replace(l,"")),2&e&&(t=t.replace(d,"")),t};t.exports=u},ab54:function(t,e,i){"use strict";i.r(e);var s=function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"site-add"},[s("div",{staticClass:"add-info"},[s("div",{staticClass:"title"},["siteadd"===t.type?s("span",[t._v(t._s(t.$t("m.siteAdd.addSite")))]):t._e(),"siteinfo"===t.type?s("span",[t._v(t._s(t.$t("m.siteAdd.waitingActivation")))]):t._e(),"siteUpdate"===t.type?s("span",[t._v(t._s(t.$t("m.siteAdd.siteUpdate")))]):t._e()]),s("el-form",{ref:"infoform",attrs:{model:t.form,"label-position":"left",rules:t.rules,"label-width":"250px"}},[s("div",{staticClass:"basic-info"},[t._v(t._s(t.$t("m.siteAdd.Basic Info")))]),s("el-form-item",{attrs:{label:t.$t("m.common.siteName"),prop:"siteName"}},["siteinfo"===t.type?s("span",{staticClass:"info-text require"},[t._v(t._s(t.form.siteName))]):s("el-input",{attrs:{placeholder:t.$t("m.siteAdd.maximum20chatacters")},on:{blur:t.toCheckSiteName,focus:function(e){return t.cancelValid("siteName")}},model:{value:t.form.siteName,callback:function(e){t.$set(t.form,"siteName",e)},expression:"form.siteName"}})],1),s("el-form-item",{attrs:{label:t.$t("m.common.siteInstitution"),prop:"institutions"}},["siteinfo"===t.type?s("span",{staticClass:"info-text"},[t._v(t._s(t.form.institutions))]):s("el-select",{attrs:{"popper-append-to-body":!1,filterable:"",placeholder:t.$t("m.siteAdd.chooseInstitutions")},on:{focus:function(e){return t.cancelValid("institutions")}},model:{value:t.form.institutions,callback:function(e){t.$set(t.form,"institutions",e)},expression:"form.institutions"}},t._l(t.institutionsdownList,function(t){return s("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1),"siteadd"===t.type||"siteUpdate"===t.type?s("span",{staticClass:"add-institutions",on:{click:t.toAddInstitutions}},[t._v(t._s(t.$t("m.common.add")))]):t._e()],1),s("el-form-item",{attrs:{label:t.$t("m.common.role"),prop:"role"}},["siteinfo"===t.type?s("span",{staticClass:"info-text"},[t._v(t._s(1===t.form.role?t.$t("m.common.guest"):t.$t("m.common.host")))]):s("el-select",{attrs:{"popper-append-to-body":!1,placeholder:t.$t("m.siteAdd.chooseRole")},on:{focus:function(e){return t.cancelValid("role")},change:t.getPartyid},model:{value:t.form.role,callback:function(e){t.$set(t.form,"role",e)},expression:"form.role"}},t._l(t.roleOp,function(t){return s("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1),s("el-form-item",{attrs:{label:t.$t("m.common.partyID")}},[s("span",{attrs:{slot:"label"},slot:"label"},[s("span",[s("span",[t._v(t._s(t.$t("m.common.partyID")))]),s("i",{staticClass:"el-icon-s-tools tools",staticStyle:{"margin-left":"15px",cursor:"pointer"},on:{click:t.toPartyid}})])]),"siteinfo"===t.type?s("span",{staticClass:"info-text"},[t._v(t._s(t.form.partyId))]):s("el-select",{attrs:{disabled:0===t.partyidname.length,filterable:"","popper-append-to-body":!1,placeholder:t.$t("m.siteAdd.chooseGroup")},on:{change:t.selectPartyid},model:{value:t.partyidSelect,callback:function(e){t.partyidSelect=e},expression:"partyidSelect"}},t._l(t.partyidname,function(t){return s("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1),s("el-form-item",{directives:[{name:"show",rawName:"v-show",value:("siteadd"===t.type||"siteUpdate"===t.type)&&t.partyidSelect,expression:"(type==='siteadd' || type==='siteUpdate') && partyidSelect"}]},[s("div",{staticClass:"dropdown"},[s("i",{staticClass:"el-icon-arrow-down down",on:{click:function(e){t.downshow=!t.downshow}}}),t.downshow?s("el-input",{attrs:{disabled:"",autosize:"",type:"textarea"},model:{value:t.groupRange,callback:function(e){t.groupRange=e},expression:"groupRange"}}):t._e(),t.downshow?t._e():s("div",{staticClass:"down-text"},[t._v(t._s(t.groupRange))])],1)]),"siteadd"===t.type||"siteUpdate"===t.type?s("el-form-item",{attrs:{label:"",prop:"partyId"}},[s("el-input",{attrs:{disabled:!t.partyidSelect,placeholder:t.$t("m.siteAdd.typePartyID")},on:{focus:function(e){return t.cancelValid("partyId")},blur:t.tocheckPartyid},model:{value:t.form.partyId,callback:function(e){t.$set(t.form,"partyId","string"===typeof e?e.trim():e)},expression:"form.partyId"}})],1):t._e(),s("el-form-item",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}],attrs:{prop:"exits"}},["siteUpdate"===t.type||"siteadd"===t.type?s("el-input",{model:{value:t.form.exits,callback:function(e){t.$set(t.form,"exits",e)},expression:"form.exits"}}):t._e()],1),"siteinfo"===t.type?s("el-form-item",{attrs:{label:"Federation Key"}},[t.keyViewDefault?s("span",{staticClass:"info-text"},[t._v(t._s(t.form.secretInfo.key)+" "),s("img",{staticClass:"view",attrs:{src:i("901a")},on:{click:function(e){t.keyViewDefault=!t.keyViewDefault}}})]):t._e(),t.keyViewDefault?t._e():s("span",{staticClass:"info-text"},[t._v("***********************"),s("img",{staticClass:"view",attrs:{src:i("c990")},on:{click:function(e){t.keyViewDefault=!t.keyViewDefault}}})])]):t._e(),"siteinfo"===t.type?s("el-form-item",{attrs:{label:"Federation Secret"}},[t.sansViewDefault?s("span",{staticClass:"info-text"},[t._v(t._s(t.form.secretInfo.secret)+" "),s("img",{staticClass:"view",attrs:{src:i("901a")},on:{click:function(e){t.sansViewDefault=!t.sansViewDefault}}})]):t._e(),t.sansViewDefault?t._e():s("span",{staticClass:"info-text"},[t._v("***********************"),s("img",{staticClass:"view",attrs:{src:i("c990")},on:{click:function(e){t.sansViewDefault=!t.sansViewDefault}}})])]):t._e(),s("span",{staticClass:"registration-box"},[s("el-form-item",{attrs:{label:"",prop:""}},[s("span",{attrs:{slot:"label"},slot:"label"},[s("span",{staticStyle:{color:"#4E5766"}},[t._v(t._s(t.$t("m.siteAdd.registrationLinkSetting")))])])]),s("el-form-item",{attrs:{label:t.$t("m.siteAdd.linkType"),prop:"protocol"}},["siteinfo"===t.type&&t.form.protocol?s("span",{staticClass:"info-text"},[t._v(t._s("https://"===t.form.protocol?"HTTPS":"HTTP"))]):s("el-radio-group",{model:{value:t.form.protocol,callback:function(e){t.$set(t.form,"protocol",e)},expression:"form.protocol"}},[s("el-radio",{attrs:{label:"https://"}},[t._v("HTTPS")]),s("el-radio",{attrs:{label:"http://"}},[t._v("HTTP")])],1)],1),s("el-form-item",{attrs:{label:t.$t("m.siteAdd.encryption"),prop:"encryptType"}},["siteinfo"===t.type&&t.form.encryptType?s("span",{staticClass:"info-text"},[t._v(t._s(1===t.form.encryptType?t.$t("m.siteAdd.encryptionType"):t.$t("m.siteAdd.unencrypted")))]):s("span",[s("el-switch",{attrs:{width:35},model:{value:t.form.encryptType,callback:function(e){t.$set(t.form,"encryptType",e)},expression:"form.encryptType"}})],1)]),s("el-form-item",{attrs:{label:t.$t("m.siteAdd.proxyNetworkAccess"),prop:"network"}},["siteinfo"===t.type?s("span",{staticClass:"info-text"},[t._v(t._s(t.form.network))]):s("el-input",{model:{value:t.form.network,callback:function(e){t.$set(t.form,"network",e)},expression:"form.network"}}),"siteadd"===t.type||"siteUpdate"===t.type?s("span",{staticClass:"add-institutions",on:{click:t.toResetNetwork}},[t._v(t._s(t.$t("m.siteAdd.resetDefault")))]):t._e()],1),"siteinfo"===t.type?s("el-form-item",{attrs:{label:t.$t("m.siteAdd.registrationLink")}},[s("el-popover",{attrs:{placement:"top",width:"300",trigger:"hover",content:t.form.registrationLink}},[s("span",{staticClass:"link-text",staticStyle:{color:"#217AD9"},attrs:{slot:"reference"},slot:"reference"},[t._v(t._s(t.form.registrationLink))])]),s("span",{staticClass:"copy formcopy",attrs:{"data-clipboard-text":t.form.registrationLink},on:{click:function(e){return t.toCopy("from")}}},[t._v(t._s(t.$t("m.common.copy")))])],1):t._e()],1),s("div",{staticClass:"basic-info"},[t._v(t._s(t.$t("m.siteAdd.Exchange Info")))]),s("el-form-item",{attrs:{label:"Exchange",prop:"exchangeId"}},["siteinfo"===t.type?s("span",{staticClass:"info-text"},[t._v(t._s(t.form.exchangeName))]):s("el-select",{attrs:{filterable:"",placeholder:t.$t("m.siteAdd.exchange")},on:{focus:function(e){return t.cancelValid("exchangeId")}},model:{value:t.form.exchangeId,callback:function(e){t.$set(t.form,"exchangeId",e)},expression:"form.exchangeId"}},t._l(t.exchangeList,function(t){return s("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1)],1),s("div",{staticClass:"Submit"},["siteadd"===t.type?s("el-button",{attrs:{type:"primary"},on:{click:t.submitAction}},[t._v(t._s(t.$t("m.common.submit")))]):t._e(),"siteUpdate"===t.type?s("el-button",{attrs:{type:"primary"},on:{click:t.submitAction}},[t._v(t._s(t.$t("m.common.resubmit")))]):t._e(),"siteinfo"===t.type?s("el-button",{attrs:{type:"primary"},on:{click:t.modifyAction}},[t._v(t._s(t.$t("m.common.modify")))]):t._e()],1)],1),s("el-dialog",{staticClass:"ok-dialog",attrs:{visible:t.okdialog,"close-on-click-modal":!1,"show-close":!0,"close-on-press-escape":!1},on:{"update:visible":function(e){t.okdialog=e}}},[s("div",{staticClass:"icon"},[s("i",{staticClass:"el-icon-success"})]),"siteadd"===t.type?s("div",{staticClass:"line-text-one"},[t._v(t._s(t.$t("m.siteAdd.addSuccessfully")))]):t._e(),"siteUpdate"===t.type?s("div",{staticClass:"line-text-one"},[t._v(t._s(t.$t("m.siteAdd.modifySuccessfully")))]):t._e(),s("div",{staticClass:"line-text-two"},[t._v(t._s(t.$t("m.siteAdd.registrationLinkGenerated")))]),s("div",{staticClass:"line-text-three"},[s("el-popover",{attrs:{placement:"top",width:"660",trigger:"hover",offset:"-30",content:t.form.registrationLink}},[s("span",{staticClass:"copy-link",attrs:{slot:"reference"},slot:"reference"},[t._v(t._s(t.form.registrationLink))])]),s("span",{staticClass:"copy dialogcopy",attrs:{"data-clipboard-text":t.form.registrationLink},on:{click:function(e){return t.toCopy("tooltip")}}},[t._v(t._s(t.$t("m.common.copy")))])],1),s("div",{staticClass:"dialog-footer"},[s("el-button",{staticClass:"ok-btn",attrs:{type:"primary"},on:{click:t.okAction}},[t._v(t._s(t.$t("m.common.OK")))])],1)]),s("el-dialog",{staticClass:"site-toleave-dialog",attrs:{visible:t.isleavedialog,width:"500px"},on:{"update:visible":function(e){t.isleavedialog=e}}},[s("div",{staticClass:"line-text-one"},[t._v(t._s(t.$t("m.siteAdd.sureLeavePage")))]),s("div",{staticClass:"line-text-two"},[t._v(t._s(t.$t("m.siteAdd.notSavedTips")))]),s("div",{staticClass:"dialog-footer"},[s("el-button",{staticClass:"ok-btn",attrs:{type:"primary"},on:{click:t.sureLeave}},[t._v(t._s(t.$t("m.common.sure")))]),s("el-button",{staticClass:"ok-btn",attrs:{type:"info"},on:{click:t.cancelLeave}},[t._v(t._s(t.$t("m.common.cancel")))])],1)]),s("siteaddip",{ref:"siteaddip"})],1)},n=[],a=(i("a481"),i("cebc")),r=(i("96cf"),i("3b8d")),o=(i("7f7f"),i("28a5"),i("ac6a"),i("3b2b"),i("c6a8")),c=i("34c7"),l=i("b311"),d=i.n(l),u=function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",[s("el-dialog",{staticClass:"add-dialog",attrs:{visible:t.adddialog,"close-on-click-modal":!1,"close-on-press-escape":!1,width:"520px"},on:{"update:visible":function(e){t.adddialog=e}}},[s("div",{staticClass:"add-dialog-head"},[s("el-button",{staticClass:"addUrl",attrs:{disabled:t.addDisabled,type:"text"},on:{click:t.addentrancesSelect}},[s("i",{staticClass:"el-icon-circle-plus"}),s("span",[t._v(t._s(t.$t("m.common.add")))])]),s("span",{staticClass:"total"},[t._v(t._s(t.$t("m.common.total"))+":"+t._s(t.addtotal))])],1),s("div",{staticClass:"add-dialog-body"},[s("div",{staticClass:"line-box"},t._l(t.entrancesSelect,function(e,i){return s("div",{key:i,staticClass:"line",on:{dblclick:function(e){return t.getEdit(i)}}},[e.show?t._e():s("span",[s("span",{staticClass:"network-text"},[t._v(t._s(e.ip))]),"entrances"===t.networkacesstype?s("span",{staticClass:"telnet",on:{click:function(e){return t.testTelnet(i)}}},[t._v(t._s(t.$t("m.siteAdd.telnet")))]):t._e(),s("i",{staticClass:"el-icon-close del",on:{click:function(e){return t.deleteEntrances(i)}}})]),e.show?s("el-input",{staticClass:"input-show",attrs:{autocomplete:"off",id:"close",placeholder:t.$t("m.siteAdd.typeLike")+": 127.0.0.1:8080"},on:{blur:function(e){return t.closeEntrances(i)}},model:{value:t.entrancesInput,callback:function(e){t.entrancesInput=e},expression:"entrancesInput"}},[s("i",{staticClass:"el-icon-check check",attrs:{slot:"suffix"},on:{blur:function(e){return t.closeEntrances(i)}},slot:"suffix"}),s("i",{staticClass:"el-icon-close del",staticStyle:{right:"2px"},attrs:{slot:"suffix"},on:{click:function(e){return t.deleteEntrances(i)},mousedown:t.mouseDown},slot:"suffix"})]):t._e()],1)}),0),s("div",{staticClass:"message-add",style:{opacity:t.showMes,transition:"all 0.2s ease"}},[t.telnetsuccess?s("span",{staticClass:"tips"},[s("img",{attrs:{src:i("65823")}}),s("span",[t._v(t._s(t.$t("m.siteAdd.telnetSuccess"))+"!\n                        "),s("span",{staticStyle:{color:"#4AA2FF"}},[t._v(t._s(t.ipPost))])])]):t._e(),t.invalidsuccess?s("span",{staticClass:"tips"},[s("img",{attrs:{src:i("98a8")}}),s("span",[t._v(t._s(t.$t("m.siteAdd.invalidInput"))+"!")])]):t._e(),t.telnetfail?s("span",{staticClass:"tips"},[s("img",{attrs:{src:i("98a8")}}),s("span",[t._v(t._s(t.$t("m.siteAdd.unableConnect"))+"!\n                        "),s("span",{staticStyle:{color:"#4AA2FF"}},[t._v(t._s(t.ipPost))])])]):t._e()])]),s("div",{staticClass:"add-dialog-footer"},[s("el-button",{staticClass:"save-btn",attrs:{disabled:t.saveDisabled,type:"primary"},on:{click:t.saveAction}},[t._v(t._s(t.$t("m.common.save")))]),s("el-button",{staticClass:"cancel-btn",attrs:{type:"info"},on:{click:t.cancelAction}},[t._v(t._s(t.$t("m.common.cancel")))])],1)])],1)},f=[],p=(i("5df3"),i("4f7f"),i("75fc")),m=i("bb14"),h={name:"siteAddIp",components:{},data:function(){return{adddialog:!1,entrancesInput:"",networkacesstype:"",showMes:0,timeLess:null,entrancesSelect:[],addtotal:"",telnetsuccess:!1,telnetfail:!1,invalidsuccess:!1,saveDisabled:!1,addDisabled:!1,canEdit:!0,ipPost:"",verifyPromise:null}},watch:{entrancesSelect:{handler:function(t){this.addtotal=t.length,t.length>=20?this.addDisabled=!0:this.saveDisabled||(this.addDisabled=!1),t.length>=20?this.saveDisabled=!0:this.saveDisabled=!1},deep:!0,immediate:!0}},created:function(){},methods:{addentrancesSelect:function(){this.entrancesSelect.push({ip:"",show:!1,checked:!1}),this.canEdit=!0,this.getEdit(this.entrancesSelect.length-1)},deleteEntrances:function(t){this.entrancesInput="",this.entrancesSelect.splice(t,1),this.saveDisabled=!1,this.addDisabled=!1,this.canEdit=!0},deleteAll:function(){this.entrancesSelect=this.entrancesSelect.filter(function(t){return!t.checked})},getEdit:function(t){this.canEdit&&(this.entrancesSelect[t].show=!0,this.entrancesInput=this.entrancesSelect[t].ip,this.entrancesindex=t,this.canEdit=!1,this.addDisabled=!0,this.saveDisabled=!0,this.$nextTick(function(){var t=document.querySelector("#close");t.focus()}))},closeEntrances:function(t){var e=this,i=this.entrancesInput;""===this.entrancesInput?this.deleteEntrances(t):Object(m["a"])(i)?(this.entrancesSelect[this.entrancesindex].show=!1,this.entrancesSelect[this.entrancesindex].ip=this.entrancesInput,this.saveDisabled=!1,this.addDisabled=!1,this.canEdit=!0):(this.saveDisabled=!0,this.addDisabled=!0,this.invalidsuccess=!0,this.showMes=.8,setTimeout(function(){e.showMes=0,e.invalidsuccess=!1},2e3))},saveAction:function(){var t=Object(p["a"])(new Set(this.entrancesSelect.map(function(t){return"".concat(t.ip,";")})));"entrances"===this.networkacesstype?(this.$parent.form.networkAccessEntrances=t.join(""),this.$parent.$refs["infoform"].validateField("networkAccessEntrances",function(t){}),this.entrancesSelect=[]):"exit"===this.networkacesstype&&(this.$parent.form.networkAccessExits=t.join(""),this.$parent.$refs["infoform"].validateField("networkAccessExits",function(t){}),this.entrancesSelect=[]),this.adddialog=!1},mouseDown:function(t){t.preventDefault()},cancelAction:function(){this.showMes=0,this.entrancesSelect=[],this.saveDisabled=!1,this.addDisabled=!1,this.adddialog=!1},testTelnet:function(t){var e=this,i={ip:this.entrancesSelect[t].ip.split(":")[0],port:parseInt(this.entrancesSelect[t].ip.split(":")[1])};this.ipPost=this.entrancesSelect[t].ip,Object(o["O"])(i).then(function(t){0===t.code&&(e.telnetsuccess=!0,e.showMes=.8,setTimeout(function(){e.telnetsuccess=!1,e.showMes=0},2e3))}).catch(function(t){109===t.code&&(e.telnetfail=!0,e.showMes=.8,setTimeout(function(){e.telnetfail=!1,e.showMes=0},2e3))})}}},v=h,A=(i("a8b2"),i("2877")),g=Object(A["a"])(v,u,f,!1,null,null,null),y=g.exports,b={name:"siteadd",components:{siteaddip:y},data:function(){var t=this;return{type:this.$route.query.type,id:"",isleave:!1,isleavedialog:!1,leaveRouteName:"",sansViewDefault:!1,keyViewDefault:!1,partyidname:[],partyidSelect:"",partyIdPostPass:!0,groupRange:"",okdialog:!1,downshow:!1,siteNameExists:!1,institutionsdownList:[],exchangeList:[],form:{groupId:"",siteName:"",institutions:"",role:"",partyId:"",network:"",secretInfo:{key:"",secret:""},exits:"",protocol:"https://",encryptType:!0,registrationLink:"",mode:"short"},roleOp:[{value:1,label:this.$t("m.common.guest")},{value:2,label:this.$t("m.common.host")}],rules:{siteName:[{required:!0,trigger:"change",validator:function(e,i,s){var n=i.trim();n?n.length>20?s(new Error(t.$t("m.siteAdd.maxCharacters"))):t.siteNameExists?s(new Error(t.$t("m.siteAdd.alreadyExists"))):s():s(new Error(t.$t("m.siteAdd.siteNameRequired")))}}],institutions:[{required:!0,message:this.$t("m.siteAdd.institutionRrequired"),trigger:"bulr"}],role:[{required:!0,message:this.$t("m.siteAdd.roleRrequired"),trigger:"bulr"}],partyId:[{required:!0,trigger:"blur",validator:function(e,i,s){var n=new RegExp(/^-?[1-9]\d*$/),a=!0;t.groupRange.split(";").forEach(function(t){var e=t.split(/~|-/).map(function(t){return parseInt(t)}),s=parseInt(i);e&&e.length>1?e[0]<=s&&e[1]>=s&&(a=!1):e&&1===e.length&&e[0]===s&&(a=!1)}),i?a||!n.test(i)?s(new Error(t.$t("m.siteAdd.invalidPartyID"))):t.partyIdPostPass?s():s(new Error(t.$t("m.siteAdd.partyIDUsed"))):s(new Error(t.$t("m.siteAdd.partyIDRequired")))}}],network:[{required:!0,trigger:"change",validator:function(e,i,s){i?Object(m["a"])(i)?s():s(new Error(t.$t("m.siteAdd.proxyNetworkAccessInvalid"))):s(new Error(t.$t("m.siteAdd.proxyNetworkAccessRequired")))}}],exchangeId:[{required:!0,message:this.$t("m.siteAdd.ExchangRequired"),trigger:"bulr"}]}}},watch:{},computed:{},created:function(){var t=this;"siteinfo"===this.type&&(this.isleave=!0),this.$route.query.id&&this.getKeySansLink(),Object(o["a"])().then(function(e){e.data.forEach(function(e,i){var s={};s.value=e,s.label=e,t.institutionsdownList.push(s)})}),this.toResetNetwork(),this.getExchangeList()},beforeDestroy:function(){},mounted:function(){var t=this;this.$router.beforeEach(function(e,i,s){t.leaveRouteName=e.name,t.isleave?s():(s(!1),t.isleavedialog=!0)})},methods:{getExchangeList:function(){var t=this;Object(o["n"])().then(function(e){e.data.forEach(function(e){var i={};i.value=e.exchangeId,i.label=e.exchangeName,t.exchangeList.push(i)})})},toCheckSiteName:function(){var t=this;if(this.form.siteName){var e={siteName:this.form.siteName.trim(),id:this.$route.query.id};Object(o["i"])(e).then(function(e){0===e.code&&(t.siteNameExists=!1,t.$refs["infoform"].validateField("siteName"))}).catch(function(e){122===e.code&&(t.siteNameExists=!0,t.$refs["infoform"].validateField("siteName"))})}},tocheckPartyid:function(){var t=this;this.$refs["infoform"].validateField("partyId",function(e){if(e!==t.$t("m.siteAdd.partyIDRequired")&&e!==t.$t("m.siteAdd.invalidPartyID")){var i={id:t.$route.query.id,partyId:t.form.partyId,rangeInfo:Object(c["a"])(t.groupRange)};Object(o["g"])(i).then(function(e){0===e.code&&(t.partyIdPostPass=!0,t.$refs["infoform"].validateField("partyId",function(t){}))}).catch(function(e){103===e.code&&(t.partyIdPostPass=!1,t.$refs["infoform"].validateField("partyId",function(t){}))})}})},submitAction:function(){var t=this;this.form.siteName=this.form.siteName.trim(),"number"!==typeof this.form.encryptType&&this.$set(this.form,"encryptType",this.getStatus(this.form.encryptType)),"siteadd"===this.type?this.$refs["infoform"].validate(function(){var e=Object(r["a"])(regeneratorRuntime.mark(function e(i){var s,n;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:if(!i){e.next=6;break}return s=Object(a["a"])({},t.form),e.next=4,Object(o["J"])(s);case 4:n=e.sent,0===n.code&&(t.isleave=!0,t.id=n.data,t.okdialog=!0,t.getKeySansLink());case 6:case"end":return e.stop()}},e)}));return function(t){return e.apply(this,arguments)}}()):"siteUpdate"===this.type&&this.$refs["infoform"].validate(function(){var e=Object(r["a"])(regeneratorRuntime.mark(function e(i){var s,n;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:if(!i){e.next=6;break}return s=Object(a["a"])({},t.form),e.next=4,Object(o["N"])(s);case 4:n=e.sent,0===n.code&&(t.isleave=!0,t.id=t.$route.query.id,t.okdialog=!0,t.getKeySansLink());case 6:case"end":return e.stop()}},e)}));return function(t){return e.apply(this,arguments)}}())},modifyAction:function(){this.type="siteUpdate",this.isleave=!1},okAction:function(){this.okdialog=!1,this.$router.push({name:"Site Manage"})},sureLeave:function(){this.isleave=!0,this.isleavedialog=!1;var t="Admin Access"===this.leaveRouteName?{type:"FATEManager"}:"";this.$router.push({name:this.leaveRouteName,query:t})},cancelLeave:function(){this.$store.dispatch("SetMune","Site Manage"),this.isleavedialog=!1},toPartyid:function(){this.$router.push({name:"Party ID"})},getPartyid:function(){var t=Object(r["a"])(regeneratorRuntime.mark(function t(e){var i,s,n=this;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:return i={pageNum:1,pageSize:100,role:e},this.partyidname=[],"siteadd"!==this.type&&"siteUpdate"!==this.type||(this.partyidSelect="",this.form.partyId=""),t.next=5,Object(o["q"])(i);case 5:s=t.sent,s.data.list.forEach(function(t){var e={};e.value=t.groupName,e.label=t.groupName,e.rangeInfo=Object(c["b"])(t.federatedGroupDetailDos),e.groupId=t.groupId,n.partyidname.push(e)});case 7:case"end":return t.stop()}},t,this)}));function e(e){return t.apply(this,arguments)}return e}(),selectPartyid:function(t){var e=this;this.partyidname.forEach(function(i){t===i.value&&(e.groupRange=i.rangeInfo,e.form.groupId=i.groupId)})},getKeySansLink:function(){var t=Object(r["a"])(regeneratorRuntime.mark(function t(){var e,i,s,n;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:return e={id:this.id?parseInt(this.id):parseInt(this.$route.query.id)},t.next=3,Object(o["s"])(e);case 3:return i=t.sent,s=i.data.registrationLink,s=s.indexOf("?st")<0?JSON.stringify(s).replace(new RegExp('"',"g"),""):JSON.stringify(s).replace(new RegExp("\\\\","g"),""),"siteinfo"===this.type?(n=Object(a["a"])({},i.data),i.data.protocol||i.data.encryptType||(n.protocol="https://",n.encryptType=!0),this.form=n,this.form.mode="short"):i.data.registrationLink.indexOf("?st")<0?this.form.registrationLink=s:this.form.registrationLink=i.data.registrationLink,this.partyidSelect=i.data.groupName,t.next=10,this.getPartyid(i.data.role);case 10:return t.next=12,this.selectPartyid(i.data.groupName);case 12:case"end":return t.stop()}},t,this)}));function e(){return t.apply(this,arguments)}return e}(),toCopy:function(t){var e=this;if("tooltip"===t){var i=new d.a(".dialogcopy");i.on("success",function(t){e.$message.success(e.$t("m.common.copySuccess")),i.destroy()})}if("from"===t){var s=new d.a(".formcopy");s.on("success",function(t){e.$message.success(e.$t("m.common.copySuccess")),s.destroy()})}},cancelValid:function(t){this.$refs["infoform"].clearValidate(t),this["".concat(t,"warnshow")]=!1},toAddInstitutions:function(){this.$router.push({name:"Admin Access",query:{type:"FATE Manager"}})},toResetNetwork:function(){var t=this;Object(o["G"])().then(function(e){t.form.network=e.data.network})},getStatus:function(t){return"number"===typeof t?1===t:!0===t?1:2}}},w=b,_=(i("a14e"),Object(A["a"])(w,s,n,!1,null,null,null));e["default"]=_.exports},aebd:function(t,e,i){t.exports=i("0b93")(26)},b39a:function(t,e,i){var s=i("d3f4");t.exports=function(t,e){if(!s(t)||t._t!==e)throw TypeError("Incompatible receiver, "+e+" required!");return t}},bb14:function(t,e,i){"use strict";i.d(e,"a",function(){return s});i("3b2b");function s(t){return t&&t.indexOf(";")>-1?new RegExp(/^([0-9a-zA-Z.*-]+:[0-9]{1,5};)+$/).test(t):new RegExp(/^[0-9a-zA-Z.*-]+:[0-9]{1,5}$/).test(t)}},c26b:function(t,e,i){"use strict";var s=i("86cc").f,n=i("2aeb"),a=i("dcbc"),r=i("9b43"),o=i("f605"),c=i("4a59"),l=i("01f9"),d=i("d53b"),u=i("7a56"),f=i("9e1e"),p=i("67ab").fastKey,m=i("b39a"),h=f?"_s":"size",v=function(t,e){var i,s=p(e);if("F"!==s)return t._i[s];for(i=t._f;i;i=i.n)if(i.k==e)return i};t.exports={getConstructor:function(t,e,i,l){var d=t(function(t,s){o(t,d,e,"_i"),t._t=e,t._i=n(null),t._f=void 0,t._l=void 0,t[h]=0,void 0!=s&&c(s,i,t[l],t)});return a(d.prototype,{clear:function(){for(var t=m(this,e),i=t._i,s=t._f;s;s=s.n)s.r=!0,s.p&&(s.p=s.p.n=void 0),delete i[s.i];t._f=t._l=void 0,t[h]=0},delete:function(t){var i=m(this,e),s=v(i,t);if(s){var n=s.n,a=s.p;delete i._i[s.i],s.r=!0,a&&(a.n=n),n&&(n.p=a),i._f==s&&(i._f=n),i._l==s&&(i._l=a),i[h]--}return!!s},forEach:function(t){m(this,e);var i,s=r(t,arguments.length>1?arguments[1]:void 0,3);while(i=i?i.n:this._f){s(i.v,i.k,this);while(i&&i.r)i=i.p}},has:function(t){return!!v(m(this,e),t)}}),f&&s(d.prototype,"size",{get:function(){return m(this,e)[h]}}),d},def:function(t,e,i){var s,n,a=v(t,e);return a?a.v=i:(t._l=a={i:n=p(e,!0),k:e,v:i,p:s=t._l,n:void 0,r:!1},t._f||(t._f=a),s&&(s.n=a),t[h]++,"F"!==n&&(t._i[n]=a)),t},getEntry:v,setStrong:function(t,e,i){l(t,e,function(t,i){this._t=m(t,e),this._k=i,this._l=void 0},function(){var t=this,e=t._k,i=t._l;while(i&&i.r)i=i.p;return t._t&&(t._l=i=i?i.n:t._t._f)?d(0,"keys"==e?i.k:"values"==e?i.v:[i.k,i.v]):(t._t=void 0,d(1))},i?"entries":"values",!i,!0),u(e)}}},c5f6:function(t,e,i){"use strict";var s=i("7726"),n=i("69a8"),a=i("2d95"),r=i("5dbc"),o=i("6a99"),c=i("79e5"),l=i("9093").f,d=i("11e9").f,u=i("86cc").f,f=i("aa77").trim,p="Number",m=s[p],h=m,v=m.prototype,A=a(i("2aeb")(v))==p,g="trim"in String.prototype,y=function(t){var e=o(t,!1);if("string"==typeof e&&e.length>2){e=g?e.trim():f(e,3);var i,s,n,a=e.charCodeAt(0);if(43===a||45===a){if(i=e.charCodeAt(2),88===i||120===i)return NaN}else if(48===a){switch(e.charCodeAt(1)){case 66:case 98:s=2,n=49;break;case 79:case 111:s=8,n=55;break;default:return+e}for(var r,c=e.slice(2),l=0,d=c.length;l<d;l++)if(r=c.charCodeAt(l),r<48||r>n)return NaN;return parseInt(c,s)}}return+e};if(!m(" 0o1")||!m("0b1")||m("+0x1")){m=function(t){var e=arguments.length<1?0:t,i=this;return i instanceof m&&(A?c(function(){v.valueOf.call(i)}):a(i)!=p)?r(new h(y(e)),i,m):y(e)};for(var b,w=i("9e1e")?l(h):"MAX_VALUE,MIN_VALUE,NaN,NEGATIVE_INFINITY,POSITIVE_INFINITY,EPSILON,isFinite,isInteger,isNaN,isSafeInteger,MAX_SAFE_INTEGER,MIN_SAFE_INTEGER,parseFloat,parseInt,isInteger".split(","),_=0;w.length>_;_++)n(h,b=w[_])&&!n(m,b)&&u(m,b,d(h,b));m.prototype=v,v.constructor=m,i("2aba")(s,p,m)}},c8bb:function(t,e,i){t.exports=i("54a1")},c990:function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAAABHNCSVQICAgIfAhkiAAAAVNJREFUOE/VkzFSwlAURe/9kVDKDoQduAOxdcYxFI6lWFCrKxBXoLZSiKVaGIcZW+MO2AFhB1gSzHvOj3wMoDijNKRL/nvn33tfHrGkh0viYEVAO/uNTc/gGECZYNXaV2gEIE4FV88Pre5sJFPWgqBeSouFR9f8U34W6g1HtTBsD1zNBGRVrBEvIEvuUIFTb5i07Xta9OsELnLw+F205tRlIKtE/EJvFqKUrlFzY2uEcmTEVEGcTWCqA5OMKlZZBto9aIQE9/JWDE0l1TQiuDH+HhuabVHp5etUEXbur2sLQSLSBbE+Drvv0avOgaBPnbtWkLPmx64pu1FxLkYiKrOMlFqft4Y3kyTliTVb+Bk2oxlY0xhzm2Ukcgii+TUI7aeCYCpsd5iN3y+EJLcWrY6qvnrJKPh2/PnGsboThZYd1DYTjN9VL3/9If+zwCuytH+x+AEzK5sT5xPjdwAAAABJRU5ErkJggg=="},d2d5:function(t,e,i){i("1654"),i("549b"),t.exports=i("584a").Array.from},e0b8:function(t,e,i){"use strict";var s=i("7726"),n=i("5ca1"),a=i("2aba"),r=i("dcbc"),o=i("67ab"),c=i("4a59"),l=i("f605"),d=i("d3f4"),u=i("79e5"),f=i("5cc5"),p=i("7f20"),m=i("5dbc");t.exports=function(t,e,i,h,v,A){var g=s[t],y=g,b=v?"set":"add",w=y&&y.prototype,_={},k=function(t){var e=w[t];a(w,t,"delete"==t?function(t){return!(A&&!d(t))&&e.call(this,0===t?0:t)}:"has"==t?function(t){return!(A&&!d(t))&&e.call(this,0===t?0:t)}:"get"==t?function(t){return A&&!d(t)?void 0:e.call(this,0===t?0:t)}:"add"==t?function(t){return e.call(this,0===t?0:t),this}:function(t,i){return e.call(this,0===t?0:t,i),this})};if("function"==typeof y&&(A||w.forEach&&!u(function(){(new y).entries().next()}))){var x=new y,I=x[b](A?{}:-0,1)!=x,E=u(function(){x.has(1)}),C=f(function(t){new y(t)}),S=!A&&u(function(){var t=new y,e=5;while(e--)t[b](e,e);return!t.has(-0)});C||(y=e(function(e,i){l(e,y,t);var s=m(new g,e,y);return void 0!=i&&c(i,v,s[b],s),s}),y.prototype=w,w.constructor=y),(E||S)&&(k("delete"),k("has"),v&&k("get")),(S||I)&&k(b),A&&w.clear&&delete w.clear}else y=h.getConstructor(e,t,v,b),r(y.prototype,i),o.NEED=!0;return p(y,t),_[t]=y,n(n.G+n.W+n.F*(y!=g),_),A||h.setStrong(y,t,v),y}},f410:function(t,e,i){i("1af6"),t.exports=i("584a").Array.isArray},fdef:function(t,e){t.exports="\t\n\v\f\r   ᠎             　\u2028\u2029\ufeff"}}]);