package Jni;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

public class TrackUtils {

	private static final String TAG = "TrackUtils";

	/**
	 * Select video track
	 * @param extractor
	 * @return
	 */
	public static int selectVideoTrack(MediaExtractor extractor) {
		int numTracks = extractor.getTrackCount();
		for (int i = 0; i < numTracks; i++) {
			MediaFormat format = extractor.getTrackFormat(i);
			String mime = format.getString(MediaFormat.KEY_MIME);
			if (mime.startsWith("video/")) {
				Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
				return i;
			}
		}
		return -1;
	}

	/**
	 * Select audio track
	 * @param extractor
	 * @return
	 */
	public static int selectAudioTrack(MediaExtractor extractor) {
		int numTracks = extractor.getTrackCount();
		for (int i = 0; i < numTracks; i++) {
			MediaFormat format = extractor.getTrackFormat(i);
			String mime = format.getString(MediaFormat.KEY_MIME);
			if (mime.startsWith("audio/")) {
				Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
				return i;
			}
		}
		return -1;
	}
}
