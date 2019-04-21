package VideoHandle;

import java.util.ArrayList;

public class VEVideo {

	private String videoPath;

	private boolean isClip = false; // whether clip
	private float clipStart;
	private float clipDuration;

	//filter
	private StringBuilder filter;

	//special drawing effect
	private ArrayList<VEDraw> epPics;

	//crop
	private Crop mCrop;


	public VEVideo(String videoPath) {
		this.videoPath = videoPath;
		epPics = new ArrayList<>();
	}

	private StringBuilder getFilter() {
		if (filter == null || filter.toString().equals("")) {
			filter = new StringBuilder();
		} else {
			filter.append(",");
		}
		return filter;
	}

	/**
	 * Get Filters
	 *
	 * @return
	 */
	public StringBuilder getFilters() {
		return filter;
	}

	/**
	 * Get Video Path
	 *
	 * @return
	 */
	public String getVideoPath() {
		return videoPath;
	}

	/**
	 * Get whether video is clipped
	 *
	 * @return
	 */
	public boolean getVideoClip() {
		return isClip;
	}

	/**
	 * Get clip start time
	 *
	 * @return
	 */
	public float getClipStart() {
		return clipStart;
	}

	/**
	 * Get clip duration
	 *
	 * @return
	 */
	public float getClipDuration() {
		return clipDuration;
	}

	/**
	 * Set up clip settings
	 *
	 * @param start    start time in second
	 * @param duration duration time in second
	 * @return
	 */
	public VEVideo clip(float start, float duration) {
		isClip = true;
		this.clipStart = start;
		this.clipDuration = duration;
		return this;
	}


	/**
	 * Set up rotation and mirror
	 *
	 * @param rotation rotation angle (only 90/180/270 degrees are supported)
	 * @param isFlip   whether mirror
	 * @return
	 */
	public VEVideo rotation(int rotation, boolean isFlip) {
		filter = getFilter();
		if (isFlip) {
			switch (rotation) {
				case 0:
					filter.append("hflip");
					break;
				case 90:
					filter.append("transpose=3");
					break;
				case 180:
					filter.append("vflip");
					break;
				case 270:
					filter.append("transpose=0");
					break;
			}
		} else {
			switch (rotation) {
				case 90:
					filter.append("transpose=2");
					break;
				case 180:
					filter.append("vflip,hflip");
					break;
				case 270:
					filter.append("transpose=1");
					break;
			}
		}
		return this;
	}

	/**
	 * Set up cropping
	 *
	 * @param width  cropped wioth
	 * @param height cropped height
	 * @param x      start position X
	 * @param y      Start position Y
	 * @return
	 */
	public VEVideo crop(float width, float height, float x, float y) {
		filter = getFilter();
		mCrop = new Crop(width,height,x,y);
		filter.append("crop=" + width + ":" + height + ":" + x + ":" + y);
		return this;
	}

	/**
	 * Get Crop Setting
	 * @return
	 */
	public Crop getCrop(){
		return mCrop;
	}

	/**
	 * Add text to video
	 *
	 * @param size  font size
	 * @param color font color(white,black,blue,red...)
	 * @param x     font coordinate x
	 * @param y     font coordinate y
	 * @param ttf   font path
	 * @param text  text needs to be added
	 * @deprecated
	 */
	@Deprecated
	public VEVideo addText(int x, int y, float size, String color, String ttf, String text) {
		filter = getFilter();
		filter.append("drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color + ":x=" + x + ":y=" + y + ":text='" + text + "'");
		return this;
	}

	/**
	 * Add text with duration
	 *
	 * @param VEText  VEText
	 */
	public VEVideo addText(VEText VEText) {
		filter = getFilter();
		filter.append(VEText.getTextFitler());
		return this;
	}

	/**
	 * Add timing info to video
	 *
	 * @param size  font size
	 * @param color font color(white,black,blue,red...)
	 * @param x     font coordinate x
	 * @param y     font coordinate y
	 * @param ttf   font path
	 * @param type  time type(1==>hh:mm:ss,2==>yyyy-MM-dd hh:mm:ss,3==>yyyyYearMMMonthddDay hhHourmmMinutessSecond)
	 */
	public VEVideo addTime(int x, int y, float size, String color, String ttf, int type){
		long time=System.currentTimeMillis()/1000;
		String  str=String.valueOf(time);
		filter = getFilter();
		String ts = "";
		switch (type){
			case 1:
				ts = "%{pts\\:localtime\\:" + str + "\\:%H\\\\\\:%M\\\\\\:%S}";
				break;
			case 2:
				ts = "%{pts\\:localtime\\:" + str + "}";
				break;
			case 3:
				ts = "%{pts\\:localtime\\:" + str + "\\:%Y\\\\年%m\\\\月%d\\\\日\n%H\\\\\\时%M\\\\\\分%S秒}";
				break;
		}
		filter.append("drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color + ":x=" + x + ":y=" + y + ":text='"+ts+"'");
		return this;
	}

	/**
	 * Add filter effect
	 *
	 * @param ofi command
	 * @return
	 */
	public VEVideo addFilter(String ofi) {
		filter = getFilter();
		filter.append(ofi);
		return this;
	}

	/**
	 * Add images to video
	 *
	 * @param VEDraw Special Image type
	 * @return
	 */
	public VEVideo addDraw(VEDraw VEDraw) {
		epPics.add(VEDraw);
		return this;
	}

	/**
	 * Get a list of the images added
	 *
	 * @return
	 */
	public ArrayList<VEDraw> getEpDraws() {
		return epPics;
	}

	/**
	 * Crop Information
	 */
	public class Crop {
		float width;
		float height;
		float x;
		float y;

		public Crop(float width, float height, float x, float y) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
		}

		public float getWidth() {
			return width;
		}

		public float getHeight() {
			return height;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}
	}
}
