package mbeb.opengldefault.curves;

import java.util.ArrayList;

import org.joml.Vector3f;

public class BezierCurve {
	/** Control Points for the Curve */
	ArrayList<Vector3f> controlPoints;
	ArrayList<Float> segmentLengths;

	float maxLength;

	public BezierCurve(ArrayList<Vector3f> controlPoints) {
		this(controlPoints, false, false);
	}

	public BezierCurve(ArrayList<Vector3f> controlPoints, boolean calculateMidPositions, boolean adaptiveSegmentLength) {
		setControlPoints(controlPoints, calculateMidPositions);
		generateSegmentLength(adaptiveSegmentLength);
	}

	public BezierCurve(ArrayList<Vector3f> controlPoints, ArrayList<Float> segmentLengths) {
		this(controlPoints, false, segmentLengths);
	}

	public BezierCurve(ArrayList<Vector3f> controlPoints, boolean calculateMidPositions, ArrayList<Float> segmentLengths) {
		setControlPoints(controlPoints, calculateMidPositions);
		setSegmentLengths(segmentLengths);
	}

	public void setControlPoints(ArrayList<Vector3f> newControlPoints, boolean calculateMidPositions) {
		if (calculateMidPositions) {
			Vector3f direction = new Vector3f();
			newControlPoints.get(newControlPoints.size() - 1).sub(newControlPoints.get(1), direction);
			direction.mul(1.0f / 3.0f);
			this.controlPoints = new ArrayList<>();

			controlPoints.add(newControlPoints.get(0));

			Vector3f nextMidPos = new Vector3f();
			newControlPoints.get(0).sub(direction, nextMidPos);
			controlPoints.add(nextMidPos);

			Vector3f lastMidPos = new Vector3f();
			newControlPoints.get(0).add(direction, lastMidPos);

			for (int i = 1; i < newControlPoints.size(); i++) {
				nextMidPos = new Vector3f();
				direction = new Vector3f();
				newControlPoints.get(i - 1).sub(newControlPoints.get((i + 1) % newControlPoints.size()), direction);
				direction.mul(1.0f / 3.0f);

				newControlPoints.get(i).add(direction, nextMidPos);
				controlPoints.add(nextMidPos);

				controlPoints.add(newControlPoints.get(i));

				nextMidPos = new Vector3f();
				newControlPoints.get(i).sub(direction, nextMidPos);
				controlPoints.add(nextMidPos);

			}

			controlPoints.add(lastMidPos);
			controlPoints.add(newControlPoints.get(0));
			for (int i = 0; i < controlPoints.size(); i++) {
				System.out.println(controlPoints.get(i).x + " " + controlPoints.get(i).y + " " + controlPoints.get(i).z);
			}

		} else {
			setControlPoints(newControlPoints);
		}
	}

	public void setControlPoints(ArrayList<Vector3f> controlPoints) {
		this.controlPoints = controlPoints;
	}

	public void generateSegmentLength(boolean adaptiveSegmentLength) {
		segmentLengths = new ArrayList<>();
		if (adaptiveSegmentLength) {
			for (int i = 0; i < controlPoints.size() - 1; i += 3) {
				float thisSegmentsLength = 0;
				Vector3f lastPos = calculateSegmentPosition(i / 3, 0);
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

	public void setSegmentLengths(ArrayList<Float> segmentLengths) {
		this.segmentLengths = segmentLengths;
		maxLength = 0;
		for (float length : segmentLengths) {
			maxLength += length;
		}
	}

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

	private float bernstein(int functionIndex, double progressInFragment) {
		float bernstein = (float) (Math.pow(progressInFragment, functionIndex) * Math.pow(1.0 - progressInFragment, 3 - functionIndex));
		if ((functionIndex + 1) % 4 >= 2) {
			bernstein *= 3;
		}
		return bernstein;
	}

	private Vector3f calculateSegmentPosition(int segmentID, float progressInFragment) {
		Vector3f result = new Vector3f();
		for (int i = 0; i < 4; i++) {
			Vector3f bernstein = new Vector3f();
			controlPoints.get(segmentID * 3 + i).mul(bernstein(i, progressInFragment), bernstein);
			result.add(bernstein);
		}
		return result;
	}

	public float getMaxLength() {
		return maxLength;
	}

}
