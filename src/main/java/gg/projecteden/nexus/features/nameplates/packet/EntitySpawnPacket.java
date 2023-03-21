package gg.projecteden.nexus.features.nameplates.packet;

import gg.projecteden.nexus.features.nameplates.packet.common.NameplatePacket;
import lombok.Data;
import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
public class EntitySpawnPacket extends NameplatePacket {
	public static int ENTITY_ID_COUNTER = 32333;

	@Getter
	private final int entityId;
	private Vector location = new Vector();

	public EntitySpawnPacket at(@NotNull Player player) {
		return at(player.getLocation().clone().add(0, 1.35 + (player.getPassengers().size() * .375), 0));
	}

	public EntitySpawnPacket at(Location location) {
		this.location = location.toVector();
		return this;
	}

	@Override
	protected Packet<ClientGamePacketListener> build() {
		return new ClientboundAddEntityPacket(
			entityId,
			UUID.randomUUID(),
			location.getX(),
			location.getY(),
			location.getZ(),
			0,
			0,
			EntityType.TEXT_DISPLAY,
			0,
			Vec3.ZERO,
			0
		);
	}

}

