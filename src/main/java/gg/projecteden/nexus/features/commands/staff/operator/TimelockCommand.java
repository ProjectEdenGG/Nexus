package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;

@Aliases("tl")
@Permission(Group.SENIOR_STAFF)
public class TimelockCommand extends CustomCommand {

	public TimelockCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Lock a world's daylight cycle")
	void set(@Vararg String time) {
		runCommandAsOp("time set " + time);
		runCommandAsOp("mv gamerule doDaylightCycle false");
		send(PREFIX + "Daylight cycle locked");
	}

	@Description("Enable a world's daylight cycle")
	void reset() {
		runCommandAsOp("mv gamerule doDaylightCycle true");
		send(PREFIX + "Normal daylight cycle resumed");
	}

}
