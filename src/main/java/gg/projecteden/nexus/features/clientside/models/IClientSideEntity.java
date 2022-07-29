package gg.projecteden.nexus.features.clientside.models;

import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.utils.PacketUtils;
import lombok.AllArgsConstructor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

@SuppressWarnings("unchecked")
public interface IClientSideEntity<
	NexusEntity extends IClientSideEntity<?, MinecraftEntity, BukkitEntity>,
	MinecraftEntity extends Entity,
	BukkitEntity extends org.bukkit.entity.Entity
> extends DatabaseObject {

	@Override
	default UUID getUuid() {
		return uuid();
	}

	default int id() {
		return entity().getId();
	}

	UUID uuid();

	Location location();

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
	List<Packet<ClientGamePacketListener>> getSpawnPackets();

	@NotNull
	List<Packet<ClientGamePacketListener>> getUpdatePackets();

	default NexusEntity send(Player player) {
		ClientSideUser.of(player).send(this);
		return (NexusEntity) this;
	}

	default NexusEntity send(ClientSideUser user) {
		user.send(this);
		return (NexusEntity) this;
	}

	default NexusEntity send(List<ClientSideUser> users) {
		users.forEach(this::send);
		return (NexusEntity) this;
	}

	default NexusEntity update(ClientSideUser player) {
		PacketUtils.sendPacket(player.getOnlinePlayer(), getUpdatePackets());
		return (NexusEntity) this;
	}

	default NexusEntity update(List<ClientSideUser> players) {
		players.forEach(this::update);
		return (NexusEntity) this;
	}

	default NexusEntity destroy(ClientSideUser player) {
		PacketUtils.entityDestroy(player.getOnlinePlayer(), id());
		return (NexusEntity) this;
	}

	@AllArgsConstructor
	enum ClientSideEntityType {
		ITEM_FRAME(itemFrame -> { return ClientSideItemFrame.of((ItemFrame) itemFrame); }),
		ARMOR_STAND(armorStand -> { return ClientSideArmorStand.of((ArmorStand) armorStand); }),
		;

		private final Function<org.bukkit.entity.Entity, ? extends IClientSideEntity<?, ?, ?>> function;

		public static IClientSideEntity<?, ?, ?> of(org.bukkit.entity.Entity entity) {
			try {
				return valueOf(entity.getType().name()).function.apply(entity);
			} catch (IllegalArgumentException ex) {
				throw new InvalidInputException("Unsupported entity type &e" + camelCase(entity.getType()));
			}
		}

	}

}
