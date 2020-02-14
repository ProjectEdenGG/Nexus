package me.pugabyte.bncore.utils;

import com.boydti.fawe.config.BBC;
import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class WorldEditUtils {
	@NonNull
	private org.bukkit.World world;
	private BukkitWorld bukkitWorld;
	private World worldEditWorld;
	private WorldGuardUtils worldGuardUtils;
	@Getter
	private String schematicsDirectory = "plugins/WorldEdit/schematics/";
	@Getter
	static WorldEditPlugin plugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

	public WorldEditUtils(org.bukkit.World world) {
		this.world = world;
		bukkitWorld = new BukkitWorld(world);
		worldEditWorld = bukkitWorld;
		worldGuardUtils = new WorldGuardUtils(world);
	}

	public EditSession getEditSession() {
		return new EditSessionBuilder(worldEditWorld).fastmode(true).build();
	}

	private File getSchematicFile(String fileName) {
		return new File(schematicsDirectory + fileName + ".schematic");
	}

	public Vector toVector(Location location) {
		return new Vector(location.getX(), location.getY(), location.getZ());
	}

	public Location toLocation(Vector vector) {
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}

	public FawePlayer<Object> getPlayer(Player player) {
		return FawePlayer.wrap(player);
	}

	public Region getPlayerSelection(Player player) {
		return getPlayer(player).getSelection();
	}

	public enum SelectionChangeDirectionType {
		HORIZONTAL(Direction::isCardinal),
		VERTICAL(Direction::isUpright),
		ALL(direction -> direction.isCardinal() || direction.isUpright());

		@Getter
		private Function<Direction, Boolean> filter;

		SelectionChangeDirectionType(Function<Direction, Boolean> filter) {
			this.filter = filter;
		}
	}

	public enum SelectionChangeType {
		EXPAND,
		CONTRACT
	}

	@SneakyThrows
	public void changeSelection(Player player, SelectionChangeType changeType, SelectionChangeDirectionType directionType, int amount) {
		if (amount <= 0) return;
		LocalSession session = plugin.getSession(player);
		Region region = session.getSelection(worldEditWorld);
		int oldSize = region.getArea();
		Vector[] directions = getDirections(directionType, amount);

		if (changeType == SelectionChangeType.EXPAND)
			region.expand(directions);
		else if (changeType == SelectionChangeType.CONTRACT)
			region.contract(directions);

		getPlayer(player).setSelection(region);
		session.getRegionSelector(worldEditWorld).learnChanges();
		int newSize = region.getArea();
		com.sk89q.worldedit.entity.Player worldEditPlayer = plugin.wrapPlayer(player);
		session.getRegionSelector(worldEditWorld).explainRegionAdjust(worldEditPlayer, session);
		BBC.SELECTION_EXPAND.send(worldEditPlayer, (newSize - oldSize));
	}

	public void setSelection(Player player, Location location) {
		setSelection(player, location, location);
	}

	public void setSelection(Player player, Vector vector) {
		setSelection(player, vector);
	}

	public void setSelection(Player player, Location min, Location max) {
		setSelection(player, toVector(min), toVector(min));
	}

	public void setSelection(Player player, Vector min, Vector max) {
		LocalSession session = plugin.getSession(player);
		Region region = new CuboidRegion(min, max);
		getPlayer(player).setSelection(region);
		com.sk89q.worldedit.entity.Player worldEditPlayer = plugin.wrapPlayer(player);
		session.getRegionSelector(worldEditWorld).explainPrimarySelection(worldEditPlayer, session, region.getMinimumPoint());
		session.getRegionSelector(worldEditWorld).explainSecondarySelection(worldEditPlayer, session, region.getMaximumPoint());
	}

	@NotNull
	private Vector[] getDirections(SelectionChangeDirectionType type, int number) {
		return Arrays.stream(Direction.values())
				.filter(direction -> type.getFilter().apply(direction))
				.map(Direction::toVector)
				.map(vector -> vector.multiply(number))
				.toArray(Vector[]::new);
	}

	public List<Block> getBlocks(CuboidRegion region) {
		List<Block> blockList = new ArrayList<>();
		for (int x = region.getMinimumPoint().getBlockX(); x <= region.getMaximumPoint().getBlockX(); x++) {
			for (int y = region.getMinimumPoint().getBlockY(); y <= region.getMaximumPoint().getBlockY(); y++) {
				for (int z = region.getMinimumPoint().getBlockZ(); z <= region.getMaximumPoint().getBlockZ(); z++) {
					blockList.add(world.getBlockAt(x, y, z));
				}
			}
		}
		return blockList;
	}

	public BaseBlock toBaseBlock(Material material) {
		return toBaseBlock(material, (short) 0);
	}

	public BaseBlock toBaseBlock(Material material, short data) {
		return new BaseBlock(material.getId(), data);
	}

	public Set<BaseBlock> toBaseBlocks(Set<Material> materials) {
		Set<BaseBlock> baseBlocks = new HashSet<>();
		materials.forEach(material -> baseBlocks.add(toBaseBlock(material)));
		return baseBlocks;
	}

	public RandomPattern toRandomPattern(Set<Material> materials) {
		RandomPattern pattern = new RandomPattern();
		toBaseBlocks(materials).forEach(baseBlock -> pattern.add(baseBlock, (float) 100 / materials.size()));
		return pattern;
	}

	public RandomPattern toRandomPattern(Map<Material, Double> materials) {
		RandomPattern pattern = new RandomPattern();
		materials.forEach((material, chance) -> pattern.add(toBaseBlock(material), chance));
		return pattern;
	}

	public Schematic copy(Location min, Location max) {
		return copy((CuboidRegion) worldGuardUtils.getRegion(min, max));
	}

	public Schematic copy(Region region) {
		return new Schematic(region);
	}

	public void paste(String fileName, Location location) {
		paste(fileName, toVector(location));
	}

	@SneakyThrows
	public void paste(String fileName, Vector vector) {
		File file = getSchematicFile(fileName);
		if (!file.exists())
			throw new InvalidInputException("Schematic " + fileName + " does not exist");

		paste(ClipboardFormats.findByFile(file).load(file), vector);
	}

	public void paste(Schematic schematic, Location location) {
		paste(schematic, toVector(location));
	}

	public void paste(Schematic schematic, Vector vector) {
		schematic.paste(worldEditWorld, vector);
	}

	public void save(String fileName, Location min, Location max) {
		save(fileName, toVector(min), toVector(max));
	}

	public void save(String fileName, Region region) {
		save(fileName, region.getMinimumPoint(), region.getMaximumPoint());
	}

	@SneakyThrows
	public void save(String fileName, Vector min, Vector max) {
		CuboidRegion region = new CuboidRegion(worldEditWorld, min, max);
		new Schematic(region).save(getSchematicFile(fileName), ClipboardFormat.SCHEMATIC);
	}

	public void fill(String region, Material material) {
		fill(region, material, 0);
	}

	public void fill(String region, Material material, int data) {
		fill(worldGuardUtils.convert(worldGuardUtils.getProtectedRegion(region)), material, data);
	}

	public void fill(Region region, Material material) {
		fill(region, material, 0);
	}

	public void fill(Region region, Material material, int data) {
		EditSession editSession = getEditSession();
		editSession.setBlocks(region, new BaseBlock(material.getId(), data));
		editSession.flushQueue();
	}

	public void replace(Region region, Material from, Material to) {
		replace(region, Collections.singleton(from), Collections.singleton(to));
	}

	public void replace(Region region, Set<Material> from, Set<Material> to) {
		replace(region, from, toRandomPattern(to));
	}

	public void replace(Region region, Set<Material> from, Map<Material, Double> pattern) {
		replace(region, from, toRandomPattern(pattern));
	}

	public void replace(Region region, Set<Material> from, Pattern pattern) {
		EditSession editSession = getEditSession();
		editSession.replaceBlocks(region, toBaseBlocks(from), pattern);
		editSession.flushQueue();
	}

	@SneakyThrows
	public Region expandAll(Region region, int amount) {
		region.expand(Arrays.stream(Direction.values())
				.filter((direction -> direction.isUpright() || direction.isCardinal()))
				.map(Direction::toVector)
				.map(vector -> vector.multiply(amount))
				.toArray(Vector[]::new));
		return region;
	}

	@SneakyThrows
	public Region contractAll(Region region, int amount) {
		region.contract(Arrays.stream(Direction.values())
				.filter((direction -> direction.isUpright() || direction.isCardinal()))
				.map(Direction::toVector)
				.map(vector -> vector.multiply(amount))
				.toArray(Vector[]::new));
		return region;
	}

	@SneakyThrows
	public void fixFlat(LocalSession session, Region region) {
		region.expand(Direction.UP.toVector().multiply(500));
		region.expand(Direction.DOWN.toVector().multiply(500));
		fill(region, Material.AIR);
		region.expand(Direction.DOWN.toVector().multiply(500));
		region.contract(Direction.DOWN.toVector().multiply(500));
		session.getRegionSelector(region.getWorld()).learnChanges();
		fill(region, Material.BEDROCK);
		region.expand(Direction.UP.toVector().multiply(3));
		region.contract(Direction.UP.toVector().multiply(1));
		session.getRegionSelector(region.getWorld()).learnChanges();
		fill(region, Material.GRASS);
	}

}
