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
                    <el-form-item :label="$t('m.common.institution',{type:'i'})+'：'">
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
            <el-form ref="exchangeForm" :model="form" label-position="left" label-width="180px">
            <el-row :gutter="140">
                <el-col :span="12">
                    <el-form-item :label="$t('m.sitemanage.exchangeName')+'：'">
                        <span class="info-text">{{form.ExchangeInfo.exchangeName}}</span>
                    </el-form-item>
                    <el-form-item  label="VIP Entrances：" >
                        <span class="info-text">{{form.ExchangeInfo.vipEntrances}}</span>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item>
                        <div class="info-text placehodler-div" ></div>
                    </el-form-item>
                    <el-form-item :label="$t('m.sitemanage.networkAccessExits')+'：'" style="height:100%;" label-width="250px">
                        <span class="info-text" v-if="form.ExchangeInfo.exchangeNetworkAccessExits">
                            <div v-for="(item,index) in form.ExchangeInfo.exchangeNetworkAccessExits.split(';')" :key="index" >{{item}}</div>
                        </span>
                    </el-form-item>
                </el-col>
            </el-row>
            </el-form>
        </div>
        <div class="Basic">
            <div class="info">
                <span>{{$t('m.sitemanage.networkConfiguration')}}</span>

            </div>
            <el-form ref="form" :model="form" label-position="left" :rules="rules" label-width="260px">
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
                        <el-col :span="12">
                            <el-form-item :label="$t('m.sitemanage.networkExits')+'：'" style="height:100%;" prop="networkAccessExits" >
                                <span class="info-text" v-if="editSubmitted!==2 && form.networkAccessExits">
                                    <div v-for="(item,index) in form.networkAccessExits.split(';')" :key="index" >{{item}}</div>
                                </span>
                                <el-input
                                    v-if="editSubmitted===2"
                                    @focus="addShow('exit')"
                                    @blur="cancelValid('networkAccessExits')"
                                    :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': networkAccessExitswarnshow }"
                                    v-model="form.networkAccessExits"
                                    placeholder >
                                    <i slot="suffix" @click="addShow('exit')" class="el-icon-edit plus" />
                                </el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <!-- site network end -->
                <!-- rollsite network start -->
                <div class="plate">
                    <div class="plate-title">
                        <span class="title-text">{{$t('m.sitemanage.rollsiteNetworkConf')}}</span>
                        <span  v-if="role.roleName==='Admin' || role.roleName==='Developer or OP'">
                            <div class="info-del">
                                <img
                                    src="@/assets/edit_click.png"
                                    v-if="rollSiteEditSubmitted === 1"
                                    @click="toEdit('rollsite')"
                                    class="edit"
                                    :class="{'disable':(editSubmitted === 2 || clusterEditSubmitted === 2)}"
                                    alt="" />
                                <el-button v-if="rollSiteEditSubmitted === 2" @click="submit('rollsite')" :disabled="tosubmit" type="primary">{{$t('m.common.submit')}}</el-button>
                                <el-button v-if="rollSiteEditSubmitted === 2" @click="cancel('rollsite')" type="info">{{$t('m.common.cancel')}}</el-button>
                            </div>
                        </span>
                    </div>
                    <el-row :gutter="140">
                        <el-col :span="12">
                            <el-form-item :label="$t('m.sitemanage.rollsiteNetworkAccess')+'：'" style="height:100%;" prop="rollsiteNetworkAccess" >
                                <span class="info-text" v-if="rollSiteEditSubmitted!==2 && form.rollsiteNetworkAccess">
                                    <div v-for="(item,index) in form.rollsiteNetworkAccess.split(';')" :key="index" >{{item}}</div>
                                </span>
                                <el-input
                                    v-if="rollSiteEditSubmitted===2"
                                    @focus="addShow('rollsite')"
                                    @blur="cancelValid('rollsiteNetworkAccess')"
                                    :class="{ 'edit-text': true, 'plus-text':true,'Network-text':true,'exitwarn': rollsiteNetworkAccesswarnshow }"
                                    v-model="form.rollsiteNetworkAccess"
                                    placeholder >
                                    <i slot="suffix" @click="addShow('rollsite')" class="el-icon-edit plus" />
                                </el-input>
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
            <el-form ref="FATEVersionForm" label-position="left" >
                <el-row :gutter="140">
                    <el-col :span="12">
                        <el-form-item :label="$t('m.sitemanage.FATEVersion')+'：'">
                            <span class="info-text">{{form.fateVersion}}</span>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
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
    <el-dialog :visible.sync="noticedialog" :close-on-click-modal="false" :close-on-press-escape="false" class="site-delete-dialog" width="774px">
      <div class="line-text-one">{{noticedesc}}</div>
      <div class="dialog-footer">
        <el-button class="ok-btn" type="primary" @click="notice">{{$t('m.common.OK')}}</el-button>
      </div>
    </el-dialog>
    <!-- 确定更改version版本弹框 -->
    <!-- <el-dialog :visible.sync="versiondialog" :close-on-click-modal="false" :close-on-press-escape="false" class="site-delete-dialog" width="774px">
        <div class="line-one">{{$t('m.sitemanage.sureChangeVersion')}}</div>
        <div class="line-text-two">{{$t('m.sitemanage.resultsSynchronized')}}</div>
        <div class="dialog-footer">
            <el-button class="ok-btn" type="primary" @click="sureVersion">{{$t('m.common.sure')}}</el-button>
            <el-button class="ok-btn" type="info" @click="versiondialog=false">{{$t('m.common.cancel')}}</el-button>
        </div>
    </el-dialog> -->
    <!-- 确定更改ip弹框 -->
    <el-dialog
        :visible.sync="changedialog"
        v-if="networkAccessEntrancesOld && networkAccessExitsOld"
        class="ip-delete-dialog"
        width="700px">
        <div class="line-text-one">{{$t('m.sitemanage.sureWantTo')}}</div>
        <div class="line-text-one" v-if="form.networkAccessEntrances!==networkAccessEntrancesOld">{{$t('m.sitemanage.changeNetworkAccess',{type:$t('m.sitemanage.networkEntrances')})}}</div>
        <div class="line-text" v-if="form.networkAccessEntrances!==networkAccessEntrancesOld">
            <div class="entrances">
            <div class="rigth-box" style="margin-right: 70px">
                <div class="from">{{$t('m.sitemanage.from')}}</div>
                <div class="text">
                <span
                    v-for="(item, index) in networkAccessEntrancesOld.split(';')"
                    :key="index"
                >{{item}}</span>
                </div>
            </div>
            <div class="rigth-box" style="margin-left: 70px">
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
        <div style="margin:36px 0;font-size:24px;color:#2D3642" v-if="form.networkAccessExits!==networkAccessExitsOld" >{{$t('m.sitemanage.and')}}{{$t('m.sitemanage.changeNetworkAccess',{type:$t('m.sitemanage.networkExits')})}}</div>
        <div class="line-text" v-if="form.networkAccessExits!==networkAccessExitsOld">
            <div class="entrances">
            <div class="rigth-box" style="margin-right: 70px">
                <div class="from">{{$t('m.sitemanage.from')}}</div>
                <div class="text">
                <span v-for="(item, index) in networkAccessExitsOld.split(';')" :key="index">{{item}}</span>
                </div>
            </div>
            <div class="rigth-box" style="margin-left: 70px">
                <div class="from">{{$t('m.sitemanage.to')}}</div>
                <div class="text">
                <span
                    v-for="(item, index) in form.networkAccessExits.split(';')"
                    :key="index"
                >{{item}}</span>
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
            <div class="dialog-text">{{$t('m.sitemanage.pleaseRetryFlow')}}</div>
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
            <div class="dialog-text">{{$t('m.sitemanage.pleaseRetryFlow')}}</div>
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

        <div class="change-section" v-if="changes.entrancesList.hasChange && changes.entrancesList.new.length > 0">
            <div class="dialog-title">{{$t('m.sitemanage.changeThe',{type:$t('m.sitemanage.networkEntrances')})}}</div>
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

        <div class="change-section" v-if="changes.exitsList.hasChange && changes.exitsList.new.length > 0">
            <div class="dialog-title">{{$t('m.sitemanage.changeThe',{type:$t('m.sitemanage.networkExits')})}}</div>
            <div class="change-rollsite-list">
                <div class="link-area">
                    <div>{{$t('m.sitemanage.from')}}</div>
                    <ul>
                        <li class="list-item" v-for="(item,index) in changes.exitsList.old" :key="index">{{item}}</li>
                    </ul>
                </div>
                <div class="link-area">
                    <div>{{$t('m.sitemanage.to')}}</div>
                    <ul>
                        <li class="list-item" v-for="(item,index) in changes.exitsList.new" :key="index">{{item}}</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="dialog-text">{{$t('m.sitemanage.rollsiteWillUpdated')}}</div>
        <el-button class="ok-btn" type="primary" @click="initInfo(1)">{{$t('m.common.OK')}}</el-button>

    </el-dialog>
    <!-- 审核结构弹窗 -->
    <!-- <el-dialog class="connection-dialog" :visible.sync="showApplyResult" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" width="400px">
        <div class="success-section" v-if="resultType === 1">
            <div class="dialog-title">{{$t('m.sitemanage.connectingStatus',{type:$t('m.common.success')})}}</div>
            <div class="dialog-text">{{$t('m.sitemanage.hasBeenUpdated',{type:$t('m.sitemanage.systemVersion')})}}</div>
            <el-button class="ok-btn" type="primary" @click="flowOk">{{$t('m.common.OK')}}</el-button>
        </div>

        <div class="failed-section" v-if="resultType === 2">
            <div class="dialog-title">{{$t('m.sitemanage.connectingStatus',{type:$t('m.common.failed')})}}</div>
            <div class="dialog-text">{{$t('m.sitemanage.pleaseRetryFlow')}}</div>
            <el-button class="ok-btn" type="primary" @click="flowOk">{{$t('m.common.OK')}}</el-button>
        </div>
    </el-dialog> -->
    <sitedetailip ref="sitedetailip" @updateIp="updateIp" />
  </div>
</template>

<script>
import {
    getSiteInfo,
    update,
    readmsg,
    getmsg,
    siteInfoList
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
            networkAccessExitswarnshow: false, // 是否显示警告样式
            rollsiteNetworkAccesswarnshow: false,
            fateFlowIpwarnshow: false,
            networkAccessEntrancesOld: '', // 缓存入口
            networkAccessExitsOld: '', // 缓存出口
            rollsiteNetworkAccessOld: '', // 缓存rollsite
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
                networkAccessExits: '',
                rollsiteNetworkAccess: '',
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
                exitsList: {
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
                            console.log(arguments, 'arg')
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
                networkAccessExits: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.networkAccessExitswarnshow = true
                                callback(
                                    new Error(
                                        this.$t('m.common.requiredfieldWithType', { type: this.$t('m.sitemanage.networkExits') })
                                    )
                                )
                            } else {
                                this.networkAccessExitswarnshow = false
                                callback()
                            }
                        }
                    }
                ],
                rollsiteNetworkAccess: [
                    {
                        required: true,
                        trigger: 'change',
                        validator: (rule, value, callback) => {
                            if (!value) {
                                this.rollsiteNetworkAccesswarnshow = true
                                callback(
                                    new Error(
                                        this.$t('m.common.requiredfieldWithType', { type: this.$t('m.sitemanage.rollsiteNetworkAccess') })
                                    )
                                )
                            } else {
                                this.rollsiteNetworkAccesswarnshow = false
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
        initInfo(isMark) {
            let data = {
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId)
            }
            if (isMark === 1) {
                data.updateMark = isMark
                this.showChanges = false
            }
            getSiteInfo(data).then(res => {
                // 初次请求
                this.form = Object.assign({}, res.data, {
                    'networkAccessEntrances': res.data.ExchangeInfo.networkAccessEntrances,
                    'networkAccessExits': res.data.ExchangeInfo.networkAccessExits
                })
                console.log(this.form, 'form')
                this.networkAccessEntrancesOld = this.form.ExchangeInfo.networkAccessEntrances // 旧ip入口
                this.networkAccessExitsOld = this.form.ExchangeInfo.networkAccessExits // 旧ip出口
                // TO DO
                this.rollsiteNetworkAccessOld = this.form.rollsiteNetworkAccess || '' // 缓存rollsite
                this.fateFlowIpOld = this.form.fateFlowIp || ''
                if (res.data.editStatus.code === 1) {
                    this.editSubmitted = 3
                } else if (res.data.editStatus.code === 2) {
                    this.editSubmitted = 1
                }
                // 展示来自cm的exchange变更提示弹窗
                if (this.form.exchangeRreadStatus === 1 && isMark !== 1) {
                    this.setExchange()
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
        setExchange() {
            let exchangeInfo = this.form.ExchangeInfo
            this.changes.ExchangeInfo = exchangeInfo
            console.log(exchangeInfo, 'exchangeInfo')
            if (exchangeInfo.exchangeNameNew && exchangeInfo.exchangeNameNew.length > 0) {
                this.changes.exchangeList.hasChange = true
                this.changes.exchangeList.new = exchangeInfo.exchangeNameNew
                this.changes.exchangeList.old = exchangeInfo.exchangeName
            }
            if (exchangeInfo.NetworkAccessExitsNew && exchangeInfo.NetworkAccessExitsNew.length > 0) {
                this.changes.exitsList.hasChange = true
                this.changes.exitsList.new = exchangeInfo.NetworkAccessExitsNew.split(';')
                this.changes.exitsList.old = exchangeInfo.networkAccessExits.split(';')
            }
            if (exchangeInfo.NetworkAccessEntrancesNew && exchangeInfo.NetworkAccessEntrancesNew.length > 0) {
                this.changes.entrancesList.hasChange = true
                this.changes.entrancesList.new = exchangeInfo.NetworkAccessEntrancesNew.split(';')
                this.changes.entrancesList.old = exchangeInfo.networkAccessEntrances.split(';')
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
            this[edit[type]] = 2
        },
        // 取消编辑
        cancel(type) {
            let param = {
                'site': ['networkAccessEntrances', 'networkAccessExits'],
                'rollsite': 'rollsiteNetworkAccess',
                'cluster': 'fateFlowIp'
            }
            let edit = {
                'site': 'editSubmitted',
                'rollsite': 'rollSiteEditSubmitted',
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
                'site': ['networkAccessEntrances', 'networkAccessExits'],
                'rollsite': 'rollsiteNetworkAccess',
                'cluster': 'fateFlowIp'
            }
            // this.changedialog = true
            let paramName = param[name]
            validateForm(paramName)
            function validateForm(paramName) {
                if (paramName.map) {
                    paramName.every(item => {
                        self.$refs['form'].validateField(`${item}`, valid => {})
                    })
                    self.changedialog = true // 站点出入口网关弹窗
                } else {
                    self.$refs['form'].validateField(`${paramName}`, valid => {})
                    if (name === 'rollsite') {
                        self.showCheckRollsiteConnection = true // rollsite弹窗
                        self.rollsiteConnectionStatus = 1
                        self.rollsiteCanClose = false
                        self.updateRollsite()
                    } else if (name === 'cluster') {
                        self.showCheckFlowConnection = true // fateFlow弹窗
                        self.flowCanClose = false
                        self.flowConnectionStatus = 1
                        self.updateFateflow()
                    }
                }
            }
        },
        // 确定修改
        okAction() {
            let data = { ...this.form }
            data.role = this.form.role.code
            update(data).then(res => {
                this.changedialog = false
                this.initInfo()
            })
        },
        // 更新rollsite
        updateRollsite() {
            let param = {
                federatedId: this.form.federatedId,
                partyId: this.form.partyId,
                UpdateRollSiteInfo: this.form.rollsiteNetworkAccess
            }
            try {
                update(param).then(res => {
                    console.log(res, 'updateRollsite')
                    this.failedList = []
                    if (res && res.data && res.data.ip_faild.length > 0) {
                        this.rollsiteConnectionStatus = 3
                        this.failedList = res.data.ip_faild
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
        // 通过请求fateflow更新系统版本号
        updateFateflow() {
            let param = {
                federatedId: this.form.federatedId,
                partyId: this.form.partyId,
                UpdateFateFlowInfo: this.form.fateFlowIp
            }
            try {
                update(param).then(res => {
                    console.log(res, 'updateFateflow')
                    if (res && res.code === 0 && res.msg === 'success') {
                        this.flowConnectionStatus = 2
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
                'exit': 'networkAccessExits',
                'rollsite': 'rollsiteNetworkAccess'
            }
            let parameterName = editType[type]
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
            self.tosubmit = verifiNochange(paramName)
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
        // 取消表单验证
        cancelValid(validtype) {
            this.$refs['form'].clearValidate(validtype)
            this[`${validtype}warnshow`] = false
        },
        // 已读通知
        notice() {
            let data = {
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId),
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
                partyId: parseInt(this.$route.query.partyId),
                federatedId: parseInt(this.$route.query.federatedId)
            }
            let res = await getmsg(data)
            if (res.data.editStatus.code === 2) {
                if (res.data.readStatus.code > 0 && res.data.readStatus.code !== 3) {
                    this.noticedialog = true
                    this.noticedesc = res.data.readStatus.desc
                } else if (res.data.readStatus.code === 3) {
                    this.noticedialog = false
                }
            } else {
                this.getmsgtimeless = setTimeout(() => {
                    this.togetMsg()
                }, 1500)
            }
        },
        updateIp(data) {
            console.log(data, 'data')
            this.$set(this.form, `${data.name}`, data.data)
        },
        rollsiteOk() {
            if (this.rollsiteConnectionStatus === 2) {
                this.$nextTick(() => {
                    this.rollSiteEditSubmitted = 1
                })
                this.initInfo()
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
        }
        // 编辑版本
        // toEditVersion() {
        //     let object = JSON.parse(JSON.stringify(this.form.componentVersion))
        //     this.tempobject = object
        //     function getres (res, setl) {
        //         return res.data && res.data.forEach(item => {
        //             let o = {}
        //             o.label = item
        //             o.value = item
        //             setl.push(o)
        //         })
        //     }
        //     this.form.componentVersion = object.map(item => {
        //         let setl = []
        //         if (item.label === 'clustermanager') {
        //             getClusterList().then(res => {
        //                 getres(res, setl)
        //             })
        //         } else if (item.label === 'fateboard') {
        //             getFateboardList().then(res => {
        //                 getres(res, setl)
        //             })
        //         } else if (item.label === 'mysql') {
        //             getMysqlList().then(res => {
        //                 getres(res, setl)
        //             })
        //         } else if (item.label === 'nodemanager') {
        //             getNodeList().then(res => {
        //                 getres(res, setl)
        //             })
        //         } else if (item.label === 'rollsite') {
        //             getrollsiteList().then(res => {
        //                 getres(res, setl)
        //             })
        //         } else if (item.label === 'fateflow') {
        //             getFateflowList().then(res => {
        //                 getres(res, setl)
        //             })
        //         }

        //         item.setl = setl
        //         return item
        //     })
        //     this.fateServingVersionList = []
        //     getFateservingList().then(res => {
        //         res.data && res.data.forEach(item => {
        //             let obj = {}
        //             obj.label = item
        //             obj.value = item
        //             this.fateServingVersionList.push(obj)
        //         })
        //     })

        //     this.editVersion = 2
        // },
        // 取消编辑版本
        // toCanceleditVersion() {
        //     this.form.componentVersion = this.tempobject
        //     this.editVersion = 1
        // },
        // 确定更新
        // sureVersion() {
        //     let data = {
        //         fateServingVersion: this.fateServingVersion,
        //         fateVersion: this.fateVersion,
        //         componentVersion: JSON.stringify(this.formVersion),
        //         partyId: parseInt(this.$route.query.partyId),
        //         federatedId: parseInt(this.$route.query.federatedId)
        //     }
        //     updateVersion(data).then(res => {
        //         this.versiondialog = false
        //         this.editVersion = 1
        //         this.initInfo()
        //     })
        // },
        // 自动更新版本
        // toRefresh() {

        // },
        // 获取子版本
        // togetcomponentVersion(val) {
        //     let data = {
        //         fateVersion: val
        //     }
        //     getcomponentversion(data).then(res => {
        //         let object = JSON.parse(res.data)
        //         let arr = []
        //         for (const key in object) {
        //             let obj = {}
        //             obj.label = key
        //             obj.version = object[key]
        //             arr.push(obj)
        //         }
        //         this.form.componentVersion = arr
        //         let OBJ = {}
        //         this.form.componentVersion.forEach(item => {
        //             OBJ[item.label] = item['version']
        //         })
        //         this.formVersion = { ...OBJ }
        //     })
        // }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/sitedetail.scss';
</style>
