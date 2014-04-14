package xmu.swordbearer.smallraccoon.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpUtils {

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
     * 将byte流转化为字符串
     *
     * @param pStream
     * @return
     * @throws IOException
     */
    public static String readStream2String(InputStream pStream)
            throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(pStream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line + "\n");
        }
        return buffer.toString();
    }

    /**
     * 生成HTTP-GET的访问网址，网址中附带请求参数
     *
     * @param prefix_uri 网址
     * @param params     参数
     * @return
     */
    public static String makeGetUri(String prefix_uri,
                                    Map<String, Object> params) {
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
                    DefaultHttpParams.DEFAULT_CONN_TIMEOUT);
        } else {
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    conn_timeout);
        }
        if (so_timeout <= 0) {
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    DefaultHttpParams.DEFAULT_SO_TIMEOUT);
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
            httpGet.setHeader(DefaultHttpParams.ACCEPT,
                    DefaultHttpParams.DEFAULT_ACCEPT);
        } else {
            httpGet.setHeader(DefaultHttpParams.ACCEPT, accept);
        }
        if (charSet == null) {
            httpGet.setHeader(DefaultHttpParams.CHARSET,
                    DefaultHttpParams.DEFAULT_CHARSET);
        } else {
            httpGet.setHeader(DefaultHttpParams.CHARSET, charSet);
        }
        if (encoding == null) {
            httpGet.setHeader(DefaultHttpParams.ENCODING,
                    DefaultHttpParams.DEFAULT_ENCODING);
        } else {
            httpGet.setHeader(DefaultHttpParams.ENCODING, encoding);
        }
        if (connection == null) {
            httpGet.setHeader(DefaultHttpParams.CONNECTION,
                    DefaultHttpParams.DEFAULT_CONNECTION);
        } else {
            httpGet.setHeader(DefaultHttpParams.CONNECTION, connection);
        }
        if (host != null) {
            httpGet.setHeader(DefaultHttpParams.HOST, host);
        }
        if (userAgent == null) {
            httpGet.setHeader(DefaultHttpParams.USER_AGENT,
                    DefaultHttpParams.DEFAULT_USER_AGENT);
        } else {
            httpGet.setHeader(DefaultHttpParams.USER_AGENT, userAgent);
        }
        if (cookie != null)
            httpGet.setHeader(DefaultHttpParams.COOKIE, cookie);
        httpGet.setHeader("Content-Type", "");
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        return httpGet;
    }
}
