package me.pugabyte.nexus.features.events.y2021.bearfair21;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Rides;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.IslandType;
import me.pugabyte.nexus.models.bearfair21.BearFair21Config;
import me.pugabyte.nexus.models.bearfair21.BearFair21ConfigService;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.godmode.GodmodeService;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.TimeUtils.Timer;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static me.pugabyte.nexus.utils.PlayerUtils.isVanished;


public class BearFair21 {
	private static final BearFair21ConfigService configService = new BearFair21ConfigService();
	@Getter
	private static final BearFair21Config config = configService.get0();
	/**
	 * TODO BF21:
	 *  When BearFair21 is over:
	 *  - disable: enableRides, enableQuests, enableWarp, and giveDailyPoints
	 *  - disable: region block break/place
	 */

	@Getter
	private static final String PREFIX = "&8&l[&eBearFair&8&l] &3";
	@Getter
	private static final String region = "bearfair21";


	public BearFair21() {
		new Timer("    Restrictions", BearFair21Restrictions::new);
		new Timer("    Fairgrounds", Fairgrounds::new);
		new Timer("    Islands", IslandType::values);
		new Timer("    Quests", Quests::new);

		Arrays.stream(BF21PointSource.values()).forEach(source -> addTokenMax(source, 25));
	}

	public static World getWorld() {
		return Bukkit.getWorld("bearfair21");
	}

	public static WorldGuardUtils getWGUtils() {
		return new WorldGuardUtils(getWorld());
	}

	public static WorldEditUtils getWEUtils() {
		return new WorldEditUtils(getWorld());
	}

	public static ProtectedRegion getProtectedRegion() {
		return getWGUtils().getProtectedRegion(region);
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

	public static boolean isInRegion(Block block, String region) {
		return isInRegion(block.getLocation(), region);
	}

	public static boolean isInRegion(Player player, String region) {
		return isInRegion(player.getLocation(), region);
	}

	public static boolean isInRegion(Location location, String region) {
		return !isNotAtBearFair(location) && getWGUtils().isInRegion(location, region);
	}

	public static boolean isInRegionRegex(Location location, String regex) {
		return !isNotAtBearFair(location) && getWGUtils().getRegionsLikeAt(regex, location).size() > 0;
	}

	public static void send(String message, Player to) {
		PlayerUtils.send(to, message);
	}

	public static String isCheatingMsg(Player player) {
		if (canWorldGuardEdit(player)) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (new GodmodeService().get(player).isEnabled()) return "godmode";

		return null;
	}

	public static Set<Player> getPlayers() {
		return new HashSet<>(PlayerUtils.getOnlinePlayers(getWorld()));
	}

	// point stuff

	private static final Map<String, Integer> tokenMaxes = new HashMap<>();

	public static void addTokenMax(BF21PointSource source, int amount) {
		tokenMaxes.put("bearfair21_" + source.name().toLowerCase(), amount);
	}

	public static int checkDailyTokens(OfflinePlayer player, BF21PointSource source, int amount) {
		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		return user.checkDaily("bearfair21_" + source.name().toLowerCase(), amount, tokenMaxes);
	}

	public static void giveDailyPoints(Player player, BF21PointSource source, int amount) {
		// TODO BF21: Remove me
		if (true) {
			player.sendMessage("Give +" + amount + " points");
			return;
		}
		//

		if (!config.isGiveDailyPoints())
			return;

		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		user.giveTokens("bearfair21_" + source.name().toLowerCase(), amount, tokenMaxes);
		service.save(user);

		ActionBarUtils.sendActionBar(player, "+" + amount + " Event Points");
	}

	public static void givePoints(Player player, int amount) {
		// TODO BF21: Remove me
		if (true) {
			player.sendMessage("Give +" + amount + " points");
			return;
		}
		//

		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		user.giveTokens(amount);
		service.save(user);

		ActionBarUtils.sendActionBar(player, "+" + amount + " Event Points");
	}

	public static boolean canWarp() {
		return config.isEnableWarp();
	}

	public static void startup() {
		Quests.startup();
		Rides.startup();
	}

	public static void shutdown() {
		Quests.shutdown();
	}

	public enum BF21PointSource {
		ARCHERY,
		MINIGOLF,
		FROGGER,
		SEEKER,
		REFLECTION
	}
}
