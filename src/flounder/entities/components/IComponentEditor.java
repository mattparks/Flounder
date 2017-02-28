package flounder.entities.components;

import flounder.entities.template.*;
import flounder.helpers.*;

import javax.swing.*;
import java.util.*;

public interface IComponentEditor {
	public static final List<Pair<String, JPanel>> ADD_SIDE_TAB = new ArrayList<>();
	public static final List<String> REMOVE_SIDE_TAB = new ArrayList<>();

	void addToPanel(JPanel panel);

	void editorUpdate();

	/**
	 * Gets the list of values that are saved with the component.
	 *
	 * @param entityName The name of the save type, can be used to save extra files under /entities/name/*.
	 *
	 * @return Returns values saved with the component.
	 */
	String[] getSavableValues(String entityName);

	/**
	 * Creates a new text panel for the component.
	 *
	 * @return The new text panel.
	 */
	public static JPanel makeTextPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new WrapLayout());
		return panel;
	}

	public static String getTabName(IComponentEditor editor) {
		String[] path = editor.getClass().getName().split("\\.");
		return path[path.length - 1].replace("Component", "").trim();
	}
}
