package me.pugabyte.nexus.features.economy;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.annotations.Disabled;
import me.pugabyte.nexus.framework.features.Feature;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

@Disabled
public class NexusEconomy extends Feature {

	@Override
	public void onStart() {
		Bukkit.getServicesManager().register(Economy.class, new NexusEconomyVaultHook(Nexus.getInstance()), Nexus.getInstance(), ServicePriority.Normal);
	}

}
