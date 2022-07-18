package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Aliases("tl")
@Permission(Group.SENIOR_STAFF)
public class TimelockCommand extends CustomCommand {

	public TimelockCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("reset")
	void reset() {
		runCommandAsOp("mv gamerule doDaylightCycle true");
		send(PREFIX + "Normal daylight cycle resumed");
	}

	@Path("<time...>")
	void set(String time) {
		runCommandAsOp("time set " + time);
		runCommandAsOp("mv gamerule doDaylightCycle false");
		send(PREFIX + "Daylight cycle locked");
	}

}
