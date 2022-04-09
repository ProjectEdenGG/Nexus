package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

public interface ICustomBlock {
	Material blockMaterial = Material.NOTE_BLOCK;
	Material itemMaterial = Material.PAPER;

	@NonNull Instrument getNoteBlockInstrument();

	int getNoteBlockStep();

	// Item

	String getName();

	int getCustomModelData();

	default ItemBuilder getItemBuilder() {
		ItemBuilder itemBuilder = new ItemBuilder(itemMaterial);
		if (getCustomModelData() == 20000)
			return itemBuilder;

		return itemBuilder.customModelData(getCustomModelData()).name(getName());
	}

	default ItemStack getItemStack() {
		if (getCustomModelData() == 20000)
			return new ItemStack(itemMaterial);
		return getItemBuilder().build();
	}

	@Nullable
	default Recipe getRecipe() {
		return null; // TODO: register recipes
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

	default Sound getBreakSound() {
		return Sound.BLOCK_WOOD_BREAK;
	}

	default Sound getPlaceSound() {
		return Sound.BLOCK_WOOD_PLACE;
	}

	default Sound getStepSound() {
		return Sound.BLOCK_WOOD_STEP;
	}

	default Sound getHitSound() {
		return Sound.BLOCK_WOOD_HIT;
	}

	private NoteBlock getBlockData() {
		NoteBlock noteBlock = (NoteBlock) blockMaterial.createBlockData();
		noteBlock.setInstrument(getNoteBlockInstrument());
		noteBlock.setNote(new Note(getNoteBlockStep()));
		return noteBlock;
	}
}
