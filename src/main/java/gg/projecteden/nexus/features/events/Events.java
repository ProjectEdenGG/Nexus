package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.events.y2021.pride21.Pride21;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.Env;

public class Events extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Events");
	public static final String STORE_PREFIX = StringUtils.getPrefix("Event Store");

	@Override
	public void onStart() {
		if (Nexus.getEnv() == Env.PROD) {
			new ScavHuntLegacy();
			new AeveonProject();
			new Pride21();
		}

		new ArmorStandStalker();
		new BearFair21();
	}

	@Override
	public void onStop() {
		MiniGolf.shutdown();
	}

}
