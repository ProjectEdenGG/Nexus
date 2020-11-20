package me.pugabyte.nexus.features.quests;

import me.pugabyte.nexus.framework.features.Feature;

public class Quests extends Feature {

	@Override
	public void startup() {
		new RegenRegions();
	}

}
