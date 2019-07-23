package me.pugabyte.bncore.features.staff.antibots;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.staff.antibots.models.AntiBotsDatabase;
import me.pugabyte.bncore.features.staff.antibots.models.AntiBotsResultType;
import org.bukkit.plugin.Plugin;

public class AntiBots {
	private boolean enabled;

	public AntiBots() {
		Plugin herochat = BNCore.getInstance().getServer().getPluginManager().getPlugin("Herochat");
		Plugin vixio = BNCore.getInstance().getServer().getPluginManager().getPlugin("Vixio");

		if (herochat != null && herochat.isEnabled() && vixio != null && vixio.isEnabled()) {
			new AntiBotsCommand();
			new AntiBotsListener();

			BNCore.scheduleSyncRepeatingTask(1200L, 1200L, AntiBots::write);
		}
	}

	public static void write() {
		if (AntiBotsCommand.isDirty(AntiBotsResultType.ALLOWED)) {
			AntiBotsCommand.setDirty(AntiBotsResultType.ALLOWED, false);
			AntiBotsDatabase.AllowedWriter allowedWriter = new AntiBotsDatabase.AllowedWriter();
			allowedWriter.write();
		}
		if (AntiBotsCommand.isDirty(AntiBotsResultType.DENIED)) {
			AntiBotsCommand.setDirty(AntiBotsResultType.DENIED, false);
			AntiBotsDatabase.DeniedWriter deniedWriter = new AntiBotsDatabase.DeniedWriter();
			deniedWriter.write();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
