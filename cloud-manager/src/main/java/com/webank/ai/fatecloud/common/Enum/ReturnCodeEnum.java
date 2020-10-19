package com.webank.ai.fatecloud.common.Enum;

public enum ReturnCodeEnum {
    SUCCESS(0, "Success!"),
    REGISTER_ERROR(100, "FederatedOrganization has been registered!"),
    PARAMETERS_ERROR(101, "The input parameters are wrong!"),
    ID_RANGE_ERROR(102, "Some new ID range have been used, please edit again!"),
    PARTYID_ERROR(103, "The PartyID has been used!"),
    GROUP_SET_ERROR(104, "The Group set doesn't exist!"),
    GROUP_DELETE_ERROR(105, "The Group set is used by some sites!"),
    PARTYID_UPDATE__ERROR(106, "The party id doesn't register or  has been activated or removed!"),
    PARTYID_ACTIVATE__ERROR(107, "The party id has been modified!"),
    PARTYID_RANGE__ERROR(108, "The party id is out of the range of group set!"),
    CONNECT_SITE_ERROR(109, "There is an error when connect to the ip!"),
    SYSTEM_ERROR(110, "System error!"),
    AUTHORITY_ERROR(111, "Authority failure!"),
    SITE_STATUS_ERROR(112, "Site doesn't survive now!"),
    SECRET_ERROR(113, "Secret or key doesn't exist in database!"),
    UPDATE_ERROR(114, "The data doesn't exist in database!"),
    PARTYID_FIND_ERROR(115, "The party id doesn't exist in database!"),
    PARTYID_DELETE_ERROR(116, "The party id has been delete in database!"),
    REGISTER_ERROR_2(117, "FederatedOrganization hasn't registered!"),
    GROUP_UPDATE_ERROR(118, "The new party id ranges of the group set don't contain used party id!"),
    CASEID_ERROR(119, "The case_id is needed!"),
    CASEID_IP_DEAL_ERROR(120, "The case_id deal failed!"),
    UPDATE_VERSION_ERROR(121, "Update fate version and serving version error!"),
    SITE_NAME_ERROR(122, "Site name exist!"),
    GROUP_NAME_ERROR(123, "Group name exist!"),
    GROUP_NAME_PARAMETER_ERROR(124, "Group name is null!"),
    GROUP_REGIONS_ERROR(125, "Group range has been used!"),
    AUTHORITY_APPLY_ERROR(126, "Some institutions has been applied!"),
    FUNCTION_ERROR(127, "This function doesn't work!"),
    CLOUD_MANAGER_USER_ERROR(128, "This cloud manager user exists!"),
    CLOUD_MANAGER_PASSWORD_ERROR(129, "This cloud manager user password errors!"),
    CLOUD_MANAGER_LOGIN_ERROR(130, "Please login first!"),
    CLOUD_MANAGER_DELETE_ERROR(131, "The last user can't be deleted!"),
    CLOUD_MANAGER_ORIGIN_USER_LOGIN_ERROR(132, "The user name or password error!"),
    CLOUD_MANAGER_LOGIN_AUTHORITY_ERROR(133, "The user doesn't get the authority!"),
    FATE_MANAGER_USER_ERROR(134, "The institution has been used!"),
    MEMORY_ERROR(135, "Memory has error!"),
    CANCEL_ERROR(136, "Institutions to cancel authority doesn't exist!"),

    ;
    private int code;
    private String message;

    ReturnCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
