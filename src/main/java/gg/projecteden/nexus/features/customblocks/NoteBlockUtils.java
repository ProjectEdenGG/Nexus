package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.events.NoteBlockPlayEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Observer;

import java.util.Set;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class NoteBlockUtils {
	private static final Set<BlockFace> neighborFaces = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
		BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

	public static void changePitch(boolean sneaking, Location location, NoteBlockData data) {
		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		data.setInteracted(true);

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


	public static void play(NoteBlock noteBlock, Location location, boolean interacted) {
		debug("PlayNoteEvent: ");
		CustomBlockData data = CustomBlockUtils.getData(noteBlock, location);
		if (data == null || !data.isNoteBlock())
			return;

		if (interacted)
			data.getNoteBlockData().setInteracted(true);

		play(location.getBlock(), data.getNoteBlockData());
	}

	private static void play(Block block, NoteBlockData data) {
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

			String cooldownType = "noteblock_" + location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
			if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK))) {
				return;
			}

			NoteBlockPlayEvent event = new NoteBlockPlayEvent(block);
			if (event.callEvent()) {
				data.play(location);
			}
		});
	}
}
