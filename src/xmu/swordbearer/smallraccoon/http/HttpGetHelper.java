/**
 * @author SwordBearer
 * @email  ranxiedao@163.com
 * @blog   http://blog.csdn.net/ranxiedao
 */
package xmu.swordbearer.smallraccoon.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

/**
 * Http Get工具类，提供HttpGet相关的方法
 */
public class HttpGetHelper {
	/**
	 * 经测试，使用HttpClient自带方法将HttpEntity 转为字符串比将
	 * InputStream转为字符串要耗时，故放弃使用httpGetString方法， 直接返回InputStream，使用
	 * HttpUtils.readStream()来转为字符串
	 **/

	/**
	 * 使用默认的HttpClient和HttpGet去访问网址,返回 Inpustream 数据流是为了XML或者Jsoup 的解析处理
	 * 
	 * @param uri
	 *            访问的网址，可以带有参数
	 * @param httpClient
	 *            [=newDefaultHttpClient()]
	 *            为了在使用完InputStream后关闭连接，需要在调用此方法后，关闭httpClient
	 * @return返回 InputStream数据流，切记处理完成后要关闭连接
	 */
	public InputStream httpGetStream(HttpClient httpClient, String uri) {
		HttpGet httpGet = createHttpGet(uri, null, null, null, null, null,
				null, null);
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
		try {
			Log.e("TEST", "httpGetStream " + inputStream.available());
		} catch (IOException e) {
		}
		return inputStream;
		// shutdown...
	}

	/**
	 * 使用自定义的HttpGet [=createHttpGet() ]来获取数据，返回数据流
	 * 
	 * @param httpClient
	 * @param httpGet
	 * @return InputStream数据流，切记处理完成后要关闭连接
	 */
	public InputStream httpGetStream(HttpClient httpClient, HttpGet httpGet) {
		InputStream inpuStream = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				inpuStream = entity.getContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inpuStream;
		// shutdown...
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