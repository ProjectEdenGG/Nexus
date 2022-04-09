package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
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
		return new ItemBuilder(itemMaterial).customModelData(getCustomModelData()).name(getName());
	}

	default ItemStack getItemStack() {
		return getItemBuilder().build();
	}

	@Nullable Recipe getRecipe = null;

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

	default void place(Location location, Block against, boolean sound) {
		BlockFace face = location.getBlock().getFace(against);
		// TODO
		if (canPlaceSideways())
			placeBlock(location, sound);
	}

	default void tryPlace(Player player, Location location, Block against) {
		BlockUtils.tryPlaceEvent(player, location.getBlock(), against, Material.NOTE_BLOCK, getBlockData());
		// TODO play sound, if event doesn't
	}

	//

	private NoteBlock getBlockData() {
		NoteBlock noteBlock = (NoteBlock) blockMaterial.createBlockData();
		noteBlock.setInstrument(getNoteBlockInstrument());
		noteBlock.setNote(new Note(getNoteBlockStep()));
		return noteBlock;
	}

	private void placeBlock(Location location, boolean silent) {
		Block block = location.getBlock();
		block.setType(blockMaterial, false);
		block.setBlockData(getBlockData(), false);

		if (!silent)
			new SoundBuilder(getPlaceSound()).location(location).play();
	}
}
