package me.pugabyte.bncore.features.holidays;

import me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.halloween20.Halloween20;
import me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20;
import me.pugabyte.bncore.framework.annotations.Environments;
import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.utils.Env;

@Environments(Env.PROD)
public class Holidays extends Feature {

	@Override
	public void startup() {
		new ScavHuntLegacy();

		new AeveonProject();

		new BearFair20();
		new Halloween20();
		new Pugmas20();
	}

}
