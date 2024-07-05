package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum VuLan24NPC implements InteractableNPC {
	CAPTAIN_LAI("Captain Lai", 5176),
	MAYOR_HOA("Mayor Hoa", 5177),
	TRUONG("Truong", 5178),
	XUAM("Xuam", 5179),
	ANH("Anh", 5181), // Community Quest
	HANH("Hanh", 5182),
	PHUONG("Phuong", 5183),
	THAM("Tham", 5241),
	BOAT_SALESMAN("Boat Salesman", 5240),
	MINER("Miner", 5309),
	TOUR_GUIDE("Tour Guide", 5311)
	;

	private final String name;
	private final int npcId;
	private final Predicate<NPC> predicate;

	VuLan24NPC(String name, int npcId) {
		this(name, npcId, npcId <= 0 ? null : npc -> npc.getId() == npcId);
	}

	VuLan24NPC(String name, Predicate<NPC> predicate) {
		this(name, -1, predicate);
	}

	@Override
	public String toString() {
		return getName();
	}

}
