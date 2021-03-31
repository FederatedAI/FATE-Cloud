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

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Slf4j
public class EncryptUtil {

    public static final String UTF8 = "UTF-8";
    private static final String HMACSHA1 = "HmacSHA1";

    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
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
            encryptText = new String(encryptText.getBytes(), EncryptUtil.UTF8);

            return Base64.getEncoder().encodeToString(EncryptUtil.HmacSHA1Encrypt(encryptText, appSecret));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    private static sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();

    /**
     * base64 encode
     *
     * @param oldStr
     * @return
     */
    public static String encode(String oldStr) throws UnsupportedEncodingException {
        return encoder.encode(oldStr.getBytes("UTF-8"));
    }

    /**
     * base64 decode
     * @param oldStr
     * @return
     * @throws IOException
     */
    public static String decode(String oldStr) throws IOException {
        return new String(decoder.decodeBuffer(oldStr));
    }


}
