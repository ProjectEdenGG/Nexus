package gg.projecteden.nexus.features.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.IDirectional;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.ITall;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockData;
import gg.projecteden.nexus.models.customblock.CustomTripwireData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tool;
import gg.projecteden.nexus.utils.Tool.ToolGrade;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class CustomBlockUtils {
	@Getter
	private static final Set<BlockFace> neighborFaces = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
		BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;

	public static CustomBlockData placeBlockDatabase(@NonNull UUID uuid, @NonNull CustomBlock customBlock, @NonNull Location location, BlockFace facing) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = new CustomBlockData(uuid, customBlock.get().getModelId(), customBlock.getType());
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

	public static @Nullable CustomBlockData getData(@NonNull BlockData blockData, Location location) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = tracker.get(location);
		if (!data.exists()) {
			CustomBlock customBlock = CustomBlock.fromBlockData(blockData, location.getBlock().getRelative(BlockFace.DOWN));
			if (customBlock == null) {
				debug("GetData: CustomBlock == null");
				return null;
			}

			debug("GetData: creating new data for " + customBlock.name());
			BlockFace facing = CustomBlockUtils.getFacing(customBlock, blockData, location.getBlock().getRelative(BlockFace.DOWN));
			data = placeBlockDatabase(UUIDUtils.UUID0, customBlock, location, facing);
		}

		return data;
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
		debug("Updating Powerable: " + block.getType());
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
		for (BlockFace face : CustomBlockUtils.getNeighborFaces()) {
			Block neighbor = current.getRelative(face);
			Location location = neighbor.getLocation();

			if (visited.contains(location))
				continue;

			visited.add(location);

			if (Nullables.isNullOrAir(neighbor))
				continue;

			CustomBlock customBlock = CustomBlock.fromBlock(neighbor);
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
		CustomBlock aboveCustomBlock = CustomBlock.fromBlock(aboveBlock);
		if (aboveCustomBlock != null && aboveCustomBlock.get() instanceof IRequireSupport)
			breakBlock(aboveBlock, aboveCustomBlock, player, tool);

		int amount = 1;

		Set<Location> fixedTripwire = new HashSet<>(List.of(brokenBlock.getLocation()));

		if (brokenCustomBlock != null) {
			if (CustomBlock.TALL_SUPPORT == brokenCustomBlock) {
				debug("Broke tall support");
				brokenCustomBlock.breakBlock(player, tool, brokenBlock, false, amount, true, true);

				Block blockUnder = brokenBlock.getRelative(BlockFace.DOWN);
				CustomBlock under = CustomBlock.fromBlock(blockUnder);

				if (under != null) {
					fixedTripwire.add(blockUnder.getLocation());
					debug("Underneath: " + under.name());
					under.breakBlock(player, tool, blockUnder, true, amount, false, true);
					blockUnder.setType(Material.AIR);
				}

				CustomBlockUtils.fixTripwireNearby(player, brokenBlock, new HashSet<>(fixedTripwire));
				return;
			}

			if (brokenCustomBlock.get() instanceof IIncremental incremental) {
				amount = incremental.getIndex() + 1;

			} else if (brokenCustomBlock.get() instanceof ITall) {
				debug("Broke isTall");

				if (CustomBlock.TALL_SUPPORT == aboveCustomBlock) {
					debug("Breaking tall support above");

					aboveCustomBlock.breakBlock(player, tool, aboveBlock, false, amount, false, true);
					aboveBlock.setType(Material.AIR);
				}
			}

			brokenCustomBlock.breakBlock(player, tool, brokenBlock, true, amount, true, true);
		}

		CustomBlockUtils.fixTripwireNearby(player, brokenBlock, new HashSet<>(fixedTripwire));
	}

	public static boolean isAcceptableTool(ItemStack tool, Material minimumTool) {
		List<Material> acceptable = new ArrayList<>();
		acceptable.add(minimumTool);
		if (minimumTool == Material.AIR)
			return true;

		if (minimumTool == Material.SHEARS || MaterialTag.SWORDS.isTagged(minimumTool))
			acceptable.add(Material.AIR);

		ToolGrade grade = ToolGrade.of(tool);
		Tool minimumToolType = Tool.of(minimumTool);
		if (grade == null || minimumToolType == null)
			return acceptable.contains(tool.getType());

		List<ToolGrade> higherGrades = grade.getHigherToolGrades();
		acceptable.addAll(minimumToolType.getTools(higherGrades));

		return acceptable.contains(tool.getType());
	}
}
