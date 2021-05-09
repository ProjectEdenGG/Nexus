package me.pugabyte.nexus.features.commands;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
