package gg.projecteden.nexus.hooks.viaversion;

import gg.projecteden.nexus.hooks.IHook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ViaVersionHook extends IHook<ViaVersionHook> {

	@Override
	protected @NotNull String getPluginName() {
		return "ViaVersion";
	}

	public String getPlayerVersion(Player player) {
		return "Unknown (ViaVersion not loaded)";
	}

}
