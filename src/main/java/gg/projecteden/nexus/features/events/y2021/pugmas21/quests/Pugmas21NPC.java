package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

@Getter
@AllArgsConstructor
public enum Pugmas21NPC implements InteractableNPC {
	GLORIA("Gloria", 4410),
	ROWAN("Rowan", 4404),
	FISH_VENDOR("Fish Vendor", 4412),
	OMALLEY("O'Malley", 4414),
	;

	private final String name;
	private final int npcId;

	public static Pugmas21NPC of(NPC npc) {
		return of(npc.getId());
	}

	public static Pugmas21NPC of(int id) {
		System.out.println("Looking for NPC #" + id);
		for (Pugmas21NPC npc : values())
			if (npc.getNpcId() == id)
				return npc;
		return null;
	}

}
