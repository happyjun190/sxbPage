package com.sxb.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.sxb.web.dto.CoreFuncReturn;

public class HttpHelper {

	String uri;
	Map<String, String> headerMap;
	Map<String, Object> paramMap;
	String paramString;
	String encode = "utf-8";
	String method = "post";
	private static final int CONNECTION_TIMEOUT = 30000;
	private static final int SO_TIMEOUT = 60000;

	public HttpHelper() {
	}

	public HttpHelper(String uri) {

		this.uri = uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setMethod(String method) {
		try {
			if (method.toLowerCase().equals("post")) {
				
			} else if (method.toLowerCase().equals("get")) {
				this.method = "get";
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 以MAP的方式设置header，如：
	 * ("Content-type", "application/json");
	 * ("Accept", "application/json");
	 * @author XuJijun
	 * @param headerMap
	 */
	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
	}

	/**
	 * 	以Map的方式设置请求参数
	 */
	public void setMapParam(Map<String, Object> map) {

		this.paramMap = map;
	}

	/**
	 * 以拼装好的String设置请求参数，如a=aaa&b=bbb&c=ccc
	 */
	public void setStringParam(String s) {
		this.paramString = s;
	}

	public void setEncode(String encode) {

		this.encode = encode;

	}

	public CoreFuncReturn sendPost() {
		CloseableHttpClient httpClient = getHttpClient(uri);// HttpClients.createDefault();
		try {
			// 使用HttpPost请求方式
			HttpPost httpPost = new HttpPost(uri);
			
			//httpPost.setHeader("Content-type", "application/json");
			//httpPost.setHeader("Accept", "application/json");
			//设置Header
			if(!(headerMap==null || headerMap.isEmpty())){
				for(Map.Entry<String, String> entry : headerMap.entrySet()){
					httpPost.setHeader(entry.getKey(),entry.getValue());
				}
			}
			
			//设置请求参数
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			if (paramMap != null && !paramMap.isEmpty()) {
				for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
					paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue() + ""));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, encode);
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
			}
			if (paramString != null) {
				paramString = new String(paramString.getBytes(encode), encode);
				StringEntity entity = new StringEntity(paramString, encode);
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
			}
			
			// 实例化一个默认的Http客户
			// final HttpParams httpParams = new BasicHttpParams();
			// HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
			// DefaultHttpClient client = new DefaultHttpClient();
			// client.setParams(httpParams);
			// // 执行请求，并获得响应数据
			// HttpResponse httpResponse = client.execute(httpPost);
			// // 判断是否请求成功，为200时表示成功，其他均问有问题
			// client.close();

			// ------------------------------------------

			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 通过HttpEntity获得响应
				String result = null;
				HttpEntity entity = httpResponse.getEntity();
				if(entity != null){
					InputStream inputStream = httpResponse.getEntity().getContent();
					result = convertStreamToString(inputStream);
				}
				return new CoreFuncReturn(true, "HTTP请求成功！", result);
			} else {
				return new CoreFuncReturn(false, "访问失败！");
			}
		} catch (Exception ex) {
			if (ex instanceof UnknownHostException) {
				return new CoreFuncReturn(false, "无法解析地址，无此主机地址！", ex.getMessage());
			} else if (ex instanceof TimeoutException) {
				return new CoreFuncReturn(false, "连接超时！", ex.getMessage());
			} else {
				return new CoreFuncReturn(false, "其他错误！", ex.getMessage());
			}
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建一个HttpClient 
	 * @param url
	 * @return
	 */
	private CloseableHttpClient getHttpClient(String url) {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SO_TIMEOUT).build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
		if (url.startsWith("https")) {
			// httpClient = SSLClientWrapper.getHttpsClient(config);
		}
		return httpClient;
	}

	/**
	 * 把Web站点返回的响应流转换为字符串格式
	 * 
	 * @param inputStream
	 *            响应
	 * @param encode
	 *            编码格式
	 * @return 转换后的字符
	 */
	/*private String changeInputStream(InputStream inputStream, String encode) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[8192];
		int len = 0;
		String result = "";
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				result = new String(outputStream.toByteArray(), encode);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}*/
	
	/**
	 * 转换Stream为字符串
	 * @author XuJijun
	 * @param is
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
        StringBuilder sb = new StringBuilder();
       
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	
	/**
	 * http get方法请求数据
	 * @return
	 */
	public CoreFuncReturn sendGet() {
		CloseableHttpClient httpClient = getHttpClient(uri);// HttpClients.createDefault();
		try {
			// 使用httpGet请求方式
			HttpGet httpGet = new HttpGet(uri);
			
			//设置Header
			if(!(headerMap==null || headerMap.isEmpty())){
				for(Map.Entry<String, String> entry : headerMap.entrySet()){
					httpGet.setHeader(entry.getKey(),entry.getValue());
				}
			}
			
			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 通过HttpEntity获得响应
				String result = null;
				HttpEntity entity = httpResponse.getEntity();
				if(entity != null){
					InputStream inputStream = httpResponse.getEntity().getContent();
					result = convertStreamToString(inputStream);
				}
				return new CoreFuncReturn(true, "获取成功", result);
			} else {
				return new CoreFuncReturn(false, "访问失败");
			}
		} catch (Exception ex) {
			if (ex instanceof UnknownHostException) {
				return new CoreFuncReturn(false, "无法解析地址，无此主机地址。", ex.getMessage());
			} else if (ex instanceof TimeoutException) {
				return new CoreFuncReturn(false, "连接超时", ex.getMessage());
			} else {
				return new CoreFuncReturn(false, "", ex.getMessage());
			}
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}