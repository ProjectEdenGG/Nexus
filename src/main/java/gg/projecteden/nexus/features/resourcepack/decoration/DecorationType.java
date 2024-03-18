package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
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
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag.PrideFlagType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flora;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Furniture;
import gg.projecteden.nexus.features.resourcepack.decoration.types.HangingBanner;
import gg.projecteden.nexus.features.resourcepack.decoration.types.StandingBanner;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.BirdHouse;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime.WindChimeType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.DyeableInstrument;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch.CouchPart;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.DyeableChair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.LongChair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Stump;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.BedAddition;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.BedAddition.AdditionType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Curtain;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Curtain.CurtainType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.TrashCan;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Waystone;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.WorkBench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.Block;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.CeilingThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
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
		- Breaking a decoration w/ no hitbox while in creative produces no break sound
		- BedAdditions not glowing & detecting properly in the store
		- CreativePickBlock isn't perfect

	TODO AFTER RELEASE:
		- Cleanup DecorationInteractionData & Decoration duplicate checks (such as "canEdit")
		- Bed Additions (Canopy)
		- Rework shelves to being light-hitbox-based, barrier-hitboxes don't work properly, and I can't figure them out
		- Add:
			- Remaining decorations:
					- toAdd
					- Dog House
					- Hoots:
						- Construction Cones -> color FF7F00
			- Add some Tickable Decorations
			- Hot Swap Kitchen Handles -> Sell handles at general store/carpenter?
			- Allow player to create their own presets in DyeStationMenu
			- Better support for:
				- Multi-Surface models -> birdhouses, banners
				- Multi-Block ceiling things
			- Inventory support (cabinets = chests, ovens = furnaces, etc)
			- Mob plushies
		- Ideas:
			- Redstone activate instrument?
			- Mailbox change model if have mail or not
 */

// @formatter:off
@AllArgsConstructor
public enum DecorationType {
// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Holiday
// 	------------------------------------------------------------------------------------------------------
	@TypeConfig(price = 550, theme = Theme.HOLIDAY)
	FIREPLACE_DARK_XMAS(new Fireplace(true, "Dark Christmas Fireplace", CustomMaterial.FIREPLACE_DARK_XMAS)),

	@TypeConfig(price = 550, theme = Theme.HOLIDAY)
	FIREPLACE_BROWN_XMAS(new Fireplace(true, "Brown Christmas Fireplace", CustomMaterial.FIREPLACE_BROWN_XMAS)),

	@TypeConfig(price = 550, theme = Theme.HOLIDAY)
	FIREPLACE_LIGHT_XMAS(new Fireplace(true, "Light Christmas Fireplace", CustomMaterial.FIREPLACE_LIGHT_XMAS)),

	@TypeConfig(price = 150, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_COLOR(new FloorThing(false, "Colorful Christmas Tree", CustomMaterial.CHRISTMAS_TREE_COLORED, HitboxFloor._1x2V)),

	@TypeConfig(price = 150, theme = Theme.HOLIDAY)
	CHRISTMAS_TREE_WHITE(new FloorThing(false, "White Christmas Tree", CustomMaterial.CHRISTMAS_TREE_WHITE, HitboxFloor._1x2V)),

	@TypeConfig(price = 45, theme = Theme.HOLIDAY)
	MISTLETOE(new CeilingThing(false, "Mistletoe", CustomMaterial.MISTLETOE)),

	@TypeConfig(price = 75, theme = Theme.HOLIDAY)
	WREATH(new WallThing(false, "Wreath", CustomMaterial.WREATH)),

	@TypeConfig(price = 30, theme = Theme.HOLIDAY)
	STOCKINGS_SINGLE(new WallThing(false, "Single Stocking", CustomMaterial.STOCKINGS_SINGLE)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	STOCKINGS_DOUBLE(new WallThing(false, "Double Stocking", CustomMaterial.STOCKINGS_DOUBLE)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_HAPPY_HOLIDAYS(new Bunting(true, "Happy Holidays Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_HOLIDAYS, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_HAPPY_NEW_YEAR(new Bunting(true, "Happy New Year Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_NEW_YEAR, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	BUNTING_PHRASE_MERRY_CHRISTMAS(new Bunting(true, "Merry Christmas Bunting", CustomMaterial.BUNTING_PHRASE_MERRY_CHRISTMAS, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(price = 300, theme = Theme.HOLIDAY)
	SNOWMAN_PLAIN(new FloorThing(false, "Plain Snowman", CustomMaterial.SNOWMAN_PLAIN, HitboxFloor._1x2V)),

	@TypeConfig(price = 375, theme = Theme.HOLIDAY)
	SNOWMAN_FANCY(new FloorThing(false, "Fancy Snowman", CustomMaterial.SNOWMAN_FANCY, HitboxFloor._1x2V)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	SNOWBALLS_SMALL(new FloorThing(false, "Small Pile of Snowballs", CustomMaterial.SNOWBALLS_SMALL)),

	@TypeConfig(price = 105, theme = Theme.HOLIDAY)
	SNOWBALLS_BIG(new FloorThing(false, "Big Pile of Snowballs", CustomMaterial.SNOWBALLS_BIG)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_CENTER(new WallThing(false, "Icicle Lights - Center", CustomMaterial.ICICLE_LIGHT_CENTER)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_LEFT(new WallThing(false, "Icicle Lights - Left", CustomMaterial.ICICLE_LIGHT_LEFT)),

	@TypeConfig(price = 60, theme = Theme.HOLIDAY)
	ICICLE_LIGHT_RIGHT(new WallThing(false, "Icicle Lights - Right", CustomMaterial.ICICLE_LIGHT_RIGHT)),

	@TypeConfig(price = 90, theme = Theme.HOLIDAY)
	ICICLE_SMALL(new CeilingThing(false, "Small Icicle", CustomMaterial.ICICLE_SMALL)),

	@TypeConfig(price = 150, theme = Theme.HOLIDAY)
	ICICLE_LARGE(new CeilingThing(false, "Large Icicle", CustomMaterial.ICICLE_LARGE, HitboxSingle._1x1)),

	@TypeConfig(price = 185, theme = Theme.HOLIDAY)
	ICICLE_MULTI(new CeilingThing(false, "Pair of Icicles", CustomMaterial.ICICLE_MULTI, HitboxSingle._1x1)),

	@TypeConfig(price = 300, theme = Theme.HOLIDAY)
	GIANT_CANDY_CANE(new DyeableFloorThing(false, "Giant Candy Cane", CustomMaterial.GIANT_CANDY_CANE, ColorableType.DYE, HitboxUnique.GIANT_CANDY_CANE)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Spooky
// 	------------------------------------------------------------------------------------------------------
	@TypeConfig(price = 75, theme = Theme.SPOOKY)
	GRAVESTONE_SMALL(new FloorThing(false, "Small Gravestone", CustomMaterial.GRAVESTONE_SMALL)),

	@TypeConfig(price = 150, theme = Theme.SPOOKY)
	GRAVESTONE_CROSS(new FloorThing(false, "Gravestone Cross", CustomMaterial.GRAVESTONE_CROSS, HitboxSingle._1x1_CHAIN)),

	@TypeConfig(price = 75, theme = Theme.SPOOKY)
	GRAVESTONE_PLAQUE(new FloorThing(false, "Gravestone Plaque", CustomMaterial.GRAVESTONE_PLAQUE)),

	@TypeConfig(price = 150, theme = Theme.SPOOKY)
	GRAVESTONE_STACK(new FloorThing(false, "Rock Stack Gravestone", CustomMaterial.GRAVESTONE_STACK)),

	@TypeConfig(price = 225, theme = Theme.SPOOKY)
	GRAVESTONE_FLOWERBED(new FloorThing(false, "Flowerbed Gravestone", CustomMaterial.GRAVESTONE_FLOWERBED)),

	@TypeConfig(price = 225, theme = Theme.SPOOKY)
	GRAVESTONE_TALL(new FloorThing(false, "Tall Gravestone", CustomMaterial.GRAVESTONE_TALL, HitboxUnique.GRAVESTONE_TALL)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Music
// 	------------------------------------------------------------------------------------------------------
	// - Noisemakers
	@TypeConfig(price = 1500, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	DRUM_KIT(new DyeableInstrument(true, "Drum Kit", CustomMaterial.DRUM_KIT, InstrumentSound.DRUM_KIT, ColorableType.DYE, HitboxUnique.DRUM_KIT, PlacementType.FLOOR)),

	@TypeConfig(price = 2250, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_GRAND(new DyeableInstrument(true, "Grand Piano", CustomMaterial.PIANO_GRAND, InstrumentSound.GRAND_PIANO, ColorableType.STAIN, HitboxUnique.PIANO_GRAND, PlacementType.FLOOR)),

	@TypeConfig(price = 750, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_KEYBOARD(new DyeableInstrument(true, "Keyboard", CustomMaterial.PIANO_KEYBOARD, InstrumentSound.PIANO, ColorableType.DYE, HitboxFloor._1x2H_LIGHT, PlacementType.FLOOR)),

	@TypeConfig(price = 900, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	PIANO_KEYBOARD_ON_STAND(new DyeableInstrument(true, "Keyboard On Stand", CustomMaterial.PIANO_KEYBOARD_ON_STAND, InstrumentSound.PIANO, ColorableType.DYE, HitboxFloor._1x2H, PlacementType.FLOOR)),

	@TypeConfig(price = 1050, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	HARP(new Instrument(false, "Harp", CustomMaterial.HARP, InstrumentSound.HARP, HitboxFloor._1x2V, PlacementType.FLOOR)),

	@TypeConfig(price = 900, theme = Theme.MUSIC, tabs = Tab.MUSIC_NOISEMAKERS)
	BONGOS(new DyeableInstrument(true, "Bongos", CustomMaterial.BONGOS, InstrumentSound.BONGOS, ColorableType.DYE, HitboxFloor._1x2H, PlacementType.FLOOR)),

	@TypeConfig(price = 675, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC(new DyeableFloorThing(false, "Acoustic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC, ColorableType.STAIN)),

	@TypeConfig(price = 675, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_WALL(new DyeableWallThing(false, "Wall Mounted Acoustic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_WALL, ColorableType.STAIN, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(price = 750, theme = Theme.MUSIC)
	GUITAR_ELECTRIC(new DyeableFloorThing(false, "Electric Guitar Display", CustomMaterial.GUITAR_ELECTRIC, ColorableType.DYE)),

	@TypeConfig(price = 750, theme = Theme.MUSIC)
	GUITAR_ELECTRIC_WALL(new DyeableWallThing(false, "Wall Mounted Electric Guitar Display", CustomMaterial.GUITAR_ELECTRIC_WALL, ColorableType.DYE, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(price = 600, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC(new FloorThing(false, "Acoustic Classic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_CLASSIC)),

	@TypeConfig(price = 600, theme = Theme.MUSIC)
	GUITAR_ACOUSTIC_CLASSIC_WALL(new WallThing(false, "Wall Mounted Acoustic Classic Guitar Display", CustomMaterial.GUITAR_ACOUSTIC_CLASSIC_WALL, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	TRUMPET(new FloorThing(false, "Trumpet Display", CustomMaterial.TRUMPET)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	SAXOPHONE(new FloorThing(false, "Saxophone Display", CustomMaterial.SAXOPHONE)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	VIOLIN(new FloorThing(false, "Violin Display", CustomMaterial.VIOLIN)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	VIOLIN_WALL(new WallThing(false, "Wall Mounted Violin Display", CustomMaterial.VIOLIN_WALL, HitboxFloor._1x2V_LIGHT_DOWN)),

	@TypeConfig(price = 750, theme = Theme.MUSIC)
	CELLO(new FloorThing(false, "Cello Display", CustomMaterial.CELLO)),

	@TypeConfig(price = 165, theme = Theme.MUSIC)
	DRUM_THRONE(new DyeableChair(false, true, "Drum Throne", CustomMaterial.DRUM_THRONE, ColorableType.DYE, 1.35)),

	@TypeConfig(price = 180, theme = Theme.MUSIC)
	PIANO_BENCH(new Bench(true, true, "Piano Bench", CustomMaterial.PIANO_BENCH, ColorableType.STAIN, 1.15, HitboxFloor._1x2H)),

	@TypeConfig(price = 210, theme = Theme.MUSIC)
	PIANO_BENCH_GRAND(new Bench(true, true, "Grand Piano Bench", CustomMaterial.PIANO_BENCH_GRAND, ColorableType.STAIN, 1.15, HitboxFloor._1x3H)),

	@TypeConfig(price = 225, theme = Theme.MUSIC)
	AMPLIFIER(new FloorThing(false, "Amplifier", CustomMaterial.AMPLIFIER, HitboxSingle._1x1)),

	@TypeConfig(price = 105, theme = Theme.MUSIC)
	GOLDEN_RECORD(new WallThing(false, "Golden Record", CustomMaterial.GOLDEN_RECORD)),

	@TypeConfig(price = 300, theme = Theme.MUSIC)
	SPEAKER_LARGE(new FloorThing(false, "Large Speaker", CustomMaterial.SPEAKER_LARGE, HitboxFloor._1x2V)),

	@TypeConfig(price = 150, theme = Theme.MUSIC)
	SPEAKER_SMALL(new FloorThing(false, "Small Speaker", CustomMaterial.SPEAKER_SMALL, HitboxSingle._1x1)),

	@TypeConfig(price = 135, theme = Theme.MUSIC)
	LAUNCHPAD(new FloorThing(false, "Launchpad", CustomMaterial.LAUNCHPAD)),

	@TypeConfig(price = 150, theme = Theme.MUSIC)
	MICROPHONE(new FloorThing(false, "Microphone", CustomMaterial.MICROPHONE)),

	@TypeConfig(price = 195, theme = Theme.MUSIC)
	MICROPHONE_WITH_BOOM_STAND(new FloorThing(false, "Microphone With Boom Stand", CustomMaterial.MICROPHONE_WITH_BOOM_STAND)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	MIXING_CONSOLE(new FloorThing(true, "Mixing Console", CustomMaterial.MIXING_CONSOLE, HitboxFloor._1x2H_LIGHT)),

	@TypeConfig(price = 450, theme = Theme.MUSIC)
	LIGHT_BOARD(new FloorThing(true, "Light Board", CustomMaterial.LIGHT_BOARD, HitboxFloor._1x2H_LIGHT)),

	@TypeConfig(price = 375, theme = Theme.MUSIC)
	SPEAKER_WOODEN_LARGE(new DyeableFloorThing(false, "Large Wooden Speaker", CustomMaterial.SPEAKER_WOODEN_LARGE, ColorableType.STAIN, HitboxFloor._1x2V)),

	@TypeConfig(price = 225, theme = Theme.MUSIC)
	SPEAKER_WOODEN_SMALL(new DyeableFloorThing(false, "Small Wooden Speaker", CustomMaterial.SPEAKER_WOODEN_SMALL, ColorableType.STAIN, HitboxSingle._1x1)),

	@TypeConfig(price = 285, theme = Theme.MUSIC)
	TAPE_MACHINE(new DyeableFloorThing(false, "Tape Machine", CustomMaterial.TAPE_MACHINE, ColorableType.STAIN, HitboxSingle._1x1)),

	@TypeConfig(price = 525, theme = Theme.MUSIC)
	DJ_TURNTABLE(new DyeableFloorThing(true, "DJ Turntable", CustomMaterial.DJ_TURNTABLE, ColorableType.DYE, HitboxFloor._1x3H_LIGHT)),

	@TypeConfig(price = 150, theme = Theme.MUSIC)
	RECORD_PLAYER_MODERN(new DyeableFloorThing(false, "Modern Record Player - Off", CustomMaterial.RECORD_PLAYER_MODERN, ColorableType.STAIN, HitboxSingle._1x1)),

	@TypeConfig(price = 165, theme = Theme.MUSIC)
	RECORD_PLAYER_MODERN_ON(new DyeableFloorThing(false, "Modern Record Player - On", CustomMaterial.RECORD_PLAYER_MODERN_ON, ColorableType.STAIN, HitboxSingle._1x1)),

	@TypeConfig(price = 300, theme = Theme.MUSIC)
	STUDIO_LIGHT_HANGING(new CeilingThing(false, "Hanging Studio Lights", CustomMaterial.STUDIO_LIGHTS_HANGING)),

	@TypeConfig(price = 225, theme = Theme.MUSIC)
	STUDIO_LIGHT_STANDING(new FloorThing(false, "Standing Studio Light", CustomMaterial.STUDIO_LIGHTS_STANDING, HitboxFloor._1x2V)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Pride
// 	------------------------------------------------------------------------------------------------------
	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_ACE(new Flag("Asexual Pride Flag", PrideFlagType.ACE)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_AGENDER(new Flag("Agender Pride Flag", PrideFlagType.AGENDER)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_ARO(new Flag("Aromatic Pride Flag", PrideFlagType.ARO)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_BI(new Flag("Bisexual Pride Flag", PrideFlagType.BI)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMI(new Flag("Demisexual Pride Flag", PrideFlagType.DEMI)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIBOY(new Flag("Demisexual Boy Pride Flag", PrideFlagType.DEMIBOY)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIGIRL(new Flag("Demisexual Girl Pride Flag", PrideFlagType.DEMIGIRL)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_DEMIROMANTIC(new Flag("Demiromantic Pride Flag", PrideFlagType.DEMIROMANTIC)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GAY(new Flag("Gay Pride Flag", PrideFlagType.GAY)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENDERFLUID(new Flag("Genderfluid Pride Flag", PrideFlagType.GENDERFLUID)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENDERFLUX(new Flag("Genderflux Pride Flag", PrideFlagType.GENDERFLUX)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GENQUEER(new Flag("Genderqueer Pride Flag", PrideFlagType.GENDERQUEER)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GRAYACE(new Flag("Gray-Asexual Pride Flag", PrideFlagType.GRAY_ACE)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_GRAYARO(new Flag("Gray-Aromatic Pride Flag", PrideFlagType.GRAY_ARO)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_INTERSEX(new Flag("Intersex Pride Flag", PrideFlagType.INTERSEX)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_LESBIAN(new Flag("Lesbian Pride Flag", PrideFlagType.LESBIAN)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_NONBINARY(new Flag("Nonbinary Pride Flag", PrideFlagType.NONBINARY)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_PAN(new Flag("Pansexual Pride Flag", PrideFlagType.PAN)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_POLYAM(new Flag("Polyamorous Pride Flag", PrideFlagType.POLYAM)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_POLYSEX(new Flag("Polysexual Pride Flag", PrideFlagType.POLYSEX)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANS(new Flag("Transgender Pride Flag", PrideFlagType.TRANS)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANSFEM(new Flag("Transfeminine Pride Flag", PrideFlagType.TRANSFEM)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_TRANSMASC(new Flag("Transmasculine Pride Flag", PrideFlagType.TRANSMASC)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_QUEER(new Flag("Queer Pride Flag", PrideFlagType.QUEER)),

	@TypeConfig(price = 75, theme = Theme.PRIDE, tabs = Tab.PRIDE_FLAGS)
	FLAG_PRIDE_PRIDE(new Flag("Queer Pride Flag", PrideFlagType.PRIDE)),

	// Pride Bunting
	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_ACE(new Bunting("Asexual Pride Bunting", PrideFlagType.ACE)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_AGENDER(new Bunting("Agender Pride Bunting", PrideFlagType.AGENDER)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_ARO(new Bunting("Aromatic Pride Bunting", PrideFlagType.ARO)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_BI(new Bunting("Bisexual Pride Bunting", PrideFlagType.BI)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMI(new Bunting("Demisexual Pride Bunting", PrideFlagType.DEMI)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIBOY(new Bunting("Demisexual Boy Pride Bunting", PrideFlagType.DEMIBOY)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIGIRL(new Bunting("Demisexual Girl Pride Bunting", PrideFlagType.DEMIGIRL)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_DEMIROMANTIC(new Bunting("Demiromantic Pride Bunting", PrideFlagType.DEMIROMANTIC)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GAY(new Bunting("Gay Pride Bunting", PrideFlagType.GAY)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENDERFLU(new Bunting("Genderfluid Pride Bunting", PrideFlagType.GENDERFLUID)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENDERFLUX(new Bunting("Genderflux Pride Bunting", PrideFlagType.GENDERFLUX)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GENQUEER(new Bunting("Genderqueer Pride Bunting", PrideFlagType.GENDERQUEER)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GRAYACE(new Bunting("Gray-Asexual Pride Bunting", PrideFlagType.GRAY_ACE)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_GRAYARO(new Bunting("Gray-Aromatic Pride Bunting", PrideFlagType.GRAY_ARO)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_INTERSEX(new Bunting("Intersex Pride Bunting", PrideFlagType.INTERSEX)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_LESBIAN(new Bunting("Lesbian Pride Bunting", PrideFlagType.LESBIAN)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_NONBINARY(new Bunting("Nonbinary Pride Bunting", PrideFlagType.NONBINARY)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_PAN(new Bunting("Pansexual Pride Bunting", PrideFlagType.PAN)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_POLYAM(new Bunting("Polyamorous Pride Bunting", PrideFlagType.POLYAM)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_POLYSEX(new Bunting("Polysexual Pride Bunting", PrideFlagType.POLYSEX)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANS(new Bunting("Transgender Pride Bunting", PrideFlagType.TRANS)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANSFEM(new Bunting("Transfeminine Pride Bunting", PrideFlagType.TRANSFEM)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_TRANSMASC(new Bunting("Transmasculine Pride Bunting", PrideFlagType.TRANSMASC)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_QUEER(new Bunting("Queer Pride Bunting", PrideFlagType.QUEER)),

	@TypeConfig(price = 45, theme = Theme.PRIDE, tabs = Tab.PRIDE_BUNTING)
	BUNTING_PRIDE_PRIDE(new Bunting("Queer Pride Bunting", PrideFlagType.PRIDE)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Outdoors
// 	------------------------------------------------------------------------------------------------------
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

	@TypeConfig(price = 150, theme = Theme.OUTDOORS)
	BIRDHOUSE_ENCHANTED_HORIZONTAL(new BirdHouse("Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HORIZONTAL, true)),

	@TypeConfig(price = 150, theme = Theme.OUTDOORS)
	BIRDHOUSE_DEPTHS_HORIZONTAL(new BirdHouse("Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HORIZONTAL, true)),

	// Flora
	@TypeConfig(price = 120, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BUSHY_PLANT(new Flora(false, "Bushy Plant", CustomMaterial.FLORA_BUSHY_PLANT, HitboxSingle.NONE, PlacementType.FLOOR)),

	@TypeConfig(price = 225, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_CHERRY_TREE(new Flora(false, "Potted Cherry Tree", CustomMaterial.FLORA_POTTED_CHERRY_TREE, HitboxSingle._1x1_HEAD, PlacementType.FLOOR)),

	@TypeConfig(price = 165, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_BAY_TREE(new Flora(false, "Potted Bay Tree", CustomMaterial.FLORA_POTTED_BAY_TREE, HitboxFloor._1x2V, PlacementType.FLOOR)),

	@TypeConfig(price = 135, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_SNAKE_PLANT(new Flora(false, "Snake Plant", CustomMaterial.FLORA_SNAKE_PLANT, PlacementType.FLOOR)),

	@TypeConfig(price = 135, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_WHITE_BIRD_PARADISE(new Flora(false, "White Bird of Paradise", CustomMaterial.FLORA_WHITE_BIRD_PARADISE, PlacementType.FLOOR)),

	@TypeConfig(price = 210, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI(new Flora(false, "Bonsai", CustomMaterial.FLORA_BONSAI, PlacementType.FLOOR)),

	@TypeConfig(price = 210, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_CHERRY(new Flora(false, "Cherry Bonsai", CustomMaterial.FLORA_BONSAI_CHERRY, PlacementType.FLOOR)),

	@TypeConfig(price = 140, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_SMALL(new Flora(false, "Small Bonsai", CustomMaterial.FLORA_BONSAI_SMALL, PlacementType.FLOOR)),

	@TypeConfig(price = 140, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_BONSAI_CHERRY_SMALL(new Flora(false, "Small Cherry Bonsai", CustomMaterial.FLORA_BONSAI_CHERRY_SMALL, PlacementType.FLOOR)),

	@TypeConfig(price = 75, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_CHINESE_EVERGREEN(new Flora(false, "Chinese Evergreen", CustomMaterial.FLORA_CHINESE_EVERGREEN, PlacementType.FLOOR)),

	@TypeConfig(price = 135, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_FLOWER_VASE(new Flora(false, "Flower Vase", CustomMaterial.FLORA_FLOWER_VASE, PlacementType.FLOOR)),

	@TypeConfig(price = 105, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_WALL_FLOWERS_1(new Flora(false, "Wall Flowers", CustomMaterial.FLORA_WALL_FLOWERS_1, PlacementType.WALL)),

	@TypeConfig(price = 95, theme = Theme.OUTDOORS, tabs = Tab.FLORA)
	FLORA_POTTED_TULIPS(new Flora(false, "Potted Tulips", CustomMaterial.FLORA_POTTED_TULIPS, HitboxSingle._1x1_HEAD, PlacementType.WALL)),

	// Misc
	@TypeConfig(price = 115, theme = Theme.OUTDOORS)
	BED_SLEEPING_BAG(new DyeableFloorThing(false, "Sleeping Bag", CustomMaterial.BED_SLEEPING_BAG, ColorableType.DYE)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: Art
// 	------------------------------------------------------------------------------------------------------
	//	Custom
	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CHERRY_FOREST(new Art("Komorebi", CustomMaterial.ART_PAINTING_CUSTOM_CHERRY_FOREST, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_END_ISLAND(new Art("Limbo", CustomMaterial.ART_PAINTING_CUSTOM_END_ISLAND, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_LOST_ENDERMAN(new Art("Lost Enderman", CustomMaterial.ART_PAINTING_CUSTOM_LOST_ENDERMAN, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PINE_TREE(new Art("Black Hills", CustomMaterial.ART_PAINTING_CUSTOM_PINE_TREE, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SUNSET(new Art("Palm Cove", CustomMaterial.ART_PAINTING_CUSTOM_SUNSET, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SWAMP_HUT(new Art("Isolation", CustomMaterial.ART_PAINTING_CUSTOM_SWAMP_HUT, HitboxWall._1x2V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_MOUNTAINS(new Art("Three Peaks", CustomMaterial.ART_PAINTING_CUSTOM_MOUNTAINS, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_MUDDY_PIG(new Art("Blissful Piggy", CustomMaterial.ART_PAINTING_CUSTOM_MUDDY_PIG, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PURPLE_SHEEP(new Art("Lavender Woolly", CustomMaterial.ART_PAINTING_CUSTOM_PURPLE_SHEEP, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_VILLAGE_HAPPY(new Art("Sweet Home", CustomMaterial.ART_PAINTING_CUSTOM_VILLAGE_HAPPY, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_VILLAGE_CHAOS(new Art("Revenge", CustomMaterial.ART_PAINTING_CUSTOM_VILLAGE_CHAOS, HitboxWall._1x2H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SKYBLOCK(new Art("Skyblock", CustomMaterial.ART_PAINTING_CUSTOM_SKYBLOCK, HitboxWall._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_FORTRESS_BRIDGE(new Art("Nether Highways", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_FORTRESS_BRIDGE, HitboxWall._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_CRIMSON_FOREST(new Art("Crimson Canopy", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_CRIMSON_FOREST, HitboxWall._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_WARPED_FOREST(new Art("Warped Woods", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_WARPED_FOREST, HitboxWall._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_BASALT_DELTAS(new Art("Basalt Summits", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_BASALT_DELTAS, HitboxWall._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_NETHER_SOUL_SAND_VALLEY(new Art("Lost Souls", CustomMaterial.ART_PAINTING_CUSTOM_NETHER_SOUL_SAND_VALLEY, HitboxWall._1x1_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CASTLE(new Art("Sintra", CustomMaterial.ART_PAINTING_CUSTOM_CASTLE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_LAKE(new Art("Reflections", CustomMaterial.ART_PAINTING_CUSTOM_LAKE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_RIVER(new Art("Flowing Home", CustomMaterial.ART_PAINTING_CUSTOM_RIVER, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_ROAD(new Art("Take Me Home", CustomMaterial.ART_PAINTING_CUSTOM_ROAD, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_ORIENTAL(new Art("Tenku No Torii", CustomMaterial.ART_PAINTING_CUSTOM_ORIENTAL, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CHICKENS(new Art("Hens Night", CustomMaterial.ART_PAINTING_CUSTOM_CHICKENS, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_OAK_TREE(new Art("English Oak", CustomMaterial.ART_PAINTING_CUSTOM_OAK_TREE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CRAB(new Art("Nomad", CustomMaterial.ART_PAINTING_CUSTOM_CRAB, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SATURN_ROCKET(new Art("Adventure Is Out There", CustomMaterial.ART_PAINTING_CUSTOM_SATURN_ROCKET, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_PARROT(new Art("Scarlet Macaw", CustomMaterial.ART_PAINTING_CUSTOM_PARROT, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_DUCKS(new Art("Voyage", CustomMaterial.ART_PAINTING_CUSTOM_DUCKS, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_STARRY_PINE_TREE(new Art("Lone Pine", CustomMaterial.ART_PAINTING_CUSTOM_STARRY_PINE_TREE, HitboxWall._2x2_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 450, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_FOREST(new Art("Misty Thicket", CustomMaterial.ART_PAINTING_CUSTOM_FOREST, HitboxWall._1x3H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 450, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_SAND_DUNES(new Art("Sahara", CustomMaterial.ART_PAINTING_CUSTOM_SAND_DUNES, HitboxWall._1x3V_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 900, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_STORY(new Art("Daydreamer", CustomMaterial.ART_PAINTING_CUSTOM_STORY, HitboxWall._2x3H_LIGHT)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_CUSTOM})
	ART_PAINTING_CUSTOM_CITY_TWILIGHT(new Art("City Twilight", CustomMaterial.ART_PAINTING_CUSTOM_CITY_TWILIGHT, HitboxWall._2x2_LIGHT)),

	// Vanilla
	@TypeConfig(theme = Theme.ART, price = 150, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_FRIEND(new Art("Friend", CustomMaterial.ART_PAINTING_VANILLA_FRIEND, HitboxWall._1x1_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_BELOW(new Art("Below", CustomMaterial.ART_PAINTING_VANILLA_BELOW, HitboxWall._1x2H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_DIRT_HUT_ROAD(new Art("Dirt Hut Road", CustomMaterial.ART_PAINTING_VANILLA_DIRT_HUT_ROAD, HitboxWall._1x2H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VOWS_OF_THE_CRAFTSMAN(new Art("Vows of the Craftsman", CustomMaterial.ART_PAINTING_VANILLA_VOWS_OF_THE_CRAFTSMAN, HitboxWall._1x2H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VILLAGER_AND_CHILD(new Art("Villager and Child", CustomMaterial.ART_PAINTING_VANILLA_VILLAGER_AND_CHILD, HitboxWall._1x2V_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 300, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_THREE_MASON(new Art("Level 3 Mason", CustomMaterial.ART_PAINTING_VANILLA_LEVEL_THREE_MASON, HitboxWall._1x2V_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_ANCIENT_POWER(new Art("Ancient Power", CustomMaterial.ART_PAINTING_VANILLA_ANCIENT_POWER, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_IRON_SEED(new Art("Iron Seed", CustomMaterial.ART_PAINTING_VANILLA_IRON_SEED, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_RIDERS(new Art("Riders", CustomMaterial.ART_PAINTING_VANILLA_RIDERS, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_ONE_MASON(new Art("Level 1 Mason", CustomMaterial.ART_PAINTING_VANILLA_LEVEL_ONE_MASON, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 600, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_LEVEL_TWO_MASON(new Art("Level 2 Mason", CustomMaterial.ART_PAINTING_VANILLA_LEVEL_TWO_MASON, HitboxWall._2x2_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 1200, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_VILLAGER_TRADE(new Art("The Trade in the House of Villagers", CustomMaterial.ART_PAINTING_VANILLA_VILLAGER_TRADE, HitboxWall._2x4H_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 1800, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_SIGNS_OF_THE_END(new Art("Signs of the End", CustomMaterial.ART_PAINTING_VANILLA_SIGNS_OF_THE_END, HitboxWall._4x4_LIGHT, true)),

	@TypeConfig(theme = Theme.ART, price = 1800, tabs = {Tab.ART_VANILLA})
	ART_PAINTING_VANILLA_BLESSED_SHEEP(new Art("Three Saints and the Blessed Sheep", CustomMaterial.ART_PAINTING_VANILLA_BLESSED_SHEEP, HitboxWall._4x4_LIGHT, true)),

// 	------------------------------------------------------------------------------------------------------
//										CATALOG: General
// 	------------------------------------------------------------------------------------------------------
	// 	Tables
	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x1(new Table(false, "Wooden Table - 1x1", CustomMaterial.TABLE_WOODEN_1X1, HitboxSingle._1x1)),

	@TypeConfig(price = 105, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x2(new Table(true, "Wooden Table - 1x2", CustomMaterial.TABLE_WOODEN_1X2, HitboxFloor._1x2H)),

	@TypeConfig(price = 135, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_1x3(new Table(true, "Wooden Table - 1x3", CustomMaterial.TABLE_WOODEN_1X3, HitboxFloor._1x3H)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x2(new Table(true, "Wooden Table - 2x2", CustomMaterial.TABLE_WOODEN_2X2, HitboxFloor._2x2)),

	@TypeConfig(price = 225, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_2x3(new Table(true, "Wooden Table - 2x3", CustomMaterial.TABLE_WOODEN_2X3, HitboxFloor._2x3H)),

	@TypeConfig(price = 300, tabs = {Tab.FURNITURE, Tab.TABLES})
	TABLE_WOODEN_3x3(new Table(true, "Wooden Table - 3x3", CustomMaterial.TABLE_WOODEN_3X3, HitboxFloor._3x3)),

	// 	Chairs
	@TypeConfig(price = 120, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_BASIC(new DyeableChair(false, false, "Wooden Chair", CustomMaterial.CHAIR_WOODEN_BASIC, ColorableType.STAIN)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_WOODEN_CUSHIONED(new DyeableChair(false, false, "Cushioned Wooden Chair", CustomMaterial.CHAIR_WOODEN_CUSHIONED, ColorableType.DYE)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_CLOTH(new DyeableChair(false, false, "Cloth Chair", CustomMaterial.CHAIR_CLOTH, ColorableType.DYE)),

	@TypeConfig(price = 135, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	ADIRONDACK(new DyeableChair(false, false, "Adirondack", CustomMaterial.ADIRONDACK, ColorableType.STAIN)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	CHAIR_BEACH(new LongChair(true, false, "Beach Chair", CustomMaterial.BEACH_CHAIR, ColorableType.DYE, HitboxUnique.BEACH_CHAIR, .875)),

	// 	Stools
	@TypeConfig(price = 90, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_BASIC(new DyeableChair(false, true, "Wooden Stool", CustomMaterial.STOOL_WOODEN_BASIC, ColorableType.STAIN)),

	@TypeConfig(price = 120, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_WOODEN_CUSHIONED(new DyeableChair(false, true, "Cushioned Wooden Stool", CustomMaterial.STOOL_WOODEN_CUSHIONED, ColorableType.DYE)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STOOLS})
	STOOL_BAR_WOODEN(new DyeableChair(false, true, "Wooden Bar Stool", CustomMaterial.STOOL_BAR_WOODEN, ColorableType.STAIN, 1.15)),

	// Stumps
	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_OAK(new Stump(false, "Oak Stump", CustomMaterial.STUMP_OAK)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_OAK_ROOTS(new Stump(false, "Rooted Oak Stump", CustomMaterial.STUMP_OAK_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_SPRUCE(new Stump(false, "Spruce Stump", CustomMaterial.STUMP_SPRUCE)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_SPRUCE_ROOTS(new Stump(false, "Rooted Spruce Stump", CustomMaterial.STUMP_SPRUCE_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_BIRCH(new Stump(false, "Birch Stump", CustomMaterial.STUMP_BIRCH)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_BIRCH_ROOTS(new Stump(false, "Rooted Birch Stump", CustomMaterial.STUMP_BIRCH_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_JUNGLE(new Stump(false, "Jungle Stump", CustomMaterial.STUMP_JUNGLE)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_JUNGLE_ROOTS(new Stump(false, "Rooted Jungle Stump", CustomMaterial.STUMP_JUNGLE_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_ACACIA(new Stump(false, "Acacia Stump", CustomMaterial.STUMP_ACACIA)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_ACACIA_ROOTS(new Stump(false, "Rooted Acacia Stump", CustomMaterial.STUMP_ACACIA_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_DARK_OAK(new Stump(false, "Dark Oak Stump", CustomMaterial.STUMP_DARK_OAK)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_DARK_OAK_ROOTS(new Stump(false, "Rooted Dark Oak Stump", CustomMaterial.STUMP_DARK_OAK_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_MANGROVE(new Stump(false, "Mangrove Stump", CustomMaterial.STUMP_MANGROVE)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_MANGROVE_ROOTS(new Stump(false, "Rooted Mangrove Stump", CustomMaterial.STUMP_MANGROVE_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CRIMSON(new Stump(false, "Crimson Stump", CustomMaterial.STUMP_CRIMSON)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CRIMSON_ROOTS(new Stump(false, "Rooted Crimson Stump", CustomMaterial.STUMP_CRIMSON_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_WARPED(new Stump(false, "Warped Stump", CustomMaterial.STUMP_WARPED)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_WARPED_ROOTS(new Stump(false, "Rooted Warped Stump", CustomMaterial.STUMP_WARPED_ROOTS)),

	@TypeConfig(price = 60, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CHERRY(new Stump(false, "Cherry Stump", CustomMaterial.STUMP_CHERRY)),

	@TypeConfig(price = 75, tabs = {Tab.FURNITURE, Tab.CHAIRS, Tab.STUMPS})
	STUMP_CHERRY_ROOTS(new Stump(false, "Rooted Cherry Stump", CustomMaterial.STUMP_CHERRY_ROOTS)),


	// 	Benches
	@TypeConfig(price = 225, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	BENCH_WOODEN(new Bench(true, false, "Wooden Bench", CustomMaterial.BENCH_WOODEN, ColorableType.STAIN, HitboxFloor._1x2H)),

	// 	Couches
	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_LEFT(new Couch(false, "Cushioned Wooden Couch Left End", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_LEFT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_END_RIGHT(new Couch(false, "Cushioned Wooden Couch Left Right", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_RIGHT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_MIDDLE(new Couch(false, "Cushioned Wooden Couch Middle", CustomMaterial.COUCH_WOODEN_CUSHIONED_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_CORNER(new Couch(false, "Cushioned Wooden Couch Corner", CustomMaterial.COUCH_WOODEN_CUSHIONED_CORNER, ColorableType.DYE, CouchPart.CORNER)),

	@TypeConfig(price = 120, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_WOODEN_CUSHIONED_OTTOMAN(new Couch(false, "Cushioned Wooden Couch Ottoman", CustomMaterial.COUCH_WOODEN_CUSHIONED_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_LEFT(new Couch(false, "Cloth Couch Left End", CustomMaterial.COUCH_CLOTH_END_LEFT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_END_RIGHT(new Couch(false, "Cloth Couch Left Right", CustomMaterial.COUCH_CLOTH_END_RIGHT, ColorableType.DYE, CouchPart.END)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_MIDDLE(new Couch(false, "Cloth Couch Middle", CustomMaterial.COUCH_CLOTH_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_CORNER(new Couch(false, "Cloth Couch Corner", CustomMaterial.COUCH_CLOTH_CORNER, ColorableType.DYE, CouchPart.CORNER)),

	@TypeConfig(price = 165, tabs = {Tab.FURNITURE, Tab.CHAIRS})
	COUCH_CLOTH_OTTOMAN(new Couch(false, "Cloth Couch Ottoman", CustomMaterial.COUCH_CLOTH_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),

	// Flags
	@TypeConfig(price = 75, tabs = Tab.FLAGS)
	FLAG_SERVER(new Flag(false, "Server Flag", CustomMaterial.FLAG_SERVER)),

	// Bunting
	@TypeConfig(price = 60, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1(new Bunting(true, "Server Colors 1 Bunting", CustomMaterial.BUNTING_SERVER_COLORS_1)),

	@TypeConfig(price = 60, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2(new Bunting(true, "Server Colors 2 Bunting", CustomMaterial.BUNTING_SERVER_COLORS_2)),

	@TypeConfig(price = 30, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_1_SMALL(new Bunting(true, "Server Colors 1 Small Bunting", CustomMaterial.BUNTING_SERVER_COLORS_1_SMALL, HitboxSingle._1x1_LIGHT)),

	@TypeConfig(price = 30, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_COLORS_2_SMALL(new Bunting(true, "Server Colors 2 Small Bunting", CustomMaterial.BUNTING_SERVER_COLORS_2_SMALL, HitboxSingle._1x1_LIGHT)),

	@TypeConfig(price = 75, tabs = {Tab.FLAGS, Tab.BUNTING})
	BUNTING_SERVER_LOGO(new Bunting(true, "Server Logo Bunting", CustomMaterial.BUNTING_SERVER_LOGO)),

	// Banners
	// 	Hanging
	@TypeConfig(price = 120, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_1(new HangingBanner("Avontyre Royal Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_1, HitboxUnique.HANGING_BANNER_1x3V)),

	@TypeConfig(price = 105, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_2(new HangingBanner("Avontyre Cyan Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_2, HitboxUnique.HANGING_BANNER_1x3V)),

	@TypeConfig(price = 105, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_3(new HangingBanner("Avontyre Yellow Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_3, HitboxUnique.HANGING_BANNER_1x3V)),

	@TypeConfig(price = 90, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_AVONTYRE_4(new HangingBanner("Avontyre Checkered Hanging Banner", CustomMaterial.BANNER_HANGING_AVONTYRE_4, HitboxUnique.HANGING_BANNER_1x3V)),

	// Vanilla Banners
	//	Hanging
	@TypeConfig(price = 90, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_HANGING})
	BANNER_HANGING_SERVER_LOGO(new HangingBanner("Server Logo Hanging Banner", CustomMaterial.BANNER_HANGING_SERVER_LOGO)),

	//	Standing
	@TypeConfig(price = 90, tabs = {Tab.FLAGS, Tab.BANNERS, Tab.BANNERS_STANDING})
	BANNER_STANDING_SERVER_LOGO(new StandingBanner("Server Logo Standing Banner", CustomMaterial.BANNER_STANDING_SERVER_LOGO)),

	// 	Fireplaces
	@TypeConfig(price = 525, tabs = Tab.FURNITURE)
	FIREPLACE_DARK(new Fireplace(true, "Dark Fireplace", CustomMaterial.FIREPLACE_DARK)),

	@TypeConfig(price = 525, tabs = Tab.FURNITURE)
	FIREPLACE_BROWN(new Fireplace(true, "Brown Fireplace", CustomMaterial.FIREPLACE_BROWN)),

	@TypeConfig(price = 525, tabs = Tab.FURNITURE)
	FIREPLACE_LIGHT(new Fireplace(true, "Light Fireplace", CustomMaterial.FIREPLACE_LIGHT)),

	//	Food
	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIZZA_BOX_SINGLE(new FloorThing(false, "Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIZZA_BOX_SINGLE_OPENED(new FloorThing(false, "Opened Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE_OPENED)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	PIZZA_BOX_STACK(new FloorThing(false, "Pizza Box Stack", CustomMaterial.FOOD_PIZZA_BOX_STACK)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	SOUP_MUSHROOM(new FloorThing(false, "Mushroom Soup", CustomMaterial.FOOD_SOUP_MUSHROOM)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	SOUP_BEETROOT(new FloorThing(false, "Beetroot Soup", CustomMaterial.FOOD_SOUP_BEETROOT)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	SOUP_RABBIT(new FloorThing(false, "Rabbit Soup", CustomMaterial.FOOD_SOUP_RABBIT)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	BREAD_LOAF(new FloorThing(false, "Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF)),

	@TypeConfig(price = 25, tabs = Tab.FOOD)
	BREAD_LOAF_CUT(new FloorThing(false, "Cut Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF_CUT)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	BROWNIES_CHOCOLATE(new FloorThing(false, "Chocolate Brownies", CustomMaterial.FOOD_BROWNIES_CHOCOLATE)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	BROWNIES_VANILLA(new FloorThing(false, "Vanilla Brownies", CustomMaterial.FOOD_BROWNIES_VANILLA)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	COOKIES_CHOCOLATE(new FloorThing(false, "Chocolate Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	COOKIES_CHOCOLATE_CHIP(new FloorThing(false, "Chocolate Chip Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE_CHIP)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	COOKIES_SUGAR(new FloorThing(false, "Sugar Cookies", CustomMaterial.FOOD_COOKIES_SUGAR)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	MILK_AND_COOKIES(new FloorThing(false, "Milk and Cookies", CustomMaterial.FOOD_MILK_AND_COOKIES)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	MUFFINS_CHOCOLATE(new FloorThing(false, "Chocolate Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	MUFFINS_CHOCOLATE_CHIP(new FloorThing(false, "Chocolate Chip Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE_CHIP)),

	@TypeConfig(price = 30, tabs = Tab.FOOD)
	MUFFINS_LEMON(new FloorThing(false, "Lemon Muffins", CustomMaterial.FOOD_MUFFINS_LEMON)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	DINNER_HAM(new FloorThing(false, "Ham Dinner", CustomMaterial.FOOD_DINNER_HAM)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	DINNER_ROAST(new FloorThing(false, "Roast Dinner", CustomMaterial.FOOD_DINNER_ROAST)),

	@TypeConfig(price = 75, tabs = Tab.FOOD)
	DINNER_TURKEY(new FloorThing(false, "Turkey Dinner", CustomMaterial.FOOD_DINNER_TURKEY)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	PUNCHBOWL(new DyeableFloorThing(false, "Dyeable Punchbowl", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	PUNCHBOWL_EGGNOG(new DyeableFloorThing(false, "Eggnog", CustomMaterial.FOOD_PUNCHBOWL_EGGNOG, ColorableType.DYE, "FFF4BB")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_SAUCE(new DyeableFloorThing(false, "Dyeable Sauce Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_SAUCE_CRANBERRIES(new DyeableFloorThing(false, "Cranberries Side", CustomMaterial.FOOD_SIDE_SAUCE_CRANBERRIES, ColorableType.DYE, "C61B1B")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_GREEN_BEAN_CASSEROLE(new FloorThing(false, "Green Bean Casserole Side", CustomMaterial.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_MAC_AND_CHEESE(new FloorThing(false, "Mac N' Cheese Side", CustomMaterial.FOOD_SIDE_MAC_AND_CHEESE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_SWEET_POTATOES(new FloorThing(false, "Sweet Potatoes Side", CustomMaterial.FOOD_SIDE_SWEET_POTATOES)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_MASHED_POTATOES(new FloorThing(false, "Mashed Potatoes Side", CustomMaterial.FOOD_SIDE_MASHED_POTATOES)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	SIDE_ROLLS(new FloorThing(false, "Rolls", CustomMaterial.FOOD_SIDE_ROLLS)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER(new DyeableFloorThing(false, "Dyeable Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER_RED_VELVET(new DyeableFloorThing(false, "Red Velvet Cake Batter", CustomMaterial.FOOD_CAKE_BATTER_VELVET, ColorableType.DYE, "720606")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER_VANILLA(new DyeableFloorThing(false, "Vanilla Cake Batter", CustomMaterial.FOOD_CAKE_BATTER_VANILLA, ColorableType.DYE, "FFF9CC")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	CAKE_BATTER_CHOCOLATE(new DyeableFloorThing(false, "Chocolate Cake Batter", CustomMaterial.FOOD_CAKE_BATTER_CHOCOLATE, ColorableType.DYE, "492804")),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	CAKE_WHITE_CHOCOLATE(new FloorThing(false, "White Chocolate Cake", CustomMaterial.FOOD_CAKE_WHITE_CHOCOLATE)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	CAKE_BUNDT(new FloorThing(false, "Bundt Cake", CustomMaterial.FOOD_CAKE_BUNDT)),

	@TypeConfig(price = 90, tabs = Tab.FOOD)
	CAKE_CHOCOLATE_DRIP(new FloorThing(false, "Chocolate Drip Cake", CustomMaterial.FOOD_CAKE_CHOCOLATE_DRIP)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_ROUGH(new DyeableFloorThing(false, "Dyeable Rough Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_ROUGH_PECAN(new DyeableFloorThing(false, "Pecan Pie", CustomMaterial.FOOD_PIE_ROUGH_PECAN, ColorableType.DYE, "4E3004")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH(new DyeableFloorThing(false, "Dyeable Smooth Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH_CHOCOLATE(new DyeableFloorThing(false, "Chocolate Pie", CustomMaterial.FOOD_PIE_SMOOTH_CHOCOLATE, ColorableType.DYE, "734008")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH_LEMON(new DyeableFloorThing(false, "Lemon Pie", CustomMaterial.FOOD_PIE_SMOOTH_LEMON, ColorableType.DYE, "FFE050")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_SMOOTH_PUMPKIN(new DyeableFloorThing(false, "Pumpkin Pie Decoration", CustomMaterial.FOOD_PIE_SMOOTH_PUMPKIN, ColorableType.DYE, "BF7D18")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED(new DyeableFloorThing(false, "Dyeable Latticed Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED_APPLE(new DyeableFloorThing(false, "Apple Pie", CustomMaterial.FOOD_PIE_LATTICED_APPLE, ColorableType.DYE, "FDC330")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED_BLUEBERRY(new DyeableFloorThing(false, "Blueberry Pie", CustomMaterial.FOOD_PIE_LATTICED_BLUEBERRY, ColorableType.DYE, "4E1892")),

	@TypeConfig(price = 45, tabs = Tab.FOOD)
	PIE_LATTICED_CHERRY(new DyeableFloorThing(false, "Cherry Pie", CustomMaterial.FOOD_PIE_LATTICED_CHERRY, ColorableType.DYE, "B60C0C")),

	//	Kitchenware
	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE(new FloorThing(false, "Wine Bottle", CustomMaterial.KITCHENWARE_WINE_BOTTLE)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP(new FloorThing(false, "Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_RANDOM(new FloorThing(false, "Random Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	WINE_BOTTLE_GROUP_SIDE(new FloorThing(false, "Wine Bottles on Side", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_SIDE)),

	@TypeConfig(price = 30, tabs = Tab.KITCHENWARE)
	WINE_GLASS(new FloorThing(false, "Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS)),

	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	WINE_GLASS_FULL(new FloorThing(false, "Full Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS_FULL)),

	@TypeConfig(price = 30, tabs = Tab.KITCHENWARE)
	MUG_GLASS(new FloorThing(false, "Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS)),

	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	MUG_GLASS_FULL(new FloorThing(false, "Full Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS_FULL)),

	@TypeConfig(price = 30, tabs = Tab.KITCHENWARE)
	MUG_WOODEN(new FloorThing(false, "Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN)),

	@TypeConfig(price = 45, tabs = Tab.KITCHENWARE)
	MUG_WOODEN_FULL(new FloorThing(false, "Full Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN_FULL)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_1(new FloorThing(false, "Random Glassware 1", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_1)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_2(new FloorThing(false, "Random Glassware 2", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_2)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	GLASSWARE_GROUP_3(new FloorThing(false, "Random Glassware 3", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_3)),

	@TypeConfig(price = 75, tabs = Tab.KITCHENWARE)
	JAR(new FloorThing(false, "Jar", CustomMaterial.KITCHENWARE_JAR)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	JAR_HONEY(new FloorThing(false, "Honey Jar", CustomMaterial.KITCHENWARE_JAR_HONEY)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	JAR_COOKIES(new FloorThing(false, "Cookie Jar", CustomMaterial.KITCHENWARE_JAR_COOKIES)),

	@TypeConfig(price = 90, tabs = Tab.KITCHENWARE)
	JAR_WIDE(new FloorThing(false, "Wide Jar", CustomMaterial.KITCHENWARE_JAR_WIDE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	BOWL_DECORATION(new FloorThing(false, "Wooden Bowl", CustomMaterial.KITCHENWARE_BOWL)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	MIXING_BOWL(new FloorThing(false, "Mixing Bowl", CustomMaterial.KITCHENWARE_MIXING_BOWL)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_CAKE(new FloorThing(false, "Cake Pan", CustomMaterial.KITCHENWARE_PAN_CAKE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_CASSEROLE(new FloorThing(false, "Casserole Pan", CustomMaterial.KITCHENWARE_PAN_CASSEROLE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_COOKIE(new FloorThing(false, "Cookie Pan", CustomMaterial.KITCHENWARE_PAN_COOKIE)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_MUFFIN(new FloorThing(false, "Muffin Pan", CustomMaterial.KITCHENWARE_PAN_MUFFIN)),

	@TypeConfig(price = 60, tabs = Tab.KITCHENWARE)
	PAN_PIE(new FloorThing(false, "Pie Pan", CustomMaterial.KITCHENWARE_PAN_PIE)),

	// 	Appliances
	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE(new DyeableFloorThing(false, "Fridge", CustomMaterial.APPLIANCE_FRIDGE, ColorableType.DYE, "FFFFFF", HitboxFloor._1x2V)),

	@TypeConfig(price = 195, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MAGNETS(new DyeableFloorThing(false, "Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MAGNETS, ColorableType.DYE, "FFFFFF", HitboxFloor._1x2V)),

	@TypeConfig(price = 270, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL(new DyeableFloorThing(false, "Tall Fridge", CustomMaterial.APPLIANCE_FRIDGE_TALL, ColorableType.DYE, "FFFFFF", HitboxFloor._1x3V)),

	@TypeConfig(price = 285, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_TALL_MAGNETS(new DyeableFloorThing(false, "Tall Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_TALL_MAGNETS, ColorableType.DYE, "FFFFFF", HitboxFloor._1x3V)),

	@TypeConfig(price = 90, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI(new DyeableFloorThing(false, "Mini Fridge", CustomMaterial.APPLIANCE_FRIDGE_MINI, ColorableType.DYE, "FFFFFF", HitboxSingle._1x1)),

	@TypeConfig(price = 105, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_FRIDGE_MINI_MAGNETS(new DyeableFloorThing(false, "Mini Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MINI_MAGNETS, ColorableType.DYE, "FFFFFF", HitboxSingle._1x1)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	APPLIANCE_SLUSHIE_MACHINE(new DyeableFloorThing(false, "Slushie Machine", CustomMaterial.APPLIANCE_SLUSHIE_MACHINE, ColorableType.DYE, HitboxSingle._1x1)),

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
	CABINET_HOOD(new WallThing(false, "Hood Cabinet", CustomMaterial.CABINET_HOOD, HitboxSingle._1x1)),

	@TypeConfig(price = 180, tabs = {Tab.FURNITURE, Tab.CABINETS})
	CABINET_WOODEN_CORNER(new Cabinet(CustomMaterial.CABINET_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.NONE, CabinetType.CORNER)),

	@TypeConfig(price = 150, tabs = {Tab.FURNITURE, Tab.CABINETS})
	CABINET_WOODEN_CORNER_SHORT(new Cabinet(CustomMaterial.CABINET_WOODEN_CORNER_SHORT, CabinetMaterial.WOODEN, HandleType.NONE, CabinetType.SHORT_CORNER)),

	@TypeConfig(price = 225, tabs = {Tab.FURNITURE, Tab.APPLIANCES})
	TOILET_MODERN(new DyeableChair(false, false, "Toilet Modern", CustomMaterial.TOILET_MODERN, ColorableType.DYE, "FFFFFF", HitboxSingle._1x1, 1.3)),

	@TypeConfig(price = 450, tabs = Tab.FURNITURE)
	WARDROBE(new Furniture(true, "Wardrobe", CustomMaterial.WARDROBE, PlacementType.FLOOR, HitboxFloor._2x3V)),

	@TypeConfig(price = 240, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_DOUBLE(new Furniture(true, "Short Cupboard Double", CustomMaterial.CUPBOARD_SHORT_DOUBLE, PlacementType.FLOOR, HitboxFloor._1x2H)),

	@TypeConfig(price = 120, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_SINGLE(new Furniture(false, "Short Cupboard Single", CustomMaterial.CUPBOARD_SHORT_SINGLE, PlacementType.FLOOR, HitboxSingle._1x1)),

	@TypeConfig(price = 240, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_BOOKSHELF_DOUBLE(new Furniture(true, "Short Bookshelf Cupboard Double", CustomMaterial.CUPBOARD_SHORT_BOOKSHELF_DOUBLE, PlacementType.FLOOR, HitboxFloor._1x2H)),

	@TypeConfig(price = 120, tabs = Tab.FURNITURE)
	CUPBOARD_SHORT_BOOKSHELF_SINGLE(new Furniture(false, "Short Bookshelf Cupboard Single", CustomMaterial.CUPBOARD_SHORT_BOOKSHELF_SINGLE, PlacementType.FLOOR, HitboxSingle._1x1)),

	@TypeConfig(unbuyable = true, price = 150, tabs = Tab.FURNITURE)
	SHELF_WALL(new DyeableWallThing(true, "Wall Shelf", CustomMaterial.SHELF_WALL, ColorableType.STAIN, HitboxFloor._1x2H)),

	// Beds, multiblock = true
	@TypeConfig(price = 215, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_1_SINGLE(new BedAddition("Generic Frame A Single", CustomMaterial.BED_GENERIC_1_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 430, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_1_DOUBLE(new BedAddition("Generic Frame A Double", CustomMaterial.BED_GENERIC_1_DOUBLE, AdditionType.FRAME, true, ColorableType.STAIN)),

	@TypeConfig(price = 235, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_2_SINGLE(new BedAddition("Generic Frame B Single", CustomMaterial.BED_GENERIC_2_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 470, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_2_DOUBLE(new BedAddition("Generic Frame B Double", CustomMaterial.BED_GENERIC_2_DOUBLE, AdditionType.FRAME, true, ColorableType.STAIN)),

	@TypeConfig(price = 215, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_3_SINGLE(new BedAddition("Generic Frame C Single", CustomMaterial.BED_GENERIC_3_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 430, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_3_DOUBLE(new BedAddition("Generic Frame C Double", CustomMaterial.BED_GENERIC_3_DOUBLE, AdditionType.FRAME, true, ColorableType.STAIN)),

	@TypeConfig(price = 255, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_4_SINGLE(new BedAddition("Generic Frame D Single", CustomMaterial.BED_GENERIC_4_SINGLE, AdditionType.FRAME, ColorableType.STAIN)),

	@TypeConfig(price = 510, tabs = {Tab.FURNITURE, Tab.BEDS})
	BED_GENERIC_4_DOUBLE(new BedAddition("Generic Frame D Double", CustomMaterial.BED_GENERIC_4_DOUBLE, AdditionType.FRAME, true, ColorableType.STAIN)),

	//	Potions
	@TypeConfig(price = 45, tabs = Tab.POTIONS)
	POTION_FILLED_TINY_1(new DyeableFloorThing(false, "Tiny Potions 1", CustomMaterial.POTION_FILLED_TINY_1, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.POTIONS)
	POTION_FILLED_TINY_2(new DyeableFloorThing(false, "Tiny Potions 2", CustomMaterial.POTION_FILLED_TINY_2, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_1(new DyeableFloorThing(false, "Small Potion 1", CustomMaterial.POTION_FILLED_SMALL_1, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_2(new DyeableFloorThing(false, "Small Potion 2", CustomMaterial.POTION_FILLED_SMALL_2, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_SMALL_3(new DyeableFloorThing(false, "Small Potion 3", CustomMaterial.POTION_FILLED_SMALL_3, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_MEDIUM_1(new DyeableFloorThing(false, "Medium Potion 1", CustomMaterial.POTION_FILLED_MEDIUM_1, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_MEDIUM_2(new DyeableFloorThing(false, "Medium Potion 2", CustomMaterial.POTION_FILLED_MEDIUM_2, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_WIDE(new DyeableFloorThing(false, "Wide Potion", CustomMaterial.POTION_FILLED_WIDE, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_SKINNY(new DyeableFloorThing(false, "Skinny Potion", CustomMaterial.POTION_FILLED_SKINNY, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_TALL(new DyeableFloorThing(false, "Tall Potion", CustomMaterial.POTION_FILLED_TALL, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_BOTTLE(new DyeableFloorThing(false, "Big Potion Bottle", CustomMaterial.POTION_FILLED_BIG_BOTTLE, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_TEAR(new DyeableFloorThing(false, "Big Potion Tear", CustomMaterial.POTION_FILLED_BIG_TEAR, ColorableType.DYE)),

	@TypeConfig(price = 120, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_DONUT(new DyeableFloorThing(false, "Big Potion Donut", CustomMaterial.POTION_FILLED_BIG_DONUT, ColorableType.DYE)),

	@TypeConfig(price = 120, tabs = Tab.POTIONS)
	POTION_FILLED_BIG_SKULL(new DyeableFloorThing(false, "Big Potion Skull", CustomMaterial.POTION_FILLED_BIG_SKULL, ColorableType.DYE)),

	@TypeConfig(price = 65, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_SMALL(new DyeableFloorThing(false, "Small Potions", CustomMaterial.POTION_FILLED_GROUP_SMALL, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_MEDIUM(new DyeableFloorThing(false, "Medium Potions", CustomMaterial.POTION_FILLED_GROUP_MEDIUM, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_TALL(new DyeableFloorThing(false, "Tall Potions", CustomMaterial.POTION_FILLED_GROUP_TALL, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_1(new DyeableFloorThing(false, "Random Potions 1", CustomMaterial.POTION_FILLED_GROUP_RANDOM_1, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_2(new DyeableFloorThing(false, "Random Potions 2", CustomMaterial.POTION_FILLED_GROUP_RANDOM_2, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_3(new DyeableFloorThing(false, "Random Potions 3", CustomMaterial.POTION_FILLED_GROUP_RANDOM_3, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_4(new DyeableFloorThing(false, "Random Potions 4", CustomMaterial.POTION_FILLED_GROUP_RANDOM_4, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_5(new DyeableFloorThing(false, "Random Potions 5", CustomMaterial.POTION_FILLED_GROUP_RANDOM_5, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_6(new DyeableFloorThing(false, "Random Potions 6", CustomMaterial.POTION_FILLED_GROUP_RANDOM_6, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_7(new DyeableFloorThing(false, "Random Potions 7", CustomMaterial.POTION_FILLED_GROUP_RANDOM_7, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_FILLED_GROUP_RANDOM_8(new DyeableFloorThing(false, "Random Potions 8", CustomMaterial.POTION_FILLED_GROUP_RANDOM_8, ColorableType.DYE)),

	@TypeConfig(price = 30, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_1(new DyeableFloorThing(false, "Empty Small Potion 1", CustomMaterial.POTION_EMPTY_SMALL_1, ColorableType.DYE)),

	@TypeConfig(price = 30, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_2(new DyeableFloorThing(false, "Empty Small Potion 2", CustomMaterial.POTION_EMPTY_SMALL_2, ColorableType.DYE)),

	@TypeConfig(price = 30, tabs = Tab.POTIONS)
	POTION_EMPTY_SMALL_3(new DyeableFloorThing(false, "Empty Small Potion 3", CustomMaterial.POTION_EMPTY_SMALL_3, ColorableType.DYE)),

	@TypeConfig(price = 45, tabs = Tab.POTIONS)
	POTION_EMPTY_MEDIUM_1(new DyeableFloorThing(false, "Empty Medium Potion 1", CustomMaterial.POTION_EMPTY_MEDIUM_1, ColorableType.DYE)),

	@TypeConfig(price = 54, tabs = Tab.POTIONS)
	POTION_EMPTY_MEDIUM_2(new DyeableFloorThing(false, "Empty Medium Potion 2", CustomMaterial.POTION_EMPTY_MEDIUM_2, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_WIDE(new DyeableFloorThing(false, "Empty Wide Potion", CustomMaterial.POTION_EMPTY_WIDE, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_SKINNY(new DyeableFloorThing(false, "Empty Skinny Potion", CustomMaterial.POTION_EMPTY_SKINNY, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_TALL(new DyeableFloorThing(false, "Empty Tall Potion", CustomMaterial.POTION_EMPTY_TALL, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_BOTTLE(new DyeableFloorThing(false, "Empty Big Potion Bottle", CustomMaterial.POTION_EMPTY_BIG_BOTTLE, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_TEAR(new DyeableFloorThing(false, "Empty Big Potion Tear", CustomMaterial.POTION_EMPTY_BIG_TEAR, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_DONUT(new DyeableFloorThing(false, "Empty Big Potion Donut", CustomMaterial.POTION_EMPTY_BIG_DONUT, ColorableType.DYE)),

	@TypeConfig(price = 105, tabs = Tab.POTIONS)
	POTION_EMPTY_BIG_SKULL(new DyeableFloorThing(false, "Empty Big Potion Skull", CustomMaterial.POTION_EMPTY_BIG_SKULL, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_SMALL(new DyeableFloorThing(false, "Empty Small Potions", CustomMaterial.POTION_EMPTY_GROUP_SMALL, ColorableType.DYE)),

	@TypeConfig(price = 75, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_MEDIUM(new DyeableFloorThing(false, "Empty Medium Potions", CustomMaterial.POTION_EMPTY_GROUP_MEDIUM, ColorableType.DYE)),

	@TypeConfig(price = 90, tabs = Tab.POTIONS)
	POTION_EMPTY_GROUP_TALL(new DyeableFloorThing(false, "Empty Tall Potions", CustomMaterial.POTION_EMPTY_GROUP_TALL, ColorableType.DYE)),

	// Books
	@TypeConfig(price = 15, tabs = Tab.BOOKS)
	BOOK_CLOSED(new DyeableFloorThing(false, "Closed Book", CustomMaterial.BOOK_CLOSED, ColorableType.DYE)),

	@TypeConfig(price = 25, tabs = Tab.BOOKS)
	BOOK_OPENED_1(new DyeableFloorThing(false, "Opened Book 1", CustomMaterial.BOOK_OPENED_1, ColorableType.DYE)),

	@TypeConfig(price = 20, tabs = Tab.BOOKS)
	BOOK_OPENED_2(new DyeableFloorThing(false, "Opened Book 2", CustomMaterial.BOOK_OPENED_2, ColorableType.DYE)),

	@TypeConfig(price = 40, tabs = Tab.BOOKS)
	BOOK_ROW_1(new DyeableFloorThing(false, "Book Row 1", CustomMaterial.BOOK_ROW_1, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.BOOKS)
	BOOK_ROW_2(new DyeableFloorThing(false, "Book Row 2", CustomMaterial.BOOK_ROW_2, ColorableType.DYE)),

	@TypeConfig(price = 30, tabs = Tab.BOOKS)
	BOOK_STACK_1(new DyeableFloorThing(false, "Book Stack 1", CustomMaterial.BOOK_STACK_1, ColorableType.DYE)),

	@TypeConfig(price = 40, tabs = Tab.BOOKS)
	BOOK_STACK_2(new DyeableFloorThing(false, "Book Stack 2", CustomMaterial.BOOK_STACK_2, ColorableType.DYE)),

	@TypeConfig(price = 60, tabs = Tab.BOOKS)
	BOOK_STACK_3(new DyeableFloorThing(false, "Book Stack 3", CustomMaterial.BOOK_STACK_3, ColorableType.DYE)),

	// Balloons
	@TypeConfig(price = 90)
	BALLOON_SHORT(new DyeableFloorThing(false, "Balloon Short", CustomMaterial.BALLOON_SHORT, ColorableType.DYE)),

	@TypeConfig(price = 105)
	BALLOON_MEDIUM(new DyeableFloorThing(false, "Balloon Medium", CustomMaterial.BALLOON_MEDIUM, ColorableType.DYE)),

	@TypeConfig(price = 120)
	BALLOON_TALL(new DyeableFloorThing(false, "Balloon Tall", CustomMaterial.BALLOON_TALL, ColorableType.DYE)),

	// Curtains
	@TypeConfig(price = 150)
	WINDOW_CURTAINS_1x2(new Curtain("Window Curtains 1x2", CurtainType._1x2_OPEN)),

	@TypeConfig(price = 250)
	WINDOW_CURTAINS_2x2(new Curtain("Window Curtains 2x2", CurtainType._2x2_OPEN)),

	@TypeConfig(price = 350)
	WINDOW_CURTAINS_2x3H(new Curtain("Window Curtains 2x3H", CurtainType._2x3H_OPEN)),

	@TypeConfig(price = 250)
	WINDOW_CURTAINS_1x3(new Curtain("Window Curtains 1x3", CurtainType._1x3_OPEN)),

	@TypeConfig(price = 350)
	WINDOW_CURTAINS_2x3V(new Curtain("Window Curtains 2x3V", CurtainType._2x3V_OPEN)),

	@TypeConfig(price = 450)
	WINDOW_CURTAINS_3x3(new Curtain("Window Curtains 3x3", CurtainType._3x3_OPEN)),

	//	Misc
	@TypeConfig(price = 75)
	TRASH_CAN(new TrashCan("Trash Can", CustomMaterial.TRASH_CAN, ColorableType.DYE, "C7C7C7", HitboxSingle._1x1)),

	@TypeConfig(price = 50)
	TRASH_BAG(new FloorThing(false, "Trash Bag", CustomMaterial.TRASH_BAG, HitboxSingle._1x1)),

	@TypeConfig(price = 15)
	INKWELL(new FloorThing(false, "Inkwell", CustomMaterial.INKWELL)),

	@TypeConfig(price = 75)
	WHEEL_SMALL(new DecorationConfig(false, "Small Wheel", CustomMaterial.WHEEL_SMALL)),

	@TypeConfig(price = 150)
	TELESCOPE(new FloorThing(false, "Telescope", CustomMaterial.TELESCOPE)),

	@TypeConfig(price = 75)
	MICROSCOPE(new FloorThing(false, "Microscope", CustomMaterial.MICROSCOPE)),

	@TypeConfig(price = 75)
	MICROSCOPE_WITH_GEM(new FloorThing(false, "Microscope With Gem", CustomMaterial.MICROSCOPE_WITH_GEM)),

	@TypeConfig(price = 135)
	HELM(new DecorationConfig(false, "Helm", CustomMaterial.HELM)),

	@TypeConfig(price = 60)
	TRAFFIC_BLOCKADE(new DyeableFloorThing(false, "Traffic Blockade", CustomMaterial.TRAFFIC_BLOCKADE, ColorableType.DYE, "FF7F00", HitboxSingle._1x1)),

	@TypeConfig(price = 75)
	TRAFFIC_BLOCKADE_LIGHTS(new DyeableFloorThing(false, "Traffic Blockade with Lights", CustomMaterial.TRAFFIC_BLOCKADE_LIGHTS, ColorableType.DYE, "FF7F00", HitboxSingle._1x1)),

	@TypeConfig(price = 60)
	TRAFFIC_CONE(new DyeableFloorThing(false, "Traffic Cone", CustomMaterial.TRAFFIC_CONE, ColorableType.DYE, "FF7F00", HitboxSingle._1x1)),

	@TypeConfig(price = 40)
	TRAFFIC_CONE_SMALL(new DyeableFloorThing(false, "Traffic Cone Small", CustomMaterial.TRAFFIC_CONE_SMALL, ColorableType.DYE, "FF7F00", HitboxSingle.NONE)),

	@TypeConfig(price = 80)
	TRAFFIC_CONE_BARREL(new DyeableFloorThing(false, "Traffic Cone Barrel", CustomMaterial.TRAFFIC_CONE_BARREL, ColorableType.DYE, "FF7F00", HitboxSingle._1x1)),

	@TypeConfig(price = 50)
	TRAFFIC_CONE_TUBE(new DyeableFloorThing(false, "Traffic Cone Tube", CustomMaterial.TRAFFIC_CONE_TUBE, ColorableType.DYE, "FF7F00", HitboxSingle._1x1_CHAIN)),

	@TypeConfig(price = 150)
	POSTBOX(new FloorThing(false, "Postbox", CustomMaterial.POSTBOX, HitboxFloor._1x2V)),

	@TypeConfig(price = 90)
	MAILBOX(new DyeableFloorThing(false, "Mailbox", CustomMaterial.MAILBOX, ColorableType.DYE, "C7C7C7", HitboxFloor._1x2V)),

	@TypeConfig(price = 60)
	SANDWICH_SIGN(new FloorThing(false, "Sandwich Sign", CustomMaterial.SANDWICH_SIGN)),

	@TypeConfig(price = 75)
	SANDWICH_SIGN_TALL(new FloorThing(false, "Sandwich Sign Tall", CustomMaterial.SANDWICH_SIGN_TALL)),

	@TypeConfig(price = 60)
	FIRE_HYDRANT(new DyeableFloorThing(false, "Fire Hydrant", CustomMaterial.FIRE_HYDRANT, ColorableType.DYE, "FF4233", HitboxSingle._1x1_CHAIN)),

	@TypeConfig(price = 90)
	ROTARY_PHONE(new DyeableFloorThing(false, "Rotary Phone", CustomMaterial.ROTARY_PHONE, ColorableType.DYE, "FF4233")),

	@TypeConfig(price = 90)
	LAPTOP(new DyeableFloorThing(false, "Laptop", CustomMaterial.LAPTOP, ColorableType.DYE, "A900FF")),

	@TypeConfig(price = 90)
	ROUTER(new DyeableFloorThing(false, "Router", CustomMaterial.ROUTER, ColorableType.DYE, "0040FF")),

	@TypeConfig(price = 90)
	REGISTER_MODERN(new FloorThing(false, "Modern Register", CustomMaterial.REGISTER_MODERN)),

	@TypeConfig(price = 40)
	CARDBOARD_BOX_SMALL(new FloorThing(false, "Small Cardboard Box", CustomMaterial.CARDBOARD_BOX_SMALL)),

	@TypeConfig(price = 60)
	CARDBOARD_BOX_MEDIUM(new FloorThing(false, "Medium Cardboard Box", CustomMaterial.CARDBOARD_BOX_MEDIUM, HitboxSingle._1x1)),

	@TypeConfig(price = 100)
	CARDBOARD_BOX_LARGE(new FloorThing(true, "Large Cardboard Box", CustomMaterial.CARDBOARD_BOX_LARGE, HitboxUnique.CARDBOARD_BOX)),

	@TypeConfig(price = 200)
	FLAT_SCREEN_TV(new WallThing(true, "Flat Screen TV", CustomMaterial.FLAT_SCREEN_TV, HitboxWall._2x3H_LIGHT)),

// 	------------------------------------------------------------------------------------------------------
//										UNBUYABLE THINGS
// 	------------------------------------------------------------------------------------------------------

	@TypeConfig(unbuyable = true)
	PAPER_LANTERN_SINGLE(new CeilingThing(false, "Paper Lanterns - Single", CustomMaterial.PAPER_LANTERN_SINGLE, HitboxUnique.PAPER_LANTERN_2V)),

	@TypeConfig(unbuyable = true)
	PAPER_LANTERN_DOUBLE(new CeilingThing(false, "Paper Lanterns - Double", CustomMaterial.PAPER_LANTERN_DOUBLE, HitboxUnique.PAPER_LANTERN_2V)),

	@TypeConfig(unbuyable = true)
	PAPER_LANTERN_TRIPLE(new CeilingThing(false, "Paper Lanterns - Triple", CustomMaterial.PAPER_LANTERN_TRIPLE, HitboxUnique.PAPER_LANTERN_3V)),

// 	------------------------------------------------------------------------------------------------------
//										INTERNAL USE ONLY
// 	------------------------------------------------------------------------------------------------------

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	SHELF_STORAGE(new Furniture(true, "Storage Shelf", CustomMaterial.SHELF_STORAGE, PlacementType.FLOOR, HitboxFloor._2x3V)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	WAYSTONE(new FloorThing(false, "Waystone", CustomMaterial.WAYSTONE)),

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

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL) // Tickable
	WAYSTONE_ACTIVATED(new Waystone("Waystone Activated", CustomMaterial.WAYSTONE_ACTIVATED)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_FOREST_VERTICAL(new BirdHouse("Vertical Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_VERTICAL, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_FOREST_HANGING(new BirdHouse("Hanging Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HANGING, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_ENCHANTED_VERTICAL(new BirdHouse("Vertical Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_VERTICAL, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_ENCHANTED_HANGING(new BirdHouse("Hanging Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HANGING, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_DEPTHS_VERTICAL(new BirdHouse("Vertical Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_VERTICAL, false)),

	@TypeConfig(tabs = Tab.INTERNAL, theme = Theme.OUTDOORS)
	BIRDHOUSE_DEPTHS_HANGING(new BirdHouse("Hanging Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HANGING, false)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	DYE_STATION(new WorkBench("Dye Station", CustomMaterial.DYE_STATION)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	ENCHANTED_BOOK_SPLITTER(new WorkBench("Enchanted Book Splitter", CustomMaterial.ENCHANTED_BOOK_SPLITTER, HitboxFloor._1x2H)),

	@TypeConfig(unbuyable = true, tabs = Tab.INTERNAL)
	TOOL_MODIFICATION_TABLE(new WorkBench("Tool Modification Table", CustomMaterial.TOOL_MODIFICATION_TABLE, HitboxFloor._1x2H)),

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
