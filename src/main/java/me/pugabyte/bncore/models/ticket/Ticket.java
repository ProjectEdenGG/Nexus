package me.pugabyte.bncore.models.ticket;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.tickets.Tickets;
import me.pugabyte.bncore.framework.persistence.serializer.LocationSerializer;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Ticket {
	@Id
	@GeneratedValue
	private int id;
	private String uuid;
	private boolean open;
	private LocalDateTime timeOpened;
	private LocalDateTime timeClosed;
	@DbSerializer(LocationSerializer.class)
	private Location location;
	private String description;

	public Ticket(Player player, String description) {
		this.uuid = player.getUniqueId().toString();
		this.open = true;
		this.timeOpened = LocalDateTime.now();
		this.location = player.getLocation();
		this.description = description;
	}

	public OfflinePlayer getOwner() {
		return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
	}

	public boolean ownsTicket(Player player) {
		return player.getUniqueId().toString().equals(uuid);
	}
	
	public boolean canBeSeenBy(Player player) {
		return player.hasPermission(Tickets.PERMISSION_MOD) || ownsTicket(player);
	}

	public void setOpen(boolean open) {
		this.open = open;
		if (!this.open && this.timeClosed != null)
			this.timeClosed = LocalDateTime.now();

	}

	public String getTimespan() {
		return Utils.timespanDiff(timeOpened);
	}

}
