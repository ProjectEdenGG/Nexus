package gg.projecteden.nexus.features.fakenpc.events;

import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import org.bukkit.entity.Player;

public class FakeNPCRightClickEvent extends FakeNPCClickEvent {

	public FakeNPCRightClickEvent(FakeNPC npc, Player rightClicker) {
		super(npc, rightClicker);
	}
}
