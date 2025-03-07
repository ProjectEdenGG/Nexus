package gg.projecteden.nexus.features.resourcepack.customblocks.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public class WorldEditHandlers {

	public WorldEditHandlers(boolean register) {
		if (register)
			WorldEdit.getInstance().getEventBus().register(this);
		else
			WorldEdit.getInstance().getEventBus().unregister(this);
	}

	@Subscribe
	public void onEditSession(EditSessionEvent event) {
		if (event.getWorld() == null)
			return;

		event.setExtent(new AbstractDelegateExtent(event.getExtent()) {
			@Override
			public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 pos, T newBlock) throws WorldEditException {
				if (true)
					return super.setBlock(pos, newBlock);

				BlockData blockData = BukkitAdapter.adapt(newBlock);
				World world = Bukkit.getWorld(event.getWorld().getName());
				Location loc = new Location(world, pos.x(), pos.y(), pos.z());

				CustomBlock customBlock = CustomBlock.from(blockData, null);

				if (blockData.getMaterial() == Material.NOTE_BLOCK) {
					if (customBlock != null)
						Tasks.wait(1, () -> CustomBlockUtils.placeBlockDatabaseAsServer(customBlock, loc, BlockFace.UP));
				} else {
					if (world == null)
						return super.setBlock(pos, newBlock);

					CustomBlock replacingCustomBlock = CustomBlock.from(loc.getBlock());
					if (replacingCustomBlock == null)
						return super.setBlock(pos, newBlock);

					Tasks.wait(1, () -> CustomBlockUtils.breakBlockDatabase(loc));
				}

				return super.setBlock(pos, newBlock);
			}
		});
	}
}
