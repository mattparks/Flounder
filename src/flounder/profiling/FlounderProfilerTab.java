package flounder.profiling;

import javax.swing.*;
import java.util.*;

/**
 * Tab object to store primary component information
 */
public class FlounderProfilerTab {
	private JPanel primaryComponent;
	private List<FlounderProfilerLabel> labels;

	protected FlounderProfilerTab(JPanel primaryComponent) {
		this.primaryComponent = primaryComponent;
		this.labels = new ArrayList<>();
	}

	protected <T> void addLabel(String contentLabel, T value) {
		for (FlounderProfilerLabel label : labels) {
			if (label.getContentLabel().equals(contentLabel)) {
				label.setValue(value);
				return;
			}
		}

		FlounderProfilerLabel label = new FlounderProfilerLabel(contentLabel, value);
		primaryComponent.add(label.getDisplayObject());
		labels.add(label);
	}

	protected void removeLabel(String contentLabel) {
		for (FlounderProfilerLabel label : labels) {
			if (label.getContentLabel().equals(contentLabel)) {
				primaryComponent.remove(label.getDisplayObject());
				labels.remove(label);
				label = null;
				return;
			}
		}
	}

	protected JPanel getPrimaryComponent() {
		return primaryComponent;
	}

	protected void dispose() {
		primaryComponent = null;
		labels = null;
	}
}
