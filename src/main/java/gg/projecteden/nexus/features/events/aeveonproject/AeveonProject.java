package gg.projecteden.nexus.features.events.aeveonproject;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.effects.Effects;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APRegions;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetToggler;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

@Data
public class AeveonProject implements Listener {
	public static String PREFIX = "&8&l[&eAeveonProject&8&l] &3";
	public static String ROOT = "Animations/AeveonProject/";

	public AeveonProject() {
		Nexus.registerListener(this);
		new Timer("    AP.Sets", APSetType::init);
		new Timer("    AP.SetToggler", APSetToggler::new);
		new Timer("    AP.Effects", Effects::new);
		new Timer("    AP.Regions", APRegions::new);
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
