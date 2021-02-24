package me.pugabyte.nexus.features.economy;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.Env;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

@Environments(Env.DEV)
public class NexusEconomy extends Feature {

	@Override
	public void onStart() {
		NexusEconomyVaultHook vaultHook = new NexusEconomyVaultHook(Nexus.getInstance());
		Bukkit.getServicesManager().register(Economy.class, vaultHook, Nexus.getInstance(), ServicePriority.Normal);
		Nexus.setEcon(vaultHook);
	}

}
