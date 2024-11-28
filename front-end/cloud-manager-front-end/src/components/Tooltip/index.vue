<template>
  <div class="tooltip" :style="elmwidth">
        <el-tooltip effect="dark" v-if="tooltip"  :placement="placement">
            <div slot="content" >{{content}}</div>
            <span ref="name">
                {{content}}
            </span>
        </el-tooltip>
        <span v-else ref="name">
            {{content}}
        </span>
  </div>
</template>

<script>

export default {
    name: 'OverflowTooltip',
    props: {
        width: {
            type: String,
            default: ''
        },
        content: {
            type: String,
            default: ''
        },
        placement: {
            type: String,
            default: 'top'
        }
    },
    data() {
        return {
            tooltip: false,
            elmwidth: ''
        }
    },
    created() {

    },
    updated() {
        this.updatawidth()
    },
    mounted() {
        this.elmwidth = `width:${this.width}`
        this.updatawidth()
    },
    methods: {
        updatawidth() {
            let width = this.$refs.name.offsetWidth
            if (width > parseInt(this.width)) {
                this.tooltip = true
            } else {
                this.tooltip = false
            }
        }
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
    .tooltip{
        // font-size: 14px;
        white-space: nowrap;
        text-overflow:ellipsis;
        overflow:hidden;

    }
    .el-tooltip__popper{
        opacity: .8;
    }

</style>
