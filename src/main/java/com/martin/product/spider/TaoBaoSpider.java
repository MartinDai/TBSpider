package com.martin.product.spider;

import com.martin.product.http.HttpUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 淘宝爬虫
 * Created by Martin on 2016/3/12.
 */
public class TaoBaoSpider {

    /**
     * 检查宝贝是否在售
     */
    public static boolean checkItemIsOnSale(String url) {
        Document document = HttpUtils.buildHtmlDocument(url);
        Element element = document.getElementById("J_LinkBuy");
        return element!= null;
    }

    public static void main(String[] args) {
        String url = "https://detail.tmall.com/item.htm?spm=a230r.1.14.13.514e2499vNWsK9&id=561872607703&cm_id=140105335569ed55e27b&abbucket=7";
        System.out.println(checkItemIsOnSale(url));
    }

}
