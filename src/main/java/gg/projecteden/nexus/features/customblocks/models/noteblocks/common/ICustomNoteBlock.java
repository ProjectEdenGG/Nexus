package gg.projecteden.nexus.features.customblocks.models.noteblocks.common;

import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ICustomNoteBlock extends ICustomBlock {
	@Override
	default Material getBlockMaterial() {
		return Material.NOTE_BLOCK;
	}

	default CustomNoteBlockConfig getNoteBlockConfig() {
		return getClass().getAnnotation(CustomNoteBlockConfig.class);
	}

	default @NonNull Instrument getNoteBlockInstrument() {
		return getNoteBlockConfig().instrument();
	}

	default int getNoteBlockStep() {
		return getNoteBlockConfig().step();
	}

	//

	default @NonNull String getBreakSound() {
		Sound sound = getNoteBlockConfig().breakSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getNoteBlockConfig().customBreakSound();
		}

		return customSound;
	}

	default @NonNull String getPlaceSound() {
		Sound sound = getNoteBlockConfig().placeSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getNoteBlockConfig().customPlaceSound();
		}

		return customSound;
	}

	default @NonNull String getStepSound() {
		Sound sound = getNoteBlockConfig().stepSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getNoteBlockConfig().customStepSound();
		}

		return customSound;
	}

	default @NonNull String getHitSound() {
		Sound sound = getNoteBlockConfig().hitSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getNoteBlockConfig().customHitSound();
		}

		return customSound;
	}

	default @NonNull String getFallSound() {
		Sound sound = getNoteBlockConfig().fallSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getNoteBlockConfig().customFallSound();
		}

		return customSound;
	}

	Set<BlockFace> directions = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	default Instrument getNoteBlockInstrument(@Nullable BlockFace facing) {
		if (facing == null || !directions.contains(facing) || !(this instanceof IDirectionalNoteBlock directional))
			return this.getNoteBlockInstrument();

		switch (facing) {
			case NORTH, SOUTH -> {
				return directional.getNoteBlockInstrument_NS();
			}
			case EAST, WEST -> {
				return directional.getNoteBlockInstrument_EW();
			}
		}

		return this.getNoteBlockInstrument();
	}

	default int getNoteBlockStep(@Nullable BlockFace facing) {
		if (facing == null || !directions.contains(facing) || !(this instanceof IDirectionalNoteBlock directional))
			return this.getNoteBlockStep();

		switch (facing) {
			case NORTH, SOUTH -> {
				return directional.getNoteBlockStep_NS();
			}
			case EAST, WEST -> {
				return directional.getNoteBlockStep_EW();
			}
		}

		return this.getNoteBlockStep();
	}

	default Note getNoteBlockNote(BlockFace facing) {
		return new Note(this.getNoteBlockStep(facing));
	}
}
