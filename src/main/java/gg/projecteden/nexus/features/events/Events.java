package gg.projecteden.nexus.features.events;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;

public class Events extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Events");
	public static final String STORE_PREFIX = StringUtils.getPrefix("Event Store");

	@Override
	public void onStart() {
		if (Nexus.getEnv() == Env.PROD) {
			new Timer("    Events.ScavHuntLegacy", ScavHuntLegacy::new);
			new Timer("    Events.AeveonProject", AeveonProject::new);

			new Timer("    Events.Pugmas24", Pugmas24::new);
		}
	}

}
