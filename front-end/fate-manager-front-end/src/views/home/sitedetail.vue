<template>
  <div class="site-detail-box">
    <div class="site-detail">
        <div class="Basic">
            <div class="info">
            <span>{{$t('m.sitemanage.basicInfo')}}</span>
            <div class="info-del">
                <span class="info-sta">{{$t('m.common.status')}}</span>
                <span class="sta-action">{{(form.status ? form.status : '') | getSiteStatus}}</span>
            </div>
            </div>
            <el-form ref="basicForm" :model="form" label-position="left" label-width="180px">
            <el-row :gutter="140">
                <el-col :span="12">
                    <el-form-item :label="$t('m.common.siteName')+'：'">
                        <span class="info-text">{{form.siteName}}</span>
                    </el-form-item>
                    <el-form-item :label="$t('m.common.institution',{type:'I'})+'：'">
                        <span class="info-text">{{form.institutions}}</span>
                    </el-form-item>
                    <el-form-item  :label="$t('m.common.role')+'：'" >
                        <span class="info-text">{{form.role | getSiteType}}</span>
                    </el-form-item>
                    <el-form-item label="Federation key：">
                        <span class="info-text">{{form.appKey}}</span>
                    </el-form-item>
                    <el-form-item label="Secret Key：">
                        <el-popover placement="top" width="400" trigger="hover" :content="form.appSecret">
                        <span slot="reference" class="link-text">{{form.appSecret}}</span>
                        </el-popover>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item :label="$t('m.common.status')+'：'" >
                        <span class="info-text">{{form.status | getSiteStatus}}</span>
                    </el-form-item>
                    <el-form-item :label="$t('m.common.partyID')+'：'" >
                        <span class="info-text">{{form.partyId}}</span>
                    </el-form-item>
                    <el-form-item  :label="$t('m.sitemanage.creationTime')+'：'">
                        <span class="info-text">{{form.createTime | dateFormat}}</span>
                    </el-form-item>
                    <el-form-item  :label="$t('m.sitemanage.activationTime')+'：'" >
                        <span class="info-text">{{form.acativationTime | dateFormat}}</span>
                    </el-form-item>
                    <el-form-item  :label="$t('m.sitemanage.registrationLink')+'：'">
                        <el-popover
                        placement="top"
                        width="400"
                        trigger="hover"
                        :content="form.registrationLink"
                        >
                        <span slot="reference" class="link-text">{{form.registrationLink}}</span>
                        </el-popover>
                    </el-form-item>
                </el-col>
            </el-row>
            </el-form>
        </div>
        <div class="Basic">
            <div class="info">
                <span>{{$t('m.sitemanage.exchangeInfo')}}</span>
            </div>
            <el-form ref="exchangeForm" :model="form" label-position="left" label-width="200px">
                <el-row :gutter="140">
                    <el-col :span="12">
                        <el-form-item :label="$t('m.sitemanage.exchangeName')+'：'">
                            <span class="info-text">{{form.ExchangeInfo.exchangeName}}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item  :label="$t('m.sitemanage.networkEntrances')+'：'" >
                            <span class="info-text">{{form.ExchangeInfo.vipEntrances}}</span>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <div class="Basic">
            <div class="info">
                <span>{{$t('m.sitemanage.networkConfiguration')}}</span>

            </div>
            <el-form ref="form" :model="form" label-position="left" :rules="rules" label-width="200px">
                <!-- site network start -->
                <div class="plate">
                    <div class="plate-title">
                        <span class="title-text">{{$t('m.sitemanage.siteNetworkConf')}}</span>
                        <span  v-if="role.roleName==='Admin' || role.roleName==='Developer or OP'">
                            <div class="info-del" v-if="form.status && form.status.code !== 3">
                                <img
                                    src="@/assets/edit_click.png"
                                    v-if="editSubmitted === 1"
                                    @click="toEdit('site')"
                                    class="edit"
                                    :class="{'disable':(rollSiteEditSubmitted === 2 || clusterEditSubmitted === 2)}"
                                    alt="" />
                                <el-button v-if="editSubmitted === 2" @click="submit('site')" :disabled="tosubmit" type="primary">{{$t('m.common.submit')}}</el-button>
                                <el-button v-if="editSubmitted === 2" @click="cancel('site')" type="info">{{$t('m.common.cancel')}}</el-button>
                                <span v-if="editSubmitted === 3" class="under">
                                    <img src="@/assets/under.png" style="margin-right:5px" alt />
                                    <span>{{$t('m.sitemanage.underReview')}}</span>
                                </span>
                            </div>
                        </span>
                    </div>
                    <el-row :gutter="140">
                        <el-col :span="12">
                            <el-form-item :label="$t('m.sitemanage.networkEntrances')+'：'" style="height:100%;" prop="networkAccessEntrances" >
                                <span class="info-text" v-if="editSubmitted!==2 && form.networkAccessEntrances">
                                    <div v-for="(item,index) in form.networkAccessEntrances.split(';')" :key="index" >{{item}}</div>
                                </span>
                                <el-input
                                    v-if="editSubmitted===2"
                                    @focus="addShow('entrances')"
                                    @blur="cancelValid('networkAccessEntrances')"
                                    :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': networkAccessEntranceswarnshow }"
                                    v-model="form.networkAccessEntrances"
                                    placeholder >
                                    <i slot="suffix" @click="addShow('entrances')" class="el-icon-edit plus" />
                                </el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <!-- site network end -->
                <!-- rollsite network start -->
                <div class="plate">
                    <el-row :gutter="140">
                        <el-col :span="12">
                            <el-form-item :label="$t('m.sitemanage.rollsiteEntrances')+'：'" style="height:100%;" prop="fmRollSiteNetworkAccess" >
                                <span class="info-text" v-if="editSubmitted!==2 && form.fmRollSiteNetworkAccess">
                                    <div v-for="(item,index) in form.fmRollSiteNetworkAccess.split(';')" :key="index" >{{item}}</div>
                                </span>
                                <el-input
                                    v-if="editSubmitted===2"
                                    @focus="addShow('rollsite')"
                                    @blur="cancelValid('fmRollSiteNetworkAccess')"
                                    :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': fmRollSiteNetworkAccesswarnshow }"
                                    v-model="form.fmRollSiteNetworkAccess"
                                    placeholder >
                                    <i slot="suffix" @click="addShow('rollsite')" class="el-icon-edit plus" />
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="$t('m.sitemanage.rollsiteExits')+'：'" style="height:100%;" >
                                 <span class="info-text" v-if="editSubmitted!==2 && form.fmRollSiteNetworkAccessExitsList">
                                    <div v-for="(item,index) in form.fmRollSiteNetworkAccessExitsList.split(';')" :key="index" >{{item}}</div>
                                </span>
                                <el-input
                                    v-if="editSubmitted===2"
                                    @focus="addShow('exit')"
                                    @blur="cancelValid('fmRollSiteNetworkAccessExitsList')"
                                    :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': fmRollSiteNetworkAccessExitsListwarnshow }"
                                    v-model="form.fmRollSiteNetworkAccessExitsList"
                                    placeholder >
                                    <i slot="suffix" @click="addShow('exit')" class="el-icon-edit plus" />
                                </el-input>
                            </el-form-item>
                            <el-form-item class="inline" :label="$t('m.siteAdd.isSecure')" prop="secureStatus" >
                                <span class="info-text" v-if="editSubmitted!==2">{{$t(`m.common.${form.secureStatus}`)}}</span>
                                <el-switch v-else v-model="form.secureStatus"></el-switch>
                            </el-form-item>
                            <el-form-item class="inline" :label="$t('m.siteAdd.isPolling')" prop="pollingStatus" >
                                <span class="info-text" v-if="editSubmitted!==2">{{$t(`m.common.${form.pollingStatus}`)}}</span>
                                <el-switch v-else v-model="form.pollingStatus"></el-switch>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <!-- rollsite network end -->
                <!-- cluster network start -->
                <div class="plate">
                    <div class="plate-title">
                        <span class="title-text">{{$t('m.sitemanage.clusterNetworkConf')}}</span>
                        <span  v-if="role.roleName==='Admin' || role.roleName==='Developer or OP'">
                            <div class="info-del" v-if="form.status && form.status.code !== 3">
                                <img
                                    src="@/assets/edit_click.png"
                                    v-if="clusterEditSubmitted === 1"
                                    @click="toEdit('cluster')"
                                    class="edit"
                                    :class="{'disable':(editSubmitted === 2 || rollSiteEditSubmitted === 2)}"
                                    alt="" />
                                <el-button v-if="clusterEditSubmitted === 2" @click="submit('cluster')" :disabled="tosubmit" type="primary">{{$t('m.common.submit')}}</el-button>
                                <el-button v-if="clusterEditSubmitted === 2" @click="cancel('cluster')" type="info">{{$t('m.common.cancel')}}</el-button>
                            </div>
                        </span>
                    </div>
                    <el-row :gutter="140">
                        <el-col :span="12">
                            <el-form-item :label="$t('m.sitemanage.fateFlowIp')+'：'" style="height:100%;" prop="fateFlowIp" >
                                <span class="info-text" v-if="clusterEditSubmitted!==2">
                                    <div v-if="form.fateFlowIp">{{form.fateFlowIp}}</div>
                                    <div v-else>{{$t('m.sitemanage.noConfiguration')}}</div>
                                </span>
                                <el-input
                                    v-if="clusterEditSubmitted===2"
                                    @blur="cancelValid('fateFlowIp')"
                                    @focus="cancelValid('fateFlowIp')"
                                    :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': fateFlowIpwarnshow }"
                                    v-model="form.fateFlowIp"
                                    placeholder >
                                </el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <!-- cluster network end -->
            </el-form>
        </div>
        <div class="Basic">
            <div class="info">
                <span>{{$t('m.sitemanage.systemVersion')}}</span>
            </div>
            <el-form v-if="form.fateVersion" ref="FATEVersionForm" label-position="left" >
                <el-row :gutter="140">
                    <el-col :span="12">
                        <el-form-item :label="$t('m.sitemanage.FATEVersion')+'：'">
                            <span class="info-text">{{form.fateVersion}}</span>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <div v-else class="complete-tips">{{$t('m.sitemanage.completeClusterConfigurationFirst')}}</div>
        </div>
        <div  class="Basic"  v-if="role.roleName==='Admin'">
            <div class="info">
                <span>{{$t('m.sitemanage.userList')}}</span>
            </div>
            <el-table
                :data="siteList"
                class="site-table"
                header-row-class-name="tableHead"
                header-cell-class-name="tableHeadCell"
                cell-class-name="tableCell"
                tooltip-effect="light">
                <el-table-column prop="" label="" width="100%" >
                    <el-table-column prop="userName" :label="$t('m.sitemanage.name')" width="200" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="role" :label="$t('m.common.role')"  width="200"></el-table-column>
                    <el-table-column prop="permission" :label="$t('m.sitemanage.permission')" ></el-table-column>
                </el-table-column>
            </el-table>
        </div>

    </div>
    <!-- 审批完成弹框 -->
    <el-dialog :visible.sync="noticedialog" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="true" class="site-finish-dialog" width="400px">
      <div class="line-text-one" v-if="noticedesc">{{$t(`m.sitemanage.changedConfigurationStatus${noticedesc}`)}}</div>
      <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" @click="notice">{{$t('m.common.OK')}}</el-button>
      </div>
    </el-dialog>
    <!-- 确定更改ip弹框 -->
    <el-dialog
        :visible.sync="changedialog"
        v-if="networkAccessEntrancesOld"
        class="ip-sure-dialog"
        :show-close="true"
        width="600px">
        <!-- 网关入口变更 -->
        <div class="line-text-one">
            {{$t('m.sitemanage.sureWantTo')}}
            <span class="line-text-one" v-if="form.networkAccessEntrances!==networkAccessEntrancesOld">{{$t('m.sitemanage.changeNetworkAccess',{type:$t('m.sitemanage.networkEntrances')})}}</span>
        </div>
        <div class="line-text" v-if="form.networkAccessEntrances!==networkAccessEntrancesOld">
            <div class="entrances">
                <div class="rigth-box" >
                    <div class="from">{{$t('m.sitemanage.from')}}</div>
                    <div class="text">
                    <span
                        v-for="(item, index) in networkAccessEntrancesOld.split(';')"
                        :key="index"
                    >{{item}}</span>
                    </div>
                </div>
                <div class="rigth-box">
                    <div class="from">{{$t('m.sitemanage.to')}}</div>
                    <div class="text">
                    <span
                        v-for="(item, index) in form.networkAccessEntrances.split(';')"
                        :key="index"
                    >{{item}}</span>
                    </div>
                </div>
            </div>
        </div>
        <!-- rollsite网关出口变更 -->
        <div class="line-text-one" v-if="form.fmRollSiteNetworkAccessExitsList!==fmRollSiteNetworkAccessExitsListOld">{{$t('m.sitemanage.changeNetworkAccess',{type:$t('m.sitemanage.rollsiteExits')})}}</div>
        <div class="line-text" v-if="form.fmRollSiteNetworkAccessExitsList!==fmRollSiteNetworkAccessExitsListOld">
            <div class="entrances">
            <div class="rigth-box">
                <div class="from">{{$t('m.sitemanage.from')}}</div>
                <div class="text">
                <span
                    v-for="(item, index) in fmRollSiteNetworkAccessExitsListOld.split(';')"
                    :key="index"
                >{{item}}</span>
                </div>
            </div>
            <div class="rigth-box">
                <div class="from">{{$t('m.sitemanage.to')}}</div>
                <div class="text">
                <span
                    v-for="(item, index) in form.fmRollSiteNetworkAccessExitsList.split(';')"
                    :key="index"
                >{{item}}</span>
                </div>
            </div>
            </div>
        </div>
        <!-- 安全类型变更 -->
        <div class="line-text-one" v-if="secureStatus!==secureStatusOld">{{$t('m.sitemanage.changeNetworkAccess',{type:$t('m.siteAdd.isSecure')})}}</div>
        <div class="line-text" v-if="secureStatus!==secureStatusOld">
            <div class="entrances">
            <div class="rigth-box">
                <div class="from">{{$t('m.sitemanage.from')}}</div>
                <div class="text">
                <span>{{$t(`m.common.${secureStatusOld}`)}}</span>
                </div>
            </div>
            <div class="rigth-box">
                <div class="from">{{$t('m.sitemanage.to')}}</div>
                <div class="text">
                <span>{{$t(`m.common.${secureStatus}`)}}</span>
                </div>
            </div>
            </div>
        </div>
        <!-- 单双向模式变更 -->
        <div class="line-text-one" v-if="pollingStatus!==pollingStatusOld">{{$t('m.sitemanage.changeNetworkAccess',{type:$t('m.siteAdd.isPolling')})}}</div>
        <div class="line-text" v-if="pollingStatus!==pollingStatusOld">
            <div class="entrances">
            <div class="rigth-box">
                <div class="from">{{$t('m.sitemanage.from')}}</div>
                <div class="text">
                <span>{{$t(`m.common.${pollingStatusOld}`)}}</span>
                </div>
            </div>
            <div class="rigth-box">
                <div class="from">{{$t('m.sitemanage.to')}}</div>
                <div class="text">
                <span>{{$t(`m.common.${pollingStatus}`)}}</span>
                </div>
            </div>
            </div>
        </div>
        <div class="line-text-two">{{$t('m.sitemanage.afterReviews')}}</div>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="okAction">{{$t('m.common.sure')}}</el-button>
            <el-button class="ok-btn" style="margin-left:20px" type="info" @click="changedialog=false" >{{$t('m.common.cancel')}}</el-button>
        </div>
    </el-dialog>
    <!-- rollsite提交验证弹窗 -->
    <el-dialog :visible.sync="showCheckRollsiteConnection" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="rollsiteCanClose" class="connection-dialog" width="500px">
        <div class="connection-loading" v-if="rollsiteConnectionStatus === 1">
            <i class="el-icon-loading"></i>
            <div class="dialog-title">{{$t('m.sitemanage.connectingIp',{type:'rollsite'})}}</div>
        </div>

        <div class="success-section" v-if="rollsiteConnectionStatus === 2">
            <div class="dialog-title">{{$t('m.sitemanage.connectingStatus',{type:$t('m.common.success')})}}</div>
            <div class="dialog-text">{{$t('m.sitemanage.hasBeenUpdated',{type:'rollsite'})}}</div>
            <el-button class="ok-btn" type="primary" @click="rollsiteOk">{{$t('m.common.OK')}}</el-button>
        </div>

        <div class="failed-section" v-if="rollsiteConnectionStatus === 3">
            <div class="dialog-title">{{$t('m.sitemanage.connectionFailedRollsite')}}</div>
            <div class="failed-rollsite-list">
                <ul>
                    <li class="list-item" v-for="(item,index) in failedList" :key="index">{{item}}</li>
                </ul>
            </div>
            <div class="dialog-text">{{$t('m.sitemanage.pleaseRetryIp',{type:$t('m.sitemanage.rollsiteEntrances')})}}</div>
            <el-button class="ok-btn" type="primary" @click="rollsiteOk">{{$t('m.common.OK')}}</el-button>
        </div>
    </el-dialog>
    <!-- fate-flow提交验证弹窗 -->
    <el-dialog class="connection-dialog" :visible.sync="showCheckFlowConnection" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="flowCanClose" width="500px">
        <div class="connection-loading" v-if="flowConnectionStatus === 1">
            <i class="el-icon-loading"></i>
            <div class="dialog-title">{{$t('m.sitemanage.connectingIp',{type:'FATE Flow'})}}</div>
        </div>

        <div class="success-section" v-if="flowConnectionStatus === 2">
            <div class="dialog-title">{{$t('m.sitemanage.connectingStatus',{type:$t('m.common.success')})}}</div>
            <div class="dialog-text">{{$t('m.sitemanage.hasBeenUpdated',{type:$t('m.sitemanage.systemVersion')})}}</div>
            <el-button class="ok-btn" type="primary" @click="flowOk">{{$t('m.common.OK')}}</el-button>
        </div>

        <div class="failed-section" v-if="flowConnectionStatus === 3">
            <div class="dialog-title">{{$t('m.sitemanage.connectingStatus',{type:$t('m.common.failed')})}}</div>
            <div class="dialog-text">{{$t('m.sitemanage.pleaseRetryIp',{type:'Fate flow'})}}</div>
            <el-button class="ok-btn" type="primary" @click="flowOk">{{$t('m.common.OK')}}</el-button>
        </div>
    </el-dialog>
    <!-- cloud-manager产生更新的提示窗 -->
    <el-dialog class="change-dialog"
        :visible.sync="showChanges"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :show-close="true"
        width="550px">
        <div class="main-title">{{$t('m.sitemanage.cloudManagerDidChangesTitle')}}</div>
        <div class="change-section" v-if="changes.exchangeList.hasChange && changes.exchangeList.new.length > 0">
            <div class="dialog-title">{{$t('m.sitemanage.changeThe',{type:'Exchange'})}}</div>
            <div class="change-rollsite-list" style="height:50px">
                <div class="link-area">
                    <div>{{$t('m.sitemanage.from')}}</div>
                    <ul>
                        <li class="list-item">{{changes.exchangeList.old}}</li>
                    </ul>
                </div>
                <div class="link-area">
                    <div>{{$t('m.sitemanage.to')}}</div>
                     <ul>
                        <li class="list-item">{{changes.exchangeList.new}}</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="change-section" v-if="changes.secureStatusList.hasChange && changes.secureStatusList.new">
            <div class="dialog-title">
                <span v-if="changes.exchangeList.hasChange">{{$t('m.common.and')}}</span>
               <span>{{$t('m.sitemanage.changeThe',{type:$t('m.siteAdd.isSecure')})}}</span>
            </div>
            <div class="change-rollsite-list" style="height:50px">
                <div class="link-area">
                    <div>{{$t('m.sitemanage.from')}}</div>
                    <ul>
                        <li class="list-item">{{changes.secureStatusList.old | getBollen}}</li>
                    </ul>
                </div>
                <div class="link-area">
                    <div>{{$t('m.sitemanage.to')}}</div>
                     <ul>
                        <li class="list-item">{{changes.secureStatusList.new | getBollen}}</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="change-section" v-if="changes.pollingStatusList.hasChange && changes.pollingStatusList.new">
            <div class="dialog-title">
                <span v-if="changes.secureStatusList.hasChange || changes.exchangeList.hasChange">{{$t('m.common.and')}}</span>
               <span>{{$t('m.sitemanage.changeThe',{type:$t('m.siteAdd.isPolling')})}}</span>
            </div>
            <div class="change-rollsite-list" style="height:50px">
                <div class="link-area">
                    <div>{{$t('m.sitemanage.from')}}</div>
                    <ul>
                        <li class="list-item">{{changes.pollingStatusList.old | getBollen}}</li>
                    </ul>
                </div>
                <div class="link-area">
                    <div>{{$t('m.sitemanage.to')}}</div>
                     <ul>
                        <li class="list-item">{{changes.pollingStatusList.new | getBollen}}</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="change-section" v-if="changes.entrancesList.hasChange && changes.entrancesList.new.length > 0">
            <div class="dialog-title">
                <span v-if="changes.pollingStatusList.hasChange || changes.secureStatusList.hasChange || changes.exchangeList.hasChange">{{$t('m.common.and')}}</span>
                <span>{{$t('m.sitemanage.changeThe',{type:$t('m.sitemanage.networkEntrances')})}}</span>
            </div>
            <div class="change-rollsite-list">
                <div class="link-area">
                    <div>{{$t('m.sitemanage.from')}}</div>
                    <ul>
                        <li class="list-item" v-for="(item,index) in changes.entrancesList.old" :key="index">{{item}}</li>
                    </ul>
                </div>
                <div class="link-area">
                    <div>{{$t('m.sitemanage.to')}}</div>
                    <ul>
                        <li class="list-item" v-for="(item,index) in changes.entrancesList.new" :key="index">{{item}}</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="dialog-text">{{$t('m.sitemanage.rollsiteWillUpdated')}}</div>
        <el-button class="ok-btn" type="primary" @click="updateNew()">{{$t('m.common.OK')}}</el-button>

    </el-dialog>
    <sitedetailip ref="sitedetailip" @updateIp="updateIp" />
  </div>
</template>

<script>
import {
    getSiteInfo,
    update,
    readmsg,
    getmsg,
    siteInfoList,
    testrollsite
    // getClusterList,
    // getFateboardList,
    // getFateflowList,
    // getFateservingList,
    // getMysqlList,
    // getNodeList,
    // getrollsiteList,
    // getcomponentversion,
    // updateVersion
} from '@/api/home'
import sitedetailip from './sitedetailip'
import { mapGetters } from 'vuex'

export default {
    name: 'sitedetail',
    components: {
        sitedetailip
    },
    data() {
        return {
            siteList: [], // 站点信息
            changedialog: false,
            noticedialog: false,
            noticedesc: '', // 已经读信息
            submitted: false, // 是否已经提交
            tosubmit: true, // 是否可提价
            edit: false,
            editSubmitted: 1, // 站点板块状态： 1 可编辑，2 可提交 3 待审批
            rollSiteEditSubmitted: 1, // rollsite板块状态：1 可编辑，2 可提交
            clusterEditSubmitted: 1, // 集群板块状态 可编辑，2 可提交
            editVersion: 1, // 是否可编辑版本
            getmsgtimeless: null,
            networkAccessEntranceswarnshow: false, // 是否显示警告样式
            fmRollSiteNetworkAccessExitsListwarnshow: false, // 是否显示警告样式
            fmRollSiteNetworkAccesswarnshow: false,
            fateFlowIpwarnshow: false,
            networkAccessEntrancesOld: '', // 缓存入口
            networkAccessExitsOld: '', // 缓存出口
            rollSiteNetworkAccessOld: '', // 缓存rollsite
            secureStatus: '',
            secureStatusOld: '', // 缓存安全类型
            pollingStatus: '',
            pollingStatusOld: '', // 缓存方向状态
            fateFlowIpOld: '', // 缓存fateflow
            formVersion: {}, // 更新版本号集合
            tempobject: {}, // 编辑版本临时数据
            fateServingVersion: '', // 待更新
            fateVersion: '', // 待更新
            versiondialog: false, // 是否确定更新版本弹框
            fateServingVersionList: [],
            showCheckRollsiteConnection: false,
            showCheckFlowConnection: false,
            rollsiteConnectionStatus: 1,
            flowConnectionStatus: 1,
            rollsiteCanClose: false,
            flowCanClose: false,
            showChanges: false,
            failedList: [],
            showApplyResult: false,
            form: {
                networkAccessEntrances: '',
                fmRollSiteNetworkAccess: '',
                fmRollSiteNetworkAccessExitsList: '',
                fateFlowIp: '',
                ExchangeInfo: {}
            },
            changes: {
                ExchangeInfo: {},
                exchangeList: {
                    hasChange: false,
                    new: '',
                    old: ''
                },
                entrancesList: {
                    hasChange: false,
                    new: '',
                    old: ''
                },
                secureStatusList: {
                    hasChange: false,
                    new: '',
                    old: ''
                },
                pollingStatusList: {
                    hasChange: false,
                    new: '',
                    old: ''
                }
            },
            rules: {
                networkAccessEntrances: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.networkAccessEntranceswarnshow = true
                                callback(
                                    new Error(
                                        this.$t('m.common.requiredfieldWithType', { type: this.$t('m.sitemanage.networkEntrances') })
                                    )
                                )
                            } else {
                                this.networkAccessEntranceswarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                fmRollSiteNetworkAccessExitsList: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.fmRollSiteNetworkAccessExitsListwarnshow = true
                                callback(
                                    new Error(
                                        this.$t('m.common.requiredfieldWithType', { type: this.$t('m.sitemanage.rollsiteExits') })
                                    )
                                )
                            } else {
                                this.fmRollSiteNetworkAccessExitsListwarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                fmRollSiteNetworkAccess: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.fmRollSiteNetworkAccesswarnshow = true
                                callback(
                                    new Error(
                                        this.$t('m.common.requiredfieldWithType', { type: this.$t('m.sitemanage.rollsiteEntrances') })
                                    )
                                )
                            } else {
                                this.fmRollSiteNetworkAccesswarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                fateFlowIp: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.fateFlowIpwarnshow = true
                                callback(
                                    new Error(
                                        this.$t('m.common.requiredfieldWithType', { type: this.$t('m.sitemanage.fateFlowIp') })
                                    )
                                )
                            } else {
                                this.fateFlowIpwarnshow = false
                                callback()
                            }
                        }
                    }
                ]
            }
        }
    },
    computed: {
        ...mapGetters(['role', 'version'])

    },
    watch: {
        'form.fateFlowIp': {
            handler: function(val) {
                if (val) {
                    let len = val.length || 0
                    this.tosubmit = len === 0
                }
            },
            immediate: true
        },
        'form.secureStatus': {
            handler: function(val) {
                this.tosubmit = this.verifiAllchange()
            },
            immediate: true
        },
        'form.pollingStatus': {
            handler: function(val) {
                this.tosubmit = this.verifiAllchange()
            },
            immediate: true
        }
    },
    created() {
        this.initInfo()
        // this.togetMsg()
        this.$store.dispatch('selectEnum')
    },
    beforeDestroy() {
        window.clearTimeout(this.getmsgtimeless)
    },
    methods: {
        initInfo(updateMark) {
            let data = {
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId),
                siteId: parseInt(this.$route.query.siteId)
            }
            if (updateMark) {
                data.updateMark = 1
            }
            getSiteInfo(data).then(res => {
                if (updateMark !== 1) {
                    // 初次请求
                    this.form = Object.assign({}, res.data, {
                        'networkAccessEntrances': res.data.ExchangeInfo.networkAccessEntrances,
                        'fmRollSiteNetworkAccessExitsList': res.data.ExchangeInfo.fmRollSiteNetworkAccessExitsList,
                        'fmRollSiteNetworkAccess': res.data.ExchangeInfo.fmRollSiteNetworkAccess,
                        'secureStatus': res.data.ExchangeInfo.secureStatus,
                        'pollingStatus': res.data.ExchangeInfo.pollingStatus
                    })
                    this.$set(this.form, 'secureStatus', this.getStatus(this.form.ExchangeInfo.secureStatus))
                    this.$set(this.form, 'pollingStatus', this.getStatus(this.form.ExchangeInfo.pollingStatus))
                    // 缓存数据，取消编辑还原
                    let exchangeInfo = this.form.ExchangeInfo
                    this.networkAccessEntrancesOld = exchangeInfo.networkAccessEntrances || '' // 旧ip入口
                    // this.networkAccessExitsOld = exchangeInfo.networkAccessExits || '' // 旧ip出口
                    this.fmRollSiteNetworkAccessExitsListOld = exchangeInfo.fmRollSiteNetworkAccessExitsList || '' // 旧ip出口
                    this.fmRollSiteNetworkAccessOld = exchangeInfo.fmRollSiteNetworkAccess || '' // 缓存rollsite
                    this.secureStatusOld = this.form.secureStatus
                    this.secureStatus = this.secureStatusOld
                    this.pollingStatusOld = this.form.pollingStatus
                    this.pollingStatus = this.pollingStatusOld
                    this.fateFlowIpOld = this.form.fateFlowIp || ''
                    if (res.data.editStatus.code === 1 || res.data.editStatus.code === -1) {
                        this.togetMsg()
                        this.editSubmitted = 3
                    } else if (res.data.editStatus.code === 2) {
                        this.editSubmitted = 1
                    }
                    // 展示来自cm的exchange变更提示弹窗
                    if (this.form.exchangeReadStatus === 1) {
                        this.setExchange()
                    }
                }
            })
            // 获取uesrid列表
            let params = {
                partyId: parseInt(this.$route.query.partyId)
            }
            siteInfoList(params).then(res => {
                this.siteList = res.data || []
            })
        },
        updateNew() {
            let ExchangeInfo = this.form.ExchangeInfo
            if (ExchangeInfo.exchangeNameNew && ExchangeInfo.exchangeNameNew.length > 0) {
                this.$set(this.form.ExchangeInfo, 'exchangeName', ExchangeInfo.exchangeNameNew)
            }
            if (ExchangeInfo.networkAccessEntrancesNew && ExchangeInfo.networkAccessEntrancesNew.length > 0) {
                this.$set(this.form.ExchangeInfo, 'networkAccessEntrances', ExchangeInfo.networkAccessEntrancesNew)
                this.form.networkAccessEntrances = ExchangeInfo.networkAccessEntrancesNew
            }
            if (ExchangeInfo.secureStatusNew) {
                this.$set(this.form.ExchangeInfo, 'secureStatus', this.getStatus(ExchangeInfo.secureStatusNew))
                this.form.secureStatus = this.getStatus(ExchangeInfo.secureStatusNew)
            }
            if (ExchangeInfo.pollingStatusNew) {
                this.$set(this.form.ExchangeInfo, 'secureStatus', this.getStatus(ExchangeInfo.pollingStatusNew))
                this.form.pollingStatus = this.getStatus(ExchangeInfo.pollingStatusNew)
            }
            if (ExchangeInfo.vipEntrancesNew && ExchangeInfo.vipEntrancesNew.length > 0) {
                this.$set(this.form.ExchangeInfo, 'vipEntrances', ExchangeInfo.vipEntrancesNew)
            }
            this.$nextTick(() => {
                this.initInfo(1)
                this.showChanges = false
            })
        },
        setExchange() {
            let exchangeInfo = this.form.ExchangeInfo
            this.changes.ExchangeInfo = exchangeInfo
            if (exchangeInfo.exchangeNameNew && exchangeInfo.exchangeNameNew !== exchangeInfo.exchangeName) {
                this.changes.exchangeList.hasChange = true
                this.changes.exchangeList.new = exchangeInfo.exchangeNameNew
                this.changes.exchangeList.old = exchangeInfo.exchangeName
            }
            if (exchangeInfo.networkAccessEntrancesNew && exchangeInfo.networkAccessEntrancesNew !== exchangeInfo.networkAccessEntrances) {
                this.changes.entrancesList.hasChange = true
                this.changes.entrancesList.new = exchangeInfo.networkAccessEntrancesNew.split(';')
                this.changes.entrancesList.old = exchangeInfo.networkAccessEntrances.split(';')
            }
            if (exchangeInfo.secureStatusNew && exchangeInfo.secureStatusNew !== exchangeInfo.secureStatus) {
                this.changes.secureStatusList.hasChange = true
                this.changes.secureStatusList.new = exchangeInfo.secureStatusNew
                this.changes.secureStatusList.old = exchangeInfo.secureStatus
            }
            if (exchangeInfo.pollingStatusNew && exchangeInfo.pollingStatusNew !== exchangeInfo.pollingStatus) {
                this.changes.pollingStatusList.hasChange = true
                this.changes.pollingStatusList.new = exchangeInfo.pollingStatusNew
                this.changes.pollingStatusList.old = exchangeInfo.pollingStatus
            }
            this.showChanges = true
        },
        // 点击编辑
        toEdit(type) {
            let edit = {
                'site': 'editSubmitted',
                'rollsite': 'rollSiteEditSubmitted',
                'cluster': 'clusterEditSubmitted'
            }
            // if (type === 'site') {
            //     this.form.secureStatus = this.getStatus(this.form.secureStatus)
            //     this.form.pollingStatus = this.getStatus(this.form.pollingStatus)
            // }
            this.tosubmit = true
            this[edit[type]] = 2
        },
        // 取消编辑
        cancel(type) {
            let param = {
                'site': ['fmRollSiteNetworkAccess', 'fmRollSiteNetworkAccessExitsList', 'networkAccessEntrances', 'secureStatus', 'pollingStatus'],
                'cluster': 'fateFlowIp'
            }
            let edit = {
                'site': 'editSubmitted',
                'cluster': 'clusterEditSubmitted'
            }
            let paramName = param[type]
            if (paramName.map) {
                paramName.map(item => {
                    this.$set(this.form, `${item}`, this[`${item}Old`])
                })
            } else {
                this.$set(this.form, `${paramName}`, this[`${paramName}Old`])
            }
            if (type === 'site') {
                this.secureStatus = this.secureStatusOld
                this.pollingStatus = this.pollingStatusOld
            }

            this[edit[type]] = 1
            this.shouldtosubmit(paramName)
            this.$nextTick(() => {
                this.$refs['form'].clearValidate()
            })
        },
        // 提交更新
        submit(name) {
            const self = this
            let param = {
                'site': ['fmRollSiteNetworkAccess', 'fmRollSiteNetworkAccessExitsList', 'networkAccessEntrances', 'secureStatus', 'pollingStatus'],
                'cluster': 'fateFlowIp'
            }
            // this.changedialog = true
            let paramName = param[name]
            validateForm(paramName)
            function validateForm(paramName) {
                if (name === 'cluster') {
                    self.$refs['form'].validateField(`${paramName}`, valid => {})
                    self.showCheckFlowConnection = true // fateFlow弹窗
                    self.flowCanClose = false
                    self.flowConnectionStatus = 1
                    self.updateFateflow()
                } else if (name === 'site') {
                    paramName.every(item => {
                        self.$refs['form'].validateField(`${item}`, valid => {})
                    })
                    // if (typeof self.form.secureStatus !== 'number') {
                    //     self.secureStatus = self.getStatus(self.form.secureStatus)
                    // }
                    // if (typeof self.form.pollingStatus !== 'number') {
                    //     self.pollingStatus = self.getStatus(self.form.pollingStatus)
                    // }
                    if (self.form.fmRollSiteNetworkAccess !== self.fmRollSiteNetworkAccessOld) {
                        self.testrollsite() // rollsite存在修改则先验证rollsite
                    } else {
                        self.secureStatus = self.form.secureStatus
                        self.pollingStatus = self.form.pollingStatus
                        self.changedialog = true // 待审核内容修改确认弹窗
                    }
                }
            }
        },
        // 确定修改
        okAction() {
            let data = { ...this.form }
            data.role = this.form.role.code
            data.networkAccessExits = data.fmRollSiteNetworkAccessExitsList
            data.secureStatus = this.getStatus(this.secureStatus)
            data.pollingStatus = this.getStatus(this.pollingStatus)
            // console.log(data, 'data')
            update(data).then(res => {
                this.changedialog = false
                this.initInfo()
            })
        },
        // 更新rollsite
        updateRollsite() {
            let param = { ...this.form }
            param.UpdateRollSiteInfo = 1
            param.secureStatus = this.getStatus(param.secureStatus)
            param.pollingStatus = this.getStatus(param.pollingStatus)
            // console.log(param, 'param')
            try {
                update(param).then(res => {
                    this.$nextTick(() => {
                        this.rollSiteEditSubmitted = 1
                    })
                    this.initInfo()
                }).catch(res => {
                    this.showCheckRollsiteConnection = false
                })
            } catch {
                this.showCheckRollsiteConnection = false
            }
        },
        // 通过请求fateflow更新系统版本号
        updateFateflow() {
            let param = {
                federatedId: this.form.federatedId,
                partyId: this.form.partyId,
                UpdateFateFlowInfo: this.form.fateFlowIp,
                siteId: this.form.siteId
            }
            try {
                update(param).then(res => {
                    if (res && res.code === 0 && res.msg === 'success') {
                        this.flowConnectionStatus = 2
                        this.flowCanClose = true
                    } else if (res && res.code === 0 && res.msg === 'failed') {
                        this.flowConnectionStatus = 3
                        this.flowCanClose = true
                    }
                }).catch(res => {
                    this.showCheckFlowConnection = false
                })
            } catch {
                this.showCheckFlowConnection = false
            }
        },
        // 显示增加行
        addShow(type) {
            this.$refs['sitedetailip'].networkacesstype = type
            this.$refs['sitedetailip'].adddialog = true

            let editType = {
                'entrances': 'networkAccessEntrances',
                'exit': 'fmRollSiteNetworkAccessExitsList',
                'rollsite': 'fmRollSiteNetworkAccess'
            }
            let parameterName = editType[type]
            // console.log(this.form, 'form')
            if (this.form[parameterName]) {
                let tempArr = []
                this.form[parameterName].split(';').forEach(item => {
                    if (item) {
                        let obj = {}
                        obj.ip = item
                        obj.show = false
                        obj.checked = false
                        tempArr.push(obj)
                    }
                })
                this.$refs['sitedetailip'].entrancesSelect = [...new Set(tempArr)]
            } else {
                this.$refs['sitedetailip'].entrancesSelect = []
            }
        },
        // 提交按钮是否可点击
        shouldtosubmit(paramName) {
            const self = this
            if (paramName !== 'fateFlowIp') {
                self.tosubmit = self.verifiAllchange()
            } else {
                self.tosubmit = verifiNochange(paramName)
            }
            function verifiNochange(paramName) {
                if (paramName.map) {
                    return paramName.every(item => {
                        return self[`${item}Old`] === self.form[`${item}`]
                    })
                } else {
                    return self[`${paramName}Old`] === self.form[`${paramName}`]
                }
            }
        },
        verifiAllchange() {
            return (this.form.secureStatus === this.secureStatusOld &&
                   this.form.pollingStatus === this.pollingStatusOld &&
                   this.form.networkAccessEntrances === this.networkAccessEntrancesOld &&
                   this.form.fmRollSiteNetworkAccessExitsList === this.fmRollSiteNetworkAccessExitsListOld &&
                   this.form.fmRollSiteNetworkAccess === this.fmRollSiteNetworkAccessOld)
        },
        // 取消表单验证
        cancelValid(validtype) {
            this.$refs['form'].clearValidate(validtype)
            this[`${validtype}warnshow`] = false
        },
        // 已读通知
        notice() {
            let data = {
                partyId: parseInt(this.form.partyId),
                federatedId: parseInt(this.form.federatedId),
                readStatus: 3
            }
            readmsg(data).then(res => {
                window.clearTimeout(this.getmsgtimeless) // 清除定时器
                this.noticedialog = false
                this.initInfo()
            })
            this.noticedialog = false
        },
        async togetMsg() {
            let data = {
                partyId: parseInt(this.form.partyId),
                federatedId: parseInt(this.form.federatedId),
                siteId: this.form.siteId
            }
            let res = await getmsg(data)
            if (res.data.editStatus.code === 2) {
                if (res.data.readStatus.code > 0 && res.data.readStatus.code !== 3) {
                    this.noticedialog = true
                    this.noticedesc = res.data.readStatus.desc.indexOf('Successfully') > -1 ? 'Success' : 'Failed'
                } else if (res.data.readStatus.code === 3) {
                    this.noticedialog = false
                }
            } else {
                this.getmsgtimeless = setTimeout(() => {
                    this.togetMsg()
                }, 3000)
            }
        },
        updateIp(data) {
            // console.log(data, 'data')
            this.$set(this.form, `${data.name}`, data.data)
        },
        testrollsite() {
            let param = {
                partyId: this.form.partyId,
                fmRollSiteNetworkAccess: this.form.fmRollSiteNetworkAccess
            }
            if (this.form.fmRollSiteNetworkAccess.length < 1) return
            this.showCheckRollsiteConnection = true // rollsite弹窗
            this.rollsiteConnectionStatus = 1
            this.rollsiteCanClose = false
            try {
                testrollsite(param).then(res => {
                    this.failedList = []
                    if (res && res.data && res.data.failed.length > 0) {
                        this.rollsiteConnectionStatus = 3
                        this.failedList = res.data.failed
                    } else {
                        this.rollsiteConnectionStatus = 2
                    }
                    this.rollsiteCanClose = true
                }).catch(res => {
                    this.showCheckRollsiteConnection = false
                })
            } catch {
                this.showCheckRollsiteConnection = false
            }
        },
        rollsiteOk() {
            if (this.rollsiteConnectionStatus === 2) {
                this.$nextTick(() => {
                    this.rollSiteEditSubmitted = 1
                })
                if (
                    this.form.fmRollSiteNetworkAccessExitsList !== this.fmRollSiteNetworkAccessExitsListOld ||
                        this.form.networkAccessEntrances !== this.networkAccessEntrancesOld ||
                        this.secureStatus !== this.secureStatusOld ||
                        this.pollingStatus !== this.pollingStatusOld
                ) {
                    this.changedialog = true
                } else {
                    this.updateRollsite()
                }
            }
            this.showCheckRollsiteConnection = false
            this.$nextTick(() => {
                this.$refs['form'].clearValidate()
            })
        },
        flowOk() {
            if (this.flowConnectionStatus === 2) {
                this.$nextTick(() => {
                    this.clusterEditSubmitted = 1
                })
                this.initInfo()
            }
            this.showCheckFlowConnection = false
            this.$nextTick(() => {
                this.$refs['form'].clearValidate()
            })
        },
        getStatus(stauts) {
            if (typeof stauts === 'number') {
                return stauts === 1
            } else {
                return stauts === true ? 1 : 2
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/sitedetail.scss';
</style>
