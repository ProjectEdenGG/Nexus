package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
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
		XLYPHONE(Instrument.XYLOPHONE) {
			@Override
			int getPitchFromItemModel(ItemModelType itemModelType) {
				return switch (itemModelType) {
					case ItemModelType.SHULKER_RED -> 1;
					case ItemModelType.SHULKER_ORANGE -> 2;
					case ItemModelType.SHULKER_YELLOW -> 3;
					case ItemModelType.SHULKER_LIME -> 4;
					case ItemModelType.SHULKER_GREEN -> 5;
					case ItemModelType.SHULKER_CYAN -> 6;
					case ItemModelType.SHULKER_LIGHT_BLUE -> 7;
					case ItemModelType.SHULKER_BLUE -> 8;
					case ItemModelType.SHULKER_PURPLE -> 9;
					case ItemModelType.SHULKER_MAGENTA -> 10;
					case ItemModelType.SHULKER_PINK -> 11;
					case ItemModelType.SHULKER_BROWN -> 12;
					case ItemModelType.SHULKER_GRAY -> 13;
					case ItemModelType.SHULKER_LIGHT_GRAY -> 14;
					case ItemModelType.SHULKER_BLACK -> 15;
					case ItemModelType.SHULKER_WHITE -> 16;
					default -> 0;
				};
			}

			@Override
			ItemModelType getItemModelFromPitch(int pitch) {
				return switch (pitch) {
					case 1 -> ItemModelType.SHULKER_RED;
					case 2 -> ItemModelType.SHULKER_ORANGE;
					case 3 -> ItemModelType.SHULKER_YELLOW;
					case 4 -> ItemModelType.SHULKER_LIME;
					case 5 -> ItemModelType.SHULKER_GREEN;
					case 6 -> ItemModelType.SHULKER_CYAN;
					case 7 -> ItemModelType.SHULKER_LIGHT_BLUE;
					case 8 -> ItemModelType.SHULKER_BLUE;
					case 9 -> ItemModelType.SHULKER_PURPLE;
					case 10 -> ItemModelType.SHULKER_MAGENTA;
					case 11 -> ItemModelType.SHULKER_PINK;
					case 12 -> ItemModelType.SHULKER_BROWN;
					case 13 -> ItemModelType.SHULKER_GRAY;
					case 14 -> ItemModelType.SHULKER_LIGHT_GRAY;
					case 15 -> ItemModelType.SHULKER_BLACK;
					case 16 -> ItemModelType.SHULKER_WHITE;
					default -> null;
				};
			}
		},
		SNARE(Instrument.SNARE_DRUM) {
			@Override
			int getPitchFromItemModel(ItemModelType itemModelType) {
				return switch (itemModelType) {
					case ItemModelType.NEON_RED -> 1;
					case ItemModelType.NEON_ORANGE -> 2;
					case ItemModelType.NEON_YELLOW -> 3;
					case ItemModelType.NEON_LIME -> 4;
					case ItemModelType.NEON_GREEN -> 5;
					case ItemModelType.NEON_CYAN -> 6;
					case ItemModelType.NEON_LIGHT_BLUE -> 7;
					case ItemModelType.NEON_BLUE -> 8;
					case ItemModelType.NEON_PURPLE -> 9;
					case ItemModelType.NEON_MAGENTA -> 10;
					case ItemModelType.NEON_PINK -> 11;
					case ItemModelType.NEON_BROWN -> 12;
					case ItemModelType.NEON_GRAY -> 13;
					case ItemModelType.NEON_LIGHT_GRAY -> 14;
					case ItemModelType.NEON_BLACK -> 15;
					case ItemModelType.NEON_WHITE -> 16;
					default -> 0;
				};
			}

			@Override
			ItemModelType getItemModelFromPitch(int pitch) {
				return switch (pitch) {
					case 1 -> ItemModelType.NEON_RED;
					case 2 -> ItemModelType.NEON_ORANGE;
					case 3 -> ItemModelType.NEON_YELLOW;
					case 4 -> ItemModelType.NEON_LIME;
					case 5 -> ItemModelType.NEON_GREEN;
					case 6 -> ItemModelType.NEON_CYAN;
					case 7 -> ItemModelType.NEON_LIGHT_BLUE;
					case 8 -> ItemModelType.NEON_BLUE;
					case 9 -> ItemModelType.NEON_PURPLE;
					case 10 -> ItemModelType.NEON_MAGENTA;
					case 11 -> ItemModelType.NEON_PINK;
					case 12 -> ItemModelType.NEON_BROWN;
					case 13 -> ItemModelType.NEON_GRAY;
					case 14 -> ItemModelType.NEON_LIGHT_GRAY;
					case 15 -> ItemModelType.NEON_BLACK;
					case 16 -> ItemModelType.NEON_WHITE;
					default -> null;
				};
			}
		},
		;

		private final Instrument instrument;

		public static SupportedInstrument of(Block block) {
			var instrument = ((NoteBlock) block.getBlockData()).getInstrument();
			for (SupportedInstrument supported : values())
				if (supported.getInstrument() == instrument)
					return supported;
			return null;
		}

		public static SupportedInstrument of(ItemModelType itemModelType) {
			for (SupportedInstrument supported : values())
				if (supported.getPitchFromItemModel(itemModelType) != 0)
					return supported;
			return null;
		}

		abstract int getPitchFromItemModel(ItemModelType itemModelType);

		abstract ItemModelType getItemModelFromPitch(int pitch);
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

		if (Model.of(item) != null)
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

		ItemModelType model = instrument.getItemModelFromPitch(pitch);
		if (model == null)
			return;

		event.setCancelled(true);
		player.getInventory().setItemInMainHand(new ItemBuilder(item).model(model).build());
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
		
		ItemModelType modelId = ItemModelType.of(item);
		if (modelId == null)
			return;
		
		var instrument = SupportedInstrument.of(modelId);
		if (instrument == null)
			return;

		var pitch = instrument.getPitchFromItemModel(modelId);
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


