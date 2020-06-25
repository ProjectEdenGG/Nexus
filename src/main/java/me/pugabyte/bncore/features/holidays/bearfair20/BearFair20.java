package me.pugabyte.bncore.features.holidays.bearfair20;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.IslandType;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.Set;

import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;
import static me.pugabyte.bncore.utils.Utils.isNullOrAir;
import static me.pugabyte.bncore.utils.Utils.isVanished;

/*
	NPC BEEs: 2730, 2731

	TODO: Delete all BF Database data, and then transfer everyones points from skript
 */

@Data
public class BearFair20 implements Listener {
	@Getter private static final World world = Bukkit.getWorld("safepvp");
	@Getter private static final String region = "bearfair2020";
	@Getter public static final WorldGuardUtils WGUtils = new WorldGuardUtils(world);
	@Getter private static final ProtectedRegion protectedRegion = WGUtils.getProtectedRegion(region);
	@Getter private static final Set<Class<? extends Island>> islands = new Reflections("me.pugabyte.bncore.features.holidays.bearfair20.islands").getSubTypesOf(Island.class);

	// TODO: Enable this.
	public static boolean givePoints = false;

	public BearFair20() {
		BNCore.registerListener(this);
		new Timer("    Fairgrounds", Fairgrounds::new);
		new Timer("    Islands", IslandType::values);
		new Timer("    BFQuests", BFQuests::new);
	}

	public static String isCheatingMsg(Player player) {
		if (player.hasPermission("worldguard.region.bypass.*")) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (BNCore.getEssentials().getUser(player.getUniqueId()).isGodModeEnabled()) return "godmode";

		return null;
	}

	@EventHandler
	public void onTameEntity(EntityTameEvent event) {
		Location loc = event.getEntity().getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(protectedRegion)) return;
		event.setCancelled(true);
	}

//	@EventHandler
//	public void onRegionEnter(RegionEnteredEvent event) {
//		Player player = event.getPlayer();
//		Location loc = player.getLocation();
//		if (!WGUtils.getRegionsAt(loc).contains(BFProtectedRg)) return;
//		if (player.hasPermission("worldguard.region.bypass.*")) {
//			Utils.runCommand(player, "wgedit off");
//		}
//
//	}

	@EventHandler
	public void onThrowEnderPearl(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(protectedRegion)) return;

		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!isNullOrAir(item)) {
				if (item.getType().equals(Material.ENDER_PEARL)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onLecternTakeBook(PlayerTakeLecternBookEvent event) {
		Location loc = event.getLectern().getBlock().getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(protectedRegion)) return;

		event.setCancelled(true);
		event.getPlayer().closeInventory();
	}

	@EventHandler
	public void onExitMinecart(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player)) return;
		if (!(event.getVehicle() instanceof Minecart)) return;

		Player player = (Player) event.getExited();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(protectedRegion)) return;

		Tasks.wait(1, () -> {
			event.getVehicle().remove();
			Fairgrounds.giveKit(Fairgrounds.BearFairKit.MINECART, player);
		});
	}

	public static boolean isAtBearFair(Block block) {
		return isAtBearFair(block.getLocation());
	}

	public static boolean isAtBearFair(Player player) {
		return isAtBearFair(player.getLocation());
	}

	public static boolean isAtBearFair(Location location) {
		return isInRegion(location, region);
	}

	public static boolean isInRegion(Block block, String region) {
		return isInRegion(block.getLocation(), region);
	}

	public static boolean isInRegion(Player player, String region) {
		return isInRegion(player.getLocation(), region);
	}

	public static boolean isInRegion(Location location, String region) {
		return location.getWorld().equals(BearFair20.getWorld()) && WGUtils.isInRegion(location, region);
	}

	public static boolean isBFItem(ItemStack item) {
		return item != null && item.getLore() != null && item.getLore().get(0).contains(itemLore);
	}

	public static void send(String message, Player to) {
		to.sendMessage(StringUtils.colorize(message));
	}

}
