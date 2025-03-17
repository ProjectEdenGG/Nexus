package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ICustomNoteBlock extends ICustomBlock {
	@Override
	default Material getVanillaBlockMaterial() {
		return Material.NOTE_BLOCK;
	}

	@Override
	default Material getVanillaItemMaterial() {
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

	default boolean getPowered() {
		return getNoteBlockConfig().powered();
	}

	// Directional
	Set<BlockFace> directions = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

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

	// Sounds

	@Override
	default @NonNull String getBreakSound() {
		String customSound = getNoteBlockConfig().breakSound();
		if (customSound.equals("ui.button.click")) {
			customSound = getNoteBlockConfig().customBreakSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getPlaceSound() {
		String customSound = getNoteBlockConfig().placeSound();
		if (customSound.equals("ui.button.click")) {
			customSound = getNoteBlockConfig().customPlaceSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getStepSound() {
		String customSound = getNoteBlockConfig().stepSound();
		if (customSound.equals("ui.button.click")) {
			customSound = getNoteBlockConfig().customStepSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getHitSound() {
		String customSound = getNoteBlockConfig().hitSound();
		if (customSound.equals("ui.button.click")) {
			customSound = getNoteBlockConfig().customHitSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getFallSound() {
		String customSound = getNoteBlockConfig().fallSound();
		if (customSound.equals("ui.button.click")) {
			customSound = getNoteBlockConfig().customFallSound();
		}

		return customSound;
	}

	default BlockFace getFacing(Block block) {
		return BlockFace.UP;
	}

	@Override
	default BlockData getBlockData(@Nullable BlockFace facing, @Nullable Block underneath) {
		NoteBlock noteBlock = (NoteBlock) this.getVanillaBlockMaterial().createBlockData();
		noteBlock.setInstrument(this.getNoteBlockInstrument());
		noteBlock.setNote(this.getNoteBlockNote(facing));
		noteBlock.setPowered(this.getPowered());
		return noteBlock;
	}

	@Override
	default String getStringBlockData(BlockData blockData) {
		NoteBlock noteBlock = (NoteBlock) blockData;
		return "&oNoteBlock:"
			+ " &fInstrument=&e" + noteBlock.getInstrument()
			+ " &fNote=&e" + noteBlock.getNote().getId()
			+ " &fPowered=" + StringUtils.bool(noteBlock.isPowered());
	}

	@Override
	default boolean equals(@NotNull BlockData blockData, @Nullable BlockFace facing, @Nullable Block underneath) {
		if (!(blockData instanceof NoteBlock noteBlock))
			return false;

		NoteBlock _noteBlock = (NoteBlock) this.getBlockData(facing, underneath);
		noteBlock.setPowered(false);

		return noteBlock.matches(_noteBlock);
	}

	default void placeBlock(Player player, EquipmentSlot hand, Block block, Block placeAgainst, BlockFace facing, ItemStack itemInHand) {
	}

	default void breakBlock(@Nullable Player source, @Nullable ItemStack tool, Block block) {
	}
}
