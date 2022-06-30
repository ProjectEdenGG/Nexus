package gg.projecteden.nexus.features.placeholderapi;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@Disabled
public class PlaceholderAPIHook extends Feature {

	private static NexusPlaceholders placeholders;

	@Override
	public void onStart() {
		if (getPlugin() != null) {
			placeholders = new NexusPlaceholders();
			placeholders.register();
		}
	}

	@Override
	public void onStop() {
		if (placeholders != null && placeholders.isRegistered())
			placeholders.unregister();
	}

	private static final String plugin = "PlaceholderAPI";

	public Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin(plugin);
	}

}
