package gg.projecteden.nexus.features.nameplates.protocol;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.protocol.listener.PlayerSpawnPacketListener;
import gg.projecteden.nexus.features.nameplates.protocol.listener.ScoreboardTeamPacketListener;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class ProtocolManager {
	private final com.comphenix.protocol.ProtocolManager protocolManager = Nexus.getProtocolManager();
	private final PlayerSpawnPacketListener spawnPacketListener;
	private final ScoreboardTeamPacketListener scoreboardPacketListener;

	public ProtocolManager() {
		this.spawnPacketListener = new PlayerSpawnPacketListener();
		this.scoreboardPacketListener = new ScoreboardTeamPacketListener();
		this.protocolManager.addPacketListener(this.spawnPacketListener);
		this.protocolManager.addPacketListener(this.scoreboardPacketListener);
	}

	public void shutdown() {
		this.protocolManager.removePacketListener(this.spawnPacketListener);
		this.protocolManager.removePacketListener(this.scoreboardPacketListener);
	}

	public void sendServerPacket(@NotNull Player player, @NotNull PacketContainer packet) {
		try {
			System.out.println("Sending " + packet.getType().getPacketClass().getSimpleName() + " packet to " + player.getName());
			this.protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException ex) {
			Nexus.warn("Unable to send " + packet.getType().getPacketClass().getSimpleName() + " packet to " + player.getName());
			ex.printStackTrace();
		}

	}
}
