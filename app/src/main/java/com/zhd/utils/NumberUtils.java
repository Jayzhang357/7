package com.zhd.utils;

import java.math.BigDecimal;

public class NumberUtils {

    public static double formatDouble(double d){
        BigDecimal b = new BigDecimal(d);
        return b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
