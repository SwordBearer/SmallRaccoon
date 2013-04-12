package xmu.swordbearer.smallraccoon.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import xmu.swordbearer.smallraccoon.R;
import xmu.swordbearer.smallraccoon.http.HttpGetHelper;
import xmu.swordbearer.smallraccoon.http.HttpUtils;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Thread(new Runnable() {
			public void run() {
				testHttpGet();
			}
		}).start();
	}

	private void testHttpGet() {
		HashMap<String, Object> params = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("catalog", 1);
				put("pageIndex", 3);
				put("pageSize", 20);
			}
		};
		String uri = HttpUtils.makeGetUri(
				"http://www.oschina.net/action/api/news_list", params);
		HttpGetHelper httpGetHelper = new HttpGetHelper();
		HttpClient httpClient = new DefaultHttpClient();
		InputStream stream = httpGetHelper.httpGetStream(httpClient, uri);
		try {
			String result = HttpUtils.readStream2String(stream);
			System.out.println("返回的结果长度是 " + result.length());
			System.out.println("返回的结果是 \n" + result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			HttpUtils.shutdown(httpClient);
		}
	}
}
