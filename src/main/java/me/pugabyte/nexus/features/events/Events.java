package me.pugabyte.nexus.features.events;

import eden.annotations.Environments;
import eden.utils.Env;
import me.pugabyte.nexus.features.events.aeveonproject.AeveonProject;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.StringUtils;

@Environments(Env.PROD)
public class Events extends Feature {
	public static String PREFIX = StringUtils.getPrefix("Events");

	@Override
	public void onStart() {
		new ArmorStandStalker();
		new ScavHuntLegacy();
		new AeveonProject();
		new BearFair21();
	}

	@Override
	public void onStop() {
		MiniGolf.shutdown();
	}

}
