package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

@Aliases("fuck")
public class BreakCommand extends CustomCommand {

	public BreakCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Break the block you are looking at")
	void fuck() {
		if (isStaff()) {
			Block block = getTargetBlockRequired();

			final BlockBreakEvent event = new BlockBreakEvent(block, player());
			if (!event.callEvent())
				error("Cannot break that block");

			block.setType(Material.AIR);
		} else if ("fuck".equalsIgnoreCase(getAliasUsed()))
			send("&4rude.");
		else
			permissionError();
	}

}
