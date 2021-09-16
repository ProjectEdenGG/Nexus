package gg.projecteden.nexus.features.events.models.instructions;

import gg.projecteden.nexus.utils.Tasks;
import kotlin.Pair;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Data
public class InstructionsInstance {
	private final Instructions instructions;
	private final Player player;
	private final AtomicInteger taskId = new AtomicInteger(-1);

	private final Iterator<Pair<Consumer<Player>, Integer>> iterator;
	private final AtomicReference<Runnable> runner;

	public InstructionsInstance(Instructions instructions, Player player) {
		this.instructions = instructions;
		this.player = player;

		this.iterator = instructions.getInstructions().iterator();

		this.runner = new AtomicReference<>() {{
			set(() -> {
				if (!iterator.hasNext() || player == null || !player.isOnline()) {
					taskId.set(-1);
					return;
				}

				final var next = iterator.next();

				if (next == null) {
					taskId.set(-1);
					return;
				}

				if (next.getFirst() != null)
					next.getFirst().accept(player);

				taskId.set(Tasks.wait(next.getSecond(), get()));
			});
		}};

		runner.get().run();
	}

	public void advance() {
		Tasks.cancel(taskId.get());
		runner.get().run();
	}

}
