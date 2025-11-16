package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class Pugmas25Caves implements Listener {
	private static final double SPIDER_MAX_SCALE = 1.5;
	private static final double SPIDER_MIN_SCALE = 0.6;
	private static final double SPIDER_MOTHER_MIN_SCALE = 1.4;
	private static final double SPIDER_BABY_SCALE = 0.4;

	public Pugmas25Caves() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onMotherSpiderDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Mob mob))
			return;

		if (!Pugmas25.get().isAtEvent(mob))
			return;

		if (!Pugmas25.get().worldguard().isInRegion(mob, Pugmas25District.CAVES.getRegionId()))
			return;

		if (mob.getType() != EntityType.SPIDER)
			return;

		var attribute = mob.getAttribute(Attribute.SCALE);
		if (attribute == null)
			return;

		if (attribute.getBaseValue() < SPIDER_MOTHER_MIN_SCALE)
			return;

		if (!(event.getDamageSource().getCausingEntity() instanceof Player player))
			return;

		Location location = mob.getLocation();
		int spawnCount = RandomUtils.randomInt(2, 5);
		World world = location.getWorld();
		Tasks.wait(10, () -> {
			new ParticleBuilder(Particle.LARGE_SMOKE)
				.location(location).offset(0.25, 0.25, 0.25)
				.count(30)
				.extra(0)
				.spawn();

			for (int i = 0; i < spawnCount; i++) {
				double offsetX = RandomUtils.randomDouble(-0.5, 0.5);
				double offsetZ = RandomUtils.randomDouble(-0.5, 0.5);
				Location spawnLocation = location.clone().add(offsetX, 0.5, offsetZ);

				new SoundBuilder(Sound.ENTITY_SPIDER_HURT).pitch(2).location(spawnLocation).play();

				world.spawn(spawnLocation, Spider.class, _spider -> {
					_spider.setAggressive(true);
					_spider.setTarget(player);
					setAttributeBaseValue(_spider, Attribute.SCALE, SPIDER_BABY_SCALE);
					setAttributeBaseValue(_spider, Attribute.ATTACK_DAMAGE, 1); // 3 base
					setAttributeBaseValue(_spider, Attribute.MOVEMENT_SPEED, 0.2); // 0.3 base
					setAttributeBaseValue(_spider, Attribute.JUMP_STRENGTH, 0.63); // 0.42 base
				});

			}
		});
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

				var attributeScale = mob.getAttribute(Attribute.SCALE);
				if (attributeScale != null) {
					if (attributeScale.getBaseValue() != 1)
						return;
				}

				double randomScale = RandomUtils.randomDouble(SPIDER_MIN_SCALE, SPIDER_MAX_SCALE);
				if (setAttributeBaseValue(mob, Attribute.SCALE, randomScale)) {
					if (randomScale >= SPIDER_MOTHER_MIN_SCALE) {
						mob.setCustomName("Mother Spider");
						setAttributeBaseValue(mob, Attribute.FOLLOW_RANGE, 32); // 16 base
						setAttributeBaseValue(mob, Attribute.ATTACK_DAMAGE, 6); // 3 base
						setAttributeBaseValue(mob, Attribute.MOVEMENT_SPEED, 0.18); // 0.3 base
					}
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

	private boolean setAttributeBaseValue(LivingEntity entity, Attribute type, double value) {
		entity.registerAttribute(type);
		var attribute = entity.getAttribute(type);
		if (attribute == null)
			return false;

		attribute.setBaseValue(value);
		return true;
	}
}
