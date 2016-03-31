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
	private static boolean m_cullingBackFace = false;
	private static boolean m_inWireframe = false;
	private static boolean m_isAlphaBlending = false;
	private static boolean m_additiveBlending = false;
	private static boolean m_antialiasing = false;

	public static void prepareNewRenderParse(final Colour colour) {
		prepareNewRenderParse(colour.getR(), colour.getG(), colour.getB());
	}

	public static void prepareNewRenderParse(final float r, final float g, final float b) {
		glClearColor(r, g, b, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		disableBlending();
		cullBackFaces(true);
		enableDepthTesting();
	}

	public static void disableBlending() {
		if (m_isAlphaBlending || m_additiveBlending) {
			glDisable(GL_BLEND);
			m_isAlphaBlending = false;
			m_additiveBlending = false;
		}
	}

	public static void cullBackFaces(final boolean cull) {
		if (cull && !m_cullingBackFace) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			m_cullingBackFace = true;
		} else if (!cull && m_cullingBackFace) {
			glDisable(GL_CULL_FACE);
			m_cullingBackFace = false;
		}
	}

	public static void enableDepthTesting() {
		glEnable(GL_DEPTH_TEST);
	}

	public static boolean isInWireframe() {
		return m_inWireframe;
	}

	public static void goWireframe(final boolean goWireframe) {
		if (goWireframe && !m_inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			m_inWireframe = true;
		} else if (!goWireframe && m_inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			m_inWireframe = false;
		}
	}

	public static void enableAlphaBlending() {
		if (!m_isAlphaBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			m_isAlphaBlending = true;
			m_additiveBlending = false;
		}
	}

	public static void enableAdditiveBlending() {
		if (!m_additiveBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			m_additiveBlending = true;
			m_isAlphaBlending = false;
		}
	}

	public static void antialias(final boolean enable) {
		if (!ManagerDevices.getDisplay().isAntialiasing()) {
			return;
		}

		if (enable && !m_antialiasing) {
			glEnable(GL_MULTISAMPLE);
			m_antialiasing = true;
		} else if (!enable && m_antialiasing) {
			glDisable(GL_MULTISAMPLE);
			m_antialiasing = false;
		}
	}

	public static void disableDepthTesting() {
		glDisable(GL_DEPTH_TEST);
	}

	public static void bindVAO(final int vaoID, final int... attributes) {
		glBindVertexArray(vaoID);

		for (int i : attributes) {
			glEnableVertexAttribArray(i);
		}
	}

	public static void unbindVAO(final int... attributes) {
		for (int i : attributes) {
			glDisableVertexAttribArray(i);
		}

		glBindVertexArray(0);
	}

	public static void bindTextureToBank(final int textureID, final int bankID) {
		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	public static void bindTextureToBank(final int textureID, final int bankID, final int lodBias) {
		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, lodBias);
		glActiveTexture(0);
	}
}
