package mbeb.opengldefault.curves;

import java.util.ArrayList;

import mbeb.opengldefault.logging.Log;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A Bezier Curve that can be used as a path for a camera or an entity.
 * Example usage in {@link mbeb.opengldefault.camera.BezierCamera}
 *
 */
public class BezierCurve {

	private static final String TAG = "BezierCurve";

	public static enum ControlPointInputMode {
		/**
		 * Directly use the given Input Points as ControlPoints
		 */
		ControlPoints,
		/**
		 * Generate path, that contains all of the Input Positions
		 */
		CameraPoints,
		/**
		 * Generate circluar path, that contains all of the Input Positions
		 */
		CameraPointsCircular
	}

	/** Control Points for the Curve */
	private ArrayList<Vector3f> controlPoints;
	/** The segments legths */
	private ArrayList<Float> segmentLengths;
	/** Total length of all segments */
	private float maxLength;
	/** The input mode of the ControlPoint Data */
	private ControlPointInputMode mode;

	/**
	 * Constructor for a BezierCurve
	 *
	 * @param controlPoints
	 *            Input Control Points
	 */
	public BezierCurve(ArrayList<Vector3f> controlPoints) {
		this(controlPoints, ControlPointInputMode.CameraPoints, false);
	}

	/**
	 * Constructor for a BezierCurve
	 *
	 * @param controlPoints
	 *            Input Control Points
	 * @param mode
	 *            ControlPointInputMode
	 */
	public BezierCurve(ArrayList<Vector3f> controlPoints, ControlPointInputMode mode) {
		this(controlPoints, mode, false);
	}

	/**
	 * Constructor for a BezierCurve
	 *
	 * @param controlPoints
	 *            Input Control Points
	 * @param mode
	 *            ControlPointInputMode
	 * @param adaptiveSegmentLength
	 *            should the segment length be generated depending on the Curve lengths
	 */
	public BezierCurve(ArrayList<Vector3f> controlPoints, ControlPointInputMode mode, boolean adaptiveSegmentLength) {
		this.mode = mode;
		setInputPoints(controlPoints);
		generateSegmentLength(adaptiveSegmentLength);
	}

	/**
	 * Constructor for a BezierCurve
	 *
	 * @param controlPoints
	 *            Input Control Points
	 * @param segmentLengths
	 *            input segment length
	 */
	public BezierCurve(ArrayList<Vector3f> controlPoints, ArrayList<Float> segmentLengths) {
		this(controlPoints, ControlPointInputMode.CameraPoints, segmentLengths);
	}

	/**
	 * Constructor for a BezierCurve
	 *
	 * @param controlPoints
	 *            Input Control Points
	 * @param mode
	 *            ControlPointInputMode
	 * @param segmentLengths
	 *            input segment length
	 */
	public BezierCurve(ArrayList<Vector3f> controlPoints, ControlPointInputMode mode, ArrayList<Float> segmentLengths) {
		this.mode = mode;
		setInputPoints(controlPoints);
		setSegmentLengths(segmentLengths);
	}

	/**
	 * Generate Control Positions according to the Input mode
	 *
	 * @param inputControlPoints
	 *            input Control Points
	 */
	public void setInputPoints(ArrayList<Vector3f> inputControlPoints) {
		switch (mode) {
			case CameraPoints:
				generatePath(inputControlPoints);
				break;
			case CameraPointsCircular:
				generateCircularPath(inputControlPoints);
				break;
			case ControlPoints:
				setControlPoints(inputControlPoints);
				break;
			default:
				Log.error(TAG, "Unknown ControlPointMode");
				break;
		}
	}

	/**
	 * Calculates the direction of a segment
	 *
	 * @param cameraPositions
	 *            input Control Points
	 * @param segment
	 *            id of the segment
	 * @return
	 */
	private Vector3f calculateSegmentDirection(ArrayList<Vector3f> cameraPositions, int segment) {
		Vector3f start = cameraPositions.get(segment % cameraPositions.size());
		Vector3f end = cameraPositions.get((segment + 1) % cameraPositions.size());

		Vector3f res = new Vector3f();
		end.sub(start, res);

		return res;
	}

	/**
	 * Sets the ControlPoints if the mode is ControlPoints
	 *
	 * @param controlPoints
	 */
	public void setControlPoints(ArrayList<Vector3f> controlPoints) {
		this.controlPoints = controlPoints;
	}

	/**
	 * Generates the path if the mode is CameraPoints.
	 *
	 * @param cameraPositions
	 *            input Control Points
	 */
	private void generatePath(ArrayList<Vector3f> cameraPositions) {
		controlPoints = new ArrayList<>();

		Vector3f dirPrevious = calculateSegmentDirection(cameraPositions, 0);
		float lengthPrevious = dirPrevious.length();
		dirPrevious.normalize();

		Vector3f dirNext;
		float lengthNext;

		generateFirstControlPoint(cameraPositions, dirPrevious, lengthPrevious);

		for (int camPosID = 1; camPosID < cameraPositions.size() - 1; camPosID++) {
			dirNext = calculateSegmentDirection(cameraPositions, camPosID);
			lengthNext = dirNext.length();
			dirNext.normalize();

			Vector3f tangent = new Vector3f();
			dirNext.add(dirPrevious, tangent);
			tangent.normalize();
			tangent.mul(Math.min(lengthNext, lengthPrevious) / 2.0f);

			generateMidControlPoint(cameraPositions, camPosID, tangent);

			dirPrevious = dirNext;
			lengthPrevious = lengthNext;

		}

		generateLastControlPoint(cameraPositions, dirPrevious, lengthPrevious, false);
	}

	/**
	 * Generates the path if the mode is CameraPointsCircular.
	 *
	 * @param cameraPositions
	 *            input Control Points
	 */
	private void generateCircularPath(ArrayList<Vector3f> cameraPositions) {
		controlPoints = new ArrayList<>();

		Vector3f dirPrevious = calculateSegmentDirection(cameraPositions, cameraPositions.size() - 1);
		float lengthPrevious = dirPrevious.length();
		dirPrevious.normalize();

		Vector3f dirNext;
		float lengthNext;

		for (int camPosID = 0; camPosID <= cameraPositions.size(); camPosID++) {
			dirNext = calculateSegmentDirection(cameraPositions, camPosID);
			lengthNext = dirNext.length();
			dirNext.normalize();

			Vector3f tangent = new Vector3f();
			dirNext.add(dirPrevious, tangent);
			tangent.normalize();
			float tangentLength = Math.min(lengthNext, lengthPrevious);

			if (camPosID == 0) {
				generateFirstControlPoint(cameraPositions, tangent, tangentLength);
			} else if (camPosID == cameraPositions.size()) {
				generateLastControlPoint(cameraPositions, tangent, tangentLength, true);
			} else {
				tangent.mul(tangentLength * 0.5f);
				generateMidControlPoint(cameraPositions, camPosID, tangent);
			}

			dirPrevious = dirNext;
			lengthPrevious = lengthNext;

		}
	}

	/**
	 * Adds the first control Point and its successor to the List of control Points
	 *
	 * @param cameraPositions
	 *            input Control Points
	 * @param direction
	 *            direction of the "Tangent"
	 * @param length
	 *            length of the "Tangent"
	 */
	private void generateFirstControlPoint(ArrayList<Vector3f> cameraPositions, Vector3f direction, float length) {
		Vector3f currentCameraPos = new Vector3f(cameraPositions.get(0));

		Vector3f tangent = new Vector3f();
		direction.mul(length * 0.5f, tangent);

		Vector3f newControlPoint = new Vector3f(currentCameraPos).add(tangent);

		controlPoints.add(currentCameraPos);
		controlPoints.add(new Vector3f(newControlPoint));
	}

	/**
	 * Adds a middle control Point and its predecessor and succesor to the List of control Points
	 *
	 * @param cameraPositions
	 *            input Control Points
	 * @param camPosID
	 *            Id of the Point in the Input List
	 * @param tangent
	 *            Tangent on this Point
	 */
	private void generateMidControlPoint(ArrayList<Vector3f> cameraPositions, int camPosID, Vector3f tangent) {
		Vector3f currentCameraPos = new Vector3f(cameraPositions.get(camPosID));

		Vector3f prevControlPos = new Vector3f(currentCameraPos).sub(tangent);
		Vector3f nextControlPos = new Vector3f(currentCameraPos).add(tangent);

		controlPoints.add(prevControlPos);
		controlPoints.add(currentCameraPos);
		controlPoints.add(nextControlPos);
	}

	/**
	 * Adds the last control Point and its predecessor to the List of control Points
	 *
	 * @param cameraPositions
	 *            input Control Points
	 * @param direction
	 *            direction of the "Tangent"
	 * @param length
	 *            length of the "Tangent"
	 */
	private void generateLastControlPoint(ArrayList<Vector3f> cameraPositions, Vector3f direction, float length, boolean circular) {
		Vector3f currentCameraPos = new Vector3f(cameraPositions.get(circular ? 0 : cameraPositions.size() - 1));

		Vector3f tangent = new Vector3f();
		direction.mul(length * 0.5f, tangent);

		Vector3f newControlPoint = new Vector3f(currentCameraPos).sub(tangent);

		controlPoints.add(new Vector3f(newControlPoint));
		controlPoints.add(currentCameraPos);
	}

	/**
	 * Generates the segments lengths
	 *
	 * @param adaptiveSegmentLength
	 *            true -> segment lengths are depending on the lengths of the actual curve
	 *            false -> segment lengths are always 1
	 */
	public void generateSegmentLength(boolean adaptiveSegmentLength) {
		segmentLengths = new ArrayList<>();
		if (adaptiveSegmentLength) {
			for (int i = 0; i < controlPoints.size() - 1; i += 3) {
				float thisSegmentsLength = 0;
				Vector3f lastPos = calculateSegmentPosition(i / 3, 0);
				//TODO: Dynamic Sample Size?
				int sampleSize = 10;
				for (int o = 1; o <= sampleSize; o++) {
					Vector3f nextPos = calculateSegmentPosition(i / 3, o / (float) sampleSize);
					thisSegmentsLength += nextPos.distance(lastPos);
					lastPos = nextPos;
				}
				segmentLengths.add(new Float(thisSegmentsLength));
				maxLength += thisSegmentsLength;
			}
		} else {
			for (int i = 0; i < controlPoints.size() - 1; i += 3) {
				segmentLengths.add(new Float(1));
			}
			maxLength = segmentLengths.size();
		}
	}

	/**
	 * Setter for segment Length. Calculates total length
	 *
	 * @param segmentLengths
	 *            input segment lengths
	 */
	public void setSegmentLengths(ArrayList<Float> segmentLengths) {
		this.segmentLengths = segmentLengths;
		maxLength = 0;
		for (float length : segmentLengths) {
			maxLength += length;
		}
	}

	/**
	 * Get Position of the Curve with given progress
	 *
	 * @param progress
	 *            total progress on the curve
	 * @return resulting position
	 */
	public Vector3f getPosition(float progress) {
		int segmentID = 0;
		float progressInFragment = 0;
		float aggregatedSegmentLength = 0;
		for (int i = 0; i < segmentLengths.size(); i++) {
			float lengthOfThisFragment = segmentLengths.get(i);
			if (aggregatedSegmentLength + lengthOfThisFragment > progress) {
				segmentID = i;
				progressInFragment = (progress - aggregatedSegmentLength) / lengthOfThisFragment;
				break;
			} else {
				aggregatedSegmentLength += lengthOfThisFragment;
			}
		}
		return calculateSegmentPosition(segmentID, progressInFragment);
	}

	/**
	 * Returns berstein Vector for given progress
	 *
	 * @param progressInFragment
	 *            the progress in the current Curve segment
	 * @return berstein Vector. ControlPointMatrix * bernsteinVector = resulting position
	 */
	private Vector4f bernstein(double progressInFragment) {
		Vector4f uVector = new Vector4f((float) Math.pow(progressInFragment, 3), (float) Math.pow(progressInFragment, 2), (float) progressInFragment, 1);
		/* @formatter:off */
		Matrix4f bernstein = new Matrix4f(
				-1,  3, -3,  1,
				 3, -6,  3,  0,
				-3,  3,  0,  0,
				 1,  0,  0,  0);
		/* @formatter:on */
		return uVector.mul(bernstein);
	}

	/**
	 * Returns the progress in the segment
	 *
	 * @param segmentID
	 *            current Curve segment
	 * @param progressInFragment
	 *            the progress in the current Curve segment
	 * @return reuslting position. ControlPointMatrix * bernsteinVector = resulting position
	 */
	private Vector3f calculateSegmentPosition(int segmentID, float progressInFragment) {
		Vector4f v0 = new Vector4f(controlPoints.get(segmentID * 3 + 0), 1);
		Vector4f v1 = new Vector4f(controlPoints.get(segmentID * 3 + 1), 1);
		Vector4f v2 = new Vector4f(controlPoints.get(segmentID * 3 + 2), 1);
		Vector4f v3 = new Vector4f(controlPoints.get(segmentID * 3 + 3), 1);

		Matrix4f controlPointMatrix = new Matrix4f(v0, v1, v2, v3);

		Vector4f result = bernstein(progressInFragment).mul(controlPointMatrix);

		return new Vector3f(result.x, result.y, result.z);
	}

	/**
	 * Getter for the total Curve length
	 *
	 * @return the curve length
	 */
	public float getMaxLength() {
		return maxLength;
	}

}
