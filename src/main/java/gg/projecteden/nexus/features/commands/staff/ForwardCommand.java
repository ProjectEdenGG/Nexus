package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.util.Vector;

@Permission(Group.STAFF)
public class ForwardCommand extends CustomCommand {

	public ForwardCommand(CommandEvent event) {
		super(event);
	}

	@Path("<blocks>")
	void forward(int blocks) {
		Vector forward = player().getEyeLocation().getDirection().multiply(blocks);
		player().teleportAsync(location().add(forward));
	}
}
