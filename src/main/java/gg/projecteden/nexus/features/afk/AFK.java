package gg.projecteden.nexus.features.afk;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.afk.AFKUser;
import gg.projecteden.nexus.models.afk.AFKUser.AFKSetting;
import gg.projecteden.nexus.models.afk.AFKUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AFK extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("AFK");
	private static final AFKUserService service = new AFKUserService();

	@Override
	public void onStart() {
		Tasks.repeatAsync(TickTime.SECOND.x(5), TickTime.SECOND.x(3), () -> {
			List<Player> onlinePlayers = Collections.unmodifiableList(OnlinePlayers.getAll());
			afkCheck(onlinePlayers);
			limboCheck(onlinePlayers);
		});
	}

	private static void afkCheck(List<Player> onlinePlayers) {
		onlinePlayers.stream().map(AFK::get).forEach(user -> {
			try {
				if (hasMoved(user))
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

	private static boolean hasMoved(AFKUser user) {
		final Player player = user.getOnlinePlayer();
		final Entity vehicle = player.getVehicle();

		if (vehicle == null || user.getLocation() == null)
			return !isSameLocation(user.getLocation(), player.getLocation());

		final float previousYaw = user.getLocation().getYaw();
		final float previousPitch = user.getLocation().getPitch();

		final float currentYaw = player.getLocation().getYaw();
		final float currentPitch = player.getLocation().getPitch();

		return previousYaw != currentYaw && previousPitch != currentPitch;
	}

	private static void limboCheck(List<Player> players) {
		if (Nexus.EPOCH.isAfter(LocalDateTime.now().minusMinutes(2)))
			return;

		// 5 min average above 17
		if (Bukkit.getTPS()[1] >= 17)
			return;

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
		for (Player player : OnlinePlayers.getAll())
			service.saveSync(service.get(player));
	}

	public static boolean isSameLocation(Location from, Location to) {
		if (from == null || to == null || from.getWorld() == null || to.getWorld() == null)
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
		return OnlinePlayers.where().afk(false).get().size();
	}

	public static boolean canPassiveReboot() {
		return OnlinePlayers.where()
			.filter(player -> {
				AFKUser user = AFK.get(player);
				return !user.isAfk() || user.getSetting(AFKSetting.PREVENT_REBOOTS);
			})
			.get()
			.isEmpty();
	}

	public static int getActiveStaff() {
		return OnlinePlayers.where().afk(false).rank(Rank::isStaff).get().size();
	}

}
