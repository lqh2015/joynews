package com.color.tools.mytools;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.hzpd.utils.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	private static final String TAG = "FileUtil";

	public static InputStream getFileInputStream(String path) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fileInputStream;
	}

	public static final String createDirectory(File storageDirectory) {
		if (!storageDirectory.exists()) {
			Log.d(TAG,
					"Trying to create storageDirectory: "
							+ String.valueOf(storageDirectory.mkdirs()));

			Log.d(TAG,
					"Exists: " + storageDirectory + " "
							+ String.valueOf(storageDirectory.exists()));
			Log.d(TAG, "State: " + Environment.getExternalStorageState());
			Log.d(TAG,
					"Isdir: " + storageDirectory + " "
							+ String.valueOf(storageDirectory.isDirectory()));
			Log.d(TAG,
					"Readable: " + storageDirectory + " "
							+ String.valueOf(storageDirectory.canRead()));
			Log.d(TAG,
					"Writable: " + storageDirectory + " "
							+ String.valueOf(storageDirectory.canWrite()));
			File tmp = storageDirectory.getParentFile();
			Log.d(TAG,
					"Exists: " + tmp + " " + String.valueOf(tmp.exists()));
			Log.d(TAG,
					"Isdir: " + tmp + " " + String.valueOf(tmp.isDirectory()));
			Log.d(TAG,
					"Readable: " + tmp + " " + String.valueOf(tmp.canRead()));
			Log.d(TAG,
					"Writable: " + tmp + " " + String.valueOf(tmp.canWrite()));
			tmp = tmp.getParentFile();
			Log.d(TAG,
					"Exists: " + tmp + " " + String.valueOf(tmp.exists()));
			Log.d(TAG,
					"Isdir: " + tmp + " " + String.valueOf(tmp.isDirectory()));
			Log.d(TAG,
					"Readable: " + tmp + " " + String.valueOf(tmp.canRead()));
			Log.d(TAG,
					"Writable: " + tmp + " " + String.valueOf(tmp.canWrite()));
		}

		File nomediaFile = new File(storageDirectory, ".nomedia");

		if (!nomediaFile.exists()) {
			try {
				Log.d(TAG,
						"Created file: " + nomediaFile + " "
								+ String.valueOf(nomediaFile.createNewFile()));
			} catch (IOException e) {
				Log.d(TAG,
						"Unable to create .nomedia file for some reason.");
				throw new IllegalStateException(
						"Unable to create nomedia file.");
			}
		}

		if ((!storageDirectory.isDirectory()) || (!nomediaFile.exists())) {
			throw new RuntimeException(
					"Unable to create storage directory and nomedia file.");
		}

		return storageDirectory.getPath();
	}

	public static byte[] getByteFromUri(Uri uri) {
		InputStream input = getFileInputStream(uri.getPath());
		try {
			int count = 0;
			while (count == 0) {
				count = input.available();
			}

			byte[] bytes = new byte[count];
			input.read(bytes);

			return bytes;
		} catch (Exception e) {
			return null;
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
				}
		}
	}

	public static void writeByte(Uri uri, byte[] data) {
		File fileFolder = new File(uri.getPath().substring(0,
				uri.getPath().lastIndexOf("/")));
		fileFolder.mkdirs();
		File file = new File(uri.getPath());
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					file));
			os.write(data);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final static String FILE_EXTENSION_SEPARATOR = ".";

	/**
	 * read file
	 * 
	 * @param filePath
	 * @return if file not exist, return null, else return content of file
	 * @throws IOException
	 *             if an error occurs while operator BufferedReader
	 */
	public static StringBuilder readFile(String filePath) {
		File file = new File(filePath);
		StringBuilder fileContent = new StringBuilder("");
		if (file == null || !file.isFile()) {
			return null;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!fileContent.toString().equals("")) {
					fileContent.append("\r\n");
				}
				fileContent.append(line);
			}
			reader.close();
			return fileContent;
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * write file
	 * 
	 * @param filePath
	 * @param content
	 * @param append
	 *            is append, if true, write to the end of file, else clear content of file and write into it
	 * @return return true
	 * @throws IOException
	 *             if an error occurs while operator FileWriter
	 */
	public static boolean writeFile(String filePath, String content, boolean append) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filePath, append);
			fileWriter.write(content);
			fileWriter.close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * write file
	 * 
	 * @param filePath
	 * @param stream
	 * @return return true
	 * @throws IOException
	 *             if an error occurs while operator FileWriter
	 */
	public static boolean writeFile(String filePath, InputStream stream) {
		OutputStream o = null;
		try {
			o = new FileOutputStream(filePath);
			byte data[] = new byte[1024];
			int length = -1;
			while ((length = stream.read(data)) != -1) {
				o.write(data, 0, length);
			}
			o.flush();
			return true;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("FileNotFoundException occurred. ", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (o != null) {
				try {
					o.close();
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * read file to string list, a element of list is a line
	 * 
	 * @param filePath
	 * @return if file not exist, return null, else return content of file
	 * @throws IOException
	 *             if an error occurs while operator BufferedReader
	 */
	public static List<String> readFileToList(String filePath) {
		File file = new File(filePath);
		List<String> fileContent = new ArrayList<String>();
		if (file == null || !file.isFile()) {
			return null;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fileContent.add(line);
			}
			reader.close();
			return fileContent;
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * get file name from path, not include suffix
	 * 
	 * <pre>
	 *      getFileNameWithoutExtension(null)               =   null
	 *      getFileNameWithoutExtension("")                 =   ""
	 *      getFileNameWithoutExtension("   ")              =   "   "
	 *      getFileNameWithoutExtension("abc")              =   "abc"
	 *      getFileNameWithoutExtension("a.mp3")            =   "a"
	 *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
	 *      getFileNameWithoutExtension("c:\\")              =   ""
	 *      getFileNameWithoutExtension("c:\\a")             =   "a"
	 *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
	 *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
	 *      getFileNameWithoutExtension("/home/admin")      =   "admin"
	 *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
	 * </pre>
	 * 
	 * @param filePath
	 * @return file name from path, not include suffix
	 * @see
	 */
	public static String getFileNameWithoutExtension(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return filePath;
		}

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (filePosi == -1) {
			return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
		}
		if (extenPosi == -1) {
			return filePath.substring(filePosi + 1);
		}
		return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
	}

	/**
	 * get file name from path, include suffix
	 * 
	 * <pre>
	 *      getFileName(null)               =   null
	 *      getFileName("")                 =   ""
	 *      getFileName("   ")              =   "   "
	 *      getFileName("a.mp3")            =   "a.mp3"
	 *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
	 *      getFileName("abc")              =   "abc"
	 *      getFileName("c:\\")              =   ""
	 *      getFileName("c:\\a")             =   "a"
	 *      getFileName("c:\\a.b")           =   "a.b"
	 *      getFileName("c:a.txt\\a")        =   "a"
	 *      getFileName("/home/admin")      =   "admin"
	 *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
	 * </pre>
	 * 
	 * @param filePath
	 * @return file name from path, include suffix
	 */
	public static String getFileName(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
	}

	/**
	 * get folder name from path
	 * 
	 * <pre>
	 *      getFolderName(null)               =   null
	 *      getFolderName("")                 =   ""
	 *      getFolderName("   ")              =   ""
	 *      getFolderName("a.mp3")            =   ""
	 *      getFolderName("a.b.rmvb")         =   ""
	 *      getFolderName("abc")              =   ""
	 *      getFolderName("c:\\")              =   "c:"
	 *      getFolderName("c:\\a")             =   "c:"
	 *      getFolderName("c:\\a.b")           =   "c:"
	 *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
	 *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
	 *      getFolderName("/home/admin")      =   "/home"
	 *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFolderName(String filePath) {

		if (TextUtils.isEmpty(filePath)) {
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
	}

	/**
	 * get suffix of file from path
	 * 
	 * <pre>
	 *      getFileExtension(null)               =   ""
	 *      getFileExtension("")                 =   ""
	 *      getFileExtension("   ")              =   "   "
	 *      getFileExtension("a.mp3")            =   "mp3"
	 *      getFileExtension("a.b.rmvb")         =   "rmvb"
	 *      getFileExtension("abc")              =   ""
	 *      getFileExtension("c:\\")              =   ""
	 *      getFileExtension("c:\\a")             =   ""
	 *      getFileExtension("c:\\a.b")           =   "b"
	 *      getFileExtension("c:a.txt\\a")        =   ""
	 *      getFileExtension("/home/admin")      =   ""
	 *      getFileExtension("/home/admin/a.txt/b")  =   ""
	 *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileExtension(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return filePath;
		}

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (extenPosi == -1) {
			return "";
		}
		return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
	}

	/**
	 * Creates the directory named by the trailing filename of this file, including the complete directory path required to create this directory. <br/>
	 * <br/>
	 * <ul>
	 * <strong>Attentions:</strong>
	 * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
	 * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
	 * </ul>
	 * 
	 * @param filePath
	 */
	public static boolean makeDirs(String filePath) {
		String folderName = getFolderName(filePath);
		if (TextUtils.isEmpty(folderName)) {
			return false;
		}

		File folder = new File(folderName);
		return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	}

	/**
	 * @param filePath
	 * @return
	 * @see #makeDirs(String)
	 */
	public static boolean makeFolders(String filePath) {
		return makeDirs(filePath);
	}

	/**
	 * Indicates if this file represents a file on the underlying file system.
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}

		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	/**
	 * Indicates if this file represents a directory on the underlying file system.
	 * 
	 * @param directoryPath
	 * @return
	 */
	public static boolean isFolderExist(String directoryPath) {
		if (TextUtils.isEmpty(directoryPath)) {
			return false;
		}

		File dire = new File(directoryPath);
		return (dire.exists() && dire.isDirectory());
	}

	/**
	 * delete file or directory
	 * <ul>
	 * <li>if path is null or empty, return true</li>
	 * <li>if path not exist, return true</li>
	 * <li>if path exist, delete recursion. return true</li>
	 * <ul>
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		if (TextUtils.isEmpty(path)) {
			return true;
		}

		File file = new File(path);
		if (!file.exists()) {
			return true;
		}
		if (file.isFile()) {
			return file.delete();
		}
		if (!file.isDirectory()) {
			return false;
		}
		for (File f : file.listFiles()) {
			if (f.isFile()) {
				f.delete();
			} else if (f.isDirectory()) {
				deleteFile(f.getAbsolutePath());
			}
		}
		return file.delete();
	}

	/**
	 * 获取文件大小
	 * <ul>
	 * <li>if path is null or empty, return -1</li>
	 * <li>if path exist and it is a file, return file size, else return -1</li>
	 * <ul>
	 * 
	 * @param path
	 * @return
	 */
	public static long getFileSize(Context context,String path) {
		if (TextUtils.isEmpty(path)) {
			return -1;
		}

		File file = new File(path);
		return (file.exists() && file.isFile() ? file.length() : -1);
	}

	/**
	 * 文件拷贝
	 * 
	 */
	public static void copyFile(File sourceFile, File targetFile) {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
			sourceFile.delete();
			sourceFile.deleteOnExit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {
				if (inBuff != null)
					inBuff.close();
				if (outBuff != null)
					outBuff.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * 文件拷贝
	 * 
	 */
	public static void copyFile(InputStream fis,OutputStream fos) {
		
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		
		try {
			inBuff = new BufferedInputStream(fis);
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(fos);
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {
				fis.close();
				fos.close();
				if (inBuff != null)
					inBuff.close();
				if (outBuff != null)
					outBuff.close();
			} catch (IOException e) {
			}
		}
	}

	public static File getExternalCacheDir(Context context, String dirs_name) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(new File(dataDir, context.getPackageName()), "cache"), dirs_name);
		if (!appCacheDir.exists()) {
			try {
				new File(dataDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Log.w("创建目录", "Can't create \".nomedia\" file in application external cache directory");
			}
			if (!appCacheDir.mkdirs()) {
				Log.w("创建目录", "Unable to create external cache directory");
				return null;
			}
		}
		return appCacheDir;
	}

	public static void write(File file, String data) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file), 1024);
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getAsString(File file) {
		if (!file.exists())
			return null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			String readString = "";
			String currentLine;
			while ((currentLine = in.readLine()) != null) {
				readString += currentLine;
			}
			return readString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void saveObject(Context context,String fileName,Serializable object) {
		FileOutputStream fos = null;
		 ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
	        oos.writeObject(object);// 写入
        } catch (Exception e) {
	        e.printStackTrace();
        } finally{
        	try {
	            if (fos!=null) {
	            	fos.close();
	            }
	            if (oos!=null) {
	            	oos.close();
	            }
            } catch (IOException e) {
            }
        }
    }
	
	public static <T> T getObject(Context context,String fileName,Class<T> clazz) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis=context.openFileInput(fileName);   //获得输入流
			ois = new ObjectInputStream(fis);
			return (T) ois.readObject();
        } catch (Exception e) {
	        e.printStackTrace();
        }finally{
        	try {
	            if (fis!=null) {
	            	fis.close();
	            }
	            if (ois!=null) {
	            	ois.close();
	            }
            } catch (IOException e) {
            }
        }
		return null;
    }

	
	public static String prettyBytes(long value) {
		String args[] = { "B", "KB", "MB", "GB", "TB" };
		StringBuilder sb = new StringBuilder();
		int i;
		if (value < 1024L) {
			sb.append(String.valueOf(value));
			i = 0;
		} else if (value < 1048576L) {
			sb.append(String.format("%.1f", value / 1024.0));
			i = 1;
		} else if (value < 1073741824L) {
			sb.append(String.format("%.2f", value / 1048576.0));
			i = 2;
		} else if (value < 1099511627776L) {
			sb.append(String.format("%.3f", value / 1073741824.0));
			i = 3;
		} else {
			sb.append(String.format("%.4f", value / 1099511627776.0));
			i = 4;
		}
		sb.append(' ');
		sb.append(args[i]);
		return sb.toString();
	}

	/**
     * @param context
     * @param dirName Only the folder name, not full path.
     * @return app_cache_path/dirName
     */
    public static String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        if (cachePath == null) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                cachePath = cacheDir.getPath();
            }
        }

        return cachePath + File.separator + dirName;
    }
    

    public static long getAvailableSpace(File dir) {
        try {
            final StatFs stats = new StatFs(dir.getPath());
            return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
        } catch (Throwable e) {
            Log.e("",e.getMessage());
            return -1;
        }

    }

    public static File getFile(String path){
		int fp=path.lastIndexOf(File.separator);
		String sfp=path.substring(0, fp);
		File fpath=new File(sfp);
		
		if(!fpath.exists()&&!fpath.isDirectory()){
			fpath.mkdirs();
		}
		
		File f=new File(path);
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}
	
	public static File getFileDir(String path){
		File f=new File(path);
		if(!f.exists()){
			f.mkdirs();
		}
		return f;
	}
	
	/**
	 * 获取单个文件的大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSizes(File f){
		long s = 0;
		FileInputStream fis = null;
		try {
			if (f.exists()){
				fis = new FileInputStream(f);
				s = fis.available();
			}else{
				LogUtils.i("文件不存在");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	/**
	 * 获取文件夹的大小
	 */
	public static long getFileDirSize(File f) throws Exception{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++){
			if (flist[i].isDirectory()){
				size = size + getFileDirSize(flist[i]);
			}else{
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 转换文件的大小
	 */
	public static String FormetFileSize(long fileS){
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024){
			fileSizeString = df.format((double) fileS) + "B";
		}else if (fileS < 1048576){
			fileSizeString = df.format((double) fileS / 1024) + "K";
		}else if (fileS < 1073741824){
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		}else{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	
}



