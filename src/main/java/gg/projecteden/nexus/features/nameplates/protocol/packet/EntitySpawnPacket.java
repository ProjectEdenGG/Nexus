package gg.projecteden.nexus.features.nameplates.protocol.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntitySpawnPacket {
	public static int ENTITY_ID_COUNTER = 32333;
	private final PacketContainer packet;
	private final int entityId;

	public EntitySpawnPacket(int var1) {
		this.packet = new PacketContainer(Server.SPAWN_ENTITY);
		this.packet.getModifier().writeDefaults();
		this.packet.getEntityTypeModifier().write(0, EntityType.AREA_EFFECT_CLOUD);
		this.packet.getUUIDs().write(0, UUID.randomUUID());
		this.entityId = var1;
		this.packet.getIntegers().write(0, var1).write(1, 0).write(2, 0).write(3, 0).write(4, 0).write(5, 0).write(6, 0);
	}

	public void writeLocation(@NotNull Location var1) {
		this.packet.getDoubles().write(0, var1.getX()).write(1, var1.getY()).write(2, var1.getZ());
	}

	public PacketContainer getPacket() {
		return this.packet;
	}

	public int getEntityId() {
		return this.entityId;
	}
}

