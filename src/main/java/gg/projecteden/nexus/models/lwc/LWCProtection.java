package gg.projecteden.nexus.models.lwc;

import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lwc_protections")
public class LWCProtection {
	private int id;
	private String owner;
	private int type;
	private int x, y, z;
	private String data;
	private String world;
	private String password;
	private int blockID;
	private String date;
	private int last_accessed;
	private String rights;

	public OfflinePlayer getPlayer() {
		return PlayerUtils.getPlayer(UUID.fromString(owner));
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	public LocalDateTime getDateCreated() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.zzz");
		return LocalDateTime.from(formatter.parse(date));
	}

}
