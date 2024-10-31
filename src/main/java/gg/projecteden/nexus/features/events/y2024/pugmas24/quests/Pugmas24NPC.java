package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum Pugmas24NPC implements InteractableNPC {
	BLACKSMITH("Blacksmith", 5340),
	ANGLER("Angler", 5341),
	TICKET_MASTER("Ticket Master", 5358),
	TICKET_MASTER_HUB("Ticket Master", 5359),
	// TODO
	;

	private final String name;
	private final int npcId;
	private final Predicate<NPC> predicate;

	Pugmas24NPC(String name, int npcId) {
		this(name, npcId, npcId <= 0 ? null : npc -> npc.getId() == npcId);
	}

	Pugmas24NPC(String name, Predicate<NPC> predicate) {
		this(name, -1, predicate);
	}

	@Override
	public String toString() {
		return getName();
	}
}
