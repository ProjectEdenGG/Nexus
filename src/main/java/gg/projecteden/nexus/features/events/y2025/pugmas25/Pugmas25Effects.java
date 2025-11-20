package gg.projecteden.nexus.features.events.y2025.pugmas25;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationAxis;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25Minigolf;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Geyser;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Environments({Env.PROD, Env.UPDATE})
public class Pugmas25Effects extends Effects {

	private static int angle = 0;
	private final List<String> windmill_1 = new ArrayList<>() {{
		add("1d7d22db-f6ad-4f64-b844-7e6e017efeaa");
		add("47c88b92-4708-4b1c-b654-b09ff8a95cef");
		add("c00b3308-4989-4e36-91f1-1dfe8304a849");
		add("99a0f5b9-420e-483f-acc2-c81e48663c3c");
	}};
	private final String watermill_1 = "d7eeb0a5-ede7-4574-ae78-fca4c2076003";

	@Override
	public World getWorld() {
		return Pugmas25.get().getWorld();
	}

	@Override
	public void onStart() {
		super.onStart();
		extractinator();
	}

	@Override
	public void sounds() {
		// Watermill
		Location watermill = location(-461, 77, -2867);
		SoundBuilder watermillSound = new SoundBuilder(CustomSound.AMBIENT_WATERMILL).category(SoundCategory.AMBIENT).location(watermill).volume(1.25);
		Tasks.repeat(0, TickTime.TICK.x(46), watermillSound::play);

		SoundBuilder waterSound = new SoundBuilder(Sound.BLOCK_WATER_AMBIENT).category(SoundCategory.AMBIENT).location(watermill).volume(1.5);
		Tasks.repeat(0, TickTime.TICK.x(32), waterSound::play);
	}

	@Override
	public void particles() {
		Tasks.repeat(0, 10, Pugmas25Geyser::animateSmoke);
		smokeStacks();
		waterfall();
		Tasks.repeat(0, 10, this::checkGarageDoor);
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
		for (String uuid : windmill_1) {
			result.add(new RotatingStand(uuid, StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, true));
		}

		result.add(new RotatingStand(watermill_1, StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, true));
		result.add(new RotatingStand(extractinatorCog, StandRotationAxis.VERTICAL, StandRotationType.POSITIVE, false));

		return result;
	}


	@Override
	public void onLoadRotatingStands(List<RotatingStand> rotatingStands) {
		angle = 0;
	}

	@Override
	public boolean customResetPose(RotatingStand rotatingStand, @NotNull ArmorStand armorStand) {
		if (!windmill_1.contains(rotatingStand.getUuid().toString()))
			return true;

		rotatingStand.resetRightArmPose();
		rotatingStand.addRightArmPose(0, Math.toRadians(angle), 0);
		armorStand.getEquipment().setItemInMainHand(new ItemBuilder(Material.PAPER).model(ItemModelType.STRUCTURE_WIND_MILL_WIND_MILL_ARM).build());
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
		Set<ProtectedRegion> smokeRegions = worldguard().getRegionsLike("^pugmas25_chimney_[0-9]+");
		List<Location> chimneyLocations = new ArrayList<>();
		for (ProtectedRegion region : smokeRegions) {
			chimneyLocations.add(worldguard().toLocation(region.getMinimumPoint()).toCenterLocation());
		}

		chimneyLocations.add(extractinatorChimneyLoc);

		if (Nullables.isNullOrEmpty(chimneyLocations))
			return;

		Tasks.repeat(0, 2, () -> {
			if (Nullables.isNullOrEmpty(chimneyLocations))
				return;

			for (Location location : chimneyLocations) {
				if (location == null || !location.isChunkLoaded())
					continue;

				if (RandomUtils.chanceOf(25))
					continue;

				if (!hasPlayersNearby(location, 50))
					continue;

				new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
					.location(location)
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

	private final String extractinatorCog = "e599805e-8f2a-438e-b68d-42416bedc501";
	private final String extractinatorPump = "bf03c2fc-bf16-4ea5-83f7-51f522ce4b88";
	private final Location extractinatorPumpLoc = location(-744.7, 104.4, -3136.3);
	private final Location extractinatorChimneyLoc = location(-745.75, 106.5, -3136.5);
	private final Location extractinatorFireLoc = location(-744.68, 105.5, -3136.95);

	private void extractinator() {
		double amplitude = 0.35;
		double speed = 0.05;
		final boolean[] upwards = {true};
		final double[] phase = {0};
		final ArmorStand[] stand = {null};
		Tasks.repeat(0, 2, () -> {
			if (stand[0] == null)
				stand[0] = (ArmorStand) Bukkit.getEntity(UUID.fromString(extractinatorPump));

			if (stand[0] == null)
				return;

			if (stand[0].isDead() || !stand[0].isValid())
				return;

			// Update phase
			if (upwards[0]) {
				phase[0] += speed;
				if (phase[0] >= 1.0) upwards[0] = false;
			} else {
				phase[0] -= speed;
				if (phase[0] <= 0.0) upwards[0] = true;
			}

			// Calculate EXACT Y from base + (phase * amplitude)
			Location newLoc = extractinatorPumpLoc.clone().add(0, phase[0] * amplitude, 0);

			stand[0].teleport(newLoc);
		});

		SoundBuilder crackle = new SoundBuilder(Sound.BLOCK_FURNACE_FIRE_CRACKLE)
			.category(SoundCategory.BLOCKS)
			.location(extractinatorFireLoc);
		ParticleBuilder flames = new ParticleBuilder(Particle.FLAME).count(2).extra(0)
			.location(extractinatorFireLoc)
			.offset(0.1, 0.05, 0.1);

		Tasks.repeat(0, TickTime.SECOND, () -> {
			if (RandomUtils.chanceOf(50)) {
				crackle.play();
				flames.spawn();
			}
		});
	}



}
