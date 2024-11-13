package gg.projecteden.nexus.features.blockmechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.blockmechanics.events.SourcedBlockRedstoneEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockMechanicsListener implements Listener {

	public BlockMechanicsListener() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {

		if (!BlockMechanicUtils.passesFilter(event, event.getBlock()))
			return;

		if (!(BlockMechanicUtils.ADVANCED_BLOCK_CHECKS && event.isCancelled())) {
			checkBlockChange(event.getPlayer(), event.getBlock(), false);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {

		if (!BlockMechanicUtils.passesFilter(event, event.getBlock()))
			return;

		if (!(BlockMechanicUtils.ADVANCED_BLOCK_CHECKS && event.isCancelled())) {
			checkBlockChange(event.getPlayer(), event.getBlock(), true);
		}
	}

	private static void checkBlockChange(Player player, Block block, boolean build) {
		switch (block.getType()) {
			case REDSTONE_TORCH:
			case REDSTONE_WALL_TORCH:
			case REDSTONE_BLOCK:
				if (BlockMechanicUtils.PEDANTIC_BLOCK_CHECKS && !BlockMechanicUtils.canBuild(player, block.getLocation(), build))
					break;
				handleRedstoneForBlock(block, build ? 0 : 15, build ? 15 : 0);
				break;
			case ACACIA_BUTTON:
			case BIRCH_BUTTON:
			case DARK_OAK_BUTTON:
			case JUNGLE_BUTTON:
			case OAK_BUTTON:
			case SPRUCE_BUTTON:
			case STONE_BUTTON:
			case LEVER:
			case DETECTOR_RAIL:
			case STONE_PRESSURE_PLATE:
			case ACACIA_PRESSURE_PLATE:
			case BIRCH_PRESSURE_PLATE:
			case DARK_OAK_PRESSURE_PLATE:
			case JUNGLE_PRESSURE_PLATE:
			case OAK_PRESSURE_PLATE:
			case SPRUCE_PRESSURE_PLATE:
			case COMPARATOR:
			case REPEATER:
				if (BlockMechanicUtils.PEDANTIC_BLOCK_CHECKS && !BlockMechanicUtils.canBuild(player, block.getLocation(), build))
					break;
				Powerable powerable = (Powerable) block.getBlockData();
				if (powerable.isPowered())
					handleRedstoneForBlock(block, build ? 0 : 15, build ? 15 : 0);
				break;
			case HEAVY_WEIGHTED_PRESSURE_PLATE:
			case LIGHT_WEIGHTED_PRESSURE_PLATE:
			case REDSTONE_WIRE:
				if (BlockMechanicUtils.PEDANTIC_BLOCK_CHECKS && !BlockMechanicUtils.canBuild(player, block.getLocation(), build))
					break;
				AnaloguePowerable analoguePowerable = (AnaloguePowerable) block.getBlockData();
				if (analoguePowerable.getPower() > 0) {
					handleRedstoneForBlock(block, build ? 0 : analoguePowerable.getPower(), build ? analoguePowerable.getPower() : 0);
				}
				break;
			default:
				break;
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {

		if (!BlockMechanicUtils.passesFilter(event, event.getBlock()))
			return;

		handleRedstoneForBlock(event.getBlock(), event.getOldCurrent(), event.getNewCurrent());
	}

	private static void handleRedstoneForBlock(Block block, int oldLevel, int newLevel) {

		World world = block.getWorld();

		// Give the method a BlockWorldVector instead of a Block
		boolean wasOn = oldLevel >= 1;
		boolean isOn = newLevel >= 1;
		boolean wasChange = wasOn != isOn;

		// For efficiency reasons, we're only going to consider changes between
		// off and on state, and ignore simple current changes (i.e. 15->13)
		if (!wasChange) return;

		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();

		// When this hook has been called, the level in the world has not
		// yet been updated, so we're going to do this very ugly thing of
		// faking the value with the new one whenever the data value of this
		// block is requested -- it is quite ugly
		switch (block.getType()) {
			case REDSTONE_WIRE:
				if (BlockMechanicUtils.INDIRECT_REDSTONE) {

					// power all blocks around the redstone wire on the same y level
					// north/south
					handleDirectWireInput(x - 1, y, z, block, oldLevel, newLevel);
					handleDirectWireInput(x + 1, y, z, block, oldLevel, newLevel);
					// east/west
					handleDirectWireInput(x, y, z - 1, block, oldLevel, newLevel);
					handleDirectWireInput(x, y, z + 1, block, oldLevel, newLevel);

					// Can be triggered from below
					handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);

					// Can be triggered from above (Eg, glass->glowstone like redstone lamps)
					handleDirectWireInput(x, y - 1, z, block, oldLevel, newLevel);
				} else {

					Material above = world.getBlockAt(x, y + 1, z).getType();

					Material westSide = world.getBlockAt(x, y, z + 1).getType();
					Material westSideAbove = world.getBlockAt(x, y + 1, z + 1).getType();
					Material westSideBelow = world.getBlockAt(x, y - 1, z + 1).getType();
					Material eastSide = world.getBlockAt(x, y, z - 1).getType();
					Material eastSideAbove = world.getBlockAt(x, y + 1, z - 1).getType();
					Material eastSideBelow = world.getBlockAt(x, y - 1, z - 1).getType();

					Material northSide = world.getBlockAt(x - 1, y, z).getType();
					Material northSideAbove = world.getBlockAt(x - 1, y + 1, z).getType();
					Material northSideBelow = world.getBlockAt(x - 1, y - 1, z).getType();
					Material southSide = world.getBlockAt(x + 1, y, z).getType();
					Material southSideAbove = world.getBlockAt(x + 1, y + 1, z).getType();
					Material southSideBelow = world.getBlockAt(x + 1, y - 1, z).getType();


					// Make sure that the wire points to only this block
					if (!MaterialTag.REDSTONE_BLOCKS.isTagged(westSide) && !MaterialTag.REDSTONE_BLOCKS.isTagged(eastSide)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(westSideAbove) || westSide == Material.AIR || above != Material.AIR)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(eastSideAbove) || eastSide == Material.AIR || above != Material.AIR)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(westSideBelow) || westSide != Material.AIR)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(eastSideBelow) || eastSide != Material.AIR)) {
						// Possible blocks north / south
						handleDirectWireInput(x - 1, y, z, block, oldLevel, newLevel);
						handleDirectWireInput(x + 1, y, z, block, oldLevel, newLevel);
						handleDirectWireInput(x - 1, y - 1, z, block, oldLevel, newLevel);
						handleDirectWireInput(x + 1, y - 1, z, block, oldLevel, newLevel);
					}

					if (!MaterialTag.REDSTONE_BLOCKS.isTagged(northSide) && !MaterialTag.REDSTONE_BLOCKS.isTagged(southSide)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(northSideAbove) || northSide == Material.AIR || above != Material.AIR)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(southSideAbove) || southSide == Material.AIR || above != Material.AIR)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(northSideBelow) || northSide != Material.AIR)
						&& (!MaterialTag.REDSTONE_BLOCKS.isTagged(southSideBelow) || southSide != Material.AIR)) {
						// Possible blocks west / east
						handleDirectWireInput(x, y, z - 1, block, oldLevel, newLevel);
						handleDirectWireInput(x, y, z + 1, block, oldLevel, newLevel);
						handleDirectWireInput(x, y - 1, z - 1, block, oldLevel, newLevel);
						handleDirectWireInput(x, y - 1, z + 1, block, oldLevel, newLevel);
					}

					// Can be triggered from below
					handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);

					// Can be triggered from above
					handleDirectWireInput(x, y - 1, z, block, oldLevel, newLevel);
				}
				return;
			case REPEATER:
			case COMPARATOR:
				Directional diode = (Directional) block.getBlockData();
				BlockFace facing = diode.getFacing();
				handleDirectWireInput(x + facing.getModX(), y, z + facing.getModZ(), block, oldLevel, newLevel);
				if (block.getRelative(facing).getType() != Material.AIR) {
					handleDirectWireInput(x + facing.getModX(), y - 1, z + facing.getModZ(), block, oldLevel, newLevel);
					handleDirectWireInput(x + facing.getModX(), y + 1, z + facing.getModZ(), block, oldLevel, newLevel);
					handleDirectWireInput(x + facing.getModX() + 1, y - 1, z + facing.getModZ(), block, oldLevel, newLevel);
					handleDirectWireInput(x + facing.getModX() - 1, y - 1, z + facing.getModZ(), block, oldLevel, newLevel);
					handleDirectWireInput(x + facing.getModX() + 1, y - 1, z + facing.getModZ() + 1, block, oldLevel, newLevel);
					handleDirectWireInput(x + facing.getModX() - 1, y - 1, z + facing.getModZ() - 1, block, oldLevel, newLevel);
				}
				return;
			case ACACIA_BUTTON:
			case BIRCH_BUTTON:
			case DARK_OAK_BUTTON:
			case JUNGLE_BUTTON:
			case OAK_BUTTON:
			case SPRUCE_BUTTON:
			case STONE_BUTTON:
			case LEVER:
				Directional button = (Directional) block.getBlockData();
				if (button != null) {
					BlockFace face = button.getFacing().getOppositeFace();
					if (face != null)
						handleDirectWireInput(x + face.getModX() * 2, y + face.getModY() * 2, z + face.getModZ() * 2, block, oldLevel, newLevel);
				}
				break;
			case POWERED_RAIL:
			case ACTIVATOR_RAIL:
				return;
		}

		// For redstone wires and repeaters, the code already exited this method
		// Non-wire blocks proceed

		handleDirectWireInput(x - 1, y, z, block, oldLevel, newLevel);
		handleDirectWireInput(x + 1, y, z, block, oldLevel, newLevel);
		handleDirectWireInput(x - 1, y - 1, z, block, oldLevel, newLevel);
		handleDirectWireInput(x + 1, y - 1, z, block, oldLevel, newLevel);
		handleDirectWireInput(x, y, z - 1, block, oldLevel, newLevel);
		handleDirectWireInput(x, y, z + 1, block, oldLevel, newLevel);
		handleDirectWireInput(x, y - 1, z - 1, block, oldLevel, newLevel);
		handleDirectWireInput(x, y - 1, z + 1, block, oldLevel, newLevel);

		// Can be triggered from below
		handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);

		// Can be triggered from above
		handleDirectWireInput(x, y - 1, z, block, oldLevel, newLevel);
	}

	private static void handleDirectWireInput(int x, int y, int z, Block sourceBlock, int oldLevel, int newLevel) {

		Block block = sourceBlock.getWorld().getBlockAt(x, y, z);
		if (LocationUtils.locationsEqual(sourceBlock.getLocation(), block.getLocation())) //The same block, don't run.
			return;

		new SourcedBlockRedstoneEvent(sourceBlock, block, oldLevel, newLevel).callEvent();
	}

}
