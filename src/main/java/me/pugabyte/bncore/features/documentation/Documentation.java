package me.pugabyte.bncore.features.documentation;

import me.pugabyte.bncore.features.documentation.commands.DocumentCommands;
import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.features.Feature;

@Disabled
public class Documentation extends Feature {

	@Override
	public void startup() {
		new DocumentCommands();
	}

}
