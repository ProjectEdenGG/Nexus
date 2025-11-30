package gg.projecteden.nexus.features.events.y2025.pugmas25.features.fairgrounds.slotmachine;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.DecoratedPot;

import java.util.List;

@Data
public class SlotMachineColumn {
	int columnIndex;
	SlotMachineColumnStatus status = SlotMachineColumnStatus.INACTIVE;
	List<DecoratedPot> pots;
	long stoppedTick;

	public SlotMachineColumn(int index, List<DecoratedPot> pots) {
		this.columnIndex = index;
		this.pots = pots;
	}

	public boolean isAbleToStop(Pugmas25SlotMachine game) {
		if (status != SlotMachineColumnStatus.RUNNING || columnIndex == 0)
			return true;

		int previousIndex = columnIndex - 1;
		SlotMachineColumn previousColumn = game.columns.get(previousIndex);
		if (previousColumn.getStatus() != SlotMachineColumnStatus.STOPPED)
			return false;

		return previousColumn.getStoppedTick() < (game.getGameTicks() + getRandomTicks(2, 5));
	}

	public boolean canSlow(Pugmas25SlotMachine game) {
		if (status != SlotMachineColumnStatus.RUNNING)
			return false;

		return game.getGameTicks() > getRandomTicks(3, 5);
	}

	public boolean canStop(Pugmas25SlotMachine game) {
		if (status != SlotMachineColumnStatus.SLOWING)
			return false;

		return game.getGameTicks() > getRandomTicks(7, 10);
	}

	private long getRandomTicks(int secondsMin, int secondsMax) {
		return TickTime.SECOND.x(RandomUtils.randomInt((columnIndex + 1) * secondsMin, (columnIndex + 1) * secondsMax));
	}

	public void playSound(Location soundLocation) {
		switch (status) {
			case RUNNING -> new SoundBuilder(Sound.UI_BUTTON_CLICK).location(soundLocation).volume(0.3).pitch(2).play();
			case SLOWING ->
				new SoundBuilder(Sound.UI_BUTTON_CLICK).location(soundLocation).volume(0.3).pitch(1.5).play();
			case STOPPED -> new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).location(soundLocation).volume(0.3).play();
		}
	}

	public enum SlotMachineColumnStatus {
		INACTIVE,
		RUNNING,
		SLOWING,
		STOPPED,
	}
}
