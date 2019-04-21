package VideoHandle;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Jni.FFmpegCmd;
import Jni.FileUtils;
import Jni.TrackUtils;
import Jni.VideoUitls;

public class VEEditor {

	private static final int DEFAULT_WIDTH = 480;//default export width
	private static final int DEFAULT_HEIGHT = 360;//default export height

	public enum Format {
		MP3, MP4
	}

	public enum PTS {
		VIDEO, AUDIO, ALL
	}

	private VEEditor() {
	}

	/**
	 * Process single video
	 *
	 * @param VEVideo      video needs to be processed
	 * @param outputOption output options
	 */
	public static void exec(VEVideo VEVideo, OutputOption outputOption, OnEditorListener onEditorListener) {
		boolean isFilter = false;
		ArrayList<VEDraw> VEDraws = VEVideo.getEpDraws();
		//Start processing
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg");
		cmd.append("-y");
		if (VEVideo.getVideoClip()) {
			cmd.append("-ss").append(VEVideo.getClipStart()).append("-t").append(VEVideo.getClipDuration()).append("-accurate_seek");
		}
		cmd.append("-i").append(VEVideo.getVideoPath());
		//add images or gif
		if (VEDraws.size() > 0) {
			for (int i = 0; i < VEDraws.size(); i++) {
				if (VEDraws.get(i).isAnimation()) {
					cmd.append("-ignore_loop");
					cmd.append(0);
				}
				cmd.append("-i").append(VEDraws.get(i).getPicPath());
			}
			cmd.append("-filter_complex");
			StringBuilder filter_complex = new StringBuilder();
			filter_complex.append("[0:v]").append(VEVideo.getFilters() != null ? VEVideo.getFilters() + "," : "")
					.append("scale=").append(outputOption.width == 0 ? "iw" : outputOption.width).append(":")
					.append(outputOption.height == 0 ? "ih" : outputOption.height)
					.append(outputOption.width == 0 ? "" : ",setdar=" + outputOption.getSar()).append("[outv0];");
			for (int i = 0; i < VEDraws.size(); i++) {
				filter_complex.append("[").append(i + 1).append(":0]").append(VEDraws.get(i).getPicFilter()).append("scale=").append(VEDraws.get(i).getPicWidth()).append(":")
						.append(VEDraws.get(i).getPicHeight()).append("[outv").append(i + 1).append("];");
			}
			for (int i = 0; i < VEDraws.size(); i++) {
				if (i == 0) {
					filter_complex.append("[outv").append(i).append("]").append("[outv").append(i + 1).append("]");
				} else {
					filter_complex.append("[outo").append(i - 1).append("]").append("[outv").append(i + 1).append("]");
				}
				filter_complex.append("overlay=").append(VEDraws.get(i).getPicX()).append(":").append(VEDraws.get(i).getPicY())
						.append(VEDraws.get(i).getTime());
				if (VEDraws.get(i).isAnimation()) {
					filter_complex.append(":shortest=1");
				}
				if (i < VEDraws.size() - 1) {
					filter_complex.append("[outo").append(i).append("];");
				}
			}
			cmd.append(filter_complex.toString());
			isFilter = true;
		} else {
			StringBuilder filter_complex = new StringBuilder();
			if (VEVideo.getFilters() != null) {
				cmd.append("-filter_complex");
				filter_complex.append(VEVideo.getFilters());
				isFilter = true;
			}
			//Set up output resolution
			if (outputOption.width != 0) {
				if (VEVideo.getFilters() != null) {
					filter_complex.append(",scale=").append(outputOption.width).append(":").append(outputOption.height)
							.append(",setdar=").append(outputOption.getSar());
				} else {
					cmd.append("-filter_complex");
					filter_complex.append("scale=").append(outputOption.width).append(":").append(outputOption.height)
							.append(",setdar=").append(outputOption.getSar());
					isFilter = true;
				}
			}
			if (!filter_complex.toString().equals("")) {
				cmd.append(filter_complex.toString());
			}
		}

		//output config
		cmd.append(outputOption.getOutputInfo().split(" "));
		if (!isFilter && outputOption.getOutputInfo().isEmpty()) {
			cmd.append("-vcodec");
			cmd.append("copy");
			cmd.append("-acodec");
			cmd.append("copy");
		} else {
			cmd.append("-preset");
			cmd.append("superfast");
		}
		cmd.append(outputOption.outPath);
		long duration = VideoUitls.getDuration(VEVideo.getVideoPath());
		if (VEVideo.getVideoClip()) {
			long clipTime = (long) ((VEVideo.getClipDuration() - VEVideo.getClipStart()) * 1000000);
			duration = clipTime < duration ? clipTime : duration;
		}
		//execute
		execCmd(cmd, duration, onEditorListener);
	}

	/**
	 * Combine multiple videos
	 *
	 * @param VEVideos     a list of all videos needs to be output
	 * @param outputOption output options
	 */
	public static void merge(List<VEVideo> VEVideos, OutputOption outputOption, OnEditorListener onEditorListener) {
		//Check whether there is music
		boolean isNoAudioTrack = false;
		for (VEVideo VEVideo : VEVideos) {
			MediaExtractor mediaExtractor = new MediaExtractor();
			try {
				mediaExtractor.setDataSource(VEVideo.getVideoPath());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			int at = TrackUtils.selectAudioTrack(mediaExtractor);
			if (at == -1) {
				isNoAudioTrack = true;
				mediaExtractor.release();
				break;
			}
			mediaExtractor.release();
		}
		//Set up default width and height
		outputOption.width = outputOption.width == 0 ? DEFAULT_WIDTH : outputOption.width;
		outputOption.height = outputOption.height == 0 ? DEFAULT_HEIGHT : outputOption.height;
		//check on the list size
		if (VEVideos.size() > 1) {
			CmdList cmd = new CmdList();
			cmd.append("ffmpeg");
			cmd.append("-y");
			//add text
			for (VEVideo e : VEVideos) {
				if (e.getVideoClip()) {
					cmd.append("-ss").append(e.getClipStart()).append("-t").append(e.getClipDuration()).append("-accurate_seek");
				}
				cmd.append("-i").append(e.getVideoPath());
			}
			for (VEVideo e : VEVideos) {
				ArrayList<VEDraw> VEDraws = e.getEpDraws();
				if (VEDraws.size() > 0) {
					for (VEDraw ep : VEDraws) {
						if (ep.isAnimation()) cmd.append("-ignore_loop").append(0);
						cmd.append("-i").append(ep.getPicPath());
					}
				}
			}
			//add filter
			cmd.append("-filter_complex");
			StringBuilder filter_complex = new StringBuilder();
			for (int i = 0; i < VEVideos.size(); i++) {
				StringBuilder filter = VEVideos.get(i).getFilters() == null ? new StringBuilder("") : VEVideos.get(i).getFilters().append(",");
				filter_complex.append("[").append(i).append(":v]").append(filter).append("scale=").append(outputOption.width).append(":").append(outputOption.height)
						.append(",setdar=").append(outputOption.getSar()).append("[outv").append(i).append("];");
			}
			//add drawing
			int drawNum = VEVideos.size();
			for (int i = 0; i < VEVideos.size(); i++) {
				for (int j = 0; j < VEVideos.get(i).getEpDraws().size(); j++) {
					filter_complex.append("[").append(drawNum++).append(":0]").append(VEVideos.get(i).getEpDraws().get(j).getPicFilter()).append("scale=")
							.append(VEVideos.get(i).getEpDraws().get(j).getPicWidth()).append(":").append(VEVideos.get(i).getEpDraws().get(j)
							.getPicHeight()).append("[p").append(i).append("a").append(j).append("];");
				}
			}
			//add logo
			for (int i = 0; i < VEVideos.size(); i++) {
				for (int j = 0; j < VEVideos.get(i).getEpDraws().size(); j++) {
					filter_complex.append("[outv").append(i).append("][p").append(i).append("a").append(j).append("]overlay=")
							.append(VEVideos.get(i).getEpDraws().get(j).getPicX()).append(":")
							.append(VEVideos.get(i).getEpDraws().get(j).getPicY())
							.append(VEVideos.get(i).getEpDraws().get(j).getTime());
					if (VEVideos.get(i).getEpDraws().get(j).isAnimation()) {
						filter_complex.append(":shortest=1");
					}
					filter_complex.append("[outv").append(i).append("];");
				}
			}
			//Start merging videos
			for (int i = 0; i < VEVideos.size(); i++) {
				filter_complex.append("[outv").append(i).append("]");
			}
			filter_complex.append("concat=n=").append(VEVideos.size()).append(":v=1:a=0[outv]");
			//whether add sound track
			if (!isNoAudioTrack) {
				filter_complex.append(";");
				for (int i = 0; i < VEVideos.size(); i++) {
					filter_complex.append("[").append(i).append(":a]");
				}
				filter_complex.append("concat=n=").append(VEVideos.size()).append(":v=0:a=1[outa]");
			}
			if (!filter_complex.toString().equals("")) {
				cmd.append(filter_complex.toString());
			}
			cmd.append("-map").append("[outv]");
			if (!isNoAudioTrack) {
				cmd.append("-map").append("[outa]");
			}
			cmd.append(outputOption.getOutputInfo().split(" "));
			cmd.append("-preset").append("superfast").append(outputOption.outPath);
			long duration = 0;
			for (VEVideo ep : VEVideos) {
				long d = VideoUitls.getDuration(ep.getVideoPath());
				if (ep.getVideoClip()) {
					long clipTime = (long) ((ep.getClipDuration() - ep.getClipStart()) * 1000000);
					d = clipTime < d ? clipTime : d;
				}
				if (d != 0) {
					duration += d;
				} else {
					break;
				}
			}

			execCmd(cmd, duration, onEditorListener);
		} else {
			throw new RuntimeException("Need more than one video");
		}
	}

	/**
	 * Merge multiple video lossless
	 * <p>
	 * Note：this method has strict requirements for video format，all videos need to have the same resolution and frame rate, bite rate
	 *
	 * @param context          Context
	 * @param VEVideos         a list of videos needs to be combined
	 * @param outputOption     output option
	 * @param onEditorListener Listener for monitoring
	 */
	public static void mergeByLc(Context context, List<VEVideo> VEVideos, OutputOption outputOption, final OnEditorListener onEditorListener) {
		String appDir = context.getFilesDir().getAbsolutePath() + "/EpVideos/";
		String fileName = "ffmpeg_concat.txt";
		List<String> videos = new ArrayList<>();
		for (VEVideo e : VEVideos) {
			videos.add(e.getVideoPath());
		}
		FileUtils.writeTxtToFile(videos, appDir, fileName);
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg").append("-y").append("-f").append("concat").append("-safe")
				.append("0").append("-i").append(appDir + fileName)
				.append("-c").append("copy").append(outputOption.outPath);
		long duration = 0;
		for (VEVideo ep : VEVideos) {
			long d = VideoUitls.getDuration(ep.getVideoPath());
			if (d != 0) {
				duration += d;
			} else {
				break;
			}
		}
		execCmd(cmd, duration, onEditorListener);
	}

	/**
	 * Add background music
	 *
	 * @param videoin          input video path
	 * @param audioin          input audio path
	 * @param output           export path
	 * @param videoVolume      video sound volume(e.g. 0.7 is 70%)
	 * @param audioVolume      background music volume(e.g. 1.5 is 150%)
	 * @param onEditorListener Listener for monitoring
	 */
	public static void music(String videoin, String audioin, String output, float videoVolume, float audioVolume, OnEditorListener onEditorListener) {
		MediaExtractor mediaExtractor = new MediaExtractor();
		try {
			mediaExtractor.setDataSource(videoin);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		int at = TrackUtils.selectAudioTrack(mediaExtractor);
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg").append("-y").append("-i").append(videoin);
		if (at == -1) {
			int vt = TrackUtils.selectVideoTrack(mediaExtractor);
			float duration = (float) mediaExtractor.getTrackFormat(vt).getLong(MediaFormat.KEY_DURATION) / 1000 / 1000;
			cmd.append("-ss").append("0").append("-t").append(duration).append("-i").append(audioin).append("-acodec").append("copy").append("-vcodec").append("copy");
		} else {
			cmd.append("-i").append(audioin).append("-filter_complex")
					.append("[0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + videoVolume + "[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + audioVolume + "[a1];[a0][a1]amix=inputs=2:duration=first[aout]")
					.append("-map").append("[aout]").append("-ac").append("2").append("-c:v")
					.append("copy").append("-map").append("0:v:0");
		}
		cmd.append(output);
		mediaExtractor.release();
		long d = VideoUitls.getDuration(videoin);
		execCmd(cmd, d, onEditorListener);
	}

	/**
	 * Seperate video with audio
	 *
	 * @param videoin          input video path
	 * @param out              export video path
	 * @param format           output format
	 * @param onEditorListener Listener for monitoring
	 */
	public static void demuxer(String videoin, String out, Format format, OnEditorListener onEditorListener) {
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg").append("-y").append("-i").append(videoin);
		switch (format) {
			case MP3:
				cmd.append("-vn").append("-acodec").append("libmp3lame");
				break;
			case MP4:
				cmd.append("-vcodec").append("copy").append("-an");
				break;
		}
		cmd.append(out);
		long d = VideoUitls.getDuration(videoin);
		execCmd(cmd, d, onEditorListener);
	}

	/**
	 * Revert play video
	 *
	 * @param videoin          input video path
	 * @param out              export video path
	 * @param vr               whether revert play video
	 * @param ar               whether revert play audio
	 * @param onEditorListener Listener for monitoring
	 */
	public static void reverse(String videoin, String out, boolean vr, boolean ar, OnEditorListener onEditorListener) {
		if (!vr && !ar) {
			Log.e("ffmpeg", "parameter error");
			onEditorListener.onFailure();
			return;
		}
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg").append("-y").append("-i").append(videoin).append("-filter_complex");
		String filter = "";
		if (vr) {
			filter += "[0:v]reverse[v];";
		}
		if (ar) {
			filter += "[0:a]areverse[a];";
		}
		cmd.append(filter.substring(0, filter.length() - 1));
		if (vr) {
			cmd.append("-map").append("[v]");
		}
		if (ar) {
			cmd.append("-map").append("[a]");
		}
		if (ar && !vr) {
			cmd.append("-acodec").append("libmp3lame");
		}
		cmd.append("-preset").append("superfast").append(out);
		long d = VideoUitls.getDuration(videoin);
		execCmd(cmd, d, onEditorListener);
	}

	/**
	 * Change speed of video
	 *
	 * @param videoin          input video path
	 * @param out              export video path
	 * @param times            speed (range 0.25-4)
	 * @param pts              speed type
	 * @param onEditorListener Listener API for monitoring
	 */
	public static void changePTS(String videoin, String out, float times, PTS pts, OnEditorListener onEditorListener) {
		if (times < 0.25f || times > 4.0f) {
			Log.e("ffmpeg", "times can only be 0.25 to 4");
			onEditorListener.onFailure();
			return;
		}
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg").append("-y").append("-i").append(videoin);
		String t = "atempo=" + times;
		if (times < 0.5f) {
			t = "atempo=0.5,atempo=" + (times / 0.5f);
		} else if (times > 2.0f) {
			t = "atempo=2.0,atempo=" + (times / 2.0f);
		}
		Log.v("ffmpeg", "atempo:" + t);
		switch (pts) {
			case VIDEO:
				cmd.append("-filter_complex").append("[0:v]setpts=" + (1 / times) + "*PTS").append("-an");
				break;
			case AUDIO:
				cmd.append("-filter:a").append(t);
				break;
			case ALL:
				cmd.append("-filter_complex").append("[0:v]setpts=" + (1 / times) + "*PTS[v];[0:a]" + t + "[a]")
						.append("-map").append("[v]").append("-map").append("[a]");
				break;
		}
		cmd.append("-preset").append("superfast").append(out);
		long d = VideoUitls.getDuration(videoin);
		double dd = d / times;
		long ddd = (long) dd;
		execCmd(cmd, ddd, onEditorListener);
	}

	/**
	 * Export video to images
	 *
	 * @param videoin          input video path
	 * @param out              export video path
	 * @param w					export image width
	 * @param h					export image height
	 * @param rate				how many images per second needs to be generated
	 * @param onEditorListener	Listener API for monitoring
	 */
	public static void video2pic(String videoin, String out, int w, int h, float rate, OnEditorListener onEditorListener) {
		if (w <= 0 || h <= 0) {
			Log.e("ffmpeg", "width and height must greater than 0");
			onEditorListener.onFailure();
			return;
		}
		if(rate <= 0){
			Log.e("ffmpeg", "rate must greater than 0");
			onEditorListener.onFailure();
			return;
		}
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg").append("-y").append("-i").append(videoin)
				.append("-r").append(rate).append("-s").append(w+"x"+h).append("-q:v").append(2)
				.append("-f").append("image2").append("-preset").append("superfast").append(out);
		long d = VideoUitls.getDuration(videoin);
		execCmd(cmd, d, onEditorListener);
	}

	/**
	 * Images to videos
	 *
	 * @param videoin          input video path
	 * @param out              export video path
	 * @param w					export video with
	 * @param h					export video height
	 * @param rate				export video frame rate
	 * @param onEditorListener	Listener API for monitoring
	 */
	public static void pic2video(String videoin, String out, int w, int h, float rate, OnEditorListener onEditorListener) {
		if (w < 0 || h < 0) {
			Log.e("ffmpeg", "width and height must greater than 0");
			onEditorListener.onFailure();
			return;
		}
		if(rate <= 0){
			Log.e("ffmpeg", "rate must greater than 0");
			onEditorListener.onFailure();
			return;
		}
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg").append("-y").append("-f").append("image2").append("-i").append(videoin)
				.append("-vcodec").append("libx264")
				.append("-r").append(rate);
//				.append("-b").append("10M");
				if(w > 0 && h > 0) {
					cmd.append("-s").append(w + "x" + h);
				}
		cmd.append(out);
		long d = VideoUitls.getDuration(videoin);
		execCmd(cmd, d, onEditorListener);
	}


	/**
	 * Export options
	 */
	public static class OutputOption {
		static final int ONE_TO_ONE = 1;// 1:1
		static final int FOUR_TO_THREE = 2;// 4:3
		static final int SIXTEEN_TO_NINE = 3;// 16:9
		static final int NINE_TO_SIXTEEN = 4;// 9:16
		static final int THREE_TO_FOUR = 5;// 3:4

		String outPath;
		public int frameRate = 0;
		public int bitRate = 0;//normally 10M
		public String outFormat = "";// only support mp4, x264, mp3, gif for now
		private int width = 0;
		private int height = 0;
		private int sar = 6;//output width/height

		public OutputOption(String outPath) {
			this.outPath = outPath;
		}

		/**
		 * Get Width/Height
		 *
		 * @return 1
		 */
		public String getSar() {
			String res;
			switch (sar) {
				case ONE_TO_ONE:
					res = "1/1";
					break;
				case FOUR_TO_THREE:
					res = "4/3";
					break;
				case THREE_TO_FOUR:
					res = "3/4";
					break;
				case SIXTEEN_TO_NINE:
					res = "16/9";
					break;
				case NINE_TO_SIXTEEN:
					res = "9/16";
					break;
				default:
					res = width + "/" + height;
					break;
			}
			return res;
		}

		public void setSar(int sar) {
			this.sar = sar;
		}

		/**
		 * Get output information
		 *
		 * @return 1
		 */
		String getOutputInfo() {
			StringBuilder res = new StringBuilder();
			if (frameRate != 0) {
				res.append(" -r ").append(frameRate);
			}
			if (bitRate != 0) {
				res.append(" -b ").append(bitRate).append("M");
			}
			if (!outFormat.isEmpty()) {
				res.append(" -f ").append(outFormat);
			}
			return res.toString();
		}

		/**
		 * Set width
		 *
		 * @param width
		 */
		public void setWidth(int width) {
			if (width % 2 != 0) width -= 1;
			this.width = width;
		}

		/**
		 * Set Height
		 *
		 * @param height
		 */
		public void setHeight(int height) {
			if (height % 2 != 0) height -= 1;
			this.height = height;
		}
	}

	/**
	 * Start executing
	 *
	 * @param cmd              command line
	 * @param duration         video uration: in millisec
	 * @param onEditorListener Listeners API for monitoring
	 */
	public static void execCmd(String cmd, long duration, final OnEditorListener onEditorListener) {
		cmd = "ffmpeg " + cmd;
		String[] cmds = cmd.split(" ");
		FFmpegCmd.exec(cmds, duration, new OnEditorListener() {
			@Override
			public void onSuccess() {
				onEditorListener.onSuccess();
			}

			@Override
			public void onFailure() {
				onEditorListener.onFailure();
			}

			@Override
			public void onProgress(final float progress) {
				onEditorListener.onProgress(progress);
			}
		});
	}

	/**
	 * Start executing several command
	 *
	 * @param cmd              a list of command line
	 * @param duration         video uration: in millisec
	 * @param onEditorListener Listeners API for monitoring
	 */
	private static void execCmd(CmdList cmd, long duration, final OnEditorListener onEditorListener) {
		String[] cmds = cmd.toArray(new String[cmd.size()]);
		String cmdLog = "";
		for (String ss : cmds) {
			cmdLog += cmds;
		}
		Log.v("EpMediaF", "cmd:" + cmdLog);
		FFmpegCmd.exec(cmds, duration, new OnEditorListener() {
			@Override
			public void onSuccess() {
				onEditorListener.onSuccess();
			}

			@Override
			public void onFailure() {
				onEditorListener.onFailure();
			}

			@Override
			public void onProgress(final float progress) {
				onEditorListener.onProgress(progress);
			}
		});
	}
}
