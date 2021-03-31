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

import java.text.SimpleDateFormat;
import java.util.Random;

public class CaseIdUtil {

    private static SimpleDateFormat orderIdformat =  new SimpleDateFormat( "yyyyMMddHHmmss" );

    private static Random random = new Random();

    public static String generateCaseId(){
        int randomNum = random.nextInt(1000);
        String orderId = orderIdformat.format(System.currentTimeMillis()) + String.format("%3d", randomNum).replace(" ", "0");
        System.out.println("orderId is " + orderId);
        return orderId;
    }
}
