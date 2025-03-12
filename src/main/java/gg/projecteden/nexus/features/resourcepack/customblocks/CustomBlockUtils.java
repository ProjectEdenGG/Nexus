package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTab;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDirectional;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.ITall;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockTrackerService;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomBlockUtils {
	@Getter
	private static final Set<BlockFace> neighborFaces = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
			BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

	private static final CustomNoteBlockTrackerService trackerService = new CustomNoteBlockTrackerService();

	public static @Nullable NoteBlockData getNoteBlockData(Location location) {
		if (CustomBlock.from(location.getBlock()) != CustomBlock.NOTE_BLOCK)
			return null;

		CustomNoteBlockTracker tracker = trackerService.fromWorld(location);
		NoteBlockData data = tracker.get(location);
		if (data == null) {
			data = new NoteBlockData(location.getBlock());
			return placeNoteBlockInDatabase(location, data.getBlockData(location));
		}

		return data;
	}

	public static @Nullable NoteBlockData placeNoteBlockInDatabase(@NonNull Location location, @NonNull BlockData blockData) {
		CustomBlock customBlock = CustomBlock.from(blockData, location.getBlock().getRelative(BlockFace.DOWN));
		if (customBlock != CustomBlock.NOTE_BLOCK)
			return null;

		return placeNoteBlockInDatabase(location, new NoteBlockData(location.getBlock()));
	}

	private static @NonNull NoteBlockData placeNoteBlockInDatabase(@NonNull Location location, NoteBlockData data) {
		CustomNoteBlockTracker tracker = trackerService.fromWorld(location);
		tracker.put(location, data);
		trackerService.save(tracker);
		return data;
	}

	public static void breakNoteBlockInDatabase(@NonNull Location location) {
		CustomNoteBlockTracker tracker = trackerService.fromWorld(location);
		tracker.remove(location);
		trackerService.save(tracker);
	}

	//

	public static boolean equals(CustomBlock customBlock, BlockData blockData, boolean directional, Block underneath) {
		BlockFace facing = null;
		if (directional) {
			facing = CustomBlockUtils.getFacing(customBlock, blockData, underneath);
		}

		return isFacing(facing, customBlock, blockData, underneath);
	}

	public static BlockFace getFacing(CustomBlock customBlock, BlockData blockData, Block underneath) {
		ICustomBlock iCustomBlock = customBlock.get();
		if (!(iCustomBlock instanceof IDirectional))
			return BlockFace.UP;

		if (isFacing(BlockFace.NORTH, customBlock, blockData, underneath))
			return BlockFace.NORTH;
		else if (isFacing(BlockFace.EAST, customBlock, blockData, underneath))
			return BlockFace.EAST;

		return BlockFace.UP;
	}

	private static boolean isFacing(BlockFace face, CustomBlock customBlock, BlockData blockData, Block underneath) {
		switch (customBlock.getType()) {
			case NOTE_BLOCK -> {
				ICustomNoteBlock customNoteBlock = (ICustomNoteBlock) customBlock.get();
				return customNoteBlock.equals(blockData, face, underneath);
			}
			case TRIPWIRE -> {
				ICustomTripwire customTripwire = (ICustomTripwire) customBlock.get();
				return customTripwire.equals(blockData, face, underneath);
			}
		}
		return false;
	}

	//

	public static void updateObservers(Block origin, Player debugger) {
		for (BlockFace face : neighborFaces) {
			Block neighbor = origin.getRelative(face);
			if (neighbor.getType().equals(Material.OBSERVER)) {
				Powerable powerable = (Powerable) neighbor.getBlockData();
				if (powerable.isPowered())
					continue;

				updatePowerable(neighbor, debugger);
			}
		}
	}

	public static void updatePowerable(Block block, Player debugger) {
		CustomBlocksLang.debug(debugger, "Updating Powerable: " + block.getType());
		Powerable powerable = (Powerable) block.getBlockData();
		boolean isPowered = powerable.isPowered();
		powerable.setPowered(!isPowered);
		block.setBlockData(powerable, true);

		NMSUtils.applyPhysics(block);

		Tasks.wait(3, () -> {
			Powerable _powerable = (Powerable) block.getBlockData();
			_powerable.setPowered(isPowered);
			block.setBlockData(_powerable, true);
		});
	}

	public static void fixTripwireNearby(Player player, Block current, Set<Location> visited) {
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled())
			return;
		//

		for (BlockFace face : CustomBlockUtils.getNeighborFaces()) {
			Block neighbor = current.getRelative(face);
			Location location = neighbor.getLocation();

			if (visited.contains(location))
				continue;

			visited.add(location);

			if (Nullables.isNullOrAir(neighbor))
				continue;

			CustomBlock customBlock = CustomBlock.from(neighbor);
			if (customBlock == null || !(customBlock.get() instanceof ICustomTripwire))
				continue;

			Block underneath = neighbor.getRelative(BlockFace.DOWN);
			BlockFace facing = CustomBlockUtils.getFacing(customBlock, neighbor.getBlockData(), underneath);

			BlockData blockData = customBlock.get().getBlockData(facing, underneath);
			Tasks.wait(1, () -> player.sendBlockChange(location, blockData));

			fixTripwireNearby(player, neighbor, visited);
		}
	}

	public static void breakBlock(Block brokenBlock, CustomBlock brokenCustomBlock, Player player, ItemStack tool, boolean applyPhysics) {
		Block aboveBlock = brokenBlock.getRelative(BlockFace.UP);
		CustomBlock aboveCustomBlock = CustomBlock.from(aboveBlock);
		if (aboveCustomBlock != null && aboveCustomBlock.get() instanceof IRequireSupport)
			breakBlock(aboveBlock, aboveCustomBlock, player, tool, applyPhysics);

		int amount = 1;

		Set<Location> fixedTripwire = new HashSet<>(List.of(brokenBlock.getLocation()));

		if (brokenCustomBlock != null) {
			if (CustomBlock.TALL_SUPPORT == brokenCustomBlock) {
				CustomBlocksLang.debug(player, "Broke tall support");
				brokenCustomBlock.breakBlock(player, tool, brokenBlock, false, amount, true, true, applyPhysics);

				Block blockUnder = brokenBlock.getRelative(BlockFace.DOWN);
				CustomBlock under = CustomBlock.from(blockUnder);

				if (under != null) {
					fixedTripwire.add(blockUnder.getLocation());
					CustomBlocksLang.debug(player, "Underneath: " + under.name());
					under.breakBlock(player, tool, blockUnder, true, amount, false, true, applyPhysics);
					blockUnder.setType(Material.AIR);
				}

				CustomBlockUtils.fixTripwireNearby(player, brokenBlock, new HashSet<>(fixedTripwire));
				return;
			}

			if (brokenCustomBlock.get() instanceof IIncremental incremental) {
				CustomBlocksLang.debug(player, "Broke incremental, setting proper amount");
				amount = incremental.getIndex() + 1;

			} else if (brokenCustomBlock.get() instanceof ITall) {
				CustomBlocksLang.debug(player, "Broke isTall");

				if (CustomBlock.TALL_SUPPORT == aboveCustomBlock) {
					CustomBlocksLang.debug(player, "Breaking tall support above");

					aboveCustomBlock.breakBlock(player, tool, aboveBlock, false, amount, false, true, applyPhysics);
					aboveBlock.setType(Material.AIR);
				}
			}

			brokenCustomBlock.breakBlock(player, tool, brokenBlock, true, amount, true, true, applyPhysics);
		}

		CustomBlockUtils.fixTripwireNearby(player, brokenBlock, new HashSet<>(fixedTripwire));
	}

	public static String getBlockDataString(Tripwire tripwire) {
		return "&oTripwire:"
			+ " &fN=" + StringUtils.bool(tripwire.hasFace(BlockFace.NORTH))
			+ " &fE=" + StringUtils.bool(tripwire.hasFace(BlockFace.EAST))
			+ " &fS=" + StringUtils.bool(tripwire.hasFace(BlockFace.SOUTH))
			+ " &fW=" + StringUtils.bool(tripwire.hasFace(BlockFace.WEST))
			+ " &fAttached=" + StringUtils.bool(tripwire.isAttached())
			+ " &fDisarmed=" + StringUtils.bool(tripwire.isDisarmed())
			+ " &fPowered=" + StringUtils.bool(tripwire.isPowered());
	}

	public static CustomCreativeItem[] getCreativeCategories() {
		return new CustomCreativeItem[] { new CustomCreativeItem(CustomBlock.APPLE_CRATE) };
	}

	public static CustomCreativeItem[] getCreativeItems() {
		return Arrays.stream(CustomBlock.values())
			.filter(customBlock -> customBlock.getCreativeTab() != CustomBlockTab.NONE)
			// TODO: Disable tripwire customblocks
			.filter(customBlock -> !(customBlock.get() instanceof ICustomTripwire))
			//
			.map(CustomCreativeItem::new)
			.toList().toArray(new CustomCreativeItem[0]);
	}

	// CoreProtect
	public static void logPlacement(Player player, Block block, CustomBlock customBlock) {
		Nexus.getCoreProtectAPI().logPlacement(player.getName(), block.getLocation(), block.getType(), block.getBlockData());
	}

	public static void logRemoval(Player player, Location location, Block block, CustomBlock customBlock) {
		Nexus.getCoreProtectAPI().logRemoval(player.getName(), location, block.getType(), block.getBlockData());
	}

	public static void logRemoval(String source, Location location, Block block, CustomBlock customBlock) {
		Nexus.getCoreProtectAPI().logRemoval(source, location, block.getType(), block.getBlockData());
	}
}
