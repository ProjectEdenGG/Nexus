package gg.projecteden.nexus.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.nickname.NicknameService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.StringUtils;
import me.lexikiq.HasUniqueId;
import me.lexikiq.OptionalPlayerLike;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static gg.projecteden.nexus.utils.AdventureUtils.identityOf;

/**
 * A mongo database object owned by a player
 */
public interface PlayerOwnedObject extends gg.projecteden.interfaces.PlayerOwnedObject, OptionalPlayerLike {

	/**
	 * Gets the unique ID of this object. Alias for {@link #getUuid()}, for compatibility with {@link HasUniqueId}.
	 * @return this object's unique ID
	 */
	@Override
	default @NotNull UUID getUniqueId() {return getUuid();}

	/**
	 * Gets the offline player for this object.
	 * <p>
	 * <b>WARNING:</b> This method involves I/O operations to fetch user data which can be costly,
	 * especially if used in a Task. Please consider if {@link #getUuid()}, {@link #getName()},
	 * or {@link #isOnline()} are suitable for your purposes.
	 * </p>
	 * If a method requires {@link OfflinePlayer} and just uses it for {@link #getUniqueId()},
	 * consider changing the parameter of the method to {@link HasUniqueId}.
	 * @return offline player
	 * @deprecated method can be costly and often unnecessary
	 */
	@Deprecated
	default @NotNull OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(getUuid());
	}

	/**
	 * Gets the online player for this object and returns null if they're not online
	 * @return online player or null
	 */
	default @Nullable Player getPlayer() {
		return Bukkit.getPlayer(getUuid());
	}

	/**
	 * Gets the online player for this object and throws if they're not online
	 * @return online player
	 * @throws PlayerNotOnlineException player is not online
	 */
	default @NotNull Player getOnlinePlayer() throws PlayerNotOnlineException {
		Player player = getPlayer();
		if (player == null)
			throw new PlayerNotOnlineException(getUuid());
		return player;
	}

	default boolean isOnline() {
		return getPlayer() != null;
	}

	default boolean isAfk() {
		return AFK.get(getOnlinePlayer()).isAfk();
	}

	default boolean isTimeAfk() {
		return AFK.get(getOnlinePlayer()).isTimeAfk();
	}

	default @NotNull Nerd getNerd() {
		return Nerd.of(this);
	}

	default @NotNull Rank getRank() {
		return Rank.of(this);
	}

	default @NotNull Nerd getOnlineNerd() {
		return Nerd.of(getOnlinePlayer());
	}

	default @NotNull WorldGroup getWorldGroup() {
		return getOnlineNerd().getWorldGroup();
	}

	@Override
	default @NotNull String getName() {
		String name = Name.of(this);
		if (name == null)
			name = Nerd.of(getUuid()).getName();
		return name;
	}

	@Override
	default @NotNull String getNickname() {
		return Nickname.of(this);
	}

	default Nickname getNicknameData() {
		return new NicknameService().get(this.getUuid());
	}

	default boolean hasNickname() {
		return !isNullOrEmpty(getNicknameData().getNicknameRaw());
	}

	default void debug(String message) {
		if (Nexus.isDebug())
			sendMessage(message);
	}

	default void debug(ComponentLike message) {
		if (Nexus.isDebug())
			sendMessage(message);
	}

	default void sendMessage(String message) {
		if (StringUtils.isUUID0(getUuid()))
			Nexus.log(message);
		else
			sendMessage(json(message));
	}

	default void sendOrMail(String message) {
		if (StringUtils.isUUID0(getUuid())) {
			Nexus.log(message);
			return;
		}

		if (isOnline())
			sendMessage(json(message));
		else
			Mail.fromServer(getUuid(), WorldGroup.SURVIVAL, message).send();
	}

	default void sendMessage(UUID sender, ComponentLike component, MessageType type) {
		if (StringUtils.isUUID0(getUuid()))
			Nexus.log(AdventureUtils.asPlainText(component));
		else
			sendMessage(identityOf(sender), component, type);
	}

	default void sendMessage(UUID sender, ComponentLike component) {
		if (StringUtils.isUUID0(getUuid()))
			Nexus.log(AdventureUtils.asPlainText(component));
		else
			sendMessage(identityOf(sender), component);
	}

	default void sendMessage(int delay, String message) {
		Tasks.wait(delay, () -> sendMessage(message));
	}

	default void sendMessage(int delay, ComponentLike component) {
		Tasks.wait(delay, () -> {
			if (StringUtils.isUUID0(getUuid()))
				Nexus.log(AdventureUtils.asPlainText(component));
			else
				sendMessage(component);
		});
	}

	default JsonBuilder json() {
		return json("");
	}

	default JsonBuilder json(String message) {
		return new JsonBuilder(message);
	}

	@Override
	default @NotNull Identity identity() {
		return Identity.identity(getUuid());
	}

}
