/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.ai.fatecloud.common;

import java.util.ArrayList;
import java.util.List;

public class Dict {
    public static final String KEY = "key";
    public static final String SECRET = "secret";
    public static final String SIGNATURE = "SIGNATURE";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String NONCE = "NONCE";
    public static final String ROLE = "ROLE";
    public static final String VERSION = "VERSION";

    public static final String PARTY_ID = "PARTY_ID";
    public static final String APP_KEY = "APP_KEY";
    public static final String HTTP_URI = "HTTP_URI";
    public static final String HTTP_BODY = "HTTP_BODY";
    public static final String HTTP_HEADER = "HTTP_HEADER";
    public static final String FATE_MANAGER_ID = "FATE_MANAGER_ID";

    public static final String CLOUD_USER = "cloud_user";

    public static final String[] FUNCTIONS = {"Auto-Deploy", "Site-Authorization"};

    public static final String FATE_MANAGER_USER = "fate_manager_user";
    public static final String FATE_SITE_USER = "fate_site_user";
    public static final String FATE_MANAGER_USER_APP_KEY = "FATE_MANAGER_APP_KEY";
    public static final String FATE_MANAGER_USER_ID = "FATE_MANAGER_USER_ID";
    public static final String FATE_MANAGER_USER_NAME = "FATE_MANAGER_USER_NAME";

    public static final List<String> ORDER_FIELDS = new ArrayList<>();

    static {

        Dict.ORDER_FIELDS.add("party_id");
        Dict.ORDER_FIELDS.add("site_name");
        Dict.ORDER_FIELDS.add("institutions");
        Dict.ORDER_FIELDS.add("create_time");
    }
}
