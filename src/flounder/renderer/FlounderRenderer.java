package flounder.renderer;

import flounder.devices.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.profiling.*;

import java.util.*;

public class FlounderRenderer extends IModule {
	private static final FlounderRenderer instance = new FlounderRenderer();

	private IRendererMaster rendererMaster;

	public FlounderRenderer() {
		super(ModuleUpdate.RENDER, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class);
		this.rendererMaster = null;
	}

	@Override
	public void init() {
		if (rendererMaster != null) {
			rendererMaster.init();
			((IExtension) rendererMaster).setInitialized(true);
		}
	}

	@Override
	public void run() {
		List<IExtension> rendererExtensions = null;

		for (IExtension extension : FlounderFramework.getExtensions()) {
			if (extension instanceof IRendererMaster) {
				rendererExtensions = new ArrayList<>();
				rendererExtensions.add(extension);
			}
		}

		if (rendererExtensions != null && !rendererExtensions.isEmpty()) {
			for (IExtension extension : rendererExtensions) {
				IRendererMaster newRenderer = (IRendererMaster) extension;

				if (newRenderer.isActive() && !newRenderer.equals(rendererMaster)) {
					if (rendererMaster != null) {
						rendererMaster.dispose();
						((IExtension) rendererMaster).setInitialized(false);
					}

					rendererMaster = newRenderer;

					if (!extension.isInitialized()) {
						rendererMaster.init();
						((IExtension) rendererMaster).setInitialized(true);
					}

					break;
				}
			}
		}

		if (rendererMaster != null) {
			rendererMaster.render();
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Renderer", "Selected", rendererMaster == null ? "NULL" : rendererMaster.getClass());
	}

	public static IRendererMaster getRendererMaster() {
		return instance.rendererMaster;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		if (rendererMaster != null) {
			rendererMaster.dispose();
			((IExtension) rendererMaster).setInitialized(false);
		}
	}
}
