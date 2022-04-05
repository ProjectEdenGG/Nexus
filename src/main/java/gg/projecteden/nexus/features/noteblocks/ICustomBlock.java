package gg.projecteden.nexus.features.noteblocks;

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
	Material material = Material.NOTE_BLOCK;

	@NonNull Instrument getNoteBlockInstrument();

	int getNoteBlockStep();

	// Item

	String getName();

	int getCustomModelData();

	default ItemBuilder getItemBuilder() {
		return new ItemBuilder(material).customModelData(getCustomModelData()).name(getName());
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

	default String getBreakSound() {
		return Sound.BLOCK_WOOD_BREAK.getKey().getKey();
	}

	default String getPlaceSound() {
		return Sound.BLOCK_WOOD_PLACE.getKey().getKey();
	}

	default String getStepSound() {
		return Sound.BLOCK_WOOD_STEP.getKey().getKey();
	}

	default String getHitSound() {
		return Sound.BLOCK_WOOD_HIT.getKey().getKey();
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
		NoteBlock noteBlock = (NoteBlock) material.createBlockData();
		noteBlock.setInstrument(getNoteBlockInstrument());
		noteBlock.setNote(new Note(getNoteBlockStep()));
		return noteBlock;
	}

	private void placeBlock(Location location, boolean silent) {
		Block block = location.getBlock();
		block.setType(material, false);
		block.setBlockData(getBlockData(), false);

		if (!silent)
			new SoundBuilder(getPlaceSound()).location(location).play();
	}
}
