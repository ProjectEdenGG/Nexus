package gg.projecteden.nexus.features.quests.interactable;

import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.CitizensUtils.NPCFinder;
import net.citizensnpcs.api.npc.NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface InteractableNPC extends Interactable {

	int getNpcId();

	Predicate<NPC> getPredicate();

	static <T extends InteractableNPC> List<NPC> getAllNPCs(Class<T> clazz) {
		List<NPC> npcs = new ArrayList<>();

		for (InteractableNPC npc : clazz.getEnumConstants())
			if (npc.getNpcId() != -1)
				npcs.add(CitizensUtils.getNPC(npc.getNpcId()));
			else
				npcs.addAll(NPCFinder.builder().predicate(npc.getPredicate()).find());

		return npcs;
	}

}
