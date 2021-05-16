package me.pugabyte.nexus.features.events;

import eden.utils.Env;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.aeveonproject.AeveonProject;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.pride21.Pride21;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.StringUtils;

public class Events extends Feature {
	public static String PREFIX = StringUtils.getPrefix("Events");

	@Override
	public void onStart() {
		if (Nexus.getEnv().equals(Env.PROD)) {
			new ArmorStandStalker();
			new ScavHuntLegacy();
			new AeveonProject();
		}

		new BearFair21();
		new Pride21();
	}

	@Override
	public void onStop() {
		MiniGolf.shutdown();
	}

}
