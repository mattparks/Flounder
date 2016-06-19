package flounder.profiling;

import flounder.engine.*;

import javax.swing.*;

/**
 * A JFrame that holds profiling tabs and values.
 */
public class FlounderProfiler implements IModule {
	private JFrame profilerJFrame;
	private FlounderTabMenu primaryTabMenu;

	private boolean profilerOpen;

	private String title;

	/**
	 * Creates the engines profiler.
	 *
	 * @param title The title to be created with.
	 */
	public FlounderProfiler(String title) {
		this.title = title;
	}

	@Override
	public void init() {
		profilerJFrame = new JFrame(title);
		profilerJFrame.setSize(420, 720);
		profilerJFrame.setResizable(true);

		primaryTabMenu = new FlounderTabMenu();
		profilerJFrame.add(primaryTabMenu);

		profilerOpen = false;
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
	}

	/**
	 * Toggles the visibility of the JFrame.
	 *
	 * @param open If the JFrame should be open.
	 */
	public void toggle(boolean open) {
		if (open) {
			profilerJFrame.setVisible(true);
			profilerOpen = true;
		} else {
			profilerJFrame.setVisible(false);
			profilerOpen = false;
		}
	}

	/**
	 * Adds a value to a tab.
	 *
	 * @param tabName The tabs name to add to.
	 * @param title The title of the label.
	 * @param value The value to add with the title.
	 * @param <T> The type of value to add.
	 */
	public <T> void add(String tabName, String title, T value) {
		addTab(tabName); // Forces the tab to be there.
		FlounderProfilerTab tab = primaryTabMenu.getCategoryComponent(tabName).get();
		tab.addLabel(title, value); // Adds the label to the tab.
	}

	/**
	 * Adds a tab by name to the menu if it does not exist.
	 *
	 * @param tabName The tab name to add.
	 */
	public void addTab(String tabName) {
		if (!primaryTabMenu.doesCategoryExist(tabName)) {
			primaryTabMenu.createCategory(tabName);
		}
	}

	/**
	 * Gets if the profiler is open.
	 *
	 * @return If the profiler is open.
	 */
	public boolean isOpen() {
		return profilerOpen;
	}

	@Override
	public void dispose() {
		primaryTabMenu.dispose();
		profilerJFrame.dispose();
	}
}
