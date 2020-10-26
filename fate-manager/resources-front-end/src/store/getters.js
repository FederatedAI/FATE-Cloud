const getters = {
    siteStatus: state => state.user.siteStatus,
    role: state => state.user.role,
    userId: state => state.user.userId,
    userName: state => state.user.userName,
    permissionList: state => state.user.permissionList,
    organization: state => state.app.organization,
    partyId: state => state.app.partyId,
    version: state => state.app.version,
    autoState: state => state.app.autoState,
    siteState: state => state.app.siteState
}
export default getters
