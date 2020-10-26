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
