package me.pugabyte.nexus.features.mobheads.common;

import me.pugabyte.nexus.features.mobheads.MobHeadType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface MobHead {

	String name();

	EntityType getEntityType();

	MobHeadType getType();

	default MobHeadVariant getVariant() {
		return null;
	}

	ItemStack getSkull();

	static MobHead of(Entity entity) {
		MobHeadType type = MobHeadType.of(entity.getType());
		MobHeadVariant variant = type.getVariant(entity);
		return variant == null ? type : variant;
	}

}
