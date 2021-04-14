package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.TimeUtils.Time;

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
