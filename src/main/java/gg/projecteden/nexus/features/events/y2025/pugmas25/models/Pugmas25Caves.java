package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public class Pugmas25Caves implements Listener {

	public Pugmas25Caves() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(CreatureSpawnEvent event) {
		if (!(event.getEntity() instanceof Mob mob))
			return;

		if (!Pugmas25.get().isAtEvent(mob))
			return;

		if (!Pugmas25.get().worldguard().isInRegion(mob, Pugmas25District.CAVES.getRegionId()))
			return;

		switch (mob.getType()) {
			case SPIDER -> {
				mob.setAggressive(true);

				// Randomize spider scale 0.6-1
				var attribute = mob.getAttribute(Attribute.SCALE);
				if (attribute != null) {
					double newValue = attribute.getBaseValue() - RandomUtils.randomDouble(0, 0.4);
					attribute.setBaseValue(newValue);
				}
			}
			case POLAR_BEAR -> mob.setAggressive(true);
		}
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
		ICE(loc(-498.5, 119.5, -3126.5, 18), loc(-187.5, 55.5, -3055.5, 180)),
		MINESHAFT(loc(-781.0, 68.0, -3029.5, 180), loc(-382.0, 70.0, -3019.5, 180)),
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
