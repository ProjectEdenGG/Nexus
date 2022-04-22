package gg.projecteden.nexus.models.teleport;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.interfaces.DatabaseObject;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Data
@Entity(value = "teleport_requests", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class TeleportRequests implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private int counter;
	private List<TeleportRequest> pending = new ArrayList<>();

	public static int nextId() {
		return new TeleportRequestsService().get0().counter++;
	}

	public TeleportRequest get(int id) {
		return pending.stream().filter(request -> request.getId() == id).findFirst().orElse(null);
	}

	public List<TeleportRequest> getByReceiver(Player receiver) {
		return get(request -> request.getReceiver().equals(receiver.getUniqueId()));
	}

	public List<TeleportRequest> getBySender(Player sender) {
		return get(request -> request.getSender().equals(sender.getUniqueId()));
	}

	public List<TeleportRequest> get(Predicate<TeleportRequest> predicate) {
		return pending.stream().filter(predicate).toList();
	}

	public void removeDuplicates(UUID sender, UUID receiver) {
		pending.removeIf(request -> request.getSender().equals(sender) && request.getReceiver().equals(receiver));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, LocationConverter.class})
	public static class TeleportRequest {
		private int id;
		private UUID sender;
		private UUID receiver;
		private RequestType type;
		private Location teleportLocation;
		private LocalDateTime timeSent = LocalDateTime.now();

		public TeleportRequest(Player sender, Player receiver, RequestType type) {
			this.id = TeleportRequests.nextId();
			this.sender = sender.getUniqueId();
			this.receiver = receiver.getUniqueId();
			this.type = type;

			if (type == RequestType.TELEPORT)
				this.teleportLocation = receiver.getLocation();
			else
				this.teleportLocation = sender.getLocation();
		}

		public OfflinePlayer getSenderPlayer() {
			return PlayerUtils.getPlayer(sender);
		}

		public OfflinePlayer getReceiverPlayer() {
			return PlayerUtils.getPlayer(receiver);
		}

		public enum RequestType {
			TELEPORT,
			SUMMON
		}
	}

}
