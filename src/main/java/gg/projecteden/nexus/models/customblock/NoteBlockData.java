package gg.projecteden.nexus.models.customblock;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.NoteBlockInstrument;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;

@Data
@NoArgsConstructor
public class NoteBlockData {
	private NoteBlockInstrument instrument = NoteBlockInstrument.PIANO;
	private int step = 0;
	private double volume = 1.0;
	private boolean powered;
	private boolean interacted;

	public NoteBlockData(Block block) {
		NoteBlock noteBlock = ((NoteBlock) block.getBlockData());
		this.instrument = NoteBlockInstrument.getInstrument(block);
		this.step = noteBlock.getNote().getId();
		this.powered = noteBlock.isPowered();
	}

	public void incrementStep() {
		int _step = (this.step + 1);
		this.step = _step > 24 ? 0 : _step;
	}

	public void decrementStep() {
		int _step = (this.step - 1);
		this.step = _step < 0 ? 24 : _step;
	}

	public void setStep(int step) {
		this.step = MathUtils.clamp(step, 0, 24);
	}

	public void incrementVolume() {
		double _volume = (this.volume + 0.1);
		this.volume = _volume > 2.0 ? 0.1 : _volume;
	}

	public void decrementVolume() {
		double _volume = (this.volume - 0.1);
		this.volume = _volume < 0.1 ? 2.0 : _volume;
	}

	public void setVolume(double volume) {
		this.volume = MathUtils.clamp(volume, 0.0, 2.0);
	}

	public void play(Location location) {
		this.instrument = NoteBlockInstrument.getInstrument(location.getBlock());

		SoundBuilder noteBlockSound = new SoundBuilder(this.instrument.getSound(location.getBlock()))
			.location(location)
			.volume(this.volume)
			.category(SoundCategory.RECORDS);

		// Ignore pitch & particle if instrument == MOB_SOUND
		if (this.instrument != NoteBlockInstrument.CUSTOM_MOB_HEAD) {
			noteBlockSound.pitchStep(this.step);

			new ParticleBuilder(Particle.NOTE)
				.location(location.toCenterLocation().add(0, 0.5, 0))
				.offset(this.step / 24.0, 0, 0)
				.count(0)
				.spawn();
		}

		noteBlockSound.play();

		this.setInteracted(false);
	}
}
