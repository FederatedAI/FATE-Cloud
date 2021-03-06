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
package com.webank.ai.fatecloud.common.Enum;

public enum FateVersionEnum  {

    V_1_3_0("1.3.0"),
    V_1_3_1("1.3.1"),
    V_1_4_0("1.4.0"),
    V_1_4_1("1.4.1"),
    V_1_4_2("1.4.2"),
    V_1_4_3("1.4.3"),
    V_1_4_4("1.4.4"),
    V_1_4_5("1.4.5"),
    V_1_4_6("1.4.6"),
    V_1_5_0("1.5.0"),
    ;

    public String getVersion() {
        return version;
    }

    FateVersionEnum(String version){
        this.version = version;
    }

    private String version;

}
