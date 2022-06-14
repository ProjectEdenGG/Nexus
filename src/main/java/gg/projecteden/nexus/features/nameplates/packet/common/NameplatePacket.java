package gg.projecteden.nexus.features.nameplates.packet.common;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class NameplatePacket {
	protected PacketContainer packet;

	public void send(Player viewer) {
		Nameplates.get().getNameplateManager().sendServerPacket(viewer, packet);
	}

}
