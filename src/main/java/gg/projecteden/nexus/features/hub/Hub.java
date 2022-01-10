package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.framework.features.Feature;

public class Hub extends Feature {

	@Override
	public void onStart() {
		new TreasureHunt();
	}

}
