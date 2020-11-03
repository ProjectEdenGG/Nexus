package me.pugabyte.bncore.features.holidays.pugmas20;

import me.pugabyte.bncore.features.holidays.pugmas20.menu.AdventMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("pugmas")
@Permission("group.staff") // TODO Pugmas - Remove
public class Pugmas20Command extends CustomCommand {

	public Pugmas20Command(CommandEvent event) {
		super(event);
	}

	@Path("advent")
	void advent() {
		AdventMenu.openAdvent(player());
	}


}
