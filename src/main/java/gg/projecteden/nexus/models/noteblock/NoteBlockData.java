package gg.projecteden.nexus.models.noteblock;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.noteblocks.NoteBlockInstrument;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.utils.MathUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;

import java.util.UUID;

@Data
@NoArgsConstructor
public class NoteBlockData {
	UUID placerUUID = null;
	NoteBlockInstrument instrument = NoteBlockInstrument.PIANO;
	int step = 0;
	double volume = 1.0;
	Instrument blockInstrument;
	int blockStep;

	public NoteBlockData(UUID uuid, Block block) {
		NoteBlock noteBlock = ((NoteBlock) block.getBlockData());
		this.placerUUID = uuid;
		this.instrument = NoteBlockInstrument.getInstrument(block);
		this.step = noteBlock.getNote().getId();
		this.blockInstrument = noteBlock.getInstrument();
		this.blockStep = this.step;
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

		new ParticleBuilder(Particle.NOTE)
			.location(location.toCenterLocation().add(0, 0.5, 0))
			.offset(this.step / 24.0, 0, 0)
			.count(0)
			.spawn();
	}

	public boolean exists() {
		return placerUUID != null;
	}

	public Note getBlockNote() {
		return new Note(this.getBlockStep());
	}
}
