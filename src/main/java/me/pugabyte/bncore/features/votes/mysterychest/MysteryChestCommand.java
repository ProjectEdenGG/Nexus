package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("mysterychest.use")
public class MysteryChestCommand extends CustomCommand {

	public MysteryChestCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void use() {
		MysteryChestProvider.time = 0;
		MysteryChestProvider.speed = 4;
		SmartInventory.builder()
				.size(3, 9)
				.title("Mystery Chest")
				.provider(new MysteryChestProvider())
				.build()
				.open(player());
	}

}
