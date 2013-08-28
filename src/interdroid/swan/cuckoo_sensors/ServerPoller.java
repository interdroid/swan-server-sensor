package interdroid.swan.cuckoo_sensors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * A sensor for the return http status of a web server
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class ServerPoller implements CuckooPoller {

	@Override
	public Map<String, Object> poll(String valuePath,
			Map<String, Object> configuration) {
		Map<String, Object> newValues = new HashMap<String, Object>();
		// valuePath can be ignored, we just have one
		long connectionTimeOut = (Long) configuration.get("timeout");
		String serverURL = ((String) configuration.get("url")).replace("'", "");
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				(int) connectionTimeOut);
		HttpConnectionParams.setSoTimeout(httpParams, (int) connectionTimeOut);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpGet httpGet = new HttpGet(serverURL);
		try {
			long statusCode = httpClient.execute(httpGet).getStatusLine()
					.getStatusCode();
			newValues.put("http_status", statusCode);
		} catch (ClientProtocolException e) {
			// ignore
		} catch (IOException e) {
			// ignore
		}
		return newValues;
	}

	@Override
	public long getInterval(Map<String, Object> configuration, boolean remote) {
		if (remote) {
			// every second
			return 1000;
		} else {
			// every 10 minutes
			return 10 * 60000;
		}
	}

}