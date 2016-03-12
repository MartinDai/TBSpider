package com.martin.product.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html字符串操作工具类
 */
public class HtmlUtils {

    /**
     * 提取一段字符串s中的js变量,如果s中有多个同名变量，返回第一个
     */
    public static String getJsVarValue(String s, String varName) {
        Pattern pattern = Pattern.compile("var\\s+" + varName + "\\s+=(.*);");
        Matcher matcher = pattern.matcher(s);
        String value = "";
        if (matcher.find()) {
            value = matcher.group(1).trim();
        }
        return value;
    }

    /**
     * 获取host
     */
    public static String getServerHost(String url) {
        if (url.contains("www") && url.indexOf(".com") > 0) {
            return url.substring(url.indexOf("www"), url.indexOf(".com") + 4);
        } else if (url.contains("https") && url.indexOf(".com") > 0) {
            return url.substring(url.indexOf("https") + 8, url.indexOf(".com") + 4);
        } else if (url.contains("http") && url.indexOf(".com") > 0) {
            return url.substring(url.indexOf("http") + 7, url.indexOf(".com") + 4);
        }
        return "";
    }

}
