package gg.projecteden.nexus.features.events.models;

import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface InteractableNPC {
	String getName();

	int getNpcId();

	Dialogue getDialogue();

	default void interact(Player player) {
		getDialogue().send(player);
	}

	@RequiredArgsConstructor
	class Dialogue {
		private final InteractableNPC npc;
		private final List<Pair<Consumer<Player>, Integer>> instructions = new ArrayList<>();

		public static Dialogue from(InteractableNPC npc) {
			return new Dialogue(npc);
		}

		public Dialogue npc(String message) {
			return npc(npc, message);
		}

		public Dialogue npc(InteractableNPC npc, String message) {
			return npc(npc == null ? "NPC" : npc.getName(), message);
		}

		public Dialogue npc(String npcName, String message) {
			instruction(player -> PlayerUtils.send(player, "&3" + npcName + " &7> &f" + message), calculateDelay(message));
			return this;
		}

		public Dialogue player(String message) {
			instruction(player -> PlayerUtils.send(player, "&b&lYOU &7> &f" + message), calculateDelay(message));
			return this;
		}

		public Dialogue wait(int ticks) {
			instruction(nothing(), ticks);
			return this;
		}

		public Dialogue thenRun(Consumer<Player> task) {
			instruction(task, 0);
			return this;
		}

		public Dialogue send(Player player) {
			AtomicInteger wait = new AtomicInteger();

			for (Pair<Consumer<Player>, Integer> instruction : instructions)
				Tasks.wait(wait.getAndAdd(instruction.getSecond()), () -> instruction.getFirst().accept(player));

			return this;
		}

		@NotNull
		private Consumer<Player> nothing() {
			return $ -> {};
		}

		private void instruction(Consumer<Player> task, int delay) {
			instructions.add(new Pair<>(task, delay));
		}

		private int calculateDelay(String message) {
			// TODO
			return 60;
		}

	}

}
