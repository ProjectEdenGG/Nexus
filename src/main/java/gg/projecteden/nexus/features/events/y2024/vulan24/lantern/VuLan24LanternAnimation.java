package gg.projecteden.nexus.features.events.y2024.vulan24.lantern;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.vulan24.VuLan24ConfigService;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Builder(buildMethodName = "_build")
public class VuLan24LanternAnimation {

	public static final int DEFAULT_STARTING_LANTERNS = 10;

	// Lower values will make it move faster
	private static final int MOVE_SPEED = 2;

	private static final List<ItemModelType> LANTERN_MATERIALS = List.of(
		ItemModelType.VULAN_WATER_LANTERN_SMALL,
		ItemModelType.VULAN_WATER_LANTERN_SMALL_NO_BASE,
		ItemModelType.VULAN_WATER_LANTERN_LARGE,
		ItemModelType.VULAN_WATER_LANTERN_LARGE_NO_BASE
	);

	// How far to move the armor stand down from the surface
	private static final double ARMOR_STAND_OFFSET = 2.15;
	// How long should lanterns spawn in for
	private static final int SPAWN_OVER_TICKS = 200;
	// How close should a lantern get to another before it slows down to make room
	private static final float RANGE_CHECK = 1.75f;
	// Default light block level
	private static final int LIGHT_LEVEL = 5;

	private static final List<ProtectedRegion> START_REGIONS;
	private static final List<ProtectedRegion> CHECKPOINT_REGIONS;

	@Getter
	private static VuLan24LanternAnimation instance;

	static  {
		START_REGIONS = new ArrayList<>(VuLan24.get().worldguard().getRegionsLike("^vulan_lanternanimation_start(_[\\d]+)?$")).stream().sorted().toList();
		CHECKPOINT_REGIONS = new ArrayList<>(VuLan24.get().worldguard().getRegionsLike("^vulan_lanternanimation_checkpoint(_[\\d]+)?$")).stream()
			.sorted(Comparator.comparingInt(region -> {
				String[] parts = region.getId().split("_");
				return Integer.parseInt(parts[parts.length - 1]);
			})).toList();
	}

	@Builder.Default
	private int startingLanterns = DEFAULT_STARTING_LANTERNS;
	@Builder.Default
	private int moveSpeed = MOVE_SPEED;
	@Builder.Default
	private int lightLevel = LIGHT_LEVEL;

	@Getter
	private List<Location>[] paths;
	private List<VuLan24Lantern> lanterns;

	public static class VuLan24LanternAnimationBuilder {

		public VuLan24LanternAnimation build() {
			return _build().build();
		}

		public VuLan24LanternAnimation start() {
			return build().start();
		}

	}

	public VuLan24LanternAnimation build() {
		if (instance != null) {
			instance.cleanup();
		}
		instance = this;

		int amount = startingLanterns + getAdditionalPlayerLanterns();
		this.paths = generatePaths(10 + amount);

		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < amount; i++)
			ids.add(i);
		Collections.shuffle(ids);

		lanterns = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			int id = ids.remove(0);
			List<Location> path = new ArrayList<>();
			for (List<Location> locs : VuLan24LanternAnimation.getInstance().getPaths())
				path.add(locs.get(Math.min(id, locs.size() - 1)));
			lanterns.add(new VuLan24Lantern(path, moveSpeed, lightLevel));
		}

		return this;
	}

	public VuLan24LanternAnimation start() {
		if (lanterns != null && !lanterns.isEmpty()) {
			List<CompletableFuture<Void>> futures = new ArrayList<>();
			for (VuLan24Lantern lantern : lanterns) {
				futures.add(lantern.start());
			}
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).thenRun(this::cleanup);
		}

		return this;
	}

	private int getAdditionalPlayerLanterns() {
		return new VuLan24ConfigService().get0().getLanterns();
	}

	private List<Location>[] generatePaths(int amount) {
		List<Location>[] locations = new List[CHECKPOINT_REGIONS.size() + 1];
		locations[0] = getRandomBlocksInRegions(amount, START_REGIONS.toArray(new ProtectedRegion[0]));
		for (int i = 0; i < CHECKPOINT_REGIONS.size(); i++) {
			locations[i + 1] = getRandomBlocksInRegions(amount, CHECKPOINT_REGIONS.get(i));
		}
		return locations;
	}

	private List<Location> getRandomBlocksInRegions(int amount, ProtectedRegion... regions) {
		List<Location> locations = new ArrayList<>();
		for (ProtectedRegion region : regions) {
			List<Location> regionLocs = new ArrayList<>();
			int i = 0;
			while (regionLocs.size() < amount && i++ < 100) {
				regionLocs.add(VuLan24.get().worldguard().getRandomBlock(region).getLocation().toCenterLocation());
			}
			locations.addAll(regionLocs);
		}
		Collections.shuffle(locations);
		return locations.stream().limit(amount).sorted(Comparator.comparingInt(Location::getBlockX)).toList();
	}

	public void cleanup() {
		if (lanterns != null && !lanterns.isEmpty())
			lanterns.forEach(VuLan24Lantern::stop);
		instance = null;
	}

	@RequiredArgsConstructor
	public static class VuLan24Lantern {

		@NonNull
		private List<Location> path;
		private int moveSpeed;
		private int lightLevel;

		private int currentPathIndex = 0;

		private List<Location> currentSplinePath;
		private int currentSplinePathSize;

		private int taskID;
		private ArmorStand stand;

		private CompletableFuture<Void> animationCompletable;

		private CompletableFuture<Void> start() {
			Tasks.wait(RandomUtils.randomInt(0, SPAWN_OVER_TICKS), () -> {
				onStart().thenRun(() -> {
					if (instance == null)
						return;

					AtomicInteger iteration = new AtomicInteger();
					taskID = Tasks.repeat(0, 1, () -> tick(iteration.getAndIncrement()));
				});
			});
			animationCompletable = new CompletableFuture<>();
			return animationCompletable;
		}

		private void stop() {
			Tasks.cancel(taskID);
			onStop();
			animationCompletable.complete(null);
		}

		private CompletableFuture<Void> onStart() {
			CompletableFuture<Void> cf = new CompletableFuture<>();
			newSpline().thenRun(() -> {
				Tasks.sync(() -> {
					if (instance == null)
						cf.complete(null);

					stand = currentSplinePath.get(0).getWorld().spawn(currentSplinePath.get(0).clone().subtract(0, ARMOR_STAND_OFFSET, 0), ArmorStand.class, as -> {
						as.setGravity(false);
						as.setVisible(false);
						as.getEquipment().setHelmet(RandomUtils.randomElement(LANTERN_MATERIALS).getItem());
					});

					cf.complete(null);
				});
			});

			return cf;
		}

		private void onStop() {
			if (stand != null)
				Tasks.sync(() -> {
					for (int x = -1; x <= 1; x++) {
						for (int z = -1; z <= 1; z++) {
							Block block = stand.getLocation().clone().add(x, ARMOR_STAND_OFFSET, z).getBlock();
							if (block.getType() == Material.LIGHT)
								block.setType(Material.AIR);
						}
					}
					stand.remove();
				});
		}

		private void tick(int iteration) {
			newSpline().thenRun(() -> {
				Tasks.sync(() -> {
					if (!stand.getLocation().getChunk().isLoaded())
						stand.getLocation().getChunk().load();

					if (currentSplinePath.isEmpty()) {
						stop();
						return;
					}

					var nearby = stand.getLocation().getNearbyEntitiesByType(ArmorStand.class, RANGE_CHECK);
					if (!nearby.isEmpty()) {
						ArmorStand max = nearby.stream().max(Comparator.comparingDouble(as -> as.getLocation().getZ())).get();
						if (max.getEntityId() != stand.getEntityId()) {
							if (iteration % (moveSpeed * 2) != 0)
								return;
						}
					}

					if (iteration % moveSpeed != 0)
						return;

					Location loc = currentSplinePath.remove(0);
					stand.teleport(loc.clone().subtract(0, ARMOR_STAND_OFFSET, 0));

					for (int x = -1; x <= 1; x++) {
						for (int z = -1; z <= 1; z++) {
							Block block = stand.getLocation().clone().add(x, ARMOR_STAND_OFFSET, z).getBlock();
							if (block.getType() == Material.LIGHT)
								block.setType(Material.AIR);
						}
					}

					Light light = (Light) Material.LIGHT.createBlockData();
					light.setLevel(lightLevel);
					stand.getLocation().clone().add(0, ARMOR_STAND_OFFSET, 0).getBlock().setBlockData(light);
				});
			});
		}

		private CompletableFuture<Void> newSpline() {
			CompletableFuture<Void> cf = new CompletableFuture<>();
			if (currentSplinePath == null || currentSplinePath.isEmpty() || (currentSplinePath.size() < (currentSplinePathSize / 2)) && currentPathIndex < path.size()) {
				Tasks.async(() -> {
					var points = getNextPath().stream().map(Location::toVector).toList().toArray(new Vector[0]);
					if (points.length < 2) {
						cf.complete(null);
						return;
					}

					currentSplinePath = new ArrayList<>(new BezierSplinePath(.1f,  points)
						.getPath().stream().map(vector -> vector.toLocation(VuLan24.get().getWorld())).toList());
					currentSplinePathSize = currentSplinePath.size();
					cf.complete(null);
				});
			}
			else
				cf.complete(null);
			return cf;
		}

		private List<Location> getNextPath() {
			List<Location> locations = new ArrayList<>();
			if (currentPathIndex >= path.size())
				return locations;

			if (currentPathIndex > 0) {
				locations.add(stand.getLocation().clone().add(0, ARMOR_STAND_OFFSET, 0));
				currentPathIndex--;
			}

			int count = 3 - locations.size();
			for (int i = 0; i < count && currentPathIndex < path.size(); i++) {
				locations.add(path.get(currentPathIndex));
				currentPathIndex++;
			}

			return locations;
		}
	}

	public static class BezierSplinePath {

		Vector a, b, c;
		double distance;

		public BezierSplinePath(double distance, Vector... points) {
			this.distance = distance;
			this.a = points[0];
			this.b = points[1];
			this.c = points[2];
		}

		public List<Vector> getPath() {
			List<Vector> path = new ArrayList<>();
			int initialNumSegments = 1000;
			double totalLength = approximateCurveLength(a, b, c, initialNumSegments);

			double numPoints = totalLength / distance;

			for (int i = 0; i <= numPoints; i++) {
				double t = i / numPoints;
				Vector point = calculateBezierPoint(t, a, b, c);
				path.add(point);
			}
			return path;
		}

		private Vector calculateBezierPoint(double t, Vector a, Vector b, Vector c) {
			double x = (1 - t) * (1 - t) * a.getX() + 2 * (1 - t) * t * b.getX() + t * t * c.getX();
			double z = (1 - t) * (1 - t) * a.getZ() + 2 * (1 - t) * t * b.getZ() + t * t * c.getZ();
			return new Vector(x, a.getY(), z);
		}

		private double approximateCurveLength(Vector a, Vector b, Vector c, int numSegments) {
			double length = 0.0;
			Vector prevPoint = a;
			for (int i = 1; i <= numSegments; i++) {
				float t = (float) i / numSegments;
				Vector point = calculateBezierPoint(t, a, b, c);
				length += prevPoint.distance(point);
				prevPoint = point;
			}
			return length;
		}

	}

}
