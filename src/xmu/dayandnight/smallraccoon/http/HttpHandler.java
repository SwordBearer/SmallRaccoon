package xmu.dayandnight.smallraccoon.http;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author SwordBearer  e-mail :ranxiedao@163.com
 *         Created by SwordBearer on 13-8-12.
 */

public class HttpHandler {
    public static final String DATA_TYPE_STREAM = "STREAM";
    public static final String DATA_TYPE_STRING = "STRING";
    private static final String PARAM_ACCEPT = "Accept";
    private static final String PARAM_CHARSET = "Charset";
    private static final String PARAM_ENCODING = "encoding";
    private static final String PARAM_CONNECTION = "Connection";
    private static final String PARAM_USER_AGENT = "User-Agent";
    private static final String PARAM_HOST = "Host";
    private static final String PARAM_COOKIE = "Cookie";
    private static final String PARAM_CONTENT_TYPE = "Content-Type";
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String DEFAULT_CONNECTION = "Keep-Alive";
    private static final int DEFAULT_CONN_TIMEOUT = 6000;
    private static final int DEFAULT_SO_TIMEOUT = 3000;
    private static HttpHandler _instance;

    private HttpHandler() {

    }

    public static HttpHandler getInstance() {
        if (null == _instance) {
            _instance = new HttpHandler();
        }
        return _instance;
    }

    /**
     * ************************** HTTP POST *************************
     */

    /**
     * 发送 POST 请求，获取的数据格式为二进制流
     *
     * @param uri         请求uri
     * @param post_params POST请求的参数
     * @return
     * @throws IOException
     */
    public static InputStream httpPostStream(String uri, Map<String, Object> post_params) throws IOException {
        return _entity2Stream(_httpPost(uri, post_params));
    }

    /**
     * 发送 POST 请求，获取的数据格式为字符串
     *
     * @param uri
     * @param post_params
     * @return
     * @throws IOException
     */
    public static String httpPostString(String uri, Map<String, Object> post_params) throws IOException {
        return _entity2String(_httpPost(uri, post_params));
    }

    private static HttpEntity _httpPost(String uri, Map<String, Object> post_params) throws IOException {
        Log.e("HttpHandler", "请求的地址是 " + uri);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new UrlEncodedFormEntity(createNameValuePair(post_params), HTTP.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);
        if (null == response) {
            return null;
        }
        return response.getEntity();
    }

    /**
     * 将HttpEntity转化为字符串
     *
     * @param entity
     * @return
     */
    private static String _entity2String(HttpEntity entity) {
        String data = null;
        if (entity != null) {
            try {
                data = EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 将HttpEntity转化为二进制流
     *
     * @param entity
     * @return
     */
    private static InputStream _entity2Stream(HttpEntity entity) {
        InputStream stream = null;
        if (entity != null) {
            try {
                stream = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stream;
    }

    /**
     * 生成post参数对
     *
     * @param map 提交的参数键值对
     * @return
     */
    private static ArrayList<NameValuePair> createNameValuePair(Map<String, Object> map) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (String name : map.keySet()) {
            pairs.add(new BasicNameValuePair(name, String.valueOf(map.get(name))));
        }
        return pairs;
    }

    /**
     * ************************** HTTP GET *********************************
     */


    /**
     * 发送 GET 请求，获取的数据格式为二进制流
     *
     * @param uri
     * @param params
     * @return
     * @throws IOException
     */
    public static InputStream httpGetStream(String uri, Map<String, Object> params) throws IOException {
        return _entity2Stream(_httpGet(uri, params));
    }


    /**
     * 发送 GET 请求，获取的数据格式为字符串
     *
     * @param uri
     * @param params
     * @return
     * @throws IOException
     */
    public static String httpGetString(String uri, Map<String, Object> params) throws IOException {
        return _entity2String(_httpGet(uri, params));
    }

    private static HttpEntity _httpGet(String prefix_uri, Map<String, Object> params) throws IOException {
        String uri = makeGetUri(prefix_uri, params);
        Log.e("TEST", "httpGet 请求的地址是:" + uri);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = httpClient.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (null == response || statusCode != 200) {
            return null;
        }
        return response.getEntity();
    }

    /**
     * 当HttpClient连接处理完成后，就需要关闭连接
     *
     * @param httpClient
     */
    public static void shutdown(HttpClient httpClient) {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }


    /**
     * 生成HTTP-GET的访问网址，网址中附带请求参数
     *
     * @param prefix_uri 网址
     * @param params     参数
     * @return
     */
    private static String makeGetUri(String prefix_uri, Map<String, Object> params) {
        StringBuilder newUri = new StringBuilder(prefix_uri);
        if (newUri.indexOf("?") < 0) {
            newUri.append('?');
        }
        if (params == null) {
            return prefix_uri;
        }
        for (String name : params.keySet()) {
            newUri.append(name);
            newUri.append('=');
            newUri.append(String.valueOf(params.get(name)));
            newUri.append('&');
            // // 不做URLEncoder处理
            // newUri.append(URLEncoder.encode(String.valueOf(params.get(name)),
            // UTF_8));
        }
        String urlString = newUri.toString();
        return urlString.substring(0, urlString.length() - 1);
    }

    /**
     * 自定义HttpClient
     *
     * @param conn_timeout 超时时间
     * @param so_timeout   读取数据超时时间
     * @param cookiePolicy 设置Cookie的策略 (默认与浏览器一样)
     * @param charSet      字符编码             (默认UTF-8)
     * @return
     */
    public static HttpClient createHttpClient(int conn_timeout, int so_timeout,
                                              Object cookiePolicy, String charSet) {
        HttpParams params = new BasicHttpParams();
        if (conn_timeout <= 0) {
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    DEFAULT_CONN_TIMEOUT);
        } else {
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    conn_timeout);
        }
        if (so_timeout <= 0) {
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    DEFAULT_SO_TIMEOUT);
        } else {
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT, so_timeout);
        }
        if (cookiePolicy == null) {
            params.setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
        } else {
            params.setParameter(ClientPNames.COOKIE_POLICY, cookiePolicy);
        }
        // 设置字符集
        if (charSet == null) {
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
                    "UTF-8");
        } else {
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
                    charSet);
        }
        return new DefaultHttpClient(params);
    }

    /**
     * 生成自定义HttpGet
     *
     * @param uri
     * @param accept
     * @param charSet
     * @param encoding
     * @param connection
     * @param host
     * @param userAgent
     * @param cookie
     * @return
     */
    public HttpGet createHttpGet(String uri, String accept, String charSet,
                                 String encoding, String connection, String host, String userAgent,
                                 String cookie) {
        HttpGet httpGet = new HttpGet(uri);
        if (accept == null) {
            httpGet.setHeader(PARAM_ACCEPT, DEFAULT_ACCEPT);
        } else {
            httpGet.setHeader(PARAM_ACCEPT, accept);
        }
        if (charSet == null) {
            httpGet.setHeader(PARAM_CHARSET, DEFAULT_CHARSET);
        } else {
            httpGet.setHeader(PARAM_CHARSET, charSet);
        }
        if (encoding == null) {
            httpGet.setHeader(PARAM_ENCODING, DEFAULT_ENCODING);
        } else {
            httpGet.setHeader(PARAM_ENCODING, encoding);
        }
        if (connection == null) {
            httpGet.setHeader(PARAM_CONNECTION, DEFAULT_CONNECTION);
        } else {
            httpGet.setHeader(PARAM_CONNECTION, connection);
        }
        if (host != null) {
            httpGet.setHeader(PARAM_HOST, host);
        }
        if (userAgent == null) {
            httpGet.setHeader(PARAM_USER_AGENT, DEFAULT_USER_AGENT);
        } else {
            httpGet.setHeader(PARAM_USER_AGENT, userAgent);
        }
        if (cookie != null)
            httpGet.setHeader(PARAM_COOKIE, cookie);
        httpGet.setHeader("Content-Type", "");
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        return httpGet;
    }
}
