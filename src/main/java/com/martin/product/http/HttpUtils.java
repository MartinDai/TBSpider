package com.martin.product.http;

import com.martin.product.html.HtmlUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * http访问工具类
 */
public class HttpUtils {

    /**
     * 根据url构建html页面
     */
    public static Document buildHtmlDocument(String url) {
        return buildHtmlDocument(false, url);
    }

    /**
     * 根据url构建html页面
     */
    public static Document buildHtmlDocument(boolean isUseProxy, String url) {
        return buildHtmlDocument(isUseProxy, url, null);
    }

    /**
     * 根据url和cookie构建html页面
     */
    public static Document buildHtmlDocument(String url, String cookie) {
        String UA = "";
        try {
            UA = UserAgents.getRandomUserAgent();
        } catch (Exception ignored) {
        }
        return buildHtmlDocument(false, url, cookie, UA);
    }

    /**
     * 根据url和cookie构建html页面
     */
    public static Document buildHtmlDocument(boolean isUseProxy, String url, String cookie) {
        String UA = "";
        try {
            UA = UserAgents.getRandomUserAgent();
        } catch (Exception ignored) {
        }
        return buildHtmlDocument(isUseProxy, url, cookie, UA);
    }

    /**
     * 根据url、cookie和UA构建html页面
     */
    public static Document buildHtmlDocument(String url, String cookie, String UA) {
        return buildHtmlDocument(false, url, cookie, UA, null);
    }

    /**
     * 根据url、cookie和UA构建html页面
     */
    public static Document buildHtmlDocument(boolean isUseProxy, String url, String cookie, String UA) {
        return buildHtmlDocument(isUseProxy, url, cookie, UA, null);
    }

    /**
     * 根据url、cookie、UA和referer构建html页面
     */
    public static Document buildHtmlDocument(String url, String cookie, String UA, String referer) {
        return buildHtmlDocument(false, url, cookie, UA, referer, null);
    }

    /**
     * 根据url、cookie、UA和referer构建html页面
     */
    public static Document buildHtmlDocument(boolean isUseProxy, String url, String cookie, String UA, String referer) {
        return buildHtmlDocument(isUseProxy, url, cookie, UA, referer, null);
    }

    /**
     * 根据url、cookie、UA、referer和host构建html页面
     */
    public static Document buildHtmlDocument(boolean isUseProxy, String url, String cookie, String UA, String referer, String host) {
        Connection connection = createGetDocumentConnection(isUseProxy, url, cookie, UA, referer, host);
        Connection.Response response = executeConnection(connection);
        if (response != null) {
            try {
                return response.parse();
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    /**
     * 构建一个同步抓取连接
     */
    public static Connection getConnection(String url) {
        return getConnection(false, url, null);
    }

    /**
     * 构建一个同步抓取连接
     */
    public static Connection getConnection(boolean isUseProxy, String url) {
        return getConnection(isUseProxy, url, null);
    }

    /**
     * 构建一个同步抓取连接
     */
    public static Connection getConnection(String url, String cookie) {
        String UA = "";
        try {
            UA = UserAgents.getRandomUserAgent();
        } catch (Exception ignored) {
        }
        return getConnection(url, cookie, UA, null, null);
    }

    /**
     * 构建一个同步抓取连接
     */
    public static Connection getConnection(boolean isUseProxy, String url, String cookie) {
        String UA = "";
        try {
            UA = UserAgents.getRandomUserAgent();
        } catch (Exception ignored) {
        }
        return getConnection(isUseProxy, url, cookie, UA, null, null);
    }

    /**
     * 构建一个同步抓取连接
     */
    public static Connection getConnection(String url, String cookie, String UA, String referer, String host) {
        Connection connection = Jsoup.connect(url).timeout(60000).ignoreContentType(true).ignoreHttpErrors(true);
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.header("Accept-Encoding", "gzip,deflate,sdch");
        connection.header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        connection.header("Cache-Control", "no-cache");
        connection.header("Connection", "keep-alive");
        if (!StringUtils.isEmpty(UA)) {
            connection.header("User-Agent", UA);
        }
        if (!StringUtils.isEmpty(referer)) {
            connection.header("Referer", referer);
        }
        if (!StringUtils.isEmpty(cookie)) {
            connection.header("Cookie", cookie);
        }

        if (StringUtils.isEmpty(host)) {
            host = HtmlUtils.getServerHost(url);
        }
        if (StringUtils.isNotEmpty(host)) {
            connection.header("Host", host);
        }

        return connection;
    }

    /**
     * 构建一个同步抓取连接
     */
    public static Connection getConnection(boolean isUseProxy, String url, String cookie, String UA, String referer, String host) {
        return getConnection(url, cookie, UA, referer, host);
    }

    /**
     * 构建一个请求类型为GET，返回类型为document的抓取连接
     */
    public static Connection createGetDocumentConnection(boolean isUseProxy, String url, String cookie) {
        Connection connection = getConnection(url, cookie);
        connection.method(Connection.Method.GET);
        return connection;
    }

    /**
     * 构建一个请求类型为GET，返回类型为document的抓取连接
     */
    public static Connection createGetDocumentConnection(boolean isUseProxy, String url, String cookie, String UA, String referer, String host) {
        Connection connection = getConnection(url, cookie, UA, referer, host);
        connection.method(Connection.Method.GET);
        return connection;
    }

    /**
     * 构建一个请求类型为POST，返回类型为document的抓取连接
     */
    public static Connection createPostDocumentConnection(boolean isUseProxy, String url, String cookie, String UA, String referer, String host) {
        Connection connection = getConnection(url, cookie, UA, referer, host);
        connection.method(Connection.Method.POST);
        return connection;
    }

    /**
     * 构建一个返回类型为String的抓取连接
     */
    public static Connection createStringConnection(boolean isUseProxy, String url, String cookie, String UA, String referer, String host) {
        Connection connection = getConnection(url, cookie, UA, referer, host);
        connection.header("Accept", "*/*");
        return connection.ignoreContentType(true);
    }

    /**
     * 构建一个请求类型为GET，返回类型为String的抓取连接
     */
    public static Connection createGetStringConnection(String url, String cookie) {
        return createGetStringConnection(false, url, cookie);
    }

    /**
     * 构建一个请求类型为GET，返回类型为String的抓取连接
     */
    public static Connection createGetStringConnection(boolean isUseProxy, String url, String cookie) {
        return createGetStringConnection(isUseProxy, url, cookie, null, null, null);
    }

    /**
     * 构建一个请求类型为GET，返回类型为String的抓取连接
     */
    public static Connection createGetStringConnection(boolean isUseProxy, String url, String cookie, String UA, String referer, String host) {
        Connection connection = createStringConnection(isUseProxy, url, cookie, UA, referer, host);
        connection.method(Connection.Method.GET);
        return connection;
    }

    /**
     * 构建一个请求类型为POST，返回类型为String的抓取连接
     */
    public static Connection createPostStringConnection(boolean isUseProxy, String url, String cookie, String UA, String referer, String host,
                                                        String... postData) {
        Connection connection = createStringConnection(isUseProxy, url, cookie, UA, referer, host);
        connection.method(Connection.Method.POST);
        connection.data(postData);
        return connection;
    }

    public static Connection.Response executeConnection(Connection connection) {
        for (int i = 0; i < 3; i++) {
            try {
                return connection.execute();
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ev) {
                    Thread.interrupted();
                }
            }
        }
        return null;
    }

    /**
     * 根据url构建byte数组返回数据，默认不使用代理
     */
    public static byte[] buildGetBytes(String url) {
        return buildGetBytes(false, url);
    }

    /**
     * 根据url构建byte数组返回数据，根据参数选择使用代理
     */
    public static byte[] buildGetBytes(boolean isUseProxy, String url) {
        return buildGetBytes(isUseProxy, url, null);
    }

    /**
     * 根据url和cookie构建byte数组返回数据，默认不使用代理
     */
    public static byte[] buildGetBytes(String url, String cookie) {
        return buildGetBytes(false, url, cookie);
    }

    /**
     * 根据url和cookie构建byte数组返回数据，根据参数选择使用代理
     */
    public static byte[] buildGetBytes(boolean isUseProxy, String url, String cookie) {
        try {
            Connection connection = createGetStringConnection(isUseProxy, url, cookie, UserAgents.getRandomUserAgent(), null,
                    HtmlUtils.getServerHost(url));
            Connection.Response response = executeConnection(connection);
            if (response != null) {
                return response.bodyAsBytes();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 根据url构建String类型返回数据
     */
    public static String buildGetString(String url) {
        return buildGetString(false, url);
    }

    /**
     * 根据url构建String类型返回数据
     */
    public static String buildGetString(boolean isUseProxy, String url) {
        return buildGetString(isUseProxy, url, null);
    }

    /**
     * 根据url和cookie构建String类型返回数据
     */
    public static String buildGetString(String url, String cookie) {
        return buildGetString(false, url, cookie);
    }

    /**
     * 根据url和cookie构建String类型返回数据
     */
    public static String buildGetString(boolean isUseProxy, String url, String cookie) {
        String UA = "";
        try {
            UA = UserAgents.getRandomUserAgent();
        } catch (Exception ignored) {
        }
        return buildGetString(isUseProxy, url, cookie, UA);
    }

    /**
     * 根据url、feferer和cookie构建String类型返回数据
     */
    public static String buildGetString(boolean isUseProxy, String url, String cookie, String UA) {
        return buildGetString(isUseProxy, url, cookie, UA, null);
    }

    /**
     * 根据url、feferer和cookie构建String类型返回数据
     */
    public static String buildGetString(boolean isUseProxy, String url, String cookie, String UA, String referer) {
        try {
            Connection connection = createGetStringConnection(isUseProxy, url, cookie, UA, referer, HtmlUtils.getServerHost(url));
            Connection.Response response = executeConnection(connection);
            if (response != null) {
                return response.body();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 根据url、cookie和stringData构建String类型返回数据
     */
    public static String buildPostString(String url, String cookie, String stringData) {
        return buildPostString(false, url, cookie, stringData);
    }

//    /**
//     * 根据url、cookie和stringData构建String类型返回数据
//     */
//    public static String buildPostString(boolean isUseProxy, String url, String cookie, String stringData) {
//        try {
//            Connection connection = createPostStringConnection(isUseProxy, url, cookie, UserAgents.getRandomUserAgent(), null,
//                    HtmlUtils.getServerHost(url));
//            connection.header("Content-Type", "application/json;charset=UTF-8");
//            connection.stringData(stringData);
//            Connection.Response response = executeConnection(connection);
//            if (response != null) {
//                return response.body();
//            }
//        } catch (Exception ignored) {
//        }
//
//        return null;
//    }

    /**
     * 根据url、cookie和postData构建String类型返回数据
     */
    public static String buildPostString(String url, String cookie, String... postData) {
        return buildPostString(false, url, cookie, postData);
    }

    /**
     * 根据url、cookie和postData构建String类型返回数据
     */
    public static String buildPostString(boolean isUseProxy, String url, String cookie, String... postData) {
        try {
            Connection connection = createPostStringConnection(isUseProxy, url, cookie, UserAgents.getRandomUserAgent(), null,
                    HtmlUtils.getServerHost(url), postData);
            Connection.Response response = executeConnection(connection);
            if (response != null) {
                return response.body();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 判断页面是否是重定向
     */
    public static boolean isRedirect(Connection.Response response) {
        int code = response.statusCode();
        return code == 301 || code == 302 || code == 303;
    }

}
