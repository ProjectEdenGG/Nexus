package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
public class TempCustomBlocks extends Feature implements Listener {
	private static final int PITCHES = 24;

	@Getter
	@AllArgsConstructor
	public enum SupportedInstrument {
		XLYPHONE(Instrument.XYLOPHONE),
		SNARE(Instrument.SNARE_DRUM),
		;

		private final Instrument instrument;

		public static SupportedInstrument of(Block block) {
			var instrument = ((NoteBlock) block.getBlockData()).getInstrument();
			for (SupportedInstrument supported : values())
				if (supported.getInstrument() == instrument)
					return supported;
			return null;
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick())
			return;

		var item = event.getItem();
		if (isNullOrAir(item))
			return;

		if (item.getType() != Material.NOTE_BLOCK)
			return;

		if (ModelId.of(item) != 0)
			return;

		var block = event.getClickedBlock();
		if (isNullOrAir(block))
			return;

		if (block.getType() != Material.NOTE_BLOCK)
			return;

		var instrument = SupportedInstrument.of(block);
		if (instrument == null)
			return;

		var player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE)
			return;

		if (!Rank.of(player).isStaff())
			return;

		var noteBlock = (NoteBlock) block.getBlockData();
		var pitch = noteBlock.getNote().getId();
		if (pitch == 0)
			return;

		var modelId = (instrument.ordinal() * PITCHES) + pitch;

		event.setCancelled(true);
		player.getInventory().setItemInMainHand(new ItemBuilder(item).modelId(modelId).build());
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		var player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE)
			return;
		
		if (!Rank.of(player).isStaff())
			return;
		
		var item = event.getItemInHand();
		if (isNullOrAir(item))
			return;
		
		if (item.getType() != Material.NOTE_BLOCK)
			return;
		
		int modelId = ModelId.of(item);
		if (modelId == 0)
			return;
		
		var ordinal = modelId / PITCHES;
		if (ordinal < 0 || ordinal >= SupportedInstrument.values().length)
			return;
		
		var instrument = SupportedInstrument.values()[ordinal];
		var pitch = modelId % PITCHES;
		if (pitch == 0)
			return;
		
		event.setCancelled(true);
		Tasks.wait(1, () -> event.getBlock().setBlockData(Material.NOTE_BLOCK.createBlockData(blockData -> {
			NoteBlock noteBlock = (NoteBlock) blockData;
			noteBlock.setInstrument(instrument.getInstrument());
			noteBlock.setNote(new Note(pitch));
		}), false));
	}
}


