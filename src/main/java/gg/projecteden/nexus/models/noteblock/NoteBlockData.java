package gg.projecteden.nexus.models.noteblock;

import gg.projecteden.nexus.features.noteblocks.NoteBlockInstrument;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteBlockData {
	UUID placerUUID;
	NoteBlockInstrument instrument;
	int step = 0;
	double volume = 1.0;

	public NoteBlockData(Player player, NoteBlockInstrument instrument, int step) {
		this.placerUUID = player.getUniqueId();
		this.instrument = instrument;
		this.step = step;
	}

	public void incrementStep() {
		int _step = (this.step + 1);
		this.step = _step > 24 ? 0 : _step;
	}

	public void decrementStep() {
		int _step = (this.step - 1);
		this.step = _step < 0 ? 25 : _step;
	}

	public void setStep(int step) {
		this.step = MathUtils.clamp(step, 0, 24);
	}

	public void setVolume(double volume) {
		this.volume = MathUtils.clamp(volume, 0.0, 2.0);
	}

	public void play(Location location) {
		this.instrument = NoteBlockInstrument.getInstrument(location.getBlock().getRelative(BlockFace.DOWN).getType());

		new SoundBuilder(this.instrument.getSound())
			.location(location)
			.pitchStep(this.step)
			.volume(this.volume)
			.category(SoundCategory.RECORDS)
			.play();

		// TODO: SHOW PARTICLE

	}

	public boolean exists() {
		return placerUUID != null;
	}
}
