package me.pugabyte.bncore.features.holidays.bearfair20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.IslandType;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.Utils;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.Set;

import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.chime;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;
import static me.pugabyte.bncore.utils.Utils.isNullOrAir;
import static me.pugabyte.bncore.utils.Utils.isVanished;

/*
	TODO:
	 - (Sometime After Release) Merchant Trader: decide how much gold -> BFP
 */

@Data
public class BearFair20 implements Listener {
	@Getter
	private static final World world = Bukkit.getWorld("safepvp");
	@Getter
	private static final String region = "bearfair2020";
	@Getter
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(world);
	@Getter
	private static final ProtectedRegion protectedRegion = WGUtils.getProtectedRegion(region);
	@Getter
	private static final Set<Class<? extends Island>> islands = new Reflections("me.pugabyte.bncore.features.holidays.bearfair20.islands").getSubTypesOf(Island.class);
	public static String PREFIX = "&8&l[&eBearFair&8&l] &3";

	// TODO: When BF is over, disable these.
	public static boolean givePoints = true;
	public static boolean allowWarp = true;

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

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(protectedRegion)) return;
		if (player.hasPermission("worldguard.region.bypass.*")) {
			Utils.runCommand(player, "wgedit off");
		}

	}

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

	@EventHandler
	public void onRegionEnterYacht(RegionEnteredEvent event) {
		if (!allowWarp) return;
		if (!event.getRegion().getId().equalsIgnoreCase("spawn_spaceyacht")) return;
		Player player = event.getPlayer();
		send("", player);
		send("&3Captain &8> &fAll aboard! Everyone to their sleeping quarters! We'll be leaving soon.", player);
		send("", player);

	}

	@EventHandler
	public void onRegionEnterQuarters(RegionEnteredEvent event) {
		if (!allowWarp) return;
		if (!event.getRegion().getId().equalsIgnoreCase("spawn_bearfair")) return;

		Location spawnTransition = new Location(Bukkit.getWorld("survival"), 24.5, 96.5, -189.5);
		Location bearFairYacht = new Location(world, -984.5, 135.5, -1529.5);
		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);

		Tasks.wait(Time.SECOND.x(2), () -> {
			player.addPotionEffects(Collections.singletonList
					(new PotionEffect(PotionEffectType.BLINDNESS, 80, 250, false, false, false)));
			player.teleport(spawnTransition);
			send("", player);
			send("&e&o*You immediately fall asleep in your bed*", player);
			send("", player);
			Tasks.wait(Time.SECOND.x(4), () -> {
				player.teleport(bearFairYacht);
				send("", player);
				send("&e&o*You awake to the sounds of birds chirping, you must have slept the whole trip*", player);
				send("", player);
				if (user.isFirstVisit()) {
					user.setFirstVisit(false);
					service.save(user);
					Tasks.wait(Time.SECOND.x(3), () -> {
						send("&8&l[&c&l!!!&8&l] &3You can now warp here using: &e/bearfair", player);
						chime(player);
					});
				}
			});
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
