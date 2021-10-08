package gg.projecteden.nexus.features.nameplates.protocol;

import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntityDestroyPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntityMetadataPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntitySpawnPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.MountPacket;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.nameplates.NameplateUserService;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeEntityManager {
	private final NameplateUserService service = new NameplateUserService();
	private final Map<UUID, NameplatePlayer> players = new HashMap<>();

	public void onStart() {
		OnlinePlayers.getAll().forEach(this::spawnFakeEntityAroundPlayer);
	}

	public void shutdown() {
		OnlinePlayers.getAll().forEach(this::removeFakeEntityAroundPlayer);
	}

	private NameplatePlayer managerOf(@NotNull Player holder) {
		return managerOf(holder.getUniqueId());
	}

	public NameplatePlayer managerOf(@NotNull UUID uuid) {
		return players.computeIfAbsent(uuid, $ -> new NameplatePlayer(uuid));
	}

	public void removeManagerOf(@NotNull Player holder) {
		players.remove(holder.getUniqueId());
	}

	public void spawnFakeEntityForSelf(@NotNull Player holder) {
		System.out.println("spawnFakeEntityForSelf(holder=" + holder.getName() + ")");
		if (service.get(holder).isViewOwnNameplate())
			spawnFakeEntity(holder, holder);
	}

	public void spawnFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		System.out.println("spawnFakeEntity(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");

		if (!PlayerUtils.canSee(viewer, holder)) {
			System.out.println(" " + viewer.getName() + " cant see " + holder.getName());
			return;
		}

		if (holder.getGameMode() == GameMode.SPECTATOR && viewer.getGameMode() != GameMode.SPECTATOR) {
			System.out.println(" " + viewer.getName() + " cant see " + holder.getName());
			return;
		}

		managerOf(holder.getUniqueId()).getSpawnPacket().send(viewer);
		updateFakeEntity(holder, viewer);
	}

	public void spawnFakeEntityAroundPlayer(@NotNull Player holder) {
		System.out.println("spawnFakeEntityAroundPlayer(holder=" + holder.getName() + ")");
		getNearbyPlayers(holder)
			.filter(viewer -> viewer != holder || service.get(holder).isViewOwnNameplate())
			.forEach(viewer -> spawnFakeEntity(holder, viewer));
	}

	public void updateFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		System.out.println("updateFakeEntity(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		final var manager = managerOf(holder.getUniqueId());
		manager.getMetadataPacket(viewer).send(viewer);
		manager.getMountPacket().send(viewer);
	}

	public void updateFakeEntityAroundPlayer(@NotNull Player holder) {
		System.out.println("updateFakeEntityAroundPlayer(holder=" + holder.getName() + ")");
		getNearbyPlayers(holder).forEach(viewer -> updateFakeEntity(holder, viewer));
	}

	public void updateFakeEntityForSelf(@NotNull Player holder) {
		System.out.println("updateFakeEntityForSelf(holder=" + holder.getName() + ")");
		if (service.get(holder).isViewOwnNameplate())
			spawnFakeEntityForSelf(holder);
		else
			removeFakeEntityForSelf(holder);
	}

	public void removeFakeEntity(@NotNull Player holder, @NotNull Player viewer) {
		System.out.println("removeFakeEntity(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		managerOf(holder).getDestroyPacket().send(viewer);
	}

	public void removeFakeEntityAroundPlayer(@NotNull Player holder) {
		System.out.println("removeFakeEntityAroundPlayer(holder=" + holder.getName() + ")");
		for (Player viewer : holder.getWorld().getPlayers())
			removeFakeEntity(holder, viewer);
	}

	public void removeFakeEntityForSelf(Player holder) {
		System.out.println("removeFakeEntityForSelf(holder=" + holder.getName() + ")");
		removeFakeEntity(holder, holder);
	}

	@NotNull
	private OnlinePlayers getNearbyPlayers(@NotNull Player holder) {
		return OnlinePlayers.where().viewer(holder).world(holder.getWorld()).radius(250);
	}

	@Data
	private static class NameplatePlayer implements PlayerOwnedObject {
		private final UUID uuid;
		private final int entityId;
		private final EntitySpawnPacket spawnPacket;
		private final EntityMetadataPacket metadataPacket;

		NameplatePlayer(UUID uuid) {
			System.out.println("Now managing " + Name.of(uuid));
			this.uuid = uuid;
			this.entityId = EntitySpawnPacket.ENTITY_ID_COUNTER++;
			this.spawnPacket = new EntitySpawnPacket(entityId);
			this.metadataPacket = new EntityMetadataPacket(entityId);
		}

		public EntitySpawnPacket getSpawnPacket() {
			spawnPacket.writeLocation(getOnlinePlayer().getLocation());
			return spawnPacket;
		}

		public EntityMetadataPacket getMetadataPacket(@NotNull Player viewer) {
			metadataPacket.setNameJson(Nameplates.of(getOnlinePlayer(), viewer));
			return metadataPacket;
		}

		public MountPacket getMountPacket() {
			return new MountPacket(getOnlinePlayer().getEntityId(), entityId);
		}

		public EntityDestroyPacket getDestroyPacket() {
			return new EntityDestroyPacket(entityId);
		}
	}

}
