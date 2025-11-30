package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomEmoji;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
@RequiredArgsConstructor
public class Pugmas25Cutscene {
	private final List<Instruction> instructions = new ArrayList<>();

	private Pugmas25Cutscene instruction(long delay, Consumer<Player> task) {
		instructions.add(new Instruction(task, delay));
		return this;
	}

	@Data
	@AllArgsConstructor
	public static class Instruction {
		private final Consumer<Player> task;
		private final long delay;
	}

	public Pugmas25Cutscene next(TickTime tickTime, Consumer<Player> task) {
		return next(tickTime.get(), task);
	}

	public Pugmas25Cutscene next(long delayTicks, Consumer<Player> task) {
		return instruction(delayTicks, task);
	}

	public Pugmas25Cutscene fade(TickTime tickTime, int stayTicks) {
		return fade(tickTime.get(), stayTicks);
	}

	public Pugmas25Cutscene fade(long delayTicks, int stayTicks) {
		return instruction(delayTicks, _player -> {
			new TitleBuilder()
				.title(CustomEmoji.SCREEN_BLACK.getChar())
				.fade(TickTime.TICK.x(10))
				.players(_player)
				.stay(TickTime.TICK.x(stayTicks))
				.send();
		});
	}

	public Pugmas25Cutscene fade(TickTime tickTime, String title, int stayTicks) {
		return fade(tickTime.get(), title, stayTicks);
	}

	public Pugmas25Cutscene fade(long delayTicks, String title, int stayTicks) {
		return instruction(delayTicks, _player -> {
			new TitleBuilder()
				.title(CustomEmoji.SCREEN_BLACK.getChar())
				.subtitle(title)
				.fade(TickTime.TICK.x(10))
				.players(_player)
				.stay(TickTime.TICK.x(stayTicks))
				.send();
		});
	}

	public Pugmas25CutsceneInstance start(Player player) {
		return new Pugmas25CutsceneInstance(player, this);
	}


}
