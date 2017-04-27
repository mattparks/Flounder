package flounder.entities;

import flounder.helpers.*;

import javax.swing.*;
import java.util.*;

public interface IComponentEditor {
	public static final List<Pair<String, JPanel>> ADD_SIDE_TAB = new ArrayList<>();
	public static final List<String> REMOVE_SIDE_TAB = new ArrayList<>();

	void addToPanel(JPanel panel);

	void editorUpdate();

	/**
	 * Gets the list of parameter datas to be saved into the Java source constructor for the component.
	 *
	 * @param entityName The name of the save type, can be used to save extra files under /entities/name/*.
	 *
	 * @return Returns static variables and parameter values saved with the component.
	 */
	Pair<String[], String[]> getSaveValues(String entityName);

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

	/**
	 * Gets the tab name for a editor.
	 *
	 * @param editor The editor to get a tab name for.
	 *
	 * @return The tabs name.
	 */
	public static String getTabName(IComponentEditor editor) {
		String[] path = editor.getClass().getName().split("\\.");
		return path[path.length - 1].replace("Component", "").trim();
	}
}
