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

	default @NonNull String getBreakSound() {
		Sound sound = getConfig().breakSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customBreakSound();
		}

		return customSound;
	}

	default @NonNull String getPlaceSound() {
		Sound sound = getConfig().placeSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customPlaceSound();
		}

		return customSound;
	}

	default @NonNull String getStepSound() {
		Sound sound = getConfig().stepSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customStepSound();
		}

		return customSound;
	}

	default @NonNull String getHitSound() {
		Sound sound = getConfig().hitSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customHitSound();
		}

		return customSound;
	}

	default @NonNull String getFallSound() {
		Sound sound = getConfig().fallSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customFallSound();
		}

		return customSound;
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
