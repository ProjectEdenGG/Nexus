package me.pugabyte.bncore.features.holidays.bearfair20;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Halloween;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

@Data
public class BearFair20 implements Listener {

	public static World world = Bukkit.getWorld("safepvp");
	public static String bearfairRg = "bearfair2020";
	public static WorldGuardUtils WGUtils = new WorldGuardUtils(world);

	public BearFair20() {
		BNCore.registerListener(this);
		new Fairgrounds();
		new Halloween();
		new BFQuests();
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

	@EventHandler
	public void onTameEntity(EntityTameEvent event) {
		Location loc = event.getEntity().getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(bearfairRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;
		event.setCancelled(true);
	}


}
