package me.pugabyte.nexus.features.store.perks;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.nexus.features.listeners.Restrictions.isPerkAllowedAt;
import static me.pugabyte.nexus.utils.StringUtils.getShortLocationString;

@NoArgsConstructor
public class NPCListener implements Listener {

	private static final Set<Integer> ALLOWED_NPCS = new HashSet<>();

	/**
	 * Removes teleportation/spawning limits for an NPC for one tick
	 */
	public static void allowNPC(Integer npc) {
		ALLOWED_NPCS.add(npc);
		Tasks.wait(1, () -> ALLOWED_NPCS.remove(npc));
	}

	/**
	 * Removes teleportation/spawning limits for an NPC for one tick
	 */
	public static void allowNPC(NPC npc) {
		allowNPC(npc.getId());
	}

	@EventHandler
	public void onNpcCreate(PlayerCreateNPCEvent event) {
		Player owner = event.getCreator();
		if (Rank.of(owner).gte(Rank.NOBLE))
			return;

		if (isPerkAllowedAt(owner.getLocation()))
			return;

		event.getNPC().despawn();
		event.getNPC().destroy();
		PlayerUtils.send(owner, "&cYou cannot create NPCs here");
		Nexus.warn("Preventing NPC create: " + event.getNPC().getId() + " from " + owner.getName());
	}

	@EventHandler
	public void onNpcTeleport(NPCTeleportEvent event) {
		UUID uuid = event.getNPC().getTrait(Owner.class).getOwnerId();
		if (uuid == null)
			return;

		if (ALLOWED_NPCS.contains(event.getNPC().getId()))
			return;

		OfflinePlayer owner = PlayerUtils.getPlayer(uuid);

		if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			if (Rank.of(owner).gte(Rank.NOBLE))
				return;

			if (isPerkAllowedAt(event.getTo()))
				return;

			event.setCancelled(true);
			PlayerUtils.send(owner, "&cYou cannot teleport NPCs here");
			Nexus.warn("Preventing NPC teleport: " + event.getNPC().getId() + " from "
					+ getShortLocationString(event.getFrom()) + " to " + getShortLocationString(event.getTo()));
		} else {
			event.setCancelled(true);
			PlayerUtils.send(owner, "&cNPCs cannot be teleported across worlds");
			Nexus.warn("Preventing NPC cross-world teleport: " + event.getNPC().getId() + " from "
					+ getShortLocationString(event.getFrom()) + " to " + getShortLocationString(event.getTo()));
		}
	}

	@EventHandler
	public void onNpcSpawn(NPCSpawnEvent event) {
		UUID uuid = event.getNPC().getTrait(Owner.class).getOwnerId();
		if (uuid == null)
			return;

		if (ALLOWED_NPCS.contains(event.getNPC().getId()))
			return;

		OfflinePlayer owner = PlayerUtils.getPlayer(uuid);
		if (Rank.of(owner).gte(Rank.NOBLE))
			return;

		if (isPerkAllowedAt(event.getLocation()))
			return;

		event.setCancelled(true);
		PlayerUtils.send(owner, "&cYou cannot teleport NPCs here");
		Nexus.warn("Preventing NPC spawn: " + event.getNPC().getId() + " from "
				+ getShortLocationString(event.getNPC().getStoredLocation()) + " to " + getShortLocationString(event.getLocation()));
	}

}
