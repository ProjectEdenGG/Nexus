package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.framework.features.Feature;

public class Quests extends Feature {

	@Override
	public void onStart() {
		new RegenRegions();
	}

}
