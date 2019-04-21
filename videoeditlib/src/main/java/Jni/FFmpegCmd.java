package Jni;

import android.support.annotation.Keep;

import VideoHandle.OnEditorListener;

@Keep
public class FFmpegCmd {
	/**
	 * Load all related library
	 */
	static {
		System.loadLibrary("avutil");
		System.loadLibrary("avcodec");
		System.loadLibrary("swresample");
		System.loadLibrary("avformat");
		System.loadLibrary("swscale");
		System.loadLibrary("avfilter");
		System.loadLibrary("avdevice");
		System.loadLibrary("ffmpeg");
	}

	private static OnEditorListener listener;
	private static long duration;

	/**
	 * Execute
	 *
	 * @param argc
	 * @param argv
	 * @return
	 */
	@Keep
	public static native int exec(int argc, String[] argv);

	@Keep
	public static native void exit();

	@Keep
	public static void onExecuted(int ret) {
		if (listener != null) {
			if (ret == 0) {
				listener.onProgress(1);
				listener.onSuccess();
			} else {
				listener.onFailure();
			}
		}
	}

	@Keep
	public static void onProgress(float progress) {
		if (listener != null) {
			if (duration != 0) {
				listener.onProgress(progress / (duration / 1000000) * 0.95f);
			}
		}
	}


	/**
	 * Execute ffmoeg command
	 *
	 * @param cmds
	 * @param listener
	 */
	@Keep
	public static void exec(String[] cmds, long duration, OnEditorListener listener) {
		FFmpegCmd.listener = listener;
		FFmpegCmd.duration = duration;
		exec(cmds.length, cmds);
	}
}
