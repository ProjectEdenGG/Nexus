package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

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

		String regionId = event.getRegion().getId();
		CaveWarp caveWarp = CaveWarp.getCaveWarp(regionId);
		if (caveWarp == null)
			return;

		Location location = caveWarp.getOppositeLocation(regionId);
		location.setPitch(player.getLocation().getPitch());
		int fadeStayTicks = 20;
		if (caveWarp != CaveWarp.MINES)
			fadeStayTicks = 10;

		new Cutscene()
			.fade(0, fadeStayTicks)
			.next(fadeStayTicks, _player -> _player.teleport(location))
			.start(player);
	}

	@Getter
	@AllArgsConstructor
	public enum CaveWarp {
		MINES(loc(-746.5, 104.5, -3153.5, -90), loc(-268.5, 40.5, -3037.5, 90)),
		SPRINGS(loc(-473.5, 108.5, -3101, 45), loc(-273.5, 35.5, -2963.5, -126)),
		;

		private final Location aboveLoc;
		private final Location belowLoc;

		public static CaveWarp getCaveWarp(String regionId) {
			for (CaveWarp caveWarp : values()) {
				if (caveWarp.getAboveRegion().equalsIgnoreCase(regionId) || caveWarp.getBelowRegion().equalsIgnoreCase(regionId))
					return caveWarp;
			}

			return null;
		}

		public Location getOppositeLocation(String regionId) {
			if (getAboveRegion().equalsIgnoreCase(regionId))
				return getBelowLoc();

			return getAboveLoc();
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
