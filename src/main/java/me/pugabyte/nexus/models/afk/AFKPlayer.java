package me.pugabyte.nexus.models.afk;

import com.dieselpoint.norm.serialize.DbSerializer;
import eden.utils.TimeUtils.Time;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.framework.persistence.serializer.mysql.LocationSerializer;
import me.pugabyte.nexus.models.afk.events.NotAFKEvent;
import me.pugabyte.nexus.models.afk.events.NowAFKEvent;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

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
			PlayerUtils.send(getPlayer(), "&7* You are now AFK" + (message == null ? "" : ". Your auto-reply message is set to:\n &e" + message));
		else
			PlayerUtils.send(getPlayer(), "&7* You are no longer AFK");
	}

	private void broadcast() {
		String broadcast = "&7* &e" + Nickname.of(getPlayer()) + " &7is " + (isAfk ? "now" : "no longer") + " AFK";
		Bukkit.getOnlinePlayers().forEach(_player -> {
			if (!PlayerUtils.canSee(_player, getPlayer()))
				return;
			if (_player.getUniqueId() == getPlayer().getUniqueId())
				return;
			if (MuteMenuUser.hasMuted(_player, MuteMenuItem.AFK))
				return;

			PlayerUtils.send(_player, broadcast);
		});
	}

}
