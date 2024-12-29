package gg.projecteden.nexus.features.wither.models;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class AntiCamping {

	@NonNull
	WitherFight currentFight;

	private final int anticampingTeleport = 10;

	private final Map<UUID, Location> recentLocations = new HashMap<>();
	private final Map<UUID, Integer> secondsCamping = new HashMap<>();

	public int start() {
		return Tasks.repeat(TimeUtils.TickTime.SECOND, TimeUtils.TickTime.SECOND, () -> {
			if (currentFight.shouldRegen)
				return;

			for (Player player : currentFight.alivePlayers()) {
				UUID uuid = player.getUniqueId();
				if (recentLocations.containsKey(uuid)) {
					Location recent = recentLocations.get(uuid);
					Location now = player.getLocation();

					double dx = Math.abs(now.getX() - recent.getX());
					double dy = Math.abs(now.getY() - recent.getY());
					double dz = Math.abs(now.getZ() - recent.getZ());

					if ((dx < 3D && dy < 2D && dz < 3D) || player.isSneaking()) {
						int seconds = secondsCamping.containsKey(uuid) ? secondsCamping.get(uuid) + 1 : 1;

						if (seconds == anticampingTeleport) {
							WitherFight.subtitle(player, "&8&kbbb &4&lThe Wither Pulls You Closer &8&kbbb");

							Vector vector = currentFight.getWither().getLocation().toVector().subtract(player.getLocation().toVector());
							vector.add(new Vector(0, .3, 0)).normalize().multiply(1.1);
							player.setVelocity(vector);

							secondsCamping.remove(uuid);
						} else {
							secondsCamping.put(uuid, seconds);
						}
					} else {
						secondsCamping.remove(uuid);
					}
				}

				recentLocations.put(uuid, player.getLocation());
			}
		});
	}

}
