package com.webank.ai.fate.manager.utils;

import com.webank.ai.fate.manager.dao.entity.NodeDo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CommUtil {

    public static Map<String, String> getHeaderInfo(NodeDo nodeDo, String body, String uri) {
        HashMap<String, String> head = new HashMap<String, String>();
        String time = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString();
        String nonce = uuid.replaceAll("-", "");
        String signature = EncryptUtil.generateSignature(nodeDo.getAppSecret(), nodeDo.getPartyId(), String.valueOf(nodeDo.getRoleType()), nodeDo.getAppKey(), time, nonce, uri, body);
        head.put("TIMESTAMP", time);
        head.put("SIGNATURE", signature);
        head.put("PARTY_ID", nodeDo.getPartyId());
        head.put("NONCE", nonce);
        head.put("ROLE", String.valueOf(nodeDo.getRoleType()));
        head.put("APP_KEY", nodeDo.getAppKey());

        return head;
    }
}
