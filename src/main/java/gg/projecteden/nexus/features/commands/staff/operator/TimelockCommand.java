package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
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

	@Path("<time...>")
	@Description("Lock a world's daylight cycle")
	void set(String time) {
		runCommandAsOp("time set " + time);
		runCommandAsOp("mv gamerule set doDaylightCycle false");
		send(PREFIX + "Daylight cycle locked");
	}

	@Path("reset")
	@Description("Enable a world's daylight cycle")
	void reset() {
		runCommandAsOp("mv gamerule set doDaylightCycle true");
		send(PREFIX + "Normal daylight cycle resumed");
	}

}
