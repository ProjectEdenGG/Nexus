package me.pugabyte.bncore.features.holidays.bearfair20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Halloween;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.inventory.ItemStack;

@Data
public class BearFair20 implements Listener {

	public static World world = Bukkit.getWorld("safepvp");
	public static String mainRg = "bearfair2020";
	public static WorldGuardUtils WGUtils = new WorldGuardUtils(world);

	public BearFair20() {
		BNCore.registerListener(this);
		new Timer("    Fairgrounds", Fairgrounds::new);
		new Timer("    Halloween", Halloween::new);
		new Timer("    BFQuests", BFQuests::new);
	}

	public static String isCheatingMsg(Player player) {
		if (player.hasPermission("worldguard.region.bypass.*")) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (Utils.isVanished(player)) return "vanish";
		if (BNCore.getEssentials().getUser(player.getUniqueId()).isGodModeEnabled()) return "godmode";

		return null;
	}

	public static void givePoints(Player player, int points) {
		player.sendMessage("TODO: given " + points + " points");
	}

	@EventHandler
	public void onTameEntity(EntityTameEvent event) {
		Location loc = event.getEntity().getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(mainRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(mainRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;
//		if (player.hasPermission("worldguard.region.bypass.*")) {
//			Utils.runCommand(player, "wgedit off");
//		}

	}

	@EventHandler
	public void onThrowEnderPearl(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(mainRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;

		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!Utils.isNullOrAir(item)) {
				if (item.getType().equals(Material.ENDER_PEARL)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onLecternTakeBook(PlayerTakeLecternBookEvent event) {
		Location loc = event.getLectern().getBlock().getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(mainRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;

		event.setCancelled(true);
		event.getPlayer().closeInventory();
	}


}
