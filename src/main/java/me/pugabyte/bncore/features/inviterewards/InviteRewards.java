package me.pugabyte.bncore.features.inviterewards;

import me.pugabyte.bncore.BNCore;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class InviteRewards {
	private static PlayerPoints playerPointsInstance;

	public InviteRewards() {

		Plugin playerPoints = BNCore.getInstance().getServer().getPluginManager().getPlugin("PlayerPoints");

		if (playerPoints != null && playerPoints.isEnabled()) {
			try {
				if (!hookPlayerPoints()) {
					BNCore.getInstance().getLogger().severe("Could not hook into PlayerPoints");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static void saveInvitation(Player invitee, Player inviter) {
		String UUIDer = inviter.getUniqueId().toString();
		String UUIDed = invitee.getUniqueId().toString();

		Configuration config = BNCore.getInstance().getConfig();

		List<String> invited = config.getStringList("inviterewards.invited." + UUIDer);
		invited.add(UUIDed);
		config.set("inviterewards.invited." + UUIDer, invited);

		try {
			BNCore.getInstance().saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static PlayerPoints getPlayerPoints() {
		return playerPointsInstance;
	}

	private boolean hookPlayerPoints() {
		final Plugin plugin = BNCore.getInstance().getServer().getPluginManager().getPlugin("PlayerPoints");
		playerPointsInstance = (PlayerPoints) plugin;
		return playerPointsInstance != null;
	}

}
