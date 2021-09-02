package gg.projecteden.nexus.features.nameplates.protocol.listener;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.Nameplates;

import java.util.Optional;

public class ScoreboardTeamPacketListener extends PacketAdapter {
	private static final int CREATE = 0;
	private static final int UPDATE = 2;

	public ScoreboardTeamPacketListener() {
		super(Nexus.getInstance(), ListenerPriority.HIGHEST, Server.SCOREBOARD_TEAM);
	}

	public void onPacketSending(PacketEvent event) {
		if (event.isCancelled())
			return;

		try {
			event.getPlayer().getUniqueId();
		} catch (RuntimeException ex) {
			if (event.getPacket().getStrings().read(0).equals(Nameplates.get().getTeamName()))
				event.setCancelled(true);

			return;
		}

		Integer mode = event.getPacket().getIntegers().read(0);
		if (mode == CREATE || mode == UPDATE) {
			Optional<InternalStructure> nameTagVisibility = event.getPacket().getOptionalStructures().read(0);
			if (nameTagVisibility.isPresent()) {
				InternalStructure structure = nameTagVisibility.get();
				structure.getStrings().write(0, "never");
			}
		}
	}
}
