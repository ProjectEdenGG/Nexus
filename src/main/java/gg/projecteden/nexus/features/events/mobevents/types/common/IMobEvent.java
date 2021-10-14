package gg.projecteden.nexus.features.events.mobevents.types.common;

import gg.projecteden.nexus.features.events.mobevents.MobEventUtils;
import gg.projecteden.nexus.features.events.mobevents.annotations.Type;
import gg.projecteden.nexus.features.events.mobevents.types.common.WorldSet.Dimension;
import gg.projecteden.nexus.features.sleep.Sleep;
import gg.projecteden.nexus.features.sleep.SleepableWorld;
import gg.projecteden.nexus.features.sleep.SleepableWorld.State;
import gg.projecteden.nexus.models.difficulty.DifficultyService;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Data
public abstract class IMobEvent {
	protected String name;
	protected boolean started;
	protected boolean active;
	protected boolean ignoreLight;
	protected boolean ignoreFloor;
	protected List<MobOptions> mobOptionsList = new ArrayList<>();
	protected Set<UUID> spawnedEntities = new HashSet<>();
	private static final DifficultyService difficultyService = new DifficultyService();

	public void spawnMob(World world, List<Player> players) {
		DifficultyUser user;
		for (Player player : players) {
			EntityType entityType = getRandomType();
			MobOptions mobOptions = getMobOptions(entityType);
			if (mobOptions == null)
				continue;

			user = difficultyService.get(player);
			Difficulty playerDifficulty = user.getDifficulty();

			if (!extraChecks(player, mobOptions, user))
				continue;

			int spawnRadius = mobOptions.getSpawnRadius();
			int mobCap = mobOptions.getCap();
			int playerPercentage = playerDifficulty.getPercentage();
			int mobPercentage = mobOptions.getDifficulty().getPercentage();

			// if player difficulty is lower than mob difficulty, cancel spawn
			if (playerPercentage < mobPercentage) {
				continue;

				// if player difficulty is higher than mobOption, increase difficulty
			} else if (playerPercentage > mobPercentage) {
				int modifier = 4;
				switch (playerDifficulty) {
					case HARD:
						--modifier;
					case EXPERT:
						--modifier;
				}

				double percent = (double) playerPercentage / 100.0;
				spawnRadius += Math.ceil((percent * (double) (spawnRadius / modifier)));
				mobCap += Math.ceil((percent * (double) (mobCap / modifier)));
			}

			Location playerLoc = player.getLocation();

			// check mob caps
			int checkRadius = spawnRadius;
			if (entityType.equals(EntityType.GHAST))
				checkRadius = spawnRadius * 2;

			if (playerLoc.getNearbyEntitiesByType(entityType.getEntityClass(), checkRadius).size() > mobCap)
				continue;

			// find valid location
			Location spawnLoc = MobEventUtils.getRandomValidLocation(playerLoc, spawnRadius, 50, this, mobOptions);
			if (spawnLoc == null)
				continue;

			Entity entity = world.spawnEntity(spawnLoc, entityType, SpawnReason.RAID);

			// entity modifications
			try {
				entity = getModifier().handleEntity(entity, user);
				entity = handleEntity(entity, user, mobOptions);

				if (entity instanceof LivingEntity livingEntity) {
					livingEntity.setCanPickupItems(false);
					livingEntity.setRemoveWhenFarAway(false);
				}

				if (entity instanceof Mob mob)
					mob.setTarget(player);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			spawnedEntities.add(entity.getUniqueId());
		}
	}

	protected boolean extraChecks(Player player, MobOptions mobOptions, DifficultyUser user) {
		return true;
	}

	public Location handleLocation(Location location, MobOptions mobOptions) {
		return location;
	}

	protected Entity handleEntity(Entity entity, DifficultyUser user, MobOptions mobOptions) {
		return entity;
	}

	public EntityType getRandomType() {
		Map<EntityType, Double> weightMap = new HashMap<>();
		for (MobOptions mobOption : getMobOptionsList()) {
			weightMap.put(mobOption.getEntityType(), mobOption.getWeight());
		}

		return RandomUtils.getWeightedRandom(weightMap);
	}

	public MobOptions getMobOptions(EntityType entityType) {
		for (MobOptions mobOption : getMobOptionsList()) {
			if (entityType.equals(mobOption.getEntityType()))
				return mobOption;
		}
		return null;
	}

	public MobEventType getModifier() {
		return getClass().getAnnotation(Type.class).value();
	}

	public List<Dimension> getDimensions() {
		return getModifier().getDimensions();
	}

	public void queue(World world) {
		MobEventUtils.addEvent(world, this);

		SleepableWorld sleepableWorld = Sleep.getSleepableWorld(world);
		if (sleepableWorld == null)
			return;

		if (getModifier().canBeSkipped())
			sleepableWorld.setPercentOverride(getModifier().getSleepPercentage());
		else
			sleepableWorld.setState(State.LOCKED);

	}

	public void startEvent(World world) {
		startEvent(world, null);
	}

	public void startEvent(World world, Player debugger) {
		MobEventUtils.debug(debugger, "&3Starting event &e" + getName());
		if (isStarted()) {
			MobEventUtils.debug(debugger, "  &cEvent is already active");
			return;
		}
		setStarted(true);
		AtomicReference<List<Player>> players = new AtomicReference<>(getAffectingPlayers());

		// notify
		ActionBarUtils.sendActionBar(players.get(), getModifier().getWarningMessage());
		for (Player player : players.get()) {
			PlayerUtils.send(player, getModifier().getWarningMessage());
		}

		Tasks.wait(TickTime.SECOND.x(20), () -> {
			setActive(true);

			if (getModifier().freezeTime())
				world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

			players.set(getAffectingPlayers());

			// notify
			new SoundBuilder(Sound.BLOCK_BELL_RESONATE).receivers(players.get()).play();
			ActionBarUtils.sendActionBar(players.get(), getModifier().getStartMessage());
			for (Player player : players.get()) {
				PlayerUtils.send(player, getModifier().getStartMessage());
			}

			Tasks.wait(getModifier().getDuration(), () -> endEvent(world));
		});
	}

	public void endEvent(World world) {
		endEvent(world, null);
	}

	public void endEvent(World world, Player debugger) {
		MobEventUtils.debug(debugger, "&3Ending event &e" + getName());
		if (!isActive()) {
			MobEventUtils.debug(debugger, "  &cEvent is already ended");
			return;
		}

		List<Player> affectedPlayers = getAffectingPlayers();

		new SoundBuilder(Sound.BLOCK_BELL_RESONATE).receivers(affectedPlayers).play();
		ActionBarUtils.sendActionBar(affectedPlayers, getModifier().getEndMessage());
		for (Player player : getAffectingPlayers())
			PlayerUtils.send(player, getModifier().getEndMessage());

		removeEntities(world);

		if (getModifier().freezeTime())
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

		setActive(false);
		MobEventUtils.removeEvent(world);
		setStarted(false);

		SleepableWorld sleepableWorld = Sleep.getSleepableWorld(world);
		if (sleepableWorld != null) {
			if (getModifier().canBeSkipped())
				sleepableWorld.setPercentOverride(null);
			else
				Tasks.wait(TickTime.SECOND.x(5), () -> sleepableWorld.setState(null));
		}
	}

	public void removeEntities(World world) {
		removeEntities(world, false);
	}

	public void removeEntities(World world, boolean now) {
		int wait = 0;
		for (UUID uuid : new ArrayList<>(spawnedEntities)) {
			Entity entity = world.getEntity(uuid);
			spawnedEntities.remove(uuid);

			if (entity == null || entity.isDead())
				continue;

			if (now)
				removeEntity(entity);
			else
				Tasks.wait(wait++, () -> removeEntity(entity));
		}
	}

	private void removeEntity(Entity entity) {
		new SoundBuilder(Sound.ENTITY_CHICKEN_EGG).location(entity.getLocation()).pitch(0.1).play();
		entity.remove();
	}

	public List<Player> getAffectingPlayers() {
		return OnlinePlayers.where()
			.filter(player -> getModifier().applies(player.getWorld()))
			.filter(player -> player.getGameMode().equals(GameMode.SURVIVAL))
			.filter(player -> !PlayerUtils.isVanished(player))
			.filter(player -> !difficultyService.get(player).getDifficulty().equals(Difficulty.EASY))
			.filter(player -> !new GodmodeService().get(player).isActive())
			.get();
	}
}
