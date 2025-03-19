package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.events.NoteBlockChangePitchEvent;
import gg.projecteden.nexus.features.resourcepack.customblocks.events.NoteBlockPlayEvent;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.NonNull;
import net.minecraft.stats.Stats;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CustomBlockConfig(
	name = "Note Block",
	itemModel = ItemModelType.NOTE_BLOCK
)
@CustomNoteBlockConfig(
	instrument = Instrument.PIANO,
	step = 0
)
public class NoteBlock implements ICustomNoteBlock {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(Material.NOTE_BLOCK);
	}

	@Override
	public double getBlockHardness() {
		return 0.8;
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_AXE;
	}

	@Override
	public boolean onRightClickedWithItem(Player player, CustomBlock customBlock, Block block, ItemStack itemInHand) {
		if (player.isSneaking())
			return false;

		return onRightClickedWithoutItem(player, customBlock, block);
	}

	@Override
	public boolean onRightClickedWithoutItem(Player player, CustomBlock customBlock, Block block) {
		CustomBlockUtils.debug(player, "&e- is changing pitch");
		Location location = block.getLocation();
		NoteBlockData data = new NoteBlockData(block);
		NoteBlockChangePitchEvent event = new NoteBlockChangePitchEvent(player, location.getBlock());
		if (event.callEvent()) {
			NMSUtils.toNMS(player).awardStat(Stats.TUNE_NOTEBLOCK);
			changePitch(player, player.isSneaking(), location, data);
			changePitch(player, player.isSneaking(), location, data);
			CustomBlockUtils.debug(player, "&a<- changed pitch");
			return true;
		}

		return false;
	}

	@Override
	public boolean onLeftClickedWithItem(Player player, CustomBlock customBlock, Block block, ItemStack itemInHand) {
		return onLeftClickedWithoutItem(player, customBlock, block);
	}

	@Override
	public boolean onLeftClickedWithoutItem(Player player, CustomBlock customBlock, Block block) {
		CustomBlockUtils.debug(player, "&e<- is playing note");
		play(block.getLocation(), true, player);
		return true;
	}

	public static void changePitch(Player player, boolean sneaking, Location location, NoteBlockData data) {
		int oldStep = data.getStep();
		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		data.setInteracted(true);

		Block block = location.getBlock();
		CustomBlockUtils.debug(player, "change pitch from " + oldStep + " to " + +data.getStep());
		play(block, data, player);

		CustomBlockUtils.updatePowerable(block, player);
		ActionBarUtils.sendActionBar(player, "Instrument: " + data.getInstrumentName() + " | " + " Note: " + data.getStep());

	}

	public static void play(Location location, boolean interacted, Player debugger) {
		NoteBlockData data = new NoteBlockData(location.getBlock());
		if (data.isPowered())
			return;

		if (interacted)
			data.setInteracted(true);

		data.setPowered(true);

		CustomBlockUtils.debug(debugger, "NoteBlockUtils#play: Instrument=" + data.getInstrument() + ", Note=" + data.getStep() + ", Powered=" + data.isPowered());
		play(location.getBlock(), data, debugger);
	}

	private static void play(Block block, NoteBlockData data, Player debugger) {
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
				CustomBlockUtils.debug(debugger, "play event: Instrument=" + data.getInstrument() + ", Note=" + data.getStep() + ", Powered=" + data.isPowered());
				data.play(location, debugger);
			}
		});
	}
}
