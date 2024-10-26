package gg.projecteden.nexus.features.nameplates;

import com.comphenix.protocol.ProtocolManager;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.nameplates.packets.NameplateMetadataPacket;
import gg.projecteden.nexus.features.nameplates.packets.NameplateSpawnPacket;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.nameplates.NameplateUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.packet.EntityDestroyPacket;
import gg.projecteden.nexus.utils.nms.packet.EntityPassengersPacket;
import gg.projecteden.nexus.utils.nms.packet.EntitySneakPacket;
import lombok.Data;
import me.libraryaddict.disguise.DisguiseAPI;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.PlayerUtils.isSelf;

public class NameplateManager {
	private final NameplateUserService service = new NameplateUserService();
	private final static Map<UUID, NameplatePlayer> players = new ConcurrentHashMap<>();
	private final ProtocolManager protocolManager = Nexus.getProtocolManager();

	public void onStart() {
		spawnAll();
	}

	public void shutdown() {
		destroyAll();
		players.clear();
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
		Nameplates.debug("  update(holder=" + holder.getName() + ")");
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

	public void sneak(@NotNull Player holder, boolean sneaking) {
		Nameplates.debug("  sneak(holder=" + holder.getName() + ", sneaking=" + sneaking + ")");
		Nameplates.getViewers(holder).forEach(viewer -> sneak(holder, viewer, sneaking));
	}

	public void sneak(@NotNull Player holder, @NotNull Player viewer, boolean sneaking) {
		Nameplates.debug("  sneak(holder=" + holder.getName() + ", viewer=" + viewer.getName() + ", sneaking=" + sneaking + ")");
		get(holder).sendSneakPacket(viewer, sneaking);
	}

	@Data
	public static class NameplatePlayer implements PlayerOwnedObject {
		private final UUID uuid;
		private final int entityId;

		private final Set<UUID> viewing = new HashSet<>();
		private final Set<UUID> viewedBy = new HashSet<>();

		NameplatePlayer(UUID uuid) {
			Nameplates.debug("Now managing " + Nickname.of(uuid));
			this.uuid = uuid;
			this.entityId = NameplateSpawnPacket.ENTITY_ID_COUNTER++;
		}

		public boolean isViewing(Player player) {
			return viewing.contains(player.getUniqueId());
		}

		private boolean ignore(Player viewer) {
			if (ResourcePack.isReloading())
				return true;
			if (!isOnline())
				return true;
			if (!viewer.isOnline())
				return true;
			if (CitizensUtils.isNPC(viewer))
				return true;

			final Player player = getOnlinePlayer();
			if (CitizensUtils.isNPC(player))
				return true;
			if (player.isDead())
				return true;
			if (DisguiseAPI.isDisguised(player))
				return true;
			if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
				return true;
			if (!player.getPassengers().isEmpty())
				return true;
			if (!player.getWorld().equals(viewer.getWorld()))
				return true;
			if (distance(player, viewer).gte(100))
				return true;
			final Minigamer minigamer = Minigamer.of(player);
			if (minigamer.isPlaying() && !minigamer.getMatch().getMechanic().shouldShowNameplate(minigamer, Minigamer.of(viewer)))
				return true;

			if (!new NameplateUserService().get(viewer).isViewNameplates())
				return true;

			if (isSelf(this, viewer))
				if (!new NameplateUserService().get(this).isViewOwnNameplate())
					return true;

			return false;
		}

		public void sendSpawnPacket(Player viewer) {
			if (ignore(viewer)) {
				sendDestroyPacket(viewer);
				return;
			}

			new NameplateSpawnPacket(entityId).at(getOnlinePlayer()).send(viewer);

			viewedBy.add(viewer.getUniqueId());
			NameplateManager.get(viewer).getViewing().add(uuid);
		}

		public void sendMetadataPacket(Player viewer) {
			if (ignore(viewer)) {
				sendDestroyPacket(viewer);
				return;
			}

			new NameplateMetadataPacket(entityId)
				.setName(GsonComponentSerializer.gson().serialize(Nameplates.of(getOnlinePlayer(), viewer).build()))
				.setSeeThroughWalls(getOnlinePlayer().isSneaking() || Minigamer.of(getOnlinePlayer()).isPlaying())
				.send(viewer);
		}

		public void sendMountPacket(Player viewer) {
			if (ignore(viewer)) {
				sendDestroyPacket(viewer);
				return;
			}

			new EntityPassengersPacket(getOnlinePlayer().getEntityId(), entityId).send(viewer);
		}

		public void sendDestroyPacket(Player viewer) {
			new EntityDestroyPacket(entityId).send(viewer);

			viewedBy.remove(viewer.getUniqueId());
			NameplateManager.get(viewer).getViewing().remove(uuid);
		}

		public void sendSneakPacket(Player viewer, boolean sneaking) {
			new EntitySneakPacket(entityId).setSeeThroughWalls(sneaking || Minigamer.of(getOnlinePlayer()).isPlaying()).send(viewer);
		}

	}

}
