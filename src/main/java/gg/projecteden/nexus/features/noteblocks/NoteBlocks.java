package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.features.noteblocks.events.NoteBlockPlayEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/*
	TODO:
		Bugs:
			Some redstone interaction with noteblocks causes the noteblock to play multiple times, when it shouldn't
		Custom Block Handling
			- Block playing/changing note blocks that aren't Piano 0
			- When breaking, drop that custom block item instead
 */
@Environments(Env.TEST)
public class NoteBlocks extends Feature {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	@Override
	public void onStart() {
		new NoteBlocksListener();
	}

	public static NoteBlockData put(UUID uuid, Location location) {
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = new NoteBlockData(uuid, location.getBlock());
		tracker.put(location, data);
		trackerService.save(tracker);

		return data;
	}

	public static NoteBlockData put(Player player, Location location) {
		return put(player.getUniqueId(), location);
	}

	public static void remove(Location location) {
		tracker = trackerService.fromWorld(location);
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

	public static String customSound(String instrument) {
		return "minecraft:custom.noteblock." + instrument;
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

//		new NotePlayEvent(block, Instrument.PIANO, new Note(0)).callEvent();
	}

	public static void debug(String message) {
		List<Dev> devs = List.of(Dev.WAKKA, Dev.GRIFFIN);
		for (Dev dev : devs) {
			if (dev.isOnline())
				dev.send(message);
		}

	}
}
