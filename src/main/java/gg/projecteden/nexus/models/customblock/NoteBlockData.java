package gg.projecteden.nexus.models.customblock;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.NoteBlockInstrument;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;

@Data
@NoArgsConstructor
public class NoteBlockData {
	private NoteBlockInstrument instrument = NoteBlockInstrument.PIANO;
	private Block block;
	private NoteBlock noteBlock;
	private int step = 0;
	private boolean powered;
	private boolean interacted;

	public NoteBlockData(Block block) {
		if (!(block.getBlockData() instanceof NoteBlock noteBlock))
			throw new IllegalArgumentException("NoteBlockData must be instance of NoteBlock");
		this.block = block;
		this.noteBlock = noteBlock;
		this.instrument = NoteBlockInstrument.getInstrument(block);
		this.step = noteBlock.getNote().getId();
		this.powered = noteBlock.isPowered();
	}

	public void incrementStep() {
		int _step = (this.step + 1);
		this.step = _step > 24 ? 0 : _step;
		update();
	}

	public void decrementStep() {
		int _step = (this.step - 1);
		this.step = _step < 0 ? 24 : _step;
		update();
	}

	public void setStep(int step) {
		this.step = MathUtils.clamp(step, 0, 24);
		update();
	}

	private void update() {
		noteBlock.setNote(new Note(this.step));
		block.setBlockData(noteBlock, true);
	}

	public void play(Location location, Player debugger) {
		this.instrument = NoteBlockInstrument.getInstrument(location.getBlock());

		SoundBuilder noteBlockSound = new SoundBuilder(this.instrument.getSound(location.getBlock()))
			.location(location)
			.pitchStep(this.step)
			.category(SoundCategory.RECORDS);

		// Ignore particle if instrument == MOB_SOUND
		if (this.instrument != NoteBlockInstrument.CUSTOM_MOB_HEAD) {
			new ParticleBuilder(Particle.NOTE)
				.location(location.toCenterLocation().add(0, 0.5, 0))
				.offset(this.step / 24.0, 0, 0)
				.count(0)
				.spawn();
		}

		CustomBlockUtils.debug(debugger, "play: Instrument=" + this.instrument + ", Note=" + this.step + ", Powered=" + this.powered);
		noteBlockSound.play();

		if (this.interacted)
			this.powered = false;

		this.interacted = false;
	}

	public @NonNull BlockData getBlockData(Location location) {
		return CustomBlock.NOTE_BLOCK.get().getBlockData(BlockFace.UP, location.getBlock().getRelative(BlockFace.DOWN));
	}

	public String getInstrumentName() {
		if (this.instrument == NoteBlockInstrument.CUSTOM_MOB_HEAD) {
			return "Mob Head"; // TODO: Display actual mob head name?
		}

		return StringUtils.camelCase(this.instrument);
	}
}
