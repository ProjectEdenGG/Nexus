package gg.projecteden.nexus.models.ticket;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "ticket_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Tickets implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Ticket> tickets = new ArrayList<>();

	public List<Ticket> getAll() {
		return tickets;
	}

	public List<Ticket> getAllOpen() {
		return tickets.stream().filter(Ticket::isOpen).toList();
	}

	public Ticket get(int id) {
		return tickets.get(id - 1);
	}

	@Data
	@NoArgsConstructor
	@Converters({UUIDConverter.class, LocationConverter.class, LocalDateTimeConverter.class})
	public static class Ticket implements PlayerOwnedObject {
		private int id;
		private UUID uuid;
		private UUID closedBy;
		private boolean open;
		private LocalDateTime timeOpened;
		private LocalDateTime timeClosed;
		private Location location;
		private String description;

		public Ticket(Player player, String description) {
			final Tickets tickets = new TicketsService().get0();
			this.id = tickets.getAll().size() + 1;
			this.uuid = player.getUniqueId();
			this.open = true;
			this.timeOpened = LocalDateTime.now();
			this.location = player.getLocation();
			this.description = description;
			tickets.getTickets().add(this);
		}

		public boolean ownsTicket(Player player) {
			return player.getUniqueId().equals(uuid);
		}

		public boolean canBeSeenBy(Player player) {
			return Rank.of(player).isMod() || ownsTicket(player);
		}

		public void setOpen(boolean open) {
			this.open = open;
			if (!this.open && this.timeClosed == null)
				this.timeClosed = LocalDateTime.now();
		}

		public String getTimespan() {
			return Timespan.of(timeOpened).format();
		}

	}

}
