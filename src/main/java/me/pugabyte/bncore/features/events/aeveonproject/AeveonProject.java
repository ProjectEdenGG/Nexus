package me.pugabyte.bncore.features.events.aeveonproject;

import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.events.aeveonproject.effects.Effects;
import me.pugabyte.bncore.features.events.aeveonproject.sets.APRegions;
import me.pugabyte.bncore.features.events.aeveonproject.sets.APSetToggler;
import me.pugabyte.bncore.features.events.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

@Data
public class AeveonProject implements Listener {
	public static final World APWorld = Bukkit.getWorld("Aeveon_Project");
	@Getter
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(APWorld);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(APWorld);

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


	public static World getAPWorld() {
		return APWorld;
	}


}
