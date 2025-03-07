package gg.projecteden.nexus.features.resourcepack.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.api.common.utils.UUIDUtils;
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
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockData;
import gg.projecteden.nexus.models.customblock.CustomTripwireData;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CustomBlockUtils {
	@Getter
	private static final Set<BlockFace> neighborFaces = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
			BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;

	public static CustomBlockData placeBlockDatabaseAsServer(@NonNull CustomBlock customBlock, @NonNull Location location, BlockFace facing) {
		return placeBlockDatabase(UUIDUtils.UUID0, customBlock, location, facing);
	}

	public static CustomBlockData placeBlockDatabase(@NonNull UUID uuid, @NonNull CustomBlock customBlock, @NonNull Location location, BlockFace facing) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = new CustomBlockData(uuid, customBlock.get().getModel(), customBlock.getType());
		switch (customBlock.getType()) {
			case NOTE_BLOCK -> {
				CustomNoteBlockData extraData = new CustomNoteBlockData(facing);
				extraData.setNoteBlockData(new NoteBlockData(location.getBlock()));
				data.setExtraData(extraData);
			}
			case TRIPWIRE -> data.setExtraData(new CustomTripwireData(facing));
		}

		tracker.put(location, data);
		trackerService.save(tracker);
		return data;
	}

	public static void breakBlockDatabase(Location location) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = tracker.get(location);
		if (!data.exists())
			return;

		tracker.remove(location);
		trackerService.save(tracker);
	}

	public static void pistonMove(Block piston, Map<CustomBlockData, Pair<Location, Location>> blocksToMove) {
		if (blocksToMove.isEmpty())
			return;

		tracker = trackerService.fromWorld(piston.getWorld());
		Map<CustomBlockData, Location> currentLocMap = new HashMap<>();
		Map<CustomBlockData, Location> newLocMap = new HashMap<>();

		for (CustomBlockData data : blocksToMove.keySet()) {
			currentLocMap.put(data, blocksToMove.get(data).getFirst());
			newLocMap.put(data, blocksToMove.get(data).getSecond());
		}

		// remove
		for (CustomBlockData data : currentLocMap.keySet()) {
			Location currentLocation = currentLocMap.get(data);
			tracker.remove(currentLocation);
		}

		// place
		for (CustomBlockData data : newLocMap.keySet()) {
			Location newLocation = newLocMap.get(data);
			tracker.put(newLocation, data);
		}

		trackerService.save(tracker);
	}

	public static @Nullable CustomBlockData getDataOrCreate(@NonNull Location location, @NonNull BlockData blockData) {
		CustomBlockData data = getData(location);
		if (!data.exists())
			data = createData(location, blockData);

		return data;
	}

	public static @NonNull CustomBlockData getData(@NonNull Location location) {
		return trackerService.fromWorld(location).get(location);
	}

	public static @Nullable CustomBlockData createData(@NonNull Location location, @NonNull BlockData blockData) {
		CustomBlock customBlock = CustomBlock.from(blockData, location.getBlock().getRelative(BlockFace.DOWN));
		if (customBlock == null) {
			CustomBlocksLang.debug("CreateData: CustomBlock == null");
			return null;
		}

		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && customBlock.get() instanceof ICustomTripwire)
			return null;
		//

		BlockFace facing = CustomBlockUtils.getFacing(customBlock, blockData, location.getBlock().getRelative(BlockFace.DOWN));
		return createData(location, customBlock, facing);
	}

	public static @NotNull CustomBlockData createData(@NonNull Location location, @NonNull CustomBlock customBlock, BlockFace facing) {
		CustomBlocksLang.debug("CreateData: creating new data for " + customBlock.name() + " at " + StringUtils.getShortLocationString(location));
		CustomBlocksLang.debug("---");
		return placeBlockDatabaseAsServer(customBlock, location, facing);
	}

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

	public static void updateObservers(Block origin) {
		for (BlockFace face : neighborFaces) {
			Block neighbor = origin.getRelative(face);
			if (neighbor.getType().equals(Material.OBSERVER)) {
				Powerable powerable = (Powerable) neighbor.getBlockData();
				if (powerable.isPowered())
					continue;

				updatePowerable(neighbor);
			}
		}
	}

	public static void updatePowerable(Block block) {
		CustomBlocksLang.debug("Updating Powerable: " + block.getType());
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

	public static void breakBlock(Block brokenBlock, CustomBlock brokenCustomBlock, Player player, ItemStack tool) {
		Block aboveBlock = brokenBlock.getRelative(BlockFace.UP);
		CustomBlock aboveCustomBlock = CustomBlock.from(aboveBlock);
		if (aboveCustomBlock != null && aboveCustomBlock.get() instanceof IRequireSupport)
			breakBlock(aboveBlock, aboveCustomBlock, player, tool);

		int amount = 1;

		Set<Location> fixedTripwire = new HashSet<>(List.of(brokenBlock.getLocation()));

		if (brokenCustomBlock != null) {
			if (CustomBlock.TALL_SUPPORT == brokenCustomBlock) {
				CustomBlocksLang.debug("Broke tall support");
				brokenCustomBlock.breakBlock(player, tool, brokenBlock, false, amount, true, true);

				Block blockUnder = brokenBlock.getRelative(BlockFace.DOWN);
				CustomBlock under = CustomBlock.from(blockUnder);

				if (under != null) {
					fixedTripwire.add(blockUnder.getLocation());
					CustomBlocksLang.debug("Underneath: " + under.name());
					under.breakBlock(player, tool, blockUnder, true, amount, false, true);
					blockUnder.setType(Material.AIR);
				}

				CustomBlockUtils.fixTripwireNearby(player, brokenBlock, new HashSet<>(fixedTripwire));
				return;
			}

			if (brokenCustomBlock.get() instanceof IIncremental incremental) {
				CustomBlocksLang.debug("Broke incremental, setting proper amount");
				amount = incremental.getIndex() + 1;

			} else if (brokenCustomBlock.get() instanceof ITall) {
				CustomBlocksLang.debug("Broke isTall");

				if (CustomBlock.TALL_SUPPORT == aboveCustomBlock) {
					CustomBlocksLang.debug("Breaking tall support above");

					aboveCustomBlock.breakBlock(player, tool, aboveBlock, false, amount, false, true);
					aboveBlock.setType(Material.AIR);
				}
			}

			brokenCustomBlock.breakBlock(player, tool, brokenBlock, true, amount, true, true);
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
}
