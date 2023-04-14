package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

@Permission(Group.STAFF)
public class UnvanishCommand extends CustomCommand {

	public UnvanishCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Turns vanish off")
	void run() {
		runCommand("vanish off");
	}

	@Description("Turns vanish off and puts you in the Minigames channel")
	void gameworld() {
		runCommand("vanish off");
		runCommand("ch join m");
	}

	@Description("Turns vanish off and puts you in the Creative channel")
	void creative() {
		runCommand("vanish off");
		runCommand("ch join c");
	}
}
