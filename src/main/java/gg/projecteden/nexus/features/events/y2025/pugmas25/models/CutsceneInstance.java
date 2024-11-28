package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Cutscene.Instruction;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class CutsceneInstance {
	private final Player player;
	private final Cutscene cutscene;
	private final AtomicInteger taskId = new AtomicInteger(-1);

	private final Queue<Instruction> iterator;
	private final AtomicReference<Runnable> runner;

	public CutsceneInstance(Player player, Cutscene cutscene) {
		this.player = player;
		this.cutscene = cutscene;

		this.iterator = new LinkedList<>(cutscene.getInstructions());

		this.runner = new AtomicReference<>() {{
			set(() -> {
				while (true) {
					if (player == null || !player.isOnline()) {
						taskId.set(-1);
						return;
					}

					final var next = iterator.poll();

					if (next == null) {
						taskId.set(-1);
						return;
					}

					if (next.getTask() != null)
						next.getTask().accept(player);

					final var peek = iterator.peek();

					if (peek == null) {
						taskId.set(-1);
						return;
					}

					if (peek.getDelay() == -1)
						continue;

					taskId.set(Tasks.wait(peek.getDelay(), get()));
					break;
				}
			});
		}};

		runner.get().run();
	}
}
