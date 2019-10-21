package com.yordan.finance.utils;

public class PriceUtils {
    private static final String suffix = " лв.";

    public static String formatPrice(double unformattedPrice){
        return String.format("%.2f", unformattedPrice) + suffix;
    }

    public static String formatPrice(float unformattedPrice){
        return String.format("%.2f", unformattedPrice) + suffix;
    }

    public static String getSuffix() {
        return suffix;
    }
}
