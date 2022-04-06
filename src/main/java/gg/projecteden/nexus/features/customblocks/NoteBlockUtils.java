package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.events.NoteBlockPlayEvent;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class NoteBlockUtils {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	public static void placeBlock(Player player, Location location) {
		placeBlock(player.getUniqueId(), location);
	}

	public static NoteBlockData placeBlock(UUID uuid, Location location) {
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = new NoteBlockData(uuid, location.getBlock());
		tracker.put(location, data);
		trackerService.save(tracker);

		return data;
	}

	public static void breakBlock(Location location) {
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = tracker.get(location);
		if (!data.exists())
			return;

		tracker.remove(location);
		trackerService.save(tracker);
	}

	public static void changePitch(boolean sneaking, Location location, NoteBlockData data) {
		tracker = trackerService.fromWorld(location);

		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

//		data.setPowered(true);
		tracker.put(location, data);
		trackerService.save(tracker);

		play(location.getBlock(), data);

		// TODO, show instrument + note somewhere to the player?
	}

	public static void changeVolume(boolean sneaking, Location location, NoteBlockData data) {
		tracker = trackerService.fromWorld(location);

		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		play(location.getBlock(), data);

		tracker.put(location, data);
		trackerService.save(tracker);

	}

	public static void play(Block block) {
		play(block, getData(block));
	}

	public static void play(Block block, NoteBlockData data) {
		Location location = block.getLocation();
		Block above = block.getRelative(BlockFace.UP);

		String version = Bukkit.getMinecraftVersion();
		if (version.matches("1.19[.]?[0-9]*")) {
			if (MaterialTag.WOOL.isTagged(above) || MaterialTag.WOOL_CARPET.isTagged(above))
				return;
		} else if (!Nullables.isNullOrAir(above))
			return;

		// TODO: validate data?

		String cooldownType = "noteblock_" + block.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
		if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK))) {
			debug("NotePlayEvent: on cooldown, cancelling");
			return;
		}

		NoteBlockPlayEvent event = new NoteBlockPlayEvent(block);
		if (event.callEvent()) {
			debug("NotePlayEvent: playing note");
			data.play(location);
		}
	}

	public static NoteBlockData getData(Block block) {
		return getData(block, false);
	}

	@NotNull
	public static NoteBlockData getData(Block block, boolean reset) {
		Location location = block.getLocation();
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = tracker.get(location);
		if (!data.exists()) {
			debug("No data exists for that location, creating");
			data = placeBlock(UUIDUtils.UUID0, location);

			if (reset) {
				NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
				noteBlock.setInstrument(CustomBlock.NOTE_BLOCK.get().getNoteBlockInstrument());
				noteBlock.setNote(new Note(CustomBlock.NOTE_BLOCK.get().getNoteBlockStep()));
				block.setBlockData(noteBlock, false);
			}
		}

		return data;
	}

	public static String customSound(String instrument) {
		return "minecraft:custom.noteblock." + instrument;
	}
}
