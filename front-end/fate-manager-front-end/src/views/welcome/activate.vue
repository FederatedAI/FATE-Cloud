<template>
    <div class="activate">
        <div class="site-add">
            <div class="add-info">
            <div class="title">
                <span>{{$t('m.welcome.activateSite')}}</span>
            </div>
            <el-form ref="infoform" :model="form" label-position="left" label-width="250px" >
                <!-- <el-form-item label="Federated Organization" prop="stiename">
                    <span class="info-text">{{form.federatedOrganization}}</span>
                </el-form-item> -->
                <el-form-item :label="$t('m.common.siteName')" prop="stiename">
                    <span class="info-text">{{form.siteName}}</span>
                </el-form-item>
                <el-form-item :label="$t('m.common.institution')" prop="institution">
                    <span class="info-text">{{form.institutions}}</span>
                </el-form-item>
                <el-form-item :label="$t('m.common.role')" prop="role">
                    <span class="info-text">{{form.role | getSiteType}}</span>
                </el-form-item>
                <el-form-item :label="$t('m.common.partyID')">
                    <span class="info-text">{{form.partyId}}</span>
                </el-form-item>
                <el-form-item :label="$t('m.sitemanage.networkEntrances')" prop="entrances">
                <span v-if='form.networkAccessEntrances' class="info-text" style="margin-top: 5px;">
                    <div style="line-height: 30px" v-for="(item, index) in form.networkAccessEntrances.split(';')" :key="index">{{item}}</div>
                </span>
                </el-form-item>
                <el-form-item :label="$t('m.sitemanage.networkExits')" prop="exit">
                    <span v-if='form.networkAccessExits' class="info-text" style="margin-top: 5px;">
                        <div style="line-height: 30px" v-for="(item, index) in form.networkAccessExits.split(';')" :key="index">{{item}}</div>
                    </span>
                </el-form-item>
                <el-form-item label="Federation Key">
                    <span v-if="keyViewDefault" class="info-text">{{form.appKey}} <img src="@/assets/view_show.png" @click="keyViewDefault = !keyViewDefault" class="view" ></span>
                    <span  v-if="!keyViewDefault" class="info-text">***********************<img src="@/assets/view_hide.png" @click="keyViewDefault = !keyViewDefault" class="view" ></span>
                </el-form-item>
                <el-form-item label="Federation Secret">
                    <span v-if="secretViewDefault" class="info-text">{{form.appSecret}} <img src="@/assets/view_show.png" @click="secretViewDefault = !secretViewDefault" class="view" ></span>
                    <span  v-if="!secretViewDefault" class="info-text">***********************<img src="@/assets/view_hide.png" @click="secretViewDefault = !secretViewDefault" class="view" ></span>
                </el-form-item>
                <el-form-item :label="$t('m.sitemanage.registrationLink')">
                    <el-popover
                        placement="top"
                        width="400"
                        trigger="hover"
                        :content="form.registrationLink">
                        <span slot="reference" class="link-text">{{form.registrationLink}}</span>
                    </el-popover>
                </el-form-item>
            </el-form>
            <div class="Submit">
                <el-button type="primary" @click="modifyAction">{{$t('m.welcome.confirmAndActivate')}}</el-button>
            </div>
            </div>
            <el-dialog :visible.sync="confirmdialog" class="site-toleave-dialog" width="700px" :close-on-click-modal="false" :close-on-press-escape="false">
                <i class="el-icon-success"></i>
                <div class="line-text-success">{{$t('m.welcome.activateSuccessfully')}}</div>
                <div class="line-text-one">{{$t('m.welcome.activateSuccessfully')}}</div>
                <div class="dialog-footer">
                    <el-button class="sure-btn" type="primary" @click="confirm">{{$t('m.common.OK')}}</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { register } from '@/api/welcomepage'
import { decode64, utf8to16 } from '@/utils/base64'

export default {
    name: 'home',
    components: {},
    data() {
        return {
            keyViewDefault: false,
            secretViewDefault: false,
            confirmdialog: false, //
            form: {}
        }
    },
    watch: {},
    computed: {
        ...mapGetters(['organization'])
    },

    beforeDestroy() {},
    created() {
        // this.$store.dispatch('selectEnum')
    },
    mounted() {
        let Url = this.$route.query.registerUrl
        if (Url.indexOf('?st') < 0) {
            Url = Url.split('\\n').join('')
            Url = utf8to16(decode64(Url))
        }
        console.log(this.$route.query.registerUrl, 'registerUrl')
        console.log(Url, 'url')
        let newStr = Url.split('st=')[1].replace(new RegExp('\\\\', 'g'), '')
        let obj = { ...JSON.parse(newStr) }
        let fromObj = {}
        fromObj.appKey = obj.secretInfo.key
        fromObj.appSecret = obj.secretInfo.secret
        fromObj.federatedUrl = `${Url.split('//')[0]}//${Url.split('//')[1].split('/')[0]}`
        fromObj.registrationLink = this.$route.query.registerUrl // 回传加密
        fromObj.federatedOrganization = obj.federatedOrganization
        fromObj.id = obj.id
        fromObj.institutions = obj.institutions
        fromObj.networkAccessEntrances = obj.networkAccessEntrances
        fromObj.networkAccessExits = obj.networkAccessExits
        fromObj.partyId = obj.partyId
        fromObj.role = obj.role
        fromObj.siteName = obj.siteName
        this.form = { ...fromObj }
    },
    methods: {

        modifyAction() {
            let data = { ...this.form }
            register(data).then((res) => {
                this.confirmdialog = true
                this.$store.dispatch('setSiteStatus', 'registered')
                console.log('激活成功并注册')
                this.$router.push({
                    name: 'sitemanage',
                    path: 'sitemanage'
                })
            })
        },
        // OK
        confirm() {
            this.$router.push({
                name: 'auto',
                query: {
                    siteName: this.form.siteName,
                    federatedId: this.form.federatedId || this.form.federatedOrganization,
                    partyId: this.form.partyId
                }
            })
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/activate.scss';
</style>
