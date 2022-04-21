package gg.projecteden.nexus.features.customblocks.models;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICraftable;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDirectional;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDyeable;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle.BambooBundle;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle.CactusBundle;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle.StickBundle;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle.SugarCaneBundle;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate.AppleCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate.BeetrootCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate.CarrotCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate.PotatoCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate.SweetBerryCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.*;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateA;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateB;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateC;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateD;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.PaperAcaciaLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.PaperBirchLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.PaperDarkOakLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.PaperJungleLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.PaperOakLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.PaperSpruceLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.ShroomCrimsonLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.ShroomWarpedLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.misc.NoteBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.misc.ShojiBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedAcaciaPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedBirchPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedCrimsonPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedDarkOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedJunglePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedSprucePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.CarvedWarpedPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.*;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalAcaciaPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalBirchPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalCrimsonPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalDarkOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalJunglePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalSprucePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalWarpedPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.*;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks.AndesiteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks.DioriteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks.GraniteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledAndesite;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledDiorite;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledGranite;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledStone;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.*;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.NMSUtils.SoundType;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Instrument;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;

public enum CustomBlock implements Keyed {
	// concrete bricks
	RED_CONCRETE_BRICKS(RedConcreteBricks.class),
	ORANGE_CONCRETE_BRICKS(OrangeConcreteBricks.class),
	YELLOW_CONCRETE_BRICKS(YellowConcreteBricks.class),
	LIME_CONCRETE_BRICKS(LimeConcreteBricks.class),
	GREEN_CONCRETE_BRICKS(GreenConcreteBricks.class),
	CYAN_CONCRETE_BRICKS(CyanConcreteBricks.class),
	LIGHT_BLUE_CONCRETE_BRICKS(LightBlueConcreteBricks.class),
	BLUE_CONCRETE_BRICKS(BlueConcreteBricks.class),
	PURPLE_CONCRETE_BRICKS(PurpleConcreteBricks.class),
	MAGENTA_CONCRETE_BRICKS(MagentaConcreteBricks.class),
	PINK_CONCRETE_BRICKS(PinkConcreteBricks.class),
	BROWN_CONCRETE_BRICKS(BrownConcreteBricks.class),
	BLACK_CONCRETE_BRICKS(BlackConcreteBricks.class),
	GRAY_CONCRETE_BRICKS(GrayConcreteBricks.class),
	LIGHT_GRAY_CONCRETE_BRICKS(LightGrayConcreteBricks.class),
	WHITE_CONCRETE_BRICKS(WhiteConcreteBricks.class),

	// compacted
	APPLE_CRATE(AppleCrate.class),
	BEETROOT_CRATE(BeetrootCrate.class),
	BERRY_CRATE(SweetBerryCrate.class),
	CARROT_CRATE(CarrotCrate.class),
	POTATO_CRATE(PotatoCrate.class),
	//
	BAMBOO_BUNDLE(BambooBundle.class),
	CACTUS_BUNDLE(CactusBundle.class),
	STICK_BUNDLE(StickBundle.class),
	SUGAR_CANE_BUNDLE(SugarCaneBundle.class),

	// lanterns
	PAPER_OAK_LANTERN(PaperOakLantern.class),
	PAPER_SPRUCE_LANTERN(PaperSpruceLantern.class),
	PAPER_BIRCH_LANTERN(PaperBirchLantern.class),
	PAPER_JUNGLE_LANTERN(PaperJungleLantern.class),
	PAPER_ACACIA_LANTERN(PaperAcaciaLantern.class),
	PAPER_DARK_OAK_LANTERN(PaperDarkOakLantern.class),
	CRIMSON_LANTERN(ShroomCrimsonLantern.class),
	WARPED_LANTERN(ShroomWarpedLantern.class),

	// colored planks
	RED_PLANKS(RedPlanks.class),
	ORANGE_PLANKS(OrangePlanks.class),
	YELLOW_PLANKS(YellowPlanks.class),
	LIME_PLANKS(LimePlanks.class),
	GREEN_PLANKS(GreenPlanks.class),
	CYAN_PLANKS(CyanPlanks.class),
	LIGHT_BLUE_PLANKS(LightBluePlanks.class),
	BLUE_PLANKS(BluePlanks.class),
	PURPLE_PLANKS(PurplePlanks.class),
	MAGENTA_PLANKS(MagentaPlanks.class),
	PINK_PLANKS(PinkPlanks.class),
	BROWN_PLANKS(BrownPlanks.class),
	BLACK_PLANKS(BlackPlanks.class),
	GRAY_PLANKS(GrayPlanks.class),
	LIGHT_GRAY_PLANKS(LightGrayPlanks.class),
	WHITE_PLANKS(WhitePlanks.class),
	// vertical planks
	VERTICAL_OAK_PLANKS(VerticalOakPlanks.class),
	VERTICAL_SPRUCE_PLANKS(VerticalSprucePlanks.class),
	VERTICAL_BIRCH_PLANKS(VerticalBirchPlanks.class),
	VERTICAL_JUNGLE_PLANKS(VerticalJunglePlanks.class),
	VERTICAL_ACACIA_PLANKS(VerticalAcaciaPlanks.class),
	VERTICAL_DARK_OAK_PLANKS(VerticalDarkOakPlanks.class),
	VERTICAL_CRIMSON_PLANKS(VerticalCrimsonPlanks.class),
	VERTICAL_WARPED_PLANKS(VerticalWarpedPlanks.class),
	// carved planks
	CARVED_OAK_PLANKS(CarvedOakPlanks.class),
	CARVED_SPRUCE_PLANKS(CarvedSprucePlanks.class),
	CARVED_BIRCH_PLANKS(CarvedBirchPlanks.class),
	CARVED_JUNGLE_PLANKS(CarvedJunglePlanks.class),
	CARVED_ACACIA_PLANKS(CarvedAcaciaPlanks.class),
	CARVED_DARK_OAK_PLANKS(CarvedDarkOakPlanks.class),
	CARVED_CRIMSON_PLANKS(CarvedCrimsonPlanks.class),
	CARVED_WARPED_PLANKS(CarvedWarpedPlanks.class),

	// quilted wool
	RED_QUILTED_WOOL(RedQuiltedWool.class),
	ORANGE_QUILTED_WOOL(OrangeQuiltedWool.class),
	YELLOW_QUILTED_WOOL(YellowQuiltedWool.class),
	LIME_QUILTED_WOOL(LimeQuiltedWool.class),
	GREEN_QUILTED_WOOL(GreenQuiltedWool.class),
	CYAN_QUILTED_WOOL(CyanQuiltedWool.class),
	LIGHT_BLUE_QUILTED_WOOL(LightBlueQuiltedWool.class),
	BLUE_QUILTED_WOOL(BlueQuiltedWool.class),
	PURPLE_QUILTED_WOOL(PurpleQuiltedWool.class),
	MAGENTA_QUILTED_WOOL(MagentaQuiltedWool.class),
	PINK_QUILTED_WOOL(PinkQuiltedWool.class),
	BROWN_QUILTED_WOOL(BrownQuiltedWool.class),
	BLACK_QUILTED_WOOL(BlackQuiltedWool.class),
	GRAY_QUILTED_WOOL(GrayQuiltedWool.class),
	LIGHT_GRAY_QUILTED_WOOL(LightGrayQuiltedWool.class),
	WHITE_QUILTED_WOOL(WhiteQuiltedWool.class),

	// stone bricks
	ANDESITE_BRICKS(AndesiteBricks.class),
	DIORITE_BRICKS(DioriteBricks.class),
	GRANITE_BRICKS(GraniteBricks.class),
	// chiseled stone
	CHISELED_STONE(ChiseledStone.class),
	CHISELED_ANDESITE(ChiseledAndesite.class),
	CHISELED_DIORITE(ChiseledDiorite.class),
	CHISELED_GRANITE(ChiseledGranite.class),

	// terracotta shingles
	TERRACOTTA_SHINGLES(TerracottaShingles.class),
	RED_TERRACOTTA_SHINGLES(RedTerracottaShingles.class),
	ORANGE_TERRACOTTA_SHINGLES(OrangeTerracottaShingles.class),
	YELLOW_TERRACOTTA_SHINGLES(YellowTerracottaShingles.class),
	LIME_TERRACOTTA_SHINGLES(LimeTerracottaShingles.class),
	GREEN_TERRACOTTA_SHINGLES(GreenTerracottaShingles.class),
	CYAN_TERRACOTTA_SHINGLES(CyanTerracottaShingles.class),
	LIGHT_BLUE_TERRACOTTA_SHINGLES(LightBlueTerracottaShingles.class),
	BLUE_TERRACOTTA_SHINGLES(BlueTerracottaShingles.class),
	PURPLE_TERRACOTTA_SHINGLES(PurpleTerracottaShingles.class),
	MAGENTA_TERRACOTTA_SHINGLES(MagentaTerracottaShingles.class),
	PINK_TERRACOTTA_SHINGLES(PinkTerracottaShingles.class),
	BROWN_TERRACOTTA_SHINGLES(BrownTerracottaShingles.class),
	BLACK_TERRACOTTA_SHINGLES(BlackTerracottaShingles.class),
	GRAY_TERRACOTTA_SHINGLES(GrayTerracottaShingles.class),
	LIGHT_GRAY_TERRACOTTA_SHINGLES(LightGrayTerracottaShingles.class),
	WHITE_TERRACOTTA_SHINGLES(WhiteTerracottaShingles.class),

	// generic crates
	GENERIC_CRATE_A(GenericCrateA.class),
	GENERIC_CRATE_B(GenericCrateB.class),
	GENERIC_CRATE_C(GenericCrateC.class),
	GENERIC_CRATE_D(GenericCrateD.class),

	// misc
	NOTE_BLOCK(NoteBlock.class),
	SHOJI_BLOCK(ShojiBlock.class),
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

	@Override
	public @NotNull NamespacedKey getKey() {
		return new NamespacedKey(Nexus.getInstance(), name().toLowerCase());
	}

	public static @Nullable CustomBlock fromItemstack(ItemStack itemInHand) {
		int modelData = CustomModelData.of(itemInHand);
		if (itemInHand.getType().equals(Material.NOTE_BLOCK) && modelData == 0)
			return CustomBlock.NOTE_BLOCK;

		if (!itemInHand.getType().equals(Material.PAPER))
			return null;

		return fromModelData(modelData);
	}

	public static @Nullable CustomBlock fromModelData(int modelData) {
		return modelDataMap.getOrDefault(modelData, null);
	}

	public static @Nullable CustomBlock fromNoteBlock(@NonNull org.bukkit.block.data.type.NoteBlock noteBlock) {
		List<CustomBlock> directional = new ArrayList<>();
		for (CustomBlock _customBlock : values()) {
			ICustomBlock customBlock = _customBlock.get();
			if (checkData(customBlock.getNoteBlockInstrument(), customBlock.getNoteBlockStep(), noteBlock))
				return _customBlock;
			else if (customBlock instanceof IDirectional)
				directional.add(_customBlock);
		}

		// Directional checks

		for (CustomBlock _customBlock : directional) {
			IDirectional directionalBlock = (IDirectional) _customBlock.get();
			if (checkData(directionalBlock.getNoteBlockInstrument_NS(), directionalBlock.getNoteBlockStep_NS(), noteBlock))
				return _customBlock;

			if (checkData(directionalBlock.getNoteBlockInstrument_EW(), directionalBlock.getNoteBlockStep_EW(), noteBlock))
				return _customBlock;
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
		Instrument instrument = this.getNoteBlockInstrument(facing);
		int step = this.getNoteBlockStep(facing);

		org.bukkit.block.data.type.NoteBlock noteBlock = (org.bukkit.block.data.type.NoteBlock) Material.NOTE_BLOCK.createBlockData();
		noteBlock.setInstrument(instrument);
		noteBlock.setNote(new Note(step));

		if (!BlockUtils.tryPlaceEvent(player, block, placeAgainst, Material.NOTE_BLOCK, noteBlock, false, new ItemStack(Material.NOTE_BLOCK)))
			return false;

		UUID uuid = player.getUniqueId();
		Location location = block.getLocation();
		CustomBlockUtils.placeBlockDatabase(uuid, this, location, facing);
		playSound(SoundType.PLACE, location);

		ItemUtils.subtract(player, itemInHand);
		player.swingMainHand();
		return true;
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

	public void playSound(SoundType type, Location location) {
		Sound sound = getSound(type);
		BlockUtils.playSound(sound, location);
	}

	public void registerRecipes() {
		if (!(get() instanceof ICraftable craftable))
			return;

		RecipeBuilder<?> recipeBuilder;

		// craft recipe
		Pair<RecipeBuilder<?>, Integer> recipePair = craftable.getCraftRecipe();
		if (recipePair != null && recipePair.getFirst() == null) {
			ItemStack toMakeItem = new ItemBuilder(craftable.getItemStack()).amount(recipePair.getSecond()).build();
			recipeBuilder = recipePair.getFirst();
			recipeBuilder.toMake(toMakeItem).build().type(RecipeType.CUSTOM_BLOCKS).register();
		}

		// uncraft recipe
		recipeBuilder = craftable.getUncraftRecipe();
		if (recipeBuilder != null) {
			recipeBuilder.build().type(RecipeType.CUSTOM_BLOCKS).register();
		}

		// redye recipes
		if (craftable instanceof IDyeable Idyeable) {
			CustomBlockTag tag = Idyeable.getRedyeTag();

			for (ColorType color : ColorType.getDyes()) {
				final Material dye = color.switchColor(Material.WHITE_DYE);

				BiConsumer<NexusRecipe, RecipeType> register = (recipe, type) -> recipe.type(type).register();

				register.accept(surround(dye).with(tag).toMake(color.switchColor(tag.first()), 8).build(), RecipeType.DYES);
			}
		}
	}

	static final Set<BlockFace> directions = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	public Instrument getNoteBlockInstrument(@Nullable BlockFace facing) {
		ICustomBlock customBlock = this.get();
		if (facing == null || !directions.contains(facing) || !(customBlock instanceof IDirectional directional))
			return customBlock.getNoteBlockInstrument();

		switch (facing) {
			case NORTH, SOUTH -> {
				return directional.getNoteBlockInstrument_NS();
			}
			case EAST, WEST -> {
				return directional.getNoteBlockInstrument_EW();
			}
		}

		return customBlock.getNoteBlockInstrument();
	}

	public int getNoteBlockStep(@Nullable BlockFace facing) {
		ICustomBlock customBlock = this.get();
		if (facing == null || !directions.contains(facing) || !(customBlock instanceof IDirectional directional))
			return customBlock.getNoteBlockStep();

		switch (facing) {
			case NORTH, SOUTH -> {
				return directional.getNoteBlockStep_NS();
			}
			case EAST, WEST -> {
				return directional.getNoteBlockStep_EW();
			}
		}

		return customBlock.getNoteBlockStep();
	}

	public Note getNoteBlockNote(BlockFace facing) {
		return new Note(this.getNoteBlockStep(facing));
	}
}
