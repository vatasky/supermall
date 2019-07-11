package com.supermall.common;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {

    //前两种都会出现都是精度的问题
    @Test
    public void test1(){
        System.out.println(0.05+0.01);
        System.out.println(1.0-0.42);
        System.out.println(4.02*100);
        System.out.println(123/100);
    }
    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.02);
        System.out.println(b1.add(b2));
    }
    //在商业价格的计算中，一定要用Bigdecimal的String构造器来解决，避免丢失精度
    @Test
    public void test3(){
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.02");
        System.out.println(b1.add(b2));
    }
}
