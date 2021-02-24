package me.pugabyte.nexus.features.events;

import me.pugabyte.nexus.features.events.aeveonproject.AeveonProject;
import me.pugabyte.nexus.features.events.y2020.bearfair20.BearFair20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.StringUtils;

@Environments(Env.PROD)
public class Events extends Feature {
	public static String PREFIX = StringUtils.getPrefix("Events");
	private Pugmas20 pugmas20;

	@Override
	public void onStart() {
		new ArmorStandStalker();

		new ScavHuntLegacy();

		new AeveonProject();

		new BearFair20();
		pugmas20 = new Pugmas20();
	}

	@Override
	public void onStop() {
		pugmas20.shutdown();
	}

}
