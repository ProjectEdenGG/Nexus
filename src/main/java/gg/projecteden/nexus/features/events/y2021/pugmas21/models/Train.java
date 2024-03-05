package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.EntityUtils.forcePacket;
import static gg.projecteden.nexus.utils.RandomUtils.randomDouble;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;
import static java.util.Comparator.comparing;

public class Train {
	private final Location location;
	private final BlockFace forwards;
	private final BlockFace backwards;
	private final double speed;
	private final int seconds;
	private final boolean test;
	private final Vector smokeBack;
	private final Vector smokeUp;

	@Getter
	private boolean active;
	private final List<ArmorStand> armorStands = new ArrayList<>();
	private final List<Integer> taskIds = new ArrayList<>();
	private Location lightLocation;

	private final WorldGuardUtils worldguard;

	private static final String REGION = Pugmas21.region("train");
	private static final int TOTAL_MODELS = 18;
	private static final double SEPARATOR = 7.5;

	@Getter
	private static final List<Train> instances = new ArrayList<>();

	public static boolean anyActiveInstances() {
		return instances.stream().anyMatch(Train::isActive);
	}

	@Builder
	public Train(Location location, BlockFace direction, double speed, int seconds, boolean test) {
		this.location = location.toCenterLocation();
		this.forwards = direction;
		this.backwards = direction.getOppositeFace();
		this.speed = speed;
		this.seconds = seconds;
		this.test = test;
		this.smokeBack = backwards.getDirection().multiply(4);
		this.smokeUp = BlockFace.UP.getDirection().multiply(5.3);
		this.worldguard = new WorldGuardUtils(location);
	}

	public static void schedule() {
		final Supplier<Long> delay = () -> TickTime.MINUTE.x(randomInt(5, 10));

		Tasks.wait(delay.get(), new AtomicReference<Runnable>() {{
			set(() -> {
				if (!Pugmas21.anyActivePlayers())
					return;

				getDefault().build().start();
				Pugmas21.actionBar("&c&lA train is passing by...", TickTime.SECOND.x(10));
				Tasks.wait(delay.get(), get());
			});
		}}.get());
	}

	public static TrainBuilder getDefault() {
		return Train.builder()
			.location(Pugmas21.location(112.5, 54, 7.5, 90, 0))
			.direction(BlockFace.WEST)
			.seconds(60)
			.speed(.3)
			.test(false);
	}

	public void start() {
		active = true;
		instances.add(this);

		taskIds.add(Tasks.wait(TickTime.SECOND.x(3), () ->
				new SoundBuilder(CustomSound.TRAIN_WHISTLE)
				.receivers(Pugmas21.getAllPlayers())
				.category(SoundCategory.AMBIENT)
				.volume(0.5)
				.play()));

		spawnArmorStands();

		taskIds.add(Tasks.repeat(0, 1, this::move));
		taskIds.add(Tasks.repeat(0, TickTime.SECOND, () ->
			Pugmas21.getPlayers("district_plaza").forEach(player -> {
				final ArmorStand nearest = getNearestArmorStand(player);
				if (nearest != null)
					new SoundBuilder(CustomSound.TRAIN_CHUG)
						.receiver(player)
						.location(nearest.getLocation())
						.category(SoundCategory.AMBIENT)
						.volume(MathUtils.clamp((63 - distance(player, nearest).getRealDistance()) * .03448275862, 0, 2))
						.play();
			})));

		taskIds.add(Tasks.wait(TickTime.SECOND.x(seconds), this::stop));
	}

	@Nullable
	private ArmorStand getNearestArmorStand(Player player) {
		final ArmorStand nearest = Collections.min(getValidArmorStands(), comparing(armorStand -> distance(player, armorStand).get()));
		if (nearest == null)
			return null;
		return nearest;
	}

	private List<ArmorStand> getValidArmorStands() {
		return armorStands.stream().filter(ArmorStand::isValid).toList();
	}

	public void stop() {
		taskIds.forEach(Tasks::cancel);
		armorStands.forEach(Entity::remove);

		active = false;
		instances.remove(this);
	}

	private void move() {
		for (ArmorStand armorStand : armorStands)
			move(armorStand);

		smoke();
		light();
	}

	private void smoke() {
		final Location location = getSmokeLocation();
		if (location != null)
			new Smoke(location);
	}

	private void light() {
		final Location front = front();
		if (front == null) {
			if (lightLocation.getBlock().getType() == Material.LIGHT)
				lightLocation.getBlock().setType(Material.AIR);
		} else {
			lightLocation = front.add(BlockFace.UP.getDirection());
			if (lightLocation.getBlock().getType() == Material.AIR) {
				lightLocation.getBlock().setType(Material.LIGHT);
				Light light = (Light) lightLocation.getBlock().getBlockData();
				light.setLevel(light.getMaximumLevel());
				lightLocation.getBlock().setBlockData(light);
			}
		}

		final Location backOneBlock = lightLocation.clone().add(backwards.getDirection());
		if (backOneBlock.getBlock().getType() == Material.LIGHT)
			backOneBlock.getBlock().setType(Material.AIR);
	}

	private void move(ArmorStand armorStand) {
		if (!armorStand.isValid())
			return;

		forcePacket(armorStand);
		final Location to = armorStand.getLocation().add(forwards.getDirection().multiply(speed));
		if (!isValidLocation(to))
			armorStand.remove();
		else
			armorStand.teleport(to);
	}

	private boolean isValidLocation(Location to) {
		return test || !worldguard.getRegionsLikeAt(REGION, to).isEmpty();
	}

	private Location getSmokeLocation() {
		final Location front = front();
		if (front == null)
			return null;

		return front.add(smokeBack).add(smokeUp);
	}

	private Location front() {
		final ArmorStand first = armorStands.iterator().next();
		if (!first.isValid())
			return null;

		return first.getLocation();
	}

	public void spawnArmorStands() {
		for (int i = 0; i < TOTAL_MODELS; i++) {
			armorStands.add(armorStand(i, location));
			location.add(backwards.getDirection().multiply(SEPARATOR));
		}
	}

	public static ArmorStand armorStand(int model, Location location) {
		return ArmorStandEditorCommand.summon(location, armorStand -> {
			armorStand.setVisible(false);
			armorStand.setItem(EquipmentSlot.HEAD, new ItemBuilder(CustomMaterial.PUGMAS21_TRAIN_1)
				.modelId(CustomMaterial.PUGMAS21_TRAIN_1.getModelId() + model)
				.build());
		});
	}

	public static class Smoke {
		private static final Particle particle = Particle.CAMPFIRE_COSY_SMOKE;
		private static final double x = 0;
		private static final double y = 3;
		private static final double z = 0;
		private static final double speed = 0.01;
		private static final int count = 0;

		public Smoke(Location location) {
			final World world = location.getWorld();
			final Supplier<Double> random = () -> randomDouble(-.25, .25);
			final Supplier<Location> offset = () -> location.clone().add(random.get(), random.get(), random.get());

			world.spawnParticle(particle, offset.get(), count, x, y, z, speed);
			world.spawnParticle(particle, offset.get(), count, x, y, z, speed);
		}
	}

}
