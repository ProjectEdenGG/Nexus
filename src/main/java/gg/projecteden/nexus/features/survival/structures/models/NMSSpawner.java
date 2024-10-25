package gg.projecteden.nexus.features.survival.structures.models;

import gg.projecteden.nexus.utils.nms.NMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class NMSSpawner {
	private final CompoundTag snapshot;

	public NMSSpawner() {
		snapshot = new CompoundTag();
		snapshot.putString("id", "minecraft:spawner");
	}

	public NMSSpawner(final CreatureSpawner cs) {
		this();

		BlockPos pos = NMSUtils.toNMS(cs.getLocation());
		ServerLevel world = NMSUtils.toNMS(cs.getWorld());
		SpawnerBlockEntity tileSpawner = (SpawnerBlockEntity) world.getBlockEntity(pos);

		if (tileSpawner != null)
			tileSpawner.getSpawner().save(snapshot);

		snapshot.remove("Delay"); //Remove the delay of the spawning. Allows spawners to stack properly
	}

	public NMSSpawner(final org.bukkit.inventory.ItemStack item) throws SpawnerItemException {
		if (item == null || item.getType() != Material.SPAWNER) {
			throw new SpawnerItemException();
		}

		ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
		if (!nmsItemStack.hasTag()) {
			throw new SpawnerItemException();
		}

		CompoundTag tag = nmsItemStack.getTag();
		if (tag == null)
			throw new SpawnerItemException();

		if (!tag.contains("BlockEntityTag")) {
			snapshot = new CompoundTag();
			tag.put("BlockEntityTag", snapshot);
		} else {
			snapshot = tag.getCompound("BlockEntityTag");
		}

		if (!snapshot.contains("id") || !snapshot.getString("id").contains("spawner")) {
			snapshot.putString("id", "minecraft:spawner");
		}
	}

	public enum SpawnerDataEnum {
		PLACE_DELAY("Delay"),
		SPAWN_RANGE("SpawnRange"),
		REQUIRED_PLAYER_RANGE("RequiredPlayerRange"),
		MAX_NEARBY_ENTITIES("MaxNearbyEntities"),
		MIN_SPAWN_DELAY("MinSpawnDelay"),
		MAX_SPAWN_DELAY("MaxSpawnDelay"),
		SPAWN_COUNT("SpawnCount");

		private final String nmsTag;

		SpawnerDataEnum(final String nmsTag) {
			this.nmsTag = nmsTag;
		}

		public String getTag() {
			return nmsTag;
		}
	}

	/**
	 * Get a specific value of this spawner
	 *
	 * @param data The spawner data to get
	 * @return The spawner data short value
	 */
	public short getData(final SpawnerDataEnum data) {
		return snapshot.getShort(data.getTag());
	}

	/**
	 * Set a specific value of this spawner
	 *
	 * @param data  The spawner data to set
	 * @param value The spawner data short value
	 */
	public void setData(final SpawnerDataEnum data, final short value) {
		snapshot.putShort(data.getTag(), value);
	}

	/**
	 * Get the SpawnData of this spawner
	 *
	 * @return The wrapped SpawnData
	 */
	public NMSSpawnData getSpawnData() {
		return new NMSSpawnData(snapshot.getCompound("SpawnData"));
	}

	/**
	 * Set the SpawnData of this spawner. If the spawnPotentials are present, this will be overriden by them
	 *
	 * @param data The wrapped SpawnData
	 */
	public void setSpawnData(final NMSSpawnData data) {
		CompoundTag localSpawnData = data.getMobSpawnerData().getEntityToSpawn(); //Method "b" get only the Entity compound
		snapshot.put("SpawnData", localSpawnData);
	}

	/**
	 * Set the Spawn Potentials of this spawner. This will override the SpawnData
	 *
	 * @param list The list of wrapped SpawnData
	 */
	public void setSpawnPotentials(final List<NMSSpawnData> list) {
		if (list == null) {
			return;
		} else if (list.isEmpty()) {
			snapshot.remove("SpawnPotentials");
			return;
		}

		ListTag localSpawnDataList = new ListTag();
		list.stream().map(NMSSpawnData::getMobSpawnerData)
			.map(SpawnData::getEntityToSpawn) //Method "a" get the compound with both Weight and Entity compound // TODO
			.forEach(localSpawnDataList::add);
		this.setSpawnData(list.get(0));
		snapshot.put("SpawnPotentials", localSpawnDataList);
	}

	/**
	 * Add a SpawnData to the existing SpawnPotentials
	 *
	 * @param newData The wrapper SpawnData to add
	 */
	public void addSpawnPotential(final NMSSpawnData newData) {
		if (newData == null) {
			return;
		}

		ListTag localSpawnDataList = snapshot.getList("SpawnPotentials", 10); // Compound id = 10
		localSpawnDataList.add(newData.getMobSpawnerData().getEntityToSpawn()); //Method "a" get the compound with both Weight and Entity compound // TODO
		snapshot.put("SpawnPotentials", localSpawnDataList);
	}

	/**
	 * Get all the wrapper SpawnData
	 *
	 * @return A list of all SpawnData
	 */
	public List<NMSSpawnData> getSpawnPotentials() {
		List<NMSSpawnData> spawnDataList = new ArrayList<>();

		ListTag list = snapshot.getList("SpawnPotentials", 10); //Compound id = 10
		IntStream.range(0, list.size()).forEach(x -> spawnDataList.add(new NMSSpawnData((CompoundTag) list.get(x))));

		return spawnDataList;
	}

	/**
	 * Remove all the SpawnPotentials
	 */
	public void clearSpawnPotentials() {
		snapshot.remove("SpawnPotentials");
	}

	/**
	 * Apply this snapshot to a placed spawner block
	 *
	 * @param cs The creature spawner
	 */
	public void update(final CreatureSpawner cs) {
		BlockPos pos = NMSUtils.toNMS(cs.getLocation());
		ServerLevel world = NMSUtils.toNMS(cs.getWorld());
		SpawnerBlockEntity blockEntity = (SpawnerBlockEntity) world.getBlockEntity(pos);
		if (blockEntity == null)
			return;

		CompoundTag localCompound = snapshot.copy();
		blockEntity.getSpawner().save(localCompound);
	}

	/**
	 * Apply this snapshot to nbt tags of an ItemStack. You will be able to use the constructor of this class to reuse this data.
	 *
	 * @param itemStack The ItemStack to apply the NBTTagCompound
	 * @return The ItemStack after the NBTTagCompound is applied
	 */
	public org.bukkit.inventory.ItemStack addSnapshot(final org.bukkit.inventory.ItemStack itemStack) {
		if (itemStack == null)
			return null;

		ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
		CompoundTag tag = nmsItemStack.getTag();
		if (tag == null)
			tag = new CompoundTag();

		tag.put("BlockEntityTag", snapshot);
		nmsItemStack.setTag(tag);

		return CraftItemStack.asBukkitCopy(nmsItemStack);
	}

	@Override
	public String toString() {
		return snapshot.toString();
	}

	public static class SpawnerItemException extends Exception {

	}


}
