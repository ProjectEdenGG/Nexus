package me.pugabyte.nexus.features.mobheads.common;

import org.bukkit.entity.EntityType;

public interface MobHead {

	String name();

	EntityType getEntityType();

	default MobHeadVariant getVariant() {
		return null;
	}

}
