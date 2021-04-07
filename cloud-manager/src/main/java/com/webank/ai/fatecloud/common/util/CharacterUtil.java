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
package com.webank.ai.fatecloud.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class CharacterUtil {

    /***
     * Chinese characters to pinyin, special characters to newC
     * @param scStr special characters string
     * @param newC special characters Characters that need to new changed
     * @return pinyin string
     */
    public static String specialCharacters2Pinyin(String scStr, char newC) throws BadHanyuPinyinOutputFormatCombination {
        if (scStr == null || scStr.trim().equals("")) return null;
        char[] chars = scStr.toCharArray();
        StringBuilder pStr = new StringBuilder(chars.length * 3);
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : chars) {
            if (c >= '\u4e00' && c <= '\u9fa5') { //19968 ~ 40869
                pStr.append(PinyinHelper.toHanyuPinyinStringArray(c, format)[0]);
            } else if ((c < 48 || c > 57) && (c < 65 || c > 90) && (c < 97 || c > 122)) {
                pStr.append(newC);
            } else {
                pStr.append(c);
            }
        }
        return pStr.toString();
    }
}
