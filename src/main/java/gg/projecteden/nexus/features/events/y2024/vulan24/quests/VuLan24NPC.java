package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum VuLan24NPC implements InteractableNPC {
	TRUONG("Truong", 5178), // Stone Quest
	XUAM("Xuam", 5179), // Hero Quest
	HANH("Hanh", 5182), // Fishing Quest
	THAM("Tham", 5241), // Pottery Quest
	PHUONG("Phuong", 5183), // Paper Quest

	ANH("Anh", 5181), // Community Quest

	CAPTAIN_LAI_AVONTYRE("Captain Lai", 4928),
	CAPTAIN_LAI_VINH_LUC("Captain Lai", 5176),
	MAYOR_HOA("Mayor Hoa", 5177),
	HAT_SALESMAN("Hat Salesman", 5180),
	BOAT_SALESMAN("Boat Salesman", 5240),
	MINER("Hungry Miner", 5309),
	TOUR_GUIDE("Tour Guide", 5311),
	FLORIST("Florist", 5257),
	STONE_MASON("Stone Mason", 5259),
	PLUNDERED_VILLAGE_VILLAGER("Villager", 5304),
	PLUNDERED_VILLAGE_FARMER("Farmer", 5313),
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
