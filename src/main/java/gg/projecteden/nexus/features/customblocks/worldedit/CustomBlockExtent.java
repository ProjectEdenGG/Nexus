package gg.projecteden.nexus.features.customblocks.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.WorldEditUtils;
import org.bukkit.World;

public class CustomBlockExtent extends AbstractDelegateExtent {
	World world;
	WorldEditUtils WEUtils;

	public CustomBlockExtent(Extent extent, org.bukkit.World world) {
		super(extent);

		this.world = world;
		this.WEUtils = new WorldEditUtils(world);

		Dev.WAKKA.send("created extent");
	}

	// TODO: these events don't seem to fire
	@Override
	public <B extends BlockStateHolder<B>> boolean setBlock(int x, int y, int z, B block) throws WorldEditException {
		Dev.WAKKA.send("setBlock(x, y, z, block)");
		return super.setBlock(x, y, z, block);
	}

	@Override
	public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) throws WorldEditException {
		Dev.WAKKA.send("setBlock(position, block)");
		return super.setBlock(position, block);
	}
}

//		BaseBlock baseBlock = block.toBaseBlock();
//		Material material = WEUtils.toMaterial(baseBlock);
//
//		Location location = WEUtils.toLocation(position);
//		Block priorBlock = location.getBlock();
//		Material priorMaterial = priorBlock.getType();
//
//		// if setting to a handled material
//		if (CustomBlocksListener.handleMaterials.contains(material)) {
//			BlockData blockData = BukkitAdapter.adapt(block);
//			Dev.WAKKA.send("BlockData: " + blockData);
//			CustomBlock _customBlock = CustomBlock.fromBlockData(blockData, priorBlock.getRelative(BlockFace.DOWN));
//			if (_customBlock != null) {
//				Dev.WAKKA.send("Custom Block: " + _customBlock.name());
//			}
//		}
