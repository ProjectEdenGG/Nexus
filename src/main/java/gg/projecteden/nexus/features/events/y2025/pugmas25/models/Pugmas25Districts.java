package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
	TODO:
		- IF PLAYER IS IN CAVES DISTRICT, SUBTRACT X FROM HEIGHT VARIABLE ON PDA
 */
public class Pugmas25Districts implements Listener {
	private static final String regionPrefix = Pugmas25.get().getRegionName() + "_district_";
	private static final Map<UUID, Pugmas25District> PLAYER_DISTRICT_MAP = new HashMap<>();

	public Pugmas25Districts() {
		Nexus.registerListener(this);
	}

	public static Pugmas25District of(Player player) {
		return PLAYER_DISTRICT_MAP.computeIfAbsent(player.getUniqueId(), k -> Pugmas25District.WILDERNESS);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		updateDistrict(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		updateDistrict(event.getPlayer());
	}

	private void updateDistrict(Player player) {
		if (!Pugmas25.get().shouldHandle(player))
			return;

		Pugmas25District newDistrict = Pugmas25District.of(player.getLocation());
		if (newDistrict == null)
			return;

		Pugmas25District currentDistrict = PLAYER_DISTRICT_MAP.get(player.getUniqueId());
		if (currentDistrict != null) {
			if (newDistrict == currentDistrict)
				return;
		}

		PLAYER_DISTRICT_MAP.put(player.getUniqueId(), newDistrict);
		Pugmas25.get().actionBar(player, "&3Area Designation: &e" + newDistrict.getName(), TickTime.SECOND.x(2));
	}


	@AllArgsConstructor
	public enum Pugmas25District {
		MINES("mines"),
		RIDGE("ridge"),
		WEST_VILLAGE("west"),
		EAST_VILLAGE("east"),
		FARM("farm"),
		PORT("port"),
		TRAIN_STATION("train_station"),
		TRAIN_TRACKS("train_tracks"),
		LAKE("lake"),
		FROZEN_LAKE("frozen_lake"),
		FAIRGROUNDS("fair"),
		HOT_SPRINGS("hot_springs"),
		SAWMILL("sawmill"),
		RIVER("river"),
		//
		CAVES("caves"),
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

		public static @Nullable Pugmas25Districts.Pugmas25District of(Location location) {
			if (!location.getWorld().equals(Pugmas25.get().getWorld()))
				return null;

			for (ProtectedRegion region : Pugmas25.get().worldguard().getRegionsAt(location)) {
				for (Pugmas25District district : Pugmas25District.values())
					if (region.getId().equalsIgnoreCase(district.getRegionId())) {
						if (district == UNDERGROUND && location.getBlock().getLightFromSky() > 0)
							continue;
						return district;
					}
			}

			return Pugmas25District.WILDERNESS;
		}
	}
}
