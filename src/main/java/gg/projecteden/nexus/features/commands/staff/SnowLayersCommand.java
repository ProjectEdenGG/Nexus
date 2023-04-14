package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.ErasureType;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.RangeArgumentValidator.Range;
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

	@Description("Overlay nearby blocks with a layer of snow")
	void overlay(int radius, boolean topBlockOnly, @ErasureType(Material.class) List<Material> materials) {
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

	@Description("Set nearby grass to be snowy if covered by snow")
	void fixGrass(@Optional("10") int radius) {
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

	@Description("Remove snow layers on top of snow layers")
	void fixOverlay(@Optional("10") int radius) {
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

	@Description("Set your current location to snow with the provided amount of layers")
	void set(@Range(min = 0, max = 8) int layers) {
		Block block = location().getBlock();
		block.setType(Material.SNOW, false);
		Snow snow = (Snow) block.getBlockData();
		snow.setLayers(layers);
		block.setBlockData(snow);
	}
}
