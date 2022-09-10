package com.flounder.logger;

import javax.swing.*;
import java.awt.event.*;

public class LoggerFrame extends JFrame {
	private boolean running;

	public LoggerFrame() {
		super.setTitle("Flounder Log Viewer");
		super.setSize(600, 800);
		super.setResizable(true);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				setVisible(false);
				running = false;
			}
		});

		JTextArea display = new JTextArea();

		for (String data : FlounderLogger.get().getSaveData()) {
			display.append(data + "\n");
		}

		display.setEditable(false);
		JScrollPane scroll = new JScrollPane(display);

		super.add(scroll);

		super.setVisible(true);
		super.toFront();

		this.running = true;
	}

	public void run() {
		while (running) {
			// Waiting for window close.
		}
	}
}
