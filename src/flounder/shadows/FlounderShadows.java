package flounder.shadows;

import flounder.camera.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

public class FlounderShadows extends Module {
	private Vector3f lightPosition;
	private float brightnessBoost;

	private int shadowSize;
	private int shadowPCF;
	private float shadowBias;
	private float shadowDarkness;
	private float shadowTransition;

	private float shadowBoxOffset;
	private float shadowBoxDistance;

	private Matrix4f projectionMatrix;
	private Matrix4f lightViewMatrix;
	private Matrix4f projectionViewMatrix;
	private Matrix4f shadowMapSpaceMatrix;
	private Matrix4f offset;

	private ShadowBox shadowBox;

	private boolean renderUnlimited;
	private Timer timerRender;
	private boolean renderNow;

	public FlounderShadows() {
		super(FlounderCamera.class, FlounderEntities.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.lightPosition = new Vector3f(0.5f, 0.0f, 0.5f);
		this.brightnessBoost = 0.1f;

		this.shadowSize = 8192;
		this.shadowPCF = 0;
		this.shadowBias = 0.001f;
		this.shadowDarkness = 0.6f;
		this.shadowTransition = 11.0f; // TODO: This is a strange setting, but works.

		this.shadowBoxOffset = 25.0f;
		this.shadowBoxDistance = 40.0f;

		this.projectionMatrix = new Matrix4f();
		this.lightViewMatrix = new Matrix4f();
		this.projectionViewMatrix = new Matrix4f();
		this.shadowMapSpaceMatrix = new Matrix4f();
		this.offset = createOffset();

		this.shadowBox = new ShadowBox(lightViewMatrix);

		this.renderUnlimited = true;
		this.timerRender = new Timer(1.0 / 24.0);
		this.renderNow = true;
	}

	/**
	 * Creates the orthographic projection matrix.
	 *
	 * @param width Shadow box width.
	 * @param height Shadow box height.
	 * @param length Shadow box length.
	 */
	private void updateOrthographicProjectionMatrix(float width, float height, float length) {
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = 2.0f / width;
		projectionMatrix.m11 = 2.0f / height;
		projectionMatrix.m22 = -2.0f / length;
		projectionMatrix.m33 = 1.0f;
	}

	/**
	 * Updates the "view" matrix of the light. The light itself has no position, so the "view" matrix is centered at the center of the shadow box.
	 *
	 * @param direction The light direct.
	 * @param position The center of the shadow box.
	 */
	private void updateLightViewMatrix(Vector3f direction, Vector3f position) {
		direction.normalize();
		position.negate();

		lightViewMatrix.setIdentity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		Matrix4f.rotate(lightViewMatrix, new Vector3f(1.0f, 0.0f, 0.0f), pitch, lightViewMatrix);
		float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
		yaw = direction.z > 0.0f ? yaw - 180.0f : yaw;
		Matrix4f.rotate(lightViewMatrix, new Vector3f(0.0f, 1.0f, 0.0f), (float) -Math.toRadians(yaw), lightViewMatrix);
		Matrix4f.translate(lightViewMatrix, position, lightViewMatrix);
	}

	/**
	 * Create the offset for part of the conversion to shadow map space.
	 *
	 * @return The offset as a matrix.
	 */
	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		Matrix4f.translate(offset, new Vector3f(0.5f, 0.5f, 0.5f), offset);
		Matrix4f.scale(offset, new Vector3f(0.5f, 0.5f, 0.5f), offset);
		return offset;
	}

	@Handler.Function(Handler.FLAG_RENDER)
	public void update() {
		// Renders when needed.
		if (timerRender.isPassedTime() || renderUnlimited) {
			// Resets the timer.
			timerRender.resetStartTime();

			shadowBox.update(FlounderCamera.get().getCamera());
			updateOrthographicProjectionMatrix(shadowBox.getWidth(), shadowBox.getHeight(), shadowBox.getLength());
			updateLightViewMatrix(lightPosition, shadowBox.getCenter());
			Matrix4f.multiply(projectionMatrix, lightViewMatrix, projectionViewMatrix);
			Matrix4f.multiply(offset, projectionViewMatrix, shadowMapSpaceMatrix);

			renderNow = true;
		}
	}

	public Vector3f getLightPosition() {
		return this.lightPosition;
	}

	public void setLightPosition(Vector3f lightPosition) {
		this.lightPosition.set(lightPosition);
	}

	public float getBrightnessBoost() {
		return this.brightnessBoost;
	}

	public void setBrightnessBoost(float brightnessBoost) {
		this.brightnessBoost = brightnessBoost;
	}

	public int getShadowSize() {
		return this.shadowSize;
	}

	public void setShadowSize(int shadowSize) {
		this.shadowSize = shadowSize;
	}

	public int getShadowPCF() {
		return this.shadowPCF;
	}

	public void setShadowPCF(int shadowPCF) {
		this.shadowPCF = shadowPCF;
	}

	public float getShadowBias() {
		return this.shadowBias;
	}

	public void setShadowBias(float shadowBias) {
		this.shadowBias = shadowBias;
	}

	public float getShadowDarkness() {
		return this.shadowDarkness;
	}

	public void setShadowDarkness(float shadowDarkness) {
		this.shadowDarkness = shadowDarkness;
	}

	public float getShadowTransition() {
		return this.shadowTransition;
	}

	public void setShadowTransition(float shadowTransition) {
		this.shadowTransition = shadowTransition;
	}

	public float getShadowBoxOffset() {
		return this.shadowBoxOffset;
	}

	public void setShadowBoxOffset(float shadowBoxOffset) {
		this.shadowBoxOffset = shadowBoxOffset;
	}

	public float getShadowBoxDistance() {
		return this.shadowBoxDistance;
	}

	public void setShadowBoxDistance(float shadowBoxDistance) {
		this.shadowBoxDistance = shadowBoxDistance;
	}

	/**
	 * @return The shadow box, so that it can be used by other class to test if engine.entities are inside the box.
	 */
	public ShadowBox getShadowBox() {
		return this.shadowBox;
	}

	public Matrix4f getProjectionViewMatrix() {
		return this.projectionViewMatrix;
	}

	/**
	 * This biased projection-view matrix is used to convert fragments into "shadow map space" when rendering the main render pass.
	 *
	 * @return The to-shadow-map-space matrix.
	 */
	public Matrix4f getToShadowMapSpaceMatrix() {
		return this.shadowMapSpaceMatrix;
	}

	/**
	 * @return The light's "view" matrix.
	 */
	protected Matrix4f getLightSpaceTransform() {
		return this.lightViewMatrix;
	}

	public boolean isRenderUnlimited() {
		return renderUnlimited;
	}

	public void setRenderUnlimited(boolean renderUnlimited) {
		this.renderUnlimited = renderUnlimited;
	}

	protected boolean renderNow() {
		if (renderNow) {
			renderNow = false;
			return true;
		} else {
			renderNow = false;
			return false;
		}
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderShadows get() {
		return (FlounderShadows) Framework.getInstance(FlounderShadows.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Shadows";
	}
}
