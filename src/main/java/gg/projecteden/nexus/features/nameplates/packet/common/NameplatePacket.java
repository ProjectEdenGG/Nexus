package gg.projecteden.nexus.features.nameplates.packet.common;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public abstract class NameplatePacket {
	protected final PacketContainer packet;

	public PacketContainer getPacket() {
		return this.packet;
	}

	public void send(Player viewer) {
		Nameplates.get().getNameplateManager().sendServerPacket(viewer, packet);
	}

}
