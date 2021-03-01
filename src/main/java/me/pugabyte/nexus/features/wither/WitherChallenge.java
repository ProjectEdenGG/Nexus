package me.pugabyte.nexus.features.wither;

import com.sk89q.worldedit.regions.Region;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.features.wither.fights.CorruptedFight;
import me.pugabyte.nexus.features.wither.fights.EasyFight;
import me.pugabyte.nexus.features.wither.fights.HardFight;
import me.pugabyte.nexus.features.wither.fights.MediumFight;
import me.pugabyte.nexus.features.wither.models.WitherFight;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@NoArgsConstructor
public class WitherChallenge extends Feature implements Listener {

	public static final String PREFIX = StringUtils.getPrefix("Wither");
	public static WitherFight currentFight;

	public static void reset() {
		if (currentFight != null) {
			Nexus.unregisterListener(currentFight);
			if (currentFight.wither != null)
				currentFight.wither.remove();
			currentFight = null;
		}
		Region region = new WorldGuardUtils("events").getRegion("witherarena");
		new WorldEditUtils("events").paster().file("wither_arena").at(region.getMinimumPoint()).pasteAsync();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!new WorldGuardUtils(event.getPlayer().getWorld()).isInRegion(event.getPlayer().getLocation(), "witherarena"))
			return;
		if (currentFight == null) Warps.spawn(event.getPlayer());
		if (currentFight.party.contains(event.getPlayer().getUniqueId())) return;
		Warps.spawn(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event) {
		if (currentFight == null) return;
		if (!currentFight.party.contains(event.getPlayer().getUniqueId())) return;
		currentFight.broadcastToParty("&e" + event.getPlayer().getName() + " &3has logged out. They have one minute to return before they are automatically removed from the party.");
		Tasks.wait(Time.MINUTE, () -> {
			if (currentFight == null) return;
			if (event.getPlayer().isOnline()) return;
			currentFight.party.remove(event.getPlayer().getUniqueId());
			if (currentFight.alivePlayers != null)
				currentFight.alivePlayers.remove(event.getPlayer().getUniqueId());
		});
	}

	public enum Difficulty {
		EASY("&a", EasyFight.class, Material.LIME_CONCRETE, "&712.5% chance of star drop"),
		MEDIUM("&6", MediumFight.class, Material.ORANGE_CONCRETE, "&725% chance of star drop\n&7If no star is dropped,\n&7you will receive 2 Wither Crate Keys"),
		HARD("&c", HardFight.class, Material.RED_CONCRETE, "&750% chance of star drop\n&7If no star is dropped,\n&7you will receive 3 Wither Crate Keys"),
		CORRUPTED("&8", CorruptedFight.class, Material.BLACK_CONCRETE, "&7100% chance of star drop\n&7and 2 Wither Crate Keys");

		String color;
		Class<? extends WitherFight> witherFightClass;
		Material menuMaterial;
		String description;

		Difficulty(String color, Class<? extends WitherFight> witherFightClass, Material menuMaterial, String description) {
			this.color = color;
			this.witherFightClass = witherFightClass;
			this.menuMaterial = menuMaterial;
			this.description = description;
		}

		public String getTitle() {
			return StringUtils.colorize(color + "&l" + StringUtils.camelCase(name()));
		}
	}

}
