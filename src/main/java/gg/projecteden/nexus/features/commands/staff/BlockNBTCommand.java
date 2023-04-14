package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

@Permission(Group.STAFF)
public class BlockNBTCommand extends CustomCommand {

	public BlockNBTCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View NBT data of your target block")
	void nbt() {
		send(getTargetBlockRequired().getBlockData().getAsString());
	}

}
