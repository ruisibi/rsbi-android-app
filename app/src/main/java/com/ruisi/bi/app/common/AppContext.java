package com.ruisi.bi.app.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.util.ImageUtil;
import com.ruisi.bi.app.util.SystemBarTintManager;

public class AppContext extends Application {
	private static AppContext context;
	public static int currentPage = 1;
	public static DisplayMetrics dm;
	private static DisplayImageOptions options;
	public static int video_progress;
	public static String avatarImageName;
	public static int[] colors=new int[24];

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
		// 关闭友盟默认的统计方式
		dm = new DisplayMetrics();
		super.onCreate();
		UserMsg.initUserMsg(this);
		initImageLoader(getApplicationContext());
		colors[0]=0xFF4572A7;
		colors[1]=0xFFAA4643;
		colors[2]=0xFF89A54E;
		colors[3]=0xFF80699B;
		colors[4]=0xFF3D96AE;
		colors[5]=0xFFDB843D;
		colors[6]=0xFF92A8CD;
		colors[7]=0xFFA47D7C;
		colors[8]=0xFFB5CA92;
		colors[9]=0xFF1D8BD1;
		colors[10]=0xFFF1683C;
		colors[11]=0xFF2AD62A;
		colors[12]=0xFFDBDC25;
		colors[13]=0xFF367517;
		colors[14]=0xFF9C9900;
		colors[15]=0xFF103667;
		colors[16]=0xFF211551;
		colors[17]=0xFF79378B;
		colors[18]=0xFFAF4A92;
		
		colors[19]=0xFF8B8B00;
		colors[20]=0xFF8B2323;
		colors[21]=0xFF8470FF;
		colors[22]=0xFF7CFC00;
		colors[23]=0xFF00FF00;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.cacheOnDisc(true).cacheInMemory(true).cacheOnDisk(true)
//				.imageScaleType(ImageScaleType.EXACTLY).build();
		File cacheDir = ImageUtil.creatFile(ImageUtil.getSavePath()+"/cacheImg/Cache");
		if  (!cacheDir .exists()  && !cacheDir .isDirectory())      
		{       
		    cacheDir .mkdir();    
		}
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(getOptions())
				.discCacheSize(50 * 1024 * 1024).memoryCacheSizePercentage(13)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheExtraOptions(480, 480)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.threadPriority(Thread.NORM_PRIORITY - 1);
		ImageLoaderConfiguration imgConfig = builder.build();
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(
				imgConfig);
	}

	public static DisplayImageOptions getOptions() {
		if (options == null) {
			options = new DisplayImageOptions.Builder().cacheOnDisc(true).cacheInMemory(true).cacheOnDisc(true)
					.imageScaleType(ImageScaleType.EXACTLY).build();
		}
		return options;
	}

	/**
	 * åˆ¤æ–­å½“å‰�ç½‘ç»œæ˜¯å�¦è¿žæŽ¥
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return null != networkInfo && networkInfo.isConnected();
	}

	/**
	 * åˆ¤æ–­å½“å‰�ç½‘ç»œæ˜¯å�¦ä¸ºWIFI
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if (null != netInfo
				&& netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	private List<Activity> mList = new LinkedList<Activity>();

	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {

		try {
			for (Activity activity : mList) {
				if (activity != null) {
					activity.finish();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// ç»“æ�Ÿè¿›ç¨‹
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		System.gc();
	}

	private boolean getSourceFile(int id, String name, String folder) {
		File file = null;
		boolean bResult = false;
		try {
			File cascadeDir = getDir(folder, Context.MODE_PRIVATE);
			file = new File(cascadeDir, name);
			if (!file.exists()) {
				InputStream is = getResources().openRawResource(id);
				FileOutputStream os = new FileOutputStream(file);

				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				is.close();
				os.close();
			}
			bResult = true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("CidtechLMS", "Failed to load file " + name
					+ ". Exception thrown: " + e);
		}

		return bResult;
	}

	public Context getInstance() {
		// TODO Auto-generated method stub
		return this;
	}

	public static Context getApplication() {
		// TODO Auto-generated method stub
		return context;
	}
	public static void setStatuColor(Activity context){
		setStatuColor(context, R.color.title_color);
	}
	public static void setStatuColor(Activity context,int color){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(context,true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(context);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(color);
	}
	@TargetApi(19) 
	private static void setTranslucentStatus(Activity context ,boolean on) {
		Window win = context.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
}
