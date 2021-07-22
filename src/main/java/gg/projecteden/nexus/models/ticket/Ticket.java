package gg.projecteden.nexus.models.ticket;

import com.dieselpoint.norm.serialize.DbSerializer;
import gg.projecteden.nexus.framework.persistence.serializer.mysql.LocationSerializer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.Data;
import lombok.NoArgsConstructor;
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
	private String closedByUuid;
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
		} catch (Exception ex) {
			return (T) Bukkit.getConsoleSender();
		}
	}

	public String getOwnerName() {
		try {
			return Name.of(UUID.fromString(uuid));
		} catch (Exception ex) {
			return Bukkit.getConsoleSender().getName();
		}
	}

	public boolean ownsTicket(Player player) {
		return player.getUniqueId().toString().equals(uuid);
	}
	
	public boolean canBeSeenBy(Player player) {
		return Nerd.of(player).getRank().isMod() || ownsTicket(player);
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
