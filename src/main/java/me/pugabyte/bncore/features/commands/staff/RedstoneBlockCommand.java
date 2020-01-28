package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.World;

@Permission("group.staff")
public class RedstoneBlockCommand extends CustomCommand {

	public RedstoneBlockCommand(CommandEvent event) {
		super(event);
	}

	@Path("<x> <y> <z> [world] [makeAir]")
	void redstoneBlock(int x, int y, int z, @Arg("current") World world, @Arg("false") boolean makeAir) {
		world.getBlockAt(x, y, z).setType(Material.REDSTONE_BLOCK);
		if (makeAir) Tasks.wait(1, () -> world.getBlockAt(x, y, z).setType(Material.AIR));
	}
}
