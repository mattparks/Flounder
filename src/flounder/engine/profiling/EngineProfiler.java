package flounder.engine.profiling;

import flounder.devices.*;
import flounder.engine.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class EngineProfiler {
	private static JFrame m_frame;
	private static boolean m_open;

	public static void init() {
		m_frame = new JFrame(ManagerDevices.getDisplay().getDisplayTitle() + " Profiler");
		m_frame.setSize(420, 720);
		m_frame.setResizable(false);
		m_frame.setLayout(new FlowLayout());
		toggle(false);

		m_frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				toggle(false);
			}
		});
	}

	public static void update(final Map<String, IRenderer> renderers) {
		// TODO: Put renderer data in display.
		int yLocation = 320;

		for (Map.Entry<String, IRenderer> entry : renderers.entrySet()) {
			JLabel label = new JLabel();
			label.setText(entry.getKey());
			label.setLocation(210, (yLocation += 50));
			m_frame.add(label);
		}

		m_frame.pack();
	}

	public static void toggle(final boolean open) {
		m_frame.setVisible(m_open = open);
	}

	public static boolean isOpen() {
		return m_open;
	}

	public static void dispose() {
		m_frame.dispose();
	}
}
