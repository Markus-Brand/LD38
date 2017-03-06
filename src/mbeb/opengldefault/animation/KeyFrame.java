package mbeb.opengldefault.animation;

/**
 * a timestamp and a pose
 */
public class KeyFrame {

	private final double timeStamp;
	private final Pose pose;

	public KeyFrame(double timeStamp, Pose pose) {
		this.timeStamp = timeStamp;
		this.pose = pose;
	}

	public double getTimeStamp() {
		return timeStamp;
	}

	public Pose getPose() {
		return pose;
	}

	/**
	 * merge the data of a given KeyFrame into this one
	 * @param newOne 
	 */
	public void mergeWith(KeyFrame newOne) {
		getPose().mergeWith(newOne.getPose());
	}

	@Override
	public String toString() {
		return "KeyFrame(" + timeStamp + ", " + pose + ")";
	}

}
