package me.pugabyte.bncore.features.events;

import me.pugabyte.bncore.features.events.aeveonproject.AeveonProject;
import me.pugabyte.bncore.features.events.y2020.bearfair20.BearFair20;
import me.pugabyte.bncore.features.events.y2020.halloween20.Halloween20;
import me.pugabyte.bncore.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.bncore.framework.annotations.Environments;
import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.utils.Env;

@Environments(Env.PROD)
public class Events extends Feature {

	@Override
	public void startup() {
		new ScavHuntLegacy();

		new AeveonProject();

		new BearFair20();
		new Halloween20();
		new Pugmas20();
	}

}
