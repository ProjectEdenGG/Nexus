package gg.projecteden.nexus.features.events.models.instructions;

import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
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
public class Instructions {
	private final InstructionNPC npc;
	private final List<Pair<Consumer<Player>, Integer>> instructions = new ArrayList<>();

	private static final Map<UUID, InstructionsInstance> instances = new HashMap<>();

	public void execute(Player player) {
		final InstructionsInstance instructions = instances.get(player.getUniqueId());

		if (instructions == null || instructions.getTaskId().get() == -1)
			instances.put(player.getUniqueId(), npc.getInstructions().send(player));
		else
			instructions.advance();
	}

	public static Instructions from(InstructionNPC npc) {
		return new Instructions(npc);
	}

	public Instructions npc(String message) {
		return npc(npc, message);
	}

	public Instructions npc(InstructionNPC npc, String message) {
		return npc(npc == null ? "NPC" : npc.getName(), message);
	}

	public Instructions npc(String npcName, String message) {
		instruction(player -> PlayerUtils.send(player, "&3" + npcName + " &7> &f" + interpolate(message, player)), calculateDelay(message));
		return this;
	}

	public Instructions player(String message) {
		instruction(player -> PlayerUtils.send(player, "&b&lYOU &7> &f" + interpolate(message, player)), calculateDelay(message));
		return this;
	}

	public Instructions wait(int ticks) {
		instruction(null, ticks);
		return this;
	}

	public Instructions thenRun(Consumer<Player> task) {
		instruction(task, 0);
		return this;
	}

	public InstructionsInstance send(Player player) {
		return new InstructionsInstance(this, player);
	}

	private void instruction(Consumer<Player> task, int delay) {
		instructions.add(new Pair<>(task, delay));
	}

	@AllArgsConstructor
	public enum Variable {
		PLAYER_NAME(Nickname::of),
		;

		private Function<Player, String> interpolater;

		public String interpolate(String message, Player player) {
			return message.replaceAll(placeholder(), interpolater.apply(player));
		}

		public String placeholder() {
			return "\\{\\{" + name() + "}}";
		}
	}

	@NotNull
	private String interpolate(String message, Player player) {
		for (Variable variable : Variable.values())
			message = variable.interpolate(message, player);

		return message;
	}

	private int calculateDelay(String message) {
		// TODO
		return 200;
	}

}
