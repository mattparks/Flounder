package flounder.profiling;

import javax.swing.*;
import java.util.*;

public class FlounderProfiler {
	private static JFrame profilerJFrame;
	private static FlounderTabMenu primaryTabMenu;

	private static boolean initialized;
	private static boolean profilerOpen;

	public static void init(String title) {
		if (!initialized) {
			profilerJFrame = new JFrame(title);
			profilerJFrame.setSize(420, 720);
			profilerJFrame.setResizable(true);

			primaryTabMenu = new FlounderTabMenu();
			profilerJFrame.add(primaryTabMenu);

			profilerOpen = false;
			initialized = true;
		}
	}

	public static void update() {
	}

	public static void toggle(boolean open) {
		if (open) {
			profilerJFrame.setVisible(true);
			profilerOpen = true;
		} else {
			profilerJFrame.setVisible(false);
			profilerOpen = false;
		}
	}

	public static <T> void add(String tabName, String title, T value) {
		if (primaryTabMenu.doesCategoryExist(tabName)) {
			Optional<FlounderProfilerTab> optionalTab = primaryTabMenu.getCategoryComponent(tabName);
			optionalTab.ifPresent(insertObject -> {
				FlounderProfilerTab grabbedTab = optionalTab.get();
				grabbedTab.addLabel(title, value);
			});
		} else {
			primaryTabMenu.createCategory(tabName);
			FlounderProfiler.add(tabName, title, value);
		}
	}

	public static void addTab(String tabName) {
		if (primaryTabMenu.doesCategoryExist(tabName)) {
			// NOP
		} else {
			primaryTabMenu.createCategory(tabName);
		}
	}

	public static boolean isOpen() {
		return profilerOpen;
	}

	public static void dispose() {
		if (initialized) {
			profilerJFrame.dispose();
			initialized = false;
		}
	}
}
