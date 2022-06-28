package gg.projecteden.nexus.features.clientside.models;

import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.clientside.ClientSideUserService;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.parchment.HasPlayer;
import lombok.AllArgsConstructor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public interface IClientSideEntity<T extends IClientSideEntity<?, E>, E extends Entity> extends DatabaseObject {

	@Override
	default UUID getUuid() {
		return uuid();
	}

	UUID uuid();

	Location location();

	E entity();

	T build();

	ClientSideEntityType getType();

	@NotNull
	List<Packet<ClientGamePacketListener>> getPackets();

	default T send(HasPlayer player) {
		if (entity() == null)
			build();

		PacketUtils.sendPacket(player, getPackets());
		new ClientSideUserService().edit(player.getPlayer(), user ->
			user.getVisibleEntities().add(entity().getId()));
		return (T) this;
	}

	default T send(List<HasPlayer> players) {
		players.forEach(this::send);
		return (T) this;
	}

	default T destroy(HasPlayer player) {
		PacketUtils.entityDestroy(player, entity().getId());
		return (T) this;
	}

	@AllArgsConstructor
	enum ClientSideEntityType {
		ITEM_FRAME(itemFrame -> { return ClientSideItemFrame.of((ItemFrame) itemFrame); }),
		ARMOR_STAND(armorStand -> { return ClientSideArmorStand.of((ArmorStand) armorStand); }),
		;

		private final Function<org.bukkit.entity.Entity, ? extends IClientSideEntity<?, ?>> function;

		public static IClientSideEntity<?, ?> of(org.bukkit.entity.Entity entity) {
			try {
				return valueOf(entity.getType().name()).function.apply(entity);
			} catch (IllegalArgumentException ex) {
				throw new InvalidInputException("Unsupported entity type &e" + camelCase(entity.getType()));
			}
		}

	}

}
