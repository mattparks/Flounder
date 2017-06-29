package editors.editor;

import flounder.camera.*;
import flounder.devices.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

public class EditorCamera extends Camera {
	// Defines basic view frustum sizes.
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 500.0f;

	private static final float FIELD_OF_VIEW = 45.0f;

	private static final float CAMERA_AIM_OFFSET = 1.5f;

	private static final float MAX_ANGLE_OF_ELEVATION = 45.0f;
	private static final float MIN_ANGLE_OF_ELEVATION = 0.0f;
	private static final float MINIMUM_ZOOM = 0.5f;
	private static final float MAXIMUM_ZOOM = 28.0f;
	private static final float NORMAL_ZOOM = 8.0f;

	private Vector3f position;
	private Vector3f rotation;

	private Frustum viewFrustum;
	private Ray viewRay;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;

	private float angleOfElevation;
	private float angleAroundPlayer;

	private Vector3f targetPosition;
	private Vector3f targetRotation;
	private float targetZoom;
	private float targetElevation;
	private float targetRotationAngle;

	private float actualDistanceFromPoint;
	private float horizontalDistanceFromFocus;
	private float verticalDistanceFromFocus;

	public EditorCamera() {
		super(FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class);
	}

	@Override
	public void init() {
		this.position = new Vector3f();
		this.rotation = new Vector3f(0.0f, 20.0f, 0.0f);

		this.viewFrustum = new Frustum();
		this.viewRay = new Ray(true, new Vector2f());
		this.viewMatrix = new Matrix4f();
		this.projectionMatrix = new Matrix4f();

		this.angleOfElevation = 25.0f;
		this.angleAroundPlayer = 0.0f;

		this.targetPosition = new Vector3f();
		this.targetRotation = new Vector3f();
		this.targetZoom = NORMAL_ZOOM;
		this.targetElevation = angleOfElevation;
		this.targetRotationAngle = angleAroundPlayer;

		this.actualDistanceFromPoint = targetZoom;
		this.horizontalDistanceFromFocus = 0.0f;
		this.verticalDistanceFromFocus = 0.0f;

		calculateDistances();
	}

	@Override
	public float getNearPlane() {
		return NEAR_PLANE;
	}

	@Override
	public float getFarPlane() {
		return FAR_PLANE;
	}

	@Override
	public float getFOV() {
		return FIELD_OF_VIEW;
	}

	@Override
	public void update(Player player) {
		calculateHorizontalAngle();
		calculateVerticalAngle();
		calculateZoom();

		if (player != null) {
			this.targetPosition.set(player.getPosition());
			this.targetRotation.set(player.getRotation());
		}

		updateActualZoom();
		updateHorizontalAngle();
		updatePitchAngle();
		calculateDistances();
		calculatePosition();
	}

	private void calculateHorizontalAngle() {
		if (targetRotationAngle >= Maths.DEGREES_IN_HALF_CIRCLE) {
			targetRotationAngle -= Maths.DEGREES_IN_CIRCLE;
		} else if (targetRotationAngle <= -Maths.DEGREES_IN_HALF_CIRCLE) {
			targetRotationAngle += Maths.DEGREES_IN_CIRCLE;
		}
	}

	private void calculateVerticalAngle() {
		if (targetElevation >= MAX_ANGLE_OF_ELEVATION) {
			targetElevation = MAX_ANGLE_OF_ELEVATION;
		} else if (targetElevation <= MIN_ANGLE_OF_ELEVATION) {
			targetElevation = MIN_ANGLE_OF_ELEVATION;
		}
	}

	private void calculateZoom() {
		if (targetZoom < MINIMUM_ZOOM) {
			targetZoom = MINIMUM_ZOOM;
		} else if (targetZoom > MAXIMUM_ZOOM) {
			targetZoom = MAXIMUM_ZOOM;
		}
	}

	private void updateActualZoom() {
	}

	private void updateHorizontalAngle() {
		float offset = targetRotationAngle - angleAroundPlayer;

		if (Math.abs(offset) > Maths.DEGREES_IN_HALF_CIRCLE) {
			if (offset < 0) {
				offset = targetRotationAngle + Maths.DEGREES_IN_CIRCLE - angleAroundPlayer;
			} else {
				offset = targetRotationAngle - Maths.DEGREES_IN_CIRCLE - angleAroundPlayer;
			}
		}

		angleAroundPlayer += offset * Framework.get().getDelta();

		if (angleAroundPlayer >= Maths.DEGREES_IN_HALF_CIRCLE) {
			angleAroundPlayer -= Maths.DEGREES_IN_CIRCLE;
		} else if (angleAroundPlayer <= -Maths.DEGREES_IN_HALF_CIRCLE) {
			angleAroundPlayer += Maths.DEGREES_IN_CIRCLE;
		}
	}

	private void updatePitchAngle() {
		float offset = targetElevation - angleOfElevation;

		if (Math.abs(offset) > Maths.DEGREES_IN_HALF_CIRCLE) {
			if (offset < 0) {
				offset = targetElevation + Maths.DEGREES_IN_CIRCLE - angleOfElevation;
			} else {
				offset = targetElevation - Maths.DEGREES_IN_CIRCLE - angleOfElevation;
			}
		}

		angleOfElevation += offset * Framework.get().getDelta();

		if (angleOfElevation >= Maths.DEGREES_IN_HALF_CIRCLE) {
			angleOfElevation -= Maths.DEGREES_IN_CIRCLE;
		} else if (angleOfElevation <= -Maths.DEGREES_IN_HALF_CIRCLE) {
			angleOfElevation += Maths.DEGREES_IN_CIRCLE;
		}
	}

	private void calculatePosition() {
		double theta = Math.toRadians(targetRotation.y + angleAroundPlayer);
		position.x = targetPosition.x - (float) (horizontalDistanceFromFocus * Math.sin(theta));
		position.y = targetPosition.y + verticalDistanceFromFocus + CAMERA_AIM_OFFSET;
		position.z = targetPosition.z - (float) (horizontalDistanceFromFocus * Math.cos(theta));

		rotation.x = angleOfElevation;
		rotation.y = angleAroundPlayer + targetRotation.y + Maths.DEGREES_IN_HALF_CIRCLE;
		rotation.z = 0.0f;
	}

	@Override
	public Frustum getViewFrustum() {
		viewFrustum.recalculateFrustum(getProjectionMatrix(), viewMatrix);
		return viewFrustum;
	}

	@Override
	public Ray getViewRay() {
		viewRay.recalculateRay(position);
		return viewRay;
	}

	@Override
	public Matrix4f getViewMatrix() {
		updateViewMatrix();
		return viewMatrix;
	}

	private void updateViewMatrix() {
		viewMatrix.setIdentity();
		position.negate();
		Matrix4f.rotate(viewMatrix, Matrix4f.REUSABLE_VECTOR.set(1.0f, 0.0f, 0.0f), (float) Math.toRadians(rotation.x), viewMatrix);
		Matrix4f.rotate(viewMatrix, Matrix4f.REUSABLE_VECTOR.set(0.0f, 1.0f, 0.0f), (float) Math.toRadians(-rotation.y), viewMatrix);
		Matrix4f.rotate(viewMatrix, Matrix4f.REUSABLE_VECTOR.set(0.0f, 0.0f, 1.0f), (float) Math.toRadians(rotation.z), viewMatrix);
		Matrix4f.translate(viewMatrix, position, viewMatrix);
		position.negate();
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		updateProjectionMatrix();
		return projectionMatrix;
	}

	private void updateProjectionMatrix() {
		Matrix4f.perspectiveMatrix(getFOV(), FlounderDisplay.get().getAspectRatio(), getNearPlane(), getFarPlane(), projectionMatrix);
	}

	@Override
	public void reflect(float waterHeight) {
		position.y -= 2.0f * (position.y - waterHeight);
		rotation.x = -rotation.x;
		updateViewMatrix();
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
	}

	@Override
	public boolean isActive() {
		return true;
	}

	private void calculateDistances() {
		horizontalDistanceFromFocus = (float) (actualDistanceFromPoint * Math.cos(Math.toRadians(angleOfElevation)));
		verticalDistanceFromFocus = (float) (actualDistanceFromPoint * Math.sin(Math.toRadians(angleOfElevation)));
	}
}
