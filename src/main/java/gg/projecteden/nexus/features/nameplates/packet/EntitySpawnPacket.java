package gg.projecteden.nexus.features.nameplates.packet;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.features.nameplates.packet.common.NameplatePacket;
import lombok.Getter;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntitySpawnPacket extends NameplatePacket {
	public static int ENTITY_ID_COUNTER = 32333;

	@Getter
	private final int entityId;
	private Vector location = new Vector();

	public EntitySpawnPacket(int entityId) {
		this.packet = PacketContainer.fromPacket(createPacket());
		this.entityId = entityId;
	}

	public EntitySpawnPacket at(@NotNull Player player) {
		return at(player.getLocation().clone().add(0, 1.35 + (player.getPassengers().size() * .375), 0));
	}

	public EntitySpawnPacket at(Location location) {
		this.location = location.toVector();
		this.packet = PacketContainer.fromPacket(createPacket());
		return this;
	}

	private ClientboundAddEntityPacket createPacket() {
		return new ClientboundAddEntityPacket(
			entityId,
			UUID.randomUUID(),
			location.getX(),
			location.getY(),
			location.getZ(),
			0,
			0,
			net.minecraft.world.entity.EntityType.AREA_EFFECT_CLOUD,
			0,
			Vec3.ZERO,
			0
		);
	}

}

