package flounder.engine.profiling;

import javax.swing.*;

/**
 * Created by Evan on 4/5/2016.
 * Profiler label to ensure compat
 */
class FlounderProfilerLabel<T> {

	private String contentLabel;
	private T value;
	private JLabel jLabel;

	protected FlounderProfilerLabel(String contentLabel, T value) {
		this.contentLabel = contentLabel;
		this.value = value;

		jLabel = new JLabel(contentLabel + " = " + value);
	}

	protected T getValue() { return value; }
	protected String getContentLabel() { return contentLabel; }
	protected JLabel getDisplayObject() { return jLabel; }

	protected void setValue(T value) {
		this.value = value;
		jLabel.setText(contentLabel + " = " + value);
	}

	protected void setContentLabel(String contentLabel) {
		this.contentLabel = contentLabel;
	}


}
