package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.ADMIN)
public class CustomBlockCommand extends CustomCommand {

	public CustomBlockCommand(CommandEvent event) {
		super(event);
	}

	@Path("list")
	void list() {
		send("Custom Blocks: ");
		for (CustomBlock customBlock : CustomBlock.values()) {
			send(" - " + customBlock.get().getName());
		}
	}

	@Path("get <block>")
	void get(CustomBlock block) {
		giveItem(block.get().getItemStack());
	}
}
