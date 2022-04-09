package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.features.customblocks.CustomBlocks;
import gg.projecteden.nexus.features.customblocks.models.blocks.AppleCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.BeetrootCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.BerryCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.CarrotCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.NoteBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.PotatoCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.SugarCaneBundle;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import lombok.SneakyThrows;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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


	public enum SoundType {
		PLACE,
		BREAK,
		STEP,
		HIT
	}

	public void playSound(SoundType type, Block block) {
		ICustomBlock customBlock = this.get();
		Sound sound = null;
		switch (type) {
			case PLACE -> sound = customBlock.getPlaceSound();
			case BREAK -> sound = customBlock.getBreakSound();
			case STEP -> sound = customBlock.getStepSound();
			case HIT -> sound = customBlock.getHitSound();
		}

		if (sound == null)
			return;

		CustomBlocks.debug(" TODO: playSound type=" + type + " - " + sound.getKey().getKey());

//		new SoundBuilder(sound)
//			.location(block)
//			.category(SoundCategory.BLOCKS)
//			.play();
	}
}
