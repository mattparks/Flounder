package flounder.engine.profiling;

import javax.swing.*;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Evan Merlock on 4/4/2016.
 * Wraps a few methods around JTabbedPane for convenience
 */
public class FlounderTabMenu extends JTabbedPane {

    private HashMap<String, FlounderProfilerTab> Components;

    public FlounderTabMenu() {
        super(SwingConstants.TOP, WRAP_TAB_LAYOUT);
        Components = new HashMap<>();
    }

    public void createCategory(String categoryName) {
        JPanel primaryComponent = new JPanel();
        super.addTab(categoryName, primaryComponent);
	    FlounderProfilerTab contentTab = new FlounderProfilerTab(primaryComponent);
        Components.put(categoryName, contentTab);
    }

    public Optional<FlounderProfilerTab> getCategoryComponent(String categoryName) {
        if (Components.containsKey(categoryName)) {
            return Optional.of(Components.get(categoryName));
        } else {
            return Optional.empty();
        }
    }

}
