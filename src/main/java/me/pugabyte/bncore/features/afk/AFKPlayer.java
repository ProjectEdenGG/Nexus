package me.pugabyte.bncore.features.afk;

import lombok.Data;
import me.pugabyte.bncore.features.afk.events.NotAFKEvent;
import me.pugabyte.bncore.features.afk.events.NowAFKEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static me.pugabyte.bncore.utils.Utils.colorize;

@Data
public class AFKPlayer {
	private Player player;
	private boolean isAfk;
	private String message;
	private LocalDateTime time;
	private Location location;
	private boolean forceAfk;

	public AFKPlayer(Player player) {
		this.player = player;
		this.time = LocalDateTime.now();
		this.location = player.getLocation();
	}

	public void setMessage(String message) {
		this.message = ChatColor.stripColor(message);
	}

	public void setTime() {
		this.time = LocalDateTime.now();
	}

	public boolean isTimeAfk() {
		return time.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 240;
	}

	public void setLocation() {
		this.location = player.getLocation().clone();
	}

	public void update() {
		setTime();
		setLocation();
	}

	public void afk() {
		setAfk(true);

		Utils.callEvent(new NowAFKEvent(this));

		Bukkit.getOnlinePlayers().forEach(_player -> {
			if (!Utils.canSee(_player, player)) return;

			String broadcast = "&7* &e" + player.getName() + " &7is now AFK";
			if (_player.getUniqueId() == player.getUniqueId()) {
				broadcast = "&7* You are now AFK";
				if (message != null)
					broadcast += ". Your auto-reply message is set to:\n &e" + message;
			}

			// TODO: Mute menu
			_player.sendMessage(colorize(broadcast));
		});
	}

	public void notAfk() {
		setAfk(false);
		setMessage(null);
		setTime();
		setLocation();

		Utils.callEvent(new NotAFKEvent(this));

		Bukkit.getOnlinePlayers().forEach(_player -> {
			if (!Utils.canSee(_player, player)) return;

			String broadcast = "&7* &e" + player.getName() + " &7is no longer AFK";
			if (_player.getUniqueId() == player.getUniqueId())
				broadcast = "&7* You are no longer AFK";

			// TODO: Mute menu
			_player.sendMessage(colorize(broadcast));
		});
	}

}
