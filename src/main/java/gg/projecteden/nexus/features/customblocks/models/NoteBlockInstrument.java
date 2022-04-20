package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public enum NoteBlockInstrument {
	NONE,
	PIANO,
	BASS_DRUM(MaterialTag.ALL_STONE),
	SNARE_DRUM(MaterialTag.CONCRETE_POWDERS, Material.SAND, Material.GRAVEL),
	STICKS(MaterialTag.STAINED_GLASS, Material.GLASS),
	BASS_GUITAR(MaterialTag.PLANKS),
	FLUTE(Material.CLAY),
	BELL(Material.GOLD_BLOCK),
	GUITAR(MaterialTag.WOOL),
	CHIME(Material.PACKED_ICE),
	XYLOPHONE(Material.BONE_BLOCK),
	IRON_XYLOPHONE(Material.IRON_BLOCK),
	COW_BELL(Material.SOUL_SAND),
	DIDGERIDOO(Material.PUMPKIN, Material.DRIED_KELP_BLOCK),
	BIT(Material.EMERALD_BLOCK),
	BANJO(Material.HAY_BLOCK),
	PLING(Material.GLOWSTONE),

	// Custom
	MARIMBA(MaterialTag.STRIPPED_LOGS),
	TRUMPET(Material.WAXED_COPPER_BLOCK),
	BUZZ(Material.HONEYCOMB_BLOCK),
	KALIMBA(Material.AMETHYST_BLOCK),
	KOTO(CustomBlock.BAMBOO_BUNDLE),
	TAIKO(CustomBlock.SHOJI_BLOCK),
	;

	private final Set<Material> materials = new HashSet<>();
	@Getter
	private CustomBlock customBlock = null;

	NoteBlockInstrument(Tag<Material> materialTag) {
		this.materials.addAll(materialTag.getValues());
	}

	NoteBlockInstrument(Material... materials) {
		this.materials.addAll(Arrays.asList(materials));
	}

	NoteBlockInstrument(Tag<Material> fromTags, Material... materials) {
		this.materials.addAll(Arrays.asList(materials));
		this.materials.addAll(fromTags.getValues());
	}

	NoteBlockInstrument(@NonNull CustomBlock customBlock) {
		this.materials.add(Material.NOTE_BLOCK);
		this.customBlock = customBlock;
	}

	public String getSound() {
		try {
			return "minecraft:block.note_block." + switch (getInstrument()) {
				case BASS_DRUM -> "basedrum";
				case SNARE_DRUM -> "snare";
				case BASS_GUITAR -> "bass";
				case STICKS -> "hat";
				case PIANO -> "harp";
				default -> name().toLowerCase();
			};
		} catch (IllegalArgumentException ex) {
			return "minecraft:custom.noteblock." + name().toLowerCase();
		}
	}

	@NotNull
	public Instrument getInstrument() {
		return Instrument.valueOf(name());
	}

	public Set<Material> getMaterials() {
		if (this.equals(PIANO)) {
			Set<Material> used = getUsedMaterials();
			return Arrays.stream(Material.values()).filter(material -> !used.contains(material)).collect(Collectors.toSet());
		}

		return this.materials;
	}

	public static Set<Material> getUsedMaterials() {
		Set<Material> result = new HashSet<>();
		for (NoteBlockInstrument instrument : values()) {
			if (instrument.equals(PIANO))
				continue;

			result.addAll(instrument.getMaterials());
		}
		return result;
	}

	public static NoteBlockInstrument getInstrument(Block block) {
		Block below = block.getRelative(BlockFace.DOWN);
		Material belowType = below.getType();
		for (NoteBlockInstrument instrument : NoteBlockInstrument.values()) {
			if (instrument.equals(PIANO))
				continue;

			if (instrument.getCustomBlock() == null) {
				if (instrument.getMaterials().contains(belowType))
					return instrument;
			} else {
				if (belowType.equals(Material.NOTE_BLOCK)) {
					CustomBlock belowCustomBlock = CustomBlock.fromNoteBlock((NoteBlock) below.getBlockData());
					if (instrument.getCustomBlock() == belowCustomBlock)
						return instrument;
				}
			}
		}

		return PIANO;
	}

	public boolean isCustom(){
		return getSound().contains("minecraft:custom.noteblock.");
	}

}
