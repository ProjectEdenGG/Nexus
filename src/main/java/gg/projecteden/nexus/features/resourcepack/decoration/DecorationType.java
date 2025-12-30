package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Tab;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxUnique;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxWall;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable.ColorableType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.IBedAddition;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.ICouch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Bunting;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet.CabinetMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet.CabinetType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.ClownInTheBox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.ClownInTheBox.ClownInTheBoxType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.HandleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.DyeableFireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.types.DyeableFurniture;
import gg.projecteden.nexus.features.resourcepack.decoration.types.DyeableOrnament;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag.PrideFlagType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flora;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Furniture;
import gg.projecteden.nexus.features.resourcepack.decoration.types.HangingBanner;
import gg.projecteden.nexus.features.resourcepack.decoration.types.LetterBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.LetterBlock.LetterBlockType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Ornament;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Ornament.OrnamentType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Present;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Present.PresentType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.StandingBanner;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table.TableShape;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table.TableTheme;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.BirdHouse;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime.WindChimeType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.SnowGlobe;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.SnowGlobe.SnowGlobeType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.DyeableInstrument;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.DyeableBench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.DyeableChair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.DyeableCouch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.LongChair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Stump;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.BedAddition;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.DyeableBedAddition;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Edible;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Edible.EdibleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Mailbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerHeadBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.TrashCan;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Waystone;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Well;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.WorkBench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.BlockThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.CeilingThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableCeilingThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.Curtain;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.Curtain.CurtainType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.DyeableGiantCandle;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.DyeableGiantCandle.DyeableCandleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.GiantCandle;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.GiantCandle.CandleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.RecordPlayer;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.RecordPlayer.RecordPlayerType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.TV;
import gg.projecteden.nexus.features.resourcepack.decoration.types.cycle.TV.ChannelType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.models.trophy.TrophyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	Known Bugs:
		- (Won't Fix) Decor doesn't check for required support on placement
		- (Won't Fix) Decor will float after removing it's support
		- Decorations can be placed at world height, where their hitboxes will not able able to fully be placed

	TODO:
		- Split Furniture & Food + Kitchenware into their own catalogs
		-
		- Cleanup DecorationInteractionData & Decoration duplicate checks (such as "canEdit")
		- Bed Additions (Canopy)
		- Rework shelves to being light-hitbox-based, barrier-hitboxes don't work properly, and I can't figure them out
		- Add:
			- Conch Shell functional decoration that plays beach sounds
			- Remaining decorations:
					- toAdd
					- Dog House
			- Add some Tickable Decorations
			- Hot Swap Kitchen Handles -> Sell handles at general store/carpenter?
			- Allow player to create their own presets in DyeStationMenu
			- Better support for:
				- Multi-Surface models -> birdhouses, banners
				- Multi-Block ceiling things
			- Inventory support (cabinets = chests, ovens = furnaces, etc)
			- Mob plushies
			- Shop manager https://github.com/ProjectEdenGG/Suggestions/issues/266
		- Ideas:
			- Redstone activate instrument?
			- Mailbox change model if have mail or not
 */

// @formatter:off
@Getter
@AllArgsConstructor
public enum DecorationType {
// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Holiday
// 	------------------------------------------------------------------------------------------------------
	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_DARK_XMAS(new Fireplace(true, "Dark Christmas Fireplace", ItemModelType.FIREPLACE_DARK_XMAS)),

	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_BROWN_XMAS(new Fireplace(true, "Brown Christmas Fireplace", ItemModelType.FIREPLACE_BROWN_XMAS)),

	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_WOODEN_XMAS(new DyeableFireplace(true, "Wooden Christmas Fireplace", ItemModelType.FIREPLACE_WOODEN_XMAS)),

	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_LIGHT_XMAS(new Fireplace(true, "Light Christmas Fireplace", ItemModelType.FIREPLACE_LIGHT_XMAS)),

	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_DARK_XMAS_SOUL(new Fireplace(true, "Dark Christmas Soul Fireplace", ItemModelType.FIREPLACE_DARK_XMAS_SOUL)),

	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_BROWN_XMAS_SOUL(new Fireplace(true, "Brown Christmas Fireplace", ItemModelType.FIREPLACE_BROWN_XMAS_SOUL)),

	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_WOODEN_XMAS_SOUL(new DyeableFireplace(true, "Wooden Christmas Soul Fireplace", ItemModelType.FIREPLACE_WOODEN_XMAS_SOUL)),

	@TypeConfig(money = 550, tokens = 55, theme = Theme.HOLIDAY, tabs = Tab.FIREPLACES)
	FIREPLACE_LIGHT_XMAS_SOUL(new Fireplace(true, "Light Christmas Soul Fireplace", ItemModelType.FIREPLACE_LIGHT_XMAS_SOUL)),

	@TypeConfig(money = 150, tokens = 55, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_COLOR(new FloorThing(false, "Colorful Christmas Tree", ItemModelType.CHRISTMAS_TREE_COLORED, HitboxFloor._1x2V)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_WHITE(new FloorThing(false, "White Christmas Tree", ItemModelType.CHRISTMAS_TREE_WHITE, HitboxFloor._1x2V)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_COLOR_BIG(new FloorThing(false, "Big Colorful Christmas Tree", ItemModelType.CHRISTMAS_TREE_COLORED_BIG, HitboxFloor._1x4V)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_WHITE_BIG(new FloorThing(false, "Big White Christmas Tree", ItemModelType.CHRISTMAS_TREE_WHITE_BIG, HitboxFloor._1x4V)),

	@TypeConfig(money = 45, tokens = 5, theme = Theme.HOLIDAY)
	MISTLETOE(new CeilingThing(false, "Mistletoe", ItemModelType.MISTLETOE)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.HOLIDAY)
	WREATH(new WallThing(false, "Wreath", ItemModelType.WREATH)),

	@TypeConfig(money = 30, tokens = 3, theme = Theme.HOLIDAY)
	STOCKINGS_SINGLE(new WallThing(false, "Single Stocking", ItemModelType.STOCKINGS_SINGLE)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.HOLIDAY)
	STOCKINGS_DOUBLE(new WallThing(false, "Double Stocking", ItemModelType.STOCKINGS_DOUBLE)),

	@TypeConfig(money = 105, tokens = 10, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_HAPPY_HOLIDAYS(new Bunting(true, "Happy Holidays Bunting", ItemModelType.BUNTING_PHRASE_HAPPY_HOLIDAYS, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(money = 105, tokens = 10, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_HAPPY_NEW_YEAR(new Bunting(true, "Happy New Year Bunting", ItemModelType.BUNTING_PHRASE_HAPPY_NEW_YEAR, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(money = 105, tokens = 10, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_MERRY_CHRISTMAS(new Bunting(true, "Merry Christmas Bunting", ItemModelType.BUNTING_PHRASE_MERRY_CHRISTMAS, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.HOLIDAY)
	SNOWMAN_PLAIN(new FloorThing(false, "Plain Snowman", ItemModelType.SNOWMAN_PLAIN, HitboxFloor._1x2V)),

	@TypeConfig(money = 375, tokens = 40, theme = Theme.HOLIDAY)
	SNOWMAN_FANCY(new FloorThing(false, "Fancy Snowman", ItemModelType.SNOWMAN_FANCY, HitboxFloor._1x2V)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.HOLIDAY)
	SNOWBALLS_SMALL(new FloorThing(false, "Small Pile of Snowballs", ItemModelType.SNOWBALLS_SMALL)),

	@TypeConfig(money = 105, tokens = 10, theme = Theme.HOLIDAY)
	SNOWBALLS_BIG(new FloorThing(false, "Big Pile of Snowballs", ItemModelType.SNOWBALLS_BIG)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_CENTER(new WallThing(false, "Icicle Lights - Center", ItemModelType.ICICLE_LIGHT_CENTER)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_LEFT(new WallThing(false, "Icicle Lights - Left", ItemModelType.ICICLE_LIGHT_LEFT)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_RIGHT(new WallThing(false, "Icicle Lights - Right", ItemModelType.ICICLE_LIGHT_RIGHT)),

	@TypeConfig(money = 90, tokens = 9, theme = Theme.HOLIDAY)
	ICICLE_SMALL(new CeilingThing(false, "Small Icicle", ItemModelType.ICICLE_SMALL)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.HOLIDAY)
	ICICLE_LARGE(new CeilingThing(false, "Large Icicle", ItemModelType.ICICLE_LARGE, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 185, tokens = 18, theme = Theme.HOLIDAY)
	ICICLE_MULTI(new CeilingThing(false, "Pair of Icicles", ItemModelType.ICICLE_MULTI, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.HOLIDAY)
	GIANT_CANDY_CANE(new DyeableFloorThing(false, "Giant Candy Cane", ItemModelType.GIANT_CANDY_CANE, ColorableType.DYE, HitboxUnique.GIANT_CANDY_CANE)),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	BAUBLE_ORNAMENT(new DyeableOrnament(OrnamentType.BAUBLE, false)),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	BAUBLE_ACCENT_ORNAMENT(new DyeableOrnament(OrnamentType.BAUBLE_ACCENT, false)),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	CANDY_CANE_ORNAMENT(new DyeableOrnament(OrnamentType.CANDY_CANE, false)),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	CONE_ORNAMENT(new DyeableOrnament(OrnamentType.CONE, false, "9C5900")),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	GINGERBREAD_ORNAMENT(new Ornament(OrnamentType.GINGERBREAD, false)),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	SNOWFLAKE_ORNAMENT(new DyeableOrnament(OrnamentType.SNOWFLAKE, false, "FFFFFF")),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	SNOWMAN_ORNAMENT(new DyeableOrnament(OrnamentType.SNOWMAN, false)),

	@TypeConfig(money = 70, tokens = 7, theme = Theme.HOLIDAY, tabs = Tab.ORNAMENTS)
	STAR_ORNAMENT(new DyeableOrnament(OrnamentType.STAR, false, "FFD83E")),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	BAUBLE_GIANT_ORNAMENT(new DyeableOrnament(OrnamentType.BAUBLE, true)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	BAUBLE_ACCENT_GIANT_ORNAMENT(new DyeableOrnament(OrnamentType.BAUBLE_ACCENT, true)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	CANDY_CANE_GIANT_ORNAMENT(new DyeableOrnament(OrnamentType.CANDY_CANE, true)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	CONE_GIANT_ORNAMENT(new DyeableOrnament(OrnamentType.CONE, true, "9C5900")),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	GINGERBREAD_GIANT_ORNAMENT(new Ornament(OrnamentType.GINGERBREAD, true)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	SNOWFLAKE_GIANT_ORNAMENT(new DyeableOrnament(OrnamentType.SNOWFLAKE, true, "FFFFFF")),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	SNOWMAN_GIANT_ORNAMENT(new DyeableOrnament(OrnamentType.SNOWMAN, true)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.HOLIDAY, tabs = Tab.GIANT_ORNAMENTS)
	STAR_GIANT_ORNAMENT(new DyeableOrnament(OrnamentType.STAR, true, "FFD83E")),

	@TypeConfig(money = 80, tokens = 8, theme = Theme.HOLIDAY)
	NUTCRACKER_SHORT(new DyeableFloorThing(false, "Short Nutcracker", ItemModelType.NUTCRACKER_SHORT, ColorableType.DYE)),

	@TypeConfig(money = 160, tokens = 16, theme = Theme.HOLIDAY)
	NUTCRACKER(new DyeableFloorThing(false, "Nutcracker", ItemModelType.NUTCRACKER, ColorableType.DYE)),

	@TypeConfig(money = 240, tokens = 24, theme = Theme.HOLIDAY)
	NUTCRACKER_TALL(new DyeableFloorThing(false, "Tall Nutcracker", ItemModelType.NUTCRACKER_TALL, ColorableType.DYE, HitboxFloor._1x3V)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	PLUSHIE_SNOWMAN(new DyeableFloorThing(false, "Snowman Plushie", ItemModelType.PLUSHIE_SNOWMAN, ColorableType.DYE, "2E6E00")),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	PLUSHIE_SANTA(new FloorThing(false, "Santa Plushie", ItemModelType.PLUSHIE_SANTA)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	PLUSHIE_ELF(new FloorThing(false, "Elf Plushie", ItemModelType.PLUSHIE_ELF)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	RIBBON_BAND(new Dyeable(false, "Band Ribbon", ItemModelType.RIBBON_BAND, ColorableType.DYE, "FF0000")),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	RIBBON_CROSS(new Dyeable(false, "Cross Ribbon", ItemModelType.RIBBON_CROSS, ColorableType.DYE, "FF0000")),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	RIBBON_BOW_FLOOR(new Dyeable(false, "Floor Ribbon", ItemModelType.RIBBON_FLOOR, ColorableType.DYE, "FF0000")),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	RIBBON_BOW_WALL(new Dyeable(false, "Wall Ribbon", ItemModelType.RIBBON_WALL, ColorableType.DYE, "FF0000")),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	SNOWGLOBE(new SnowGlobe(SnowGlobeType.PRESENT)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY, tabs = Tab.PRESENTS)
	PRESENT_SHORT_DOUBLE_RED(new Present(PresentType.SHORT_DOUBLE_RED)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY, tabs = Tab.PRESENTS)
	PRESENT_MINI_RED(new Present(PresentType.MINI_RED)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY, tabs = Tab.PRESENTS)
	PRESENT_BLOCK_RED(new Present(PresentType.BLOCK_RED)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY, tabs = Tab.PRESENTS)
	PRESENT_TALL_RED(new Present(PresentType.TALL_RED)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	SANTAS_CHAIR(new DyeableChair(false, true, "Santa's Chair", ItemModelType.SANTAS_CHAIR, ColorableType.DYE, HitboxSingle._1x1_BARRIER, 1.2)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	SANTAS_TABLE(new FloorThing(true, "Santa's Table", ItemModelType.SANTAS_TABLE, HitboxUnique.SANTAS_TABLE)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	STRING_LIGHTS(new WallThing(true, "String Lights", ItemModelType.STRING_LIGHTS, HitboxUnique.STRING_LIGHTS)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	STRING_LIGHTS_FLIPPED(new WallThing(true, "String Lights (Flipped)", ItemModelType.STRING_LIGHTS_FLIPPED, HitboxUnique.STRING_LIGHTS_FLIPPED)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	HOLLY_CANDLE_BIG(new FloorThing(false, "Big Holly Candle", ItemModelType.HOLLY_CANDLE_BIG, HitboxUnique.HOLLY_CANDLE_BIG)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	HOLLY_CANDLES(new FloorThing(false, "Holly Candles", ItemModelType.HOLLY_CANDLES, HitboxUnique.HOLLY_CANDLES)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	LETTER_BLOCK_A(new LetterBlock(LetterBlockType.A)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	LETTER_BLOCK_B(new LetterBlock(LetterBlockType.B)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	LETTER_BLOCK_C(new LetterBlock(LetterBlockType.C)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	GINGERBREAD_HOUSE(new FloorThing(false, "Gingerbread House on a Plate", ItemModelType.GINGERBREAD_HOUSE_PLATE, HitboxSingle._1x1_LIGHT(8))),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	GINGERBREAD_MAN(new FloorThing(false, "Gingerbread Man on a Plate", ItemModelType.GINGERBREAD_MAN_PLATE)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	CLOWN_IN_THE_BOX(new ClownInTheBox(ClownInTheBoxType.CLOSED_OFF)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	GEARS_CHRISTMAS(new WallThing(false, "Christmas Gears", ItemModelType.GEARS_CHRISTMAS)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Spooky
// 	------------------------------------------------------------------------------------------------------
	@TypeConfig(money = 75, tokens = 7, theme = Theme.SPOOKY)
	GRAVESTONE_SMALL(new FloorThing(false, "Small Gravestone", ItemModelType.GRAVESTONE_SMALL)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY)
	GRAVESTONE_CROSS(new FloorThing(false, "Gravestone Cross", ItemModelType.GRAVESTONE_CROSS, HitboxSingle._1x1_CHAIN)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.SPOOKY)
	GRAVESTONE_PLAQUE(new FloorThing(false, "Gravestone Plaque", ItemModelType.GRAVESTONE_PLAQUE)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY)
	GRAVESTONE_STACK(new FloorThing(false, "Rock Stack Gravestone", ItemModelType.GRAVESTONE_STACK)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.SPOOKY)
	GRAVESTONE_FLOWERBED(new FloorThing(false, "Flowerbed Gravestone", ItemModelType.GRAVESTONE_FLOWERBED)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.SPOOKY)
	GRAVESTONE_TALL(new FloorThing(false, "Tall Gravestone", ItemModelType.GRAVESTONE_TALL, HitboxUnique.GRAVESTONE_TALL)),

	@TypeConfig(money = 215, tokens = 21, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_BEDS})
	SPOOKY_BED_GENERIC_1_DOUBLE(new BedAddition("Spooky Generic Frame A Double", ItemModelType.BED_GENERIC_1_DOUBLE_SPOOKY, IBedAddition.AdditionType.FRAME, true)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_BENCH_WOODEN(new Bench(true, false, "Spooky Wooden Bench", ItemModelType.BENCH_SPOOKY, HitboxFloor._1x2H)),

	@TypeConfig(money = 120, tokens = 12, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_CHAIR_WOODEN_BASIC(new DyeableChair(false, false, "Spooky Wooden Chair", ItemModelType.CHAIR_WOODEN_BASIC_SPOOKY, ColorableType.DYE)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_CHAIR_WOODEN_CUSHIONED(new DyeableChair(false, false, "Spooky Cushioned Wooden Chair", ItemModelType.CHAIR_WOODEN_CUSHIONED_SPOOKY, ColorableType.DYE)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_COUCH_WOODEN_CUSHIONED_END_LEFT(new DyeableCouch(false, "Spooky Cushioned Wooden Couch Left End", ItemModelType.COUCH_WOODEN_CUSHIONED_END_LEFT_SPOOKY, ColorableType.DYE, ICouch.CouchPart.END)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_COUCH_WOODEN_CUSHIONED_END_RIGHT(new DyeableCouch(false, "Spooky Cushioned Wooden Couch Right End", ItemModelType.COUCH_WOODEN_CUSHIONED_END_RIGHT_SPOOKY, ColorableType.DYE, ICouch.CouchPart.END)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_COUCH_WOODEN_CUSHIONED_MIDDLE(new DyeableCouch(false, "Spooky Cushioned Wooden Couch Middle", ItemModelType.COUCH_WOODEN_CUSHIONED_MIDDLE_SPOOKY, ColorableType.DYE, ICouch.CouchPart.STRAIGHT)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_COUCH_WOODEN_CUSHIONED_CORNER(new DyeableCouch(false, "Spooky Cushioned Wooden Couch Corner", ItemModelType.COUCH_WOODEN_CUSHIONED_CORNER_SPOOKY, ColorableType.DYE, ICouch.CouchPart.CORNER)),

	@TypeConfig(money = 120, tokens = 12, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_CHAIRS})
	SPOOKY_COUCH_WOODEN_CUSHIONED_OTTOMAN(new DyeableCouch(false, "Spooky Cushioned Wooden Couch Ottoman", ItemModelType.COUCH_WOODEN_CUSHIONED_OTTOMAN_SPOOKY, ColorableType.DYE, ICouch.CouchPart.STRAIGHT)),

	@TypeConfig(money = 80, tokens = 8, theme = Theme.SPOOKY)
	GIANT_CANDLE_ONE_UNLIT(new GiantCandle("Giant Candle", CandleType.ONE, false)),

	@TypeConfig(money = 80, tokens = 8, theme = Theme.SPOOKY)
	GIANT_CANDLE_TWO_UNLIT(new GiantCandle("Giant Candles", CandleType.TWO, false)),

	@TypeConfig(money = 80, tokens = 8, theme = Theme.SPOOKY)
	GIANT_CANDLE_THREE_UNLIT(new GiantCandle("Giant Candles", CandleType.THREE, false)),

	@TypeConfig(money = 80, tokens = 8, theme = Theme.SPOOKY)
	GIANT_CANDLE_ONE_UNLIT_DYEABLE(new DyeableGiantCandle("Giant Candle", DyeableCandleType.ONE, false)),

	@TypeConfig(money = 80, tokens = 8, theme = Theme.SPOOKY)
	GIANT_CANDLE_TWO_UNLIT_DYEABLE(new DyeableGiantCandle("Giant Candles", DyeableCandleType.TWO, false)),

	@TypeConfig(money = 80, tokens = 8, theme = Theme.SPOOKY)
	GIANT_CANDLE_THREE_UNLIT_DYEABLE(new DyeableGiantCandle("Giant Candles", DyeableCandleType.THREE, false)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_1x1(new Table(TableTheme.SPOOKY_WOODEN, TableShape._1X1, ItemModelType.TABLE_WOODEN_1X1_SPOOKY)),

	@TypeConfig(money = 105, tokens = 10, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_1x2(new Table(TableTheme.SPOOKY_WOODEN, TableShape._1X2, ItemModelType.TABLE_WOODEN_1X2_SPOOKY)),

	@TypeConfig(money = 135, tokens = 13, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_1x3(new Table(TableTheme.SPOOKY_WOODEN, TableShape._1X3, ItemModelType.TABLE_WOODEN_1X3_SPOOKY)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_2x2(new Table(TableTheme.SPOOKY_WOODEN, TableShape._2X2, ItemModelType.TABLE_WOODEN_2X2_SPOOKY)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_2x2_CORNER(new Table(TableTheme.SPOOKY_WOODEN, TableShape._2X2_CORNER, ItemModelType.TABLE_WOODEN_2X2_CORNER_SPOOKY)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_2x3(new Table(TableTheme.SPOOKY_WOODEN, TableShape._2X3, ItemModelType.TABLE_WOODEN_2X3_SPOOKY)),

	@TypeConfig(money = 185, tokens = 18, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_2x3_CORNER(new Table(TableTheme.SPOOKY_WOODEN, TableShape._2X3_CORNER, ItemModelType.TABLE_WOODEN_2X3_CORNER_SPOOKY)),

	@TypeConfig(money = 185, tokens = 18, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_2x3_CORNER_FLIPPED(new Table(TableTheme.SPOOKY_WOODEN, TableShape._2X3_CORNER_FLIPPED, ItemModelType.TABLE_WOODEN_2X3_CORNER_FLIPPED_SPOOKY)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_3x3(new Table(TableTheme.SPOOKY_WOODEN, TableShape._3X3, ItemModelType.TABLE_WOODEN_3X3_SPOOKY)),

	@TypeConfig(money = 275, tokens = 27, theme = Theme.SPOOKY, tabs = {Tab.SPOOKY_FURNITURE, Tab.SPOOKY_TABLES})
	SPOOKY_TABLE_WOODEN_3x3_CORNER(new Table(TableTheme.SPOOKY_WOODEN, TableShape._3X3_CORNER, ItemModelType.TABLE_WOODEN_3X3_CORNER_SPOOKY)),

	@TypeConfig(money = 250, tokens = 25, theme = Theme.SPOOKY, tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_CUPBOARD_TALL_DOUBLE_EMPTY(new Furniture(true, "Spooky Tall Empty Cupboard Double", ItemModelType.CUPBOARD_TALL_DOUBLE_EMPTY_SPOOKY, PlacementType.FLOOR, HitboxFloor._2x2V)),

	@TypeConfig(money = 250, tokens = 25, theme = Theme.SPOOKY, tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_CUPBOARD_TALL_DOUBLE_FILLED(new Furniture(true, "Spooky Tall Cupboard Double", ItemModelType.CUPBOARD_TALL_DOUBLE_FILLED_SPOOKY, PlacementType.FLOOR, HitboxFloor._2x2V)),

	@TypeConfig(money = 125, tokens = 12, theme = Theme.SPOOKY, tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_DESK_1X2(new Furniture(true, "Spooky Desk 1x2", ItemModelType.DESK_1X2_SPOOKY, PlacementType.FLOOR, HitboxFloor._1x2H)),

	@TypeConfig(money = 125, tokens = 12, theme = Theme.SPOOKY, tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_DRESSER_1X2(new Furniture(true, "Spooky Dresser 1x2", ItemModelType.DRESSER_1X2_SPOOKY, PlacementType.FLOOR, HitboxFloor._1x2H)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.SPOOKY, tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_WARDROBE(new Furniture(true, "Spooky Wardrobe", ItemModelType.WARDROBE_SPOOKY, PlacementType.FLOOR, HitboxFloor._2x3V)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.SPOOKY, tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_WARDROBE_OPENED(new Furniture(true, "Spooky Opened Wardrobe", ItemModelType.WARDROBE_SPOOKY_OPENED, PlacementType.FLOOR, HitboxFloor._2x3V)),

	@TypeConfig(money = 900, tokens = 90, theme = Theme.SPOOKY, tabs = Tab.ART)
	ART_PAINTING_CUSTOM_SAINT_JEROME(new Art("Saint Jerome", ItemModelType.ART_PAINTING_CUSTOM_SAINT_JEROME, HitboxWall._2x3V_LIGHT)),

	@TypeConfig(money = 900, tokens = 90, theme = Theme.SPOOKY, tabs = Tab.ART)
	ART_PAINTING_CUSTOM_LUTE_PLAYER(new Art("Lute Player", ItemModelType.ART_PAINTING_CUSTOM_LUTE_PLAYER, HitboxWall._2x3H_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.SPOOKY, tabs = Tab.ART)
	ART_PAINTING_CUSTOM_FOREST_KNIGHT(new Art("Dark Knight in Forest", ItemModelType.ART_PAINTING_CUSTOM_FOREST_KNIGHT, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.SPOOKY, tabs = Tab.ART)
	ART_PAINTING_CUSTOM_RUFF_MAN(new Art("Man in a Ruff", ItemModelType.ART_PAINTING_CUSTOM_RUFF_MAN, HitboxWall._2x2_LIGHT)),



// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Music
// 	------------------------------------------------------------------------------------------------------
	// - Noisemakers
	@TypeConfig(money = 1500, tokens = 150, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	DRUM_KIT(new DyeableInstrument(true, "Drum Kit", ItemModelType.DRUM_KIT, InstrumentSound.DRUM_KIT, ColorableType.DYE, HitboxUnique.DRUM_KIT, PlacementType.FLOOR)),

	@TypeConfig(money = 2250, tokens = 225, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_GRAND(new DyeableInstrument(true, "Grand Piano", ItemModelType.PIANO_GRAND, InstrumentSound.GRAND_PIANO, ColorableType.STAIN, HitboxUnique.PIANO_GRAND, PlacementType.FLOOR)),

	@TypeConfig(money = 750, tokens = 75, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_KEYBOARD(new DyeableInstrument(true, "Keyboard", ItemModelType.PIANO_KEYBOARD, InstrumentSound.PIANO, ColorableType.DYE, HitboxFloor._1x2H_LIGHT, PlacementType.FLOOR)),

	@TypeConfig(money = 900, tokens = 90, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_KEYBOARD_ON_STAND(new DyeableInstrument(true, "Keyboard On Stand", ItemModelType.PIANO_KEYBOARD_ON_STAND, InstrumentSound.PIANO, ColorableType.DYE, HitboxFloor._1x2H, PlacementType.FLOOR)),

	@TypeConfig(money = 1050, tokens = 105, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	HARP(new Instrument(false, "Harp", ItemModelType.HARP, InstrumentSound.HARP, HitboxFloor._1x2V, PlacementType.FLOOR)),

	@TypeConfig(money = 900, tokens = 90, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	BONGOS(new DyeableInstrument(true, "Bongos", ItemModelType.BONGOS, InstrumentSound.BONGOS, ColorableType.DYE, HitboxFloor._1x2H, PlacementType.FLOOR)),

	@TypeConfig(money = 675, tokens = 67, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC(new DyeableFloorThing(false, "Acoustic Guitar Display", ItemModelType.GUITAR_ACOUSTIC, ColorableType.STAIN)),

	@TypeConfig(money = 675, tokens = 67, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_WALL(new DyeableWallThing(false, "Wall Mounted Acoustic Guitar Display", ItemModelType.GUITAR_ACOUSTIC_WALL, ColorableType.STAIN, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(money = 750, tokens = 75, theme = Theme.MUSIC)
	GUITAR_ELECTRIC(new DyeableFloorThing(false, "Electric Guitar Display", ItemModelType.GUITAR_ELECTRIC, ColorableType.DYE)),

	@TypeConfig(money = 750, tokens = 75, theme = Theme.MUSIC)
	GUITAR_ELECTRIC_WALL(new DyeableWallThing(false, "Wall Mounted Electric Guitar Display", ItemModelType.GUITAR_ELECTRIC_WALL, ColorableType.DYE, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC(new FloorThing(false, "Acoustic Classic Guitar Display", ItemModelType.GUITAR_ACOUSTIC_CLASSIC)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC_WALL(new WallThing(false, "Wall Mounted Acoustic Classic Guitar Display", ItemModelType.GUITAR_ACOUSTIC_CLASSIC_WALL, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.MUSIC)
	TRUMPET(new FloorThing(false, "Trumpet Display", ItemModelType.TRUMPET)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.MUSIC)
	SAXOPHONE(new FloorThing(false, "Saxophone Display", ItemModelType.SAXOPHONE)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.MUSIC)
	VIOLIN(new FloorThing(false, "Violin Display", ItemModelType.VIOLIN)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.MUSIC)
	VIOLIN_WALL(new WallThing(false, "Wall Mounted Violin Display", ItemModelType.VIOLIN_WALL, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(money = 750, tokens = 75, theme = Theme.MUSIC)
	CELLO(new FloorThing(false, "Cello Display", ItemModelType.CELLO)),

	@TypeConfig(money = 165, tokens = 16, theme = Theme.MUSIC)
	DRUM_THRONE(new DyeableChair(false, true, "Drum Throne", ItemModelType.DRUM_THRONE, ColorableType.DYE, 1.35)),

	@TypeConfig(money = 180, tokens = 18, theme = Theme.MUSIC)
	PIANO_BENCH(new DyeableBench(true, true, "Piano Bench", ItemModelType.PIANO_BENCH, ColorableType.STAIN, 1.15, HitboxFloor._1x2H)),

	@TypeConfig(money = 210, tokens = 21, theme = Theme.MUSIC)
	PIANO_BENCH_GRAND(new DyeableBench(true, true, "Grand Piano Bench", ItemModelType.PIANO_BENCH_GRAND, ColorableType.STAIN, 1.15, HitboxFloor._1x3H)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.MUSIC)
	AMPLIFIER(new FloorThing(false, "Amplifier", ItemModelType.AMPLIFIER, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 105, tokens = 10, theme = Theme.MUSIC)
	GOLDEN_RECORD(new WallThing(false, "Golden Record", ItemModelType.GOLDEN_RECORD)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.MUSIC)
	SPEAKER_LARGE(new FloorThing(false, "Large Speaker", ItemModelType.SPEAKER_LARGE, HitboxFloor._1x2V)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.MUSIC)
	SPEAKER_SMALL(new FloorThing(false, "Small Speaker", ItemModelType.SPEAKER_SMALL, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 135, tokens = 13, theme = Theme.MUSIC)
	LAUNCHPAD(new FloorThing(false, "Launchpad", ItemModelType.LAUNCHPAD)),

	@TypeConfig(money = 150, tokens = 150, theme = Theme.MUSIC)
	MICROPHONE(new FloorThing(false, "Microphone", ItemModelType.MICROPHONE)),

	@TypeConfig(money = 195, tokens = 19, theme = Theme.MUSIC)
	MICROPHONE_WITH_BOOM_STAND(new FloorThing(false, "Microphone With Boom Stand", ItemModelType.MICROPHONE_WITH_BOOM_STAND)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.MUSIC)
	MIXING_CONSOLE(new FloorThing(true, "Mixing Console", ItemModelType.MIXING_CONSOLE, HitboxFloor._1x2H_LIGHT)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.MUSIC)
	LIGHT_BOARD(new FloorThing(true, "Light Board", ItemModelType.LIGHT_BOARD, HitboxFloor._1x2H_LIGHT)),

	@TypeConfig(money = 375, tokens = 37, theme = Theme.MUSIC)
	SPEAKER_WOODEN_LARGE(new DyeableFloorThing(false, "Large Wooden Speaker", ItemModelType.SPEAKER_WOODEN_LARGE, ColorableType.STAIN, HitboxFloor._1x2V)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.MUSIC)
	SPEAKER_WOODEN_SMALL(new DyeableFloorThing(false, "Small Wooden Speaker", ItemModelType.SPEAKER_WOODEN_SMALL, ColorableType.STAIN, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 285, tokens = 28, theme = Theme.MUSIC)
	TAPE_MACHINE(new DyeableFloorThing(false, "Tape Machine", ItemModelType.TAPE_MACHINE, ColorableType.STAIN, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 525, tokens = 52, theme = Theme.MUSIC)
	DJ_TURNTABLE(new DyeableFloorThing(true, "DJ Turntable", ItemModelType.DJ_TURNTABLE, ColorableType.DYE, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(money = 155, tokens = 15, theme = Theme.MUSIC)
	RECORD_PLAYER_MODERN_OFF(new RecordPlayer("Modern Record Player", RecordPlayerType.OFF)),

	@TypeConfig(money = 155, tokens = 15, theme = Theme.MUSIC)
	RECORD_PLAYER_MODERN_ON(new RecordPlayer("Modern Record Player", RecordPlayerType.ON)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.MUSIC)
	STUDIO_LIGHT_HANGING(new CeilingThing(false, "Hanging Studio Lights", ItemModelType.STUDIO_LIGHTS_HANGING)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.MUSIC)
	STUDIO_LIGHT_STANDING(new FloorThing(false, "Standing Studio Light", ItemModelType.STUDIO_LIGHTS_STANDING, HitboxFloor._1x2V)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Pride
// 	------------------------------------------------------------------------------------------------------
	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_ACE(new Flag("Asexual Pride Flag", PrideFlagType.ACE)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_AGENDER(new Flag("Agender Pride Flag", PrideFlagType.AGENDER)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_ARO(new Flag("Aromatic Pride Flag", PrideFlagType.ARO)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_BI(new Flag("Bisexual Pride Flag", PrideFlagType.BI)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMI(new Flag("Demi Pride Flag", PrideFlagType.DEMI)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIBOY(new Flag("Demi Boy Pride Flag", PrideFlagType.DEMIBOY)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIGIRL(new Flag("Demi Girl Pride Flag", PrideFlagType.DEMIGIRL)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIROMANTIC(new Flag("Demiromantic Pride Flag", PrideFlagType.DEMIROMANTIC)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GAY(new Flag("Gay Pride Flag", PrideFlagType.GAY)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENDERFLUID(new Flag("Genderfluid Pride Flag", PrideFlagType.GENDERFLUID)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENDERFLUX(new Flag("Genderflux Pride Flag", PrideFlagType.GENDERFLUX)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENQUEER(new Flag("Genderqueer Pride Flag", PrideFlagType.GENDERQUEER)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GRAYACE(new Flag("Gray-Asexual Pride Flag", PrideFlagType.GRAY_ACE)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GRAYARO(new Flag("Gray-Aromatic Pride Flag", PrideFlagType.GRAY_ARO)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_INTERSEX(new Flag("Intersex Pride Flag", PrideFlagType.INTERSEX)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_LESBIAN(new Flag("Lesbian Pride Flag", PrideFlagType.LESBIAN)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_NONBINARY(new Flag("Nonbinary Pride Flag", PrideFlagType.NONBINARY)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_PAN(new Flag("Pansexual Pride Flag", PrideFlagType.PAN)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_POLYAM(new Flag("Polyamorous Pride Flag", PrideFlagType.POLYAM)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_POLYSEX(new Flag("Polysexual Pride Flag", PrideFlagType.POLYSEX)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANS(new Flag("Transgender Pride Flag", PrideFlagType.TRANS)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANSFEM(new Flag("Transfeminine Pride Flag", PrideFlagType.TRANSFEM)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANSMASC(new Flag("Transmasculine Pride Flag", PrideFlagType.TRANSMASC)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_QUEER(new Flag("Queer Pride Flag", PrideFlagType.QUEER)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_PRIDE(new Flag("Pride Flag", PrideFlagType.PRIDE)),

	// Pride Bunting
	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_ACE(new Bunting("Asexual Pride Bunting", PrideFlagType.ACE)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_AGENDER(new Bunting("Agender Pride Bunting", PrideFlagType.AGENDER)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_ARO(new Bunting("Aromatic Pride Bunting", PrideFlagType.ARO)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_BI(new Bunting("Bisexual Pride Bunting", PrideFlagType.BI)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMI(new Bunting("Demi Pride Bunting", PrideFlagType.DEMI)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIBOY(new Bunting("Demi Boy Pride Bunting", PrideFlagType.DEMIBOY)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIGIRL(new Bunting("Demi Girl Pride Bunting", PrideFlagType.DEMIGIRL)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIROMANTIC(new Bunting("Demiromantic Pride Bunting", PrideFlagType.DEMIROMANTIC)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GAY(new Bunting("Gay Pride Bunting", PrideFlagType.GAY)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENDERFLU(new Bunting("Genderfluid Pride Bunting", PrideFlagType.GENDERFLUID)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENDERFLUX(new Bunting("Genderflux Pride Bunting", PrideFlagType.GENDERFLUX)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENQUEER(new Bunting("Genderqueer Pride Bunting", PrideFlagType.GENDERQUEER)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GRAYACE(new Bunting("Gray-Asexual Pride Bunting", PrideFlagType.GRAY_ACE)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GRAYARO(new Bunting("Gray-Aromatic Pride Bunting", PrideFlagType.GRAY_ARO)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_INTERSEX(new Bunting("Intersex Pride Bunting", PrideFlagType.INTERSEX)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_LESBIAN(new Bunting("Lesbian Pride Bunting", PrideFlagType.LESBIAN)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_NONBINARY(new Bunting("Nonbinary Pride Bunting", PrideFlagType.NONBINARY)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_PAN(new Bunting("Pansexual Pride Bunting", PrideFlagType.PAN)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_POLYAM(new Bunting("Polyamorous Pride Bunting", PrideFlagType.POLYAM)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_POLYSEX(new Bunting("Polysexual Pride Bunting", PrideFlagType.POLYSEX)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANS(new Bunting("Transgender Pride Bunting", PrideFlagType.TRANS)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANSFEM(new Bunting("Transfeminine Pride Bunting", PrideFlagType.TRANSFEM)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANSMASC(new Bunting("Transmasculine Pride Bunting", PrideFlagType.TRANSMASC)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_QUEER(new Bunting("Queer Pride Bunting", PrideFlagType.QUEER)),

	@TypeConfig(money = 45, tokens = 4, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_PRIDE(new Bunting("Pride Bunting", PrideFlagType.PRIDE)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Outdoors
// 	------------------------------------------------------------------------------------------------------
	//	Windchimes
	@TypeConfig(money = 150, tokens = 15, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_IRON(new WindChime("Iron Windchimes", WindChimeType.IRON)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_GOLD(new WindChime("Gold Windchimes", WindChimeType.GOLD)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_COPPER(new WindChime("Copper Windchimes", WindChimeType.COPPER)),

	@TypeConfig(money = 900, tokens = 90, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_AMETHYST(new WindChime("Amethyst Windchimes", WindChimeType.AMETHYST)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_LAPIS(new WindChime("Lapis Windchimes", WindChimeType.LAPIS)),

	@TypeConfig(money = 3000, tokens = 300, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_NETHERITE(new WindChime("Netherite Windchimes", WindChimeType.NETHERITE)),

	@TypeConfig(money = 1500, tokens = 150, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_DIAMOND(new WindChime("Diamond Windchimes", WindChimeType.DIAMOND)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_REDSTONE(new WindChime("Redstone Windchimes", WindChimeType.REDSTONE)),

	@TypeConfig(money = 1050, tokens = 105, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_EMERALD(new WindChime("Emerald Windchimes", WindChimeType.EMERALD)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_QUARTZ(new WindChime("Quartz Windchimes", WindChimeType.QUARTZ)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_COAL(new WindChime("Coal Windchimes", WindChimeType.COAL)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_ICE(new WindChime("Ice Windchimes", WindChimeType.ICE)),

	// 	Birdhouses
	@TypeConfig(money = 150, tokens = 15, theme = Theme.OUTDOORS)
	BIRDHOUSE_FOREST_HORIZONTAL(new BirdHouse("Forest Birdhouse", ItemModelType.BIRDHOUSE_FOREST_HORIZONTAL, true)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.OUTDOORS)
	BIRDHOUSE_ENCHANTED_HORIZONTAL(new BirdHouse("Enchanted Birdhouse", ItemModelType.BIRDHOUSE_ENCHANTED_HORIZONTAL, true)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.OUTDOORS)
	BIRDHOUSE_DEPTHS_HORIZONTAL(new BirdHouse("Depths Birdhouse", ItemModelType.BIRDHOUSE_DEPTHS_HORIZONTAL, true)),

	// Flora
	@TypeConfig(money = 120, tokens = 12, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BUSHY_PLANT(new Flora(false, "Bushy Plant", ItemModelType.FLORA_BUSHY_PLANT, HitboxSingle.NONE, PlacementType.FLOOR)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_CHERRY_TREE(new Flora(false, "Potted Cherry Tree", ItemModelType.FLORA_POTTED_CHERRY_TREE, HitboxSingle._1x1_HEAD, PlacementType.FLOOR)),

	@TypeConfig(money = 165, tokens = 16, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_BAY_TREE(new Flora(false, "Potted Bay Tree", ItemModelType.FLORA_POTTED_BAY_TREE, HitboxFloor._1x2V, PlacementType.FLOOR)),

	@TypeConfig(money = 135, tokens = 13, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_SNAKE_PLANT(new Flora(false, "Snake Plant", ItemModelType.FLORA_SNAKE_PLANT, PlacementType.FLOOR)),

	@TypeConfig(money = 135, tokens = 13, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_WHITE_BIRD_PARADISE(new Flora(false, "White Bird of Paradise", ItemModelType.FLORA_WHITE_BIRD_PARADISE, PlacementType.FLOOR)),

	@TypeConfig(money = 210, tokens = 21, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI(new Flora(false, "Bonsai", ItemModelType.FLORA_BONSAI, PlacementType.FLOOR)),

	@TypeConfig(money = 210, tokens = 21, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_CHERRY(new Flora(false, "Cherry Bonsai", ItemModelType.FLORA_BONSAI_CHERRY, PlacementType.FLOOR)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_SMALL(new Flora(false, "Small Bonsai", ItemModelType.FLORA_BONSAI_SMALL, PlacementType.FLOOR)),

	@TypeConfig(money = 140, tokens = 14, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_CHERRY_SMALL(new Flora(false, "Small Cherry Bonsai", ItemModelType.FLORA_BONSAI_CHERRY_SMALL, PlacementType.FLOOR)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_CHINESE_EVERGREEN(new Flora(false, "Chinese Evergreen", ItemModelType.FLORA_CHINESE_EVERGREEN, PlacementType.FLOOR)),

	@TypeConfig(money = 135, tokens = 13, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_FLOWER_VASE(new Flora(false, "Flower Vase", ItemModelType.FLORA_FLOWER_VASE, PlacementType.FLOOR)),

	@TypeConfig(money = 105, tokens = 10, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_WALL_FLOWERS_1(new Flora(false, "Wall Flowers", ItemModelType.FLORA_WALL_FLOWERS_1, PlacementType.WALL)),

	@TypeConfig(money = 95, tokens = 9, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_TULIPS(new Flora(false, "Potted Tulips", ItemModelType.FLORA_POTTED_TULIPS, HitboxSingle._1x1_HEAD, PlacementType.FLOOR)),

	// Stumps
	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_OAK(new Stump(false, "Oak Stump", ItemModelType.STUMP_OAK)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_OAK_ROOTS(new Stump(false, "Rooted Oak Stump", ItemModelType.STUMP_OAK_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_SPRUCE(new Stump(false, "Spruce Stump", ItemModelType.STUMP_SPRUCE)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_SPRUCE_ROOTS(new Stump(false, "Rooted Spruce Stump", ItemModelType.STUMP_SPRUCE_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_BIRCH(new Stump(false, "Birch Stump", ItemModelType.STUMP_BIRCH)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_BIRCH_ROOTS(new Stump(false, "Rooted Birch Stump", ItemModelType.STUMP_BIRCH_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_JUNGLE(new Stump(false, "Jungle Stump", ItemModelType.STUMP_JUNGLE)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_JUNGLE_ROOTS(new Stump(false, "Rooted Jungle Stump", ItemModelType.STUMP_JUNGLE_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_ACACIA(new Stump(false, "Acacia Stump", ItemModelType.STUMP_ACACIA)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_ACACIA_ROOTS(new Stump(false, "Rooted Acacia Stump", ItemModelType.STUMP_ACACIA_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_DARK_OAK(new Stump(false, "Dark Oak Stump", ItemModelType.STUMP_DARK_OAK)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_DARK_OAK_ROOTS(new Stump(false, "Rooted Dark Oak Stump", ItemModelType.STUMP_DARK_OAK_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_MANGROVE(new Stump(false, "Mangrove Stump", ItemModelType.STUMP_MANGROVE)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_MANGROVE_ROOTS(new Stump(false, "Rooted Mangrove Stump", ItemModelType.STUMP_MANGROVE_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_CRIMSON(new Stump(false, "Crimson Stump", ItemModelType.STUMP_CRIMSON)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_CRIMSON_ROOTS(new Stump(false, "Rooted Crimson Stump", ItemModelType.STUMP_CRIMSON_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_WARPED(new Stump(false, "Warped Stump", ItemModelType.STUMP_WARPED)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_WARPED_ROOTS(new Stump(false, "Rooted Warped Stump", ItemModelType.STUMP_WARPED_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_CHERRY(new Stump(false, "Cherry Stump", ItemModelType.STUMP_CHERRY)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_CHERRY_ROOTS(new Stump(false, "Rooted Cherry Stump", ItemModelType.STUMP_CHERRY_ROOTS)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_PALE_OAK(new Stump(false, "Pale Oak Stump", ItemModelType.STUMP_PALE_OAK)),

	@TypeConfig(money = 75, tokens = 7, theme = Theme.OUTDOORS, tabs = Tab.STUMPS)
	STUMP_PALE_OAK_ROOTS(new Stump(false, "Rooted Pale Oak Stump", ItemModelType.STUMP_PALE_OAK_ROOTS)),

	// Misc
	@TypeConfig(money = 115, tokens = 11, theme = Theme.OUTDOORS)
	BED_SLEEPING_BAG(new DyeableFloorThing(false, "Sleeping Bag", ItemModelType.BED_SLEEPING_BAG, ColorableType.DYE)),

	@TypeConfig(money = 165, tokens = 16, theme = Theme.OUTDOORS)
	WELL(new Well(false, "Well", ItemModelType.WELL, ColorableType.STAIN, HitboxFloor._1x2V)),

	@TypeConfig(money = 120, tokens = 12, theme = Theme.OUTDOORS)
	CHAIR_METALLIC_BASIC(new DyeableChair(false, false, "Metallic Chair", ItemModelType.CHAIR_METALLIC_BASIC, ColorableType.MINERAL)),

	@TypeConfig(money = 60, tokens = 6, theme = Theme.OUTDOORS)
	TABLE_METALLIC_1x1(new Table(TableTheme.METALLIC, TableShape._1X1, ItemModelType.TABLE_METALLIC_1X1)),

	@TypeConfig(money = 225, tokens = 22, theme = Theme.OUTDOORS)
	BENCH_WOODEN(new DyeableBench(true, false, "Wooden Bench", ItemModelType.BENCH_WOODEN, ColorableType.STAIN, HitboxFloor._1x2H)),

	@TypeConfig(money = 700, tokens = 50, theme = Theme.OUTDOORS)
	WOODEN_PICNIC_TABLE(new DyeableBench(true, true, "Wooden Picnic Table", ItemModelType.WOODEN_PICNIC_TABLE, ColorableType.STAIN, HitboxUnique.WOODEN_PICNIC_TABLE)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Art
// 	------------------------------------------------------------------------------------------------------
	//	Custom
	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CHERRY_FOREST(new Art("Komorebi", ItemModelType.ART_PAINTING_CUSTOM_CHERRY_FOREST, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_END_ISLAND(new Art("Limbo", ItemModelType.ART_PAINTING_CUSTOM_END_ISLAND, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_LOST_ENDERMAN(new Art("Lost Enderman", ItemModelType.ART_PAINTING_CUSTOM_LOST_ENDERMAN, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PINE_TREE(new Art("Black Hills", ItemModelType.ART_PAINTING_CUSTOM_PINE_TREE, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SUNSET(new Art("Palm Cove", ItemModelType.ART_PAINTING_CUSTOM_SUNSET, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SWAMP_HUT(new Art("Isolation", ItemModelType.ART_PAINTING_CUSTOM_SWAMP_HUT, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_MOUNTAINS(new Art("Three Peaks", ItemModelType.ART_PAINTING_CUSTOM_MOUNTAINS, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_MUDDY_PIG(new Art("Blissful Piggy", ItemModelType.ART_PAINTING_CUSTOM_MUDDY_PIG, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PURPLE_SHEEP(new Art("Lavender Woolly", ItemModelType.ART_PAINTING_CUSTOM_PURPLE_SHEEP, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_VILLAGE_HAPPY(new Art("Sweet Home", ItemModelType.ART_PAINTING_CUSTOM_VILLAGE_HAPPY, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_VILLAGE_CHAOS(new Art("Revenge", ItemModelType.ART_PAINTING_CUSTOM_VILLAGE_CHAOS, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SKYBLOCK(new Art("Skyblock", ItemModelType.ART_PAINTING_CUSTOM_SKYBLOCK, HitboxWall._1x1_LIGHT)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_FORTRESS_BRIDGE(new Art("Nether Highways", ItemModelType.ART_PAINTING_CUSTOM_NETHER_FORTRESS_BRIDGE, HitboxWall._1x1_LIGHT)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_CRIMSON_FOREST(new Art("Crimson Canopy", ItemModelType.ART_PAINTING_CUSTOM_NETHER_CRIMSON_FOREST, HitboxWall._1x1_LIGHT)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_WARPED_FOREST(new Art("Warped Woods", ItemModelType.ART_PAINTING_CUSTOM_NETHER_WARPED_FOREST, HitboxWall._1x1_LIGHT)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_BASALT_DELTAS(new Art("Basalt Summits", ItemModelType.ART_PAINTING_CUSTOM_NETHER_BASALT_DELTAS, HitboxWall._1x1_LIGHT)),

	@TypeConfig(money = 150, tokens = 15, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_SOUL_SAND_VALLEY(new Art("Lost Souls", ItemModelType.ART_PAINTING_CUSTOM_NETHER_SOUL_SAND_VALLEY, HitboxWall._1x1_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CASTLE(new Art("Sintra", ItemModelType.ART_PAINTING_CUSTOM_CASTLE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_LAKE(new Art("Reflections", ItemModelType.ART_PAINTING_CUSTOM_LAKE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_RIVER(new Art("Flowing Home", ItemModelType.ART_PAINTING_CUSTOM_RIVER, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_ROAD(new Art("Take Me Home", ItemModelType.ART_PAINTING_CUSTOM_ROAD, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_ORIENTAL(new Art("Tenku No Torii", ItemModelType.ART_PAINTING_CUSTOM_ORIENTAL, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CHICKENS(new Art("Hens Night", ItemModelType.ART_PAINTING_CUSTOM_CHICKENS, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_OAK_TREE(new Art("English Oak", ItemModelType.ART_PAINTING_CUSTOM_OAK_TREE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CRAB(new Art("Nomad", ItemModelType.ART_PAINTING_CUSTOM_CRAB, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SATURN_ROCKET(new Art("Adventure Is Out There", ItemModelType.ART_PAINTING_CUSTOM_SATURN_ROCKET, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PARROT(new Art("Scarlet Macaw", ItemModelType.ART_PAINTING_CUSTOM_PARROT, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_DUCKS(new Art("Voyage", ItemModelType.ART_PAINTING_CUSTOM_DUCKS, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_STARRY_PINE_TREE(new Art("Lone Pine", ItemModelType.ART_PAINTING_CUSTOM_STARRY_PINE_TREE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_FOREST(new Art("Misty Thicket", ItemModelType.ART_PAINTING_CUSTOM_FOREST, HitboxWall._1x3H_LIGHT)),

	@TypeConfig(money = 450, tokens = 45, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SAND_DUNES(new Art("Sahara", ItemModelType.ART_PAINTING_CUSTOM_SAND_DUNES, HitboxWall._1x3V_LIGHT)),

	@TypeConfig(money = 900, tokens = 90, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_STORY(new Art("Daydreamer", ItemModelType.ART_PAINTING_CUSTOM_STORY, HitboxWall._2x3H_LIGHT)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CITY_TWILIGHT(new Art("City Twilight", ItemModelType.ART_PAINTING_CUSTOM_CITY_TWILIGHT, HitboxWall._2x2_LIGHT)),

	// Vanilla
	@TypeConfig(money = 150, tokens = 15, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_FRIEND(new Art("Friend", ItemModelType.ART_PAINTING_VANILLA_FRIEND, HitboxWall._1x1_LIGHT, true)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_BELOW(new Art("Below", ItemModelType.ART_PAINTING_VANILLA_BELOW, HitboxWall._1x2H_LIGHT, true)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_DIRT_HUT_ROAD(new Art("Dirt Hut Road", ItemModelType.ART_PAINTING_VANILLA_DIRT_HUT_ROAD, HitboxWall._1x2H_LIGHT, true)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VOWS_OF_THE_CRAFTSMAN(new Art("Vows of the Craftsman", ItemModelType.ART_PAINTING_VANILLA_VOWS_OF_THE_CRAFTSMAN, HitboxWall._1x2H_LIGHT, true)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VILLAGER_AND_CHILD(new Art("Villager and Child", ItemModelType.ART_PAINTING_VANILLA_VILLAGER_AND_CHILD, HitboxWall._1x2V_LIGHT, true)),

	@TypeConfig(money = 300, tokens = 30, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_THREE_MASON(new Art("Level 3 Mason", ItemModelType.ART_PAINTING_VANILLA_LEVEL_THREE_MASON, HitboxWall._1x2V_LIGHT, true)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_ANCIENT_POWER(new Art("Ancient Power", ItemModelType.ART_PAINTING_VANILLA_ANCIENT_POWER, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_IRON_SEED(new Art("Iron Seed", ItemModelType.ART_PAINTING_VANILLA_IRON_SEED, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_RIDERS(new Art("Riders", ItemModelType.ART_PAINTING_VANILLA_RIDERS, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_ONE_MASON(new Art("Level 1 Mason", ItemModelType.ART_PAINTING_VANILLA_LEVEL_ONE_MASON, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(money = 600, tokens = 60, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_TWO_MASON(new Art("Level 2 Mason", ItemModelType.ART_PAINTING_VANILLA_LEVEL_TWO_MASON, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(money = 1200, tokens = 120, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VILLAGER_TRADE(new Art("The Trade in the House of Villagers", ItemModelType.ART_PAINTING_VANILLA_VILLAGER_TRADE, HitboxWall._2x4H_LIGHT, true)),

	@TypeConfig(money = 1800, tokens = 180, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_SIGNS_OF_THE_END(new Art("Signs of the End", ItemModelType.ART_PAINTING_VANILLA_SIGNS_OF_THE_END, HitboxWall._4x4_LIGHT, true)),

	@TypeConfig(money = 1800, tokens = 180, theme = Theme.ART, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_BLESSED_SHEEP(new Art("Three Saints and the Blessed Sheep", ItemModelType.ART_PAINTING_VANILLA_BLESSED_SHEEP, HitboxWall._4x4_LIGHT, true)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: General
// 	------------------------------------------------------------------------------------------------------
	// 	Tables

	@TypeConfig(money = 60, tokens = 6, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x1(new Table(TableTheme.WOODEN, TableShape._1X1, ItemModelType.TABLE_WOODEN_1X1)),

	@TypeConfig(money = 105, tokens = 10, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x2(new Table(TableTheme.WOODEN, TableShape._1X2, ItemModelType.TABLE_WOODEN_1X2)),

	@TypeConfig(money = 135, tokens = 13, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x3(new Table(TableTheme.WOODEN, TableShape._1X3, ItemModelType.TABLE_WOODEN_1X3)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x2(new Table(TableTheme.WOODEN, TableShape._2X2, ItemModelType.TABLE_WOODEN_2X2)),

	@TypeConfig(money = 140, tokens = 14, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x2_CORNER(new Table(TableTheme.WOODEN, TableShape._2X2_CORNER, ItemModelType.TABLE_WOODEN_2X2_CORNER)),

	@TypeConfig(money = 225, tokens = 22, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x3(new Table(TableTheme.WOODEN, TableShape._2X3, ItemModelType.TABLE_WOODEN_2X3)),

	@TypeConfig(money = 185, tokens = 18, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x3_CORNER(new Table(TableTheme.WOODEN, TableShape._2X3_CORNER, ItemModelType.TABLE_WOODEN_2X3_CORNER)),

	@TypeConfig(money = 185, tokens = 18, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x3_CORNER_FLIPPED(new Table(TableTheme.WOODEN, TableShape._2X3_CORNER_FLIPPED, ItemModelType.TABLE_WOODEN_2X3_CORNER_FLIPPED)),

	@TypeConfig(money = 300, tokens = 30, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_3x3(new Table(TableTheme.WOODEN, TableShape._3X3, ItemModelType.TABLE_WOODEN_3X3)),

	@TypeConfig(money = 275, tokens = 27, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_3x3_CORNER(new Table(TableTheme.WOODEN, TableShape._3X3_CORNER, ItemModelType.TABLE_WOODEN_3X3_CORNER)),

	// 	Chairs
	@TypeConfig(money = 120, tokens = 12, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_BASIC(new DyeableChair(false, false, "Wooden Chair", ItemModelType.CHAIR_WOODEN_BASIC, ColorableType.STAIN)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_CUSHIONED(new DyeableChair(false, false, "Cushioned Wooden Chair", ItemModelType.CHAIR_WOODEN_CUSHIONED, ColorableType.DYE)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_CLOTH(new DyeableChair(false, false, "Cloth Chair", ItemModelType.CHAIR_CLOTH, ColorableType.DYE)),

	@TypeConfig(money = 135, tokens = 13, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	ADIRONDACK(new DyeableChair(false, false, "Adirondack", ItemModelType.ADIRONDACK, ColorableType.STAIN)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_BEACH(new LongChair(true, false, "Beach Chair", ItemModelType.BEACH_CHAIR, ColorableType.DYE, HitboxUnique.BEACH_CHAIR, .875)),

	// 	Stools
	@TypeConfig(money = 90, tokens = 9, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_BASIC(new DyeableChair(false, true, "Wooden Stool", ItemModelType.STOOL_WOODEN_BASIC, ColorableType.STAIN)),

	@TypeConfig(money = 120, tokens = 12, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_CUSHIONED(new DyeableChair(false, true, "Cushioned Wooden Stool", ItemModelType.STOOL_WOODEN_CUSHIONED, ColorableType.DYE)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_BAR_WOODEN(new DyeableChair(false, true, "Wooden Bar Stool", ItemModelType.STOOL_BAR_WOODEN, ColorableType.STAIN, 1.15)),

	// 	Couches
	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_LEFT(new DyeableCouch(false, "Cushioned Wooden Couch Left End", ItemModelType.COUCH_WOODEN_CUSHIONED_END_LEFT, ColorableType.DYE, ICouch.CouchPart.END)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_RIGHT(new DyeableCouch(false, "Cushioned Wooden Couch Right End", ItemModelType.COUCH_WOODEN_CUSHIONED_END_RIGHT, ColorableType.DYE, ICouch.CouchPart.END)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_MIDDLE(new DyeableCouch(false, "Cushioned Wooden Couch Middle", ItemModelType.COUCH_WOODEN_CUSHIONED_MIDDLE, ColorableType.DYE, ICouch.CouchPart.STRAIGHT)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_CORNER(new DyeableCouch(false, "Cushioned Wooden Couch Corner", ItemModelType.COUCH_WOODEN_CUSHIONED_CORNER, ColorableType.DYE, ICouch.CouchPart.CORNER)),

	@TypeConfig(money = 120, tokens = 12, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_OTTOMAN(new DyeableCouch(false, "Cushioned Wooden Couch Ottoman", ItemModelType.COUCH_WOODEN_CUSHIONED_OTTOMAN, ColorableType.DYE, ICouch.CouchPart.STRAIGHT)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_LEFT(new DyeableCouch(false, "Cloth Couch Left End", ItemModelType.COUCH_CLOTH_END_LEFT, ColorableType.DYE, ICouch.CouchPart.END)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_RIGHT(new DyeableCouch(false, "Cloth Couch Right End", ItemModelType.COUCH_CLOTH_END_RIGHT, ColorableType.DYE, ICouch.CouchPart.END)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_MIDDLE(new DyeableCouch(false, "Cloth Couch Middle", ItemModelType.COUCH_CLOTH_MIDDLE, ColorableType.DYE, ICouch.CouchPart.STRAIGHT)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_CORNER(new DyeableCouch(false, "Cloth Couch Corner", ItemModelType.COUCH_CLOTH_CORNER, ColorableType.DYE, ICouch.CouchPart.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_OTTOMAN(new DyeableCouch(false, "Cloth Couch Ottoman", ItemModelType.COUCH_CLOTH_OTTOMAN, ColorableType.DYE, ICouch.CouchPart.STRAIGHT)),

	// Flags
	@TypeConfig(money = 75, tokens = 7, tabs = Tab.FLAGS)
	FLAG_SERVER(new Flag(false, "Server Flag", ItemModelType.FLAG_SERVER)),

	// Bunting
	@TypeConfig(money = 60, tokens = 6, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1(new Bunting(true, "Server Colors 1 Bunting", ItemModelType.BUNTING_SERVER_COLORS_1)),

	@TypeConfig(money = 60, tokens = 6, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2(new Bunting(true, "Server Colors 2 Bunting", ItemModelType.BUNTING_SERVER_COLORS_2)),

	@TypeConfig(money = 30, tokens = 3, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1_SMALL(new Bunting(true, "Server Colors 1 Small Bunting", ItemModelType.BUNTING_SERVER_COLORS_1_SMALL, HitboxSingle._1x1_LIGHT)),

	@TypeConfig(money = 30, tokens = 3, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2_SMALL(new Bunting(true, "Server Colors 2 Small Bunting", ItemModelType.BUNTING_SERVER_COLORS_2_SMALL, HitboxSingle._1x1_LIGHT)),

	@TypeConfig(money = 75, tokens = 7, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_LOGO(new Bunting(true, "Server Logo Bunting", ItemModelType.BUNTING_SERVER_LOGO)),

	// Banners
	// 	Hanging
	@TypeConfig(money = 120, tokens = 12, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_1(new HangingBanner("Avontyre Royal Hanging Banner", ItemModelType.BANNER_HANGING_AVONTYRE_1, HitboxUnique.HANGING_BANNER_1x3V)),

	@TypeConfig(money = 105, tokens = 10, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_2(new HangingBanner("Avontyre Cyan Hanging Banner", ItemModelType.BANNER_HANGING_AVONTYRE_2, HitboxUnique.HANGING_BANNER_1x3V)),

	@TypeConfig(money = 105, tokens = 10, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_3(new HangingBanner("Avontyre Yellow Hanging Banner", ItemModelType.BANNER_HANGING_AVONTYRE_3, HitboxUnique.HANGING_BANNER_1x3V)),

	@TypeConfig(money = 90, tokens = 9, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_4(new HangingBanner("Avontyre Checkered Hanging Banner", ItemModelType.BANNER_HANGING_AVONTYRE_4, HitboxUnique.HANGING_BANNER_1x3V)),

	// Vanilla Banners
	//	Hanging
	@TypeConfig(money = 90, tokens = 9, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_SERVER_LOGO(new HangingBanner("Server Logo Hanging Banner", ItemModelType.BANNER_HANGING_SERVER_LOGO)),

	//	Standing
	@TypeConfig(money = 90, tokens = 9, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_STANDING})
	BANNER_STANDING_SERVER_LOGO(new StandingBanner("Server Logo Standing Banner", ItemModelType.BANNER_STANDING_SERVER_LOGO)),

	// 	Fireplaces
	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_DARK(new Fireplace(true, "Dark Fireplace", ItemModelType.FIREPLACE_DARK)),

	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_BROWN(new Fireplace(true, "Brown Fireplace", ItemModelType.FIREPLACE_BROWN)),

	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_WOODEN(new DyeableFireplace(true, "Wooden Fireplace", ItemModelType.FIREPLACE_WOODEN)),

	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_LIGHT(new Fireplace(true, "Light Fireplace", ItemModelType.FIREPLACE_LIGHT)),

	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_DARK_SOUL(new Fireplace(true, "Dark Soul Fireplace", ItemModelType.FIREPLACE_DARK_SOUL)),

	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_BROWN_SOUL(new Fireplace(true, "Brown Soul Fireplace", ItemModelType.FIREPLACE_BROWN_SOUL)),

	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_WOODEN_SOUL(new DyeableFireplace(true, "Wooden Soul Fireplace", ItemModelType.FIREPLACE_WOODEN_SOUL)),

	@TypeConfig(money = 525, tokens = 52, tabs = {Tab.FURNITURE, Tab.FIREPLACES})
	FIREPLACE_LIGHT_SOUL(new Fireplace(true, "Light Soul Fireplace", ItemModelType.FIREPLACE_LIGHT_SOUL)),

	//	Food
	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIZZA_BOX_SINGLE(new FloorThing(false, "Pizza Box", ItemModelType.FOOD_PIZZA_BOX_SINGLE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIZZA_BOX_SINGLE_OPENED(new FloorThing(false, "Opened Pizza Box", ItemModelType.FOOD_PIZZA_BOX_SINGLE_OPENED)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.FOOD)
	PIZZA_BOX_STACK(new FloorThing(false, "Pizza Box Stack", ItemModelType.FOOD_PIZZA_BOX_STACK)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	SOUP_MUSHROOM(new FloorThing(false, "Mushroom Soup", ItemModelType.FOOD_MUSHROOM_STEW)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	SOUP_BEETROOT(new FloorThing(false, "Beetroot Soup", ItemModelType.FOOD_BEETROOT_STEW)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	SOUP_RABBIT(new FloorThing(false, "Rabbit Soup", ItemModelType.FOOD_RABBIT_STEW)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	BREAD_LOAF(new FloorThing(false, "Loaf of Bread", ItemModelType.FOOD_BREAD_LOAF)),

	@TypeConfig(money = 25, tokens = 2, tabs = Tab.FOOD)
	BREAD_LOAF_CUT(new FloorThing(false, "Cut Loaf of Bread", ItemModelType.FOOD_BREAD_LOAF_CUT)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	BROWNIES_CHOCOLATE(new FloorThing(false, "Chocolate Brownies", ItemModelType.FOOD_BROWNIES_CHOCOLATE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	BROWNIES_VANILLA(new FloorThing(false, "Vanilla Brownies", ItemModelType.FOOD_BROWNIES_VANILLA)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	COOKIES_CHOCOLATE(new FloorThing(false, "Chocolate Cookies", ItemModelType.FOOD_COOKIES_CHOCOLATE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	COOKIES_CHOCOLATE_CHIP(new FloorThing(false, "Chocolate Chip Cookies", ItemModelType.FOOD_COOKIE_TRAY_CHOCOLATE_CHIP)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	COOKIES_SUGAR(new FloorThing(false, "Sugar Cookies", ItemModelType.FOOD_COOKIES_SUGAR)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	MILK_AND_COOKIES(new FloorThing(false, "Milk and Cookies", ItemModelType.FOOD_MILK_AND_COOKIES)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	MUFFINS_CHOCOLATE(new FloorThing(false, "Chocolate Muffins", ItemModelType.FOOD_MUFFINS_CHOCOLATE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	MUFFINS_CHOCOLATE_CHIP(new FloorThing(false, "Chocolate Chip Muffins", ItemModelType.FOOD_MUFFINS_CHOCOLATE_CHIP)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.FOOD)
	MUFFINS_LEMON(new FloorThing(false, "Lemon Muffins", ItemModelType.FOOD_MUFFINS_LEMON)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.FOOD)
	DINNER_HAM(new FloorThing(false, "Ham Dinner", ItemModelType.FOOD_DINNER_HAM)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.FOOD)
	DINNER_ROAST(new FloorThing(false, "Roast Dinner", ItemModelType.FOOD_DINNER_ROAST)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.FOOD)
	DINNER_TURKEY(new FloorThing(false, "Turkey Dinner", ItemModelType.FOOD_DINNER_TURKEY)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.FOOD)
	PUNCHBOWL(new DyeableFloorThing(false, "Dyeable Punchbowl", ItemModelType.FOOD_PUNCHBOWL, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.FOOD)
	PUNCHBOWL_EGGNOG(new DyeableFloorThing(false, "Eggnog", ItemModelType.FOOD_PUNCHBOWL_EGGNOG, ColorableType.DYE, "FFF4BB")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	SIDE_SAUCE(new DyeableFloorThing(false, "Dyeable Sauce Side", ItemModelType.FOOD_SIDE_SAUCE, ColorableType.DYE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	SIDE_SAUCE_CRANBERRIES(new DyeableFloorThing(false, "Cranberries Side", ItemModelType.FOOD_SIDE_SAUCE_CRANBERRIES, ColorableType.DYE, "C61B1B")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	SIDE_GREEN_BEAN_CASSEROLE(new FloorThing(false, "Green Bean Casserole Side", ItemModelType.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	SIDE_MAC_AND_CHEESE(new FloorThing(false, "Mac N' Cheese Side", ItemModelType.FOOD_SIDE_MAC_AND_CHEESE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	SIDE_SWEET_POTATOES(new FloorThing(false, "Sweet Potatoes Side", ItemModelType.FOOD_SIDE_SWEET_POTATOES)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	SIDE_MASHED_POTATOES(new FloorThing(false, "Mashed Potatoes Side", ItemModelType.FOOD_SIDE_MASHED_POTATOES)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	SIDE_ROLLS(new FloorThing(false, "Rolls", ItemModelType.FOOD_SIDE_ROLLS)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	CAKE_BATTER(new DyeableFloorThing(false, "Dyeable Cake Batter", ItemModelType.FOOD_CAKE_BATTER, ColorableType.DYE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	CAKE_BATTER_RED_VELVET(new DyeableFloorThing(false, "Red Velvet Cake Batter", ItemModelType.FOOD_CAKE_BATTER_VELVET, ColorableType.DYE, "720606")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	CAKE_BATTER_VANILLA(new DyeableFloorThing(false, "Vanilla Cake Batter", ItemModelType.FOOD_CAKE_BATTER_VANILLA, ColorableType.DYE, "FFF9CC")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	CAKE_BATTER_CHOCOLATE(new DyeableFloorThing(false, "Chocolate Cake Batter", ItemModelType.FOOD_CAKE_BATTER_CHOCOLATE, ColorableType.DYE, "492804")),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.FOOD)
	CAKE_WHITE_CHOCOLATE(new FloorThing(false, "White Chocolate Cake", ItemModelType.FOOD_CAKE_WHITE_CHOCOLATE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.FOOD)
	CAKE_BUNDT(new FloorThing(false, "Bundt Cake", ItemModelType.FOOD_CAKE_BUNDT)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.FOOD)
	CAKE_CHOCOLATE_DRIP(new FloorThing(false, "Chocolate Drip Cake", ItemModelType.FOOD_CAKE_CHOCOLATE_DRIP)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_ROUGH(new DyeableFloorThing(false, "Dyeable Rough Pie", ItemModelType.FOOD_PIE_ROUGH, ColorableType.DYE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_ROUGH_PECAN(new DyeableFloorThing(false, "Pecan Pie", ItemModelType.FOOD_PIE_ROUGH_PECAN, ColorableType.DYE, "4E3004")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_SMOOTH(new DyeableFloorThing(false, "Dyeable Smooth Pie", ItemModelType.FOOD_PIE_SMOOTH, ColorableType.DYE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_SMOOTH_CHOCOLATE(new DyeableFloorThing(false, "Chocolate Pie", ItemModelType.FOOD_PIE_SMOOTH_CHOCOLATE, ColorableType.DYE, "734008")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_SMOOTH_LEMON(new DyeableFloorThing(false, "Lemon Pie", ItemModelType.FOOD_PIE_SMOOTH_LEMON, ColorableType.DYE, "FFE050")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_SMOOTH_PUMPKIN(new DyeableFloorThing(false, "Pumpkin Pie Decoration", ItemModelType.FOOD_PIE_SMOOTH_PUMPKIN, ColorableType.DYE, "BF7D18")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_LATTICED(new DyeableFloorThing(false, "Dyeable Latticed Pie", ItemModelType.FOOD_PIE_LATTICED, ColorableType.DYE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_LATTICED_APPLE(new DyeableFloorThing(false, "Apple Pie", ItemModelType.FOOD_PIE_LATTICED_APPLE, ColorableType.DYE, "FDC330")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_LATTICED_BLUEBERRY(new DyeableFloorThing(false, "Blueberry Pie", ItemModelType.FOOD_PIE_LATTICED_BLUEBERRY, ColorableType.DYE, "4E1892")),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.FOOD)
	PIE_LATTICED_CHERRY(new DyeableFloorThing(false, "Cherry Pie", ItemModelType.FOOD_PIE_LATTICED_CHERRY, ColorableType.DYE, "B60C0C")),

	//	Kitchenware
	@TypeConfig(money = 45, tokens = 4, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE(new FloorThing(false, "Wine Bottle", ItemModelType.KITCHENWARE_WINE_BOTTLE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP(new FloorThing(false, "Wine Bottles", ItemModelType.KITCHENWARE_WINE_BOTTLE_GROUP)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_RANDOM(new FloorThing(false, "Random Wine Bottles", ItemModelType.KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_SIDE(new FloorThing(false, "Wine Bottles on Side", ItemModelType.KITCHENWARE_WINE_BOTTLE_GROUP_SIDE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.KITCHENWARE)
	WINE_GLASS(new FloorThing(false, "Wine Glass", ItemModelType.KITCHENWARE_WINE_GLASS)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.KITCHENWARE)
	WINE_GLASS_FULL(new FloorThing(false, "Full Wine Glass", ItemModelType.KITCHENWARE_WINE_GLASS_FULL)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.KITCHENWARE)
	MUG_GLASS(new FloorThing(false, "Glass Mug", ItemModelType.KITCHENWARE_MUG_GLASS)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.KITCHENWARE)
	MUG_GLASS_FULL(new FloorThing(false, "Full Glass Mug", ItemModelType.KITCHENWARE_MUG_GLASS_FULL)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.KITCHENWARE)
	MUG_WOODEN(new FloorThing(false, "Wooden Mug", ItemModelType.KITCHENWARE_MUG_WOODEN)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.KITCHENWARE)
	MUG_WOODEN_FULL(new FloorThing(false, "Full Wooden Mug", ItemModelType.KITCHENWARE_MUG_WOODEN_FULL)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_1(new FloorThing(false, "Random Glassware 1", ItemModelType.KITCHENWARE_GLASSWARE_GROUP_1)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_2(new FloorThing(false, "Random Glassware 2", ItemModelType.KITCHENWARE_GLASSWARE_GROUP_2)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_3(new FloorThing(false, "Random Glassware 3", ItemModelType.KITCHENWARE_GLASSWARE_GROUP_3)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.KITCHENWARE)
	JAR(new FloorThing(false, "Jar", ItemModelType.KITCHENWARE_JAR)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	JAR_HONEY(new FloorThing(false, "Honey Jar", ItemModelType.KITCHENWARE_JAR_HONEY)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	JAR_COOKIES(new FloorThing(false, "Cookie Jar", ItemModelType.KITCHENWARE_JAR_COOKIES)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.KITCHENWARE)
	JAR_WIDE(new FloorThing(false, "Wide Jar", ItemModelType.KITCHENWARE_JAR_WIDE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.KITCHENWARE)
	BOWL_DECORATION(new FloorThing(false, "Wooden Bowl", ItemModelType.KITCHENWARE_BOWL)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.KITCHENWARE)
	MIXING_BOWL(new FloorThing(false, "Mixing Bowl", ItemModelType.KITCHENWARE_MIXING_BOWL)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.KITCHENWARE)
	PAN_CAKE(new FloorThing(false, "Cake Pan", ItemModelType.KITCHENWARE_PAN_CAKE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.KITCHENWARE)
	PAN_CASSEROLE(new FloorThing(false, "Casserole Pan", ItemModelType.KITCHENWARE_PAN_CASSEROLE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.KITCHENWARE)
	PAN_COOKIE(new FloorThing(false, "Cookie Pan", ItemModelType.KITCHENWARE_PAN_COOKIE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.KITCHENWARE)
	PAN_MUFFIN(new FloorThing(false, "Muffin Pan", ItemModelType.KITCHENWARE_PAN_MUFFIN)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.KITCHENWARE)
	PAN_PIE(new FloorThing(false, "Pie Pan", ItemModelType.KITCHENWARE_PAN_PIE)),

	// 	Appliances
	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE(new DyeableFloorThing(false, "Fridge", ItemModelType.APPLIANCE_FRIDGE, ColorableType.DYE, "FFFFFF", HitboxFloor._1x2V)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MAGNETS(new DyeableFloorThing(false, "Fridge With Magnets", ItemModelType.APPLIANCE_FRIDGE_MAGNETS, ColorableType.DYE, "FFFFFF", HitboxFloor._1x2V)),

	@TypeConfig(money = 270, tokens = 27, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL(new DyeableFloorThing(false, "Tall Fridge", ItemModelType.APPLIANCE_FRIDGE_TALL, ColorableType.DYE, "FFFFFF", HitboxFloor._1x3V)),

	@TypeConfig(money = 285, tokens = 28, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL_MAGNETS(new DyeableFloorThing(false, "Tall Fridge With Magnets", ItemModelType.APPLIANCE_FRIDGE_TALL_MAGNETS, ColorableType.DYE, "FFFFFF", HitboxFloor._1x3V)),

	@TypeConfig(money = 90, tokens = 90, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI(new DyeableFloorThing(false, "Mini Fridge", ItemModelType.APPLIANCE_FRIDGE_MINI, ColorableType.DYE, "FFFFFF", HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 105, tokens = 10, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI_MAGNETS(new DyeableFloorThing(false, "Mini Fridge With Magnets", ItemModelType.APPLIANCE_FRIDGE_MINI_MAGNETS, ColorableType.DYE, "FFFFFF", HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_SLUSHIE_MACHINE(new DyeableFloorThing(false, "Slushie Machine", ItemModelType.APPLIANCE_SLUSHIE_MACHINE, ColorableType.DYE, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_GRILL_COMMERCIAL(new BlockThing("Commercial Grill", ItemModelType.APPLIANCE_GRILL_COMMERCIAL, RotationSnap.BOTH)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_OVEN_COMMERCIAL(new BlockThing("Commercial Oven", ItemModelType.APPLIANCE_OVEN_COMMERCIAL, RotationSnap.BOTH)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_DEEP_FRYER_COMMERCIAL(new BlockThing("Commercial Deep Fryer", ItemModelType.APPLIANCE_DEEP_FRYER_COMMERCIAL, RotationSnap.BOTH)),

	// Counters - STEEL HANDLES
	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_ISLAND(new Counter(ItemModelType.COUNTER_STEEL_MARBLE_ISLAND, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_CORNER(new Counter(ItemModelType.COUNTER_STEEL_MARBLE_CORNER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_DRAWER(new Counter(ItemModelType.COUNTER_STEEL_MARBLE_DRAWER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_CABINET(new Counter(ItemModelType.COUNTER_STEEL_MARBLE_CABINET, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_OVEN(new Counter(ItemModelType.COUNTER_STEEL_MARBLE_OVEN, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_SINK(new Counter(ItemModelType.COUNTER_STEEL_MARBLE_SINK, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_BAR(new Counter(ItemModelType.COUNTER_STEEL_MARBLE_BAR, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_ISLAND(new Counter(ItemModelType.COUNTER_STEEL_SOAPSTONE_ISLAND, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_CORNER(new Counter(ItemModelType.COUNTER_STEEL_SOAPSTONE_CORNER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_DRAWER(new Counter(ItemModelType.COUNTER_STEEL_SOAPSTONE_DRAWER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_CABINET(new Counter(ItemModelType.COUNTER_STEEL_SOAPSTONE_CABINET, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_OVEN(new Counter(ItemModelType.COUNTER_STEEL_SOAPSTONE_OVEN, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_SINK(new Counter(ItemModelType.COUNTER_STEEL_SOAPSTONE_SINK, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_BAR(new Counter(ItemModelType.COUNTER_STEEL_SOAPSTONE_BAR, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_ISLAND(new Counter(ItemModelType.COUNTER_STEEL_STONE_ISLAND, HandleType.STEEL, CounterMaterial.STONE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_CORNER(new Counter(ItemModelType.COUNTER_STEEL_STONE_CORNER, HandleType.STEEL, CounterMaterial.STONE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_DRAWER(new Counter(ItemModelType.COUNTER_STEEL_STONE_DRAWER, HandleType.STEEL, CounterMaterial.STONE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_CABINET(new Counter(ItemModelType.COUNTER_STEEL_STONE_CABINET, HandleType.STEEL, CounterMaterial.STONE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_OVEN(new Counter(ItemModelType.COUNTER_STEEL_STONE_OVEN, HandleType.STEEL, CounterMaterial.STONE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_SINK(new Counter(ItemModelType.COUNTER_STEEL_STONE_SINK, HandleType.STEEL, CounterMaterial.STONE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_BAR(new Counter(ItemModelType.COUNTER_STEEL_STONE_BAR, HandleType.STEEL, CounterMaterial.STONE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_ISLAND(new Counter(ItemModelType.COUNTER_STEEL_WOODEN_ISLAND, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_CORNER(new Counter(ItemModelType.COUNTER_STEEL_WOODEN_CORNER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_DRAWER(new Counter(ItemModelType.COUNTER_STEEL_WOODEN_DRAWER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_CABINET(new Counter(ItemModelType.COUNTER_STEEL_WOODEN_CABINET, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_OVEN(new Counter(ItemModelType.COUNTER_STEEL_WOODEN_OVEN, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_SINK(new Counter(ItemModelType.COUNTER_STEEL_WOODEN_SINK, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_BAR(new Counter(ItemModelType.COUNTER_STEEL_WOODEN_BAR, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - BRASS HANDLES
	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_ISLAND(new Counter(ItemModelType.COUNTER_BRASS_MARBLE_ISLAND, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_CORNER(new Counter(ItemModelType.COUNTER_BRASS_MARBLE_CORNER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_DRAWER(new Counter(ItemModelType.COUNTER_BRASS_MARBLE_DRAWER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_CABINET(new Counter(ItemModelType.COUNTER_BRASS_MARBLE_CABINET, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_OVEN(new Counter(ItemModelType.COUNTER_BRASS_MARBLE_OVEN, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_SINK(new Counter(ItemModelType.COUNTER_BRASS_MARBLE_SINK, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_BAR(new Counter(ItemModelType.COUNTER_BRASS_MARBLE_BAR, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_ISLAND(new Counter(ItemModelType.COUNTER_BRASS_SOAPSTONE_ISLAND, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_CORNER(new Counter(ItemModelType.COUNTER_BRASS_SOAPSTONE_CORNER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_DRAWER(new Counter(ItemModelType.COUNTER_BRASS_SOAPSTONE_DRAWER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_CABINET(new Counter(ItemModelType.COUNTER_BRASS_SOAPSTONE_CABINET, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_OVEN(new Counter(ItemModelType.COUNTER_BRASS_SOAPSTONE_OVEN, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_SINK(new Counter(ItemModelType.COUNTER_BRASS_SOAPSTONE_SINK, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_BAR(new Counter(ItemModelType.COUNTER_BRASS_SOAPSTONE_BAR, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_ISLAND(new Counter(ItemModelType.COUNTER_BRASS_STONE_ISLAND, HandleType.BRASS, CounterMaterial.STONE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_CORNER(new Counter(ItemModelType.COUNTER_BRASS_STONE_CORNER, HandleType.BRASS, CounterMaterial.STONE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_DRAWER(new Counter(ItemModelType.COUNTER_BRASS_STONE_DRAWER, HandleType.BRASS, CounterMaterial.STONE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_CABINET(new Counter(ItemModelType.COUNTER_BRASS_STONE_CABINET, HandleType.BRASS, CounterMaterial.STONE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_OVEN(new Counter(ItemModelType.COUNTER_BRASS_STONE_OVEN, HandleType.BRASS, CounterMaterial.STONE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_SINK(new Counter(ItemModelType.COUNTER_BRASS_STONE_SINK, HandleType.BRASS, CounterMaterial.STONE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_BAR(new Counter(ItemModelType.COUNTER_BRASS_STONE_BAR, HandleType.BRASS, CounterMaterial.STONE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_ISLAND(new Counter(ItemModelType.COUNTER_BRASS_WOODEN_ISLAND, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_CORNER(new Counter(ItemModelType.COUNTER_BRASS_WOODEN_CORNER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_DRAWER(new Counter(ItemModelType.COUNTER_BRASS_WOODEN_DRAWER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_CABINET(new Counter(ItemModelType.COUNTER_BRASS_WOODEN_CABINET, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_OVEN(new Counter(ItemModelType.COUNTER_BRASS_WOODEN_OVEN, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_SINK(new Counter(ItemModelType.COUNTER_BRASS_WOODEN_SINK, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_BAR(new Counter(ItemModelType.COUNTER_BRASS_WOODEN_BAR, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - BLACK HANDLES
	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_ISLAND(new Counter(ItemModelType.COUNTER_BLACK_MARBLE_ISLAND, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_CORNER(new Counter(ItemModelType.COUNTER_BLACK_MARBLE_CORNER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_DRAWER(new Counter(ItemModelType.COUNTER_BLACK_MARBLE_DRAWER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_CABINET(new Counter(ItemModelType.COUNTER_BLACK_MARBLE_CABINET, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_OVEN(new Counter(ItemModelType.COUNTER_BLACK_MARBLE_OVEN, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_SINK(new Counter(ItemModelType.COUNTER_BLACK_MARBLE_SINK, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_BAR(new Counter(ItemModelType.COUNTER_BLACK_MARBLE_BAR, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_ISLAND(new Counter(ItemModelType.COUNTER_BLACK_SOAPSTONE_ISLAND, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_CORNER(new Counter(ItemModelType.COUNTER_BLACK_SOAPSTONE_CORNER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_DRAWER(new Counter(ItemModelType.COUNTER_BLACK_SOAPSTONE_DRAWER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_CABINET(new Counter(ItemModelType.COUNTER_BLACK_SOAPSTONE_CABINET, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_OVEN(new Counter(ItemModelType.COUNTER_BLACK_SOAPSTONE_OVEN, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_SINK(new Counter(ItemModelType.COUNTER_BLACK_SOAPSTONE_SINK, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_BAR(new Counter(ItemModelType.COUNTER_BLACK_SOAPSTONE_BAR, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_ISLAND(new Counter(ItemModelType.COUNTER_BLACK_STONE_ISLAND, HandleType.BLACK, CounterMaterial.STONE, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_CORNER(new Counter(ItemModelType.COUNTER_BLACK_STONE_CORNER, HandleType.BLACK, CounterMaterial.STONE, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_DRAWER(new Counter(ItemModelType.COUNTER_BLACK_STONE_DRAWER, HandleType.BLACK, CounterMaterial.STONE, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_CABINET(new Counter(ItemModelType.COUNTER_BLACK_STONE_CABINET, HandleType.BLACK, CounterMaterial.STONE, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_OVEN(new Counter(ItemModelType.COUNTER_BLACK_STONE_OVEN, HandleType.BLACK, CounterMaterial.STONE, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_SINK(new Counter(ItemModelType.COUNTER_BLACK_STONE_SINK, HandleType.BLACK, CounterMaterial.STONE, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_BAR(new Counter(ItemModelType.COUNTER_BLACK_STONE_BAR, HandleType.BLACK, CounterMaterial.STONE, CounterType.BAR)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_ISLAND(new Counter(ItemModelType.COUNTER_BLACK_WOODEN_ISLAND, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_CORNER(new Counter(ItemModelType.COUNTER_BLACK_WOODEN_CORNER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CORNER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_DRAWER(new Counter(ItemModelType.COUNTER_BLACK_WOODEN_DRAWER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@TypeConfig(money = 165, tokens = 16, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_CABINET(new Counter(ItemModelType.COUNTER_BLACK_WOODEN_CABINET, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CABINET)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_OVEN(new Counter(ItemModelType.COUNTER_BLACK_WOODEN_OVEN, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.OVEN)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_SINK(new Counter(ItemModelType.COUNTER_BLACK_WOODEN_SINK, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.SINK)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_BAR(new Counter(ItemModelType.COUNTER_BLACK_WOODEN_BAR, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.BAR)),

	// Cabinets - STEEL HANDLES
	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN(new Cabinet(ItemModelType.CABINET_STEEL_WOODEN, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.CABINET)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN_HOOD(new Cabinet(ItemModelType.CABINET_STEEL_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.HOOD)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN_SHORT(new Cabinet(ItemModelType.CABINET_STEEL_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.SHORT)),

	// Cabinets - BRASS HANDLES
	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN(new Cabinet(ItemModelType.CABINET_BRASS_WOODEN, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.CABINET)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN_HOOD(new Cabinet(ItemModelType.CABINET_BRASS_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.HOOD)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN_SHORT(new Cabinet(ItemModelType.CABINET_BRASS_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.SHORT)),

	// Cabinets - BLACK HANDLES
	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN(new Cabinet(ItemModelType.CABINET_BLACK_WOODEN, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.CABINET)),

	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN_HOOD(new Cabinet(ItemModelType.CABINET_BLACK_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.HOOD)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN_SHORT(new Cabinet(ItemModelType.CABINET_BLACK_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.SHORT)),

	// Cabinets - GENERIC
	@TypeConfig(money = 195, tokens = 19, tabs = {Tab.FURNITURE, Tab.CABINETS})
	CABINET_HOOD(new WallThing(false, "Hood Cabinet", ItemModelType.CABINET_HOOD, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 180, tokens = 18, tabs = {Tab.FURNITURE, Tab.CABINETS})
	CABINET_WOODEN_CORNER(new Cabinet(ItemModelType.CABINET_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.NONE, CabinetType.CORNER)),

	@TypeConfig(money = 150, tokens = 15, tabs = {Tab.FURNITURE, Tab.CABINETS})
	CABINET_WOODEN_CORNER_SHORT(new Cabinet(ItemModelType.CABINET_WOODEN_CORNER_SHORT, CabinetMaterial.WOODEN, HandleType.NONE, CabinetType.SHORT_CORNER)),

	@TypeConfig(money = 225, tokens = 22, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	TOILET_MODERN(new DyeableChair(false, false, "Toilet Modern", ItemModelType.TOILET_MODERN, ColorableType.DYE, "FFFFFF", HitboxSingle._1x1_BARRIER, 1.3)),

	@TypeConfig(money = 450, tokens = 45, tabs = Tab.FURNITURE)
	WARDROBE(new DyeableFurniture(true, "Wardrobe", ItemModelType.WARDROBE, PlacementType.FLOOR, HitboxFloor._2x3V)),

	@TypeConfig(money = 240, tokens = 24, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_DOUBLE(new DyeableFurniture(true, "Short Cupboard Double", ItemModelType.CUPBOARD_SHORT_DOUBLE, PlacementType.FLOOR, HitboxFloor._1x2H)),

	@TypeConfig(money = 120, tokens = 12, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_SINGLE(new DyeableFurniture(false, "Short Cupboard Single", ItemModelType.CUPBOARD_SHORT_SINGLE, PlacementType.FLOOR, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 240, tokens = 24, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_BOOKSHELF_DOUBLE(new DyeableFurniture(true, "Short Bookshelf Cupboard Double", ItemModelType.CUPBOARD_SHORT_BOOKSHELF_DOUBLE, PlacementType.FLOOR, HitboxFloor._1x2H)),

	@TypeConfig(money = 120, tokens = 12, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_BOOKSHELF_SINGLE(new DyeableFurniture(false, "Short Bookshelf Cupboard Single", ItemModelType.CUPBOARD_SHORT_BOOKSHELF_SINGLE, PlacementType.FLOOR, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(unbuyable = true, money = 150, tabs = Tab.FURNITURE)
	SHELF_WALL(new DyeableWallThing(true, "Wall Shelf", ItemModelType.SHELF_WALL, ColorableType.STAIN, HitboxFloor._1x2H)),

	// Beds, multiblock = true
	@TypeConfig(money = 215, tokens = 21, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_1_SINGLE(new DyeableBedAddition("Generic Frame A Single", ItemModelType.BED_GENERIC_1_SINGLE, IBedAddition.AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(money = 430, tokens = 43, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_1_DOUBLE(new DyeableBedAddition("Generic Frame A Double", ItemModelType.BED_GENERIC_1_DOUBLE, IBedAddition.AdditionType.FRAME, true, ColorableType.STAIN)),

	@TypeConfig(money = 235, tokens = 23, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_2_SINGLE(new DyeableBedAddition("Generic Frame B Single", ItemModelType.BED_GENERIC_2_SINGLE, IBedAddition.AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(money = 470, tokens = 47, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_2_DOUBLE(new DyeableBedAddition("Generic Frame B Double", ItemModelType.BED_GENERIC_2_DOUBLE, IBedAddition.AdditionType.FRAME, true, ColorableType.STAIN)),

	@TypeConfig(money = 215, tokens = 21, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_3_SINGLE(new DyeableBedAddition("Generic Frame C Single", ItemModelType.BED_GENERIC_3_SINGLE, IBedAddition.AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(money = 430, tokens = 43, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_3_DOUBLE(new DyeableBedAddition("Generic Frame C Double", ItemModelType.BED_GENERIC_3_DOUBLE, IBedAddition.AdditionType.FRAME, true, ColorableType.STAIN)),

	@TypeConfig(money = 255, tokens = 25, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_4_SINGLE(new DyeableBedAddition("Generic Frame D Single", ItemModelType.BED_GENERIC_4_SINGLE, IBedAddition.AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(money = 510, tokens = 51, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_4_DOUBLE(new DyeableBedAddition("Generic Frame D Double", ItemModelType.BED_GENERIC_4_DOUBLE, IBedAddition.AdditionType.FRAME, true, ColorableType.STAIN)),

	//	Potions
	@TypeConfig(money = 45, tokens = 4, tabs = Tab.POTIONS)
	POTION_FILLED_TINY_1(new DyeableFloorThing(false, "Tiny Potions 1", ItemModelType.POTION_FILLED_TINY_1, ColorableType.DYE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.POTIONS)
	POTION_FILLED_TINY_2(new DyeableFloorThing(false, "Tiny Potions 2", ItemModelType.POTION_FILLED_TINY_2, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_1(new DyeableFloorThing(false, "Small Potion 1", ItemModelType.POTION_FILLED_SMALL_1, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_2(new DyeableFloorThing(false, "Small Potion 2", ItemModelType.POTION_FILLED_SMALL_2, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_3(new DyeableFloorThing(false, "Small Potion 3", ItemModelType.POTION_FILLED_SMALL_3, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_MEDIUM_1(new DyeableFloorThing(false, "Medium Potion 1", ItemModelType.POTION_FILLED_MEDIUM_1, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_MEDIUM_2(new DyeableFloorThing(false, "Medium Potion 2", ItemModelType.POTION_FILLED_MEDIUM_2, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.POTIONS)
	POTION_FILLED_WIDE(new DyeableFloorThing(false, "Wide Potion", ItemModelType.POTION_FILLED_WIDE, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.POTIONS)
	POTION_FILLED_SKINNY(new DyeableFloorThing(false, "Skinny Potion", ItemModelType.POTION_FILLED_SKINNY, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.POTIONS)
	POTION_FILLED_TALL(new DyeableFloorThing(false, "Tall Potion", ItemModelType.POTION_FILLED_TALL, ColorableType.DYE)),

	@TypeConfig(money = 105, tokens = 10, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_BOTTLE(new DyeableFloorThing(false, "Big Potion Bottle", ItemModelType.POTION_FILLED_BIG_BOTTLE, ColorableType.DYE)),

	@TypeConfig(money = 105, tokens = 10, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_TEAR(new DyeableFloorThing(false, "Big Potion Tear", ItemModelType.POTION_FILLED_BIG_TEAR, ColorableType.DYE)),

	@TypeConfig(money = 120, tokens = 12, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_DONUT(new DyeableFloorThing(false, "Big Potion Donut", ItemModelType.POTION_FILLED_BIG_DONUT, ColorableType.DYE)),

	@TypeConfig(money = 120, tokens = 12, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_SKULL(new DyeableFloorThing(false, "Big Potion Skull", ItemModelType.POTION_FILLED_BIG_SKULL, ColorableType.DYE)),

	@TypeConfig(money = 65, tokens = 6, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_SMALL(new DyeableFloorThing(false, "Small Potions", ItemModelType.POTION_FILLED_GROUP_SMALL, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_MEDIUM(new DyeableFloorThing(false, "Medium Potions", ItemModelType.POTION_FILLED_GROUP_MEDIUM, ColorableType.DYE)),

	@TypeConfig(money = 105, tokens = 10, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_TALL(new DyeableFloorThing(false, "Tall Potions", ItemModelType.POTION_FILLED_GROUP_TALL, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_1(new DyeableFloorThing(false, "Random Potions 1", ItemModelType.POTION_FILLED_GROUP_RANDOM_1, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_2(new DyeableFloorThing(false, "Random Potions 2", ItemModelType.POTION_FILLED_GROUP_RANDOM_2, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_3(new DyeableFloorThing(false, "Random Potions 3", ItemModelType.POTION_FILLED_GROUP_RANDOM_3, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_4(new DyeableFloorThing(false, "Random Potions 4", ItemModelType.POTION_FILLED_GROUP_RANDOM_4, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_5(new DyeableFloorThing(false, "Random Potions 5", ItemModelType.POTION_FILLED_GROUP_RANDOM_5, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_6(new DyeableFloorThing(false, "Random Potions 6", ItemModelType.POTION_FILLED_GROUP_RANDOM_6, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_7(new DyeableFloorThing(false, "Random Potions 7", ItemModelType.POTION_FILLED_GROUP_RANDOM_7, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_8(new DyeableFloorThing(false, "Random Potions 8", ItemModelType.POTION_FILLED_GROUP_RANDOM_8, ColorableType.DYE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_1(new DyeableFloorThing(false, "Empty Small Potion 1", ItemModelType.POTION_EMPTY_SMALL_1, ColorableType.DYE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_2(new DyeableFloorThing(false, "Empty Small Potion 2", ItemModelType.POTION_EMPTY_SMALL_2, ColorableType.DYE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_3(new DyeableFloorThing(false, "Empty Small Potion 3", ItemModelType.POTION_EMPTY_SMALL_3, ColorableType.DYE)),

	@TypeConfig(money = 45, tokens = 4, tabs = Tab.POTIONS)
	POTION_EMPTY_MEDIUM_1(new DyeableFloorThing(false, "Empty Medium Potion 1", ItemModelType.POTION_EMPTY_MEDIUM_1, ColorableType.DYE)),

	@TypeConfig(money = 54, tokens = 5, tabs = Tab.POTIONS)
	POTION_EMPTY_MEDIUM_2(new DyeableFloorThing(false, "Empty Medium Potion 2", ItemModelType.POTION_EMPTY_MEDIUM_2, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_EMPTY_WIDE(new DyeableFloorThing(false, "Empty Wide Potion", ItemModelType.POTION_EMPTY_WIDE, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_EMPTY_SKINNY(new DyeableFloorThing(false, "Empty Skinny Potion", ItemModelType.POTION_EMPTY_SKINNY, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_EMPTY_TALL(new DyeableFloorThing(false, "Empty Tall Potion", ItemModelType.POTION_EMPTY_TALL, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_BOTTLE(new DyeableFloorThing(false, "Empty Big Potion Bottle", ItemModelType.POTION_EMPTY_BIG_BOTTLE, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_TEAR(new DyeableFloorThing(false, "Empty Big Potion Tear", ItemModelType.POTION_EMPTY_BIG_TEAR, ColorableType.DYE)),

	@TypeConfig(money = 105, tokens = 10, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_DONUT(new DyeableFloorThing(false, "Empty Big Potion Donut", ItemModelType.POTION_EMPTY_BIG_DONUT, ColorableType.DYE)),

	@TypeConfig(money = 105, tokens = 10, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_SKULL(new DyeableFloorThing(false, "Empty Big Potion Skull", ItemModelType.POTION_EMPTY_BIG_SKULL, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_SMALL(new DyeableFloorThing(false, "Empty Small Potions", ItemModelType.POTION_EMPTY_GROUP_SMALL, ColorableType.DYE)),

	@TypeConfig(money = 75, tokens = 7, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_MEDIUM(new DyeableFloorThing(false, "Empty Medium Potions", ItemModelType.POTION_EMPTY_GROUP_MEDIUM, ColorableType.DYE)),

	@TypeConfig(money = 90, tokens = 9, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_TALL(new DyeableFloorThing(false, "Empty Tall Potions", ItemModelType.POTION_EMPTY_GROUP_TALL, ColorableType.DYE)),

	// Books
	@TypeConfig(money = 15, tokens = 1, tabs = Tab.BOOKS)
	BOOK_CLOSED(new DyeableFloorThing(false, "Closed Book", ItemModelType.BOOK_CLOSED, ColorableType.DYE)),

	@TypeConfig(money = 25, tokens = 2, tabs = Tab.BOOKS)
	BOOK_OPENED_1(new DyeableFloorThing(false, "Opened Book 1", ItemModelType.BOOK_OPENED_1, ColorableType.DYE)),

	@TypeConfig(money = 20, tokens = 2, tabs = Tab.BOOKS)
	BOOK_OPENED_2(new DyeableFloorThing(false, "Opened Book 2", ItemModelType.BOOK_OPENED_2, ColorableType.DYE)),

	@TypeConfig(money = 40, tokens = 4, tabs = Tab.BOOKS)
	BOOK_ROW_1(new DyeableFloorThing(false, "Book Row 1", ItemModelType.BOOK_ROW_1, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.BOOKS)
	BOOK_ROW_2(new DyeableFloorThing(false, "Book Row 2", ItemModelType.BOOK_ROW_2, ColorableType.DYE)),

	@TypeConfig(money = 30, tokens = 3, tabs = Tab.BOOKS)
	BOOK_STACK_1(new DyeableFloorThing(false, "Book Stack 1", ItemModelType.BOOK_STACK_1, ColorableType.DYE)),

	@TypeConfig(money = 40, tokens = 4, tabs = Tab.BOOKS)
	BOOK_STACK_2(new DyeableFloorThing(false, "Book Stack 2", ItemModelType.BOOK_STACK_2, ColorableType.DYE)),

	@TypeConfig(money = 60, tokens = 6, tabs = Tab.BOOKS)
	BOOK_STACK_3(new DyeableFloorThing(false, "Book Stack 3", ItemModelType.BOOK_STACK_3, ColorableType.DYE)),

	// Balloons
	@TypeConfig(money = 90, tokens = 9)
	BALLOON_SHORT(new DyeableFloorThing(false, "Balloon Short", ItemModelType.BALLOON_SHORT, ColorableType.DYE)),

	@TypeConfig(money = 105, tokens = 10)
	BALLOON_MEDIUM(new DyeableFloorThing(false, "Balloon Medium", ItemModelType.BALLOON_MEDIUM, ColorableType.DYE)),

	@TypeConfig(money = 120, tokens = 12)
	BALLOON_TALL(new DyeableFloorThing(false, "Balloon Tall", ItemModelType.BALLOON_TALL, ColorableType.DYE)),

	// Curtains
	@TypeConfig(money = 150, tokens = 15)
	WINDOW_CURTAINS_1x2(new Curtain("Window Curtains 1x2", CurtainType._1x2_OPEN)),

	@TypeConfig(money = 250, tokens = 25)
	WINDOW_CURTAINS_2x2(new Curtain("Window Curtains 2x2", CurtainType._2x2_OPEN)),

	@TypeConfig(money = 350, tokens = 35)
	WINDOW_CURTAINS_2x3H(new Curtain("Window Curtains 2x3H", CurtainType._2x3H_OPEN)),

	@TypeConfig(money = 250, tokens = 25)
	WINDOW_CURTAINS_1x3(new Curtain("Window Curtains 1x3", CurtainType._1x3_OPEN)),

	@TypeConfig(money = 350, tokens = 35)
	WINDOW_CURTAINS_2x3V(new Curtain("Window Curtains 2x3V", CurtainType._2x3V_OPEN)),

	@TypeConfig(money = 450, tokens = 45)
	WINDOW_CURTAINS_3x3(new Curtain("Window Curtains 3x3", CurtainType._3x3_OPEN)),

	// Barrels

	@TypeConfig(money = 200, tokens = 20)
	BARREL_LARGE(new FloorThing(true, "Large Barrel", ItemModelType.BARREL_LARGE, HitboxFloor._2x2x2)),

	@TypeConfig(money = 200, tokens = 20)
	BARREL_LARGE_SIDE(new FloorThing(true, "Large Barrel", ItemModelType.BARREL_LARGE_SIDE, HitboxFloor._2x2x2)),

	@TypeConfig(money = 250, tokens = 25)
	BARREL_LARGE_TAPPED(new FloorThing(true, "Large Barrel", ItemModelType.BARREL_LARGE_TAPPED, HitboxFloor._2x2x2)),

	@TypeConfig(money = 50, tokens = 5)
	BARREL_SMALL(new FloorThing(false, "Small Barrel", ItemModelType.BARREL_SMALL, HitboxSingle._1x1_HEAD)),

	@TypeConfig(money = 50, tokens = 5)
	BARREL_SMALL_SIDE(new FloorThing(false, "Small Barrel", ItemModelType.BARREL_SMALL_SIDE, HitboxSingle._1x1_HEAD)),

	//	Misc
	@TypeConfig(money = 75, tokens = 7)
	TRASH_CAN(new TrashCan("Trash Can", ItemModelType.TRASH_CAN, ColorableType.DYE, "C7C7C7", HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 50, tokens = 5)
	TRASH_BAG(new FloorThing(false, "Trash Bag", ItemModelType.TRASH_BAG, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 50, tokens = 5)
	JERRYCAN(new FloorThing(false, "Jerrycan", ItemModelType.JERRYCAN)),

	@TypeConfig(money = 300, tokens = 30)
	CEILING_FAN(new DyeableCeilingThing(false, "Ceiling Fan", ItemModelType.CEILING_FAN, ColorableType.STAIN, HitboxSingle._1x1_LIGHT)),

	@TypeConfig(money = 75, tokens = 7)
	LEATHER_BRIEFCASE(new FloorThing(false, "Leather Briefcase", ItemModelType.LEATHER_BRIEFCASE)),

	@TypeConfig(money = 15, tokens = 1)
	INKWELL(new FloorThing(false, "Inkwell", ItemModelType.INKWELL)),

	@TypeConfig(money = 75, tokens = 7)
	WHEEL_SMALL(new DecorationConfig(false, "Small Wheel", ItemModelType.WHEEL_SMALL)),

	@TypeConfig(money = 150, tokens = 15)
	TELESCOPE(new FloorThing(false, "Telescope", ItemModelType.TELESCOPE)),

	@TypeConfig(money = 75, tokens = 7)
	MICROSCOPE(new FloorThing(false, "Microscope", ItemModelType.MICROSCOPE)),

	@TypeConfig(money = 75, tokens = 7)
	MICROSCOPE_WITH_GEM(new FloorThing(false, "Microscope With Gem", ItemModelType.MICROSCOPE_WITH_GEM)),

	@TypeConfig(money = 135, tokens = 13)
	HELM(new DecorationConfig(false, "Helm", ItemModelType.HELM)),

	@TypeConfig(money = 60, tokens = 6)
	TRAFFIC_BLOCKADE(new DyeableFloorThing(false, "Traffic Blockade", ItemModelType.TRAFFIC_BLOCKADE, ColorableType.DYE, "FF7F00", HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 75, tokens = 7)
	TRAFFIC_BLOCKADE_LIGHTS(new DyeableFloorThing(false, "Traffic Blockade with Lights", ItemModelType.TRAFFIC_BLOCKADE_LIGHTS, ColorableType.DYE, "FF7F00", HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 60, tokens = 6)
	TRAFFIC_CONE(new DyeableFloorThing(false, "Traffic Cone", ItemModelType.TRAFFIC_CONE, ColorableType.DYE, "FF7F00", HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 40, tokens = 4)
	TRAFFIC_CONE_SMALL(new DyeableFloorThing(false, "Traffic Cone Small", ItemModelType.TRAFFIC_CONE_SMALL, ColorableType.DYE, "FF7F00", HitboxSingle.NONE)),

	@TypeConfig(money = 80, tokens = 8)
	TRAFFIC_CONE_BARREL(new DyeableFloorThing(false, "Traffic Cone Barrel", ItemModelType.TRAFFIC_CONE_BARREL, ColorableType.DYE, "FF7F00", HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 50, tokens = 5)
	TRAFFIC_CONE_TUBE(new DyeableFloorThing(false, "Traffic Cone Tube", ItemModelType.TRAFFIC_CONE_TUBE, ColorableType.DYE, "FF7F00", HitboxSingle._1x1_CHAIN)),

	@TypeConfig(money = 150, tokens = 15)
	POSTBOX(new FloorThing(false, "Postbox", ItemModelType.POSTBOX, HitboxFloor._1x2V)),

	@TypeConfig(money = 90, tokens = 9)
	MAILBOX(new Mailbox(false, "Mailbox", ItemModelType.MAILBOX, ColorableType.DYE, "C7C7C7", HitboxFloor._1x2V)),

	@TypeConfig(money = 60, tokens = 6)
	SANDWICH_SIGN(new FloorThing(false, "Sandwich Sign", ItemModelType.SANDWICH_SIGN)),

	@TypeConfig(money = 75, tokens = 7)
	SANDWICH_SIGN_TALL(new FloorThing(false, "Sandwich Sign Tall", ItemModelType.SANDWICH_SIGN_TALL)),

	@TypeConfig(money = 60, tokens = 6)
	FIRE_HYDRANT(new DyeableFloorThing(false, "Fire Hydrant", ItemModelType.FIRE_HYDRANT, ColorableType.DYE, "FF4233", HitboxSingle._1x1_CHAIN)),

	@TypeConfig(money = 90, tokens = 9)
	ROTARY_PHONE(new DyeableFloorThing(false, "Rotary Phone", ItemModelType.ROTARY_PHONE, ColorableType.DYE, "FF4233")),

	@TypeConfig(money = 90, tokens = 9)
	LAPTOP(new DyeableFloorThing(false, "Laptop", ItemModelType.LAPTOP, ColorableType.DYE, "A900FF")),

	@TypeConfig(money = 90, tokens = 9)
	ROUTER(new DyeableFloorThing(false, "Router", ItemModelType.ROUTER, ColorableType.DYE, "0040FF")),

	@TypeConfig(money = 90, tokens = 9)
	REGISTER_MODERN(new FloorThing(false, "Modern Register", ItemModelType.REGISTER_MODERN)),

	@TypeConfig(money = 40, tokens = 4)
	CARDBOARD_BOX_SMALL(new FloorThing(false, "Small Cardboard Box", ItemModelType.CARDBOARD_BOX_SMALL)),

	@TypeConfig(money = 60, tokens = 6)
	CARDBOARD_BOX_MEDIUM(new FloorThing(false, "Medium Cardboard Box", ItemModelType.CARDBOARD_BOX_MEDIUM, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 100, tokens = 10)
	CARDBOARD_BOX_LARGE(new FloorThing(true, "Large Cardboard Box", ItemModelType.CARDBOARD_BOX_LARGE, HitboxFloor._2x2x2)),

	@TypeConfig(money = 500, tokens = 50)
	PLAYER_HEAD_BLOCK(new PlayerHeadBlock(false, "Player Head Block", ItemModelType.PLAYER_HEAD_BLOCK, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 200, tokens = 20)
	FLAT_SCREEN_TV(new TV("Flat Screen TV", ChannelType.OFF)),

	@TypeConfig(money = 20, tokens = 2)
	LUMBER_PILE(new DyeableFloorThing(false, "Lumber Pile", ItemModelType.LUMBER_PILE, ColorableType.STAIN)),

	@TypeConfig(money = 50, tokens = 5)
	LUMBER_STACK(new DyeableFloorThing(true, "Lumber Stack", ItemModelType.LUMBER_STACK, ColorableType.STAIN, HitboxFloor._2x2)),

	@TypeConfig(money = 20, tokens = 2)
	WOODEN_PALLET(new DyeableFloorThing(false, "Wooden Pallet", ItemModelType.WOODEN_PALLET, ColorableType.STAIN)),

	@TypeConfig(money = 30, tokens = 3)
	VAULT(new FloorThing(false, "Vault", ItemModelType.VAULT, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(money = 40, tokens = 4)
	ANTIQUE_LAMP_SHORT(new DyeableFloorThing(false, "Short Antique Lamp", ItemModelType.ANTIQUE_LAMP_SHORT, ColorableType.DYE, HitboxUnique.LAMP_SHORT)),

	@TypeConfig(money = 60, tokens = 6)
	ANTIQUE_LAMP(new DyeableFloorThing(false, "Antique Lamp", ItemModelType.ANTIQUE_LAMP, ColorableType.DYE, HitboxUnique.LAMP)),

// 	------------------------------------------------------------------------------------------------------
//									UNBUYABLE & EXCLUSIVE
// 	------------------------------------------------------------------------------------------------------

	@TypeConfig(unbuyable = true)
	ARCADE_MACHINE(new FloorThing(true, "Arcade Machine", ItemModelType.ARCADE_MACHINE, HitboxFloor._1x2V)),

	@TypeConfig(unbuyable = true)
	PAPER_LANTERN_SINGLE(new CeilingThing(true, false, "Paper Lanterns - Single", ItemModelType.PAPER_LANTERN_SINGLE, HitboxUnique.PAPER_LANTERN_2V)),

	@TypeConfig(unbuyable = true)
	PAPER_LANTERN_DOUBLE(new CeilingThing(true, false, "Paper Lanterns - Double", ItemModelType.PAPER_LANTERN_DOUBLE, HitboxUnique.PAPER_LANTERN_2V)),

	@TypeConfig(unbuyable = true)
	PAPER_LANTERN_TRIPLE(new CeilingThing(true, false, "Paper Lanterns - Triple", ItemModelType.PAPER_LANTERN_TRIPLE, HitboxUnique.PAPER_LANTERN_3V)),

// 	------------------------------------------------------------------------------------------------------
//											EDIBLE
// 	------------------------------------------------------------------------------------------------------

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	ROAST_CHICKEN(new Edible(EdibleType.ROAST_CHICKEN, ItemModelType.ROAST_CHICKEN_STAGE_0, 0)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	ROAST_CHICKEN_1(new Edible(EdibleType.ROAST_CHICKEN, ItemModelType.ROAST_CHICKEN_STAGE_1, 1)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	ROAST_CHICKEN_2(new Edible(EdibleType.ROAST_CHICKEN, ItemModelType.ROAST_CHICKEN_STAGE_2, 2)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	ROAST_CHICKEN_3(new Edible(EdibleType.ROAST_CHICKEN, ItemModelType.ROAST_CHICKEN_STAGE_3, 3)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	ROAST_CHICKEN_4(new Edible(EdibleType.ROAST_CHICKEN, ItemModelType.ROAST_CHICKEN_STAGE_4, 4)),

// 	------------------------------------------------------------------------------------------------------
//										INTERNAL USE ONLY
// 	------------------------------------------------------------------------------------------------------

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	CLOWN_IN_THE_BOX_CLOSED_ON(new ClownInTheBox(ClownInTheBoxType.CLOSED_ON)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	CLOWN_IN_THE_BOX_OPEN_ON(new ClownInTheBox(ClownInTheBoxType.OPEN_ON)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY)
	CLOWN_IN_THE_BOX_OPEN_OFF(new ClownInTheBox(ClownInTheBoxType.OPEN_OFF)),

	@TypeConfig(unbuyable = true, theme = Theme.SPOOKY, tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_SHELF_WALL_1X1(new WallThing(false, "Spooky Wall Shelf 1x1", ItemModelType.SHELF_WALL_1X1_SPOOKY, HitboxSingle._1x1_BARRIER)),

	@TypeConfig(unbuyable = true, theme = Theme.SPOOKY,  tabs = Tab.SPOOKY_FURNITURE)
	SPOOKY_SHELF_WALL_1X2(new WallThing(true, "Spooky Wall Shelf 1x2", ItemModelType.SHELF_WALL_1X2_SPOOKY, HitboxFloor._1x2H)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	GIANT_CANDLE_ONE_LIT(new GiantCandle("Giant Candle", CandleType.ONE, true)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	GIANT_CANDLE_TWO_LIT(new GiantCandle("Giant Candles", CandleType.TWO, true)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	GIANT_CANDLE_THREE_LIT(new GiantCandle("Giant Candles", CandleType.THREE, true)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	GIANT_CANDLE_ONE_LIT_DYEABLE(new DyeableGiantCandle("Giant Candle", DyeableCandleType.ONE, true)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	GIANT_CANDLE_TWO_LIT_DYEABLE(new DyeableGiantCandle("Giant Candles", DyeableCandleType.TWO, true)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	GIANT_CANDLE_THREE_LIT_DYEABLE(new DyeableGiantCandle("Giant Candles", DyeableCandleType.THREE, true)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY, tabs = Tab.INTERNAL)
	SNOWGLOBE_TREE(new SnowGlobe(SnowGlobeType.TREE)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY, tabs = Tab.INTERNAL)
	SNOWGLOBE_PUG(new SnowGlobe(SnowGlobeType.PUG)),

	@TypeConfig(unbuyable = true, theme = Theme.HOLIDAY, tabs = Tab.INTERNAL)
	SNOWGLOBE_SNOWMAN(new SnowGlobe(SnowGlobeType.SNOWMAN)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_TEST_PATTERN(new TV("Flat Screen TV", ChannelType.TEST_PATTERN)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_STATIC(new TV("Flat Screen TV", ChannelType.STATIC)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_BACK_TO_THE_FUTURE(new TV("Flat Screen TV", ChannelType.BACK_TO_THE_FUTURE)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_BEE_MOVIE(new TV("Flat Screen TV", ChannelType.BEE_MOVIE)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_BREAKING_BAD(new TV("Flat Screen TV", ChannelType.BREAKING_BAD)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_DEXTER(new TV("Flat Screen TV", ChannelType.DEXTER)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_ET(new TV("Flat Screen TV", ChannelType.ET)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_FOREST_GUMP(new TV("Flat Screen TV", ChannelType.FOREST_GUMP)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_HOUSE_MD(new TV("Flat Screen TV", ChannelType.HOUSE_MD)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_HUB(new TV("Flat Screen TV", ChannelType.HUB)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_INTERSTELLAR(new TV("Flat Screen TV", ChannelType.INTERSTELLAR)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_INVINCIBLE(new TV("Flat Screen TV", ChannelType.INVINCIBLE)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_JOKER(new TV("Flat Screen TV", ChannelType.JOKER)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_LION_KING(new TV("Flat Screen TV", ChannelType.LION_KING)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_LORD_OF_THE_RINGS(new TV("Flat Screen TV", ChannelType.LORD_OF_THE_RINGS)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_MATRIX(new TV("Flat Screen TV", ChannelType.MATRIX)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_ROKU(new TV("Flat Screen TV", ChannelType.ROKU)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_SHINING(new TV("Flat Screen TV", ChannelType.SHINING)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_SMILING_FRIENDS(new TV("Flat Screen TV", ChannelType.SMILING_FRIENDS)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_STAR_WARS(new TV("Flat Screen TV", ChannelType.STAR_WARS)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_TITANIC(new TV("Flat Screen TV", ChannelType.TITANIC)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_TRUMAN_SHOW(new TV("Flat Screen TV", ChannelType.TRUMAN_SHOW)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_WALL_E(new TV("Flat Screen TV", ChannelType.WALL_E)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	FLAT_SCREEN_TV_WIZARD_OF_OZ(new TV("Flat Screen TV", ChannelType.WIZARD_OF_OZ)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	SHELF_STORAGE(new DyeableFurniture(true, "Storage Shelf", ItemModelType.SHELF_STORAGE, PlacementType.FLOOR, HitboxFloor._2x3V)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WAYSTONE(new Waystone("Waystone", ItemModelType.WAYSTONE, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL) // Tickable
	WAYSTONE_ACTIVATED(new Waystone("Activated Waystone", ItemModelType.WAYSTONE_ACTIVATED, true)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WINDOW_CURTAINS_1x2_CLOSED(new Curtain("Window Curtains 1x2", CurtainType._1x2_CLOSED)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WINDOW_CURTAINS_1x3_CLOSED(new Curtain("Window Curtains 1x3", CurtainType._1x3_CLOSED)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WINDOW_CURTAINS_2x2_CLOSED(new Curtain("Window Curtains 2x2", CurtainType._2x2_CLOSED)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WINDOW_CURTAINS_2x3H_CLOSED(new Curtain("Window Curtains 2x3H",CurtainType._2x3H_CLOSED)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WINDOW_CURTAINS_2x3V_CLOSED(new Curtain("Window Curtains 2x3V",CurtainType._2x3V_CLOSED)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WINDOW_CURTAINS_3x3_CLOSED(new Curtain("Window Curtains 3x3", CurtainType._3x3_CLOSED)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	BIRDHOUSE_FOREST_VERTICAL(new BirdHouse("Vertical Forest Birdhouse", ItemModelType.BIRDHOUSE_FOREST_VERTICAL, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	BIRDHOUSE_FOREST_HANGING(new BirdHouse("Hanging Forest Birdhouse", ItemModelType.BIRDHOUSE_FOREST_HANGING, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	BIRDHOUSE_ENCHANTED_VERTICAL(new BirdHouse("Vertical Enchanted Birdhouse", ItemModelType.BIRDHOUSE_ENCHANTED_VERTICAL, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	BIRDHOUSE_ENCHANTED_HANGING(new BirdHouse("Hanging Enchanted Birdhouse", ItemModelType.BIRDHOUSE_ENCHANTED_HANGING, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	BIRDHOUSE_DEPTHS_VERTICAL(new BirdHouse("Vertical Depths Birdhouse", ItemModelType.BIRDHOUSE_DEPTHS_VERTICAL, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	BIRDHOUSE_DEPTHS_HANGING(new BirdHouse("Hanging Depths Birdhouse", ItemModelType.BIRDHOUSE_DEPTHS_HANGING, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	DYE_STATION(new WorkBench("Dye Station", ItemModelType.DYE_STATION)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	ENCHANTED_BOOK_SPLITTER(new WorkBench("Enchanted Book Splitter", ItemModelType.ENCHANTED_BOOK_SPLITTER, HitboxFloor._1x2H)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	TOOL_MODIFICATION_TABLE(new WorkBench("Tool Modification Table", ItemModelType.TOOL_MODIFICATION_TABLE, HitboxFloor._1x2H)),

	;
	// @formatter:on

	private final DecorationConfig config;

	@SneakyThrows
	public TypeConfig getTypeConfig() {
		return this.getClass().getField(this.name()).getAnnotation(TypeConfig.class);
	}

	public static void initDecorations() {
		// Init all decoration creators
		TrophyType.initDecorations();
		Pose.initDecorations();
		BackpackTier.initDecoration();
	}

	public static void registerRecipes() {
		for (DecorationType decorationType : DecorationType.values()) {
			if (decorationType.getConfig() instanceof CraftableDecoration craftable) {
				NexusRecipe recipe = craftable.buildRecipe();
				if (recipe != null)
					recipe.register();
			}
		}
	}

	public static @Nullable DecorationType of(DecorationConfig config) {
		for (DecorationType type : values()) {
			if (type.getConfig().equals(config))
				return type;
		}

		return null;
	}

	public static final Map<Tab, List<DecorationType>> tabTypeMap = getTabTypeMap();

	@SneakyThrows
	public static Map<Tab, List<DecorationType>> getTabTypeMap() {
		if (tabTypeMap != null)
			return tabTypeMap;

		Map<Tab, List<DecorationType>> newTabMap = new HashMap<>();
		Tab lastTab = Tab.INTERNAL_ROOT;
		for (DecorationType type : values()) {
			TypeConfig typeConfig = type.getTypeConfig();
			if (typeConfig != null && typeConfig.tabs().length != 0) {
				Tab[] tabs = typeConfig.tabs();
				lastTab = tabs[tabs.length - 1];
			}

			if (lastTab == Tab.INTERNAL)
				continue;

			List<DecorationType> tabTypes = newTabMap.getOrDefault(lastTab, new ArrayList<>());
			tabTypes.add(type);
			newTabMap.put(lastTab, tabTypes);
		}

		return newTabMap;
	}

	public static final Map<Tab, List<Tab>> subTabMap = getSubTabMap();

	@SneakyThrows
	public static Map<Tab, List<Tab>> getSubTabMap() {
		if (subTabMap != null)
			return subTabMap;

		Map<Tab, List<Tab>> newSubTabMap = new HashMap<>();

		Tab tab = Tab.INTERNAL_ROOT;
		for (DecorationType type : values()) {
			List<Tab> subTabs = newSubTabMap.getOrDefault(tab, new ArrayList<>());

			TypeConfig typeConfig = type.getTypeConfig();
			if (typeConfig != null && typeConfig.tabs().length != 0) {
				List<Tab> tabs = new ArrayList<>(List.of(typeConfig.tabs()));

				tab = tabs.removeFirst();
				subTabs.addAll(tabs);
			}

			newSubTabMap.put(tab, subTabs);
		}

		return new HashMap<>();
	}


	public static final CategoryTree categoryTree = getCategoryTree();

	@SneakyThrows
	public static CategoryTree getCategoryTree() {
		if (categoryTree != null)
			return categoryTree;

		CategoryTree root = new CategoryTree(Tab.INTERNAL_ROOT);

		for (DecorationType type : values()) {
			CategoryTree current = root;

			TypeConfig typeConfig = type.getTypeConfig();
			if (typeConfig == null || typeConfig.tabs().length == 0) {
				root.addDecorationType(type);
				continue;
			}

			Tab[] tabs = typeConfig.tabs();
			Tab lastTab = tabs[tabs.length - 1];
			for (Tab _tab : tabs) {
				if (current == null)
					break;

				CategoryTree childTree = current.getChild(_tab);
				if (!current.containsChild(_tab)) {
					childTree = new CategoryTree(_tab);
					current.addChild(childTree);
				}

				if (childTree != null && lastTab == _tab)
					childTree.addDecorationType(type);

				current = childTree;
			}
		}

		return root;
	}

	@Data
	@AllArgsConstructor
	public static class CategoryTree {
		@NonNull Tab tabParent;
		List<DecorationType> decorationTypes;
		List<CategoryTree> tabChildren;

		public CategoryTree(@NotNull Tab tab) {
			this.tabParent = tab;
			this.decorationTypes = new ArrayList<>();
			this.tabChildren = new ArrayList<>();
		}

		public boolean isRoot() {
			return tabParent == Tab.INTERNAL_ROOT;
		}

		public boolean isInvisible() {
			return tabParent == Tab.INTERNAL;
		}

		public void addChild(CategoryTree tree) {
			tabChildren.add(tree);
		}

		public void addDecorationType(DecorationType type) {
			decorationTypes.add(type);
		}

		public boolean containsChild(Tab tab) {
			return getChild(tab) != null;
		}

		public @Nullable CategoryTree getChild(Tab tab) {
			for (CategoryTree tabChild : tabChildren) {
				if (tabChild.getTabParent() == tab)
					return tabChild;
			}

			return null;
		}
	}

}
