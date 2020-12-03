package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;

import java.util.Arrays;
import java.util.List;

@Permission("group.staff")
public class SnowLayersCommand extends CustomCommand {

	public SnowLayersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("overlay <radius> <topBlockOnly> <materials>")
	void overlay(int radius, boolean topBlockOnly, @Arg(type = Material.class) List<Material> materials) {
		BlockUtils.getBlocksInRadius(player().getLocation(), radius).forEach(block -> {
			if (!materials.contains(block.getType()))
				return;

			if (topBlockOnly) {
				Location up = block.getLocation();
				for (int y = block.getY() + 1; y < 256; y++) {
					up.setY(y);
					if (up.getBlock().getType() != Material.AIR)
						return;
				}
			}

			block.getRelative(BlockFace.UP).setType(Material.SNOW);
		});
	}

	@Path("fixGrass [radius]")
	void fixGrass(@Arg("10") int radius) {
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

	@Path("set <layers>")
	void layers(@Arg(min = 0, max = 8) int layers) {
		Block block = player().getLocation().getBlock();
		block.setType(Material.SNOW, false);
		Snow snow = (Snow) block.getBlockData();
		snow.setLayers(layers);
		block.setBlockData(snow);
	}
}
