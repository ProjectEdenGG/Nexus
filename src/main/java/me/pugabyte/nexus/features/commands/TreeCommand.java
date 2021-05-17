package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Block;

@Permission("group.staff")
public class TreeCommand extends CustomCommand {

	public TreeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[type]")
	void run(@Arg("tree") TreeType treeType) {
		Block target = getTargetBlockRequired();

		Location location = target.getLocation().add(0, 1, 0);
		if (location.getBlock().getType().isSolid())
			error("Could not generate tree on " + camelCase(target.getType()));

		if (world().generateTree(target.getLocation(), treeType))
			send(PREFIX + "Generated " + camelCase(treeType));
		else
			send(PREFIX + "Tree generation failed");
	}

}
