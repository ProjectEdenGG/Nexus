package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.models.Train;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Pugmas25ModelTrain implements Listener {

	private static final ItemModelType TRAIN_FRONT = ItemModelType.PUGMAS_TRAIN_SET_ENGINE;
	private static final ItemModelType TRAIN_END = ItemModelType.PUGMAS_TRAIN_SET_CABOOSE;
	private static final Set<ItemModelType> TRAIN_CARS = Set.of(
		ItemModelType.PUGMAS_TRAIN_SET_PASSENGER,
		ItemModelType.PUGMAS_TRAIN_SET_CARGO_COAL,
		ItemModelType.PUGMAS_TRAIN_SET_CARGO_TREE,
		ItemModelType.PUGMAS_TRAIN_SET_CARGO_EMPTY
	);

	private static final List<ColorType> TRAIN_COLORS = List.of(ColorType.RED, ColorType.LIGHT_RED, ColorType.LIGHT_GREEN, ColorType.GREEN,
		ColorType.CYAN, ColorType.LIGHT_BLUE, ColorType.BLUE, ColorType.PURPLE, ColorType.MAGENTA, ColorType.PINK);

	private static final Location standSpawnLoc = Pugmas25.get().location(-680.5, 115.25, -3110.5);
	private static final Location minecartSpawnLoc = Pugmas25.get().location(-680.5, 116.25, -3111.5);
	private static final Location trackCenter = Pugmas25.get().location(-679.0, 115.0, -3117.0);
	private static final World world = Pugmas25.get().getWorld();

	private static final List<ArmorStand> trainStands = new ArrayList<>();
	private static final List<Entity> seatEntities = new ArrayList<>();
	private static final List<Minecart> minecarts = new ArrayList<>();
	private static final Map<UUID, Float> previousYaw = new HashMap<>();
	private static int standTask = -1;
	private static int radiusCheckTask = -1;
	private static int soundTask = -1;
	private static boolean started = false;
	//
	@Getter
	@Setter
	private static int trainLength = 5;


	public Pugmas25ModelTrain() {
		Nexus.registerListener(this);
	}

	public static void startup() {
		radiusCheckTask = Tasks.repeat(5, TickTime.SECOND.x(2), () -> {
			if (Pugmas25.get().getOnlinePlayers().radius(trackCenter, 30).get().isEmpty()) {
				if (started)
					stop();
			} else {
				if (!started)
					start();
			}
		});
	}

	public static void shutdown() {
		Tasks.cancel(radiusCheckTask);
		stop();
	}

	public static void stop() {
		started = false;
		Tasks.cancel(soundTask);
		Tasks.cancel(standTask);
		seatEntities.forEach(Entity::remove);
		trainStands.forEach(Entity::remove);
		minecarts.forEach(Entity::remove);
		seatEntities.clear();
		trainStands.clear();
		minecarts.clear();
	}

	public static void start() {
		started = true;

		SoundBuilder whistle = new SoundBuilder(CustomSound.TRAIN_WHISTLE).category(SoundCategory.AMBIENT).volume(0.1).pitch(1.5);
		soundTask = Tasks.repeat(5, TickTime.SECOND.x(10), () -> {
			if (!started)
				return;

			if (!RandomUtils.chanceOf(25))
				return;

			for (Player player : Pugmas25.get().getPlayers()) {
				double radiusVolume = Train.getRadiusVolume(player, seatEntities.getFirst().getLocation(), 15, true, 0.01, 0.1);
				whistle.clone().receiver(player).volume(radiusVolume).play();
			}
		});

		List<ItemModelType> trainModels = new ArrayList<>();
		trainModels.add(TRAIN_FRONT);
		for (int i = 0; i < (trainLength - 2); i++) {
			trainModels.add(RandomUtils.randomElement(TRAIN_CARS));
		}
		trainModels.add(TRAIN_END);

		ColorType trainColor = RandomUtils.randomElement(TRAIN_COLORS);
		for (int i = 0; i < trainLength; i++) {
			int finalI = i;
			ArmorStand trainStand = world.spawn(standSpawnLoc, ArmorStand.class, _stand -> {
				ItemStack cart = new ItemBuilder(trainModels.get(finalI)).dyeColor(trainColor.getBukkitColor()).build();
				_stand.setHelmet(cart);
				_stand.setVisible(false);
				_stand.setInvulnerable(true);

				for (EquipmentSlot slot : EquipmentSlot.values()) {
					_stand.addEquipmentLock(slot, LockType.ADDING_OR_CHANGING);
					_stand.addEquipmentLock(slot, LockType.REMOVING_OR_CHANGING);
				}
			});
			trainStands.add(trainStand);

			Slime slime = world.spawn(standSpawnLoc, Slime.class, _slime -> {
				_slime.setInvisible(true);
				_slime.setInvulnerable(true);
				_slime.setSize(1);
				_slime.setAI(false);
			});

			seatEntities.add(slime);
		}

		for (int i = 0; i < trainLength; i++) {
			ArmorStand trainStand = trainStands.get(i);
			Entity seat = seatEntities.get(i);

			Tasks.wait(i * 18L, () -> {
				Minecart minecart = world.spawn(minecartSpawnLoc, Minecart.class, _minecart -> {
					_minecart.setMaxSpeed(0.1);
					_minecart.setSlowWhenEmpty(false);
					_minecart.setInvulnerable(true);
					_minecart.setFrictionState(TriState.FALSE);
					_minecart.setVelocity(BlockFace.WEST.getDirection().multiply(0.1));
				});

				minecarts.add(minecart);
				minecart.addPassenger(trainStand);
				trainStand.addPassenger(seat);
			});
		}

		standTask = Tasks.repeat(TickTime.TICK.x(5), TickTime.TICK.x(2), () -> {
			if (!started)
				return;

			minecarts.forEach(minecart -> {
				Vector velocity = minecart.getVelocity();

				if (velocity.lengthSquared() <= 0.0001) // avoids NaN when stopped
					return;

				float globalYaw = (float) Math.toDegrees(Math.atan2(-velocity.getX(), velocity.getZ()));

				minecart.getPassengers().forEach(passenger -> {
					if (!(passenger instanceof ArmorStand armorStand))
						return;

					UUID uuid = armorStand.getUniqueId();
					float prevYaw = previousYaw.getOrDefault(uuid, globalYaw);
					float smoothedYaw = MathUtils.rotLerp(0.5f, prevYaw, globalYaw);
					float deltaYaw = smoothedYaw - prevYaw;

					// Get normalized head pose
					EulerAngle currentAngle = armorStand.getHeadPose();
					double normalizedHeadYaw = MathUtils.wrapRadians(currentAngle.getY());

					// Apply delta
					double newHeadYaw = normalizedHeadYaw + Math.toRadians(deltaYaw);

					EulerAngle newAngle = new EulerAngle(currentAngle.getX(), newHeadYaw, currentAngle.getZ());

					armorStand.setHeadPose(newAngle);
					previousYaw.put(uuid, smoothedYaw);

					armorStand.getPassengers().forEach(slime -> slime.setRotation(smoothedYaw, 0f));
				});
			});
		});
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!Pugmas25.get().isAtEvent(event.getPlayer()))
			return;

		if (!(event.getRightClicked() instanceof Slime slime))
			return;

		if (!slime.getPassengers().isEmpty())
			return;

		if (slime.isInvisible() && !slime.hasAI())
			slime.addPassenger(event.getPlayer());
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player))
			return;

		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (!(event.getEntity() instanceof Slime slime))
			return;

		if (slime.isInvisible() && !slime.hasAI())
			event.setCancelled(true);
	}

	@EventHandler
	public void on(VehicleEntityCollisionEvent event) {
		Entity entity = event.getVehicle();

		// Skip server event location checks since it checks UUID

		minecarts.forEach(minecart -> {
			if (entity.getUniqueId().equals(minecart.getUniqueId())) {
				event.setCancelled(true);
			}
		});
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (!player.isInsideVehicle())
			return;

		player.leaveVehicle();
	}

}
