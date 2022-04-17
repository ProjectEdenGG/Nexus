package gg.projecteden.nexus.features.customblocks.models.interfaces;

import com.mojang.datafixers.util.Pair;
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

	@NonNull Instrument getNoteBlockInstrument();

	int getNoteBlockStep();

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

	default @Nullable Pair<RecipeBuilder<?>, Integer> getRecipe() {
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

	default Pair<RecipeBuilder<?>, Integer> getCompactRecipe(@NotNull Material material) {
		return new Pair<>(shaped("111", "111", "111").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull Material material) {
		return new Pair<>(shaped("11", "11").add('1', material), 4);
	}

	default Pair<RecipeBuilder<?>, Integer> getCombineSlabsRecipe(@NotNull Material material) {
		return new Pair<>(shaped("1", "1").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getVerticalRecipe(@NotNull Material material) {
		return new Pair<>(shaped("1", "1", "1").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getPaperLanternRecipe(@NotNull Material material) {
		return new Pair<>(shaped("121", "343", "121")
				.add('1', Material.STICK)
				.add('2', material)
				.add('3', Material.PAPER)
				.add('4', Material.TORCH)
				, 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getShroomLanternRecipe(@NotNull Material material) {
		return new Pair<>(shaped("121", "333", "121")
				.add('1', Material.STICK)
				.add('2', material)
				.add('3', Material.SHROOMLIGHT)
				, 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getChiseledRecipe(@NotNull Material material) {
		return new Pair<>(shaped("11").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getSurroundRecipe(@NonNull Material center, @NotNull Tag<Material> surround) {
		return new Pair<>(surround(center).with(surround), 8);
	}
}
