package gg.projecteden.nexus.features.survival.structures.models;

import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.SpawnData;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NMSSpawnData {
	@Getter
	private final CompoundTag compound;

	public NMSSpawnData(final CompoundTag compound) {
		this.compound = compound;
	}

	public NMSSpawnData() {
		this(new CompoundTag());
		this.setEntityType(EntityType.PIG);
		this.setWeight(1);
	}

	public NMSSpawnData(final EntityType et, final int weight) {
		this.compound = new CompoundTag();
		this.setEntityType(et);
		this.setWeight(weight);
	}

	public NMSSpawnData(final EntityType et) {
		this(et, 1);
	}

	public NMSSpawnData(final String name, final int weight) {
		this.compound = new CompoundTag();
		this.setEntityName(name);
		this.setWeight(weight);
	}

	public NMSSpawnData(final String name) {
		this(name, 1);
	}


	@AllArgsConstructor
	public enum SpawnDataEnum {
		ENTITY("Entity"),
		WEIGHT("Weight");

		@Getter
		private final String tag;
	}

	public NMSSpawnData setSilent(final boolean silent) {
		String tag = SpawnDataEnum.ENTITY.getTag();
		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.putBoolean("Silent", silent);
		compound.put(tag, localCompound);

		return this;
	}

	public NMSSpawnData setGlowing(final boolean glow) {
		String tag = SpawnDataEnum.ENTITY.getTag();
		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.putBoolean("Glowing", glow);
		compound.put(tag, localCompound);

		return this;
	}

	public NMSSpawnData setFireTicks(final int ticks) {
		String tag = SpawnDataEnum.ENTITY.getTag();
		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.putShort("Fire", (short) ticks);
		compound.put(tag, localCompound);

		return this;
	}

	public NMSSpawnData setAi(final boolean ai) {
		String tag = SpawnDataEnum.ENTITY.getTag();
		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.putBoolean("NoAI", !ai);
		compound.put(tag, localCompound);

		return this;
	}

	public String getEntityName() {
		String tag = SpawnDataEnum.ENTITY.getTag();
		return compound.getCompound(tag).getString("id").replace("minecraft:", "");
	}

	public EntityType getEntityType() {
		return EntityType.fromName(getEntityName());
	}

	public NMSSpawnData setEntityName(final String name) {
		String tag = SpawnDataEnum.ENTITY.getTag();
		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.putString("id", "minecraft:" + name);
		compound.put(tag, localCompound);

		return this;
	}

	public NMSSpawnData setEntityType(final EntityType type) {
		String tag = SpawnDataEnum.ENTITY.getTag();
		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.putString("id", "minecraft:" + type.getName());
		compound.put(tag, localCompound);

		return this;
	}

	public NMSSpawnData setLeftHanded(final boolean left) {
		String tag = SpawnDataEnum.ENTITY.getTag();
		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.putBoolean("LeftHanded", left);
		compound.put(tag, localCompound);

		return this;
	}

	public NMSSpawnData setWeight(final int weight) {
		String tag = SpawnDataEnum.WEIGHT.getTag();
		compound.putInt(tag, weight);

		return this;
	}

	public int getWeight() {
		String tag = SpawnDataEnum.WEIGHT.getTag();
		return compound.getInt(tag);
	}

	public NMSSpawnData setArmorItems(List<org.bukkit.inventory.ItemStack> armorItems) {
		Collections.reverse(armorItems);
		String tag = SpawnDataEnum.ENTITY.getTag();

		ListTag localArmorList = new ListTag();
		for (ItemStack armorItem : armorItems) {
			CompoundTag itemTag = armorItem == null ? new CompoundTag() : NMSUtils.toNMS(armorItem).save(new CompoundTag());

			localArmorList.add(itemTag);
		}

		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.put("ArmorItems", localArmorList);
		compound.put(tag, localCompound);

		return this;
	}

	public NMSSpawnData setHandItems(final @Nullable org.bukkit.inventory.ItemStack mainHand, final @Nullable org.bukkit.inventory.ItemStack offHand) {
		String tag = SpawnDataEnum.ENTITY.getTag();

		ListTag localHandList = new ListTag();
		localHandList.add(mainHand == null ? new CompoundTag() : NMSUtils.toNMS(mainHand).save(new CompoundTag()));
		localHandList.add(offHand == null ? new CompoundTag() : NMSUtils.toNMS(offHand).save(new CompoundTag()));

		CompoundTag localCompound = compound.getCompound(tag);
		localCompound.put("HandItems", localHandList);
		compound.put(tag, localCompound);

		return this;
	}

	protected SpawnData getMobSpawnerData() {
		return new SpawnData(compound, Optional.empty());
	}

	@Override
	public String toString() {
		return compound.toString();
	}
}
