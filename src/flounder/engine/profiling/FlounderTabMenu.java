package flounder.engine.profiling;

import javax.swing.*;
import java.util.*;

/**
 * Wraps a few methods around JTabbedPane for convenience
 */
public class FlounderTabMenu extends JTabbedPane {
	private final HashMap<String, FlounderProfilerTab> components;

	public FlounderTabMenu() {
		super(SwingConstants.TOP, WRAP_TAB_LAYOUT);
		this.components = new HashMap<>();
	}

	public void createCategory(final String categoryName) {
		final JPanel primaryComponent = new JPanel();
		super.addTab(categoryName, primaryComponent);
		final FlounderProfilerTab contentTab = new FlounderProfilerTab(primaryComponent);
		components.put(categoryName, contentTab);
	}

	public Boolean doesCategoryExist(final String categoryName) {
		return components.containsKey(categoryName);
	}

	public Optional<FlounderProfilerTab> getCategoryComponent(final String categoryName) {
		if (components.containsKey(categoryName)) {
			return Optional.of(components.get(categoryName));
		} else {
			return Optional.empty();
		}
	}

	public void dispose() {
		final Iterator disposalIterator = components.entrySet().iterator();

		while (disposalIterator.hasNext()) {
			final HashMap.Entry pair = (HashMap.Entry) disposalIterator.next();
			final FlounderProfilerTab disposeObject = (FlounderProfilerTab) pair.getValue();
			disposeObject.dispose();
		}
	}
}
