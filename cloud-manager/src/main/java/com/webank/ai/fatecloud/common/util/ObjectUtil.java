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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ObjectUtil {

    public static boolean isEmpty(Object... args) {
        if (args == null) return true;
        for (Object o : args) {
            if (o == null || o.toString().trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    public static boolean noEmpty(Object... args) {
        return !isEmpty(args);
    }

    public static boolean equals(Object object, Object target) {
        if (object != null && target != null) {
            return object == target || object.equals(target);
        }

        return false;
    }

    public static boolean matchNetworkAddress(String address) {
        if (address == null) return false;
        //return address.matches("[\\d]{1,3}(\\.[\\d]{1,3}){3}:[0-9]{1,5}");
        return address.matches("[0-9a-zA-Z.*-]+:[0-9]{1,5}");
    }

    public static boolean matchNetworkAddressNew(String address) {
        if (address == null) return false;
        //return address.matches("([\\d]{1,3}(\\.[\\d]{1,3}){3}:[0-9]{1,5};)+");
        return address.matches("([0-9a-zA-Z.*-]+:[0-9]{1,5};)+");
    }

    /**
     * encapsulate the elements in the Collection into a map of <element field value, element>
     *
     * @param function element::getFiled
     * @param objects  element collection
     * @param <Field>  field type
     * @param <Object> element
     * @return Map<element field value, element>
     */
    public static <Field, Object> Map<Field, Object> toMap(Function<? super Object, ? extends Field> function, Collection<Object> objects) {
        Map<Field, Object> objectMap = new HashMap<>();
        for (Object object : objects) {
            Field field = function.apply(object);
            objectMap.put(field, object);
        }
        return objectMap;
    }

    /***
     * format json string
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        jsonStr = jsonStr.replace("\\n", "");
        StringBuilder sb = new StringBuilder();
        char last;
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                case ' ':
                    if (',' != jsonStr.charAt(i - 1)) {
                        sb.append(current);
                    }
                    break;
                case '\\':
                    sb.append("\\");
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }
    }

    public static <Object> Object notNull(Object object, Object transfer) {
        return object == null ? transfer : object;
    }
}
