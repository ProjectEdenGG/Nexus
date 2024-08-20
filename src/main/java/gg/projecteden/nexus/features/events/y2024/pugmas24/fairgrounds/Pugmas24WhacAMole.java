package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.EntityUtils.forcePacket;

public class Pugmas24WhacAMole implements Listener {
	private static final Pugmas24 PUGMAS = Pugmas24.get();
	private static final WorldGuardUtils worldguard = PUGMAS.worldguard();
	private static final WorldEditUtils worldedit = PUGMAS.worldedit();

	private static final String BASE_REGION = PUGMAS.getRegionName() + "_whacamole_";
	private static final String STANDS_REGION = BASE_REGION + "stands";
	private static final String PLAY_REGION = BASE_REGION + "play";
	private static final String CLEAN_ARROWS_REGION = BASE_REGION + "arrows";

	@Getter
	private static boolean playing = false;
	private static Player gamer;
	private static int score = 0;
	private static int gameTaskId = -1;
	private static long gameTicks;

	private static final List<Location> spawnLocations = new ArrayList<>();
	private static final List<ArmorStand> armorStands = new ArrayList<>();
	private static final long UPDATE_INTERVAL = TimeUtils.TickTime.TICK.x(4);

	private static final Material targetGood = Material.TARGET;
	private static final Material targetBad = Material.TNT;
	private static final List<Material> targetItems = java.util.List.of(targetGood, targetGood, targetBad);

	private static List<ItemStack> kit = new ArrayList<>();

	private static final Hologram holoTimeLeft = HologramsAPI.byId(PUGMAS.getWorld(), "pugmas24_whacamole_time_left");
	private static final Hologram holoScore = HologramsAPI.byId(PUGMAS.getWorld(), "pugmas24_whacamole_score");

	public Pugmas24WhacAMole() {
		Nexus.registerListener(this);

		kit = List.of(
			new ItemBuilder(Material.BOW).enchant(Enchant.INFINITY).unbreakable().build(),
			new ItemBuilder(Material.ARROW).unbreakable().build());

		init();
	}

	public static void init() {
		spawnLocations.clear();

		worldedit.getBlocks(worldguard.getProtectedRegion(STANDS_REGION)).forEach(block -> {
			if ((block.getBlockData() instanceof Piston piston)) {
				if (piston.isExtended()) {
					Location location = block.getLocation().clone();
					location.add(0.2, -1.5, 0.5);
					location.setYaw(-90);
					spawnLocations.add(location);
				}
			}
		});
	}

	public static void reset() {
		Tasks.cancel(gameTaskId);
		armorStands.forEach(Entity::remove);
		if (gamer != null)
			PlayerUtils.removeItems(gamer, kit);
		worldguard.getEntitiesInRegion(CLEAN_ARROWS_REGION).stream()
			.filter(entity -> entity.getType() == EntityType.ARROW)
			.forEach(Entity::remove);
		//
		score = 0;
		gameTicks = 0;
		gamer = null;
		playing = false;
	}

	public static void start(Player player) {
		Pugmas24 pugmas = Pugmas24.get();
		if (Nexus.isMaintenanceQueued()) {
			pugmas.send(player, "&cServer maintenance is queued, try again later");
			return;
		}

		if (playing) {
			pugmas.send(player, "&cThe game is already being played");
			return;
		}

		if (!PlayerUtils.hasRoomFor(player, kit)) {
			pugmas.send(player, "&cYou don't have enough room for the kit");
			return;
		}

		playing = true;
		gamer = player;
		gameTicks = 0;
		score = 0;
		updateScore();
		updateTime();
		PUGMAS.give(gamer, kit);
		armorStands.forEach(Entity::remove);
		armorStands.clear();

		for (Location location : spawnLocations) {

			ArmorStand armorStand = gamer.getWorld().spawn(location, ArmorStand.class, stand -> {
				stand.setRightArmPose(EulerAngle.ZERO);
				stand.setLeftArmPose(EulerAngle.ZERO);
				stand.setHeadPose(EulerAngle.ZERO);
				stand.setVisible(false);
				stand.setInvulnerable(true);
				stand.setGravity(false);
				stand.setBasePlate(false);
				stand.setDisabledSlots(EquipmentSlot.values());
			});

			armorStands.add(armorStand);
		}

		gameTaskId = Tasks.repeat(0, UPDATE_INTERVAL, () -> {
			update();
			gameTicks += UPDATE_INTERVAL;
		});
	}

	private static final Map<ArmorStand, Long> activeStands = new ConcurrentHashMap<>();
	private static final long MAX_LIFE_TICKS = TickTime.SECOND.x(3);

	private static void update() {
		updateTime();

		if (gameTicks >= TimeUtils.TickTime.MINUTE.get()) {
			end();
			return;
		}

		Map<ArmorStand, Long> _activeStands = new HashMap<>(activeStands);

		// Remove any stands still active after maxStandTicks
		if (!_activeStands.isEmpty()) {
			for (ArmorStand stand : _activeStands.keySet()) {
				long ticks = _activeStands.get(stand);
				long tickDiff = gameTicks - ticks;
				if (tickDiff >= MAX_LIFE_TICKS) {
					resetStand(stand);
				}
			}
		}

		if (gameTicks % TimeUtils.TickTime.SECOND.x(3) == 0) {
			List<ArmorStand> standChoices = new ArrayList<>(armorStands);
			standChoices.removeAll(_activeStands.keySet());

			int standCount = RandomUtils.randomInt(3, 5);
			for (int i = 0; i < standCount; i++) {
				ArmorStand stand = RandomUtils.randomElement(standChoices);
				stand.getEquipment().setHelmet(new ItemStack(RandomUtils.randomElement(targetItems)));

				forcePacket(stand);
				stand.teleport(getStandBaseLocation(stand).add(0, 1, 0));

				activeStands.put(stand, gameTicks);
			}
		}
	}

	private static Location getStandBaseLocation(ArmorStand stand) {
		return spawnLocations.get(armorStands.indexOf(stand)).clone();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow))
			return;

		if (!(arrow.getShooter() instanceof Player player))
			return;

		if (!shouldHandle(player))
			return;

		if (!(event.getHitEntity() instanceof ArmorStand armorStand))
			return;

		boolean exists = false;
		for (ArmorStand stand : activeStands.keySet()) {
			if (stand.getUniqueId().equals(armorStand.getUniqueId()))
				exists = true;
		}

		if (!exists) {
			return;
		}

		event.setCancelled(true);
		arrow.remove();

		ItemStack headItem = armorStand.getEquipment().getItem(EquipmentSlot.HEAD);
		if (Nullables.isNotNullOrAir(headItem)) {
			Material type = headItem.getType();
			if (type == targetGood) {
				new SoundBuilder(Sound.ENTITY_ARROW_HIT_PLAYER).location(gamer).volume(0.5).play();
				score++;
			} else if (type == targetBad) {
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO).location(gamer).volume(0.5).pitch(0.5).play();
				score -= 2;
			}

			updateScore();
		}

		resetStand(armorStand);
	}

	private static void resetStand(ArmorStand stand) {
		activeStands.remove(stand);
		stand.teleport(getStandBaseLocation(stand).subtract(0, 1, 0));
		stand.getEquipment().setHelmet(null);
	}

	private static void end() {
		armorStands.forEach(Entity::remove);
		Tasks.cancel(gameTaskId);
		PUGMAS.send(gamer, "Ending Score = " + score);
		//
		reset();
	}

	private static void updateScore() {
		holoScore.setLine(0, "&3Score: &e" + score);
	}

	private static void updateTime() {
		if (gameTicks % TickTime.SECOND.get() == 0) {
			int secondsLeft = (int) ((TickTime.MINUTE.get() - gameTicks) / TickTime.SECOND.get());
			holoTimeLeft.setLine(0, "&3Time Left: &e" + secondsLeft + "s");
		}
	}

	@EventHandler
	public void on(PlayerLeavingRegionEvent event) {
		if (!shouldHandle(event.getPlayer()))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(PLAY_REGION))
			return;

		event.setCancelled(true);
		PUGMAS.sendCooldown(gamer, "&cYou can't leave while playing the game", "pugmas24_whacamole_playing");
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!shouldHandle(player)) return;

		reset();
	}

	private static boolean shouldHandle(Player player) {
		if (!playing)
			return false;

		if (!PUGMAS.shouldHandle(player))
			return false;

		return gamer.getUniqueId().equals(player.getUniqueId());
	}


}
