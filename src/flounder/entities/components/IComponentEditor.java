package flounder.entities.components;

import flounder.helpers.*;

import javax.swing.*;
import java.util.*;

public interface IComponentEditor {
	void addToPanel(JPanel panel);

	void editorUpdate();

	/**
	 * Gets the list of parameter datas to be saved into the Java source constructor for the component.
	 *
	 * @param entityName The name of the save type, can be used to save extra files under /entities/name/*.
	 *
	 * @return Returns parameter values saved with the component.
	 */
	String[] getSaveParameters(String entityName);

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
