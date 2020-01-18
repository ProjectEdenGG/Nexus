package me.pugabyte.bncore.utils;

import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldEditUtils {
	@NonNull
	private org.bukkit.World world;
	private BukkitWorld bukkitWorld;
	private World worldEditWorld;
	private WorldGuardUtils worldGuardUtils;
	@Getter
	private String schematicsDirectory = "plugins/WorldEdit/schematics/";

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

}
