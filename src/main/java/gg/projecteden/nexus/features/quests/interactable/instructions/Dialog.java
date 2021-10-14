package gg.projecteden.nexus.features.quests.interactable.instructions;

import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.users.Quester;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
@RequiredArgsConstructor
public class Dialog {
	private final Interactable interactable;
	private final List<Pair<Consumer<Quester>, Integer>> instructions = new ArrayList<>();

	private static final Map<UUID, DialogInstance> instances = new HashMap<>();

	public static Dialog from(Interactable npc) {
		return new Dialog(npc);
	}

	public Dialog npc(String message) {
		return npc(interactable, message);
	}

	public Dialog npc(Interactable npc, String message) {
		return npc(npc == null ? "NPC" : npc.getName(), message);
	}

	public Dialog npc(String npcName, String message) {
		instruction(quester -> PlayerUtils.send(quester, "&3" + npcName + " &7> &f" + interpolate(message, quester)), calculateDelay(message));
		return this;
	}

	public Dialog player(String message) {
		instruction(quester -> PlayerUtils.send(quester, "&b&lYOU &7> &f" + interpolate(message, quester)), calculateDelay(message));
		return this;
	}

	public Dialog wait(int ticks) {
		instruction(null, ticks);
		return this;
	}

	public Dialog thenRun(Consumer<Quester> task) {
		instruction(task, 0);
		return this;
	}

	public DialogInstance send(Quester quester) {
		return new DialogInstance(quester, this);
	}

	private void instruction(Consumer<Quester> task, int delay) {
		instructions.add(new Pair<>(task, delay));
	}

	@AllArgsConstructor
	public enum Variable {
		PLAYER_NAME(Nickname::of),
		;

		private Function<Quester, String> interpolater;

		public String interpolate(String message, Quester quester) {
			return message.replaceAll(placeholder(), interpolater.apply(quester));
		}

		public String placeholder() {
			return "\\{\\{" + name() + "}}";
		}
	}

	@NotNull
	private String interpolate(String message, Quester quester) {
		for (Variable variable : Variable.values())
			message = variable.interpolate(message, quester);

		return message;
	}

	private int calculateDelay(String message) {
		// TODO
		return 200;
	}

	public static DialogInstance genericGreeting(Quester quester, Interactable interactable) {
		final String message = RandomUtils.randomElement("Hello!", "Hi!", "Greetings", "Hey there");
		return new DialogInstance(quester, new Dialog(interactable).npc(message));
	}

}