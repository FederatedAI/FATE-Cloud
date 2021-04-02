<template>
  <div class="prepare">
    <div class="prepare-box">
        <div class="prepare-title">
            <div class="title">
            </div>
            <div class="prepare-form">
                <el-form :model="formInline" label-position="left" size="mini" label-width="225px">
                    <!-- <el-form-item label="Federated organization">
                        <el-select v-model="formInline.federatedId" placeholder=""  @change="changeFederatedId" >
                            <el-option
                                v-for="item in organization"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item> -->
                    <el-form-item label="PartyID">
                        <el-select v-model="formInline.partyId" :class="{'option-select':showwarn}" @change="tochangepartyId" placeholder="">
                            <el-option
                                v-for="item in partyId"
                                :key="item.value"
                                :label="item.label"
                                :disabled="item.deployStatus"
                                :value="item.value">
                            </el-option>
                        </el-select>
                        <div v-if="showwarn" class="option-partyId">The PartyID is required.</div>
                    </el-form-item>
                    <el-form-item label="Site name">
                        <span class="sitename">{{formInline.siteName}}</span>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <div class="middle-title">
            Before your begin
        </div>
        <div class="middle-text">
            To follow this guide, you need:
        </div>
        <div class="middle-box">
            <div class="left">
                <span v-for="(item, index) in itmeList" :key="index">
                    <div>{{item.procName}}</div>
                </span>
            </div>
            <div class="right">
               <span v-for="(item, index) in itmeList" :key="index">
                    <div>{{item.procDesc}}<span style="color:#848C99"> and above</span></div>
                </span>
            </div>
        </div>
        <div class="foot-text">
            Make sure all the above requirements are met and go ahead with the installation.
        </div>
        <div class="foot-btn">
            <el-button type="primary" @click="toDeploy">Start</el-button>
        </div>
    </div>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { pagesStep, deployversion } from '@/api/deploy'
import { getPrepare, toClick } from '@/api/fatedeploy'
export default {
    name: 'prepare',
    data() {
        return {
            showwarn: false,
            formInline: {
                federatedId: parseInt(this.$route.query.federatedId),
                partyId: parseInt(this.$route.query.partyId),
                siteName: this.$route.query.siteName
            },
            itmeList: []
        }
    },
    watch: {

    },
    computed: {
        ...mapGetters(['organization', 'partyId'])
    },
    created() {
        this.$store.dispatch('selectEnum', this.formInline.federatedId)
        this.initi()
    },
    methods: {
        initi() {
            getPrepare().then(res => {
                this.itmeList = [...res.data]
            })
        },
        toDeploy() {
            if (this.formInline.partyId) {
                let data = {
                    federatedId: this.formInline.federatedId,
                    partyId: this.formInline.partyId,
                    productType: 1,
                    clickType: 2
                }
                toClick(data).then(res => {
                    this.$router.push({
                        name: 'deploying',
                        query: {
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            siteName: this.formInline.siteName
                        }
                    })
                })
            } else {
                this.showwarn = true
            }
        },
        // changeFederatedId(data) {
        //     this.$store.dispatch('selectEnum', data)
        //     this.formInline.partyId = ''
        //     this.formInline.siteName = ''
        // },
        tochangepartyId() {
            this.showwarn = false
            this.partyId.forEach(element => {
                if (element.value === this.formInline.partyId) {
                    this.formInline.siteName = element.text
                }
            })
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId
            }
            deployversion(data).then(res => {
                this.formInline.fateVersion = res.data
                // 获取步骤
                this.topagesStep()
            })
        },
        topagesStep() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            // 获取步骤
            pagesStep(data).then(res => {
                if (res.data.pageStatus.code === 0) {
                    this.$router.push({
                        name: 'auto',
                        query: {
                            siteName: this.formInline.siteName,
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                } else if (res.data.pageStatus.code === 1) {
                    this.$router.push({
                        name: 'prepare',
                        query: {
                            siteName: this.formInline.siteName,
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                } else if (res.data.pageStatus.code === 2) {
                    this.$router.push({
                        name: 'deploying',
                        query: {
                            siteName: this.formInline.siteName,
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                } else {
                    this.$router.push({
                        name: 'service',
                        query: {
                            siteName: this.formInline.siteName,
                            federatedId: this.formInline.federatedId,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion
                        }
                    })
                }
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/prepare.scss';
</style>
