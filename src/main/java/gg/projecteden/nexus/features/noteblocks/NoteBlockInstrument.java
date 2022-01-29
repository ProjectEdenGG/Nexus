package gg.projecteden.nexus.features.noteblocks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Instrument;

import java.util.Arrays;

@AllArgsConstructor
public enum NoteBlockInstrument {
	PIANO(getSoundFrom(Instrument.PIANO)),
	BASS_DRUM(getSoundFrom(Instrument.BASS_DRUM)),
	SNARE_DRUM(getSoundFrom(Instrument.SNARE_DRUM)),
	STICKS(getSoundFrom(Instrument.STICKS)),
	BASS_GUITAR(getSoundFrom(Instrument.BASS_GUITAR)),
	FLUTE(getSoundFrom(Instrument.FLUTE)),
	BELL(getSoundFrom(Instrument.BELL)),
	GUITAR(getSoundFrom(Instrument.GUITAR)),
	CHIME(getSoundFrom(Instrument.CHIME)),
	XYLOPHONE(getSoundFrom(Instrument.XYLOPHONE)),
	IRON_XYLOPHONE(getSoundFrom(Instrument.IRON_XYLOPHONE)),
	COW_BELL(getSoundFrom(Instrument.COW_BELL)),
	DIDGERIDOO(getSoundFrom(Instrument.DIDGERIDOO)),
	BIT(getSoundFrom(Instrument.BIT)),
	BANJO(getSoundFrom(Instrument.BANJO)),
	PLING(getSoundFrom(Instrument.PLING)),
	PARTY_HORN("minecraft:custom.misc.party_horn"),
	;

	@Getter
	private final String sound;

	public static NoteBlockInstrument of(Instrument instrument) {
		return Arrays.stream(values()).filter(_instrument -> _instrument.name().equals(instrument.name())).findFirst().orElse(null);
	}

	private static String getSoundFrom(Instrument instrument) {
		return "minecraft:block_note_block_" + instrument.name().toLowerCase();
	}
}
