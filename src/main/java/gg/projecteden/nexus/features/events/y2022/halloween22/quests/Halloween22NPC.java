package gg.projecteden.nexus.features.events.y2022.halloween22.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum Halloween22NPC implements InteractableNPC {
	;

	private final String name;
	private final int npcId;
	private final Predicate<NPC> predicate;

	Halloween22NPC(String name, int npcId) {
		this(name, npcId, npcId <= 0 ? null : npc -> npc.getId() == npcId);
	}

	Halloween22NPC(String name, Predicate<NPC> predicate) {
		this(name, -1, predicate);
	}

	@Override
	public String toString() {
		return getName();
	}

}
