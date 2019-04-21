package VideoHandle;

public class VEDraw {

	private String picPath;
	private int picX;//image position x
	private int picY;//image position y
	private float picWidth;
	private float picHeight;
	private boolean isAnimation;//if is gif

	private String time = "";//start end time

	private String picFilter;//filter

	public VEDraw(String picPath, int picX, int picY, float picWidth, float picHeight, boolean isAnimation) {
		this.picPath = picPath;
		this.picX = picX;
		this.picY = picY;
		this.picWidth = picWidth;
		this.picHeight = picHeight;
		this.isAnimation = isAnimation;
	}

	public VEDraw(String picPath, int picX, int picY, float picWidth, float picHeight, boolean isAnimation, int start, int end) {
		this.picPath = picPath;
		this.picX = picX;
		this.picY = picY;
		this.picWidth = picWidth;
		this.picHeight = picHeight;
		this.isAnimation = isAnimation;
		time = ":enable=between(t\\," + start + "\\," + end + ")";
	}

	public String getPicPath() {
		return picPath;
	}

	public int getPicX() {
		return picX;
	}

	public int getPicY() {
		return picY;
	}

	public float getPicWidth() {
		return picWidth;
	}

	public float getPicHeight() {
		return picHeight;
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	public String getPicFilter() {
		return picFilter == null ? "" : (picFilter + ",");
	}

	public String getTime() {
		return time;
	}

	public void setPicFilter(String picFilter) {
		this.picFilter = picFilter;
	}
}
