package gg.projecteden.nexus.features.fakenpc.events;

import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import org.bukkit.entity.Player;

public class FakeNPCLeftClickEvent extends FakeNPCClickEvent {

	public FakeNPCLeftClickEvent(FakeNPC npc, Player leftClicker) {
		super(npc, leftClicker);
	}
}
