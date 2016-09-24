package flounder.particles;

import flounder.engine.*;
import flounder.engine.entrance.*;
import flounder.helpers.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.particles.loading.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.space.*;
import org.lwjgl.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.ARBDrawInstanced.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ParticleRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "particles", "particleVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "particles", "particleFragment.glsl");

	private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	private static final int MAX_INSTANCES = 27500;
	private static final int INSTANCE_DATA_LENGTH = 22;

	private static final int VAO = FlounderEngine.getLoader().createInterleavedVAO(VERTICES, 2);
	private static final FloatBuffer BUFFER = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
	private static final int VBO = FlounderEngine.getLoader().createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);

	private Shader shader;
	private int pointer;
	private int rendered;

	public ParticleRenderer() {
		shader = Shader.newShader("particles").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).createInSecondThread();

		pointer = 0;
		rendered = 0;

		FlounderEngine.getLoader().addInstancedAttribute(VAO, VBO, 1, 4, INSTANCE_DATA_LENGTH, 0);
		FlounderEngine.getLoader().addInstancedAttribute(VAO, VBO, 2, 4, INSTANCE_DATA_LENGTH, 4);
		FlounderEngine.getLoader().addInstancedAttribute(VAO, VBO, 3, 4, INSTANCE_DATA_LENGTH, 8);
		FlounderEngine.getLoader().addInstancedAttribute(VAO, VBO, 4, 4, INSTANCE_DATA_LENGTH, 12);
		FlounderEngine.getLoader().addInstancedAttribute(VAO, VBO, 5, 4, INSTANCE_DATA_LENGTH, 16);
		FlounderEngine.getLoader().addInstancedAttribute(VAO, VBO, 6, 1, INSTANCE_DATA_LENGTH, 20);
		FlounderEngine.getLoader().addInstancedAttribute(VAO, VBO, 7, 1, INSTANCE_DATA_LENGTH, 21);
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!shader.isLoaded() || FlounderEngine.getParticles().getParticles().size() < 1) {
			return;
		}

		prepareRendering(clipPlane, camera);

		for (StructureBasic<Particle> list : FlounderEngine.getParticles().getParticles()) {
			List<Particle> particles = list.queryInFrustum(new ArrayList<>(), camera.getViewFrustum());

			if (particles.size() > 0) {
				// Added to particles first -> last, so no initial reverse needed.
				ArraySorting.heapSort(particles); // Sorts the list big to small.
				Collections.reverse(particles); // Reverse as the sorted list should be close(small) -> far(big).

				// Creates the data to be used when rendering.
				float[] vboData = new float[Math.min(particles.size(), MAX_INSTANCES) * INSTANCE_DATA_LENGTH];
				boolean textureBound = false;
				pointer = 0;

				// Prepares each particle instance, and add them to the list.
				for (Particle particle : particles) {
					if (!textureBound) {
						prepareTexturedModel(particle.getParticleTemplate());
						textureBound = true;
					}

					prepareInstance(particle, camera, vboData);
				}

				// Renders the particles list.
				FlounderEngine.getLoader().updateVBO(VBO, vboData, BUFFER);
				glDrawArraysInstancedARB(GL_TRIANGLE_STRIP, 0, VERTICES.length, particles.size());
				unbindTexturedModel();
			}
		}

		endRendering();
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Particles", "Render Time", super.getRenderTimeMs());
	}

	private void prepareRendering(Vector4f clipPlane, ICamera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderEngine.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		rendered = 0;
	}

	private void prepareTexturedModel(ParticleTemplate particleTemplate) {
		unbindTexturedModel();

		OpenGlUtils.bindVAO(VAO, 0, 1, 2, 3, 4, 5, 6, 7);
		OpenGlUtils.antialias(FlounderEngine.getDevices().getDisplay().isAntialiasing());
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.enableAlphaBlending();
		glDepthMask(false); // Stops particles from being rendered to the depth BUFFER.

		if (particleTemplate.getTexture() != null) {
			shader.getUniformFloat("numberOfRows").loadFloat(particleTemplate.getTexture().getNumberOfRows());
			OpenGlUtils.bindTextureToBank(particleTemplate.getTexture().getTextureID(), 0);
		}
	}

	private void unbindTexturedModel() {
		glDepthMask(true);
		OpenGlUtils.disableBlending();
		OpenGlUtils.unbindVAO(0, 1, 2, 3, 4, 5, 6, 7);
	}

	private void prepareInstance(Particle particle, ICamera camera, float[] vboData) {
		if (rendered >= MAX_INSTANCES) {
			FlounderEngine.getLogger().error("Particles overflow: " + rendered);
			return;
		}

		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(modelMatrix, particle.getPosition(), modelMatrix);
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate(modelMatrix, new Vector3f(0.0f, 0.0f, 1.0f), (float) Math.toRadians(particle.getRotation()), modelMatrix);
		Matrix4f.scale(modelMatrix, new Vector3f(particle.getScale(), particle.getScale(), particle.getScale()), modelMatrix);

		vboData[pointer++] = modelMatrix.m00;
		vboData[pointer++] = modelMatrix.m01;
		vboData[pointer++] = modelMatrix.m02;
		vboData[pointer++] = modelMatrix.m03;
		vboData[pointer++] = modelMatrix.m10;
		vboData[pointer++] = modelMatrix.m11;
		vboData[pointer++] = modelMatrix.m12;
		vboData[pointer++] = modelMatrix.m13;
		vboData[pointer++] = modelMatrix.m20;
		vboData[pointer++] = modelMatrix.m21;
		vboData[pointer++] = modelMatrix.m22;
		vboData[pointer++] = modelMatrix.m23;
		vboData[pointer++] = modelMatrix.m30;
		vboData[pointer++] = modelMatrix.m31;
		vboData[pointer++] = modelMatrix.m32;
		vboData[pointer++] = modelMatrix.m33;
		vboData[pointer++] = particle.getTextureOffset1().x;
		vboData[pointer++] = particle.getTextureOffset1().y;
		vboData[pointer++] = particle.getTextureOffset2().x;
		vboData[pointer++] = particle.getTextureOffset2().y;
		vboData[pointer++] = particle.getTextureBlendFactor();
		vboData[pointer++] = particle.getTransparency();

		rendered++;
	}

	private void endRendering() {
		unbindTexturedModel();
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
