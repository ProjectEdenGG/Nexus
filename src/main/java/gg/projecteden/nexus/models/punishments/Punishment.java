package gg.projecteden.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.AdventureUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Punishment implements PlayerOwnedObject {
	private UUID id;
	private UUID uuid;
	private UUID punisher;

	private PunishmentType type;
	private String reason;
	private boolean active;

	private LocalDateTime timestamp;
	private int seconds;
	private LocalDateTime expiration;
	private LocalDateTime received;

	private UUID remover;
	private LocalDateTime removed;

	private UUID replacedBy;

	@Builder
	public Punishment(@NotNull UUID uuid, @NotNull UUID punisher, @NotNull PunishmentType type, String input, boolean now) {
		this.id = UUID.randomUUID();
		this.uuid = uuid;
		this.type = type;
		this.punisher = punisher;
		this.timestamp = LocalDateTime.now();
		this.active = true;

		if (type.hasTimespan()) {
			Timespan timespan = Timespan.find(input);
			this.reason = timespan.getRest();
			this.seconds = (int) (timespan.getOriginal() / 1000);

			if (this.reason != null && this.reason.matches("^[rR]:.*"))
				this.reason = reason.replaceFirst("[rR]:", "");

			if (now)
				received();
			else if (type.isAutomaticallyReceived())
				received();
			else if (isOnline() && !isAfk())
				received();
		} else
			this.reason = input;
	}

	public static PunishmentBuilder ofType(PunishmentType type) {
		return builder().type(type);
	}

	public boolean isActive() {
		LocalDateTime now = LocalDateTime.now();
		if (!active)
			return false;

		if (type.hasTimespan()) {
			if (timestamp != null && timestamp.isAfter(now))
				return false;
			if (expiration != null && expiration.isBefore(now))
				return false;
		}

		return true;
	}

	public boolean hasReason() {
		return !Nullables.isNullOrEmpty(reason);
	}

	boolean hasBeenRemoved() {
		return removed != null;
	}

	boolean hasBeenReceived() {
		return received != null;
	}

	boolean hasBeenReplaced() {
		return replacedBy != null;
	}

	public void received() {
		if (!type.isReceivedIfAfk())
			if (isOnline() && AFK.get(getOnlinePlayer()).isAfk())
				return;

		actuallyReceived();
	}

	public void actuallyReceived() {
		if (hasBeenReceived())
			return;

		received = LocalDateTime.now();
		if (type.hasTimespan() && seconds > 0)
			expiration = Timespan.ofSeconds(seconds).fromNow();
	}

	public void deactivate(UUID remover) {
		this.active = false;
		this.removed = LocalDateTime.now();
		this.remover = remover;
		announceEnd();
		getType().onExpire(this);
		save();
	}

	void announceStart() {
		Punishments.broadcast(("&e" + Nickname.of(punisher) + " &c" + type.getPastTense() + " &e" + getNickname()) + getTimeAndReason());
	}

	String getTimeAndReason() {
		String message = "";
		if (seconds > 0)
			message += " &cfor &e" + Timespan.ofSeconds(seconds).format(FormatType.LONG);

		if (!Nullables.isNullOrEmpty(reason))
			message += " &cfor &7" + reason;
		return message;
	}

	void announceEnd() {
		if (remover != null)
			Punishments.broadcast("&e" + Nickname.of(remover) + " &3un" + type.getPastTense() + " &e" + getNickname());
	}

	public Component getDisconnectMessage() {
		String message = getType().getDisconnectMessage(this);
		if (Nullables.isNullOrEmpty(message))
			return null;
		return AdventureUtils.toComponent(message);
	}

	public String getTimeLeft() {
		if (expiration == null)
			if (seconds > 0)
				return Timespan.ofSeconds(seconds).format() + " left";
			else
				return "forever";
		else
			if (hasBeenRemoved())
				return "removed";
			else
				if (expiration.isBefore(LocalDateTime.now()))
					return "expired";
				else
					return Timespan.of(expiration).format() + " left";
	}

	public String getTimeSince() {
		return Timespan.of(timestamp).format() + " ago";
	}

	public String getTimeSinceRemoved() {
		return Timespan.of(removed).format() + " ago";
	}

	void save() {
		Punishments.of(uuid).save();
	}

}
