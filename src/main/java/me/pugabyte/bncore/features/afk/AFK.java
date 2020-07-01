package me.pugabyte.bncore.features.afk;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import me.pugabyte.bncore.models.afk.AFKService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AFK {
	static Map<UUID, AFKPlayer> players = new AFKService().getMap();

	public static void shutdown() {
		new AFKService().saveAll();
	}

	static {
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
				BNCore.warn("Error in AFK scheduler: " + ex.getMessage());
				ex.printStackTrace();
			}
		}));
	}

	private static boolean isSameLocation(Location from, Location to) {
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
				Nerd nerd = new Nerd(player);
				if (nerd.getRank().isStaff())
					if (!get(player).isAfk())
						++result;
			}
			return result;
		}
	}

}
