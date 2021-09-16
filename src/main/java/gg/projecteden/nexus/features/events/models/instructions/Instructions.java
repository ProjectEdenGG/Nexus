package gg.projecteden.nexus.features.events.models.instructions;

import gg.projecteden.nexus.utils.PlayerUtils;
import kotlin.Pair;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Data
@RequiredArgsConstructor
public
class Instructions {
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
		instruction(player -> PlayerUtils.send(player, "&3" + npcName + " &7> &f" + message), calculateDelay(message));
		return this;
	}

	public Instructions player(String message) {
		instruction(player -> PlayerUtils.send(player, "&b&lYOU &7> &f" + message), calculateDelay(message));
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

	private int calculateDelay(String message) {
		// TODO
		return 60;
	}

}
