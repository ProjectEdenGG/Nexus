package gg.projecteden.nexus.features.resourcepack.customblocks.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public class CustomBlockExtent extends AbstractDelegateExtent {
	protected final EditSessionEvent event;
	private final World world;

	public CustomBlockExtent(EditSessionEvent event, org.bukkit.World world) {
		super(event.getExtent());

		this.event = event;
		this.world = world;
	}

	@Override
	public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y, int z, T block) throws WorldEditException {
		Location location = new Location(world, x, y, z);

		CustomBlock previousCustomBlock = CustomBlock.from(location.getBlock());
		if (previousCustomBlock != null) {
			previousCustomBlock.breakBlock(null, null, location, true, false, 0, false, false);
		}

		if (CustomBlockParser.isCustomBlockType(block.getBlockType()))
			setCustomBlock(location, block);

		return super.setBlock(x, y, z, block);
	}


	private boolean setCustomBlock(Location location, BlockStateHolder<?> block) {
		if (block == null)
			return false;

		BlockData blockData = BukkitAdapter.adapt(block);
		if (blockData == null)
			return false;

		CustomBlock customBlock = CustomBlock.from(blockData, null);
		if (customBlock == null)
			return false;

		CustomBlockUtils.placeBlockDatabaseAsServer(customBlock, location, BlockFace.UP);
		return true;
	}
}


