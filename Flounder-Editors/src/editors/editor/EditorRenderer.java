package editors.editor;

import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.fbos.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.particles.*;
import flounder.physics.bounding.*;
import flounder.post.piplines.*;
import flounder.renderer.*;
import flounder.shadows.*;
import flounder.skybox.*;

public class EditorRenderer extends RendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

	private ShadowRenderer shadowRenderer;
	private SkyboxRenderer skyboxRenderer;
	private EntitiesRenderer entitiesRenderer;
	private ParticleRenderer particleRenderer;
	private BoundingRenderer boundingRenderer;
	private GuisRenderer guisRenderer;
	private FontRenderer fontRenderer;

	private FBO rendererFBO;

	private PipelineMRT pipelineMRT;
	private PipelineBloom pipelineBloom;

	public EditorRenderer() {
		super(FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.shadowRenderer = new ShadowRenderer();
		this.skyboxRenderer = new SkyboxRenderer();
		this.entitiesRenderer = new EntitiesRenderer();
		this.particleRenderer = new ParticleRenderer();
		this.boundingRenderer = new BoundingRenderer();
		this.guisRenderer = new GuisRenderer();
		this.fontRenderer = new FontRenderer();

		this.rendererFBO = FBO.newFBO(1.0f).attachments(3).withAlphaChannel(true).depthBuffer(DepthBufferType.TEXTURE).create();

		this.pipelineMRT = new PipelineMRT();
		this.pipelineBloom = new PipelineBloom();
	}

	@Override
	public void render() {
		// Shadow rendering.
		renderShadows();

		// Binds the render FBO.
		rendererFBO.bindFrameBuffer();

		// Scene rendering.
		renderScene(POSITIVE_INFINITY);

		// Unbinds the render FBO.
		rendererFBO.unbindFrameBuffer();

		// Post rendering.
		if (FlounderGuis.get().getGuiMaster() != null) {
			renderPost(FlounderGuis.get().getGuiMaster().isGamePaused(), FlounderGuis.get().getGuiMaster().getBlurFactor());
		}
	}

	private void renderShadows() {
		// Renders the shadows.
		shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.get().getCamera());
	}

	private void renderScene(Vector4f clipPlane) {
		// Clears and renders.
		Camera camera = FlounderCamera.get().getCamera();
		FlounderOpenGL.get().prepareNewRenderParse(0.0f, 0.0f, 0.0f);

		skyboxRenderer.render(clipPlane, camera);
		entitiesRenderer.render(clipPlane, camera);

		boundingRenderer.render(clipPlane, camera);

		particleRenderer.render(clipPlane, camera);
	}

	private void renderPost(boolean isPaused, float blurFactor) {
		pipelineMRT.setShadowFactor(1.0f);
		pipelineMRT.renderPipeline(
				rendererFBO.getColourTexture(0), // Colours
				rendererFBO.getColourTexture(1), // Normals
				rendererFBO.getColourTexture(2), // Extras
				rendererFBO.getDepthTexture(), // Depth
				shadowRenderer.getShadowMap() // Shadow Map
		);
		FBO output = pipelineMRT.getOutput();

		// Render Bloom Filter.
		pipelineBloom.renderPipeline(output.getColourTexture(0));
		output = pipelineBloom.getOutput();

		// Scene independents.
		renderIndependents(output);

		// Displays the image to the screen.
		output.blitToScreen();
	}

	private void renderIndependents(FBO output) {
		output.bindFrameBuffer();
		guisRenderer.render(null, null);
		fontRenderer.render(null, null);
		output.unbindFrameBuffer();
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		shadowRenderer.dispose();
		skyboxRenderer.dispose();
		entitiesRenderer.dispose();
		particleRenderer.dispose();
		boundingRenderer.dispose();
		guisRenderer.dispose();
		fontRenderer.dispose();

		rendererFBO.delete();

		pipelineMRT.dispose();
		pipelineBloom.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
