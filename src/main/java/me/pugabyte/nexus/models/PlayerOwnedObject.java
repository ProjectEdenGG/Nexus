package me.pugabyte.nexus.models;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.nickname.NicknameService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.AdventureUtils.identityOf;

/**
 * A mongo database object owned by a player
 */
public interface PlayerOwnedObject extends eden.interfaces.PlayerOwnedObject, Identified {

	default OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(getUuid());
	}

	default Player getPlayer() {
		if (!getOfflinePlayer().isOnline())
			throw new PlayerNotOnlineException(getOfflinePlayer());
		return getOfflinePlayer().getPlayer();
	}

	default boolean isOnline() {
		return getOfflinePlayer().isOnline() && getOfflinePlayer().getPlayer() != null;
	}

	default Nerd getNerd() {
		return Nerd.of(this.getUuid());
	}

	@Override
	default @NotNull String getName() {
		String name = getOfflinePlayer().getName();
		if (name == null) {
			if (Nexus.isDebug()) try { throw new InvalidInputException("Stacktrace"); } catch (InvalidInputException ex) { ex.printStackTrace(); }
			name = "nexus-" + getUuid().toString();
		}
		return name;
	}

	@Override
	default String getNickname() {
		return Nickname.of(this);
	}

	default Nickname getNicknameData() {
		return new NicknameService().get(this.getUuid());
	}

	default boolean hasNickname() {
		return !isNullOrEmpty(getNicknameData().getNicknameRaw());
	}

	default void send(String message) {
		send(json(message));
	}

	default void sendOrMail(String message) {
		if (isOnline())
			send(json(message));
		else {
			DeliveryService service = new DeliveryService();
			DeliveryUser deliveryUser = service.get(getUuid());
			deliveryUser.add(WorldGroup.SURVIVAL, Delivery.serverDelivery(message));
			service.save(deliveryUser);
		}
	}

	default void send(ComponentLike component) {
		if (isOnline())
			getPlayer().sendMessage(component);
	}

	default void send(ComponentLike component, MessageType type) {
		if (type == null) {
			send(component);
			return;
		}
		if (isOnline())
			getPlayer().sendMessage(component, type);
	}

	default void send(Identity identity, ComponentLike component, MessageType type) {
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

	default void send(Identified sender, ComponentLike component, MessageType type) {
		send(sender.identity(), component, type);
	}

	default void send(UUID sender, ComponentLike component, MessageType type) {
		send(identityOf(sender), component, type);
	}

	default void send(Identity identity, ComponentLike component) {
		if (identity == null) {
			send(component);
			return;
		}
		if (isOnline())
			getPlayer().sendMessage(identity, component);
	}

	default void send(Identified sender, ComponentLike component) {
		send(sender.identity(), component);
	}

	default void send(UUID sender, ComponentLike component) {
		send(identityOf(sender), component);
	}

	default void send(int delay, String message) {
		Tasks.wait(delay, () -> send(message));
	}

	default void send(int delay, ComponentLike message) {
		Tasks.wait(delay, () -> send(message));
	}

	default JsonBuilder json() {
		return json("");
	}

	default JsonBuilder json(String message) {
		return new JsonBuilder(message);
	}

	@Override
	default @NonNull Identity identity() {
		return Identity.identity(getUuid());
	}

}
