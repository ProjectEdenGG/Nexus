package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.events.NoteBlockPlayEvent;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.models.customblock.NoteBlockTracker;
import gg.projecteden.nexus.models.customblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Observer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class NoteBlockUtils {
	private static final Set<BlockFace> neighborFaces = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
	private static final NoteBlockTrackerService noteBlockTrackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker noteBlockTracker;

	public static NoteBlockData placeBlockDatabase(UUID uuid, Location location) {
		noteBlockTracker = noteBlockTrackerService.fromWorld(location);
		NoteBlockData data = new NoteBlockData(uuid, location.getBlock());

		noteBlockTracker.put(location, data);
		noteBlockTrackerService.save(noteBlockTracker);

		return data;
	}

	public static void breakBlock(Location location) {
		noteBlockTracker = noteBlockTrackerService.fromWorld(location);
		NoteBlockData data = noteBlockTracker.get(location);
		if (!data.exists())
			return;

		noteBlockTracker.remove(location);
		noteBlockTrackerService.save(noteBlockTracker);
	}

	public static void changePitch(boolean sneaking, Location location, NoteBlockData data) {
		noteBlockTracker = noteBlockTrackerService.fromWorld(location);

		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		data.setInteracted(true);

		noteBlockTracker.put(location, data);
		noteBlockTrackerService.save(noteBlockTracker);

		Block block = location.getBlock();
		play(block, data);

		// update observers (MOSTLY WORKS)
		//	-  a block on top of a block behind an observer doesn't get updated: https://i.imgur.com/LFJ4RTW.png
		boolean exists = false;
		for (BlockFace face : neighborFaces) {
			Block neighbor = block.getRelative(face);
			if (neighbor.getType().equals(Material.OBSERVER)) {
				exists = true;

				Observer observer = (Observer) neighbor.getBlockData();
				Block facingBlock = neighbor.getRelative(observer.getFacing());
				if (!facingBlock.getLocation().equals(block.getLocation()))
					continue;

				if (!observer.isPowered()) {
					observer.setPowered(true);
					neighbor.setBlockData(observer, true);
					neighbor.getState().update(true);

					Tasks.wait(2, () -> {
						observer.setPowered(false);
						neighbor.setBlockData(observer, true);
						neighbor.getState().update(true);
					});
				}
			}
		}
		if (exists)
			block.getState().update(true);

		// TODO, show instrument + note somewhere to the player?
	}

	public static void changeVolume(boolean sneaking, Location location, NoteBlockData data) {
		noteBlockTracker = noteBlockTrackerService.fromWorld(location);

		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		data.setInteracted(true);

		noteBlockTracker.put(location, data);
		noteBlockTrackerService.save(noteBlockTracker);

		play(location.getBlock(), data);
	}


	public static void play(Block block, boolean interacted) {
		NoteBlockData data = getData(block);
		if (interacted)
			data.setInteracted(true);

		play(block, data);
	}

	public static void play(Block block, NoteBlockData data) {
		Location location = block.getLocation();
		Block above = block.getRelative(BlockFace.UP);

		// TODO: 1.19
		String version = Bukkit.getMinecraftVersion();
		if (version.matches("1.19[.]?[0-9]*")) {
			if (MaterialTag.WOOL.isTagged(above) || MaterialTag.WOOL_CARPET.isTagged(above))
				return;
		} else if (!Nullables.isNullOrAir(above))
			return;

		Tasks.wait(1, () -> {
			if (!data.isPowered() && !data.isInteracted()) {
				return;
			}

			String cooldownType = "noteblock_" + block.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
			if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK))) {
				return;
			}

			NoteBlockPlayEvent event = new NoteBlockPlayEvent(block);
			if (event.callEvent()) {
				data.play(location);
			}
		});
	}

	public static @NotNull NoteBlockData getData(Block block) {
		return getData(block, false);
	}


	public static @NotNull NoteBlockData getData(Block block, boolean reset) {
		Location location = block.getLocation();
		noteBlockTracker = noteBlockTrackerService.fromWorld(location);
		NoteBlockData data = noteBlockTracker.get(location);
		if (!data.exists()) {
			debug("No note block data exists for that location, creating");
			data = placeBlockDatabase(UUIDUtils.UUID0, location);

			if (reset) {
				NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
				noteBlock.setInstrument(CustomBlock.NOTE_BLOCK.get().getNoteBlockInstrument());
				noteBlock.setNote(new Note(CustomBlock.NOTE_BLOCK.get().getNoteBlockStep()));
				block.setBlockData(noteBlock, false);
			}
		}

		debug("got the data");
		return data;
	}

	public static String customSound(String instrument) {
		return "minecraft:custom.noteblock." + instrument;
	}
}
