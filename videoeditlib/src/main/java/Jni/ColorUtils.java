package Jni;

public class ColorUtils {

	/**
	 * Load library
	 */
	static {
		System.loadLibrary("colorutils");
	}

	public static native byte[] rgb2yuvfloat(byte[] rgbs, int size, int width, int height);
}
