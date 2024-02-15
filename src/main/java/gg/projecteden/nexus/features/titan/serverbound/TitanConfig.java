package gg.projecteden.nexus.features.titan.serverbound;

import com.google.gson.annotations.SerializedName;
import gg.projecteden.nexus.features.titan.models.Message;
import gg.projecteden.nexus.features.titan.models.Serverbound;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class TitanConfig extends Serverbound {

	SaturnSettings Saturn;
	UtilitiesSettings Utilities;
	BackpackSettings Backpacks;

	@Override
	public void onReceive(Player player) {
		new LocalResourcePackUserService().edit(player, user -> user.setTitanSettings(this));
	}

	@Getter
	public static class SaturnSettings {
		@SerializedName("saturn-updater")
		private String saturnUpdater;

		@SerializedName("saturn-manage-status")
		private String saturnManageStatus;

		@SerializedName("saturn-enabled-default")
		private boolean saturnEnabledDefault;

		@SerializedName("hard-reset")
		private boolean hardReset;
	}

	@Getter
	private static class UtilitiesSettings {
		@SerializedName("stop-entity-culling")
		private boolean stopEntityCulling;
	}

	@Getter
	private static class BackpackSettings {
		@SerializedName("do-backpack-previews")
		private boolean doBackpackPreviews;

		@SerializedName("previews-require-shift")
		private boolean previewsRequireShift;

		@SerializedName("use-background-colors")
		private boolean useBackgroundColors;
	}


}
