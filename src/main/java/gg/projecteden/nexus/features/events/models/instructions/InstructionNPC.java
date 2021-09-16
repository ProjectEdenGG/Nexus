package gg.projecteden.nexus.features.events.models.instructions;

import org.bukkit.entity.Player;

public interface InstructionNPC {
	String getName();

	int getNpcId();

	Instructions getInstructions();

	default void execute(Player player) {
		if (getInstructions().getNpc() != this)
			return;

		getInstructions().execute(player);
	}


}
