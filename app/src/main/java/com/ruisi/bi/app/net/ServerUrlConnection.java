package com.ruisi.bi.app.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ruisi.bi.app.FormActivity;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.parser.FormParser;
import com.ruisi.bi.app.parser.TuBingxingParser;
import com.ruisi.bi.app.parser.TuLeidaParser;
import com.ruisi.bi.app.parser.TuParser;
import com.ruisi.bi.app.parser.TuZhuxingParser;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerUrlConnection implements Runnable {
	private static final int SUCCEEND = 1001;
	private static final int FAILED = 1002;
	private static final int EXCEPTION = 1003;
	private static final int ConnectionTimeout = 10000;// 连接超时
	private static final int SoTimeout = 10000;// 响应超时
	/** 默认的套接字缓冲区大�?*/
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	/** http请求�?��并发连接�?*/
	private static final int DEFAULT_MAX_CONNECTIONS = 10;
	private ServerCallbackInterface serverCallbackInterface;
	private ServerEngine serverEngine;
	private String finalUrl;
	private RequestVo vo;

	public static BasicHeader[] headers = new BasicHeader[2];
	static {
		headers[0] = new BasicHeader("Content-Type", "application/json");
		headers[1] = new BasicHeader("Accept", "application/json");// 手机串号
	}

	/**
	 * Handler向主线程发�?消息,进行回调
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String uuid=bundle.getString("identifiy");
			if (serverEngine.CloseConnection(uuid)) {
//				System.out.println("关闭链接---�? + bundle.getString("identifiy"));
			}
			switch (msg.what) {
			case SUCCEEND:
				if (!Thread.interrupted()) {
					serverCallbackInterface.succeedReceiveData(msg.obj,uuid);
				}
				break;
			case FAILED:
				ServerErrorMessage errorMessage = (ServerErrorMessage) msg.obj;
				if (!Thread.interrupted())
					serverCallbackInterface.failedWithErrorInfo(errorMessage,uuid);
				break;
			case EXCEPTION:
				ServerErrorMessage errorMessage2 = (ServerErrorMessage) msg.obj;
				if (!Thread.interrupted()) {
					serverCallbackInterface.failedWithErrorInfo(
							errorMessage2, uuid);
				}
				break;
			default:
				break;
			}

		};
	};

	public ServerUrlConnection(ServerEngine serverEngine2,
			ServerCallbackInterface serverCallbackInterface2, RequestVo vo) {
		this.serverEngine = serverEngine2;
		this.serverCallbackInterface = serverCallbackInterface2;
		this.vo = vo;
		this.finalUrl = makeUrl(vo);
	}

	@Override
	public void run() {
		if (!UserMsg.getAppStatus()){
			Log.e("LLL", "服务异常！！！");
			return;
		}
		if (null != vo.Type && !"".equals(vo.Type)
				&& APIContext.POST.equals(vo.Type)) {
			postRequest();
		} else if (null != vo.Type && !"".equals(vo.Type)
				&& APIContext.GET.equals(vo.Type)) {
			getRequest();
		}
	}

	public String makeUrl(RequestVo vo) {
		StringBuffer finalurl = null;
		if (vo.hostType != null && "uploadRoute".equals(vo.hostType)) {
			finalurl = new StringBuffer(APIContext.HOST);
		} else {
			finalurl = new StringBuffer(APIContext.HOST);
		}
		if (null != vo.modulePath && !"".equals(vo.modulePath)) {
			finalurl.append(vo.modulePath);
			finalurl.append("/");
			finalurl.append(vo.functionPath);
		} else {
			finalurl.append(vo.functionPath);
		}
		if (APIContext.GET.equals(vo.Type)) {
			try {
				if (vo.requestDataMap != null) {
					finalurl.append(makeURL(vo.modulePath, vo.functionPath,
							vo.requestDataMap));
				} 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return finalurl.toString();
	}

	/**
	 * 请求为GET方式时拼揍URL地址
	 * 
	 * @param modulePath
	 *            请求的模块路�?
	 * @param functionPath
	 *            请求的具体功能路�?
	 * @param dataMap
	 *            请求时携带的数据
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String makeURL(String modulePath, String functionPath,
			Map<String, String> dataMap) throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder();
		if (url.indexOf("?") < 0)
			url.append('?');
		int count = 0;
		for (String name : dataMap.keySet()) {
			if (count > 0) {
				url.append("&");
			}
			count++;
			url.append(name);
			url.append('=');
			url.append(URLEncoder.encode(String.valueOf(dataMap.get(name)),
					"UTF-8"));
		}
		return url.toString().replace("?&", "?");
	}

	private void getRequest() {
		//保持cookie
		OkHttpClient client = new OkHttpClient.Builder().cookieJar(new MyCookieJar()).build();
		Request request = new Request.Builder().url(finalUrl).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			String cookie = response.header("Cookie");
			System.out.print("cookie = " + cookie);
			String jsonStr = response.body().string();
			try {
				System.out.println("jsonStr---->" + finalUrl + "---->" + jsonStr);
				Object obj = vo.parser.parse(jsonStr);
				if (obj != null) {
//						if (vo.isSaveToLocation) {
//							Object dbObj=vo.parser.saveData(obj,vo.dm);
//							sendSuccendMessage(dbObj, vo.uuId);
//						}else {
					sendSuccendMessage(obj, vo.uuId);
//						}
				} else {
					sendExceptionMessage(null,"数据异常", vo.uuId);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				sendExceptionMessage(e, "数据异常", vo.uuId);
			}
		} catch (IOException e) {
			e.printStackTrace();
			sendExceptionMessage(e, "网络异常", vo.uuId);
		}
	}

	/**
	 * 处理POST方式请求
	 */
	private void postRequest() {
		//保持cookie
		OkHttpClient client = new OkHttpClient.Builder().cookieJar(new MyCookieJar()).build();
		FormBody.Builder builder = new FormBody.Builder();
		if (vo.requestDataMap != null) {
			ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
			for (Map.Entry<String, String> entry : vo.requestDataMap.entrySet()) {
				builder.add(entry.getKey(), entry.getValue());
			}
		}
		RequestBody body  =  builder.build();

		// 3 创建请求方式
		Request request = new Request.Builder().url(finalUrl).post(body).build();

		// 4 执行请求操作
		try {
			Response response = client.newCall(request).execute();
			if(response.isSuccessful()){
				String jsonStr = response.body().string();
				System.out.println("jsonStr---->" + finalUrl + "---->" + jsonStr);
				if (vo.isSaveToLocation) {
					if (vo.context instanceof FormActivity) {
						UserMsg.saveForm(vo.tag01,jsonStr.toString());
					}else if (vo.parser instanceof TuParser) {
						UserMsg.saveTuQuxian(vo.tag01,jsonStr.toString());
					}else if (vo.parser instanceof TuZhuxingParser) {
						UserMsg.saveTuZhuxing(vo.tag01,jsonStr.toString());
					}else if (vo.parser instanceof TuBingxingParser) {
						UserMsg.saveTuBingxing(vo.tag01,jsonStr.toString());
					}else if (vo.parser instanceof TuLeidaParser) {
						UserMsg.saveTuLeida(vo.tag01,jsonStr.toString());
					}
				}
				try {
					Object obj = vo.parser.parse(jsonStr);
					if (obj != null) {
						sendSuccendMessage(obj, vo.uuId);
					} else {
						sendExceptionMessage(null,"数据异常", vo.uuId);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sendExceptionMessage(e, "数据异常", vo.uuId);
				}
			}else{
				sendExceptionMessage(null,"服务器异常：", vo.uuId);
			}
		} catch (IOException e) {
			sendExceptionMessage(e, "网络异常", vo.uuId);
		}
	}

	public void sendExceptionMessage(Exception e, String des, String uuId) {
		
		ServerErrorMessage exceptionMessage = new ServerErrorMessage();
		exceptionMessage.setErrormessage(e!=null?e.getMessage():"");
		exceptionMessage.setErrorDes(des);
		Message message = Message.obtain();
		message.what = EXCEPTION;
		Bundle bundle = new Bundle();
		bundle.putString("identifiy", uuId);
		message.setData(bundle);
		message.obj=exceptionMessage;
		handler.sendMessage(message);
	}

	public void sendSuccendMessage(Object obj, String uuId) {
		Message message = Message.obtain();
		message.what = SUCCEEND;
		Bundle bundle = new Bundle();
		bundle.putString("identifiy", uuId);
		message.obj = obj;
		message.setData(bundle);
		handler.sendMessage(message);
	}

	public void sendFailedMessage(String failedMessage, String uuId) {
		Message message = Message.obtain();
		message.what = FAILED;
		Bundle bundle = new Bundle();
		bundle.putString("identifiy", uuId);
		message.obj = failedMessage;
		message.setData(bundle);
		handler.sendMessage(message);
	}
}
