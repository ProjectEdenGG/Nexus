package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.events.NoteBlockPlayEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;

public class NoteBlockUtils {

	public static void changePitch(boolean sneaking, Location location, NoteBlockData data) {
		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		data.setInteracted(true);

		Block block = location.getBlock();
		play(block, data);

		CustomBlockUtils.updatePowerable(block);

		// TODO, show instrument + note somewhere to the player?
	}


	public static void play(NoteBlock noteBlock, Location location, boolean interacted) {
		CustomBlockData data = CustomBlockUtils.getDataOrCreate(location, noteBlock);
		if (data == null)
			return;

		NoteBlockData noteBlockData = ((CustomNoteBlockData) data.getExtraData()).getNoteBlockData();

		if (interacted)
			noteBlockData.setInteracted(true);

		play(location.getBlock(), noteBlockData);
	}

	private static void play(Block block, NoteBlockData data) {
		Location location = block.getLocation();
		Block above = block.getRelative(BlockFace.UP);

		if (!Nullables.isNullOrAir(above)) {
			if (!MaterialTag.SKULLS.isTagged(above)) {
				return;
			}
		}

		Tasks.wait(2, () -> { // 2 is required
			if (!data.isPowered() && !data.isInteracted()) {
				return;
			}

			if (block.getType() != Material.NOTE_BLOCK)
				return;

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
