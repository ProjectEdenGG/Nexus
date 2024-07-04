package gg.projecteden.nexus.features.survival.structures.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftCreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Spawner {
	Location location;
	int spawnedEntities;

	int maxSpawnedEntities = -1;

	// blockstate data
	int delay = 20;
	int maxNearbyEntities = 4;
	int spawnDelayMin = 200;
	int spawnDelayMax = 800;
	int requiredPlayerRange = 16;
	int spawnCount = 4;

	EntityType spawnType;
	// TODO: EntityData -> requires NMS / NBT shit "SpawnData"

	public Spawner(Location location) {
		this.location = location;
	}

	public boolean isPlaced() {
		return location.getBlock().getType().equals(Material.SPAWNER);
	}

	public void update() {
		if (!(location.getBlock().getState() instanceof CreatureSpawner spawner)) {
			Nexus.warn("Block at " + StringUtils.getLocationString(location) + " is not a spawner!");
			Thread.dumpStack();
			return;
		}

		spawner.setDelay(getDelay());
		spawner.setMaxNearbyEntities(getMaxNearbyEntities());
		spawner.setMinSpawnDelay(getSpawnDelayMin());
		spawner.setMaxSpawnDelay(getSpawnDelayMax());
		spawner.setRequiredPlayerRange(getRequiredPlayerRange());
		spawner.setSpawnCount(getSpawnCount());
		spawner.setSpawnedType(getSpawnType());
		spawner.update();
	}

	public void onSpawnEntity() {
		if (!isPlaced())
			return;

		spawnedEntities++;
		if (maxSpawnedEntities == -1)
			return;

		if (spawnedEntities == maxSpawnedEntities)
			location.getBlock().breakNaturally();
	}

	public static @Nullable SpawnerBlockEntity getNMSSpawner(Block block) {
		if (block.getType() != Material.SPAWNER)
			return null;

		BlockPos pos = NMSUtils.toNMS(block.getLocation());
		ServerLevel world = NMSUtils.toNMS(block.getLocation().getWorld());
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (!(tileEntity instanceof SpawnerBlockEntity spawnerBlock))
			return null;

		return spawnerBlock;
	}

	public static SpawnerBlockEntity getTileEntity(Block block) {
		SpawnerBlockEntity spawnerBlock = Spawner.getNMSSpawner(block);
		if (spawnerBlock == null)
			throw new InvalidInputException("Target block is not a spawner");

		CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
		CraftCreatureSpawner craftCreatureSpawner = (CraftCreatureSpawner) creatureSpawner;
		return craftCreatureSpawner.getTileEntity();
	}

	public static void createSpawnData(Block block, EntityType entityType) {
		if (entityType == null || entityType.getName() == null) {
			throw new IllegalArgumentException("Can't spawn EntityType " + entityType + " from mobspawners!");
		}

		BlockPos pos = NMSUtils.toNMS(block.getLocation());
		ServerLevel world = NMSUtils.toNMS(block.getLocation().getWorld());
		BaseSpawner spawner = getTileEntity(block).getSpawner();

		// NMS Spawn Data
		NMSSpawnData nmsSpawnData = new NMSSpawnData();

		nmsSpawnData.setEntityType(entityType);
		nmsSpawnData.setWeight(2);

		List<ItemStack> armorItems = new ArrayList<>() {{
			add(new ItemStack(Material.DIAMOND_HELMET));
			add(new ItemStack(Material.IRON_CHESTPLATE));
			add(new ItemStack(Material.GOLDEN_LEGGINGS));
			add(new ItemStack(Material.LEATHER_BOOTS));
		}};
		nmsSpawnData.setArmorItems(armorItems);

		nmsSpawnData.setHandItems(new ItemStack(Material.STONE_SWORD), null);

		nmsSpawnData.setSilent(true);
		nmsSpawnData.setLeftHanded(true);
		//

		// NMSSpawnData#getMobSpawnerData
		SpawnData spawnData = new SpawnData(nmsSpawnData.getCompound(), Optional.empty());

		// new NMSSpawner()
		CompoundTag snapshot = new CompoundTag();
		snapshot.putString("id", "minecraft:spawner");

		// NMSSpawner#setSpawnData
		CompoundTag localSpawnData = spawnData.getEntityToSpawn();
		snapshot.put("SpawnData", localSpawnData);

		// NMSSpawner#Update
		CompoundTag localCompound = snapshot.copy();
		spawner.save(localCompound);

		Dev.WAKKA.send(localCompound.toString());
		//


	}

//		// clears spawn potentials
//		spawner.setEntityId(NMSUtils.toNMS(entityType));

//		SpawnData spawnData = new SpawnData();
//		spawnData.getEntityToSpawn();
//
//		// Setting data - SINGLE
//		spawner.spawnPotentials = SimpleWeightedRandomList.single(spawnData);
//		spawner.setNextSpawnData(world, pos, spawnData);
//
//		// Setting data - MULTIPLE
}
