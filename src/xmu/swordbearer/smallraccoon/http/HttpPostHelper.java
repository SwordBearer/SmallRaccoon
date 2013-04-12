package xmu.swordbearer.smallraccoon.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class HttpPostHelper {
	/**
	 * 使用默认的HttpClient来访问网络，使用结束后，需要使用 HttpUtils.shutdown(httpClient)来关闭连接
	 * 
	 * @param httpClient
	 * @param uri
	 * @param post_params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream httpPostStream(HttpClient httpClient, String uri,
			Map<String, Object> post_params) {
		HttpPost httpPost = new HttpPost(uri);
		try {
			return this.httpPostExecute(httpClient, httpPost, post_params);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用自定义的HttpClient和HttpPost ，使用结束后，需要使用 HttpUtils.shutdown(httpClient)来关闭连接
	 * 
	 * @param httpClient
	 * @param httpPost
	 * @param post_params
	 * @return
	 */
	public InputStream httpPostStream(HttpClient httpClient, HttpPost httpPost,
			Map<String, Object> post_params) {
		try {
			return this.httpPostExecute(httpClient, httpPost, post_params);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 执行HttpPost方法，返回byte流
	 * 
	 * @param httpClient
	 * @param httpPost
	 * @param post_params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private InputStream httpPostExecute(HttpClient httpClient,
			HttpPost httpPost, Map<String, Object> post_params)
			throws ClientProtocolException, IOException {
		httpPost.setEntity(new UrlEncodedFormEntity(
				createNameValuePair(post_params)));
		HttpResponse response = httpClient.execute(httpPost);
		if (response != null) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return entity.getContent();
			}
		}
		return null;
	}

	/**
	 * 自定义HttpPost，添加更多参数，为完善
	 * 
	 * @param uri
	 * @param cookie
	 * @param userAgent
	 * @return
	 */
	public HttpPost createHttpPost(String uri, String cookie, String userAgent) {
		HttpPost httpPost = new HttpPost(uri);

		if (userAgent == null) {
			httpPost.setHeader("User-Agent",
					DefaultHttpParams.DEFAULT_USER_AGENT);
		} else {
			httpPost.setHeader("User-Agent", userAgent);
		}
		// ....继续添加
		return httpPost;
	}

	/**
	 * 生成post参数对
	 * 
	 * @param map
	 *            提交的参数键值对
	 * @return
	 */
	private static ArrayList<NameValuePair> createNameValuePair(
			Map<String, Object> map) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (String name : map.keySet()) {
			pairs.add(new BasicNameValuePair(name,
					String.valueOf(map.get(name))));
		}
		return pairs;
	}
}
