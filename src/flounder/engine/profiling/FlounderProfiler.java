package flounder.engine.profiling;

import flounder.engine.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class FlounderProfiler {
	private static boolean initialized;
	private static JFrame frame;
	private static boolean open;
	private static List<ProfileTab> tabList;

	public static void init(final String title) {
		if (!initialized) {
			frame = new JFrame(title);
			frame.setSize(420, 720);
			frame.setResizable(false);
			frame.setLayout(new FlowLayout());
			tabList = new ArrayList<>();
			toggle(true);

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					toggle(false);
				}
			});
			initialized = true;
		}
	}

	public static void toggle(final boolean open) {
		frame.setVisible(FlounderProfiler.open = open);
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static void addTab(final ProfileTab tab) {
		tabList.add(tab);

		Logger.error(tab.getTabName());
	}

	public static void update() {
		for (ProfileTab tab : tabList) {
			for (ProfileLabel label : tab.getLabels()) {
				frame.remove(label.getJLabel());
				frame.add(label.getJLabel());
			}
		}

		//	frame.pack();
	}

	public static boolean isOpen() {
		return open;
	}

	public static void dispose() {
		if (initialized) {
			frame.dispose();
			initialized = false;
		}
	}
}
