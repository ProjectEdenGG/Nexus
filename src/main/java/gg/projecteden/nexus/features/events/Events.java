package gg.projecteden.nexus.features.events;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Timer;

@Deprecated
public class Events extends Feature {

	@Override
	public void onStart() {
		if (Nexus.getEnv() == Env.PROD) {
			new Timer("    Events.ScavHuntLegacy", ScavHuntLegacy::new);
			new Timer("    Events.AeveonProject", AeveonProject::new);
		}
	}
}
