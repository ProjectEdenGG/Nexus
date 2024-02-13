package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Tab;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable.ColorableType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Basic;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.FloorShape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Unique;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.WallShape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
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
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.AdditionType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.TestThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.TrashCan;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.WorkBench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.Block;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.CeilingThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.Shelf;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.models.trophy.TrophyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	TODO:
		- Bugs:
			- Cannot close a trapdoor that is underneath a painting only when clicking on the TOP of the trapdoor
				- crouching bypasses the check that is cancelling the interact
			- Cabinet Hood is "Stain: Oak"
*/

/*
	TODO AFTER RELEASE:
		- Add:
			- Remaining decorations:
					- toAdd
					- Mailbox -> texture
					- Red lawn chair -> texture
					- Dog House
					- Hoots:
						- Construction Cones -> color FF7F00
						- Trash Bag
						- Boxes
			- Add some Tickable Decorations
			- Hot Swap Kitchen Handles -> Sell handles at general store/carpenter?
			- Allow player to create their own presets in DyeStationMenu
			- Better support for:
				- Multi-Surface models -> birdhouses, banners
				- Multi-Block ceiling things
			- Inventory support (cabinets = chests, ovens = furnaces, etc)
			- Mob plushies
			- "Structure" type
			- Creative pick block
				- maybe use titan to listen to when pick block is used clientside, and send relevant info to the server?
				- Fabric pick blocking mod, for reference: https://github.com/Sjouwer/pick-block-pro
		- Ideas:
			- Redstone activate instrument?
			- Better buy prompt?
 */

// @formatter:off
@AllArgsConstructor
public enum DecorationType {
// Catalog: Holiday
	@TypeConfig(price = 550, theme = Theme.HOLIDAY)
	FIREPLACE_DARK_XMAS(new Fireplace("Dark Christmas Fireplace", CustomMaterial.FIREPLACE_DARK_XMAS)),

	@TypeConfig(price = 550, theme = Theme.HOLIDAY)
	FIREPLACE_BROWN_XMAS(new Fireplace("Brown Christmas Fireplace", CustomMaterial.FIREPLACE_BROWN_XMAS)),

	@TypeConfig(price = 550, theme = Theme.HOLIDAY)
	FIREPLACE_LIGHT_XMAS(new Fireplace("Light Christmas Fireplace", CustomMaterial.FIREPLACE_LIGHT_XMAS)),

	@TypeConfig(price = 150, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_COLOR(new FloorThing("Colorful Christmas Tree", CustomMaterial.CHRISTMAS_TREE_COLORED, FloorShape._1x2V)),

	@TypeConfig(price = 150, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_WHITE(new FloorThing("White Christmas Tree", CustomMaterial.CHRISTMAS_TREE_WHITE, FloorShape._1x2V)),

	//@TypeConfig(price = 4.20, theme = Theme.HOLIDAY)
	//TOY_TRAIN(new FloorThing("Toy Train", CustomMaterial.TOY_TRAIN)), // TODO: Add as part of a Christmas tree structure

	@TypeConfig(price = 45, theme = Theme.HOLIDAY)
	MISTLETOE(new CeilingThing("Mistletoe", CustomMaterial.MISTLETOE)),

	@TypeConfig(price = 75, theme = Theme.HOLIDAY)
	WREATH(new WallThing("Wreath", CustomMaterial.WREATH)),

	@TypeConfig(price = 30, theme = Theme.HOLIDAY)
	STOCKINGS_SINGLE(new WallThing("Single Stocking", CustomMaterial.STOCKINGS_SINGLE)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	STOCKINGS_DOUBLE(new WallThing("Double Stocking", CustomMaterial.STOCKINGS_DOUBLE)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_HAPPY_HOLIDAYS(new Bunting("Happy Holidays Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_HOLIDAYS, FloorShape._1x3H_LIGHT)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_HAPPY_NEW_YEAR(new Bunting("Happy New Year Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_NEW_YEAR, FloorShape._1x3H_LIGHT)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_MERRY_CHRISTMAS(new Bunting("Merry Christmas Bunting", CustomMaterial.BUNTING_PHRASE_MERRY_CHRISTMAS, FloorShape._1x3H_LIGHT)),

	@TypeConfig(price = 300, theme = Theme.HOLIDAY)
	SNOWMAN_PLAIN(new FloorThing("Plain Snowman", CustomMaterial.SNOWMAN_PLAIN, FloorShape._1x2V)),

	@TypeConfig(price = 375, theme = Theme.HOLIDAY)
	SNOWMAN_FANCY(new FloorThing("Fancy Snowman", CustomMaterial.SNOWMAN_FANCY, FloorShape._1x2V)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	SNOWBALLS_SMALL(new FloorThing("Small Pile of Snowballs", CustomMaterial.SNOWBALLS_SMALL)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	SNOWBALLS_BIG(new FloorThing("Big Pile of Snowballs", CustomMaterial.SNOWBALLS_BIG)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_CENTER(new WallThing("Icicle Lights - Center", CustomMaterial.ICICLE_LIGHT_CENTER)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_LEFT(new WallThing("Icicle Lights - Left", CustomMaterial.ICICLE_LIGHT_LEFT)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_RIGHT(new WallThing("Icicle Lights - Right", CustomMaterial.ICICLE_LIGHT_RIGHT)),

	@TypeConfig(price = 90, theme = Theme.HOLIDAY)
	ICICLE_SMALL(new CeilingThing("Small Icicle", CustomMaterial.ICICLE_SMALL)),

	@TypeConfig(price = 150, theme = Theme.HOLIDAY)
	ICICLE_LARGE(new CeilingThing("Large Icicle", CustomMaterial.ICICLE_LARGE, Basic._1x1)),

	@TypeConfig(price = 185, theme = Theme.HOLIDAY)
	ICICLE_MULTI(new CeilingThing("Pair of Icicles", CustomMaterial.ICICLE_MULTI, Basic._1x1)),

	@TypeConfig(price = 300, theme = Theme.HOLIDAY)
	GIANT_CANDY_CANE(new DyeableFloorThing("Giant Candy Cane", CustomMaterial.GIANT_CANDY_CANE, ColorableType.DYE, Unique.GIANT_CANDY_CANE)),

// Catalog: Spooky
	@TypeConfig(price = 75, theme = Theme.SPOOKY)
	GRAVESTONE_SMALL(new FloorThing("Small Gravestone", CustomMaterial.GRAVESTONE_SMALL)),

	@TypeConfig(price = 150, theme = Theme.SPOOKY)
	GRAVESTONE_CROSS(new FloorThing("Gravestone Cross", CustomMaterial.GRAVESTONE_CROSS, Basic._1x1_BARS)),

	@TypeConfig(price = 75, theme = Theme.SPOOKY)
	GRAVESTONE_PLAQUE(new FloorThing("Gravestone Plaque", CustomMaterial.GRAVESTONE_PLAQUE)),

	@TypeConfig(price = 150, theme = Theme.SPOOKY)
	GRAVESTONE_STACK(new FloorThing("Rock Stack Gravestone", CustomMaterial.GRAVESTONE_STACK)),

	@TypeConfig(price = 225, theme = Theme.SPOOKY)
	GRAVESTONE_FLOWERBED(new FloorThing("Flowerbed Gravestone", CustomMaterial.GRAVESTONE_FLOWERBED)),

	@TypeConfig(price = 225, theme = Theme.SPOOKY)
	GRAVESTONE_TALL(new FloorThing("Tall Gravestone", CustomMaterial.GRAVESTONE_TALL, Unique.GRAVESTONE_TALL)),

// Catalog: Music
	// - Noisemakers
	@TypeConfig(price = 1500, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	DRUM_KIT(new DyeableInstrument("Drum Kit", CustomMaterial.DRUM_KIT, InstrumentSound.DRUM_KIT, ColorableType.DYE, Unique.DRUM_KIT, true, InstrumentType.FLOOR)),

	@TypeConfig(price = 2250, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_GRAND(new DyeableInstrument("Grand Piano", CustomMaterial.PIANO_GRAND, InstrumentSound.GRAND_PIANO, ColorableType.STAIN, Unique.PIANO_GRAND, true, InstrumentType.FLOOR)),

	@TypeConfig(price = 750, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_KEYBOARD(new DyeableInstrument("Keyboard", CustomMaterial.PIANO_KEYBOARD, InstrumentSound.PIANO, ColorableType.DYE, FloorShape._1x2H_LIGHT, true, InstrumentType.FLOOR)),

	@TypeConfig(price = 900, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_KEYBOARD_ON_STAND(new DyeableInstrument("Keyboard On Stand", CustomMaterial.PIANO_KEYBOARD_ON_STAND, InstrumentSound.PIANO, ColorableType.DYE, FloorShape._1x2H, true, InstrumentType.FLOOR)),

	@TypeConfig(price = 1050, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	HARP(new Instrument("Harp", CustomMaterial.HARP, InstrumentSound.HARP, FloorShape._1x2V, InstrumentType.FLOOR)),

	@TypeConfig(price = 900, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	BONGOS(new DyeableInstrument("Bongos", CustomMaterial.BONGOS, InstrumentSound.BONGOS, ColorableType.DYE, FloorShape._1x2H, true, InstrumentType.FLOOR)),

	@TypeConfig(price = 675, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC(new DyeableInstrument("Acoustic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC, InstrumentSound.TODO, ColorableType.STAIN, InstrumentType.FLOOR)),

	@TypeConfig(price = 675, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_WALL(new DyeableInstrument("Wall Mounted Acoustic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_WALL, InstrumentSound.TODO, ColorableType.STAIN, FloorShape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@TypeConfig(price = 750, theme = Theme.MUSIC)
	GUITAR_ELECTRIC(new DyeableInstrument("Electric Guitar Display", CustomMaterial.GUITAR_ELECTRIC, InstrumentSound.TODO, ColorableType.DYE, InstrumentType.FLOOR)),

	@TypeConfig(price = 750, theme = Theme.MUSIC)
	GUITAR_ELECTRIC_WALL(new DyeableInstrument("Wall Mounted Electric Guitar Display", CustomMaterial.GUITAR_ELECTRIC_WALL, InstrumentSound.TODO, ColorableType.DYE, FloorShape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@TypeConfig(price = 600, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC(new Instrument("Acoustic Classic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_CLASSIC, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@TypeConfig(price = 600, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC_WALL(new Instrument("Wall Mounted Acoustic Classic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_CLASSIC_WALL, InstrumentSound.TODO, FloorShape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	TRUMPET(new Instrument("Trumpet Display", CustomMaterial.TRUMPET, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	SAXOPHONE(new Instrument("Saxophone Display", CustomMaterial.SAXOPHONE, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	VIOLIN(new Instrument("Violin Display", CustomMaterial.VIOLIN, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	VIOLIN_WALL(new Instrument("Wall Mounted Violin Display", CustomMaterial.VIOLIN_WALL, InstrumentSound.TODO, FloorShape._1x2V_LIGHT_DOWN, InstrumentType.WALL)),

	@TypeConfig(price = 750, theme = Theme.MUSIC)
	CELLO(new Instrument("Cello Display", CustomMaterial.CELLO, InstrumentSound.TODO, InstrumentType.FLOOR)),

	@TypeConfig(price = 165, theme = Theme.MUSIC)
	DRUM_THRONE(new Chair("Drum Throne", CustomMaterial.DRUM_THRONE, ColorableType.DYE, 1.35)),

	@TypeConfig(price = 180, theme = Theme.MUSIC)
	PIANO_BENCH(new Bench("Piano Bench", CustomMaterial.PIANO_BENCH, ColorableType.STAIN, 1.15, FloorShape._1x2H)),

	@TypeConfig(price = 210, theme = Theme.MUSIC)
	PIANO_BENCH_GRAND(new Bench("Grand Piano Bench", CustomMaterial.PIANO_BENCH_GRAND, ColorableType.STAIN, 1.15, FloorShape._1x3H)),

	@TypeConfig(price = 225, theme = Theme.MUSIC)
	AMPLIFIER(new FloorThing("Amplifier", CustomMaterial.AMPLIFIER, Basic._1x1)),

	@TypeConfig(price = 105, theme = Theme.MUSIC)
	GOLDEN_RECORD(new WallThing("Golden Record", CustomMaterial.GOLDEN_RECORD)),

	@TypeConfig(price = 300, theme = Theme.MUSIC)
	SPEAKER_LARGE(new FloorThing("Large Speaker", CustomMaterial.SPEAKER_LARGE, FloorShape._1x2V)),

	@TypeConfig(price = 150, theme = Theme.MUSIC)
	SPEAKER_SMALL(new FloorThing("Small Speaker", CustomMaterial.SPEAKER_SMALL, Basic._1x1)),

	@TypeConfig(price = 135, theme = Theme.MUSIC)
	LAUNCHPAD(new FloorThing("Launchpad", CustomMaterial.LAUNCHPAD)),

	@TypeConfig(price = 150, theme = Theme.MUSIC)
	MICROPHONE(new FloorThing("Microphone", CustomMaterial.MICROPHONE)),

	@TypeConfig(price = 195, theme = Theme.MUSIC)
	MICROPHONE_WITH_BOOM_STAND(new FloorThing("Microphone With Boom Stand", CustomMaterial.MICROPHONE_WITH_BOOM_STAND)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	MIXING_CONSOLE(new FloorThing("Mixing Console", CustomMaterial.MIXING_CONSOLE, FloorShape._1x2H_LIGHT, true)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	LIGHT_BOARD(new FloorThing("Light Board", CustomMaterial.LIGHT_BOARD, FloorShape._1x2H_LIGHT, true)),

	@TypeConfig(price = 375, theme = Theme.MUSIC)
	SPEAKER_WOODEN_LARGE(new DyeableFloorThing("Large Wooden Speaker", CustomMaterial.SPEAKER_WOODEN_LARGE, ColorableType.STAIN, FloorShape._1x2V)),

	@TypeConfig(price = 225, theme = Theme.MUSIC)
	SPEAKER_WOODEN_SMALL(new DyeableFloorThing("Small Wooden Speaker", CustomMaterial.SPEAKER_WOODEN_SMALL, ColorableType.STAIN, Basic._1x1)),

	@TypeConfig(price = 285, theme = Theme.MUSIC)
	TAPE_MACHINE(new DyeableFloorThing("Tape Machine", CustomMaterial.TAPE_MACHINE, ColorableType.STAIN, Basic._1x1)),

	@TypeConfig(price = 525, theme = Theme.MUSIC)
	DJ_TURNTABLE(new DyeableFloorThing("DJ Turntable", CustomMaterial.DJ_TURNTABLE, ColorableType.DYE, FloorShape._1x3H_LIGHT, true)),

	@TypeConfig(price = 150, theme = Theme.MUSIC)
	RECORD_PLAYER_MODERN(new DyeableFloorThing("Modern Record Player - Off", CustomMaterial.RECORD_PLAYER_MODERN, ColorableType.STAIN, Basic._1x1)),

	@TypeConfig(price = 165, theme = Theme.MUSIC)
	RECORD_PLAYER_MODERN_ON(new DyeableFloorThing("Modern Record Player - On", CustomMaterial.RECORD_PLAYER_MODERN_ON, ColorableType.STAIN, Basic._1x1)),

	@TypeConfig(price = 300, theme = Theme.MUSIC)
	STUDIO_LIGHT_HANGING(new CeilingThing("Hanging Studio Lights", CustomMaterial.STUDIO_LIGHTS_HANGING)),

	@TypeConfig(price = 225, theme = Theme.MUSIC)
	STUDIO_LIGHT_STANDING(new FloorThing("Standing Studio Light", CustomMaterial.STUDIO_LIGHTS_STANDING, FloorShape._1x2V)),

// Catalog: Pride
	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_ACE(new Flag("Asexual Pride Flag", CustomMaterial.FLAG_PRIDE_ACE)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_AGENDER(new Flag("Agender Pride Flag", CustomMaterial.FLAG_PRIDE_AGENDER)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_ARO(new Flag("Aromatic Pride Flag", CustomMaterial.FLAG_PRIDE_ARO)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_BI(new Flag("Bisexual Pride Flag", CustomMaterial.FLAG_PRIDE_BI)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMI(new Flag("Demisexual Pride Flag", CustomMaterial.FLAG_PRIDE_DEMI)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIBOY(new Flag("Demisexual Boy Pride Flag", CustomMaterial.FLAG_PRIDE_DEMIBOY)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIGIRL(new Flag("Demisexual Girl Pride Flag", CustomMaterial.FLAG_PRIDE_DEMIGIRL)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIROMANTIC(new Flag("Demiromantic Pride Flag", CustomMaterial.FLAG_PRIDE_DEMIROMANTIC)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GAY(new Flag("Gay Pride Flag", CustomMaterial.FLAG_PRIDE_GAY)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENDERFLUID(new Flag("Genderfluid Pride Flag", CustomMaterial.FLAG_PRIDE_GENDERFLU)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENDERFLUX(new Flag("Genderflux Pride Flag", CustomMaterial.FLAG_PRIDE_GENDERFLUX)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENQUEER(new Flag("Genderqueer Pride Flag", CustomMaterial.FLAG_PRIDE_GENQUEER)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GRAYACE(new Flag("Gray-Asexual Pride Flag", CustomMaterial.FLAG_PRIDE_GRAYACE)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GRAYARO(new Flag("Gray-Aromatic Pride Flag", CustomMaterial.FLAG_PRIDE_GRAYARO)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_INTERSEX(new Flag("Intersex Pride Flag", CustomMaterial.FLAG_PRIDE_INTERSEX)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_LESBIAN(new Flag("Lesbian Pride Flag", CustomMaterial.FLAG_PRIDE_LESBIAN)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_NONBINARY(new Flag("Nonbinary Pride Flag", CustomMaterial.FLAG_PRIDE_NONBINARY)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_PAN(new Flag("Pansexual Pride Flag", CustomMaterial.FLAG_PRIDE_PAN)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_POLYAM(new Flag("Polyamorous Pride Flag", CustomMaterial.FLAG_PRIDE_POLYAM)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_POLYSEX(new Flag("Polysexual Pride Flag", CustomMaterial.FLAG_PRIDE_POLYSEX)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANS(new Flag("Transgender Pride Flag", CustomMaterial.FLAG_PRIDE_TRANS)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANSFEM(new Flag("Transfeminine Pride Flag", CustomMaterial.FLAG_PRIDE_TRANSFEM)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANSMASC(new Flag("Transmasculine Pride Flag", CustomMaterial.FLAG_PRIDE_TRANSMASC)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_QUEER(new Flag("Queer Pride Flag", CustomMaterial.FLAG_PRIDE_QUEER)),

	// Pride Bunting
	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_ACE(new Bunting("Asexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_ACE)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_AGENDER(new Bunting("Agender Pride Bunting", CustomMaterial.BUNTING_PRIDE_AGENDER)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_ARO(new Bunting("Aromatic Pride Bunting", CustomMaterial.BUNTING_PRIDE_ARO)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_BI(new Bunting("Bisexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_BI)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMI(new Bunting("Demisexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMI)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIBOY(new Bunting("Demisexual Boy Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMIBOY)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIGIRL(new Bunting("Demisexual Girl Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMIGIRL)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIROMANTIC(new Bunting("Demiromantic Pride Bunting", CustomMaterial.BUNTING_PRIDE_DEMIROMANTIC)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GAY(new Bunting("Gay Pride Bunting", CustomMaterial.BUNTING_PRIDE_GAY)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENDERFLU(new Bunting("Genderfluid Pride Bunting", CustomMaterial.BUNTING_PRIDE_GENDERFLU)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENDERFLUX(new Bunting("Genderflux Pride Bunting", CustomMaterial.BUNTING_PRIDE_GENDERFLUX)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENQUEER(new Bunting("Genderqueer Pride Bunting", CustomMaterial.BUNTING_PRIDE_GENQUEER)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GRAYACE(new Bunting("Gray-Asexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_GRAYACE)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GRAYARO(new Bunting("Gray-Aromatic Pride Bunting", CustomMaterial.BUNTING_PRIDE_GRAYARO)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_INTERSEX(new Bunting("Intersex Pride Bunting", CustomMaterial.BUNTING_PRIDE_INTERSEX)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_LESBIAN(new Bunting("Lesbian Pride Bunting", CustomMaterial.BUNTING_PRIDE_LESBIAN)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_NONBINARY(new Bunting("Nonbinary Pride Bunting", CustomMaterial.BUNTING_PRIDE_NONBINARY)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_PAN(new Bunting("Pansexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_PAN)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_POLYAM(new Bunting("Polyamorous Pride Bunting", CustomMaterial.BUNTING_PRIDE_POLYAM)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_POLYSEX(new Bunting("Polysexual Pride Bunting", CustomMaterial.BUNTING_PRIDE_POLYSEX)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANS(new Bunting("Transgender Pride Bunting", CustomMaterial.BUNTING_PRIDE_TRANS)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANSFEM(new Bunting("Transfeminine Pride Bunting", CustomMaterial.BUNTING_PRIDE_TRANSFEM)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANSMASC(new Bunting("Transmasculine Pride Bunting", CustomMaterial.BUNTING_PRIDE_TRANSMASC)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_QUEER(new Bunting("Queer Pride Bunting", CustomMaterial.BUNTING_PRIDE_QUEER)),

// Catalog: Outdoors
	//	Windchimes
	@TypeConfig(price = 150, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_IRON(new WindChime("Iron Windchimes", WindChimeType.IRON)),

	@TypeConfig(price = 300, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_GOLD(new WindChime("Gold Windchimes", WindChimeType.GOLD)),

	@TypeConfig(price = 225, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_COPPER(new WindChime("Copper Windchimes", WindChimeType.COPPER)),

	@TypeConfig(price = 900, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_AMETHYST(new WindChime("Amethyst Windchimes", WindChimeType.AMETHYST)),

	@TypeConfig(price = 150, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_LAPIS(new WindChime("Lapis Windchimes", WindChimeType.LAPIS)),

	@TypeConfig(price = 3000, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_NETHERITE(new WindChime("Netherite Windchimes", WindChimeType.NETHERITE)),

	@TypeConfig(price = 1500, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_DIAMOND(new WindChime("Diamond Windchimes", WindChimeType.DIAMOND)),

	@TypeConfig(price = 600, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_REDSTONE(new WindChime("Redstone Windchimes", WindChimeType.REDSTONE)),

	@TypeConfig(price = 1050, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_EMERALD(new WindChime("Emerald Windchimes", WindChimeType.EMERALD)),

	@TypeConfig(price = 300, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_QUARTZ(new WindChime("Quartz Windchimes", WindChimeType.QUARTZ)),

	@TypeConfig(price = 150, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_COAL(new WindChime("Coal Windchimes", WindChimeType.COAL)),

	@TypeConfig(price = 150, theme = Theme.OUTDOORS, tabs = Tab.WINDCHIMES)
	WINDCHIME_ICE(new WindChime("Ice Windchimes", WindChimeType.ICE)),

	// 	Birdhouses
	@TypeConfig(price = 150, theme = Theme.OUTDOORS)
	BIRDHOUSE_FOREST_HORIZONTAL(new BirdHouse("Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL, true)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_FOREST_VERTICAL(new BirdHouse("Vertical Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_VERTICAL, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_FOREST_HANGING(new BirdHouse("Hanging Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HANGING, false)),

	@TypeConfig(price = 150, theme = Theme.OUTDOORS)
	BIRDHOUSE_ENCHANTED_HORIZONTAL(new BirdHouse("Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HORIZONTAL, true)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_ENCHANTED_VERTICAL(new BirdHouse("Vertical Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_VERTICAL, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_ENCHANTED_HANGING(new BirdHouse("Hanging Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HANGING, false)),

	@TypeConfig(price = 150, theme = Theme.OUTDOORS)
	BIRDHOUSE_DEPTHS_HORIZONTAL(new BirdHouse("Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HORIZONTAL, true)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_DEPTHS_VERTICAL(new BirdHouse("Vertical Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_VERTICAL, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_DEPTHS_HANGING(new BirdHouse("Hanging Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HANGING, false)),

	// Flora
	@TypeConfig(price = 120, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BUSHY_PLANT(new DyeableFloorThing("Bushy Plant", CustomMaterial.FLORA_BUSHY_PLANT, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 225, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_CHERRY_TREE(new DyeableFloorThing("Potted Cherry Tree", CustomMaterial.FLORA_POTTED_CHERRY_TREE, ColorableType.DYE, Basic._1x1_HEAD)),

	@TypeConfig(price = 165, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_BAY_TREE(new DyeableFloorThing("Potted Bay Tree", CustomMaterial.FLORA_POTTED_BAY_TREE, ColorableType.DYE, FloorShape._1x2V)),

	@TypeConfig(price = 135, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_SNAKE_PLANT(new DyeableFloorThing("Snake Plant", CustomMaterial.FLORA_SNAKE_PLANT, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 135, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_WHITE_BIRD_PARADISE(new DyeableFloorThing("White Bird of Paradise", CustomMaterial.FLORA_WHITE_BIRD_PARADISE, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 210, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI(new DyeableFloorThing("Bonsai", CustomMaterial.FLORA_BONSAI, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 210, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_CHERRY(new DyeableFloorThing("Cherry Bonsai", CustomMaterial.FLORA_BONSAI_CHERRY, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 140, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_SMALL(new DyeableFloorThing("Small Bonsai", CustomMaterial.FLORA_BONSAI_SMALL, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 140, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_CHERRY_SMALL(new DyeableFloorThing("Small Cherry Bonsai", CustomMaterial.FLORA_BONSAI_CHERRY_SMALL, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 75, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_CHINESE_EVERGREEN(new DyeableFloorThing("Chinese Evergreen", CustomMaterial.FLORA_CHINESE_EVERGREEN, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 135, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_FLOWER_VASE(new DyeableFloorThing("Flower Vase", CustomMaterial.FLORA_FLOWER_VASE, ColorableType.DYE, Basic.NONE)),

	@TypeConfig(price = 105, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_WALL_FLOWERS_1(new DyeableWallThing("Wall Flowers", CustomMaterial.FLORA_WALL_FLOWERS_1, ColorableType.DYE, Basic._1x1)),

	@TypeConfig(price = 95, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_TULIPS(new DyeableFloorThing("Potted Tulips", CustomMaterial.FLORA_POTTED_TULIPS, ColorableType.DYE, Basic._1x1_HEAD)),

	// Misc
	@TypeConfig(price = 115, theme = Theme.OUTDOORS)
	BED_SLEEPING_BAG(new DyeableFloorThing("Sleeping Bag", CustomMaterial.BED_SLEEPING_BAG, ColorableType.DYE)),

// Catalog: Art
	//	Custom
	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CHERRY_FOREST(new Art("Komorebi", CustomMaterial.ART_PAINTING_CUSTOM_CHERRY_FOREST, WallShape._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_END_ISLAND(new Art("Limbo", CustomMaterial.ART_PAINTING_CUSTOM_END_ISLAND, WallShape._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_LOST_ENDERMAN(new Art("Lost Enderman", CustomMaterial.ART_PAINTING_CUSTOM_LOST_ENDERMAN, WallShape._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PINE_TREE(new Art("Black Hills", CustomMaterial.ART_PAINTING_CUSTOM_PINE_TREE, WallShape._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SUNSET(new Art("Palm Cove", CustomMaterial.ART_PAINTING_CUSTOM_SUNSET, WallShape._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SWAMP_HUT(new Art("Isolation", CustomMaterial.ART_PAINTING_CUSTOM_SWAMP_HUT, WallShape._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_MOUNTAINS(new Art("Three Peaks", CustomMaterial.ART_PAINTING_CUSTOM_MOUNTAINS, WallShape._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_MUDDY_PIG(new Art("Blissful Piggy", CustomMaterial.ART_PAINTING_CUSTOM_MUDDY_PIG, WallShape._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PURPLE_SHEEP(new Art("Lavender Woolly", CustomMaterial.ART_PAINTING_CUSTOM_PURPLE_SHEEP, WallShape._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_VILLAGE_HAPPY(new Art("Sweet Home", CustomMaterial.ART_PAINTING_CUSTOM_VILLAGE_HAPPY, WallShape._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_VILLAGE_CHAOS(new Art("Revenge", CustomMaterial.ART_PAINTING_CUSTOM_VILLAGE_CHAOS, WallShape._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SKYBLOCK(new Art("Skyblock", CustomMaterial.ART_PAINTING_CUSTOM_SKYBLOCK, WallShape._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_FORTRESS_BRIDGE(new Art("Nether Highways", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_FORTRESS_BRIDGE, WallShape._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_CRIMSON_FOREST(new Art("Crimson Canopy", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_CRIMSON_FOREST, WallShape._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_WARPED_FOREST(new Art("Warped Woods", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_WARPED_FOREST, WallShape._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_BASALT_DELTAS(new Art("Basalt Summits", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_BASALT_DELTAS, WallShape._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_SOUL_SAND_VALLEY(new Art("Lost Souls", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_SOUL_SAND_VALLEY, WallShape._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CASTLE(new Art("Sintra", CustomMaterial.ART_PAINTING_CUSTOM_CASTLE, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_LAKE(new Art("Reflections", CustomMaterial.ART_PAINTING_CUSTOM_LAKE, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_RIVER(new Art("Flowing Home", CustomMaterial.ART_PAINTING_CUSTOM_RIVER, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_ROAD(new Art("Take Me Home", CustomMaterial.ART_PAINTING_CUSTOM_ROAD, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_ORIENTAL(new Art("Tenku No Torii", CustomMaterial.ART_PAINTING_CUSTOM_ORIENTAL, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CHICKENS(new Art("Hens Night", CustomMaterial.ART_PAINTING_CUSTOM_CHICKENS, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_OAK_TREE(new Art("English Oak", CustomMaterial.ART_PAINTING_CUSTOM_OAK_TREE, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CRAB(new Art("Nomad", CustomMaterial.ART_PAINTING_CUSTOM_CRAB, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SATURN_ROCKET(new Art("Adventure Is Out There", CustomMaterial.ART_PAINTING_CUSTOM_SATURN_ROCKET, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PARROT(new Art("Scarlet Macaw", CustomMaterial.ART_PAINTING_CUSTOM_PARROT, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_DUCKS(new Art("Voyage", CustomMaterial.ART_PAINTING_CUSTOM_DUCKS, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_STARRY_PINE_TREE(new Art("Lone Pine", CustomMaterial.ART_PAINTING_CUSTOM_STARRY_PINE_TREE, WallShape._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 450, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_FOREST(new Art("Misty Thicket", CustomMaterial.ART_PAINTING_CUSTOM_FOREST, WallShape._1x3H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 450, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SAND_DUNES(new Art("Sahara", CustomMaterial.ART_PAINTING_CUSTOM_SAND_DUNES, WallShape._1x3V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 900, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_STORY(new Art("Daydreamer", CustomMaterial.ART_PAINTING_CUSTOM_STORY, WallShape._2x3H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CITY_TWILIGHT(new Art("City Twilight", CustomMaterial.ART_PAINTING_CUSTOM_CITY_TWILIGHT, WallShape._2x2_LIGHT)),

	// Vanilla
	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_FRIEND(new Art("Friend", CustomMaterial.ART_PAINTING_VANILLA_FRIEND, WallShape._1x1_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_BELOW(new Art("Below", CustomMaterial.ART_PAINTING_VANILLA_BELOW, WallShape._1x2H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_DIRT_HUT_ROAD(new Art("Dirt Hut Road", CustomMaterial.ART_PAINTING_VANILLA_DIRT_HUT_ROAD, WallShape._1x2H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VOWS_OF_THE_CRAFTSMAN(new Art("Vows of the Craftsman", CustomMaterial.ART_PAINTING_VANILLA_VOWS_OF_THE_CRAFTSMAN, WallShape._1x2H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VILLAGER_AND_CHILD(new Art("Villager and Child", CustomMaterial.ART_PAINTING_VANILLA_VILLAGER_AND_CHILD, WallShape._1x2V_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_THREE_MASON(new Art("Level 3 Mason", CustomMaterial.ART_PAINTING_VANILLA_LEVEL_THREE_MASON, WallShape._1x2V_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_ANCIENT_POWER(new Art("Ancient Power", CustomMaterial.ART_PAINTING_VANILLA_ANCIENT_POWER, WallShape._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_IRON_SEED(new Art("Iron Seed", CustomMaterial.ART_PAINTING_VANILLA_IRON_SEED, WallShape._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_RIDERS(new Art("Riders", CustomMaterial.ART_PAINTING_VANILLA_RIDERS, WallShape._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_ONE_MASON(new Art("Level 1 Mason", CustomMaterial.ART_PAINTING_VANILLA_LEVEL_ONE_MASON, WallShape._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_TWO_MASON(new Art("Level 2 Mason", CustomMaterial.ART_PAINTING_VANILLA_LEVEL_TWO_MASON, WallShape._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 1200, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VILLAGER_TRADE(new Art("The Trade in the House of Villagers", CustomMaterial.ART_PAINTING_VANILLA_VILLAGER_TRADE, WallShape._2x4H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 1800, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_SIGNS_OF_THE_END(new Art("Signs of the End", CustomMaterial.ART_PAINTING_VANILLA_SIGNS_OF_THE_END, WallShape._4x4_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 1800, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_BLESSED_SHEEP(new Art("Three Saints and the Blessed Sheep", CustomMaterial.ART_PAINTING_VANILLA_BLESSED_SHEEP, WallShape._4x4_LIGHT, true)),

	// Catalog: General
	// 	Tables
	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x1(new Table("Wooden Table - 1x1", CustomMaterial.TABLE_WOODEN_1X1, Basic._1x1)),

	@TypeConfig(price = 105, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x2(new Table("Wooden Table - 1x2", CustomMaterial.TABLE_WOODEN_1X2, FloorShape._1x2H, true)),

	@TypeConfig(price = 135, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x3(new Table("Wooden Table - 1x3", CustomMaterial.TABLE_WOODEN_1X3, FloorShape._1x3H, true)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x2(new Table("Wooden Table - 2x2", CustomMaterial.TABLE_WOODEN_2X2, FloorShape._2x2, true)),

	@TypeConfig(price = 225, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x3(new Table("Wooden Table - 2x3", CustomMaterial.TABLE_WOODEN_2X3, FloorShape._2x3H, true)),

	@TypeConfig(price = 300, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_3x3(new Table("Wooden Table - 3x3", CustomMaterial.TABLE_WOODEN_3X3, FloorShape._3x3, true)),

	// 	Chairs
	@TypeConfig(price = 120, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_BASIC(new Chair("Wooden Chair", CustomMaterial.CHAIR_WOODEN_BASIC, ColorableType.STAIN)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_CUSHIONED(new Chair("Cushioned Wooden Chair", CustomMaterial.CHAIR_WOODEN_CUSHIONED, ColorableType.DYE)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_CLOTH(new Chair("Cloth Chair", CustomMaterial.CHAIR_CLOTH, ColorableType.DYE)),

	@TypeConfig(price = 135, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	ADIRONDACK(new Chair("Adirondack", CustomMaterial.ADIRONDACK, ColorableType.STAIN)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_BEACH(new LongChair("Beach Chair", CustomMaterial.BEACH_CHAIR, ColorableType.DYE, Hitbox.light(), .875)),

	// 	Stools
	@TypeConfig(price = 90, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_BASIC(new Chair("Wooden Stool", CustomMaterial.STOOL_WOODEN_BASIC, ColorableType.STAIN)),

	@TypeConfig(price = 120, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_CUSHIONED(new Chair("Cushioned Wooden Stool", CustomMaterial.STOOL_WOODEN_CUSHIONED, ColorableType.DYE)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_BAR_WOODEN(new Chair("Wooden Bar Stool", CustomMaterial.STOOL_BAR_WOODEN, ColorableType.STAIN, 1.4)),

	// Stumps
	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_OAK(new Stump("Oak Stump", CustomMaterial.STUMP_OAK)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_OAK_ROOTS(new Stump("Rooted Oak Stump", CustomMaterial.STUMP_OAK_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_SPRUCE(new Stump("Spruce Stump", CustomMaterial.STUMP_SPRUCE)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_SPRUCE_ROOTS(new Stump("Rooted Spruce Stump", CustomMaterial.STUMP_SPRUCE_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_BIRCH(new Stump("Birch Stump", CustomMaterial.STUMP_BIRCH)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_BIRCH_ROOTS(new Stump("Rooted Birch Stump", CustomMaterial.STUMP_BIRCH_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_JUNGLE(new Stump("Jungle Stump", CustomMaterial.STUMP_JUNGLE)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_JUNGLE_ROOTS(new Stump("Rooted Jungle Stump", CustomMaterial.STUMP_JUNGLE_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_ACACIA(new Stump("Acacia Stump", CustomMaterial.STUMP_ACACIA)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_ACACIA_ROOTS(new Stump("Rooted Acacia Stump", CustomMaterial.STUMP_ACACIA_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_DARK_OAK(new Stump("Dark Oak Stump", CustomMaterial.STUMP_DARK_OAK)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_DARK_OAK_ROOTS(new Stump("Rooted Dark Oak Stump", CustomMaterial.STUMP_DARK_OAK_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_MANGROVE(new Stump("Mangrove Stump", CustomMaterial.STUMP_MANGROVE)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_MANGROVE_ROOTS(new Stump("Rooted Mangrove Stump", CustomMaterial.STUMP_MANGROVE_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CRIMSON(new Stump("Crimson Stump", CustomMaterial.STUMP_CRIMSON)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CRIMSON_ROOTS(new Stump("Rooted Crimson Stump", CustomMaterial.STUMP_CRIMSON_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_WARPED(new Stump("Warped Stump", CustomMaterial.STUMP_WARPED)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_WARPED_ROOTS(new Stump("Rooted Warped Stump", CustomMaterial.STUMP_WARPED_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CHERRY(new Stump("Cherry Stump", CustomMaterial.STUMP_CHERRY)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CHERRY_ROOTS(new Stump("Rooted Cherry Stump", CustomMaterial.STUMP_CHERRY_ROOTS)),


	// 	Benches
	@TypeConfig(price = 225, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	BENCH_WOODEN(new Bench("Wooden Bench", CustomMaterial.BENCH_WOODEN, ColorableType.STAIN, FloorShape._1x2H)),

	// 	Couches
	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_LEFT(new Couch("Cushioned Wooden Couch Left End", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_LEFT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_RIGHT(new Couch("Cushioned Wooden Couch Left Right", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_RIGHT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_MIDDLE(new Couch("Cushioned Wooden Couch Middle", CustomMaterial.COUCH_WOODEN_CUSHIONED_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_CORNER(new Couch("Cushioned Wooden Couch Corner", CustomMaterial.COUCH_WOODEN_CUSHIONED_CORNER, ColorableType.DYE, CouchPart.CORNER)),

	@TypeConfig(price = 120, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_OTTOMAN(new Couch("Cushioned Wooden Couch Ottoman", CustomMaterial.COUCH_WOODEN_CUSHIONED_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_LEFT(new Couch("Cloth Couch Left End", CustomMaterial.COUCH_CLOTH_END_LEFT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_RIGHT(new Couch("Cloth Couch Left Right", CustomMaterial.COUCH_CLOTH_END_RIGHT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_MIDDLE(new Couch("Cloth Couch Middle", CustomMaterial.COUCH_CLOTH_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_CORNER(new Couch("Cloth Couch Corner", CustomMaterial.COUCH_CLOTH_CORNER, ColorableType.DYE, CouchPart.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_OTTOMAN(new Couch("Cloth Couch Ottoman", CustomMaterial.COUCH_CLOTH_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),

	// 	Blocks
	@TypeConfig(price = 75)
	TRASH_CAN(new TrashCan("Trash Can", CustomMaterial.TRASH_CAN, ColorableType.DYE, "C7C7C7", Basic._1x1)),

	// Custom Workbenches
	@TypeConfig(tabs = Tab.INTERNAL)
	DYE_STATION(new WorkBench("Dye Station", CustomMaterial.DYE_STATION)),

	@TypeConfig(tabs = Tab.INTERNAL)
	ENCHANTED_BOOK_SPLITTER(new WorkBench("Enchanted Book Splitter", CustomMaterial.ENCHANTED_BOOK_SPLITTER, FloorShape._1x2H)),

	@TypeConfig(tabs = Tab.INTERNAL)
	TOOL_MODIFICATION_TABLE(new WorkBench("Tool Modification Table", CustomMaterial.TOOL_MODIFICATION_TABLE, FloorShape._1x2H)),

	// Bunting
	@TypeConfig(price = 60, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1(new Bunting("Server Colors 1 Bunting", CustomMaterial.BUNTING_SERVER_COLORS_1)),

	@TypeConfig(price = 60, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2(new Bunting("Server Colors 2 Bunting", CustomMaterial.BUNTING_SERVER_COLORS_2)),

	@TypeConfig(price = 30, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1_SMALL(new Bunting("Server Colors 1 Small Bunting", CustomMaterial.BUNTING_SERVER_COLORS_1_SMALL, Basic._1x1_LIGHT)),

	@TypeConfig(price = 30, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2_SMALL(new Bunting("Server Colors 2 Small Bunting", CustomMaterial.BUNTING_SERVER_COLORS_2_SMALL, Basic._1x1_LIGHT)),

	@TypeConfig(price = 75, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_LOGO(new Bunting("Server Logo Bunting", CustomMaterial.BUNTING_SERVER_LOGO)),


	// Banners
	// 	Hanging
	@TypeConfig(price = 120, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_1(new HangingBanner("Avontyre Royal Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_1, Unique.HANGING_BANNER_1x3V)),

	@TypeConfig(price = 105, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_2(new HangingBanner("Avontyre Cyan Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_2, Unique.HANGING_BANNER_1x3V)),

	@TypeConfig(price = 105, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_3(new HangingBanner("Avontyre Yellow Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_3, Unique.HANGING_BANNER_1x3V)),

	@TypeConfig(price = 90, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_4(new HangingBanner("Avontyre Checkered Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_4, Unique.HANGING_BANNER_1x3V)),

	@TypeConfig(price = 90, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_SERVER_LOGO(new HangingBanner("Server Logo Hanging Banner", CustomMaterial.BANNER_HANGING_SERVER_LOGO)),

	//	Standing
	@TypeConfig(price = 90, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_STANDING})
	BANNER_STANDING_SERVER_LOGO(new StandingBanner("Server Logo Standing Banner", CustomMaterial.BANNER_STANDING_SERVER_LOGO)),


	// 	Fireplaces
	@TypeConfig(price = 525, tabs = Tab.FURNITURE)
	FIREPLACE_DARK(new Fireplace("Dark Fireplace", CustomMaterial.FIREPLACE_DARK)),

	@TypeConfig(price = 525, tabs = Tab.FURNITURE)
	FIREPLACE_BROWN(new Fireplace("Brown Fireplace", CustomMaterial.FIREPLACE_BROWN)),

	@TypeConfig(price = 525, tabs = Tab.FURNITURE)
	FIREPLACE_LIGHT(new Fireplace("Light Fireplace", CustomMaterial.FIREPLACE_LIGHT)),

	//	Food
	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIZZA_BOX_SINGLE(new FloorThing("Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIZZA_BOX_SINGLE_OPENED(new FloorThing("Opened Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE_OPENED)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	PIZZA_BOX_STACK(new FloorThing("Pizza Box Stack", CustomMaterial.FOOD_PIZZA_BOX_STACK)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	SOUP_MUSHROOM(new FloorThing("Mushroom Soup", CustomMaterial.FOOD_SOUP_MUSHROOM)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	SOUP_BEETROOT(new FloorThing("Beetroot Soup", CustomMaterial.FOOD_SOUP_BEETROOT)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	SOUP_RABBIT(new FloorThing("Rabbit Soup", CustomMaterial.FOOD_SOUP_RABBIT)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	BREAD_LOAF(new FloorThing("Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF)),

	@TypeConfig(price = 25, tabs = Tab.FOOD)
	BREAD_LOAF_CUT(new FloorThing("Cut Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF_CUT)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	BROWNIES_CHOCOLATE(new FloorThing("Chocolate Brownies", CustomMaterial.FOOD_BROWNIES_CHOCOLATE)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	BROWNIES_VANILLA(new FloorThing("Vanilla Brownies", CustomMaterial.FOOD_BROWNIES_VANILLA)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	COOKIES_CHOCOLATE(new FloorThing("Chocolate Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	COOKIES_CHOCOLATE_CHIP(new FloorThing("Chocolate Chip Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE_CHIP)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	COOKIES_SUGAR(new FloorThing("Sugar Cookies", CustomMaterial.FOOD_COOKIES_SUGAR)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	MILK_AND_COOKIES(new FloorThing("Milk and Cookies", CustomMaterial.FOOD_MILK_AND_COOKIES)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	MUFFINS_CHOCOLATE(new FloorThing("Chocolate Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	MUFFINS_CHOCOLATE_CHIP(new FloorThing("Chocolate Chip Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE_CHIP)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	MUFFINS_LEMON(new FloorThing("Lemon Muffins", CustomMaterial.FOOD_MUFFINS_LEMON)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	DINNER_HAM(new FloorThing("Ham Dinner", CustomMaterial.FOOD_DINNER_HAM)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	DINNER_ROAST(new FloorThing("Roast Dinner", CustomMaterial.FOOD_DINNER_ROAST)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	DINNER_TURKEY(new FloorThing("Turkey Dinner", CustomMaterial.FOOD_DINNER_TURKEY)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	PUNCHBOWL(new DyeableFloorThing("Dyeable Punchbowl", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	PUNCHBOWL_EGGNOG(new DyeableFloorThing("Eggnog", CustomMaterial.FOOD_PUNCHBOWL_EGGNOG, ColorableType.DYE, "FFF4BB")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_SAUCE(new DyeableFloorThing("Dyeable Sauce Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_SAUCE_CRANBERRIES(new DyeableFloorThing("Cranberries Side", CustomMaterial.FOOD_SIDE_SAUCE_CRANBERRIES, ColorableType.DYE, "C61B1B")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_GREEN_BEAN_CASSEROLE(new FloorThing("Green Bean Casserole Side", CustomMaterial.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_MAC_AND_CHEESE(new FloorThing("Mac N' Cheese Side", CustomMaterial.FOOD_SIDE_MAC_AND_CHEESE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_SWEET_POTATOES(new FloorThing("Sweet Potatoes Side", CustomMaterial.FOOD_SIDE_SWEET_POTATOES)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_MASHED_POTATOES(new FloorThing("Mashed Potatoes Side", CustomMaterial.FOOD_SIDE_MASHED_POTATOES)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_ROLLS(new FloorThing("Rolls", CustomMaterial.FOOD_SIDE_ROLLS)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER(new DyeableFloorThing("Dyeable Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER_RED_VELVET(new DyeableFloorThing("Red Velvet Cake Batter", CustomMaterial.FOOD_CAKE_BATTER_VELVET, ColorableType.DYE, "720606")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER_VANILLA(new DyeableFloorThing("Vanilla Cake Batter", CustomMaterial.FOOD_CAKE_BATTER_VANILLA, ColorableType.DYE, "FFF9CC")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER_CHOCOLATE(new DyeableFloorThing("Chocolate Cake Batter", CustomMaterial.FOOD_CAKE_BATTER_CHOCOLATE, ColorableType.DYE, "492804")),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	CAKE_WHITE_CHOCOLATE(new FloorThing("White Chocolate Cake", CustomMaterial.FOOD_CAKE_WHITE_CHOCOLATE)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	CAKE_BUNDT(new FloorThing("Bundt Cake", CustomMaterial.FOOD_CAKE_BUNDT)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	CAKE_CHOCOLATE_DRIP(new FloorThing("Chocolate Drip Cake", CustomMaterial.FOOD_CAKE_CHOCOLATE_DRIP)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_ROUGH(new DyeableFloorThing("Dyeable Rough Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_ROUGH_PECAN(new DyeableFloorThing("Pecan Pie", CustomMaterial.FOOD_PIE_ROUGH_PECAN, ColorableType.DYE, "4E3004")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH(new DyeableFloorThing("Dyeable Smooth Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH_CHOCOLATE(new DyeableFloorThing("Chocolate Pie", CustomMaterial.FOOD_PIE_SMOOTH_CHOCOLATE, ColorableType.DYE, "734008")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH_LEMON(new DyeableFloorThing("Lemon Pie", CustomMaterial.FOOD_PIE_SMOOTH_LEMON, ColorableType.DYE, "FFE050")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH_PUMPKIN(new DyeableFloorThing("Pumpkin Pie Decoration", CustomMaterial.FOOD_PIE_SMOOTH_PUMPKIN, ColorableType.DYE, "BF7D18")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED(new DyeableFloorThing("Dyeable Latticed Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED_APPLE(new DyeableFloorThing("Apple Pie", CustomMaterial.FOOD_PIE_LATTICED_APPLE, ColorableType.DYE, "FDC330")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED_BLUEBERRY(new DyeableFloorThing("Blueberry Pie", CustomMaterial.FOOD_PIE_LATTICED_BLUEBERRY, ColorableType.DYE, "4E1892")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED_CHERRY(new DyeableFloorThing("Cherry Pie", CustomMaterial.FOOD_PIE_LATTICED_CHERRY, ColorableType.DYE, "B60C0C")),


	//	Kitchenware
	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE(new FloorThing("Wine Bottle", CustomMaterial.KITCHENWARE_WINE_BOTTLE)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP(new FloorThing("Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_RANDOM(new FloorThing("Random Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_SIDE(new FloorThing("Wine Bottles on Side", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_SIDE)),

	@TypeConfig(price = 30, tabs = Tab.KITCHENWARE)
	WINE_GLASS(new FloorThing("Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS)),

	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	WINE_GLASS_FULL(new FloorThing("Full Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS_FULL)),

	@TypeConfig(price = 30, tabs = Tab.KITCHENWARE)
	MUG_GLASS(new FloorThing("Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS)),

	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	MUG_GLASS_FULL(new FloorThing("Full Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS_FULL)),

	@TypeConfig(price = 30, tabs = Tab.KITCHENWARE)
	MUG_WOODEN(new FloorThing("Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN)),

	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	MUG_WOODEN_FULL(new FloorThing("Full Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN_FULL)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_1(new FloorThing("Random Glassware 1", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_1)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_2(new FloorThing("Random Glassware 2", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_2)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_3(new FloorThing("Random Glassware 3", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_3)),

	@TypeConfig(price = 75, tabs = Tab.KITCHENWARE)
	JAR(new FloorThing("Jar", CustomMaterial.KITCHENWARE_JAR)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	JAR_HONEY(new FloorThing("Honey Jar", CustomMaterial.KITCHENWARE_JAR_HONEY)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	JAR_COOKIES(new FloorThing("Cookie Jar", CustomMaterial.KITCHENWARE_JAR_COOKIES)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	JAR_WIDE(new FloorThing("Wide Jar", CustomMaterial.KITCHENWARE_JAR_WIDE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	BOWL_DECORATION(new FloorThing("Wooden Bowl", CustomMaterial.KITCHENWARE_BOWL)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	MIXING_BOWL(new FloorThing("Mixing Bowl", CustomMaterial.KITCHENWARE_MIXING_BOWL)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_CAKE(new FloorThing("Cake Pan", CustomMaterial.KITCHENWARE_PAN_CAKE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_CASSEROLE(new FloorThing("Casserole Pan", CustomMaterial.KITCHENWARE_PAN_CASSEROLE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_COOKIE(new FloorThing("Cookie Pan", CustomMaterial.KITCHENWARE_PAN_COOKIE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_MUFFIN(new FloorThing("Muffin Pan", CustomMaterial.KITCHENWARE_PAN_MUFFIN)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_PIE(new FloorThing("Pie Pan", CustomMaterial.KITCHENWARE_PAN_PIE)),


	// 	Appliances
	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE(new DyeableFloorThing("Fridge", CustomMaterial.APPLIANCE_FRIDGE, ColorableType.DYE, "FFFFFF", FloorShape._1x2V)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MAGNETS(new DyeableFloorThing("Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MAGNETS, ColorableType.DYE, "FFFFFF", FloorShape._1x2V)),

	@TypeConfig(price = 270, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL(new DyeableFloorThing("Tall Fridge", CustomMaterial.APPLIANCE_FRIDGE_TALL, ColorableType.DYE, "FFFFFF", FloorShape._1x3V)),

	@TypeConfig(price = 285, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL_MAGNETS(new DyeableFloorThing("Tall Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_TALL_MAGNETS, ColorableType.DYE, "FFFFFF", FloorShape._1x3V)),

	@TypeConfig(price = 90, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI(new DyeableFloorThing("Mini Fridge", CustomMaterial.APPLIANCE_FRIDGE_MINI, ColorableType.DYE, "FFFFFF", Basic._1x1)),

	@TypeConfig(price = 105, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI_MAGNETS(new DyeableFloorThing("Mini Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MINI_MAGNETS, ColorableType.DYE, "FFFFFF", Basic._1x1)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_SLUSHIE_MACHINE(new DyeableFloorThing("Slushie Machine", CustomMaterial.APPLIANCE_SLUSHIE_MACHINE, ColorableType.DYE, Basic._1x1)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_GRILL_COMMERCIAL(new Block("Commercial Grill", CustomMaterial.APPLIANCE_GRILL_COMMERCIAL, RotationSnap.BOTH)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_OVEN_COMMERCIAL(new Block("Commercial Oven", CustomMaterial.APPLIANCE_OVEN_COMMERCIAL, RotationSnap.BOTH)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_DEEP_FRYER_COMMERCIAL(new Block("Commercial Deep Fryer", CustomMaterial.APPLIANCE_DEEP_FRYER_COMMERCIAL, RotationSnap.BOTH)),


	// Counters - STEEL HANDLES
	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_ISLAND, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_CORNER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_DRAWER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_CABINET, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_OVEN, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_SINK(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_SINK, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_STEEL_MARBLE_BAR(new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_BAR, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_ISLAND, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_CORNER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_DRAWER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_CABINET, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_OVEN, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_SINK(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_SINK, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_STEEL_SOAPSTONE_BAR(new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_BAR, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_STONE_ISLAND, HandleType.STEEL, CounterMaterial.STONE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_STONE_CORNER, HandleType.STEEL, CounterMaterial.STONE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_STONE_DRAWER, HandleType.STEEL, CounterMaterial.STONE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_STONE_CABINET, HandleType.STEEL, CounterMaterial.STONE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_STONE_OVEN, HandleType.STEEL, CounterMaterial.STONE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_SINK(new Counter(CustomMaterial.COUNTER_STEEL_STONE_SINK, HandleType.STEEL, CounterMaterial.STONE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.STONE_COUNTER})
	COUNTER_STEEL_STONE_BAR(new Counter(CustomMaterial.COUNTER_STEEL_STONE_BAR, HandleType.STEEL, CounterMaterial.STONE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_ISLAND(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_ISLAND, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_CORNER(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_CORNER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_DRAWER(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_DRAWER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_CABINET(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_CABINET, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_OVEN(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_OVEN, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_SINK(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_SINK, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.STEEL_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_STEEL_WOODEN_BAR(new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_BAR, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - BRASS HANDLES
	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_ISLAND, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_CORNER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_DRAWER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_CABINET, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_OVEN, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_SINK(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_SINK, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BRASS_MARBLE_BAR(new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_BAR, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_ISLAND, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_CORNER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_DRAWER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_CABINET, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_OVEN, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_SINK(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_SINK, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BRASS_SOAPSTONE_BAR(new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_BAR, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_STONE_ISLAND, HandleType.BRASS, CounterMaterial.STONE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_STONE_CORNER, HandleType.BRASS, CounterMaterial.STONE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_STONE_DRAWER, HandleType.BRASS, CounterMaterial.STONE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_STONE_CABINET, HandleType.BRASS, CounterMaterial.STONE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_STONE_OVEN, HandleType.BRASS, CounterMaterial.STONE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_SINK(new Counter(CustomMaterial.COUNTER_BRASS_STONE_SINK, HandleType.BRASS, CounterMaterial.STONE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BRASS_STONE_BAR(new Counter(CustomMaterial.COUNTER_BRASS_STONE_BAR, HandleType.BRASS, CounterMaterial.STONE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_ISLAND(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_ISLAND, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_CORNER(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_CORNER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_DRAWER(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_DRAWER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_CABINET(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_CABINET, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_OVEN(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_OVEN, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_SINK(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_SINK, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BRASS_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BRASS_WOODEN_BAR(new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_BAR, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - BLACK HANDLES
	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_ISLAND, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_CORNER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_DRAWER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_CABINET, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_OVEN, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_SINK(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_SINK, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.MARBLE_COUNTER})
	COUNTER_BLACK_MARBLE_BAR(new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_BAR, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_ISLAND, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CORNER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_DRAWER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_OVEN, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_SINK(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_SINK, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.SOAPSTONE_COUNTER})
	COUNTER_BLACK_SOAPSTONE_BAR(new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_BAR, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_STONE_ISLAND, HandleType.BLACK, CounterMaterial.STONE, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_STONE_CORNER, HandleType.BLACK, CounterMaterial.STONE, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_STONE_DRAWER, HandleType.BLACK, CounterMaterial.STONE, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_STONE_CABINET, HandleType.BLACK, CounterMaterial.STONE, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_STONE_OVEN, HandleType.BLACK, CounterMaterial.STONE, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_SINK(new Counter(CustomMaterial.COUNTER_BLACK_STONE_SINK, HandleType.BLACK, CounterMaterial.STONE, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.STONE_COUNTER})
	COUNTER_BLACK_STONE_BAR(new Counter(CustomMaterial.COUNTER_BLACK_STONE_BAR, HandleType.BLACK, CounterMaterial.STONE, CounterType.BAR)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_ISLAND(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_ISLAND, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.ISLAND)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_CORNER(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_CORNER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_DRAWER(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_DRAWER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.DRAWER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_CABINET(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_CABINET, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CABINET)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_OVEN(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_OVEN, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.OVEN)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_SINK(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_SINK, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.SINK)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.COUNTERS_MENU, Tab.BLACK_HANDLES, Tab.WOODEN_COUNTER})
	COUNTER_BLACK_WOODEN_BAR(new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_BAR, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.BAR)),

	// Cabinets - STEEL HANDLES
	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN(new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.CABINET)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN_HOOD(new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.HOOD)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.STEEL_HANDLES})
	CABINET_STEEL_WOODEN_SHORT(new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.SHORT)),

	// Cabinets - BRASS HANDLES
	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN(new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.CABINET)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN_HOOD(new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.HOOD)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BRASS_HANDLES})
	CABINET_BRASS_WOODEN_SHORT(new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.SHORT)),

	// Cabinets - BLACK HANDLES
	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN(new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.CABINET)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN_HOOD(new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.HOOD)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CABINETS, Tab.BLACK_HANDLES})
	CABINET_BLACK_WOODEN_SHORT(new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN_SHORT, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.SHORT)),

	// Cabinets - GENERIC
	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CABINETS})
	CABINET_HOOD(new Cabinet(CustomMaterial.CABINET_HOOD, CabinetMaterial.NONE, HandleType.NONE, CabinetType.HOOD)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.CABINETS})
	CABINET_WOODEN_CORNER(new Cabinet(CustomMaterial.CABINET_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.NONE, CabinetType.CORNER)),

	@TypeConfig(price = 225, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	TOILET_MODERN(new Chair("Toilet Modern", CustomMaterial.TOILET_MODERN, ColorableType.DYE, "FFFFFF", Basic._1x1, 1.3)),

	@TypeConfig(price = 450, tabs = Tab.FURNITURE)
	WARDROBE(new Furniture("Wardrobe", CustomMaterial.WARDROBE, FurnitureSurface.FLOOR, FloorShape._2x3V)),

	@TypeConfig(price = 240, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_DOUBLE(new Furniture("Short Cupboard Double", CustomMaterial.CUPBOARD_SHORT_DOUBLE, FurnitureSurface.FLOOR, FloorShape._1x2H)),

	@TypeConfig(price = 120, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_SINGLE(new Furniture("Short Cupboard Single", CustomMaterial.CUPBOARD_SHORT_SINGLE, FurnitureSurface.FLOOR, Basic._1x1)),

	@TypeConfig(price = 240, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_BOOKSHELF_DOUBLE(new Furniture("Short Bookshelf Cupboard Double", CustomMaterial.CUPBOARD_SHORT_BOOKSHELF_DOUBLE, FurnitureSurface.FLOOR, FloorShape._1x2H)),

	@TypeConfig(price = 120, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_BOOKSHELF_SINGLE(new Furniture("Short Bookshelf Cupboard Single", CustomMaterial.CUPBOARD_SHORT_BOOKSHELF_SINGLE, FurnitureSurface.FLOOR, Basic._1x1)),

	@TypeConfig(price = 150, tabs = Tab.FURNITURE)
	SHELF_WALL(new Shelf("Wall Shelf", CustomMaterial.SHELF_WALL, ColorableType.STAIN, FloorShape._1x2H)),

	@TypeConfig(tabs = Tab.INTERNAL)
	SHELF_STORAGE(new Furniture("Storage Shelf", CustomMaterial.SHELF_STORAGE, FurnitureSurface.FLOOR, FloorShape._2x3V)),


	// Beds
	@TypeConfig(price = 215, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_1_SINGLE(new BedAddition("Generic Frame A Single", CustomMaterial.BED_GENERIC_1_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 430, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_1_DOUBLE(new BedAddition("Generic Frame A Double", CustomMaterial.BED_GENERIC_1_DOUBLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 235, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_2_SINGLE(new BedAddition("Generic Frame B Single", CustomMaterial.BED_GENERIC_2_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 470, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_2_DOUBLE(new BedAddition("Generic Frame B Double", CustomMaterial.BED_GENERIC_2_DOUBLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 215, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_3_SINGLE(new BedAddition("Generic Frame C Single", CustomMaterial.BED_GENERIC_3_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 430, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_3_DOUBLE(new BedAddition("Generic Frame C Double", CustomMaterial.BED_GENERIC_3_DOUBLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 255, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_4_SINGLE(new BedAddition("Generic Frame D Single", CustomMaterial.BED_GENERIC_4_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 510, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_4_DOUBLE(new BedAddition("Generic Frame D Double", CustomMaterial.BED_GENERIC_4_DOUBLE, AdditionType.FRAME, ColorableType.STAIN)),


	//	Potions
	@TypeConfig(price = 45, tabs = Tab.POTIONS)
	POTION_FILLED_TINY_1(new DyeableFloorThing("Tiny Potions 1", CustomMaterial.POTION_FILLED_TINY_1, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.POTIONS)
	POTION_FILLED_TINY_2(new DyeableFloorThing("Tiny Potions 2", CustomMaterial.POTION_FILLED_TINY_2, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_1(new DyeableFloorThing("Small Potion 1", CustomMaterial.POTION_FILLED_SMALL_1, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_2(new DyeableFloorThing("Small Potion 2", CustomMaterial.POTION_FILLED_SMALL_2, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_3(new DyeableFloorThing("Small Potion 3", CustomMaterial.POTION_FILLED_SMALL_3, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_MEDIUM_1(new DyeableFloorThing("Medium Potion 1", CustomMaterial.POTION_FILLED_MEDIUM_1, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_MEDIUM_2(new DyeableFloorThing("Medium Potion 2", CustomMaterial.POTION_FILLED_MEDIUM_2, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_WIDE(new DyeableFloorThing("Wide Potion", CustomMaterial.POTION_FILLED_WIDE, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_SKINNY(new DyeableFloorThing("Skinny Potion", CustomMaterial.POTION_FILLED_SKINNY, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_TALL(new DyeableFloorThing("Tall Potion", CustomMaterial.POTION_FILLED_TALL, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_BOTTLE(new DyeableFloorThing("Big Potion Bottle", CustomMaterial.POTION_FILLED_BIG_BOTTLE, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_TEAR(new DyeableFloorThing("Big Potion Tear", CustomMaterial.POTION_FILLED_BIG_TEAR, ColorableType.DYE)),

	@TypeConfig(price = 120, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_DONUT(new DyeableFloorThing("Big Potion Donut", CustomMaterial.POTION_FILLED_BIG_DONUT, ColorableType.DYE)),

	@TypeConfig(price = 120, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_SKULL(new DyeableFloorThing("Big Potion Skull", CustomMaterial.POTION_FILLED_BIG_SKULL, ColorableType.DYE)),

	@TypeConfig(price = 65, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_SMALL(new DyeableFloorThing("Small Potions", CustomMaterial.POTION_FILLED_GROUP_SMALL, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_MEDIUM(new DyeableFloorThing("Medium Potions", CustomMaterial.POTION_FILLED_GROUP_MEDIUM, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_TALL(new DyeableFloorThing("Tall Potions", CustomMaterial.POTION_FILLED_GROUP_TALL, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_1(new DyeableFloorThing("Random Potions 1", CustomMaterial.POTION_FILLED_GROUP_RANDOM_1, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_2(new DyeableFloorThing("Random Potions 2", CustomMaterial.POTION_FILLED_GROUP_RANDOM_2, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_3(new DyeableFloorThing("Random Potions 3", CustomMaterial.POTION_FILLED_GROUP_RANDOM_3, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_4(new DyeableFloorThing("Random Potions 4", CustomMaterial.POTION_FILLED_GROUP_RANDOM_4, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_5(new DyeableFloorThing("Random Potions 5", CustomMaterial.POTION_FILLED_GROUP_RANDOM_5, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_6(new DyeableFloorThing("Random Potions 6", CustomMaterial.POTION_FILLED_GROUP_RANDOM_6, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_7(new DyeableFloorThing("Random Potions 7", CustomMaterial.POTION_FILLED_GROUP_RANDOM_7, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_8(new DyeableFloorThing("Random Potions 8", CustomMaterial.POTION_FILLED_GROUP_RANDOM_8, ColorableType.DYE)),

	@TypeConfig(price = 30, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_1(new DyeableFloorThing("Empty Small Potion 1", CustomMaterial.POTION_EMPTY_SMALL_1, ColorableType.DYE)),

	@TypeConfig(price = 30, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_2(new DyeableFloorThing("Empty Small Potion 2", CustomMaterial.POTION_EMPTY_SMALL_2, ColorableType.DYE)),

	@TypeConfig(price = 30, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_3(new DyeableFloorThing("Empty Small Potion 3", CustomMaterial.POTION_EMPTY_SMALL_3, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.POTIONS)
	POTION_EMPTY_MEDIUM_1(new DyeableFloorThing("Empty Medium Potion 1", CustomMaterial.POTION_EMPTY_MEDIUM_1, ColorableType.DYE)),

	@TypeConfig(price = 54, tabs = Tab.POTIONS)
	POTION_EMPTY_MEDIUM_2(new DyeableFloorThing("Empty Medium Potion 2", CustomMaterial.POTION_EMPTY_MEDIUM_2, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_WIDE(new DyeableFloorThing("Empty Wide Potion", CustomMaterial.POTION_EMPTY_WIDE, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_SKINNY(new DyeableFloorThing("Empty Skinny Potion", CustomMaterial.POTION_EMPTY_SKINNY, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_TALL(new DyeableFloorThing("Empty Tall Potion", CustomMaterial.POTION_EMPTY_TALL, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_BOTTLE(new DyeableFloorThing("Empty Big Potion Bottle", CustomMaterial.POTION_EMPTY_BIG_BOTTLE, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_TEAR(new DyeableFloorThing("Empty Big Potion Tear", CustomMaterial.POTION_EMPTY_BIG_TEAR, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_DONUT(new DyeableFloorThing("Empty Big Potion Donut", CustomMaterial.POTION_EMPTY_BIG_DONUT, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_SKULL(new DyeableFloorThing("Empty Big Potion Skull", CustomMaterial.POTION_EMPTY_BIG_SKULL, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_SMALL(new DyeableFloorThing("Empty Small Potions", CustomMaterial.POTION_EMPTY_GROUP_SMALL, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_MEDIUM(new DyeableFloorThing("Empty Medium Potions", CustomMaterial.POTION_EMPTY_GROUP_MEDIUM, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_TALL(new DyeableFloorThing("Empty Tall Potions", CustomMaterial.POTION_EMPTY_GROUP_TALL, ColorableType.DYE)),


	// Balloons
	@TypeConfig(price = 90)
	BALLOON_SHORT(new DyeableFloorThing("Balloon Short", CustomMaterial.BALLOON_SHORT, ColorableType.DYE)),

	@TypeConfig(price = 105)
	BALLOON_MEDIUM(new DyeableFloorThing("Balloon Medium", CustomMaterial.BALLOON_MEDIUM, ColorableType.DYE)),

	@TypeConfig(price = 120)
	BALLOON_TALL(new DyeableFloorThing("Balloon Tall ", CustomMaterial.BALLOON_TALL, ColorableType.DYE)),


	//	Misc
	@TypeConfig(price = 15)
	INKWELL(new FloorThing("Inkwell", CustomMaterial.INKWELL)),

	@TypeConfig(price = 75)
	WHEEL_SMALL(new DecorationConfig("Small Wheel", CustomMaterial.WHEEL_SMALL)),

	@TypeConfig(price = 150)
	TELESCOPE(new FloorThing("Telescope", CustomMaterial.TELESCOPE)),

	@TypeConfig(price = 75)
	MICROSCOPE(new FloorThing("Microscope", CustomMaterial.MICROSCOPE)),

	@TypeConfig(price = 75)
	MICROSCOPE_WITH_GEM(new FloorThing("Microscope With Gem", CustomMaterial.MICROSCOPE_WITH_GEM)),

	@TypeConfig(price = 135)
	HELM(new DecorationConfig("Helm", CustomMaterial.HELM)),

	@TypeConfig(price = 60)
	TRAFFIC_BLOCKADE(new FloorThing("Traffic Blockade", CustomMaterial.TRAFFIC_BLOCKADE, Basic._1x1)),

	@TypeConfig(price = 75)
	TRAFFIC_BLOCKADE_LIGHTS(new FloorThing("Traffic Blockade with Lights", CustomMaterial.TRAFFIC_BLOCKADE_LIGHTS, Basic._1x1)),

	@TypeConfig(price = 60)
	TRAFFIC_CONE(new FloorThing("Traffic Cone", CustomMaterial.TRAFFIC_CONE, Basic._1x1)),

	@TypeConfig(price = 150)
	POSTBOX(new FloorThing("Postbox", CustomMaterial.POSTBOX, FloorShape._1x2V)),

	@TypeConfig(price = 90)
	MAILBOX(new DyeableFloorThing("Mailbox", CustomMaterial.MAILBOX, ColorableType.DYE, "C7C7C7", FloorShape._1x2V)),

	@TypeConfig(price = 60)
	SANDWICH_SIGN(new FloorThing("Sandwich Sign", CustomMaterial.SANDWICH_SIGN)),

	@TypeConfig(price = 75)
	SANDWICH_SIGN_TALL(new FloorThing("Sandwich Sign Tall", CustomMaterial.SANDWICH_SIGN_TALL)),

	@TypeConfig(price = 60)
	FIRE_HYDRANT(new FloorThing("Fire Hydrant", CustomMaterial.FIRE_HYDRANT)),

//	@TypeConfig(tabs = Tab.INTERNAL)
//	WAYSTONE(new FloorThing("Waystone", CustomMaterial.WAYSTONE)),
//
//	@TypeConfig(tabs = Tab.INTERNAL)
//	WAYSTONE_ACTIVATED(new FloorThing("Waystone Activated", CustomMaterial.WAYSTONE_ACTIVATED)),

	@TypeConfig(price = 90)
	ROTARY_PHONE(new FloorThing("Rotary Phone", CustomMaterial.ROTARY_PHONE)), // TODO: DYEABLE

	@TypeConfig(price = 90)
	LAPTOP(new FloorThing("Laptop", CustomMaterial.LAPTOP)), // TODO: DYEABLE

	@TypeConfig(price = 90)
	ROUTER(new FloorThing("Router", CustomMaterial.ROUTER)),

	@TypeConfig(price = 90)
	REGISTER_MODERN(new FloorThing("Modern Register", CustomMaterial.REGISTER_MODERN)),

	// TESTING
	@TypeConfig(tabs = Tab.INTERNAL)
	TEST(new TestThing("Test Thing", CustomMaterial.WAYSTONE_ACTIVATED)),
	;
	// @formatter:on

	@Getter
	private final DecorationConfig config;

	@SneakyThrows
	public TypeConfig getTypeConfig() {
		return this.getClass().getField(this.name()).getAnnotation(TypeConfig.class);
	}

	public static void initDecorations() {
		// Init all decoration creators
		TrophyType.initDecorations();
		Pose.initDecorations();
		Backpacks.BackpackTier.initDecoration();
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
