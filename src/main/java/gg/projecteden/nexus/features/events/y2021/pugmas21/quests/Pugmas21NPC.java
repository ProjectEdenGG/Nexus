package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

@Getter
@AllArgsConstructor
public enum Pugmas21NPC implements InteractableNPC {
	BELLAMY("Bellamy", 4413),
	CAPTAIN_NERISSA("Captain Nerissa", 4402),
	CASSIA("Cassia", 4408),
	CEDAR("Cedar", -1),
	ESTELLE("Estelle", -1),
	FISH_VENDOR("Fish Vendor", 4412),
	FLINT("Flint", 4405),
	GLORIA("Gloria", 4410),
	JUNIPER("Juniper", 4409),
	MYSTERIOUS_WOMAN("Mysterious Woman", 4418),
	OMALLEY("O'Malley", 4414),
	PANSY("Pansy", 4401),
	PINE("Pine", 4403),
	REED("Reed", -1),
	ROWAN("Rowan", 4404),
	WARREN("Warren", 4400),
	;


	private final String name;
	private final int npcId;

	public static Pugmas21NPC of(NPC npc) {
		return of(npc.getId());
	}

	public static Pugmas21NPC of(int id) {
		for (Pugmas21NPC npc : values())
			if (npc.getNpcId() == id)
				return npc;
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}
}
