package gg.projecteden.nexus.features.nameplates.protocol.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.features.nameplates.protocol.packet.common.NameplatePacket;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntitySpawnPacket extends NameplatePacket {
	public static int ENTITY_ID_COUNTER = 32333;

	@Getter
	private final int entityId;

	public EntitySpawnPacket(int entityId) {
		super(new PacketContainer(Server.SPAWN_ENTITY));
		this.packet.getModifier().writeDefaults();
		this.packet.getEntityTypeModifier().write(0, EntityType.AREA_EFFECT_CLOUD);
		this.packet.getUUIDs().write(0, UUID.randomUUID());
		this.entityId = entityId;
		this.packet.getIntegers().write(0, entityId).write(1, 0).write(2, 0).write(3, 0).write(4, 0).write(5, 0).write(6, 0);
	}

	public EntitySpawnPacket at(@NotNull Player player) {
		return at(player.getLocation().clone().add(0, 1.35 + (player.getPassengers().size() * .375), 0));
	}

	public EntitySpawnPacket at(Location location) {
		this.packet.getDoubles()
			.write(0, location.getX())
			.write(1, location.getY())
			.write(2, location.getZ());

		return this;
	}

}

