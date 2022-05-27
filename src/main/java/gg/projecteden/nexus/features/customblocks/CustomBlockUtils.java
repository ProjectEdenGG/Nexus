package gg.projecteden.nexus.features.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundType;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.IDirectional;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockData;
import gg.projecteden.nexus.models.customblock.CustomTripwireData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.UUIDUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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

	public static void unlockRecipe(Player player, Material material) {
		if (Nullables.isNullOrAir(material))
			return;

		for (CustomBlock customBlock : CustomBlock.values()) {
			if (!(customBlock.get() instanceof ICraftableNoteBlock craftable))
				continue;

			Material unlockMaterial = craftable.getRecipeUnlockMaterial();
			if (Nullables.isNullOrAir(unlockMaterial))
				continue;

			if (unlockMaterial.equals(material)) {
				for (NexusRecipe recipe : customBlock.getRecipes()) {
					Keyed keyedRecipe = (Keyed) recipe.getRecipe();
					player.discoverRecipe(keyedRecipe.getKey());
				}
			}
		}
	}

	public static boolean tryPlayDefaultSound(SoundAction soundAction, Block block) {
		Sound sound = NMSUtils.getSound(soundAction, block);
		if (sound == null)
			return false;

		SoundType soundType = SoundType.fromSound(sound);
		if (soundType == null)
			return false;

		String blockSound = "custom." + sound.getKey().getKey();
		String defaultSound = soundAction.getCustomSound(soundType);
		if (!blockSound.equalsIgnoreCase(defaultSound)) {
			return false;
		}

		return playDefaultSound(soundAction, soundType, block.getLocation());
	}

	public static boolean playDefaultSounds(Sound sound, Location location) {
		SoundAction soundAction = SoundAction.fromSound(sound);
		if (soundAction == null)
			return false;

		SoundType soundType = SoundType.fromSound(sound);
		if (soundType == null)
			return false;

		return playDefaultSound(soundAction, soundType, location);
	}

	private static boolean playDefaultSound(SoundAction soundAction, SoundType soundType, Location location) {
		String soundKey = soundAction.getCustomSound(soundType);
		SoundBuilder soundBuilder = new SoundBuilder(soundKey).location(location).volume(soundAction.getVolume());

		String locationStr = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
		String cooldownType = "CustomDefaultSound_" + soundAction + "_" + locationStr;
		if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK.x(3)))) {
			return false;
		}

//		debug("&6CustomDefaultSound:&f " + soundAction + " - " + soundKey);
		BlockUtils.playSound(soundBuilder);
		return true;
	}

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
}
