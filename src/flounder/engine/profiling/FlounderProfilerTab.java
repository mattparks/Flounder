package flounder.engine.profiling;

import javax.swing.*;
import java.util.*;

/**
 * Tab object to store primary component information
 */
public class FlounderProfilerTab {
	private JPanel primaryComponent;
	private List<FlounderProfilerLabel> labels;

	protected FlounderProfilerTab(final JPanel primaryComponent) {
		this.primaryComponent = primaryComponent;
		this.labels = new ArrayList<>();
	}

	public <T> void addLabel(final String contentLabel, final T value) {
		for (final FlounderProfilerLabel label : labels) {
			if (label.getContentLabel().equals(contentLabel)) {
				label.setValue(value);
				return;
			}
		}

		FlounderProfilerLabel label = new FlounderProfilerLabel(contentLabel, value);
		primaryComponent.add(label.getDisplayObject());
		labels.add(label);
	}

	public void removeLabel(final String contentLabel) {
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

	public void dispose() {
		primaryComponent = null;
		labels = null;
	}
}
