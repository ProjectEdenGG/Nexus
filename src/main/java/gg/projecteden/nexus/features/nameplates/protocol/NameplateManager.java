package gg.projecteden.nexus.features.nameplates.protocol;

import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntityDestroyPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntityMetadataPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.EntitySpawnPacket;
import gg.projecteden.nexus.features.nameplates.protocol.packet.MountPacket;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.nameplates.NameplateUserService;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NameplateManager {
	private final NameplateUserService service = new NameplateUserService();
	private final Map<UUID, NameplatePlayer> players = new HashMap<>();

	public void onStart() {
		spawnAll();
	}

	public void shutdown() {
		destroyAll();
	}

	private NameplatePlayer managerOf(@NotNull Player holder) {
		return managerOf(holder.getUniqueId());
	}

	public NameplatePlayer managerOf(@NotNull UUID uuid) {
		return players.computeIfAbsent(uuid, $ -> new NameplatePlayer(uuid));
	}

	public void removeManagerOf(@NotNull Player holder) {
		destroy(holder);
		players.remove(holder.getUniqueId());
	}

	private void spawnAll() {
		OnlinePlayers.getAll().forEach(this::spawn);
	}

	public void spawnViewable(Player player) {
		Nameplates.getViewable(player).forEach(holder -> spawn(holder, player));
	}

	public void spawnForSelf(@NotNull Player holder) {
		System.out.println("  spawnForSelf(holder=" + holder.getName() + ")");
		if (service.get(holder).isViewOwnNameplate())
			spawn(holder, holder);
	}

	public void spawn(@NotNull Player holder) {
		System.out.println("  spawnFor(holder=" + holder.getName() + ")");
		Nameplates.getViewers(holder)
			.filter(viewer -> viewer != holder || service.get(holder).isViewOwnNameplate())
			.forEach(viewer -> spawn(holder, viewer));
	}

	public void spawn(@NotNull Player holder, @NotNull Player viewer) {
		System.out.println("  spawn(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		managerOf(holder.getUniqueId()).getSpawnPacket().send(viewer);
		update(holder, viewer);
	}

	public void updateViewable(Player player) {
		Nameplates.getViewable(player).forEach(holder -> update(holder, player));
	}

	public void updateForSelf(@NotNull Player holder) {
		System.out.println("  updateForSelf(holder=" + holder.getName() + ")");
		if (service.get(holder).isViewOwnNameplate())
			spawnForSelf(holder);
		else
			destroyForSelf(holder);
	}

	public void update(@NotNull Player holder) {
		System.out.println("  updateFor(holder=" + holder.getName() + ")");
		Nameplates.getViewers(holder).forEach(viewer -> update(holder, viewer));
	}

	public void update(@NotNull Player holder, @NotNull Player viewer) {
		System.out.println("  update(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		final var manager = managerOf(holder.getUniqueId());
		manager.getMetadataPacket(viewer).send(viewer);
		manager.getMountPacket().send(viewer);
	}

	private void destroyAll() {
		OnlinePlayers.getAll().forEach(this::destroy);
	}

	public void destroyViewable(Player player) {
		Nameplates.getViewable(player).forEach(holder -> destroy(holder, player));
	}

	public void destroyForSelf(Player holder) {
		System.out.println("  destroyForSelf(holder=" + holder.getName() + ")");
		destroy(holder, holder);
	}

	public void destroy(@NotNull Player holder) {
		System.out.println("  destroyFor(holder=" + holder.getName() + ")");
		for (Player viewer : holder.getWorld().getPlayers())
			destroy(holder, viewer);
	}

	public void destroy(@NotNull Player holder, @NotNull Player viewer) {
		System.out.println("  destroy(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		managerOf(holder).getDestroyPacket().send(viewer);
	}

	public void respawn(Player holder) {
		destroy(holder);
		Tasks.waitAsync(2, () -> {
			spawn(holder);
			spawnViewable(holder);
		});
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
