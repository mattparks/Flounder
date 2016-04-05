package flounder.engine.profiling;

import javax.swing.*;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Evan Merlock on 4/4/2016.
 * Wraps a few methods around JTabbedPane for convenience
 */
public class FlounderTabMenu extends JTabbedPane {

    private HashMap<String, JPanel> Components;

    public FlounderTabMenu() {
        super(SwingConstants.TOP, WRAP_TAB_LAYOUT);
    }

    private JPanel createTab(String tabName) {
        JPanel primaryComponent = new JPanel();
        super.addTab(tabName, primaryComponent);
        return primaryComponent;
    }

    public void createCategory(String categoryName) {
        JPanel swingFrame = this.createTab(categoryName);
        Components.put(categoryName, swingFrame);
    }

    public Optional<JPanel> getCategoryComponent(String categoryName) {
        if (Components.containsKey(categoryName)) {
            return Optional.of(Components.get(categoryName));
        } else {
            return Optional.empty();
        }
    }



}
