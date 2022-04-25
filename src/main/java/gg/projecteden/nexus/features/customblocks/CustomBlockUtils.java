package gg.projecteden.nexus.features.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.CustomBlockType;
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
import gg.projecteden.nexus.utils.NMSUtils.SoundType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.UUIDUtils;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class CustomBlockUtils {
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
			CustomBlock customBlock = CustomBlock.fromBlockData(blockData);
			if (customBlock == null) {
				debug("GetData: CustomBlock == null");
				return null;
			}

			debug("GetData: creating new data for " + customBlock.name());
			BlockFace facing = CustomBlockUtils.getFacing(customBlock, blockData);
			data = placeBlockDatabase(UUIDUtils.UUID0, customBlock, location, facing);
		}

		return data;
	}

	public static BlockFace getFacing(CustomBlock customBlock, BlockData blockData) {
		ICustomBlock iCustomBlock = customBlock.get();
		if (!(iCustomBlock instanceof IDirectional))
			return BlockFace.UP;

		CustomBlockType type = customBlock.getType();
		if (isFacing(BlockFace.NORTH, type, iCustomBlock, blockData))
			return BlockFace.NORTH;
		else if (isFacing(BlockFace.EAST, type, iCustomBlock, blockData))
			return BlockFace.EAST;

		return BlockFace.UP;
	}

	private static boolean isFacing(BlockFace face, CustomBlockType type, ICustomBlock customBlock, BlockData blockData) {
		switch (type) {
			case NOTE_BLOCK -> {
				NoteBlock noteBlock = (NoteBlock) blockData;
				Instrument instrument = noteBlock.getInstrument();
				int step = noteBlock.getNote().getId();

				ICustomNoteBlock customNoteBlock = (ICustomNoteBlock) customBlock;
				Instrument _instrument = customNoteBlock.getNoteBlockInstrument(face);
				int _step = customNoteBlock.getNoteBlockStep(face);

				return _instrument == instrument && _step == step;
			}
			case TRIPWIRE -> {
				Tripwire tripwire = (Tripwire) blockData;
				boolean north = tripwire.hasFace(BlockFace.NORTH);
				boolean south = tripwire.hasFace(BlockFace.SOUTH);
				boolean east = tripwire.hasFace(BlockFace.EAST);
				boolean west = tripwire.hasFace(BlockFace.WEST);
				boolean attached = tripwire.isAttached();
				boolean disarmed = tripwire.isDisarmed();
				boolean powered = tripwire.isPowered();

				ICustomTripwire customTripwire = (ICustomTripwire) customBlock;
				boolean _north = customTripwire.isNorth(face);
				boolean _south = customTripwire.isSouth(face);
				boolean _east = customTripwire.isEast(face);
				boolean _west = customTripwire.isWest(face);
				boolean _attached = customTripwire.isAttached(face);
				boolean _disarmed = customTripwire.isDisarmed(face);
				boolean _powered = customTripwire.isPowered(face);

				return north == _north
					&& south == _south
					&& east == _east
					&& west == _west
					&& attached == _attached
					&& disarmed == _disarmed
					&& powered == _powered;
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

	public static void playDefaultWoodSounds(Sound sound, Location location) {
		SoundType soundType = SoundType.fromSound(sound);
		if (soundType == null)
			return;

		playDefaultWoodSound(soundType, location);
	}

	public static void tryPlayDefaultWoodSound(SoundType soundType, Block block) {
		Sound sound = NMSUtils.getSound(soundType, block);
		if (sound == null)
			return;

		String blockSound = "custom." + sound.getKey().getKey();
		String woodSound = soundType.getCustomWoodSound();
		if (!blockSound.equalsIgnoreCase(woodSound)) {
//			Dev.WAKKA.send(blockSound + " == " + woodSound);
			return;
		}

		playDefaultWoodSound(soundType, block.getLocation());
	}

	private static void playDefaultWoodSound(SoundType soundType, Location location) {
		String soundKey = soundType.getCustomWoodSound();
		SoundBuilder soundBuilder = new SoundBuilder(soundKey).location(location).volume(soundType.getVolume());

		String locationStr = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
		String cooldownType = "CustomWoodSound_" + soundType + "_" + locationStr;
		if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK.x(4)))) {
			return;
		}

//		debug("DefaultWoodSound: " + soundType + " - " + soundKey);
		BlockUtils.playSound(soundBuilder);
	}


}
