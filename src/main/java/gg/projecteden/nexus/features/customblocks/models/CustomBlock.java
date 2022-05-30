package gg.projecteden.nexus.features.customblocks.models;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
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
import gg.projecteden.nexus.features.customblocks.models.noteblocks.misc.HazardBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.misc.NoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.misc.ShojiBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.misc.Wireframe;
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
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar.AndesitePillar;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar.BlackstonePillar;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar.DeepslatePillar;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar.DioritePillar;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar.GranitePillar;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar.StoneBricksPillar;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar.StonePillar;
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
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.IWaterLogged;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.FungusCover;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.aubrieta.BlueAubrieta;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.aubrieta.OrangeAubrieta;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.aubrieta.PinkAubrieta;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.aubrieta.PurpleAubrieta;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.aubrieta.RainbowAubrieta;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.aubrieta.WhiteAubrieta;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.pebbles.Pebbles_0;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.pebbles.Pebbles_1;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.pebbles.Pebbles_2;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.rocks.Rocks_0;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.rocks.Rocks_1;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.rocks.Rocks_2;
import gg.projecteden.nexus.features.customblocks.models.tripwire.misc.Lavender;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.BlueSage;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.Bluebell;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.Cattail;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.ITall;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.MountainLaurel;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.OrangeGladiolus;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.PurpleHibiscus;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.TallSupport;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tripwire.Tripwire;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tripwire.TripwireCross;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
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

	// bricks
	ANDESITE_BRICKS(AndesiteBricks.class),
	DIORITE_BRICKS(DioriteBricks.class),
	GRANITE_BRICKS(GraniteBricks.class),

	// chiseled
	CHISELED_STONE(ChiseledStone.class),
	CHISELED_ANDESITE(ChiseledAndesite.class),
	CHISELED_DIORITE(ChiseledDiorite.class),
	CHISELED_GRANITE(ChiseledGranite.class),

	// pillar
	STONE_PILLAR(StonePillar.class),
	ANDESITE_PILLAR(AndesitePillar.class),
	DIORITE_PILLAR(DioritePillar.class),
	GRANITE_PILLAR(GranitePillar.class),
	STONE_BRICKS_PILLAR(StoneBricksPillar.class),
	DEEPSLATE_PILLAR(DeepslatePillar.class),
	BLACKSTONE_PILLAR(BlackstonePillar.class),

	// generic crates
	GENERIC_CRATE_A(GenericCrateA.class),
	GENERIC_CRATE_B(GenericCrateB.class),
	GENERIC_CRATE_C(GenericCrateC.class),
	GENERIC_CRATE_D(GenericCrateD.class),

	// misc
	NOTE_BLOCK(NoteBlock.class),
	SHOJI_BLOCK(ShojiBlock.class),
	HAZARD_BLOCK(HazardBlock.class),
	WIREFRAME(Wireframe.class),

	// TRIPWIRE

	// misc
	TRIPWIRE(Tripwire.class),
	TRIPWIRE_CROSS(TripwireCross.class),
	TALL_SUPPORT(TallSupport.class),

	// flora tall
	CATTAIL(Cattail.class),
	BLUE_SAGE(BlueSage.class),
	ORANGE_GLADIOLUS(OrangeGladiolus.class),
	MOUNTAIN_LAUREL(MountainLaurel.class),
	BLUEBELL(Bluebell.class),
	PURPLE_HIBISCUS(PurpleHibiscus.class),

	// flora short
	LAVENDER(Lavender.class),

	// rocks
	ROCKS_0(Rocks_0.class),
	ROCKS_1(Rocks_1.class),
	ROCKS_2(Rocks_2.class),
	PEBBLES_0(Pebbles_0.class),
	PEBBLES_1(Pebbles_1.class),
	PEBBLES_2(Pebbles_2.class),

	// cover
	AUBRIETA_ORANGE(OrangeAubrieta.class),
	AUBRIETA_BLUE(BlueAubrieta.class),
	AUBRIETA_PURPLE(PurpleAubrieta.class),
	AUBRIETA_PINK(PinkAubrieta.class),
	AUBRIETA_WHITE(WhiteAubrieta.class),
	AUBRIETA_RAINBOW(RainbowAubrieta.class),
	FUNGUS_COVER(FungusCover.class), // TODO: make model 3d

	/*
		TODO:
		 - FLOWER + FUNGUS COVER --> HOW TO OBTAIN?
		 - LOTUS LILLY FLOWER --> HOW TO OBTAIN?
		 - HAZARD, WIREFRAME, DOTS
	 */;

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

		return fromBlockData(block.getBlockData(), block.getRelative(BlockFace.DOWN));
	}

	public static @Nullable CustomBlock fromBlockData(@NonNull BlockData blockData, Block underneath) {
		if (blockData instanceof org.bukkit.block.data.type.NoteBlock noteBlock) {
			List<CustomBlock> directional = new ArrayList<>();
			for (CustomBlock customBlock : getType(CustomBlockType.NOTE_BLOCK)) {
				ICustomBlock iCustomBlock = customBlock.get();

				if (CustomBlockUtils.equals(customBlock, noteBlock, false, underneath)) {
//					debug("CustomBlock: BlockData equals " + customBlock.name());
					return customBlock;
				} else if (iCustomBlock instanceof IDirectionalNoteBlock) {
					directional.add(customBlock);
				}
			}

			// Directional checks

			for (CustomBlock _customBlock : directional) {
				if (CustomBlockUtils.equals(_customBlock, noteBlock, true, underneath)) {
//					debug("CustomBlock: BlockData equals directional " + _customBlock.name());
					return _customBlock;
				}
			}

			debug("CustomBlock: Couldn't find NoteBlock: " + noteBlock);

		} else if (blockData instanceof org.bukkit.block.data.type.Tripwire tripwire) {
			for (CustomBlock customBlock : getType(CustomBlockType.TRIPWIRE)) {
				if (CustomBlockUtils.equals(customBlock, tripwire, true, underneath)) {
					return customBlock;
				}
			}

			debug("CustomBlock: Couldn't find Tripwire: " + tripwire);
		}

		return null;
	}

	public boolean placeBlock(Player player, Block block, Block placeAgainst, BlockFace facing, ItemStack itemInHand) {
		ICustomBlock customBlock = this.get();

		Material blockMaterial = customBlock.getVanillaBlockMaterial();
		ItemStack item = new ItemStack(customBlock.getVanillaItemMaterial());
		BlockFace facingFinal = facing;
		boolean placeTallSupport = false;

		boolean setup = false;
		switch (this.getType()) {
			case NOTE_BLOCK -> setup = true;
			case TRIPWIRE -> {
				if (customBlock instanceof ITall) {
					Block above = block.getRelative(BlockFace.UP);

					if (!(customBlock instanceof IWaterLogged))
						placeTallSupport = true;
					else if (placeAgainst.getType() != Material.WATER)
						placeTallSupport = true;

					if (placeTallSupport && !Nullables.isNullOrAir(above)) {
						return false;
					}
				}

				facingFinal = BlockUtils.getNextCardinalBlockFace(BlockUtils.getCardinalBlockFace(player));
				setup = true;
			}
		}

		if (setup) {
			BlockData blockData = customBlock.getBlockData(facingFinal, placeAgainst);
			debug("Placing Block: " + this.name());

			if (BlockUtils.tryPlaceEvent(player, block, placeAgainst, blockMaterial, blockData, false, item)) {
				CustomBlockUtils.updateObservers(block);
				CustomBlockUtils.placeBlockDatabase(player.getUniqueId(), this, block.getLocation(), facingFinal);
				if (placeTallSupport)
					tallSupport(player, block, facingFinal);

				playSound(player, SoundAction.PLACE, block.getLocation());

				player.swingMainHand();
				ItemUtils.subtract(player, itemInHand);
				return true;
			}
		}

		return false;
	}

	private void tallSupport(Player player, Block block, BlockFace facingFinal) {
		CustomBlock _tallSupport = CustomBlock.TALL_SUPPORT;
		ICustomBlock tallSupport = _tallSupport.get();
		Material _blockMaterial = tallSupport.getVanillaBlockMaterial();
		ItemStack _item = new ItemStack(tallSupport.getVanillaItemMaterial());

		Block _block = block.getRelative(BlockFace.UP);
		BlockData _blockData = tallSupport.getBlockData(facingFinal, block);

		if (BlockUtils.tryPlaceEvent(player, _block, block, _blockMaterial, _blockData, false, _item)) {
			CustomBlockUtils.updateObservers(block);
			CustomBlockUtils.placeBlockDatabase(player.getUniqueId(), _tallSupport, _block.getLocation(), facingFinal);
		}
	}

	public void incrementBlock(Player player, CustomBlock newCustomBlock, Block block) {
		if (newCustomBlock == null || !(this.get() instanceof IIncremental) || !(newCustomBlock.get() instanceof IIncremental))
			return;

		Block underneath = block.getRelative(BlockFace.DOWN);
		BlockFace facing = CustomBlockUtils.getFacing(this, block.getBlockData(), underneath);
		BlockData newBlockData = newCustomBlock.customBlock.getBlockData(facing, underneath);

		Location location = block.getLocation();
		UUID uuid = player.getUniqueId();

		CustomBlockUtils.breakBlockDatabase(location);

		block.setType(newCustomBlock.get().getVanillaBlockMaterial(), false);
		block.setBlockData(newBlockData, false);
		playSound(player, SoundAction.PLACE, block.getLocation());

		CustomBlockUtils.placeBlockDatabase(uuid, newCustomBlock, location, facing);
		CustomBlockUtils.updateObservers(block);

		// TODO: update logs
	}

	public void breakBlock(Player source, Block block, boolean dropItem, boolean playSound, boolean spawnParticle) {
		breakBlock(source, block.getLocation(), true, dropItem, playSound, spawnParticle);
	}

	public void breakBlock(Player source, Location location, boolean dropItem, boolean playSound, boolean spawnParticle) {
		breakBlock(source, location, true, dropItem, playSound, spawnParticle);
	}

	private void breakBlock(Player source, Location location, boolean updateDatabase, boolean dropItem, boolean playSound, boolean spawnParticle) {
		if (updateDatabase)
			CustomBlockUtils.breakBlockDatabase(location);

		if (this != TALL_SUPPORT) {
			if (spawnParticle && this.get() instanceof ICustomTripwire)
				spawnParticle(location);

			if (playSound)
				playSound(source, SoundAction.BREAK, location);

			if (dropItem)
				dropItem(location);
		} else {
			CustomBlock below = CustomBlock.fromBlock(location.getBlock().getRelative(BlockFace.DOWN));
			if (below == null) return;

			below.breakBlock(source, location, false, false, playSound, spawnParticle);
		}
	}


	public @Nullable String getSound(SoundAction type) {
		ICustomBlock customBlock = this.get();

		return switch (type) {
			case PLACE -> customBlock.getPlaceSound();
			case BREAK -> customBlock.getBreakSound();
			case STEP -> customBlock.getStepSound();
			case HIT -> customBlock.getHitSound();
			case FALL -> customBlock.getFallSound();
		};
	}

	public void playSound(@Nullable Player source, SoundAction type, Location location) {
		// TODO: vanish check on source to determine to play sound or not

		String sound = getSound(type);
		if (Nullables.isNullOrEmpty(sound))
			return;

		double volume = type.getVolume();

//		debug("&eCustomBlockSound:&f " + type + " - " + sound);

		SoundBuilder soundBuilder = new SoundBuilder(sound).location(location).volume(volume);
		BlockUtils.playSound(soundBuilder);
	}

	private void spawnParticle(Location loc) {
		World world = loc.getWorld();
		loc = loc.toCenterLocation();
		Particle particle = Particle.ITEM_CRACK;
		ItemStack itemStack = get().getItemStack();

		world.spawnParticle(particle, loc, 25, 0.25, 0.25, 0.25, 0.1, itemStack);
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

		// other recipes
		if (!craftable.getOtherRecipes().isEmpty()) {
			for (NexusRecipe recipe : craftable.getOtherRecipes()) {
				recipe.register();
				recipes.add(recipe);
			}
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
