package gg.projecteden.nexus.hooks;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class IHook<T extends IHook<?>> implements Listener {

	protected abstract @NotNull String getPluginName();

	public boolean isEnabled() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(getPluginName());
	}

}
