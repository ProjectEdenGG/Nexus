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

import java.util.List;

@Permission("group.staff")
public class SnowLayersCommand extends CustomCommand {

	public SnowLayersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("overlay <radius> <topBlockOnly> <materials>")
	void overlay(int radius, boolean topBlockOnly, @Arg(type = Material.class) List<Material> materials) {
		int placed = 0;
		main:
		for (Block block : BlockUtils.getBlocksInRadius(player().getLocation(), radius)) {

			if (!materials.contains(block.getType()))
				continue;

			if (topBlockOnly) {
				Location up = block.getLocation();
				for (int y = block.getY() + 1; y < 256; y++) {
					up.setY(y);
					if (up.getBlock().getType() != Material.AIR)
						continue main;
				}
			}

			block.getRelative(BlockFace.UP).setType(Material.SNOW);
			++placed;
		}

		send(PREFIX + "Placed " + placed + " snow");
	}

	@Path("fixGrass [radius]")
	void fixGrass(@Arg("10") int radius) {
		int fixedDirt = 0, fixedGrass = 0;
		for (Block block : BlockUtils.getBlocksInRadius(player().getLocation(), radius)) {
			boolean grass = block.getType() == Material.GRASS_BLOCK;
			boolean dirt = block.getType() == Material.DIRT;
			if (!(dirt || grass))
				continue;

			boolean snowy = block.getRelative(BlockFace.UP).getType() == Material.SNOW;

			if (!snowy)
				continue;

			if (dirt) {
				block.setType(Material.GRASS_BLOCK);
				++fixedDirt;
			}

			Snowable snowable = (Snowable) block.getBlockData();
			if (!snowable.isSnowy()) {
				snowable.setSnowy(snowy);
				block.setBlockData(snowable);
				if (grass)
					++fixedGrass;
			}
		}

		send(PREFIX + "Fixed " + fixedDirt + " dirt and " + fixedGrass + " grass");
	}

	@Path("fixOverlay [radius]")
	void fixOverlay(@Arg("10") int radius) {
		int fixed = 0;
		for (Block block : BlockUtils.getBlocksInRadius(player().getLocation(), radius)) {
			if (block.getType() != Material.SNOW)
				continue;

			Block relative = block.getRelative(BlockFace.UP);
			if (relative.getType() == Material.SNOW) {
				relative.setType(Material.AIR);
				++fixed;
			}
		}

		send(PREFIX + "Fixed " + fixed + " blocks");
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
