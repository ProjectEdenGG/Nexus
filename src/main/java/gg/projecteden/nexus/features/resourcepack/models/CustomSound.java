package gg.projecteden.nexus.features.resourcepack.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CustomSound {
	// @formatter:off

	// DECOR
	DECOR_PAINT(						"custom.decoration.paint"),
	DECOR_CURTAINS_USE(					"custom.decoration.curtain.use"),
	DECOR_TRASH_CAN_OPEN(				"custom.decoration.trash_can.open"),
	DECOR_TRASH_CAN_CLOSE(				"custom.decoration.trash_can.close"),
	DECOR_INSTRUMENT_GRAND_PIANO(		"custom.instrument.grand_piano"),
	DECOR_INSTRUMENT_HARP(				"custom.instrument.harp"),
	DECOR_INSTRUMENT_BONGO(				"custom.instrument.bongos"),
	DECOR_INSTRUMENT_DRUMS(				"custom.instrument.drum_kit"),

	// CUSTOM NOTE BLOCKS
	NOTE_MARIMBA(						"custom.noteblock.marimba"),
	NOTE_TRUMPET(						"custom.noteblock.trumpet"),
	NOTE_BUZZ(							"custom.noteblock.buzz"),
	NOTE_KALIMBA(						"custom.noteblock.kalimba"),
	NOTE_KOTO(							"custom.noteblock.koto"),
	NOTE_TAIKO(							"custom.noteblock.taiko"),

	// AMBIENT
	AMBIENT_MILLSTONE(					"custom.ambient.misc.millstone"),
	AMBIENT_WATERMILL(					"custom.ambient.misc.watermill"),
	AMBIENT_WINDCHIMES_METAL(			"custom.ambient.windchimes.metal"),
	WEATHER_THUNDER(					"custom.weather.thunder"),

	// MINIGAMES
	MINIGAMES_SABOTAGE_ALARM(			"custom.minigames.sabotage.alarm"),

	// CRATES
	CRATES_GEMCRAFTER_INFUSE(			"custom.crates.gemcrafter.infuse"),
	CRATES_WEEKLYWAKKA_BURP(			"custom.crates.weeklywakka.burp"),

	// EVENTS
	TRAIN_CHUG(							"custom.train.chug"),
	TRAIN_WHISTLE(						"custom.train.whistle"),
	HEARTBEAT(							"custom.misc.heartbeat"),
	BLOOD_GUSHING(						"custom.misc.blood"),

	// MISC
	BURP(								"custom.misc.burp"),
	BONK(								"custom.misc.bonk"),
	PARTY_HORN(							"custom.misc.party_horn"),
	YOU_GOT_MAIL(						"custom.misc.you_got_mail"),
	FLASH_BANG(							"custom.misc.flashbang"),
	STONE_DOOR(							"custom.misc.stone"),

	EXPLOSION_1(						"custom.explosion.explosion_1"),
	EXPLOSION_2(						"custom.explosion.explosion_2"),
	EXPLOSION_3(						"custom.explosion.explosion_3"),
	EXPLOSION_4(						"custom.explosion.explosion_4"),
	;

	// @formatter:on

	@Getter
	final String path;
}
