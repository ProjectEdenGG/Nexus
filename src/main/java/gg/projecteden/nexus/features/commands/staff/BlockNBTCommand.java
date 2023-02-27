package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.STAFF)
public class BlockNBTCommand extends CustomCommand {

	public BlockNBTCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Sends nbt data in chat of the block being looked at.")
	void nbt() {
		send(getTargetBlockRequired().getBlockData().getAsString());
	}

}
