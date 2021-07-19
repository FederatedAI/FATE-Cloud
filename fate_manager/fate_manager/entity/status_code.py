
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
    NoFoundPartyId = 20002


class SiteStatusCode:
    NoFoundSite = 30001
    PARTY_ID_NOT_EXIST = 30002
    SITE_NOT_JOINED = 30003
    SITE_NOT_ALLOW_UPDATE = 30004


class RequestCloudCode:
    HttpRequestFailed = 30001
    SignatureFailed = 30002 # Signature verification failed


class RequestFateFlowCode:
    HttpRequestFailed = 40001




