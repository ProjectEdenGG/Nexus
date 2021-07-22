package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
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
