import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.renderer.*;
import flounder.space.*;
import flounder.visual.*;
import sun.reflect.generics.reflectiveObjects.*;

import static org.lwjgl.glfw.GLFW.*;

public class Testing1 {
	public static void main(String[] args) {
		Implementation implementation = new Implementation(new IGame() {
			private final float FRONT_SPEED = 40.0f;
			private final float UP_SPEED = 30.0f;
			private final float SIDE_SPEED = 40.0f;

			private Vector3f velocity;
			private Vector3f rotation;

			private IAxis inputForward;
			private IAxis inputUp;
			private IAxis inputSide;

			@Override
			public void init() {
				velocity = new Vector3f();
				rotation = new Vector3f();

				IButton leftKeyButtons = new KeyButton(GLFW_KEY_A, GLFW_KEY_LEFT);
				IButton rightKeyButtons = new KeyButton(GLFW_KEY_D, GLFW_KEY_RIGHT);
				IButton forwardsKeyButtons = new KeyButton(GLFW_KEY_W, GLFW_KEY_UP);
				IButton backwardsKeyButtons = new KeyButton(GLFW_KEY_S, GLFW_KEY_DOWN);
				IButton upKeyButtons = new KeyButton(GLFW_KEY_SPACE);
				IButton downKeyButtons = new KeyButton(GLFW_KEY_LEFT_SHIFT);
				inputForward = new CompoundAxis(new ButtonAxis(forwardsKeyButtons, backwardsKeyButtons));
				inputUp = new CompoundAxis(new ButtonAxis(downKeyButtons, upKeyButtons));
				inputSide = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons));
			}

			@Override
			public void update() {
				rotation.set(0.0f, FlounderEngine.getCamera().getYaw(), 0.0f);
				velocity.x = SIDE_SPEED * FlounderEngine.getDelta() * Maths.deadband(0.05f, inputSide.getAmount());
				velocity.y = UP_SPEED * FlounderEngine.getDelta() * Maths.deadband(0.05f, inputUp.getAmount());
				velocity.z = FRONT_SPEED * FlounderEngine.getDelta() * Maths.deadband(0.05f, inputForward.getAmount());
				Vector3f.rotate(velocity, rotation, velocity);
				Vector3f.add(focusPosition, velocity, focusPosition);
				update(focusPosition, focusRotation);
			}

			@Override
			public void dispose() {
			}
		}, new ICamera() {
			private Frustum viewFrustum;
			private Vector3f position;
			private Vector3f rotation;
			private Matrix4f viewMatrix;

			@Override
			public void init() {
				viewFrustum = new Frustum();
				position = new Vector3f();
				rotation = new Vector3f();
				viewMatrix = new Matrix4f();
			}

			@Override
			public float getNearPlane() {
				return 3200.0f;
			}

			@Override
			public float getFarPlane() {
				return 0.1f;
			}

			@Override
			public float getFOV() {
				return 70.0f;
			}

			@Override
			public void update(Vector3f focusPosition, Vector3f focusRotation, boolean gamePaused) {
				position.set(focusPosition);
				rotation.set(focusRotation);
				updateViewMatrix();

				FlounderEngine.getProfiler().add("Camera", "Position", position);
				FlounderEngine.getProfiler().add("Camera", "Rotation", rotation);
			}

			private void updateViewMatrix() {
				viewMatrix.setIdentity();
				position.negate();
				Matrix4f.rotate(viewMatrix, new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(rotation.x), viewMatrix);
				Matrix4f.rotate(viewMatrix, new Vector3f(0.0f, 1.0f, 0.0f), (float) Math.toRadians(-rotation.y), viewMatrix);
				Matrix4f.rotate(viewMatrix, new Vector3f(0.0f, 0.0f, 1.0f), (float) Math.toRadians(rotation.z), viewMatrix);
				Matrix4f.translate(viewMatrix, position, viewMatrix);
				position.negate();
				viewFrustum.recalculateFrustum(FlounderEngine.getProjectionMatrix(), getViewMatrix());
			}

			@Override
			public Matrix4f getViewMatrix() {
				return viewMatrix;
			}

			@Override
			public Frustum getViewFrustum() {
				return viewFrustum;
			}

			@Override
			public Matrix4f getReflectionViewMatrix(float planeHeight) {
				throw new NotImplementedException();
			}

			@Override
			public void reflect(float waterHeight) {
				throw new NotImplementedException();
			}

			@Override
			public Vector3f getPosition() {
				return position;
			}

			@Override
			public float getPitch() {
				return rotation.x;
			}

			@Override
			public float getYaw() {
				return rotation.y;
			}

			@Override
			public float getRoll() {
				return rotation.z;
			}

			@Override
			public void setRotation(float pitch, float yaw, float roll) {
				this.rotation.x = pitch;
				this.rotation.y = yaw;
				this.rotation.z = roll;
			}

			@Override
			public float getAimDistance() {
				throw new NotImplementedException();
			}
		}, new IRendererMaster() {
			private Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

			private FontRenderer fontRenderer;
			private GuiRenderer guiRenderer;
			private AABBRenderer aabbRenderer;

			private Matrix4f projectionMatrix;
			private Colour clearColour;

			private SinWaveDriver clearColourX;
			private SinWaveDriver clearColourY;

			@Override
			public void init() {
				fontRenderer = new FontRenderer();
				guiRenderer = new GuiRenderer(GuiRenderer.GuiRenderType.GUI);
				aabbRenderer = new AABBRenderer();

				projectionMatrix = new Matrix4f();
				clearColour = new Colour();

				clearColourX = new SinWaveDriver(0.0f, 1.0f, 30.0f);
				clearColourY = new SinWaveDriver(0.0f, 1.0f, 15.0f);
			}

			@Override
			public void render() {
				clearColour.set(clearColourX.update(FlounderEngine.getDelta()), clearColourY.update(FlounderEngine.getDelta()), 0.3f);
				OpenGlUtils.prepareNewRenderParse(clearColour);
				Matrix4f.perspectiveMatrix(FlounderEngine.getCamera().getFOV(), FlounderEngine.getDevices().getDisplay().getAspectRatio(), FlounderEngine.getCamera().getNearPlane(), FlounderEngine.getCamera().getFarPlane(), projectionMatrix);

				fontRenderer.render(POSITIVE_INFINITY, FlounderEngine.getCamera());
				guiRenderer.render(POSITIVE_INFINITY, FlounderEngine.getCamera());
				aabbRenderer.render(POSITIVE_INFINITY, FlounderEngine.getCamera());
			}

			@Override
			public Matrix4f getProjectionMatrix() {
				return projectionMatrix;
			}

			@Override
			public void dispose() {
				fontRenderer.dispose();
				guiRenderer.dispose();
				aabbRenderer.dispose();
			}
		}, new IManagerGUI() {
			@Override
			public void init() {

			}

			@Override
			public void update() {

			}

			public boolean isMenuIsOpen() {
				return false;
			}

			@Override
			public void openMenu() {

			}

			@Override
			public float getBlurFactor() {
				return 0;
			}
		}, -1);
		FlounderEngine engine = new FlounderEngine(implementation, 1080, 720, "Testing", false, true, 0, false);
		engine.startEngine(FlounderEngine.getFonts().bungee);
	}
}
