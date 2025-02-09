package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
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
			int getPitchFromCustomMaterial(CustomMaterial customMaterial) {
				return switch (customMaterial) {
					case CustomMaterial.SHULKER_RED -> 1;
					case CustomMaterial.SHULKER_ORANGE -> 2;
					case CustomMaterial.SHULKER_YELLOW -> 3;
					case CustomMaterial.SHULKER_LIME -> 4;
					case CustomMaterial.SHULKER_GREEN -> 5;
					case CustomMaterial.SHULKER_CYAN -> 6;
					case CustomMaterial.SHULKER_LIGHT_BLUE -> 7;
					case CustomMaterial.SHULKER_BLUE -> 8;
					case CustomMaterial.SHULKER_PURPLE -> 9;
					case CustomMaterial.SHULKER_MAGENTA -> 10;
					case CustomMaterial.SHULKER_PINK -> 11;
					case CustomMaterial.SHULKER_BROWN -> 12;
					case CustomMaterial.SHULKER_GRAY -> 13;
					case CustomMaterial.SHULKER_LIGHT_GRAY -> 14;
					case CustomMaterial.SHULKER_BLACK -> 15;
					case CustomMaterial.SHULKER_WHITE -> 16;
					default -> 0;
				};
			}

			@Override
			CustomMaterial getCustomMaterialFromPitch(int pitch) {
				return switch (pitch) {
					case 1 -> CustomMaterial.SHULKER_RED;
					case 2 -> CustomMaterial.SHULKER_ORANGE;
					case 3 -> CustomMaterial.SHULKER_YELLOW;
					case 4 -> CustomMaterial.SHULKER_LIME;
					case 5 -> CustomMaterial.SHULKER_GREEN;
					case 6 -> CustomMaterial.SHULKER_CYAN;
					case 7 -> CustomMaterial.SHULKER_LIGHT_BLUE;
					case 8 -> CustomMaterial.SHULKER_BLUE;
					case 9 -> CustomMaterial.SHULKER_PURPLE;
					case 10 -> CustomMaterial.SHULKER_MAGENTA;
					case 11 -> CustomMaterial.SHULKER_PINK;
					case 12 -> CustomMaterial.SHULKER_BROWN;
					case 13 -> CustomMaterial.SHULKER_GRAY;
					case 14 -> CustomMaterial.SHULKER_LIGHT_GRAY;
					case 15 -> CustomMaterial.SHULKER_BLACK;
					case 16 -> CustomMaterial.SHULKER_WHITE;
					default -> null;
				};
			}
		},
		SNARE(Instrument.SNARE_DRUM) {
			@Override
			int getPitchFromCustomMaterial(CustomMaterial customMaterial) {
				return switch (customMaterial) {
					case CustomMaterial.NEON_RED -> 1;
					case CustomMaterial.NEON_ORANGE -> 2;
					case CustomMaterial.NEON_YELLOW -> 3;
					case CustomMaterial.NEON_LIME -> 4;
					case CustomMaterial.NEON_GREEN -> 5;
					case CustomMaterial.NEON_CYAN -> 6;
					case CustomMaterial.NEON_LIGHT_BLUE -> 7;
					case CustomMaterial.NEON_BLUE -> 8;
					case CustomMaterial.NEON_PURPLE -> 9;
					case CustomMaterial.NEON_MAGENTA -> 10;
					case CustomMaterial.NEON_PINK -> 11;
					case CustomMaterial.NEON_BROWN -> 12;
					case CustomMaterial.NEON_GRAY -> 13;
					case CustomMaterial.NEON_LIGHT_GRAY -> 14;
					case CustomMaterial.NEON_BLACK -> 15;
					case CustomMaterial.NEON_WHITE -> 16;
					default -> 0;
				};
			}

			@Override
			CustomMaterial getCustomMaterialFromPitch(int pitch) {
				return switch (pitch) {
					case 1 -> CustomMaterial.NEON_RED;
					case 2 -> CustomMaterial.NEON_ORANGE;
					case 3 -> CustomMaterial.NEON_YELLOW;
					case 4 -> CustomMaterial.NEON_LIME;
					case 5 -> CustomMaterial.NEON_GREEN;
					case 6 -> CustomMaterial.NEON_CYAN;
					case 7 -> CustomMaterial.NEON_LIGHT_BLUE;
					case 8 -> CustomMaterial.NEON_BLUE;
					case 9 -> CustomMaterial.NEON_PURPLE;
					case 10 -> CustomMaterial.NEON_MAGENTA;
					case 11 -> CustomMaterial.NEON_PINK;
					case 12 -> CustomMaterial.NEON_BROWN;
					case 13 -> CustomMaterial.NEON_GRAY;
					case 14 -> CustomMaterial.NEON_LIGHT_GRAY;
					case 15 -> CustomMaterial.NEON_BLACK;
					case 16 -> CustomMaterial.NEON_WHITE;
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

		public static SupportedInstrument of(CustomMaterial customMaterial) {
			for (SupportedInstrument supported : values())
				if (supported.getPitchFromCustomMaterial(customMaterial) != 0)
					return supported;
			return null;
		}

		abstract int getPitchFromCustomMaterial(CustomMaterial customMaterial);

		abstract CustomMaterial getCustomMaterialFromPitch(int pitch);
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

		CustomMaterial model = instrument.getCustomMaterialFromPitch(pitch);
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
		
		CustomMaterial modelId = CustomMaterial.of(item);
		if (modelId == null)
			return;
		
		var instrument = SupportedInstrument.of(modelId);
		if (instrument == null)
			return;

		var pitch = instrument.getPitchFromCustomMaterial(modelId);
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


