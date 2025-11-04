package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

public class Pugmas25Caves implements Listener {

	public Pugmas25Caves() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().shouldHandle(player))
			return;

		if (CooldownService.isOnCooldown(player.getUniqueId(), "pugmas25_cavewarp", TickTime.SECOND.x(2)))
			return;

		Location location = CaveWarp.getWarpLocation(event.getRegion());
		if (location == null)
			return;

		new Cutscene()
			.fade(0, 20)
			.next(TickTime.SECOND, _player -> _player.teleport(location))
			.start(player);
	}

	@Getter
	@AllArgsConstructor
	public enum CaveWarp {
		MINES(loc(-746.5, 104.5, -3153.5, -90), loc(-630.5, -43.5, -3019.5, 90)),
		;

		private final Location aboveLoc;
		private final Location belowLoc;

		public static Location getWarpLocation(ProtectedRegion region) {
			String regionId = region.getId();

			for (CaveWarp caveWarp : values()) {
				if (caveWarp.getAboveRegion().equalsIgnoreCase(regionId))
					return caveWarp.getBelowLoc();

				if (caveWarp.getBelowRegion().equalsIgnoreCase(regionId))
					return caveWarp.getAboveLoc();
			}

			return null;
		}

		public String getAboveRegion() {
			return Pugmas25.get().getRegionName() + "_cave_" + this.name().toLowerCase() + "_above";
		}

		public String getBelowRegion() {
			return Pugmas25.get().getRegionName() + "_cave_" + this.name().toLowerCase() + "_below";
		}

		public static Location loc(double x, double y, double z, int yaw) {
			return Pugmas25.get().location(x, y, z, yaw, 0);
		}
	}
}
