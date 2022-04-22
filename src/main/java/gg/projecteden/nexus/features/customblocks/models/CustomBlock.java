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
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.BlackConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.BlueConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.BrownConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.CyanConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.GrayConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.GreenConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.LightBlueConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.LightGrayConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.LimeConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.MagentaConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.OrangeConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.PinkConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.PurpleConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.RedConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.WhiteConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.YellowConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateA;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateB;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateC;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.GenericCrateD;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.AcaciaPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.BirchPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.CrimsonShroomLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.DarkOakPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.JunglePaperLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.OakPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.SprucePaperLantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.WarpedShroomLantern;
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
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.BlackPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.BluePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.BrownPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.CyanPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.GrayPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.GreenPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.LightBluePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.LightGrayPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.LimePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.MagentaPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.OrangePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.PinkPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.PurplePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.RedPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.WhitePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.YellowPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalAcaciaPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalBirchPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalCrimsonPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalDarkOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalJunglePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalSprucePlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.VerticalWarpedPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.BlackQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.BlueQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.BrownQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.CyanQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.GrayQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.GreenQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.LightBlueQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.LightGrayQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.LimeQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.MagentaQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.OrangeQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.PinkQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.PurpleQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.RedQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.WhiteQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.YellowQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks.AndesiteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks.DioriteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks.GraniteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledAndesite;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledDiorite;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledGranite;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.ChiseledStone;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.BlackTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.BlueTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.BrownTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.CyanTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.GrayTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.GreenTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.LightBlueTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.LightGrayTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.LimeTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.MagentaTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.OrangeTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.PinkTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.PurpleTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.RedTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.TerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.WhiteTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.YellowTerracottaShingles;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.NMSUtils.SoundType;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Instrument;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
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

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;

public enum CustomBlock implements Keyed {
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

	// crates
	APPLE_CRATE(AppleCrate.class),
	BEETROOT_CRATE(BeetrootCrate.class),
	BERRY_CRATE(SweetBerryCrate.class),
	CARROT_CRATE(CarrotCrate.class),
	POTATO_CRATE(PotatoCrate.class),

	// bundles
	BAMBOO_BUNDLE(BambooBundle.class),
	CACTUS_BUNDLE(CactusBundle.class),
	STICK_BUNDLE(StickBundle.class),
	SUGAR_CANE_BUNDLE(SugarCaneBundle.class),

	// lanterns
	OAK_PAPER_LANTERN(OakPaperLantern.class),
	SPRUCE_PAPER_LANTERN(SprucePaperLantern.class),
	BIRCH_PAPER_LANTERN(BirchPaperLantern.class),
	JUNGLE_PAPER_LANTERN(JunglePaperLantern.class),
	ACACIA_PAPER_LANTERN(AcaciaPaperLantern.class),
	DARK_OAK_PAPER_LANTERN(DarkOakPaperLantern.class),
	CRIMSON_SHROOM_LANTERN(CrimsonShroomLantern.class),
	WARPED_SHROOM_LANTERN(WarpedShroomLantern.class),

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

	// stone bricks
	ANDESITE_BRICKS(AndesiteBricks.class),
	DIORITE_BRICKS(DioriteBricks.class),
	GRANITE_BRICKS(GraniteBricks.class),

	// chiseled stone
	CHISELED_STONE(ChiseledStone.class),
	CHISELED_ANDESITE(ChiseledAndesite.class),
	CHISELED_DIORITE(ChiseledDiorite.class),
	CHISELED_GRANITE(ChiseledGranite.class),

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

	public @NonNull String getSound(SoundType type) {
		return switch (type) {
			case PLACE -> customBlock.getPlaceSound();
			case BREAK -> customBlock.getBreakSound();
			case STEP -> customBlock.getStepSound();
			case HIT -> customBlock.getHitSound();
			case FALL -> customBlock.getFallSound();
		};
	}

	public void playSound(SoundType type, Location location) {
		String sound = getSound(type);
		double volume = type.getVolume();

//		debug("CustomBlockSound: " + type + " - " + sound);

		SoundBuilder soundBuilder = new SoundBuilder(sound).location(location).volume(volume);
		BlockUtils.playSound(soundBuilder);
	}

	@Getter
	final List<NexusRecipe> recipes = new ArrayList<>();

	public void registerRecipes() {
		if (!(get() instanceof ICraftable craftable))
			return;

		// craft recipe
		@Nullable Pair<RecipeBuilder<?>, Integer> craftRecipePair = craftable.getCraftRecipe();
		if (craftRecipePair != null && craftRecipePair.getFirst() != null) {
			ItemStack toMakeItem = new ItemBuilder(craftable.getItemStack()).amount(craftRecipePair.getSecond()).build();
			NexusRecipe recipe = craftRecipePair.getFirst().toMake(toMakeItem).build().type(RecipeType.CUSTOM_BLOCKS);
			recipe.register();

			recipes.add(recipe);
		}

		// uncraft recipe
		if (craftable.getUncraftRecipe() != null) {
			NexusRecipe recipe = craftable.getUncraftRecipe().build().type(RecipeType.CUSTOM_BLOCKS);
			recipe.register();

			recipes.add(recipe);
		}

		// re-dye recipes
		if (craftable instanceof IDyeable dyeable) {
			CustomBlockTag tag = dyeable.getRedyeTag();

			final ColorType color = ColorType.of(this);
			if (color != null) {
				final Material dye = color.switchColor(Material.WHITE_DYE);
				final CustomBlockTag tagExcludingSelf = new CustomBlockTag(tag).exclude(this).key(tag);
				final NexusRecipe recipe = surround(dye).with(tagExcludingSelf).toMake(color.switchColor(tag), 8).build();
				recipe.type(RecipeType.DYES).register();
				recipes.add(recipe);
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
