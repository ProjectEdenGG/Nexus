package gg.projecteden.nexus.features.documentation;

import gg.projecteden.nexus.framework.features.Feature;

public class Documentation extends Feature {

	@Override
	public void onStart() {
		new DocumentCommands();
	}

}
