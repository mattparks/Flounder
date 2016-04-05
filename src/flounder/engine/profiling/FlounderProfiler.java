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
	private static ProfileTab selectedTab;

	public static void init(final String title) {
		if (!initialized) {
			frame = new JFrame(title);
			frame.setSize(420, 720);
			frame.setResizable(false);
			frame.setLayout(new BorderLayout());
			toggle(false);

			tabList = new ArrayList<>();
			itemsPanel = new JPanel();
			// itemsPanel.setLayout(new FlowLayout());
			frame.add(itemsPanel);

			menuBar = new JMenuBar();
			itemsMenu = new JMenu("Menu");
			menuBar.add(itemsMenu);
			frame.add(menuBar, BorderLayout.NORTH);
			selectedTab = null;

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

	public static <T> void add(final String tabName, final String title, final T value) {
		for (final ProfileTab tab : tabList) {
			if (tab.getTabName().equals(tabName)) {
				tab.addLabel(title, value);
				return;
			}
		}

		ProfileTab tab = new ProfileTab(tabName);
		tab.addLabel(title, value);
		addTab(tab);
	}

	protected static void addTab(final ProfileTab tab) {
		tabList.add(tab);
		JMenuItem item = new JMenuItem(tab.getTabName());

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedTab = tab;
			}
		});

		if (selectedTab == null) {
			selectedTab = tab;
		}

		itemsMenu.add(item);
	}

	public static void update() {
		itemsPanel.removeAll();
		if (selectedTab != null) {
			selectedTab.update(itemsPanel);
		}
		itemsPanel.repaint();
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
