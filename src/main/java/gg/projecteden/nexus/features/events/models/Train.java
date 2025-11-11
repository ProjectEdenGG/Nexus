package gg.projecteden.nexus.features.events.models;

import com.mojang.datafixers.util.Pair;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.features.events.models.Train.Crossing.TrackSide;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ChunkLoader;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldEditUtils.Paster;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Train {
	@Getter
	private boolean active;

	private final Location location;
	private final BlockFace forwards;
	private final BlockFace backwards;
	private final double speed;
	private final int seconds;
	private final boolean test;
	private final Vector smokeBack;
	private final Vector smokeUp;
	private final String regionTrack;
	private final String regionAnnounce;
	private final Location whistleLocation;
	private final double whistleRadius;
	private final String regionReveal;
	private final TrainCrossings trainCrossings;
	private final boolean bonkPlayers;
	private final Consumer<Player> onBonk;
	private final Map<Integer, ItemModelType> modelOverrides;
	private final String forceLoadRegion;
	private final String regionAnnounceMuteRegex;

	private final List<ArmorStand> armorStands = new ArrayList<>();
	private final List<Integer> taskIds = new ArrayList<>();
	private Location lightLocation;

	private final WorldGuardUtils worldguard;


	private static final int TOTAL_MODELS = 18;
	private static final double SEPARATOR = 7.5;

	@Getter
	private static final List<Train> instances = new ArrayList<>();

	public static boolean anyActiveInstances() {
		return instances.stream().anyMatch(Train::isActive);
	}

	@Builder
	public Train(Location location, BlockFace direction, double speed, int seconds, boolean test, String regionAnnounce,
				 Location whistleLocation, double whistleRadius, String regionTrack, String regionReveal, TrainCrossings trainCrossings,
				 boolean bonkPlayers, Consumer<Player> onBonk, Map<Integer, ItemModelType> modelOverrides, String forceLoadRegion, String regionAnnounceMuteRegex) {
		this.location = location.toCenterLocation();
		this.worldguard = new WorldGuardUtils(location);
		this.forwards = direction;
		this.backwards = direction.getOppositeFace();
		this.speed = speed;
		this.seconds = seconds;
		this.test = test;
		this.modelOverrides = modelOverrides;
		this.smokeBack = backwards.getDirection().multiply(4);
		this.smokeUp = BlockFace.UP.getDirection().multiply(5.3);
		this.regionAnnounce = regionAnnounce;
		this.whistleLocation = whistleLocation;
		this.whistleRadius = whistleRadius;
		this.regionTrack = regionTrack;
		this.regionReveal = regionReveal;
		this.trainCrossings = trainCrossings;
		this.bonkPlayers = bonkPlayers;
		this.onBonk = onBonk;
		this.forceLoadRegion = forceLoadRegion;
		this.regionAnnounceMuteRegex = regionAnnounceMuteRegex;

		if (this.trainCrossings != null) {
			this.trainCrossings.allLightsOff();
		}
	}

	private List<Player> getPlayers() {
		List<Player> players = new ArrayList<>(worldguard.getPlayersInRegion(regionAnnounce).stream().toList());

		if (regionAnnounceMuteRegex != null) {
			Set<Player> mutedPlayers = new HashSet<>();
			worldguard.getRegionsLike(regionAnnounceMuteRegex)
				.forEach(region -> mutedPlayers.addAll(worldguard.getPlayersInRegion(region)));

			players.removeAll(mutedPlayers);
		}

		return players;
	}

	public void debug(Player debugger) {
		for (int i = 1; i <= TOTAL_MODELS; i++) {
			ItemBuilder trainItem = getTrainItem(i);
			PlayerUtils.send(debugger, "Armorstand " + i + ":  Model=" + trainItem.model());
		}
	}

	public void stop() {
		taskIds.forEach(Tasks::cancel);
		armorStands.forEach(Entity::remove);

		active = false;
		instances.remove(this);
		ChunkLoader.forceLoad(location.getWorld(), forceLoadRegion, false);
	}

	public void start() {
		ChunkLoader.forceLoad(location.getWorld(), forceLoadRegion, true);
		active = true;
		instances.add(this);

		taskIds.add(Tasks.wait(TickTime.SECOND.x(3), () -> {
			SoundBuilder whistle = new SoundBuilder(CustomSound.TRAIN_WHISTLE).category(SoundCategory.AMBIENT);

			if (whistleLocation != null) {
				for (Player player : getPlayers()) {
					double radiusVolume = getRadiusVolume(player, whistleLocation, whistleRadius, false, 0.01, 0.8);
					whistle.clone().receiver(player).volume(radiusVolume).play();
				}
			} else
				whistle.receivers(getPlayers()).volume(0.25).play();
		}));

		spawnArmorStands();

		taskIds.add(Tasks.repeat(0, 1, this::move));

		if (trainCrossings != null) {
			taskIds.add(Tasks.repeat(0, TickTime.SECOND, () -> {

				if (trainCrossings.getCrossingsA().getFirst().isClosed()) {
					trainCrossings.switchLights(true, trainCrossings.getCrossingLightsA1());
					trainCrossings.switchLights(false, trainCrossings.getCrossingLightsA2());
					trainCrossings.crossingSound(trainCrossings.getCrossingsA().getFirst().getArmLocation(), 1);
					trainCrossings.crossingSound(trainCrossings.getCrossingsA().getSecond().getArmLocation(), 1);

					Tasks.wait(10, () -> {
						trainCrossings.switchLights(false, trainCrossings.getCrossingLightsA1());
						trainCrossings.switchLights(true, trainCrossings.getCrossingLightsA2());
						trainCrossings.crossingSound(trainCrossings.getCrossingsA().getFirst().getArmLocation(), 2);
						trainCrossings.crossingSound(trainCrossings.getCrossingsA().getSecond().getArmLocation(), 2);
					});
				} else {
					trainCrossings.switchLights(false, trainCrossings.getCrossingLightsA1());
					trainCrossings.switchLights(false, trainCrossings.getCrossingLightsA2());
				}

				if (trainCrossings.getCrossingsB().getFirst().isClosed()) {
					trainCrossings.switchLights(true, trainCrossings.getCrossingLightsB1());
					trainCrossings.switchLights(false, trainCrossings.getCrossingLightsB2());
					trainCrossings.crossingSound(trainCrossings.getCrossingsB().getFirst().getArmLocation(), 1);
					trainCrossings.crossingSound(trainCrossings.getCrossingsB().getSecond().getArmLocation(), 1);

					Tasks.wait(10, () -> {
						trainCrossings.switchLights(false, trainCrossings.getCrossingLightsB1());
						trainCrossings.switchLights(true, trainCrossings.getCrossingLightsB2());
						trainCrossings.crossingSound(trainCrossings.getCrossingsB().getFirst().getArmLocation(), 2);
						trainCrossings.crossingSound(trainCrossings.getCrossingsB().getSecond().getArmLocation(), 2);
					});
				} else {
					trainCrossings.switchLights(false, trainCrossings.getCrossingLightsB1());
					trainCrossings.switchLights(false, trainCrossings.getCrossingLightsB2());
				}
			}));

			taskIds.add(Tasks.repeat(0, TickTime.TICK.x(10), () -> {
				Set<Crossing> activateCrossings = new HashSet<>();
				for (ArmorStand armorStand : getValidArmorStands()) {
					for (ProtectedRegion region : worldguard.getRegionsAt(armorStand.getLocation())) {
						Set<Crossing> crossings = trainCrossings.getCrossingsAt(region.getId());
						if (crossings.isEmpty())
							continue;

						activateCrossings.addAll(crossings);
					}
				}

				// Open any crossings that aren't near the train
				List<Crossing> crossings = new ArrayList<>(trainCrossings.getCrossings());
				crossings.removeAll(activateCrossings);
				for (Crossing crossing : crossings) {
					if (!crossing.isClosed())
						continue;

					crossing.open();
				}

				for (Crossing crossing : activateCrossings) {
					if (crossing.isClosed())
						continue;

					crossing.close();
				}
			}));
		}

		if (bonkPlayers) {
			taskIds.add(Tasks.repeat(0, TickTime.TICK.x(5), () -> {
				for (ArmorStand armorStand : getValidArmorStands()) {
					if (Nullables.isNullOrAir(armorStand.getEquipment().getHelmet()))
						continue;

					for (Entity entity : EntityUtils.getNearbyEntities(armorStand.getLocation(), 4).keySet()) {
						if (!(entity instanceof Player player))
							continue;

						if (!worldguard.isInRegion(player, regionTrack))
							continue;

						player.setVelocity(EntityUtils.getForcefieldVelocity(player, armorStand.getLocation()));
						new SoundBuilder(CustomSound.BONK)
								.location(player.getLocation())
								.receiver(player)
								.play();

						onBonk.accept(player);
					}
				}
			}));
		}

		taskIds.add(Tasks.repeat(0, TickTime.SECOND, () ->
				getPlayers().forEach(player -> {
					final ArmorStand nearest = getNearestArmorStand(player);
					if (nearest != null) {
						double radiusVolume = getRadiusVolume(player, nearest.getLocation(), 60, true, 0, 1);
						new SoundBuilder(CustomSound.TRAIN_CHUG)
							.receiver(player)
							.location(nearest.getLocation())
							.category(SoundCategory.AMBIENT)
							.volume(radiusVolume)
							.play();
					}
				})));

		taskIds.add(Tasks.wait(TickTime.SECOND.x(seconds), this::stop));
	}

	private double getRadiusVolume(Player player, Location location, double radius, boolean checkRadius, double minVolume, double maxVolume) {
		double distance = Distance.distance(player.getLocation(), location).getRealDistance();
		if (checkRadius && distance > radius)
			return minVolume;

		double fallOff = (1.0 - (distance / radius));
		double volumeSquared = maxVolume * Math.pow(fallOff, 2.0);
		return MathUtils.clamp(volumeSquared, minVolume, maxVolume);
	}

	@Nullable
	private ArmorStand getNearestArmorStand(Player player) {
		if (getValidArmorStands().isEmpty())
			return null;

		return Collections.min(getValidArmorStands(), Comparator.comparing(armorStand -> Distance.distance(player, armorStand).get()));
	}

	private List<ArmorStand> getValidArmorStands() {
		return armorStands.stream().filter(ArmorStand::isValid).toList();
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

		EntityUtils.forcePacket(armorStand);
		final Location to = armorStand.getLocation().clone().add(forwards.getDirection().multiply(speed));

		reveal(armorStand, to);

		if (!isValidLocation(to))
			armorStand.remove();
		else
			armorStand.teleport(to);
	}

	private void reveal(ArmorStand armorStand, Location location) {
		if (regionReveal == null)
			return;

		if (!worldguard.isInRegion(location, regionReveal))
			return;

		int ndx = 1;
		for (ArmorStand _armorStand : armorStands) {
			if (_armorStand.getUniqueId().toString().equalsIgnoreCase(armorStand.getUniqueId().toString())) {
				setTrainItem(armorStand, ndx);
				return;
			}
			ndx++;
		}
	}

	private boolean isValidLocation(Location to) {
		return test || !worldguard.getRegionsLikeAt(regionTrack, to).isEmpty();
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

	private void spawnArmorStands() {
		for (int i = 1; i <= TOTAL_MODELS; i++) {
			armorStands.add(armorStand(i, location));
			location.add(backwards.getDirection().multiply(SEPARATOR));
		}
	}

	private ArmorStand armorStand(int model, Location location) {
		return ArmorStandEditorCommand.summon(location, armorStand -> {
			armorStand.setVisible(false);

			if (regionReveal == null)
				setTrainItem(armorStand, model);
		});
	}

	private void setTrainItem(ArmorStand armorStand, int modelNdx) {
		ItemBuilder trainItem = getTrainItem(modelNdx);
		armorStand.setItem(EquipmentSlot.HEAD, trainItem.build());
	}

	private ItemBuilder getTrainItem(int modelNdx) {
		ItemModelType baseItemModelType = ItemModelType.TRAIN_1;
		String baseEnum = baseItemModelType.name().replace("1", "");
		ItemModelType ndxItemModelType = ItemModelType.valueOf(baseEnum + modelNdx);

		if (modelOverrides.containsKey(modelNdx)) {
			ndxItemModelType = modelOverrides.get(modelNdx);
		}

		return new ItemBuilder(ItemModelType.TRAIN_1).model(ndxItemModelType);
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
			final Supplier<Double> random = () -> RandomUtils.randomDouble(-.25, .25);
			final Supplier<Location> offset = () -> location.clone().add(random.get(), random.get(), random.get());

			world.spawnParticle(particle, offset.get(), count, x, y, z, speed);
			world.spawnParticle(particle, offset.get(), count, x, y, z, speed);
		}
	}

	@Getter
	public static class TrainCrossings {
		World world;
		Pair<Crossing, Crossing> crossingsA;
		List<Location> crossingLightsA1 = new ArrayList<>();
		List<Location> crossingLightsA2 = new ArrayList<>();

		Pair<Crossing, Crossing> crossingsB;

		List<Location> crossingLightsB1 = new ArrayList<>();
		List<Location> crossingLightsB2 = new ArrayList<>();

		public TrainCrossings(Crossing crossingA1, Crossing crossingA2, Crossing crossingB1, Crossing crossingB2) {
			this.world = crossingA1.getArmLocation().getWorld();

			crossingsA = Pair.of(crossingA1, crossingA2);

			crossingLightsA1 = List.of(lightLocation1(crossingA1), lightLocation1(crossingA2));
			crossingLightsA2 = List.of(lightLocation2(crossingA1), lightLocation2(crossingA2));

			crossingsB = Pair.of(crossingB1, crossingB2);

			crossingLightsB1 = List.of(lightLocation1(crossingB1), lightLocation1(crossingB2));
			crossingLightsB2 = List.of(lightLocation2(crossingB1), lightLocation2(crossingB2));
		}

		private Location lightLocation1(Crossing crossing) {
			int lightY = crossing.getBlockY() + 5;

			if (crossing.getTrackSide() == TrackSide.NORTH_SIDE) {
				return location(crossing.getBlockX(), lightY, crossing.getBlockZ() + 1);
			} else {
				return location(crossing.getBlockX(), lightY, crossing.getBlockZ() - 1);
			}
		}

		private Location lightLocation2(Crossing crossing) {
			int lightY = crossing.getBlockY() + 5;

			if (crossing.getTrackSide() == TrackSide.NORTH_SIDE) {
				return location(crossing.getBlockX() - 2, lightY, crossing.getBlockZ() + 1);
			} else {
				return location(crossing.getBlockX() + 2, lightY, crossing.getBlockZ() - 1);
			}
		}

		private Location location(double x, double y, double z) {
			return new Location(world, x, y, z);
		}

		public void switchLights(boolean powered, List<Location> lights) {
			Tasks.sync(() -> {
				for (Location light : lights) {
					if (light.getBlock().getBlockData() instanceof Lightable lightable) {
						lightable.setLit(powered);
						light.getBlock().setBlockData(lightable);
					}
				}
			});
		}

		public void allLightsOff() {
			Tasks.sync(() -> {
				switchLights(false, crossingLightsA1);
				switchLights(false, crossingLightsA2);
				switchLights(false, crossingLightsB1);
				switchLights(false, crossingLightsB2);
			});
		}

		public void crossingSound(Location location, double pitch) {
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL)
				.location(location)
				.volume(0.5)
				.pitch(pitch)
				.play();
		}

		private List<Crossing> getCrossings() {
			return List.of(crossingsA.getFirst(), crossingsA.getSecond(), crossingsB.getFirst(), crossingsB.getSecond());

		}

		public Set<Crossing> getCrossingsAt(String regionId) {
			Set<Crossing> result = new HashSet<>();

			for (Crossing crossing : getCrossings()) {
				if (crossing.getActivateRegion().equalsIgnoreCase(regionId))
					result.add(crossing);
			}

			return result;
		}

		public void openCrossings() {
			for (Crossing crossing : getCrossings()) {
				crossing.open();
			}
		}

		public void closeCrossings() {
			for (Crossing crossing : getCrossings()) {
				crossing.close();
			}
		}
	}

	@Getter
	public static class Crossing {
		@Setter
		private boolean closed = false;
		private final Location armLocation;
		private final String activateRegion;
		private final TrackSide trackSide;
		private final String animationPath = "Animations/Train/Crossing/";
		private final WorldEditUtils worldedit;

		public Crossing(Location armLocation, String activateRegion, TrackSide trackSide) {
			this.armLocation = armLocation;
			this.activateRegion = activateRegion;
			this.trackSide = trackSide;
			this.worldedit = new WorldEditUtils(armLocation);
		}

		public int getBlockX() {
			return armLocation.getBlockX();
		}

		public int getBlockY() {
			return armLocation.getBlockY();
		}

		public int getBlockZ() {
			return armLocation.getBlockZ();
		}

		public enum TrackSide {
			NORTH_SIDE,
			SOUTH_SIDE;
		}

		public void close() {
			if (closed)
				return;

			closed = true;

			Queue<Paster> pasters = new LinkedList<>();

			String direction = trackSide == TrackSide.NORTH_SIDE ? "North" : "South";
			for (int i = 1; i <= 7; i++) {
				pasters.add(worldedit.paster().file(animationPath + direction + "_Closing_" + i).at(armLocation));
			}

			animateCrossings(pasters);
		}

		public void open() {
			if (!closed)
				return;

			closed = false;

			Queue<Paster> pasters = new LinkedList<>();

			String direction = trackSide == TrackSide.NORTH_SIDE ? "North" : "South";
			for (int i = 1; i <= 7; i++) {
				pasters.add(worldedit.paster().file(animationPath + direction + "_Opening_" + i).at(armLocation));
			}

			animateCrossings(pasters);
		}

		private static void animateCrossings(Queue<Paster> pasters) {
			if (!animateCrossing(pasters)) return;
			if (!animateCrossing(pasters)) return;

			Tasks.waitAsync(4, () -> animateCrossings(pasters));
		}

		private static boolean animateCrossing(Queue<Paster> pasters) {
			Paster paster = pasters.poll();
			if (paster == null) {
				return false;
			}

			paster.build();
			return true;
		}
	}
}
