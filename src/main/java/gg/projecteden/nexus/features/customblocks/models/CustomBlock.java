package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.blocks.AppleCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.BeetrootCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.BerryCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.CarrotCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.NoteBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.PotatoCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.SugarCaneBundle;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.SneakyThrows;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
			modelDataMap.put(customBlock.get().getCustomModelData(), customBlock);
		}
	}

	public static @Nullable CustomBlock fromItemstack(ItemStack itemInHand) {
		int modelData = CustomModelData.of(itemInHand);
		if (itemInHand.getType().equals(Material.NOTE_BLOCK) && modelData == 0)
			return CustomBlock.NOTE_BLOCK;

		return modelDataMap.getOrDefault(modelData, null);
	}

	public static @Nullable CustomBlock fromNoteBlock(Block block) {
		return fromNoteBlock((org.bukkit.block.data.type.NoteBlock) block.getBlockData());
	}

	public static @Nullable CustomBlock fromNoteBlock(org.bukkit.block.data.type.NoteBlock noteBlock) {
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

		for (CustomBlock customBlock : sideWays) {
			ICustomBlock block = customBlock.get();
			if (checkData(block.getNoteBlockInstrument(), block.getNoteBlockStep(), noteBlock))
				return customBlock;

			if (checkData(block.getNoteBlockInstrument_NS(), block.getNoteBlockStep_NS(), noteBlock))
				return customBlock;

			if (checkData(block.getNoteBlockInstrument_EW(), block.getNoteBlockStep_EW(), noteBlock))
				return customBlock;
		}

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

	public boolean placeBlock(Player player, Block block, Block placeAgainst, ItemStack itemInHand) {
		ICustomBlock customBlock = this.get();
		Instrument instrument = customBlock.getNoteBlockInstrument();
		int step = customBlock.getNoteBlockStep();

		// TODO: sideways

		org.bukkit.block.data.type.NoteBlock noteBlock = (org.bukkit.block.data.type.NoteBlock) Material.NOTE_BLOCK.createBlockData();
		noteBlock.setInstrument(instrument);
		noteBlock.setNote(new Note(step));

		if (!BlockUtils.tryPlaceEvent(player, block, placeAgainst, Material.NOTE_BLOCK, noteBlock, false, new ItemStack(Material.NOTE_BLOCK)))
			return false;

		if (CustomBlock.NOTE_BLOCK.equals(this))
			NoteBlockUtils.placeBlock(player, block.getLocation());

		playSound(SoundType.PLACE, block);
		ItemUtils.subtract(player, itemInHand);
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
		if (true) // TODO: wait until SoundEvents are fixed
			return;

		Sound sound = getSound(type);
		if (sound == null)
			return;

		new SoundBuilder(sound)
			.location(block)
			.category(SoundCategory.BLOCKS)
			.play();
	}
}
