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

public enum FateServingVersionEnum  {

    V_1_2_0("1.2.0"),
    V_1_2_1("1.2.1"),
    V_1_2_2("1.2.2"),
    V_1_2_3("1.2.3"),
    V_1_2_4("1.2.4"),
    V_1_2_5("1.2.5"),
    V_1_3_0("1.3.0"),
    V_1_3_1("1.3.1"),
    V_1_3_2("1.3.2"),
    V_1_3_3("1.3.3"),
    V_2_0_0("2.0.0"),
    V_2_0_1("2.0.1"),
    V_2_0_2("2.0.2"),
    V_2_0_3("2.0.3"),
    ;

    public String getVersion() {
        return version;
    }

    FateServingVersionEnum(String version){
        this.version = version;
    }

    private String version;

}
