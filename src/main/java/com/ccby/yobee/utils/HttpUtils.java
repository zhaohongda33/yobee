package com.ccby.yobee.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import com.ccby.core.applicationcontext.SpringContextUtil;
import com.ccby.core.base.util.HttpClientHandlerCallback;
import com.ccby.core.base.util.MD5;
import com.ccby.core.exception.SystemRunTimeException;

@SuppressWarnings("deprecation")
public class HttpUtils {

	public static final String CHARSET_UTF8 = "UTF-8";

	private static final String APPLICATION_JSON = "application/json";
	private static final Logger LOGER = Logger.getLogger(HttpUtils.class);
	
	public static String APP_ID = "";
	public static String APP_KEY = "";
	
	static {
//		APP_ID = "1f644a2afcba068f443f0666e833a306f9d76b08";
//		APP_KEY = "w009WZGAh9ZW9lIFGjES3bkv8OH3F/ZljItunhoYJmvrtLw=";
		
		Properties pro = SpringContextUtil.getBean("propertiesConfig", Properties.class);
		APP_ID = pro.getProperty("yoube_app_id");
		APP_KEY = pro.getProperty("yoube_app_key");
	}
	
	public static String httpPostXml(final String url, final String xml) throws Exception {
		return (String) executePost(new HttpClientHandlerCallback() {

			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {
				method.setRequestHeader("Accept", MediaType.ALL_VALUE);
				method.getParams().setContentCharset(CHARSET_UTF8);
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
				((PostMethod) method).setRequestEntity(new StringRequestEntity(xml, "text/xml", CHARSET_UTF8));
				method.setURI(new URI(url, true, method.getParams().getUriCharset()));

				client.executeMethod(method);

				InputStream is = method.getResponseBodyAsStream();
				return IOUtils.toString(is);
			} 
		});
	}

	/**
	 * post提交JSON数据到指定的服务中
	 * 
	 * @param url
	 * @param data
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String httpPostJson(final String url, final JSONObject data) throws Exception {

		return (String) executePost(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				method.setRequestHeader("Accept", MediaType.ALL_VALUE);

				if (data != null) {
					((PostMethod) method).setRequestBody(data.toString());
				}

				method.getParams().setContentCharset(CHARSET_UTF8);
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
				method.setURI(new URI(url, true, method.getParams().getUriCharset()));

				int status = client.executeMethod(method);
				StringBuffer result = new StringBuffer();

				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200) {
					return result.toString();
				}

				throw new Exception(result.toString());
			}
		});
	}

	public static String httpPost(final String url, final Map<String, ? extends Object> data) throws Exception {

		return (String) executePost(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				String urlStr = url;
				String accept = "application/vnd.botpy.v1+json";
				String time = System.currentTimeMillis() / 1000 + "";
				
//				method.setRequestHeader("POST", "/demo HTTP/1.1");
				method.setRequestHeader("Host", "agent-open.iyobee.com");
				method.setRequestHeader("User-Agent", "curl/7.49.1");
				method.setRequestHeader("Accept", accept);
				method.setRequestHeader("Content-Type", "application/json");
				method.setRequestHeader("Authorization", "appid " + APP_ID);
				method.setRequestHeader("X-Yobee-Timestamp", time);
				
				String paramsStr = com.alibaba.fastjson.JSONObject.toJSONString(data);
				
				String StringToSign = time + APP_KEY + accept + urlStr + MD5.toMD5(paramsStr) + APP_ID;
				method.setRequestHeader("X-Yobee-Signature", HttpUtils.sha256_HMAC(StringToSign, APP_KEY));
				((PostMethod) method).setRequestEntity(new StringRequestEntity(paramsStr));

				method.setURI(new URI(urlStr));
				
				int status = client.executeMethod(method);
				StringBuffer result = new StringBuffer();

				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200 || status == 201 || status == 202) {
					return result.toString();
				}

				throw new Exception(result.toString());
			}
		});
	}

	public static String httpPost(String url) throws Exception {
		return httpPost(url, null);
	}

	/**
	 * 发送get请求并拼接参数到url上
	 * 
	 * @param url
	 * @param data
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static JSONObject httpGetToJson(final String host, final String url, final Map<String, ? extends Object> data) {

		try {
			String result = httpGet(host, url, data);

			return new JSONObject(result);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * 发送get请求并拼接参数到url上
	 * 
	 * @param url
	 * @param data
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String httpGet(final String host, final String url, final Map<String, ? extends Object> data) throws Exception {

		return (String) executeGet(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				Map<String, Object> params = new TreeMap<String, Object>(new Comparator<String>() {

					/**
					 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
					 */
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});
				
				if (data != null){
					params.putAll(data);
				}
				
				String urlStr = url;

				method.setRequestHeader("Host", host);
				method.setRequestHeader("User-Agent", "curl/7.49.1");

				String accept = "application/vnd.botpy.v1+json";
				method.setRequestHeader("Accept", accept);
				method.setRequestHeader("Content-Type", "application/json");

				method.setRequestHeader("Authorization", "appid " + APP_ID);
				String time = System.currentTimeMillis() / 1000 + "";
				method.setRequestHeader("X-Yobee-Timestamp", time);

				String paramsStr = "";
				String fullUrl = urlStr;
				
				if (data != null && data.size() > 0) {
					
					fullUrl += "?";
					
					for (Entry<String, ? extends Object> entry : params.entrySet()) {
						paramsStr += entry.getKey() + entry.getValue().toString();
						fullUrl += entry.getKey()+"="+entry.getValue().toString()+"&";
					}
					
					fullUrl = params.size() > 1 ? fullUrl.substring(0, fullUrl.length()-1) : fullUrl;
				}
				
				String StringToSign = time + APP_KEY
						+ accept + urlStr +paramsStr + APP_ID;
				method.setRequestHeader("X-Yobee-Signature", HttpUtils.sha256_HMAC(StringToSign, APP_KEY));

				method.setURI(new URI(fullUrl));
				int status = client.executeMethod(method);

				StringBuffer result = new StringBuffer();
				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200 || status == 201) {
					return result.toString();
				}

				if (status == 404) {
					return "";
				}
				
				throw new HttpException(result.toString());
			}
		});
	}
	
	/**
	 * 发送get请求并拼接参数到url上
	 * 
	 * @param url
	 * @param data
	 * @param connectionTimeout 连接超时时间（毫秒）
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String httpGet(final String host, final String url, final Map<String, ? extends Object> data, int connectionTimeout) throws Exception {

		return (String) executeGet(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {
				
				if (connectionTimeout > 0){
					client.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
					method.getParams().setSoTimeout(connectionTimeout);
				}
				
				Map<String, Object> params = new TreeMap<String, Object>(new Comparator<String>() {

					/**
					 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
					 */
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});
				
				if (data != null){
					params.putAll(data);
				}
				
				String urlStr = url;

				method.setRequestHeader("Host", host);
				method.setRequestHeader("User-Agent", "curl/7.49.1");

				String accept = "application/vnd.botpy.v1+json";
				method.setRequestHeader("Accept", accept);
				method.setRequestHeader("Content-Type", "application/json");

				method.setRequestHeader("Authorization", "appid " + APP_ID);
				String time = System.currentTimeMillis() / 1000 + "";
				method.setRequestHeader("X-Yobee-Timestamp", time);

				String paramsStr = "";
				String fullUrl = urlStr;
				
				if (data != null && data.size() > 0) {
					
					fullUrl += "?";
					
					for (Entry<String, ? extends Object> entry : params.entrySet()) {
						paramsStr += entry.getKey() + entry.getValue().toString();
						fullUrl += entry.getKey()+"="+entry.getValue().toString()+"&";
					}
					
					fullUrl = params.size() > 1 ? fullUrl.substring(0, fullUrl.length()-1) : fullUrl;
				}
				
				String StringToSign = time + APP_KEY
						+ accept + urlStr +paramsStr + APP_ID;
				method.setRequestHeader("X-Yobee-Signature", HttpUtils.sha256_HMAC(StringToSign, APP_KEY));

				method.setURI(new URI(fullUrl));
				int status = client.executeMethod(method);

				StringBuffer result = new StringBuffer();
				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200 || status == 201) {
					return result.toString();
				}

				if (status == 404) {
					return "";
				}
				
				throw new HttpException(result.toString());
			}
		});
	}

	public static String httpGet(final String url) throws Exception {

		return (String) executeGet(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				method.setRequestHeader("Accept", MediaType.ALL_VALUE);
				method.getParams().setContentCharset(CHARSET_UTF8);
				method.setURI(new URI(url, true, method.getParams().getUriCharset()));
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
				int status = client.executeMethod(method);

				StringBuffer result = new StringBuffer();
				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200) {
					return result.toString();
				}

				throw new HttpException(result.toString());
			}
		});
	}

	/**
	 * 发送http get请求，并携带cookie
	 * 
	 * @param url
	 * @param req
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String httpGetCookie(final String url, final HttpServletRequest req) throws Exception {

		return (String) executeGet(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				method.setRequestHeader("Accept", MediaType.ALL_VALUE);
				method.setRequestHeader("Cookie", req.getHeader("Cookie"));
				method.getParams().setContentCharset(CHARSET_UTF8);
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
				method.setURI(new URI(url, true, method.getParams().getUriCharset()));

				int status = client.executeMethod(method);
				StringBuffer result = new StringBuffer();

				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200) {
					return result.toString();
				}

				throw new HttpException(result.toString());
			}
		});
	}

	/**
	 * 发送http post请求，并携带cookie
	 * 
	 * @param url
	 * @param req
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String httpPostCookie(final String url, final Map<String, Object> data, final HttpServletRequest req)
			throws Exception {

		return (String) executePost(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				method.setRequestHeader("Accept", MediaType.ALL_VALUE);
				method.setRequestHeader("Cookie", req.getHeader("Cookie"));
				method.getParams().setContentCharset(CHARSET_UTF8);
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);

				if (null != data) {

					List<NameValuePair> pair = new ArrayList<NameValuePair>();
					for (Entry<String, Object> entry : data.entrySet()) {

						Object obj = entry.getValue();
						if (obj != null) {

							String key = entry.getKey();

							if (obj instanceof List) {
								@SuppressWarnings("unchecked")
								List<String> objs = (List<String>) obj;
								for (String val : objs) {
									pair.add(new NameValuePair(key, val.toString()));
								}

								continue;
							}

							pair.add(new NameValuePair(key, obj.toString()));
						}
					}

					NameValuePair[] ps = new NameValuePair[pair.size()];
					pair.toArray(ps);
					((PostMethod) method).setRequestBody(ps);
				}

				method.getParams().setContentCharset(CHARSET_UTF8);
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
				method.setURI(new URI(url, true, method.getParams().getUriCharset()));

				int status = client.executeMethod(method);

				StringBuffer result = new StringBuffer();
				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200) {
					return result.toString();
				}

				throw new HttpException(result.toString());
			}
		});
	}
	
	public static String httpDelete(final String url, final Map<String, ? extends Object> data, final HttpClientHandlerCallback headerCallback) throws Exception {
		return (String) executeDelete(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				headerCallback.execute(client, method);
				
				if (data != null){
					NameValuePair[] pair = new NameValuePair[data.size()];

					int x = 0;
					for (Entry<String, ? extends Object> entry : data.entrySet()) {
						pair[x] = new NameValuePair(entry.getKey(), entry.getValue().toString());
						x++;
					}

					method.setQueryString(pair);
				}
				
				method.getParams().setContentCharset(CHARSET_UTF8);
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
				method.setURI(new URI(url, true, method.getParams().getUriCharset()));
				client.executeMethod(method);

				StringBuffer result = new StringBuffer();
				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				return result.toString();
			}
		});
	}
	
	public static String httpPatch(final String url, final Map<String, ? extends Object> data) throws Exception {

		return (String) executePatch(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				String urlStr = url;
				String accept = "application/vnd.botpy.v1+json";
				String time = System.currentTimeMillis() / 1000 + "";
				
//				method.setRequestHeader("POST", "/demo HTTP/1.1");
				method.setRequestHeader("Host", "agent-open.iyobee.com");
				method.setRequestHeader("User-Agent", "curl/7.49.1");
				method.setRequestHeader("Accept", accept);
				method.setRequestHeader("Content-Type", "application/json");
				method.setRequestHeader("Authorization", "appid " + APP_ID);
				method.setRequestHeader("X-Yobee-Timestamp", time);
				
				String paramsStr = com.alibaba.fastjson.JSONObject.toJSONString(data);
				
				String StringToSign = time + APP_KEY + accept + urlStr + MD5.toMD5(paramsStr) + APP_ID;
				method.setRequestHeader("X-Yobee-Signature", HttpUtils.sha256_HMAC(StringToSign, APP_KEY));
				((PatchMethod) method).setRequestEntity(new StringRequestEntity(paramsStr));

				method.setURI(new URI(urlStr));
				
				int status = client.executeMethod(method);
				StringBuffer result = new StringBuffer();

				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				if (status == 200 || status == 201) {
					return result.toString();
				}

				throw new Exception(result.toString());
			}
		});
	}

	public static String httpDelete(final String url, final Map<String, ? extends Object> data) throws Exception {

		return (String) executeDelete(new HttpClientHandlerCallback() {

			/**
			 * @see com.apew.niuke.handler.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
			 *      org.apache.commons.httpclient.HttpMethod)
			 */
			@Override
			public Object execute(HttpClient client, HttpMethod method) throws Exception {

				method.setRequestHeader("Accept", MediaType.ALL_VALUE);
				NameValuePair[] pair = new NameValuePair[data.size()];

				int x = 0;
				for (Entry<String, ? extends Object> entry : data.entrySet()) {
					pair[x] = new NameValuePair(entry.getKey(), entry.getValue().toString());
					x++;
				}

				method.setQueryString(pair);
				method.getParams().setContentCharset(CHARSET_UTF8);
				method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
				method.setURI(new URI(url, true, method.getParams().getUriCharset()));
				client.executeMethod(method);

				StringBuffer result = new StringBuffer();
				InputStream is = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
				String line = "";

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				return result.toString();
			}
		});
	}

	/**
	 * http get方式发送请求
	 * 
	 * @param call
	 * @throws Exception
	 */
	private static final Object executeGet(HttpClientHandlerCallback call) throws Exception {

		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod();
		
		try {
			// 执行
			return call.execute(client, getMethod);
		} catch (Exception e) {
			
			if (e instanceof SocketTimeoutException){
				LOGER.error(String.format("[GET请求] 链接：%s请求超时！", getMethod.getURI()));
				return "";
			}
			
			throw e;
			
		} finally {

			// 关闭httpclient连接
			getMethod.releaseConnection();
			
			try {
				((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * http post方式发送请求
	 * 
	 * @param call
	 * @throws Exception
	 */
	private static final Object executePost(HttpClientHandlerCallback call) throws Exception {

		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod();

		try {
			// 执行
			return call.execute(client, method);
		} catch (Exception e) {
			throw e;
		} finally {

			// 关闭httpclient连接
			method.releaseConnection();
			((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
		}
	}

	/**
	 * http delete方式发送请求
	 * 
	 * @param call
	 * @throws Exception
	 */
	private static final Object executeDelete(HttpClientHandlerCallback call) throws Exception {

		HttpClient client = new HttpClient();
		DeleteMethod method = new DeleteMethod();
		
		try {
			// 执行
			return call.execute(client, method);
		} catch (Exception e) {
			throw e;
		} finally {

			// 关闭httpclient连接
			method.releaseConnection();
			((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
		}
	}
	
	/**
	 * http delete方式发送请求
	 * 
	 * @param call
	 * @throws Exception
	 */
	private static final Object executePatch(HttpClientHandlerCallback call) throws Exception {

		HttpClient client = new HttpClient();
		PatchMethod method = new PatchMethod();
		
		try {
			// 执行
			return call.execute(client, method);
		} catch (Exception e) {
			throw e;
		} finally {

			// 关闭httpclient连接
			method.releaseConnection();
			((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
		}
	}

	/**
	 * <p>
	 * Description:[post调用接口]
	 * </p>
	 * 
	 * @param url
	 *            ：请求路径
	 * @param params
	 *            ：请求参数
	 * @param charsetStr
	 *            ：编码方式，如果为null,默认utf-8
	 * @return
	 */
	public static String post(String url, String params, String charsetStr) {
		Charset charset = null;
		if (StringUtils.isNotBlank(charsetStr)) {
			charset = Charset.forName(charsetStr);
		}

		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		try {
			client = wrapClient(client);

			HttpPost post = new HttpPost(url);

			/* 请求超时时间 */
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);

			/* 读取response超时的时间 */
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

			post.setEntity(new StringEntity(params, charset == null ? Consts.UTF_8 : null));

			HttpResponse res = client.execute(post);

			HttpEntity entity = res.getEntity();
			if (entity == null) {
				return null;
			}

			return EntityUtils.toString(res.getEntity(), charset == null ? Consts.UTF_8 : null);
		} catch (Exception e) {
			throw new SystemRunTimeException(e);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static String post(String url, Map<String, String> params) {
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		try {
			client = wrapClient(client);

			org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(url);
			List<org.apache.http.NameValuePair> nvps = new ArrayList<org.apache.http.NameValuePair>();

			for (Entry<String, String> entry : params.entrySet()) {
				if (StringUtils.isNotBlank(entry.getValue())) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			/* 请求超时时间 */
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);

			/* 读取response超时的时间 */
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

			post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			HttpResponse res = client.execute(post);

			HttpEntity entity = res.getEntity();

			if (entity == null) {
				return null;
			}

			return EntityUtils.toString(entity, Consts.UTF_8);
		} catch (Exception e) {
			new SystemRunTimeException("Connection timeout for rest interface!", e);
		} finally {
			client.getConnectionManager().shutdown();
		}
		return null;
	}

	/**
	 * 把request中的参数拼接到url中
	 * 
	 * @param request
	 * @param url
	 * 
	 * @return 带参数的url
	 */
	public static String joinRequestParamsUrl(HttpServletRequest request, String url) {

		if (request == null) {
			return url;
		}

		Map<?, ?> params = request.getParameterMap();

		if (params.isEmpty()) {
			return url.toString();
		}

		StringBuffer ub = new StringBuffer(url);

		if (!StringUtils.contains(ub, "?")) {
			ub.append("?");
		}

		for (Entry<?, ?> entry : params.entrySet()) {

			Object key = entry.getKey();

			if (key != null && StringUtils.equals("code", key.toString())) {
				continue;
			}

			if (entry.getValue().getClass() == String[].class) {

				Object[] vs = (Object[]) entry.getValue();

				ub.append(entry.getKey().toString() + "=" + vs[0].toString() + "&");
				continue;
			}

			ub.append(entry.getKey().toString() + "=" + entry.getValue().toString() + "&");
		}

		return StringUtils.substringBeforeLast(ub.toString(), "&");
	}

	/**
	 * 把request中的url与参数拼接成一个完整的url地址
	 * 
	 * @param request
	 * 
	 * @return 带参数的url
	 */
	public static String joinRequestParamsUrl(HttpServletRequest request) {

		if (request == null) {
			return null;
		}

		StringBuffer url = request.getRequestURL();
		Map<?, ?> params = request.getParameterMap();

		if (params.isEmpty()) {
			return url.toString();
		}

		url.append("?");

		for (Entry<?, ?> entry : params.entrySet()) {

			if (entry.getValue().getClass() == String[].class) {

				Object[] vs = (Object[]) entry.getValue();

				url.append(entry.getKey().toString() + "=" + vs[0].toString() + "&");
				continue;
			}

			url.append(entry.getKey().toString() + "=" + entry.getValue().toString() + "&");
		}

		return StringUtils.substringBeforeLast(url.toString(), "&");
	}

	/**
	 * 
	 * @param url
	 *            ：请求url
	 * @param jsonParams
	 *            :请求参数为json字符串格式
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String post(String url, String jsonParams) {

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			StringEntity stringEntity = new StringEntity(jsonParams, Consts.UTF_8);

			stringEntity.setContentType(APPLICATION_JSON);// 发送json数据需要设置contentType

			post.setEntity(stringEntity);

			HttpResponse res = client.execute(post);

			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 返回json格式
				return EntityUtils.toString(res.getEntity(), Consts.UTF_8);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * <p>
	 * Description:[httpGet请求]
	 * </p>
	 * 
	 * @param url
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String get(String url) {
		org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			/* 请求超时时间 */
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			/* 读取response超时的时间 */
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

			HttpResponse result = httpClient.execute(httpGet);
			if (result.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = result.getEntity();
				return streamConvertString(httpEntity.getContent());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static org.apache.http.client.HttpClient wrapClient(org.apache.http.client.HttpClient base)
			throws Exception {
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
					throws java.security.cert.CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
					throws java.security.cert.CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		ClientConnectionManager ccm = base.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", ssf, 443));
		return new DefaultHttpClient(ccm, base.getParams());
	}

	private static String streamConvertString(InputStream inputStream) {
		StringBuffer buffer = new StringBuffer();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, CHARSET_UTF8));
			String line = br.readLine();
			while (StringUtils.isNotBlank(line)) {
				buffer.append(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public static String sha256_HMAC(String message, String secret) {
		String hash = "";
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(),
					"HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			hash = byteArrayToHexString(bytes);
		} catch (Exception e) {
			System.out.println("Error HmacSHA256 ===========" + e.getMessage());
		}
		return hash;
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}

}