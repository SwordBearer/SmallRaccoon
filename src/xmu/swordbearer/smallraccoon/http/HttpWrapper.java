package xmu.swordbearer.smallraccoon.http;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class HttpWrapper {
    /***************************** HTTP POST **************************/
    /**
     * 执行HttpPost方法
     *
     * @param uri
     * @param post_params
     * @return
     * @throws org.apache.http.client.ClientProtocolException
     * @throws java.io.IOException
     */
    public static String httpPost(String uri, Map<String, Object> post_params) throws ClientProtocolException, IOException {
        String result = null;
        Log.e("HttpWrapper", "请求的地址是 " + uri);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new UrlEncodedFormEntity(createNameValuePair(post_params), HTTP.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);
        if (response != null) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    result = InputStreamTOString(entity.getContent());
                    result = result.substring(result.indexOf("{"), result.length());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return result;
    }

    /**
     * InputStream转化成String
     *
     * @param in
     * @return
     * @throws Exception
     */
    private static String InputStreamTOString(InputStream in) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int BUFFER_SIZE = 1024;
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);
        data = null;
        return new String(outStream.toByteArray());
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

    /***************************** HTTP GET **********************************/

    /**
     * 经测试，使用HttpClient自带方法将HttpEntity 转为字符串比将
     * InputStream转为字符串要耗时，故放弃使用httpGetString方法， 直接返回InputStream，使用
     * HttpUtils.readStream()来转为字符串
     **/

    /**
     * 使用默认的HttpClient和HttpGet去访问网址,返回 Inpustream 数据流是为了XML或者Jsoup 的解析处理
     *
     * @param uri 访问的网址，可以带有参数
     * @return返回 InputStream数据流，切记处理完成后要关闭连接
     */
    public static InputStream httpGet(String uri) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        InputStream inputStream = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return inputStream;
        // shutdown...
    }

}
