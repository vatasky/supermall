package com.supermall.util;

import java.math.BigDecimal;

/**
 * @author
 */
public class BigDecimalUtil {

    private BigDecimalUtil(){

    }
    /**
     * 加法
     */
    public static BigDecimal add(double a1,double a2){
        BigDecimal b1 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(a2));
        return b1.add(b2);
    }
    /**
     * 减法
     */
    public static BigDecimal sub(double a1,double a2){
        BigDecimal b1 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(a2));
        return b1.subtract(b2);
    }
    /**
     * 乘法
     */
    public static BigDecimal mul(double a1,double a2){
        BigDecimal b1 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(a2));
        return b1.multiply(b2);
    }
    /**
     * 除法
     */
    public static BigDecimal div(double a1,double a2){
        BigDecimal b1 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(a2));
        //保留两位小数，四舍五入
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }
}
