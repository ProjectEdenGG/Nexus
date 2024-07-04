package gg.projecteden.nexus.features.quests.interactable.instructions;

import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.ComponentLike;
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
import java.util.function.Function;

import static gg.projecteden.nexus.utils.PlayerUtils.playerHas;

@Data
@RequiredArgsConstructor
public class Dialog {
	private final Interactable interactable;
	private final List<Instruction> instructions = new ArrayList<>();

	private static final Map<UUID, DialogInstance> instances = new HashMap<>();

	public static Dialog from(Interactable interactable) {
		return new Dialog(interactable);
	}

	private Dialog instruction(Consumer<Quester> task, long delay) {
		instructions.add(new Instruction(task, delay));
		return this;
	}

	@Data
	@AllArgsConstructor
	public static class Instruction {
		private final Consumer<Quester> task;
		private final long delay;
	}

	public Dialog npc(String message) {
		return npc(quester -> message);
	}

	public Dialog npc(Interactable npc, String message) {
		return instruction(quester -> quester.sendMessage(getNpcMessage(quester, npc, message)), calculateDelay(message));
	}

	public Dialog npc(Function<Quester, String> task) {
		return npc(task, DEFAULT_DELAY);
	}

	public Dialog npc(Function<Quester, String> task, long delay) {
		return instruction(quester -> quester.sendMessage(getNpcMessage(quester, interactable, task.apply(quester))), delay);
	}

	@NotNull
	public String getNpcMessage(Quester quester, Interactable npc, String message) {
		final String name = npc == null ? "NPC" : npc.getName();
		return "&3" + name + " &7> &f" + interpolate(message, quester);
	}

	public Dialog player(String message) {
		return instruction(quester -> PlayerUtils.send(quester, "&b&lYOU &7> &f" + interpolate(message, quester)), calculateDelay(message));
	}

	public Dialog raw(String message) {
		return instruction(quester -> PlayerUtils.send(quester, message), calculateDelay(message));
	}

	public Dialog raw(ComponentLike message) {
		return instruction(quester -> PlayerUtils.send(quester, message), calculateDelay(AdventureUtils.asPlainText(message)));
	}

	public Dialog pause(long ticks) {
		return instruction(null, ticks);
	}

	public Dialog thenRun(Consumer<Quester> task) {
		return instruction(task, 0);
	}

	public Dialog reward(QuestReward reward) {
		return instruction(reward::apply, -1);
	}

	public Dialog reward(QuestReward reward, int amount) {
		return instruction(quester -> reward.apply(quester, amount), -1);
	}

	public Dialog give(Material material) {
		return give(new ItemStack(material));
	}

	public Dialog give(Material material, int amount) {
		return give(new ItemStack(material, amount));
	}

	public Dialog give(ItemBuilder itemBuilder) {
		return give(itemBuilder.build());
	}

	public Dialog give(ItemStack item) {
		return instruction(quester -> PlayerUtils.giveItem(quester, item), -1);
	}

	public Dialog give(CustomModel... items) {
		for (CustomModel item : items)
			give(item.getItem());
		return this;
	}

	public Dialog give(QuestItem... items) {
		for (QuestItem item : items)
			give(item.get());
		return this;
	}

	public Dialog give(QuestItem item, int amount) {
		return give(new ItemBuilder(item.get()).amount(amount));
	}

	public Dialog giveIfMissing(Material material) {
		return giveIfMissing(new ItemStack(material));
	}

	public Dialog giveIfMissing(QuestItem item) {
		return giveIfMissing(item.get());
	}

	public Dialog giveIfMissing(ItemStack item) {
		return instruction(quester -> {
			if (!playerHas(quester, item))
				PlayerUtils.giveItem(quester, item);
		}, -1);
	}

	public Dialog take(Material material) {
		return give(new ItemStack(material));
	}

	public Dialog take(Material material, int amount) {
		return give(new ItemStack(material, amount));
	}

	public Dialog take(ItemStack item) {
		return instruction(quester -> PlayerUtils.removeItem(quester.getOnlinePlayer(), item), -1);
	}

	public DialogInstance send(Quester quester) {
		return new DialogInstance(quester, this);
	}

	@AllArgsConstructor
	public enum Variable {
		PLAYER_NAME((quester, interactable) -> Nickname.of(quester)),
		NPC_NAME((quester, interactable) -> interactable.getName()),
		;

		private final BiFunction<Quester, Interactable, String> interpolator;

		public String interpolate(String message, Quester quester, Interactable interactable) {
			return message.replaceAll(placeholder(), interpolator.apply(quester, interactable));
		}

		public String placeholder() {
			return "\\{\\{" + name() + "}}";
		}

		@Override
		public String toString() {
			return "{{" + name() + "}}";
		}
	}

	@NotNull
	private String interpolate(String message, Quester quester) {
		for (Variable variable : Variable.values())
			message = variable.interpolate(message, quester, interactable);

		return message;
	}

	public static final int DEFAULT_DELAY = 200;

	private int calculateDelay(String message) {
		// TODO
		return DEFAULT_DELAY;
	}

	private static final List<String> genericGreetings = List.of("Hello!", "Hi!", "Greetings", "Hey there", "Hey!",
		"Hi there!", "G'day", "Good to see you", "Nice to see you");

	public static DialogInstance genericGreeting(Quester quester, Interactable interactable) {
		List<String> genericGreetings = new ArrayList<>(Dialog.genericGreetings);

		if (quester.getLocation() != null) {
			final long time = quester.getLocation().getWorld().getTime();
			if (time < 6000 || time > 18000)
				genericGreetings.add("Good morning");
			else if (time <= 12000)
				genericGreetings.add("Good afternoon");
			else
				genericGreetings.add("Good evening");
		}

		final String message = RandomUtils.randomElement(genericGreetings);

		return new DialogInstance(quester, new Dialog(interactable).npc(message));
	}

}
