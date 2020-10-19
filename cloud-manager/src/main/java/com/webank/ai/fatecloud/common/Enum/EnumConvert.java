package com.webank.ai.fatecloud.common.Enum;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.collect.Lists;
import com.webank.ai.fatecloud.common.DropDownConstants;
import com.webank.ai.fatecloud.common.IdNamePair;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class EnumConvert {

    public Map<String, Object> all() throws ReflectiveOperationException {
        HashMap<String, Object> dropDowns = new HashMap<>();
        String role = toJson(RoleTypeEnum.class);
        String fateVersion = toJson(FateVersionEnum.class);
        String fateServingVersion = toJson(FateServingVersionEnum.class);
        String siteStatus = toJson(SiteStatusEnum.class);
        String groupStatus = toJson(GroupSetStatusEnum.class);
        JSONArray roleObjects = JSON.parseArray(role);
        JSONArray fateVersionObjects = JSON.parseArray(fateVersion);
        JSONArray fateServingVersionObjects = JSON.parseArray(fateServingVersion);
        JSONArray siteStatusObjects = JSON.parseArray(siteStatus);
        JSONArray groupStatusObjects = JSON.parseArray(groupStatus);
        dropDowns.put(DropDownConstants.ROLE_TYPE,roleObjects);
        dropDowns.put(DropDownConstants.FATE_VERSION,fateVersionObjects);
        dropDowns.put(DropDownConstants.FATE_SERVING_VERSION,fateServingVersionObjects);
        dropDowns.put(DropDownConstants.SITE_STATUS,siteStatusObjects);
        dropDowns.put(DropDownConstants.GROUP_SET_STATUS,groupStatusObjects);
        return dropDowns;
    }

    public String toJson(Class<? extends Enum> enumClass) throws ReflectiveOperationException {
        Method methodValues = enumClass.getMethod("values");
        Object invoke = methodValues.invoke(null);
        int length = java.lang.reflect.Array.getLength(invoke);
        List<Object> values = new ArrayList<Object>();
        for (int i=0; i<length; i++) {
            values.add(java.lang.reflect.Array.get(invoke, i));
        }

        SerializeConfig config = new SerializeConfig();
        config.configEnumAsJavaBean(enumClass);

        return JSON.toJSONString(values,config);
    }

}

