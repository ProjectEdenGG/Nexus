package me.pugabyte.nexus.features.store.perks;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.getShortLocationString;

@NoArgsConstructor
public class NPCListener implements Listener {
	private static final List<WorldGroup> allowedWorldGroups = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.CREATIVE,
			WorldGroup.SKYBLOCK, WorldGroup.ONEBLOCK);
	private static final List<String> blockedWorlds = Arrays.asList("safepvp", "events");

	@EventHandler
	public void onNpcCreate(PlayerCreateNPCEvent event) {
		Player owner = event.getCreator();
		if (Rank.of(owner).gte(Rank.NOBLE))
			return;

		if (isNpcAllowedAt(owner.getLocation()))
			return;

		event.getNPC().despawn();
		event.getNPC().destroy();
		PlayerUtils.send(owner, "&cYou cannot create NPCs here");
		Nexus.warn("Preventing NPC create: " + event.getNPC().getId() + " from " + owner.getName());
	}

	private boolean isNpcAllowedAt(Location location) {
		WorldGroup worldGroup = WorldGroup.get(location);
		if (!allowedWorldGroups.contains(worldGroup)) {
			Nexus.warn("NPC not allowed here (allowedWorldGroups)");
			return false;
		}

		if (blockedWorlds.contains(location.getWorld().getName())) {
			Nexus.warn("NPC not allowed here (blockedWorlds)");
			return false;
		}

		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(location);
		if (!worldGuardUtils.getRegionsAt(location).isEmpty()) {
			Nexus.warn("NPC not allowed here (regions)");
			return false;
		}

		return true;
	}

	@EventHandler
	public void onNpcTeleport(NPCTeleportEvent event) {
		UUID uuid = event.getNPC().getTrait(Owner.class).getOwnerId();
		if (uuid == null)
			return;

		OfflinePlayer owner = PlayerUtils.getPlayer(uuid);

		if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			if (Rank.of(owner).gte(Rank.NOBLE))
				return;

			if (isNpcAllowedAt(event.getTo()))
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

		OfflinePlayer owner = PlayerUtils.getPlayer(uuid);
		if (Rank.of(owner).gte(Rank.NOBLE))
			return;

		if (isNpcAllowedAt(event.getLocation()))
			return;

		event.setCancelled(true);
		PlayerUtils.send(owner, "&cYou cannot teleport NPCs here");
		Nexus.warn("Preventing NPC spawn: " + event.getNPC().getId() + " from "
				+ getShortLocationString(event.getNPC().getStoredLocation()) + " to " + getShortLocationString(event.getLocation()));
	}

}
