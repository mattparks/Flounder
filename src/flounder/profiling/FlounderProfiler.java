package flounder.profiling;

import flounder.devices.*;
import flounder.engine.*;

import javax.swing.*;

/**
 * A JFrame that holds profiling tabs and values.
 */
public class FlounderProfiler extends IModule {
	private static final FlounderProfiler instance = new FlounderProfiler();

	private JFrame profilerJFrame;
	private FlounderTabMenu primaryTabMenu;
	private boolean profilerOpen;

	private FlounderProfiler() {
		super(FlounderDisplay.class);
	}

	@Override
	public void init() {
		String title = FlounderDisplay.getTitle() + " Profiler";
		profilerJFrame = new JFrame(title);
		profilerJFrame.setSize(420, 720);
		profilerJFrame.setResizable(true);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		profilerJFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		profilerJFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(profilerJFrame,
						"Are you sure to close this profiler?", "Really Closing?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					profilerOpen = false;
				}
			}
		});

		primaryTabMenu = new FlounderTabMenu();
		profilerJFrame.add(primaryTabMenu);

		profilerOpen = false;

		// Opens the profiler if not running from jar.
		//	toggle(!FlounderLogger.RUNNING_FROM_JAR); // TODO
	}

	@Override
	public void update() {
		if (profilerJFrame.isVisible() != profilerOpen) {
			profilerJFrame.setVisible(profilerOpen);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Profiler", "Is Open", profilerOpen);
	}

	/**
	 * Toggles the visibility of the JFrame.
	 *
	 * @param open If the JFrame should be open.
	 */
	public static void toggle(boolean open) {
		instance.profilerOpen = open;
	}

	/**
	 * Adds a value to a tab.
	 *
	 * @param tabName The tabs name to add to.
	 * @param title The title of the label.
	 * @param value The value to add with the title.
	 * @param <T> The type of value to add.
	 */
	public static <T> void add(String tabName, String title, T value) {
		addTab(tabName); // Forces the tab to be there.
		FlounderProfilerTab tab = instance.primaryTabMenu.getCategoryComponent(tabName).get();
		tab.addLabel(title, value); // Adds the label to the tab.
	}

	/**
	 * Adds a tab by name to the menu if it does not exist.
	 *
	 * @param tabName The tab name to add.
	 */
	public static void addTab(String tabName) {
		if (!instance.primaryTabMenu.doesCategoryExist(tabName)) {
			instance.primaryTabMenu.createCategory(tabName);
		}
	}

	/**
	 * Gets if the profiler is open.
	 *
	 * @return If the profiler is open.
	 */
	public static boolean isOpen() {
		return instance.profilerOpen;
	}

	@Override
	public void dispose() {
		primaryTabMenu.dispose();
		profilerJFrame.dispose();
	}
}
