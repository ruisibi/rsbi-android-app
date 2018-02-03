package com.ruisi.bi.app.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ruisi.bi.app.bean.CookieSerializBean;
import com.ruisi.bi.app.bean.UserBean;
import com.ruisi.bi.app.net.MyCookieJar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Cookie;


public class UserMsg {
	private static SharedPreferences sp;
	public static void initUserMsg(Context context) {
		sp = context.getSharedPreferences("UserMsg", Context.MODE_PRIVATE);
	}

	public static void saveRigester(UserBean info){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {

			List<Cookie> cookies = MyCookieJar.getCookies();
			List<CookieSerializBean> serCookies = new ArrayList<CookieSerializBean>();
			for(Cookie ck : cookies){
				CookieSerializBean b = new CookieSerializBean();
				b.domain = (ck.domain());
				b.name = (ck.name());
				b.value = (ck.value());
				b.expiresAt = ck.expiresAt();
				b.path = ck.path();
				serCookies.add(b);
			}
			//把用户cookies持久化
			ObjectOutputStream outputStream = new ObjectOutputStream(os);
			outputStream.writeObject(serCookies);
			Editor editor = sp.edit();
			byte[] bt = os.toByteArray();
			String str = byteArrayToHexString(bt);
			editor.putString("cookies", str);

			//兼容以前版本，放入tokeen
			editor.putString("token", info.token);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void removeCookie(){
		Editor editor = sp.edit();
		//兼容以前版本，放入tokeen
		editor.remove("token");
		editor.remove("cookies");
		editor.commit();
	}

	public static String getToken(){
		return sp.getString("token", null);
	}

	/**
	 * Using some super basic byte array <-> hex conversions so we don't have to rely on any
	 * large Base64 libraries. Can be overridden if you like!
	 *
	 * @param bytes byte array to be converted
	 * @return string containing hex values
	 */
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte element : bytes) {
			int v = element & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase(Locale.US);
	}

	/**
	 * Converts hex values from strings to byte arra
	 *
	 * @param hexString string of hex-encoded values
	 * @return decoded byte array
	 */
	public static byte[] hexStringToByteArray(String hexString) {
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}

	public static void savaAppStatus(boolean isUsed){
		Editor editor = sp.edit();
		editor.putBoolean("isUsed", isUsed);
		editor.commit();
	}
	public static boolean  getAppStatus(){
		return sp.getBoolean("isUsed", true);
	}
	public static String getUserId(){
		return sp.getString("UserId", null);
	}
	public static List<Cookie> getCookies(){
		List<Cookie> ret = new ArrayList<Cookie>();
		String cookieString = sp.getString("cookies", null);
		if(cookieString != null) {
			byte[] bytes = hexStringToByteArray(cookieString);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
				List<CookieSerializBean> serCookies  = ((List<CookieSerializBean>) objectInputStream.readObject());
				for(int i=0; serCookies != null && i<serCookies.size(); i++){
					CookieSerializBean b = serCookies.get(i);
					Cookie.Builder builder = new Cookie.Builder();
					builder.name(b.name);
					builder.value(b.value);
					builder.expiresAt(b.expiresAt);
					builder.domain(b.domain);
					builder.path(b.path);
					Cookie ck = builder.build();
					ret.add(ck);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally{
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	public static String getName(){
		return sp.getString("name", null);
	}
	public static void clearAccount(){
		sp.edit().clear().commit();
	}
	public static void saveTheme(String themes){
		Editor editor = sp.edit();
		editor.putString("Themes", themes);
		editor.commit();
	}
	public static String getThemes(){
		return sp.getString("Themes", null);
	}
	public static void saveFenxi(String Fenxi){
		Editor editor = sp.edit();
		editor.putString("Fenxi", Fenxi);
		editor.commit();
	}
	public static String getFenxi(){
		return sp.getString("Fenxi", null);
	}
	public static void saveForm(String tag,String form){
		Editor editor = sp.edit();
		editor.putString("Form"+tag, form);
		editor.commit();
	}
	public static String getForm(String tag){
		return sp.getString("Form"+tag, null);
	}
	public static void saveTuQuxian(String tag,String form){
		Editor editor = sp.edit();
		editor.putString("Quxian"+tag, form);
		editor.commit();
	}
	public static String getTuQuxian(String tag){
		return sp.getString("Quxian"+tag, null);
	}
	public static void saveTuZhuxing(String tag,String form){
		Editor editor = sp.edit();
		editor.putString("Zhuxing"+tag, form);
		editor.commit();
	}
	public static String getTuZhuxing(String tag){
		return sp.getString("Zhuxing"+tag, null);
	}
	public static void saveTuTiaoxing(String tag,String form){
		Editor editor = sp.edit();
		editor.putString("Tiaoxing"+tag, form);
		editor.commit();
	}
	public static String getTuTiaoxing(String tag){
		return sp.getString("Tiaoxing"+tag, null);
	}
	public static void saveTuBingxing(String tag,String form){
		Editor editor = sp.edit();
		editor.putString("Bingxing"+tag, form);
		editor.commit();
	}
	public static String getTuBingxing(String tag){
		return sp.getString("Bingxing"+tag, null);
	}
	public static void saveTuLeida(String tag,String form){
		Editor editor = sp.edit();
		editor.putString("Leida"+tag, form);
		editor.commit();
	}
	public static String getTuLeida(String tag){
		return sp.getString("Leida"+tag, null);
	}
	public static void savefenxiId(int tid,int id){
		Editor editor = sp.edit();
		editor.putInt("fenxiID"+tid, id);
		editor.commit();
	}
	public static int getFenxiId(int tid){
		return sp.getInt("fenxiID"+tid, -1);
	}
	public static void savefenxiJson(int tid,String json){
		Editor editor = sp.edit();
		editor.putString("fenxiJson"+tid, json);
		editor.commit();
	}
	public static String getFenxiJson(int tid){
		return sp.getString("fenxiJson"+tid, null);
	}
	public static void saveIPJson(String json){
		Editor editor = sp.edit();
		editor.putString("IP", json);
		editor.commit();
	}
	public static String getIPJson(){
		return sp.getString("IP", null);
	}

	public static void saveChannelId(String channelId) {
			Editor editor = sp.edit();
			editor.putString("channelId", channelId);
			editor.commit();
	}

	public static String getChannelId() {
		return sp.getString("channelId", null);
	}

	public static void saveHost(String host){
		Editor editor = sp.edit();
		editor.putString("host", host);
		editor.commit();
	}

	public static String getHost(){
		return sp.getString("host", null);
	}
}
