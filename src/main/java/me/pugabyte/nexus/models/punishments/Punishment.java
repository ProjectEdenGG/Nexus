package me.pugabyte.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.utils.TimeUtils.Timespan.FormatType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Punishment extends PlayerOwnedObject {
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
	// TODO: For ip bans? for multi-punishments too?
	//  private Set<UUID> related = new HashSet<>();

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
			this.seconds = timespan.getOriginal();
			if (type.isAutomaticallyReceived())
				received();
			if (now)
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
		return !isNullOrEmpty(reason);
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
			if (isOnline() && AFK.get(getPlayer()).isAfk())
				return;

		actuallyReceived();
	}

	public void actuallyReceived() {
		if (hasBeenReceived())
			return;

		received = LocalDateTime.now();
		if (type.hasTimespan() && seconds > 0)
			expiration = Timespan.of(seconds).fromNow();
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
		String message = "&e" + Nickname.of(punisher) + " &c" + type.getPastTense() + " &e" + getNickname();
		if (seconds > 0)
			message += " &cfor &e" + Timespan.of(seconds).format(FormatType.LONG);

		if (!isNullOrEmpty(reason))
			message += " &cfor &7" + reason;

		Punishments.broadcast(message);
	}

	void announceEnd() {
		if (remover != null)
			Punishments.broadcast("&e" + Nickname.of(remover) + " &3un" + type.getPastTense() + " &e" + getNickname());
	}

	public Component getDisconnectMessage() {
		String message = getType().getDisconnectMessage(this);
		if (isNullOrEmpty(message))
			return null;
		return Component.text(message);
	}

	public String getTimeLeft() {
		if (expiration == null)
			if (seconds > 0)
				return Timespan.of(seconds).format() + " left";
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
