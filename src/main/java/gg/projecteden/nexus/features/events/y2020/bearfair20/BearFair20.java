package gg.projecteden.nexus.features.events.y2020.bearfair20;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.models.BearFairTalker;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.IslandType;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MainIsland;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.EasterEggs;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.Getter;
import net.citizensnpcs.api.event.NPCRightClickEvent;
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
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
public class BearFair20 implements Listener {
	@Getter
	private static final String region = "bearfair2020";
	@Getter
//	private static final Set<Class<? extends BearFairIsland>> islands = subTypesOf(BearFair20.class.getPackageName() + ".islands", BearFairIsland.class);
	public static String PREFIX = "&8&l[&eBearFair&8&l] &3";

	// TODO: When BF is over, disable these, and disable block break/place on regions
	public static boolean enableQuests = false;
	public static boolean givePoints = false;
	public static boolean giveDailyPoints = false;

	// TODO: When working on BF, disable this.
	public static boolean allowWarp = true;

	public BearFair20() {
		Nexus.registerListener(this);
		new Timer("      BF20.Fairgrounds", Fairgrounds::new);
		new Timer("      BF20.Islands", IslandType::values);
		if (enableQuests) {
			new Timer("      BF20.BFQuests", BFQuests::new);
			new Timer("      BF20.EasterEggs", EasterEggs::new);
		}
	}

	public static World getWorld() {
		return Bukkit.getWorld("safepvp");
	}

	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public static WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	public static ProtectedRegion getProtectedRegion() {
		return worldguard().getProtectedRegion(region);
	}

	@EventHandler
	public void onRightClickWakka(NPCRightClickEvent event) {
		Player player = event.getClicker();
		if (isAtBearFair(player)) {
			if (!enableQuests) {
				if (CooldownService.isOnCooldown(player, "BF_NPCInteract", TickTime.SECOND.x(2)))
					return;

				int id = event.getNPC().getId();
				if (id == MainIsland.MainNPCs.WakkaFlocka.getNpcId()) {
					List<String> script = new ArrayList<>();
					script.add("Welcome to Bear Fair, Project Eden's anniversary event!");
					script.add("wait 80");
					script.add("This event starts every year on June 29th and lasts until the 5th!");
					script.add("wait 80");
					script.add("While this years event is over, you can still explore the island and play the minigames at the carnival.");
					script.add("wait 80");
					script.add("And if you need help figuring out where you are, check out this map to my side.");
					BearFairTalker.sendScript(player, MainIsland.MainNPCs.WakkaFlocka, script);
				}
			}
		}
	}

	@EventHandler
	public void onTameEntity(EntityTameEvent event) {
		Location loc = event.getEntity().getLocation();
		if (!isAtBearFair(loc)) return;
		event.setCancelled(true);
	}

//	@EventHandler
//	public void onRegionEnter(PlayerEnteredRegionEvent event) {
//		Player player = event.getPlayer();
//		if(!isAtBearFair(player)) return;
//		if (player.hasPermission("worldguard.region.bypass.*")) {
//			Utils.runCommand(player, "wgedit off");
//		}
//	}

	@EventHandler
	public void onThrowEnderPearl(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;

		if (ActionGroup.RIGHT_CLICK.applies(event)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!Nullables.isNullOrAir(item)) {
				if (item.getType().equals(Material.ENDER_PEARL)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onLecternTakeBook(PlayerTakeLecternBookEvent event) {
		Location loc = event.getLectern().getBlock().getLocation();
		if (!isAtBearFair(loc)) return;

		event.setCancelled(true);
		event.getPlayer().closeInventory();
	}

	@EventHandler
	public void onExitMinecart(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player player)) return;
		if (!(event.getVehicle() instanceof Minecart)) return;

		if (!isAtBearFair(player)) return;

		Tasks.wait(1, () -> {
			event.getVehicle().remove();
			Fairgrounds.giveKit(Fairgrounds.BearFairKit.MINECART, player);
		});
	}

//	@EventHandler
//	public void onRegionEnterYacht(PlayerEnteredRegionEvent event) {
//		if (!allowWarp) return;
//		if (!event.getRegion().getId().equalsIgnoreCase("spawn_spaceyacht")) return;
//		Player player = event.getPlayer();
//		send("", player);
//		send("&3Captain &8> &fAll aboard! Everyone to their sleeping quarters! We'll be leaving soon.", player);
//		send("", player);
//	}

//	@EventHandler
//	public void onRegionEnterQuarters(PlayerEnteredRegionEvent event) {
//		if (!allowWarp) return;
//		if (!event.getRegion().getId().equalsIgnoreCase("spawn_bearfair")) return;
//
//		Location spawnTransition = new Location(Bukkit.getWorld("legacy2"), 24.5, 96.5, -189.5);
//		Location bearFairYacht = new Location(world, -984.5, 135.5, -1529.5);
//		Player player = event.getPlayer();
//		BearFairService service = new BearFairService();
//		BearFairUser user = service.get(player);
//
//		Tasks.wait(TickTime.SECOND.x(2), () -> {
//			player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(80).amplifier(250).build());
//			player.teleportAsync(spawnTransition);
//			send("", player);
//			send("&e&o*You immediately fall asleep in your bed*", player);
//			send("", player);
//			Tasks.wait(TickTime.SECOND.x(4), () -> {
//				player.teleport(bearFairYacht);
//				send("", player);
//				send("&e&o*You awake to the sounds of birds chirping, you must have slept the whole trip*", player);
//				send("", player);
//				if (user.isFirstVisit()) {
//					user.setFirstVisit(false);
//					service.save(user);
//					Tasks.wait(TickTime.SECOND.x(3), () -> {
//						send("&8&l[&c&l!!!&8&l] &3You can now warp here using: &e/bearfair", player);
//						chime(player);
//					});
//				}
//			});
//		});
//	}

	public static String isCheatingMsg(Player player) {
		if (PlayerUtils.isWGEdit(player)) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (Vanish.isVanished(player)) return "vanish";
		if (new GodmodeService().get(player).isActive()) return "godemode";

		return null;
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
		return location.getWorld().equals(BearFair20.getWorld()) && worldguard().isInRegion(location, region);
	}

	public static boolean isBFItem(ItemStack item) {
		return item != null && item.getLore() != null && item.getLore().get(0).contains(BFQuests.itemLore);
	}

	public static void send(String message, Player to) {
		PlayerUtils.send(to, message);
	}

}
