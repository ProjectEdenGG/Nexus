package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCService;
import gg.projecteden.nexus.models.fakenpcs.npcs.types.PlayerNPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public enum FakeNPCType {
	PLAYER(PlayerNPC.class),
	;

	private final Class<? extends FakeNPC> clazz;

	public <T extends FakeNPC> T create(Player owner) {
		try {
			final T npc = (T) clazz.getConstructor(Player.class).newInstance(owner);
			final FakeNPCService service = new FakeNPCService();
			service.cache(npc);
			service.save(npc);
			return npc;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InvalidInputException("Error creating NPC: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}
}
