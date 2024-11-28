
class SuccessStatusCode:
    SUCCESS = 0


class ParameterStatusCode:
    MissingRequestParameters = 10010


class UserStatusCode:
    NoFoundUser = 20001
    NoFoundAccount = 20002
    LoginFailed = 20003  # username or password error
    AccountRoleLow = 20004
    NoFoundToken = 20005
    TokenExpired = 10066
    NoFoundFederated = 20007
    AddUserFailed = 20008
    CheckUserFailed = 20009
    DeleteUserFailed = 20010
    EditUserFailed = 20011
    NoFoundPartyId = 20012
    UserStatusDelete = 20013


class SiteStatusCode:
    NoFoundSite = 30001
    PARTY_ID_NOT_EXIST = 30002
    SITE_NOT_JOINED = 30003
    SITE_NOT_ALLOW_UPDATE = 30004
    PARTY_ID_HAS_EXIST = 30005


class RequestCloudCode:
    HttpRequestFailed = 30001
    SignatureFailed = 30002 # Signature verification failed
    InstitutionDelete = 30003


class RequestFateFlowCode:
    HttpRequestFailed = 40001


class InstitutionStatusCode:
    Institution_Not_Allow_Activate = 30001


class FateFlowInfoStatusCode:
    FateFlow_Connect_Fail = 30001


class RollsiteInfoStatusCode:
    Write_RollSite_Fail = 30001




