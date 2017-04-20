package mbeb.opengldefault.curves;

import java.util.*;

import org.joml.*;

import mbeb.opengldefault.logging.*;

/**
 * A Bezier Curve that can be used as a path for a camera or an entity.
 * Example usage in {@link mbeb.opengldefault.scene.behaviour.BezierBehaviour}
 */
public class BezierCurve {

	private static final String TAG = "BezierCurve";

	public enum ControlPointInputMode {
		/**
		 * Directly use the given Input Points as ControlPoints
		 */
		CONTROLPOINTS,
		/**
		 * Generate path, that contains all of the Input Positions
		 */
		CAMERAPOINTS,
		/**
		 * Generate circular path, that contains all of the Input Positions
		 */
		CAMERAPOINTSCIRCULAR
	}

	/**
	 * Matrices that can be multiplied with a progress Vector (u^3, u^2, u, 1) to determine a position on a segment with
	 * given progress
	 */
	private List<Matrix4f> bezierMatrices;
	/** Control Points for the Curve */
	private List<Vector3f> controlPoints;
	/** The segments lengths */
	private List<Float> segmentLengths;
	/** Total length of all segments */
	private float totalLength;
	/** The input mode of the ControlPoint Data */
	private final ControlPointInputMode mode;

	/**
	 * Constructor for a BezierCurve
	 *
	 * @param controlPoints
	 *            Input Control Points
	 */
	public BezierCurve(final List<Vector3f> controlPoints) {
		this(controlPoints, ControlPointInputMode.CAMERAPOINTS, false);
	}

	/**
	 * Constructor for a BezierCurve
	 *
	 * @param controlPoints
	 *            Input Control Points
	 * @param mode
	 *            ControlPointInputMode
	 */
	public BezierCurve(final List<Vector3f> controlPoints, final ControlPointInputMode mode) {
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
	public BezierCurve(final List<Vector3f> controlPoints, final ControlPointInputMode mode, final boolean adaptiveSegmentLength) {
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
	public BezierCurve(final List<Vector3f> controlPoints, final List<Float> segmentLengths) {
		this(controlPoints, ControlPointInputMode.CAMERAPOINTS, segmentLengths);
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
	public BezierCurve(final List<Vector3f> controlPoints, final ControlPointInputMode mode, final List<Float> segmentLengths) {
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
	public final void setInputPoints(final List<Vector3f> inputControlPoints) {
		switch(mode) {
			case CAMERAPOINTS:
				generatePath(inputControlPoints);
				break;
			case CAMERAPOINTSCIRCULAR:
				generateCircularPath(inputControlPoints);
				break;
			case CONTROLPOINTS:
				setControlPoints(inputControlPoints);
				break;
			default:
				Log.error(TAG, "Unknown ControlPointMode");
				break;
		}
		generateMatrices();
	}

	/**
	 * Generates the Bezier Matrices
	 */
	private void generateMatrices() {
		bezierMatrices = new ArrayList<>();
		for (int segmentID = 0; segmentID < controlPoints.size() / 3; segmentID++) {
			final Vector4f v0 = new Vector4f(controlPoints.get(segmentID * 3 + 0), 1);
			final Vector4f v1 = new Vector4f(controlPoints.get(segmentID * 3 + 1), 1);
			final Vector4f v2 = new Vector4f(controlPoints.get(segmentID * 3 + 2), 1);
			final Vector4f v3 = new Vector4f(controlPoints.get(segmentID * 3 + 3), 1);
			bezierMatrices.add(new Matrix4f(v0, v1, v2, v3));
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
	private Vector3f calculateSegmentDirection(final List<Vector3f> cameraPositions, final int segment) {
		final Vector3f start = cameraPositions.get(segment % cameraPositions.size());
		final Vector3f end = cameraPositions.get((segment + 1) % cameraPositions.size());

		final Vector3f res = new Vector3f();
		end.sub(start, res);

		return res;
	}

	/**
	 * Sets the ControlPoints if the mode is ControlPoints
	 *
	 * @param controlPoints
	 */
	public void setControlPoints(final List<Vector3f> controlPoints) {
		this.controlPoints = controlPoints;
	}

	/**
	 * Generates the path if the mode is CameraPoints.
	 *
	 * @param cameraPositions
	 *            input Control Points
	 */
	private void generatePath(final List<Vector3f> cameraPositions) {
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

			final Vector3f tangent = new Vector3f();
			dirNext.add(dirPrevious, tangent);
			tangent.normalize();
			tangent.mul(java.lang.Math.min(lengthNext, lengthPrevious) / 2.0f);

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
	private void generateCircularPath(final List<Vector3f> cameraPositions) {
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

			final Vector3f tangent = new Vector3f();
			dirNext.add(dirPrevious, tangent);
			tangent.normalize();
			final float tangentLength = java.lang.Math.min(lengthNext, lengthPrevious);

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
	private void generateFirstControlPoint(final List<Vector3f> cameraPositions, final Vector3f direction, final float length) {
		final Vector3f currentCameraPos = new Vector3f(cameraPositions.get(0));

		final Vector3f tangent = new Vector3f();
		direction.mul(length * 0.5f, tangent);

		final Vector3f newControlPoint = new Vector3f(currentCameraPos).add(tangent);

		controlPoints.add(currentCameraPos);
		controlPoints.add(new Vector3f(newControlPoint));
	}

	/**
	 * Adds a middle control Point and its predecessor and successor to the List of control Points
	 *
	 * @param cameraPositions
	 *            input Control Points
	 * @param camPosID
	 *            Id of the Point in the Input List
	 * @param tangent
	 *            Tangent on this Point
	 */
	private void generateMidControlPoint(final List<Vector3f> cameraPositions, final int camPosID, final Vector3f tangent) {
		final Vector3f currentCameraPos = new Vector3f(cameraPositions.get(camPosID));

		final Vector3f prevControlPos = new Vector3f(currentCameraPos).sub(tangent);
		final Vector3f nextControlPos = new Vector3f(currentCameraPos).add(tangent);

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
	private void generateLastControlPoint(final List<Vector3f> cameraPositions, final Vector3f direction, final float length, final boolean circular) {
		final Vector3f currentCameraPos = new Vector3f(cameraPositions.get(circular ? 0 : cameraPositions.size() - 1));

		final Vector3f tangent = new Vector3f();
		direction.mul(length * 0.5f, tangent);

		final Vector3f newControlPoint = new Vector3f(currentCameraPos).sub(tangent);

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
	public final void generateSegmentLength(final boolean adaptiveSegmentLength) {
		segmentLengths = new ArrayList<>();
		if (adaptiveSegmentLength) {
			for (int i = 0; i < controlPoints.size() - 1; i += 3) {
				float thisSegmentsLength = 0;
				Vector3f lastPos = calculateSegmentPosition(i / 3, 0);
				//TODO: Dynamic Sample Size?
				final int sampleSize = 10;
				for (int o = 1; o <= sampleSize; o++) {
					final Vector3f nextPos = calculateSegmentPosition(i / 3, o / (float) sampleSize);
					thisSegmentsLength += nextPos.distance(lastPos);
					lastPos = nextPos;
				}
				segmentLengths.add(thisSegmentsLength);
				totalLength += thisSegmentsLength;
			}
		} else {
			for (int i = 0; i < controlPoints.size() - 1; i += 3) {
				segmentLengths.add(1f);
			}
			totalLength = segmentLengths.size();
		}
	}

	/**
	 * Setter for segment Length. Calculates total length
	 *
	 * @param segmentLengths
	 *            input segment lengths
	 */
	public final void setSegmentLengths(final List<Float> segmentLengths) {
		this.segmentLengths = segmentLengths;
		totalLength = 0;
		for (final float length : segmentLengths) {
			totalLength += length;
		}
	}

	/**
	 * Get Position of the Curve with given progress
	 *
	 * @param progress
	 *            total progress on the curve in [0, curve.getTotalLength()]
	 * @return resulting position
	 */
	public Vector3f getPosition(final float progress) {
		return getPosition(progress, new Matrix4f());
	}

	/**
	 * Get Position of the Curve with given progress
	 *
	 * @param progress
	 *            total progress on the curve in [0, curve.getTotalLength()]
	 * @param modelTransform
	 *            a matrix to apply to the result
	 * @return resulting position
	 */
	public Vector3f getPosition(final float progress, final Matrix4f modelTransform) {
		int segmentID = 0;
		float progressInFragment = 0;
		float aggregatedSegmentLength = 0;
		for (int i = 0; i < segmentLengths.size(); i++) {
			final float lengthOfThisFragment = segmentLengths.get(i);
			if (aggregatedSegmentLength + lengthOfThisFragment > progress) {
				segmentID = i;
				progressInFragment = (progress - aggregatedSegmentLength) / lengthOfThisFragment;
				break;
			} else {
				aggregatedSegmentLength += lengthOfThisFragment;
			}
		}
		Vector3f localResult = calculateSegmentPosition(segmentID, progressInFragment);
		Vector4f transformed = modelTransform.transform(new Vector4f(localResult, 1));
		return new Vector3f(transformed.x, transformed.y, transformed.z);
	}

	/**
	 * Returns bernstein Matrix
	 *
	 * @return bernstein Vector. ControlPointMatrix * bernsteinVector = resulting position
	 */

	public Matrix4f bernstein() {
		/* @formatter:off */
		return new Matrix4f(
				-1,  3, -3,  1,
				 3, -6,  3,  0,
				-3,  3,  0,  0,
				 1,  0,  0,  0);
		/* @formatter:on */
	}

	/**
	 * Returns progess Vector for given progress
	 *
	 * @param progressInFragment
	 *            the progress in the current Curve segment
	 * @return progess Vector.
	 */
	private Vector4f progressVector(final double progressInFragment) {
		return new Vector4f((float) java.lang.Math.pow(progressInFragment, 3), (float) java.lang.Math.pow(progressInFragment, 2), (float) progressInFragment, 1);
	}

	/**
	 * Returns the progress in the segment
	 *
	 * @param segmentID
	 *            current Curve segment
	 * @param progressInFragment
	 *            the progress in the current Curve segment
	 * @return resulting position. ControlPointMatrix * bernsteinVector = resulting position
	 */

	private Vector3f calculateSegmentPosition(final int segmentID, final float progressInFragment) {
		final Vector4f result = progressVector(progressInFragment).mul(bernstein()).mul(bezierMatrices.get(segmentID));

		return new Vector3f(result.x, result.y, result.z);
	}

	/**
	 * Getter for the Bezier Matrices. They contain the four control points in their columns
	 *
	 * @return the Bezier Matrices
	 */
	public List<Matrix4f> getBezierMatrices() {
		return bezierMatrices;
	}

	/**
	 * Getter for the control points
	 *
	 * @return control points
	 */
	public List<Vector3f> getControlPoints() {
		return controlPoints;
	}

	/**
	 * Getter for the total Curve length
	 *
	 * @return the curve length
	 */
	public float getTotalLength() {
		return totalLength;
	}

}
