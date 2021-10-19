package gg.projecteden.nexus.features.nameplates;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.packet.EntityDestroyPacket;
import gg.projecteden.nexus.features.nameplates.packet.EntityMetadataPacket;
import gg.projecteden.nexus.features.nameplates.packet.EntitySpawnPacket;
import gg.projecteden.nexus.features.nameplates.packet.MountPacket;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.nameplates.NameplateUserService;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.PlayerUtils.isSelf;

public class NameplateManager {
	private final NameplateUserService service = new NameplateUserService();
	private final static Map<UUID, NameplatePlayer> players = new HashMap<>();
	private final ProtocolManager protocolManager = Nexus.getProtocolManager();

	public void onStart() {
		spawnAll();
	}

	public void shutdown() {
		destroyAll();
	}

	public static NameplatePlayer get(@NotNull Player holder) {
		return get(holder.getUniqueId());
	}

	public static NameplatePlayer get(@NotNull UUID uuid) {
		return players.computeIfAbsent(uuid, $ -> new NameplatePlayer(uuid));
	}

	public void removeManagerOf(@NotNull Player holder) {
		destroy(holder);
		players.remove(holder.getUniqueId());
	}

	public void spawnAll() {
		Nameplates.debug("  spawnAll()");
		OnlinePlayers.getAll().forEach(this::spawn);
	}

	public void spawnViewable(Player player) {
		Nameplates.debug("  spawnViewable(holder=" + player.getName() + ")");
		Nameplates.getViewable(player).forEach(holder -> spawn(holder, player));
	}

	public void spawnForSelf(@NotNull Player holder) {
		Nameplates.debug("  spawnForSelf(holder=" + holder.getName() + ")");
		if (service.get(holder).isViewOwnNameplate())
			spawn(holder, holder);
	}

	public void spawn(@NotNull Player holder) {
		Nameplates.debug("  spawnFor(holder=" + holder.getName() + ")");
		Nameplates.getViewers(holder).forEach(viewer -> spawn(holder, viewer));
	}

	public void spawn(@NotNull Player holder, @NotNull Player viewer) {
		Nameplates.debug("  spawn(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		get(holder.getUniqueId()).sendSpawnPacket(viewer);
		update(holder, viewer);
	}

	public void updateAll() {
		Nameplates.debug("  updateAll()");
		for (Player player : OnlinePlayers.getAll())
			update(player);
	}

	public void updateViewable(Player player) {
		Nameplates.debug("  updateViewable()");
		Nameplates.getViewable(player).forEach(holder -> update(holder, player));
	}

	public void updateForSelf(@NotNull Player holder) {
		Nameplates.debug("  updateForSelf(holder=" + holder.getName() + ")");
		if (service.get(holder).isViewOwnNameplate())
			spawnForSelf(holder);
		else
			destroyForSelf(holder);
	}

	public void update(@NotNull Player holder) {
		Nameplates.debug("  updateFor(holder=" + holder.getName() + ")");
		Nameplates.getViewers(holder).forEach(viewer -> update(holder, viewer));
	}

	public void update(@NotNull Player holder, @NotNull Player viewer) {
		Nameplates.debug("  update(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		final var manager = get(holder.getUniqueId());
		manager.sendMetadataPacket(viewer);
		manager.sendMountPacket(viewer);
	}

	public void destroyAll() {
		Nameplates.debug("  destroyAll()");
		OnlinePlayers.getAll().forEach(this::destroy);
	}

	public void destroyViewable(Player player) {
		Nameplates.debug("  destroyViewable(player=" + player.getName() + ")");
		Nameplates.getViewable(player).forEach(holder -> destroy(holder, player));
	}

	public void destroyForSelf(Player holder) {
		Nameplates.debug("  destroyForSelf(holder=" + holder.getName() + ")");
		destroy(holder, holder);
	}

	public void destroy(@NotNull Player holder) {
		Nameplates.debug("  destroyFor(holder=" + holder.getName() + ")");
		for (Player viewer : holder.getWorld().getPlayers())
			destroy(holder, viewer);
	}

	public void destroy(@NotNull Player holder, @NotNull Player viewer) {
		Nameplates.debug("  destroy(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ")");
		get(holder).sendDestroyPacket(viewer);
	}

	public void respawn(Player holder) {
		Nameplates.debug("  respawn(holder=" + holder.getName() + ")");
		destroy(holder);
		Tasks.waitAsync(2, () -> {
			spawn(holder);
			spawnViewable(holder);
		});
	}

	@Data
	public static class NameplatePlayer implements PlayerOwnedObject {
		private final UUID uuid;
		private final int entityId;
		private final EntitySpawnPacket spawnPacket;
		private final EntityMetadataPacket metadataPacket;

		private final Set<UUID> viewing = new HashSet<>();
		private final Set<UUID> viewedBy = new HashSet<>();

		NameplatePlayer(UUID uuid) {
			Nameplates.debug("Now managing " + Name.of(uuid));
			this.uuid = uuid;
			this.entityId = EntitySpawnPacket.ENTITY_ID_COUNTER++;
			this.spawnPacket = new EntitySpawnPacket(entityId);
			this.metadataPacket = new EntityMetadataPacket(entityId);
		}

		public boolean isViewing(Player player) {
			return viewing.contains(player.getUniqueId());
		}

		private boolean ignore(Player viewer) {
			if (ResourcePack.isReloading())
				return true;
			if (!isOnline())
				return true;

			final Player player = getOnlinePlayer();
			if (player.isDead())
				return true;
			if (player.isSneaking())
				return true;
			if (DisguiseAPI.isDisguised(player))
				return true;
			if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
				return true;

			if (isSelf(this, viewer))
				if (!new NameplateUserService().get(this).isViewOwnNameplate())
					return true;

			return false;
		}

		public void sendSpawnPacket(Player viewer) {
			if (ignore(viewer))
				return;

			spawnPacket.at(getOnlinePlayer()).send(viewer);

			viewedBy.add(viewer.getUniqueId());
			NameplateManager.get(viewer).getViewing().add(uuid);
		}

		public void sendMetadataPacket(Player viewer) {
			if (ignore(viewer))
				return;

			metadataPacket.setNameJson(Nameplates.of(getOnlinePlayer(), viewer)).send(viewer);
		}

		public void sendMountPacket(Player viewer) {
			if (ignore(viewer))
				return;

			new MountPacket(getOnlinePlayer().getEntityId(), entityId).send(viewer);
		}

		public void sendDestroyPacket(Player viewer) {
			new EntityDestroyPacket(entityId).send(viewer);

			viewedBy.remove(viewer.getUniqueId());
			NameplateManager.get(viewer).getViewing().remove(uuid);
		}
	}

	public void sendServerPacket(@NotNull Player player, @NotNull PacketContainer packet) {
		try {
			if (!player.isOnline())
				return;

			Nameplates.debug("    Sending " + packet.getType().getPacketClass().getSimpleName() + " packet to " + player.getName());
			this.protocolManager.sendServerPacket(player, packet);
		} catch (IllegalArgumentException ignore) {
		} catch (InvocationTargetException ex) {
			Nexus.warn("      Unable to send " + packet.getType().getPacketClass().getSimpleName() + " packet to " + player.getName());
			ex.printStackTrace();
		}
	}

}
