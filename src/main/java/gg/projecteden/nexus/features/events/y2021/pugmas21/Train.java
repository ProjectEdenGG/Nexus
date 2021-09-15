package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Train {
	private final Location location;
	private final BlockFace forwards;
	private final BlockFace backwards;
	private final double speed;
	private final int seconds;
	private final Vector smokeBack;
	private final Vector smokeUp;

	private final List<ArmorStand> armorStands = new ArrayList<>();
	private int taskId;

	private static final int TOTAL_MODELS = 18;
	private static final double SEPARATOR = 7.5;

	@Builder
	public Train(Location location, BlockFace direction, double speed, int seconds, double smokeBack, double smokeUp) {
		this.location = LocationUtils.getCenteredLocation(location);
		this.forwards = direction;
		this.backwards = direction.getOppositeFace();
		this.speed = speed;
		this.seconds = seconds;
		this.smokeBack = backwards.getDirection().multiply(smokeBack);
		this.smokeUp = BlockFace.UP.getDirection().multiply(smokeUp);
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

		new Smoke(getSmokeLocation());
	}

	private void move(ArmorStand armorStand) {
		forcePacket(armorStand);
		armorStand.teleport(armorStand.getLocation().add(forwards.getDirection().multiply(speed)));
	}

	private void forcePacket(ArmorStand armorStand) {
		((CraftArmorStand) armorStand).getHandle().af = true; // hasImpulse
	}

	private Location getSmokeLocation() {
		return armorStands.iterator().next().getLocation().add(smokeBack).add(smokeUp);
	}

	public void spawnArmorStands() {
		for (int i = 1; i <= TOTAL_MODELS; i++) {
			armorStands.add(armorStand(i, location));
			location.add(backwards.getDirection().multiply(SEPARATOR));
		}
	}

	public static ArmorStand armorStand(int model, Location location) {
		return ArmorStandEditorCommand.summon(location, armorStand ->
			armorStand.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.MINECART).customModelData(model).build()));
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
			world.spawnParticle(particle, location, count, x, y, z, speed);
			world.spawnParticle(particle, location, count, x, y, z, speed);
		}
	}

}
