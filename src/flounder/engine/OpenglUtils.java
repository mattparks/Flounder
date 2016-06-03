package flounder.engine;

import flounder.devices.*;
import flounder.maths.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Runs basic OpenGL rendering functions.
 */
public class OpenglUtils {
	private static boolean cullingBackFace = false;
	private static boolean inWireframe = false;
	private static boolean isAlphaBlending = false;
	private static boolean additiveBlending = false;
	private static boolean antialiasing = false;

	public static void prepareNewRenderParse(Colour colour) {
		prepareNewRenderParse(colour.getR(), colour.getG(), colour.getB());
	}

	public static void prepareNewRenderParse(float r, float g, float b) {
		glClearColor(r, g, b, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		disableBlending();
		cullBackFaces(true);
		enableDepthTesting();
	}

	public static void disableBlending() {
		if (isAlphaBlending || additiveBlending) {
			glDisable(GL_BLEND);
			isAlphaBlending = false;
			additiveBlending = false;
		}
	}

	public static void cullBackFaces(boolean cull) {
		if (cull && !cullingBackFace) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			cullingBackFace = true;
		} else if (!cull && cullingBackFace) {
			glDisable(GL_CULL_FACE);
			cullingBackFace = false;
		}
	}

	public static void enableDepthTesting() {
		glEnable(GL_DEPTH_TEST);
	}

	public static boolean isInWireframe() {
		return inWireframe;
	}

	public static void goWireframe(boolean goWireframe) {
		if (goWireframe && !inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			inWireframe = true;
		} else if (!goWireframe && inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			inWireframe = false;
		}
	}

	public static void enableAlphaBlending() {
		if (!isAlphaBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			isAlphaBlending = true;
			additiveBlending = false;
		}
	}

	public static void enableAdditiveBlending() {
		if (!additiveBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			additiveBlending = true;
			isAlphaBlending = false;
		}
	}

	public static void antialias(boolean enable) {
		if (!FlounderDevices.getDisplay().isAntialiasing()) {
			return;
		}

		if (enable && !antialiasing) {
			glEnable(GL_MULTISAMPLE);
			antialiasing = true;
		} else if (!enable && antialiasing) {
			glDisable(GL_MULTISAMPLE);
			antialiasing = false;
		}
	}

	public static void disableDepthTesting() {
		glDisable(GL_DEPTH_TEST);
	}

	public static void bindVAO(int vaoID, int... attributes) {
		glBindVertexArray(vaoID);

		for (int i : attributes) {
			glEnableVertexAttribArray(i);
		}
	}

	public static void unbindVAO(int... attributes) {
		for (int i : attributes) {
			glDisableVertexAttribArray(i);
		}

		glBindVertexArray(0);
	}

	public static void bindTextureToBank(int textureID, int bankID) {
		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	public static void bindTextureToBank(int textureID, int bankID, int lodBias) {
		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, lodBias);
		glActiveTexture(0);
	}
}
