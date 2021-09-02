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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@Depends({Nameplates.class, ProtocolManager.class})
@Environments({Env.DEV, Env.TEST})
public class FakeEntityManager extends Feature {
	private final Nexus plugin = Nexus.getInstance();
	private final Map<UUID, Pair<EntitySpawnPacket, EntityMetadataPacket>> playerMap = new HashMap<>();
	private final NameplateUserService service = new NameplateUserService();

	@Override
	public void onStart() {
		System.out.println("FakeEntityManager#onStart");
		plugin.getServer().getOnlinePlayers().forEach(this::addPlayer);
		plugin.getServer().getOnlinePlayers().forEach(this::spawnFakeEntityAroundPlayer);
	}

	@Override
	public void onStop() {
		System.out.println("FakeEntityManager#onStop");
		plugin.getServer().getOnlinePlayers().forEach(this::removeFakeEntityAroundPlayer);
	}

	private boolean isManaging(@NotNull Player player) {
		return isManaging(player.getUniqueId());
	}

	private boolean isManaging(@NotNull UUID uuid) {
		return playerMap.containsKey(uuid);
	}

	public void addPlayer(@NotNull UUID uuid, @NotNull Location location) {
		if (isManaging(uuid)) {
			playerMap.get(uuid).getFirst().writeLocation(location);
		} else {
			EntitySpawnPacket entitySpawnPacket = new EntitySpawnPacket(EntitySpawnPacket.ENTITY_ID_COUNTER++);
			EntityMetadataPacket entityMetadataPacket = new EntityMetadataPacket(entitySpawnPacket.getEntityId());
			entitySpawnPacket.writeLocation(location);
			playerMap.put(uuid, new Pair<>(entitySpawnPacket, entityMetadataPacket));
		}

	}

	public void addPlayer(@NotNull Player player) {
		addPlayer(player.getUniqueId(), player.getLocation());
	}

	@NotNull
	public Optional<Pair<EntitySpawnPacket, EntityMetadataPacket>> getPlayer(@NotNull UUID uuid) {
		return Optional.ofNullable(playerMap.getOrDefault(uuid, null));
	}

	public void spawnFakeEntityForSelf(@NotNull Player player) {
		if (service.get(player).isViewOwnNameplate())
			spawnFakeEntity(player, player);
	}

	public void spawnFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		if (!isManaging(holder))
			return;

		if (!PlayerUtils.canSee(viewer, holder))
			return;

		if (holder.getGameMode() == GameMode.SPECTATOR && viewer.getGameMode() != GameMode.SPECTATOR)
			return;

		getPlayer(holder.getUniqueId()).ifPresent(packets -> {
			String nameplate = Nameplates.of(holder, viewer);

			final EntitySpawnPacket spawnPacket = packets.getFirst();
			final EntityMetadataPacket metadataPacket = packets.getSecond();

			spawnPacket.writeLocation(holder.getLocation());
			metadataPacket.setNameJson(nameplate);

			MountPacket mountPacket = new MountPacket(holder.getEntityId(), spawnPacket.getEntityId());
			Nameplates.get().getProtocolManager().sendServerPacket(viewer, spawnPacket.getPacket());
			Nameplates.get().getProtocolManager().sendServerPacket(viewer, metadataPacket.getPacket());
			Nameplates.get().getProtocolManager().sendServerPacket(viewer, mountPacket.getPacket());
		});
	}

	public void spawnFakeEntityAroundPlayer(@NotNull Player player) {
		PlayerUtils.getOnlinePlayers(player, player.getWorld()).stream()
			.filter(_player -> player.getLocation().distanceSquared(_player.getLocation()) <= 250.0D)
			.filter(_player -> service.get(player).isViewOwnNameplate() || _player != player)
			.forEach(_player -> spawnFakeEntity(player, _player));
	}

	public void spawnFakeEntitiesToPlayer(@NotNull Player player) {
		PlayerUtils.getOnlinePlayers(player, player.getWorld()).stream()
			.filter(_player -> player.getLocation().distanceSquared(_player.getLocation()) <= 250.0D)
			.filter(_player -> service.get(player).isViewOwnNameplate() || _player != player)
			.forEach(_player -> spawnFakeEntity(player, _player));
	}

	public void updateFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		if (!isManaging(holder))
			return;

		getPlayer(holder.getUniqueId()).ifPresent(packets -> {
			String nameplate = Nameplates.of(holder, viewer);

			final EntitySpawnPacket spawnPacket = packets.getFirst();
			final EntityMetadataPacket metadataPacket = packets.getSecond();

			metadataPacket.setNameJson(nameplate);

			MountPacket mountPacket = new MountPacket(holder.getEntityId(), spawnPacket.getEntityId());
			Nameplates.get().getProtocolManager().sendServerPacket(viewer, metadataPacket.getPacket());
			Nameplates.get().getProtocolManager().sendServerPacket(viewer, mountPacket.getPacket());
		});
	}

	public void updateFakeEntityAroundPlayer(@NotNull Player player) {
		PlayerUtils.getOnlinePlayers(player, player.getWorld()).parallelStream()
			.filter(_player -> player.getLocation().distanceSquared(_player.getLocation()) <= 250.0D)
			.forEach(_player -> updateFakeEntity(player, _player));
	}

	public void updateFakeEntityForSelf(@NotNull Player player) {
		if (service.get(player).isViewOwnNameplate())
			spawnFakeEntityForSelf(player);
		else
			removeFakeEntityForSelf(player);
	}

	public void removeFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		if (!isManaging(holder))
			return;

		getPlayer(holder.getUniqueId()).ifPresent(packets -> {
			EntityDestroyPacket packet = new EntityDestroyPacket(packets.getFirst().getEntityId());
			Nameplates.get().getProtocolManager().sendServerPacket(viewer, packet.getPacket());
		});
	}

	public void removeFakeEntityAroundPlayer(@NotNull Player player) {
		player.getWorld().getPlayers().parallelStream().forEach(viewer ->
			removeFakeEntity(player, viewer));
	}

	public void removeFakeEntityForSelf(Player player) {
		removeFakeEntity(player, player);
	}

	public void removeFromCache(@NotNull Player player) {
		playerMap.remove(player.getUniqueId());
	}

}
