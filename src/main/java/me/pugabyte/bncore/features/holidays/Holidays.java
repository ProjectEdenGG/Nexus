package me.pugabyte.bncore.features.holidays;

import me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.halloween20.Halloween20;

public class Holidays {

	public Holidays() {
		new ScavHuntLegacy();

		new AeveonProject();

		new BearFair20();
		new Halloween20();
	}

}
