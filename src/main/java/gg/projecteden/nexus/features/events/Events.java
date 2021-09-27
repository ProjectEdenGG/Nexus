package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.events.y2021.pride21.Pride21;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.utils.Env;

public class Events extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Events");
	public static final String STORE_PREFIX = StringUtils.getPrefix("Event Store");

	@Override
	public void onStart() {
		if (Nexus.getEnv() == Env.PROD) {
			new Timer("    Events.ScavHuntLegacy", ScavHuntLegacy::new);
			new Timer("    Events.AeveonProject", AeveonProject::new);
			new Timer("    Events.Pride21", Pride21::new);
		}

		new Timer("    Events.ArmorStandStalker", ArmorStandStalker::new);
		new Timer("    Events.BearFair21", BearFair21::new);
		new Timer("    Events.Pugmas21", Pugmas21::new);
	}

	@Override
	public void onStop() {
		MiniGolf.shutdown();
		BearFair21.shutdown();
		Pugmas21.shutdown();
	}

}
