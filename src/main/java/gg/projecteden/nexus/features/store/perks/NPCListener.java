package gg.projecteden.nexus.features.store.perks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
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

import static gg.projecteden.nexus.features.listeners.Restrictions.isPerkAllowedAt;
import static gg.projecteden.nexus.utils.StringUtils.getShortLocationString;

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
		if (Rank.of(owner).gte(Rank.NOBLE) || Dev.of(owner.getUniqueId()) != null)
			return;

		if (isPerkAllowedAt(owner, owner.getLocation()))
			return;

		if (event.getCreator().getWorld().getName().equals("events") && new WorldGuardUtils(event.getCreator()).isInRegion(event.getCreator(), "pride21_parade"))
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

		if (event.getTo() == null)
			return;

		if (event.getTo().getWorld().getName().equals("events") && new WorldGuardUtils(event.getTo()).isInRegion(event.getTo(), "pride21_parade"))
			return;

		OfflinePlayer owner = PlayerUtils.getPlayer(uuid);

		if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			event.setCancelled(true);
			PlayerUtils.send(owner, "&cNPCs cannot be teleported across worlds");
			Nexus.warn("Preventing NPC cross-world teleport: " + event.getNPC().getId() + " from "
					+ getShortLocationString(event.getFrom()) + " to " + getShortLocationString(event.getTo()));
			return;
		}

		if (ALLOWED_NPCS.contains(event.getNPC().getId()))
			return;

		if (Rank.of(owner).gte(Rank.NOBLE) || Dev.of(owner.getUniqueId()) != null)
			return;

		if (isPerkAllowedAt(owner, event.getTo()))
			return;

		event.setCancelled(true);
		PlayerUtils.send(owner, "&cYou cannot teleport NPCs here");
		Nexus.warn("Preventing NPC teleport: " + event.getNPC().getId() + " from "
				+ getShortLocationString(event.getFrom()) + " to " + getShortLocationString(event.getTo()));
	}

	@EventHandler
	public void onNpcSpawn(NPCSpawnEvent event) {
		UUID uuid = event.getNPC().getTrait(Owner.class).getOwnerId();
		if (uuid == null)
			return;

		if (ALLOWED_NPCS.contains(event.getNPC().getId()))
			return;

		if (event.getLocation().getWorld().getName().equals("events") && new WorldGuardUtils(event.getLocation()).isInRegion(event.getLocation(), "pride21_parade"))
			return;

		OfflinePlayer owner = PlayerUtils.getPlayer(uuid);
		if (Rank.of(owner).gte(Rank.NOBLE) || Dev.of(owner.getUniqueId()) != null)
			return;

		if (isPerkAllowedAt(owner, event.getLocation()))
			return;

		event.setCancelled(true);
		PlayerUtils.send(owner, "&cYou cannot teleport NPCs here");
		Nexus.warn("Preventing NPC spawn: " + event.getNPC().getId() + " from "
				+ getShortLocationString(event.getNPC().getStoredLocation()) + " to " + getShortLocationString(event.getLocation()));
	}

}
