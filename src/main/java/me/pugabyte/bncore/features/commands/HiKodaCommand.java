package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class HiKodaCommand extends CustomCommand {

	public HiKodaCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(value = 60 * 20)
	void hiKoda() {
		runCommand("ch qm g Hi Koda!");
	}

}
