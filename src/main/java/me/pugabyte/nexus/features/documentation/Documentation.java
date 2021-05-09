package me.pugabyte.nexus.features.documentation;

import eden.annotations.Disabled;
import me.pugabyte.nexus.features.documentation.commands.DocumentCommands;
import me.pugabyte.nexus.framework.features.Feature;

@Disabled
public class Documentation extends Feature {

	@Override
	public void onStart() {
		new DocumentCommands();
	}

}
