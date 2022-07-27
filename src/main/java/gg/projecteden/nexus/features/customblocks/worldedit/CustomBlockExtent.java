package gg.projecteden.nexus.features.customblocks.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class CustomBlockExtent extends AbstractDelegateExtent {
	private final Extent extent;
	private final World world;
	private final WorldEditUtils worldedit;

	public CustomBlockExtent(Extent extent, org.bukkit.World world) {
		super(extent);

		this.extent = extent;
		this.world = world;
		this.worldedit = new WorldEditUtils(world);
	}

	@Override
	public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y, int z, T block) throws WorldEditException {
		debug("setBlock(x, y, z, block)");
		Location location = new Location(world, x, y, z);
		if (setBlock(location, block, EditType.PLACE))
			return true;

		return super.setBlock(x, y, z, block);
	}

	@Override
	public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) throws WorldEditException {
		debug("setBlock(position, block)");
		Location location = worldedit.toLocation(position);

		if (setBlock(location, block, EditType.REMOVE))
			return true;

		return super.setBlock(position, block);
	}


	private boolean setBlock(Location location, BlockStateHolder<?> block, EditType editType) {
		BaseBlock baseBlock = block.toBaseBlock();
		Material material = worldedit.toMaterial(baseBlock);

		org.bukkit.block.Block priorBlock = location.getBlock();
		Material priorMaterial = priorBlock.getType();
		DotEffect.debug(Dev.WAKKA.getPlayer(), location.toCenterLocation(), editType.getColor());

		debug("Edit Type: " + editType.name());

		// if setting to a handled material
//		if (CustomBlockType.getBlockMaterials().contains(material)) {
		BlockData blockData = BukkitAdapter.adapt(block);
//			debug("BlockData: " + blockData);
		debug("Type: " + block.getBlockType());
		CustomBlock _customBlock = CustomBlock.fromBlockData(blockData, priorBlock.getRelative(BlockFace.DOWN));
		if (_customBlock != null) {
			debug("Custom Block Data: " + CustomBlockUtils.getData(location));
//				return true;
		}
//		}

		debug("");

		return false;
	}

	@AllArgsConstructor
	private enum EditType {
		PLACE(ColorType.BLUE),
		REMOVE(ColorType.RED),
		;

		final ColorType colorType;

		public Color getColor() {
			return colorType.getBukkitColor();
		}
	}
}


