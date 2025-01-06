package gg.projecteden.nexus.features.events.y2025.pugmas25;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationAxis;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25Minigolf;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Geyser;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Environments(Env.PROD)
public class Pugmas25Effects extends Effects {

	private final List<Location> SMOKE_STACKS = List.of(
		location(-459, 92, -2858),
		location(-459, 93, -2860)
	);

	private static int angle = 0;
	private final List<String> windmill1 = new ArrayList<>() {{
		add("1d7d22db-f6ad-4f64-b844-7e6e017efeaa");
		add("47c88b92-4708-4b1c-b654-b09ff8a95cef");
		add("c00b3308-4989-4e36-91f1-1dfe8304a849");
		add("99a0f5b9-420e-483f-acc2-c81e48663c3c");
	}};

	@Override
	public World getWorld() {
		return Pugmas25.get().getWorld();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void particles() {
		Tasks.repeat(0, 10, Pugmas25Geyser::animateSmoke);
		smokeStacks();
		waterfall();
		Tasks.repeat(0, 10, () -> checkGarageDoor());
	}

	@Override
	public void animations() {
		geyser();
		slotMachine();
		minigolfWindmill();
	}

	@Override
	public List<RotatingStand> getRotatingStands() {
		List<RotatingStand> result = new ArrayList<>();
		for (String uuid : windmill1) {
			result.add(new RotatingStand(uuid, StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, true));
		}

		return result;
	}


	@Override
	public void onLoadRotatingStands(List<RotatingStand> rotatingStands) {
		angle = 0;
	}

	@Override
	public boolean customResetPose(RotatingStand rotatingStand, @NotNull ArmorStand armorStand) {
		if (!windmill1.contains(rotatingStand.getUuid()))
			return true;

		rotatingStand.resetRightArmPose();
		rotatingStand.addRightArmPose(0, Math.toRadians(angle), 0);
		armorStand.getEquipment().setItemInMainHand(new ItemBuilder(Material.PAPER).modelId(6247).build());
		angle += 90;
		return true;
	}

	//

	private void waterfall() {
		Location fallingWaterLoc1 = location(-485.05, 75.5, -2901.5);
		Location fallingWaterLoc2 = location(-485.05, 75.5, -2900.5);
		Location splashLoc1 = location(-485.05, 73, -2901.5);
		Location splashLoc2 = location(-485.05, 73, -2900.5);

		Tasks.repeat(0, 2, () -> {
			waterfallEffect(Particle.FALLING_WATER, fallingWaterLoc1);
			waterfallEffect(Particle.FALLING_WATER, fallingWaterLoc2);
			waterfallEffect(Particle.SPLASH, splashLoc1);
			waterfallEffect(Particle.SPLASH, splashLoc2);
		});
	}

	private void waterfallEffect(Particle particle, Location location) {
		if (location == null || !location.isChunkLoaded())
			return;

		if (!hasPlayersNearby(location, 50))
			return;

		new ParticleBuilder(particle).location(location)
			.offset(0, 0.2, 0.2)
			.extra(0.1)
			.count(1)
			.spawn();
	}

	private void smokeStacks() {
		Tasks.repeat(0, 2, () -> {
			if (this.SMOKE_STACKS == null)
				return;

			for (Location location : SMOKE_STACKS) {
				if (location == null || !location.isChunkLoaded())
					continue;

				if (RandomUtils.chanceOf(25))
					continue;

				if (!hasPlayersNearby(location, 50))
					continue;

				new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
					.location(location.toCenterLocation())
					.offset(0, 4, 0)
					.extra(0.01)
					.count(0)
					.spawn();
			}
		});
	}

	private void geyser() {
		AtomicInteger tries = new AtomicInteger(0);
		Tasks.repeat(0, TickTime.MINUTE.x(2), () -> {
			if (!shouldAnimate(Pugmas25Geyser.GEYSER_ORIGIN))
				return;

			if (RandomUtils.chanceOf(75) && tries.get() < 5) {
				tries.getAndIncrement();
				return;
			}

			tries.set(0);
			Pugmas25Geyser.animate();
		});
	}

	private void minigolfWindmill() {
		Tasks.repeat(TickTime.SECOND.x(2), TickTime.TICK.x(38), () -> {
			if (!shouldAnimate(Pugmas25Minigolf.minigolfAnimationLoc))
				return;


			Pugmas25Minigolf.minigolfAnimationLoc.getBlock().setType(Material.REDSTONE_BLOCK);
			Tasks.wait(2, () -> Pugmas25Minigolf.minigolfAnimationLoc.getBlock().setType(Material.AIR));
		});

	}

	private void slotMachine() {
		Tasks.repeat(0, TickTime.SECOND, () -> {
			Location location = Pugmas25SlotMachine.get().getBaseLocation();
			if (location == null || !location.isChunkLoaded())
				return;

			if (!shouldAnimate(location, 25))
				return;

			Pugmas25SlotMachine.get().nextLight();
		});
	}

	private final Location garageDoorLoc = location(-479, 75, -2871);
	private final Location openGarageDoor = location(-465, 57, -2871);
	private final Location closeGarageDoor = location(-484, 57, -2873);
	boolean garageDoorOpen = false;

	private void checkGarageDoor() {
		if (Pugmas25.get().getOnlinePlayers().radius(garageDoorLoc, 7).get().isEmpty()) {
			if (garageDoorOpen)
				toggleGarageDoor(closeGarageDoor, false);
			return;
		}

		if (!garageDoorOpen)
			toggleGarageDoor(openGarageDoor, true);
	}

	private void toggleGarageDoor(Location location, boolean open) {
		garageDoorOpen = open;
		location.getBlock().setType(Material.REDSTONE_BLOCK);
		Tasks.wait(2, () -> location.getBlock().setType(Material.AIR));
	}



}
