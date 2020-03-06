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
package com.webank.ai.fate.manager.common.enums;

public enum NodeStatusEnum implements InterfaceEnum{

    UNKONWN(-1, "unknown"),
    NODE_NO_JOIN(1, "not join"),
    NODE_JOIN(2, "joined"),
    NODE_REMOVE(3, "remove");

    NodeStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public static NodeStatusEnum getEnumByType(Integer codeVaule) {
        if (null == codeVaule) {
            return UNKONWN;
        }
        for (NodeStatusEnum ele : NodeStatusEnum.values()) {
            if (ele.getValue().equals(codeVaule)) {
                return ele;
            }
        }
        return UNKONWN;
    }

    @Override
    public Integer getValue() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
