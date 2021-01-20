package com.webank.ai.fatecloud.system;

import com.alibaba.fastjson.JSONObject;


import com.webank.ai.fatecloud.common.EncryptUtil;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class NeptuneServiceTest {

//    @Test
//    public void testSdk() throws IOException {
//        Fatecloud fatecloud = new Fatecloud();
//        Integer partyId = 10002;
//        String result = fatecloud.getSecretInfo("http://***REMOVED***:9091/fate-manager/api/site/secretinfo", String.valueOf(partyId));
//        System.out.println(result);
//
//        JSONObject data = JSONObject.parseObject(result);
//        String secret = data.getJSONObject("data").getString("appSecret");
//        String key = data.getJSONObject("data").getString("appKey");
//        String role = data.getJSONObject("data").getString("role").equals("Guest") ? "1":"2";
//        String time = String.valueOf(System.currentTimeMillis());
//        String uuid = UUID.randomUUID().toString();
//        String nonce = uuid.replaceAll("-", "");
//        String httpURI = "/cloud-manager/api/site/rollsite/checkPartyId";
//        String body ="";
//        String trueSignature = NeptuneServiceTest.generateSignature(secret, String.valueOf(partyId), role, key, time, nonce, httpURI, body);
//
//        HashMap<String, String> heads = new HashMap<>();
//        heads.put("TIMESTAMP", time);
//        heads.put("PARTY_ID", String.valueOf(partyId));
//        heads.put("NONCE", nonce);
//        heads.put("ROLE", role);
//        heads.put("APP_KEY", key);
//        heads.put("URI", httpURI);
//        heads.put("SIGNATURE", trueSignature);
//        boolean ret = fatecloud.checkPartyId("http://172.16.153.21:8999/cloud-manager/api/site/rollsite/checkPartyId", heads,body);
//        System.out.println(ret);
//    }

    public static void main (String[] args){

        String trueSignature = NeptuneServiceTest.generateSignature("8e90be2d063045f10d317afe6f5678d520491f80", String.valueOf(10002), "2", "7VP0rbru", "1611122751925", "6c91e1e37acc40889b25e9079c63a242", "/cloud-manager/api/site/rollsite/checkPartyId", "");
        String aaaaaaaaaaaaaaaaaaaa = EncryptUtil.generateSignature("8e90be2d063045f10d317afe6f5678d520491f80", String.valueOf(10002), "2", "7VP0rbru", "1611122751925", "6c91e1e37acc40889b25e9079c63a242", "/cloud-manager/api/site/rollsite/checkPartyId", "");

    }

    private static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(UTF8);
        SecretKey secretKey = new SecretKeySpec(data, HMACSHA1);
        Mac mac = Mac.getInstance(HMACSHA1);
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(UTF8);
        return mac.doFinal(text);
    }

    public static String generateSignature(String appSecret, String... encryptParams) {
        try {
            String encryptText = "";
            for (int i = 0; i < encryptParams.length; i++) {
                if (i != 0) {
                    encryptText += "\n";
                }
                encryptText += encryptParams[i];
            }
            encryptText = new String(encryptText.getBytes(), NeptuneServiceTest.UTF8);

            return Base64.getEncoder().encodeToString(NeptuneServiceTest.HmacSHA1Encrypt(encryptText, appSecret));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String UTF8 = "UTF-8";
    private static final String HMACSHA1 = "HmacSHA1";
}
