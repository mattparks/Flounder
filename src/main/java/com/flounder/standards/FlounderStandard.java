package com.flounder.standards;

import com.flounder.framework.*;

import java.util.*;

/**
 * A module used for managing simple update injection standards.
 */
public class FlounderStandard extends com.flounder.framework.Module {
	private List<Standard> standards;

	/**
	 * Creates a new standards manager.
	 */
	public FlounderStandard() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		standards = new ArrayList<>();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		List<Extension> newStandards = getExtensionMatches(standards, Standard.class, true);
		cancelChange();

		if (newStandards != null) {
			List<Standard> newCasted = new ArrayList<>();
			newStandards.forEach(extension -> newCasted.add(((Standard) extension)));

			if (standards != null) {
				List<Standard> removedStandards = new ArrayList<>();
				removedStandards.addAll(standards);
				removedStandards.removeAll(newCasted);

				removedStandards.forEach(removed -> {
					removed.dispose();
					removed.setInitialized(false);
				});
			} else {
				standards = new ArrayList<>();
			}

			standards.clear();
			standards.addAll(newCasted);

			standards.forEach(standard -> {
				if (!standard.isInitialized()) {
					standard.init();
					standard.setInitialized(true);
				}
			});
		}

		if (standards != null && !standards.isEmpty()) {
			standards.forEach(Standard::update);
		}
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		if (standards != null && !standards.isEmpty()) {
			standards.forEach(standard -> {
				standard.dispose();
				standard.setInitialized(false);
			});
			standards.clear();
		}
	}

	@com.flounder.framework.Module.Instance
	public static FlounderStandard get() {
		return (FlounderStandard) Framework.get().getModule(FlounderStandard.class);
	}
}
