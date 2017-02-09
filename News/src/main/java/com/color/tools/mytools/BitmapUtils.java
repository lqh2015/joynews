package com.color.tools.mytools;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import com.hzpd.utils.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {
	public static String getBase64FromBitmap(Bitmap bitmap) {
		String base64Str = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				byte[] bitmapBytes = baos.toByteArray();
				base64Str = Base64.encodeToString(bitmapBytes, 2);
				Log.d("base64Str", base64Str);

				baos.flush();
				baos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return base64Str;
	}

	public static Bitmap getBitmapFromBase64(String base64Str) {
		if (TextUtils.isEmpty(base64Str)) {
			return null;
		}

		byte[] bytes = Base64.decode(base64Str, 2);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	public static Bitmap getFixSizeBitmap(int byteLength, Bitmap bitmap) {
		int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();

		if ((bitmap == null) || (byteLength == 0) || (bitmapSize <= byteLength)) {
			return bitmap;
		}

		if (bitmap != null) {
			while (bitmap.getRowBytes() * bitmap.getHeight() > byteLength) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();

				Log.d("BasePhtotActivity---getThumbnailBitmap--", "width:"
						+ width + "----" + "height:" + height);

				bitmap = ThumbnailUtils.extractThumbnail(bitmap, width / 2,
						height / 2);
			}

		}

		return bitmap;
	}

	public static Bitmap getResizedBitmap(Context context, Uri uri,int widthLimit, int heightLimit) throws IOException {
		String path = null;
		Bitmap result = null;

		if (uri.getScheme().equals("file")) {
			path = uri.getPath();
		} else if (uri.getScheme().equals("content")) {
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { "_data" }, null, null, null);
			cursor.moveToFirst();
			path = cursor.getString(0);
			cursor.close();
		} else {
			return null;
		}

		ExifInterface exifInterface = new ExifInterface(path);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		int orientation = exifInterface.getAttributeInt("Orientation", 0);

		if ((orientation == 6) || (orientation == 8) || (orientation == 5)
				|| (orientation == 7)) {
			int tmp = widthLimit;
			widthLimit = heightLimit;
			heightLimit = tmp;
		}

		int width = options.outWidth;
		int height = options.outHeight;
		int sampleW = 1;
		int sampleH = 1;
		while (width / 2 > widthLimit) {
			width /= 2;
			sampleW <<= 1;
		}

		while (height / 2 > heightLimit) {
			height /= 2;
			sampleH <<= 1;
		}
		int sampleSize = 1;

		options = new BitmapFactory.Options();
		if ((widthLimit == 2147483647) || (heightLimit == 2147483647))
			sampleSize = Math.max(sampleW, sampleH);
		else {
			sampleSize = Math.max(sampleW, sampleH);
		}
		options.inSampleSize = sampleSize;
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			options.inSampleSize <<= 1;
			bitmap = BitmapFactory.decodeFile(path, options);
		}

		Matrix matrix = new Matrix();
		if (bitmap == null) {
			return result;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		if ((orientation == 6) || (orientation == 8) || (orientation == 5)
				|| (orientation == 7)) {
			int tmp = w;
			w = h;
			h = tmp;
		}
		switch (orientation) {
		case 6:
			matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
			break;
		case 3:
			matrix.setRotate(180.0F, w / 2.0F, h / 2.0F);
			break;
		case 8:
			matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
			break;
		case 2:
			matrix.preScale(-1.0F, 1.0F);
			break;
		case 4:
			matrix.preScale(1.0F, -1.0F);
			break;
		case 5:
			matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
			matrix.preScale(1.0F, -1.0F);
			break;
		case 7:
			matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
			matrix.preScale(1.0F, -1.0F);
		}

		float xS = widthLimit / bitmap.getWidth();
		float yS = heightLimit / bitmap.getHeight();

		matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));
		try {
			result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			Log.d("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight()
					+ "Width:" + bitmap.getHeight() + "matrix:" + xS + " " + yS);
			return null;
		}
		return result;
	}

	public static Bitmap getResizedBitmap(Bitmap bitmap, int widthLimit,
			int heightLimit) {
		Bitmap result = null;

		Matrix matrix = new Matrix();
		if (bitmap == null) {
			return result;
		}

		float xS = widthLimit / bitmap.getWidth();
		float yS = heightLimit / bitmap.getHeight();

		matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));
		try {
			result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			Log.d("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight()
					+ "Width:" + bitmap.getHeight() + "matrix:" + xS + " " + yS);
			return null;
		}
		return result;
	}

	public static Bitmap getResizedBitmap(Context context, Uri uri,int quality, int widthLimit, int heightLimit) {
		BitmapFactory.Options opt = decodeBitmapOptionsInfo(context, uri);

		int outSize = opt.outWidth > opt.outHeight ? opt.outWidth
				: opt.outHeight;

		int s = 1;
		while (outSize / s > widthLimit) {
			s++;
		}

		Log.d("Util",
				String.format(
						"OUTPUT: x:%1$s  y:%2$s",
						new Object[] { String.valueOf(opt.outWidth / s),
								String.valueOf(opt.outHeight / s) }));

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = s;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds = false;

		InputStream input = null;
		try {
			input = context.getContentResolver().openInputStream(uri);
			Bitmap b = BitmapFactory.decodeStream(input, null, options);
			String path = uri.getPath();
			if (!TextUtils.isEmpty(path)) {
				b = rotateBitMap(path, b);
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.PNG, quality, os);
			return b;
		} catch (Exception e) {
			if (input == null) {
				input = FileUtil.getFileInputStream(uri.getPath());
			}

			Bitmap b = BitmapFactory.decodeStream(input, null, options);
			String path = uri.getPath();
			if (!TextUtils.isEmpty(path)) {
				b = rotateBitMap(path, b);
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.PNG, quality, os);

			return b;
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
				}
		}
	}

	public static byte[] getResizedImageData(Context context, Uri uri,
			int quality, int widthLimit, int heightLimit) {
		BitmapFactory.Options opt = decodeBitmapOptionsInfo(context, uri);
		int outSize = opt.outWidth > opt.outHeight ? opt.outWidth
				: opt.outHeight;

		int s = 1;
		while (outSize / s > widthLimit) {
			s++;
		}

		Log.d("Util",
				String.format(
						"OUTPUT: x:%1$s  y:%2$s",
						new Object[] { String.valueOf(opt.outWidth / s),
								String.valueOf(opt.outHeight / s) }));

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = s;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds = false;

		InputStream input = null;
		try {
			input = context.getContentResolver().openInputStream(uri);
			Bitmap b = BitmapFactory.decodeStream(input, null, options);
			String path = uri.getPath();
			if (!TextUtils.isEmpty(path)) {
				b = rotateBitMap(path, b);
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.PNG, 100, os);
			b.recycle();
			return os.toByteArray();
		} catch (FileNotFoundException e) {
			if (input == null) {
				input = FileUtil.getFileInputStream(uri.getPath());
			}

			Bitmap b = BitmapFactory.decodeStream(input, null, options);
			String path = uri.getPath();
			if (!TextUtils.isEmpty(path)) {
				b = rotateBitMap(path, b);
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.PNG, 100, os);
			b.recycle();

			return os.toByteArray();
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
				}
		}
	}

	private static BitmapFactory.Options decodeBitmapOptionsInfo(
			Context context, Uri uri) {
		InputStream input = null;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		try {
			input = context.getContentResolver().openInputStream(uri);
			opt.inJustDecodeBounds = true;
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			BitmapFactory.decodeStream(input, null, opt);
			return opt;
		} catch (FileNotFoundException e) {
			if (input == null) {
				input = FileUtil.getFileInputStream(uri.getPath());
			}
			opt.inJustDecodeBounds = true;
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			BitmapFactory.decodeStream(input, null, opt);
			return opt;
		} finally {
			if (null != input)
				try {
					input.close();
				} catch (IOException e) {
				}
		}
	}

	public static Bitmap getRotateBitmap(float degrees, Bitmap bm) {
		int bmpW = bm.getWidth();
		int bmpH = bm.getHeight();

		Matrix mt = new Matrix();

		mt.setRotate(degrees);
		return Bitmap.createBitmap(bm, 0, 0, bmpW, bmpH, mt, true);
	}

	private static Bitmap rotateBitMap(String srcFilePath, Bitmap bitmap) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(srcFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		float degree = 0.0F;

		if (exif != null) {
			switch (exif.getAttributeInt("Orientation", 0)) {
			case 6:
				degree = 90.0F;
				break;
			case 3:
				degree = 180.0F;
				break;
			case 8:
				degree = 270.0F;
				break;
			}

		}

		if (degree != 0.0F) {
			Matrix matrix = new Matrix();
			matrix.setRotate(degree, bitmap.getWidth(), bitmap.getHeight());
			Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			if ((b2 != null) && (bitmap != b2)) {
				bitmap.recycle();
				bitmap = b2;
			}
		}

		return bitmap;
	}


	public static boolean isGIFImage(String filePath) {
		byte[] imageHearByte = new byte[28];

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			inputStream.read(imageHearByte, 0, 28);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String picType = bytesToHexString(imageHearByte);

		if (picType.startsWith("47494638")) {
			return true;
		}
		return false;
	}

	public static boolean isGIFImage(InputStream inputStream) {
		if (inputStream == null) {
			return false;
		}
		byte[] imageHearByte = new byte[28];
		try {
			inputStream.read(imageHearByte, 0, 28);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String picType = bytesToHexString(imageHearByte);

		if (picType.startsWith("47494638")) {
			return true;
		}
		return false;
	}

	public static boolean isGIFImage(byte[] imgData) {
		if ((imgData == null) || (imgData.length == 0)) {
			return false;
		}

		byte[] imageHearByte = new byte[28];

		System.arraycopy(imgData, 0, imageHearByte, 0, 28);

		String picType = bytesToHexString(imageHearByte);

		if (picType.startsWith("47494638")) {
			return true;
		}
		return false;
	}

	private static String bytesToHexString(byte[] bytes) {
		if ((bytes == null) || (bytes.length <= 0)) {
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			int intValue = bytes[i] & 0xFF;
			String hexValue = Integer.toHexString(intValue);

			if (hexValue.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hexValue);
		}

		return stringBuilder.toString().toUpperCase();
	}


}