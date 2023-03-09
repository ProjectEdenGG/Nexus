package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.BlockUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;

import java.util.List;

@Permission(Group.STAFF)
public class SnowLayersCommand extends CustomCommand {

	public SnowLayersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("overlay <radius> <topBlockOnly> <materials>")
	@Description("Overlay nearby blocks with a layer of snow")
	void overlay(int radius, boolean topBlockOnly, @Arg(type = Material.class) List<Material> materials) {
		int placed = 0;
		main:
		for (Block block : BlockUtils.getBlocksInRadius(location(), radius)) {

			if (!materials.contains(block.getType()))
				continue;

			if (topBlockOnly) {
				Location up = block.getLocation();
				for (int y = block.getY() + 1; y < world().getMaxHeight(); y++) {
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
	@Description("Set nearby grass to be snowy if covered by snow")
	void fixGrass(@Arg("10") int radius) {
		int fixedDirt = 0, fixedGrass = 0;
		for (Block block : BlockUtils.getBlocksInRadius(location(), radius)) {
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
	@Description("Remove snow layers on top of snow layers")
	void fixOverlay(@Arg("10") int radius) {
		int fixed = 0;
		for (Block block : BlockUtils.getBlocksInRadius(location(), radius)) {
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
	@Description("Set your current location to snow with the provided amount of layers")
	void layers(@Arg(min = 0, max = 8) int layers) {
		Block block = location().getBlock();
		block.setType(Material.SNOW, false);
		Snow snow = (Snow) block.getBlockData();
		snow.setLayers(layers);
		block.setBlockData(snow);
	}
}
