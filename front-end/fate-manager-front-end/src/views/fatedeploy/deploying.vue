<template>
  <div class="deploying">
    <div class="deploying-box">
        <div class="serve-title">
            <div class="inline">
                <el-form :inline="true" :model="formInline" class="demo-form-inline" size="mini">
                    <el-form-item label="PartyID:">
                        <el-select v-model="formInline.partyId" placeholder="" @change="tochangepartyId">
                            <el-option
                                v-for="item in partyId"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="Site name:" class="form-item">
                        <span style="color:#217AD9">{{formInline.siteName}}</span>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <div v-if="page!==1" class="steps-content">

            <!-- 第一步 -->
            <div @click="toSteps('1')" :class="{'define':true,'hover':currentSteps.pullPrepare,'activa':currentSteps.pullFinish}">
                <div class="steps" >
                    <span class="text circul">1</span>
                    <span class="text">Pull image</span>
                </div>
                <i class="el-icon-caret-right"></i>
            </div>
            <!-- 第二步 -->
            <div @click="toSteps('2')" :class="{'define':true ,'hover':currentSteps.instllPrepare,'activa':currentSteps.instllFinish}">
                <div class="steps-two steps" >
                    <span class="circul">2</span>
                    <span class="text">Install image</span>
                </div>
                <i class="el-icon-caret-right"></i>
            </div>
            <!-- 第三步 -->
            <div @click="toSteps('3')" :class="{'define':true ,'hover':currentSteps.autoPrepare,'activa':currentSteps.autoFinish}">
                <div class="steps-three steps" >
                    <span class=" circul">3</span>
                    <span class="text">Auto-test</span>
                </div>
            </div>
        </div>
        <prepare ref="prepare" v-show="page === 1" :currentSteps='currentSteps' :formInline='formInline' />
        <tablepull ref="tablepull" v-show="page === 2" :currentSteps='currentSteps' :formInline='formInline' />
        <tableinstall ref="tableinstall" v-show="page === 3" :currentSteps='currentSteps' :formInline='formInline'/>
        <tableauto ref="tableauto" v-show="page === 4"  :currentSteps='currentSteps' :formInline='formInline'/>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import prepare from './prepare'
import tablepull from './tablepull'
import tableinstall from './tableinstall'
import tableauto from './tableauto'
import { pagesStep, deployversion } from '@/api/deploy'
export default {
    name: 'deploying',
    components: { prepare, tablepull, tableinstall, tableauto },
    data() {
        return {
            showwarn: false,
            page: 1, // 当前页
            currentpage: 2, // 当前进行到第几步
            currentSteps: {
                pullPrepare: false,
                pullFinish: false,
                instllPrepare: false,
                instllFinish: false,
                autoPrepare: false,
                autoFinish: false
            },
            formInline: {
                federatedId: parseInt(this.$route.query.federatedId),
                partyId: parseInt(this.$route.query.partyId),
                siteName: this.$route.query.siteName,
                fateVersion: this.$route.query.fateVersion,
                deployType: this.$route.query.deployType// 部署类型
            }
        }
    },
    computed: {
        ...mapGetters(['organization', 'partyId', 'version'])
    },
    created() {
        this.$store.dispatch('selectEnum', this.formInline.federatedId)
    },
    mounted() {
        let data = {
            federatedId: this.formInline.federatedId,
            partyId: this.formInline.partyId
        }
        deployversion(data).then(res => {
            if (res.data) {
                this.formInline.fateVersion = res.data
                this.$refs.tablepull.disVersion = true
            }
            this.togetpages()
        })
    },

    methods: {

        // 下拉改变federatedId
        // changeFederatedId(data) {
        //     this.$store.dispatch('selectEnum', data)
        //     this.formInline.partyId = ''
        // },
        tochangepartyId() {
            this.showwarn = false
            this.formInline.fateVersion = '' // 清空版本号
            this.$refs.tablepull.disVersion = false
            // 替换sitename
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
                if (res.data) {
                    this.formInline.fateVersion = res.data
                    this.$refs.tablepull.disVersion = true
                }
                this.togetpages()
            })
        },
        // 当前页步骤
        togetpages() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1
            }
            pagesStep(data).then(res => {
                if (res.data.pageStatus.code === 0) {
                    this.$router.push({
                        name: 'auto',
                        query: {
                            siteName: this.formInline.siteName,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion,
                            deployType: this.formInline.deployType
                        }
                    })
                }
                switch (res.data.deployType.code) {
                case 1 :
                    this.currentpage = this.page = res.data.pageStatus.code
                    if (res.data.pageStatus.code === 0) {
                        this.$router.push({
                            name: 'auto',
                            query: {
                                siteName: this.formInline.siteName,
                                federatedId: this.formInline.federatedId,
                                partyId: this.formInline.partyId,
                                fateVersion: this.formInline.fateVersion,
                                deployType: this.formInline.deployType
                            }
                        })
                    } else if (res.data.pageStatus.code === 1) {
                        this.page = 1
                    // this.$router.push({
                    //     name: 'prepare',
                    //     query: {
                    //         siteName: this.formInline.siteName,
                    //         federatedId: this.formInline.federatedId,
                    //         partyId: this.formInline.partyId,
                    //         fateVersion: this.formInline.fateVersion
                    //     }
                    // })
                    } else if (res.data.pageStatus.code === 2) {
                        this.currentSteps = {
                            pullPrepare: false,
                            pullFinish: false,
                            instllPrepare: false,
                            instllFinish: false,
                            autoPrepare: false,
                            autoFinish: false
                        }
                        this.$refs['tablepull'].lessTime()
                    } else if (res.data.pageStatus.code === 3) {
                        this.$nextTick(() => {
                            this.$refs['tableinstall'].lessTime()
                            this.$refs['tablepull'].initiPullList()
                        })
                        this.currentSteps = {
                            pullPrepare: true,
                            pullFinish: true,
                            instllPrepare: true,
                            instllFinish: false,
                            autoPrepare: false,
                            autoFinish: false
                        }
                    } else if (res.data.pageStatus.code === 4) {
                        this.$nextTick(() => {
                            this.$refs['tableauto'].lessTime()
                            this.$refs['tablepull'].initiPullList()
                            this.$refs['tableinstall'].initiInstallList()
                        })
                        this.currentSteps = {
                            pullPrepare: true,
                            pullFinish: true,
                            instllPrepare: true,
                            instllFinish: true,
                            autoPrepare: true,
                            autoFinish: false
                        }
                    } else if (res.data.pageStatus.code === 5) {
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
                    break
                case 2 :
                    this.$router.push({
                        name: 'service',
                        query: {
                            siteName: this.formInline.siteName,
                            partyId: this.formInline.partyId,
                            fateVersion: this.formInline.fateVersion,
                            deployType: this.formInline.deployType
                        }
                    })
                    break
                }
            })
        },
        // 点击前往第几步
        toSteps(step) {
            if (step === '1' && this.currentSteps.pullPrepare) {
                this.page = 2
                if (this.currentpage !== this.page) {
                    this.$refs['tablepull'].btntype = 'info'
                    this.$refs['tablepull'].distopull = true
                }
            } else if (step === '2' && this.currentSteps.instllPrepare) {
                this.page = 3
                if (this.currentpage !== this.page) {
                    this.$refs['tableinstall'].btntype = 'info'
                }
            } else if (step === '3' && this.currentSteps.autoPrepare) {
                this.page = 4
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/deploying.scss';
</style>
