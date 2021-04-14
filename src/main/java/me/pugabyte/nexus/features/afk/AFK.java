package me.pugabyte.nexus.features.afk;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.afk.AFKPlayer;
import me.pugabyte.nexus.models.afk.AFKService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AFK extends Feature {
	static Map<UUID, AFKPlayer> players = new AFKService().getMap();

	@Override
	public void onStart() {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(3), () -> Bukkit.getOnlinePlayers().stream().map(AFK::get).forEach(player -> {
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

	public static AFKPlayer get(Player player) {
		return get(player.getUniqueId());
	}

	public static AFKPlayer get(UUID uuid) {
		if (!players.containsKey(uuid))
			players.put(uuid, new AFKPlayer(uuid));

		return players.get(uuid);
	}

	public static void remove(Player player) {
		players.remove(player.getUniqueId());
	}

	public static int getActivePlayers() {
		if (Bukkit.getOnlinePlayers().size() == 0)
			return 0;
		else {
			int result = 0;
			for (Player player : Bukkit.getOnlinePlayers())
				if (!get(player).isAfk())
					++result;

			return result;
		}
	}

	public static int getActiveStaff() {
		if (Bukkit.getOnlinePlayers().size() == 0)
			return 0;
		else {
			int result = 0;
			Collection<? extends Player> playerList = Bukkit.getOnlinePlayers();
			for (Player player : playerList) {
				Nerd nerd = Nerd.of(player);
				if (nerd.getRank().isStaff())
					if (!get(player).isAfk())
						++result;
			}
			return result;
		}
	}

}
