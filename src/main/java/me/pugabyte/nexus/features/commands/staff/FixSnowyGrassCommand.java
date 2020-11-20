package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Snowable;

import java.util.Arrays;

@Permission("group.staff")
public class FixSnowyGrassCommand extends CustomCommand {

	public FixSnowyGrassCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[radius]")
	void run(@Arg("10") int radius) {
		for (Block block : BlockUtils.getBlocksInRadius(player().getLocation(), radius)) {
			if (!Arrays.asList(Material.GRASS_BLOCK, Material.DIRT).contains(block.getType()))
				continue;

			Material above = block.getRelative(BlockFace.UP).getType();
			boolean snowy = above == Material.SNOW;

			if (snowy)
				block.setType(Material.GRASS_BLOCK);
			else if (block.getType() != Material.GRASS_BLOCK)
				continue;

			Snowable grass = (Snowable) block.getBlockData();
			grass.setSnowy(snowy);
			block.setBlockData(grass);
		}
	}

}
