package flounder.engine.profiling;

import javax.swing.*;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Evan Merlock on 4/4/2016.
 * Wraps a few methods around JTabbedPane for convenience
 */
public class FlounderTabMenu extends JTabbedPane {

    private HashMap<String, FlounderProfilerTab> components;

    public FlounderTabMenu() {
        super(SwingConstants.TOP, WRAP_TAB_LAYOUT);
        components = new HashMap<>();
    }

    public void createCategory(String categoryName) {
        JPanel primaryComponent = new JPanel();
        super.addTab(categoryName, primaryComponent);
	    FlounderProfilerTab contentTab = new FlounderProfilerTab(primaryComponent);
        components.put(categoryName, contentTab);
    }

    public Boolean doesCategoryExist(String categoryName) {
	    if (components.containsKey(categoryName)) {
		    return true;
	    } else {
		    return false;
	    }
    }

    public Optional<FlounderProfilerTab> getCategoryComponent(String categoryName) {
        if (components.containsKey(categoryName)) {
            return Optional.of(components.get(categoryName));
        } else {
            return Optional.empty();
        }
    }

}
