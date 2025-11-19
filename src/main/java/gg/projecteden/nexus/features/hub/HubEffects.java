package gg.projecteden.nexus.features.hub;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HubEffects extends Effects {
	// HALLOWEEN
	private final List<Location> bloodDripLocations = new ArrayList<>() {{
		add(location(2, 122, 57));
		add(location(3, 122, 54));
		add(location(-3, 122, 54));
		add(location(-2, 121, 57));
		add(location(-4, 120, 55));
		add(location(-2, 120, 53));
		add(location(4, 120, 55));
		add(location(3, 119, 56));
		add(location(1, 119, 56));
		add(location(-1, 119, 57));
		add(location(-2, 118, 55));
		add(location(1, 118, 55));
	}};

	private final Location heartLocation = location(0, 123, 55);
	private final Location bloodLocation = location(0, 121, 55);

	@Override
	public @NotNull String getRegion() {
		return Hub.getBaseRegion();
	}

	@Override
	public void onStart() {
		super.onStart();

		startModelTrain();
	}

	@Override
	public void onStop() {
		stopModelTrain();
	}

	@Override
	public void onEnterRegion(Player player) {
		PotionEffect unluck = new PotionEffectBuilder()
			.type(PotionEffectType.UNLUCK)
			.infinite()
			.particles(false)
			.ambient(false)
			.build();

		player.removePotionEffect(PotionEffectType.UNLUCK);
		player.addPotionEffect(unluck);
	}

	@Override
	public void onExitRegion(Player player) {
		player.removePotionEffect(PotionEffectType.UNLUCK);
	}

	@Override
	public void particles() {
		List<ParticleBuilder> particles = List.of(
			new ParticleBuilder(Particle.DRIPPING_DRIPSTONE_WATER)
				.location(location(100.5, 137.9, 39.5))
				.offset(0.15, 0, 0.15),

			new ParticleBuilder(Particle.SPLASH)
				.location(location(81.5, 130.3, 60.5))
				.offset(0.15, 0, 0.15)
		);

		Tasks.repeat(0, 2, () -> {
			for (ParticleBuilder particleBuilder : particles) {
				final Location location = particleBuilder.location();
				if (location == null || !location.isChunkLoaded())
					continue;

				if (RandomUtils.chanceOf(50))
					if (hasPlayersNearby(location, 25))
						particleBuilder.spawn();
			}
		});

		if (LocalDate.now().getMonth() == Month.OCTOBER) {
			// HALLOWEEN
			SoundBuilder heartbeat = new SoundBuilder("custom.misc.heartbeat").location(heartLocation).volume(2).pitch(0.1);
			ParticleBuilder heartParticles = new ParticleBuilder(Particle.CRIMSON_SPORE).location(heartLocation).offset(3, 2, 3).count(50).extra(0);
			Tasks.repeat(0, TickTime.SECOND.x(2.8), () -> {
				heartbeat.play();

				if (hasPlayersNearby(heartParticles.location(), 25))
					heartParticles.spawn();

			});

			SoundBuilder bloodGush = new SoundBuilder("custom.misc.blood").location(bloodLocation).volume(0.8).pitch(0.1);
			Tasks.repeat(0, TickTime.TICK.x(16), bloodGush::play);

			ParticleBuilder bloodDrip = new ParticleBuilder(Particle.FALLING_LAVA).extra(0.1).count(1);
			Tasks.repeat(0, TickTime.SECOND, () -> {
				if (!hasPlayersNearby(heartLocation, 25))
					return;

				for (Location location : bloodDripLocations) {
					if (location == null || !location.isChunkLoaded())
						continue;

					if (RandomUtils.chanceOf(25)) {
						Tasks.wait(RandomUtils.randomInt(1, 20), () -> bloodDrip.location(location.toCenterLocation().add(0, 0.45, 0)).spawn());
					}
				}
			});
		}
	}

	private static final List<ItemModelType> TREE_MINECART_MODELS = List.of(
		ItemModelType.PUGMAS_TRAIN_SET_ENGINE,
		ItemModelType.PUGMAS_TRAIN_SET_PASSENGER,
		ItemModelType.PUGMAS_TRAIN_SET_PASSENGER,
		ItemModelType.PUGMAS_TRAIN_SET_PASSENGER,
		ItemModelType.PUGMAS_TRAIN_SET_CARGO);
	private final Location treeMinecartStandSpawnLoc = location(-1, -1.25, -1);
	private static final List<ArmorStand> treeMinecartStands = new ArrayList<>();
	private final Location treeMinecartSpawnLoc = location(-1, -1.25, -1);
	private static final List<Minecart> treeMinecarts = new ArrayList<>();
	private static int treeMinecartStandTask = -1;
	private static final Map<UUID, Float> previousYaw = new HashMap<>();

	private void stopModelTrain() {
		treeMinecartStands.forEach(Entity::remove);
		treeMinecarts.forEach(Entity::remove);
		Tasks.cancel(treeMinecartStandTask);
	}

	private void startModelTrain() {
		/*
		 	TODO: SETUP LOCATIONS
		 		- treeMinecartStandSpawnLoc = Yellow Concrete at y + .25
		 		- treeMinecartSpawnLoc = Red Concrete at y + .25
		 */
		if (true)
			return;

		final int TRAIN_SIZE = TREE_MINECART_MODELS.size();
		for (int i = 0; i < TRAIN_SIZE; i++) {
			int finalI = i;
			ArmorStand armorStand = getWorld().spawn(treeMinecartStandSpawnLoc, ArmorStand.class, _stand -> {
				ItemStack cart = new ItemBuilder(TREE_MINECART_MODELS.get(finalI)).dyeColor(ColorType.PURPLE.getBukkitColor()).build();
				_stand.setHelmet(cart);
				_stand.setVisible(false);
				_stand.setInvulnerable(true);

				for (EquipmentSlot slot : EquipmentSlot.values()) {
					_stand.addEquipmentLock(slot, LockType.ADDING_OR_CHANGING);
					_stand.addEquipmentLock(slot, LockType.REMOVING_OR_CHANGING);
				}
			});
			treeMinecartStands.add(armorStand);
		}

		for (int i = 0; i < TRAIN_SIZE; i++) {
			ArmorStand armorStand = treeMinecartStands.get(i);

			Tasks.wait(i * 18L, () -> {
				Minecart minecart = getWorld().spawn(treeMinecartSpawnLoc, Minecart.class, _minecart -> {
					_minecart.setMaxSpeed(0.1);
					_minecart.setSlowWhenEmpty(false);
					_minecart.setInvulnerable(true);
					_minecart.setFrictionState(TriState.FALSE);
					_minecart.setVelocity(BlockFace.WEST.getDirection().multiply(0.1));
				});

				treeMinecarts.add(minecart);
				Tasks.wait(5L, () -> minecart.addPassenger(armorStand));
			});

		}

		treeMinecartStandTask = Tasks.repeat(TickTime.TICK.x(5), TickTime.TICK.x(2), () -> {
			treeMinecarts.forEach(minecart -> {
				if (!minecart.getChunk().isLoaded())
					return;

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
				});
			});
		});
	}

}
