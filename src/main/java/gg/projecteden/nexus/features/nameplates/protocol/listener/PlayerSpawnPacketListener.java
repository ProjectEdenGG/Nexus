package gg.projecteden.nexus.features.nameplates.protocol.listener;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketPostAdapter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import org.jetbrains.annotations.NotNull;

public class PlayerSpawnPacketListener extends PacketAdapter {

	public PlayerSpawnPacketListener() {
		super(Nexus.getInstance(), ListenerPriority.HIGHEST, Server.NAMED_ENTITY_SPAWN);
	}

	public void onPacketSending(@NotNull PacketEvent event) {
		if (event.isCancelled())
			return;

		event.getNetworkMarker().addPostListener(new PacketPostAdapter(this.plugin) {
			public void onPostEvent(PacketEvent event) {
				Nameplates.get().getNameplateManager().spawn(event.getPlayer(), event.getPlayer());
			}
		});
	}

	/* TODO
	public void onPacketSending(@NotNull PacketEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		int entityId = event.getPacket().getIntegers().read(0);

		final Optional<Entity> entity = player.getWorld().getEntities().stream()
			.filter(_entity -> _entity.getEntityId() == entityId)
			.findFirst();

		if (entity.isEmpty())
			return;

		event.getNetworkMarker().addPostListener(new PacketPostAdapter(this.plugin) {
			public void onPostEvent(PacketEvent event) {
				entity.ifPresent((playerEntity) ->
					fakeEntityManager.spawnFakeEntity((Player) playerEntity, event.getPlayer()));
			}
		});
	}
	*/
}
