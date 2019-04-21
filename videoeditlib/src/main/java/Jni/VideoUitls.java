package Jni;

import android.media.MediaExtractor;
import android.media.MediaFormat;

public class VideoUitls {

	private VideoUitls() {
	}

	/**
	 * Get video information
	 *
	 * @param url
	 * @return	video duration in millisec
	 */
	public static long getDuration(String url) {
		try {
			MediaExtractor mediaExtractor = new MediaExtractor();
			mediaExtractor.setDataSource(url);
			int videoExt = TrackUtils.selectVideoTrack(mediaExtractor);
			if(videoExt == -1){
				videoExt = TrackUtils.selectAudioTrack(mediaExtractor);
				if(videoExt == -1){
					return 0;
				}
			}
			MediaFormat mediaFormat = mediaExtractor.getTrackFormat(videoExt);
			long res = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(MediaFormat.KEY_DURATION) : 0;
			mediaExtractor.release();
			return res;
		} catch (Exception e) {
			return 0;
		}
	}
}
