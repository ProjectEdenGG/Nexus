package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import org.bukkit.util.Vector;

@Permission(Group.STAFF)
public class ForwardCommand extends CustomCommand {

	public ForwardCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Teleport yourself forward a specific amount of blocks")
	void run(int blocks) {
		Vector forward = player().getEyeLocation().getDirection().multiply(blocks);
		player().teleportAsync(location().add(forward));
	}
}
