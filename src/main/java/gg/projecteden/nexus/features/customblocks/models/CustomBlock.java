package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.blocks.AppleCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.BeetrootCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.BerryCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.CarrotCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.NoteBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.PotatoCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.SugarCaneBundle;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public enum CustomBlock {
	NOTE_BLOCK(NoteBlock.class),
	APPLE_CRATE(AppleCrate.class),
	BEETROOT_CRATE(BeetrootCrate.class),
	BERRY_CRATE(BerryCrate.class),
	CARROT_CRATE(CarrotCrate.class),
	POTATO_CRATE(PotatoCrate.class),
	SUGAR_CANE_BUNDLE(SugarCaneBundle.class),
	;

	private final ICustomBlock customBlock;

	@SneakyThrows
	CustomBlock(Class<? extends ICustomBlock> clazz) {
		this.customBlock = (ICustomBlock) clazz.getDeclaredConstructors()[0].newInstance();
	}

	public static final HashMap<Integer, CustomBlock> modelDataMap = new HashMap<>();

	static {
		for (CustomBlock customBlock : values()) {
			ICustomBlock iCustomBlock = customBlock.get();

			modelDataMap.put(iCustomBlock.getCustomModelData(), customBlock);
		}
	}

	public static @Nullable CustomBlock fromItemstack(ItemStack itemInHand) {
		int modelData = CustomModelData.of(itemInHand);
		if (itemInHand.getType().equals(Material.NOTE_BLOCK) && modelData == 0)
			return CustomBlock.NOTE_BLOCK;

		return fromModelData(modelData);
	}

	public static @Nullable CustomBlock fromModelData(int modelData) {
		return modelDataMap.getOrDefault(modelData, null);
	}

	public static @Nullable CustomBlock fromNoteBlock(@NonNull org.bukkit.block.data.type.NoteBlock noteBlock) {
		List<CustomBlock> sideWays = new ArrayList<>();
		for (CustomBlock customBlock : values()) {
			ICustomBlock block = customBlock.get();

			if (block.canPlaceSideways()) {
				sideWays.add(customBlock);
				continue;
			}

			if (checkData(block.getNoteBlockInstrument(), block.getNoteBlockStep(), noteBlock))
				return customBlock;
		}

		// Sideways checks

		for (CustomBlock customBlock : sideWays) {
			ICustomBlock block = customBlock.get();
			if (checkData(block.getNoteBlockInstrument(), block.getNoteBlockStep(), noteBlock))
				return customBlock;

			if (checkData(block.getNoteBlockInstrument_NS(), block.getNoteBlockStep_NS(), noteBlock))
				return customBlock;

			if (checkData(block.getNoteBlockInstrument_EW(), block.getNoteBlockStep_EW(), noteBlock))
				return customBlock;
		}

		debug("CustomBlock: Couldn't find custom block with: " + noteBlock.getInstrument() + " " + noteBlock.getNote().getId());
		return null;
	}

	private static boolean checkData(Instrument _instrument, int _step, org.bukkit.block.data.type.NoteBlock noteBlock) {
		Instrument instrument = noteBlock.getInstrument();
		int step = noteBlock.getNote().getId();
		return _step == step && _instrument.equals(instrument);
	}

	public ICustomBlock get() {
		return this.customBlock;
	}

	public boolean placeBlock(Player player, Block block, Block placeAgainst, BlockFace facing, ItemStack itemInHand) {
		ICustomBlock customBlock = this.get();
		Instrument instrument = customBlock.getNoteBlockInstrument(facing);
		int step = customBlock.getNoteBlockStep(facing);

		org.bukkit.block.data.type.NoteBlock noteBlock = (org.bukkit.block.data.type.NoteBlock) Material.NOTE_BLOCK.createBlockData();
		noteBlock.setInstrument(instrument);
		noteBlock.setNote(new Note(step));

		if (!BlockUtils.tryPlaceEvent(player, block, placeAgainst, Material.NOTE_BLOCK, noteBlock, false, new ItemStack(Material.NOTE_BLOCK)))
			return false;

		UUID uuid = player.getUniqueId();
		Location location = block.getLocation();
		if (this.equals(NOTE_BLOCK)) {
			NoteBlockUtils.placeBlockDatabase(uuid, location);
		} else {
			CustomBlockUtils.placeBlockDatabase(uuid, this, location);
		}

		playSound(SoundType.PLACE, block);
		ItemUtils.subtract(player, itemInHand);
		player.swingMainHand();
		return true;
	}

	public enum SoundType {
		PLACE,
		BREAK,
		STEP,
		HIT,
		;
	}

	public @Nullable SoundType getSoundType(Sound sound) {
		String soundKey = sound.getKey().getKey();
		if (soundKey.endsWith(".step"))
			return SoundType.STEP;
		else if (soundKey.endsWith(".hit"))
			return SoundType.HIT;
		else if (soundKey.endsWith(".place"))
			return SoundType.PLACE;
		else if (soundKey.endsWith(".break"))
			return SoundType.BREAK;

		return null;
	}

	public @Nullable Sound getSound(SoundType type) {
		switch (type) {
			case PLACE -> {
				return customBlock.getPlaceSound();
			}
			case BREAK -> {
				return customBlock.getBreakSound();
			}
			case STEP -> {
				return customBlock.getStepSound();
			}
			case HIT -> {
				return customBlock.getHitSound();
			}
		}
		return null;
	}

	public void playSound(SoundType type, Block block) {
		Sound sound = getSound(type);
		if (sound == null)
			return;

		new SoundBuilder(sound)
			.location(block)
			.category(SoundCategory.BLOCKS)
			.play();
	}

	public void registerRecipe() {
		ICustomBlock customBlock = get();
		@Nullable RecipeBuilder<?> recipe = customBlock.getRecipe();
		if (recipe == null)
			return;

		recipe.toMake(customBlock.getItemStack()).build().type(RecipeType.CUSTOM_BLOCKS).register();
	}
}
