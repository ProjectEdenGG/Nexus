package gg.projecteden.nexus.features.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
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
			case TRIPWIRE -> data.setExtraData(new CustomTripwireData());
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
			if (blockData instanceof NoteBlock noteBlock) {
				BlockFace facing = CustomBlockUtils.getFacing(customBlock.getNoteBlock(), noteBlock);
				data = placeBlockDatabase(UUIDUtils.UUID0, customBlock, location, facing);
			} else if (blockData instanceof Tripwire tripwire) {
				data = placeBlockDatabase(UUIDUtils.UUID0, customBlock, location, null);
			}

		}

		return data;
	}

	public static BlockFace getFacing(ICustomNoteBlock customNoteBlock, NoteBlock noteBlock) {
		if (customNoteBlock instanceof IDirectionalNoteBlock)
			return BlockFace.UP;

		if (isFacing(BlockFace.NORTH, customNoteBlock, noteBlock))
			return BlockFace.NORTH;
		else if (isFacing(BlockFace.EAST, customNoteBlock, noteBlock))
			return BlockFace.EAST;

		return BlockFace.UP;
	}

	private static boolean isFacing(BlockFace face, ICustomNoteBlock customNoteBlock, NoteBlock noteBlock) {
		Instrument instrument = noteBlock.getInstrument();
		int step = noteBlock.getNote().getId();

		return customNoteBlock.getNoteBlockInstrument(face) == instrument && customNoteBlock.getNoteBlockStep(face) == step;
	}

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
