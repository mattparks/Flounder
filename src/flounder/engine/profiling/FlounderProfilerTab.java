package flounder.engine.profiling;

import javax.swing.*;
import java.util.*;

/**
 * Created by Evan on 4/5/2016.
 * Tab object to store primary component information
 */
public class FlounderProfilerTab {

	private JPanel primaryComponent;
	private List<FlounderProfilerLabel> labels;

	protected FlounderProfilerTab(JPanel primaryComponent) {
		this.primaryComponent = primaryComponent;
	}

	public <T> void addLabel(String contentLabel, T value) {
		for (FlounderProfilerLabel label : labels) {
			if (label.getContentLabel().equals(contentLabel)) {
				label.setValue(value);
				return;
			}
		}

		FlounderProfilerLabel label = new FlounderProfilerLabel(contentLabel, value);
		labels.add(label);
	}

	public void removeLabel(String contentLabel) {
		for (FlounderProfilerLabel label : labels) {
			if (label.getContentLabel().equals(contentLabel)) {
				labels.remove(label);
				return;
			}
		}
	}

	protected JPanel getPrimaryComponent() { return primaryComponent; }


}
