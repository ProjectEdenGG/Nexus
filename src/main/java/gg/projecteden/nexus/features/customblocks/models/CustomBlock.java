package gg.projecteden.nexus.features.customblocks.models;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.common.ICraftable;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.IDyeable;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.bundle.BambooBundle;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.bundle.CactusBundle;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.bundle.StickBundle;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.bundle.SugarCaneBundle;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.crate.AppleCrate;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.crate.BeetrootCrate;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.crate.CarrotCrate;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.crate.PotatoCrate;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.crate.SweetBerryCrate;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.BlackConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.BlueConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.BrownConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.CyanConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.GrayConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.GreenConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.LightBlueConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.LightGrayConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.LimeConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.MagentaConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.OrangeConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.PinkConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.PurpleConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.RedConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.WhiteConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks.YellowConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.genericcrate.GenericCrateA;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.genericcrate.GenericCrateB;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.genericcrate.GenericCrateC;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.genericcrate.GenericCrateD;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.AcaciaPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.BirchPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.CrimsonShroomLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.DarkOakPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.JunglePaperLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.OakPaperLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.SprucePaperLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.lanterns.WarpedShroomLantern;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.misc.NoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.misc.ShojiBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedAcaciaPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedBirchPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedCrimsonPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedDarkOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedJunglePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedSprucePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.carved.CarvedWarpedPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.BlackPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.BluePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.BrownPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.CyanPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.GrayPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.GreenPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.LightBluePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.LightGrayPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.LimePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.MagentaPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.OrangePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.PinkPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.PurplePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.RedPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.WhitePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.colored.YellowPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalAcaciaPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalBirchPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalCrimsonPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalDarkOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalJunglePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalOakPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalSprucePlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical.VerticalWarpedPlanks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.BlackQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.BlueQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.BrownQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.CyanQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.GrayQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.GreenQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.LightBlueQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.LightGrayQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.LimeQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.MagentaQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.OrangeQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.PinkQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.PurpleQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.RedQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.WhiteQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool.YellowQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.bricks.AndesiteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.bricks.DioriteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.bricks.GraniteBricks;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled.ChiseledAndesite;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled.ChiseledDiorite;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled.ChiseledGranite;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled.ChiseledStone;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.BlackTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.BlueTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.BrownTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.CyanTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.GrayTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.GreenTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.LightBlueTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.LightGrayTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.LimeTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.MagentaTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.OrangeTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.PinkTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.PurpleTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.RedTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.TerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.WhiteTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles.YellowTerracottaShingles;
import gg.projecteden.nexus.features.customblocks.models.tripwire.TallSupport;
import gg.projecteden.nexus.features.customblocks.models.tripwire.Tripwire;
import gg.projecteden.nexus.features.customblocks.models.tripwire.TripwireCross;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.Cattail;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.NMSUtils.SoundType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;

public enum CustomBlock implements Keyed {

	// NOTE BLOCKS
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

	// TRIPWIRE

	// misc
	TRIPWIRE(Tripwire.class),
	TRIPWIRE_CROSS(TripwireCross.class),
	TALL_SUPPORT(TallSupport.class),

	// tall
	CATTAIL(Cattail.class),
	;

	private final ICustomBlock customBlock;
	@Getter
	private final CustomBlockType type;

	@SneakyThrows
	CustomBlock(Class<? extends ICustomBlock> clazz) {
		this.customBlock = (ICustomBlock) clazz.getDeclaredConstructors()[0].newInstance();

		if (this.customBlock instanceof ICustomNoteBlock)
			this.type = CustomBlockType.NOTE_BLOCK;
		else if (this.customBlock instanceof ICustomTripwire)
			this.type = CustomBlockType.TRIPWIRE;
		else
			this.type = CustomBlockType.UNKNOWN;
	}

	public static final HashMap<Integer, CustomBlock> modelIdMap = new HashMap<>();

	static {
		for (CustomBlock customBlock : values()) {
			modelIdMap.put(customBlock.get().getModelId(), customBlock);
		}
	}

	public ICustomBlock get() {
		return this.customBlock;
	}

	public static List<CustomBlock> getType(CustomBlockType type) {
		return Arrays.stream(values()).filter(customBlock -> customBlock.getType().equals(type)).collect(Collectors.toList());
	}

	@Override
	public @NotNull NamespacedKey getKey() {
		return new NamespacedKey(Nexus.getInstance(), name().toLowerCase());
	}

	public static @Nullable CustomBlock fromItemstack(ItemStack itemInHand) {
		int modelId = CustomModelData.of(itemInHand);
		if (itemInHand.getType().equals(Material.NOTE_BLOCK) && modelId == 0)
			return CustomBlock.NOTE_BLOCK;

		if (itemInHand.getType().equals(Material.STRING) && modelId == 0)
			return CustomBlock.TRIPWIRE;

		if (!itemInHand.getType().equals(Material.PAPER))
			return null;

		return fromModelId(modelId);
	}

	public static @Nullable CustomBlock fromModelId(int modelId) {
		return modelIdMap.getOrDefault(modelId, null);
	}

	public static @Nullable CustomBlock fromBlock(Block block) {
		if (Nullables.isNullOrAir(block))
			return null;

		return fromBlockData(block.getBlockData());
	}

	public static @Nullable CustomBlock fromBlockData(@NonNull BlockData blockData) {
		if (blockData instanceof org.bukkit.block.data.type.NoteBlock noteBlock) {
			List<CustomBlock> directional = new ArrayList<>();
			for (CustomBlock customBlock : getType(CustomBlockType.NOTE_BLOCK)) {
				ICustomBlock iCustomBlock = customBlock.get();

				if (CustomBlockUtils.equals(customBlock, noteBlock, false)) {
//					debug("CustomBlock: BlockData equals " + customBlock.name());
					return customBlock;
				} else if (iCustomBlock instanceof IDirectionalNoteBlock) {
					directional.add(customBlock);
				}
			}

			// Directional checks

			for (CustomBlock _customBlock : directional) {
				if (CustomBlockUtils.equals(_customBlock, noteBlock, true)) {
//					debug("CustomBlock: BlockData equals directional " + _customBlock.name());
					return _customBlock;
				}
			}

			debug("CustomBlock: Couldn't find NoteBlock: " + noteBlock);

		} else if (blockData instanceof org.bukkit.block.data.type.Tripwire tripwire) {
			// TODO TRIPWIRE
			for (CustomBlock customBlock : getType(CustomBlockType.TRIPWIRE)) {
				if (CustomBlockUtils.equals(customBlock, tripwire, true)) {
					return customBlock;
				}
			}

			debug("(TODO) CustomBlock: Couldn't find Tripwire: " + tripwire);
		}

		return null;
	}


	public boolean placeBlock(Player player, Block block, Block placeAgainst, BlockFace facing, ItemStack itemInHand) {
		ICustomBlock customBlock = this.get();

		Material blockMaterial = null;
		ItemStack item = null;
		boolean setup = false;
		switch (this.getType()) {
			case NOTE_BLOCK -> {
				blockMaterial = Material.NOTE_BLOCK;
				item = new ItemStack(Material.NOTE_BLOCK);
				setup = true;
			}
			case TRIPWIRE -> {
				blockMaterial = Material.TRIPWIRE;
				item = new ItemStack(Material.STRING);
				setup = true;
			}
		}

		if (setup) {
			boolean placed = BlockUtils.tryPlaceEvent(player, block, placeAgainst,
				blockMaterial, customBlock.getBlockData(facing), false, item);

			if (placed) {
				CustomBlockUtils.updateObservers(block);

				UUID uuid = player.getUniqueId();
				Location location = block.getLocation();

				CustomBlockUtils.placeBlockDatabase(uuid, this, location, facing);
				debug("CustomBlock: playing place sound");
				playSound(SoundType.PLACE, location);

				ItemUtils.subtract(player, itemInHand);
				player.swingMainHand();
				return true;
			}
		}

		return false;
	}

	public @Nullable String getSound(SoundType type) {
		ICustomBlock customBlock = this.get();

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
		if (Nullables.isNullOrEmpty(sound))
			return;

		double volume = type.getVolume();

		debug("CustomBlockSound: " + type + " - " + sound);

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

	public void breakBlock(Block block, boolean dropItem) {
		breakBlock(block.getLocation(), dropItem);
	}

	public void breakBlock(Location location, boolean dropItem) {
		debug("Break block");
		playSound(SoundType.BREAK, location);
		CustomBlockUtils.breakBlockDatabase(location);
		if (dropItem)
			dropItem(location);
	}

	public void dropItem(Location location) {
		location.getWorld().dropItemNaturally(location, this.get().getItemStack());
	}

	public enum CustomBlockType {
		UNKNOWN(Material.AIR),
		NOTE_BLOCK(Material.NOTE_BLOCK),
		TRIPWIRE(Material.TRIPWIRE, Material.STRING),
		;

		@Getter
		private final Set<Material> materials;

		CustomBlockType(Material... materials) {
			this.materials = new HashSet<>(List.of(materials));
		}

		public static Set<Material> getItemMaterials() {
			Set<Material> result = new HashSet<>();
			for (CustomBlockType type : values()) {
				result.addAll(type.getMaterials());
			}
			return result;
		}
	}


}
