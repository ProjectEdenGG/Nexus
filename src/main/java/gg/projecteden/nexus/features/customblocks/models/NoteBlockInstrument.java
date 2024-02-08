package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public enum NoteBlockInstrument {
	NONE,

	// Skulls
	ZOMBIE(Material.ZOMBIE_HEAD),
	SKELETON(Material.SKELETON_SKULL),
	CREEPER(Material.CREEPER_HEAD),
	WITHER_DRAGON(Material.DRAGON_HEAD),
	WITHER_SKELETON(Material.WITHER_SKELETON_SKULL),
	PIGLIN(Material.PIGLIN_HEAD),

	// Custom Skulls
	CUSTOM_MOB_HEAD(Material.PLAYER_HEAD),

	// Instruments
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

	// Custom Instruments
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

	public static List<NoteBlockInstrument> getVanillaMobInstruments() {
		return List.of(ZOMBIE, SKELETON, CREEPER, WITHER_DRAGON, WITHER_SKELETON, PIGLIN);
	}

	public static List<NoteBlockInstrument> getVanillaInstruments() {
		return List.of(PIANO, BASS_DRUM, SNARE_DRUM, STICKS, BASS_GUITAR, FLUTE, BELL, GUITAR, CHIME, XYLOPHONE,
			IRON_XYLOPHONE, COW_BELL, DIDGERIDOO, BIT, BANJO, PLING);
	}

	public static List<NoteBlockInstrument> getCustomInstruments() {
		return List.of(MARIMBA, TRUMPET, BUZZ, KALIMBA, KOTO, TAIKO);
	}

	public boolean isMobHead() {
		return this == CUSTOM_MOB_HEAD || getVanillaMobInstruments().contains(this);
	}

	public boolean isVanillaInstrument() {
		return getVanillaInstruments().contains(this);
	}

	public boolean isCustomInstrument() {
		return getCustomInstruments().contains(this);
	}

	public String getSound() {
		return getSound(null);
	}

	public String getSound(Block block) {
		String enumName = name().toLowerCase();

		if (isMobHead()) {
			Block above = block.getRelative(BlockFace.UP);
			MobHead mobHead = MobHead.from(above);
			if (mobHead == null) {
				throw new InvalidInputException("Unknown MobHead of " + enumName
					+ " from block " + block.getRelative(BlockFace.UP).getType()
					+ " at " + StringUtils.getLocationString(block.getLocation()));
			}

			return mobHead.getAmbientSound().getKey().getKey();
		}

		if (isCustomInstrument())
			return "minecraft:custom.noteblock." + enumName;

		try {
			return "minecraft:block.note_block." + switch (getInstrument()) {
				case BASS_DRUM -> "basedrum";
				case SNARE_DRUM -> "snare";
				case BASS_GUITAR -> "bass";
				case STICKS -> "hat";
				case PIANO -> "harp";
				default -> enumName;
			};
		} catch (Exception ex) {
			return "Unknown Instrument of " + enumName
				+ " from block " + block.getRelative(BlockFace.DOWN).getType()
				+ " at " + StringUtils.getLocationString(block.getLocation());
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
		// check mob head above first
		Material aboveType = block.getRelative(BlockFace.UP).getType();
		for (NoteBlockInstrument _mobInstrument : getVanillaMobInstruments()) {
			if (_mobInstrument.getMaterials().contains(aboveType))
				return _mobInstrument;
		}

		if (CUSTOM_MOB_HEAD.getMaterials().contains(aboveType))
			return CUSTOM_MOB_HEAD;

		// then check instrument below
		Block below = block.getRelative(BlockFace.DOWN);
		Material belowType = below.getType();

		for (NoteBlockInstrument instrument : getVanillaInstruments()) {
			if (instrument == PIANO)
				continue;

			if (instrument.getCustomBlock() == null) {
				if (instrument.getMaterials().contains(belowType))
					return instrument;
			} else {
				if (belowType.equals(Material.NOTE_BLOCK)) {
					CustomBlock belowCustomBlock = CustomBlock.fromBlockData(below.getBlockData(), below.getRelative(BlockFace.DOWN));
					if (instrument.getCustomBlock() == belowCustomBlock)
						return instrument;
				}
			}
		}

		return PIANO;
	}

}
