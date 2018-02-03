package com.ruisi.bi.app.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class ImageUtil {

	private static final String SDCARD_CACHE_IMG_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/cloudcidtech/images/";
	private static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath();
	private static final int SCALE = 1;
	private static final int IMG_FROM_LOCAL = 0x01;
	private static final int IMG_FROM_NET = 0x02;

	public static final String getSavePath() {
		return SDCARD_CACHE_IMG_PATH;
	}

	public static final String getSdcardPath() {
		return SDCARD_PATH;
	}

	public static String getLocalPathFromUrl(String imageUrl) {

		if (imageUrl == null || "".equals(imageUrl)) {
			return null;
		}
		return SDCARD_CACHE_IMG_PATH + md5(imageUrl);
	}

	/**
	 * 把bitmap转换成String
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] bitmapToString(String filePath) {
		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) {
			baos.reset();
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		byte[] b = baos.toByteArray();
		return b;
	}

	/**
	 * 计算图片的缩放量
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @param imagesrc
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// float scaleHeight = (float)720 / height;
		float scaleWidht = (float) 720 / width;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidht, scaleWidht);
		// return BitmapFactory.decodeFile(filePath, options);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	public static File creatFile(String imagePathLocal) {
		File f = new File(imagePathLocal);
		if (!f.exists()) {
			File parentFile = f.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return f;
	}

	/**
	 * 存储图片
	 * 
	 * @param imagePathLocal
	 * @param buffer
	 * @throws IOException
	 */
	private static void saveImage(String imagePathLocal, byte[] buffer) {
		if (buffer == null || buffer.length < 1)
			return;
		Log.e("saveImage: ", imagePathLocal.toString());
		File f = creatFile(imagePathLocal);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(buffer);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 读取图片
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImageFromLocal(String imagePath) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(imagePath);
		} catch (OutOfMemoryError e1) {
			return null;
		} catch (Exception ooe) {
			return null;
		}
		return bitmap;
	}

	public static boolean fileIsExit(String imagePath) {
		if (new File(imagePath).exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *Bitmap转换成bytes
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bas);
		return bas.toByteArray();
	}

	/**
	 *加载图片
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Bitmap loadImage(final String imgUrl,
			final ImageCallback callback) {
		final String imgPath = getCacheImgPath() + "/head/" + md5(imgUrl);
		Bitmap bitmap = getImageFromLocal(imgPath);
		if (bitmap != null) {
			return bitmap;
		} else {// 从网上加�?
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.obj != null) {
						Bitmap bitmap = (Bitmap) msg.obj;
						callback.loadImage(bitmap, imgPath);
					}
				}
			};

			Runnable runnable = new Runnable() {
				public void run() {
					try {
						URL url = new URL(imgUrl);
						Log.e("图片加载", imgUrl);
						URLConnection conn = url.openConnection();
						conn.setConnectTimeout(10000);
						InputStream is = conn.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);
						Bitmap bitmap = BitmapFactory.decodeStream(bis);
						System.out.println("hello");
						// 保存文件到sd�?
						saveImage(imgPath, bitmap2Bytes(bitmap));
						Message msg = handler.obtainMessage();
						msg.obj = bitmap;
						handler.sendMessage(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			TaskManager.getInstance().addTask(runnable);
		}
		return null;
	}

	/*
	 * 得到图片字节�?数组大小
	 */
	public static byte[] readStream(InputStream inStream) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outStream.toByteArray();
	}

	private static String getCacheImgPath() {
		return ImageUtil.SDCARD_CACHE_IMG_PATH;
	}

	public static void loadImage(final String imgUrl, final ImageView imageView) {
		final String imgPathByURL = getLocalPathFromUrl(imgUrl);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// if(msg.what==IMG_FROM_LOCAL){
				//
				// }else if (msg.what==IMG_FROM_NET) {
				//
				// }
				if (msg.obj != null) {
					Bitmap bitmap = (Bitmap) msg.obj;
					imageView.setImageBitmap(bitmap);
					// callback.loadImage(bitmap, imageView);
				}
			}
		};
		if (new File(imgPathByURL).exists()) {
			loadImgByLocal(handler, imgUrl, imgPathByURL);
			// imageView.setImageBitmap(bitmap);
		} else {// ���������?
			loadImgByNet(handler, imgUrl, imgPathByURL);

		}
	}

	public static void loadImgByLocal(final Handler handler,
			final String imgUrl, final String imgPath) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bitmap = getImageFromLocal(imgPath);
				if (bitmap == null) {
					loadImgByNet(handler, imgUrl, imgPath);
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = bitmap;
					msg.what = IMG_FROM_LOCAL;
					handler.sendMessage(msg);
				}
			}
		};
		TaskManager.getInstance().addTask(runnable);
	}

	public static void loadImgByNet(final Handler handler, final String imgUrl,
			final String imgPath) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				URL url = null;
				try {
					url = new URL(imgUrl);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				URLConnection conn = null;
				BufferedInputStream bis = null;
				try {
					conn = url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					bis = new BufferedInputStream(conn.getInputStream(), 8192);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				Bitmap bitmap = null;
				try {
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = SCALE;
					bitmap = BitmapFactory.decodeStream(bis, null, opts);
				} catch (OutOfMemoryError e1) {
					return;
				} catch (Exception ooe) {
					return;
				}
				if (bitmap != null) {
					saveImage(imgPath, bitmap2Bytes(bitmap));
					Message msg = handler.obtainMessage();
					msg.obj = bitmap;
					msg.what = IMG_FROM_NET;
					handler.sendMessage(msg);
				}
			}
		};
		TaskManager.getInstance().addTask(runnable);
	}

	public static void loadImageSmile(final String imgUrl,
			final ImageView imageView, final ImageViewCallback callback) {
		final String imgPathByURL = getLocalPathFromUrl(imgUrl);
		Bitmap bitmap = getImageThumbnail(imgPathByURL, 80, 60);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {// ���������?
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.obj != null) {
						Bitmap bitmap = (Bitmap) msg.obj;
						callback.loadImage(bitmap, imageView);
					}
				}
			};

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					URL url = null;
					try {
						url = new URL(imgUrl);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}
					URLConnection conn = null;
					BufferedInputStream bis = null;
					try {
						conn = url.openConnection();
						conn.setConnectTimeout(5000);
						conn.setReadTimeout(5000);
						conn.connect();
						bis = new BufferedInputStream(conn.getInputStream(),
								8192);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}
					Bitmap bitmap = null;
					try {
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inSampleSize = SCALE;
						bitmap = BitmapFactory.decodeStream(bis, null, opts);
					} catch (Exception e1) {
						return;
					}
					if (bitmap != null) {
						saveImage(imgPathByURL, bitmap2Bytes(bitmap));
						Message msg = handler.obtainMessage();
						msg.obj = getImageThumbnail(imgPathByURL, 80, 60);
						;
						handler.sendMessage(msg);
					}
				}
			};
			TaskManager.getInstance().addTask(runnable);
		}
	}

	private static String md5(String paramString) {
		String returnStr;
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			returnStr = byteToHexString(localMessageDigest.digest());
			return returnStr;
		} catch (Exception e) {
			return paramString;
		}
	}

	/**
	 * ��ָ��byte����ת����16�����ַ�
	 * 
	 * @param b
	 * @return
	 */
	private static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	/**
	 * 
	 * @author Mathew
	 * 
	 */
	public interface ImageCallback {
		public void loadImage(Bitmap bitmap, String imagePath);
	}

	public interface ImageViewCallback {
		public void loadImage(Bitmap bitmap, ImageView imageView);
	}

	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		if (fileIsExit(imagePath)) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// 获取这个图片的宽和高，注意此处的bitmap为null
			bitmap = BitmapFactory.decodeFile(imagePath, options);
			options.inJustDecodeBounds = false; // 设为 false
			// 计算缩放�?
			int h = options.outHeight;
			int w = options.outWidth;
			int beWidth = w / width;
			int beHeight = h / height;
			int be = 1;
			if (beWidth < beHeight) {
				be = beWidth;
			} else {
				be = beHeight;
			}
			if (be <= 0) {
				be = 1;
			}
			options.inSampleSize = be;
			// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为
			// false
			bitmap = BitmapFactory.decodeFile(imagePath, options);
			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

}
