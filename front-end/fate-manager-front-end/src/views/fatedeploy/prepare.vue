<template>
  <div class="prepare">
    <div class="prepare-box">
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

import { getPrepare, toClick } from '@/api/fatedeploy'
export default {
    name: 'prepare',
    props: {
        formInline: {
            type: Object,
            default: function () {
                return {}
            }
        },
        currentSteps: {
            type: Object,
            default: function () {
                return { }
            }
        }
    },
    data() {
        return {
            itmeList: []
        }
    },
    watch: {

    },
    computed: {

    },
    created() {
        this.initi()
    },
    methods: {
        initi() {
            getPrepare().then(res => {
                this.itmeList = [...res.data]
            })
        },
        toDeploy() {
            let data = {
                federatedId: this.formInline.federatedId,
                partyId: this.formInline.partyId,
                productType: 1,
                clickType: 2
            }
            toClick(data).then(res => {
                this.$parent.page = 2
                this.$router.push({
                    name: 'deploying',
                    query: {
                        federatedId: this.formInline.federatedId,
                        partyId: this.formInline.partyId,
                        siteName: this.formInline.siteName
                    }
                })
            })
        }

    }
}
</script>

<style rel="stylesheet/scss" lang="scss" >
@import 'src/styles/prepare.scss';
</style>
