package flounder.engine.profiling;

import javax.swing.*;
import java.util.List;

/**
 * Created by Evan on 4/4/2016.
 */
public class FlounderTabMenu extends JTabbedPane {

    private List<JPanel> Components;

    public FlounderTabMenu() {
        super(SwingConstants.TOP, WRAP_TAB_LAYOUT);
    }

    private JPanel createTab(String tabName) {
        JPanel primaryComponent = new JPanel();
        super.addTab(tabName, primaryComponent);
        return primaryComponent;
    }

}
