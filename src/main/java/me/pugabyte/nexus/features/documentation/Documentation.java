package me.pugabyte.nexus.features.documentation;

import me.pugabyte.nexus.framework.features.Feature;

public class Documentation extends Feature {

	@Override
	public void onStart() {
		new DocumentCommands();
	}

}
