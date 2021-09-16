package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.features.events.models.InstructionNPC;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Getter;

import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL;

@Getter
public enum Pugmas21InstructionNPC implements InstructionNPC {
	JOE("Joe", 1234, instructions -> instructions
		.npc("You're a nerd")
		.player("No you!")
		.thenRun(player -> {
			new SoundBuilder(BLOCK_NOTE_BLOCK_BELL).receiver(player).play();
			PlayerUtils.send(player, "boop");
		})
		.wait(50)
		.thenRun(player -> runCommandAsConsole("slap " + player.getName()))
		.npc("testing 123")
	),
	BOBBY("Bobby", 4321, instructions -> instructions
		.npc("You're still a nerd")
		.player("No you!")
	)
	;

	private final String name;
	private final int npcId;
	private final Instructions instructions;

	Pugmas21InstructionNPC(String name, int npcId, Consumer<Instructions> instructions) {
		this.name = name;
		this.npcId = npcId;
		this.instructions = Instructions.from(this);
		instructions.accept(this.instructions);
	}

}
