package flounder.engine.profiling;

import flounder.devices.*;
import flounder.engine.*;
import javafx.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class EngineProfiler {
	private static JFrame frame;
	private static HashMap<Pair<String, JLabel>, Float> displayed;
	private static boolean open;
	private static int yLocation = 320;

	public static void init() {
		frame = new JFrame(ManagerDevices.getDisplay().getTitle() + " Profiler");
		frame.setSize(420, 720);
		frame.setResizable(false);
		frame.setLayout(new FlowLayout());
		displayed = new HashMap<>();
		toggle(false);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				toggle(false);
			}
		});
	}

	public static void toggle(final boolean open) {
		frame.setVisible(EngineProfiler.open = open);
	}

	public static void update(final Map<String, IRenderer> renderers) {
		// TODO: Put renderer data in display.

		for (Map.Entry<String, IRenderer> entry : renderers.entrySet()) {
			JLabel label = new JLabel();
			label.setText(entry.getKey());
			label.setLocation(210, (yLocation += 50));
			frame.add(label);
		}

		frame.pack();
	}

	public static void addLabel(String s, float v) {
		for (Pair<String, JLabel> pair : displayed.keySet()) {
			if (pair.getKey().equals(s)) {
				pair.getValue().setText(s + "" + v);
				return;
			}
		}

		JLabel label = new JLabel();
		label.setText(s + "" + v);
		label.setLocation(210, (yLocation += 50));
		frame.add(label);
		displayed.put(new Pair<>(s, label), v);

	}

	public static boolean isOpen() {
		return open;
	}

	public static void dispose() {
		frame.dispose();
	}
}
