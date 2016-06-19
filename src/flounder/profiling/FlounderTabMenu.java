package flounder.profiling;

import javax.swing.*;
import java.util.*;

/**
 * Wraps a few methods around JTabbedPane for convenience
 */
public class FlounderTabMenu extends JTabbedPane {
	private HashMap<String, FlounderProfilerTab> components;

	protected FlounderTabMenu() {
		super(SwingConstants.TOP, WRAP_TAB_LAYOUT);
		this.components = new HashMap<>();
	}

	protected void createCategory(String categoryName) {
		JPanel primaryComponent = new JPanel();
		super.addTab(categoryName, primaryComponent);
		FlounderProfilerTab contentTab = new FlounderProfilerTab(primaryComponent);
		components.put(categoryName, contentTab);
	}

	protected Boolean doesCategoryExist(String categoryName) {
		return components.containsKey(categoryName);
	}

	protected Optional<FlounderProfilerTab> getCategoryComponent(String categoryName) {
		if (components.containsKey(categoryName)) {
			return Optional.of(components.get(categoryName));
		} else {
			return Optional.empty();
		}
	}

	protected void dispose() {
		Iterator disposalIterator = components.entrySet().iterator();

		while (disposalIterator.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) disposalIterator.next();
			FlounderProfilerTab disposeObject = (FlounderProfilerTab) pair.getValue();
			disposeObject.dispose();
		}
	}
}
