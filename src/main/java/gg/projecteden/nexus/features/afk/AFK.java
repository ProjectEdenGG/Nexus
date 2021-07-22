package gg.projecteden.nexus.features.afk;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.afk.AFKPlayer;
import gg.projecteden.nexus.models.afk.AFKService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class AFK extends Feature {
	static Map<UUID, AFKPlayer> players = new AFKService().getMap();

	@Override
	public void onStart() {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(3), () -> PlayerUtils.getOnlinePlayers().stream().map(AFK::get).forEach(player -> {
			try {
				if (!isSameLocation(player.getLocation(), player.getPlayer().getLocation()) && player.getPlayer().getVehicle() == null)
					if (player.isAfk() && !player.isForceAfk())
						player.notAfk();
					else
						player.update();
				else if (!player.isAfk() && player.isTimeAfk())
					player.afk();
			} catch (Exception ex) {
				Nexus.warn("Error in AFK scheduler: " + ex.getMessage());
				ex.printStackTrace();
			}
		}));
	}

	@Override
	public void onStop() {
		new AFKService().saveAll();
	}

	public static boolean isSameLocation(Location from, Location to) {
		if (from == null || to == null)
			return false;
		if (!from.getWorld().equals(to.getWorld()))
			return false;

		boolean x = Math.abs(Math.round(from.getX()) - Math.round(to.getX())) < 2;
		boolean z = Math.abs(Math.round(from.getZ()) - Math.round(to.getZ())) < 2;
		return x && z;
	}

	public static AFKPlayer get(PlayerOwnedObject player) {
		return get(player.getUuid());
	}

	public static AFKPlayer get(Player player) {
		return get(player.getUniqueId());
	}

	public static AFKPlayer get(UUID uuid) {
		return players.computeIfAbsent(uuid, $ -> new AFKPlayer(uuid));
	}

	public static void remove(Player player) {
		players.remove(player.getUniqueId());
	}

	public static int getActivePlayers() {
		return (int) PlayerUtils.getOnlinePlayers().stream().filter(player -> !get(player).isAfk()).count();
	}

	public static int getActiveStaff() {
		return (int) Rank.getOnlineStaff().stream().filter(nerd -> !get(nerd).isAfk()).count();
	}

}
