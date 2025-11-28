package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum Pugmas25NPC implements InteractableNPC {
	BLACKSMITH("Blacksmith", 5591),
	TINKERER("Tinkerer", 5597),
	ANGLER("Angler", 5590),
	TICKET_MASTER("Ticket Master", 5587),
	TICKET_MASTER_HUB("Ticket Master", Nexus.getEnv() == Env.UPDATE ? 5634 : 5661),
	INN_KEEPER("Inn Keeper", 5630),
	POWER("Power", 5589),
	GNOME("Gnome", 5598),
	ELF("Elf", 5631),
	KID("Kid", 5632),
	MAYOR("Mayor", 5636),
	AERONAUT("Aeronaut", 5664),
	RESIDENT("Resident", npc -> MathUtils.isBetween(npc.getId(), 5637, 5657)),
	;

	private final String name;
	private final int npcId;
	private final Predicate<NPC> predicate;

	Pugmas25NPC(String name, int npcId) {
		this(name, npcId, npcId <= 0 ? null : npc -> npc.getId() == npcId);
	}

	Pugmas25NPC(String name, Predicate<NPC> predicate) {
		this(name, -1, predicate);
	}

	@Override
	public String toString() {
		return getName();
	}
}
