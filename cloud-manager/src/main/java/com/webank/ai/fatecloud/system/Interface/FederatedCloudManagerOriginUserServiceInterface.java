package com.webank.ai.fatecloud.system.Interface;

public interface FederatedCloudManagerOriginUserServiceInterface {

    boolean checkFederatedCloudManagerOriginUser(String username, String password);
    boolean findFederatedCloudManagerOriginUser(String username);

}
