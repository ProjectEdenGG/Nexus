package me.pugabyte.nexus.features.events.aeveonproject;

import lombok.Data;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.aeveonproject.effects.Effects;
import me.pugabyte.nexus.features.events.aeveonproject.sets.APRegions;
import me.pugabyte.nexus.features.events.aeveonproject.sets.APSetToggler;
import me.pugabyte.nexus.features.events.aeveonproject.sets.APSetType;
import me.pugabyte.nexus.utils.Timer;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

@Data
public class AeveonProject implements Listener {
	public static String PREFIX = "&8&l[&eAeveonProject&8&l] &3";
	public static String ROOT = "Animations/AeveonProject/";

	public AeveonProject() {
		Nexus.registerListener(this);
		new Timer("    Sets", () -> {
			APSetType.values();
			new APSetToggler();
		});
		new Timer("    Effects", Effects::new);
		new Timer("    Regions", APRegions::new);
	}

	public static World getWorld() {
		return Bukkit.getWorld("Aeveon_Project");
	}

	public static WorldGuardUtils getWGUtils() {
		return new WorldGuardUtils(getWorld());
	}

	public static WorldEditUtils getWEUtils() {
		return new WorldEditUtils(getWorld());
	}


}
