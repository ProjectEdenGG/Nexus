package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Cutscene;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.api.common.utils.RandomUtils.chanceOf;

public class Pugmas25Caves implements Listener {
	private static final String EXTRACTINATOR_REGION = Pugmas25.get().getRegionName() + "_extractinator";
	private static final String EXTRACTINATOR_PREFIX = StringUtils.getPrefix("Extractinator");
	private static final double SPIDER_MAX_SCALE = 1.2;
	private static final double SPIDER_MIN_SCALE = 0.6;
	private static final double SPIDER_QUEEN_SCALE = 1.5;
	private static final double SPIDER_BABY_SCALE = 0.4;

	private static final Map<Long, Integer> recentlyClearedChunks = new HashMap<>();
	private static final Map<Long, Integer> recentlyOpenedChunks = new HashMap<>();

	private static int polarBearTask = -1;
	private static int playerCheckerTask = -1;

	public Pugmas25Caves() {
		Nexus.registerListener(this);

		polarBearTask = Tasks.repeat(5, TickTime.SECOND.x(5), () -> {
			String region = Pugmas25District.CAVES.getRegionId();
			Pugmas25.get().worldguard().getEntitiesInRegionByClass(region, PolarBear.class).forEach(polarBear -> {
				polarBear.setAggressive(true);
				Player target = PlayerUtils.getNearestVisiblePlayer(polarBear.getLocation(), 15).getObject();
				polarBear.setTarget(target);
			});
		});

		playerCheckerTask = Tasks.repeat(5, TickTime.SECOND.x(30), () -> {
			String region = Pugmas25District.CAVES.getRegionId();
			int players = Pugmas25.get().worldguard().getPlayersInRegion(region).size();
			if (players > 0)
				return;

			Pugmas25.get().worldguard().getEntitiesInRegion(region).stream()
				.filter(entity -> entity instanceof Mob)
				.forEach(Entity::remove);
		});
	}

	public static void shutdown() {
		Tasks.cancel(playerCheckerTask);
		Tasks.cancel(polarBearTask);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!Pugmas25.get().isAtEvent(event))
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		if (block.getType() != Material.BARRIER)
			return;

		if (!Pugmas25.get().worldguard().isInRegion(block.getLocation(), EXTRACTINATOR_REGION))
			return;

		Player player = event.getPlayer();
		if (!CommonQuestItem.COIN_POUCH.isInInventoryOf(player)) {
			event.setCancelled(true);
			PlayerUtils.send(player, Pugmas25.PREFIX + "&cYou need your Coin Pouch to interact with this");
			return;
		}

		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(tool) || !Pugmas25QuestItem.SUSPICIOUS_DEBRIS.fuzzyMatch(tool))
			return;

		event.setCancelled(true);
		boolean lucky = Pugmas25QuestItem.LUCKY_HORSESHOE.isInInventoryOf(player);
		ItemStack drop = getExtractinatorDrop(player, lucky);
		if (drop == null)
			return;

		// TODO GRIFFIN Stack at a time

		ItemUtils.subtract(player, tool);
		int wait = 0;
		for (int i = 0; i < 4; i++) {
			Tasks.wait(wait += 5, () -> new SoundBuilder(Sound.BLOCK_GRAVEL_BREAK).volume(0.5).pitch(0.25).location(block).play());
		}

		wait += 5;
		Tasks.wait(wait, () -> {
			new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).volume(0.5).pitch(2).location(player).play();
			PlayerUtils.giveItem(player, drop);
		});
	}

	private static final Map<Material, Double> weightedExtractinatorDrops = new HashMap<>() {{
		put(Material.AIR, 30.0);
		put(Material.STICK, 25.0);
		put(Material.STRING, 25.0);
		put(Material.FEATHER, 25.0);
		put(Material.COAL, 25.0);
		put(Material.RAW_COPPER, 22.0);
		put(Material.RAW_GOLD, 22.0);
		put(Material.RAW_IRON, 20.0);
		put(Material.FLINT, 18.0);
		put(Material.GOLD_NUGGET, 18.0);
		put(Material.GOLD_INGOT, 16.0);
		put(Material.COPPER_INGOT, 16.0);
		put(Material.IRON_NUGGET, 13.0);
		put(Material.IRON_INGOT, 10.0);
		put(Material.LAPIS_LAZULI, 10.0);
		put(Material.EMERALD, 5.0);
		put(Material.DIAMOND, 1.0);
		put(Material.NETHERITE_SCRAP, 0.25);
	}};

	public ItemStack getExtractinatorDrop(Player player, boolean lucky) {
		int luckMin = lucky ? RandomUtils.randomInt(5, 20) : 0;
		int luck = RandomUtils.randomInt(luckMin, 30);

		// Coins
		if (chanceOf(5)) {
			int coinAmount = Pugmas25.getLuckyAmount(1, 10, luck);
			try {
				Currency.COIN_POUCH.deposit(player, Price.of(coinAmount));
				PlayerUtils.send(player, EXTRACTINATOR_PREFIX + "&3Deposited &e" + coinAmount + " coins &3to Coin Pouch");
			} catch (Exception e) {
				PlayerUtils.send(player, e.getMessage());
			}
			return null;
		}

		Material type = RandomUtils.getWeightedRandom(weightedExtractinatorDrops);
		if (type == Material.AIR)
			return null;

		return new ItemBuilder(type).amount(Pugmas25.getLuckyAmount(1, 3, luck)).build();
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
	public void onQueenSpiderDeath(EntityDeathEvent event) {
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

		if (attribute.getBaseValue() <= SPIDER_MAX_SCALE)
			return;

		if (!(event.getDamageSource().getCausingEntity() instanceof Player player))
			return;

		Location location = mob.getLocation();
		int spawnCount = RandomUtils.randomInt(3, 7);
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

		if (mob.getType() != EntityType.POLAR_BEAR && Pugmas25.get().worldguard().isInRegion(mob, "pugmas25_biome_ice")) {
			if (RandomUtils.chanceOf(10)) {
				event.setCancelled(true);
				mob.getWorld().spawn(mob.getLocation(), PolarBear.class);
				return;
			}
		}

		if (mob.getType() != EntityType.SPIDER)
			return;

		if (chanceOf(10)) {
			event.setCancelled(true);
			mob.getWorld().spawn(mob.getLocation(), CaveSpider.class);
		}

		var attributeScale = mob.getAttribute(Attribute.SCALE);
		if (attributeScale != null) {
			if (attributeScale.getBaseValue() != 1)
				return;
		}

		double randomScale = RandomUtils.randomDouble(SPIDER_MIN_SCALE, SPIDER_MAX_SCALE);
		if (!setAttributeBaseValue(mob, Attribute.SCALE, randomScale))
			return;

		if (!chanceOf(8))
			return;

		mob.setCustomName("Queen Spider");
		setAttributeBaseValue(mob, Attribute.SCALE, SPIDER_QUEEN_SCALE);
		setAttributeBaseValue(mob, Attribute.FOLLOW_RANGE, 32); // 16 base
		setAttributeBaseValue(mob, Attribute.ATTACK_DAMAGE, 6); // 3 base
		setAttributeBaseValue(mob, Attribute.MOVEMENT_SPEED, 0.18); // 0.3 base
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

		new Pugmas25Cutscene()
			.fade(0, fadeStayTicks)
			.next(fadeStayTicks, _player -> _player.teleport(location))
			.start(player);
	}

	@Getter
	@AllArgsConstructor
	public enum CaveWarp {
		MINES(loc(-746.5, 104.5, -3153.5, 90), loc(-268.5, 40.5, -3037.5, -90)),
		SPRINGS(loc(-473.5, 108.5, -3101, 45), loc(-273.5, 35.5, -2963.5, -126)),
		ICE(loc(-498.5, 119.5, -3126.5, 18), loc(-187.5, 55.5, -3055.5, 180)),
		MINESHAFT(loc(-781.0, 68.0, -3029.5, 180), loc(-382.0, 70.0, -3019.5, -40)),
		HOLE(loc(-528.0, 72.5, -2866.0, -172), loc(-213.5, 80.5, -2960.5, -1)),
		LUSH(loc(-720.5, 142.5, -3276.5, -90), loc(-351.5, 71.5, -3075.5, 90)),
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
		var attribute = entity.getAttribute(type);
		if (attribute == null)
			return false;

		attribute.setBaseValue(value);
		return true;
	}
}
