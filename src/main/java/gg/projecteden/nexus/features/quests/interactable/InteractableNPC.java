package gg.projecteden.nexus.features.quests.interactable;

import net.citizensnpcs.api.npc.NPC;

import java.util.function.Predicate;

public interface InteractableNPC extends Interactable {

	int getNpcId();

	Predicate<NPC> getPredicate();

}
