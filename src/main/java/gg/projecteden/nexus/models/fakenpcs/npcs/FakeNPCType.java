package gg.projecteden.nexus.models.fakenpcs.npcs;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
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

	public <T extends FakeNPC> T create(Player owner, String name) {
		try {
			final T npc = (T) clazz.getConstructor(Player.class, String.class).newInstance(owner, name);
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
