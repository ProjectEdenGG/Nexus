package me.pugabyte.bncore.models.ticket;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.conversations.Conversable;
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

	public <T extends Conversable> T getOwner() {
		try {
			return (T) Bukkit.getOfflinePlayer(UUID.fromString(uuid));
		} catch (Throwable ex) {
			return (T) Bukkit.getConsoleSender();
		}
	}

	public String getOwnerName() {
		try {
			return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
		} catch (Throwable ex) {
			return Bukkit.getConsoleSender().getName();
		}
	}

	public boolean ownsTicket(Player player) {
		return player.getUniqueId().toString().equals(uuid);
	}
	
	public boolean canBeSeenBy(Player player) {
		return player.hasPermission("group.moderator") || ownsTicket(player);
	}

	public void setOpen(boolean open) {
		this.open = open;
		if (!this.open && this.timeClosed == null)
			this.timeClosed = LocalDateTime.now();

	}

	public String getTimespan() {
		return StringUtils.timespanDiff(timeOpened);
	}

}
