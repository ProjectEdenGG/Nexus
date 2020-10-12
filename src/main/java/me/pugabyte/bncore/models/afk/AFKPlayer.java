package me.pugabyte.bncore.models.afk;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

import com.dieselpoint.norm.serialize.DbSerializer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import me.pugabyte.bncore.models.afk.events.NotAFKEvent;
import me.pugabyte.bncore.models.afk.events.NowAFKEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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


	public void forceAfk(Runnable action) {
		setForceAfk(true);
		action.run();
		Tasks.wait(Time.SECOND.x(10), () -> {
			setLocation();
			setForceAfk(false);
		});
	}

	public void update() {
		setTime();
		setLocation();
	}

	public void afk() {
		setAfk(true);

		new NowAFKEvent(this).callEvent();

		message();
		broadcast();
	}

	public void notAfk() {
		setAfk(false);
		setMessage(null);
		setTime();
		setLocation();

		new NotAFKEvent(this).callEvent();

		message();
		broadcast();
	}

	public void message() {
		if (isAfk)
			Utils.send(getPlayer(), "&7* You are now AFK" + (message == null ? "" : ". Your auto-reply message is set to:\n &e" + message));
		else
			Utils.send(getPlayer(), "&7* You are no longer AFK");
	}

	private void broadcast() {
		String broadcast = "&7* &e" + getPlayer().getName() + " &7is " + (isAfk ? "now" : "no longer") + " AFK";
		Bukkit.getOnlinePlayers().forEach(_player -> {
			if (!Utils.canSee(_player, getPlayer()))
				return;
			if (_player.getUniqueId() == getPlayer().getUniqueId())
				return;

			// TODO: Mute menu
			Utils.send(_player, (broadcast));
		});
	}

}
