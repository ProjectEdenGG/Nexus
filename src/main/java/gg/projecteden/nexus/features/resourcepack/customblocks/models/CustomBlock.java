package gg.projecteden.nexus.features.resourcepack.customblocks.models;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICraftable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDirectional;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDyeable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle.CactusBundle;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle.StickBundle;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle.SugarCaneBundle;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate.AppleCrate;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate.BeetrootCrate;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate.CarrotCrate;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate.PotatoCrate;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate.SweetBerryCrate;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.BlackConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.BlueConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.BrownConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.CyanConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.GrayConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.GreenConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.LightBlueConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.LightGrayConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.LimeConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.MagentaConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.OrangeConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.PinkConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.PurpleConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.RedConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.WhiteConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks.YellowConcreteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.genericcrate.GenericCrateA;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.genericcrate.GenericCrateB;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.genericcrate.GenericCrateC;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.genericcrate.GenericCrateD;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.AcaciaPaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.BambooPaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.BirchPaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.CherryPaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.CrimsonShroomLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.DarkOakPaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.JunglePaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.OakPaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.SprucePaperLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns.WarpedShroomLantern;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc.HazardBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc.NoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc.ShojiBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc.Wireframe;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedAcaciaPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedBirchPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedCherryPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedCrimsonPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedDarkOakPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedJunglePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedMangrovePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedOakPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedSprucePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved.CarvedWarpedPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.BlackPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.BluePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.BrownPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.CyanPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.GrayPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.GreenPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.LightBluePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.LightGrayPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.LimePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.MagentaPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.OrangePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.PinkPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.PurplePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.RedPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.WhitePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored.YellowPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalAcaciaPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalBambooPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalBirchPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalCherryPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalCrimsonPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalDarkOakPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalJunglePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalMangrovePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalOakPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalSprucePlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical.VerticalWarpedPlanks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.BlackQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.BlueQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.BrownQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.CyanQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.GrayQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.GreenQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.LightBlueQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.LightGrayQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.LimeQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.MagentaQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.OrangeQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.PinkQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.PurpleQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.RedQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.WhiteQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool.YellowQuiltedWool;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.bricks.AndesiteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.bricks.DioriteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.bricks.GraniteBricks;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled.ChiseledAndesite;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled.ChiseledDiorite;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled.ChiseledGranite;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled.ChiseledPurpur;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled.ChiseledStone;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar.AndesiteStonePillar;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar.BlackstoneStonePillar;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar.DeepslateStonePillar;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar.DioriteStonePillar;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar.GraniteStonePillar;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar.StoneBricksStonePillar;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar.StoneStonePillar;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.BlackTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.BlueTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.BrownTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.CyanTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.GrayTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.GreenTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.LightBlueTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.LightGrayTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.LimeTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.MagentaTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.OrangeTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.PinkTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.PurpleTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.RedTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.TerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.WhiteTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles.YellowTerracottaShingles;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IWaterLogged;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.FungusCover;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.DarkBlueAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.LightBlueAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.OrangeAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.PinkAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.PurpleAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.RainbowAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.RedAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.WhiteAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta.YellowAubrieta;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles.Pebbles_0;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles.Pebbles_1;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles.Pebbles_2;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.rocks.Rocks_0;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.rocks.Rocks_1;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.rocks.Rocks_2;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.misc.Lavender;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.BlueSage;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.Bluebell;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.Cattail;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.ITall;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.MountainLaurel;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.OrangeGladiolus;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.PurpleHibiscus;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.TallSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tripwire.IActualTripwire;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tripwire.Tripwire;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tripwire.TripwireCross;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum CustomBlock implements Keyed {

	// NOTE BLOCKS
	// terracotta shingles
	TERRACOTTA_SHINGLES(TerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	RED_TERRACOTTA_SHINGLES(RedTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	ORANGE_TERRACOTTA_SHINGLES(OrangeTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	YELLOW_TERRACOTTA_SHINGLES(YellowTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	LIME_TERRACOTTA_SHINGLES(LimeTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	GREEN_TERRACOTTA_SHINGLES(GreenTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	CYAN_TERRACOTTA_SHINGLES(CyanTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	LIGHT_BLUE_TERRACOTTA_SHINGLES(LightBlueTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	BLUE_TERRACOTTA_SHINGLES(BlueTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	PURPLE_TERRACOTTA_SHINGLES(PurpleTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	MAGENTA_TERRACOTTA_SHINGLES(MagentaTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	PINK_TERRACOTTA_SHINGLES(PinkTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	BROWN_TERRACOTTA_SHINGLES(BrownTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	BLACK_TERRACOTTA_SHINGLES(BlackTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	GRAY_TERRACOTTA_SHINGLES(GrayTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	LIGHT_GRAY_TERRACOTTA_SHINGLES(LightGrayTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),
	WHITE_TERRACOTTA_SHINGLES(WhiteTerracottaShingles.class, CustomBlockTab.TERRACOTTA_SHINGLES),

	// concrete bricks
	RED_CONCRETE_BRICKS(RedConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	ORANGE_CONCRETE_BRICKS(OrangeConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	YELLOW_CONCRETE_BRICKS(YellowConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	LIME_CONCRETE_BRICKS(LimeConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	GREEN_CONCRETE_BRICKS(GreenConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	CYAN_CONCRETE_BRICKS(CyanConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	LIGHT_BLUE_CONCRETE_BRICKS(LightBlueConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	BLUE_CONCRETE_BRICKS(BlueConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	PURPLE_CONCRETE_BRICKS(PurpleConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	MAGENTA_CONCRETE_BRICKS(MagentaConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	PINK_CONCRETE_BRICKS(PinkConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	BROWN_CONCRETE_BRICKS(BrownConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	BLACK_CONCRETE_BRICKS(BlackConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	GRAY_CONCRETE_BRICKS(GrayConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	LIGHT_GRAY_CONCRETE_BRICKS(LightGrayConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),
	WHITE_CONCRETE_BRICKS(WhiteConcreteBricks.class, CustomBlockTab.CONCRETE_BRICKS),

	// colored planks
	RED_PLANKS(RedPlanks.class, CustomBlockTab.COLORED_PLANKS),
	ORANGE_PLANKS(OrangePlanks.class, CustomBlockTab.COLORED_PLANKS),
	YELLOW_PLANKS(YellowPlanks.class, CustomBlockTab.COLORED_PLANKS),
	LIME_PLANKS(LimePlanks.class, CustomBlockTab.COLORED_PLANKS),
	GREEN_PLANKS(GreenPlanks.class, CustomBlockTab.COLORED_PLANKS),
	CYAN_PLANKS(CyanPlanks.class, CustomBlockTab.COLORED_PLANKS),
	LIGHT_BLUE_PLANKS(LightBluePlanks.class, CustomBlockTab.COLORED_PLANKS),
	BLUE_PLANKS(BluePlanks.class, CustomBlockTab.COLORED_PLANKS),
	PURPLE_PLANKS(PurplePlanks.class, CustomBlockTab.COLORED_PLANKS),
	MAGENTA_PLANKS(MagentaPlanks.class, CustomBlockTab.COLORED_PLANKS),
	PINK_PLANKS(PinkPlanks.class, CustomBlockTab.COLORED_PLANKS),
	BROWN_PLANKS(BrownPlanks.class, CustomBlockTab.COLORED_PLANKS),
	BLACK_PLANKS(BlackPlanks.class, CustomBlockTab.COLORED_PLANKS),
	GRAY_PLANKS(GrayPlanks.class, CustomBlockTab.COLORED_PLANKS),
	LIGHT_GRAY_PLANKS(LightGrayPlanks.class, CustomBlockTab.COLORED_PLANKS),
	WHITE_PLANKS(WhitePlanks.class, CustomBlockTab.COLORED_PLANKS),

	// quilted wool
	RED_QUILTED_WOOL(RedQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	ORANGE_QUILTED_WOOL(OrangeQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	YELLOW_QUILTED_WOOL(YellowQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	LIME_QUILTED_WOOL(LimeQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	GREEN_QUILTED_WOOL(GreenQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	CYAN_QUILTED_WOOL(CyanQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	LIGHT_BLUE_QUILTED_WOOL(LightBlueQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	BLUE_QUILTED_WOOL(BlueQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	PURPLE_QUILTED_WOOL(PurpleQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	MAGENTA_QUILTED_WOOL(MagentaQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	PINK_QUILTED_WOOL(PinkQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	BROWN_QUILTED_WOOL(BrownQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	BLACK_QUILTED_WOOL(BlackQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	GRAY_QUILTED_WOOL(GrayQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	LIGHT_GRAY_QUILTED_WOOL(LightGrayQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),
	WHITE_QUILTED_WOOL(WhiteQuiltedWool.class, CustomBlockTab.QUILTED_WOOL),

	// crates
	APPLE_CRATE(AppleCrate.class, CustomBlockTab.COMPACTED),
	BEETROOT_CRATE(BeetrootCrate.class, CustomBlockTab.COMPACTED),
	BERRY_CRATE(SweetBerryCrate.class, CustomBlockTab.COMPACTED),
	CARROT_CRATE(CarrotCrate.class, CustomBlockTab.COMPACTED),
	POTATO_CRATE(PotatoCrate.class, CustomBlockTab.COMPACTED),

	// bundles
	CACTUS_BUNDLE(CactusBundle.class, CustomBlockTab.COMPACTED),
	STICK_BUNDLE(StickBundle.class, CustomBlockTab.COMPACTED),
	SUGAR_CANE_BUNDLE(SugarCaneBundle.class, CustomBlockTab.COMPACTED),

	// lanterns
	OAK_PAPER_LANTERN(OakPaperLantern.class, CustomBlockTab.LANTERNS),
	SPRUCE_PAPER_LANTERN(SprucePaperLantern.class, CustomBlockTab.LANTERNS),
	BIRCH_PAPER_LANTERN(BirchPaperLantern.class, CustomBlockTab.LANTERNS),
	JUNGLE_PAPER_LANTERN(JunglePaperLantern.class, CustomBlockTab.LANTERNS),
	ACACIA_PAPER_LANTERN(AcaciaPaperLantern.class, CustomBlockTab.LANTERNS),
	DARK_OAK_PAPER_LANTERN(DarkOakPaperLantern.class, CustomBlockTab.LANTERNS),
	CRIMSON_SHROOM_LANTERN(CrimsonShroomLantern.class, CustomBlockTab.LANTERNS),
	WARPED_SHROOM_LANTERN(WarpedShroomLantern.class, CustomBlockTab.LANTERNS),
	CHERRY_PAPER_LANTERN(CherryPaperLantern.class, CustomBlockTab.LANTERNS),
	BAMBOO_PAPER_LANTERN(BambooPaperLantern.class, CustomBlockTab.LANTERNS),

	// vertical planks
	VERTICAL_OAK_PLANKS(VerticalOakPlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_SPRUCE_PLANKS(VerticalSprucePlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_BIRCH_PLANKS(VerticalBirchPlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_JUNGLE_PLANKS(VerticalJunglePlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_ACACIA_PLANKS(VerticalAcaciaPlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_DARK_OAK_PLANKS(VerticalDarkOakPlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_CRIMSON_PLANKS(VerticalCrimsonPlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_WARPED_PLANKS(VerticalWarpedPlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_MANGROVE_PLANKS(VerticalMangrovePlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_CHERRY_PLANKS(VerticalCherryPlanks.class, CustomBlockTab.VERTICAL_PLANKS),
	VERTICAL_BAMBOO_PLANKS(VerticalBambooPlanks.class, CustomBlockTab.VERTICAL_PLANKS),

	// carved planks
	CARVED_OAK_PLANKS(CarvedOakPlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_SPRUCE_PLANKS(CarvedSprucePlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_BIRCH_PLANKS(CarvedBirchPlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_JUNGLE_PLANKS(CarvedJunglePlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_ACACIA_PLANKS(CarvedAcaciaPlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_DARK_OAK_PLANKS(CarvedDarkOakPlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_CRIMSON_PLANKS(CarvedCrimsonPlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_WARPED_PLANKS(CarvedWarpedPlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_MANGROVE_PLANKS(CarvedMangrovePlanks.class, CustomBlockTab.CARVED_PLANKS),
	CARVED_CHERRY_PLANKS(CarvedCherryPlanks.class, CustomBlockTab.CARVED_PLANKS),

	// bricks
	ANDESITE_BRICKS(AndesiteBricks.class, CustomBlockTab.STONE_BRICKS),
	DIORITE_BRICKS(DioriteBricks.class, CustomBlockTab.STONE_BRICKS),
	GRANITE_BRICKS(GraniteBricks.class, CustomBlockTab.STONE_BRICKS),

	// chiseled
	CHISELED_STONE(ChiseledStone.class, CustomBlockTab.CHISELED_STONE),
	CHISELED_ANDESITE(ChiseledAndesite.class, CustomBlockTab.CHISELED_STONE),
	CHISELED_DIORITE(ChiseledDiorite.class, CustomBlockTab.CHISELED_STONE),
	CHISELED_GRANITE(ChiseledGranite.class, CustomBlockTab.CHISELED_STONE),
	CHISELED_PURPUR(ChiseledPurpur.class, CustomBlockTab.CHISELED_STONE),

	// pillar
	STONE_PILLAR(StoneStonePillar.class, CustomBlockTab.STONE_PILLARS),
	ANDESITE_PILLAR(AndesiteStonePillar.class, CustomBlockTab.STONE_PILLARS),
	DIORITE_PILLAR(DioriteStonePillar.class, CustomBlockTab.STONE_PILLARS),
	GRANITE_PILLAR(GraniteStonePillar.class, CustomBlockTab.STONE_PILLARS),
	STONE_BRICKS_PILLAR(StoneBricksStonePillar.class, CustomBlockTab.STONE_PILLARS),
	DEEPSLATE_PILLAR(DeepslateStonePillar.class, CustomBlockTab.STONE_PILLARS),
	BLACKSTONE_PILLAR(BlackstoneStonePillar.class, CustomBlockTab.STONE_PILLARS),

	// generic crates
	GENERIC_CRATE_A(GenericCrateA.class, CustomBlockTab.GENERIC_CRATES),
	GENERIC_CRATE_B(GenericCrateB.class, CustomBlockTab.GENERIC_CRATES),
	GENERIC_CRATE_C(GenericCrateC.class, CustomBlockTab.GENERIC_CRATES),
	GENERIC_CRATE_D(GenericCrateD.class, CustomBlockTab.GENERIC_CRATES),

	// misc
	NOTE_BLOCK(NoteBlock.class, CustomBlockTab.NONE),
	HAZARD_BLOCK(HazardBlock.class, CustomBlockTab.MISC),
	SHOJI_BLOCK(ShojiBlock.class, CustomBlockTab.MISC),
	WIREFRAME(Wireframe.class, CustomBlockTab.MISC),

	// TRIPWIRE

	// misc
	TRIPWIRE(Tripwire.class, CustomBlockTab.NONE),
	TRIPWIRE_CROSS(TripwireCross.class, CustomBlockTab.NONE),
	TALL_SUPPORT(TallSupport.class, CustomBlockTab.NONE),

	// flora tall
	CATTAIL(Cattail.class, CustomBlockTab.FLORA),
	BLUE_SAGE(BlueSage.class, CustomBlockTab.FLORA),
	ORANGE_GLADIOLUS(OrangeGladiolus.class, CustomBlockTab.FLORA),
	MOUNTAIN_LAUREL(MountainLaurel.class, CustomBlockTab.FLORA),
	BLUEBELL(Bluebell.class, CustomBlockTab.FLORA),
	PURPLE_HIBISCUS(PurpleHibiscus.class, CustomBlockTab.FLORA),

	// flora short
	LAVENDER(Lavender.class, CustomBlockTab.FLORA),

	// rocks
	ROCKS_0(Rocks_0.class, CustomBlockTab.ROCKS),
	ROCKS_1(Rocks_1.class, CustomBlockTab.ROCKS),
	ROCKS_2(Rocks_2.class, CustomBlockTab.ROCKS),
	PEBBLES_0(Pebbles_0.class, CustomBlockTab.ROCKS),
	PEBBLES_1(Pebbles_1.class, CustomBlockTab.ROCKS),
	PEBBLES_2(Pebbles_2.class, CustomBlockTab.ROCKS),

	// cover
	AUBRIETA_RED(RedAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_ORANGE(OrangeAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_YELLOW(YellowAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_LIGHT_BLUE(LightBlueAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_DARK_BLUE(DarkBlueAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_PURPLE(PurpleAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_PINK(PinkAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_WHITE(WhiteAubrieta.class, CustomBlockTab.FLORA),
	AUBRIETA_RAINBOW(RainbowAubrieta.class, CustomBlockTab.FLORA),
	FUNGUS_COVER(FungusCover.class, CustomBlockTab.FLORA),
	;

	private final ICustomBlock customBlock;
	@Getter
	private final CustomBlockType type;
	@Getter
	private final CustomBlockTab creativeTab;

	@SneakyThrows
	CustomBlock(Class<? extends ICustomBlock> clazz, CustomBlockTab tab) {
		this.creativeTab = tab;
		this.customBlock = (ICustomBlock) clazz.getDeclaredConstructors()[0].newInstance();

		if (this.customBlock instanceof ICustomNoteBlock)
			this.type = CustomBlockType.NOTE_BLOCK;
		else if (this.customBlock instanceof ICustomTripwire)
			this.type = CustomBlockType.TRIPWIRE;
		else
			this.type = CustomBlockType.UNKNOWN;
	}

	public static final HashMap<String, CustomBlock> modelIdMap = new HashMap<>();

	public static void init() {
		for (CustomBlock customBlock : values())
			modelIdMap.put(customBlock.get().getModel(), customBlock);

		CustomBlockTag.init();
		CustomBlockTab.init();
		CustomBlockType.init();
		NoteBlockInstrument.init();
		CustomToolBlock.init();
	}

	public static CustomBlock valueofObtainable(String name) {
		CustomBlock customBlock = valueOf(name);
		if (!customBlock.isObtainable())
			throw new IllegalArgumentException("Custom Block: " + name + " is not obtainable!");

		return customBlock;
	}

	public ICustomBlock get() {
		return this.customBlock;
	}

	public static CustomBlock of(ICustomBlock iCustomBlock) {
		for (CustomBlock block : values())
			if (block.get().getClass() == iCustomBlock.getClass())
				return block;

		return null;
	}

	public boolean isObtainable() {
		return customBlock.getClass().getAnnotation(Unobtainable.class) == null;
	}

	public static List<CustomBlock> getBy(CustomBlockType type) {
		return Arrays.stream(values()).filter(customBlock -> customBlock.getType() == type).collect(Collectors.toList());
	}

	public static List<CustomBlock> getBy(CustomBlockTab tab) {
		return Arrays.stream(values()).filter(customBlock -> customBlock.getCreativeTab() == tab).collect(Collectors.toList());
	}

	public static List<CustomBlock> matching(String filter) {
		return Arrays.stream(values())
			.filter(customBlock -> customBlock.name().toLowerCase().contains(filter))
			.collect(Collectors.toList());
	}

	public static List<CustomBlock> getObtainable() {
		return Arrays.stream(values()).filter(CustomBlock::isObtainable).collect(Collectors.toList());
	}

	@Override
	public @NotNull NamespacedKey getKey() {
		return new NamespacedKey(Nexus.getInstance(), name().toLowerCase());
	}

	public static @Nullable CustomBlock from(ItemStack itemInHand) {
		String modelId = Model.of(itemInHand);
		if (itemInHand.getType().equals(Material.NOTE_BLOCK) && modelId == null)
			return CustomBlock.NOTE_BLOCK;

		if (itemInHand.getType().equals(Material.STRING) && modelId == null)
			return CustomBlock.TRIPWIRE;

		if (!itemInHand.getType().equals(Material.PAPER))
			return null;

		return from(modelId);
	}

	public static @Nullable CustomBlock from(String modelId) {
		return modelIdMap.getOrDefault(modelId, null);
	}

	public static @Nullable CustomBlock from(Block block) {
		if (Nullables.isNullOrAir(block))
			return null;

		return from(block.getBlockData(), block.getRelative(BlockFace.DOWN));
	}

	public static @Nullable CustomBlock from(BlockData blockData, Block underneath) {
		if (blockData == null)
			return null;

		if (blockData instanceof org.bukkit.block.data.type.NoteBlock noteBlock) {
			List<CustomBlock> directional = new ArrayList<>();
			for (CustomBlock customBlock : getBy(CustomBlockType.NOTE_BLOCK)) {
				ICustomBlock iCustomBlock = customBlock.get();

				if (CustomBlockUtils.equals(customBlock, noteBlock, false, underneath)) {
//					CustomBlocksLang.debug("CustomBlock: BlockData equals " + customBlock.name());
					return customBlock;
				} else if (iCustomBlock instanceof IDirectionalNoteBlock) {
					directional.add(customBlock);
				}
			}

			// Directional checks
			for (CustomBlock _customBlock : directional) {
				if (CustomBlockUtils.equals(_customBlock, noteBlock, true, underneath)) {
//					CustomBlocksLang.debug("CustomBlock: BlockData equals directional " + _customBlock.name());
					return _customBlock;
				}
			}

//			CustomBlocksLang.debug("CustomBlock: Couldn't find NoteBlock: " + noteBlock);

		} else if (blockData instanceof org.bukkit.block.data.type.Tripwire tripwire) {
			for (CustomBlock customBlock : getBy(CustomBlockType.TRIPWIRE)) {
				// TODO: Disable tripwire customblocks
				if (ICustomTripwire.isNotEnabled())
					return null;
				//

				boolean directional = customBlock.get() instanceof IDirectional;

				if (CustomBlockUtils.equals(customBlock, tripwire, directional, underneath)) {
					return customBlock;
				}
			}

//			CustomBlocksLang.debug("CustomBlock: Couldn't find Tripwire: " + CustomBlockUtils.getBlockDataString(tripwire));
		}

		return null;
	}

	public boolean placeBlock(Player player, Block block, Block placeAgainst, BlockFace facing, ItemStack itemInHand) {
		ICustomBlock customBlock = this.get();

		Material blockMaterial = customBlock.getVanillaBlockMaterial();
		ItemStack item = new ItemStack(customBlock.getVanillaItemMaterial());
		BlockFace facingFinal = facing;
		boolean placeTallSupport = false;
		boolean checkNeighbors = false;

		boolean setup = false;
		switch (this.getType()) {
			case NOTE_BLOCK -> setup = true;
			case TRIPWIRE -> {
				CustomBlockUtils.debug(player, "&e- tripwire handling...");
				// TODO: Disable tripwire customblocks
				if (ICustomTripwire.isNotEnabled())
					return false;
				//

				// ITall
				if (customBlock instanceof ITall) {
					if (!(customBlock instanceof IWaterLogged))
						placeTallSupport = true;
					else if (placeAgainst.getType() != Material.WATER)
						placeTallSupport = true;
				}

				// Cross
				if (customBlock instanceof IActualTripwire) {
					checkNeighbors = true;

					// check self
					if (shouldConvert(block))
						customBlock = CustomBlock.TRIPWIRE_CROSS.get();
				}

				// Setup done
				facingFinal = BlockUtils.getNextCardinalBlockFace(BlockUtils.getCardinalBlockFace(player));
				setup = true;
			}
		}

		if (setup) {
			BlockData blockData = customBlock.getBlockData(facingFinal, placeAgainst);
			CustomBlockUtils.debug(player, "&e- placing: " + StringUtils.camelCase(this));

			if (BlockUtils.tryPlaceEvent(player, block, placeAgainst, blockMaterial, blockData, false, item)) {
				player.swingMainHand();
				playSound(player, SoundAction.PLACE, block.getLocation());
				ItemUtils.subtract(player, itemInHand);

				CustomBlockUtils.updateObservers(block, player);
				if (this == NOTE_BLOCK)
					CustomBlockUtils.placeNoteBlockInDatabase(block.getLocation(), blockData);

				if (placeTallSupport)
					tallSupport(player, block, facingFinal);

				if (checkNeighbors)
					checkNeighbors(player, block, blockData);

				return true;
			}
		}

		return false;
	}

	private void tallSupport(Player player, Block block, BlockFace facingFinal) {
		CustomBlockUtils.debug(player, "&e - placing tall support");
		ICustomBlock tallSupport = CustomBlock.TALL_SUPPORT.get();
		Material _blockMaterial = tallSupport.getVanillaBlockMaterial();
		ItemStack _item = new ItemStack(tallSupport.getVanillaItemMaterial());

		Block _block = block.getRelative(BlockFace.UP);
		BlockData _blockData = tallSupport.getBlockData(facingFinal, block);

		if (BlockUtils.tryPlaceEvent(player, _block, block, _blockMaterial, _blockData, false, _item)) {
			CustomBlockUtils.updateObservers(_block, player);
		}
	}

	private final List<BlockFace> directions = List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
	private void checkNeighbors(Player player, Block block, BlockData blockData) {
		ICustomBlock tripwireCross = CustomBlock.TRIPWIRE_CROSS.get();
		Material _blockMaterial = tripwireCross.getVanillaBlockMaterial();
		ItemStack _item = new ItemStack(tripwireCross.getVanillaItemMaterial());

		for (BlockFace direction : directions) {
			Block _block = block.getRelative(direction);

			if (!shouldConvert(_block, (org.bukkit.block.data.type.Tripwire) blockData, direction.getOppositeFace()))
				continue;

			Block _under = _block.getRelative(BlockFace.DOWN);
			BlockData _blockData = tripwireCross.getBlockData(BlockFace.UP, _under);

			if (BlockUtils.tryPlaceEvent(player, _block, _under, _blockMaterial, _blockData, false, _item)) {
				CustomBlockUtils.updateObservers(_block, player);
			}
		}

	}

	private boolean shouldConvert(Block neighbor) {
		return shouldConvert(neighbor, null, null);
	}

	private boolean shouldConvert(Block neighbor, org.bukkit.block.data.type.Tripwire originData, BlockFace origin) {
		boolean convertingNeighbors = originData != null && origin != null;
		for (BlockFace direction : directions) {
			Block _block = neighbor.getRelative(direction);
			BlockData _blockData = _block.getBlockData();

			if (_blockData instanceof TripwireHook hook) {
				if (hook.getFacing() != direction.getOppositeFace())
					return false;
				continue;
			}

			if (convertingNeighbors && origin == direction) {
				if (!originData.hasFace(direction))
					return false;
				continue;
			}

			if (!(_blockData instanceof org.bukkit.block.data.type.Tripwire tripwire))
				return false;

			CustomBlock _customBlock = CustomBlock.from(_block);
			if (_customBlock == null || !(_customBlock.get() instanceof IActualTripwire))
				return false;

			if (!tripwire.hasFace(direction))
				return false;
		}

		return true;
	}

	public void updateBlock(Player player, CustomBlock newCustomBlock, Block block) {
		if (newCustomBlock == null)
			return;

		Block underneath = block.getRelative(BlockFace.DOWN);
		BlockFace facing = CustomBlockUtils.getFacing(this, block.getBlockData(), underneath);
		BlockData newBlockData = newCustomBlock.customBlock.getBlockData(facing, underneath);

		block.setType(newCustomBlock.get().getVanillaBlockMaterial(), false);
		block.setBlockData(newBlockData, false);
		playSound(player, SoundAction.PLACE, block.getLocation());

		CustomBlockUtils.updateObservers(block, player);

		CustomBlockUtils.logPlacement(player, block, newCustomBlock);
	}

	public void breakBlock(@Nullable Player source, @Nullable ItemStack tool, Block block, boolean dropItem, int amount, boolean playSound, boolean spawnParticle, boolean applyPhysics) {
		boolean dropIngredients = false;
		CustomBlockUtils.debug(source, "&b- CustomBlock#breakBlock: " + this.get().getItemName());

		Location location = block.getLocation();
		if (source != null)
			CustomBlockUtils.logRemoval(source, location, block, this);

		if (Nullables.isNotNullOrAir(tool) && this != TALL_SUPPORT) {
			CustomBlockUtils.debug(source, "&e- tool != null/air");
			ICustomBlock iCustomBlock = get();

			if (iCustomBlock.requiresCorrectToolForDrops() && !iCustomBlock.canHarvestWith(tool, source)) {
				dropItem = false;
				CustomBlockUtils.debug(source, "&e- dropItem = " + dropItem);

				boolean requiresSilkTouch = iCustomBlock.requiresSilkTouchForDrops();
				CustomBlockUtils.debug(source, "&e- requiresSilkTouch = " + requiresSilkTouch);
				List<ItemStack> nonSilkTouchDrops = iCustomBlock.getNonSilkTouchDrops();
				if (requiresSilkTouch && !Nullables.isNullOrEmpty(nonSilkTouchDrops)) {
					dropIngredients = true;
					CustomBlockUtils.debug(source, "&e- dropIngredients = " + dropIngredients);
				} else {
					if (requiresSilkTouch)
						CustomBlockUtils.debug(source, "&e- nonSilkTouchDrops is null/empty");
				}
			}
		}

		if (source != null && source.getGameMode() == GameMode.CREATIVE) {
			dropItem = false;
			dropIngredients = false;
		}

		if (this == NOTE_BLOCK) {
			CustomBlockUtils.breakNoteBlockInDatabase(location);
		}

		block.setType(Material.AIR, applyPhysics);

		if (this == TALL_SUPPORT) {
			CustomBlock belowCustomBlock = CustomBlock.from(block.getRelative(BlockFace.DOWN));
			if (belowCustomBlock == null) return;

			CustomBlockUtils.debug(source, "&e- breaking tall support w/ particle: " + belowCustomBlock.get().getItemName());
			belowCustomBlock.breakBlock(source, tool, block, false, amount, playSound, spawnParticle, applyPhysics);
			return;
		}

		if (spawnParticle && this.get() instanceof ICustomTripwire) {
			CustomBlockUtils.debug(source, "&e- spawning particle");
			spawnParticle(source, location);
		}

		if (playSound) {
			playSound(source, SoundAction.BREAK, location);
		}

		if (dropItem) {
			CustomBlockUtils.debug(source, "&e- dropping item");
			dropItem(amount, location);
		}

		if (dropIngredients) {
			CustomBlockUtils.debug(source, "&e- dropping silk items");
			dropSilkItems(location, source);
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
		boolean silent = source != null && Vanish.isVanished(source);

		String sound = getSound(type);
		if (Nullables.isNullOrEmpty(sound))
			return;

		SoundBuilder soundBuilder = new SoundBuilder(sound)
				.location(location)
				.volume(type.getVolume())
				.pitch(type.getPitch());

		if (silent)
			soundBuilder.receiver(source);

		CustomBlockUtils.debug(source, "&a<- playing sound: " + sound);
		soundBuilder.category(SoundCategory.BLOCKS).play();
	}

	public void spawnParticle(Player source, Location loc) {
		boolean silent = source != null && Vanish.isVanished(source);

		World world = loc.getWorld();
		loc = loc.toCenterLocation();
		Particle particle = Particle.ITEM;
		ItemStack itemStack = get().getItemStack();

		if (silent)
			source.spawnParticle(particle, loc, 25, 0.25, 0.25, 0.25, 0.1, itemStack);
		else
			world.spawnParticle(particle, loc, 25, 0.25, 0.25, 0.25, 0.1, itemStack);
	}

	@Getter
	final static Map<Class<? extends ICraftable>, NexusRecipe> recipes = new HashMap<>();

	public void registerRecipes() {
		if (!(get() instanceof ICraftable craftable))
			return;

		// craft recipe
		@Nullable Pair<RecipeBuilder<?>, Integer> craftRecipePair = craftable.getCraftRecipe();
		if (craftRecipePair != null && craftRecipePair.getFirst() != null) {
			ItemStack toMakeItem = new ItemBuilder(craftable.getItemStack()).amount(craftRecipePair.getSecond()).build();
			NexusRecipe recipe = craftRecipePair.getFirst().toMake(toMakeItem).build().type(RecipeType.CUSTOM_BLOCKS);
			recipe.register();

			recipes.put(craftable.getClass(), recipe);
		}

		// uncraft recipe
		if (craftable.getUncraftRecipe() != null) {
			NexusRecipe recipe = craftable.getUncraftRecipe().build().type(RecipeType.CUSTOM_BLOCKS);
			recipe.register();

			recipes.put(craftable.getClass(), recipe);
		}

		// other recipes
		if (!craftable.getOtherRecipes().isEmpty()) {
			for (NexusRecipe recipe : craftable.getOtherRecipes()) {
				recipe.register();

				recipes.put(craftable.getClass(), recipe);
			}
		}

		// re-dye recipes
		if (craftable instanceof IDyeable dyeable) {
			CustomBlockTag tag = dyeable.getRedyeTag();

			final ColorType color = ColorType.of(this);
			if (color != null) {
				final Material dye = color.switchColor(Material.WHITE_DYE);
				final CustomBlockTag tagExcludingSelf = new CustomBlockTag(tag).exclude(this).key(tag);
				final NexusRecipe recipe = RecipeBuilder.surround(dye).with(tagExcludingSelf).toMake(color.switchColor(tag), 8).build();
				recipe.type(RecipeType.DYES).register();
				recipes.put(craftable.getClass(), recipe);
			}
		}
	}

	public void dropItem(int amount, Location location) {
		ItemStack item = this.get().getItemStack();
		item.setAmount(amount);
		dropItem(item, location);
	}

	private void dropSilkItems(Location location, Player debugger) {
		CustomBlockUtils.debug(debugger, "dropping ingredients");
		for (ItemStack item : this.get().getNonSilkTouchDrops()) {
			dropItem(item, location);
		}
	}

	private void dropItem(ItemStack item, Location location) {
		location.getWorld().dropItemNaturally(location.toCenterLocation(), item);
	}


	@Getter
	public enum CustomBlockType {
		UNKNOWN(null, null),
		NOTE_BLOCK(Material.NOTE_BLOCK, Material.NOTE_BLOCK),
		TRIPWIRE(Material.TRIPWIRE, Material.STRING),
		;

		private final Material blockMaterial;
		private final Material itemMaterial;

		CustomBlockType(Material blockMaterial, Material itemMaterial) {
			this.blockMaterial = blockMaterial;
			this.itemMaterial = itemMaterial;
		}

		public static void init() {
		}

		public static Set<Material> getItemMaterials() {
			return new HashSet<>() {{
				for (CustomBlockType type : EnumUtils.valuesExcept(CustomBlockType.class, CustomBlockType.UNKNOWN))
					add(type.getItemMaterial());
			}};
		}

		public static Set<Material> getBlockMaterials() {
			return new HashSet<>() {{
				for (CustomBlockType type : EnumUtils.valuesExcept(CustomBlockType.class, CustomBlockType.UNKNOWN))
					add(type.getBlockMaterial());
			}};
		}
	}


}
