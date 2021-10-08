package gg.projecteden.nexus.features.quests.interactable.instructions;

import gg.projecteden.nexus.features.quests.users.Quester;
import gg.projecteden.nexus.utils.Tasks;
import kotlin.Pair;
import lombok.Data;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Data
public class DialogInstance {
	private final Quester quester;
	private final Dialog dialog;
	private final AtomicInteger taskId = new AtomicInteger(-1);

	private final Iterator<Pair<Consumer<Quester>, Integer>> iterator;
	private final AtomicReference<Runnable> runner;

	public DialogInstance(Quester quester, Dialog dialog) {
		this.quester = quester;
		this.dialog = dialog;

		this.iterator = dialog.getInstructions().iterator();

		this.runner = new AtomicReference<>() {{
			set(() -> {
				if (!iterator.hasNext() || quester == null || !quester.isOnline()) {
					taskId.set(-1);
					return;
				}

				final var next = iterator.next();

				if (next == null) {
					taskId.set(-1);
					return;
				}

				if (next.getFirst() != null)
					next.getFirst().accept(quester);

				if (!iterator.hasNext()) {
					taskId.set(-1);
					return;
				}

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
