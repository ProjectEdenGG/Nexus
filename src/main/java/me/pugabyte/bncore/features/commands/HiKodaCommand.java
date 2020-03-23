package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;

@Cooldown(@Part(Time.MINUTE))
public class HiKodaCommand extends CustomCommand {

	public HiKodaCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void hiKoda() {
		runCommand("ch qm g Hi Koda!");
	}

}
