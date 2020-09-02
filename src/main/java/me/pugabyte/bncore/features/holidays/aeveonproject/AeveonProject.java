package me.pugabyte.bncore.features.holidays.aeveonproject;

import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.effects.Effects;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APRegions;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetToggler;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

/*
	TODO:
		PDA - Iron Pressure Plate, includes current objectives
		Radius based armorstand names for NPCs
			- Armorstands assigned to NPC names that mimic the NPCs nameplate, but disappear clientside
			- https://www.spigotmc.org/threads/spawning-in-a-clientside-nametag-using-armor-stands-protocollib-packets.371934/
			- Only toggle the armorstands name on if the player is nearby + has "met" the NPC
			- armorstand data: gravity=deny, invulnerablie=true, equiptment=locked, size=small, visible=false
 */
@Data
public class AeveonProject implements Listener {
	@Getter
	public static final World WORLD = Bukkit.getWorld("Aeveon_Project");
	@Getter
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(WORLD);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(WORLD);

	public static String PREFIX = "&8&l[&eAeveonProject&8&l] &3";
	public static String ROOT = "Animations/AeveonProject/";

	public AeveonProject() {
		BNCore.registerListener(this);
		new Timer("    Sets", () -> {
			APSetType.values();
			new APSetToggler();
		});
		new Timer("    Effects", Effects::new);
		new Timer("    Regions", APRegions::new);
	}


}
