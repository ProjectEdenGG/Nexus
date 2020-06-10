package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Block;

public class TreeCommand extends CustomCommand {

	public TreeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[type]")
	void run(TreeType treeType) {
		Block target = player().getTargetBlockExact(500);
		if (target == null)
			error("Target block not found");

		Location location = target.getLocation().add(0, 1, 0);
		if (!location.getBlock().isPassable())
			error("Could not generate tree on " + camelCase(target.getType()));

		player().getWorld().generateTree(target.getLocation(), treeType);
		send(PREFIX + "Generated " + camelCase(treeType));
	}

}
