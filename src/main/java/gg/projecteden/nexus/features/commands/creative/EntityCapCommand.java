package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

@Permission(Group.MODERATOR)
public class EntityCapCommand extends CustomCommand {

	public EntityCapCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Change the entity cap of a creative plot")
	void run(int amount) {
		runCommand("plot flag set entity-cap " + amount);
		send("&3Set the entity cap to " + amount);
	}

}
