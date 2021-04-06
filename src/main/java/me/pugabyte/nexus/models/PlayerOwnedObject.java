package me.pugabyte.nexus.models;

import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

import static me.pugabyte.nexus.utils.AdventureUtils.identityOf;

public abstract class PlayerOwnedObject implements Identified {

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

	public void send(Component component, MessageType type) {
		if (type == null) {
			send(component);
			return;
		}
		if (isOnline())
			getPlayer().sendMessage(component, type);
	}

	public void send(Identity identity, Component component, MessageType type) {
		// fail safes, as sendMessage requires NonNull args
		if (component == null)
			return;

		if (identity == null && type == null) {
			send(component);
			return;
		}
		if (type == null) {
			send(identity, component);
			return;
		}
		if (identity == null) {
			send(component, type);
			return;
		}

		if (isOnline())
			getPlayer().sendMessage(identity, component, type);
	}

	public void send(Identified sender, Component component, MessageType type) {
		send(sender.identity(), component, type);
	}

	public void send(UUID sender, Component component, MessageType type) {
		send(identityOf(sender), component, type);
	}

	public void send(Identity identity, Component component) {
		if (identity == null) {
			send(component);
			return;
		}
		if (isOnline())
			getPlayer().sendMessage(identity, component);
	}

	public void send(Identified sender, Component component) {
		send(sender.identity(), component);
	}

	public void send(UUID sender, Component component) {
		send(identityOf(sender), component);
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

	@Override
	public @NonNull Identity identity() {
		return Identity.identity(getUuid());
	}
}
