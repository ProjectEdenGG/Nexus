package gg.projecteden.nexus.utils.nms.packet;

import lombok.Data;
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
public class EntitySpawnPacket extends EdenPacket {

	private final int entityId;
	private final EntityType<?> entityType;
	private Vector location = new Vector();


	public EntitySpawnPacket at(@NotNull Player player) {
		return at(player.getLocation());
	}

	public EntitySpawnPacket at(@NotNull Player player, double verticalOffset) {
		return at(player.getLocation().clone().add(0, verticalOffset, 0));
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
			entityType,
			0,
			Vec3.ZERO,
			0
		);
	}
}
