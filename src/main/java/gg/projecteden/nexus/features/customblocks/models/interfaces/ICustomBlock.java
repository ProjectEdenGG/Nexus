package gg.projecteden.nexus.features.customblocks.models.interfaces;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;

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

	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return null;
	}

	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return null;
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

	default RecipeBuilder<?> getUncraftRecipe(@NotNull Material toMake, int count) {
		ItemStack toMakeItem = new ItemBuilder(toMake).amount(count).build();
		return RecipeBuilder.shapeless().add(getItemStack()).toMake(toMakeItem);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull Material material) {
		return new Pair<>(shaped("11", "11").add('1', material), 4);
	}

	default Pair<RecipeBuilder<?>, Integer> getCombineSlabsRecipe(@NotNull Material material) {
		return new Pair<>(shaped("1", "1").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getChiseledRecipe(@NotNull Material material) {
		return new Pair<>(shaped("11").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getSurroundRecipe(@NonNull Material center, @NotNull Tag<Material> surround) {
		return new Pair<>(surround(center).with(surround), 8);
	}
}
