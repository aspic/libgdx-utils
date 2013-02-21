package no.mehl.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.utils.Json;

public class Network {
	
	private static final String URL = "http://mehl.no:8080/";
	
	private Json json;
	private HashMap<String, String> params;
	
	
	public Network(String url) {
		json = new Json();
		params = new HashMap<String, String>();
	}
	
	/** Shortcut for de-serializing items when received (not arrays) */
	public <T> T handleAndSerialize(String route, Class<T> clazz) {
		String json = handle(route);
		return this.json.fromJson(clazz, json);
	}
	
	public String post(String route, Object object) {
		String jsonString = json.toJson(object);
		return jsonString;
	}
	
	/** Returns a string from the server */
	public String handle(String route) {
		StringBuilder builder = new StringBuilder();
		try {
		    // Send data
		    URL url = new URL(URL + route);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    // Write parameters
		    System.out.println("Connects to: " + URL + route);
		    wr.write(encode(params));
		    wr.flush();
		    
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line = null;
		    while ((line = rd.readLine()) != null) {
		    	builder.append(line);
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cleanParams();
		String built = builder.toString();
		System.out.println("Response: " + built);
		return built;
	}
	
	/** Encodes and returns a string from the given hash map */
	private String encode(HashMap<String, String> map) throws UnsupportedEncodingException {
		String parameters = "";
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			String value = map.get(key);
			parameters += (URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8")) + "&";
		}
		System.out.println("Request: " + parameters);
		return parameters;
	}
	
	/** Add parameters for this request */
	public void addParam(String key, String value) {
		params.put(key, value);
	}
	
	/** Clean out parameters except farm name and password from post */
	private void cleanParams() {
		String[] keys = new String[params.size()];
		params.keySet().toArray(keys);
		for (int i = 0; i < keys.length; i++) {
			params.remove(keys[i]);
		}
		
	}
	
	public static int download(byte[] out, String url) {
		InputStream in = null;
		try {
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
			conn.setUseCaches(true);
			conn.connect();
			in = conn.getInputStream();
			int readBytes = 0;
			while (true) {
				int length = in.read(out, readBytes, out.length - readBytes);
				if (length == -1)
					break;
				readBytes += length;
			}
			return readBytes;
		} catch (Exception ex) {
			return 0;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception ignored) {
			}
		}
}

	public Json getJson() {
		return this.json;
	}

}
