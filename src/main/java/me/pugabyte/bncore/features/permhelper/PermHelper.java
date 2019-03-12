package me.pugabyte.bncore.features.permhelper;

import me.pugabyte.bncore.BNCore;
import org.bukkit.plugin.Plugin;

public class PermHelper {
	public PermHelper() {
		Plugin pex = BNCore.getInstance().getServer().getPluginManager().getPlugin("PermissionsEx");

		if (pex != null && pex.isEnabled()) {
			new PermHelperCommand();
		}
	}

}
