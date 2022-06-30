package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.utils.Env;

public class Events extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Events");
	public static final String STORE_PREFIX = StringUtils.getPrefix("Event Store");

	@Override
	public void onStart() {
		switch (Nexus.getEnv()) {
			case TEST -> new Timer("    Events.MobEvents", MobEvents::new);
			case PROD -> {
				new Timer("    Events.ScavHuntLegacy", ScavHuntLegacy::new);
				new Timer("    Events.AeveonProject", AeveonProject::new);
			}
		}
	}

}
