package me.pugabyte.bncore.features.quests;

import me.pugabyte.bncore.framework.features.Feature;

public class Quests extends Feature {

	@Override
	public void startup() {
		new RegenRegions();
	}

}
