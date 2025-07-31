package gg.projecteden.nexus.features.clientside.models;

import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.parchment.HasLocation;
import lombok.AllArgsConstructor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public interface IClientSideEntity<
	NexusEntity extends IClientSideEntity<?, MinecraftEntity, BukkitEntity>,
	MinecraftEntity extends Entity,
	BukkitEntity extends org.bukkit.entity.Entity
> extends DatabaseObject, HasLocation {

	@Override
	default UUID getUuid() {
		return uuid();
	}

	int id();

	UUID uuid();

	UUID entityUuid();

	Location location();

	default Location getLocation() {
		return location();
	}

	MinecraftEntity entity();

	NexusEntity build();

	boolean isHidden();

	void setHidden(boolean hidden);

	default void hide() {
		setHidden(true);
	}

	default void show() {
		setHidden(false);
	}

	default BukkitEntity spawn() {
		build();
		((CraftWorld) location().getWorld()).addEntity(entity(), SpawnReason.CUSTOM);
		return (BukkitEntity) Bukkit.getEntity(entity().getUUID());
	}

	ClientSideEntityType getType();

	@NotNull
	List<Packet<ClientGamePacketListener>> getSpawnPackets(Player player);

	@NotNull
	List<Packet<ClientGamePacketListener>> getUpdatePackets(Player player);

	@Deprecated
	// TODO Remove
	default NexusEntity send(Player player) {
		ClientSideUser.of(player).show(this);
		return (NexusEntity) this;
	}

	@AllArgsConstructor
	enum ClientSideEntityType {
		ARMOR_STAND(armorStand -> ClientSideArmorStand.of((ArmorStand) armorStand)),
		ITEM_FRAME(itemFrame -> ClientSideItemFrame.of((ItemFrame) itemFrame)),
		PAINTING(painting -> ClientSidePainting.of((Painting) painting)),
		;

		private final Function<org.bukkit.entity.Entity, ? extends IClientSideEntity<?, ?, ?>> function;

		public static ClientSideEntityType of(EntityType entityType) {
			if (!isSupportedType(entityType))
				throw new InvalidInputException("Unsupported entity type &e" + StringUtils.camelCase(entityType));

			return valueOf(entityType.name());
		}

		public static IClientSideEntity<?, ?, ?> createFrom(org.bukkit.entity.Entity entity) {
			return of(entity.getType()).function.apply(entity);
		}

		public static boolean isSupportedType(EntityType entityType) {
			try {
				valueOf(entityType.name());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}

	}

}
