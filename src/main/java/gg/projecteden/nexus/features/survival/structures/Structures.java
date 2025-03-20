package gg.projecteden.nexus.features.survival.structures;

import com.magmaguy.betterstructures.api.BuildPlaceEvent;
import com.magmaguy.betterstructures.buildingfitter.FitAnything;
import com.magmaguy.betterstructures.buildingfitter.util.LocationProjector;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.survival.structures.models.Spawner;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.structure.Structure;
import gg.projecteden.nexus.models.structure.StructureService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.LocationUtils.NeighborDirection;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class Structures extends Feature implements Listener {
	private static final String region_buildadmin = "structures";
	StructureService structureService = new StructureService();

	List<Material> spawnEggTypes = new ArrayList<>() {{
		addAll(MaterialTag.SPAWN_EGGS.getValues());
		add(Material.END_CRYSTAL);
	}};

	List<Material> MUSHROOMS = List.of(Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS);

	@EventHandler
	public void onPlaceUnsafe(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item)) return;
		Material type = item.getType();

		if (!MUSHROOMS.contains(type)) return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock)) return;
		if (!isInBuildRegion(event.getPlayer())) return;

		Block block = clickedBlock.getRelative(event.getBlockFace());
		if (!MaterialTag.ALL_AIR.isTagged(block)) return;

		// TODO: cancel if block can be placed
		block.setType(type, false);
		new SoundBuilder(Sound.BLOCK_GRASS_PLACE).location(block).volume(0.5).play();
	}

	@EventHandler
	public void onUseSpawnEgg(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item)) return;
		if (!spawnEggTypes.contains(item.getType())) return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock)) return;
		if (clickedBlock.getType().equals(Material.SPAWNER)) return;
		if (!isInBuildRegion(event.getPlayer())) return;

		Block block = clickedBlock.getRelative(event.getBlockFace());
		boolean isAir = MaterialTag.ALL_AIR.isTagged(block);
		boolean isWater = block.getType().equals(Material.WATER);
		if (!isAir && !isWater) return;

		EntityType entityType = null;
		if (MaterialTag.SPAWN_EGGS.isTagged(item.getType())) {
			String spawnEggType = item.getType().name().toUpperCase().replaceAll("_SPAWN_EGG", "");

			try {
				entityType = EntityType.valueOf(spawnEggType);
			} catch (Exception ignored) {
				return;
			}
		} else if (item.getType().equals(Material.END_CRYSTAL)) {
			entityType = EntityType.END_CRYSTAL;
		}

		if (entityType == null) return;

		// Place spawn sign instead
		event.setCancelled(true);

		block.setType(Material.OAK_SIGN);
		if (isWater) {
			Waterlogged waterlogged = (Waterlogged) block.getBlockData();
			waterlogged.setWaterlogged(true);
			block.setBlockData(waterlogged);
		}

		Sign sign = (Sign) block.getState();
		sign.setLine(0, "[spawn]");
		sign.setLine(1, entityType.name().toLowerCase());
		sign.update();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(BuildPlaceEvent event) {
		if (event.isCancelled()) return;

		FitAnything fitAnything = event.getFitAnything();
		Location baseLocation = fitAnything.getLocation();
		Clipboard clipboard = fitAnything.getSchematicContainer().getClipboard();
		Vector schematicOffset = WorldEditUtils.getSchematicOffset(clipboard);

		World world = baseLocation.getWorld();
		WorldEditUtils worldedit = new WorldEditUtils(world);
		BlockVector3 minVector = clipboard.getMinimumPoint();
		Location structLoc = worldedit.toLocation(minVector);

		if (structureService.existsAt(structLoc)) {
			event.setCancelled(true);
			Dev.WAKKA.send("Structure exists at potential location, cancelling");
			return;
		}

		Structure structure = structureService.getOrCreate(structLoc);

		BlockVector3 dimensions = clipboard.getDimensions();
		for (Vector vector : LocationUtils.getVectorsInAABB(new Vector(), worldedit.toVector(dimensions))) {

			BlockVector3 blockVector = worldedit.toBlockVector3(vector).add(minVector);
			BlockState blockState = clipboard.getBlock(blockVector);
			Material material = BukkitAdapter.adapt(blockState.getBlockType());
			if (material == null)
				continue;

			if (Material.SPAWNER.equals(material)) {
				Dev.WAKKA.send("Spawner spawned");
				Location blockLocation = LocationProjector.project(baseLocation, schematicOffset, vector).clone();
				Spawner spawner = new Spawner(blockLocation);

				//
				boolean configuredSpawner = false;
				for (NeighborDirection dir : NeighborDirection.values()) {
					BlockVector3 translatedVector = blockVector.add(worldedit.toBlockVector3(dir.getDirection()));

					BaseBlock baseBlock = clipboard.getFullBlock(translatedVector);
					List<String> lines = worldedit.getSignLines(baseBlock);
					if (Nullables.isNullOrEmpty(lines))
						continue;

					if (!lines.get(0).equalsIgnoreCase("[spawner]"))
						continue;

					// Sets sign to air when it is pasted
					AtomicInteger id = new AtomicInteger();
					id.set(Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(5), () -> {
						Location signLoc = worldedit.toLocation(translatedVector);
						if (MaterialTag.SIGNS.isTagged(signLoc.getBlock().getType())) {
							signLoc.getBlock().setType(Material.AIR);
							Tasks.cancel(id.get());
						}
					}));

					if (Utils.isInt(lines.get(1))) {
						spawner.setMaxSpawnedEntities(Integer.parseInt(lines.get(1)));
						StringUtils.getJsonLocation("[Limited Spawner]", spawner.getLocation());
						configuredSpawner = true;
					}
				}
				//

				if (configuredSpawner)
					spawner.update();

				structure.getSpawners().add(spawner);
			}
		}

		structureService.save(structure);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(SpawnerSpawnEvent event) {
		Structure structure = structureService.getFrom(event.getSpawner());
		if (structure == null) return;

		Spawner spawner = structure.getSpawner(event.getSpawner());
		if (spawner == null) return;

		spawner.onSpawnEntity();
	}


	@Data
	@AllArgsConstructor
	public static class AbstractBlock {
		World world;
		BlockVector3 vector;
		Material material;

		public Location getLocation() {
			return new Location(world, vector.x(), vector.y(), vector.z());
		}
	}


	@AllArgsConstructor
	public enum SpecialBlockType {
		TINTED_GLASS(Material.TINTED_GLASS, Material.AIR),
		RED_GLASS(Material.RED_STAINED_GLASS, Material.BARRIER),
		NETHERITE_BLOCK(Material.NETHERITE_BLOCK, Material.TNT),

		;

		@Getter
		private final Material fromType;
		@Getter
		private final Material toType;

		public static @Nullable Material convert(Material fromType) {
			SpecialBlockType type = Arrays.stream(values())
				.filter(specialBlockType -> specialBlockType.getFromType().equals(fromType))
				.findFirst()
				.orElse(null);

			return type == null ? null : type.getToType();
		}
	}

	public static boolean isInBuildRegion(Player player) {
		World world = player.getWorld();
		if (!world.equals(Bukkit.getWorld("buildadmin")))
			return false;

		return new WorldGuardUtils(world).isInRegion(player, region_buildadmin);
	}
}
