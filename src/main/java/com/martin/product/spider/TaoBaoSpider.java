package com.martin.product.spider;

import com.martin.product.http.HttpUtils;
import org.jsoup.nodes.Document;
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
        Elements orderElements = document.getElementsByClass("unit-detail-order-action");
        return !orderElements.isEmpty();
    }

    public static void main(String[] args) {
//        String url = "http://detail.1688.com/offer/520887449688.html?spm=a2615.7691456.0.0.NdFJ60";
        String url = "https://detail.1688.com/offer/38543019067.html?spm=a2615.7691456.0.0.DxOiLN";
        System.out.println(checkItemIsOnSale(url));
    }

}
