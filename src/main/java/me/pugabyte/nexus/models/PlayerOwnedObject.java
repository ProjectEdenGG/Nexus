package me.pugabyte.nexus.models;

import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class PlayerOwnedObject {

	public abstract UUID getUuid();

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(getUuid());
	}

	public Player getPlayer() {
		if (!getOfflinePlayer().isOnline())
			throw new PlayerNotOnlineException(getOfflinePlayer());
		return getOfflinePlayer().getPlayer();
	}

	public Nerd getNerd() {
		return Nerd.of(getUuid());
	}

	public boolean isOnline() {
		return getOfflinePlayer().isOnline() && getOfflinePlayer().getPlayer() != null;
	}

	public String getName() {
		return getOfflinePlayer().getName();
	}

	public String getNickname() {
		return getNerd().getNickname();
	}

	public void send(String message) {
		send(new JsonBuilder(message));
	}

	public void send(JsonBuilder message) {
		if (isOnline())
			getPlayer().sendMessage(message.build());
	}

	public void send(Component component) {
		if (isOnline())
			getPlayer().sendMessage(component);
	}

	public void send(int delay, String message) {
		Tasks.wait(delay, () -> send(message));
	}

	public void send(int delay, JsonBuilder message) {
		Tasks.wait(delay, () -> send(message));
	}

	public JsonBuilder json() {
		return json("");
	}

	public JsonBuilder json(String message) {
		return new JsonBuilder(message);
	}

	public String toPrettyString() {
		try {
			return StringUtils.toPrettyString(this);
		} catch (Exception ignored) {
			return this.toString();
		}
	}

}
