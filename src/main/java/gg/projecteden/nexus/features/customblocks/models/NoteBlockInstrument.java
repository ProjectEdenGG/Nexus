package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum NoteBlockInstrument {
	PIANO(getSoundFrom(Instrument.PIANO)),
	BASS_DRUM(getSoundFrom(Instrument.BASS_DRUM),
		fromTags(MaterialTag.ALL_STONE.getValues(), MaterialTag.ALL_QUARTZ.getValues(), MaterialTag.CONCRETES.getValues(),
			MaterialTag.MINERAL_ORES.getValues(), MaterialTag.CORAL_BLOCKS.getValues()),
		Material.NETHERRACK, Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM, Material.SANDSTONE, Material.BEDROCK,
		Material.OBSERVER, Material.RESPAWN_ANCHOR, Material.BRICKS, Material.OBSIDIAN),
	SNARE_DRUM(getSoundFrom(Instrument.SNARE_DRUM), MaterialTag.CONCRETE_POWDERS.getValues(), Material.SAND, Material.GRAVEL, Material.SOUL_SOIL),
	STICKS(getSoundFrom(Instrument.STICKS), MaterialTag.STAINED_GLASS.getValues(), Material.GLASS, Material.SEA_LANTERN, Material.BEACON),
	BASS_GUITAR(getSoundFrom(Instrument.BASS_GUITAR), MaterialTag.PLANKS.getValues()),
	FLUTE(getSoundFrom(Instrument.FLUTE), Material.CLAY),
	BELL(getSoundFrom(Instrument.BELL), Material.GOLD_BLOCK),
	GUITAR(getSoundFrom(Instrument.GUITAR), MaterialTag.WOOL.getValues()),
	CHIME(getSoundFrom(Instrument.CHIME), Material.PACKED_ICE),
	XYLOPHONE(getSoundFrom(Instrument.XYLOPHONE), Material.BONE_BLOCK),
	IRON_XYLOPHONE(getSoundFrom(Instrument.IRON_XYLOPHONE), Material.IRON_BLOCK),
	COW_BELL(getSoundFrom(Instrument.COW_BELL), Material.SOUL_SAND),
	DIDGERIDOO(getSoundFrom(Instrument.DIDGERIDOO), Material.PUMPKIN, Material.DRIED_KELP_BLOCK),
	BIT(getSoundFrom(Instrument.BIT), Material.EMERALD_BLOCK),
	BANJO(getSoundFrom(Instrument.BANJO), Material.HAY_BLOCK),
	PLING(getSoundFrom(Instrument.PLING), Material.GLOWSTONE),
	// Custom
	MARIMBA(customSound("marimba"), MaterialTag.STRIPPED_LOGS.getValues()),
	TRUMPET(customSound("trumpet"), Material.COPPER_BLOCK, Material.WAXED_COPPER_BLOCK),
	BUZZ(customSound("buzz"), Material.HONEYCOMB_BLOCK),
	KALIMBA(customSound("kalimba"), Material.REDSTONE_LAMP),
	KOTO(customSound("koto"), CustomBlock.SUGAR_CANE_BUNDLE), //TODO: better material
	TAIKO(customSound("taiko"), CustomBlock.APPLE_CRATE), //TODO: better material
	;

	@SafeVarargs
	private static Set<Material> fromTags(EnumSet<Material>... lists) {
		Set<Material> result = new HashSet<>();
		for (EnumSet<Material> list : lists) {
			result.addAll(list);
		}

		return result;
	}

	@Getter
	private final String sound;
	private Set<Material> materials = new HashSet<>();
	private ICustomBlock customBlock = null;

	public Set<Material> getMaterials() {
		if (this.equals(PIANO)) {
			Set<Material> used = getUsedMaterials();
			return Arrays.stream(Material.values()).filter(material -> !used.contains(material)).collect(Collectors.toSet());
		}

		return this.materials;
	}

	NoteBlockInstrument(String piano) {
		this.sound = piano;
	}

	NoteBlockInstrument(String sound, Material material) {
		this.sound = sound;
		this.materials = Collections.singleton(material);
	}

	NoteBlockInstrument(String sound, Material... materials) {
		this.sound = sound;
		this.materials = new HashSet<>(Arrays.stream(materials).toList());
	}

	NoteBlockInstrument(String sound, Set<Material> fromTags, Material... materials) {
		this.sound = sound;
		this.materials = new HashSet<>(Arrays.stream(materials).toList());
		this.materials.addAll(fromTags);
	}

	NoteBlockInstrument(String sound, @NonNull CustomBlock customBlock) {
		this.sound = sound;
		this.materials = Collections.singleton(Material.NOTE_BLOCK);
		this.customBlock = customBlock.get();
	}

	private static String getSoundFrom(Instrument instrument) {
		String name = switch (instrument) {
			case BASS_DRUM -> "basedrum";
			case SNARE_DRUM -> "snare";
			case BASS_GUITAR -> "bass";
			case STICKS -> "hat";
			case PIANO -> "harp";
			default -> instrument.name().toLowerCase();
		};

		return "minecraft:block.note_block." + name;
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

			if (instrument.customBlock == null) {
				if (instrument.getMaterials().contains(belowType))
					return instrument;
			} else {
				if (belowType.equals(Material.NOTE_BLOCK)) {
					ICustomBlock instrumentCustomBlock = instrument.customBlock;
					CustomBlock _belowCustomBlock = CustomBlock.fromNoteBlock((NoteBlock) below.getBlockData());
					if (_belowCustomBlock != null && instrumentCustomBlock.equals(_belowCustomBlock.get())) {
						return instrument;
					}
				}
			}
		}

		return PIANO;
	}

	private static String customSound(String instrument) {
		return "minecraft:custom.noteblock." + instrument;
	}
}
