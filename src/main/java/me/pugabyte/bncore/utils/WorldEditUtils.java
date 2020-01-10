package me.pugabyte.bncore.utils;

import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;

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

	private File getSchematicFile(String fileName) {
		return new File(schematicsDirectory + fileName + ".schematic");
	}

	static Vector getVector(Location location) {
		return new Vector(location.getX(), location.getY(), location.getZ());
	}

	public void paste(String fileName, Location location) {
		paste(fileName, getVector(location));
	}

	@SneakyThrows
	public void paste(String fileName, Vector vector) {
		File file = getSchematicFile(fileName);
		if (!file.exists())
			throw new InvalidInputException("Schematic " + fileName + " does not exist");

		ClipboardFormats.findByFile(file).load(file).paste(worldEditWorld, vector);
		BNCore.log("Schematic " + file.getName() + " pasted at " + vector.toString());
	}

	public void save(String fileName, Location min, Location max) {
		save(fileName, getVector(min), getVector(max));
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
		fill(worldGuardUtils.getRegion(region), material, data);
	}

	public void fill(ProtectedRegion region, Material material) {
		fill(region, material, 0);
	}

	public void fill(ProtectedRegion region, Material material, int data) {
		EditSession editSession = new EditSessionBuilder(worldEditWorld).fastmode(true).build();
		editSession.setBlocks(worldGuardUtils.convert(region), new BaseBlock(material.getId(), data));
		editSession.flushQueue();
	}
}
