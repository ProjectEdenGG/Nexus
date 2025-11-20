package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.Tasks;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pugmas25ModelTrain implements Listener {

	private static final List<ItemModelType> TREE_MINECART_MODELS = List.of(ItemModelType.PUGMAS_TRAIN_SET_ENGINE, ItemModelType.PUGMAS_TRAIN_SET_PASSENGER, ItemModelType.PUGMAS_TRAIN_SET_PASSENGER, ItemModelType.PUGMAS_TRAIN_SET_CARGO);
	private static final Location treeMinecartStandSpawnLoc = Pugmas25.get().location(-680.5, 115.25, -3110.5);
	private static final List<ArmorStand> treeMinecartStands = new ArrayList<>();
	private static final List<Entity> treeMinecartSeatEntities = new ArrayList<>();
	private static final Location treeMinecartSpawnLoc = Pugmas25.get().location(-680.5, 116.25, -3111.5);
	private static final List<Minecart> treeMinecarts = new ArrayList<>();
	private static int treeMinecartStandTask = -1;
	private static final Map<UUID, Float> previousYaw = new HashMap<>();
	private static boolean modelTrainStarted = false;
	private static World world = Pugmas25.get().getWorld();

	public Pugmas25ModelTrain() {
		Nexus.registerListener(this);
	}

	private static final Location treeCenter = Pugmas25.get().location(-679.0, 115.0, -3117.0);
	private static int modelTrainCheckerTask = -1;

	public static void startup() {
		modelTrainCheckerTask = Tasks.repeat(5, TickTime.SECOND.x(2), () -> {
			if (Pugmas25.get().getOnlinePlayers().radius(treeCenter, 30).get().isEmpty()) {
				if (modelTrainStarted)
					start();
			} else {
				if (!modelTrainStarted)
					stop();
			}
		});
	}

	public static void shutdown() {
		Tasks.cancel(modelTrainCheckerTask);
		stop();
	}

	public static void stop() {
		modelTrainStarted = false;
		Tasks.cancel(treeMinecartStandTask);
		treeMinecartSeatEntities.forEach(Entity::remove);
		treeMinecartStands.forEach(Entity::remove);
		treeMinecarts.forEach(Entity::remove);
		treeMinecartSeatEntities.clear();
		treeMinecartStands.clear();
		treeMinecarts.clear();
	}

	public static void start() {
		modelTrainStarted = true;

		final int TRAIN_SIZE = TREE_MINECART_MODELS.size();
		for (int i = 0; i < TRAIN_SIZE; i++) {
			int finalI = i;
			ArmorStand trainStand = world.spawn(treeMinecartStandSpawnLoc, ArmorStand.class, _stand -> {
				ItemStack cart = new ItemBuilder(TREE_MINECART_MODELS.get(finalI)).dyeColor(ColorType.PURPLE.getBukkitColor()).build();
				_stand.setHelmet(cart);
				_stand.setVisible(false);
				_stand.setInvulnerable(true);

				for (EquipmentSlot slot : EquipmentSlot.values()) {
					_stand.addEquipmentLock(slot, LockType.ADDING_OR_CHANGING);
					_stand.addEquipmentLock(slot, LockType.REMOVING_OR_CHANGING);
				}
			});
			treeMinecartStands.add(trainStand);

			Slime slime = world.spawn(treeMinecartStandSpawnLoc, Slime.class, _slime -> {
				_slime.setInvisible(true);
				_slime.setInvulnerable(true);
				_slime.setSize(1);
				_slime.setAI(false);
			});

			treeMinecartSeatEntities.add(slime);
		}

		for (int i = 0; i < TRAIN_SIZE; i++) {
			ArmorStand trainStand = treeMinecartStands.get(i);
			Entity seat = treeMinecartSeatEntities.get(i);

			Tasks.wait(i * 18L, () -> {
				Minecart minecart = world.spawn(treeMinecartSpawnLoc, Minecart.class, _minecart -> {
					_minecart.setMaxSpeed(0.1);
					_minecart.setSlowWhenEmpty(false);
					_minecart.setInvulnerable(true);
					_minecart.setFrictionState(TriState.FALSE);
					_minecart.setVelocity(BlockFace.WEST.getDirection().multiply(0.1));
				});

				treeMinecarts.add(minecart);
				minecart.addPassenger(trainStand);
				trainStand.addPassenger(seat);
			});
		}

		treeMinecartStandTask = Tasks.repeat(TickTime.TICK.x(5), TickTime.TICK.x(2), () -> {
			if (!modelTrainStarted)
				return;

			treeMinecarts.forEach(minecart -> {
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
	public void on(VehicleEntityCollisionEvent event) {
		Entity entity = event.getVehicle();

		// Skip server event location checks since it checks UUID

		treeMinecarts.forEach(minecart -> {
			if (entity.getUniqueId().equals(minecart.getUniqueId())) {
				event.setCancelled(true);
			}
		});
	}

}
