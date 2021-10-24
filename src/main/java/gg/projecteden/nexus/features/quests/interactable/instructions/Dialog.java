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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Data
@RequiredArgsConstructor
public class Dialog {
	private final Interactable interactable;
	private final List<Pair<Consumer<Quester>, Integer>> instructions = new ArrayList<>();

	private static final Map<UUID, DialogInstance> instances = new HashMap<>();

	public static Dialog from(Interactable interactable) {
		return new Dialog(interactable);
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

	public Dialog give(Material material) {
		return give(new ItemStack(material));
	}

	public Dialog give(Material material, int amount) {
		return give(new ItemStack(material, amount));
	}

	public Dialog give(ItemStack item) {
		instruction(quester -> PlayerUtils.giveItem(quester, item), 0);
		return this;
	}

	@AllArgsConstructor
	public enum Variable {
		PLAYER_NAME((quester, interactable) -> Nickname.of(quester)),
		NPC_NAME(((quester, interactable) -> interactable.getName())),
		;

		private BiFunction<Quester, Interactable, String> interpolator;

		public String interpolate(String message, Quester quester, Interactable interactable) {
			return message.replaceAll(placeholder(), interpolator.apply(quester, interactable));
		}

		public String placeholder() {
			return "\\{\\{" + name() + "}}";
		}
	}

	@NotNull
	private String interpolate(String message, Quester quester) {
		for (Variable variable : Variable.values())
			message = variable.interpolate(message, quester, interactable);

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
