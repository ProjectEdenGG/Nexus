package me.pugabyte.bncore.features.holidays.bearfair20;

import com.earth2me.essentials.Essentials;
import lombok.Data;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Halloween;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@Data
public class BearFair20 implements Listener {

	public static World world = Bukkit.getWorld("safepvp");
	public static String mainRg = "bearfair2020";
	public static WorldGuardUtils WGUtils = new WorldGuardUtils(world);

	public BearFair20() {
		BNCore.registerListener(this);
		new Fairgrounds();
		new Halloween();
	}

	public static String isCheatingMsg(Player player) {
		if (player.hasPermission("worldguard.region.bypass.*")) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (Utils.isVanished(player)) return "vanish";
		Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		if (essentials.getUser(player.getUniqueId()).isGodModeEnabled()) return "godmode";

		return null;
	}

	public static void givePoints(Player player, int points) {
		player.sendMessage("TODO: given " + points + " points");
	}


}
