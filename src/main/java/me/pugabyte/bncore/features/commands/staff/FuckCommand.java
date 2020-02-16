package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public class FuckCommand extends CustomCommand {

	public FuckCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void fuck() {
		if (player().hasPermission("group.staff")) {
			Block block = player().getTargetBlock(null, 20);
			if (block == null || block.getType() == Material.AIR)
				error("No block found");

			final BlockBreakEvent event = new BlockBreakEvent(block, player());
			Utils.callEvent(event);
			if (event.isCancelled())
				error("Cannot break that block");

			block.setType(Material.AIR);
		} else
			send("&4rude.");
	}

}
