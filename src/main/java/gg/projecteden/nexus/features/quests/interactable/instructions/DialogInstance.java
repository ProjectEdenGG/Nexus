package gg.projecteden.nexus.features.quests.interactable.instructions;

import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog.Instruction;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class DialogInstance {
	private final Quester quester;
	private final Dialog dialog;
	private final AtomicInteger taskId = new AtomicInteger(-1);

	private final Queue<Instruction> iterator;
	private final AtomicReference<Runnable> runner;

	public DialogInstance(Quester quester, Dialog dialog) {
		this.quester = quester;
		this.dialog = dialog;

		this.iterator = new LinkedList<>(dialog.getInstructions());

		this.runner = new AtomicReference<>() {{
			set(() -> {
				while (true) {
					if (quester == null || !quester.isOnline()) {
						taskId.set(-1);
						return;
					}

					final var next = iterator.poll();

					if (next == null) {
						taskId.set(-1);
						return;
					}

					if (next.getTask() != null)
						next.getTask().accept(quester);

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

	public void advance() {
		Tasks.cancel(taskId.get());
		runner.get().run();
	}

}
