package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.framework.features.Feature;

public class McMMO extends Feature {

	public static final int TIER_ONE = 100;

	@Override
	public void onStart() {
		new McMMOListener();
	}

}
