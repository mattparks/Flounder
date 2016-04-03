package flounder.engine.profiling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class FlounderProfiler {
	private static boolean initialized;

	private static JFrame frame;
	private static boolean open;

	private static JPanel itemsPanel;

	private static List<ProfileTab> tabList;
	private static JMenuBar menuBar;
	private static JMenu itemsMenu;
	private static String selectedTab;

	public static void init(final String title) {
		if (!initialized) {
			frame = new JFrame(title);
			frame.setSize(420, 720);
			frame.setResizable(false);
			frame.setLayout(new BorderLayout());
			toggle(true);

			tabList = new ArrayList<>();
			itemsPanel = new JPanel();
			// itemsPanel.setLayout(new FlowLayout());
			frame.add(itemsPanel);

			menuBar = new JMenuBar();
			itemsMenu = new JMenu("Menu");
			menuBar.add(itemsMenu);
			frame.add(menuBar, BorderLayout.NORTH);
			selectedTab = "NULL";

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
		JMenuItem item = new JMenuItem(tab.getTabName());

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedTab = tab.getTabName();
			}
		});

		if (selectedTab.equals("NULL")) {
			selectedTab = tab.getTabName();
		}

		itemsMenu.add(item);
	}

	public static void update() {
		// TODO: Clear display of artifacts.
		itemsPanel.repaint();

		for (ProfileTab tab : tabList) {
			for (ProfileLabel label : tab.getLabels()) {
				if (label.isDisplayed()) {
					itemsPanel.remove(label.getJLabel());
					label.setDisplayed(false);
				}

				if (tab.getTabName().equals(selectedTab)) {
					itemsPanel.add(label.getJLabel());
					label.setDisplayed(true);
				}
			}
		}
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
