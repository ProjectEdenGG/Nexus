package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pugmas25Caves implements Listener {
	private static final double SPIDER_MAX_SCALE = 1.5;
	private static final double SPIDER_MIN_SCALE = 0.6;
	private static final double SPIDER_MOTHER_MIN_SCALE = 1.4;
	private static final double SPIDER_BABY_SCALE = 0.4;

	private static final Map<Long, Integer> recentlyClearedChunks = new HashMap<>();
	private static final Map<Long, Integer> recentlyOpenedChunks = new HashMap<>();

	public Pugmas25Caves() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onMobDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Mob mob))
			return;

		if (!Pugmas25.get().isAtEvent(mob))
			return;

		if (!Pugmas25.get().worldguard().isInRegion(mob, Pugmas25District.CAVES.getRegionId()))
			return;

		if (mob.getKiller() == null)
			return;

		Chunk chunk = mob.getLocation().getChunk();
		long chunkKey = chunk.getChunkKey();
		int currentTick = Bukkit.getCurrentTick();

		if (recentlyOpenedChunks.containsKey(chunkKey)) {
			int openedTick = recentlyOpenedChunks.get(chunkKey);
			if (currentTick - openedTick < TickTime.MINUTE.x(5)) // cooldown before preventing spawns
				return;

			recentlyOpenedChunks.remove(chunkKey);
		}

		List<Entity> mobsLeft = Arrays.stream(chunk.getEntities())
			.filter(entity -> entity instanceof Mob)
			.toList();

		if (!mobsLeft.isEmpty())
			return;

		recentlyClearedChunks.put(chunkKey, currentTick);
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (!(event.getEntity() instanceof Mob mob))
			return;

		if (!Pugmas25.get().isAtEvent(mob))
			return;

		if (!Pugmas25.get().worldguard().isInRegion(mob, Pugmas25District.CAVES.getRegionId()))
			return;

		long chunkKey = mob.getLocation().getChunk().getChunkKey();
		if (!recentlyClearedChunks.containsKey(chunkKey))
			return;

		int clearedTick = recentlyClearedChunks.get(chunkKey);
		if (Bukkit.getCurrentTick() - clearedTick >= TickTime.MINUTE.x(5)) { // cooldown to prevent spawns
			recentlyClearedChunks.remove(chunkKey);
			recentlyOpenedChunks.put(chunkKey, Bukkit.getCurrentTick());
			return;
		}

		event.setCancelled(true);
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

		mob.setAggressive(true);

		switch (mob.getType()) {
			case SPIDER -> {
				if (RandomUtils.chanceOf(10)) {
					event.setCancelled(true);
					mob.getWorld().spawn(mob.getLocation(), CaveSpider.class);
				}

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
		}
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().shouldHandle(player))
			return;

		if (PlayerUtils.isWGEdit(event.getPlayer()))
			return;

		if (CooldownService.isOnCooldown(player.getUniqueId(), "pugmas25_cavewarp", TickTime.SECOND.x(2)))
			return;

		String regionId = event.getRegion().getId();
		CaveWarp caveWarp = CaveWarp.getCaveWarp(regionId);
		if (caveWarp == null)
			return;

		Location location = caveWarp.getOppositeLocation(regionId, player);
		int fadeStayTicks = caveWarp.getFadeTicks();

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
		HOLE(loc(-528.0, 72.5, -2866.0, -172), loc(-213.5, 80.5, -2960.5, -1))
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

		public int getFadeTicks() {
			if (this == MINES)
				return 20;

			return 10;
		}

		public Location getOppositeLocation(String regionId, Player player) {
			Location opposite = getAboveLoc();
			boolean above = !getAboveRegion().equalsIgnoreCase(regionId);

			if (!above) {
				opposite = getBelowLoc();
				if (this == CaveWarp.HOLE)
					opposite.setYaw(player.getLocation().getYaw());
			}

			opposite.setPitch(player.getLocation().getPitch());

			return opposite;
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
