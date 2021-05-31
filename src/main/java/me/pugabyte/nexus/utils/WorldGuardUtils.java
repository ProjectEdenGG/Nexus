package me.pugabyte.nexus.utils;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import eden.utils.RegexUtils;
import lombok.Data;
import lombok.NonNull;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class WorldGuardUtils {
	@NonNull
	private final org.bukkit.World world;
	private final BukkitWorld bukkitWorld;
	private final World worldEditWorld;
	private final RegionManager manager;
	public static final WorldGuardPlugin plugin = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

	public WorldGuardUtils(@NonNull org.bukkit.entity.Entity entity) {
		this(entity.getWorld());
	}

	public WorldGuardUtils(@NonNull org.bukkit.Location location) {
		this(location.getWorld());
	}

	public WorldGuardUtils(@NonNull org.bukkit.block.Block block) {
		this(block.getWorld());
	}

	public WorldGuardUtils(@NonNull String world) {
		this(Bukkit.getWorld(world));
	}

	public WorldGuardUtils(@NonNull org.bukkit.World world) {
		this.world = world;
		this.bukkitWorld = new BukkitWorld(world);
		this.worldEditWorld = bukkitWorld;
		this.manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(bukkitWorld);
	}

	public RegionContainer getContainer() {
		return WorldGuard.getInstance().getPlatform().getRegionContainer();
	}

	public ProtectedRegion getProtectedRegion(String name) {
		ProtectedRegion region = manager.getRegion(name.toLowerCase());
		if (region == null)
			throw new InvalidInputException("Region &e" + name + " &cnot found");
		return region;
	}

	public static org.bukkit.World getWorld(ProtectedRegion protectedRegion) {
		for (org.bukkit.World world : Bukkit.getWorlds()) {
			try {
				if (protectedRegion.equals(new WorldGuardUtils(world).getProtectedRegion(protectedRegion.getId())))
					return world;
			} catch (InvalidInputException ignore) {}
		}
		return null;
	}

	public static Vector3 toVector3(Location location) {
		return Vector3.at(location.getX(), location.getY(), location.getZ());
	}

	public static Vector3 toVector3(Vector vector) {
		return Vector3.at(vector.getX(), vector.getY(), vector.getZ());
	}

	public static BlockVector3 toBlockVector3(Location location) {
		return BlockVector3.at(location.getX(), location.getY(), location.getZ());
	}

	public static BlockVector3 toBlockVector3(Vector vector) {
		return BlockVector3.at(vector.getX(), vector.getY(), vector.getZ());
	}

	public Location toLocation(Vector3 vector) {
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}

	public Location toLocation(BlockVector3 vector) {
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}

	public Region getRegion(String name) {
		return convert(getProtectedRegion(name));
	}

	public Region getRegion(Location min, Location max) {
		return new CuboidRegion(worldEditWorld, toBlockVector3(min), toBlockVector3(max));
	}

	public Set<ProtectedRegion> getRegionsAt(Location location) {
		if (!isSameWorld(location))
			return new HashSet<>();

		return manager.getApplicableRegions(toBlockVector3(location)).getRegions();
	}

	public Set<ProtectedRegion> getRegionsAt(Vector vector) {
		return getRegionsAt(vector.toLocation(world));
	}

	public Set<String> getRegionNamesAt(Location location) {
		if (!isSameWorld(location))
			return new HashSet<>();

		return getRegionsAt(location).stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
	}

	public Set<String> getRegionNamesAt(Vector vector) {
		return getRegionNamesAt(vector.toLocation(world));
	}

	public boolean isInRegion(HasPlayer player, String region) {
		return isInRegion(player.getPlayer().getLocation(), region);
	}

	public boolean isInRegion(HasPlayer player, ProtectedRegion region) {
		return isInRegion(player.getPlayer().getLocation(), region);
	}

	public boolean isInRegion(Location location, String region) {
		return getRegionNamesAt(location).contains(region);
	}

	public boolean isInRegion(Vector vector, String region) {
		return getRegionNamesAt(vector).contains(region);
	}

	public boolean isInRegion(Location location, ProtectedRegion region) {
		return region.contains(toBlockVector3(location));
	}

	public boolean isInRegion(Vector vector, ProtectedRegion region) {
		return region.contains(toBlockVector3(vector));
	}

	public Collection<Player> getPlayersInRegion(String region) {
		return getPlayersInRegion(getProtectedRegion(region));
	}

	public Collection<Player> getPlayersInRegion(ProtectedRegion region) {
		return Bukkit.getOnlinePlayers().stream().filter(player -> isInRegion(player.getLocation(), region) && !CitizensUtils.isNPC(player)).collect(Collectors.toList());
	}

	public Collection<NPC> getNPCsInRegion(String region) {
		return getNPCsInRegion(getProtectedRegion(region));
	}

	public Collection<NPC> getNPCsInRegion(ProtectedRegion region) {
		return CitizensUtils.GetNPCs.builder().world(world).region(region).build().get();
	}

	public Collection<Entity> getEntitiesInRegion(String region) {
		return getEntitiesInRegion(getProtectedRegion(region));
	}

	public Collection<Entity> getEntitiesInRegion(ProtectedRegion region) {
		return world.getEntities().stream().filter(entity -> isInRegion(entity.getLocation(), region)).collect(Collectors.toList());
	}

	public <T extends Entity> Collection<T> getEntitiesInRegionByClass(String region, Class<T> type) {
		return getEntitiesInRegionByClass(getProtectedRegion(region), type);
	}

	public <T extends Entity> Collection<T> getEntitiesInRegionByClass(ProtectedRegion region, Class<T> type) {
		return world.getEntitiesByClass(type).stream().filter(entity -> isInRegion(entity.getLocation(), region)).collect(Collectors.toList());
	}

	public Set<ProtectedRegion> getRegionsLike(String regex) {
		Map<String, ProtectedRegion> regions = manager.getRegions();
		Pattern pattern = RegexUtils.ignoreCasePattern(regex);
		return regions.keySet().stream().filter(id -> pattern.matcher(id).matches()).map(regions::get).collect(Collectors.toSet());
	}

	public Set<ProtectedRegion> getRegionsLikeAt(String regex, Location location) {
		if (!isSameWorld(location)) return new HashSet<>();
		Pattern pattern = RegexUtils.ignoreCasePattern(regex);
		return getRegionsAt(location).stream().filter(region -> pattern.matcher(region.getId()).matches()).collect(Collectors.toSet());
	}

	public Set<ProtectedRegion> getRegionsLikeAt(String regex, Vector vector) {
		return getRegionsLikeAt(regex, vector.toLocation(world));
	}

	public boolean isSameWorld(Location location) {
		return location.getWorld().equals(world);
	}

	public ProtectedRegion getRegionLike(String regex) {
		Set<ProtectedRegion> matches = getRegionsLike(regex);
		if (matches.size() == 0)
			throw new InvalidInputException("No regions found");
		return matches.iterator().next();
	}

	public ProtectedRegion convert(Region region) {
		return convert("temp", region);
	}

	public ProtectedRegion convert(String id, Region region) {
		return new ProtectedCuboidRegion(id, region.getMaximumPoint(), region.getMinimumPoint());
	}

	public Region convert(ProtectedRegion region) {
		return new CuboidRegion(worldEditWorld, region.getMaximumPoint(), region.getMinimumPoint());
	}

	public Block getRandomBlock(String region) {
		return getRandomBlock(getProtectedRegion(region));
	}

	public Block getRandomBlock(ProtectedRegion region) {
		int xMin = region.getMinimumPoint().getBlockX();
		int yMin = region.getMinimumPoint().getBlockY();
		int zMin = region.getMinimumPoint().getBlockZ();

		int xDiff = region.getMaximumPoint().getBlockX() - xMin;
		int yDiff = region.getMaximumPoint().getBlockY() - yMin;
		int zDiff = region.getMaximumPoint().getBlockZ() - zMin;

		int x = xMin + RandomUtils.randomInt(0, xDiff);
		int y = yMin + RandomUtils.randomInt(0, yDiff);
		int z = zMin + RandomUtils.randomInt(0, zDiff);

		return world.getBlockAt(x, y, z);
	}

	public Block getRandomBlock(ProtectedRegion region, Material type) {
		int ATTEMPTS = 5;
		for (int i = 0; i < ATTEMPTS; i++) {
			Block block = getRandomBlock(region);
			if (block.getType() == type)
				return block;
		}
		return null;
	}

	public List<Block> getRandomBlocks(ProtectedRegion region, Material type, int count) {
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

}
