package com.example.administrator.baofen.base;

public class JSUtil {

    public static String buildJSF(String js){
        return "javascript:(function test(){ "+js+" })();";
    }
}
