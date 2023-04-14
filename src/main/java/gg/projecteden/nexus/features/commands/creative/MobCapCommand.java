package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

@Permission(Group.MODERATOR)
public class MobCapCommand extends CustomCommand {

	public MobCapCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Change the mob cap of a creative plot")
	void run(int amount) {
		runCommand("plot flag set mob-cap " + amount);
		runCommand("plot flag set hostile-cap " + amount);
		runCommand("plot flag set animal-cap " + amount);
		send("&3Set the mob cap to " + amount);
	}

}
