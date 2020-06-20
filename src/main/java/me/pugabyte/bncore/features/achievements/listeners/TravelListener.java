package me.pugabyte.bncore.features.achievements.listeners;

import com.mewin.worldguardregionapi.MovementType;
import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.achievements.events.travel.WarpEvent;
import me.pugabyte.bncore.models.achievement.Achievement;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TravelListener implements Listener {
	private static final int km = 100000;

	static {
		Tasks.repeat(300, 300, TravelListener::check);
	}

	@EventHandler
	public void onPlayerTeleport(WarpEvent event) {
		Player player = event.getPlayer();

		Achievement.TAKING_A_SHORTCUT.check(player);
		Achievement.WORLD_TRAVELER.check(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		ProtectedRegion region = event.getRegion();

		switch (event.getRegion().getId()) {
			case "staffhall":
				Achievement.A_LIGHT_OF_HOPE.check(player);
				break;
			case "hallofhistory":
				Achievement.BLAST_FROM_THE_PAST.check(player);
				break;
			case "kodahead":
				Achievement.HIS_POINT_OF_VIEW.check(player);
				break;
		}

		if (region.getId().contains("warp_"))
			Achievement.FAST_TRAVEL.check(player, region.getId().replaceFirst("warp_", ""));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onRegionLeave(RegionLeftEvent event) {
		Player player = event.getPlayer();
		ProtectedRegion region = event.getRegion();

		if (event.getMovementType() == MovementType.DISCONNECT) return;

		switch (region.getId()) {
			case "spawn":
				Achievement.JOURNEY_OF_A_THOUSAND_MILES.check(player);
				break;
		}
	}

	private static void check() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			checkWalk(player);
			checkRide(player);
			checkAviate(player);
		}
	}

	private static void checkWalk(Player player) {
		int distance = player.getStatistic(Statistic.WALK_ONE_CM);
		distance += player.getStatistic(Statistic.SPRINT_ONE_CM);
		distance += player.getStatistic(Statistic.CROUCH_ONE_CM);
		if (distance >= (100 * km)) Achievement.THE_EXPLORER.check(player);
		if (distance >= (1000 * km)) Achievement.THE_WELL_HEELD_TRAVELER.check(player);
	}

	private static void checkRide(Player player) {
		int distance = player.getStatistic(Statistic.MINECART_ONE_CM);
		distance += player.getStatistic(Statistic.BOAT_ONE_CM);
		distance += player.getStatistic(Statistic.PIG_ONE_CM);
		distance += player.getStatistic(Statistic.HORSE_ONE_CM);
		if (distance >= (100 * km)) Achievement.RIDE_OR_DIE.check(player);
	}

	private static void checkAviate(Player player) {
		int distance = player.getStatistic(Statistic.AVIATE_ONE_CM);
		if (distance >= (100 * km)) Achievement.UP_COMMA_UP_COMMA_AND_AWAY.check(player);
	}

}
