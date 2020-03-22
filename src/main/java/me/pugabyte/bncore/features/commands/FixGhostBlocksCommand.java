package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.block.Block;

// Copy and rename this file as a template for a new command

@Permission("permission")
public class FixGhostBlocksCommand extends CustomCommand {

	public FixGhostBlocksCommand(CommandEvent event) {
		super(event);
	}

	@Path("[range]")
	void fgb(@Arg("10") int finalRadius) {
		for (int x = -finalRadius; x <= finalRadius; x++) {
			for (int z = -finalRadius; z <= finalRadius; z++) {
				for (int y = -finalRadius; y < finalRadius; y++) {
					Block tempBlock = player().getLocation().getBlock().getRelative(x, y, z);
					tempBlock.setType(tempBlock.getType());
				}
			}
		}
		send("&eAll ghost blocks within " + finalRadius + " blocks updated");
	}


}
