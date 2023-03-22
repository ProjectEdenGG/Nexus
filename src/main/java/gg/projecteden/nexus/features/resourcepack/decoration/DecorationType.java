package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Tab;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable.ColorableType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Shape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Unique;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art.ArtSize;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Bunting;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet.CabinetMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet.CabinetType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.HandleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fridge;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fridge.FridgeSize;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Furniture;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Furniture.FurnitureSurface;
import gg.projecteden.nexus.features.resourcepack.decoration.types.HangingBanner;
import gg.projecteden.nexus.features.resourcepack.decoration.types.StandingBanner;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.BirdHouse;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime.WindChimeType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.DyeableInstrument;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Chair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch.CouchPart;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.LongChair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Stump;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.TestThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.WorkBench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.Block;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.CeilingThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.Shelf;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	TODO:
		- Add:
			- Remaining decorations:
				- toAdd
				- Mailbox -> texture
				- Red lawn chair -> texture
				- Dog House
				- Firewatch painting
			- Prices:
				- Catalogs
				- Paintbrush -> Painter?
				- Buyable Decorations
		- Release Feature, afterwards:
			- Better support for:
				- Multi-Surface models -> birdhouses, banners
				- Multi-Block ceiling things
			- Better buy prompt?
			- Inventory support (cabinets = chests, ovens = furnaces, etc)
			- Database support -> tickable decoration
			- Add mob plushies
			- Add "structure" type
			- Add creative pick block
				-- maybe use titan to listen to when pick block is used clientside, and send relevant info to the server?
				-- Fabric pick blocking mod, for reference: https://github.com/Sjouwer/pick-block-pro
 */

public enum DecorationType {
	// Catalog: Holiday
	//	Fireplaces
	FIREPLACE_DARK_XMAS(Theme.HOLIDAY, new Fireplace("Dark Christmas Fireplace", CustomMaterial.FIREPLACE_DARK_XMAS)),

	FIREPLACE_BROWN_XMAS(Theme.HOLIDAY, new Fireplace("Brown Christmas Fireplace", CustomMaterial.FIREPLACE_BROWN_XMAS)),

	FIREPLACE_LIGHT_XMAS(Theme.HOLIDAY, new Fireplace("Light Christmas Fireplace", CustomMaterial.FIREPLACE_LIGHT_XMAS)),

	CHRISTMAS_TREE_COLOR(Theme.HOLIDAY, new FloorThing("Colorful Christmas Tree", CustomMaterial.CHRISTMAS_TREE_COLORED, Shape._1x2V)),

	CHRISTMAS_TREE_WHITE(Theme.HOLIDAY, new FloorThing("White Christmas Tree", CustomMaterial.CHRISTMAS_TREE_WHITE, Shape._1x2V)),

//	TOY_TRAIN(Theme.HOLIDAY, new FloorThing("Toy Train", CustomMaterial.TOY_TRAIN)), // TODO: Add as part of a Christmas tree structure

	MISTLETOE(Theme.HOLIDAY, new CeilingThing("Mistletoe", CustomMaterial.MISTLETOE)),

	WREATH(Theme.HOLIDAY, new WallThing("Wreath", CustomMaterial.WREATH)),

	STOCKINGS_SINGLE(Theme.HOLIDAY, new WallThing("Single Stocking", CustomMaterial.STOCKINGS_SINGLE)),

	STOCKINGS_DOUBLE(Theme.HOLIDAY, new WallThing("Double Stocking", CustomMaterial.STOCKINGS_DOUBLE)),

	BUNTING_PHRASE_HAPPY_HOLIDAYS(Theme.HOLIDAY, new Bunting("Happy Holidays Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_HOLIDAYS, Shape._1x3H_LIGHT)),

	BUNTING_PHRASE_HAPPY_NEW_YEAR(Theme.HOLIDAY, new Bunting("Happy New Year Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_NEW_YEAR, Shape._1x3H_LIGHT)),

	BUNTING_PHRASE_MERRY_CHRISTMAS(Theme.HOLIDAY, new Bunting("Merry Christmas Bunting", CustomMaterial.BUNTING_PHRASE_MERRY_CHRISTMAS, Shape._1x3H_LIGHT)),

	SNOWMAN_PLAIN(Theme.HOLIDAY, new FloorThing("Plain Snowman", CustomMaterial.SNOWMAN_PLAIN, Shape._1x2V)),

	SNOWMAN_FANCY(Theme.HOLIDAY, new FloorThing("Fancy Snowman", CustomMaterial.SNOWMAN_FANCY, Shape._1x2V)),

	SNOWBALLS_SMALL(Theme.HOLIDAY, new FloorThing("Small Pile of Snowballs", CustomMaterial.SNOWBALLS_SMALL)),

	SNOWBALLS_BIG(Theme.HOLIDAY, new FloorThing("Big Pile of Snowballs", CustomMaterial.SNOWBALLS_BIG)),

	ICICLE_LIGHT_CENTER(Theme.HOLIDAY, new WallThing("Icicle Lights - Center", CustomMaterial.ICICLE_LIGHT_CENTER)),

	ICICLE_LIGHT_LEFT(Theme.HOLIDAY, new WallThing("Icicle Lights - Left", CustomMaterial.ICICLE_LIGHT_LEFT)),

	ICICLE_LIGHT_RIGHT(Theme.HOLIDAY, new WallThing("Icicle Lights - Right", CustomMaterial.ICICLE_LIGHT_RIGHT)),

	ICICLE_SMALL(Theme.HOLIDAY, new CeilingThing("Small Icicle", CustomMaterial.ICICLE_SMALL)),

	ICICLE_LARGE(Theme.HOLIDAY, new CeilingThing("Large Icicle", CustomMaterial.ICICLE_LARGE, Shape._1x1)),

	ICICLE_MULTI(Theme.HOLIDAY, new CeilingThing("Pair of Icicles", CustomMaterial.ICICLE_MULTI, Shape._1x1)),

	GIANT_CANDY_CANE(Theme.HOLIDAY, new DyeableFloorThing("Giant Candy Cane", CustomMaterial.GIANT_CANDY_CANE, ColorableType.DYE, Unique.GIANT_CANDY_CANE)),

	// Catalog: Spooky
	// 	Gravestones
	GRAVESTONE_SMALL(Theme.SPOOKY, new FloorThing("Small Gravestone", CustomMaterial.GRAVESTONE_SMALL)),

	GRAVESTONE_CROSS(Theme.SPOOKY, new FloorThing("Gravestone Cross", CustomMaterial.GRAVESTONE_CROSS, Hitbox.single(Material.IRON_BARS))),

	GRAVESTONE_PLAQUE(Theme.SPOOKY, new FloorThing("Gravestone Plaque", CustomMaterial.GRAVESTONE_PLAQUE)),

	GRAVESTONE_STACK(Theme.SPOOKY, new FloorThing("Rock Stack Gravestone", CustomMaterial.GRAVESTONE_STACK)),

	GRAVESTONE_FLOWERBED(Theme.SPOOKY, new FloorThing("Flowerbed Gravestone", CustomMaterial.GRAVESTONE_FLOWERBED)),

	GRAVESTONE_TALL(Theme.SPOOKY, new FloorThing("Tall Gravestone", CustomMaterial.GRAVESTONE_TALL, List.of(Hitbox.origin(Material.IRON_BARS), Hitbox.offset(Material.IRON_BARS, BlockFace.UP)))),

	// Catalog: General
	// 	Tables
	@Categories({Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x1(new Table("Wooden Table - 1x1", CustomMaterial.TABLE_WOODEN_1X1, Table.TableSize._1x1)),

	@Categories({Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x2(new Table("Wooden Table - 1x2", CustomMaterial.TABLE_WOODEN_1X2, Table.TableSize._1x2)),

	@Categories({Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x3(new Table("Wooden Table - 1x3", CustomMaterial.TABLE_WOODEN_1X3, Table.TableSize._1x3)),

	@Categories({Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x2(new Table("Wooden Table - 2x2", CustomMaterial.TABLE_WOODEN_2X2, Table.TableSize._2x2)),

	@Categories({Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x3(new Table("Wooden Table - 2x3", CustomMaterial.TABLE_WOODEN_2X3, Table.TableSize._2x3)),

	@Categories({Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_3x3(new Table("Wooden Table - 3x3", CustomMaterial.TABLE_WOODEN_3X3, Table.TableSize._3x3)),

	// 	Chairs
	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_BASIC(new Chair("Wooden Chair", CustomMaterial.CHAIR_WOODEN_BASIC, ColorableType.STAIN)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_CUSHIONED(new Chair("Cushioned Wooden Chair", CustomMaterial.CHAIR_WOODEN_CUSHIONED, ColorableType.DYE)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_CLOTH(new Chair("Cloth Chair", CustomMaterial.CHAIR_CLOTH, ColorableType.DYE)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	ADIRONDACK(new Chair("Adirondack", CustomMaterial.ADIRONDACK, ColorableType.STAIN)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_BEACH(new LongChair("Beach Chair", CustomMaterial.BEACH_CHAIR, ColorableType.DYE, Hitbox.light(), .675)),

	@Categories(Tab.MUSIC)
	DRUM_THRONE(new Chair("Drum Throne", CustomMaterial.DRUM_THRONE, ColorableType.DYE, 1.15)),

	// 	Stools
	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_BASIC(new Chair("Wooden Stool", CustomMaterial.STOOL_WOODEN_BASIC, ColorableType.STAIN)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_CUSHIONED(new Chair("Cushioned Wooden Stool", CustomMaterial.STOOL_WOODEN_CUSHIONED, ColorableType.DYE)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_BAR_WOODEN(new Chair("Wooden Bar Stool", CustomMaterial.STOOL_BAR_WOODEN, ColorableType.STAIN, 1.2)),

	// Stumps
	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_OAK(new Stump("Oak Stump", CustomMaterial.STUMP_OAK)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_OAK_ROOTS(new Stump("Rooted Oak Stump", CustomMaterial.STUMP_OAK_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_SPRUCE(new Stump("Spruce Stump", CustomMaterial.STUMP_SPRUCE)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_SPRUCE_ROOTS(new Stump("Rooted Spruce Stump", CustomMaterial.STUMP_SPRUCE_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_BIRCH(new Stump("Birch Stump", CustomMaterial.STUMP_BIRCH)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_BIRCH_ROOTS(new Stump("Rooted Birch Stump", CustomMaterial.STUMP_BIRCH_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_JUNGLE(new Stump("Jungle Stump", CustomMaterial.STUMP_JUNGLE)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_JUNGLE_ROOTS(new Stump("Rooted Jungle Stump", CustomMaterial.STUMP_JUNGLE_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_ACACIA(new Stump("Acacia Stump", CustomMaterial.STUMP_ACACIA)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_ACACIA_ROOTS(new Stump("Rooted Acacia Stump", CustomMaterial.STUMP_ACACIA_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_DARK_OAK(new Stump("Dark Oak Stump", CustomMaterial.STUMP_DARK_OAK)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_DARK_OAK_ROOTS(new Stump("Rooted Dark Oak Stump", CustomMaterial.STUMP_DARK_OAK_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_MANGROVE(new Stump("Mangrove Stump", CustomMaterial.STUMP_MANGROVE)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_MANGROVE_ROOTS(new Stump("Rooted Mangrove Stump", CustomMaterial.STUMP_MANGROVE_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CRIMSON(new Stump("Crimson Stump", CustomMaterial.STUMP_CRIMSON)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CRIMSON_ROOTS(new Stump("Rooted Crimson Stump", CustomMaterial.STUMP_CRIMSON_ROOTS)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_WARPED(new Stump("Warped Stump", CustomMaterial.STUMP_WARPED)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_WARPED_ROOTS(new Stump("Rooted Warped Stump", CustomMaterial.STUMP_WARPED_ROOTS)),

	// 	Benches
	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	BENCH_WOODEN(new Bench("Wooden Bench", CustomMaterial.BENCH_WOODEN, ColorableType.STAIN, Shape._1x2H)),

	@Categories(Tab.MUSIC)
	PIANO_BENCH(new Bench("Piano Bench", CustomMaterial.PIANO_BENCH, ColorableType.STAIN, 0.95, Shape._1x2H)),

	@Categories(Tab.MUSIC)
	PIANO_BENCH_GRAND(new Bench("Grand Piano Bench", CustomMaterial.PIANO_BENCH_GRAND, ColorableType.STAIN, 0.95, Shape._1x3H)),

	// 	Couches
	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_LEFT(new Couch("Cushioned Wooden Couch Left End", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_LEFT, ColorableType.DYE, CouchPart.END)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_RIGHT(new Couch("Cushioned Wooden Couch Left Right", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_RIGHT, ColorableType.DYE, CouchPart.END)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_MIDDLE(new Couch("Cushioned Wooden Couch Middle", CustomMaterial.COUCH_WOODEN_CUSHIONED_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_CORNER(new Couch("Cushioned Wooden Couch Corner", CustomMaterial.COUCH_WOODEN_CUSHIONED_CORNER, ColorableType.DYE, CouchPart.CORNER)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_OTTOMAN(new Couch("Cushioned Wooden Couch Ottoman", CustomMaterial.COUCH_WOODEN_CUSHIONED_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_LEFT(new Couch("Cloth Couch Left End", CustomMaterial.COUCH_CLOTH_END_LEFT, ColorableType.DYE, CouchPart.END)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_RIGHT(new Couch("Cloth Couch Left Right", CustomMaterial.COUCH_CLOTH_END_RIGHT, ColorableType.DYE, CouchPart.END)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_MIDDLE(new Couch("Cloth Couch Middle", CustomMaterial.COUCH_CLOTH_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_CORNER(new Couch("Cloth Couch Corner", CustomMaterial.COUCH_CLOTH_CORNER, ColorableType.DYE, CouchPart.CORNER)),

	@Categories({Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_OTTOMAN(new Couch("Cloth Couch Ottoman", CustomMaterial.COUCH_CLOTH_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),

	// 	Blocks
	TRASH_CAN(new DyeableFloorThing("Trash Can", CustomMaterial.TRASH_CAN, ColorableType.DYE, "C7C7C7", Shape._1x2V)),

	// Custom Workbenches
	@Categories(Tab.INTERNAL)
	TOOL_MODIFICATION_TABLE(new WorkBench("Tool Modification Table", CustomMaterial.TOOL_MODIFICATION_TABLE, Shape._1x2H)),

	@Categories(Tab.INTERNAL)
	DYE_STATION(new WorkBench("Dye Station", CustomMaterial.DYE_STATION)),

	// Noise Makers
	@Categories(Tab.MUSIC)
	DRUM_KIT(new DyeableInstrument("Drum Kit", CustomMaterial.DRUM_KIT, InstrumentSound.DRUM_KIT, ColorableType.DYE, Unique.DRUM_KIT, true, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	PIANO_GRAND(new DyeableInstrument("Grand Piano", CustomMaterial.PIANO_GRAND, InstrumentSound.GRAND_PIANO, ColorableType.STAIN, Unique.PIANO_GRAND, true, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	PIANO_KEYBOARD(new DyeableInstrument("Keyboard", CustomMaterial.PIANO_KEYBOARD, InstrumentSound.PIANO, ColorableType.DYE, Shape._1x2H_LIGHT, true, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	PIANO_KEYBOARD_ON_STAND(new DyeableInstrument("Keyboard On Stand", CustomMaterial.PIANO_KEYBOARD_ON_STAND, InstrumentSound.PIANO, ColorableType.DYE, Shape._1x2H, true, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	HARP(new Instrument("Harp", CustomMaterial.HARP, InstrumentSound.HARP, Shape._1x2V, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	BONGOS(new DyeableInstrument("Bongos", CustomMaterial.BONGOS, InstrumentSound.BONGOS, ColorableType.DYE, Shape._1x2H, true, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	GUITAR_ACOUSTIC(new DyeableInstrument("Acoustic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC, InstrumentSound.TODO, ColorableType.STAIN, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	GUITAR_ACOUSTIC_WALL(new DyeableInstrument("Wall Mounted Acoustic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_WALL, InstrumentSound.TODO, ColorableType.STAIN, Shape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@Categories(Tab.MUSIC)
	GUITAR_ELECTRIC(new DyeableInstrument("Electric Guitar Display", CustomMaterial.GUITAR_ELECTRIC, InstrumentSound.TODO, ColorableType.DYE, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	GUITAR_ELECTRIC_WALL(new DyeableInstrument("Wall Mounted Electric Guitar Display", CustomMaterial.GUITAR_ELECTRIC_WALL, InstrumentSound.TODO, ColorableType.DYE, Shape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@Categories(Tab.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC(new Instrument("Acoustic Classic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_CLASSIC, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC_WALL(new Instrument("Wall Mounted Acoustic Classic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_CLASSIC_WALL, InstrumentSound.TODO, Shape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@Categories(Tab.MUSIC)
	TRUMPET(new Instrument("Trumpet Display", CustomMaterial.TRUMPET, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	SAXOPHONE(new Instrument("Saxophone Display", CustomMaterial.SAXOPHONE, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	VIOLIN(new Instrument("Violin Display", CustomMaterial.VIOLIN, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@Categories(Tab.MUSIC)
	VIOLIN_WALL(new Instrument("Wall Mounted Violin Display", CustomMaterial.VIOLIN_WALL, InstrumentSound.TODO, Shape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@Categories(Tab.MUSIC)
	CELLO(new Instrument("Cello Display", CustomMaterial.CELLO, InstrumentSound.TODO, InstrumentType.FLOOR)),

	// Music
	@Categories(Tab.MUSIC)
	AMPLIFIER(new FloorThing("Amplifier", CustomMaterial.AMPLIFIER, Shape._1x1)),

	@Categories(Tab.MUSIC)
	GOLDEN_RECORD(new WallThing("Golden Record", CustomMaterial.GOLDEN_RECORD)),

	@Categories(Tab.MUSIC)
	SPEAKER_LARGE(new FloorThing("Large Speaker", CustomMaterial.SPEAKER_LARGE, Shape._1x2V)),

	@Categories(Tab.MUSIC)
	SPEAKER_SMALL(new FloorThing("Small Speaker", CustomMaterial.SPEAKER_SMALL, Shape._1x1)),

	@Categories(Tab.MUSIC)
	LAUNCHPAD(new FloorThing("Launchpad", CustomMaterial.LAUNCHPAD)),

	@Categories(Tab.MUSIC)
	MICROPHONE(new FloorThing("Microphone", CustomMaterial.MICROPHONE)),

	@Categories(Tab.MUSIC)
	MICROPHONE_WITH_BOOM_STAND(new FloorThing("Microphone With Boom Stand", CustomMaterial.MICROPHONE_WITH_BOOM_STAND)),

	@Categories(Tab.MUSIC)
	MIXING_CONSOLE(new FloorThing("Mixing Console", CustomMaterial.MIXING_CONSOLE, Shape._1x2H_LIGHT)),

	@Categories(Tab.MUSIC)
	LIGHT_BOARD(new FloorThing("Light Board", CustomMaterial.LIGHT_BOARD, Shape._1x2H_LIGHT)),

	@Categories(Tab.MUSIC)
	SPEAKER_WOODEN_LARGE(new DyeableFloorThing("Large Wooden Speaker", CustomMaterial.SPEAKER_WOODEN_LARGE, ColorableType.STAIN, Shape._1x2V)),

	@Categories(Tab.MUSIC)
	SPEAKER_WOODEN_SMALL(new DyeableFloorThing("Small Wooden Speaker", CustomMaterial.SPEAKER_WOODEN_SMALL, ColorableType.STAIN, Shape._1x1)),

	@Categories(Tab.MUSIC)
	TAPE_MACHINE(new DyeableFloorThing("Tape Machine", CustomMaterial.TAPE_MACHINE, ColorableType.STAIN, Shape._1x1)),

	@Categories(Tab.MUSIC)
	DJ_TURNTABLE(new DyeableFloorThing("DJ Turntable", CustomMaterial.DJ_TURNTABLE, ColorableType.DYE, Shape._1x3H_LIGHT, true)),

	@Categories(Tab.MUSIC)
	RECORD_PLAYER_MODERN(new DyeableFloorThing("Modern Record Player - Off", CustomMaterial.RECORD_PLAYER_MODERN, ColorableType.STAIN, Shape._1x1)),

	@Categories(Tab.MUSIC)
	RECORD_PLAYER_MODERN_ON(new DyeableFloorThing("Modern Record Player - On", CustomMaterial.RECORD_PLAYER_MODERN_ON, ColorableType.STAIN, Shape._1x1)),

	@Categories(Tab.MUSIC)
	STUDIO_LIGHT_HANGING(new CeilingThing("Hanging Studio Lights", CustomMaterial.STUDIO_LIGHTS_HANGING)),

	@Categories(Tab.MUSIC)
	STUDIO_LIGHT_STANDING(new FloorThing("Standing Studio Light", CustomMaterial.STUDIO_LIGHTS_STANDING, Shape._1x2V)),


	// Pride Flags
	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_ACE(new Flag("Asexual Pride Flag", CustomMaterial.FLAG_PRIDE_ACE)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_AGENDER(new Flag("Agender Pride Flag", CustomMaterial.FLAG_PRIDE_AGENDER)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_ARO(new Flag("Aromatic Pride Flag", CustomMaterial.FLAG_PRIDE_ARO)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_BI(new Flag("Bisexual Pride Flag", CustomMaterial.FLAG_PRIDE_BI)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_DEMI(new Flag("Demisexual Pride Flag", CustomMaterial.FLAG_PRIDE_DEMI)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_DEMIBOY(new Flag("Demisexual Boy Pride Flag", CustomMaterial.FLAG_PRIDE_DEMIBOY)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_DEMIGIRL(new Flag("Demisexual Girl Pride Flag", CustomMaterial.FLAG_PRIDE_DEMIGIRL)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_DEMIROMANTIC(new Flag("Demiromantic Pride Flag", CustomMaterial.FLAG_PRIDE_DEMIROMANTIC)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_GAY(new Flag("Gay Pride Flag", CustomMaterial.FLAG_PRIDE_GAY)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_GENDERFLU(new Flag("Genderfluid Pride Flag", CustomMaterial.FLAG_PRIDE_GENDERFLU)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_GENDERFLUX(new Flag("Genderflux Pride Flag", CustomMaterial.FLAG_PRIDE_GENDERFLUX)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_GENQUEER(new Flag("Genderqueer Pride Flag", CustomMaterial.FLAG_PRIDE_GENQUEER)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_GRAYACE(new Flag("Gray-Asexual Pride Flag", CustomMaterial.FLAG_PRIDE_GRAYACE)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_GRAYARO(new Flag("Gray-Aromatic Pride Flag", CustomMaterial.FLAG_PRIDE_GRAYARO)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_INTERSEX(new Flag("Intersex Pride Flag", CustomMaterial.FLAG_PRIDE_INTERSEX)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_LESBIAN(new Flag("Lesbian Pride Flag", CustomMaterial.FLAG_PRIDE_LESBIAN)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_NONBINARY(new Flag("Nonbinary Pride Flag", CustomMaterial.FLAG_PRIDE_NONBINARY)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_PAN(new Flag("Pansexual Pride Flag", CustomMaterial.FLAG_PRIDE_PAN)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_POLYAM(new Flag("Polyamorous Pride Flag", CustomMaterial.FLAG_PRIDE_POLYAM)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_POLYSEX(new Flag("Polysexual Pride Flag", CustomMaterial.FLAG_PRIDE_POLYSEX)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_TRANS(new Flag("Transgender Pride Flag", CustomMaterial.FLAG_PRIDE_TRANS)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_TRANSFEM(new Flag("Transfeminine Pride Flag", CustomMaterial.FLAG_PRIDE_TRANSFEM)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_TRANSMASC(new Flag("Transmasculine Pride Flag", CustomMaterial.FLAG_PRIDE_TRANSMASC)),

	@Categories({Tab.FLAGS, Tab.PRIDE_FLAGS})
	FLAG_PRIDE_QUEER(new Flag("Queer Pride Flag", CustomMaterial.FLAG_PRIDE_QUEER)),

	// Pride Bunting
	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_ACE(new Bunting("Asexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_ACE)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_AGENDER(new Bunting("Agender Pride Bunting", CustomMaterial.BUNTING_PRIDE_AGENDER)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_ARO(new Bunting("Aromatic Pride Bunting", CustomMaterial.BUNTING_PRIDE_ARO)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_BI(new Bunting("Bisexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_BI)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_DEMI(new Bunting("Demisexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMI)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_DEMIBOY(new Bunting("Demisexual Boy Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMIBOY)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_DEMIGIRL(new Bunting("Demisexual Girl Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMIGIRL)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_DEMIROMANTIC(new Bunting("Demiromantic Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMIROMANTIC)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_GAY(new Bunting("Gay Pride Bunting", CustomMaterial.BUNTING_PRIDE_GAY)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_GENDERFLU(new Bunting("Genderfluid Pride Bunting", CustomMaterial.BUNTING_PRIDE_GENDERFLU)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_GENDERFLUX(new Bunting("Genderflux Pride Bunting", CustomMaterial.BUNTING_PRIDE_GENDERFLUX)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_GENQUEER(new Bunting("Genderqueer Pride Bunting", CustomMaterial.BUNTING_PRIDE_GENQUEER)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_GRAYACE(new Bunting("Gray-Asexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_GRAYACE)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_GRAYARO(new Bunting("Gray-Aromatic Pride Bunting", CustomMaterial.BUNTING_PRIDE_GRAYARO)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_INTERSEX(new Bunting("Intersex Pride Bunting", CustomMaterial.BUNTING_PRIDE_INTERSEX)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_LESBIAN(new Bunting("Lesbian Pride Bunting", CustomMaterial.BUNTING_PRIDE_LESBIAN)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_NONBINARY(new Bunting("Nonbinary Pride Bunting", CustomMaterial.BUNTING_PRIDE_NONBINARY)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_PAN(new Bunting("Pansexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_PAN)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_POLYAM(new Bunting("Polyamorous Pride Bunting", CustomMaterial.BUNTING_PRIDE_POLYAM)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_POLYSEX(new Bunting("Polysexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_POLYSEX)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_TRANS(new Bunting("Transgender Pride Bunting", CustomMaterial.BUNTING_PRIDE_TRANS)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_TRANSFEM(new Bunting("Transfeminine Pride Bunting", CustomMaterial.BUNTING_PRIDE_TRANSFEM)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_TRANSMASC(new Bunting("Transmasculine Pride Bunting", CustomMaterial.BUNTING_PRIDE_TRANSMASC)),

	@Categories({Tab.FLAGS, Tab.BUNTING, Tab.PRIDE_BUNTING})
	BUNTING_PRIDE_QUEER(new Bunting("Queer Pride Bunting", CustomMaterial.BUNTING_PRIDE_QUEER)),

	// Bunting
	@Categories({Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1(new Bunting("Server Colors 1 Bunting", CustomMaterial.BUNTING_SERVER_COLORS_1)),

	@Categories({Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2(new Bunting("Server Colors 2 Bunting", CustomMaterial.BUNTING_SERVER_COLORS_2)),

	@Categories({Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1_SMALL(new Bunting("Server Colors 1 Small Bunting", CustomMaterial.BUNTING_SERVER_COLORS_1_SMALL, Shape._1x1_LIGHT)),

	@Categories({Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2_SMALL(new Bunting("Server Colors 2 Small Bunting", CustomMaterial.BUNTING_SERVER_COLORS_2_SMALL, Shape._1x1_LIGHT)),

	@Categories({Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_LOGO(new Bunting("Server Logo Bunting", CustomMaterial.BUNTING_SERVER_LOGO)),


	// Banners
	// 	Hanging
	@Categories({Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_1(new HangingBanner("Avontyre Royal Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_1, Unique.HANGING_BANNER_1x3V)),

	@Categories({Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_2(new HangingBanner("Avontyre Cyan Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_2, Unique.HANGING_BANNER_1x3V)),

	@Categories({Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_3(new HangingBanner("Avontyre Yellow Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_3, Unique.HANGING_BANNER_1x3V)),

	@Categories({Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_4(new HangingBanner("Avontyre Checkered Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_4, Unique.HANGING_BANNER_1x3V)),

	@Categories({Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_SERVER_LOGO(new HangingBanner("Server Logo Hanging Banner", CustomMaterial.BANNER_HANGING_SERVER_LOGO)),

	//	Standing
	@Categories({Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_STANDING})
	BANNER_STANDING_SERVER_LOGO(new StandingBanner("Server Logo Standing Banner", CustomMaterial.BANNER_STANDING_SERVER_LOGO)),


	// 	Fireplaces
	@Categories(Tab.FURNITURE)
	FIREPLACE_DARK(new Fireplace("Dark Fireplace", CustomMaterial.FIREPLACE_DARK)),

	@Categories(Tab.FURNITURE)
	FIREPLACE_BROWN(new Fireplace("Brown Fireplace", CustomMaterial.FIREPLACE_BROWN)),

	@Categories(Tab.FURNITURE)
	FIREPLACE_LIGHT(new Fireplace("Light Fireplace", CustomMaterial.FIREPLACE_LIGHT)),

	//	Windchimes
	@Categories(Tab.WINDCHIMES)
	WINDCHIME_IRON(new WindChime("Iron Windchimes", WindChimeType.IRON)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_GOLD(new WindChime("Gold Windchimes", WindChimeType.GOLD)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_COPPER(new WindChime("Copper Windchimes", WindChimeType.COPPER)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_AMETHYST(new WindChime("Amethyst Windchimes", WindChimeType.AMETHYST)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_LAPIS(new WindChime("Lapis Windchimes", WindChimeType.LAPIS)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_NETHERITE(new WindChime("Netherite Windchimes", WindChimeType.NETHERITE)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_DIAMOND(new WindChime("Diamond Windchimes", WindChimeType.DIAMOND)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_REDSTONE(new WindChime("Redstone Windchimes", WindChimeType.REDSTONE)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_EMERALD(new WindChime("Emerald Windchimes", WindChimeType.EMERALD)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_QUARTZ(new WindChime("Quartz Windchimes", WindChimeType.QUARTZ)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_COAL(new WindChime("Coal Windchimes", WindChimeType.COAL)),

	@Categories(Tab.WINDCHIMES)
	WINDCHIME_ICE(new WindChime("Ice Windchimes", WindChimeType.ICE)),

	// 	Birdhouses
	BIRDHOUSE_FOREST_HORIZONTAL(new BirdHouse("Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL, true)),

	@Categories(Tab.INTERNAL)
	BIRDHOUSE_FOREST_VERTICAL(new BirdHouse("Vertical Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_VERTICAL, false)),

	@Categories(Tab.INTERNAL)
	BIRDHOUSE_FOREST_HANGING(new BirdHouse("Hanging Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HANGING, false)),

	BIRDHOUSE_ENCHANTED_HORIZONTAL(new BirdHouse("Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HORIZONTAL, true)),

	@Categories(Tab.INTERNAL)
	BIRDHOUSE_ENCHANTED_VERTICAL(new BirdHouse("Vertical Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_VERTICAL, false)),

	@Categories(Tab.INTERNAL)
	BIRDHOUSE_ENCHANTED_HANGING(new BirdHouse("Hanging Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HANGING, false)),

	BIRDHOUSE_DEPTHS_HORIZONTAL(new BirdHouse("Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HORIZONTAL, true)),

	@Categories(Tab.INTERNAL)
	BIRDHOUSE_DEPTHS_VERTICAL(new BirdHouse("Vertical Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_VERTICAL, false)),

	@Categories(Tab.INTERNAL)
	BIRDHOUSE_DEPTHS_HANGING(new BirdHouse("Hanging Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HANGING, false)),

	//	Food
	@Categories(Tab.FOOD)
	PIZZA_BOX_SINGLE(new FloorThing("Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE)),

	@Categories(Tab.FOOD)
	PIZZA_BOX_SINGLE_OPENED(new FloorThing("Opened Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE_OPENED)),

	@Categories(Tab.FOOD)
	PIZZA_BOX_STACK(new FloorThing("Pizza Box Stack", CustomMaterial.FOOD_PIZZA_BOX_STACK)),

	@Categories(Tab.FOOD)
	SOUP_MUSHROOM(new FloorThing("Mushroom Soup", CustomMaterial.FOOD_SOUP_MUSHROOM)),

	@Categories(Tab.FOOD)
	SOUP_BEETROOT(new FloorThing("Beetroot Soup", CustomMaterial.FOOD_SOUP_BEETROOT)),

	@Categories(Tab.FOOD)
	SOUP_RABBIT(new FloorThing("Rabbit Soup", CustomMaterial.FOOD_SOUP_RABBIT)),

	@Categories(Tab.FOOD)
	BREAD_LOAF(new FloorThing("Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF)),

	@Categories(Tab.FOOD)
	BREAD_LOAF_CUT(new FloorThing("Cut Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF_CUT)),

	@Categories(Tab.FOOD)
	BROWNIES_CHOCOLATE(new FloorThing("Chocolate Brownies", CustomMaterial.FOOD_BROWNIES_CHOCOLATE)),

	@Categories(Tab.FOOD)
	BROWNIES_VANILLA(new FloorThing("Vanilla Brownies", CustomMaterial.FOOD_BROWNIES_VANILLA)),

	@Categories(Tab.FOOD)
	COOKIES_CHOCOLATE(new FloorThing("Chocolate Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE)),

	@Categories(Tab.FOOD)
	COOKIES_CHOCOLATE_CHIP(new FloorThing("Chocolate Chip Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE_CHIP)),

	@Categories(Tab.FOOD)
	COOKIES_SUGAR(new FloorThing("Sugar Cookies", CustomMaterial.FOOD_COOKIES_SUGAR)),

	@Categories(Tab.FOOD)
	MILK_AND_COOKIES(new FloorThing("Milk and Cookies", CustomMaterial.FOOD_MILK_AND_COOKIES)),

	@Categories(Tab.FOOD)
	MUFFINS_CHOCOLATE(new FloorThing("Chocolate Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE)),

	@Categories(Tab.FOOD)
	MUFFINS_CHOCOLATE_CHIP(new FloorThing("Chocolate Chip Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE_CHIP)),

	@Categories(Tab.FOOD)
	MUFFINS_LEMON(new FloorThing("Lemon Muffins", CustomMaterial.FOOD_MUFFINS_LEMON)),

	@Categories(Tab.FOOD)
	DINNER_HAM(new FloorThing("Ham Dinner", CustomMaterial.FOOD_DINNER_HAM)),

	@Categories(Tab.FOOD)
	DINNER_ROAST(new FloorThing("Roast Dinner", CustomMaterial.FOOD_DINNER_ROAST)),

	@Categories(Tab.FOOD)
	DINNER_TURKEY(new FloorThing("Turkey Dinner", CustomMaterial.FOOD_DINNER_TURKEY)),

	@Categories(Tab.FOOD)
	PUNCHBOWL(new DyeableFloorThing("Dyeable Punchbowl", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE)),

	@Categories(Tab.FOOD)
	PUNCHBOWL_EGGNOG(new DyeableFloorThing("Eggnog", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE, "FFF4BB")),

	@Categories(Tab.FOOD)
	SIDE_SAUCE(new DyeableFloorThing("Dyeable Sauce Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE)),

	@Categories(Tab.FOOD)
	SIDE_SAUCE_CRANBERRIES(new DyeableFloorThing("Cranberries Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE, "C61B1B")),

	@Categories(Tab.FOOD)
	SIDE_GREEN_BEAN_CASSEROLE(new FloorThing("Green Bean Casserole Side", CustomMaterial.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),

	@Categories(Tab.FOOD)
	SIDE_MAC_AND_CHEESE(new FloorThing("Mac N' Cheese Side", CustomMaterial.FOOD_SIDE_MAC_AND_CHEESE)),

	@Categories(Tab.FOOD)
	SIDE_SWEET_POTATOES(new FloorThing("Sweet Potatoes Side", CustomMaterial.FOOD_SIDE_SWEET_POTATOES)),

	@Categories(Tab.FOOD)
	SIDE_MASHED_POTATOES(new FloorThing("Mashed Potatoes Side", CustomMaterial.FOOD_SIDE_MASHED_POTATOES)),

	@Categories(Tab.FOOD)
	SIDE_ROLLS(new FloorThing("Rolls", CustomMaterial.FOOD_SIDE_ROLLS)),

	@Categories(Tab.FOOD)
	CAKE_BATTER(new DyeableFloorThing("Dyeable Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE)),

	@Categories(Tab.FOOD)
	CAKE_BATTER_RED_VELVET(new DyeableFloorThing("Red Velvet Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "720606")),

	@Categories(Tab.FOOD)
	CAKE_BATTER_VANILLA(new DyeableFloorThing("Vanilla Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "FFF9CC")),

	@Categories(Tab.FOOD)
	CAKE_BATTER_CHOCOLATE(new DyeableFloorThing("Chocolate Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "492804")),

	@Categories(Tab.FOOD)
	CAKE_WHITE_CHOCOLATE(new FloorThing("White Chocolate Cake", CustomMaterial.FOOD_CAKE_WHITE_CHOCOLATE)),

	@Categories(Tab.FOOD)
	CAKE_BUNDT(new FloorThing("Bundt Cake", CustomMaterial.FOOD_CAKE_BUNDT)),

	@Categories(Tab.FOOD)
	CAKE_CHOCOLATE_DRIP(new FloorThing("Chocolate Drip Cake", CustomMaterial.FOOD_CAKE_CHOCOLATE_DRIP)),

	@Categories(Tab.FOOD)
	PIE_ROUGH(new DyeableFloorThing("Dyeable Rough Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE)),

	@Categories(Tab.FOOD)
	PIE_ROUGH_PECAN(new DyeableFloorThing("Pecan Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE, "4E3004")),

	@Categories(Tab.FOOD)
	PIE_SMOOTH(new DyeableFloorThing("Dyeable Smooth Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE)),

	@Categories(Tab.FOOD)
	PIE_SMOOTH_CHOCOLATE(new DyeableFloorThing("Chocolate Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "734008")),

	@Categories(Tab.FOOD)
	PIE_SMOOTH_LEMON(new DyeableFloorThing("Lemon Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "FFE050")),

	@Categories(Tab.FOOD)
	PIE_SMOOTH_PUMPKIN(new DyeableFloorThing("Pumpkin Pie Decoration", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "BF7D18")),

	@Categories(Tab.FOOD)
	PIE_LATTICED(new DyeableFloorThing("Dyeable Latticed Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE)),

	@Categories(Tab.FOOD)
	PIE_LATTICED_APPLE(new DyeableFloorThing("Apple Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "FDC330")),

	@Categories(Tab.FOOD)
	PIE_LATTICED_BLUEBERRY(new DyeableFloorThing("Blueberry Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "4E1892")),

	@Categories(Tab.FOOD)
	PIE_LATTICED_CHERRY(new DyeableFloorThing("Cherry Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "B60C0C")),

	//	Kitchenware
	@Categories(Tab.KITCHENWARE)
	WINE_BOTTLE(new FloorThing("Wine Bottle", CustomMaterial.KITCHENWARE_WINE_BOTTLE)),

	@Categories(Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP(new FloorThing("Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP)),

	@Categories(Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_RANDOM(new FloorThing("Random Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM)),

	@Categories(Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_SIDE(new FloorThing("Wine Bottles on Side", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_SIDE)),

	@Categories(Tab.KITCHENWARE)
	WINE_GLASS(new FloorThing("Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS)),

	@Categories(Tab.KITCHENWARE)
	WINE_GLASS_FULL(new FloorThing("Full Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS_FULL)),

	@Categories(Tab.KITCHENWARE)
	MUG_GLASS(new FloorThing("Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS)),

	@Categories(Tab.KITCHENWARE)
	MUG_GLASS_FULL(new FloorThing("Full Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS_FULL)),

	@Categories(Tab.KITCHENWARE)
	MUG_WOODEN(new FloorThing("Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN)),

	@Categories(Tab.KITCHENWARE)
	MUG_WOODEN_FULL(new FloorThing("Full Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN_FULL)),

	@Categories(Tab.KITCHENWARE)
	GLASSWARE_GROUP_1(new FloorThing("Random Glassware 1", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_1)),

	@Categories(Tab.KITCHENWARE)
	GLASSWARE_GROUP_2(new FloorThing("Random Glassware 2", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_2)),

	@Categories(Tab.KITCHENWARE)
	GLASSWARE_GROUP_3(new FloorThing("Random Glassware 3", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_3)),

	@Categories(Tab.KITCHENWARE)
	JAR(new FloorThing("Jar", CustomMaterial.KITCHENWARE_JAR)),

	@Categories(Tab.KITCHENWARE)
	JAR_HONEY(new FloorThing("Honey Jar", CustomMaterial.KITCHENWARE_JAR_HONEY)),

	@Categories(Tab.KITCHENWARE)
	JAR_COOKIES(new FloorThing("Cookie Jar", CustomMaterial.KITCHENWARE_JAR_COOKIES)),

	@Categories(Tab.KITCHENWARE)
	JAR_WIDE(new FloorThing("Wide Jar", CustomMaterial.KITCHENWARE_JAR_WIDE)),

	@Categories(Tab.KITCHENWARE)
	BOWL(new FloorThing("Wooden Bowl", CustomMaterial.KITCHENWARE_BOWL)),

	@Categories(Tab.KITCHENWARE)
	MIXING_BOWL(new FloorThing("Mixing Bowl", CustomMaterial.KITCHENWARE_MIXING_BOWL)),

	@Categories(Tab.KITCHENWARE)
	PAN_CAKE(new FloorThing("Cake Pan", CustomMaterial.KITCHENWARE_PAN_CAKE)),

	@Categories(Tab.KITCHENWARE)
	PAN_CASSEROLE(new FloorThing("Casserole Pan", CustomMaterial.KITCHENWARE_PAN_CASSEROLE)),

	@Categories(Tab.KITCHENWARE)
	PAN_COOKIE(new FloorThing("Cookie Pan", CustomMaterial.KITCHENWARE_PAN_COOKIE)),

	@Categories(Tab.KITCHENWARE)
	PAN_MUFFIN(new FloorThing("Muffin Pan", CustomMaterial.KITCHENWARE_PAN_MUFFIN)),

	@Categories(Tab.KITCHENWARE)
	PAN_PIE(new FloorThing("Pie Pan", CustomMaterial.KITCHENWARE_PAN_PIE)),

	// 	Appliances
	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE(new Fridge("Fridge", CustomMaterial.APPLIANCE_FRIDGE, FridgeSize.STANDARD)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MAGNETS(new Fridge("Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MAGNETS, FridgeSize.STANDARD)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL(new Fridge("Tall Fridge", CustomMaterial.APPLIANCE_FRIDGE_TALL, FridgeSize.TALL)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL_MAGNETS(new Fridge("Tall Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_TALL_MAGNETS, FridgeSize.TALL)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI(new Fridge("Mini Fridge", CustomMaterial.APPLIANCE_FRIDGE_MINI, FridgeSize.MINI)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI_MAGNETS(new Fridge("Mini Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MINI_MAGNETS, FridgeSize.MINI)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_SLUSHIE_MACHINE(new DyeableFloorThing("Slushie Machine", CustomMaterial.APPLIANCE_SLUSHIE_MACHINE, ColorableType.DYE, Shape._1x1)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_GRILL_COMMERCIAL(new Block("Commercial Grill", CustomMaterial.APPLIANCE_GRILL_COMMERCIAL, RotationType.BOTH)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_OVEN_COMMERCIAL(new Block("Commercial Oven", CustomMaterial.APPLIANCE_OVEN_COMMERCIAL, RotationType.BOTH)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_DEEP_FRYER_COMMERCIAL(new Block("Commercial Deep Fryer", CustomMaterial.APPLIANCE_DEEP_FRYER_COMMERCIAL, RotationType.BOTH)),

	// Counters - STEEL HANDLES
	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_ISLAND, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_CORNER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_DRAWER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_CABINET, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_OVEN, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_SINK(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_SINK, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_BAR(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_BAR, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_ISLAND, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_CORNER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_DRAWER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_CABINET, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_OVEN, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_SINK(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_SINK, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_BAR(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_BAR, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_STONE_ISLAND, HandleType.STEEL, CounterMaterial.STONE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_STONE_CORNER, HandleType.STEEL, CounterMaterial.STONE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_STONE_DRAWER, HandleType.STEEL, CounterMaterial.STONE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_STONE_CABINET, HandleType.STEEL, CounterMaterial.STONE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_STONE_OVEN, HandleType.STEEL, CounterMaterial.STONE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_SINK(new Counter(CustomMaterial.COUNTER_STEEL_STONE_SINK, HandleType.STEEL, CounterMaterial.STONE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_BAR(new Counter(CustomMaterial.COUNTER_STEEL_STONE_BAR, HandleType.STEEL, CounterMaterial.STONE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_ISLAND, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_CORNER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_DRAWER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_CABINET, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_OVEN, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_SINK(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_SINK, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_BAR(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_BAR, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - BRASS HANDLES
	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_ISLAND, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_CORNER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_DRAWER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_CABINET, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_OVEN, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_SINK(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_SINK, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_BAR(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_BAR, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_ISLAND, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_CORNER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_DRAWER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_CABINET, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_OVEN, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_SINK(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_SINK, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_BAR(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_BAR, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_STONE_ISLAND, HandleType.BRASS, CounterMaterial.STONE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_STONE_CORNER, HandleType.BRASS, CounterMaterial.STONE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_STONE_DRAWER, HandleType.BRASS, CounterMaterial.STONE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_STONE_CABINET, HandleType.BRASS, CounterMaterial.STONE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_STONE_OVEN, HandleType.BRASS, CounterMaterial.STONE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_SINK(new Counter(CustomMaterial.COUNTER_BRASS_STONE_SINK, HandleType.BRASS, CounterMaterial.STONE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_BAR(new Counter(CustomMaterial.COUNTER_BRASS_STONE_BAR, HandleType.BRASS, CounterMaterial.STONE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_ISLAND, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_CORNER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_DRAWER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_CABINET, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_OVEN, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_SINK(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_SINK, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_BAR(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_BAR, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - BLACK HANDLES
	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_ISLAND, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_CORNER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_DRAWER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_CABINET, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_OVEN, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_SINK(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_SINK, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_BAR(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_BAR, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_ISLAND, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CORNER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_DRAWER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_OVEN, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_SINK(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_SINK, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_BAR(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_BAR, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_STONE_ISLAND, HandleType.BLACK, CounterMaterial.STONE, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_STONE_CORNER, HandleType.BLACK, CounterMaterial.STONE, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_STONE_DRAWER, HandleType.BLACK, CounterMaterial.STONE, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_STONE_CABINET, HandleType.BLACK, CounterMaterial.STONE, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_STONE_OVEN, HandleType.BLACK, CounterMaterial.STONE, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_SINK(new Counter(CustomMaterial.COUNTER_BLACK_STONE_SINK, HandleType.BLACK, CounterMaterial.STONE, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_BAR(new Counter(CustomMaterial.COUNTER_BLACK_STONE_BAR, HandleType.BLACK, CounterMaterial.STONE, CounterType.BAR)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_ISLAND, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_CORNER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_DRAWER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_CABINET, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_OVEN, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.OVEN)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_SINK(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_SINK, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.SINK)),

	@Categories({Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_BAR(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_BAR, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.BAR)),

	// Cabinets - STEEL HANDLES
	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN(new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN_CORNER(new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN_HOOD(new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.HOOD)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN_SHORT(new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.SHORT)),

	// Cabinets - BRASS HANDLES
	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN(new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN_CORNER(new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN_HOOD(new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.HOOD)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN_SHORT(new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.SHORT)),

	// Cabinets - BLACK HANDLES
	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN(new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.CABINET)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN_CORNER(new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.CORNER)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN_HOOD(new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.HOOD)),

	@Categories({Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN_SHORT(new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.SHORT)),

	// Cabinets - GENERIC
	@Categories({Tab.FURNITURE, Tab.CABINETS})
	CABINET_HOOD(new Cabinet(CustomMaterial.CABINET_HOOD, CabinetMaterial.NONE, HandleType.NONE, CabinetType.HOOD)),

	@Categories({Tab.FURNITURE, Tab.APPLIANCES})
	TOILET_MODERN(new DyeableFloorThing("Toilet Modern", CustomMaterial.TOILET_MODERN, ColorableType.DYE, "FFFFFF", Shape._1x1)),

	@Categories(Tab.FURNITURE)
	WARDROBE(new Furniture("Wardrobe", CustomMaterial.WARDROBE, FurnitureSurface.FLOOR, Shape._2x3V)),

	@Categories(Tab.FURNITURE)
	CUPBOARD_SHORT(new Furniture("Short Cupboard", CustomMaterial.CUPBOARD_SHORT, FurnitureSurface.FLOOR, Shape._1x2H)),

	@Categories(Tab.FURNITURE)
	CUPBOARD_SHORT_BOOKSHELF(new Furniture("Short Bookshelf Cupboard", CustomMaterial.CUPBOARD_SHORT_BOOKSHELF, FurnitureSurface.FLOOR, Shape._1x2H)),

	@Categories(Tab.FURNITURE)
	SHELF_WALL(new Shelf("Wall Shelf", CustomMaterial.SHELF_WALL, ColorableType.STAIN, Shape._1x2H)),

	@Categories(Tab.FURNITURE)
	SHELF_STORAGE(new Furniture("Storage Shelf", CustomMaterial.SHELF_STORAGE, FurnitureSurface.FLOOR, Shape._2x3V, true)),

	//	Art
	@Categories(Tab.ART)
	ART_PAINTING_CHERRY_FOREST(new Art("Cherry Forest Painting", CustomMaterial.ART_PAINTING_CHERRY_FOREST, ArtSize._1x2v)),

	@Categories(Tab.ART)
	ART_PAINTING_END_ISLAND(new Art("End Island Painting", CustomMaterial.ART_PAINTING_END_ISLAND, ArtSize._1x2v)),

	@Categories(Tab.ART)
	ART_PAINTING_LOST_ENDERMAN(new Art("Lost Enderman Painting", CustomMaterial.ART_PAINTING_LOST_ENDERMAN, ArtSize._1x2v)),

	@Categories(Tab.ART)
	ART_PAINTING_PINE_TREE(new Art("Pine Tree Painting", CustomMaterial.ART_PAINTING_PINE_TREE, ArtSize._1x2v)),

	@Categories(Tab.ART)
	ART_PAINTING_SUNSET(new Art("Sunset Painting", CustomMaterial.ART_PAINTING_SUNSET, ArtSize._1x2v)),

	@Categories(Tab.ART)
	ART_PAINTING_SWAMP_HUT(new Art("Swamp Hut Painting", CustomMaterial.ART_PAINTING_SWAMP_HUT, ArtSize._1x2v)),

	@Categories(Tab.ART)
	ART_PAINTING_MOUNTAINS(new Art("Mountains Painting", CustomMaterial.ART_PAINTING_MOUNTAINS, ArtSize._1x2h)),

	@Categories(Tab.ART)
	ART_PAINTING_MUDDY_PIG(new Art("Muddy Pig Painting", CustomMaterial.ART_PAINTING_MUDDY_PIG, ArtSize._1x2h)),

	@Categories(Tab.ART)
	ART_PAINTING_PURPLE_SHEEP(new Art("Purple Sheep Painting", CustomMaterial.ART_PAINTING_PURPLE_SHEEP, ArtSize._1x2h)),

	@Categories(Tab.ART)
	ART_PAINTING_VILLAGE_HAPPY(new Art("Happy Village Painting", CustomMaterial.ART_PAINTING_VILLAGE_HAPPY, ArtSize._1x2h)),

	@Categories(Tab.ART)
	ART_PAINTING_VILLAGE_CHAOS(new Art("Chaos Village Painting", CustomMaterial.ART_PAINTING_VILLAGE_CHAOS, ArtSize._1x2h)),

	@Categories(Tab.ART)
	ART_PAINTING_SKYBLOCK(new Art("Skyblock Painting", CustomMaterial.ART_PAINTING_SKYBLOCK, ArtSize._1x1)),

	@Categories(Tab.ART)
	ART_PAINTING_NETHER_FORTRESS_BRIDGE(new Art("Nether Fortress Bridge Painting", CustomMaterial.ART_PAINTING_NETHER_FORTRESS_BRIDGE, ArtSize._1x1)),

	@Categories(Tab.ART)
	ART_PAINTING_NETHER_CRIMSON_FOREST(new Art("Nether Crimson Forest Painting", CustomMaterial.ART_PAINTING_NETHER_CRIMSON_FOREST, ArtSize._1x1)),

	@Categories(Tab.ART)
	ART_PAINTING_NETHER_WARPED_FOREST(new Art("Nether Warped Forest Painting", CustomMaterial.ART_PAINTING_NETHER_WARPED_FOREST, ArtSize._1x1)),

	@Categories(Tab.ART)
	ART_PAINTING_NETHER_BASALT_DELTAS(new Art("Nether Basalt Deltas Painting", CustomMaterial.ART_PAINTING_NETHER_BASALT_DELTAS, ArtSize._1x1)),

	@Categories(Tab.ART)
	ART_PAINTING_NETHER_SOUL_SAND_VALLEY(new Art("Nether Soul Sand Valley Painting", CustomMaterial.ART_PAINTING_NETHER_SOUL_SAND_VALLEY, ArtSize._1x1)),

	@Categories(Tab.ART)
	ART_PAINTING_CASTLE(new Art("Castle Painting", CustomMaterial.ART_PAINTING_CASTLE, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_LAKE(new Art("Lake Painting", CustomMaterial.ART_PAINTING_LAKE, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_RIVER(new Art("River Painting", CustomMaterial.ART_PAINTING_RIVER, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_ROAD(new Art("Road Painting", CustomMaterial.ART_PAINTING_ROAD, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_ORIENTAL(new Art("Oriental Painting", CustomMaterial.ART_PAINTING_ORIENTAL, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_CHICKENS(new Art("Chickens Painting", CustomMaterial.ART_PAINTING_CHICKENS, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_OAK_TREE(new Art("Oak Tree Painting", CustomMaterial.ART_PAINTING_OAK_TREE, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_CRAB(new Art("Crab Painting", CustomMaterial.ART_PAINTING_CRAB, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_SATURN_ROCKET(new Art("Saturn Rocket Painting", CustomMaterial.ART_PAINTING_SATURN_ROCKET, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_PARROT(new Art("Oak Tree Painting", CustomMaterial.ART_PAINTING_PARROT, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_DUCKS(new Art("Ducks Painting", CustomMaterial.ART_PAINTING_DUCKS, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_STARRY_PINE_TREE(new Art("Starry Pine Tree Painting", CustomMaterial.ART_PAINTING_STARRY_PINE_TREE, ArtSize._2x2)),

	@Categories(Tab.ART)
	ART_PAINTING_FOREST(new Art("Forest Painting", CustomMaterial.ART_PAINTING_FOREST, ArtSize._1x3h)),

	@Categories(Tab.ART)
	ART_PAINTING_SAND_DUNES(new Art("Sand Dunes Painting", CustomMaterial.ART_PAINTING_SAND_DUNES, ArtSize._1x3v)),

	@Categories(Tab.ART)
	ART_PAINTING_STORY(new Art("Story Painting", CustomMaterial.ART_PAINTING_STORY, ArtSize._2x3h)),

	//	Potions
	@Categories(Tab.POTIONS)
	POTION_FILLED_TINY_1(new DyeableFloorThing("Tiny Potions 1", CustomMaterial.POTION_FILLED_TINY_1, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_TINY_2(new DyeableFloorThing("Tiny Potions 2", CustomMaterial.POTION_FILLED_TINY_2, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_SMALL_1(new DyeableFloorThing("Small Potion 1", CustomMaterial.POTION_FILLED_SMALL_1, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_SMALL_2(new DyeableFloorThing("Small Potion 2", CustomMaterial.POTION_FILLED_SMALL_2, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_SMALL_3(new DyeableFloorThing("Small Potion 3", CustomMaterial.POTION_FILLED_SMALL_3, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_MEDIUM_1(new DyeableFloorThing("Medium Potion 1", CustomMaterial.POTION_FILLED_MEDIUM_1, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_MEDIUM_2(new DyeableFloorThing("Medium Potion 2", CustomMaterial.POTION_FILLED_MEDIUM_2, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_WIDE(new DyeableFloorThing("Wide Potion", CustomMaterial.POTION_FILLED_WIDE, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_SKINNY(new DyeableFloorThing("Skinny Potion", CustomMaterial.POTION_FILLED_SKINNY, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_TALL(new DyeableFloorThing("Tall Potion", CustomMaterial.POTION_FILLED_TALL, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_BIG_BOTTLE(new DyeableFloorThing("Big Potion Bottle", CustomMaterial.POTION_FILLED_BIG_BOTTLE, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_BIG_TEAR(new DyeableFloorThing("Big Potion Tear", CustomMaterial.POTION_FILLED_BIG_TEAR, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_BIG_DONUT(new DyeableFloorThing("Big Potion Donut", CustomMaterial.POTION_FILLED_BIG_DONUT, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_BIG_SKULL(new DyeableFloorThing("Big Potion Skull", CustomMaterial.POTION_FILLED_BIG_SKULL, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_SMALL(new DyeableFloorThing("Small Potions", CustomMaterial.POTION_FILLED_GROUP_SMALL, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_MEDIUM(new DyeableFloorThing("Medium Potions", CustomMaterial.POTION_FILLED_GROUP_MEDIUM, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_TALL(new DyeableFloorThing("Tall Potions", CustomMaterial.POTION_FILLED_GROUP_TALL, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_1(new DyeableFloorThing("Random Potions 1", CustomMaterial.POTION_FILLED_GROUP_RANDOM_1, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_2(new DyeableFloorThing("Random Potions 2", CustomMaterial.POTION_FILLED_GROUP_RANDOM_2, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_3(new DyeableFloorThing("Random Potions 3", CustomMaterial.POTION_FILLED_GROUP_RANDOM_3, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_4(new DyeableFloorThing("Random Potions 4", CustomMaterial.POTION_FILLED_GROUP_RANDOM_4, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_5(new DyeableFloorThing("Random Potions 5", CustomMaterial.POTION_FILLED_GROUP_RANDOM_5, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_6(new DyeableFloorThing("Random Potions 6", CustomMaterial.POTION_FILLED_GROUP_RANDOM_6, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_7(new DyeableFloorThing("Random Potions 7", CustomMaterial.POTION_FILLED_GROUP_RANDOM_7, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_8(new DyeableFloorThing("Random Potions 8", CustomMaterial.POTION_FILLED_GROUP_RANDOM_8, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_SMALL_1(new DyeableFloorThing("Empty Small Potion 1", CustomMaterial.POTION_EMPTY_SMALL_1, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_SMALL_2(new DyeableFloorThing("Empty Small Potion 2", CustomMaterial.POTION_EMPTY_SMALL_2, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_SMALL_3(new DyeableFloorThing("Empty Small Potion 3", CustomMaterial.POTION_EMPTY_SMALL_3, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_MEDIUM_1(new DyeableFloorThing("Empty Medium Potion 1", CustomMaterial.POTION_EMPTY_MEDIUM_1, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_MEDIUM_2(new DyeableFloorThing("Empty Medium Potion 2", CustomMaterial.POTION_EMPTY_MEDIUM_2, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_WIDE(new DyeableFloorThing("Empty Wide Potion", CustomMaterial.POTION_EMPTY_WIDE, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_SKINNY(new DyeableFloorThing("Empty Skinny Potion", CustomMaterial.POTION_EMPTY_SKINNY, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_TALL(new DyeableFloorThing("Empty Tall Potion", CustomMaterial.POTION_EMPTY_TALL, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_BIG_BOTTLE(new DyeableFloorThing("Empty Big Potion Bottle", CustomMaterial.POTION_EMPTY_BIG_BOTTLE, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_BIG_TEAR(new DyeableFloorThing("Empty Big Potion Tear", CustomMaterial.POTION_EMPTY_BIG_TEAR, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_BIG_DONUT(new DyeableFloorThing("Empty Big Potion Donut", CustomMaterial.POTION_EMPTY_BIG_DONUT, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_BIG_SKULL(new DyeableFloorThing("Empty Big Potion Skull", CustomMaterial.POTION_EMPTY_BIG_SKULL, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_GROUP_SMALL(new DyeableFloorThing("Empty Small Potions", CustomMaterial.POTION_EMPTY_GROUP_SMALL, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_GROUP_MEDIUM(new DyeableFloorThing("Empty Medium Potions", CustomMaterial.POTION_EMPTY_GROUP_MEDIUM, ColorableType.DYE)),

	@Categories(Tab.POTIONS)
	POTION_EMPTY_GROUP_TALL(new DyeableFloorThing("Empty Tall Potions", CustomMaterial.POTION_EMPTY_GROUP_TALL, ColorableType.DYE)),

	// 	Balloons
	BALLOON_SHORT(new DyeableFloorThing("Balloon Short", CustomMaterial.BALLOON_SHORT, ColorableType.DYE)),

	BALLOON_MEDIUM(new DyeableFloorThing("Balloon Medium", CustomMaterial.BALLOON_MEDIUM, ColorableType.DYE)),

	BALLOON_TALL(new DyeableFloorThing("Balloon Tall ", CustomMaterial.BALLOON_TALL, ColorableType.DYE)),

	//	Misc
	INKWELL(new FloorThing("Inkwell", CustomMaterial.INKWELL)),

	WHEEL_SMALL(new DecorationConfig("Small Wheel", CustomMaterial.WHEEL_SMALL)),

	TELESCOPE(new FloorThing("Telescope", CustomMaterial.TELESCOPE)),

	MICROSCOPE(new FloorThing("Microscope", CustomMaterial.MICROSCOPE)),

	MICROSCOPE_WITH_GEM(new FloorThing("Microscope With Gem", CustomMaterial.MICROSCOPE_WITH_GEM)),

	HELM(new DecorationConfig("Helm", CustomMaterial.HELM)),

	TRAFFIC_BLOCKADE(new FloorThing("Traffic Blockade", CustomMaterial.TRAFFIC_BLOCKADE, Shape._1x1)),

	TRAFFIC_BLOCKADE_LIGHTS(new FloorThing("Traffic Blockade with Lights", CustomMaterial.TRAFFIC_BLOCKADE_LIGHTS, Shape._1x1)),

	TRAFFIC_CONE(new FloorThing("Traffic Cone", CustomMaterial.TRAFFIC_CONE, Shape._1x1)),

	POSTBOX(new FloorThing("Postbox", CustomMaterial.POSTBOX, Shape._1x2V)),

	MAILBOX(new DyeableFloorThing("Mailbox", CustomMaterial.MAILBOX, ColorableType.DYE, "C7C7C7", Shape._1x2V)),

	SANDWICH_SIGN(new FloorThing("Sandwich Sign", CustomMaterial.SANDWICH_SIGN)),

	SANDWICH_SIGN_TALL(new FloorThing("Sandwich Sign Tall", CustomMaterial.SANDWICH_SIGN_TALL)),

	FIRE_HYDRANT(new FloorThing("Fire Hydrant", CustomMaterial.FIRE_HYDRANT)),

	WAYSTONE(new FloorThing("Waystone", CustomMaterial.WAYSTONE)),

	WAYSTONE_ACTIVATED(new FloorThing("Waystone Activated", CustomMaterial.WAYSTONE_ACTIVATED)),

	ROTARY_PHONE(new FloorThing("Rotary Phone", CustomMaterial.ROTARY_PHONE)),

	LAPTOP(new FloorThing("Rotary Phone", CustomMaterial.LAPTOP)),

	ROUTER(new FloorThing("Rotary Phone", CustomMaterial.ROUTER)),

	REGISTER_MODERN(new FloorThing("Rotary Phone", CustomMaterial.REGISTER_MODERN)),

	// Testing
	@Categories(Tab.INTERNAL)
	TEST(new TestThing("Test Thing", CustomMaterial.WAYSTONE_ACTIVATED)),
	;

	@Getter
	private final Theme theme;
	@Getter
	private final DecorationConfig config;

	DecorationType(DecorationConfig config) {
		this.config = config;
		this.theme = Theme.GENERAL;
	}

	DecorationType(Theme theme, DecorationConfig config) {
		this.config = config;
		this.theme = theme;
	}

	public static void init() {}

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
		for (DecorationType type : values()) {
			Tab lastTab = Tab.INTERNAL_ROOT;
			Categories annotation = type.getClass().getField(type.name()).getAnnotation(Categories.class);
			if (annotation != null) {
				Tab[] tabs = annotation.value();
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


		for (DecorationType type : values()) {
			Tab tab = Tab.INTERNAL_ROOT;
			List<Tab> subTabs = newSubTabMap.getOrDefault(tab, new ArrayList<>());

			Categories annotation = type.getClass().getField(type.name()).getAnnotation(Categories.class);

			if (annotation != null) {
				List<Tab> tabs = new ArrayList<>(List.of(annotation.value()));

				tab = tabs.remove(0);
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

			Categories annotation = type.getClass().getField(type.name()).getAnnotation(Categories.class);
			if (annotation == null) {
				root.addDecorationType(type);
				continue;
			}

			Tab[] tabs = annotation.value();
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
	@NoArgsConstructor
	public static class CategoryTree {
		@NonNull Tab tabParent;
		List<DecorationType> decorationTypes = new ArrayList<>();
		List<CategoryTree> tabChildren = new ArrayList<>();

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
