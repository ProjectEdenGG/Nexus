package gg.projecteden.nexus.framework.interfaces;

import gg.projecteden.api.interfaces.DatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * A mongo database object owned by an entity
 */
public interface EntityOwnedObject extends DatabaseObject {

	@Override
	default @NotNull UUID getUniqueId() {
		return getUuid();
	}

	@Nullable
	default Entity getEntity() {
		return Bukkit.getEntity(getUuid());
	}

	default @NotNull Entity getLoadedEntity() {
		return Objects.requireNonNull(getEntity());
	}

	default net.minecraft.world.entity.Entity getNMSEntity() {
		return ((CraftEntity) getLoadedEntity()).getHandle();
	}

	default @NotNull EntityType getEntityType() {
		return getLoadedEntity().getType();
	}

	default @NotNull World getWorld() {
		return getLoadedEntity().getWorld();
	}

	default @NotNull Location getLocation() {
		return getLoadedEntity().getLocation();
	}

	default boolean isLoaded() {
		final Entity entity = getEntity();
		return entity != null && entity.isValid();
	}

}
