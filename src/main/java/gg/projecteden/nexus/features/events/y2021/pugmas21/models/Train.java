package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.EntityUtils.forcePacket;
import static gg.projecteden.nexus.utils.RandomUtils.randomDouble;

public class Train {
	private final Location location;
	private final BlockFace forwards;
	private final BlockFace backwards;
	@Builder.Default
	private double speed = .25;
	@Builder.Default
	private int seconds = 75;
	private final Vector smokeBack;
	private final Vector smokeUp;

	private final List<ArmorStand> armorStands = new ArrayList<>();
	private int taskId;

	private static final int TOTAL_MODELS = 18;
	private static final double SEPARATOR = 7.5;

	@Builder
	public Train(Location location, BlockFace direction, double speed, int seconds) {
		this.location = location.toCenterLocation();
		this.forwards = direction;
		this.backwards = direction.getOppositeFace();
		this.speed = speed;
		this.seconds = seconds;
		this.smokeBack = backwards.getDirection().multiply(4);
		this.smokeUp = BlockFace.UP.getDirection().multiply(5.3);
	}

	public static void schedule() {
		final TrainBuilder train = Train.builder()
			.location(Pugmas21.location(112.5, 54, 7.5, 90, 0))
			.direction(BlockFace.WEST)
			.seconds(60)
			.speed(.3);

		Tasks.repeat(TickTime.SECOND.x(30), TickTime.MINUTE.x(5), () -> {
			if (Pugmas21.getPlayers().size() == 0)
				return;

			train.build().start();
			Pugmas21.actionBar("&c&lA train is passing by", TickTime.SECOND.x(10));
		});
	}

	public void start() {
		spawnArmorStands();

		taskId = Tasks.repeat(1, 1, this::move);

		Tasks.wait(TickTime.SECOND.x(seconds), this::stop);
	}

	public void stop() {
		Tasks.cancel(taskId);
		armorStands.forEach(Entity::remove);
	}

	private void move() {
		for (ArmorStand armorStand : armorStands)
			move(armorStand);

		smoke();
		light();
	}

	private void smoke() {
		new Smoke(getSmokeLocation());
	}

	private void light() {
		final Location lightBlock = front().add(BlockFace.UP.getDirection());
		if (lightBlock.getBlock().getType() == Material.AIR) {
			lightBlock.getBlock().setType(Material.LIGHT);
			Light light = (Light) lightBlock.getBlock().getBlockData();
			light.setLevel(light.getMaximumLevel());
			lightBlock.getBlock().setBlockData(light);
		}

		final Location backOneBlock = lightBlock.add(backwards.getDirection());
		if (backOneBlock.getBlock().getType() == Material.LIGHT)
			backOneBlock.getBlock().setType(Material.AIR);
	}

	private void move(ArmorStand armorStand) {
		forcePacket(armorStand);
		armorStand.teleport(armorStand.getLocation().add(forwards.getDirection().multiply(speed)));
	}

	private Location getSmokeLocation() {
		return front().add(smokeBack).add(smokeUp);
	}

	private @NotNull Location front() {
		return armorStands.iterator().next().getLocation();
	}

	public void spawnArmorStands() {
		for (int i = 1; i <= TOTAL_MODELS; i++) {
			armorStands.add(armorStand(i, location));
			location.add(backwards.getDirection().multiply(SEPARATOR));
		}
	}

	public static ArmorStand armorStand(int model, Location location) {
		return ArmorStandEditorCommand.summon(location, armorStand -> {
			armorStand.setVisible(false);
			armorStand.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.MINECART).customModelData(model).build());
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
