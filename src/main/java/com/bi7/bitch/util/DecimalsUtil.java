package com.bi7.bitch.util;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by foxer on 2017/8/30.
 */
@Service
public class DecimalsUtil {

    /*
    decimals 精度
    localDecimals 本地精度
     */
    public String encode(String value, int decimals, int localDecimals) {
        long pre;
        String aft;
        if (value.length() <= decimals) {
            pre = 0;
            aft = paddingLeft(value, decimals - value.length(), "0");
        } else {
            pre = Long.parseLong(value.substring(0, value.length() - decimals));
            aft = value.substring(value.length() - decimals);
        }
        return String.format("%d.%s", pre, aft.substring(0, localDecimals));
    }

    /*
    double to wei
     */
    public BigInteger decode(String value, int decimals) {
        BigDecimal dec = new BigDecimal(value);
        dec = dec.multiply(new BigDecimal(10).pow(decimals));
        return dec.toBigIntegerExact();

    }

    private String paddingLeft(String str, int len, String c) {
        while (len-- > 0) {
            str = c + str;
        }
        return str;
    }

    public static void main(String[] args) {
        DecimalsUtil util = new DecimalsUtil();

        System.out.println(util.encode("629260100000000000", 18, 6));

        System.out.println(util.decode("8.62145", 18));
    }


}
