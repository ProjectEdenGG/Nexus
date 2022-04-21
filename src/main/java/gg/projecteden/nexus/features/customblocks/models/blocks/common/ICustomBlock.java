package gg.projecteden.nexus.features.customblocks.models.blocks.common;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;

public interface ICustomBlock {
	Material blockMaterial = Material.NOTE_BLOCK;
	Material itemMaterial = Material.PAPER;

	default CustomBlockConfig getConfig() {
		return getClass().getAnnotation(CustomBlockConfig.class);
	}

	default @NonNull Instrument getNoteBlockInstrument(){
		return getConfig().instrument();
	}

	default int getNoteBlockStep(){
		return getConfig().step();
	}

	// Item

	default @NonNull String getName(){
		return getConfig().name();
	}

	default int getCustomModelData(){
		return getConfig().modelId();
	}

	default @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemMaterial).customModelData(getCustomModelData()).name(getName());
	}

	default @NonNull ItemStack getItemStack() {
		return getItemBuilder().build();
	}

	// Sounds

	default @NonNull Sound getBreakSound() {
		return getConfig().breakSound();
	}

	default @NonNull Sound getPlaceSound() {
		return getConfig().placeSound();
	}

	default @NonNull Sound getStepSound() {
		return getConfig().stepSound();
	}

	default @NonNull Sound getHitSound() {
		return getConfig().hitSound();
	}

	// Misc

	default boolean isPistonPushable() {
		return getConfig().isPistonPushable();
	}

	//

	private NoteBlock getBlockData() {
		NoteBlock noteBlock = (NoteBlock) blockMaterial.createBlockData();
		noteBlock.setInstrument(getNoteBlockInstrument());
		noteBlock.setNote(new Note(getNoteBlockStep()));
		return noteBlock;
	}
}
