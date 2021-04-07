package me.pugabyte.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

@Data
@Builder
@Entity("punishments")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Punishments extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Punishment> punishments = new ArrayList<>();
	private List<String> ipHistory = new ArrayList<>();

	public static transient final String PREFIX = StringUtils.getPrefix("Punishments");
	public static transient final String DISCORD_PREFIX = StringUtils.getDiscordPrefix("Punishments");

	public Optional<Punishment> getActiveBan() {
		return punishments.stream()
				.filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.IP_BAN)
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public Optional<Punishment> getActiveMute() {
		return punishments.stream()
				.filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.MUTE)
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public List<Punishment> getNewWarnings() {
		return punishments.stream()
				.filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.WARN && punishment.getReceived() == null)
				.collect(Collectors.toList());
	}

	public void add(Punishment punishment) {
		punishments.add(punishment);
		punishment.getType().action(punishment);
		String message = "&e" + punishment.getPunisher().getName() + " &c" + punishment.getType().getEnglish() + " &e" + punishment.getName() + " &ffor ";
		if (punishment.getExpiration() != null)
			message += "&e" + punishment.getTimeLeft() + " &ffor ";
		message += "&7" + punishment.getReason();
		Chat.broadcastIngame(PREFIX + message);
		Chat.broadcastDiscord(DISCORD_PREFIX + message);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, LocationConverter.class})
	public static class Punishment extends PlayerOwnedObject {
		private UUID id;
		private UUID uuid;
		private UUID punisher;
		private PunishmentType type;
		private String reason;
		private boolean active;
		private LocalDateTime timestamp;
		private LocalDateTime expiration;
		private LocalDateTime received;
		private UUID replacedBy;

		@Builder
		public Punishment(UUID uuid, UUID punisher, PunishmentType type, String input) {
			this.id = UUID.randomUUID();
			this.uuid = uuid;
			this.type = type;
			this.punisher = punisher;
			this.timestamp = LocalDateTime.now();
			// TODO parse from input
			this.reason = reason;
			this.expiration = expiration;
		}

		public OfflinePlayer getPunisher() {
			return PlayerUtils.getPlayer(punisher);
		}

		public boolean isActive() {
			LocalDateTime now = LocalDateTime.now();
			return active && (timestamp == null || timestamp.isBefore(now)) && (expiration == null || expiration.isAfter(now));
		}

		// TODO
		public String getDisconnectMessage() {
			return getType().getDisconnectMessage(this);
		}

		public String getTimeLeft() {
			return timespanDiff(expiration);
		}

		@Getter
		public enum PunishmentType {
			BAN("banned") {
				@Override
				public void action(Punishment punishment) {
					if (punishment.isOnline())
						punishment.getPlayer().kickPlayer(punishment.getDisconnectMessage());
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			IP_BAN("ip-banned") {
				@Override
				public void action(Punishment punishment) {
					if (punishment.isOnline())
						punishment.getPlayer().kickPlayer(punishment.getDisconnectMessage());
					// TODO look for alts, kick
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			KICK("kicked") {
				@Override
				public void action(Punishment punishment){
					if (punishment.isOnline())
						punishment.getPlayer().kickPlayer(punishment.getDisconnectMessage());
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			MUTE("muted") {
				@Override
				public void action(Punishment punishment){
					// TODO send message
				}
			},
			WARN("warned") {
				@Override
				public void action(Punishment punishment) {
					// TODO send message
				}
			};

			private final String english;

			PunishmentType(String english) {
				this.english = english;
			}

			public abstract void action(Punishment punishment);

			public String getDisconnectMessage(Punishment punishment) {
				return punishment.getReason();
			}
		}
	}

}
