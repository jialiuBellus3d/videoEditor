package VideoHandle;

public class VEText {

	private String textFitler;

	/**
	 * @param size  font size
	 * @param color font color(white,black,blue,red...)
	 * @param x     font coordinate x
	 * @param y     font coordinate y
	 * @param ttf   font path
	 * @param text  text needs to be added
	 * @param time  start and end time(null means shown all time)
	 */
	public VEText(int x, int y, float size, Color color, String ttf, String text, Time time) {
		this.textFitler = "drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color.getColor() + ":x=" + x + ":y=" + y + ":text='" + text + "'" + (time == null ? "" : time.getTime());
	}

	public String getTextFitler() {
		return textFitler;
	}

	/**
	 * Start End time class
	 */
	public static class Time {
		private String time;

		public Time(int start, int end) {
			this.time = ":enable=between(t\\," + start + "\\," + end + ")";
		}

		public String getTime() {
			return time;
		}
	}

	/**
	 * Color class
	 */
	public enum Color {
		Red("Red"), Blue("Blue"), Yellow("Yellow"), Black("Black"), DarkBlue("DarkBlue"),
		Green("Green"), SkyBlue("SkyBlue"), Orange("Orange"), White("White"), Cyan("Cyan");
		private String color;

		Color(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}
	}
}
