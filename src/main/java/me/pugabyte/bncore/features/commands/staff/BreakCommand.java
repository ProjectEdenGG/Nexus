package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

@Aliases("fuck")
public class BreakCommand extends CustomCommand {

	public BreakCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void fuck() {
		if (player().hasPermission("group.staff")) {
			Block block = player().getTargetBlockExact(500);
			if (block == null || block.getType() == Material.AIR)
				error("No block found");

			final BlockBreakEvent event = new BlockBreakEvent(block, player());
			Utils.callEvent(event);
			if (event.isCancelled())
				error("Cannot break that block");

			block.setType(Material.AIR);
		} else
			if ("fuck".equalsIgnoreCase(getAliasUsed()))
				send("&4rude.");
			else
				throw new NoPermissionException();
	}

}
