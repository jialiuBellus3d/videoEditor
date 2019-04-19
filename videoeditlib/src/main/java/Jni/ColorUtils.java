package Jni;

public class ColorUtils {

	/**
	 * 加载所有相关链接库
	 */
	static {
		System.loadLibrary("colorutils");
	}

	public static native byte[] rgb2yuvfloat(byte[] rgbs, int size, int width, int height);
}
