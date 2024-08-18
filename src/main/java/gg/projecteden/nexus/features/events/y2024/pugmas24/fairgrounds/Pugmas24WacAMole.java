package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.EntityUtils.forcePacket;

public class Pugmas24WacAMole implements Listener {

	private static final WorldGuardUtils worldguard = Pugmas24.get().worldguard();
	private static final WorldEditUtils worldedit = Pugmas24.get().worldedit();

	private static final String REGION = Pugmas24.get().getRegionName() + "_wacamole_";
	private static final String STANDS_REGION = REGION + "stands";
	private static final String PLAY_REGION = REGION + "play";

	@Getter
	private static boolean playing = false;
	private static Player gamer;
	private static int gameTaskId = -1;

	private static final List<Location> spawnLocations = new ArrayList<>();

	private static final List<ItemStack> kit = List.of(
		new ItemBuilder(Material.BOW).enchant(Enchant.INFINITY).unbreakable().build(),
		new ItemBuilder(Material.ARROW).unbreakable().build()
	);

	public Pugmas24WacAMole() {
		Nexus.registerListener(this);
		init(null);
	}

	public static void init(Player debugger) {
		spawnLocations.clear();

		worldedit.getBlocks(worldguard.getProtectedRegion(STANDS_REGION)).forEach(block -> {
			if ((block.getBlockData() instanceof Piston piston)) {
				if (piston.isExtended()) {
					Location location = block.getLocation().clone();
					location.add(0.5, -1.5, 0.5);
					location.setYaw(-90);
					spawnLocations.add(location);
				}
			}
		});
	}

	public static void reset() {
		Tasks.cancel(gameTaskId);
		armorStands.forEach(Entity::remove);
		PlayerUtils.removeItems(gamer, kit);
		//
		gamer = null;
		playing = false;
	}

	private static long gameTicks;
	private static final List<ArmorStand> armorStands = new ArrayList<>();
	private static final long UPDATE_INTERVAL = TimeUtils.TickTime.TICK.x(4);

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
		Pugmas24.get().give(gamer, kit);

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
	private static boolean spawnStands = true;
	private static final long MAX_LIFE_TICKS = TickTime.SECOND.x(2);

	private static void update() {
		if (gameTicks >= TimeUtils.TickTime.MINUTE.get()) {
			end();
			return;
		}

		// Remove any stands still active after maxStandTicks
		if (!activeStands.isEmpty()) {
			for (ArmorStand stand : activeStands.keySet()) {
				long ticks = activeStands.get(stand);
				long tickDiff = gameTicks - ticks;
				if (tickDiff >= MAX_LIFE_TICKS) {
					resetStand(stand);
				}
			}
		}

		if (activeStands.isEmpty())
			spawnStands = true;

		if (spawnStands || gameTicks % TimeUtils.TickTime.SECOND.x(2) == 0) {
			spawnStands = false;
			List<ArmorStand> standChoices = new ArrayList<>(armorStands);
			standChoices.removeAll(activeStands.keySet());

			int standCount = 3; //RandomUtils.randomInt(2, 5);
			for (int i = 0; i < standCount; i++) {
				ArmorStand stand = RandomUtils.randomElement(standChoices);
				stand.setVisible(true);

				forcePacket(stand);
				final Location to = stand.getLocation().clone().add(0, 1, 0);
				stand.teleport(to);

				activeStands.put(stand, gameTicks);
			}
		}
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

		if (!exists)
			return;

		if (!armorStand.isVisible())
			return;

		event.setCancelled(true);

		resetStand(armorStand);
		arrow.remove();
		new SoundBuilder(Sound.ENTITY_ARROW_HIT_PLAYER).location(player).volume(0.5).play();
	}

	private static void resetStand(ArmorStand stand) {
		stand.setVisible(false);
		activeStands.remove(stand);
		stand.teleport(stand.getLocation().clone().subtract(0, 1.5, 0));
	}

	private static void end() {
		armorStands.forEach(Entity::remove);
		Tasks.cancel(gameTaskId);

		//
		reset();
	}

	@EventHandler
	public void on(PlayerLeavingRegionEvent event) {
		if (!shouldHandle(event.getPlayer()))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(PLAY_REGION))
			return;

		event.setCancelled(true);
		Pugmas24.get().sendCooldown(gamer, "&cYou can't leave while playing the game", "pugmas24_wacamole_playing");
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

		if (!Pugmas24.get().shouldHandle(player))
			return false;

		return gamer.getUniqueId().equals(player.getUniqueId());
	}


}
