package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.events.NoteBlockPlayEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;

public class NoteBlockUtils {

	public static void changePitch(Player player, boolean sneaking, Location location, NoteBlockData data) {
		int oldStep = data.getStep();
		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		data.setInteracted(true);

		Block block = location.getBlock();
		CustomBlocksLang.debug("change pitch from " + oldStep + " to " + +data.getStep());
		play(block, data);

		CustomBlockUtils.updatePowerable(block);
		ActionBarUtils.sendActionBar(player, "Instrument: " + data.getInstrumentName() + " | " + " Note: " + data.getStep());
	}


	public static void play(NoteBlock noteBlock, Location location, boolean interacted) {
		NoteBlockData data = CustomBlockUtils.getNoteBlockData(location);
		if (data == null)
			return;

		if (data.isPowered())
			return;

		if (interacted)
			data.setInteracted(true);

		data.setPowered(true);

		CustomBlocksLang.debug("NoteBlockUtils#play: Instrument=" + data.getInstrument() + ", Note=" + data.getStep() + ", Powered=" + data.isPowered());
		play(location.getBlock(), data);
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
				CustomBlocksLang.debug("play event: Instrument=" + data.getInstrument() + ", Note=" + data.getStep() + ", Powered=" + data.isPowered());
				data.play(location);
			}
		});
	}
}
