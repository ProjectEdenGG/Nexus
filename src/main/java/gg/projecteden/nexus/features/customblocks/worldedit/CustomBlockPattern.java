package gg.projecteden.nexus.features.customblocks.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractExtentPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public class CustomBlockPattern extends AbstractExtentPattern {
	CustomBlock customBlock;
	ParserContext context;

	public CustomBlockPattern(Extent extent, ParserContext context, CustomBlock customBlock) {
		super(extent);

		this.customBlock = customBlock;
		this.context = context;

		Dev.WAKKA.send("new custom block pattern");
	}

	@Override
	public BaseBlock applyBlock(BlockVector3 position) {
		Dev.WAKKA.send("apply block");
//		BlockState oldBlock = getExtent().getBlock(position);

		World world = BukkitAdapter.adapt(context.getWorld());
		Location location = BukkitAdapter.adapt(world, position);

		BlockFace facing = BlockFace.UP;
		Block underneath = location.getBlock().getRelative(BlockFace.DOWN);
		BlockData blockData = customBlock.get().getBlockData(facing, underneath);

		BlockState newBlock = BukkitAdapter.adapt(blockData);
		return newBlock.toBaseBlock();
	}


}
