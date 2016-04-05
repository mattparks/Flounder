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
        Components = new HashMap<>();
    }

    public void createCategory(String categoryName) {
        JPanel primaryComponent = new JPanel();
        System.out.println(primaryComponent);
        super.addTab(categoryName, primaryComponent);
        System.out.println(primaryComponent);
        Components.put(categoryName, primaryComponent);
    }

    public Optional<JPanel> getCategoryComponent(String categoryName) {
        if (Components.containsKey(categoryName)) {
            return Optional.of(Components.get(categoryName));
        } else {
            return Optional.empty();
        }
    }



}
