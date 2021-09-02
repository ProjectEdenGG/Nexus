package gg.projecteden.nexus.features.nameplates.protocol;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.nameplates.protocol.listener.PlayerSpawnPacketListener;
import gg.projecteden.nexus.features.nameplates.protocol.listener.ScoreboardTeamPacketListener;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.utils.Env;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

@NoArgsConstructor
@Depends(Nameplates.class)
@Environments({Env.DEV, Env.TEST})
public class ProtocolManager extends Feature {
	private final com.comphenix.protocol.ProtocolManager protocolManager = Nexus.getProtocolManager();
	private PlayerSpawnPacketListener spawnPacketListener;
	private ScoreboardTeamPacketListener scoreboardPacketListener;

	@Override
	public void onStart() {
		System.out.println("ProtocolManager#onStart");
		this.spawnPacketListener = new PlayerSpawnPacketListener();
		this.scoreboardPacketListener = new ScoreboardTeamPacketListener();
		this.protocolManager.addPacketListener(this.spawnPacketListener);
		this.protocolManager.addPacketListener(this.scoreboardPacketListener);
	}

	@Override
	public void onStop() {
		System.out.println("ProtocolManager#onStop");
		this.protocolManager.removePacketListener(this.spawnPacketListener);
		this.protocolManager.removePacketListener(this.scoreboardPacketListener);
	}

	public void sendServerPacket(@NotNull Player player, @NotNull PacketContainer packet) {
		try {
			this.protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException ex) {
			Nexus.warn("Unable to send " + packet.getType().getPacketClass().getSimpleName() + " packet to " + player.getName());
			ex.printStackTrace();
		}

	}
}
