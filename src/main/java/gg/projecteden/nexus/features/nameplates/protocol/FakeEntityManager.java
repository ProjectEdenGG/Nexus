package gg.projecteden.nexus.features.nameplates.protocol;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntityDestroyPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntityMetadataPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntitySpawnPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.MountPacket;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nameplates.NameplateUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.utils.Env;
import kotlin.Pair;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@NoArgsConstructor
@Depends({Nameplates.class, ProtocolManager.class})
@Environments({Env.DEV, Env.TEST})
public class FakeEntityManager extends Feature {
	private final Nexus plugin = Nexus.getInstance();
	private final Map<UUID, Pair<EntitySpawnPacket, EntityMetadataPacket>> players = new HashMap<>();
	private final NameplateUserService service = new NameplateUserService();

	@Override
	public void onStart() {
		plugin.getServer().getOnlinePlayers().forEach(this::spawnFakeEntityAroundPlayer);
	}

	@Override
	public void onStop() {
		plugin.getServer().getOnlinePlayers().forEach(this::removeFakeEntityAroundPlayer);
	}

	@NotNull
	public Pair<EntitySpawnPacket, EntityMetadataPacket> getPackets(@NotNull UUID uuid) {
		return players.computeIfAbsent(uuid, $ -> {
			EntitySpawnPacket entitySpawnPacket = new EntitySpawnPacket(EntitySpawnPacket.ENTITY_ID_COUNTER++);
			EntityMetadataPacket entityMetadataPacket = new EntityMetadataPacket(entitySpawnPacket.getEntityId());
			return new Pair<>(entitySpawnPacket, entityMetadataPacket);
		});
	}

	public void spawnFakeEntityForSelf(@NotNull Player holder) {
		if (service.get(holder).isViewOwnNameplate())
			spawnFakeEntity(holder, holder);
	}

	public void spawnFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		/*
		if (!PlayerUtils.canSee(viewer, holder))
			return;

		if (holder.getGameMode() == GameMode.SPECTATOR && viewer.getGameMode() != GameMode.SPECTATOR)
			return;
		*/

		String nameplate = Nameplates.of(holder, viewer);

		final var packets = getPackets(holder.getUniqueId());
		final EntitySpawnPacket spawnPacket = packets.getFirst();
		final EntityMetadataPacket metadataPacket = packets.getSecond();

		spawnPacket.writeLocation(holder.getLocation());
		metadataPacket.setNameJson(nameplate);

		MountPacket mountPacket = new MountPacket(holder.getEntityId(), spawnPacket.getEntityId());
		Nameplates.get().getProtocolManager().sendServerPacket(viewer, spawnPacket.getPacket());
		Nameplates.get().getProtocolManager().sendServerPacket(viewer, metadataPacket.getPacket());
		Nameplates.get().getProtocolManager().sendServerPacket(viewer, mountPacket.getPacket());
	}

	public void spawnFakeEntityAroundPlayer(@NotNull Player holder) {
		getNearbyPlayers(holder)
			.filter(viewer -> viewer != holder || service.get(holder).isViewOwnNameplate())
			.forEach(viewer -> spawnFakeEntity(holder, viewer));
	}

	public void updateFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		String nameplate = Nameplates.of(holder, viewer);

		final var packets = getPackets(holder.getUniqueId());
		final EntitySpawnPacket spawnPacket = packets.getFirst();
		final EntityMetadataPacket metadataPacket = packets.getSecond();

		metadataPacket.setNameJson(nameplate);

		MountPacket mountPacket = new MountPacket(holder.getEntityId(), spawnPacket.getEntityId());
		Nameplates.get().getProtocolManager().sendServerPacket(viewer, metadataPacket.getPacket());
		Nameplates.get().getProtocolManager().sendServerPacket(viewer, mountPacket.getPacket());
	}

	public void updateFakeEntityAroundPlayer(@NotNull Player holder) {
		getNearbyPlayers(holder)
			.forEach(viewer -> updateFakeEntity(holder, viewer));
	}

	public void updateFakeEntityForSelf(@NotNull Player holder) {
		if (service.get(holder).isViewOwnNameplate())
			spawnFakeEntityForSelf(holder);
		else
			removeFakeEntityForSelf(holder);
	}

	public void removeFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		final var packets = getPackets(holder.getUniqueId());
		final EntitySpawnPacket spawnPacket = packets.getFirst();
		EntityDestroyPacket packet = new EntityDestroyPacket(spawnPacket.getEntityId());
		Nameplates.get().getProtocolManager().sendServerPacket(viewer, packet.getPacket());
	}

	public void removeFakeEntityAroundPlayer(@NotNull Player holder) {
		for (Player viewer : holder.getWorld().getPlayers())
			removeFakeEntity(holder, viewer);
	}

	public void removeFakeEntityForSelf(Player holder) {
		removeFakeEntity(holder, holder);
	}

	public void removeFromCache(@NotNull Player holder) {
		players.remove(holder.getUniqueId());
	}

	@NotNull
	private Stream<Player> getNearbyPlayers(@NotNull Player holder) {
		return PlayerUtils.getOnlinePlayers(holder, holder.getWorld()).stream()
			.filter(_player -> holder.getLocation().distanceSquared(_player.getLocation()) <= 250);
	}

}
