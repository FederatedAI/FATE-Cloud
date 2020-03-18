<template>
  <div class="login-container">
    <div class="header">
      <div class="logo">
        <span style="font-weight: 800;">Fate</span>
        <span>Cloud</span>
      </div>
    </div>
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      label-position="left"
    >
      <div class="title">Fate Cloud Manager</div>
      <el-tabs v-model="activeName" class="tabs" @tab-click="handleClick">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            name="username"
            type="text"
            placeholder="username"
            auto-complete="off"
            @keyup.enter.native="handleLogin"
          >
            <i slot="prefix" class="el-icon-s-custom acct" />
            <i slot="suffix" class="view" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            :type="pwdType"
            v-model="loginForm.password"
            name="password"
            placeholder="password"
            auto-complete="off"
            @keyup.enter.native="handleLogin"
          >
            <i slot="prefix" class="el-icon-lock acct" />
            <i slot="suffix" class="el-icon-view view" @click="showPwd" />
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            :loading="loading"
            type="primary"
            style="width:100%;"
            @click.native.prevent="handleLogin"
          >Login</el-button>
        </el-form-item>
      </el-tabs>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      const name = value.trim()
      if (name.length === 0) {
        callback(new Error('Please input username'))
      } else {
        callback()
      }
    }
    const validatePass = (rule, value, callback) => {
      if (value.length < 5) {
        callback(new Error('Password must be lager'))
      } else {
        callback()
      }
    }
    return {
      activeName: 'second',
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, trigger: 'blur', validator: validateUsername }
        ],
        password: [{ required: true, trigger: 'blur', validator: validatePass }]
      },
      loading: false,
      pwdType: 'password',
      redirect: '/view/federalSite'
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    showPwd() {
      if (this.pwdType === 'password') {
        this.pwdType = ''
      } else {
        this.pwdType = 'password'
      }
    },
    // 切换
    handleClick(tab, event) {
      // console.log(tab, event)
    },
    // 登入
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store
            .dispatch('Login', this.loginForm)
            .then(() => {
              this.loading = false
              this.$router.push({ path: this.redirect || '/federalSite/index' })
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login-container {
	position: relative;
	min-width: 1440px;
	height: 100%;
  background-color: #fff;
  .header{
    padding: 0;
    background-color: #2D3A4B;
    color: #fff;
    height: 75px !important;
    line-height: 75px;
    border-bottom:1px  solid #2D3A4B;
    .logo {
      // cursor: pointer;
      position: absolute;
      left: 20px;
      font-size: 22px;
    }
  }
	.title {
		font-size: 28px;
		font-weight: 800;
		text-align: center;
		margin: 25px 0px;
	}
	.tabs {
		width: 80%;
		margin-left: 50px;
		.el-tabs__active-bar {
			background-color: #e4e7ed;
		}
	}
	.login-form {
		position: absolute;
		top: calc(40% - 200px);
		left: calc(50% - 250px);
		width: 500px;
		height: 400px;
		box-shadow: rgb(214, 214, 214) 0px 4px 21px;
		border-radius: 5px;
		.el-input {
			// margin-left: 50px;
			// width: 80%;
			.view {
				cursor: pointer;
				margin-right: 5px;
			}
			.acct {
				margin-left: 5px;
			}
		}
	}
	.el-button {
		margin-top: 50px;
		display: table;
		margin: 0 auto;
	}
	.logon {
		display: inline-block;
		position: absolute;
		left: 50px;
	}
	.forgetpsd {
		position: absolute;
		right: 50px;
		display: inline-block;
	}
}
</style>

