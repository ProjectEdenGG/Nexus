package gg.projecteden.nexus.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import gg.projecteden.api.common.utils.RegexUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.CitizensUtils.NPCFinder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.parchment.HasLocation;
import lombok.Data;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public final class WorldGuardUtils {
	@NotNull
	private final org.bukkit.World world;
	@NotNull
	private final BukkitWorld bukkitWorld;
	@NotNull
	private final World worldEditWorld;
	@NotNull
	private final RegionManager manager;
	@NotNull
	private final static Map<org.bukkit.World, LoadingCache<BlockVector3, Set<ProtectedRegion>>> REGIONS_AT_CACHE = new HashMap<>();

	public static final @Nullable WorldGuardPlugin plugin = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

	public WorldGuardUtils(@NotNull Entity entity) {
		this(entity.getWorld());
	}

	public WorldGuardUtils(@NotNull Location location) {
		this(location.getWorld());
	}

	public WorldGuardUtils(@NotNull Block block) {
		this(block.getWorld());
	}

	public WorldGuardUtils(@NotNull String world) {
		this(Objects.requireNonNull(Bukkit.getWorld(world), "No world could be found by the name " + world));
	}

	public WorldGuardUtils(@NotNull org.bukkit.World world) {
		this.world = world;
		this.bukkitWorld = new BukkitWorld(world);
		this.worldEditWorld = bukkitWorld;
		this.manager = getManager(bukkitWorld);

		REGIONS_AT_CACHE.putIfAbsent(world, CacheBuilder.newBuilder()
			.expireAfterWrite(10, TimeUnit.SECONDS)
			.build(CacheLoader.from(location -> getManager(bukkitWorld).getApplicableRegions(location).getRegions())));
	}

	@NotNull
	private static RegionManager getManager(BukkitWorld bukkitWorld) {
		return Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(bukkitWorld), "Could not load RegionManager for world " + bukkitWorld.getName());
	}

	public @NotNull RegionContainer getContainer() {
		return WorldGuard.getInstance().getPlatform().getRegionContainer();
	}

	public @NotNull ProtectedRegion getProtectedRegion(@NotNull String name) throws InvalidInputException {
		ProtectedRegion region = manager.getRegion(name.toLowerCase());
		if (region == null)
			throw new InvalidInputException("Region &e" + name + " &cnot found");
		return region;
	}

	public static @Nullable org.bukkit.World getWorld(@NotNull ProtectedRegion protectedRegion) {
		for (org.bukkit.World world : Bukkit.getWorlds()) {
			try {
				if (protectedRegion.equals(new WorldGuardUtils(world).getProtectedRegion(protectedRegion.getId())))
					return world;
			} catch (InvalidInputException ignore) {}
		}
		return null;
	}

	public static @NotNull Vector3 toVector3(@NotNull Location location) {
		return Vector3.at(location.getX(), location.getY(), location.getZ());
	}

	public static @NotNull Vector3 toVector3(@NotNull Vector vector) {
		return Vector3.at(vector.getX(), vector.getY(), vector.getZ());
	}

	public static @NotNull BlockVector3 toBlockVector3(@NotNull Location location) {
		return BlockVector3.at(location.getX(), location.getY(), location.getZ());
	}

	public static @NotNull BlockVector3 toBlockVector3(@NotNull Vector vector) {
		return BlockVector3.at(vector.getX(), vector.getY(), vector.getZ());
	}

	public @NotNull Location toLocation(@NotNull Vector3 vector) {
		return new Location(world, vector.x(), vector.y(), vector.z());
	}

	public @NotNull Location toLocation(@NotNull BlockVector3 vector) {
		return new Location(world, vector.x(), vector.y(), vector.z());
	}

	public @NotNull Region getRegion(@NotNull String name) {
		return convert(getProtectedRegion(name));
	}

	public @NotNull Region getRegion(@NotNull Location min, @NotNull Location max) {
		return new CuboidRegion(worldEditWorld, toBlockVector3(min), toBlockVector3(max));
	}

	public @NotNull Region getRegion(@NotNull BlockVector3 min, @NotNull BlockVector3 max) {
		return new CuboidRegion(worldEditWorld, min, max);
	}

	public @NotNull Set<ProtectedRegion> getRegionsAt(@NotNull BlockVector3 location) {
		try {
			return REGIONS_AT_CACHE.get(world).get(location);
		} catch (ExecutionException ex) {
			ex.printStackTrace();
			return Collections.emptySet();
		}
	}

	public @NotNull Set<ProtectedRegion> getRegionsAt(@NotNull HasLocation location) {
		if (!isSameWorld(location.getLocation()))
			return Collections.emptySet();

		return getRegionsAt(toBlockVector3(location.getLocation()));
	}

	public @NotNull Set<ProtectedRegion> getRegionsAt(@NotNull Vector vector) {
		return getRegionsAt(toBlockVector3(vector));
	}

	public @NotNull Set<String> getRegionNamesAt(@NotNull Location location) {
		if (!isSameWorld(location))
			return Collections.emptySet();

		return getRegionsAt(location).stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
	}

	public @NotNull Set<String> getRegionNamesAt(@NotNull Vector vector) {
		return getRegionNamesAt(vector.toLocation(world));
	}

	public boolean isInRegion(@NotNull HasLocation location, @NotNull String region) {
		return isInRegion(location.getLocation(), region);
	}

	public boolean isInRegion(@NotNull HasLocation player, @NotNull ProtectedRegion region) {
		return isInRegion(player.getLocation(), region);
	}

	public boolean isInRegion(@NotNull Location location, @NotNull String region) {
		return getRegionNamesAt(location).contains(region);
	}

	public boolean isInRegion(@NotNull Vector vector, @NotNull String region) {
		return getRegionNamesAt(vector).contains(region);
	}

	public boolean isInRegion(@NotNull Location location, @NotNull ProtectedRegion region) {
		return region.contains(toBlockVector3(location));
	}

	public boolean isInRegion(@NotNull Vector vector, @NotNull ProtectedRegion region) {
		return region.contains(toBlockVector3(vector));
	}

	public boolean isInRegionLikeAt(@NotNull String regex, @NotNull Location location) {
		return !getRegionsLikeAt(regex, location).isEmpty();
	}

	public @NotNull Collection<Player> getPlayersInRegion(@NotNull String region) {
		return getPlayersInRegion(getProtectedRegion(region));
	}

	public @NotNull Collection<Player> getPlayersInRegion(@NotNull ProtectedRegion region) {
		return OnlinePlayers.where().world(world).get().stream().filter(player -> isInRegion(player.getLocation(), region) && !CitizensUtils.isNPC(player)).collect(Collectors.toList());
	}

	public @NotNull Collection<NPC> getNPCsInRegion(@NotNull String region) {
		return getNPCsInRegion(getProtectedRegion(region));
	}

	public @NotNull Collection<NPC> getNPCsInRegion(@NotNull ProtectedRegion region) {
		return NPCFinder.builder().world(world).region(region).build().get();
	}

	public @NotNull Collection<Entity> getEntitiesInRegion(@NotNull String region) {
		return getEntitiesInRegion(getProtectedRegion(region));
	}

	public @NotNull Collection<Entity> getEntitiesInRegion(@NotNull ProtectedRegion region) {
		return world.getEntities().stream().filter(entity -> isInRegion(entity.getLocation(), region)).collect(Collectors.toList());
	}

	public @NotNull <T extends Entity> Collection<T> getEntitiesInRegionByClass(@NotNull String region, @NotNull Class<T> type) {
		return getEntitiesInRegionByClass(getProtectedRegion(region), type);
	}

	public @NotNull <T extends Entity> Collection<T> getEntitiesInRegionByClass(@NotNull ProtectedRegion region, @NotNull Class<T> type) {
		return world.getEntitiesByClass(type).stream().filter(entity -> isInRegion(entity.getLocation(), region)).collect(Collectors.toList());
	}

	public @NotNull Set<ProtectedRegion> getRegionsLike(@NotNull String regex) {
		Map<String, ProtectedRegion> regions = manager.getRegions();
		Pattern pattern = RegexUtils.ignoreCasePattern(regex);
		return regions.keySet().stream().filter(id -> pattern.matcher(id).matches()).map(regions::get).collect(Collectors.toSet());
	}

	public @NotNull Set<ProtectedRegion> getRegionsLikeAt(@NotNull String regex, @NotNull HasLocation location) {
		return getRegionsLikeAt(regex, location.getLocation());
	}

	public @NotNull Set<ProtectedRegion> getRegionsLikeAt(@NotNull String regex, @NotNull Location location) {
		if (!isSameWorld(location)) return new HashSet<>();
		Pattern pattern = RegexUtils.ignoreCasePattern(regex);
		return getRegionsAt(location).stream().filter(region -> pattern.matcher(region.getId()).matches()).collect(Collectors.toSet());
	}

	public @NotNull Set<ProtectedRegion> getRegionsLikeAt(@NotNull String regex, @NotNull Vector vector) {
		return getRegionsLikeAt(regex, vector.toLocation(world));
	}

	public boolean isSameWorld(@NotNull Location location) {
		return location.getWorld().equals(world);
	}

	public @NotNull ProtectedRegion getRegionLike(@NotNull String regex) throws InvalidInputException {
		Set<ProtectedRegion> matches = getRegionsLike(regex);
		if (matches.size() == 0)
			throw new InvalidInputException("No regions found");
		return matches.iterator().next();
	}

	public @NotNull ProtectedRegion convert(@NotNull Region region) {
		return convert("temp", region);
	}

	public @NotNull ProtectedRegion convert(@NotNull String id, @NotNull Region region) {
		return new ProtectedCuboidRegion(id, region.getMaximumPoint(), region.getMinimumPoint());
	}

	public @NotNull Region convert(@NotNull ProtectedRegion region) {
		if (region instanceof ProtectedCuboidRegion)
			return new CuboidRegion(worldEditWorld, region.getMaximumPoint(), region.getMinimumPoint());
		else if (region instanceof ProtectedPolygonalRegion)
			return new Polygonal2DRegion(worldEditWorld, region.getPoints(), region.getMinimumPoint().y(), region.getMaximumPoint().y());
		else
			throw new InvalidInputException("Unsupported region type");
	}

	public @NotNull List<BlockVector3> getAllBlocks(Region region) {
		List<BlockVector3> blocks = new ArrayList<>();

		if (region instanceof CuboidRegion cuboidRegion) {
			BlockVector3 min = cuboidRegion.getMinimumPoint();
			BlockVector3 max = cuboidRegion.getMaximumPoint();
			for (int x = min.x(); x <= max.x(); x++)
				for (int y = min.y(); y <= max.y(); y++)
					for (int z = min.z(); z <= max.z(); z++)
						blocks.add(BlockVector3.at(x, y, z));

			return blocks;
		} else {
			BlockVector3 min = region.getMinimumPoint();
			BlockVector3 max = region.getMaximumPoint();
			for (int x = min.x(); x <= max.x(); x++)
				for (int y = min.y(); y <= max.y(); y++)
					for (int z = min.z(); z <= max.z(); z++)
						if (region.contains(BlockVector3.at(x, y, z)))
							blocks.add(BlockVector3.at(x, y, z));

			return blocks;
		}
	}

	public @NotNull Block getRandomBlock(@NotNull String region) {
		return getRandomBlock(getRegion(region));
	}

	public @NotNull Block getRandomBlock(@NotNull ProtectedRegion region) {
		return getRandomBlock(convert(region));
	}

	public @NotNull Block getRandomBlock(@NotNull Region region) {
		int xMin = region.getMinimumPoint().x();
		int yMin = region.getMinimumPoint().y();
		int zMin = region.getMinimumPoint().z();

		int xDiff = region.getMaximumPoint().x() - xMin;
		int yDiff = region.getMaximumPoint().y() - yMin;
		int zDiff = region.getMaximumPoint().z() - zMin;

		int x = xMin + RandomUtils.randomInt(0, xDiff);
		int y = yMin + RandomUtils.randomInt(0, yDiff);
		int z = zMin + RandomUtils.randomInt(0, zDiff);

		return world.getBlockAt(x, y, z);
	}

	public @Nullable Block getRandomBlock(@NotNull ProtectedRegion region, @NotNull Material type) {
		return getRandomBlock(convert(region), type);
	}

	public @Nullable Block getRandomBlock(@NotNull Region region, @NotNull Material type) {
		int ATTEMPTS = 5;
		for (int i = 0; i < ATTEMPTS; i++) {
			Block block = getRandomBlock(region);
			if (block.getType() == type)
				return block;
		}
		return null;
	}

	public @NotNull List<Block> getRandomBlocks(@NotNull ProtectedRegion region, @NotNull Material type, int count) {
		return getRandomBlocks(convert(region), type, count);
	}

	public @NotNull List<Block> getRandomBlocks(@NotNull Region region, @NotNull Material type, int count) {
		List<Block> blocks = new ArrayList<>();
		int SAFETY = 0;
		while (blocks.size() < count && ++SAFETY < (count * 2)) {
			Block block = getRandomBlock(region, type);
			if (block == null) continue;
			if (blocks.contains(block)) continue;
			blocks.add(block);
		}
		return blocks;
	}

	public List<Location> get2DOutline(ProtectedRegion protectedRegion, double yValue) {
		List<BlockVector2> points = new ArrayList<>();

		Region region = convert(protectedRegion);
		if (region instanceof CuboidRegion cuboidRegion) {
			BlockVector3 min = cuboidRegion.getMinimumPoint();
			int minX = min.x();
			int minZ = min.z();

			BlockVector3 max = cuboidRegion.getMaximumPoint();
			int maxX = max.x();
			int maxZ = max.z();

			points.add(BlockVector2.at(minX, minZ));
			points.add(BlockVector2.at(maxX, minZ));
			points.add(BlockVector2.at(maxX, maxZ));
			points.add(BlockVector2.at(minX, maxZ));

		} else if (region instanceof Polygonal2DRegion polyRegion)
			points.addAll(polyRegion.getPoints());

		List<Location> locations = new ArrayList<>();
		for (BlockVector2 point : points) {
			locations.add(new Location(world, point.x(), yValue, point.z()));
		}

		return locations;
	}
}
