package gg.projecteden.nexus.features.events.y2021.bearfair21;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.BearFair21Rides;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21IslandType;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21MainIsland.MainNPCs;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21MinigameNightIsland;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21Merchants;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.models.bearfair21.BearFair21Config;
import gg.projecteden.nexus.models.bearfair21.BearFair21ConfigService;
import gg.projecteden.nexus.models.bearfair21.BearFair21User;
import gg.projecteden.nexus.models.bearfair21.BearFair21UserService;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BearFair21 implements Listener {
	private static final BearFair21ConfigService configService = new BearFair21ConfigService();
	@Getter
	private static final BearFair21Config config = configService.get0();
	private static final BearFair21UserService userService = new BearFair21UserService();

	/**
	 * TODO BF21: When BearFair21 is over disable: GIVE_REWARDS
	 */

	@Getter
	private static final String PREFIX = "&8&l[&eBearFair&8&l] &3";
	@Getter
	private static final String region = "bearfair21";
	@Getter
	private static final Location shipSpawnLoc = BearFair21.locationOf(5, 135, 32, 90, 0).toCenterLocation();

	public BearFair21() {
		Nexus.registerListener(this);

		new Timer("      BF21.Restrictions", BearFair21Restrictions::new);
		new Timer("      BF21.Fairgrounds", BearFair21Fairgrounds::new);
		new Timer("      BF21.Islands", BearFair21IslandType::values);
		new Timer("      BF21.Quests", BearFair21Quests::new);

		addTokenMax(BF21PointSource.ARCHERY, 25);
		addTokenMax(BF21PointSource.MINIGOLF, 25);
		addTokenMax(BF21PointSource.FROGGER, 25);
		addTokenMax(BF21PointSource.SEEKER, 25);
		addTokenMax(BF21PointSource.REFLECTION, 25);
		addTokenMax(BF21PointSource.TRADER, 50);

		startup();
	}

	public static World getWorld() {
		return Bukkit.getWorld("bearfair21");
	}

	public static Location locationOf(double x, double y, double z) {
		return locationOf(x, y, z, 0, 0);
	}

	public static Location locationOf(double x, double y, double z, float yaw, float pitch) {
		return new Location(getWorld(), x, y, z, yaw, pitch);
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

	public static boolean isNotAtBearFair(Block block) {
		return isNotAtBearFair(block.getLocation());
	}

	public static boolean isNotAtBearFair(Entity entity) {
		return isNotAtBearFair(entity.getLocation());
	}

	public static boolean isNotAtBearFair(Player player) {
		return isNotAtBearFair(player.getLocation());
	}

	public static boolean isNotAtBearFair(Location location) {
		return !location.getWorld().equals(getWorld());
	}

	public static boolean isNotAtBearFair(PlayerInteractEvent event) {
		return isNotAtBearFair(event.getHand(), event.getPlayer());
	}

	public static boolean isNotAtBearFair(PlayerInteractEntityEvent event) {
		return isNotAtBearFair(event.getHand(), event.getPlayer());
	}

	private static boolean isNotAtBearFair(EquipmentSlot slot, Player player) {
		if (!EquipmentSlot.HAND.equals(slot)) return true;

		return BearFair21.isNotAtBearFair(player);
	}

	public static boolean canDoBearFairQuest(Player player) {
		if (!BearFair21.getConfig().isEnabled(BearFair21Config.BearFair21ConfigOption.QUESTS)) return false;
		return !isNotAtBearFair(player);
	}

	public static boolean canDoBearFairQuest(PlayerInteractEvent event) {
		if (!canDoBearFairQuest(event.getPlayer())) return false;
		return !isNotAtBearFair(event);
	}

	public static boolean canDoBearFairQuest(PlayerInteractEntityEvent event) {
		if (!canDoBearFairQuest(event.getPlayer())) return false;
		return !isNotAtBearFair(event);
	}

	public static boolean isInRegion(Block block, String region) {
		return isInRegion(block.getLocation(), region);
	}

	public static boolean isInRegion(Player player, String region) {
		return isInRegion(player.getLocation(), region);
	}

	public static boolean isInRegion(Location location, String region) {
		return !isNotAtBearFair(location) && worldguard().isInRegion(location, region);
	}

	public static boolean isInRegionRegex(Location location, String regex) {
		return !isNotAtBearFair(location) && worldguard().getRegionsLikeAt(regex, location).size() > 0;
	}

	public static void send(String message, Player to) {
		PlayerUtils.send(to, message);
	}

	public static String isCheatingMsg(Player player) {
		if (WorldGuardEditCommand.isEnabled(player)) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (Vanish.isVanished(player)) return "vanish";
		if (new GodmodeService().get(player).isActive()) return "godmode";

		return null;
	}

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND, () -> {
			if (!config.isEnabled(BearFair21Config.BearFair21ConfigOption.WARP))
				return;

			for (Player player : BearFair21.getPlayers()) {
				if (Vanish.isVanished(player) || player.getGameMode() != GameMode.SURVIVAL) continue;

				if (player.isFlying()) {
					player.setFallDistance(0);
					PlayerUtils.setAllowFlight(player, false, BearFair21.class);
					PlayerUtils.setFlying(player, false, BearFair21.class);
					player.sendMessage(StringUtils.colorize("&cNo cheating!"));
				}
			}
		});
	}

	public static Set<Player> getPlayers() {
		return new HashSet<>(OnlinePlayers.where().world(getWorld()).get());
	}

	// point stuff

	@Getter
	private static final Map<String, Integer> tokenMaxes = new HashMap<>();

	public static void addTokenMax(BF21PointSource source, int amount) {
		tokenMaxes.put("bearfair21_" + source.name().toLowerCase(), amount);
	}

	public static int getTokenMax(BF21PointSource source) {
		return tokenMaxes.get("bearfair21_" + source.name().toLowerCase());
	}

	public static int getDailyTokensLeft(OfflinePlayer player, BF21PointSource source, int amount) {
		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		return user.getDailyTokensLeft(source.getId(), amount, tokenMaxes);
	}

	public static void giveDailyTokens(Player player, BF21PointSource source, int amount) {
		if (!config.isEnabled(BearFair21Config.BearFair21ConfigOption.GIVE_DAILY_TOKENS))
			return;

		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		final int dailyTokensLeft = Math.abs(getDailyTokensLeft(player, source, 0));

		if (dailyTokensLeft == 0) {
			ActionBarUtils.sendActionBar(player, "&cDaily token limit reached");
		} else {
			user.giveTokens(source.getId(), amount, tokenMaxes);
			service.save(user);

			ActionBarUtils.sendActionBar(player, "&a+" + amount + " Event Tokens");
		}
	}

	public static void giveTokens(BearFair21User user, int amount) {
		giveTokens(user.getPlayer(), amount);
	}

	public static void giveTokens(Player player, int amount) {
		if (!config.isEnabled(BearFair21Config.BearFair21ConfigOption.GIVE_REWARDS))
			return;

		new EventUserService().edit(player, user -> user.giveTokens(amount));

		ActionBarUtils.sendActionBar(player, "&a+" + amount + " Event Tokens");
	}

	public static boolean canWarp() {
		return config.isEnabled(BearFair21Config.BearFair21ConfigOption.WARP);
	}

	public static void startup() {
		BearFair21Rides.startup();
	}

	public static void shutdown() {
		BearFair21Quests.shutdown();
	}

	public enum BF21PointSource {
		TRADER,
		MINIGOLF,
		SEEKER,
		ARCHERY,
		FROGGER,
		REFLECTION,
		;

		public String getId() {
			return "bearfair21_" + name().toLowerCase();
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (isNotAtBearFair(player)) return;
		if (player.hasPermission(Group.STAFF) && !Vanish.isVanished(player))
			PlayerUtils.runCommand(player, "cheats off");
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
		if (!(event.getPlayer() instanceof Player player)) return;
		if (isNotAtBearFair(player)) return;

		BearFair21UserService userService = new BearFair21UserService();
		BearFair21User user = userService.get(player);
		// Trader
		{
			List<ItemStack> items = BearFair21Quests.getItemsLikeFrom(user, Collections.singletonList(BearFair21Merchants.traderCoupon.clone()));
			if (Nullables.isNullOrEmpty(items))
				return;

			BearFair21Quests.removeItemStacks(user, items);
			giveDailyTokens(player, BF21PointSource.TRADER, 50);
			BearFair21Quests.sound_obtainItem(player);
		}

		// James
		{
			List<ItemStack> items = BearFair21Quests.getItemsLikeFrom(user, Collections.singletonList(BearFair21MinigameNightIsland.getCarKey()));
			if (Nullables.isNullOrEmpty(items))
				return;

			user.setMgn_boughtCar(true);
			user.getNextStepNPCs().remove(MainNPCs.JAMES.getNpcId());
			userService.save(user);
		}
	}

	@EventHandler
	public void onRegionEnterYacht(PlayerEnteredRegionEvent event) {
		if (!config.isEnabled(BearFair21Config.BearFair21ConfigOption.WARP)) return;
		if (!event.getRegion().getId().equalsIgnoreCase("spawn_spaceyacht")) return;
		Player player = event.getPlayer();
		send("", player);
		send("&3Captain &8> &fAll aboard! Everyone to their sleeping quarters! We'll be leaving soon.", player);
		send("", player);
	}

	@EventHandler
	public void onRegionEnterQuarters(PlayerEnteredRegionEvent event) {
		if (!config.isEnabled(BearFair21Config.BearFair21ConfigOption.WARP)) return;
		if (!event.getRegion().getId().equalsIgnoreCase("spawn_bearfair")) return;

		Location spawnTransition = new Location(Bukkit.getWorld("legacy2"), 9.5, 100, -180.5);
		Player player = event.getPlayer();
		BearFair21User user = userService.get(player);

		Tasks.wait(TickTime.SECOND.x(2), () -> {
			player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(80).amplifier(250).build());
			player.teleportAsync(spawnTransition);
			send("", player);
			send("&e&o*You immediately fall asleep in your bed*", player);
			send("", player);

			Tasks.wait(TickTime.SECOND.x(4), () -> {
				boolean firstVisit = user.isFirstVisit();
				user.setFirstVisit(false);
				userService.save(user);
				player.teleportAsync(shipSpawnLoc);
				send("", player);
				send("&e&o*You awake to the sounds of birds chirping, you must have slept the whole trip*", player);
				send("", player);
				if (firstVisit) {
//					user.getOnlinePlayer().getInventory().setContents(new ItemStack[0]);

					Tasks.wait(TickTime.SECOND.x(3), () -> {
						send("&8&l[&c&l!!!&8&l] &3You can now warp here using: &e/bearfair21", player);
						BearFair21Quests.sound_obtainItem(player);
					});
				}
			});
		});
	}

}
