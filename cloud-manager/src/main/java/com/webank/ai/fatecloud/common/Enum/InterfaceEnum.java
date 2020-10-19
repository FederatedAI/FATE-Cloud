package com.webank.ai.fatecloud.common.Enum;

import java.io.Serializable;

public interface InterfaceEnum<T extends Serializable>{
    T getValue();
}
