package Jni;

import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

public class FileUtils {

	/**
	 * Generate output file
	 * @param strcontent a list of all video path
	 * @param filePath generated file path
	 * @param fileName generated file name
	 */
	public static void writeTxtToFile(List<String> strcontent, String filePath, String fileName) {
		//generate directory first
		makeFilePath(filePath, fileName);
		String strFilePath = filePath + fileName;
		// start new line
		String strContent = "";
		for (int i = 0; i < strcontent.size(); i++) {
			strContent += "file " + strcontent.get(i) + "\r\n";
		}
		try {
			File file = new File(strFilePath);
			//check whether file exists already, if so, delete it
			if (file.isFile() && file.exists()) {
				file.delete();
			}
			file.getParentFile().mkdirs();
			file.createNewFile();
			RandomAccessFile raf = new RandomAccessFile(file, "rwd");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
			Log.e("TestFile", "Written successfully:" + strFilePath);
		} catch (Exception e) {
			Log.e("TestFile", "Error on writing File:" + e);
		}
	}

	//Create file path
	public static File makeFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	//Create directory
	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {
			Log.i("error:", e + "");
		}
	}
}
