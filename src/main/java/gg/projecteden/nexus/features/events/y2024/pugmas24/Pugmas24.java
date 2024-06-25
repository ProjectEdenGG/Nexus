package gg.projecteden.nexus.features.events.y2024.pugmas24;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.Rides;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Advent24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Fairgrounds;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24Entity;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24NPC;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24Quest;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItem;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestReward;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestTask;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.features.vanish.Vanish.isVanished;

@QuestConfig(
	quests = Pugmas24Quest.class,
	tasks = Pugmas24QuestTask.class,
	npcs = Pugmas24NPC.class,
	entities = Pugmas24Entity.class,
	items = Pugmas24QuestItem.class,
	rewards = Pugmas24QuestReward.class,
	start = @Date(m = 12, d = 1, y = 2024),
	end = @Date(m = 1, d = 10, y = 2025),
	world = "buildadmin", // TODO: FINAL WORLD
	region = "pugmas24", // TODO: FINAL REGION NAME
	warpType = WarpType.PUGMAS24
)
public class Pugmas24 extends EdenEvent {
	private static Pugmas24 instance;
	public static final String PREFIX = StringUtils.getPrefix("Pugmas 2024");

	public static final LocalDate _25TH = LocalDate.of(2024, 12, 25);

	public static final String LORE = "&ePugmas 2024 Item";
	public Location warp = location(0.5, 52, 0.5);

	public Pugmas24() {
		instance = this;
	}

	public static Pugmas24 get() {
		return instance;
	}

	@Override
	public void onStart() {
		new Advent24();
		new Fairgrounds();
		Rides.startup();
	}

	@Override
	public void onStop() {
		Advent24.shutdown();
	}

	public static void send(String message, Player to) {
		PlayerUtils.send(to, message);
	}

	public static String isCheatingMsg(Player player) {
		if (canWorldGuardEdit(player)) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (new GodmodeService().get(player).isActive()) return "godmode";

		return null;
	}

	public static boolean isAdventActive(LocalDate date) {
		return get().isEventActive() && !date.isAfter(_25TH);
	}

	public static boolean is25thOrAfter() {
		return is25thOrAfter(LocalDate.now());
	}

	public static boolean is25thOrAfter(LocalDate date) {
		return date.isAfter(_25TH.plusDays(-1));
	}


	public static boolean isNotAtPugmas(Player player) {
		return isNotAtPugmas(player.getLocation());
	}

	public static boolean isNotAtPugmas(PlayerInteractEvent event) {
		return isNotAtPugmas(event.getHand(), event.getPlayer());
	}

	public static boolean isNotAtPugmas(PlayerInteractEntityEvent event) {
		return isNotAtPugmas(event.getHand(), event.getPlayer());
	}

	private static boolean isNotAtPugmas(EquipmentSlot slot, Player player) {
		if (!EquipmentSlot.HAND.equals(slot)) return true;

		return isNotAtPugmas(player);
	}

	public static boolean isNotAtPugmas(Location location) {
		return !location.getWorld().equals(get().getWorld());
	}

	public static boolean isInRegion(Block block, String region) {
		return isInRegion(block.getLocation(), region);
	}

	public static boolean isInRegion(Player player, String region) {
		return isInRegion(player.getLocation(), region);
	}

	public static boolean isInRegion(Location location, String region) {
		return !isNotAtPugmas(location) && get().worldguard().isInRegion(location, region);
	}

	public static boolean isInRegionRegex(Location location, String regex) {
		return !isNotAtPugmas(location) && !get().worldguard().getRegionsLikeAt(regex, location).isEmpty();
	}

	public static Set<Player> getPlayers() {
		return new HashSet<>(OnlinePlayers.where().world(get().getWorld()).get());
	}

	public static Set<Player> getPlayersIn(ProtectedRegion region) {
		return getPlayersIn(region.getId());
	}

	public static Set<Player> getPlayersIn(String region) {
		return new HashSet<>(OnlinePlayers.where().world(get().getWorld()).region(region).get());
	}

}
