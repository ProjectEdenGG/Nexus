package gg.projecteden.nexus.features.afk;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.afk.AFKUser;
import gg.projecteden.nexus.models.afk.AFKUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import me.lexikiq.HasUniqueId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AFK extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("AFK");
	private static final AFKUserService service = new AFKUserService();

	@Override
	public void onStart() {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(3), () -> {
			afkCheck();
			limboCheck();
		});
	}

	private static void afkCheck() {
		PlayerUtils.getOnlinePlayers().stream().map(AFK::get).forEach(user -> {
			try {
				final Player player = user.getOnlinePlayer();
				if (!isSameLocation(user.getLocation(), player.getLocation()) && player.getVehicle() == null)
					if (user.isAfk() && !user.isForceAfk())
						user.notAfk();
					else
						user.update();
				else if (!user.isAfk() && user.isTimeAfk())
					user.afk();
			} catch (Exception ex) {
				Nexus.warn("Error in AFK scheduler: " + ex.getMessage());
				ex.printStackTrace();
			}
		});
	}

	private static void limboCheck() {
		if (Nexus.EPOCH.isAfter(LocalDateTime.now().minusMinutes(2)))
			return;

		// 5 min average above 17
		if (Bukkit.getTPS()[1] >= 17)
			return;

		final List<Player> players = PlayerUtils.getOnlinePlayers();
		final List<AFKUser> afkUsers = players.stream()
			.map(AFK::get)
			.filter(AFKUser::isTimeAfk)
			.toList();

		if (players.size() == afkUsers.size())
			return;

		afkUsers.forEach(AFKUser::limbo);
	}

	@Override
	public void onStop() {
		final AFKUserService service = new AFKUserService();
		for (Player player : PlayerUtils.getOnlinePlayers())
			service.saveSync(service.get(player));
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

	public static AFKUser get(HasUniqueId player) {
		return get(player.getUniqueId());
	}

	public static AFKUser get(UUID uuid) {
		return service.get(uuid);
	}

	public static int getActivePlayers() {
		return (int) PlayerUtils.getOnlinePlayers().stream().filter(player -> !get(player).isAfk()).count();
	}

	public static int getActiveStaff() {
		return (int) Rank.getOnlineStaff().stream().filter(nerd -> !get(nerd).isAfk()).count();
	}

}
