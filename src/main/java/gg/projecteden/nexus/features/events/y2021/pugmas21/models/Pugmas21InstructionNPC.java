package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.nexus.features.events.models.instructions.InstructionNPC;
import gg.projecteden.nexus.features.events.models.instructions.Instructions;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

import java.util.function.Consumer;

@Getter
public enum Pugmas21InstructionNPC implements InstructionNPC {
	GLORIA("Gloria", 4410, instructions -> instructions
		.npc("Well ain't you a sight for these tired old eyes. I didn't think anyone would be coming this year with the storm and everything.")
		.npc("Where are my manners, my name is Gloria and welcome to Pugmas!")
		.player("Oh... ah, hey there! My name's {{PLAYER_NAME}}. Is this weather normal for this time of year?")
		.npc("No, it isn't, or at least it didn't use to be. Ever since that incident with the crystal, the weather has been out of control and everything seems to be falling apart!")
		.player("I'm sorry to hear that, is there anything I can do?")
		.npc("Unfortunately not, I'm afraid. The only one who would be able to fix the crystal is its guardian...")
		.npc("Hey, you seem like a fairly nice person. Maybe you could help out. Would you mind helping an old lady out?")
		.player("Of course! I'm more than happy to help.")
		.npc("Could you go around and ask the people of the town if they've heard about a deer in the woods? These legs ain't what they used to be.")
		.player("Sure thing. I'll come back if I find anything.")
	),
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

	public static Pugmas21InstructionNPC of(NPC npc) {
		return of(npc.getId());
	}

	public static Pugmas21InstructionNPC of(int id) {
		for (Pugmas21InstructionNPC npc : values())
			if (npc.getNpcId() == id)
				return npc;
		return null;
	}

}
