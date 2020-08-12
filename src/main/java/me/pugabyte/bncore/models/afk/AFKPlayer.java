package me.pugabyte.bncore.models.afk;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import me.pugabyte.bncore.models.afk.events.NotAFKEvent;
import me.pugabyte.bncore.models.afk.events.NowAFKEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@Data
@NoArgsConstructor
@Table(name = "afk")
public class AFKPlayer {
	private String uuid;
	private boolean isAfk;
	private String message;
	private LocalDateTime time;
	@DbSerializer(LocationSerializer.class)
	private Location location;
	private boolean forceAfk;

	public AFKPlayer(Player player) {
		this(player.getUniqueId());
	}

	public AFKPlayer(UUID uuid) {
		this.uuid = uuid.toString();
		update();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(UUID.fromString(uuid));
	}

	public void setMessage(String message) {
		this.message = stripColor(message);
	}

	public void setTime() {
		this.time = LocalDateTime.now();
	}

	public boolean isTimeAfk() {
		return time.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 240;
	}

	public void setLocation() {
		Player player = getPlayer();
		if (player != null)
			this.location = player.getLocation().clone();
	}

	public void update() {
		setTime();
		setLocation();
	}

	public void afk() {
		setAfk(true);

		new NowAFKEvent(this).callEvent();

		Bukkit.getOnlinePlayers().forEach(_player -> {
			if (!Utils.canSee(_player, getPlayer())) return;

			String broadcast = "&7* &e" + getPlayer().getName() + " &7is now AFK";
			if (_player.getUniqueId() == getPlayer().getUniqueId()) {
				broadcast = "&7* You are now AFK";
				if (message != null)
					broadcast += ". Your auto-reply message is set to:\n &e" + message;
			}

			// TODO: Mute menu
			Utils.send(_player, (broadcast));
		});
	}

	public void notAfk() {
		setAfk(false);
		setMessage(null);
		setTime();
		setLocation();

		new NotAFKEvent(this).callEvent();

		Bukkit.getOnlinePlayers().forEach(_player -> {
			if (!Utils.canSee(_player, getPlayer())) return;

			String broadcast = "&7* &e" + getPlayer().getName() + " &7is no longer AFK";
			if (_player.getUniqueId() == getPlayer().getUniqueId())
				broadcast = "&7* You are no longer AFK";

			// TODO: Mute menu
			Utils.send(_player, broadcast);
		});
	}

}
