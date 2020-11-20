package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Block;

public class TreeCommand extends CustomCommand {

	public TreeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[type]")
	void run(@Arg("tree") TreeType treeType) {
		Block target = player().getTargetBlockExact(500);
		if (target == null)
			error("Target block not found");

		Location location = target.getLocation().add(0, 1, 0);
		if (location.getBlock().getType().isSolid())
			error("Could not generate tree on " + camelCase(target.getType()));

		if (player().getWorld().generateTree(target.getLocation(), treeType))
			send(PREFIX + "Generated " + camelCase(treeType));
		else
			send(PREFIX + "Tree generation failed");
	}

}
