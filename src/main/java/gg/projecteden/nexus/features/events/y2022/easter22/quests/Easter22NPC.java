package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

@Getter
@AllArgsConstructor
public enum Easter22NPC implements InteractableNPC {
	EASTER_BUNNY("Easter Bunny", 4672),
	BASIL("Basil", 4673),
	;

	private final String name;
	private final int npcId;

	public static Easter22NPC of(NPC npc) {
		return of(npc.getId());
	}

	public static Easter22NPC of(int id) {
		for (Easter22NPC npc : values())
			if (npc.getNpcId() == id)
				return npc;
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}
}
