package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.recipes.models.builders.ShapedBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

public interface ICustomBlock {
	Material blockMaterial = Material.NOTE_BLOCK;
	Material itemMaterial = Material.PAPER;
	Set<BlockFace> directions = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	@NonNull Instrument getNoteBlockInstrument();

	default Instrument getNoteBlockInstrument(@Nullable BlockFace facing) {
		if (!canPlaceSideways() || facing == null || !directions.contains(facing))
			return getNoteBlockInstrument();

		switch (facing) {
			case NORTH, SOUTH -> {
				return getNoteBlockInstrument_NS();
			}
			case EAST, WEST -> {
				return getNoteBlockInstrument_EW();
			}
		}

		return getNoteBlockInstrument();
	}

	int getNoteBlockStep();

	default int getNoteBlockStep(@Nullable BlockFace facing) {
		if (!canPlaceSideways() || facing == null || !directions.contains(facing))
			return getNoteBlockStep();

		switch (facing) {
			case NORTH, SOUTH -> {
				return getNoteBlockStep_NS();
			}
			case EAST, WEST -> {
				return getNoteBlockStep_EW();
			}
		}

		return getNoteBlockStep();
	}

	default Note getNoteBlockNote(BlockFace facing) {
		return new Note(this.getNoteBlockStep(facing));
	}

	// Item

	@NonNull String getName();

	int getCustomModelData();

	default @NonNull ItemBuilder getItemBuilder() {
		ItemBuilder itemBuilder = new ItemBuilder(itemMaterial);
		if (getCustomModelData() == 20000)
			return itemBuilder;

		return itemBuilder.customModelData(getCustomModelData()).name(getName());
	}

	default @NonNull ItemStack getItemStack() {
		if (getCustomModelData() == 20000)
			return new ItemStack(itemMaterial);
		return getItemBuilder().build();
	}

	default @Nullable RecipeBuilder<?> getRecipe() {
		return null;
	}

	// Sideways

	default boolean canPlaceSideways() {
		return false;
	}

	default @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	default @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	default int getNoteBlockStep_NS() {
		return getNoteBlockStep();
	}

	default int getNoteBlockStep_EW() {
		return getNoteBlockStep();
	}

	// Sounds

	default @NonNull Sound getBreakSound() {
		return Sound.BLOCK_WOOD_BREAK;
	}

	default @NonNull Sound getPlaceSound() {
		return Sound.BLOCK_WOOD_PLACE;
	}

	default @NonNull Sound getStepSound() {
		return Sound.BLOCK_WOOD_STEP;
	}

	default @NonNull Sound getHitSound() {
		return Sound.BLOCK_WOOD_HIT;
	}

	// Misc

	default boolean isPistonPushable() {
		return true;
	}

	//

	private NoteBlock getBlockData() {
		NoteBlock noteBlock = (NoteBlock) blockMaterial.createBlockData();
		noteBlock.setInstrument(getNoteBlockInstrument());
		noteBlock.setNote(new Note(getNoteBlockStep()));
		return noteBlock;
	}

	default ShapedBuilder compacted(Material material) {
		return shaped("111", "111", "111").add('1', material);
	}
}
