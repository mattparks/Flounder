package flounder.engine.profiling;

import java.util.*;

public class ProfileTab {
	private String tabName;
	private List<ProfileLabel> labels;
	private int yLocation;

	protected ProfileTab(final String tabName) {
		this.tabName = tabName;
		labels = new ArrayList<>();
		yLocation = 320;
	}

	protected <T> void addLabel(final String title, final T value) {
		for (ProfileLabel label : labels) {
			if (label.getLabel().equals(title)) {
				label.setValue(value);
				return;
			}
		}

		ProfileLabel label = new ProfileLabel(title, value, 210, (yLocation += 50));
		labels.add(label);
	}

	//protected void removeLabel(final String title) {
	//	for (ProfileLabel label : labels) {
	//		if (label.getLabel().equals(title)) {
	//			labels.remove(label);
	//			return;
	//		}
	//	}
	//}

	protected String getTabName() {
		return tabName;
	}

	protected List<ProfileLabel> getLabels() {
		return labels;
	}
}
