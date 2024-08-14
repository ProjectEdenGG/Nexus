package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Pugmas24Districts implements Listener {
	private static final String regionPrefix = Pugmas24.get().getRegionName() + "_district_";

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		if (!Pugmas24.get().shouldHandle(event.getPlayer()))
			return;

		for (Pugmas24District district : Pugmas24District.values()) {
			if (district.getRegionId().equalsIgnoreCase(event.getRegion().getId())) {
				Pugmas24.get().actionBarBroadcast("Area Designation: " + district.getName(), TickTime.SECOND.x(2));
				return;
			}
		}
	}


	@AllArgsConstructor
	public enum Pugmas24District {
		RIDGE("ridge"),
		WEST_SIDE("west"),
		EAST_SIDE("east"),
		FARM("farm"),
		MINES("mines"),
		PORT("port"),
		FROZEN_LAKE("lake"),
		TRAIN_STATION("train_station"),
		TRAIN_TRACKS("train_tracks"),
		FAIRGROUNDS("fair"),
		HOT_SPRING("spring"),
		SAWMILL("sawmill"),
		RIVER("river"),
		//
		UNDERGROUND("underground"),
		WILDERNESS(null);

		final String regionId;

		public String getName() {
			return StringUtils.camelCase(this);
		}

		public String getRegionId() {
			return regionPrefix + regionId;
		}

		public static Pugmas24District of(Location location) {
			if (!location.getWorld().equals(Pugmas24.get().getWorld()))
				return null;

			for (ProtectedRegion region : Pugmas24.get().worldguard().getRegionsAt(location)) {
				for (Pugmas24District district : Pugmas24District.values())
					if (region.getId().equalsIgnoreCase(district.getRegionId())) {
						if (district == UNDERGROUND && location.getBlock().getLightFromSky() > 0)
							continue;
						return district;
					}
			}

			return Pugmas24District.WILDERNESS;
		}
	}
}
