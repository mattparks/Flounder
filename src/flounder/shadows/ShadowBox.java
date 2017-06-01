package flounder.shadows;

import flounder.camera.*;
import flounder.devices.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

/**
 * Represents the 3D area of the world in which engine.shadows will be cast (basically represents the orthographic projection area for the shadow render pass).
 * It can be updated each frame to optimise the area, making it as small as possible (to allow for optimal shadow map resolution) while not being too small to avoid objects not having shadows when they should.
 * This class also provides functionality to test whether an object is inside this shadow box. Everything inside the box will be rendered to the shadow map in the shadow render pass.
 */
public class ShadowBox {
	private static final Vector4f UP = new Vector4f(0.0f, 1.0f, 0.0f, 0.0f);
	private static final Vector4f FORWARD = new Vector4f(0.0f, 0.0f, -1.0f, 0.0f);

	private Matrix4f lightViewMatrix;

	private float minX, maxX;
	private float minY, maxY;
	private float minZ, maxZ;
	private float farHeight, farWidth, nearHeight, nearWidth;

	/**
	 * Creates a new shadow box and calculates some initial values relating to the camera's view frustum.
	 *
	 * @param lightViewMatrix Basically the "view matrix" of the light. Can be used to transform a point from world space into "light" space.
	 */
	protected ShadowBox(Matrix4f lightViewMatrix) {
		this.lightViewMatrix = lightViewMatrix;
	}

	/**
	 * Updates the bounds of the shadow box based on the light direction and the camera's view frustum.
	 * Will make sure that the box covers the smallest area possible while still ensuring that everything.
	 * Objects inside the camera's view (and in range) will be shadowed.
	 *
	 * @param camera The camera object to be used when calculating the shadow boxes size.
	 */
	protected void update(Camera camera) {
		if (camera == null) {
			return;
		}

		updateWidthsAndHeights(camera);

		Matrix4f rotation = calculateCameraRotationMatrix(camera);
		Vector3f forwardVector = new Vector3f(Matrix4f.transform(rotation, FORWARD, null));

		Vector3f toFar = new Vector3f(forwardVector);
		toFar.scale(FlounderShadows.get().getShadowBoxDistance());
		Vector3f toNear = new Vector3f(forwardVector);
		toNear.scale(camera.getNearPlane());
		Vector3f centreNear = Vector3f.add(toNear, camera.getPosition(), null);
		Vector3f centreFar = Vector3f.add(toFar, camera.getPosition(), null);

		Vector4f[] points = calculateFrustumVertices(rotation, forwardVector, centreNear, centreFar);

		boolean first = true;

		for (Vector4f point : points) {
			if (first) {
				minX = point.x;
				maxX = point.x;
				minY = point.y;
				maxY = point.y;
				minZ = point.z;
				maxZ = point.z;
				first = false;
				continue;
			}

			if (point.x > maxX) {
				maxX = point.x;
			} else if (point.x < minX) {
				minX = point.x;
			}

			if (point.y > maxY) {
				maxY = point.y;
			} else if (point.y < minY) {
				minY = point.y;
			}

			if (point.z > maxZ) {
				maxZ = point.z;
			} else if (point.z < minZ) {
				minZ = point.z;
			}
		}

		maxZ += FlounderShadows.get().getShadowBoxOffset();
	}

	/**
	 * Updates the widths and heights of the box panes.
	 *
	 * @param camera The camera object.
	 */
	private void updateWidthsAndHeights(Camera camera) {
		farWidth = (float) (FlounderShadows.get().getShadowBoxDistance() * Math.tan(Math.toRadians(camera.getFOV())));
		nearWidth = (float) (camera.getNearPlane() * Math.tan(Math.toRadians(camera.getFOV())));
		farHeight = farWidth / FlounderDisplay.get().getAspectRatio();
		nearHeight = nearWidth / FlounderDisplay.get().getAspectRatio();
	}

	/**
	 * Calculates the rotation of the camera represented as a matrix.
	 *
	 * @return The rotation of the camera represented as a matrix.
	 */
	private Matrix4f calculateCameraRotationMatrix(Camera camera) {
		Matrix4f rotation = new Matrix4f();
		Matrix4f.rotate(rotation, new Vector3f(0.0f, 1.0f, 0.0f), (float) Math.toRadians(camera.getRotation().y), rotation);
		Matrix4f.rotate(rotation, new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-camera.getRotation().x), rotation);
		return rotation;
	}

	/**
	 * Calculates the vertex of each corner of the view frustum in light space.
	 *
	 * @param rotation - camera's rotation.
	 * @param forwardVector - the direction that the camera is aiming, and thus the direction of the frustum.
	 * @param centreNear - the centre point of the frustum's near plane.
	 * @param centreFar - the centre point of the frustum's far plane.
	 *
	 * @return The vertices of the frustum in light space.
	 */
	private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector, Vector3f centreNear, Vector3f centreFar) {
		Vector3f upVector = new Vector3f(Matrix4f.transform(rotation, UP, null));
		Vector3f rightVector = Vector3f.cross(forwardVector, upVector, null);
		Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
		Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);
		Vector3f farTop = Vector3f.add(centreFar, new Vector3f(upVector.x * farHeight, upVector.y * farHeight, upVector.z * farHeight), null);
		Vector3f farBottom = Vector3f.add(centreFar, new Vector3f(downVector.x * farHeight, downVector.y * farHeight, downVector.z * farHeight), null);
		Vector3f nearTop = Vector3f.add(centreNear, new Vector3f(upVector.x * nearHeight, upVector.y * nearHeight, upVector.z * nearHeight), null);
		Vector3f nearBottom = Vector3f.add(centreNear, new Vector3f(downVector.x * nearHeight, downVector.y * nearHeight, downVector.z * nearHeight), null);

		Vector4f[] points = new Vector4f[8];
		points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
		points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
		points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
		points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
		points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
		points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
		points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
		points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);
		return points;
	}

	/**
	 * Calculates one of the corner vertices of the view frustum in world space and converts it to light space.
	 *
	 * @param startPoint The starting centre point on the view frustum.
	 * @param direction The direction of the corner from the start point.
	 * @param width The distance of the corner from the start point.
	 *
	 * @return The relevant corner vertex of the view frustum in light space.
	 */
	private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction, float width) {
		Vector3f point = Vector3f.add(startPoint, new Vector3f(direction.x * width, direction.y * width, direction.z * width), null);
		Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1.0f);
		Matrix4f.transform(lightViewMatrix, point4f, point4f);
		return point4f;
	}

	/**
	 * Test if a bounding sphere intersects the shadow box. Can be used to decide which engine.entities should be rendered in the shadow render pass.
	 *
	 * @param position The centre of the bounding sphere in world space.
	 * @param radius The radius of the bounding sphere.
	 *
	 * @return {@code true} if the sphere intersects the box.
	 */
	public boolean isInBox(Vector3f position, float radius) {
		Vector4f entityPos = Matrix4f.transform(lightViewMatrix, new Vector4f(position.getX(), position.getY(), position.getZ(), 1.0f), null);
		float closestX = Maths.clamp(entityPos.x, minX, maxX);
		float closestY = Maths.clamp(entityPos.y, minY, maxY);
		float closestZ = Maths.clamp(entityPos.z, minZ, maxZ);
		Vector3f closestPoint = new Vector3f(closestX, closestY, closestZ);
		Vector3f centre = new Vector3f(entityPos.x, entityPos.y, entityPos.z);
		float disSquared = Vector3f.subtract(centre, closestPoint, null).lengthSquared();
		return disSquared < radius * radius;
	}

	/**
	 * Gets the centre of the shadow box (orthographic projection area).
	 *
	 * @return The centre of the shadow box.
	 */
	protected Vector3f getCenter() {
		float x = (minX + maxX) / 2.0f;
		float y = (minY + maxY) / 2.0f;
		float z = (minZ + maxZ) / 2.0f;
		Vector4f centre = new Vector4f(x, y, z, 1.0f);
		Matrix4f invertedLight = new Matrix4f();
		Matrix4f.invert(lightViewMatrix, invertedLight);
		return new Vector3f(Matrix4f.transform(invertedLight, centre, null));
	}

	/**
	 * Gets the width of the shadow box (orthographic projection area).
	 *
	 * @return The width of the shadow box.
	 */
	protected float getWidth() {
		return maxX - minX;
	}

	/**
	 * Gets the height of the shadow box (orthographic projection area).
	 *
	 * @return The height of the shadow box.
	 */
	protected float getHeight() {
		return maxY - minY;
	}

	/**
	 * Gets the length of the shadow box (orthographic projection area).
	 *
	 * @return The length of the shadow box.
	 */
	protected float getLength() {
		return maxZ - minZ;
	}
}