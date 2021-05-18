<template>
    <div class="deployansible">
        <div class="deploying-box">
            <div class="serve-title">
                <div class="inline">
                <el-form :inline="true" :model="formInline" class="demo-form-inline" size="mini">
                    <el-form-item label="PartyID:">
                    <el-select v-model="formInline.partyId" placeholder="" @change="tochangepartyId" :class="{'option-select':showwarn}">
                        <el-option
                            v-for="item in partyId"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                    <div v-if="showwarn" class="option-partyId">The PartyID is required.</div>
                    </el-form-item>
                    <el-form-item label="Site name:" class="form-item">
                        <span style="color:#217AD9">{{formInline.siteName}}</span>
                    </el-form-item>
                </el-form>
                </div>
            </div>
            <!-- <prepare v-if='page === 1' /> -->
            <div class="steps-content">
                <!-- 第三步 -->
                <div @click="toSteps(4)" :class="{'define':true,'hover':currentSteps.acquisiPrepare,'activa':currentSteps.acquisiFinish}">
                    <div class="steps" >
                        <span class="text circul">1</span>
                        <span class="text">FATE package acquisition</span>
                    </div>
                    <i class="el-icon-caret-right"></i>
                </div>
                <!-- 第四步 -->
                <div @click="toSteps(5)" :class="{'define':true ,'hover':currentSteps.instllPrepare,'activa':currentSteps.instllFinish}">
                    <div class="steps-two steps" >
                        <span class="circul">2</span>
                        <span class="text">FATE installation</span>
                    </div>
                     <i class="el-icon-caret-right"></i>
                </div>
                <!-- 第五步 -->
                <div @click="toSteps(6)" :class="{'define':true ,'hover':currentSteps.autoPrepare,'activa':currentSteps.autoFinish}">
                    <div class="steps-three steps" >
                        <span class=" circul">3</span>
                        <span class="text">Auto-test</span>
                    </div>
                </div>
            </div>
            <!-- <system ref="system" v-show="page === 2"  :page='page' :currentpage='currentpage' :currentSteps='currentSteps' :formInline='formInline' /> -->
            <!-- <ansible ref="ansible" v-show="page === 3" :page='page' :currentpage='currentpage'  :currentSteps='currentSteps' :formInline='formInline' /> -->
            <fatepackage ref="fatepackage" v-show="page === 4" :page='page' :currentpage='currentpage'  :currentSteps='currentSteps' :formInline='formInline' />
            <tableinstall ref="tableinstall" v-show="page === 5" :page='page' :currentpage='currentpage'  :currentSteps='currentSteps' :formInline='formInline'/>
            <tableauto ref="tableauto" v-show="page === 6" :page='page' :currentpage='currentpage'  :currentSteps='currentSteps' :formInline='formInline'/>
        </div>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
// import prepare from './prepare'
// import system from './system'
// import ansible from './ansible'
import fatepackage from './fatepackage'
import tableinstall from './tableinstall'
import tableauto from './tableauto'
import { pagesStep } from '@/api/deploy'
export default {
    name: 'deployansible',
    components: { fatepackage, tableinstall, tableauto },
    data() {
        return {
            showwarn: false,
            page: 0, // 当前显示页(点击可切换)
            currentpage: 0, // 当前进行到第几步
            currentSteps: {
                systemPrepare: false,
                systemFinish: false,
                ansiblePrepare: false,
                ansibleFinish: false,
                acquisiPrepare: false,
                acquisiFinish: false,
                instllPrepare: false,
                instllFinish: false,
                autoPrepare: false,
                autoFinish: false
            },
            formInline: {
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
        this.togetpages()
    },
    methods: {
        tochangepartyId() {
            this.showwarn = false
            // 替换sitename
            this.partyId.forEach(element => {
                if (element.value === this.formInline.partyId) {
                    this.formInline.siteName = element.text
                }
            })
            this.togetpages()
        },
        // 当前页步骤
        togetpages() {
            this.currentSteps = {
                acquisiPrepare: false,
                acquisiFinish: false,
                instllPrepare: false,
                instllFinish: false,
                autoPrepare: false,
                autoFinish: false
            }
            let data = {
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
                case 2 :
                    this.page = this.currentpage = res.data.pageStatus.code
                    if (res.data.pageStatus.code === 4) {
                        this.currentSteps.acquisiPrepare = true
                    } else if (res.data.pageStatus.code === 5) {
                        this.currentSteps.acquisiFinish = true
                        this.currentSteps.instllPrepare = true
                    } else if (res.data.pageStatus.code === 6) {
                        this.$refs.fatepackage.disReacquire = false // 第三步后不能选择版本
                        this.currentSteps.acquisiFinish = true
                        this.currentSteps.instllFinish = true
                        this.currentSteps.autoPrepare = true
                    } else if (res.data.pageStatus.code === 7) {
                        this.$router.push({
                            name: 'service',
                            query: {
                                siteName: this.formInline.siteName,
                                partyId: this.formInline.partyId,
                                fateVersion: this.formInline.fateVersion,
                                deployType: this.formInline.deployType
                            }
                        })
                    }
                    break
                }
            })
        },
        // 点击前往第几步
        toSteps(step) {
            if (step <= this.currentpage) {
                this.page = step
            }
        },
        getAutoTest() {
            this.$refs['tableauto'].lessTime()
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/deployansible.scss';
</style>
