const getters = {
    siteName: state => state.user.siteName,
    getInfo: state => state.user.getInfo,
    loginName: state => state.user.loginName,
    siteStatus: state => state.user.siteStatus,
    sidebar: state => state.app.sidebar,
    sitestatus: state => state.app.sitestatus,
    autostatus: state => state.app.autostatus
}
export default getters
